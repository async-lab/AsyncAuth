package club.asynclab.asyncraft.asyncauth.client.gui.widget

import club.asynclab.asyncraft.asyncauth.common.misc.Colors
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastComponent
import net.minecraft.network.chat.Component

class ToastSimple(
    private val content: Component,
) : Toast {
    override fun render(
        guiGraphics: GuiGraphics,
        toastComponent: ToastComponent,
        timeSinceLastVisible: Long
    ):  Toast.Visibility {
        // 设置Toast背景纹理
        guiGraphics.blit(
            Toast.TEXTURE,
            0,       // 屏幕X坐标
            0,       // 屏幕Y坐标
            0,       // 纹理U偏移（像素单位）
            64,      // 纹理V偏移（像素单位）
            width(),   // 绘制宽度（对应纹理uWidth）
            height()   // 绘制高度（对应纹理vHeight）
        )

        // 绘制文本内容
        guiGraphics.drawString(
            toastComponent.minecraft.font,
            content,
            18,
            12,
            Colors.WHITE,
            false
        )

        return if (timeSinceLastVisible >= 5000L) Toast.Visibility.HIDE else Toast.Visibility.SHOW
    }
}