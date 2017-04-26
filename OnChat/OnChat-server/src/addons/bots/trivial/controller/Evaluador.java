package addons.bots.trivial.controller;

import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import addons.bots.trivial.model.Pregunta;
import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;

/**
 * 
 * Controlador usat per evaluar les respostes dels participants a les preguntes del joc
 *
 */

public class Evaluador implements Runnable {
	
	private ObjectOutputStream output;
	private Pregunta pregunta;
	private ConcurrentHashMap<String, Integer> marcador = new ConcurrentHashMap<String, Integer>();
	private String resposta;
	private ConcurrentLinkedDeque<Request> requestStack;

	public Evaluador(ConcurrentLinkedDeque<Request> requestStack, ObjectOutputStream output,
			Pregunta preguntaSeleccionada, ConcurrentHashMap<String, Integer> marcador) {
		this.requestStack = requestStack;
		this.output = output;
		this.pregunta = preguntaSeleccionada;
		this.marcador = marcador;
	}

	@Override
	public void run() {

		try {
			
			Set<String> jugadorsAmbResposta = new HashSet<String>();
			
			while (true) {
				if (Thread.currentThread().interrupted())
					throw new InterruptedException();

				if (!requestStack.isEmpty()) {
					Request rx = (Request) requestStack.pollFirst();
					if (marcador.containsKey(rx.getNomRemitent())) {
						if (rx.getMissatge().startsWith("resposta ") || rx.getMissatge().equals("resposta")) {

							if (!jugadorsAmbResposta.contains(rx.getNomRemitent())) {
								jugadorsAmbResposta.add(rx.getNomRemitent());

								String msg[] = rx.getMissatge().split(" ");
								if (msg.length > 1) {
									switch (msg[1]) {
									case "1":
										resposta = pregunta.getResposta1();
										break;
									case "2":
										resposta = pregunta.getResposta2();
										break;
									case "3":
										resposta = pregunta.getResposta3();
										break;
									case "4":
										resposta = pregunta.getResposta4();
										break;
									default:
										resposta = "";
									}
									if (resposta.equals(pregunta.getRespostaCorrecta())) {

										marcador.put(rx.getNomRemitent(), marcador.get(rx.getNomRemitent()) + 3);
										Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.CLIENT,
												TipusRemitent.BOT, "@Trivial", "Enhorabona has encertat!");
										request.setNomDestinatari(rx.getNomRemitent());
										synchronized (this) {
											Request.enviar(request, output);
										}
									} 
									// resposta incorrecta
									else {
										marcador.put(rx.getNomRemitent(), marcador.get(rx.getNomRemitent()) - 1);
										Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.CLIENT,
												TipusRemitent.BOT, "@Trivial", "Has fallat no es "+resposta+", quina llastima, OOOOOOH!");
										request.setNomDestinatari(rx.getNomRemitent());
										synchronized (this) {
											Request.enviar(request, output);
										}
									}
								} 
								else {

									marcador.put(rx.getNomRemitent(), marcador.get(rx.getNomRemitent()) - 1);

									Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.CLIENT,
											TipusRemitent.BOT, "@Trivial", "Has fallat no es , quina llastima, OOOOOOH!");
									request.setNomDestinatari(rx.getNomRemitent());
									synchronized (this) {
										Request.enviar(request, output);
									}
								}
							} 
							// Si ja ha respos
							else {
								Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.CLIENT,
										TipusRemitent.BOT, "@Trivial", "Ho sento, només pots donar una resposta");
								request.setNomDestinatari(rx.getNomRemitent());
								synchronized (this) {
									Request.enviar(request, output);
								}
							}

						} else if (rx.getMissatge().equals("pista")) {
							Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.CLIENT,
									TipusRemitent.BOT, "@Trivial", pregunta.getPista());
							request.setNomDestinatari(rx.getNomRemitent());
							synchronized (this) {
								Request.enviar(request, output);
							}
						}
						else{
							rx.setMissatge("!trivial." + rx.getMissatge());
							Request.enviar(Request.convertRequestToAll(rx), output);
						}
					}
					else {
						if (rx.getMissatge().startsWith("resposta ") || rx.getMissatge().equals("resposta")
								|| rx.getMissatge().equals("participar")) {
							Request request = new Request(TipusRequest.COMUNICACIO, TipusDestinatari.CLIENT,
									TipusRemitent.BOT, "@Trivial",
									"Ho sento, no pots participar, ja estan tancades les inscripcions");
							request.setNomDestinatari(rx.getNomRemitent());
							synchronized (this) {
								Request.enviar(request, output);
							}
						} else {
							rx.setMissatge("!trivial." + rx.getMissatge());
							Request.enviar(Request.convertRequestToAll(rx), output);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			return;
		}

	}
}
