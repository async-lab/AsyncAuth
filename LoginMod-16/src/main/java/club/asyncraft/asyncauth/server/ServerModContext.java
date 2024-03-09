package club.asyncraft.asyncauth.server;

import net.minecraft.server.MinecraftServer;

import javax.sql.DataSource;

public class ServerModContext {

    //server side
    public static DataSource dataSource;

    public static MinecraftServer serverInstance;

    public static String modJarPath;

}
