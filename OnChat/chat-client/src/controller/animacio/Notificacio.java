package controller.animacio;

import controller.animacio.NotificacioStage;
import javafx.animation.*;
import javafx.util.Duration;
/*
 * Classe utilitzada per crear el tipus d'animació que es mostra quan un usuari entra al xat
 */
public class Notificacio{
	private final NotificacioStage stage;
    private final Timeline mostraAnim, amagaAnim;
    private final SequentialTransition sq;
    private boolean esMostra;

    public Notificacio(NotificacioStage s) {
        this.stage = s;
        mostraAnim = mostraAnimConf();
        amagaAnim = setupDismissAnimation();
        sq = new SequentialTransition(mostraAnimConf(), setupDismissAnimation());
    }

    /*Es creen els marcs necessaris per mostrar l'animació de la notificació al amagar-se*/
    private Timeline setupDismissAnimation() {
        Timeline tl = new Timeline();

        KeyValue kv1 = new KeyValue(stage.yUbicacioProperty(), stage.getY() + stage.getWidth());
        KeyFrame kf1 = new KeyFrame(Duration.millis(1500), kv1);
        KeyValue kv2 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame kf2 = new KeyFrame(Duration.millis(1500), kv2);

        tl.getKeyFrames().addAll(kf1, kf2);
        tl.setOnFinished(e -> {
            esMostra = false;
            stage.close();
            stage.setUbicacio(stage.getBottomRight());
        });

        return tl;
    }

    /*Es creen els marcs necessaris per mostrar l'animació de la notificació al mostrar-se*/
    private Timeline mostraAnimConf() {
        Timeline tl = new Timeline();

        KeyValue kv1 = new KeyValue(stage.yUbicacioProperty(), stage.getBottomRight().getY() + stage.getWidth());
        KeyFrame kf1 = new KeyFrame(Duration.ZERO, kv1);
        KeyValue kv2 = new KeyValue(stage.yUbicacioProperty(), stage.getBottomRight().getY());
        KeyFrame kf2 = new KeyFrame(Duration.millis(1000), kv2);
        KeyValue kv3 = new KeyValue(stage.opacityProperty(), 0.0);
        KeyFrame kf3 = new KeyFrame(Duration.ZERO, kv3);
        KeyValue kv4 = new KeyValue(stage.opacityProperty(), 1.0);
        KeyFrame kf4 = new KeyFrame(Duration.millis(2000), kv4);

        tl.getKeyFrames().addAll(kf1, kf2, kf3, kf4);
        tl.setOnFinished(e -> esMostra = true);
        return tl;
    }
       
    public TipusAnimacio getAnimationType() { return TipusAnimacio.POPUP; }

    public void playMostraAnim() { mostraAnim.play(); }

    public void playAmagaAnim() { amagaAnim.play(); }

    public boolean esMostra() { return esMostra;  }
    
    public void AnimSequencia(Duration dismissDelay) {
        sq.getChildren().get(1).setDelay(dismissDelay);
        sq.play();
    }
}

/*Tipus d'animació*/
enum TipusAnimacio { POPUP }