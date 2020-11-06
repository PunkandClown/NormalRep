import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

public class HashmapClass {


    static HashMap<String, Person> PersonBase = new HashMap<>();

    public static void base(String Nickname, String Name, String Password, HttpExchange httpExchange)
            throws IOException {
        if(PersonBase.containsKey(Nickname)){
            System.out.println("Такой ник уже есть");
            MyHttpHandler.handleCodeResponse(httpExchange,"204");
        } else {
            PersonBase.put(Nickname, new fab().Fabm( Nickname,Name,Password));
            MyHttpHandler.handleCodeResponse(httpExchange,"203");
        }
}

    public static void KeysValue(){
        Set<String> keys = PersonBase.keySet();
        System.out.println("Ключи: " + keys);

        Set<String> values = Collections.singleton(PersonBase.values().toString());
        System.out.println("Значения: " +values);
    }
}


