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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Log_InController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Log In";
    private GRider oApp;

    @FXML
    private HBox hboxUserName, hboxPassword;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField txtField01, txtField03;
    @FXML
    private Button btnSignIn, btnEyeIcon;
    @FXML
    Label lblCopyright;

    @FXML
    private PasswordField txtField02;

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    /**
     * Initializes the controller class.
     */
    private DashboardController dashboardController;

    public void setMainController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DashboardController mainController = LoginControllerHolder.getMainController();
        mainController.triggervbox();

        txtField03.textProperty().bindBidirectional(txtField02.textProperty());

        showYearCopyRights();
        setupPasswordToggle();
        setupBorderHandling();

        borderPane.setOnMouseClicked(event -> {
            if (!(event.getTarget() instanceof TextField) && !(event.getTarget() instanceof FontAwesomeIconView)) {
                removeAllBorders();
                borderPane.requestFocus();
            }
        });
    }

    private void showYearCopyRights() {
        lblCopyright.setText("© " + Year.now().getValue() + " Guanzon Group of Companies. All Rights Reserved.");
    }

    private void setupPasswordToggle() {
        btnEyeIcon.setOnMouseClicked(event -> togglePasswordVisibility());

        txtField03.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                hidePlainText();
            }
        });

        txtField02.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !txtField03.isFocused()) {
                removeBorder(hboxPassword);
            }
        });
    }

    private void togglePasswordVisibility() {
        boolean isPasswordVisible = txtField02.isVisible();
        txtField03.setText(txtField02.getText());

        txtField02.setVisible(!isPasswordVisible);
        txtField03.setVisible(isPasswordVisible);

        (isPasswordVisible ? txtField03 : txtField02).requestFocus();

        updateEyeIcon(isPasswordVisible ? FontAwesomeIcon.EYE_SLASH : FontAwesomeIcon.EYE);
        setBorder(hboxPassword, true);
    }

    private void hidePlainText() {
        if (txtField03.isVisible()) {
            txtField02.setText(txtField03.getText());
            txtField03.setVisible(false);
            txtField02.setVisible(true);
            updateEyeIcon(FontAwesomeIcon.EYE);
        }
    }

    private void updateEyeIcon(FontAwesomeIcon icon) {
        FontAwesomeIconView eyeIcon = new FontAwesomeIconView(icon);
        eyeIcon.setStyle("-fx-fill: gray; -glyph-size: 20;");
        btnEyeIcon.setGraphic(eyeIcon);
    }

    private void setupBorderHandling() {
        setBorderWeightTextField(txtField01, hboxUserName);
        setBorderWeightTextField(txtField03, hboxPassword);
        setBorderWeightTextField(txtField02, hboxPassword);
    }

    private void setBorderWeightTextField(TextField textField, HBox parentBox) {
        textField.setOnMouseClicked(event -> setBorder(parentBox, true));

        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                removeBorder(parentBox);
            }
        });
    }

    private void setBorder(HBox parentBox, boolean isActive) {
        parentBox.getStyleClass().removeAll("hbox-default", "hbox-border-width");
        parentBox.getStyleClass().add(isActive ? "hbox-border-width" : "hbox-default");
    }

    private void removeBorder(HBox parentBox) {
        setBorder(parentBox, false);
    }

    private void removeAllBorders() {
        removeBorder(hboxUserName);
        removeBorder(hboxPassword);
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
