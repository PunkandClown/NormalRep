import com.sun.net.httpserver.HttpServer;
        import java.io.IOException;
        import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpServer2 {
    public static void main(String[] arg) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("192.168.0.105", 1337), 0);
        server.createContext("/text", new  MyHttpHandler());
        server.createContext("/login", new  MyHttpHandler());
        server.createContext("/main", new  MyHttpHandler());
        server.createContext("/CSS", new  MyHttpHandler());
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        server.setExecutor(threadPoolExecutor);
        server.start();
    }

}
