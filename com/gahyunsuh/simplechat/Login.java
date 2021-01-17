package com.gahyunsuh.simplechat;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;

public class Login extends JFrame {	
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JTextField txtPort;
	private JLabel lblAddressDesc;
	private JLabel lblPortDesc;

	public Login() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 380);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(133, 44, 46, 16);
		contentPane.add(lblName);
		
		txtName = new JTextField();
		txtName.setBounds(67, 57, 165, 26);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblAddress = new JLabel("IP Address:");
		lblAddress.setBounds(112, 102, 75, 16);
		contentPane.add(lblAddress);
		
		txtAddress = new JTextField();
		txtAddress.setBounds(67, 116, 165, 26);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(133, 173, 34, 16);
		contentPane.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.setBounds(67, 187, 165, 26);
		contentPane.add(txtPort);
		txtPort.setColumns(10);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() { // Anonymous Inner Type
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String address = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name, address, port);
			}
			
		});
		
		
		// TEMPORARY!! ------------------------------------------------
		txtPort.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String name = txtName.getText();
					String address = txtAddress.getText();
					int port = Integer.parseInt(txtPort.getText());
					login(name, address, port);
				}
			}
		}); // ---------------------------------------------------------
		
		
		btnLogin.setBounds(91, 288, 117, 35);
		contentPane.add(btnLogin);
		
		lblAddressDesc = new JLabel("(eq. 192.168.0.2)");
		lblAddressDesc.setBounds(96, 140, 107, 16);
		contentPane.add(lblAddressDesc);
		
		lblPortDesc = new JLabel("(eq. 8192)");
		lblPortDesc.setBounds(118, 212, 63, 16);
		contentPane.add(lblPortDesc);
	}
	
	private void login(String name, String address, int port) {
		dispose();
		ClientWindow client = new ClientWindow(name, address, port);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
