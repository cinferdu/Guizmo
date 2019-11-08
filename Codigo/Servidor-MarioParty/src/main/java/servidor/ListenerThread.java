package servidor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.google.gson.Gson;

import game.Partida;
import mensaje.Mensaje;

public class ListenerThread extends Thread {
	private String nombreCliente;
	private int id_salaActiva; // sala en la que se encuetra el cliente
	private HashMap<String, DataOutputStream> clientesConectados;
	private DataInputStream entrada;
	private DataOutputStream salida;
	private Sala lobby;
	private TreeMap<Integer, Sala> salas;
	private TreeMap<Integer, Partida> partidas;
	
	private Gson gson = new Gson();

	public ListenerThread(Socket clienteRead, Socket clienteWrite, HashMap<String, DataOutputStream> clientesConectados, Sala lobby, TreeMap<Integer, Sala> salas, TreeMap<Integer, Partida> partidas) {
		this.partidas = new TreeMap<Integer, Partida>();
		try {
			salida = new DataOutputStream(new BufferedOutputStream(clienteWrite.getOutputStream()));
			salida.flush();
			entrada = new DataInputStream(new BufferedInputStream(clienteRead.getInputStream()));
		} catch (IOException e) {
			System.err.println("Error cliente");
			e.printStackTrace();
		}
		this.clientesConectados = clientesConectados;
		this.lobby = lobby;
		this.salas = salas;
		id_salaActiva = -1;
	}
	
	@Override
	public void run() {
		try {
			String cadenaLeida = entrada.readUTF();
			
			while (true) {//preguntar si es Desconectar
				Mensaje msj = Mensaje.getMensaje(cadenaLeida);
				msj.setListener(this);
				msj.ejecutar();

				//Servidor.test("Ejecutado");
				cadenaLeida = entrada.readUTF();
			}
			
			
			
		} catch (IOException e ) {
			System.err.println("Error de conexion con el cliente " + nombreCliente);
			e.printStackTrace();
		}
		/*
		comando.setListener(this);
		comando.ejecutar();
		*/
		// DESCONEXION
		// lo borro de las listas de conectado
		// .close() a todo
		// mensaje cliente X desconectado si esta en alguna sala
	
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public DataInputStream getEntrada() {
		return entrada;
	}

	public DataOutputStream getSalida() {
		return salida;
	}

	public Gson getGson() {
		return gson;
	}

	public HashMap<String, DataOutputStream> getClientesConectados() {
		return clientesConectados;
	}

	public Sala getLobby() {
		return lobby;
	}

	public TreeMap<Integer, Sala> getSalas() {
		return salas;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public void setEntrada(DataInputStream entrada) {
		this.entrada = entrada;
	}

	public void setSalida(DataOutputStream salida) {
		this.salida = salida;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public void setClientesConectados(HashMap<String, DataOutputStream> clientesConectados) {
		this.clientesConectados = clientesConectados;
	}

	public void setLobby(Sala lobby) {
		this.lobby = lobby;
	}

	public void setSalas(TreeMap<Integer, Sala> salas) {
		this.salas = salas;
	}

	public int getSalaActiva() {
		return id_salaActiva;
	}

	public void setSalaActiva(int salaActiva) {
		this.id_salaActiva = salaActiva;
	}

	public TreeMap<Integer, Partida> getPartidas() {
		return partidas;
	}

	public void setPartidas(TreeMap<Integer, Partida> partidas) {
		this.partidas = partidas;
	}

	public void enviarMensaje(Object mensaje) {
		try {
			salida.writeUTF(gson.toJson(mensaje));
			salida.flush();
		} catch (IOException e) {
			System.err.println("No se pudo enviar el mensaje");
			e.printStackTrace();
		}
	}
	
	public void enviarMensajeBroadcast(Object mensaje, ArrayList<String> nombres) {
		try {
			for (String string : nombres) {
				this.clientesConectados.get(string).writeUTF(gson.toJson(mensaje));
				this.clientesConectados.get(string).flush();
			}
		} catch (IOException e) {
			System.err.println("No se pudo enviar el mensaje");
			e.printStackTrace();
		}
	}

	public void agregarSala(Sala nuevaSala) {
		synchronized (salas) {
			this.salas.put(nuevaSala.getId_sala(), nuevaSala);
		}
	}
	
	public void agregarClienteAlLobby(String nombre){
		synchronized (lobby) {
			lobby.addCliente(nombre);
		}
	}
	
	public void sacarClienteAlLobby(String nombre){
		synchronized (lobby) {
			lobby.removeCliente(nombre);
		}
	}
	
}
