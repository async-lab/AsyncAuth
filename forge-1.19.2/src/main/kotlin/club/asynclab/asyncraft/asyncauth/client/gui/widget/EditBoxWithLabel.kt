package club.asynclab.asyncraft.asyncauth.client.gui.widget

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

open class EditBoxWithLabel(
    private val font: Font, x: Int, y: Int, width: Int, height: Int, message: Component,
) : EditBox(font, x, y + font.lineHeight + 2, width, height, message) {
    override fun renderButton(pose: PoseStack, mouseX: Int, mouseY: Int, delta: Float) {
        font.draw(pose, message, x.toFloat(), (this.y - font.lineHeight - 2).toFloat(), 0xFFFFFF)
        super.renderButton(pose, mouseX, mouseY, delta)
    }
}