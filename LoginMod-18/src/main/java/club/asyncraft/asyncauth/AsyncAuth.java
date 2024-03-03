package club.asyncraft.asyncauth;

import club.asyncraft.asyncauth.client.event.LoginCommandHandler;
import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import club.asyncraft.asyncauth.client.event.ClientPlayerEventHandler;
import club.asyncraft.asyncauth.server.event.PlayerEventHandler;
import lombok.extern.slf4j.Slf4j;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("async_auth")
@Slf4j
public class AsyncAuth {

    public AsyncAuth() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(ClientPlayerEventHandler::onPlayerLogout);
            MinecraftForge.EVENT_BUS.addListener(LoginCommandHandler::onPlayerChat);
        } else if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER , MyModConfig.SPEC);

            MinecraftForge.EVENT_BUS.addListener(PlayerEventHandler::onPlayerLogin);
            MinecraftForge.EVENT_BUS.addListener(AsyncAuth::setup);
        }
        CommonPacketManager.init();
        log.info("初始化完成");
    }

    public static void setup(ServerStartingEvent event) {
        log.info("加载数据库...");
        SqlUtils.initDataSource();
    }


}
