package controller.animacio;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/*
 * Classe per personalitzar la finestra del missatge, indicant on volem que es mostri
 */

public class NotificacioStage extends Stage {
    private final Ubicacio bottomRight;

    public NotificacioStage(AnchorPane ap) {
        /*Agafar la mida de la pantalla i ubicar la vista de la notificació*/
        setMida(ap.getPrefWidth(), ap.getPrefHeight());
        Rectangle2D limitsScreen = Screen.getPrimary().getVisualBounds();
        double x = limitsScreen.getMinX() + limitsScreen.getWidth() - ap.getPrefWidth() - 2;
        double y = limitsScreen.getMinY() + limitsScreen.getHeight() - ap.getPrefHeight() - 2;
        bottomRight = new Ubicacio(x,y);
    }

    public void setMida(double width, double height) {
        setWidth(width);
        setHeight(height);
    }

    public void setUbicacio(Ubicacio loc) {
        setX(loc.getX());
        setY(loc.getY());
    }

    private SimpleDoubleProperty yUbicacioProperty = new SimpleDoubleProperty() {
        @Override
        public void set(double newValue) { setY(newValue); }

        @Override
        public double get() { return getY(); }
    };

    public SimpleDoubleProperty yUbicacioProperty() { return yUbicacioProperty; }
    
    public Ubicacio getBottomRight() { return bottomRight; }
}

/* Proporciona les dades sobre la ubicacio del stage*/
class Ubicacio {
    private double x, y;
    public Ubicacio(double xLoc, double yLoc) {
        this.x = xLoc;
        this.y = yLoc;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
}