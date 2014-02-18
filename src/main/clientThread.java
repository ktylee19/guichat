package main;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import pubsub.Event;

/**
 * DEFINITION:
 * 
 * This class creates a thread for each client connection,
 * handles requests from the client by reading it from each Client 
 * and performing any requested action. Clients can log in, log out, 
 * and type messages. Actions of the client are broadcasted to 
 * other users instantly. When a client leaves the chat room this 
 * thread informs all the clients about that and terminates. 
 * 
 * REPRESENTATION INVARIANT: 
 * 	
 * clientThreads is a list of unique Threads where each client thread
 * has a unique user name.
 * 
 */

class clientThread extends Thread {
	
	// To read from the socket.
	public BufferedReader inputS;
	// To write to the socket.
	public PrintWriter outputS; 
	// The list of all the client threads.
	private List<clientThread> clientThreads = new ArrayList<clientThread>();
	// The client socket.
	public Socket clientSocket;
	// The user name assigned to each client. 
	public String username;
	public String message;
	private static ArrayList<String> defaultAll = new ArrayList<String>(){{
	    add("&all");
	}};
	
    /**
     * Make a clientThread that handles requests from the client 
     * by reading it from each Client and performing any requested 
     * action.
     * @param clientSocket, socket for the client that is attached to the server's socket.
     * @param clientThreads, list of all the client threads.
     * @throws IOException 
     */
	clientThread(Socket clientSocket, List<clientThread> clientThreads) throws IOException {
		this.clientSocket = clientSocket;
		this.clientThreads = clientThreads;
		try {
			// Create input and output streams for the client.
		    inputS  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	        outputS = new PrintWriter(clientSocket.getOutputStream(),true);  
		} catch (IOException e) {
			System.out.println("Exception while creating input/output streams for the client: "+e);
		}
		// Get the username;
        newUser();
		
	}

	public void run() {
		try {
            // Read the client input until they type quit to logout. 
		    while (true) {
		    	
                String message = inputS.readLine();
                
                ArrayList<String> toSend = Event.StringToEvent(message).getSendTo();
                if (message.startsWith("RemoveUser "+"&all "+ username)) {
                    //System.out.println("breaking");
                    broadcast(message, toSend); 
                    break;
                }
                else if (message.startsWith("AllUsers")) {
                	updateUserList();
                }
                // Broadcast the client's message to other users. 
                broadcast(message, toSend); 
            
            // Remove the user from the clientThreads list.
			removeUser();
			// Close the output stream, close the input stream, close the socket
			// when the user logs out. 
			outputS.close();
			inputS.close();
			clientSocket.close();
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {		 
            removeUser();
            broadcastUserLogout();
		    System.out.println("Client exited chat. Exception: "+e);
		}
	}
	
	/**
	 * Asks for the client's user name. Prints a welcome message.
	 * @throws IOException
	 */
	private void newUser() throws IOException {
		String possibleUsername;
            possibleUsername = inputS.readLine();
            if (isUsernameTaken(possibleUsername) == true) {
                outputS.println("Username already taken.");
            } else {
            	outputS.println("Username not taken.");
                username = possibleUsername;
                broadcastNewUser();
                
            }
		
	}
	
	/**
	 * Checks if the user name the new client is trying to use is already taken.
	 * @param possibleUsername is a String that is passed in of a potential name the user would like 
	 * to assume 
	 */
	public boolean isUsernameTaken(String possibleUsername) {
		boolean taken = false;
		synchronized(clientThreads) {
			for (int i = 0; i < clientThreads.size(); i++) {
				if (clientThreads.get(i) != null && clientThreads.get(i) != this) {
					if (possibleUsername.equals(clientThreads.get(i).username)) {
						taken = true;
						return taken;
					} else {
						taken = false;
					}
				} else {
					taken = false;
				}
			}
		}
		return taken;
	}
	
	/**
	 * Broadcasts to other users that client has joined the chat room. 
	 * @throws IOException 
	 */
	private void broadcastNewUser() {
		synchronized(clientThreads) {
		    for (int i = 0; i < clientThreads.size(); i++) {
                if (clientThreads.get(i) != null) {
                    String message = username+" has joined the chatroom.";
                    clientThreads.get(i).outputS.println(new Event(Event.Type.AddUser,defaultAll, message));
                }
            }
		}
	}
	
	/**
	 * Updates the online users list. 
	 * @throws IOException 
	 */
	private void updateUserList() {
		synchronized(clientThreads) {
		for(int i = 0; i < clientThreads.size(); ++i) {
			clientThread ct = clientThreads.get(i);
			outputS.println(new Event(Event.Type.AllUsers, defaultAll, ct.username));
		}
		}
	}

	
	/**
	 * Broadcasts the client's message to other users instantly. 
	 * @param message, a String that represents the words that one user has sent to a chat conversation
	 * @param user, an ArrayList of String usernames of all the recipients of the message
	 * @throws IOException 
	 */
	private void broadcast(String message, ArrayList<String> user) {
		synchronized(clientThreads) {
			for (int i = 0; i < clientThreads.size(); i++) {
				if ( (clientThreads.get(i) != null) && ( user.contains(clientThreads.get(i).username) | user.contains("&all")  )      ) {
				    clientThreads.get(i).outputS.println(message);
				}
			}
		}	
	}
	
	/**
	 * Broadcasts to other users that the client has logged out.
	 * @throws IOException 
	 */
	private void broadcastUserLogout() {
		synchronized(clientThreads) {
			for (int i = 0; i < clientThreads.size(); i++) {
				if (clientThreads.get(i) != null && clientThreads.get(i) != this) {
					if (username != (null)) {
                    clientThreads.get(i).outputS.println(new Event(Event.Type.RemoveUser, defaultAll, username+ " left the chat room."));
					}
                }
			}
		}
	}
	
	/**
	 * Removes the logged out client from the clientThreads list.
	 */
	private void removeUser() {
		synchronized (clientThreads) {
			for (int i = 0; i < clientThreads.size(); i++) {
				if (clientThreads.get(i) == this) {
					clientThreads.remove(i);
				}	  
			}
		}	
	}
	
	/**
     * Returns the username of the clientThread
     */
	public String getUsername() {
	    return username;
	}
	
	/**
     * Returns the clientThreads ArrayList of the clientThread
     */
	public String getClientThreads() {
	    return clientThreads.toString();
	}
}