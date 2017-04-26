package controller;

import model.Request;
import model.TipusRequest;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Listener implements Runnable{
	
	private Configurador cfg;
	private ChatController controller;
	private Request rx;
	
	public Listener(Configurador cfg, ChatController controller){
		this.cfg = cfg;
		this.controller = controller;
	}
	
	public void run() {
		
		while(cfg.getSocket().isConnected()){
			try {
				rx = (Request) cfg.getInput().readObject();
				Logger.getLogger("Main").log(Level.INFO, "Missatge rebut de: " + rx.getNomRemitent());
				
				//Aqui es rep el missatge que s'ha de publicar a la finestra del xat o actualitzar la llista
				if(rx.getTipusRequest() == TipusRequest.COMUNICACIO){
					//publicar al xat
					controller.addToChat(rx);
					controller.notificacioUsuari(rx);
				}
				else if(rx.getTipusRequest() == TipusRequest.UPDATE){
					
					controller.setUserList(rx);
				}
			} catch (ClassNotFoundException | IOException e) {
				Logger.getLogger("CLIENT").log(Level.WARNING, e.getMessage());
				System.exit(1);
			}
		}
	}   
}
