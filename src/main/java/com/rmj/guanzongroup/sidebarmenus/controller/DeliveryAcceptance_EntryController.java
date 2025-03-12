/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_EntryController implements Initializable, ScreenInterface{
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
    private Button btnUpdate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnHistory;
    @FXML
    private Button btnRetrieve;
    @FXML
    private Button btnClose;
    @FXML
    private HBox hboxid;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtTransactionNo;
    @FXML
    private DatePicker txtTransactionDate;
    @FXML
    private TextField txtIndustry;
    @FXML
    private TextField txtCompany;
    @FXML
    private TextField txtSupplier;
    @FXML
    private TextField txtTrucking;
    @FXML
    private TextArea txtAreaRemarks;
    @FXML
    private DatePicker dtReferenceDate;
    @FXML
    private TextField txtReferenceNo;
    @FXML
    private TextField txtTerm;
    @FXML
    private TextField txtDiscountRate;
    @FXML
    private TextField txtDiscountAmount;
    @FXML
    private TextField txtTotal;
    @FXML
    private TextField txtOrderNo;
    @FXML
    private TextField txtBarcode;
    @FXML
    private TextField txtSupersede;
    @FXML
    private TextField txtDescription;
    @FXML
    private TextField txtBrand;
    @FXML
    private TextField txtModel;
    @FXML
    private TextField txtColor;
    @FXML
    private TextField txtInventoryType;
    @FXML
    private TextField txtMeasure;
    @FXML
    private DatePicker dtExpiryDate;
    @FXML
    private TextField txtCost;
    @FXML
    private TextField txtOrderQuantity;
    @FXML
    private TextField txtReceiveQuantity;
    @FXML
    private TableView tblViewOrderDetails;
    @FXML
    private TableColumn tblindexRowNoOrderDetails;
    @FXML
    private TableColumn tblindexOrderNoOrderDetails;
    @FXML
    private TableColumn tblindexBarcodeOrderDetails;
    @FXML
    private TableColumn tblindexDescriptionOrderDetails;
    @FXML
    private TableColumn tblindexCostOrderDetails;
    @FXML
    private TableColumn tblindexOrderQuantityOrderDetails;
    @FXML
    private TableColumn tblindexReceiveQuantityOrderDetails;
    @FXML
    private TableColumn tblindexTotalOrderDetails;
    @FXML
    private TableView tblViewStock_Request;
    @FXML
    private TableColumn tblindexRowNoPurchaseOrderList;
    @FXML
    private TableColumn tblindexSupplierPurchaseOrderList;
    @FXML
    private TableColumn tblindexDatePurchaseOrderList;
    @FXML
    private TableColumn tblIndexReferenceNoPurchaseOrderList;

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
