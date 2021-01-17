package com.gahyunsuh.simplechat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class ClientWindow extends JFrame implements Runnable {

	private JTextArea history;
	private JTextField txtMessage;
	private JPanel contentPane;
	
	private Thread run, listen; // what's the diff from 'run' thread and 'run' method ???
	private Client client;
	
	private boolean running = false;
	
	public ClientWindow(String name, String address, int port) {
		setTitle("Molly Chat Client");
		client = new Client(name, address, port); 
		boolean connect = client.openConnection();
		if (!connect) {
			System.err.println("Connection Failed!");
			console("Connection Failed!");
		} else {
			createWindow();
			console("[SYSTEM] Attempting a connection to " + address + ":" + port + ", user: " + name);
			String connection = "/c/" + name + "/e/"; 
			client.send(connection.getBytes());

			running = true;
			run = new Thread(this, "Running");
			run.start();
		}
	}
	
	
	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880, 550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{30, 805, 40, 5}; // SUM == 880
		gbl_contentPane.rowHeights = new int[]{55, 455, 40}; // SUM == 550
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		JScrollPane scroll = new JScrollPane(history);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 5, 5, 5); // top, left, bottom, right (insets == padding)
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0; // index of the columnWidths array
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3; // how many grid cells this component will take up (default: 1)
		scrollConstraints.gridheight = 2;
		contentPane.add(scroll, scrollConstraints);

		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					send(txtMessage.getText()); //btnSend.doClick();
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() { // Button Action means 'clicked'.
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = txtMessage.getText();
				send(message);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		// handle Closing
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				//System.out.println("close");
				running = false;
				client.terminate();
			}
		});

		setVisible(true);
		txtMessage.requestFocusInWindow();
	}
	
	
	public void run() {
		listen();
	}
	
	public void listen() {
		
		listen = new Thread("Listen") {
			public void run() {
				while(running) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						// "/c/8125/e/             . . .    "
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						console("[SYSTEM] Successfully connected to server! ID: " + client.getID());
					} else if (message.startsWith("/m/")) {
						String messageToConsole = message.substring(3, message.length());
						messageToConsole = messageToConsole.split("/e/")[0]; // just to handle ... /m//m/hello/e/
						console(messageToConsole);
					} else if (message.startsWith("/u/")) {
						String userName = message.split("/u/|/e/")[1];
						console("[SYSTEM] '*+. New user (" + userName + ") has entered! .+*' ");
					} else if (message.startsWith("/i/")) {
						String response = "/i/" + client.getID() + "/e/";
						client.send(response.getBytes());
					} else {
						console(message);
					}
				}
			}
		};
		listen.start();
	}
	
	
	public void console(String message) {
		history.append(message + "\n\r");
		history.setCaretPosition(history.getDocument().getLength());
		System.out.println(message);
	}
	
	private void send(String message) {
		if (!message.equals("")) {
			message = client.getName() + ": " + message;
			message = "/m/" + message;
			client.send(message.getBytes());
			txtMessage.setText("");
			txtMessage.requestFocus();
		}
	}
}
