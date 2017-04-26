package controller.animacio;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/*
 * Classe controlador de la vista de la notificació
 */

public final class NotificacioController {

    @FXML
    private Label lblMessage;
    
    @FXML
    private AnchorPane rootNode;

    private NotificacioStage stage;
    private TipusAnimacio animationType;
    private Notificacio animator;
    private AnimacioManager animationProvider;

    public NotificacioController() {
        initNotificacio("");
    }

    private void initNotificacio(String message) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/NotificacioView.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.load();
            
            stage = new NotificacioStage(rootNode);
            stage.getIcons().add(new Image(getClass().getClassLoader().getResource("views/chat.png").toString()));
            stage.setTitle("OnChat - AVÍS");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(rootNode));
            stage.setAlwaysOnTop(true);
            stage.setUbicacio(stage.getBottomRight());
            animationProvider = new AnimacioManager(new Notificacio(stage));
            setNot(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setNot(String message) {
        setMissatge(message);
    }

    public boolean esNotVisible() {
        return animator.esMostra();
    }

    public void mostra_amaga(Duration dismissDelay) {
        if (esNotVisible()) {
            animator.playAmagaAnim();
        } else {
            stage.show();
            animator.AnimSequencia(dismissDelay);
        }
    }

    public void setMissatge(String txt) {
        lblMessage.setText(txt);
    }

    public void setTipus() {
        animator = animationProvider.selecciona(a -> a.getAnimationType() == TipusAnimacio.POPUP);
        animationType = TipusAnimacio.POPUP;
    }

    public TipusAnimacio getTipus() {
        return animationType;
    }
}

/*Utilitat per gestionar les animacions*/
class AnimacioManager {
    private List<Notificacio> llistaAnim;
    
    public AnimacioManager(Notificacio... animations) {
        llistaAnim = new ArrayList<>();
        Collections.addAll(llistaAnim, animations);
    }
    
    public Notificacio selecciona(Predicate<? super Notificacio> predicate) {
        return llistaAnim.stream().filter(predicate).findFirst().orElse(null);
    }
}
