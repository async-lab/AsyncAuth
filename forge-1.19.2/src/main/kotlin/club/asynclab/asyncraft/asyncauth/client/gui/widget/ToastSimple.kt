package club.asynclab.asyncraft.asyncauth.client.gui.widget

import club.asynclab.asyncraft.asyncauth.common.misc.Colors
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastComponent
import net.minecraft.network.chat.Component

class ToastSimple(
    private val content: Component,
) : Toast {
    override fun render(
        poseStack: PoseStack,
        toastComponent: ToastComponent,
        timeSinceLastVisible: Long,
    ): Toast.Visibility {
        RenderSystem.setShaderTexture(0, Toast.TEXTURE)
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        toastComponent.blit(poseStack, 0, 0, 0, 64, this.width(), this.height())
        toastComponent.minecraft.font.draw(poseStack, this.content, 18.0f, 12.0f, Colors.WHITE)
        return if (timeSinceLastVisible >= 5000L) Toast.Visibility.HIDE else Toast.Visibility.SHOW
    }
}