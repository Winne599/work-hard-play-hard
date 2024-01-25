package sg.lwx.work.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusProcessor {

    private static final String JDBC_URL = "jdbc:your_database_url";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    public void processStatusData() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD)) {
            connection.setAutoCommit(false);

            // 查询符合条件的数据
            String selectQuery = "SELECT id, status, version FROM your_table WHERE status IS NULL";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        int currentVersion = resultSet.getInt("version");

                        // 再次查询确认数据仍然满足条件
                        String reselectQuery = "SELECT status FROM your_table WHERE id = ? AND version = ?";
                        try (PreparedStatement reselectStatement = connection.prepareStatement(reselectQuery)) {
                            reselectStatement.setInt(1, id);
                            reselectStatement.setInt(2, currentVersion);

                            try (ResultSet reselectResultSet = reselectStatement.executeQuery()) {
                                if (reselectResultSet.next()) {
                                    // 数据仍然满足条件，执行更新操作
                                    String updateQuery = "UPDATE your_table SET status = 0, version = ? WHERE id = ?";
                                    try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                                        updateStatement.setInt(1, currentVersion + 1);
                                        updateStatement.setInt(2, id);
                                        updateStatement.executeUpdate();
                                        connection.commit();
                                        System.out.println("Status updated successfully for id: " + id);
                                    }
                                } else {
                                    // 数据已经被其他任务更新，跳过
                                    System.out.println("Skipped processing for id: " + id);
                                }
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        StatusProcessor statusProcessor = new StatusProcessor();
        statusProcessor.processStatusData();
    }
}

