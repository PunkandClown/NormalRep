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
    public static String indexCSS = "Рабочие Html страницы\\indexCSS.css";
    public String  index = "Рабочие Html страницы\\index.html";
    public static String login = "Рабочие Html страницы\\login.html";
    public static String  main = "Рабочие Html страницы\\main.html";

    public static Map<Integer, Message> AllMessage = new HashMap<>();
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.getProperty("mk.dir");
        LocalDateTime localitytime = LocalDateTime.now();
        String url = httpExchange.getHttpContext().getPath();
        String RequestMeth = httpExchange.getRequestMethod();
        String date = localitytime.getHour() + ":" + localitytime.getMinute() + ":" +localitytime.getSecond();
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
                System.out.println("Дефолт");
                handleResponse(httpExchange, index, 200);
                break;
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
        String nick = st.nextToken().trim();
        String name = st.nextToken().trim();
        String pass = st.nextToken().trim();
        BDclass.BDhandlerUserSet(nick,name,pass, httpExchange,0);
    }
    public static void caseLogin(HttpExchange httpExchange, String RequestMeth) throws IOException {
        if("GET".equals(RequestMeth)) {
            handleResponse(httpExchange, login,200);
        }else if("POST".equals(RequestMeth)){
            LoginTrueFalse(BufferInGetRequestBody(httpExchange), httpExchange);
        }
    }
    public static void LoginTrueFalse(String  information, HttpExchange httpExchange) throws IOException {
        String Str = information.substring(information.indexOf('{') +1, information.lastIndexOf('}'));
        StringTokenizer st = new StringTokenizer(Str, ",");
        String nickname = st.nextToken().trim();
        String pass = st.nextToken().trim();
        BDclass.BDhandlerUserSet(nickname, "name", pass, httpExchange, 1);
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


                    StringBuilder SBB = new StringBuilder();
                    SBB.append(BufferInGetRequestBody(httpExchange));
                    System.out.println(SBB);
                    if(!SBB.toString().equals("")){
                        Message message = new Message(HashmapClass.getKeyByValue(HashmapClass.NickAndCookie,
                                cookieInBrowser), date, SBB.toString());
                        AllMessage.put(keymessage, message);
                        handleResponseForMessage(httpExchange, SBAllMessageJson(AllMessage, 1));
                        if (AllMessage.size() > 9) {
                            for (int i = 0; i < 10; i++) {
                                AllMessage.remove(i);
                                keymessage--;
                            }
                        }
                        keymessage++;
                    }
                    handleResponseForMessage(httpExchange, SBAllMessageJson(AllMessage, 1));
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
            System.out.println(ParseMessage);
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
        return SB.toString();
    }
    public static void handleResponseForMessage(HttpExchange httpExchange, String numb)  throws IOException {
        byte[] arrayss = numb.getBytes();
        httpExchange.sendResponseHeaders(200, arrayss.length);
        handleOutputStream(httpExchange, arrayss);
        System.out.println("Сообщения отправлены: " + numb);
    }
    public static String KeysValue(){
//        Set<String> keys = AllMessage.keySet();
//        System.out.println("Ключи: " + keys);
        Set<String> values = Collections.singleton(AllMessage.values().toString());
        System.out.println("Значения: " +values);
        return String.valueOf(values);
    }

}