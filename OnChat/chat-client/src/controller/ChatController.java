package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.Request;
import model.TipusDestinatari;
import model.TipusRemitent;
import model.TipusRequest;

import controller.animacio.NotificacioController;
import controller.missatge.chat.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/*
 * Classe controlador de la vista del xat
 */

public class ChatController implements Initializable {

	/*Definició dels elements de la vista*/
	@FXML
	private TextArea messageBox;
	@FXML
	private Label usernameLabel, onlineCountLabel;
	@SuppressWarnings("rawtypes")
	@FXML
	private ListView userList, chatPane;
	@FXML
	BorderPane borderPane;
	private Configurador cfg;
	private Listener listener;

	
	/*Constructor principal de la classe*/
	public ChatController() {
		cfg = Configurador.getConfigurador();
		listener = new Listener(cfg, this);
		new Thread(listener).start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		messageBox.setWrapText(true);
		setUsernameLabel(cfg.getNomusuari());
		userList.getStylesheets().add(getClass().getResource("../views/application.css").toExternalForm());
		chatPane.getStylesheets().add(getClass().getResource("../views/chat.css").toExternalForm());

		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	messageBox.requestFocus();
	        }
	    });
		/*
		 * Afegir filtre per afegir l'entrada amb el botó enter
		 */
		messageBox.addEventFilter(KeyEvent.KEY_PRESSED, ke -> {
			if (ke.getCode().equals(KeyCode.ENTER)) {
				try {
					sendButtonAction();
				} catch (IOException e) {
					e.printStackTrace();
				}
				ke.consume();
			}
		});
		
		chatPane.getItems().addListener(new ListChangeListener<ListView>(){
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ListView> c) {
				 chatPane.scrollTo(c.getList().size()-1);
			}
		});
	}

	//Mètode que controla l'acció a realitzar quan es clica el botó enviar del xat
	public void sendButtonAction() throws IOException {
		String msg = messageBox.getText();
		if (!messageBox.getText().isEmpty()) {
			Request request = requestMaker(msg);
			Request.enviar(request, cfg.getOutput());
			messageBox.clear();
		}
	}

	private Request requestMaker(String msg) {
		if (msg.startsWith("!")) {
			if (msg.split("\\.").length > 1) {
				return new Request(TipusRequest.COMUNICACIO, TipusDestinatari.BOT, TipusRemitent.USUARI,
						cfg.getNomusuari(), msg);
			} else {
				return new Request(TipusRequest.JOINBOT, TipusDestinatari.BOT, TipusRemitent.USUARI, cfg.getNomusuari(), msg);
			}
		} else {
			return new Request(TipusRequest.COMUNICACIO, TipusDestinatari.ALL, TipusRemitent.USUARI, cfg.getNomusuari(), msg);
		}
	}

	public void setUsernameLabel(String username) {
		this.usernameLabel.setText(username);
	}

	public void setOnlineLabel(String usercount) {
		Platform.runLater(() -> onlineCountLabel.setText(usercount));
	}

	public void sendMethod(KeyEvent event) throws IOException {
		if (event.getCode() == KeyCode.ENTER) {
			sendButtonAction();
		}
	}

	@FXML
	public void closeApplication() {
		Platform.exit();
		System.exit(0);
	}

	/*Controla els missatges que es mostren a la sala, segons el destinatari*/
	@SuppressWarnings("unchecked")
	public synchronized void addToChat(Request msg) {

		Task<HBox> othersMessages = new Task<HBox>() {
			HBox x = new HBox();
			@Override
			public HBox call() throws Exception {
				if(msg.getTipusRemitent() == TipusRemitent.SERVER){
					BubbledLabel bl6 = new BubbledLabel();
					bl6.setText("Server" + ": " + msg.getMissatge());
					bl6.setBackground(new Background(new BackgroundFill(Color.web("#e0eeee"), null, null)));
					bl6.setStyle("-fx-padding:3px");
					x.getChildren().addAll(bl6);
					bl6.requestFocus();
					return x;
				}
				else if(msg.getTipusRemitent() == TipusRemitent.BOT){
					BubbledLabel bl6 = new BubbledLabel();
					bl6.setText(msg.getNomRemitent() + ": " + msg.getMissatge());
					bl6.setWrapText(true);
					bl6.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, null, null)));
					bl6.setStyle("-fx-padding:3px");
					x.setMaxWidth(chatPane.getWidth() - 20);
					x.getChildren().addAll(bl6);
					bl6.requestFocus();
					return x;
				}
				else {
					BubbledLabel bl6 = new BubbledLabel();
					bl6.setText(msg.getNomRemitent() + ": " + msg.getMissatge());
					bl6.setWrapText(true);
					bl6.setBackground(new Background(new BackgroundFill(Color.LIGHTGOLDENRODYELLOW, null, null)));
					bl6.setStyle("-fx-padding:3px");
					x.setMaxWidth(chatPane.getWidth() - 20);
					x.getChildren().addAll(bl6);
					bl6.requestFocus();
					return x;
				}
			}
		};

		othersMessages.setOnSucceeded(event -> {
			chatPane.getItems().add(othersMessages.getValue());
		});

		Task<HBox> yourMessages = new Task<HBox>() {
			@Override
			public HBox call() throws Exception {
				BubbledLabel bl6 = new BubbledLabel();
				bl6.setText(msg.getNomRemitent() + ": " + msg.getMissatge());
				bl6.setBackground(new Background(new BackgroundFill(Color.web("#c1ffc1"), null, null)));
				HBox x = new HBox();
				bl6.setWrapText(true);
				x.setMaxWidth(chatPane.getWidth() - 20);
				bl6.setStyle("-fx-padding:3px");
				x.setAlignment(Pos.TOP_RIGHT);
				x.getChildren().addAll(bl6);
				bl6.requestFocus();
				return x;
			}
		};
		yourMessages.setOnSucceeded(event -> chatPane.getItems().add(yourMessages.getValue()));

		if (msg.getNomRemitent().equals(usernameLabel.getText())) {
			Thread t2 = new Thread(yourMessages);
			t2.setDaemon(true);
			t2.start();
		} else {
			Thread t = new Thread(othersMessages);
			t.setDaemon(true);
			t.start();
		}
	}

	/*Llista dels usuaris connectats a la sala*/
	@SuppressWarnings("unchecked")
	public void setUserList(Request msg) {
		Platform.runLater(() -> {
			ObservableList<String> users = FXCollections.observableList(msg.getUsuaris());
			userList.setItems(users);
			userList.setCellFactory(new UserList());
			setOnlineLabel(String.valueOf(msg.getUsuaris().size()));
		});
	}
	
	/*Notificació que es mostra quan entra a la sala un nou usuari*/
    public void notificacioUsuari(Request msg) {
        Platform.runLater(() -> {
        	if(msg.getMissatge().contains("Entra")){
        		NotificacioController tray = new NotificacioController();
	            tray.setMissatge("Ha entrat " + msg.getNomRemitent()+ "!");
	            tray.setTipus();
	            tray.mostra_amaga(Duration.seconds(5));
	            try {
	                Media hit = new Media(getClass().getResource("./animacio/sons/notification.mp3").toString());
	                MediaPlayer mediaPlayer = new MediaPlayer(hit);
	                mediaPlayer.play();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
        	}
        });
    }
}