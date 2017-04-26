package controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Configurador {

	private static ObjectOutputStream output;
	private static ObjectInputStream input;
	private static String nomusuari;
	private static Socket socket;
	private static Configurador myConf;

	private Configurador(){}
	
	public static Configurador getConfigurador() {

		if (myConf == null) {
			myConf = new Configurador();
		}
		return myConf;
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	public void setOutput(ObjectOutputStream output) {
		Configurador.output = output;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public void setInput(ObjectInputStream input) {
		Configurador.input = input;
	}

	public String getNomusuari() {
		return nomusuari;
	}

	public void setNomusuari(String nomusuari) {
		Configurador.nomusuari = nomusuari;
	}
	
	public String getMissatge() {
		return nomusuari;
	}

	public void setMissatge(String msg) {
		Configurador.nomusuari = msg;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		Configurador.socket = socket;
	}
	
}
