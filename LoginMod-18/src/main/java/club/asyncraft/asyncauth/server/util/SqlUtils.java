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
import java.util.function.Function;

@Slf4j
public class SqlUtils {

    private static String tableName;

    private static String remotePasswordCache;

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
    }

    public static boolean isRegistered(Player player) {
        return Boolean.TRUE.equals(executeStatement((statement) -> {
            String sql = "SELECT password from " + tableName + " where realname= '" + player.getName().getString() + "'";
            try {
                ResultSet result = statement.executeQuery(sql);
                if (result.next()) {
                    remotePasswordCache = result.getString("password");
                    result.close();
                    return true;
                }
                result.close();
                return false;

            } catch (SQLException e) {
                log.error(e.getLocalizedMessage());
            }
            return null;
        }));
    }

    public static boolean checkPassword(String username, String password) {
        String encodedPassword = PasswordEncoder.encodePassword(password, resolveAuthmePassword(remotePasswordCache).getRight());
        if (!StringUtils.isEmpty(remotePasswordCache)) {
            return encodedPassword.equals(remotePasswordCache);
        } else {
            String remotePassword = executeStatement((statement -> {
                try {
                    ResultSet resultSet = statement.executeQuery("SELECT password from " + tableName + " where realname= '" + username + "'");
                    if (resultSet.next()) {
                        resultSet.close();
                        return resultSet.getString("password");
                    }
                } catch (SQLException e) {
                    log.error(e.getLocalizedMessage());
                }
                return null;
            }));
            if (StringUtils.isEmpty(remotePassword)) {
                return false;
            }
            return encodedPassword.equals(remotePassword);
        }
    }

    private static <V> V executeStatement(Function<Statement, V> statementFunction) {
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
            log.info(e.getLocalizedMessage());
        }
        return null;
    }

    private static <V> V execute(Function<Connection, V> statementFunction) {
        if (ServerModContext.dataSource == null) {
            return null;
        }
        try {
            Connection connection = ServerModContext.dataSource.getConnection();
            V result = statementFunction.apply(connection);
            connection.close();
            return result;
        } catch (SQLException e) {
            log.info(e.getLocalizedMessage());
        }
        return null;
    }

    //left password, right salt
    private static Pair<String, String> resolveAuthmePassword(String data) {
        String salt = data.substring(5, 21);
        String password = data.substring(22);
        return Pair.of(password, salt);
    }

}
