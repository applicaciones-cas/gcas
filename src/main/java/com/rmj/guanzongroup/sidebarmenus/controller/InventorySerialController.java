/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvSerial;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.LogWrapper;
//import org.guanzon.cas.inv.Inv;
import org.guanzon.cas.inv.ObservableListUtil;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class InventorySerialController implements Initializable {

    private final String pxeModuleName = "Inventory Serial";
    private GRider oApp;
    private int pnEditMode;

    private int pnIndex = -1;
    private int pnRow = 0;

    private boolean pbLoaded = false;
    private boolean state = false;

    private String psCode;
    private String lsStockID, lsBrand;
//    private Inv oTrans;
//    private Inv oTrans;
    private ParamControllers oParameters;
    private InventoryMaintenanceController parentController;

    public int tbl_row = 0;
    private ObservableList<ModelInvSerial> data = FXCollections.observableArrayList();
    @FXML
    private AnchorPane AnchorMain;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnOkay;
    @FXML
    private Button btnClose;
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
    private TextField txtField06;
    @FXML
    private ComboBox cmbField01;
    @FXML
    private Button btnLoadSerial;
    @FXML
    private TableView tblSerialLedger;

    @FXML
    private TableColumn index01;

    @FXML
    private TableColumn index02;

    @FXML
    private TableColumn index03;

    @FXML
    private TableColumn index04;

    @FXML
    private TableColumn index05;

    @FXML
    private TableColumn index06;

    @FXML
    private TableColumn index07;

    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    public void setStockID(String foValue) {
        lsStockID = foValue;
    }
//
//    private String fsCode;
//
//    private Inv poTrans;
//
//    public void setFsCode(Inv fsCode) {
//        this.poTrans = fsCode;
//    }
//
//    public void setBranchNme(String foValue) {
//        lsBrand = foValue;
//    }

    public void setParentController(InventoryMaintenanceController cVal) {
        parentController = cVal;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

//        initializeObject();
//        initBrand();
//        initDetails();
//        pbLoaded = true;
//        ClickButton();
//        initTable();
    }

//    private void initializeObject() {
//        String category = System.getProperty("store.inventory.industry");
//        System.out.println("category == " + category);
//        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
//        oParameters = new ParamControllers(oApp, logwrapr);
//        oTrans = new Inv(oApp, "", logwrapr);  // Ensure this isn't overwriting necessary data
//    }
//
//    private void initBrand() {
//        JSONObject poJson;
//        poJson = new JSONObject();
//        poJson = oParameters.Brand().searchRecord(lsBrand, true);
//        if ("success".equals((String) poJson.get("result"))) {
//            txtField03.setText(oParameters.Brand().getModel().getDescription());
//        }
//    }
//
//    private void initDetails() {
//        lsStockID = poTrans.InvMaster().getModel().Inventory().getStockId();
//        txtField01.setText(poTrans.InvMaster().getModel().Inventory().getBarCode());
//        txtField02.setText(poTrans.InvMaster().getModel().Inventory().getDescription());
//
//        txtField04.setText(poTrans.InvMaster().getModel().Inventory().Model().getDescription());
//        txtField05.setText(poTrans.InvMaster().getModel().Inventory().Color().getDescription());
//        txtField06.setText(poTrans.InvMaster().getModel().Inventory().Measure().getMeasureName());
//
//        ObservableList<String> unitTypes = ObservableListUtil.UNIT_TYPES;
//        cmbField01.setItems(unitTypes);
//        cmbField01.getSelectionModel().select(7);
//    }
//
//    /*Handle button click*/
//    private void ClickButton() {
//        btnOkay.setOnAction(this::handleButtonAction);
//        btnLoadSerial.setOnAction(this::handleButtonAction);
//        btnClose.setOnAction(this::handleButtonAction);
//    }
//
//    private void handleButtonAction(ActionEvent event) {
//        Object source = event.getSource();
//        JSONObject poJSON;
//        if (source instanceof Button) {
//            Button clickedButton = (Button) source;
//            unloadForm appUnload = new unloadForm();
//            switch (clickedButton.getId()) {
//                case "btnClose":  //Close
//                    appUnload.useParentController("");
//                    initializeObject();
//                    CommonUtils.closeStage(btnClose);
//                    break;
//
//                case "btnOkay":  //Close
//                    appUnload.useParentController("");
//                    initializeObject();
//                    CommonUtils.closeStage(btnOkay);
//                    break;
//
//                case "btnLoadSerial":
////                    System.out.println("LOAD SERIAL == " + poTrans.InvMaster().s);
//                    String UnitType = String.valueOf(cmbField01.getSelectionModel().getSelectedIndex());
//                    poJSON = new JSONObject();
//
//                    poTrans.OpenInvSerialLedger(lsStockID, "7".equals(UnitType) ? "%" : UnitType);
//                    loadSerialLedger();
//                    break;
//            }
//        }
//    }
//
//    private void loadSerialLedger() {
//        System.out.println("nagload and ledger");
//        data.clear();
//        System.out.println("Count == " + poTrans.getInvSerialCount());
//        if (poTrans.getInvSerialCount() > 0) {
//            for (int lnCtr = 0; lnCtr < poTrans.getInvSerialCount(); lnCtr++) {
//                System.out.println("Processing Serial Ledger at Index: " + lnCtr);
//
//                // Debugging individual components
//                System.out.println("Serial 02: " + String.valueOf(lnCtr + 1));
//                System.out.println("Serial ID: " + poTrans.InvSerial(lnCtr).getSerialId());
//                System.out.println("Serial 01: " + poTrans.InvSerial(lnCtr).getSerialOne());
//                System.out.println("Serial 02: " + poTrans.InvSerial(lnCtr).getSerialTwo());
//                System.out.println("Serial 01: " + poTrans.InvSerial(lnCtr).getLocationId());
//                System.out.println("Serial 02: " + poTrans.InvSerial(lnCtr).getSoldStatus());
//                System.out.println("Serial 02: " + poTrans.InvSerial(lnCtr).getUnitType());
//                System.out.println("-------------------------------------------------------------");
//
//                data.add(new ModelInvSerial(
//                        String.valueOf(lnCtr + 1),
//                        poTrans.InvSerial(lnCtr).getSerialId(),
//                        poTrans.InvSerial(lnCtr).getSerialOne(),
//                        poTrans.InvSerial(lnCtr).getSerialTwo(),
//                        poTrans.InvSerial(lnCtr).getLocationId(),
//                        poTrans.InvSerial(lnCtr).getSoldStatus(),
//                        poTrans.InvSerial(lnCtr).getUnitType(),
//                        ""
//                ));
//            }
//        } else {
//            ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
//        }
//    }
//
////    private void loadSerialLedger() {
////        data.clear();
////
////        if (oTrans.getInvSerialCount() >= 0) {
////            for (int lnCtr = 0; lnCtr < oTrans.getInvSerialCount(); lnCtr++) {
////                System.out.println("Processing Serial Ledger at Index: " + lnCtr);
////
////                // Debugging individual components
////                System.out.println("Transaction Date: " + oTrans.InvSerial(lnCtr).getSerialId());
////                System.out.println("Branch Name: " + oTrans.InvSerial(lnCtr).getSerialOne());
////                System.out.println("Source Code: " + oTrans.InvSerial(lnCtr).getSerialTwo());
//////                System.out.println("Source No: " + oTrans.InvSerial(lnCtr).Inventory().);
////
////                data.add(new ModelInvSerial(
////                        String.valueOf(lnCtr + 1),
////                        oTrans.InvSerial(lnCtr).getSerialId(),
////                        oTrans.InvSerial(lnCtr).getSerialOne(),
////                        oTrans.InvSerial(lnCtr).getSerialTwo(),
////                        oTrans.InvSerial(lnCtr).getLocationId(),
////                        oTrans.InvSerial(lnCtr).getSoldStatus(),
////                        oTrans.InvSerial(lnCtr).getUnitType()
////                             ,""
////                ));
//////            lnCtr += 1;
////            }
////        }
////    }
//    private void initTable() {
//        index01.setStyle("-fx-alignment: CENTER;");
//        index02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index06.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index07.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//
//        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
//        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
//        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));
//        index04.setCellValueFactory(new PropertyValueFactory<>("index04"));
//        index05.setCellValueFactory(new PropertyValueFactory<>("index05"));
//        index06.setCellValueFactory(new PropertyValueFactory<>("index06"));
//        index07.setCellValueFactory(new PropertyValueFactory<>("index07"));
//        tblSerialLedger.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
//            TableHeaderRow header = (TableHeaderRow) tblSerialLedger.lookup("TableHeaderRow");
//            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                header.setReordering(false);
//            });
//        });
//        tblSerialLedger.setItems(data);
//        tblSerialLedger.autosize();
//    }
}
