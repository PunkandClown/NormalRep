import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.lang.reflect.Member;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;


public class MyHttpHandler implements HttpHandler {

    public String  index = "E:\\Рабочие Html страницы\\index.html";
    public static String  Log = "E:\\Рабочие Html страницы\\login.html";
    public static String  main = "E:\\Рабочие Html страницы\\main.html";
    public static Boolean Accx = true;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String url = httpExchange.getHttpContext().getPath();
        boolean AccAvt = false;

        switch (url){
        case "/text":
            if ("GET".equals(httpExchange.getRequestMethod())) {
                MyHttpHandler.handleResponse(httpExchange, index);
            } else if ("POST".equals(httpExchange.getRequestMethod())) {
                System.out.println("/text. Регистрация с данными:");
                handleRequest(httpExchange);
            }
            break;
        case "/login":
            if("GET".equals(httpExchange.getRequestMethod())) {
                MyHttpHandler.handleResponse(httpExchange, Log);
            } else if("POST".equals(httpExchange.getRequestMethod())) {
                System.out.println("/login. Вход с данными");
                String information = MyHttpHandler.handleloginReq(httpExchange);
                if(LoginTrueFalse(information)){
                    MyHttpHandler.handleCodeResponse(httpExchange, "203");
                } else {
                    handleCodeResponse(httpExchange, "204");
                }
            }
        case "/main":
            if((url.equals("/main") && Accx)){
                if("GET".equals(httpExchange.getRequestMethod())) {
                    MyHttpHandler.handleResponse(httpExchange, main);
                } else if("POST".equals(httpExchange.getRequestMethod())) {
                    System.out.println("ВВеденное сообщение:");
                    System.out.println(handleloginReq(httpExchange));
                }
            } else {
                MyHttpHandler.handleResponse(httpExchange, Log);
            }
        }
    }

    public boolean LoginTrueFalse(String information){
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String pas = st.nextToken().trim();
        if(HashmapClass.PersonBase.containsKey(Nick)){
            String Getpass = HashmapClass.PersonBase.get(Nick).getPassword();
            if(Getpass.equals(pas)){
                System.out.println("Логин пользователя успешно введен");
                    Accx = true;
                    return true;
            }
        }
        return false;
    }

    public static void handleResponse(HttpExchange httpExchange, String index)  throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        byte[] arrayss = Files.readAllBytes(Paths.get(index));
        httpExchange.sendResponseHeaders(200, arrayss.length);
        outputStream.write(arrayss);
        outputStream.flush();
        outputStream.close();
        System.out.println("Конец отправки: " + index);
    }

    public static void handleRequest(HttpExchange httpExchange)  throws IOException {

        BufferedReader bufferedReader = new BufferedReader
                (new InputStreamReader(httpExchange.getRequestBody()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        for (;(line = bufferedReader.readLine()) != null;) {
            stringBuilder.append(line);
        }
        String information = stringBuilder.toString();

        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
            String Nick = st.nextToken().trim();
            String Nam = st.nextToken().trim();
            String pas = st.nextToken().trim();
        HashmapClass.base(Nick,Nam,pas, httpExchange);
        System.out.println("Ввод в Хеш аккаунта:" +"{"+  Nick +" " + Nam +" "+ pas+"}");
        bufferedReader.close();
    }

    public static String handleloginReq(HttpExchange httpExchange)  throws IOException {
        BufferedReader bufferedReader = new BufferedReader
                (new InputStreamReader(httpExchange.getRequestBody()));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        for (;(line = bufferedReader.readLine()) != null;) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    public static void handleCodeResponse(HttpExchange httpExchange, String numb)  throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        byte[] arrayss = numb.getBytes();
        httpExchange.sendResponseHeaders(200, arrayss.length);
        outputStream.write(arrayss);
        outputStream.flush();
        outputStream.close();
        System.out.println("Коды отправлены");
    }
}