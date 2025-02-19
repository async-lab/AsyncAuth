package club.asynclab.asyncraft.asyncauth.constant;

import lombok.Getter;

/**
 * 客户端文本信息常量
 */
@Getter
public enum ClientTextConstants {

    OLD_PASSWORD_WRONG("msg.asyncauth.old_password_wrong"),
    CHANGE_PASSWORD_SUCCEED("msg.asyncauth.change_password_succeed"),
    PASSWORD_CHANGED_BY_ADMIN("Password was changed by the administrator");

    final String key;

    ClientTextConstants(String key) {
        this.key = key;
    }

}
