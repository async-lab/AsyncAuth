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

    public static final ForgeConfigSpec.ConfigValue<String> language;

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

        language = builder.comment("same with files in locals directory").define("lang","zh-CN");

        SPEC = builder.build();
    }

}
