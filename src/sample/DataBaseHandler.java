package sample;

import java.sql.*;

public class DataBaseHandler
{
        private String dbHost = "localhost";
        private String dbPort = "3306";
        private String dbUser = "root";
        private String dbPass = "12345";
        private String dbName = "vovanshema";

        public static final String USER_TABLE = "users";
        public static final String USER_ID = "idUsers";
        public static final String USER_EMail = "Email";
        public static final String USER_Password = "Password";
        public static final String USER_NickName = "NickName";

        public Connection dbConnection;

        public Connection getDbConnection () throws ClassNotFoundException, SQLException
        {
            String connectionString = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName + "?useUnicode=true&serverTimezone=UTC";

            Class.forName("com.mysql.cj.jdbc.Driver");

            dbConnection = DriverManager.getConnection(connectionString, dbUser, dbPass);

            System.out.println("Подключение к бд выполнено");
            return dbConnection;
        }

        public void signUpUser (User user)
        {
            String insert = "INSERT INTO " + USER_TABLE + " (" + USER_EMail + ", " + USER_Password + ", " + USER_NickName + ") " + "VALUES (?,?,?)";

            try {
                PreparedStatement prSt = getDbConnection().prepareStatement(insert);
                prSt.setString(1, user.geteMail());
                prSt.setString(2, user.getPassword());
                prSt.setString(3, user.getNickName());
                prSt.executeUpdate();
                System.out.println("Запись в базу данных прошла успешно");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public ResultSet getUser (User user)
        {
            ResultSet resSet = null;

            String select = "SELECT * FROM " + USER_TABLE + " WHERE " + USER_EMail + "=? AND " + USER_Password + "=?";

            try {
                PreparedStatement prSt = getDbConnection().prepareStatement(select);
                prSt.setString(1, user.geteMail());
                prSt.setString(2, user.getPassword());
                resSet = prSt.executeQuery();
                System.out.println("Проверка в базе данных прошла успешно");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return resSet;
        }


}

