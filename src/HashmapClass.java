import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;

public class HashmapClass {

    public static HashMap<String, String> NickAndCookie = new HashMap<>();
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }


}


