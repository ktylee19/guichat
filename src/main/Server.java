package main;

import java.util.*;
import java.io.*;
import java.net.*;

import main.clientThread;
	
/**
 * DEFINITION:
 * 
 * This is a chat server that hosts a chat room and handles multiple connections 
 * with clients and their requests concurrently. The Server class is run before
 * any clients can connect to the Server.
 * 
 * The server has two threads that continuously pull from streams. The first thread
 * waits for new connections and attaches any new connection's socket to the 
 * serverSocket. The other thread handles requests from the client by reading it 
 * from each Client and performing any requested action. Clients can log in, log out, 
 * and type messages which are broadcasted to other users instantly.
 * 
 * REPRESENTATION INVARIANT: 
 * 	
 * clientThreads is a list of unique Threads where each client thread
 * has a unique user name.
 * 
 */
public class Server {
	
	// The server socket.
	private static ServerSocket serverSocket;
	// The client socket.
	private static Socket clientSocket; 
	// The list of all the client threads. 
	private static final List<clientThread> clientThreads = new ArrayList<clientThread>();
	// The port to open the server socket on.
	private int port;
	// Boolean to see if server is closed.

	protected boolean closed;

	
	/**
	 * Construct the server, listening for client connections and
	 * handling them. 
	 * @param port, The network port on which the server should listen.
	 */
	public Server(int port)
	   {
		this.port = port;
	}

	/**
	 * Run the server, listening for client connections and handling them.  
	 * Never returns unless an exception is thrown.
	 * @throws IOException if the main server socket is broken
	 * (IOExceptions from individual clients do *not* terminate serve()).
	 */
	public void serve() throws IOException {
		try {
			// Open a server socket on the port number (default 4444).
			serverSocket = new ServerSocket(port);
			System.out.println("Server waiting for clients on port "+port+".");
			// Infinite loop to wait for client connections.
			while(!closed) { 
				/* 
				 Create a client socket for each connection and 
				 pass it to a new client thread along with the 
				 list of other clients.
				 */ 
				clientSocket = serverSocket.accept();
				// Break when server is closed. 
				if(closed) {
					break;
				}
				clientThread thread = new clientThread(clientSocket,clientThreads); 
				clientThreads.add(thread);
				thread.start();
			}
			try {
				for(int i = 0; i < clientThreads.size(); ++i) {
					clientThread thread = clientThreads.get(i);
					try {
						thread.inputS.close();
						thread.outputS.close();
						thread.clientSocket.close();
					}
					catch(IOException e) {
						System.out.println("Server closed: "+e);
					}
				}
                serverSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getClientThreadsSize() {
	    return clientThreads.size();
	}
	
	public void endServer() {
	    for(int i = 0; i < clientThreads.size(); ++i) {
            clientThread thread = clientThreads.get(i);
            try {
                thread.inputS.close();
                thread.outputS.close();
                thread.clientSocket.close();
            }
            catch(IOException e) {
                System.out.println("Server closed: "+e);
            }
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }}

    
	
	public static void main(String args[]) throws IOException {
		final int port;
		final String host;
		String portProp = System.getProperty("main.customport");
		// Use default port and host if any other port and host are not specified.
		if (portProp == null) {
			port = 4444; // Default port.
			host = "localhost"; // Default host.
		} else {
			host = args[0];
			port = Integer.parseInt(portProp);
		}
		// Start and run the server.
		Server server = new Server(port);
		server.serve();
	}
	
}
