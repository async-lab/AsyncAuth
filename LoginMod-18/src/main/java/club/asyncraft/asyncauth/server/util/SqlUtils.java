package club.asyncraft.asyncauth.server.util;

import club.asyncraft.asyncauth.server.ServerModContext;
import club.asyncraft.asyncauth.server.config.MyModConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class SqlUtils {

    private static String tableName;

    private static final Map<String, String> playerPasswordCache = new HashMap<>();

    public static void initDataSource() {

        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://" + MyModConfig.address.get() + ":" + MyModConfig.port.get() + "/" + MyModConfig.database.get() + "?characterEncoding=utf8&useSSL=" + MyModConfig.useSSL.get());
            config.setUsername(MyModConfig.username.get());
            config.setPassword(MyModConfig.password.get());
            ServerModContext.dataSource = new HikariDataSource(config);
            tableName = MyModConfig.tableName.get();
            log.info("数据库已连接");
        } catch (Exception e) {
            log.info("连接数据库失败");
            e.printStackTrace();
        }

        executeStatement(statement -> {
            String sql = "create table if not exists '" + tableName + "' (" +
                    "'id' bigint(20) not null auto_increment PRIMARY KEY ," +
                    "'username' varchar(50) not null ," +
                    "'realname' varchar(50) not null ," +
                    "'password' varchar(255) not null ," +
                    "index('username')" +
                    ");";
            statement.executeUpdate(sql);
            return null;
        });
    }

    public static boolean isRegistered(Player player) {
        return Boolean.TRUE.equals(getRawPassword(player) != null);
    }

    public static boolean checkPassword(Player player, String password) {
        if (StringUtils.isEmpty(password)) {
            return false;
        }
        String rawPassword = getRawPassword(player);
        String encodedPassword = PasswordEncoder.encodePassword(password, PasswordEncoder.resolvePassword(rawPassword).getRight());
        return encodedPassword.equals(rawPassword);
    }

    public static boolean register(Player player, String password) {
        if (isRegistered(player)) {
            return false;
        }
        String encodedPassword = PasswordEncoder.encodePassword(password);
        String sql = "INSERT INTO " + tableName + "(username,realname,password) values ('" +
                player.getName().getString().toLowerCase() + "','" +
                player.getName().getString() + "','" +
                encodedPassword + "');";
        executeStatement(statement -> {
            statement.executeUpdate(sql);
            return null;
        });
        playerPasswordCache.put(player.getStringUUID(), encodedPassword);
        return true;
    }

    public static boolean changePassword(Player player, String password) {
        if (!isRegistered(player)) {
            return false;
        }
        String encodedPassword = PasswordEncoder.encodePassword(password);
        String sql = "UPDATE " + tableName + " set password =" + "'" + encodedPassword + "' where username = '" + player.getName().getString().toLowerCase() + "';";
        executeStatement(statement -> {
            statement.executeUpdate(sql);
            return null;
        });
        String uuid = player.getStringUUID();
        if (playerPasswordCache.containsKey(uuid)) {
            playerPasswordCache.replace(uuid, encodedPassword);
        } else {
            playerPasswordCache.put(uuid, encodedPassword);
        }
        return true;
    }

    private static String getRawPassword(Player player) {
        String uuid = player.getStringUUID();
        if (playerPasswordCache.containsKey(uuid)) {
            return playerPasswordCache.get(uuid);
        }
        return executeStatement((statement -> {
            String sql = "SELECT password from " + tableName + " where username= '" + player.getName().getString().toLowerCase() + "'";
            ResultSet result = statement.executeQuery(sql);
            if (result.next()) {
                String password = result.getString("password");
                playerPasswordCache.put(player.getStringUUID(), password);
                result.close();
                return password;
            }
            result.close();
            return null;
        }));
    }

    private static <V> V executeStatement(ThrowingFunction<Statement, V> statementFunction) {
        if (ServerModContext.dataSource == null) {
            return null;
        }
        try {
            Connection connection = ServerModContext.dataSource.getConnection();
            Statement statement = connection.createStatement();
            V result = statementFunction.apply(statement);
            statement.close();
            connection.close();
            return result;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }

    private static <V> V execute(ThrowingFunction<Connection, V> statementFunction) {
        if (ServerModContext.dataSource == null) {
            return null;
        }
        try {
            Connection connection = ServerModContext.dataSource.getConnection();
            V result = statementFunction.apply(connection);
            connection.close();
            return result;
        } catch (SQLException e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }

    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws SQLException;
    }

}
