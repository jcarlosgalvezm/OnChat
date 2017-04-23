package addons.bots;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;


/**
 * 
 * Classe genèrica per generar bots
 *
 */
public class Bot implements Runnable{
	
	
	@Override
	public void run() {

	}
	
	/*Mètode per solicitar una connexió al server*/
	public boolean ping(ObjectOutputStream output, ObjectInputStream input, String username)
			throws IOException, ClassNotFoundException {
		Request ping = new Request(TipusRequest.CONNEXIO, TipusDestinatari.SERVER, TipusRemitent.BOT, username,
				"ping");
		output.writeObject(ping);
		output.reset();
		Logger.getLogger("BOT").log(Level.INFO, "ping!");

		Request rx = (Request) input.readObject();

		if (rx.getMissatge().equals("pong"))
			return true;
		else {
			System.out.println(rx.getMissatge());
			return false;
		}
	}
}
