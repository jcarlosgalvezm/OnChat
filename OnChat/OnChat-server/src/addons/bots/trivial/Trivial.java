package addons.bots.trivial;

import java.io.IOException;
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
	/*	private Socket socket;
	private OutputStream os; 
	private ObjectOutputStream output;
	private InputStream is;
	private ObjectInputStream input;
	*/
	public Trivial(String benvinguda) throws IOException, ClassNotFoundException {
		super(benvinguda);
		requestStack = new ConcurrentLinkedDeque<Request>();
	
	}

	@Override
	public void run() {

		try {
			connect();
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
