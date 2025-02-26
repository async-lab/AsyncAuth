package club.asynclab.asyncraft.asyncauth.misc

import club.asynclab.asyncraft.asyncauth.common.manager.ManagerAuth
import club.asynclab.asyncraft.asyncauth.common.manager.ManagerDb
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.event.server.ServerAboutToStartEvent

object ModContext {

    @OnlyIn(Dist.DEDICATED_SERVER)
    object Server {
        lateinit var MANAGER_AUTH: ManagerAuth

        fun onServerAboutToStart(event: ServerAboutToStartEvent) {
            this.MANAGER_AUTH = ManagerAuth(
                ManagerDb(
                    ModSetting.address.get(),
                    ModSetting.port.get(),
                    ModSetting.database.get(),
                    ModSetting.table.get(),
                    ModSetting.useSSL.get(),
                    ModSetting.username.get(),
                    ModSetting.password.get()
                )
            )
        }
    }
}