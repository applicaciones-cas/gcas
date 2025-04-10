/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelAPClientLedger;
import com.rmj.guanzongroup.sidebarmenus.table.model.SharedModel;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
//import org.guanzon.cas.client.account.APClients;
import org.guanzon.cas.client.account.GlobalVariables;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class AccountsPayableController implements Initializable, ScreenInterface {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
//    private final String pxeModuleName = "Accounts Payable";
//    private GRider oApp;
//    private int pnEditMode;
//    private APClients oTrans;
//    private ParamControllers oParameters;
//    private boolean state = false;
//    private boolean pbLoaded = false;
//    private String oTransnox = "";
//    private DashboardController fdController = new DashboardController();
//    private String lsSearchRes = "";
//    private SharedModel sharedModel;
//    String category = System.getProperty("store.inventory.industry", "").trim().replace(";", "");
//
//
//
//    public void initModel(SharedModel sharedModel) {
//        this.sharedModel = sharedModel;
//    }
//
//    @FXML
//    private AnchorPane AnchorMain,AnchorCompany;
//
//    @FXML
//    private HBox hbButtons;
//
//    @FXML
//    private Button btnBrowse;
//
//    @FXML
//    private Button btnSave;
//
//    @FXML
//    private Label lblStat;
//
//    @FXML
//    private Button btnUpdate;
//
//    @FXML
//    private Button btnSearch;
//
//    @FXML
//    private Button btnCancel;
//
//    @FXML
//    private Button btnClose;
//
//    @FXML
//    private TextField txtSearch02;
//
//    @FXML
//    private TextField txtSearch01;
//
//    @FXML
//    private TextField txtField01;
//
//    @FXML
//    private TextField txtField04;
//
//    @FXML
//    private TextField txtField09;
//
//    @FXML
//    private TextField txtField02;
//
//    @FXML
//    private TextField txtField08;
//
//    @FXML
//    private TextField txtField10;
//
//    @FXML
//    private TextField txtField07;
//
//    @FXML
//    private TextField txtField11;
//
//    @FXML
//    private TextField txtField12;
//
//    @FXML
//    private TextField txtField13;
//
//    @FXML
//    private CheckBox chkfield01;
//
//    @FXML
//    private TextField txtField03;
//
//    @FXML
//    private TextField txtField05;
//
//    @FXML
//    private TextField txtField06;
//
//    @FXML
//    private DatePicker cpField02;
//
//    @FXML
//    private DatePicker cpField01;
//
//    @FXML
//    private TableView tblLedger;
//
//    @FXML
//    private TableColumn index01;
//
//    @FXML
//    private TableColumn index02;
//
//    @FXML
//    private TableColumn index03;
//
//    @FXML
//    private TableColumn index04;
//
//    @FXML
//    private TableColumn index05;
//
//    @FXML
//    private TableColumn index06;
//
//    @FXML
//    void tblLedger_Clicked(MouseEvent event) {
//
//    }
//    public void receiveString(String message) {
//        lsSearchRes = (message);
//    }
//
//    private ObservableList<ModelAPClientLedger> data = FXCollections.observableArrayList();
//    /**
//     * Initializes the controller class.
//     */
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//
//        pnEditMode = EditMode.UNKNOWN;
//        initButton(pnEditMode);
//        clearAllFields();
//        initializeObject();
//        InitTextFields();
//        ClickButton();
//        initSearchFields();
////        InitSearchFields();
//        initTabAnchor();
//        pbLoaded = true;
//
//        chkfield01.setOnAction(event -> {
//            String value = chkfield01.isSelected() ? "1" : "0";
//            oTrans.APClientMaster().getModel().setVatable(value);
//        });
//    }
//
//    private void initializeObject() {
//        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
//        oTrans = new APClients(oApp, "", logwrapr);
//        oParameters = new ParamControllers(oApp, logwrapr);
//
//    }
//
//    @Override
//    public void setGRider(GRider foValue) {
//        oApp = foValue;
//
//    }
//    public void setTransaction(String fsValue){
//        oTransnox = fsValue;
//    }
//
//    public void setState(boolean fsValue){
//        state = fsValue;
//    }
//
//    private void ClickButton() {
//        btnCancel.setOnAction(this::handleButtonAction);
//        btnSave.setOnAction(this::handleButtonAction);
//        btnUpdate.setOnAction(this::handleButtonAction);
//        btnClose.setOnAction(this::handleButtonAction);
//        btnBrowse.setOnAction(this::handleButtonAction);
//    }
//    private void initSearchFields(){
//        /*textFields FOCUSED PROPERTY*/
//        txtSearch01.setOnKeyPressed(this::searchinfo_KeyPressed);
//        txtSearch02.setOnKeyPressed(this::searchinfo_KeyPressed);
//    }
//    private void searchinfo_KeyPressed(KeyEvent event){
//        TextField txtSearch = (TextField)event.getSource();
//        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(9,11));
//        String lsValue = txtSearch.getText();
//        JSONObject poJson;
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex){
//                    case 01: /*search company id*/
////                        poJson = oTrans.(lsValue, true);
////                        if ("error".equals((String) poJson.get("result"))){
////                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
////                            txtSearch02.clear();
////                            break;
////                        }
////                        pnEditMode = oTrans.getEditMode();
////                        retrieveDetails();
//                    break;
//                    case 02: /*search company name*/
////                        poJson = oTrans.searchRecord(lsValue, false);
////                        if ("error".equals((String) poJson.get("result"))){
////                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
////                            txtSearch02.clear();
////                            break;
////                        }
////                        pnEditMode = oTrans.getEditMode();
////                        retrieveDetails();
//                    break;
//                }
//            case ENTER:
//        }
//
//        switch (event.getCode()){
//        case ENTER:
//            CommonUtils.SetNextFocus(txtSearch);
//        case DOWN:
//            CommonUtils.SetNextFocus(txtSearch);
//            break;
//        case UP:
//            CommonUtils.SetPreviousFocus(txtSearch);
//        }
//    }
//    private void handleButtonAction(ActionEvent event) {
//        Object source = event.getSource();
//        JSONObject poJSON;
//        if (source instanceof Button) {
//            Button clickedButton = (Button) source;
//            unloadForm appUnload = new unloadForm();
//            switch (clickedButton.getId()) {
//                case"btnClose":
//                     appUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
////                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)){
////
////                        }
//                    break;
//                case "btnUpdate":
//                         poJSON = oTrans.APClientMaster().updateRecord();
//                        if ("error".equals((String) poJSON.get("result"))){
//                            ShowMessageFX.Information((String)poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        }
//                        pnEditMode =  oTrans.APClientMaster().getEditMode();
//
//                        initButton(pnEditMode);
//                        initTabAnchor();
//                    break;
//                case "btnCancel":
//                        if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)){
//                            clearAllFields();
//                            initializeObject();
//                            pnEditMode = EditMode.UNKNOWN;
//                            initButton(pnEditMode);
//                            initTabAnchor();
//                        }
//                    break;
//                case "btnBrowse":
//                            String lsValue = (txtSearch01.getText()==null?"":txtSearch01.getText());
//                            poJSON =  oTrans.APClientMaster().searchRecord(lsValue, false);
//                           System.out.println("poJson = " + poJSON.toJSONString());
//                           if("error".equalsIgnoreCase(poJSON.get("result").toString())){
//                                ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                                break;
//                           }
//                           pnEditMode = EditMode.READY;
//
//                            initTable();
//                            retrieveDetails();
//                            oTrans.OpenLedger(oTrans.APClientMaster().getModel().getClientId());
//                            loadLedger();
//                    break;
//                case "btnSave":
//                    oTrans.APClientMaster().getModel().setModifyingId(oApp.getUserID());
//                    oTrans.APClientMaster().getModel().setModifiedDate(oApp.getServerDate());
//                    JSONObject saveResult = oTrans.APClientMaster().saveRecord();
//                    if ("success".equals((String) saveResult.get("result"))) {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                        pnEditMode = EditMode.UNKNOWN;
//                        initButton(pnEditMode);
//                        clearAllFields();
//                    } else {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                    }
//
//                     break;
//
//        }
//    }
//}
//    private void InitSearchFields(){
//        txtSearch01.setOnKeyPressed(this::txtSearch_KeyPressed);
//        txtSearch02.setOnKeyPressed(this::txtSearch_KeyPressed);
//    }
//
//    private void InitTextFields(){
//
//        txtField01.focusedProperty().addListener(txtField_Focus);
//        txtField02.focusedProperty().addListener(txtField_Focus);
//        txtField03.focusedProperty().addListener(txtField_Focus);
//        txtField04.focusedProperty().addListener(txtField_Focus);
//        txtField05.focusedProperty().addListener(txtField_Focus);
//        txtField06.focusedProperty().addListener(txtField_Focus);
//        txtField07.focusedProperty().addListener(txtField_Focus);
//        txtField08.focusedProperty().addListener(txtField_Focus);
////        txtField09.focusedProperty().addListener(txtField_Focus);
//        txtField10.focusedProperty().addListener(txtField_Focus);
//        txtField11.focusedProperty().addListener(txtField_Focus);
//        txtField12.focusedProperty().addListener(txtField_Focus);
//
//        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
//        txtField09.setOnKeyPressed(this::txtField_KeyPressed);
//        txtField13.setOnKeyPressed(this::txtField_KeyPressed);
//
//        // Set a custom StringConverter to format date
//          DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
////        initdatepicker();
//    }
//    private void initTabAnchor(){
//        boolean pbValue = pnEditMode == EditMode.ADDNEW ||
//                pnEditMode == EditMode.UPDATE;
//        AnchorCompany.setDisable(!pbValue);
//
//    }
//
//    private void txtField_KeyPressed(KeyEvent event){
//        TextField txtField = (TextField)event.getSource();
//        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(8,10));
//        String lsValue = (txtField.getText() == null ?"": txtField.getText());
//        JSONObject poJson;
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex){
//                    case 02: /*search branch*/
//                        poJson = new JSONObject();
//                        String input = "";
//                        input = lsValue;
//                           poJson =  oTrans.APClientMaster().searchRecord(lsValue, false);
//                           System.out.println("poJson = " + poJson.toJSONString());
//                           if("error".equalsIgnoreCase(poJson.get("result").toString())){
//                               loadCompanyTransaction();
//                               String receivedData = SharedModel.sharedString;
//                               System.out.println("receivedData = " + GlobalVariables.sClientID);
////                               poJson = oTrans.SearchClient(receivedData, tr);
//                           }
//                           retrieveDetails();
//                        break;
//                    case 9: /*search branch*/
//                        poJson = new JSONObject();
//                        input = lsValue;
//                           poJson =  oParameters.Term().searchRecord(lsValue, false) ;
//                           System.out.println("poJson = " + poJson.toJSONString());
//                           if("success".equals((String) poJson.get("result"))){
//                               txtField09.setText(oParameters.Term().getModel().getDescription());
//                               oTrans.APClientMaster().getModel().setTermId(oParameters.Term().getModel().getTermCode());
//                           }
//                        break;
//                }
//            case ENTER:
//
//        }
//        switch (event.getCode()){
//        case ENTER:
//            CommonUtils.SetNextFocus(txtField);
//        case DOWN:
//            CommonUtils.SetNextFocus(txtField);
//            break;
//        case UP:
//            CommonUtils.SetPreviousFocus(txtField);
//        }
//    }
//
//    final ChangeListener<? super Boolean> txtField_Focus = (ObservableValue<? extends Boolean> o,Boolean ov,Boolean nv)->{
//        if (!pbLoaded) return;
//
//        TextField txtField = (TextField)((ReadOnlyBooleanPropertyBase)o).getBean();
//        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
//        String lsValue = txtField.getText();
//        JSONObject jsonObject = new JSONObject();
//        if (lsValue == null) return;
//        if(!nv){ /*Lost Focus*/
//            switch (lnIndex){
//                case 1: /*company id*/
////                    jsonObject = oTrans.setMaster( 8,lsValue);
////                    jsonObject =  oTrans.APClientMaster().getModel().(lsValue);
//                    break;
//                case 2:/*company name*/
//                    break;
//                case 3:/*address*/
//                    break;
//                case 4:/*Contact person*/
////                    jsonObject =oTrans.setMaster(3,jsonObject.get("sContctID"));
//                    break;
//                case 5:/*contact no*/
//                    break;
//                case 6:/*tin No */
//                    break;
//                case 7:/*Credit limit*/
//                case 8:
//                case 10:
//                case 11:
//                case 12:
//                    double discount = Double.parseDouble(lsValue);
//                        if (lnIndex == 7) {
//                            oTrans.APClientMaster().getModel().setCreditLimit(discount);
//                        }
//                        if (lnIndex == 8) {
//                            oTrans.APClientMaster().getModel().setDiscount(discount);
//                        }
//                        if (lnIndex == 10) {
//                            oTrans.APClientMaster().getModel().setBeginningBalance(discount);
//                        }
//                        if (lnIndex == 11) {
//                            oTrans.APClientMaster().getModel().setAccountBalance(discount);
//                        }
//                        if (lnIndex == 12) {
//                            oTrans.APClientMaster().getModel().setOBalance(discount);
//                        }
//                        txtField.setText(CommonUtils.NumberFormat(discount, "0.00"));
//                        break;
////                    txtField.setText( (CommonUtils.NumberFormat(Double.parseDouble(lsValue), "#,##0.00")));
////                    jsonObject = oTrans.setMaster(10,(Double.parseDouble(lsValue.replace(",", ""))));
////                    break;
////                case 8:/*discount*/
//////                    txtField.setText(CommonUtils.NumberFormat(Double.parseDouble(lsValue), "0.00"));
//////                    jsonObject = oTrans.setMaster(9,(Double.parseDouble(lsValue)));
////                    break;
//                case 9:/*term */
//                    break;
////                case 10 :/*beginning balance*/
////
//////                    txtField.setText( (CommonUtils.NumberFormat(Double.parseDouble(lsValue), "#,##0.00")));
//////                    jsonObject = oTrans.setMaster(7,(Double.parseDouble(lsValue.replace(",", ""))));
////                    break;
////                case 11 :/*available balance*/
//////                    txtField.setText( (CommonUtils.NumberFormat(Double.parseDouble(lsValue), "#,##0.00")));
//////                    jsonObject = oTrans.setMaster(11,(Double.parseDouble(lsValue.replace(",", ""))));
////                    break;
////                case 12 :/*outstanding balance*/
//////                    txtField.setText( (CommonUtils.NumberFormat(Double.parseDouble(lsValue), "#,##0.00")));
////                    jsonObject = oTrans.setMaster(12,(Double.parseDouble(lsValue.replace(",", ""))));
////                    break;
//            }
//        } else
//            txtField.selectAll();
//    };
//
//    @FXML
//    void ChkVATReg_Clicked(MouseEvent event) {
//        boolean isChecked = chkfield01.isSelected();
////        oTrans.setMaster(14,(isChecked) ? "1" : "0");
//        String val = (isChecked) ? "1" : "0";
//    }
//    /*OPEN WINDOW FOR */
//    private void loadCompanyTransaction() {
//        try {
//            String sFormName = "Client Transactions Company";
//            FXMLLoader fxmlLoader = new FXMLLoader();
//            unloadForm unload = new unloadForm();
//            ClientMasterTransactionCompanyController loControl = new ClientMasterTransactionCompanyController();
//            loControl.setGRider(oApp);
//            loControl.setParentController(this);
//            loControl.setCompanyName((txtField02.getText()==null?"":txtField02.getText().toString()));
//            fxmlLoader.setLocation(getClass().getResource("/org/guanzon/cas/views/ClientMasterTransactionCompany.fxml"));
//            fxmlLoader.setController(loControl);
//            Parent parent = fxmlLoader.load();
//            AnchorPane otherAnchorPane = loControl.AnchorMain;
//
//            // Get the parent of the TabContent node
//            Node tabContent = AnchorMain.getParent();
//            Parent tabContentParent = tabContent.getParent();
//
//            // If the parent is a TabPane, you can work with it directly
//            if (tabContentParent instanceof TabPane) {
//                TabPane tabpane = (TabPane) tabContentParent;
//
//                for (Tab tab : tabpane.getTabs()) {
//                    if (tab.getText().equals(sFormName)) {
//                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "You have unsaved data on " + pxeModuleName + ". Are you sure you want to create new client for account payable  record?") == true) {
//
//                            System.out.println("globalvariable = " + GlobalVariables.sClientID);
//                        } else {
//                            System.out.println("globalvariable = " + GlobalVariables.sClientID);
//                            return;
//                        }
//
//                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "You have opened " + pxeModuleName + ".  Are you sure you want to create new client for account payable record?") == true) {
//
//                            System.out.println("globalvariable = " + GlobalVariables.sClientID);
//                        } else {
//
//                            System.out.println("globalvariable = " + GlobalVariables.sClientID);
//                            return;
//                        }
//                        tabpane.getSelectionModel().select(tab);
//                        unload.unloadForm(AnchorMain, oApp, sFormName);
//                        unload.unloadForm(otherAnchorPane, oApp, sFormName);
//
//                        System.out.println("globalvariable = " + GlobalVariables.sClientID);
//                        loadCompanyTransaction();
//                        return;
//                    }
//                }
//
//                Tab newTab = new Tab(sFormName, parent);
//                newTab.setStyle("-fx-font-weight: bold; -fx-pref-width: 180; -fx-font-size: 10.5px; -fx-font-family: arial;");
//                newTab.setContextMenu(fdController.createContextMenu(tabpane, newTab, oApp));
//                tabpane.getTabs().add(newTab);
//                tabpane.getSelectionModel().select(newTab);
//                newTab.setOnCloseRequest(event -> {
//                    if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure, do you want to close tab?") == true) {
//                        if (unload != null) {
//                            unload.unloadForm(otherAnchorPane, oApp, sFormName);
//
//                            System.out.println("globalvariable = " + GlobalVariables.sClientID);
//                        } else {
////                            ShowMessageFX.Warning(getStage(), "Please notify the system administrator to configure the null value at the close button.", "Warning", pxeModuleName);
//                        }
//                    } else {
//                        // Cancel the close request
//                        event.consume();
//                    }
//
//                });
//
//                List<String> tabName = new ArrayList<>();
//
//                tabName.remove(sFormName);
//                tabName.add(sFormName);
//                // Save the list of tab IDs to the JSON file
//
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
////            ShowMessageFX.Warning(getStage(), e.getMessage(), "Warning", null);
//            System.exit(1);
//
//         }
//    }
//
//    private void initButton(int fnValue){
//        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
//         btnCancel.setVisible(lbShow);
//        btnSearch.setVisible(lbShow);
//        btnSave.setVisible(lbShow);
//
//        btnSave.setManaged(lbShow);
//        btnCancel.setManaged(lbShow);
//        btnSearch.setManaged(lbShow);
//        btnUpdate.setVisible(!lbShow);
//        btnBrowse.setVisible(!lbShow);
//
//
//        txtSearch01.setDisable(!lbShow);
//        txtSearch02.setDisable(!lbShow);
//
//        if (lbShow){
//            txtSearch01.setDisable(lbShow);
//            txtSearch01.clear();
//            txtSearch02.setDisable(lbShow);
//
//            btnCancel.setVisible(lbShow);
//            btnSearch.setVisible(lbShow);
//            btnSave.setVisible(lbShow);
//            btnUpdate.setVisible(!lbShow);
//            btnBrowse.setVisible(!lbShow);
//            btnBrowse.setManaged(false);
//            btnUpdate.setManaged(false);
//            btnClose.setManaged(false);
//        }
//        else{
//            txtSearch01.setDisable(lbShow);
//            txtSearch01.requestFocus();
//            txtSearch02.setDisable(lbShow);
//        }
//
//    }
//    private void txtSearch_KeyPressed(KeyEvent event){
//        TextField txtSearch = (TextField)event.getSource();
//        int lnIndex = Integer.parseInt(((TextField)event.getSource()).getId().substring(9,11));
//        String lsValue = txtSearch.getText();
//        JSONObject poJson;
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex){
//                    case 01: /*search company*/
////                        poJson = new JSONObject();
////                            poJson =  oTrans.searchRecord(lsValue, true);
////                           System.out.println("poJson = " + poJson.toJSONString());
////                           if("error".equalsIgnoreCase(poJson.get("result").toString())){
////                                ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
////                           }
////                           pnEditMode = oTrans.getEditMode();
////                           txtSearch02.setText(oTrans.getModel().getClientName());
////                           txtSearch01.setText(oTrans.getModel().getClientID());
////
////                        break;
////                    case 02: /*search company*/
////                        poJson = new JSONObject();
////                            poJson =  oTrans.searchRecord(lsValue, false);
////                           System.out.println("poJson = " + poJson.toJSONString());
////                           if("error".equalsIgnoreCase(poJson.get("result").toString())){
////                                ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
////                           }
////                           pnEditMode = oTrans.getEditMode();
////                           txtSearch02.setText(oTrans.getModel().getClientName());
////                           txtSearch01.setText(oTrans.getModel().getClientID());
//
//                        break;
//                }
//            case ENTER:
//
//        }
//        switch (event.getCode()){
//        case ENTER:
//            CommonUtils.SetNextFocus(txtSearch);
//        case DOWN:
//            CommonUtils.SetNextFocus(txtSearch);
//            break;
//        case UP:
//            CommonUtils.SetPreviousFocus(txtSearch);
//        }
//
//        retrieveDetails();
//    }
//    private void retrieveDetails(){
//
//        if (pnEditMode == EditMode.READY
//                || pnEditMode == EditMode.ADDNEW
//                || pnEditMode == EditMode.UPDATE) {
//            System.out.println("address = " + oTrans.APClientMaster().getModel().ClientAddress().getAddress());
//            txtField01.setText((String) oTrans.APClientMaster().getModel().getClientId());
//            txtField02.setText((String) oTrans.APClientMaster().getModel().ClientMaster().getCompanyName());
//            txtField03.setText((String) oTrans.APClientMaster().getModel().ClientAddress().getAddress());
//            txtField04.setText((String) oTrans.APClientMaster().getModel().ClientInstitutionContact().getContactPersonName());
//            txtField05.setText((String) oTrans.APClientMaster().getModel().ClientInstitutionContact().getMobileNo());
//            txtField06.setText((String) oTrans.APClientMaster().getModel().ClientMaster().getTaxIdNumber());
//            txtField07.setText(CommonUtils.NumberFormat(oTrans.APClientMaster().getModel().getCreditLimit(), "#,##0.00"));
//            txtField08.setText(CommonUtils.NumberFormat(oTrans.APClientMaster().getModel().getDiscount(), "#,##0.00"));
//            txtField09.setText((String) oTrans.APClientMaster().getModel().Term().getDescription());
//            txtField10.setText(CommonUtils.NumberFormat(oTrans.APClientMaster().getModel().getBeginningBalance(), "#,##0.00"));
//            txtField11.setText(CommonUtils.NumberFormat(oTrans.APClientMaster().getModel().getAccountBalance(), "#,##0.00"));
//            txtField12.setText(CommonUtils.NumberFormat(oTrans.APClientMaster().getModel().getOBalance(), "#,##0.00"));
//            txtField13.setText((String) oTrans.APClientMaster().getModel().Category().getDescription());
//
//
//            if (oTrans.APClientMaster().getModel().getdateClientSince() != null && !oTrans.APClientMaster().getModel().getdateClientSince().toString().isEmpty()) {
//                cpField01.setValue(strToDate(SQLUtil.dateFormat(oTrans.APClientMaster().getModel().getdateClientSince(), SQLUtil.FORMAT_SHORT_DATE)));
//            }
//            if (oTrans.APClientMaster().getModel().getBeginningDate()!= null && !oTrans.APClientMaster().getModel().getBeginningDate().toString().isEmpty()) {
//                cpField02.setValue(strToDate(SQLUtil.dateFormat(oTrans.APClientMaster().getModel().getBeginningDate(), SQLUtil.FORMAT_SHORT_DATE)));
//            }
//
//
//            chkfield01.setSelected(("1".equals((String) oTrans.APClientMaster().getModel().getVatable())));
//            String lsValue = oTrans.APClientMaster().getModel().getRecordStatus();
//
//            // Use a Map to store the status mappings
//            Map<String, String> statusMap = new HashMap<>();
//            statusMap.put("0", "OPEN");
//            statusMap.put("1", "APPROVED");
//            statusMap.put("3", "DISAPPROVED");
//            statusMap.put("4", "BLACKLISTED");
//
//            // Set the label text based on the status
//            lblStat.setText(statusMap.getOrDefault(lsValue, "UNKNOWN"));
//
//
////              loadLedger();
//////              txtSearch01.clear();
//////              txtSearch02.clear();
//        }
//
//    }
//
//    public static LocalDate strToDate(String val) {
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return LocalDate.parse(val, dateFormatter);
//    }
//    private void loadLedger() {
//        System.out.println("nagload and ledger");
//        data.clear();
//
//        if (oTrans.getLedgerCount() >= 0) {
//            for (int lnCtr = 0; lnCtr < oTrans.getLedgerCount(); lnCtr++) {
//                System.out.println("Processing Serial Ledger at Index: " + lnCtr);
//
//                // Debugging individual components
////                System.out.println("Transaction Date: " + strToDate(SQLUtil.dateFormat(oTrans.APClientLedger(lnCtr).getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
//                System.out.println("Source Code: " + oTrans.APClientLedger(lnCtr).getSourceCode());
//                System.out.println("Source No: " + oTrans.APClientLedger(lnCtr).getSourceNo());
////                System.out.println("Source Code: " + CommonUtils.NumberFormat(oTrans.APClientLedger(lnCtr).getAmountIn(), "#,##0.00"));
////                System.out.println("Source No: " + CommonUtils.NumberFormat(oTrans.APClientLedger(lnCtr).getAmountOt(), "#,##0.00"));
//
//                data.add(new ModelAPClientLedger(
//                        String.valueOf(lnCtr + 1),
//                        oTrans.APClientLedger(lnCtr).getTransactionDate().toString(),
//                        oTrans.APClientLedger(lnCtr).getSourceNo(),
//                        oTrans.APClientLedger(lnCtr).getSourceCode(),
//                        CommonUtils.NumberFormat(oTrans.APClientLedger(lnCtr).getAmountIn(), "#,##0.00"),
//                       CommonUtils.NumberFormat(oTrans.APClientLedger(lnCtr).getAmountOt(), "#,##0.00")
//                ));
//            }
//        } else {
//            ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
//        }
//    }
//    private void clearAllFields() {
//        // Arrays of TextFields grouped by sections
//        TextField[][] allFields = {
//            // Text fields related to specific sections
//            {txtSearch01, txtSearch02, txtField01, txtField02, txtField03, txtField04,
//             txtField05, txtField06, txtField07, txtField08, txtField09,txtField10, txtField11, txtField12, txtField13},
//
//
//        };
//        cpField01.setValue(null);
//        cpField02.setValue(null);
//        chkfield01.setSelected(false);
//
//
//        // Loop through each array of TextFields and clear them
//        for (TextField[] fields : allFields) {
//            for (TextField field : fields) {
//                field.clear();
//            }
//        }
//    }
//
//    private void initTable() {
//        index01.setStyle("-fx-alignment: CENTER;");
//        index02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        index05.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 10 0 5;");
//        index06.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 10 0 5;");
//
//        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
//        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
//        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));
//        index04.setCellValueFactory(new PropertyValueFactory<>("index04"));
//        index05.setCellValueFactory(new PropertyValueFactory<>("index05"));
//        index06.setCellValueFactory(new PropertyValueFactory<>("index06"));
//        tblLedger.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
//            TableHeaderRow header = (TableHeaderRow) tblLedger.lookup("TableHeaderRow");
//            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                header.setReordering(false);
//            });
//        });
//        tblLedger.setItems(data);
//        tblLedger.autosize();
//    }
//
////    public void loadReturn(String lsValue) {
////        System.out.println("loadReturn lsValue = " + lsValue);
////        JSONObject poJson = new JSONObject();
////         poJson =  oTrans.SearchClient(lsValue, true);
////        System.out.println("poJson = " + poJson.toJSONString());
////        if("error".equalsIgnoreCase(poJson.get("result").toString())){
////            ShowMessageFX.Information((String)poJson.get("message"), "Computerized Acounting System", pxeModuleName);
////        }
////        txtField01.setText((String) poJson.get("sClientID"));
////        txtField02.setText((String) poJson.get("sCompnyNm"));
////        txtField03.setText((String) poJson.get("xAddressx"));
////        txtField04.setText((String) poJson.get("sCPerson1"));
////        txtField05.setText((String) poJson.get("sMobileNo"));
////        txtField06.setText((String) poJson.get("sTaxIDNox"));
////    }
//
////    private void StatusLabel(){
//
////    }
//
//

    @Override
    public void setIndustryID(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setCompanyID(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
