package club.asynclab.asyncraft.asyncauth.client.gui

import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.gui.ScreenRegister
import club.asynclab.asyncraft.asyncauth.gui.widget.EditBoxWithLabel
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketLogin
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import club.asynclab.asyncraft.asyncauth.util.UtilNetwork.disconnect
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.network.NetworkEvent
import org.lwjgl.glfw.GLFW
import java.time.Instant
import java.util.function.Supplier

@OnlyIn(Dist.CLIENT)
class ScreenLogin(ctx: Supplier<NetworkEvent.Context>, deadline: Long) : BaseScreenOnConnecting(TITLE, ctx, deadline) {
    private lateinit var passwordEditBox: EditBox
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var exitButton: Button

    override fun init() {
        super.init()
        val centerX = this.width / 2

        this.passwordEditBox = object : EditBoxWithLabel(
            this.font,
            centerX - 100, 100,
            200, 20,
            UtilComponent.getTranslatableComponent(Lang.Gui.PASSWORD)
        ) {
            override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
                when (keyCode) {
                    GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> this@ScreenLogin.loginButton.onPress()
                    else -> return super.keyPressed(keyCode, scanCode, modifiers)
                }

                return true
            }
        }.apply {
            setFormatter { text: String, _: Int -> UtilComponent.getLiteralComponent("*".repeat(text.length)).visualOrderText }
        }

        this.loginButton = Button(
            centerX - 100, 150,
            100, 20,
            UtilComponent.getTranslatableComponent(Lang.Gui.LOGIN)
        ) {
            if (this.passwordEditBox.value.isEmpty()) {
                UtilToast.toast(UtilComponent.getTranslatableComponent(Lang.Auth.EMPTY))
                return@Button
            }
            NetworkHandler.LOGIN.reply(
                PacketLogin(Minecraft.getInstance().user.name, passwordEditBox.value),
                this.ctx.get()
            )
        }

        this.registerButton = Button(
            centerX, 150,
            100, 20,
            UtilComponent.getTranslatableComponent(Lang.Gui.REGISTER)
        ) { Minecraft.getInstance().pushGuiLayer(ScreenRegister(ctx, this.deadline)) }

        this.exitButton = Button(
            20, this.height - 40,
            100, 20,
            UtilComponent.getTranslatableComponent(Lang.Gui.EXIT)
        ) { this.ctx.get().networkManager.disconnect() }

        this.setInitialFocus(this.passwordEditBox)

        this.addRenderableWidget(this.passwordEditBox)
        this.addRenderableWidget(this.loginButton)
        this.addRenderableWidget(this.registerButton)
        this.addRenderableWidget(this.exitButton)
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground(poseStack)
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 0xFFFFFF)
        val timeout = this.deadline - Instant.now().epochSecond
        if (timeout > 0) {
            drawCenteredString(
                poseStack,
                font,
                UtilComponent.getTranslatableComponent(Lang.Gui.TIMEOUT).string.format(timeout),
                width / 2,
                loginButton.y - 10,
                0xFF0000
            )
        }
        super.render(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> ctx.get().networkManager.disconnect()
            else -> return super.keyPressed(keyCode, scanCode, modifiers)
        }

        return true
    }

    companion object {
        private val TITLE = UtilComponent.getTranslatableComponent(Lang.Gui.LOGIN)
    }
}