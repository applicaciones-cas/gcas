/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import org.guanzon.appdriver.base.GRider;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

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

    @FXML
    private TextField txtField01;

    @FXML
    private CheckBox cbShowPassword;

    @FXML
    private PasswordField txtField02;

    @FXML
    private TextField txtField03;

    @FXML
    private Button eyeButton;

    @FXML
    private Button BtnLogIn;

    /**
     * Initializes the controller class.
     */
    private final String pxeModuleName = "Log In";
    private GRider oApp;

    private DashboardController mainController; // Reference to MainController
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DashboardController mainController = ControllerHolder.getMainController();
        mainController.triggervbox();
        // TODO
        Platform.runLater(() -> {
            imageView.setImage(new Image(getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/images/logo_Vertical.png").toExternalForm()));

            imageView.setPreserveRatio(true); // Keep the aspect ratio
            imageView.setSmooth(true);        // Smooth scaling for better quality
            imageView.fitHeightProperty().bind(stackPane.heightProperty());
        });

        txtField03.textProperty().bindBidirectional(txtField02.textProperty());
        txtField02.textProperty().bindBidirectional(txtField03.textProperty());

        initEyeButton();
    }

    private void initEyeButton() {
        PauseTransition delay = new PauseTransition(Duration.seconds(0.08));
        FontAwesomeIconView eyeIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);

        updateVisibility();
        eyeButton.setOnAction(e -> {
            if (!cbShowPassword.isSelected()) {
                txtField03.setVisible(!txtField03.isVisible());
                txtField02.setVisible(!txtField02.isVisible());
            }
            updateVisibility();
            delay.stop();
        });

        eyeButton.setOnMouseExited(e -> {
            if (!cbShowPassword.isSelected()) {
                delay.setOnFinished(event -> {
                    txtField03.setVisible(false);
                    txtField02.setVisible(true);
                    eyeIcon.setIcon(FontAwesomeIcon.EYE);
                    updateVisibility();
                });
                delay.playFromStart();
            }
        });

    }

    @FXML
    void cbShowPassword_Click(ActionEvent event) {
        boolean isChecked = cbShowPassword.isSelected();
        txtField02.setVisible(!isChecked);
        txtField03.setVisible(isChecked);
        updateVisibility();
    }

    public void setMainController(DashboardController controller) {
        this.mainController = controller;
    }

    private void updateVisibility() {
        FontAwesomeIconView eyeIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
        boolean isPermanentUnmask = cbShowPassword.isSelected();
        boolean isEyeVisible = txtField03.isVisible();

        boolean showPlainText = isPermanentUnmask || isEyeVisible;
        txtField03.setVisible(showPlainText);
        txtField02.setVisible(!showPlainText);
        eyeIcon.setIcon(showPlainText ? FontAwesomeIcon.EYE_SLASH : FontAwesomeIcon.EYE);
        eyeIcon.setStyle("-fx-fill: gray; ");
        eyeButton.setGraphic(eyeIcon);

    }

    //@Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        switch (lsButton) {
            case "BtnLogIn":
                // must trigger the vbox controller to enable
                DashboardController mainController = ControllerHolder.getMainController();
                mainController.triggervbox2();

                break;
            default:
                break;
        }

    }
}
