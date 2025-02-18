package club.asynclab.asyncraft.asyncauth.util;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import org.slf4j.Logger;

import java.sql.*;
import java.util.function.Consumer;

public class SqlUtils {

    public static <V> V executeStatement(ThrowableFunction<Statement, V> statementFunction) {
        if (AsyncAuth.dataSource == null) {
            return null;
        }
        try {
            Connection connection = AsyncAuth.dataSource.getConnection();
            Statement statement = connection.createStatement();
            V result = statementFunction.apply(statement);
            statement.close();
            connection.close();
            return result;
        } catch (SQLException e) {
            log().error(e.getLocalizedMessage());
        }
        return null;
    }

    public static int executeUpdate(String sql, ThrowableConsumer<PreparedStatement> paramSetter) {

        try (Connection conn = AsyncAuth.dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            paramSetter.accept(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            log().error(e.getLocalizedMessage());
            return 0;
        }
    }

    public static <T> T executeQuery(String sql,
                                     ThrowableConsumer<PreparedStatement> paramSetter,
                                     ThrowableFunction<ResultSet, T> resultHandler) {

        try (Connection conn = AsyncAuth.dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            paramSetter.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                return resultHandler.apply(rs);
            }
        } catch (SQLException e) {
            log().error(e.getLocalizedMessage());
            return null;
        }
    }

    public static <V> V execute(ThrowableFunction<Connection, V> statementFunction) {
        if (AsyncAuth.dataSource == null) {
            return null;
        }
        try {
            Connection connection = AsyncAuth.dataSource.getConnection();
            V result = statementFunction.apply(connection);
            connection.close();
            return result;
        } catch (SQLException e) {
            log().error(e.getLocalizedMessage());
        }
        return null;
    }

    @FunctionalInterface
    public interface ThrowableFunction<T, R> {
        R apply(T t) throws SQLException;
    }

    @FunctionalInterface
    public interface ThrowableConsumer<T> {
        void accept(T t) throws SQLException;
    }

    private static Logger log() {
        return AsyncAuth.LOGGER;
    }

}
