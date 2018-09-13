package org.cboard.services;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TestData {
    public static Connection getConnection() throws Exception {
        String driver = ("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String jdbcurl = ("jdbc:sqlserver://localhost:1435;DatabaseName=test");
        String username = ("su");
        String password = ("123456");

        Class.forName(driver);
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);

        return DriverManager.getConnection(jdbcurl, props);
    }

    public static void main(String args[]) throws Exception {


        Connection con = null;

        PreparedStatement ps = null;
        ResultSet rs = null;
        List<String[]> list = null;

        try {
            con = getConnection();
            ps = con.prepareStatement("select * from message where description='内容'");
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            list = new LinkedList<>();
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = metaData.getColumnLabel(i + 1);
            }
            list.add(row);
            while (rs.next()) {
                row = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    row[j] = rs.getString(j + 1);
                }
                list.add(row);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception e) {
                }
            }
        }
    }
}
