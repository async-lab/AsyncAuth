package club.asynclab.asyncraft.asyncauth;

import club.asynclab.asyncraft.asyncauth.command.AdminCommands;
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import javax.sql.DataSource;

@Mod("asyncauth")
@SuppressWarnings("removal")
public class AsyncAuth {

    public static final String MODID = "asyncauth";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static boolean isEnabled = false;
    public static DataSource dataSource;

    public AsyncAuth() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        NetworkHandler.init();

        NetworkHandler.registerPackets();

//        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,() -> NetworkHandler::registerClientPackets);

        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
            MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER , club.asynclab.asyncraft.asyncauth.ModConfig.SPEC,"AsyncAuth/config.toml");
        });
    }

    private void setup(final FMLCommonSetupEvent event) {

        LOGGER.info("AsyncAuth loaded");
    }

    private void onServerStarting(ServerAboutToStartEvent event) {
        DatabaseManager.init();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        LiteralArgumentBuilder<CommandSourceStack> main = Commands.literal("asyncauth")
                .then(AdminCommands.adminCommand());

        dispatcher.register(
                Commands.literal("aa").redirect(dispatcher.register(main))
        );

        LOGGER.info("Commands registered");
    }

}
