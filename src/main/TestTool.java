package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class TestTool {
    
    static String host = "localhost";
    static int port = 4445;
    static Server server;
    
    public static void startServer() throws IOException {
        System.out.println("opening port " + port);
        server = new Server(port);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    server.serve();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });  
        t.start();
    }
    

    public static void endServer() {
        //server.getserverSocket.close();
        server.closed = true;
        server.endServer();
    }

    
    public static Socket socketConnect() throws IOException {
        Socket socket;
        final int MAX_ATTEMPTS = 50;
        int attempts = 0;
        socket = null;
        do {
            try {
                socket = new Socket(host, port);
            } catch (ConnectException ce) {
                try {
                    if (++attempts > MAX_ATTEMPTS)
                        throw new IOException("Exceeded max connection attempts", ce);
                    Thread.sleep(300);
                } catch (InterruptedException ie) {
                    throw new IOException("Unexpected InterruptedException", ie);
                }
            }
        } while (socket == null);
        return socket;
    }
    
    public static Client clientConnect(String username, int port) throws IOException {
        
        Client client = new Client(host, port, username) ;
        return client;
    }
    
    public static String nextNonEmptyLine(BufferedReader in) throws IOException {
        while (true) {
            String ret = in.readLine();
            if (ret == null || !ret.equals(""))
                return ret;
        }
    }
}
