import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;


public class MyHttpHandler implements HttpHandler {
    public static int keymessage = 0;
    public String  index = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\index.html";
    public static String  Log = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\login.html";
    public static String  main = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\main.html";
    public static Boolean Accx = false;

    public static Map<Integer, Message> AllMessage = new HashMap<>();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        HashmapClass.PersonBase.put("1", new fab().Fabm( "1","1","1"));
        HashmapClass.PersonBase.put("2", new fab().Fabm( "2","2","2"));
        String url = httpExchange.getHttpContext().getPath();
        LocalDateTime localdate = LocalDateTime.now();
        String date = localdate.getHour() + ":" + localdate.getMinute() + ":" +localdate.getSecond();

        switch (url){

            case "/text":
                if ("GET".equals(httpExchange.getRequestMethod())) {
                    MyHttpHandler.handleResponse(httpExchange, index);
                } else if ("POST".equals(httpExchange.getRequestMethod())) {
                    handleRequest(httpExchange);
                }
                break;

            case "/login":
                if("GET".equals(httpExchange.getRequestMethod())) {
                    MyHttpHandler.handleResponse(httpExchange, Log);
                } else if("POST".equals(httpExchange.getRequestMethod())) {

                    String information = MyHttpHandler.handleloginReq(httpExchange);
                    String Cookie =  information.substring(information.indexOf('{') +1,
                            information.lastIndexOf('}')).replaceAll(",", "") + "Cookie";

                    if(LoginTrueFalse(information, Cookie)){
                        if(HashmapClass.NickAndCookie.containsKey(Cookie)){
                            MyHttpHandler.handleCodeResponse(httpExchange, "203");
                        } else {
                            MyHttpHandler.CookieSetter(httpExchange, Cookie, "203");
                        }
                    }
                    else {
                        handleCodeResponse(httpExchange, "204");
                    }
                }
            case "/main":
                if((url.equals("/main") && Accx)){
                    if("GET".equals(httpExchange.getRequestMethod())) {
                        String Cookie = httpExchange.getRequestHeaders().get("Cookie").toString().replaceAll("session=", "")
                                .replaceAll("[\\[\\]]", "");
                        if(HashmapClass.NickAndCookie.containsValue(Cookie)){
                            if(AllMessage.size() == 0 ){
                                handleResponse(httpExchange, main);
                            }
                            MyHttpHandler.handleCodeResponse(httpExchange,
                                    MyHttpHandler.SBAllMessage((HashMap) AllMessage));
                            }
                        MyHttpHandler.handleResponse(httpExchange, main);
                    } else if("POST".equals(httpExchange.getRequestMethod())) {
                        System.out.println("Пост /main");
                            String Cookie = httpExchange.getRequestHeaders().get("Cookie").toString().replaceAll("session=", "")
                                    .replaceAll("[\\[\\]]", "");
                            if(HashmapClass.NickAndCookie.containsValue(Cookie)){
                                Message message = new Message(HashmapClass.getKeyByValue(HashmapClass.NickAndCookie, Cookie), date , handleloginReq(httpExchange));
                                AllMessage.put(keymessage, message);
                                System.out.println(KeysValue());
                                if (AllMessage.size() > 7){
                                    for(int i = 0; i < 8; i++){
                                        AllMessage.remove(i);
                                        keymessage--;
                                    }
                                }
                                keymessage++;
                            }
                    }
                    break;
                }
            default:
                MyHttpHandler.handleResponse(httpExchange, Log);
        }
    }
public static String SBAllMessage(HashMap hashmap){
        StringBuilder SB = new StringBuilder();
    for(int i = 0; i < hashmap.size(); i++){
        String ParseMessage = hashmap.get(i).toString();
        SB.append(ParseMessage);
    }
        return SB.toString();
}
    public boolean LoginTrueFalse(String  information, String Cookie) {
        System.out.println("/login. Вход с данными");
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String pas = st.nextToken().trim();
        if(HashmapClass.PersonBase.containsKey(Nick)){
            String Getpass = HashmapClass.PersonBase.get(Nick).getPassword();
            if(Getpass.equals(pas)){
                System.out.println("Логин пользователя успешно введен");
                HashmapClass.NickAndCookie.put(Nick, Cookie);
                Accx = true;
                return true;
            }
        }
        return false;
    }

    public static void CookieSetter(HttpExchange httpExchange, String Cookie, String code) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        byte[] arrayss = code.getBytes();
        httpExchange.getResponseHeaders().put("Set-Cookie",
                Collections.singletonList("session=" + Cookie + ";Path=/;Max-Age=600;httponly"));
        httpExchange.sendResponseHeaders(200, arrayss.length);
        outputStream.write(arrayss);
        outputStream.flush();
        outputStream.close();
        System.out.println("Конец отправки: Куки" );
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
    public static void handleCodeResponse(HttpExchange httpExchange, String numb)  throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        byte[] arrayss = numb.getBytes();
        httpExchange.sendResponseHeaders(200, arrayss.length);
        outputStream.write(arrayss);
        outputStream.flush();
        outputStream.close();
        System.out.println("Коды отправлены");
    }
    public static void handleRequest(HttpExchange httpExchange)  throws IOException {
        String information = handleloginReq(httpExchange);
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String Nam = st.nextToken().trim();
        String pas = st.nextToken().trim();
        HashmapClass.base(Nick,Nam,pas, httpExchange);
        System.out.println("Ввод в Хеш аккаунта:" +"["+  Nick +" " + Nam +" "+ pas+"]");
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

    public static String KeysValue(){
//        Set<String> keys = AllMessage.keySet();
//        System.out.println("Ключи: " + keys);

        Set<String> values = Collections.singleton(AllMessage.values().toString());
        System.out.println("Значения: " +values);
        return String.valueOf(values);
    }

}