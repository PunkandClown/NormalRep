import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;


public class MyHttpHandler implements HttpHandler {
    public static String indexCSS = "Рабочие Html страницы\\indexCSS.css";
    public String  index = "Рабочие Html страницы\\index.html";
    public static String login = "Рабочие Html страницы\\login.html";
    public static String  main = "Рабочие Html страницы\\main.html";
    public static String  users = "Рабочие Html страницы\\users.html";
    public static String  lschat = "Рабочие Html страницы\\lschat.html";
    public static Map<Integer, Message> AllMessage = new HashMap<>();
    public static Connection conn;
    static {
        try {
            conn = BDconnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static int primeMessagekey;
    public static int timeMessagekey;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.getProperty("mk.dir");
        String url = httpExchange.getHttpContext().getPath();
        String requestMeth = httpExchange.getRequestMethod();
        String unixtime = String.valueOf(Instant.now().getEpochSecond());
        switch (url){
            case "/text":
                if ("GET".equals(requestMeth)) {
                    handleResponse(httpExchange, index,200);
                }else if("POST".equals(requestMeth)) {
                    handlePersonBaseTokenizer(httpExchange);
                }
                break;
            case "/login":
                try {
                    caseLogin(httpExchange, requestMeth);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "/main":
                try {
                    caseMain(httpExchange, requestMeth, unixtime);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case  "/CSS":
                handleCssResponse(httpExchange, indexCSS, 200);
                break;
            case "/users":
                String query = httpExchange.getRequestURI().getQuery();
                String[] split = query.split("=", 2);
                try {
                    caseUsersInfo(split[1], httpExchange, requestMeth);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "/lschat":
                try {
                    caseLschat(conn, httpExchange, requestMeth, unixtime);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "/allmessage":
                try {
                    handleResponseForMessage(httpExchange, DBhelper.getAllOrOneMessage(conn,2));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                break;
            case "/":
                handleResponse(httpExchange, index, 200);
                break;
        }
    }

    public static Connection BDconnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/mydb?serverTimezone=UTC";
        String usernameConnection = "root";
        String passwordConnection = "Qwerty123456";
        conn = DriverManager.getConnection(url, usernameConnection, passwordConnection);
        return conn;
    }
    public static void handleCssResponse(HttpExchange httpExchange, String index, int codeHeader)  throws IOException {
        httpExchange.getResponseHeaders().put("Content-Type", Collections.singletonList("text/css; charset=windows-1251"));
        byte[] arrayss = Files.readAllBytes(Paths.get(index));
        httpExchange.sendResponseHeaders(codeHeader, arrayss.length);
        handleOutputStream(httpExchange, arrayss);
        System.out.println("Конец отправки: " + index);
    }
    public static void handleResponse(HttpExchange httpExchange, String index, int codeHeader)  throws IOException {
        byte[] arrayss = Files.readAllBytes(Paths.get(index));
        httpExchange.sendResponseHeaders(codeHeader, arrayss.length);
        handleOutputStream(httpExchange, arrayss);
        System.out.println("Конец отправки: " + index);
    }
    public static void handleOutputStream(HttpExchange httpExchange, byte[] array) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        outputStream.write(array);
        outputStream.flush();
        outputStream.close();
    }
    public static String BufferInGetRequestBody(HttpExchange httpExchange)  throws IOException {
        BufferedReader bufferedReader = new BufferedReader
                (new InputStreamReader(httpExchange.getRequestBody()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
    public static void caseUsersInfo(String query, HttpExchange httpExchange, String RequestMeth) throws SQLException, IOException {
        StringBuilder SB = new StringBuilder(query);
        if("GET".equals(RequestMeth)) {
              handleResponse(httpExchange, users, 200);
        }else if("POST".equals(RequestMeth)){
            if(DBhelper.callRegister(conn, SB.toString())){
                if(httpExchange.getRequestHeaders().containsKey("Cookie")) {
                    String cookieInBrowser = httpExchange.getRequestHeaders().get("Cookie").toString()
                            .replaceAll("session=", "").replaceAll("[\\[\\]]", "");
                    if(HashmapClass.NickAndCookie.containsValue(cookieInBrowser)) {
                        String poster = HashmapClass.getKeyByValue(HashmapClass.NickAndCookie, cookieInBrowser);
                        String postedUser = DBhelper.getUser(conn, SB.toString(), poster);
                        handleResponseForMessage(httpExchange, postedUser);
                    }
                }

            }
        }
    }
    public static void handlePersonBaseTokenizer(HttpExchange httpExchange)  throws IOException {
        String information = BufferInGetRequestBody(httpExchange);
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String nick = st.nextToken().trim();
        String name = st.nextToken().trim();
        String pass = st.nextToken().trim();
        if(DBhelper.PutBasePerson(conn,nick,name,pass)){
            MyHttpHandler.handleResponse(httpExchange, MyHttpHandler.login, 200);
        } else {
            MyHttpHandler.handleResponse(httpExchange, MyHttpHandler.login, 204);
        }
    }
    public static void caseLogin(HttpExchange httpExchange, String RequestMeth) throws IOException, SQLException {
        if("GET".equals(RequestMeth)) {
            handleResponse(httpExchange, login,200);
        }else if("POST".equals(RequestMeth)){
            LoginTrueFalse(BufferInGetRequestBody(httpExchange), httpExchange);
        }
    }
    public static void LoginTrueFalse(String  information, HttpExchange httpExchange) throws IOException, SQLException {
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String nickname = st.nextToken().trim();
        String pass = st.nextToken().trim();
        if(DBhelper.LoginTrueFalse(conn, nickname, pass)){
            String newCookie = "Cock"+nickname+"boo";
            HashmapClass.NickAndCookie.put(nickname, newCookie);
            MyHttpHandler.CookieSetter(httpExchange, newCookie, "202", MyHttpHandler.main);
        } else {
            MyHttpHandler.handleResponse(httpExchange, MyHttpHandler.login, 204);
        }
    }
    public static void CookieSetter(HttpExchange httpExchange, String Cookie, String code, String page) throws IOException {
        httpExchange.getResponseHeaders().put("Set-Cookie",
                Collections.singletonList("session=" + Cookie + ";Path=/;Max-Age=600;httponly"));
        byte[] arrayss = Files.readAllBytes(Paths.get(page));
        httpExchange.sendResponseHeaders(Integer.parseInt(code), arrayss.length);
        handleOutputStream(httpExchange,arrayss);
    }
    public static boolean trueSystemCookie(HttpExchange httpExchange){
        if(httpExchange.getRequestHeaders().containsKey("Cookie")) {
            String cookieInBrowser = httpExchange.getRequestHeaders().get("Cookie").toString()
                    .replaceAll("session=", "").replaceAll("[\\[\\]]", "");
            return HashmapClass.NickAndCookie.containsValue(cookieInBrowser);
        }
        return false;
    }
    public static String NicknameCookie(HttpExchange httpExchange){
        if(httpExchange.getRequestHeaders().containsKey("Cookie")) {
            String cookieInBrowser = httpExchange.getRequestHeaders().get("Cookie").toString()
                    .replaceAll("session=", "").replaceAll("[\\[\\]]", "");
            return HashmapClass.getKeyByValue(HashmapClass.NickAndCookie,
                    cookieInBrowser);
        }
        return null;
    }
    public static void caseMain(HttpExchange httpExchange, String RequestMete, String unixtime) throws IOException, SQLException {
            if(trueSystemCookie(httpExchange)){
                if ("GET".equals(RequestMete)) {
                    handleResponse(httpExchange, main, 200);
                } else if ("POST".equals(RequestMete)) {
                    StringBuilder SBB = new StringBuilder();
                    SBB.append(BufferInGetRequestBody(httpExchange));
                    if (primeMessagekey > timeMessagekey) {
                        System.out.println(primeMessagekey+ " " + timeMessagekey);
                        handleResponseForMessage(httpExchange,DBhelper.getAllOrOneMessage(conn,1));
                        primeMessagekey = timeMessagekey;
                    }
                    if(!SBB.toString().equals("")){
                        DBhelper.putMessage(conn, NicknameCookie(httpExchange), unixtime, SBB.toString());
                        primeMessagekey++;
                    }
                    handleResponseForMessage(httpExchange, "0");
                }
            }
        }
    public static void caseLschat(Connection conn, HttpExchange httpExchange, String RequestMete, String unixtime) throws IOException, SQLException {
        String adressat = httpExchange.getRequestURI().getQuery().replaceAll("lshcat", "");
        StringTokenizer stringTokenizer = new StringTokenizer(adressat, "\"");
        String a = stringTokenizer.nextToken().replaceAll("=","");
        String nameposter = stringTokenizer.nextToken();
        String b = stringTokenizer.nextToken();
        String recievier = stringTokenizer.nextToken();
        String table = "ls" + nameposter + recievier;
        System.out.println(table);
        if(trueSystemCookie(httpExchange)){
            if(NicknameCookie(httpExchange).equals(nameposter) || NicknameCookie(httpExchange).equals(recievier)) {
                if ("GET".equals(RequestMete)) {
                    if(adressat.contains("all")){
                        handleResponseForMessage(httpExchange, DBhelper.getAllLsMessage(conn, table, 2));
                    }
                    handleResponse(httpExchange, lschat, 200);
                } else if ("POST".equals(RequestMete)) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(BufferInGetRequestBody(httpExchange));

                    if (!stringBuilder.toString().equals("")) {
                        DBhelper.putLsMessage(conn, table, NicknameCookie(httpExchange), nameposter, unixtime, stringBuilder.toString());
                    }
                    handleResponseForMessage(httpExchange,DBhelper.getAllLsMessage(conn, table, 1));

                }
            }
        }
    }

    public static void handleResponseForMessage(HttpExchange httpExchange, String numb)  throws IOException {
        byte[] arrayss = numb.getBytes();
        httpExchange.sendResponseHeaders(200, arrayss.length);
        handleOutputStream(httpExchange, arrayss);
        System.out.println("Отправлено: " + numb);
    }
    public static String KeysValue(){
//        Set<String> keys = AllMessage.keySet();
//        System.out.println("Ключи: " + keys);
        Set<String> values = Collections.singleton(AllMessage.values().toString());
        System.out.println("Значения: " +values);
        return String.valueOf(values);
    }

}