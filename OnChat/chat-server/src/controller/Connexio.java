package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import addons.bots.BotFactory;
import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;

/**
 * 
 * Controllador del thread que contindrà cada connexió
 *
 */

public class Connexio implements Runnable {

	private final static ThreadLocal<Socket> socket = new ThreadLocal<Socket>();
	private Socket sharedSocket;
	private ConcurrentHashMap<String, ObjectOutputStream> connexions;

	// Nom del usuari que realitza la request
	private String nickname;

	public Connexio(Socket socket, ConcurrentHashMap<String, ObjectOutputStream> connexions) throws IOException {
		sharedSocket = socket;
		this.connexions = connexions;
		nickname = "";
	}

	@Override
	public void run() {

		socket.set(sharedSocket);

		Logger.getLogger("Main").log(Level.INFO, "Connexió rebuda de " + this.socket.get().getInetAddress());

		try (InputStream is = socket.get().getInputStream();
				ObjectInputStream input = new ObjectInputStream(is);
				OutputStream os = socket.get().getOutputStream();
				ObjectOutputStream output = new ObjectOutputStream(os)) {

			Request peticio = (Request) input.readObject();

			if (!checkRequest(peticio, output))
				return;

			sendListUpdate();

			Request info = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.SERVER, nickname,
					"Entra " + nickname + " al xat.");
			difondreAtots(info);

			while (socket.get().isConnected()) {

				Request rx = (Request) input.readObject();

				Logger.getLogger("SERVER").log(Level.INFO, "Missatge rebut de " + rx.getNomRemitent());

				if (rx.getTipusDestinatari() == TipusDestinatari.ALL) {
					difondreAtots(rx);
				} else if (rx.getTipusDestinatari() == TipusDestinatari.SERVER) {
					if (rx.getTipusRequest() == TipusRequest.DESCONNEXIO) {
						synchronized (connexions) {
							connexions.remove(rx.getNomRemitent());
						}
					}
				} else if (rx.getTipusDestinatari() == TipusDestinatari.BOT) {
					parseMessageFromBot(rx, output);
				} else if (rx.getTipusDestinatari() == TipusDestinatari.CLIENT) {
					Request.enviar(rx, getOutputOf(rx.getNomDestinatari()));
				}
			}

		} catch (IOException | ClassNotFoundException e) {
			Logger.getLogger("SERVER").log(Level.WARNING, e.getMessage() + " " + nickname);
			connexions.remove(nickname);	
			Request info = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.SERVER, nickname,
					"Surt del xat " + nickname +".");
			
			try {
				difondreAtots(info);
				sendListUpdate();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
	}

	private void difondreAtots(Request rx) throws IOException {
		connexions.forEach((k, v) -> {
			if (!k.startsWith("@"))
				Request.enviar(rx, v);
		});
	}

	private ObjectOutputStream getOutputOf(String name) {
		if (connexions.containsKey(name))
			return connexions.get(name);
		else
			return null;
	}

	private boolean checkRequest(Request peticio, ObjectOutputStream output) {

		synchronized (connexions) {
			if (connexions.containsKey(peticio.getNomRemitent())) {
				Request resposta = new Request(TipusRequest.DESCONNEXIO, TipusDestinatari.CLIENT, TipusRemitent.SERVER,
						"server", "Nom d'usuari ocupat");
				Request.enviar(resposta, output);
				return false;
			}
			nickname = peticio.getNomRemitent();
			connexions.put(nickname, output);
		}

		Request resposta = new Request(TipusRequest.CONNEXIO, TipusDestinatari.CLIENT, TipusRemitent.SERVER, "server",
				"pong");
		Request.enviar(resposta, output);

		return true;
	}

	private void sendListUpdate() throws IOException {
		ArrayList<String> usuaris = new ArrayList<String>(connexions.keySet());
		Collections.sort(usuaris);
		Request update = new Request(TipusRequest.UPDATE, TipusDestinatari.ALL, TipusRemitent.SERVER, nickname,
				"update");
		update.setUsuaris(usuaris);
		difondreAtots(update);
	}

	private void parseMessageFromBot(Request rx, ObjectOutputStream output) throws IOException{
		if (rx.getTipusRequest() == TipusRequest.JOINBOT) {

			String botName = "@" + Character.toUpperCase(rx.getMissatge().substring(1).charAt(0))
					+ rx.getMissatge().substring(2);

			if (!connexions.containsKey(botName)) {
				BotFactory.launch(rx.getMissatge().substring(1));
				difondreAtots(Request.convertRequestToAll(rx));
			} else {

				difondreAtots(Request.convertRequestToAll(rx));
				Request error = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.CLIENT,
						TipusRemitent.SERVER, "Server", "El bot ja esta dintre de la sala.");
				Request.enviar(error, output);
			}
		} else if (rx.getTipusRequest() == TipusRequest.COMUNICACIO) {

			String[] msg = rx.getMissatge().substring(1).split("\\.");
			String botName = "@" + Character.toUpperCase(msg[0].charAt(0)) + msg[0].substring(1);
			if (connexions.containsKey(botName)) {
				if (rx.getTipusRemitent() == TipusRemitent.USUARI) {
					if (rx.getTipusRequest() == TipusRequest.COMUNICACIO) {
						rx.setMissatge(msg[1]);
						Request.enviar(rx, connexions.get(botName));
					}
				}
			} else {
				difondreAtots(Request.convertRequestToAll(rx));
			}
		}
	}

}