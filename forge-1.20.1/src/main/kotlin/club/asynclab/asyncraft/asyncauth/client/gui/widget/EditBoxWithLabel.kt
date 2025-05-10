package club.asynclab.asyncraft.asyncauth.client.gui.widget

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component


open class EditBoxWithLabel(
    private val font: Font, x: Int, y: Int, width: Int, height: Int, message: Component,
) : EditBox(font, x, y + font.lineHeight + 2, width, height, message) {
    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        guiGraphics.drawString(
            font,
            message,
            this.x,
            (this.y - font.lineHeight - 2),
            0xFFFFFF
        )
        super.renderWidget(guiGraphics, mouseX, mouseY, delta)
    }
}