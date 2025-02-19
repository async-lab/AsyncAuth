package club.asynclab.asyncraft.asyncauth.constant;

import lombok.Getter;

/**
 * 客户端文本信息常量
 */
@Getter
public enum ClientTextConstants {

    OLD_PASSWORD_WRONG("msg.asyncauth.old_password_wrong"),
    CHANGE_PASSWORD_SUCCEED("msg.asyncauth.change_password_succeed"),
    PASSWORD_CHANGED_BY_ADMIN("msg.asyncauth.password_changed_by_admin"),
    Change_PASSWORD_Too_Short("msg.asyncauth.new_password_too_short"),
    PASSWORD_CONTAINS_SPACE("msg.asyncauth.password_contain_space");

    final String key;

    ClientTextConstants(String key) {
        this.key = key;
    }

}
