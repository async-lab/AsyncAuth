package club.asynclab.asyncraft.asyncauth.util

import club.asynclab.asyncraft.asyncauth.client.gui.widget.ToastSimple
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component

object UtilToast {
    fun toast(component: Component) = Minecraft.getInstance().toasts.addToast(ToastSimple(component))
}