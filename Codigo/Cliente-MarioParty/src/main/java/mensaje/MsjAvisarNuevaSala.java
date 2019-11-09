package mensaje;

import java.util.TreeMap;

import javax.swing.JFrame;

import cliente.Sala;
import entornoGrafico.LobbyVentana;

public class MsjAvisarNuevaSala extends Mensaje {

	private static final long serialVersionUID = 1L;
	private TreeMap<Integer, Sala> salas;
	
	
	public MsjAvisarNuevaSala(TreeMap<Integer, Sala> salas) {
		this.setSalas(salas);
		this.clase = this.getClass().getSimpleName();
	}

	@Override
	public void ejecutar() {
		JFrame ventanaActual = listenerClient.getCliente().getVentanaActual();
		((LobbyVentana) ventanaActual).mostrarSala(salas);
	}

	public TreeMap<Integer, Sala> getSalas() {
		return salas;
	}

	public void setSalas(TreeMap<Integer, Sala> salas) {
		this.salas = salas;
	}

}