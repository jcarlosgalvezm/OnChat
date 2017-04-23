package controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.ChatController;
import controller.LoginController;

/*
 * Classe controlador de la vista de login
 */

public class LoginController implements Initializable {
	/* Definicio dels textfields */
	@FXML
	public TextField hostnameTextfield, portTextfield, usernameTextfield;
	
	@FXML
	public Label nomOcupat;

	public static ChatController con;
	private static LoginController instance;
	private Scene scene;

	public LoginController() {
		instance = this;
	}

	//Retorna una instancia d'aquest controlador
	public static LoginController getInstance() {
		return instance;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {	
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	usernameTextfield.requestFocus();
	        }
	    });

		usernameTextfield.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
			if (ke.getCode().equals(KeyCode.ENTER)) {
				try {
					try {
						this.loginButtonAction();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				ke.consume();
			}
		});
	}

	/* Mètode que s'executa al fer click al botó connectar */
	@FXML
	public void loginButtonAction() throws ClassNotFoundException, IOException {
		/*
		 * S'agafa el valor introduït als textfields per l'usuari i
		 * s'emmagatzema en variables
		 */
		String hostname;
		String username;
		int port;

		try {
			port = Integer.parseInt(portTextfield.getText());
		} catch (NumberFormatException ne) {

			port = 9001;
		}

		if (!hostnameTextfield.getText().isEmpty() && !usernameTextfield.getText().isEmpty()) {

			username = usernameTextfield.getText();
			hostname = hostnameTextfield.getText();

			try {

				System.out.println("Connectant...");
				Socket socket = new Socket(hostname, port);

				OutputStream os = socket.getOutputStream();
				ObjectOutputStream output = new ObjectOutputStream(os);

				InputStream is = socket.getInputStream();
				ObjectInputStream input = new ObjectInputStream(is);

				if (ping(output, input, username)) {
					Logger.getLogger("Main").log(Level.INFO, "pong!");
					showScene(username, socket, output, input);
					//loading(username, socket, output, input);
				}
			} catch (IOException e) {
				Logger.getLogger("Main").log(Level.SEVERE, "Error: " + e.getMessage());
			}
		}
		else {
			nomOcupat.setText("Nom d'usuari buit!");
		}
	}

	public boolean ping(ObjectOutputStream output, ObjectInputStream input, String username)
			throws IOException, ClassNotFoundException {
		Request ping = new Request(TipusRequest.CONNEXIO, TipusDestinatari.SERVER, TipusRemitent.USUARI, username,
				"ping");
		Request.enviar(ping, output);

		Logger.getLogger("Main").log(Level.INFO, "ping!");

		Request rx = (Request) input.readObject();
		if (rx.getMissatge().equals("pong"))
			return true;
		else {
			nomOcupat.setText("Nom d'usuari ocupat");
			Logger.getLogger("Main").log(Level.WARNING, rx.getMissatge());
			return false;
		}
	}

	//Mètode que carrega la fnestra del xat si la connexió ha estat correcte
	public void showScene(String username, Socket socket, ObjectOutputStream output, ObjectInputStream input) throws IOException {

		Configurador cfg = Configurador.getConfigurador();
		cfg.setInput(input);
		cfg.setOutput(output);
		cfg.setNomusuari(username);
		cfg.setSocket(socket);
		
		//Es carrega l'arxiu que conté la vista del xat
		FXMLLoader fmxlLoader = new FXMLLoader(getClass().getResource("/views/ChatView.fxml"));
	    Parent window = (AnchorPane) fmxlLoader.load();
	    con = fmxlLoader.<ChatController>getController();
				
		Platform.runLater(() -> {
            Stage stage = (Stage) hostnameTextfield.getScene().getWindow();
            stage.setTitle("OnChat");
            stage.setOnCloseRequest((WindowEvent e) -> {
                Platform.exit();
                System.exit(0);
            });
            stage.setScene(this.scene);
            stage.centerOnScreen();
        });
		this.scene = new Scene(window);
	}
	
	/*Crear la finestra de carrega*/
	public void loading (String username, Socket socket, ObjectOutputStream output, ObjectInputStream input){
		Stage stage = new Stage();
		stage.setTitle("OnChat -Carregant");
		stage.getIcons().add(new Image(getClass().getClassLoader().getResource("views/chat.png").toString()));
		stage.initStyle(StageStyle.UNDECORATED);
		Task<?> connexioWorker; //controla la barra de progrés
		Task<?> connexioWorker1; //controla la barra de progrés
		Group root = new Group();
	    Scene scene = new Scene(root, 300, 280);
	    root.setStyle("-fx-margin-top: 10px;");
	    root.setStyle("-fx-border-color:#ffffff;");
	    root.setStyle("-fx-border-width: 2px;");
		Label text = new Label();
		// es crea un contenidor vertical per a afegir els altres elements
		VBox vb = new VBox(20); 
	    connexioWorker = crearWorker();       
	    connexioWorker1 = crearWorker2();    

	    //Creació i declaració del text
	    text.setText("Connectant al xat");
        text.setTextFill(Color.BLACK);
        text.setStyle("-fx-font-size: 1.5em;");
	    	    
	    //es crea un nou indicador de progress
	    ProgressIndicator p1 = new ProgressIndicator();
	    p1.setStyle("-fx-color: #000000");
	    p1.setStyle("-fx-progress-color:  #5f9ea0;");
	    p1.setMinWidth(100);
	    p1.setMinHeight(100);
	    p1.progressProperty().unbind();
        p1.progressProperty().bind(connexioWorker.progressProperty());
        
         // es centren el components dins del contenidor
        vb.setAlignment(Pos.CENTER);       
        vb.getChildren().addAll( text, p1);
        vb.setPrefWidth(scene.getWidth());
        vb.setPrefHeight(scene.getHeight());
        vb.setStyle("-fx-background-color: #b4cdcd");
        
        //Agefir els elements a l'scene i mostrar-ho
        root.getChildren().add(vb);
        stage.setScene(scene);
	    stage.show();	
		
        //afegim un escoltador per controlar el temps que dura el progres, quan canvia el missatge
        connexioWorker.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	p1.applyCss();
            	text.setTextFill(Color.GREEN);
            	vb.getChildren().remove( text);
                text.setText("Servidor connectat!");
                try {
					showScene(username, socket, output, input);
				} catch (IOException e) {
					e.printStackTrace();
				}
                new Thread(connexioWorker1).start();
            	connexioWorker.cancel(true);
            	stage.close();
            }
        });
        new Thread(connexioWorker).start();
	}
	
	//Controla la tasca que realitza l'indicador de progres, quan canvia el seu valor
	public Task<Object> crearWorker() {
	    return new Task<Object>() {
	        @Override
	        protected Object call() throws Exception {
	        	 for (int i = 0; i <10; i++) {
		           Thread.sleep(2000);
		           updateMessage("Esperant resposta servidor");	 
		           updateProgress(i+1, 1);
	        	 }
	           return true;
	        }	        
	    };
	}
	
	//Controla una nova tasca per tancar la vista
	public Task<Object> crearWorker2() {
	    return new Task<Object>() {
	        @Override
	        protected Object call() throws Exception {
		           Thread.sleep(5000); 
	           return true;
	        }	        
	    };
	}
}

