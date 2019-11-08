package mensaje;

import java.util.ArrayList;
import java.util.TreeMap;

import game.Jugador;
import game.Partida;
import game.Personaje;

public class MsjIniciarPartida extends Mensaje {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> nombresJugadores;
	private Partida game;
	
	public MsjIniciarPartida(ArrayList<String> nombresJugadores) {
		this.clase = this.getClass().getSimpleName();
	}
	
	@Override
	public void ejecutar() {
		ArrayList<Jugador> participantes = new ArrayList<Jugador>();
		for (String name : nombresJugadores) {
			Jugador jug = new Jugador(name);
			jug.setPersonaje(new Personaje("peach")); //Cambiar nombre
			participantes.add(jug);
		}
		this.game = new Partida(participantes, 50); // EN LA VENTANA DE CREAR SALA AGREGAR "OBJETIVO" o "LIMITE DE MONEDAS"
		TreeMap<Integer, PartidaThread> listaPartida = listenerServer.getPartidas();
		PartidaThread hiloPartida = listenerServer.crearHiloPartida(game, nombresJugadores);
		
		synchronized (listaPartida) {
			listaPartida.put(game.getIdpartida(), hiloPartida);
		}
		listenerServer.enviarMensajeBroadcast(this, nombresJugadores);
		listenerServer.asignarThread(game.getIdpartida(), nombresJugadores);
	}

	public ArrayList<String> getNombresJugadores() {
		return nombresJugadores;
	}

	public Partida getGame() {
		return game;
	}

	public void setNombresJugadores(ArrayList<String> nombresJugadores) {
		this.nombresJugadores = nombresJugadores;
	}

	public void setGame(Partida game) {
		this.game = game;
	}

}
