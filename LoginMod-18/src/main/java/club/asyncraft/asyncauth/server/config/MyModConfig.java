package club.asyncraft.asyncauth.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class MyModConfig {

    public static final ForgeConfigSpec SPEC;

    public static final String DATABASE_TABLE = "database";
    public static final ForgeConfigSpec.ConfigValue<Integer> port;
    public static final ForgeConfigSpec.ConfigValue<String> username;
    public static final ForgeConfigSpec.ConfigValue<String> password;
    public static final ForgeConfigSpec.ConfigValue<String> database;
    public static final ForgeConfigSpec.ConfigValue<String> tableName;
    public static final ForgeConfigSpec.ConfigValue<Boolean> useSSL;
    public static final ForgeConfigSpec.ConfigValue<String> address;

    public static final String MSG_TABLE = "message";
    public static final ForgeConfigSpec.ConfigValue<String> prefix;
    public static final ForgeConfigSpec.ConfigValue<String> alreadyLogin;
    public static final ForgeConfigSpec.ConfigValue<String> unRegisteredMsg;
    public static final ForgeConfigSpec.ConfigValue<String> loginCommandUsage;
    public static final ForgeConfigSpec.ConfigValue<String> passwordIncorrect;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push(DATABASE_TABLE);
        port = builder.comment("mysql port")
                .defineInRange("port",3306,1,65535);
        username = builder.define("username","authme");
        password = builder.define("password","123456");
        database = builder.comment("mysql database name")
                .define("database","authme");
        tableName = builder.define("tableName","authme");
        useSSL = builder.define("useSSL",true);
        address = builder.define("address","127.0.0.1");
        builder.pop();

        builder.push(MSG_TABLE);
        alreadyLogin = builder.define("already_login","&c您已经登录了");
        prefix = builder.define("prefix","&b&lAsyncraft &r&e» ");
        unRegisteredMsg = builder.define("un_registered","&c您还未注册，请先注册");
        loginCommandUsage = builder.define("login_command_usage","&c用法: {cmd} <你的密码>");
        passwordIncorrect = builder.define("password.incorrect","&c密码错误");
        builder.pop();

        SPEC = builder.build();
    }

}
