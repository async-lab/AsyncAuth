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

    public static final ForgeConfigSpec.ConfigValue<Integer> ticks;

    public static final ForgeConfigSpec.ConfigValue<String> language;

    public static final ForgeConfigSpec.ConfigValue<Integer> minLength;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push(DATABASE_TABLE);
        port = builder.comment("mysql port")
                .defineInRange("port",3306,1,65535);
        username = builder.define("username","user");
        password = builder.define("password","123456");
        database = builder.comment("mysql database name")
                .define("database","asyncauth");
        tableName = builder.define("tableName","asyncauth");
        useSSL = builder.define("useSSL",true);
        address = builder.define("address","127.0.0.1");
        builder.pop();

        ticks = builder.comment("发送登录信息间隔时间(单位: tick)").defineInRange("ticks",40,1,200);

        language = builder.comment("与locals文件中的文件名保持一致").define("lang","zh-CN");

        minLength = builder.comment("密码最小长度").defineInRange("minLength",6,6,50);

        SPEC = builder.build();
    }

}
