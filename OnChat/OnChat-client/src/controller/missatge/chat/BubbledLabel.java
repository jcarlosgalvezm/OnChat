package controller.missatge.chat;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/*
 * Classe que controla com es mostren els labels o missatges que es mostren a la sala del xat
 */

public class BubbledLabel extends Label {

	private double pading = 6.0;
	private boolean systemCall = false;

	public BubbledLabel() {
		super();
		init();
	}

	public BubbledLabel(String arg0) {
		super(arg0);
		init();
	}

	//S'indiquen les propietats dels labels, l'aspecte que tindrán
	private void init(){
		DropShadow ds = new DropShadow();
		ds.setOffsetX(1.3);
		ds.setOffsetY(1.3);
		ds.setColor(Color.DARKGRAY);
		setPrefSize(Label.USE_COMPUTED_SIZE, Label.USE_COMPUTED_SIZE);
		shapeProperty().addListener(new ChangeListener<Shape>() {
			@Override
			public void changed(ObservableValue<? extends Shape> arg0, Shape arg1, Shape arg2) {
				if(systemCall){					
					systemCall = false;
				}else{
					shapeIt();
				}
			}
		});

		heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable arg0) {
				if(!systemCall)
					setPrefHeight(Label.USE_COMPUTED_SIZE);
			}
		});
		
		widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if(!systemCall)
					setPrefHeight(Label.USE_COMPUTED_SIZE);
			}
		});
		shapeIt();		
	}


	public final double getPading() {
		return pading;
	}

	public void setPading(double pading) {
		if(pading > 25.0)
			return;
		this.pading = pading;
	}

	private final void shapeIt(){
		systemCall = true;
		System.gc();
	}
}
