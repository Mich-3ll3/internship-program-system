package mx.uv.internshipprogramsystem.logic.test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.mockito.MockedStatic;

import mx.uv.internshipprogramsystem.dataaccess.DataBaseManager;

final class DaoTestSupport {
    private DaoTestSupport() {
    }

    static MockedStatic<DataBaseManager> mockDataBaseConnection(
            Connection connection
    ) throws SQLException {
        MockedStatic<DataBaseManager> dataBaseManager =
            mockStatic(DataBaseManager.class);

        dataBaseManager.when(DataBaseManager::getConnection)
            .thenReturn(connection);

        return dataBaseManager;
    }

    static void mockPreparedStatement(
            Connection connection,
            PreparedStatement statement
    ) throws SQLException {
        when(connection.prepareStatement(anyString()))
            .thenReturn(statement);
    }

    static void mockPreparedStatementWithGeneratedKeys(
            Connection connection,
            PreparedStatement statement
    ) throws SQLException {
        when(connection.prepareStatement(anyString(), anyInt()))
            .thenReturn(statement);
    }

    static ResultSet generatedKeys(int generatedId)
            throws SQLException {
        return resultSet(row("1", generatedId));
    }

    static ResultSet resultSet(Map<String, Object>... rows)
            throws SQLException {
        AtomicInteger index = new AtomicInteger(-1);

        return (ResultSet) Proxy.newProxyInstance(
            ResultSet.class.getClassLoader(),
            new Class<?>[] { ResultSet.class },
            (proxy, method, arguments) -> {
                String methodName = method.getName();

                if ("next".equals(methodName)) {
                    return index.incrementAndGet() < rows.length;
                }

                if ("close".equals(methodName)) {
                    return null;
                }

                if ("wasNull".equals(methodName)) {
                    return false;
                }

                if ("toString".equals(methodName)) {
                    return "ResultSet test double";
                }

                if (arguments == null || arguments.length == 0) {
                    return defaultValue(method.getReturnType());
                }

                Object value = rows[index.get()].get(String.valueOf(arguments[0]));

                return switch (methodName) {
                    case "getInt" -> asInt(value);
                    case "getString" -> asString(value);
                    case "getBoolean" -> asBoolean(value);
                    case "getDate" -> (Date) value;
                    case "getTime" -> (Time) value;
                    case "getTimestamp" -> (Timestamp) value;
                    default -> defaultValue(method.getReturnType());
                };
            }
        );
    }

    static Map<String, Object> row(Object... keyValues) {
        Map<String, Object> row = new LinkedHashMap<>();

        for (int index = 0; index < keyValues.length; index += 2) {
            row.put((String) keyValues[index], keyValues[index + 1]);
        }

        return row;
    }

    private static int asInt(Object value) {
        return ((Number) value).intValue();
    }

    private static String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private static boolean asBoolean(Object value) {
        return (Boolean) value;
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }

        if (boolean.class.equals(returnType)) {
            return false;
        }

        if (int.class.equals(returnType)) {
            return 0;
        }

        if (long.class.equals(returnType)) {
            return 0L;
        }

        if (double.class.equals(returnType)) {
            return 0D;
        }

        if (float.class.equals(returnType)) {
            return 0F;
        }

        if (short.class.equals(returnType)) {
            return (short) 0;
        }

        if (byte.class.equals(returnType)) {
            return (byte) 0;
        }

        if (char.class.equals(returnType)) {
            return '\0';
        }

        return null;
    }
}
