package club.asynclab.asyncraft.asyncauth.common.misc

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import java.nio.file.Paths
import java.util.*
import kotlin.reflect.KProperty

object Lang {
    open class Category(private val name: String) {
        protected fun build(path: String) = "${name}.${BuiltConstantsCommon.MOD_ID}.${path}"
        protected fun build() = Delegator()

        protected inner class Delegator {
            private lateinit var key: String
            private val pairs = mutableListOf<Pair<Locale, String>>()

            operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>) =
                this.apply { key = this@Category.build(prop.name.lowercase()).addTranslation(*pairs.toTypedArray()) }

            operator fun getValue(thisRef: Any?, prop: KProperty<*>) = this.key
            operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: String) {
                throw UnsupportedOperationException("Cannot set value of a Category.Delegator")
            }

            fun addTranslation(vararg pairs: Pair<Locale, String>) = this.apply { this.pairs.addAll(pairs) }
        }
    }

    object Auth : Category("auth") {
        val SUCCESS by this.build().addTranslation(
            Locale.PRC to "成功",
            Locale.US to "Successful"
        )
        val WRONG_PASSWORD by this.build().addTranslation(
            Locale.PRC to "密码错误",
            Locale.US to "Incorrect password"
        )
        val TOO_SHORT by this.build().addTranslation(
            Locale.PRC to "密码太短",
            Locale.US to "Password is to short"
        )
        val EMPTY by this.build().addTranslation(
            Locale.PRC to "密码不能为空",
            Locale.US to "Password cannot be empty"
        )
        val EXISTS by this.build().addTranslation(
            Locale.PRC to "用户已存在",
            Locale.US to "User already exists"
        )
        val NOT_EXISTS by this.build().addTranslation(
            Locale.PRC to "用户不存在",
            Locale.US to "User does not exist"
        )
        val UNKNOWN by this.build().addTranslation(
            Locale.PRC to "未知错误",
            Locale.US to "Unknown error"
        )

        fun from(status: AuthStatus) = when (status) {
            AuthStatus.SUCCESS -> SUCCESS
            AuthStatus.WRONG_PASSWORD -> WRONG_PASSWORD
            AuthStatus.TOO_SHORT -> TOO_SHORT
            AuthStatus.EMPTY -> EMPTY
            AuthStatus.EXISTS -> EXISTS
            AuthStatus.NOT_EXISTS -> NOT_EXISTS
            else -> UNKNOWN
        }
    }

    object Msg : Category("msg") {
        val UNAUTHORIZED by this.build().addTranslation(
            Locale.PRC to "未登录",
            Locale.US to "Unauthorized"
        )
        val PASSWORD_NOT_MATCH by this.build().addTranslation(
            Locale.PRC to "密码不一致",
            Locale.US to "Passwords do not match"
        )
    }

    object Gui : Category("gui") {
        val PASSWORD by this.build().addTranslation(
            Locale.PRC to "密码",
            Locale.US to "Password"
        )
        val CONFIRM_PASSWORD by this.build().addTranslation(
            Locale.PRC to "确认密码",
            Locale.US to "Confirm Password"
        )
        val TIMEOUT by this.build().addTranslation(
            Locale.PRC to "超时: %%d",
            Locale.US to "Timeout: %%d"
        )
        val LOGIN by this.build().addTranslation(
            Locale.PRC to "登录",
            Locale.US to "Log In"
        )
        val REGISTER by this.build().addTranslation(
            Locale.PRC to "注册",
            Locale.US to "Register"
        )
        val EXIT by this.build().addTranslation(
            Locale.PRC to "退出",
            Locale.US to "Exit"
        )
    }

    object Commands : Category("commands") {
        val PASSWORD_CHANGED by this.build().addTranslation(
            Locale.PRC to "密码已修改",
            Locale.US to "Password changed"
        )
    }

    private val langMap = HashMap<Locale, MutableList<Pair<String, String>>>()

    private fun String.addTranslation(vararg pairs: Pair<Locale, String>): String {
        pairs.forEach { (locale, translation) ->
            if (!langMap.containsKey(locale)) langMap[locale] = mutableListOf()
            langMap[locale]!!.add(this to translation)
        }
        return this
    }

    private fun stash() {
        this.langMap.forEach { (locale, entries) ->
            val path = Paths.get("${locale.toString().lowercase()}.json")
            path.toFile().writeText(
                entries.joinToString(
                    prefix = "{\n\t",
                    postfix = "\n}",
                    separator = ",\n\t"
                ) { (key, value) -> "\"$key\": \"$value\"" }
            )
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        Auth
        Msg
        Gui
        Commands
        stash()
    }
}
