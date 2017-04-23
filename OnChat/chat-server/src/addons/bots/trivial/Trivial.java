package addons.bots.trivial;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;
import addons.bots.Bot;
import addons.bots.trivial.controller.Game;
import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;

/**
 * 
 * Controlador general del bot trivial
 *
 */

public class Trivial extends Bot {

	private ConcurrentLinkedDeque<Request> requestStack;
	private Socket socket;
	
	public Trivial() {
		
		requestStack = new ConcurrentLinkedDeque<Request>();
	
	}

	@Override
	public void run() {
		super.run();

		try {
			socket = new Socket("localhost", 9001);
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream output = new ObjectOutputStream(os);
			InputStream is = socket.getInputStream();
			ObjectInputStream input = new ObjectInputStream(is);

			if (ping(output, input, "@Trivial")) {
				Logger.getLogger("BOT").log(Level.INFO, "pong!");
			}
			
			Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.USUARI, "@Trivial", "Hola @Channel, escriviu !trivial.iniciar per començar el joc");
			Request.enviar(request, output);
			Game game = new Game(requestStack,output);
			Thread th_game = new Thread(game);
			th_game.start();
			
			while (socket.isConnected()) {
				requestStack.add((Request) input.readObject());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}
