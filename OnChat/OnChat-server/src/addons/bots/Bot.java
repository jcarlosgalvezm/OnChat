package addons.bots;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
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
public abstract class Bot implements Runnable{
	
	protected Socket socket;
	private OutputStream os; 
	protected ObjectOutputStream output;
	private InputStream is;
	protected ObjectInputStream input;
	protected String benvinguda;
	
	public Bot(String benvinguda) throws IOException, ClassNotFoundException{
		socket = new Socket("localhost", 9001);
		os = socket.getOutputStream();
		output = new ObjectOutputStream(os);
		is = socket.getInputStream();
		input = new ObjectInputStream(is);
		this.benvinguda = benvinguda;
		
	}
	
	
	/*Mètode per solicitar una connexió al server*/
	protected boolean ping(ObjectOutputStream output, ObjectInputStream input, String username)
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
	
	protected void connect() throws ClassNotFoundException, IOException{
		if (ping(output, input, "@"+this.getClass().getSimpleName())) {
			Logger.getLogger("BOT").log(Level.INFO, "pong!");
			Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.USUARI, this.getClass().getSimpleName(), benvinguda);
			Request.enviar(request, output);
		}
	}
}
