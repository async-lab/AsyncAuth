package club.asynclab.asyncraft.asyncauth.constant;

/**
 * 登录结果码
 */
public interface LoginResultStatusCode {

    // 登录成功
    int LOGIN_SUCCESS = 1;

    // 密码错误
    int WRONG_PASSWORD = 2;

    // 注册时密码过短
    int REG_PASS_SHORT = 3;

}
