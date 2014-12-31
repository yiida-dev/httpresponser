package yiida.httpresponser.net;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class HttpsServer extends HttpServer {
    static {
        System.setProperty("javax.net.ssl.keyStore",
                "/path/your_keystore");
        System.setProperty("javax.net.ssl.keyStoreType", "JKS");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
    }

    public HttpsServer() {}

    public HttpsServer(int connectionCount, int port) {
        super(connectionCount, port);
    }

    @Override
    protected ServerSocketFactory getServerSocketFactory() {
        return SSLServerSocketFactory.getDefault();
    }

}
