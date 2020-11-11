import org.junit.Test;

import java.sql.*;

/**
 * @author: Lenovo
 * @date:Create：in 2020/9/16 14:21
 */
public class ClickHouseNativeJDBCTest {

    @Test
    public void test1() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:clickhouse://192.168.251.10:9000");

        Statement statement = conn.createStatement();
        final ResultSet resultSet = statement.executeQuery("select * from hdfs_test_tbl limit 10");
        while (resultSet.next()){
            final long aLong = resultSet.getLong(0);
            System.out.println(aLong);
        }

    }


}
