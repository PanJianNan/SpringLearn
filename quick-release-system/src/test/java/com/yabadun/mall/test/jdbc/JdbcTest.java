package com.yabadun.mall.test.jdbc;

import org.junit.Test;

import java.sql.*;

import static org.junit.Assert.*;

/**
 * Created by panjiannan on 2018/8/1.
 */
public class JdbcTest {
    @Test
    public void test() {
        try {
            // 1.加载驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            //2.创建连接
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?characterEncoding=utf-8", "root", "123456");
            conn.setAutoCommit(false);//关闭事务自动提交
            //3.创建statment
            stmt = conn.createStatement();
            //PreparedStatement pstmt = conn.prepareStatement("CREATE TABLE test_table(id INT(11) PRIMARY KEY , name VARCHAR(20))");
            //CallableStatement cstmt =  conn.prepareCall("{CALL demoSp(? , ?)}") ;

            //4.执行SQL语句
            try {
                stmt.execute("CREATE TABLE test_table(id INT(11) PRIMARY KEY , name VARCHAR(20))");
            } catch (SQLException e) {
                System.err.println("创建表失败！");
                e.printStackTrace();
            }
            int update = stmt.executeUpdate("UPDATE test_table SET name ='潘建南' WHERE id =1");
            System.out.println(update + " cows update");
            conn.commit();//手动提交事务

            //5.遍历结果集
            rs = stmt.executeQuery("SELECT * FROM test_table");
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                System.out.println("id:" + id + " name:" + name);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //6.处理异常，关闭JDBC对象资源
//            操作完成以后要把所有使用的JDBC对象全都关闭，以释放JDBC资源，关闭顺序和声
//            明顺序相反：
//            1、先关闭requestSet
//            2、再关闭preparedStatement
//            3、最后关闭连接对象connection
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}