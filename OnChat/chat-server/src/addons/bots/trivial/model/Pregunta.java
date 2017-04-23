package addons.bots.trivial.model;

/*
 * Classe que recull el model de pregunta utilitzat al trivial i els seus atibuts a partir del model creat als arxius csv
 */

public class Pregunta {
	private String pregunta;
	private String resposta1;
	private String resposta2;
	private String resposta3;
	private String resposta4;
	private String respostaCorrecta;
	private String pista;
	
	public Pregunta(String pregunta, String resposta1, String resposta2, String resposta3, String resposta4,String respostaCorrecta, String pista){
		this.pregunta = pregunta;
		this.resposta1 = resposta1;
		this.resposta2 = resposta2;
		this.resposta3 = resposta3;
		this.resposta4 = resposta4;
		this.respostaCorrecta = respostaCorrecta;
		this.pista = pista;
	}
	
	
	public String getPregunta() {
		return pregunta;
	}


	public void setPregunta(String pregunta) {
		this.pregunta = pregunta;
	}


	public String getResposta1() {
		return resposta1;
	}


	public void setRespota1(String resposta1) {
		this.resposta1 = resposta1;
	}


	public String getResposta2() {
		return resposta2;
	}


	public void setResposta2(String resposta2) {
		this.resposta2 = resposta2;
	}


	public String getResposta3() {
		return resposta3;
	}


	public void setResposta3(String resposta3) {
		this.resposta3 = resposta3;
	}


	public String getResposta4() {
		return resposta4;
	}


	public void setResposta4(String resposta4) {
		this.resposta4 = resposta4;
	}


	public String getRespostaCorrecta() {
		return respostaCorrecta;
	}


	public void setRespostaCorrecta(String respostaCorrecta) {
		this.respostaCorrecta = respostaCorrecta;
	}


	public String getPista() {
		return pista;
	}


	public void setPista(String pista) {
		this.pista = pista;
	}

	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		char[] nom = getPregunta().toLowerCase().toCharArray();
		nom[0] = Character.toUpperCase(nom[0]);
		
		b.append(nom);
		b.append("\n1: ").append(getResposta1());
		b.append("\n2: ").append(getResposta2());
		b.append("\n3: ").append(getResposta3());
		b.append("\n4: ").append(getResposta4());
	
		return b.toString();
	}	
}
