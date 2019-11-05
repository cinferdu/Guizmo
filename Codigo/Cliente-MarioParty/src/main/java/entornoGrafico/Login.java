package entornoGrafico;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.google.gson.Gson;

import cliente.Cliente;
import mensaje.MsjLogin;
import mensajeCliente.MsjClienteLogin;
import mensajeCliente.Paquete;
import paquete.PaqueteLogin;

import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.SwingConstants;

public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private Cliente cliente;

	public static void main(String[] args) {
		new Login(null).setVisible(true);
		
	}
	/**
	 * Create the frame.
	 */
	public Login(Cliente cliente) {
		this.cliente = cliente;
		setResizable(false);
		setTitle("Ingreso");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 174, 275);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(29, 112, 100, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblLogin = new JLabel("Login");
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogin.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblLogin.setBounds(0, 11, 158, 37);
		contentPane.add(lblLogin);
		
		JButton btnNewButton = new JButton("Ingresar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String nombre = textField.getText().trim();
				System.out.println(new Gson().toJson(new PaqueteLogin(nombre)));
				if (nombre.length() > 0) {
					cliente.enviarMensaje((Object) new PaqueteLogin(nombre));
				}
			}
		});
		btnNewButton.setBounds(29, 163, 100, 23);
		contentPane.add(btnNewButton);
		
		JLabel lblNombre = new JLabel("Nombre");
		lblNombre.setBounds(29, 87, 46, 14);
		contentPane.add(lblNombre);
	}
}