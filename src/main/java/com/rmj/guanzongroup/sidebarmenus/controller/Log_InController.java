/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.time.Year;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import org.guanzon.appdriver.base.GRider;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Log_InController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Log In";
    private GRider oApp;

    @FXML
    private TextField txtField01;
    @FXML
    private Button btnSignIn;
    @FXML
    Label lblCopyright;
    @FXML
    private TextField txtField03;
    @FXML
    private PasswordField txtField02;
    @FXML
    private Button btnEyeIcon;

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    /**
     * Initializes the controller class.
     */
    private DashboardController dashboardController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DashboardController mainController = LoginControllerHolder.getMainController();
        mainController.triggervbox();

        txtField03.textProperty().bindBidirectional(txtField02.textProperty());
        String year = String.valueOf(Year.now().getValue());
        lblCopyright.setText("© " + year + " Guanzon Group of Companies. All Rights Reserved.");
        showText();
    }

    public void setMainController(DashboardController controller) {
        this.dashboardController = controller;
    }

    private void showText() {
        FontAwesomeIconView eyeIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
        btnEyeIcon.setOnMouseClicked(event -> {
            if (txtField02.isVisible()) {
                txtField03.setText(txtField02.getText());
                txtField02.setVisible(false);
                txtField03.setVisible(true);
                eyeIcon.setIcon(FontAwesomeIcon.EYE_SLASH);
                eyeIcon.setStyle("-fx-fill: gray; -glyph-size: 20; ");
                btnEyeIcon.setGraphic(eyeIcon);
            } else {
                txtField02.setText(txtField03.getText());
                txtField03.setVisible(false);
                txtField02.setVisible(true);
                eyeIcon.setIcon(FontAwesomeIcon.EYE);
                eyeIcon.setStyle("-fx-fill: gray; -glyph-size: 20; ");
                btnEyeIcon.setGraphic(eyeIcon);
            }
        });
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        switch (lsButton) {
            case "btnSignIn":
                DashboardController dashboardController = LoginControllerHolder.getMainController();
                dashboardController.triggervbox2();
                LoginControllerHolder.setLogInStatus(true);
                break;
            default:
                break;
        }

    }
}
