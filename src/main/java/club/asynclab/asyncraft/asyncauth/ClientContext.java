package club.asynclab.asyncraft.asyncauth;

import club.asynclab.asyncraft.asyncauth.util.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientContext {

    public static Screen loginScreen = null;

    protected static boolean isLoggedIn = false;

    public static void markUnverified(boolean isRegistered) {
        if (isRegistered) GuiUtils.openLoginGui();
        else GuiUtils.openRegisterGui();
        isLoggedIn = false;
    }

    public static void markVerified() {
        isLoggedIn = true;
        loginScreen = null;
        Minecraft.getInstance().setScreen(null);
        Minecraft.getInstance().player.displayClientMessage(Component.translatable("msg.asyncauth.login_success"),false);
    }

    public static void logout() {
        isLoggedIn = false;
        loginScreen = null;
        AsyncAuth.isEnabled = false;
    }
}
