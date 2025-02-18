package club.asynclab.asyncraft.asyncauth.util;

import club.asynclab.asyncraft.asyncauth.ClientContext;
import club.asynclab.asyncraft.asyncauth.gui.LoginScreen;
import club.asynclab.asyncraft.asyncauth.gui.RegisterScreen;
import net.minecraft.client.Minecraft;

public class GuiUtils {

    public static void openLoginGui() {
        LoginScreen screen = new LoginScreen();
        ClientContext.loginScreen = screen;
        Minecraft.getInstance().setScreen(screen);
    }

    public static void openRegisterGui() {
        RegisterScreen screen = new RegisterScreen();
        ClientContext.loginScreen = screen;
        Minecraft.getInstance().setScreen(screen);
    }

}
