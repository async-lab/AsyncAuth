package club.asynclab.asyncraft.asyncauth.client.gui

import club.asynclab.asyncraft.asyncauth.gui.widget.EditBoxWithLabel
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.PacketAuth
import club.asynclab.asyncraft.asyncauth.util.UtilNetwork.disconnect
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.network.NetworkEvent
import org.lwjgl.glfw.GLFW
import java.util.function.Supplier

@OnlyIn(Dist.CLIENT)
class ScreenLogin(ctx: Supplier<NetworkEvent.Context>) : BaseScreenOnConnecting(TITLE, ctx) {
    private lateinit var passwordEditBox: EditBox
    private lateinit var submitButton: Button
    private lateinit var exitButton: Button

    override fun init() {
        super.init()
        val centerX = this.width / 2

        this.passwordEditBox = EditBoxWithLabel(
            this.font,
            centerX - 100, 100,
            200, 20,
            TranslatableComponent("gui.asyncauth.password")
        ).apply {
            setFormatter { text: String, _: Int -> TextComponent("*".repeat(text.length)).visualOrderText }
        }

        this.submitButton = Button(
            centerX - 50, 150,
            100, 20,
            TranslatableComponent("gui.asyncauth.submit_login")
        ) {
            if (passwordEditBox.value.isEmpty()) return@Button
            NetworkHandler.LOGIN.reply(
                PacketAuth(Minecraft.getInstance().user.name, passwordEditBox.value),
                this.ctx.get()
            )
        }

        this.exitButton = Button(
            20, this.height - 40,
            100, 20,
            TranslatableComponent("gui.asyncauth.exit")
        ) { ctx.get().networkManager.disconnect() }

        this.addRenderableWidget(this.passwordEditBox)
        this.addRenderableWidget(this.submitButton)
        this.addRenderableWidget(this.exitButton)
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        drawCenteredString(
            poseStack, this.font, this.title,
            this.width / 2, 20,
            0xFFFFFF
        )

        super.render(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> ctx.get().networkManager.disconnect()
            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> this.submitButton.onPress()
            else -> return super.keyPressed(keyCode, scanCode, modifiers)
        }

        return true
    }

    companion object {
        private val TITLE: Component =
            TranslatableComponent("gui.asyncauth.title_login")
    }
}