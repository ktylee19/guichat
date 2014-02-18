package gui;



import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

//import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.DefaultListModel;
import javax.swing.JList;
//import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import funstuff.Music;

import pubsub.Event;
import pubsub.Listener;

import main.Client;


/**
 * Creates main Chatroom window.
 */
public class ChatWindow extends JPanel implements ActionListener, Listener{
    private final DefaultListModel listModel;
    private JList inChat;
    private final JScrollPane inChatScroll;
    private final JTextArea chatBox;
    private final JScrollPane chatBoxScroll;
    private final JTextPane sendBox;
    private final JScrollPane sendBoxScroll;
    private final JButton submit;
    private final JButton disconnect;
    private final Client client;
    private final String username;

    private final JLabel mainText;
    private final JLabel inChatText;

    private static ArrayList<String> defaultAll = new ArrayList<String>(){{
        add("&all");
    }};

    /**
     * Creates a ChatWindow
     * 
     * @param current client
     * @param username of other client being communicated to
     */
    public ChatWindow(Client client, String username) {
        //New Client
        this.client = client;
        this.username = username;
        client.getPublisher().addListener(this);
        //online list


        //labels
        mainText = new JLabel("Chat with "+username);
        inChatText = new JLabel("Users in this chat:");
        listModel = new DefaultListModel();
        inChat = new JList(listModel);
        listModel.addElement(username);
        listModel.addElement(client.username);
        inChatScroll = new JScrollPane(inChat);
        inChatScroll.setPreferredSize(new Dimension(209,360));

        //all chats
        chatBox = new JTextArea("");
        chatBox.setEditable(false);
        chatBox.setLineWrap(true);
        chatBoxScroll = new JScrollPane(chatBox);
        chatBoxScroll.setPreferredSize(new Dimension(391,360));
        chatBoxScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //type message area
        sendBox = new JTextPane();
        sendBox.setSize(new Dimension(450,40));
        sendBox.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submit.doClick();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendBox.setText(null);
                }
            }
            @Override
            public void keyTyped(KeyEvent e) {}

        });
        sendBoxScroll = new JScrollPane(sendBox);

        sendBoxScroll.setPreferredSize(new Dimension(349,40));
        sendBoxScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //submit
        submit = new JButton("Send");
        submit.setPreferredSize(new Dimension(40,40));
        submit.addActionListener(this);

        //disconnect
        disconnect = new JButton("Disconnect");
        disconnect.setPreferredSize(new Dimension(60,40));
        disconnect.addActionListener(this);

        // defines layout
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        // place the components in the layout 
        layout.setHorizontalGroup(
                layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup()
                        .addComponent(mainText)
                        .addComponent(chatBoxScroll)
                        .addComponent(sendBoxScroll))

                        .addGroup(layout.createParallelGroup()
                                .addComponent(inChatText)
                                .addComponent(inChatScroll)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(submit)
                                        .addComponent(disconnect)))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(mainText)
                        .addComponent(inChatText))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(chatBoxScroll)
                                .addComponent(inChatScroll))
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(sendBoxScroll)
                                        .addComponent(submit)
                                        .addComponent(disconnect))
        );
    }

    /**
     * Adds message to this 1-on-1 chat window 
     * 
     * @param message
     */
    public void ChatThisWindow(String message) {
        chatBox.append(message+"\n");
    }

    /**
     * Returns all messages that have taken place in this ChatWindow 
     * during the time connected to the server
     */
    public String getHistory() {
        return chatBox.getText();
    }

    /**
     * Listens for actions on the GUI.  
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //client submits text
        if(e.getSource() == submit) {
            String message = client.username + ": " + sendBox.getText();
            message = message.replaceAll("\\n", " ");
            client.output.println("Message "+client.username+"-"+this.username+" "+message);
            sendBox.setText(null);
        }
        //client requests logout
        else if (e.getSource() == disconnect) {
            String message = client.username + " left the chat room.";
            client.output.println(new Event(Event.Type.RemoveUser,defaultAll, message));
            System.exit(0); 
        }

    }

    /**
     * Handles events broadcasted by the Publisher.
     */
    @Override
    public void event(final String serverResponse) { 
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Event myEvent = Event.StringToEvent(serverResponse);
                if (myEvent.getType().equals(Event.Type.Message) && myEvent.getSendTo().contains(username)) {
                    chatBox.append(myEvent.getMessage()+"\n");

                    //check if file has keywords
                    String key = Music.keywordIn(myEvent.getMessage().substring(myEvent.getMessage().indexOf(" ")));
                    if (key != null) {
                        new Music(key); 
                    }

                }
                else if (myEvent.getType().equals(Event.Type.RemoveUser)) {
                    // if user is particular user who left the chat, then write it on this specific chat window
                    if (myEvent.getThirdToken().equals(username)) {
                        chatBox.append(myEvent.getMessage()+"\n");
                        listModel.removeElement(username);
                    }
                }
                else if (myEvent.getType().equals(Event.Type.AddUser)) {
                    // if user is particular user who left the chat, then write it on this specific chat window
                    if (myEvent.getThirdToken().equals(username)) {
                        chatBox.append(myEvent.getMessage()+"\n");
                        listModel.addElement(username);
                    }
                }
            }});
    }
}
