package club.asynclab.asyncraft.asyncauth.misc

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerTokenServer
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.event.server.ServerStoppedEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.config.ConfigTracker

object ModSetting {
    private val SPEC: ForgeConfigSpec

    val enabled: ConfigValue<Boolean>
    val minLength: ConfigValue<Int>
    val timeout: ConfigValue<Int>
    val tokenExpiryMinutes: ConfigValue<Int>

    private const val DATABASE_TABLE: String = "database"
    val address: ConfigValue<String>
    val port: ConfigValue<Int>
    val database: ConfigValue<String>
    val table: ConfigValue<String>
    val useSSL: ConfigValue<Boolean>
    val username: ConfigValue<String>
    val password: ConfigValue<String>

    init {
        val builder = ForgeConfigSpec.Builder()

        enabled = builder.define("enabled", true)
        timeout = builder.define("timeout", 180)
        tokenExpiryMinutes = builder.define("tokenExpiryMinutes", 10)
        minLength = builder.define("minLength", 6)

        builder.push(DATABASE_TABLE)
        address = builder.define("address", "127.0.0.1")
        port = builder.define("port", 3306)
        database = builder.define("database", "asyncauth")
        table = builder.define("table", "asyncauth")
        useSSL = builder.define("useSSL", true)
        username = builder.define("username", "user")
        password = builder.define("password", "123456")
        builder.pop()

        SPEC = builder.build()
    }

    fun init() {
//        FileUtils.getOrCreateDirectory(
//            FMLPaths.CONFIGDIR.get().resolve(BuiltConstantsCommon.MOD_ID),
//            BuiltConstantsCommon.MOD_ID
//        )
        ModLoadingContext.get()
            .registerConfig(ModConfig.Type.SERVER, SPEC, "${BuiltConstantsCommon.MOD_ID}-server.toml")
    }

    fun onServerAboutToStart(event: ServerAboutToStartEvent) {
        ServerLoginPacketListenerImpl.MAX_TICKS_BEFORE_LOGIN = timeout.get() * 20
        ManagerTokenServer.configure(tokenExpiryMinutes.get())
        val configDir = event.server.serverDirectory.toPath().resolve("config")
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, configDir)
    }

    fun onServerStopped(event: ServerStoppedEvent) {
        val configDir = event.server.serverDirectory.toPath().resolve("config")
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, configDir)
    }
}
