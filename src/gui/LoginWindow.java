package gui;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import main.Client;


/**
 * Creates initial login window.
 */

public class LoginWindow extends JFrame implements ActionListener {
    private final JLabel title;
    private final JButton startButton;
    private final JLabel usernameLabel;
    private final JTextField usernameInput;
    private final JLabel portLabel;
    private final JTextField portInput;
    private final JLabel hostLabel;
    private final JTextField hostInput;
    private final JTextArea errorLabel;
    // the Client object
    private Client client;
    // the default port number
    private int port;
    private String host;

    /**
     * Login Window GUI for GUIChat.
     * 
     * User types in desired username, and submits to create a new Client.
     * Error messages will appear if username is not valid OR username is 
     * already taken.
     * If no error in username, then LoginWindow will close and MainWindow 
     *  will be created.
     */
    public LoginWindow() {
        //Components

        //title label
    	ImageIcon icon = new ImageIcon("src/funstuff/icon3.png");
    	title = new JLabel(); 
    	title.setIcon(icon);

        //port
        portLabel = new JLabel("Port:");
        portInput = new JTextField("4444");
        portInput.setSize(new Dimension(30,25));

        //host
        hostLabel = new JLabel("Host:");
        hostLabel.setSize(new Dimension(50,25));
        hostInput = new JTextField("localhost");
        hostInput.setSize(new Dimension(50,25));

        //username
        usernameLabel = new JLabel("Username:");
        usernameInput = new JTextField();
        usernameInput.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startButton.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}

        });
        usernameLabel.setSize(new Dimension(165,25));

        //label for potential error message
        errorLabel = new JTextArea("");
        errorLabel.setName("errorLabel");
        errorLabel.setLineWrap(true);
        errorLabel.setOpaque(false);
        errorLabel.setEditable(false);

        //startChatting
        startButton = new JButton("Login");
        startButton.addActionListener(this);
        startButton.setPreferredSize(new Dimension(40,140));

        //main window
        setTitle("ChatTrollette - Login"); //title of page
        setLayout(new BorderLayout());

        //defines layout
        JPanel myPanel = new JPanel();
        GroupLayout layout = new GroupLayout(myPanel);
        myPanel.setLayout(layout);
        this.getContentPane().add(myPanel);

        // get some margins around components by default
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        //testing
        Container c = getContentPane();
        c.setPreferredSize( new Dimension(312,260));

        // place the components in the layout (which also adds them
        // as children of this view)
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addComponent(title)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(hostLabel)
                        .addComponent(hostInput)
                        .addComponent(portLabel)
                        .addComponent(portInput))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(usernameLabel)
                                .addComponent(usernameInput))
                                .addComponent(errorLabel)
                                .addComponent(startButton)
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addComponent(title)
                .addGroup(layout.createParallelGroup()
                        .addComponent(hostLabel)
                        .addComponent(hostInput)
                        .addComponent(portLabel)
                        .addComponent(portInput))
                        .addGroup(layout.createParallelGroup()
                                .addComponent(usernameLabel)
                                .addComponent(usernameInput))
                                .addComponent(errorLabel)
                                .addComponent(startButton)
        );

    }
    /**
     * Opens login window
     */
    public static void main(final String[] args) {
        LoginWindow main = new LoginWindow();
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.pack();
        main.setResizable(false);
        main.setVisible(true);
    }
    
    /**
     * ActionListener to handle an attempted login
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        /**
         * ActionListener for Start Button.  
         * 
         * Creates MainWindow and closes LoginWindow if username is valid.
         * Else, print an error.
         */
        if(e.getSource().equals(startButton)) {
            //check for valid inputs. 
            String newUser = usernameInput.getText();
            if (!portInput.getText().matches("[0-9]+")) {
                errorLabel.setText("Set valid port number.");
                return;
            }
            if (hostInput.getText().equals(null)) {
                errorLabel.setText("Set valid host.");
                return;
            }
            if (!usernameInput.getText().trim().matches("[a-zA-Z0-9]+")) {
                errorLabel.setText("Username must be alphanumeric characters.");
                return;
            }

            port = Integer.parseInt(portInput.getText()); 
            host = hostInput.getText();
            hostInput.setEditable(false);
            portInput.setEditable(false);
            usernameInput.setEditable(false);
            if (newUser.trim() != null) {
                client = new Client(host,port,newUser);
                try {
                    String result = client.startDialog();
                    if(!result.equals("Connected")) {
                        if (result.equals("Username already taken.")){
                            errorLabel.setText("Username is already taken.");
                            usernameInput.setEditable(true);
                        }
                        else {
                            errorLabel.setText("Error connecting to server.");
                            hostInput.setText("");
                            portInput.setText("");
                            hostInput.setEditable(true);
                            portInput.setEditable(true);
                            usernameInput.setEditable(true);
                        }
                    }

                    else if (client.username.equals(null)) {
                        errorLabel.setText("Username must be alphanumeric characters.");
                        usernameInput.setText("");
                        hostInput.setEditable(true);
                        portInput.setEditable(true);
                        usernameInput.setEditable(true);
                    }

                    else {
                        dispose();
                        startMainWindow(client);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            else {
                errorLabel.setText("Connection failed. Please try again!");
                usernameInput.setText("");
                hostInput.setEditable(true);
                portInput.setEditable(true);
                usernameInput.setEditable(true);
            }
        }   
    }

    /**
     * Begins a MainWindow with the client created from a successful Server connect.
     * @param client
     */
    public void startMainWindow(final Client client) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final MainWindow main = new MainWindow(client);
                client.getPublisher().addListener(main);
                main.setDefaultCloseOperation(EXIT_ON_CLOSE);
                Container c = main.getContentPane();
                c.setPreferredSize( new Dimension(600,400));
                main.pack();
                main.setResizable(false);
                main.setVisible(true);

            }
        });
    }
}
