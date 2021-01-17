package com.gahyunsuh.simplechat;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import java.awt.GridBagLayout;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JTextField;

public class Client extends JFrame {
	private static final long serialVersionUID = 1L;

	// Connection by UDP (not TCP)
	private DatagramSocket socket;

	private String name;
	private String address;
	private int port; // server's port
	private InetAddress ip;
	private int ID;

	private Thread sendThread;

	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	// Open the connection with Data-gram socket
	boolean openConnection() { // it's package-private!
		boolean connect = false;
		try {
			socket = new DatagramSocket(); // remove "port" parameter when creating a socket.
			ip = InetAddress.getByName(address);
			connect = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return connect;
	}

	// Receive a packet (data) using the Data-gram socket
	String receive() {
		byte[] data = new byte[1024]; // 1 KB of data
		DatagramPacket packet = new DatagramPacket(data, data.length); // empty packet, currently.
		try {
			socket.receive(packet); // Will Sit there UNTIL it receives something! (will freeze our application...)
		} catch (IOException e) {
			e.printStackTrace();
		}
//		process(packet);
		String message = new String(packet.getData()); // converts the bytes into a string
		return message;
	}

	void setID(int inID) {
		this.ID = inID;
	}

	public int getID() {
		return ID;
	}

//	private void process(DatagramPacket packet) {
//		byte[] data = packet.getData();
//		String string = data.toString(); // new String( packet.getData() )
//		if (string.startsWith("/m")) {
//			
//		} else if (string.startsWith("/c")) {
//			int id = Integer.parseInt(string.substring(3, string.length()));
//		}
//	}

	// Sending a packet
	void send(final byte[] data) {
		sendThread = new Thread("Send") {
			public void run() {
				// a packet to Send to that (server ip) address
				DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}

	// Terminate an application (disconnect)
	void terminate() {
		String disconnectMsg = "/d/" + this.ID + "/e/";
		send(disconnectMsg.getBytes());
		new Thread() {
			public void run() {
				synchronized (socket) { 
					socket.close();
				}
			}
		}.start();
	}

}
