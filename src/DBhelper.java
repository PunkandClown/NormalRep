import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.*;

class DBhelper {
    public static boolean PutBasePerson(Connection conn, String Nickname, String Name, String Password){
    try {
        if(!callRegister(conn, Nickname)){
            System.out.println("Создание акка");
            put(conn, Nickname,Name,Password);
            get(conn);
            return true;
        } else {
            return false;
        }
    } catch (SQLException throwables) {
        throwables.printStackTrace();
    }
        return false;
    }

    public static boolean LoginTrueFalse(Connection conn, String Nickname, String Password) throws SQLException{
        return callLogin(conn, Nickname, Password);
    }
    static String getUser(Connection conn, String nicknameUserForInfo, String poster) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("select * from users where Nickname = ?");
        preparedStatement.setString(1, nicknameUserForInfo);
        ResultSet resultSet = preparedStatement.executeQuery();
        StringBuilder SB = new StringBuilder();
        while (resultSet.next()) {
            SB.append("{\n");
            SB.append("\"Poster\":" + "\"" + poster + "\"" + ",\n");
            SB.append("\"Nickname\":" + "\"" + resultSet.getString("Nickname") + "\"" + ",\n");
            SB.append("\"Name\":" + "\"" + resultSet.getString("Name") + "\"" + "\n");
            SB.append("}");
        }
        return SB.toString();
    }


    static void get(Connection conn) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("select * from users");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("Nickname"));
        }
    }
    static String getTimelastMessage(Connection conn) throws SQLException {
         PreparedStatement preparedStatement = conn.prepareStatement("select * from messagetable order by idMessageTable DESC LIMIT 1");
        ResultSet resultSet = preparedStatement.executeQuery();
        StringBuilder SB = new StringBuilder();
        SB.append( "[\n" +"{\n" + "\"Time\":" + "\"");
        while (resultSet.next()){
            SB.append(resultSet.getString("Time"));
        }
        SB.append("\"" +"\n} \n" + "]\n");
        return SB.toString();
    }


    static void putMessage(Connection conn, String name, String data, String textMessage) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("insert into messagetable(Nickname,Time,TextMessage) values(?,?,?)");
        preparedStatement.setString(1,name);
        preparedStatement.setString(2,data);
        preparedStatement.setString(3,textMessage);
        preparedStatement.execute();
    }
    public static String toJsonParser(ResultSet resultSet) throws SQLException {
        StringBuilder SB = new StringBuilder();
        SB.append("[\n");
        while (resultSet.next()) {
            SB.append("{\n");
            SB.append("\"Nickname\":" + "\"" + resultSet.getString("Nickname") + "\"" + ",\n");
            SB.append("\"Time\":" + "\"" + resultSet.getString("Time") + "\"" + ",\n");
            SB.append("\"TextMessage\":" + "\"" + resultSet.getString("TextMessage") + "\"" + "\n");
            SB.append("},");
        }
        SB.setLength(SB.length() - 1);
        SB.append("\n");
        SB.append("]\n");
        return SB.toString();
    }


    static String getAllOrOneMessage(Connection conn, int idMessageinDB) throws SQLException {
        String SQL = "";
        if(idMessageinDB == 2){
            SQL = "SELECT * FROM (SELECT * FROM messagetable ORDER BY idMessageTable DESC LIMIT 10) t ORDER BY idMessageTable;";
        } else if(idMessageinDB == 1) SQL = "select * from messagetable order by idMessageTable DESC LIMIT 1";
        PreparedStatement preparedStatement = conn.prepareStatement(SQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        return toJsonParser(resultSet);
    }
    static String getAllLsMessage(Connection conn, String table, int confget) throws SQLException {
        String SQL = "";
        System.out.println(table);
        if(confget == 2){
            SQL = String.format("SELECT * FROM (SELECT * FROM %s ORDER BY id DESC LIMIT 10) t ORDER BY id",table);
        } else if(confget == 1) {
            SQL = String.format("select * from %s order by id DESC LIMIT 1;",table);
        }
        PreparedStatement preparedStatement = conn.prepareStatement(SQL);
        ResultSet resultSet = preparedStatement.executeQuery();
        return toJsonParser(resultSet);
    }
    public static void putLsMessage(Connection conn, String table, String poster, String recievier,String Time, String textMessage) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement("insert into ?(Poster, Recievier, Time, TextMessage) values(?,?,?,?)");
        preparedStatement.setString(1,table);
        preparedStatement.setString(2,poster);
        preparedStatement.setString(3,recievier);
        preparedStatement.setString(4,textMessage);
        preparedStatement.execute();
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
        callableStatement.registerOutParameter(2, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(2);
    }
    static boolean callLogin(Connection conn, String Nickname, String Password) throws SQLException {
        CallableStatement callableStatement = conn.prepareCall("call userNicknamePassEx(?,?,?)");
        callableStatement.setString(1, Nickname);
        callableStatement.setString(2, Password);
        callableStatement.registerOutParameter(3, Types.TINYINT);
        callableStatement.execute();
        return callableStatement.getBoolean(3);
    }
}