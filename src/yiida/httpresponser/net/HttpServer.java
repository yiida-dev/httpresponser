package yiida.httpresponser.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ServerSocketFactory;

public class HttpServer {
    private int connectionCount;
    private int port;

    private ServerSocket server;
    private Thread listenThread;
    private ResponseHandler responseHandler;

    private ExecutorService handlerThread;
    private AtomicBoolean running;

    public HttpServer() {
        this(1, 34441);
    }

    public HttpServer(int connectionCount, int port) {
        this.connectionCount = connectionCount;
        this.port = port;
        this.server = null;
        this.listenThread = null;
        this.responseHandler = null;
        this.running = new AtomicBoolean(false);
        handlerThread = null;
    }

    public final void setConnectionCount(int count) {
        if(running.get()) throw new IllegalStateException();
        connectionCount = count;
    }

    public final int getConnectionCount() {
        return connectionCount;
    }

    public final void setPort(int port) {
        if(running.get()) throw new IllegalStateException();
        this.port = port;
    }

    public final int getPort() {
        return port;
    }

    public final void setResponseHandler(ResponseHandler handler) {
        if(running.get()) throw new IllegalStateException();
        responseHandler = handler;
    }

    public final ResponseHandler getResponseHandler() {
        return responseHandler;
    }

    public final synchronized void run() throws IOException {
        if(running.get()) throw new IllegalStateException();
        ServerSocketFactory ssf = getServerSocketFactory();
        server = ssf.createServerSocket(port);
        running.set(true);
        handlerThread = Executors.newSingleThreadExecutor();
        listenThread = new Thread(new Runnable() {
            public void run() {
                while(!tryRunning());
                running.set(false);
            }

            private boolean tryRunning() {
                boolean done = false;
                ExecutorService executor = Executors.newFixedThreadPool(connectionCount);
                try {
                    while(running.get()) {
                        Socket socket = server.accept();
                        System.out.println("accept");
                        executor.execute(new ConnectionHandler(socket));
                    }
                    done = true;
                } catch(SocketException e) {
                    System.out.println("server has stopped listening.");
                    done = true;
                } catch(IOException e) {
                    e.printStackTrace();
                } finally {
                    executor.shutdownNow();
                }
                return done;
            }
        });
        listenThread.start();
    }

    public final boolean stop() {
        if(!running.get()) return false;
        try {
            server.close();
            System.out.println("closing server...");
        } catch(IOException e) {
            e.printStackTrace();
        }
        handlerThread.shutdownNow();
        try {
            listenThread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        running.set(false);
        return true;
    }

    public boolean isRunnning() {
        return running.get();
    }

    protected ServerSocketFactory getServerSocketFactory() {
        return ServerSocketFactory.getDefault();
    }

    private final class ConnectionHandler implements Runnable {
        private Socket socket;
        ConnectionHandler(Socket socket) {
            this.socket = socket;
            //System.out.println("connected!");
        }
        public void run() {
            InputStream is = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            OutputStream os = null;
            OutputStreamWriter osr = null;
            BufferedWriter bw = null;
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
                isr = new InputStreamReader(is, Charset.forName("utf-8"));
                osr = new OutputStreamWriter(os, Charset.forName("utf-8"));
                br = new BufferedReader(isr);
                bw = new BufferedWriter(osr);
                String req = br.readLine();
                if(req == null) {
                    return;
                }
                boolean endReq = false;
                boolean gReq = false;
                if(req.startsWith("GET")) {
                    gReq = true;
                } else if(req.startsWith("POST")) {
                    gReq = false;
                } else {
                    endReq = true;
                }
                int contentLength = 0;
                while(!endReq) {
                    String ln = br.readLine();
                    if(ln == null){
                        ln = "";
                    } else {
                        if(ln.toLowerCase().startsWith("content-length:")) {
                            contentLength = Integer.parseInt(ln.substring(15).trim());
                        }
                    }
                    req += "\r\n" + ln;
                    if(ln.length() <= 0) {
                        endReq = true;
                        if(!gReq && contentLength > 0) {
                            ByteBuffer buf = ByteBuffer.allocate(contentLength);
                            for(int b = 0; b < contentLength;) {
                                int c = br.read();
                                if(c == -1) break;
                                buf.put((byte)(c & 0xff));
                                b++;
                                if(c > 0xff) {
                                    buf.put((byte)((c >> 8) & 0xff));
                                    b++;
                                }
                            }
                            buf.position(0);
                            req += "\r\n" + Charset.forName("utf-8").decode(buf).toString();
                        }
                    }
                }
                String res = "hello";
                if(responseHandler != null) {
                    Future<String> result = handlerThread.submit(new Notifier(req));
                    try {
                        res = result.get();
                    } catch(ExecutionException e) {
                        res = "error";
                    }
                }
                bw.write(res);
                bw.flush();

            } catch(IOException e) {
                e.printStackTrace();
            } catch(InterruptedException e) {
                System.out.println("Connection closed by force.");
            } finally {
                if(br != null) try{br.close();} catch(IOException e) {}
                if(isr != null) try{isr.close();} catch(IOException e) {}
                if(is != null) try{is.close();} catch(IOException e) {}
                if(bw != null) try{bw.close();} catch(IOException e) {}
                if(osr != null) try{osr.close();} catch(IOException e) {}
                if(os != null) try{os.close();} catch(IOException e) {}
                if(socket != null) try{socket.close();} catch(IOException e) {}
            }
        }
    }

    private final class Notifier implements Callable<String> {
        private String req;
        Notifier(String req) {
            this.req = req;
        }
        @Override
        public String call() throws Exception {
            return responseHandler.respond(req);
        }
    }
}
