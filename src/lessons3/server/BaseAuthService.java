package lessons3.server;

import java.sql.*;
import java.util.List;

public class BaseAuthService implements AuthService {

    private Connection conn = null;

    @Override
    public void start() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lessons", "root", "123qRT!");
        } catch (ClassNotFoundException | SQLException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
    }

    @Override
    public void stop() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
    }

    @Override
    public String getNickByLoginPass(String login, String password) throws SQLException {

        PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM new_table WHERE login = ? AND password = ?");
        preparedStatement.setString(1, login);
        preparedStatement.setString(2, password);

        ResultSet set = preparedStatement.executeQuery();

        if (set.next()) {
            return set.getString("nick");
        }

        return null;
    }
}
