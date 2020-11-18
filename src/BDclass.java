import com.sun.net.httpserver.HttpExchange;

import java.sql.*;

class BDclass{
    public static String url = "jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC";
    public static String usernameConnection = "root";
    public static String passwordConnection = "Qwerty123456";
    public static void BDhandlerUserSet(String Nickname, String Name, String Password, HttpExchange httpExchange, int logoutindex){
    try{
        try (Connection conn = DriverManager.getConnection(url, usernameConnection, passwordConnection)){
            if(logoutindex == 0){
                if(!callRegister(conn, Nickname)){
                    put(conn, Nickname,Name,Password);
                    get(conn);
                    MyHttpHandler.handleResponse(httpExchange, MyHttpHandler.login, 200);
                } else {
                    MyHttpHandler.handleResponse(httpExchange, MyHttpHandler.login, 204);
                }
            } else if(logoutindex == 1){
                LoginTrueFalse(conn,Nickname,Password);
            }
            conn.close();
        }
    }
        catch(Exception ex){
        System.out.println("Connection failed...");
        System.out.println(ex);
    }
}
    public static boolean LoginTrueFalse(Connection conn, String Nickname, String Password) throws SQLException {
            if(callLogin(conn,Nickname, Password)){
                System.out.println("Логин успешен");
            }

        return false;
    }



    static void get(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("select * from users");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("Nickname"));
        }
    }
    static void put(Connection conn, String Nickname, String Name, String Password) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("insert into users(Nickname,Name,Password) values(?,?,?)");
        preparedStatement.setString(1,Nickname);
        preparedStatement.setString(2,Name);
        preparedStatement.setString(3,Password);
        preparedStatement.execute();
    }
    static boolean callRegister(Connection conn, String Nickname) throws SQLException {
        CallableStatement callableStatement = conn.prepareCall("call userNicknameEx(?,?)");
        callableStatement.setString(1, Nickname);
        callableStatement.registerOutParameter(2, Types.BOOLEAN);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }
    static boolean callLogin(Connection conn, String Nickname, String Password) throws SQLException {
        CallableStatement callableStatement = conn.prepareCall("call userNicknamePassEx(?,?,?)");
        callableStatement.setString(1, Nickname);
        callableStatement.setString(2, Password);
        callableStatement.registerOutParameter(3, Types.BOOLEAN);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }
}