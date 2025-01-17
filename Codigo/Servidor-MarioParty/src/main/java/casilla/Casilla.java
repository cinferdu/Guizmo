package casilla;

import java.util.ArrayList;
import java.util.List;

import game.Jugador;

public class Casilla {
	private int posX;
	private int posY;
	private TipoDeCasilla tipo;
	private transient List<Casilla> siguientesCasillas;

	public Casilla(int posX, int posY, TipoDeCasilla tipo) {
		this.posX = posX;
		this.posY = posY;

		// Define el tipo de casillero
		this.tipo = tipo;

		siguientesCasillas = new ArrayList<Casilla>();
	}

	public Casilla caminoUnico() {
		// Retorna la casilla siguiente
		if (siguientesCasillas.size() == 1) {
			return siguientesCasillas.get(0);
		}
		return null;
	}

	public void activarCasilla(Jugador player) {
		this.tipo.activarCasilla(player);
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public TipoDeCasilla getTipo() {
		return tipo;
	}

	public void setTipo(TipoDeCasilla tipo) {
		this.tipo = tipo;
	}

	public List<Casilla> getSiguientesCasillas() {
		return siguientesCasillas;
	}

	public void setSiguientesCasillas(List<Casilla> siguientesCasillas) {
		this.siguientesCasillas = siguientesCasillas;
	}

	public void addSiguiente(Casilla sig) {
		this.siguientesCasillas.add(sig);
	}

	@Override
	public String toString() {
		return "Casilla " + tipo;
	}

}
