package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrder;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
import org.json.simple.JSONObject;

public class DeliveryAcceptance_EntryController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Purchasing Receiving";
    private GRiderCAS oApp;
    private JSONObject poJSON;

    private ObservableList<ModelPurchaseOrder> data = FXCollections.observableArrayList();

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
    private TextField txtField03;
    @FXML
    private TextField txtField04;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField08;
    @FXML
    private CheckBox cbAdv;
    @FXML
    private TextField txtField09;
    @FXML
    private TextField txtField11;
    @FXML
    private TextField txtField12;
    @FXML
    private TextField txtField14;
    @FXML
    private TextField txtField06;
    @FXML
    private TextField txtField18;
    @FXML
    private TextField txtField19;
    @FXML
    private TextField txtField25;
    @FXML
    private TextField txtField26;
    @FXML
    private TextField txtField27;
    @FXML
    private TextField txtField17;
    @FXML
    private TextField txtField16;
    @FXML
    private TextField txtField15;
    @FXML
    private TextField txtField20;
    @FXML
    private TextField txtField21;
    @FXML
    private TextField txtField22;
    @FXML
    private TextField txtField23;
    @FXML
    private TextField txtField24;
    @FXML
    private TableView tblViewStock_Request;
    @FXML
    private TableColumn tblindex01;
    @FXML
    private TableColumn tblindex02;
    @FXML
    private TableColumn tblindex03;
    @FXML
    private TableColumn tblindex04;
    @FXML
    private TableColumn tblindex05;
    @FXML
    private TableView<?> tblViewOrderDetails;
    @FXML
    private TableColumn tblindex01_order_details;
    @FXML
    private TableColumn tblindex02_order_details;
    @FXML
    private TableColumn tblindex04_order_details;
    @FXML
    private TableColumn tblindex05_order_details;
    @FXML
    private TableColumn tblindex06_order_details;
    @FXML
    private TableColumn tblindex07_order_details;
    @FXML
    private TableColumn tblindex08_order_details;
    @FXML
    private TableColumn tblindex12_order_details;
    @FXML
    private TableColumn tblindex09_order_details;
    @FXML
    private TableColumn tblindex09_order_details1;
    @FXML
    private TableColumn tblindex10_order_details;
    @FXML
    private TableColumn tblindex11_order_details;
    @FXML
    private TableColumn tblindex13_order_details;

    @FXML
    void cmdButton_Click(ActionEvent event) {

    }

    public void inittable() {
        tblindex01.setStyle("-fx-alignment: CENTER;");
        tblindex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblindex03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblindex04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblindex05.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 0 0 5;");

        tblindex01.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index01"));
        tblindex02.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index02"));
        tblindex03.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index03"));
        tblindex04.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index04"));
        tblindex05.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index05"));

        tblViewStock_Request.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewStock_Request.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        tblViewStock_Request.setItems(data);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("working");
        //        data.add(new ModelPurchaseOrder("1", "LP - General Warehouse", "2025-02-11", "M00125000000", "10"
//        ));
        inittable();
    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setCompanyID(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
