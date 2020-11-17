import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;


public class MyHttpHandler implements HttpHandler {
    public static int keymessage = 0;
//    public String  index = "C:\\Users\\Павел\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\index.html";
//    public static String login = "C:\\Users\\Павел\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\login.html";
//    public static String  main = "C:\\Users\\Павел\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\main.html";

    public static String indexCSS = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\indexCSS.css";
    public String  index = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\index.html";
    public static String login = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\login.html";
    public static String  main = "C:\\Users\\user\\IdeaProjects\\NormalRep\\Рабочие Html страницы\\main.html";

    public static Map<Integer, Message> AllMessage = new HashMap<>();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.getProperty("mk.dir");
        LocalDateTime localitytime = LocalDateTime.now();
        String url = httpExchange.getHttpContext().getPath();
        String RequestMeth = httpExchange.getRequestMethod();
        String date = localitytime.getHour() + ":" + localitytime.getMinute() + ":" +localitytime.getSecond();
        HashmapClass.PersonBase.put("1", new Person( "1","1","1"));
        HashmapClass.PersonBase.put("2", new Person( "2","2","2"));
        switch (url){
            case "/text":
                if ("GET".equals(RequestMeth)) {
                    handleResponse(httpExchange, index,200);
                }else if("POST".equals(RequestMeth)) {
                    handlePersonBaseTokenizer(httpExchange);
                }
                break;
            case "/login":
                caseLogin(httpExchange, RequestMeth);
                break;
            case "/main":
                caseMain(httpExchange, RequestMeth, date);
                break;
            case  "/CSS":
                handleCssResponse(httpExchange, indexCSS, 200);
                break;
            default:
                handleResponse(httpExchange, login, 200);
        }
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
    public static void handlePersonBaseTokenizer(HttpExchange httpExchange)  throws IOException {
        String information = BufferInGetRequestBody(httpExchange);
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String Nam = st.nextToken().trim();
        String pas = st.nextToken().trim();
        if(HashmapClass.base(Nick,Nam,pas, httpExchange)){
            handleResponse(httpExchange, login, 200);
        } else {
            handleResponse(httpExchange, login, 204);
        }
    }
    public static void caseLogin(HttpExchange httpExchange, String RequestMeth) throws IOException {
        if("GET".equals(RequestMeth)) {
            handleResponse(httpExchange, login,200);
        }else if("POST".equals(RequestMeth)){
            if(LoginTrueFalse(BufferInGetRequestBody(httpExchange), httpExchange)) {
                handleResponse(httpExchange, main,202);
            }
                handleResponse(httpExchange, login,204);
        }
    }
    public static boolean LoginTrueFalse(String  information, HttpExchange httpExchange) throws IOException {
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String Nick = st.nextToken().trim();
        String pas = st.nextToken().trim();
        if(HashmapClass.PersonBase.containsKey(Nick)){
            String Getpass = HashmapClass.PersonBase.get(Nick).getPassword();
            if(Getpass.equals(pas)){
                if(httpExchange.getRequestHeaders().containsKey("Cookie")){
                    String cookieInBrowser = httpExchange.getRequestHeaders().get("Cookie").toString()
                            .replaceAll("session=", "").replaceAll("[\\[\\]]", "");
                    if(!HashmapClass.NickAndCookie.containsValue(cookieInBrowser)){
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
        httpExchange.getResponseHeaders().put("Set-Cookie",
                Collections.singletonList("session=" + Cookie + ";Path=/;Max-Age=600;httponly"));
        byte[] arrayss = Files.readAllBytes(Paths.get(page));
        httpExchange.sendResponseHeaders(Integer.parseInt(code), arrayss.length);
        handleOutputStream(httpExchange,arrayss);
    }
    public static void caseMain(HttpExchange httpExchange, String RequestMete, String date) throws IOException {
        if(httpExchange.getRequestHeaders().containsKey("Cookie")) {
            String cookieInBrowser = httpExchange.getRequestHeaders().get("Cookie").toString()
                    .replaceAll("session=", "").replaceAll("[\\[\\]]", "");

            if(HashmapClass.NickAndCookie.containsValue(cookieInBrowser)) {
                if ("GET".equals(RequestMete)) {

                    handleResponse(httpExchange, main, 200);
                    handleResponseForMessage(httpExchange, SBAllMessageJson(AllMessage,2));
                } else if ("POST".equals(RequestMete)) {
                    Message message = new Message(HashmapClass.getKeyByValue(HashmapClass.NickAndCookie,
                            cookieInBrowser), date, BufferInGetRequestBody(httpExchange));
                    AllMessage.put(keymessage, message);
                    handleResponseForMessage(httpExchange, SBAllMessageJson(AllMessage, 2));

                    if (AllMessage.size() > 7) {
                        for (int i = 0; i < 8; i++) {
                            AllMessage.remove(i);
                            keymessage--;
                        }
                    }
                    keymessage++;
                }
            }
        } else {
            handleResponse(httpExchange, login, 200);
        }
    }
    public static String SBAllMessageJson(Map map, int OneMessageElseAll){
        StringBuilder SB = new StringBuilder();
        SB.append("[\n");
        if(OneMessageElseAll == 1){
            int counter = 0;
            for(int i = 0; i < map.size(); i++){
                counter = i;
            }
            String ParseMessage = map.get(counter).toString();
            SB.append(ParseMessage);
        } else if (OneMessageElseAll == 2){
            for(int a = 0; a < map.size(); a++){
            String ParseMessage = map.get(a).toString();
            if(a != 0){
                SB.append(",\n");
            }
            SB.append(ParseMessage);
        }
        }
        SB.append("\n]");
        System.out.println(SB.toString());
        return SB.toString();
    }
    public static void handleResponseForMessage(HttpExchange httpExchange, String numb)  throws IOException {
        byte[] arrayss = numb.getBytes();
        httpExchange.sendResponseHeaders(200, arrayss.length);
        handleOutputStream(httpExchange, arrayss);
        System.out.println("Сообщения отправлены");
    }
    public static String KeysValue(){
//        Set<String> keys = AllMessage.keySet();
//        System.out.println("Ключи: " + keys);
        Set<String> values = Collections.singleton(AllMessage.values().toString());
        System.out.println("Значения: " +values);
        return String.valueOf(values);
    }

}