/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.base.GRider;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Log_In_2Controller implements Initializable, ScreenInterface {

    @FXML
    private StackPane stackPane;

    @FXML
    private ImageView imageView;

    /**
     * Initializes the controller class.
     */
    private final String pxeModuleName = "Log In";
    private GRider oApp;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("Samp");
        
 
        Platform.runLater(() -> {
            imageView.setImage(new Image(getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/images/logo_Vertical.png").toExternalForm()));

            imageView.setPreserveRatio(true); // Keep the aspect ratio
            imageView.setSmooth(true);        // Smooth scaling for better quality
            imageView.fitHeightProperty().bind(stackPane.heightProperty());
        });

    }

    //@Override
    public void setGRider(GRider foValue) {
  oApp = foValue;
    }
}
