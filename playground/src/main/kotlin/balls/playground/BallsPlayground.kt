package balls.playground

import balls.geometry.Rectangle
import balls.physics.entity.EntitySpawnRequest
import balls.physics.scene.Scene
import balls.physics.scene.SceneQuery
import balls.physics.tile.TilePlaceRequest
import com.github.knokko.boiler.BoilerInstance
import com.github.knokko.boiler.buffers.MappedVkbBuffer
import com.github.knokko.boiler.builders.BoilerBuilder
import com.github.knokko.boiler.builders.WindowBuilder
import com.github.knokko.boiler.commands.CommandRecorder
import com.github.knokko.boiler.descriptors.HomogeneousDescriptorPool
import com.github.knokko.boiler.descriptors.VkbDescriptorSetLayout
import com.github.knokko.boiler.images.VkbImage
import com.github.knokko.boiler.pipelines.GraphicsPipelineBuilder
import com.github.knokko.boiler.synchronization.ResourceUsage
import com.github.knokko.boiler.window.AcquiredImage
import com.github.knokko.boiler.window.SimpleWindowRenderLoop
import com.github.knokko.boiler.window.SwapchainResourceManager
import com.github.knokko.boiler.window.VkbWindow
import com.github.knokko.boiler.window.WindowEventLoop
import com.github.knokko.update.UpdateLoop
import fixie.DistanceUnit
import fixie.m
import fixie.mm
import org.joml.Math.toRadians
import org.joml.Matrix4f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.memFloatBuffer
import org.lwjgl.vulkan.*
import org.lwjgl.vulkan.KHRSurface.VK_PRESENT_MODE_MAILBOX_KHR
import org.lwjgl.vulkan.VK10.*
import java.util.*
import kotlin.time.Duration

fun main() {
	val boiler = BoilerBuilder(
		VK_API_VERSION_1_0, "BallsPlayground", 1
	)
		.validation()
		.enableDynamicRendering()
		.addWindow(WindowBuilder(
		1200, 800, VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
		)).build()

	val spawnPlayer = EntitySpawnRequest(x = 0.m, y = 1.m, z = 0.m, radius = 250.mm)

	val scene = Scene()
	scene.spawnEntity(spawnPlayer)
	scene.addTile(TilePlaceRequest(collider = Rectangle(
		startX = -5.m, startY = 0.m, startZ = -5.m,
		lengthX1 = 10.m, lengthY1 = 0.m, lengthZ1 = 0.m,
		lengthX2 = 0.m, lengthY2 = 0.m, lengthZ2 = 10.m
	)))
	scene.update(Duration.ZERO)

	val updateLoop = UpdateLoop({ scene.update(Scene.STEP_DURATION) }, Scene.STEP_DURATION.inWholeNanoseconds)

	val updateThread = Thread(updateLoop)
	updateThread.start()

	val eventLoop = WindowEventLoop()
	eventLoop.addWindow(BallsPlayground(boiler.window(), updateThread, scene, spawnPlayer.id!!))
	eventLoop.runMain()

	updateLoop.stop()

	boiler.destroyInitialObjects()
}

class BallsPlayground(
	window: VkbWindow,
	private val updateThread: Thread,
	private val scene: Scene,
	private val playerID: UUID
) : SimpleWindowRenderLoop(
	window, 2, false, VK_PRESENT_MODE_MAILBOX_KHR,
	ResourceUsage.COLOR_ATTACHMENT_WRITE, ResourceUsage.COLOR_ATTACHMENT_WRITE
) {

	private val sceneQuery = SceneQuery()

	private var depthFormat = 0
	private var pipelineLayout = 0L
	private var pipeline = 0L

	private lateinit var swapchainResources: SwapchainResourceManager<VkbImage>
	private lateinit var vertexBuffer: MappedVkbBuffer
	private lateinit var cameraBuffer: MappedVkbBuffer
	private lateinit var cameraLayout: VkbDescriptorSetLayout
	private lateinit var cameraPool: HomogeneousDescriptorPool
	private lateinit var cameraSets: LongArray

	override fun setup(boiler: BoilerInstance, stack: MemoryStack) {
		super.setup(boiler, stack)

		depthFormat = boiler.images.chooseDepthStencilFormat(
			VK_FORMAT_D24_UNORM_S8_UINT, VK_FORMAT_X8_D24_UNORM_PACK32,
			VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT
		)

		val cameraBindings = VkDescriptorSetLayoutBinding.calloc(1, stack)
		boiler.descriptors.binding(cameraBindings, 0, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, VK_SHADER_STAGE_VERTEX_BIT)
		cameraLayout = boiler.descriptors.createLayout(stack, cameraBindings, "CameraLayout")
		cameraPool = cameraLayout.createPool(numFramesInFlight, 0, "CameraPool")
		cameraSets = cameraPool.allocate(numFramesInFlight)
		cameraBuffer = boiler.buffers.createMapped(
			64L * numFramesInFlight, VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT, "CameraBuffer"
		)

		boiler.descriptors.bufferInfo(stack, cameraBuffer.fullRange())
		val cameraWrites = VkWriteDescriptorSet.calloc(numFramesInFlight, stack)
		for (frame in 0 until numFramesInFlight) {
			val range = cameraBuffer.range(frame * 64L, 64)
			boiler.descriptors.writeBuffer(
				stack, cameraWrites, cameraSets[frame], frame, VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, range
			)
			cameraWrites[frame].dstBinding(0)
		}
		vkUpdateDescriptorSets(boiler.vkDevice(), cameraWrites, null)
		pipelineLayout = boiler.pipelines.createLayout(
			null, "RectanglePipelineLayout", cameraLayout.vkDescriptorSetLayout
		)

		val vertexBindings = VkVertexInputBindingDescription.calloc(1, stack)
		vertexBindings.binding(0)
		vertexBindings.stride(12)
		vertexBindings.inputRate(VK_VERTEX_INPUT_RATE_VERTEX)

		val vertexAttributes = VkVertexInputAttributeDescription.calloc(1, stack)
		vertexAttributes.location(0)
		vertexAttributes.binding(0)
		vertexAttributes.offset(0)
		vertexAttributes.format(VK_FORMAT_R32G32B32_SFLOAT)

		val ciVertexInput = VkPipelineVertexInputStateCreateInfo.calloc(stack)
		ciVertexInput.`sType$Default`()
		ciVertexInput.pVertexBindingDescriptions(vertexBindings)
		ciVertexInput.pVertexAttributeDescriptions(vertexAttributes)

		val builder = GraphicsPipelineBuilder(boiler, stack)
		builder.simpleShaderStages(
			"RectangleShader", "balls/playground/shaders/rectangle.vert.spv",
			"balls/playground/shaders/rectangle.frag.spv"
		)
		builder.ciPipeline.pVertexInputState(ciVertexInput)
		builder.simpleInputAssembly()
		builder.dynamicViewports(1)
		builder.simpleRasterization(VK_CULL_MODE_NONE)
		builder.noMultisampling()
		builder.simpleDepth(VK_COMPARE_OP_LESS)
		builder.noColorBlending(1)
		builder.dynamicStates(VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR)
		builder.ciPipeline.layout(pipelineLayout)
		builder.dynamicRendering(0, depthFormat, VK_FORMAT_UNDEFINED, window.surfaceFormat)
		pipeline = builder.build("RectanglePipeline")

		swapchainResources = SwapchainResourceManager({ swapchainImage -> boiler.images.createSimple(
			swapchainImage.width(), swapchainImage.height(), depthFormat,
			VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT, VK_IMAGE_ASPECT_DEPTH_BIT, "DepthImage"
		) }, { it.destroy(boiler) })

		vertexBuffer = boiler.buffers.createMapped(
			10 * 4 * 12, VK_BUFFER_USAGE_VERTEX_BUFFER_BIT, "RectangleVertexBuffer"
		)
	}

	override fun recordFrame(
		stack: MemoryStack,
		frameIndex: Int,
		recorder: CommandRecorder,
		acquiredImage: AcquiredImage,
		instance: BoilerInstance
	) {
		if (!updateThread.isAlive) glfwSetWindowShouldClose(window.glfwWindow, true)

		val colorAttachments = VkRenderingAttachmentInfo.calloc(1, stack)
		recorder.simpleColorRenderingAttachment(
			colorAttachments[0], acquiredImage.image().vkImageView,
			VK_ATTACHMENT_LOAD_OP_CLEAR, VK_ATTACHMENT_STORE_OP_STORE, 0f, 0f, 1f, 1f
		)
		val depthAttachment = recorder.simpleDepthRenderingAttachment(
			swapchainResources[acquiredImage].vkImageView, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
			VK_ATTACHMENT_STORE_OP_DONT_CARE, 1f, 0
		)
		recorder.beginSimpleDynamicRendering(
			window.width, window.height, colorAttachments, depthAttachment, null
		)
		vkCmdBindPipeline(recorder.commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS, pipeline)
		recorder.dynamicViewportAndScissor(window.width, window.height)

		val projectionMatrix = Matrix4f().setPerspective(
			toRadians(70f), acquiredImage.width().toFloat() / acquiredImage.height(),
			0.1f, 100f, true
		)
		val viewMatrix = Matrix4f().scale(1f, -1f, 1f).lookAt(
			Vector3f(0f, 1f, 5f),
			Vector3f(0f, 0f, 0f),
			Vector3f(0f, 1f, 0f)
		)
		projectionMatrix.mul(viewMatrix).getToAddress(cameraBuffer.hostAddress + 64 * frameIndex)

		scene.read(sceneQuery, -10.m, -10.m, -10.m, 10.m, 10.m, 10.m)
		val vertexFloatBuffer = memFloatBuffer(vertexBuffer.hostAddress, vertexBuffer.size.toInt() / 4)

		for (index in 0 until sceneQuery.tiles.size) {
			val rectangle = sceneQuery.tiles[index].collider
			val coordinates = arrayOf(
				rectangle.startX, rectangle.startY, rectangle.startZ,
				rectangle.startX + rectangle.lengthX1,
				rectangle.startY + rectangle.lengthY1,
				rectangle.startZ + rectangle.lengthZ1,
				rectangle.startX + rectangle.lengthX1 + rectangle.lengthX2,
				rectangle.startY + rectangle.lengthY1 + rectangle.lengthY2,
				rectangle.startZ + rectangle.lengthZ1 + rectangle.lengthZ2,
				rectangle.startX + rectangle.lengthX1 + rectangle.lengthX2,
				rectangle.startY + rectangle.lengthY1 + rectangle.lengthY2,
				rectangle.startZ + rectangle.lengthZ1 + rectangle.lengthZ2,
				rectangle.startX + rectangle.lengthX2,
				rectangle.startY + rectangle.lengthY2,
				rectangle.startZ + rectangle.lengthZ2,
				rectangle.startX, rectangle.startY, rectangle.startZ
			)
			for (coordinate in coordinates) {
				vertexFloatBuffer.put(coordinate.toDouble(DistanceUnit.METER).toFloat())
			}
		}

		vkCmdBindDescriptorSets(
			recorder.commandBuffer, VK_PIPELINE_BIND_POINT_GRAPHICS,
			pipelineLayout, 0, stack.longs(cameraSets[frameIndex]), null
		)
		vkCmdBindVertexBuffers(recorder.commandBuffer, 0, stack.longs(vertexBuffer.vkBuffer()), stack.longs(0))
		vkCmdDraw(recorder.commandBuffer, 6 * sceneQuery.tiles.size, 1, 0, 0)
		recorder.endDynamicRendering()
	}

	override fun cleanUp(boiler: BoilerInstance) {
		super.cleanUp(boiler)
		cameraPool.destroy()
		cameraLayout.destroy()
		vertexBuffer.destroy(boiler)
		cameraBuffer.destroy(boiler)
		vkDestroyPipeline(boiler.vkDevice(), pipeline, null)
		vkDestroyPipelineLayout(boiler.vkDevice(), pipelineLayout, null)
	}
}
