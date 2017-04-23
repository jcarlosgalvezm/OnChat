package model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * Model usat per transmetre la informació entre el servidor i el client
 *
 */

public class Request implements Serializable {
	private TipusRequest tipusRequest;
	private TipusDestinatari tipusDestinatari;
	private TipusRemitent tipusRemitent;
	private String nomRemitent;
	private String nomDestinatari;
	private String missatge;
	private ArrayList<String> users = new ArrayList<>();
	
	public Request(TipusRequest tipusRequest, TipusDestinatari tipusDestinatari, 
			TipusRemitent tipusRemitent, String nomRemitent, String missatge){
		this.tipusDestinatari = tipusDestinatari;
		this.tipusRequest = tipusRequest;
		this.tipusRemitent = tipusRemitent;
		this.nomRemitent = nomRemitent;
		this.missatge = missatge;
	}

	public TipusRequest getTipusRequest() {
		return tipusRequest;
	}

	public void setTipusRequest(TipusRequest tipusRequest) {
		this.tipusRequest = tipusRequest;
	}

	public TipusDestinatari getTipusDestinatari() {
		return tipusDestinatari;
	}

	public void setTipusDestinatari(TipusDestinatari tipusDestinatari) {
		this.tipusDestinatari = tipusDestinatari;
	}

	public String getMissatge() {
		return missatge;
	}

	public void setMissatge(String missatge) {
		this.missatge = missatge;
	}

	public TipusRemitent getTipusRemitent() {
		return tipusRemitent;
	}

	public void setTipusRemitent(TipusRemitent tipusRemitent) {
		this.tipusRemitent = tipusRemitent;
	}

	public String getNomRemitent() {
		return nomRemitent;
	}

	public void setNomRemitent(String nomUsuari) {
		this.nomRemitent = nomUsuari;
	}
	
    public ArrayList<String> getUsuaris() {
        return users;
    }

    public void setUsuaris(ArrayList<String> usuaris) {
        this.users = usuaris;
    } 
    
	public String getNomDestinatari() {
		return nomDestinatari;
	}

	public void setNomDestinatari(String nomDestinatari) {
		this.nomDestinatari = nomDestinatari;
	}
    
    public static void enviar(Request request, ObjectOutputStream output){
    	try {
			output.writeObject(request);
			output.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static Request convertRequestToAll(Request rx){
		rx.setTipusDestinatari(TipusDestinatari.ALL);
		rx.setTipusRequest(TipusRequest.COMUNICACIO);		
		return rx;
	}
}