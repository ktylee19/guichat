package pubsub;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class EventTest {
	/**
	 * Tests every public method of the Event.java class.
	 */
    @Test
    public void EventTest(){
    	ArrayList<String> users = new ArrayList<String>();
    	users.add("&all");
    	Event testEvent = new Event(Event.Type.AddUser,users,"birkan has entered the chat");
    	assertEquals(testEvent.getType(),Event.Type.AddUser);
    	assertEquals(testEvent.getSendTo().contains("&all"),true);
    	assertEquals(testEvent.getMessage(),"birkan has entered the chat");
    	assertEquals(testEvent.getThirdToken(),"birkan");
    	assertEquals(testEvent.toString(),"AddUser &all birkan has entered the chat");
    	
        //type, arraylist, message
        Event myEvent = Event.StringToEvent("Message joreman-ryzhang ryzhang: hello");
        assertEquals(Event.Type.Message, myEvent.getType());
        assertEquals(myEvent.getSendTo().contains("joreman"), true);
        assertEquals(myEvent.getSendTo().contains("ryzhang"), true);
        System.out.println(myEvent.getSendTo());
        assertEquals(myEvent.getMessage(), "ryzhang: hello");   
    }
}
