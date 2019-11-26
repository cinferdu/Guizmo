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

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import casilla.Casilla;
import game.Jugador;
import game.Partida;
import mensaje.Mensaje;
import mensaje.MsjAvisarClienteAbandonoSala;
import mensaje.MsjPartidaPuntajesFinales;
import util.UtilesLog;

public class ListenerThread extends Thread {
	private String nombreCliente;
	private int id_salaActiva; // sala en la que se encuetra el cliente
	private int id_partidaActiva; // partida en la que se encuetra el cliente
	private HashMap<String, ListenerThread> clientesConectados;
	private DataInputStream entrada;
	private DataOutputStream salida;
	private Sala lobby;
	private TreeMap<Integer, Sala> salas;
	
	private final static Logger LOGGER = Logger.getLogger(ListenerThread.class);
	
	private Gson gson = new Gson();

	public ListenerThread(Socket clienteRead, Socket clienteWrite, HashMap<String, ListenerThread> clientesConectados, Sala lobby, TreeMap<Integer, Sala> salas, TreeMap<Integer, PartidaThread> partidas) {

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
		setId_partidaActiva(-1);
	}
	
	@Override
	public void run() {
		Mensaje msj = null;
		try {
			String cadenaLeida = entrada.readUTF();
			while (true) {//preguntar si es Desconectar
				LOGGER.info("nombre del cliente : " + this.nombreCliente);
				msj = Mensaje.getMensaje(cadenaLeida);
				LOGGER.info("mjs leido!");
				msj.setListener(this);
				msj.ejecutar();
				LOGGER.info("mjs ejecutado!");
				cadenaLeida = entrada.readUTF();
			}
			
		} catch (IOException e ) {
			clientesConectados.remove(nombreCliente);
			
			if (id_salaActiva == 0) {
				lobby.removeCliente(nombreCliente);
			}
			
			if (id_salaActiva > 0) {
				salas.get(id_salaActiva).removeCliente(nombreCliente);
				enviarMensajeBroadcast(new MsjAvisarClienteAbandonoSala(nombreCliente), salas.get(id_salaActiva).getNombreJugadores());
			}
			
			PartidaThread partidaThread = Servidor.partidas.get(id_partidaActiva);
			
			if (partidaThread != null) {
				Partida partida = partidaThread.getPartida();
				ArrayList<String> jugadores = partidaThread.getNombreJugadores();
				jugadores.remove(nombreCliente);
				partidaThread.setNombreJugadores(jugadores);
				ArrayList<Jugador> jugadoresX = partida.getJugadores();
				jugadoresX.remove(new Jugador(nombreCliente));
				partida.setJugadores(jugadoresX);
				if(jugadores.size() == 1) {
					partida.setJugadorGanador(partidaThread.getJugadores().get(0));
					partida.setHayGanador(true);
				}
				
				partidaThread.avisar(new MsjPartidaPuntajesFinales(partida.getJugadorGanador(),partida.getJugadores()));
			}
			
			LOGGER.error("Error de conexion con el cliente " + nombreCliente);
			UtilesLog.loggerStackTrace(e, this.getClass());
		}
	
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

	public HashMap<String, ListenerThread> getClientesConectados() {
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

	public void setClientesConectados(HashMap<String, ListenerThread> clientesConectados) {
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

	public int getId_partidaActiva() {
		return id_partidaActiva;
	}

	public void setId_partidaActiva(int id_partidaActiva) {
		this.id_partidaActiva = id_partidaActiva;
	}

	public synchronized void enviarMensaje(Object mensaje) {
		try {
			salida.writeUTF(gson.toJson(mensaje));
			salida.flush();
		} catch (IOException e) {
			System.err.println("No se pudo enviar el mensaje");
			e.printStackTrace();
		}
	}
	
	public synchronized void enviarMensajeBroadcast(Object mensaje, ArrayList<String> nombres) {
		for (String string : nombres) {
			this.clientesConectados.get(string).enviarMensaje(mensaje);
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

	public PartidaThread crearHiloPartida(Partida game, ArrayList<String> nombresJugadores) {
		PartidaThread hilo = new PartidaThread(game,nombresJugadores, this);
		hilo.start();
		return hilo;
	}

	public void asignarThread(int id, ArrayList<String> nombresJugadores) {
		for (String name : nombresJugadores) {
			this.clientesConectados.get(name).asignarPartida(id);
		}
		
	}
	
	public void asignarPartida(int id) {
		this.setId_partidaActiva(id);
	}
	
	public void notificarPartida() {
		synchronized (Servidor.partidas) {
			PartidaThread thread = Servidor.partidas.get(this.getId_partidaActiva());
			synchronized (thread) {
				thread.notify();
			}
		}
	}
	
	public void notificarCasillaElegida(Casilla casilla) {
		synchronized (Servidor.partidas) {
			PartidaThread thread = Servidor.partidas.get(this.getId_partidaActiva());
			synchronized (thread) {
				thread.setCaminoSeleccionado(casilla);
				thread.notify();
			}
		}
	}
	
	public void notificarObjetoElegido(int indexObjeto, Jugador jugObjetivo) {
		synchronized (Servidor.partidas) {
			PartidaThread thread = Servidor.partidas.get(this.getId_partidaActiva());
			synchronized (thread) {
				thread.setObjetoSelecionado(indexObjeto);
				thread.setJugadorSelecionado(jugObjetivo);
				thread.notify();
			}
		}
	}

	public synchronized void enviarMensaje(Mensaje msjPartida, String nombre) {
		
		try {
			clientesConectados.get(nombre).getSalida().writeUTF(gson.toJson(msjPartida));
			clientesConectados.get(nombre).getSalida().flush();
		} catch (IOException e) {
			System.err.println("No se pudo enviar el mensaje");
			UtilesLog.loggerStackTrace(e, this.getClass());
		}
	}

	public void enviarMensajeBroadcast(Mensaje msj) {
		ArrayList<Jugador> jugadores = Servidor.partidas.get(this.id_partidaActiva).getJugadores();
		ArrayList<String> nombres = new ArrayList<String>();
		for (Jugador jugador : jugadores) {
			nombres.add(jugador.getNombre());
		}
		enviarMensajeBroadcast(msj, nombres);
		
	}
}
