/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelAccredetations;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.client.Client;
//import org.guanzon.cas.client.account.Account_Accreditation;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class AccountsAccreditationController implements Initializable, ScreenInterface {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setGRider(GRider foValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

//    private final String pxeModuleName = "Accounts Accreditation";
//    private GRider oApp;
//    private Account_Accreditation oTrans;
//    private ParamControllers oParameters;
//    private String oTransnox = "";
//    private int pnEditMode;
//    private String a = "";
//    private String b = "";
//    private String c = "";
//    private boolean state = false;
//    private boolean pbLoaded = false;
//    private int pnCompany = 0;
//
//    String category = System.getProperty("store.inventory.industry", "").trim().replace(";", "");
//
//    ObservableList<String> AccountType = FXCollections.observableArrayList(
//            "Accounts Payable",
//            "Accounts Receivable"
//    );
//
//    ObservableList<String> Accreditation = FXCollections.observableArrayList(
//            "Accredition",
//            "Blocklisted"
//    );
//    ObservableList<String> statusList = FXCollections.observableArrayList("OPEN", "APPROVED", "DISAPPROVED");
//    private ObservableList<ModelAccredetations> data = FXCollections.observableArrayList();
//
//    @FXML
//    private AnchorPane AnchorMain, AnchorInputs, AnchorTable;
//
//    @FXML
//    private HBox hbButtons;
//
//    @FXML
//    private Button btnBrowse,
//            btnNew,
//            btnSave,
//            btnUpdate,
//            btnSearch,
//            btnCancel,
//            btnClose,
//            btnDisapproved,
//            btnApproved,
//            btnUpload;
//
//    @FXML
//    private Label lblStat;
//
//    @FXML
//    private ComboBox cmbField01,
//            cmbField02,
//            cmbField03;
//
//    @FXML
//    private DatePicker cpField01,
//            cpField02;
//
//    @FXML
//    private TextField txtSeek01,
//            txtField01,
//            txtField02,
//            txtField03,
//            txtField04,
//            txtField05;
//
//    @FXML
//    private TableView tblAccreditation;
//
//    @FXML
//    private TableColumn indexCompany01,
//            indexCompany02,
//            indexCompany03,
//            indexCompany04,
//            indexCompany05,
//            indexCompany06;
//
//    @FXML
//    void tblAccreditation_Clicked(MouseEvent event) {
////        pnCompany = tblAccreditation.getSelectionModel().getSelectedIndex();
////        getAccreditationSelectedItems();
//
//    }
//
//    @Override
//    public void setGRider(GRider foValue) {
//        oApp = foValue;
//
//    }
//
//    /**
//     * Initializes the controller class.
//     */
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        pnEditMode = EditMode.UNKNOWN;
//        clearAllFields();
//        initializeObject();
//        InitTextFields();
//        initComboBoxes();
//        initTabAnchor();
//        ClickButton();
//        initButton(pnEditMode);
//        AnchorTable.setVisible(false);
//        pbLoaded = true;
////        System.out.println("categ == " + );
//    }
//
//    private void initTabAnchor() {
//        boolean pbValue = pnEditMode == EditMode.ADDNEW
//                || pnEditMode == EditMode.UPDATE;
//        AnchorInputs.setDisable(false);
//        if (pnEditMode == EditMode.READY || pnEditMode == EditMode.UNKNOWN) {
//            AnchorInputs.setDisable(true);
//        }
//    }
//
//    private void initializeObject() {
//
//        System.out.println("category == " + category);
//        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
//        oTrans = new Account_Accreditation();
//        oTrans.setApplicationDriver(oApp);
//        oTrans.setWithParentClass(false);
//        oTrans.setLogWrapper(logwrapr);
//        oTrans.initialize();
//        oParameters = new ParamControllers(oApp, logwrapr);
//
//    }
//
////    /*Handle button click*/
//    private void ClickButton() {
//        Button[] buttons = {btnUpload,
//                            btnCancel,
//                            btnNew,
//                            btnSave,
//                            btnUpdate,
//                            btnClose,
//                            btnBrowse,
//                            btnApproved,
//                            btnDisapproved};
//        for (Button button : buttons) {
//            button.setOnAction(this::handleButtonAction);
//        }
//    }
//
//    private void handleButtonAction(ActionEvent event) {
//        Object source = event.getSource();
//
//        if (source instanceof Button) {
//            Button clickedButton = (Button) source;
//            unloadForm appUnload = new unloadForm();
//            switch (clickedButton.getId()) {
//                case "btnClose":
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
//                        clearAllFields();
//                        appUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
//                    }
//                    break;
//                case "btnNew":
//                    clearAllFields();
//                    txtField02.requestFocus();
//                    JSONObject poJSON;
//                    poJSON = oTrans.newRecord();
//                    pnEditMode = EditMode.READY;
//                    if ("success".equals((String) poJSON.get("result"))) {
//                        pnEditMode = EditMode.ADDNEW;
//                        initButton(pnEditMode);
//                        RetreiveDetails();
//                        initTabAnchor();
//                    } else {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        System.out.println((String) poJSON.get("message"));
//                        initTabAnchor();
//
//                    }
//
//                    break;
//                case "btnBrowse":
//                    clearAllFields();
//                    String lsValue = (txtSeek01.getText() == null) ? "" : txtSeek01.getText();
//                    poJSON = oTrans.searchRecord(lsValue, false, true);
//                    if ("error".equals((String) poJSON.get("result"))) {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
////                        txtSeeks01.clear();
//                        break;
//                    }
//                    pnEditMode = EditMode.READY;
////                    data.clear();
//                    RetreiveDetails();
//                    break;
//                case "btnUpdate":
//                    poJSON = oTrans.updateRecord();
//                    if ("error".equals((String) poJSON.get("result"))) {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        break;
//                    } else {
//                        pnEditMode = oTrans.getEditMode();
//                        System.out.println("EDITMODE sa update= " + pnEditMode);
//                        initButton(pnEditMode);
//                        initTabAnchor();
//                        break;
//                    }
//                case "btnCancel":
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
//                        clearAllFields();
//                        initializeObject();
//                        pnEditMode = EditMode.UNKNOWN;
//                        initButton(pnEditMode);
//                        initTabAnchor();
//                    }
//                    System.out.println("EDITMODE sa cancel= " + pnEditMode);
//                    break;
//                case "btnSave":
//                    oTrans.getModel().setModifyingId(oApp.getUserID());
//                    oTrans.getModel().setModifiedDate(oApp.getServerDate());
//                    JSONObject saveResult = oTrans.saveRecord();
//                    if ("success".equals((String) saveResult.get("result"))) {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                        pnEditMode = EditMode.UNKNOWN;
//                        initButton(pnEditMode);
//                        clearAllFields();
//                        System.out.println("Record saved successfully.");
//                    } else {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                        System.out.println("Record not saved successfully.");
//                        System.out.println((String) saveResult.get("message"));
//                    }
//
//                    break;
//                case "btnApproved":
//                case "btnDisapproved":
//                    if (oTrans.getModel().getTransactionNo() != null) {
//                        String buttonId = ((Button) event.getSource()).getId();
//                        boolean isApproved = "btnApproved".equals(buttonId);
//                        poJSON = isApproved ? oTrans.postTransaction() : oTrans.voidTransaction();
//                        String result = (String) poJSON.get("result");
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
////                        ShowMessageFX.Information("success".equals(result)
////                                ? (String) poJSON.get("message") + (isApproved ? "approved." : "disapproved.")
////                                : "Unable to " + (isApproved ? "approve" : "disapprove") + " the transaction.",
////                                "Computerized Accounting System",
////                                pxeModuleName);
//                        
//                        if ("success".equals(result)) {
//                            clearAllFields();
//                        }
//                    }else{
//                        ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
//                    }
//                    break;
//                case "btnUpload":
//                    ShowMessageFX.Information("This feature is currently in development!", "Computerized Acounting System", pxeModuleName);
//                    break;
//            }
//        }
//    }
//
//    private void initButton(int fnValue) {
//        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
//        btnCancel.setVisible(lbShow);
//        btnSearch.setVisible(lbShow);
//        btnSave.setVisible(lbShow);
//
//        btnSave.setManaged(lbShow);
//        btnCancel.setManaged(lbShow);
//        btnSearch.setManaged(lbShow);
//        btnUpdate.setVisible(!lbShow);
//        
//        btnApproved.setVisible(!lbShow);
//        btnUpload.setVisible(!lbShow);
//        btnDisapproved.setVisible(!lbShow);
//        
//        btnBrowse.setVisible(!lbShow);
//        btnNew.setVisible(!lbShow);
//
//        if (lbShow) {
//            btnCancel.setVisible(lbShow);
//            btnSearch.setVisible(lbShow);
//            btnSave.setVisible(lbShow);
//
//            btnApproved.setVisible(lbShow);
//            btnDisapproved.setVisible(lbShow);
//            btnUpload.setVisible(lbShow);
//            
//            btnUpdate.setVisible(!lbShow); 
//            btnBrowse.setVisible(!lbShow);
//            btnNew.setVisible(!lbShow);
//            btnBrowse.setManaged(false);
//            btnNew.setManaged(false);
//            btnUpdate.setManaged(false);
//            
//            btnUpload.setManaged(false);
//            btnApproved.setManaged(false);
//            btnDisapproved.setManaged(false);
//            
//            btnClose.setManaged(false);
//        } else {
//        }
//
//    }
//
//    private void InitTextFields() {
//        // Text fields that require focus listeners
//        TextField[] focusTextFields = {
//            txtField01, txtField02, txtField03, txtField04, txtField05,};
//
//        // Add focus listener to each text field
//        for (TextField textField : focusTextFields) {
//            textField.focusedProperty().addListener(txtField_Focus);
//        }
//
//        // Text fields that require key press handlers
//        TextField[] keyPressedTextFields = {
//            txtField02, txtField05
//        };
//
//        // Add key press event handler to each text field
//        for (TextField textField : keyPressedTextFields) {
//            textField.setOnKeyPressed(this::txtField_KeyPressed);
//        }
//
//    }
//
////    /*textfield lost focus*/
//    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
//        if (!pbLoaded) {
//            return;
//        }
//
//        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
//        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
//        String lsValue = txtField.getText();
//
//        if (lsValue == null) {
//            return;
//        }
//
//        if (!nv) { // Lost focus
//            try {
//                switch (lnIndex) {
//                    case 1: // 
//                        oTrans.getModel().setTransactionNo(lsValue);
//                        break;
//                    case 2: // 
//                        oTrans.getModel().setClientId(oTrans.Client().Master().getModel().getClientId());
//                        break;
//                    case 3: //     
//                        oTrans.getModel().setClientId(oTrans.Client().ClientInstitutionContact().getModel().getClientId());
//                        break;
//                    case 4: // 
//                        oTrans.getModel().setRemarks(lsValue);
//                        break;
//                    case 5: // Description
//                        oTrans.getModel().setCategoryCode(category);
//                        break;
//                    default:
//                        // Other cases can be handled here if needed.
//                        break;
//                }
//            } catch (Exception e) {
//                System.err.println("Error processing input: " + e.getMessage());
//            }
//        } else { // Gained focus
//            txtField.selectAll();
//        }
//    };
//
//    /*Text Field with search*/
//    private void txtField_KeyPressed(KeyEvent event) {
//        TextField txtField = (TextField) event.getSource();
//        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
//        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
//        JSONObject poJson;
//        poJson = new JSONObject();
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex) {
//                    case 05:
//                        poJson = oParameters.Category().searchRecord(lsValue, false);
//                        if ("error".equalsIgnoreCase(poJson.get("result").toString())) {
//                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                        }
//                        oTrans.getModel().setCategoryCode(oParameters.Category().getModel().getCategoryId());
//                        txtField05.setText((String) oParameters.Category().getModel().getDescription());
//
//                        break;
//                    case 02:
//                        poJson = oTrans.Client().Master().searchRecord(lsValue, false);
//                        if ("success".equals(poJson.get("result"))) {
//                            poJson = oTrans.searchRecordbyClient(oTrans.Client().Master().getModel().getClientId(), false);
//                            if ("success".equals(poJson.get("result"))) {
//                                ShowMessageFX.Information("The company is already accredited or has already gained entry.!", "Computerized Acounting System", pxeModuleName);
//                                break;
//                            }
//                                txtField02.setText(oTrans.Client().Master().getModel().getCompanyName());
//                                oTrans.getModel().setClientId(oTrans.Client().Master().getModel().getClientId());
//                                
//                                poJson = oTrans.Client().ClientInstitutionContact().searchRecordbyclient(oTrans.Client().Master().getModel().getClientId(), false);
//                                if ("success".equals(poJson.get("result"))) {
//                                    txtField03.setText(oTrans.Client().ClientInstitutionContact().getModel().getContactPersonName());
//                                    oTrans.getModel().setContactId(oTrans.Client().ClientInstitutionContact().getModel().getClientId());
//                                } 
//                        }
//                        poJson = oTrans.Client().ClientAddress().searchRecordbyclient(oTrans.Client().Master().getModel().getClientId(), false);
//                                if ("success".equals(poJson.get("result"))) {
//                                    oTrans.getModel().setAddressId(oTrans.Client().ClientAddress().getModel().getClientId());
//                                }
//                        break;
//                    
//                }
//            case ENTER:
//        }
//        switch (event.getCode()) {
//            case ENTER:
//                CommonUtils.SetNextFocus(txtField);
//            case DOWN:
//                CommonUtils.SetNextFocus(txtField);
//                break;
//            case UP:
//                CommonUtils.SetPreviousFocus(txtField);
//        }
//    }
////    private void txtSeeks_KeyPressed(KeyEvent event) {
////        TextField txtSeeks = (TextField) event.getSource();
////        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
////        String lsValue = (txtSeeks.getText() == null ? "" : txtSeeks.getText());
////        JSONObject poJSON;
////        switch (event.getCode()) {
////            case F3:
////                switch (lnIndex) {
////                    case 1:
////                        System.out.print("LSVALUE OF SEARCH 1 ==== " + lsValue);
////                        poJSON = oTrans.searchRecord(lsValue, true);
////                        if ("error".equals((String) poJSON.get("result"))) {
////                            ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
////                            txtSeeks01.clear();
////                            break;
////                        }
////                        txtSeeks01.setText(oTrans.getModel().getBarCode());
////                        txtSeeks02.setText(oTrans.getModel().getDescription());
////                        pnEditMode = oTrans.getEditMode();
////                        loadInventory();
////                        
////                        
////                        break;
////                    case 2:
////                        poJSON = oTrans.searchRecord(lsValue, false);
////                        if ("error".equals((String) poJSON.get("result"))) {
////                            ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
////                            txtSeeks01.clear();
////                            break;
////                        }
////                        pnEditMode = oTrans.getEditMode();
////                        txtSeeks01.setText(oTrans.getModel().getBarCode());
////                        txtSeeks02.setText(oTrans.getModel().getDescription());
////                        System.out.print("\neditmode on browse == " + pnEditMode);
////                        loadInventory();
////                        System.out.println("EDITMODE sa cancel= " + pnEditMode);
////                        break;
////                }
////            case ENTER:
////        }
////        switch (event.getCode()) {
////            case ENTER:
////                CommonUtils.SetNextFocus(txtSeeks);
////            case DOWN:
////                CommonUtils.SetNextFocus(txtSeeks);
////                break;
////            case UP:
////                CommonUtils.SetPreviousFocus(txtSeeks);
////        }
////    }
//
//    private void initComboBoxes() {
//
//        // Set the items of the ComboBox to the list of genders
//        cmbField02.setItems(AccountType);
//        cmbField02.getSelectionModel().select(0);
//        cmbField02.setOnAction(event -> {
//            oTrans.getModel().setAccountType(String.valueOf(cmbField02.getSelectionModel().getSelectedIndex()));
//            System.out.print("\ncAcctType = " + cmbField02.getSelectionModel().getSelectedIndex());
//        });
//
//        cmbField03.setItems(Accreditation);
//        cmbField03.getSelectionModel().select(0);
//        cmbField03.setOnAction(event -> {
//            oTrans.getModel().setTransactionType(String.valueOf(cmbField03.getSelectionModel().getSelectedIndex()));
//        });
//
//        cpField02.setOnAction(event -> {
//            // Get the selected date
//            oTrans.getModel().setDateTransact(SQLUtil.toDate(cpField02.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
//        });
//    }
//
//    private void clearAllFields() {
//        TextField[][] allFields = {
//            {txtField01, txtField02, txtField03, txtField04,
//                txtField05,},};
//        cmbField02.getSelectionModel().select(0);
//        cmbField03.getSelectionModel().select(0);
//        for (TextField[] fields : allFields) {
//            for (TextField field : fields) {
//                field.clear();
//            }
//        }
//        data.clear();
//        cpField02.setValue(LocalDate.now());
//        lblStat.setText("UNKNOWN");
//
//    }
//
//    private void RetreiveDetails() {
//        JSONObject poJson;
//        poJson = new JSONObject();
//        if (pnEditMode == EditMode.READY
//                || pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.ADDNEW) {
//
//            String lsValue = oTrans.getModel().getRecordStatus();
//            System.out.println(lsValue + " lblstat");
//            // Use a Map to store the status mappings
//            Map<String, String> statusMap = new HashMap<>();
//            statusMap.put("0", "OPEN");
//            statusMap.put("1", "APPROVED");
//            statusMap.put("3", "DISAPPROVED");
//
//            // Set the label text based on the status
//            lblStat.setText(statusMap.getOrDefault(lsValue, "UNKNOWN"));
//
//            txtField01.setText(oTrans.getModel().getTransactionNo() == null ? "" : oTrans.getModel().getTransactionNo());
//            txtField02.setText(oTrans.getModel().ClientMaster().getCompanyName() == null ? "" : oTrans.getModel().ClientMaster().getCompanyName());
//            txtField03.setText(oTrans.getModel().ClientInstitutionContact().getContactPersonName() == null ? "" : oTrans.getModel().ClientInstitutionContact().getContactPersonName());
//            txtField04.setText(oTrans.getModel().getRemarks() == null ? "" : oTrans.getModel().getRemarks());
//
//            cmbField02.setItems(AccountType);
//            cmbField02.setValue(AccountType.get(
//                    (oTrans.getModel().getAccountType() == null || oTrans.getModel().getAccountType().trim().isEmpty())
//                    ? 0
//                    : Integer.parseInt(oTrans.getModel().getAccountType())
//            ));
//
//            cmbField03.setItems(Accreditation);
//            cmbField03.setValue(Accreditation.get(
//                    (oTrans.getModel().getTransactionType() == null || oTrans.getModel().getTransactionType().trim().isEmpty())
//                    ? 0
//                    : Integer.parseInt(oTrans.getModel().getTransactionType())
//            ));
//            if (pnEditMode == 0) {
//
//                oTrans.getModel().setDateTransact(SQLUtil.toDate(cpField02.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
//                poJson = oParameters.Category().searchRecord(category, true);
//                if ("success".equals((String) poJson.get("result"))) {
//                    txtField05.setText(oParameters.Category().getModel().getDescription());
//                }
//            } else if (oTrans.getModel().getDateTransact() != null && !oTrans.getModel().getDateTransact().toString().isEmpty()) {
//                cpField02.setValue(strToDate(SQLUtil.dateFormat(oTrans.getModel().getDateTransact(), SQLUtil.FORMAT_SHORT_DATE)));
//                txtField05.setText((String) oTrans.getModel().Category().getDescription());
//            }
//
//            oTrans.getModel().setAccountType(String.valueOf(cmbField02.getSelectionModel().getSelectedIndex()));
//            oTrans.getModel().setTransactionType(String.valueOf(cmbField03.getSelectionModel().getSelectedIndex()));
//
//            poJson = oParameters.Category().searchRecord(category, true);
//            if ("success".equals(poJson.get("result") == null ? "" : poJson.get("result"))) {
//                txtField05.setText(oParameters.Category().getModel().getDescription() == null ? "" : oParameters.Category().getModel().getDescription());
//                oTrans.getModel().setCategoryCode(oParameters.Category().getModel().getCategoryId());
//            }
//        }
//        
//    }
//
//    public static LocalDate strToDate(String val) {
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        return LocalDate.parse(val, dateFormatter);
//    }
//    private void insertApprove(){
//        JSONObject poJson;
//        poJson = new JSONObject();
//        String AcctType = oTrans.getModel().getAccountType();
//        
//        switch (AcctType) {
//            case "1":
//                System.out.println("Account Type is 1");
//                oTrans.ARClient().ARClientMaster().getModel().setClientId(oTrans.getModel().ClientMaster().getClientId());
//                // Add logic for AcctType = 1
//                break;
//            case "0":
//                System.out.println("Account Type is 0");
//                 oTrans.APClient().APClientMaster().getModel().setClientId(oTrans.getModel().ClientMaster().getClientId());
//                 poJson = oTrans.APClient().APClientMaster().saveRecord();
//     
//                    if ("success".equals((String) poJson.get("result"))) {
//                           ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                           pnEditMode = EditMode.UNKNOWN;
//                    }else{
//                        ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                    }
//                // Add logic for AcctType = 0
//                break;
//            default:
//                System.out.println("Unknown Account Type: " + AcctType);
//            // Handle unexpected values
//        }
//        
//    
//    }
//
}
