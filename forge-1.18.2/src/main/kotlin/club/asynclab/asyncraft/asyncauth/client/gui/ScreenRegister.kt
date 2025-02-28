package club.asynclab.asyncraft.asyncauth.gui

import club.asynclab.asyncraft.asyncauth.client.gui.BaseScreenOnConnecting
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.gui.widget.EditBoxWithLabel
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler
import club.asynclab.asyncraft.asyncauth.network.packet.auth.PacketRegister
import club.asynclab.asyncraft.asyncauth.util.UtilToast
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.network.NetworkEvent
import org.lwjgl.glfw.GLFW
import java.time.Instant
import java.util.function.Supplier

@OnlyIn(Dist.CLIENT)
class ScreenRegister(
    ctx: Supplier<NetworkEvent.Context>,
    deadline: Long,
) : BaseScreenOnConnecting(TITLE, ctx, deadline) {
    private lateinit var passwordEditBox: EditBox
    private lateinit var confirmEditBox: EditBox
    private lateinit var registerButton: Button
    private lateinit var exitButton: Button

    override fun init() {
        super.init()
        val centerX = this.width / 2

        this.passwordEditBox = object : EditBoxWithLabel(
            this.font,
            centerX - 100,  // x坐标
            80,  // y坐标
            200, 20,  // 宽度/高度
            TranslatableComponent(Lang.Gui.PASSWORD),
        ) {
            override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
                when (keyCode) {
                    GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> this@ScreenRegister.registerButton.onPress()
                    else -> return super.keyPressed(keyCode, scanCode, modifiers)
                }

                return true
            }
        }.apply {
            setFormatter { text: String, _: Int? -> TextComponent("*".repeat(text.length)).visualOrderText }
        }

        this.confirmEditBox = object : EditBoxWithLabel(
            this.font,
            centerX - 100,
            120,
            200, 20,
            TranslatableComponent(Lang.Gui.CONFIRM_PASSWORD),
        ) {
            override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
                when (keyCode) {
                    GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> this@ScreenRegister.registerButton.onPress()
                    else -> return super.keyPressed(keyCode, scanCode, modifiers)
                }

                return true
            }
        }.apply { setFormatter { text: String, _: Int? -> TextComponent("*".repeat(text.length)).visualOrderText } }

        this.registerButton = Button(
            centerX - 50,  // x坐标
            170,  // y坐标
            100, 20,  // 宽度/高度
            TranslatableComponent(Lang.Gui.REGISTER)
        ) {
            if (this.passwordEditBox.value.isEmpty()) {
                UtilToast.toast(TranslatableComponent(Lang.Auth.EMPTY))
                return@Button
            } else if (this.confirmEditBox.value != this.passwordEditBox.value) {
                UtilToast.toast(TranslatableComponent(Lang.Msg.PASSWORD_NOT_MATCH))
                return@Button
            }
            NetworkHandler.LOGIN.reply(
                PacketRegister(Minecraft.getInstance().user.name, this.passwordEditBox.value),
                this.ctx.get()
            )
        }

        this.exitButton = Button(
            20,
            this.height - 40,
            100, 20,
            TranslatableComponent(Lang.Gui.EXIT)
        ) { this.onClose() }

        this.setInitialFocus(this.passwordEditBox)

        this.addRenderableWidget(this.passwordEditBox)
        this.addRenderableWidget(this.confirmEditBox)
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
                TranslatableComponent(Lang.Gui.TIMEOUT).string.format(timeout),
                width / 2,
                registerButton.y - 10,
                0xFF0000
            )
        }
        super.render(poseStack, mouseX, mouseY, partialTicks)
    }


    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        when (keyCode) {
            GLFW.GLFW_KEY_ESCAPE -> this.onClose()
            else -> return super.keyPressed(keyCode, scanCode, modifiers)
        }

        return true
    }

    companion object {
        private val TITLE = TranslatableComponent(Lang.Gui.REGISTER)
    }
}