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
    public static String login = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\login.html";
    public static String  main = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\main.html";
    public static Boolean accx = false;

    public static Map<Integer, Message> AllMessage = new HashMap<>();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        LocalDateTime localdate = LocalDateTime.now();
        String url = httpExchange.getHttpContext().getPath();
        String RequestMeth = httpExchange.getRequestMethod();
        String date = localdate.getHour() + ":" + localdate.getMinute() + ":" +localdate.getSecond();
        HashmapClass.PersonBase.put("1", new fab().Fabm( "1","1","1"));
        HashmapClass.PersonBase.put("2", new fab().Fabm( "2","2","2"));
    switch (url){
        case "/text":
            if ("GET".equals(RequestMeth)) {
                handleResponse(httpExchange, index,200);
            }else if("POST".equals(RequestMeth)) {
                handleRequest(httpExchange);
            } break;
        case "/login":
            if("GET".equals(RequestMeth)) {
                handleResponse(httpExchange, login,200);
            }else if("POST".equals(RequestMeth)){
                if(LoginTrueFalse(BufferInGetRequestBody(httpExchange), httpExchange)) {
                    System.out.println("Успешный логин и переход в /main");
                    handleResponse(httpExchange, main,202);
                } else {
                    httpExchange.sendResponseHeaders(204,0);
                }
            }
            break;
        case "/main":
            String Cookie = httpExchange.getRequestHeaders().get("Cookie").toString().replaceAll("session=", "")
                    .replaceAll("[\\[\\]]", "");
            if(HashmapClass.NickAndCookie.containsValue(Cookie)){
                if ("GET".equals(RequestMeth)) {
                        if (AllMessage.size() == 0) {
                            handleResponse(httpExchange, main, 200);
                        } else {
                            handleCodeResponse(httpExchange,MyHttpHandler.SBAllMessage(AllMessage));
                        }
                } else if ("POST".equals(RequestMeth)) {
                    Message message = new Message(HashmapClass.getKeyByValue(HashmapClass.NickAndCookie, Cookie), date, BufferInGetRequestBody(httpExchange));
                    AllMessage.put(keymessage, message);
                    System.out.println(KeysValue());
                    if (AllMessage.size() > 7) {
                        for (int i = 0; i < 8; i++) {
                            AllMessage.remove(i);
                            keymessage--;
                        }
                    }
                    keymessage++;

                }
            } else  {
                MyHttpHandler.handleResponse(httpExchange, login, 200);
            }
            break;
        default:
            MyHttpHandler.handleResponse(httpExchange, login, 200);
    }
    }
    public static String SBAllMessage(Map map){
        StringBuilder SB = new StringBuilder();
    for(int i = 0; i < map.size(); i++){
        String ParseMessage = map.get(i).toString();
        SB.append(ParseMessage);
    }
        return SB.toString();
    }

    public boolean LoginTrueFalse(String  information, HttpExchange httpExchange) throws IOException {
        System.out.println("/login. Вход с данными");
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String pas = st.nextToken().trim();
        if(HashmapClass.PersonBase.containsKey(Nick)){
            String Getpass = HashmapClass.PersonBase.get(Nick).getPassword();
            if(Getpass.equals(pas)){
                System.out.println("Логин пользователя успешно введен");
                if(httpExchange.getRequestHeaders().containsKey("Cookie")){
                    System.out.println("есть куки пользователя");
                    String cookieInBrowser = httpExchange.getRequestHeaders().get("Cookie").toString()
                            .replaceAll("session=", "").replaceAll("[\\[\\]]", "");
                if(!HashmapClass.NickAndCookie.containsValue(cookieInBrowser)){
                    System.out.println("Куки браузера нет в системе");
                    HashmapClass.NickAndCookie.put(Nick, cookieInBrowser);
                    return true;
                }
                } else {
                    String newCookie = Str.replaceAll(",", "") + "Cookie";
                    MyHttpHandler.CookieSetter(httpExchange, newCookie, "202", main);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static void CookieSetter(HttpExchange httpExchange, String Cookie, String code, String page) throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        httpExchange.getResponseHeaders().put("Set-Cookie",
                Collections.singletonList("session=" + Cookie + ";Path=/;Max-Age=600;httponly"));
        byte[] arrayss = Files.readAllBytes(Paths.get(page));
        httpExchange.sendResponseHeaders(Integer.parseInt(code), arrayss.length);
        outputStream.write(arrayss);
        outputStream.flush();
        outputStream.close();
        System.out.println("Конец отправки: " + page);
        System.out.println("Конец отправки: Куки" );
    }
    public static void handleResponse(HttpExchange httpExchange, String index, int codeHeader)  throws IOException {
        OutputStream outputStream = httpExchange.getResponseBody();
        byte[] arrayss = Files.readAllBytes(Paths.get(index));
        httpExchange.sendResponseHeaders(codeHeader, arrayss.length);
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
        String information = BufferInGetRequestBody(httpExchange);
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String Nam = st.nextToken().trim();
        String pas = st.nextToken().trim();
        HashmapClass.base(Nick,Nam,pas, httpExchange);
        System.out.println("Ввод в Хеш аккаунта:" +"["+  Nick +" " + Nam +" "+ pas+"]");
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

    public static String KeysValue(){
//        Set<String> keys = AllMessage.keySet();
//        System.out.println("Ключи: " + keys);

        Set<String> values = Collections.singleton(AllMessage.values().toString());
        System.out.println("Значения: " +values);
        return String.valueOf(values);
    }

}