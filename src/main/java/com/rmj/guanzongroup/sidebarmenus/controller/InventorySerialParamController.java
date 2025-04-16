/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvSerialLedger;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
//import org.guanzon.cas.inv.InvSerial;
//import org.guanzon.cas.inv.Serials;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class InventorySerialParamController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Inventory Serial Parameter";
    private GRiderCAS oApp;
    private String oTransnox = "";
    private int pnEditMode;

//    private Serials oTrans;
    private ParamControllers oParameters;
//    private InvSerial oTrans;
//    private InvSerialLedger poTrans;

    private boolean state = false;
    private boolean pbLoaded = false;
    private int pnInventory = 0;
    private int pnRow = 0;

    private ObservableList<ModelInvSerialLedger> data = FXCollections.observableArrayList();
    // Anchor Panes
    @FXML
    private AnchorPane AnchorMain, AnchorInput, AnchorInput1;

// Text Labels
    @FXML
    private Text lblSearch01, lblSearch02, lblSearch011, lblSerial01, lblSerial02;

// Text Fields
    @FXML
    private TextField txtSeeks01, txtSeeks02, txtSeeks03,
            txtField01, txtField02, txtField03, txtField04, txtField05,
            txtField08, txtField09, txtField10, txtField11, txtField12,
            txtField13, txtField14, txtField15;

// ComboBox and CheckBox
    @FXML
    private ComboBox cmbField01;
    @FXML
    private CheckBox chkField01;

// TableView and TableColumns
    @FXML
    private TableView tblInventorySerialLedger;
    @FXML
    private TableColumn index01, index02, index03, index04, index05;

// Buttons and HBox
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnClose;

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }
    ObservableList<String> unitType = FXCollections.observableArrayList(
            "LDU",
            "Regular",
            "Free",
            "Live",
            "Service",
            "RDU",
            "Others"
    );

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

//        initializeObject();
//        clearAllFields();
//        ClickButton();
//        initTable();
//        pnEditMode = EditMode.UNKNOWN;
//        ClickButton();
        pbLoaded = true;

    }

//    private void initializeObject() {
//        String category = System.getProperty("store.inventory.industry");
//        System.out.println("category == " + category);
//        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
//
//        oTrans = new Serials(oApp, "", logwrapr);
//        oParameters = new ParamControllers(oApp, logwrapr);
//
//    }
//
//    private void ClickButton() {
//        btnClose.setOnAction(this::handleButtonAction);
//        btnBrowse.setOnAction(this::handleButtonAction);
//    }
//
//    private void handleButtonAction(ActionEvent event) {
//        Object source = event.getSource();
//        JSONObject poJSON;
//        if (source instanceof Button) {
//            Button clickedButton = (Button) source;
//            unloadForm appUnload = new unloadForm();
//            switch (clickedButton.getId()) {
//                case "btnClose":
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
//                        initializeObject();
//                        appUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
//
//                    }
//                    break;
//
//                case "btnBrowse":
//                    initializeObject();
//
//                    String lsValue = (txtSeeks01.getText().toString().isEmpty() ? "" : txtSeeks01.getText().toString());
//                    poJSON = oTrans.Serial().searchRecord(lsValue, true);
//
//                    if ("success".equals(poJSON.get("result"))) {
//                        pnEditMode = oTrans.Serial().getEditMode();
//                        loadSerial();
//                        oTrans.OpenSerialLedger(oTrans.Serial().getModel().getSerialId());
//                        loadSerialLedger();
//                    } else {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//
//                    }
//                    break;
////
//            }
//        }
//
//    }
//
//    private void loadSerial() {
//        if (pnEditMode == EditMode.READY
//                || pnEditMode == EditMode.ADDNEW
//                || pnEditMode == EditMode.UPDATE) {
//
//            // Set text fields from oTrans
//            txtField01.setText(oTrans.Serial().getModel().getSerialId());
//            txtField02.setText(oTrans.Serial().getModel().getSerialOne());
//            txtField03.setText(oTrans.Serial().getModel().getSerialTwo());
//            txtField04.setText(oTrans.Serial().getModel().Inventory().getBarCode());
//            txtField05.setText(oTrans.Serial().getModel().Inventory().getDescription());
//
////            System.out.println("categ == " + oTrans.getModel().Inventory().Brand().getDescription());
//            txtField08.setText(oTrans.Serial().getModel().Inventory().Category().getDescription());
//            txtField09.setText(oTrans.Serial().getModel().Inventory().Brand().getDescription());
//            txtField10.setText(oTrans.Serial().getModel().Inventory().Model().getDescription());
//            txtField11.setText(oTrans.Serial().getModel().Inventory().Color().getDescription());
//
////
//            // Define an array of location descriptions
//            String[] locationDescriptions = {
//                "Warehouse", // Index 0
//                "Branch", // Index 1
//                "Supplier", // Index 2
//                "Customer", // Index 3
//                "On Transit", // Index 4
//                "Service Center", // Index 5
//                "Service Unit" // Index 6
//            };
//
//            // Retrieve the location code and handle it
//            String locationCode = (String) oTrans.Serial().getModel().getLocationId();
//            int index;
//            try {
//                index = Integer.parseInt(locationCode);
//            } catch (NumberFormatException e) {
//                throw new AssertionError("Invalid location code format: " + locationCode);
//            }
//
//            // Set the description or handle an invalid code
//            if (index >= 0 && index < locationDescriptions.length) {
//                txtField15.setText(locationDescriptions[index]);
//            } else {
//                throw new AssertionError("Unexpected location code: " + locationCode);
//            }
//
//            int SoldStat = Integer.parseInt(oTrans.Serial().getModel().getSoldStatus().toString());
//            chkField01.setSelected(SoldStat == 1);
//
////            cmbField01.getSelectionModel().select(Integer.parseInt(oTrans.getModel(pnRow).getUnitType().toString()));
//        }
//    }
//
//    private void clearAllFields() {
//        // Arrays of TextFields grouped by sections
//        TextField[][] allFields = {
//            // Text fields related to specific sections
//            {txtSeeks01, txtSeeks02, txtField01, txtField02, txtField03, txtField04,
//                txtField05, txtField08, txtField09, txtField10, txtField11, txtField12,
//                txtField13, txtField14, txtField15},};
//
//        cmbField01.setValue(null);
//        chkField01.setSelected(false);
//
//        // Loop through each array of TextFields and clear them
//        for (TextField[] fields : allFields) {
//            for (TextField field : fields) {
//                field.clear();
//            }
//        }
//        data.clear();
//    }
//
//    private void txtSeeks_KeyPressed(KeyEvent event) {
//        TextField txtSeeks = (TextField) event.getSource();
//        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
//        String lsValue = (txtSeeks.getText() == null ? "" : txtSeeks.getText());
//        JSONObject poJSON;
//        switch (event.getCode()) {
//            case F3:
//            case ENTER:
//
//                break;
//        }
//        switch (event.getCode()) {
//            case ENTER:
//                CommonUtils.SetNextFocus(txtSeeks);
//            case DOWN:
//                CommonUtils.SetNextFocus(txtSeeks);
//                break;
//            case UP:
//                CommonUtils.SetPreviousFocus(txtSeeks);
//        }
//    }
//
//    private void loadSerialLedger() {
//        int lnCtr2 = 0;
//        data.clear();
//
//        if (oTrans.getSerialLedgerCount() >= 0) {
//            for (int lnCtr = 0; lnCtr < oTrans.getSerialLedgerCount(); lnCtr++) {
//                System.out.println("Processing Serial Ledger at Index: " + lnCtr);
//
//                // Debugging individual components
//                System.out.println("Transaction Date: " + oTrans.SerialLedger(lnCtr).getTransactionDate());
//                System.out.println("Branch Name: " + oTrans.SerialLedger(lnCtr).Branch().getBranchName());
//                System.out.println("Source Code: " + oTrans.SerialLedger(lnCtr).getSourceCode());
//                System.out.println("Source No: " + oTrans.SerialLedger(lnCtr).getSourceNo());
//
//                data.add(new ModelInvSerialLedger(
//                        String.valueOf(lnCtr + 1),
//                        oTrans.SerialLedger(lnCtr).getTransactionDate().toString(),
//                        oTrans.SerialLedger(lnCtr).Branch().getBranchName(),
//                        oTrans.SerialLedger(lnCtr).getSourceCode(),
//                        oTrans.SerialLedger(lnCtr).getSourceNo()
//                ));
////            lnCtr += 1;
//            }
//        }
//    }
//
//    private void initTable() {
//        index01.setStyle("-fx-alignment: CENTER;");
//        index02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//
//        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
//        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
//        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));
//        index04.setCellValueFactory(new PropertyValueFactory<>("index04"));
//        index05.setCellValueFactory(new PropertyValueFactory<>("index05"));
//        tblInventorySerialLedger.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
//            TableHeaderRow header = (TableHeaderRow) tblInventorySerialLedger.lookup("TableHeaderRow");
//            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                header.setReordering(false);
//            });
//        });
//        tblInventorySerialLedger.setItems(data);
//        tblInventorySerialLedger.autosize();
//    }
    @Override
    public void setIndustryID(String fsValue) {
    }

    @Override
    public void setCompanyID(String fsValue) {
    }

    @Override
    public void setCategoryID(String fsValue) {
    }

}
