import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {      
	
	/**
	 * Constructor principal del m�dul client
	 *
	 * @param args els arguments
	 * @throws Exception llen�a una exception
	 */
	
	public static void main(String[] args) throws Exception {
		launch(args);     
	}     
	
	/* M�tode que inicia la interficie gr�fica
	 * animaci� progr�s carrega login 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/views/LoginView.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("OnChat");
			primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("views/chat.png").toString()));
			primaryStage.initStyle(StageStyle.UNIFIED);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	} 
} 