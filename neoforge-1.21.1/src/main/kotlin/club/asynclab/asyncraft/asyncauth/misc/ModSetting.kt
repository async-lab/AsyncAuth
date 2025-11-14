package club.asynclab.asyncraft.asyncauth.misc

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerTokenServer
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.event.config.ModConfigEvent
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

object ModSetting {
    private val SPEC: ModConfigSpec
    private var cachedEnabled = true

    val enabled: ModConfigSpec.ConfigValue<Boolean>
    val minLength: ModConfigSpec.ConfigValue<Int>
    val timeout: ModConfigSpec.ConfigValue<Int>
    val tokenExpiryMinutes: ModConfigSpec.ConfigValue<Int>

    private const val DATABASE_TABLE: String = "database"
    val address: ModConfigSpec.ConfigValue<String>
    val port: ModConfigSpec.ConfigValue<Int>
    val database: ModConfigSpec.ConfigValue<String>
    val table: ModConfigSpec.ConfigValue<String>
    val useSSL: ModConfigSpec.ConfigValue<Boolean>
    val username: ModConfigSpec.ConfigValue<String>
    val password: ModConfigSpec.ConfigValue<String>

    init {
        val builder = ModConfigSpec.Builder()

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

        val context = ModLoadingContext.get()
        context.activeContainer.registerConfig(ModConfig.Type.SERVER, SPEC, "${BuiltConstantsCommon.MOD_ID}-server.toml")
        cachedEnabled = enabled.default
        MOD_BUS.addListener(ModConfigEvent::class.java, ::onConfigEvent)
    }

    private fun onConfigEvent(event: ModConfigEvent) {
        if (event.config.modId == BuiltConstantsCommon.MOD_ID && event.config.type == ModConfig.Type.SERVER) {
            cachedEnabled = if (event.config.loadedConfig != null) enabled.get() else enabled.default
        }
    }

    fun isEnabled(): Boolean = cachedEnabled

    fun onServerAboutToStart(event: ServerAboutToStartEvent) {
        ServerLoginPacketListenerImpl.MAX_TICKS_BEFORE_LOGIN = timeout.get() * 20
        ManagerTokenServer.configure(tokenExpiryMinutes.get())
    }
}
