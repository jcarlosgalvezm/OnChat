package addons.bots.trivial.controller;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

import addons.bots.trivial.model.Pregunta;
import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;

/**
 * 
 * Controlador general de la dinàmica del joc
 *
 */

public class Game implements Runnable {

	private ConcurrentLinkedDeque<Request> requestStack;
	private ObjectOutputStream output;
	private ConcurrentHashMap<String, Integer> marcador;
	private FileController fitxers;
	private List<String> listCategories;
	private List<Pregunta> preguntes;
	private final int MAXPREGUNTES = 15;
	private final int TEMPSENTREPREGUNTES = 30;
	private final int TEMPSINSCRIPCIO = 30;
	private Random random;

	public Game(ConcurrentLinkedDeque<Request> requestStack, ObjectOutputStream output) {
		// TODO Auto-generated constructor stub
		this.requestStack = requestStack;
		this.output = output;
		this.marcador = new ConcurrentHashMap<String, Integer>();
		fitxers = new FileController();
		listCategories = new ArrayList<String>();
		preguntes = new ArrayList<Pregunta>();
		random = new Random();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		boolean iniciat = false;

		while (true) {
			if (!requestStack.isEmpty()) {
				Request rx = (Request) requestStack.pollFirst();

				if (rx.getMissatge().equals("iniciar") && !iniciat) {

					rx.setMissatge("!trivial." + rx.getMissatge());
					Request.enviar(Request.convertRequestToAll(rx), output);

					iniciat = true;

					try {
						Thread.currentThread().sleep(300);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					String missatge = "Obertes les inscripcions durant " + TEMPSINSCRIPCIO
							+ " segons. Escriu !trivial.participar per participar";
					Request sortida = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.BOT,
							"@Trivial", missatge);
					Request.enviar(sortida, output);

					Inscriptor ins = new Inscriptor(requestStack, output, marcador);
					Thread th_inscripcions = new Thread(ins);
					th_inscripcions.setName("inscriptor");
					th_inscripcions.start();

					try {
						th_inscripcions.join(TEMPSINSCRIPCIO * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					th_inscripcions.interrupt();

					if (!marcador.isEmpty()) {
						StringBuilder sb = new StringBuilder("Tancades les inscripcions. Inscrits: ");
						marcador.forEach((k, v) -> {
							sb.append(k + ", ");
						});
						Request tancat = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.BOT,
								"@Trivial", sb.toString());
						Request.enviar(tancat, output);
						try {
							Thread.currentThread().sleep(300);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						String inici = "Comença el joc!!.\nContesta les preguntes escrivint !trivial.resposta amb el número associat.\n"
								+ "Exemple: !trivial.resposta 1"
								+ " \n- Resposta encertada et donarà 3 punts. \n- Resposta incorrecta et restarà 1 punt.\n- No contestar ni suma ni resta punts.";
						Request start = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.BOT,
								"@Trivial", inici);
						Request.enviar(start, output);
						try {
							Thread.currentThread().sleep(300);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						int nPreguntes = 0;

						listCategories = fitxers.categoriaToList();

						Set<String> controlPreguntes = new HashSet<String>();

						while (nPreguntes < MAXPREGUNTES) { // triar categoria
															// String

							String categoriaSeleccionada = listCategories
									.get((int) (random.nextDouble() * listCategories.size()));
							StringBuilder sb1 = new StringBuilder("\nCategoria: \n");
							sb1.append(categoriaSeleccionada + "\n");

							// Llençar pregunta de la categoria

							preguntes = fitxers.llegeixCSV(categoriaSeleccionada);

							Pregunta preguntaSeleccionada = preguntes
									.get((int) (random.nextDouble() * preguntes.size()));
							
							// preguntes no repetides
							if (preguntes.size() >= nPreguntes)
								while (controlPreguntes.contains(categoriaSeleccionada + preguntes.indexOf(preguntaSeleccionada)))
									preguntaSeleccionada = preguntes.get((int) (random.nextDouble() * preguntes.size()));
							
							controlPreguntes.add(categoriaSeleccionada + preguntes.indexOf(preguntaSeleccionada));
							
							StringBuilder sb2 = new StringBuilder();
							sb2.append("Pregunta " + (nPreguntes + 1) + "/" + MAXPREGUNTES + ": \n");
							sb2.append(preguntaSeleccionada.toString());

							sb1.append(sb2);
							Request pregunta = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL,
									TipusRemitent.BOT, "@Trivial", sb1.toString());

							Request.enviar(pregunta, output);

							Evaluador eval = new Evaluador(requestStack, output, preguntaSeleccionada, marcador);
							Thread th_evaluacio = new Thread(eval);
							th_evaluacio.setName("evaluador");
							th_evaluacio.start();
							Logger.getLogger("Main").log(Level.INFO, "Evaluant respostes");

							try {
								th_evaluacio.join(TEMPSENTREPREGUNTES * 1000);
								th_evaluacio.interrupt();

							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							nPreguntes++;
						}
						StringBuilder result = new StringBuilder();
						result.append("Fi del joc. Aquests son els resultats:\n");
						marcador.forEach((k, v) -> {
							result.append(k + ": " + v + "\n");
						});
						Request resultats = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL,
								TipusRemitent.BOT, "@Trivial", result.toString());
						Request.enviar(resultats, output);
						marcador.clear();
						iniciat = false;
					} else {
						iniciat = false;
						Request tancat = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.BOT,
								"@Trivial",
								"Tancades les inscripcions. No hi ha participants per tant s'anula la partida");
						Request.enviar(tancat, output);
					}

				} else {
					rx.setMissatge("!trivial." + rx.getMissatge());
					Request.enviar(Request.convertRequestToAll(rx), output);
				}
			}
		}
	}
}
