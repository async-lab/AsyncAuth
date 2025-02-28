package club.asynclab.asyncraft.asyncauth.common.misc

import club.asynclab.asyncraft.asyncauth.built.BuiltConstantsCommon
import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import java.nio.file.Paths
import java.util.*

class Lang {
    open class Category(private val name: String) {
        fun build(path: String) = "${name}.${BuiltConstantsCommon.MOD_ID}.${path}"
    }

    object Auth : Category("auth") {
        val SUCCESS = build("success").addTranslation(
            Locale.PRC to "成功",
            Locale.US to "Successful"
        )
        val WRONG_PASSWORD = build("wrong_password").addTranslation(
            Locale.PRC to "密码错误",
            Locale.US to "Incorrect password"
        )
        val TOO_SHORT = build("too_short").addTranslation(
            Locale.PRC to "密码太短",
            Locale.US to "Password is to short"
        )
        val EMPTY = build("empty").addTranslation(
            Locale.PRC to "密码不能为空",
            Locale.US to "Password cannot be empty"
        )
        val EXISTS = build("exists").addTranslation(
            Locale.PRC to "用户已存在",
            Locale.US to "User already exists"
        )
        val NOT_EXISTS = build("not_exists").addTranslation(
            Locale.PRC to "用户不存在",
            Locale.US to "User does not exist"
        )
        val UNKNOWN = build("unknown").addTranslation(
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
        val UNAUTHORIZED = build("unauthorized").addTranslation(
            Locale.PRC to "未登录",
            Locale.US to "Unauthorized"
        )
        val PASSWORD_NOT_MATCH = build("password_not_match").addTranslation(
            Locale.PRC to "密码不一致",
            Locale.US to "Passwords do not match"
        )
    }

    object Gui : Category("gui") {
        val PASSWORD = build("password").addTranslation(
            Locale.PRC to "密码",
            Locale.US to "Password"
        )
        val CONFIRM_PASSWORD = build("confirm_password").addTranslation(
            Locale.PRC to "确认密码",
            Locale.US to "Confirm Password"
        )
        val TIMEOUT = build("timeout").addTranslation(
            Locale.PRC to "超时: %%d",
            Locale.US to "Timeout: %%d"
        )
        val LOGIN = build("login").addTranslation(
            Locale.PRC to "登录",
            Locale.US to "Log In"
        )
        val REGISTER = build("register").addTranslation(
            Locale.PRC to "注册",
            Locale.US to "Register"
        )
        val EXIT = build("exit").addTranslation(
            Locale.PRC to "退出",
            Locale.US to "Exit"
        )
    }

    object Commands : Category("commands") {
        val PASSWORD_CHANGED = build("password_changed").addTranslation(
            Locale.PRC to "密码已修改",
            Locale.US to "Password changed"
        )
    }

    private companion object {
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
                    ) { (key, value) -> "  \"$key\": \"$value\"" }
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
}
