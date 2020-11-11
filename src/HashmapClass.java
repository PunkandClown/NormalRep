import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;

public class HashmapClass {


    static HashMap<String, Person> PersonBase = new HashMap<>();
    public static HashMap<String, String> NickAndCookie = new HashMap<>();


    public static void base(String Nickname, String Name, String Password, HttpExchange httpExchange)
            throws IOException {
        if(PersonBase.containsKey(Nickname)){
            System.out.println("Такой ник уже есть");
            MyHttpHandler.handleCodeResponse(httpExchange,"203");
        } else {
            PersonBase.put(Nickname, new fab().Fabm( Nickname,Name,Password));
            MyHttpHandler.handleCodeResponse(httpExchange,"204");
        }
    }
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


}


