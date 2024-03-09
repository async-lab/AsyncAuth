package club.asyncraft.asyncauth;

import club.asyncraft.asyncauth.client.event.LoginCommandHandler;
import club.asyncraft.asyncauth.common.network.CommonPacketManager;
import club.asyncraft.asyncauth.common.util.CommandTabSupport;
import club.asyncraft.asyncauth.common.util.LoggerUtils;
import club.asyncraft.asyncauth.server.PlayerManager;
import club.asyncraft.asyncauth.server.ServerModContext;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import club.asyncraft.asyncauth.server.util.SqlUtils;
import club.asyncraft.asyncauth.server.util.i18n.TranslationContext;
import lombok.extern.slf4j.Slf4j;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.forgespi.language.IModInfo;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

@Mod(AsyncAuth.MOD_ID)
public class AsyncAuth {

    public static final String MOD_ID = "async_auth";

    public AsyncAuth() {
        CommonPacketManager.init();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            MinecraftForge.EVENT_BUS.addListener(LoginCommandHandler::onPlayerChat);
        } else if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER , MyModConfig.SPEC,"AsyncAuth/config.toml");
//            MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
            MinecraftForge.EVENT_BUS.addListener(AsyncAuth::setup);
            MinecraftForge.EVENT_BUS.addListener(PlayerManager::sendLoginInfo);
            MinecraftForge.EVENT_BUS.addListener(AsyncAuth::registerCommands);
            Logger logger = LoggerUtils.getLogger();
            logger.info("初始化完成");
        }

    }

    public static void setup(FMLServerStartingEvent event) {
        Logger log = LoggerUtils.getLogger();
        ServerModContext.serverInstance = ServerLifecycleHooks.getCurrentServer();
        log.info("加载语言配置文件...");


        try {
            TranslationContext.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        log.info("加载数据库...");
        SqlUtils.initDataSource();
        if (ServerModContext.dataSource == null) {
            log.error("请检查数据库配置");
            throw new RuntimeException();
        }
    }

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandTabSupport.initCommands(event.getDispatcher());
    }


}
