package pubsub;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import pubsub.Listener;

public class Publisher extends Thread {
        // Login Window GUI
        private BufferedReader input;
        private final CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<Listener>();
        
        /**
         * publisher's input stream
         * 
         */
        public void addInputStream(BufferedReader inputStream) {
            this.input = inputStream;
        }
        
        /**
         * add listener to the publisher
         * 
         */
        public void addListener(Listener listener) {
            listeners.add(listener);
        }
        
        /**
         * announce message to publisher's listeners
         */
        public void announce(String serverResponse) throws IOException {
            for (Listener listener : listeners) {
                listener.event(serverResponse);
            }
        }
        /**
         * Keep reading from the server once you see the "Bye"
         * message to break and close connection.
         */
        public void run() {
            String serverResponse;
            try {
                while ((serverResponse = input.readLine()) != null) {
                    //serverResponse is an event from the Server,
                    // to be sent to the GUI.
                    announce(serverResponse);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } 
        }
    }
