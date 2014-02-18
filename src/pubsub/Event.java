package pubsub;

import java.util.ArrayList;

/** 
 * Represents a single event in the GitHub event stream. 
 */
public class Event {
    public enum Type {
        AddUser,
        RemoveUser,
        Message,
        AllUsers
    };
    
    private final Type type;
    private final ArrayList<String> sendTo;
    private final String message;

    public Event(Type inputType, ArrayList<String> inputSendTo, String inputMessage) {
        type = inputType;
        sendTo = inputSendTo;
        message = inputMessage;
    }
    
    public Type getType() {
        return type;
    }
    
    public String getThirdToken() {
        String[] tokens = message.split(" ");
        return tokens[0];
    }
    
    public String getMessage() {
        return message;
    }
    
    public ArrayList<String> getSendTo() {
        return sendTo;
    }
    
    //returns sendTo into a string of users separated by "-"s
    private String sendToToString() {
        StringBuilder myString = new StringBuilder("");
        for (int i=0; i<sendTo.size(); i++) {
            myString.append(sendTo.get(i)+"-");
        }
        return myString.toString().substring(0, myString.toString().lastIndexOf("-"));
    }
    
    //turns string into a type
    public static Type StringToType(String convert) {
    	if (convert.equals("AddUser")) {
    		return Type.AddUser;
    	}
    	else if (convert.equals("RemoveUser")) {
    		return Type.RemoveUser;
    	}
    	else if (convert.equals("Message")) {
    		return Type.Message;
    	}
    	else if (convert.equals("AllUsers")) {
    		return Type.AllUsers;
    	}
    	else {
    		return null;
    	}
		
    }
    
    //create from string
    public static Event StringToEvent(String myString) {
    	String[] tokens = myString.split(" ");
    	
        Type newType=StringToType(tokens[0]);
        String[] users = tokens[1].split("-");
        ArrayList<String> newSendTo = new ArrayList<String>();
        for (int i=0; i<users.length; i++) {
            newSendTo.add(users[i]);
        }
        String newMessage=myString.replace(tokens[0]+" "+tokens[1]+" ", "");
        return new Event(newType, newSendTo, newMessage);
    }
    //toString.
    public String toString() {
        return type.toString()+" "+sendToToString()+" "+message;
    }
}

