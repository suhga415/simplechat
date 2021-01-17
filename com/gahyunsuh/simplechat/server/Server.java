package com.gahyunsuh.simplechat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;

public class Server implements Runnable {

	private List<ServerClient> clients;
	private List<Integer> clientResponse;
	
	private int port;
	private DatagramSocket socket;
	private boolean running = false;
	private Thread run, manage, send, receive;
	
	private final int MAX_ATTEMPT = 4;
	
	public Server(int port) {
		this.clients = new ArrayList<ServerClient>();
		this.clientResponse = new ArrayList<Integer>();
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this, "Server"); 
		run.start();
	}

	@Override
	public void run() {
		running = true;
		System.out.println("Server started on port " + port);
		manageClients();
		receive();
		
		Scanner scanner = new Scanner(System.in);
		while (running) {
			String text = scanner.nextLine(); // get one line from the console 
			if (!text.startsWith("/")) {
				sendToAll("/m/Server: " + text + "/e/");
			} else { // Let the Sever commands start with "/".
				
			}
		}
	}
	
	private void manageClients() { 
		// sending ping...? check if any user is timed out
		manage = new Thread("Manage") { // anonymous class
			public void run() {
				while (running) {
					// Managing ... such as disconnection of clients.
					// sending ping to each client
					sendToAll("/i/server");
					try {
						Thread.sleep(2000);  // the 'manage' Thread will wait for 2 seconds
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (!clientResponse.contains(c.getID())) {
							if (c.attempt > MAX_ATTEMPT) {
								disconnectUser(c.getID(), false); // unclean exit
							} else {
								c.attempt++;
							}
						} else {
							clientResponse.remove(new Integer(c.getID())); // should be an object, not an index
							c.attempt = 0;
						}
					}
				}
			}
		};
		manage.start();
	}
	
	private void receive() {
		receive = new Thread("Receive") { // anonymous class
			public void run() {
				while (running) {
					//System.out.println(clients.size());
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);	
					try {
						socket.receive(packet); // waits until it gets something.
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					process(packet);
					
				}
			} // �� thread �� run �޼���� ���� ��ġ��? 
		};
		receive.start();
	}
	
	private void process(DatagramPacket packet) {
		String string = new String(packet.getData());
		if (string.startsWith("/c/")) {
			String userName = string.split("/c/|/e/")[1];
			// The prefix "/c/" means a connection packet.
//			int random = new Random().nextInt(); 
//			UUID id = UUID.randomUUID(); // it returns a string of massive (hex-type?) unique identifier...
			int id = UniqueIdentifier.getIdentifier();
			System.out.println("Identifier: " + id);
			clients.add(new ServerClient(userName, packet.getAddress(), packet.getPort(), id));
			String messageID = "/c/" + id;
			send(messageID, packet.getAddress(), packet.getPort());
			String messageToAll = "/u/" + userName + "/e/";
			sendToAll(messageToAll);
		} else if (string.startsWith("/m/")) {
			// The prefix "/m/" means a message packet from one client.
			sendToAll(string);
		} else if (string.startsWith("/d/")) {
			int userID = Integer.parseInt(string.split("/d/|/e/")[1]);
			disconnectUser(userID, true); // status -- disconnecting by timed-out? or intentionally? 
		} else if (string.startsWith("/i/")) {
			clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
		} else {
			System.out.println(string);
		}
	}
	
	private void sendToAll(String message) { 
		for (int i = 0; i < clients.size(); i++) { 
			ServerClient client = clients.get(i);
			byte[] data = message.getBytes();
			send(data, client.address, client.port); // they are public...
		}
	}
	
	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		send.start();
	}
	
	private void send(String message, InetAddress address, int port) {
		message += "/e/"; 
		send(message.getBytes(), address, port);
	}

	
	private void disconnectUser(int id, boolean status) {
		// remove the client from the clients List
		String name = "";
		String consoleMsg;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == id) {
				name = clients.get(i).name;
				clients.remove(i);
				break;
			}
		}
		// get feedback to console
		if (status)
			consoleMsg = "[SYSTEM] User (" + name + ") disconnected.";
		else
			consoleMsg = "[SYSETM] User (" + name + ") timed out.";
		sendToAll(consoleMsg);
		System.out.println(consoleMsg);
	}
	

}


