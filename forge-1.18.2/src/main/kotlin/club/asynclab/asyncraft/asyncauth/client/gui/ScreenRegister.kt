package club.asynclab.asyncraft.asyncauth.gui

import club.asynclab.asyncraft.asyncauth.gui.widget.EditBoxWithLabel
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.Connection
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import org.lwjgl.glfw.GLFW

@OnlyIn(Dist.CLIENT)
class ScreenRegister(
    private val connection: Connection,
) : Screen(TITLE) {
    private lateinit var passwordField: EditBox
    private lateinit var confirmPasswordField: EditBox
    private lateinit var submitButton: Button
    var errorMessage: Component = TextComponent("")

    override fun init() {
        super.init()
        val centerX = this.width / 2

        this.passwordField = EditBoxWithLabel(
            this.font,
            centerX - 100,  // x坐标
            80,  // y坐标
            200, 20,  // 宽度/高度
            TranslatableComponent("gui.asyncauth.password"),
        ).apply { setFormatter { text: String, pos: Int? -> TextComponent("*".repeat(text.length)).visualOrderText } }

        this.confirmPasswordField = EditBoxWithLabel(
            this.font,
            centerX - 100,
            120,
            200, 20,
            TranslatableComponent("gui.asyncauth.confirm_password"),
        ).apply { setFormatter { text: String, pos: Int? -> TextComponent("*".repeat(text.length)).visualOrderText } }

        this.submitButton = Button(
            centerX - 50,  // x坐标
            170,  // y坐标
            100, 20,  // 宽度/高度
            TranslatableComponent("gui.asyncauth.submit_register")
        ) { this.onSubmitButtonPressed(it) }

        val exitButton = Button(
            20,
            this.height - 40,
            100, 20,
            TranslatableComponent("gui.asyncauth.exit")
        ) {
            this.onClose()
            connection.disconnect(TextComponent("msg.asyncauth.unauthorized"))
        }

        this.addRenderableWidget(passwordField)
        this.addRenderableWidget(confirmPasswordField)
        this.addRenderableWidget(submitButton)
        this.addRenderableWidget(exitButton)
    }

    private fun onSubmitButtonPressed(button: Button?) {
        val password = passwordField.value
        if (password.isEmpty()) {
            errorMessage = TranslatableComponent("msg.asyncauth.login_empty_password")
            return
        }
        val confirmPassword = confirmPasswordField.value
        if (confirmPassword != password) {
            errorMessage = TranslatableComponent("gui.asyncauth.password_inconsistency")
            return
        }
        login()
    }

    private fun login() {
//        NetworkHandler.LOGIN.sendTo(
//            PacketAuth(passwordField.value),
//            this.connection,
//            NetworkDirection.LOGIN_TO_SERVER
//        )
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        drawCenteredString(
            poseStack, this.font, this.title,
            this.width / 2, 20, 0xFFFFFF
        )

//        if (errorMessage != null) {
        val errorY = submitButton.y - 10
        drawCenteredString(poseStack, font, errorMessage, width / 2, errorY, 0xFF0000)
//        }

        super.render(poseStack, mouseX, mouseY, partialTicks)
    }


    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> {
                this.onClose()
                connection.disconnect(TextComponent("msg.asyncauth.unauthorized"))
            }

            GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> this.onSubmitButtonPressed(submitButton)
            else -> return super.keyPressed(keyCode, scanCode, modifiers)
        }

        return true
    }

    companion object {
        private val TITLE: Component =
            TranslatableComponent("gui.asyncauth.title_register")
    }
}