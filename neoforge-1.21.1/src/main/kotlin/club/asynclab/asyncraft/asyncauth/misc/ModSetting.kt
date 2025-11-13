package club.asynclab.asyncraft.asyncauth.misc

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent

object ModSetting {
    private val SPEC: ModConfigSpec

    val enabled: ModConfigSpec.ConfigValue<Boolean>
    val minLength: ModConfigSpec.ConfigValue<Int>
    val timeout: ModConfigSpec.ConfigValue<Int>

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

        ModLoadingContext.get().activeContainer
            .registerConfig(ModConfig.Type.SERVER, SPEC, "${BuiltConstantsCommon.MOD_ID}-server.toml")
    }

    fun onServerAboutToStart(event: ServerAboutToStartEvent) {
//        ServerLoginPacketListenerImpl.MAX_TICKS_BEFORE_LOGIN = timeout.get() * 20
    }
}
