package club.asynclab.asyncraft.asyncauth.misc

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import net.minecraft.server.network.ServerLoginPacketListenerImpl
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue
import net.minecraftforge.event.server.ServerAboutToStartEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

object ModSetting {
    private val SPEC: ForgeConfigSpec

    private const val DATABASE_TABLE: String = "database"
    val port: ConfigValue<Int>
    val username: ConfigValue<String>
    val password: ConfigValue<String>
    val database: ConfigValue<String>
    val table: ConfigValue<String>
    val useSSL: ConfigValue<Boolean>
    val address: ConfigValue<String>
    val minLength: ConfigValue<Int>
    val timeout: ConfigValue<Int>

    init {
        val builder = ForgeConfigSpec.Builder()

        builder.push(DATABASE_TABLE)
        address = builder.define("address", "127.0.0.1")
        port = builder.define("port", 3306)
        database = builder.define("database", "asyncauth")
        table = builder.define("table", "asyncauth")
        useSSL = builder.define("useSSL", true)
        username = builder.define("username", "user")
        password = builder.define("password", "123456")
        builder.pop()

        minLength = builder.comment("密码最小长度").defineInRange("minLength", 6, 6, 50)
        timeout = builder.comment("连接超时时间").define("timeout", 180)
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
    }
}