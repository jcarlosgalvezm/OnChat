package addons.bots.trivial.controller;

import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;


/**
 * 
 * Controlador usat per inscriure participants al joc
 *
 */
public class Inscriptor implements Runnable {

	private ObjectOutputStream output;
	private ConcurrentHashMap<String, Integer> marcador;
	private ConcurrentLinkedDeque<Request> requestStack;

	public Inscriptor(ConcurrentLinkedDeque<Request> requestStack, ObjectOutputStream output, ConcurrentHashMap<String, Integer> marcador) {
		this.output = output;
		this.requestStack = requestStack;
		this.marcador = marcador;
	}

	@Override
	public void run() {

		try {
			while (true) {
				if (Thread.currentThread().interrupted())
					throw new InterruptedException();

				if (!requestStack.isEmpty()) {
					Request rx = (Request) requestStack.pollFirst();
					
					
					if (rx.getMissatge().equals("participar")) {
						
						rx.setMissatge("!trivial." + rx.getMissatge());
						Request.enviar(Request.convertRequestToAll(rx), output);
						
						Thread.currentThread().sleep(300);
						
						Request sortida;
						if (!marcador.containsKey(rx.getNomRemitent())) {
							marcador.put(rx.getNomRemitent(), 0);
							sortida = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.BOT,
									"@Trivial", rx.getNomRemitent() + " inscrit");
						} else
							sortida = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.BOT,
									"@Trivial", "Tranquil " + rx.getNomRemitent() + ", ja estas inscrit");

						Request.enviar(sortida, output);
					}
					else{
						rx.setMissatge("!trivial." + rx.getMissatge());
						Request.enviar(Request.convertRequestToAll(rx), output);
					}
				}
			}
		} catch (InterruptedException e) {
			return;
		}
	}
}
