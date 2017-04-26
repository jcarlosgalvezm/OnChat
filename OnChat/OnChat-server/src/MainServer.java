import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.Connexio;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Classe principal del mòdul servidor
 *
 */

public class MainServer extends Application {
	
	protected static final int PORT = 9001;
	
	public static void main(String[] args) throws IOException, InterruptedException {
		launch(args);
		ServerSocket listener = new ServerSocket(PORT);
		Logger.getLogger("Main").log(Level.INFO, "Servidor connectat");
		Logger.getLogger("Main").log(Level.INFO, "Escoltant per el port " +PORT);
		ConcurrentHashMap<String, ObjectOutputStream> connexions = new ConcurrentHashMap<String, ObjectOutputStream>();
		try {
			while (true) {
				// Per cada petició de connexio que rep genera un nou thread
				new Thread(new Connexio(listener.accept(), connexions)).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			listener.close();
		}
	}
	
	/*ANIMACIO D'INICI*/
	//afegir una vista per confirmar la connexió amb el servidor
	@Override
	public void start(Stage stage) throws Exception {
		Task<?> connexioWorker; //controla la barra de progrés
		Task<?> connexioWorker1; //controla la barra de progrés
		Group root = new Group();
	    Scene scene = new Scene(root, 250, 200);
		stage.setTitle("OnChat - SERVIDOR");
		stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/chat.png").toString()));
		stage.initStyle(StageStyle.UNDECORATED);
		Label text = new Label();
		// es crea un contenidor vertical per a afegir els altres elements
		VBox vb = new VBox(20); 
	    connexioWorker = crearWorker();    
	    connexioWorker1 = crearWorker();    
	    
	    //Creació i declaració del text
	    text.setText("Connectant al servidor");
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
        
	    //afegim un escoltador per controlar quan finalitza el progres i es modifiqui l'interficie
        connexioWorker.progressProperty().addListener(new ChangeListener<Number>() {  
            @Override  
            public void changed(ObservableValue<? extends Number> ov, Number t, Number newValue) {    
                if (newValue.doubleValue() >= 1.0) {  
                    Text text = (Text) p1.lookup(".percentage"); 
                    text.setText("CORRECTE");  
                    text.setStyle("-fx-font-size: 1.5em;");              
               }  
          }});  

        //afegim un escoltador per controlar el temps que dura el progres, quan canvia el missatge
        connexioWorker.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	p1.applyCss();
            	text.setTextFill(Color.GREEN);
            	vb.getChildren().remove( text);
                text.setText("Servidor connectat!");
                new Thread(connexioWorker1).start();
            	connexioWorker.cancel(true);
            }
        });
        new Thread(connexioWorker).start();
        
        connexioWorker1.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	connexioWorker1.cancel(true);
            	stage.close();
            }
        });
        
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
		           Thread.sleep(2000); 
	           return true;
	        }	        
	    };
	}
}

