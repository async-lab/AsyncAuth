package club.asynclab.asyncraft.asyncauth.client.gui

import club.asynclab.asyncraft.asyncauth.client.ManagerClient
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraftforge.network.NetworkEvent
import java.time.Instant
import java.util.function.Supplier

class ScreenAutoLogin(
    ctx: Supplier<NetworkEvent.Context>,
    deadline: Long,
) : BaseScreenOnConnecting(TITLE, ctx, deadline) {
    private lateinit var openLoginButton: Button

    override fun init() {
        super.init()
        openLoginButton = Button(
            width / 2 - 70,
            height / 2 + 20,
            140,
            20,
            UtilComponent.getTranslatableComponent(Lang.Gui.OPEN_LOGIN_SCREEN)
        ) {
            ManagerClient.cancelAutoLogin()
        }
        addRenderableWidget(openLoginButton)
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        renderBackground(poseStack)
        drawCenteredString(
            poseStack,
            font,
            title,
            width / 2,
            height / 2 - 20,
            0xFFFFFF
        )
        val timeout = deadline - Instant.now().epochSecond
        if (timeout > 0) {
            drawCenteredString(
                poseStack,
                font,
                UtilComponent.getTranslatableComponent(Lang.Gui.TIMEOUT).string.format(timeout),
                width / 2,
                height / 2,
                0xAAAAAA
            )
        }
        super.render(poseStack, mouseX, mouseY, partialTicks)
    }

    override fun shouldCloseOnEsc(): Boolean = false

    companion object {
        private val TITLE: Component = UtilComponent.getTranslatableComponent(Lang.Gui.AUTO_LOGIN_PENDING)
    }
}
