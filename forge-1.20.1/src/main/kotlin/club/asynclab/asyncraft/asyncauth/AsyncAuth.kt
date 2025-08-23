package club.asynclab.asyncraft.asyncauth

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import club.asynclab.asyncraft.asyncauth.util.Utils
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.fml.common.Mod
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Mod(BuiltConstantsCommon.MOD_ID)
object AsyncAuth {

    val LOGGER: Logger = LoggerFactory.getLogger(AsyncAuth::class.java)

    fun resourceLocation(path: String): ResourceLocation = ResourceLocation(BuiltConstantsCommon.MOD_ID, path)

    init {
        ModSetting.init()
        Utils.asciiArt()
    }
}
