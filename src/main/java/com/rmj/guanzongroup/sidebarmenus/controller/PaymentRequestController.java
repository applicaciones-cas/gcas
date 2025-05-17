package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONObject;

public class PaymentRequestController implements Initializable,ScreenInterface{
    private final String pxeModuleName = "Payment Request";
    private GRider oApp;
    private JSONObject poJSON;
    
    @FXML
    private AnchorPane apBrowse;
    @FXML
    private AnchorPane apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnClose;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnCancel;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField14;
    @FXML
    private TextField txtField011;
    @FXML
    private TextField txtField021;
    @FXML
    private TextField txtField141;
    @FXML
    private TextField txtField0111;
    @FXML
    private TableView<?> tblViewOrderDetails;
    @FXML
    private TableColumn<?, ?> tblindex01_order_details;
    @FXML
    private TableColumn<?, ?> tblindex02_order_details;
    @FXML
    private TableColumn<?, ?> tblindex04_order_details;
    @FXML
    private TableColumn<?, ?> tblindex05_order_details;
    @FXML
    private TableColumn<?, ?> tblindex06_order_details;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void cmdButton_Click(ActionEvent event) {
    }

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
}
