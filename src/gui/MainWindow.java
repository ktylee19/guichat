package gui;

/*
 * TabComponentDemo.java requires one additional file:
 *   ButtonTabComponent.java
 */
import javax.swing.*;

import funstuff.Music;

import main.Client;

import pubsub.Event;
import pubsub.Listener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Creating and using TabComponentsDemo example  
 */
public class MainWindow extends JFrame implements ActionListener, Listener {    
    private final JTabbedPane pane = new JTabbedPane();

    //properties inside the lobby
    private final DefaultListModel listModel;
    private final JLabel mainText;
    private final JLabel allOnlineText;
    private JList online;
    private final JScrollPane onlineScroll;
    private static JTextArea allBox;
    private final JScrollPane allBoxScroll;
    private final JTextPane sendBox;
    private final JScrollPane sendBoxScroll;
    private final JButton submit;
    private final JButton disconnect;

    //properties inside the history
    private final DefaultListModel historyModel;
    private final JLabel historyMainText;
    private final JLabel allPriorText;
    private JList allPrior;
    private final JScrollPane allPriorScroll;
    private final JTextArea historyBox;
    private final JScrollPane historyBoxScroll;

    //all
    private final Client client; 
    private final HashMap<String, ChatWindow> allChatWindows = new HashMap<String, ChatWindow>();
    private static ArrayList<String> defaultAll = new ArrayList<String>(){{
        add("&all");
    }};

    /**
     * Initializes a MainWindow
     * 
     * Begins with two tabs:: Lobby and History
     * User can begin new chats by clicking on people online in the lobby.
     * User can view chats that have been started with other users in the History section. 
     * 
     * @param client
     */
    public MainWindow(Client client) {
        super("ChatTrollette");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);     
        add(pane);   
        this.client = client;
        this.setTitle(client.username+" - ChatTrollette");

        /**
         * Lobby Section
         * 
         * Lobby area for all users.
         * Shows everyone who is connected to the server.
         * Group Chat available for all people in the lobby. 
         * Can begin chats with users onlnie by double clicking 
         *  on usernames on the list. 
         */
        //labels
        mainText = new JLabel("Chat with Everyone:");
        mainText.setPreferredSize(new Dimension(349,40));
        allOnlineText = new JLabel("Online Users:");

        //main window thing:
        listModel = new DefaultListModel();
        online = new JList(listModel);
        onlineScroll = new JScrollPane(online);
        onlineScroll.setPreferredSize(new Dimension(209,360));
        // add listeners for user input
        online.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                // respond only on double-click
                if (event.getClickCount() == 2) {
                    chatWithFriend();
                }
            }
        });
        online.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    chatWithFriend(); 
                }
            }
        });

        String welcome= client.username + ", welcome to the ChatTrollette! \n";

        //all chats
        allBox = new JTextArea(welcome); 
        allBox.setEditable(false);
        allBox.setLineWrap(true);
        allBoxScroll = new JScrollPane(allBox);
        allBoxScroll.setPreferredSize(new Dimension(391,360));
        allBoxScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //type message area
        sendBox = new JTextPane();
        sendBox.setSize(new Dimension(450,40));
        sendBox.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { //allow people to send messages with Enter
                    submit.doClick();
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { //clears message box after entering. 
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
        Panel myPanel = new Panel();
        GroupLayout layout = new GroupLayout(myPanel);
        myPanel.setLayout(layout);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        // place the components in the layout 
        layout.setHorizontalGroup(
                layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup()
                        .addComponent(mainText)
                        .addComponent(allBoxScroll)
                        .addComponent(sendBoxScroll))

                        .addGroup(layout.createParallelGroup()
                                .addComponent(allOnlineText)
                                .addComponent(onlineScroll)
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(submit)
                                        .addComponent(disconnect)))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()

                .addGroup(layout.createParallelGroup()
                        .addComponent(mainText)
                        .addComponent(allOnlineText))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(allBoxScroll)
                                .addComponent(onlineScroll))
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(sendBoxScroll)
                                        .addComponent(submit)
                                        .addComponent(disconnect))
        );

        //creates Lobby tab
        String title1 = "Lobby";
        pane.add(title1, myPanel);
        initTabComponent(0, title1);

        /**
         * History Section
         * 
         * Stores history of all chats with users that have taken part beforehand.
         * 
         */

        //labels
        allPriorText = new JLabel("See Prior Chats with...");
        historyMainText = new JLabel("History View");

        //components
        historyModel = new DefaultListModel();
        allPrior = new JList(historyModel);
        allPriorScroll = new JScrollPane(allPrior);
        allPriorScroll.setPreferredSize(new Dimension(209,360));

        // add listeners for user input
        allPrior.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    showNewHistory();
                    System.out.println("history");
                }
            }

        });
        allPrior.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    showNewHistory(); 
                    System.out.println("history");
                }
            }
        });

        //historyView
        historyBox = new JTextArea("Select a chat to see the history!"); 
        historyBox.setEditable(false);
        historyBox.setLineWrap(true);
        historyBoxScroll = new JScrollPane(historyBox);
        historyBox.setPreferredSize(new Dimension(391,360));
        historyBoxScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // defines layout
        Panel myHistoryPanel = new Panel();
        GroupLayout historyLayout = new GroupLayout(myHistoryPanel);
        myHistoryPanel.setLayout(historyLayout);
        historyLayout.setAutoCreateContainerGaps(true);
        historyLayout.setAutoCreateGaps(true);

        // place the components in the layout 
        historyLayout.setHorizontalGroup(
                historyLayout.createSequentialGroup()
                .addGroup(historyLayout.createParallelGroup()
                        .addComponent(historyMainText)
                        .addComponent(historyBoxScroll))

                        .addGroup(historyLayout.createParallelGroup()
                                .addComponent(allPriorText)
                                .addComponent(allPriorScroll))
        );

        historyLayout.setVerticalGroup(
                historyLayout.createSequentialGroup()
                .addGroup(historyLayout.createParallelGroup()
                        .addComponent(historyMainText)
                        .addComponent(allPriorText))
                        .addGroup(historyLayout.createParallelGroup()
                                .addComponent(historyBoxScroll)
                                .addComponent(allPriorScroll))
        );

        //creates History Tab
        String title2 = "History";
        pane.add(title2, myHistoryPanel);
        initTabComponent(1, title2);


        //sets properties of this.
        pane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        setSize(new Dimension(400, 200));
        setLocationRelativeTo(null);
        setVisible(true);

        //asks to repopulate allUsers list for Lobby.
        client.output.println(new Event(Event.Type.AllUsers, defaultAll, ""));
    }
    /**
     * shows new history
     */
    private void showNewHistory() {
        if (allPrior.isSelectionEmpty()) return; 
        int selectedIndex = allPrior.getMinSelectionIndex();
        Object selected = allPrior.getModel().getElementAt(selectedIndex);
        historyMainText.setText("Chat History with "+selected);

        //select text for given chatWindow if it exists
        if (allChatWindows.containsKey(selected)) {
            historyBox.setText(allChatWindows.get(selected).getHistory());
        }
        else { 
            historyBox.setText("No chat has been started with this user.");
        }
    }

    /**
     * Switch the model to display the friend currently selected in the friends list.
     * If no friend is selected, then has no effect. 
     */
    private void chatWithFriend() {
        if (online.isSelectionEmpty()) return; // nobody selected
        int selectedIndex = online.getMinSelectionIndex();
        Object selected = online.getModel().getElementAt(selectedIndex);
        //if current chat is already existing, switch to that chat.
        if (ButtonTabComponent.getAllTabs().contains("Chat with "+selected.toString())) {
            pane.setEnabledAt(ButtonTabComponent.getAllTabs().indexOf("Chat with "+selected.toString()), true);

        }
        else if (selected.toString().equals(client.username)) {/*pass*/}
        else { addChat((String)selected); }
    }

    /**
     * Creates chat window with given user
     * @param username of user who you want to chat to
     * @return ChatWindow instance of chatting with that person. 
     */
    public ChatWindow addChat(String username) {
        int i=pane.getTabCount();
        String title3 = "Chat with "+username;
        ChatWindow newChat;
        if (allChatWindows.containsKey(username)) {
            newChat=allChatWindows.get(username);
        }
        else {
            newChat = new ChatWindow(client, username);
            allChatWindows.put(username, newChat);
        }
        pane.add(title3, newChat);
        initTabComponent(i, title3).addClose();
        pane.setSelectedIndex(i);
        return newChat;
    }


    private ButtonTabComponent initTabComponent(int i, String title) {
        ButtonTabComponent myBTC = new ButtonTabComponent(pane, title);
        pane.setTabComponentAt(i, myBTC);
        return myBTC;
    }
    /**
     * Handles action listeners for submit and disconnect buttons
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //send text from client to server
        if(e.getSource() == submit) {
            String message = client.username + ": " + sendBox.getText();
            message = message.replaceAll("\\n", " ");
            client.output.println(new Event(Event.Type.Message,defaultAll, message));
            sendBox.setText(null);
        }
        //request disconnect from client to server
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
                if (myEvent.getType().equals(Event.Type.AddUser)) {
                    allBox.append(myEvent.getMessage()+"\n");
                    listModel.addElement(myEvent.getThirdToken());
                    if (!historyModel.contains(myEvent.getThirdToken())) {
                        historyModel.addElement(myEvent.getThirdToken());
                    }
                }
                else if (myEvent.getType().equals(Event.Type.Message) && myEvent.getSendTo().contains("&all")) {
                    allBox.append(myEvent.getMessage()+"\n"); 

                    //check if file has keywords
                    String key = Music.keywordIn(myEvent.getMessage().substring(myEvent.getMessage().indexOf(" ")));
                    if (key != null) {
                        new Music(key); 
                    }
                }
                else if (myEvent.getType().equals(Event.Type.Message) && 
                        myEvent.getSendTo().contains(client.username)) {
                    //find username that is not you. only two people per chat.
                    String otherUser;
                    if (myEvent.getSendTo().indexOf(client.username)==1) {otherUser=myEvent.getSendTo().get(0);}
                    else {otherUser=myEvent.getSendTo().get(1);}

                    //if a tab that is not your username pops up, create it.
                    if (ButtonTabComponent.getAllTabs().contains("Chat with "+otherUser)) {
                        //chat already exists. stop bothering me.
                    }
                    else if (allChatWindows.containsKey(otherUser)) {
                        addChat(otherUser);
                    }
                    else {
                        ChatWindow myChat = addChat(otherUser);
                        myChat.ChatThisWindow(myEvent.getMessage());
                    }
                }


                else if (myEvent.getType().equals(Event.Type.RemoveUser)) {
                    allBox.append(myEvent.getMessage()+"\n");
                    listModel.removeElement(myEvent.getThirdToken());
                    //do not remove from historyModel because want to store chats with logged-out users
                    //close tab with specific user if you are currently chatting with that user.
                    if (ButtonTabComponent.getAllTabs().contains("Chat with "+myEvent.getThirdToken())) {
                        int i= pane.indexOfTab("Chat with "+myEvent.getThirdToken());
                        pane.remove(i);
                        ButtonTabComponent.closeTab("Chat with "+myEvent.getThirdToken());
                        pane.setSelectedIndex(0);

                    }
                }
                else if (myEvent.getType().equals(Event.Type.AllUsers)){
                    if (!listModel.contains(myEvent.getMessage()) && !myEvent.getMessage().trim().equals("")) {
                        listModel.addElement(myEvent.getThirdToken());
                        if (!myEvent.getThirdToken().equals(client.username)){
                            historyModel.addElement(myEvent.getThirdToken());
                        }
                    }
                }

            }});
    }
}