package club.asynclab.asyncraft.asyncauth;

import club.asynclab.asyncraft.asyncauth.util.PasswordEncoder;
import club.asynclab.asyncraft.asyncauth.util.SqlUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {

    private static String tableName;

    private static final Map<String, String> playerPasswordCache = new HashMap<>();
    private static final Map<String,String> playerNameCache = new HashMap<>();

    public static void init() {

        try {
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://" + ModConfig.address.get() + ":" + ModConfig.port.get() + "/" + ModConfig.database.get() + "?characterEncoding=utf8&useSSL=" + ModConfig.useSSL.get());
            config.setUsername(ModConfig.username.get());
            config.setPassword(ModConfig.password.get());
            AsyncAuth.dataSource = new HikariDataSource(config);
            tableName = ModConfig.tableName.get();
            log().info("Database connected");
        } catch (Exception e) {
            log().error("Database connection failure",e);
            AsyncAuth.dataSource = null;
        }

        SqlUtils.executeStatement(statement -> {
            String sql = "create table if not exists " + tableName + " (" +
                    "id bigint(20) not null auto_increment PRIMARY KEY ," +
                    "username varchar(50) not null ," +
                    "realname varchar(50) not null ," +
                    "password varchar(255) not null ," +
                    "index(username)" +
                    ");";
            return statement.executeUpdate(sql);
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
        if (isRegistered(player)) return false;

        String encodedPassword = PasswordEncoder.encodePassword(password);
        String sql = "INSERT INTO " + tableName + " (username, realname, password) VALUES (?, ?, ?)";

        int result = SqlUtils.executeUpdate(sql, stmt -> {
            stmt.setString(1, player.getName().getString().toLowerCase());
            stmt.setString(2, player.getName().getString());
            stmt.setString(3, encodedPassword);
        });

        if (result > 0) {
            playerPasswordCache.put(player.getStringUUID(), encodedPassword);
            return true;
        }
        return false;
    }

    public static boolean changePassword(Player player, String password) {
        if (!isRegistered(player)) return false;

        String encodedPassword = PasswordEncoder.encodePassword(password);
        String sql = "UPDATE " + tableName + " SET password = ? WHERE username = ?";

        int result = SqlUtils.executeUpdate(sql, stmt -> {
            stmt.setString(1, encodedPassword);
            stmt.setString(2, player.getName().getString().toLowerCase());
        });

        if (result > 0) {
            playerPasswordCache.put(player.getStringUUID(), encodedPassword);
            return true;
        }
        return false;
    }

    private static String getRawPassword(Player player) {
        String uuid = player.getStringUUID();
        if (playerPasswordCache.containsKey(uuid)) {
            return playerPasswordCache.get(uuid);
        }

        String sql = "SELECT password FROM " + tableName + " WHERE username = ?";
        return SqlUtils.executeQuery(sql, stmt -> {
            stmt.setString(1, player.getName().getString().toLowerCase());
        }, rs -> {
            if (rs.next()) {
                return rs.getString("password");
            }
            return null;
        });
    }

    /**
     * 检查玩家名是否有效
     */
    public static boolean checkPlayerName(String playerName) {
        String existedName = playerNameCache.get(playerName.toLowerCase());
        if (existedName != null) {
            return existedName.equals(playerName);
        }

        String sql = "SELECT realname FROM " + tableName + " WHERE username = ?";
        String realname = SqlUtils.executeQuery(sql, stmt -> {
            stmt.setString(1, playerName.toLowerCase());
        }, rs -> {
            if (rs.next()) return rs.getString("realname");
            return null;
        });
        if (realname == null) return true;
        playerNameCache.put(playerName.toLowerCase(),realname);
        return realname.equals(playerName);
    }
    
    private static Logger log() {
        return AsyncAuth.LOGGER;
    }
    
}
