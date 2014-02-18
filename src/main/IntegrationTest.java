package main;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pubsub.Event;


/**
 * Tests integration of Server, clientThread classes to make sure that initalization of clientThread is secure.
 * We test that a socket can connect to the Server and assume a username, 
 * that a second socket can connect with a different username and that the first socket will be notifies,
 * and that a second socket cannot assume the same username as the first.
 * @category no_didit
 */
public class IntegrationTest {
    @Before
    public void setUp() throws IOException, InterruptedException {
        //To let next server startup wait a little after the last test:
        Thread.sleep(150);
        System.out.println("calling server");
        TestTool.startServer();

    }

    @After
    public void tearDown() {
        System.out.println("closed");
        TestTool.endServer();

    }

    //To make sure that Server does not use ArrayList of String usernames that Socket provides.
    //Should be using own clientThreads ArrayList instead (which should be empty)
    @Test(timeout = 10000)

    public void basicOneSocketName1Test() throws IOException, InterruptedException {

        try {
            //socket serves as client
            Socket socket = TestTool.socketConnect();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //Create a dummy list of usernames to send to... only for the purposes of creating a param
            ArrayList<String> sendTo = new ArrayList<String>();
            sendTo.add("UserA");
            sendTo.add("UserB");
            //socket wants to join as first user of Server, as "UserA"
            out.println(new Event(Event.Type.AddUser, sendTo, "UserA"));

            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in));

            in.close();
            out.close();
            socket.close();
            // server.closed = true;
        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    
    //To test that a socket can connect to the Server as a user and receives correct reply back
    @Test(timeout = 10000)
    public void basicOneSocketName2Test() throws IOException, InterruptedException {


        try {
            //socket = user

            Socket socket = TestTool.socketConnect();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            //Create a dummy list of usernames to send to... only for the purposes of creating a param
            ArrayList<String> sendTo = new ArrayList<String>();
            sendTo.add("UserA");
            sendTo.add("UserB");

            
            //socket wants to join as first user of Server, as "UserC"
            out.println(new Event(Event.Type.AddUser, sendTo, "UserC"));


            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in));
            

            in.close();
            out.close();
            socket.close();

        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    

    //Tests that Server notifies all other users that a new user has joined the chatroom
    @Test(timeout = 10000)
    public void basicTwoSocketTest() throws IOException, InterruptedException {

        try {
            //user #0:
            Socket socket0 = TestTool.socketConnect();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket0.getInputStream()));
            PrintWriter out = new PrintWriter(socket0.getOutputStream(), true);

            
            ArrayList<String> sendTo = new ArrayList<String>();
            sendTo.add("UserA");
            sendTo.add("UserB");
            //user #0 joins as "UserC"
            out.println(new Event(Event.Type.AddUser, sendTo, "UserC"));


            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in));

            
            //user #1:
            Socket socket1 = TestTool.socketConnect();
            BufferedReader in1 = new BufferedReader(new InputStreamReader(
                    socket1.getInputStream()));
            PrintWriter out1 = new PrintWriter(socket1.getOutputStream(), true);

            //user #1 joins as "UserD"
            out1.println(new Event(Event.Type.AddUser, sendTo, "UserD"));


            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in1));
            
            //Should let user #0 know that a new user has joined the chatroom.
            assertEquals("AddUser &all AddUser UserA-UserB UserD has joined the chatroom.", TestTool.nextNonEmptyLine(in));

            in.close();
            out.close();
            in1.close();
            out1.close();
            socket0.close();
            socket1.close();
        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    //Tests that Server can correctly reject a username if it's already taken
    @Test(timeout = 10000)
    public void basicTwoSocketSameNameTest() throws IOException, InterruptedException {

        try {
            //user 2
            Socket socket2 = TestTool.socketConnect();
            BufferedReader in2 = new BufferedReader(new InputStreamReader(
                    socket2.getInputStream()));
            PrintWriter out2 = new PrintWriter(socket2.getOutputStream(), true);

            ArrayList<String> sendTo = new ArrayList<String>();
            sendTo.add("UserA");
            sendTo.add("UserB");
           //user 2 joins as "UserC"
            out2.println(new Event(Event.Type.AddUser, sendTo, "UserC"));


            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in2));

            //user 3
            Socket socket3 = TestTool.socketConnect();
            BufferedReader in3 = new BufferedReader(new InputStreamReader(
                    socket3.getInputStream()));
            PrintWriter out3 = new PrintWriter(socket3.getOutputStream(), true);

           //user 3 tries to join as "UserC"
            out3.println(new Event(Event.Type.AddUser, sendTo, "UserC"));


            //Should verify that 'username' is taken by user 2 when Server's newUser() is called
            assertEquals("Username already taken.", TestTool.nextNonEmptyLine(in3));
            
            in2.close();
            out2.close();
            in3.close();
            out3.close();
           
            socket2.close();
            socket3.close();
        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    //Tests that three sockets can connect to the Server
    @Test(timeout = 10000)
    public void basicThreeSocketTest() throws IOException, InterruptedException {

        try {
            //user 4
            Socket socket4 = TestTool.socketConnect();
            BufferedReader in4 = new BufferedReader(new InputStreamReader(
                    socket4.getInputStream()));
            PrintWriter out4 = new PrintWriter(socket4.getOutputStream(), true);

            ArrayList<String> sendTo = new ArrayList<String>();
            sendTo.add("UserA");
            sendTo.add("UserB");
            //user 4 joins with "User C"
            out4.println(new Event(Event.Type.AddUser, sendTo, "UserC"));


            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in4));

             
            //user 5
            Socket socket5 = TestTool.socketConnect();
            BufferedReader in5 = new BufferedReader(new InputStreamReader(
                    socket5.getInputStream()));
            PrintWriter out5 = new PrintWriter(socket5.getOutputStream(), true);

           //user 5 joins with "User E"
            out5.println(new Event(Event.Type.AddUser, sendTo, "UserE"));


            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in5));
            
            
           //Should let user 4 know that a new user has joined the chatroom.
            assertEquals("AddUser &all AddUser UserA-UserB UserE has joined the chatroom.", TestTool.nextNonEmptyLine(in4));

            //user 6
            Socket socket6 = TestTool.socketConnect();
            BufferedReader in6 = new BufferedReader(new InputStreamReader(
                    socket6.getInputStream()));
            PrintWriter out6 = new PrintWriter(socket6.getOutputStream(), true);

           //user 6 tries to join as "UserF"
            out6.println(new Event(Event.Type.AddUser, sendTo, "UserF"));


            //Should verify that 'username' is unique, because Server's newUser() is called
            assertEquals("Username not taken.", TestTool.nextNonEmptyLine(in6));
            
           //Should let user 4 know that a new user has joined the chatroom.
            assertEquals("AddUser &all AddUser UserA-UserB UserF has joined the chatroom.", TestTool.nextNonEmptyLine(in4));

            //Should let user 5 know that a new user has joined the chatroom.
            assertEquals("AddUser &all AddUser UserA-UserB UserF has joined the chatroom.", TestTool.nextNonEmptyLine(in5));

            socket4.close();
            socket5.close();
            socket6.close();
        } catch (SocketTimeoutException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    
    
    
}


