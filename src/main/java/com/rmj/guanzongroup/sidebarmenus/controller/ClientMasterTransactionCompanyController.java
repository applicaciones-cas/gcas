/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

//import com.rmj.guanzongroup.cas.maven.model.ModelInstitutionalContactPerson;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelAddress;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelEmail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInstitutionalContactPerson;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelMobile;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSocialMedia;
import com.rmj.guanzongroup.sidebarmenus.table.model.SharedModel;
import com.sun.javafx.scene.control.skin.TableHeaderRow;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.cas.client.Client_Master;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class ClientMasterTransactionCompanyController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Client Transactions Company";
    private GRiderCAS oApp;
    private Client_Master oTrans;
//    private JSONObject poJSON;
    private int pnEditMode;
    private ObservableList<ModelInstitutionalContactPerson> contact_data = FXCollections.observableArrayList();
    private int pnContact = 0;
    private String oTransnox = "";

    private boolean state = false;
    private boolean pbLoaded = false;
    private SharedModel sharedModel;

    public void initModel(SharedModel sharedModel) {
        this.sharedModel = sharedModel;
    }
    @FXML
    AnchorPane AnchorMain;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnClose;

    @FXML
    private TextField txtField01;

    @FXML
    private TextField txtField02;

    @FXML
    private TextField txtField03;

    @FXML
    private TextField cmpnyInfo02;

    @FXML
    private TextField cmpnyInfo03;

    @FXML
    private TextField cmpnyInfo05;

    @FXML
    private TextField cmpnyInfo06;

    @FXML
    private TextField cmpnyInfo01;

    @FXML
    private TableView tblContact;

    @FXML
    private TableColumn indexContact01;

    @FXML
    private TableColumn indexContact02;

    @FXML
    private TableColumn indexContact03;

    @FXML
    private TableColumn indexContact04;

    @FXML
    private TableColumn indexContact05;

    @FXML
    private TableColumn indexContact06;

    @FXML
    private TableColumn indexContact07;

    @FXML
    private TextField txtContact01;

    @FXML
    private TextField txtContact02;

    @FXML
    private TextField txtContact03;

    @FXML
    private TextField txtContact06;

    @FXML
    private TextArea txtContact10;

    @FXML
    private Button btnAddInsContact, btnDelInsContact;

    @FXML
    private CheckBox cbContact01;

    @FXML
    private CheckBox cbContact02;

    @FXML
    private TextField txtContact07;

    @FXML
    private TextField txtContact08;

    @FXML
    private TextField txtContact04;

    @FXML
    private TextField txtContact05;

    @FXML
    private TextField txtContact09;

    @FXML
    private TextField cmpnyInfo07;

    @FXML
    private TextField cmpnyInfo08;

    @FXML
    private TextField cmpnyInfo09;

    private ObservableList<ModelMobile> data = FXCollections.observableArrayList();
    private ObservableList<ModelEmail> email_data = FXCollections.observableArrayList();
    private ObservableList<ModelSocialMedia> social_data = FXCollections.observableArrayList();
    private ObservableList<ModelAddress> address_data = FXCollections.observableArrayList();

    ObservableList<String> mobileType = FXCollections.observableArrayList("Mobile No", "Tel No", "Fax No");
    ObservableList<String> mobileOwn = FXCollections.observableArrayList("Personal", "Office", "Others");
    ObservableList<String> EmailOwn = FXCollections.observableArrayList("Personal", "Office", "Others");
    ObservableList<String> socialTyp = FXCollections.observableArrayList("Facebook", "Instagram", "Twitter");
    private int pnMobile = 0;
    private int pnEmail = 0;
    private int pnSocMed = 0;
    private int pnAddress = 0;
    public String lsCompanyName = "";

    /**
     * Initializes the controller class.
     */
    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
    }

    @Override
    public void setCompanyID(String fsValue) {
    }

    @Override
    public void setCategoryID(String fsValue) {
    }

    public void setTransaction(String fsValue) {
        oTransnox = fsValue;
    }

    public void setCompanyName(String fsValue) {
        lsCompanyName = fsValue;
    }
    private Object parentController;
    private unloadForm loadform;

    // Method to set the parent controller
    public void setParentController(Object parentController) {
        System.out.println("parentController = " + parentController);
        loadform = new unloadForm();
        loadform.setParentController(parentController);
        this.parentController = parentController;
    }

    public void setState(boolean fsValue) {
        state = fsValue;
    }

    /**
     * ********************************
     */
    /*Initializes the controller class.*/
    /**
     * ********************************
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            if (oTransnox == null || oTransnox.isEmpty()) { // Check if oTransnox is null or empty
                pnEditMode = EditMode.ADDNEW;
            }
//        ClickButton();

// Initialize the Client_Master transaction
//        oTrans = new Client_Master(oApp, false, oApp.getBranchCode());
// Call newRecord to initialize a new record
            oTrans.newRecord();

// Access sClientID directly from the jsonResult and set it to txtField01
            String sClientID = (String) oTrans.getMaster("sClientID");
            if (txtField01 != null) { // Check if txtField01 is not null before setting its text
                txtField01.setText(sClientID);
            } else {
                // Handle the case where txtField01 is null
                System.out.println("txtField01 is null");
            }
            initcompny();
            initTextFields();
            InitContctPersonInfo();
//        initAddressInfo();
//
//        loadContctPerson();
//        initContctPersonGrid();
            cmpnyInfo01.setText(lsCompanyName);
            txtField02.setText(lsCompanyName);
//        oTrans.getModel().setFullName(lsCompanyName);
//        cmpnyInfo01.requestFocus();
//        oTrans.setType(ValidatorFactory.ClientTypes.COMPANY);
            pbLoaded = true;
        } catch (SQLException ex) {
            Logger.getLogger(ClientMasterTransactionCompanyController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(ClientMasterTransactionCompanyController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * *********************
     */
    /*initialize text fields*/
    /**
     * *********************
     */
    private void initcompny() {
        /*company FOCUSED PROPERTY*/
        cmpnyInfo01.focusedProperty().addListener(cmpny_Focus);
        cmpnyInfo07.focusedProperty().addListener(cmpny_Focus);
        cmpnyInfo08.focusedProperty().addListener(cmpny_Focus);
        cmpnyInfo09.focusedProperty().addListener(cmpny_Focus);
    }

    /**
     * *****************************************
     */
    /*initialize value to data                  */
 /*serves also as lost focus FOR company info*/
    /**
     * *****************************************
     */
    final ChangeListener<? super Boolean> cmpny_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField cmpnyInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(cmpnyInfo.getId().substring(9, 11));
        String lsValue = cmpnyInfo.getText();
        JSONObject jsonObject = new JSONObject();
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lnIndex) {
                case 1:
                    /*company name*/
                    oTrans.setMaster(8, lsValue);
                    break;
                case 7:/*tin id*/
                    oTrans.setMaster(16, lsValue);
                    break;
                case 8:/*lto id*/
                    oTrans.setMaster(17, lsValue);
                    break;
                case 9:/*business id*/
                    oTrans.setMaster(18, lsValue);
                    break;
            }
            txtField02.setText(cmpnyInfo01.getText());
            oTrans.setMaster(12, "0000-00-00");
            //            loadAddress();
        } else {
            cmpnyInfo.selectAll();
        }
    };

    /**
     * *********************
     */
    /*initialize text fields*/
    /**
     * *********************
     */
    private void initTextFields() {
        /*textFields FOCUSED PROPERTY*/
        txtField01.focusedProperty().addListener(txtField_Focus);
    }

    /**
     * *****************************************
     */
    /*initialize value to data                  */
 /*serves also as lost focus FOR company info*/
    /**
     * *****************************************
     */
    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        if (!nv) {
            /*Lost Focus*/
            switch (lnIndex) {
                case 1:
                    //                    jsonObject = oTrans.setMaster(1, lsValue);
                    if ("error".equalsIgnoreCase((String) jsonObject.get("result"))) {
                    }
                    break;
                case 2:
                    jsonObject = oTrans.setMaster(2, "0");
                    if ("error".equals((String) jsonObject.get("result"))) {
                        System.err.println((String) jsonObject.get("message"));
                        System.exit(1);
                    }
                    break;
            }
        } else {
            txtField.selectAll();
        }
    };

    /**
     * ****************************
     */
    /*initialize contactinfo fields*/
    /**
     * ****************************
     */
    private void InitContctPersonInfo() {
//        /*MOBILE INFO FOCUSED PROPERTY*/
//        txtContact01.focusedProperty().addListener(contactinfo_Focus);
//        txtContact02.focusedProperty().addListener(contactinfo_Focus);
//        txtContact03.focusedProperty().addListener(contactinfo_Focus);
//        txtContact04.focusedProperty().addListener(contactinfo_Focus);
//        txtContact05.focusedProperty().addListener(contactinfo_Focus);
//        txtContact06.focusedProperty().addListener(contactinfo_Focus);
//        txtContact07.focusedProperty().addListener(contactinfo_Focus);
//        txtContact08.focusedProperty().addListener(contactinfo_Focus);
//        txtContact09.focusedProperty().addListener(contactinfo_Focus);
//        txtContact10.focusedProperty().addListener(contactinfoTextArea_Focus);
//    }

        /**
         * ****************************************
         */
        /*initialize value to data                 */
 /*serves also as lost focus FOR contactinfo*/
        /**
         * ****************************************
         */
//        final ChangeListener<? super Boolean> contactinfo_Focus = (o, ov, nv) -> {
//            if (!pbLoaded) {
//                return;
//            }
//            TextField txtContact = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
//            int lnIndex = Integer.parseInt(txtContact.getId().substring(10, 12));
//            String lsValue = (txtContact.getText() == null ?"": txtContact.getText());
//            JSONObject jsonObject = new JSONObject();
//            if (lsValue == null) {
//                return;
//            }
//            if (!nv) {
//                /*Lost Focus*/
//                switch (lnIndex) {
//                    case 1:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, 3, lsValue);
//                        break;
//                    case 2:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, "sCPPosit1", lsValue);
//                        break;
//                    case 3:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, "sAccount1", lsValue);
//                        break;
//                    case 4:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, "sAccount2", lsValue);
//                        break;
//                    case 5:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, "sAccount3", lsValue);
//                        break;
//                    case 6:
//                        /*company name*/
//                         if (lsValue.matches("\\d{11}")) {
//                            oTrans.setInsContact(pnContact, "sMobileNo", lsValue);
//                        } else {
//                             ShowMessageFX.OkayCancel(null, pxeModuleName, "Contact number must be exactly 11 digits.");
//                            txtContact.requestFocus();
//                            break;
//                        }
//                        oTrans.setInsContact(pnContact, "sMobileNo", lsValue);
//                        break;
//                    case 7:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, "sTelNoxxx", lsValue);
//                        break;
//                    case 8:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, "sFaxNoxxx", lsValue);
//                        break;
//                    case 9:
//                        /*company name*/
//                        oTrans.setInsContact(pnContact, "sEMailAdd", lsValue);
//                        break;
//                }
//                loadContctPerson();
//            } else {
//                txtContact.selectAll();
//            }
//        };
//
//    /***************************************************/
//    /*initialize value to data                         */
//    /*serves also as lost focus FOR contactinfotextarea*/
//    /***************************************************/
//        final ChangeListener<? super Boolean> contactinfoTextArea_Focus = (o, ov, nv) -> {
//            if (!pbLoaded) {
//                return;
//            }
//
//            TextArea txtContact = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
//            int lnIndex = Integer.parseInt(txtContact.getId().substring(10, 12));
//            String lsValue = (txtContact.getText() == null ?"": txtContact.getText());
//            JSONObject jsonObject = new JSONObject();
//            if (lsValue == null) {
//                return;
//            }
//            if (!nv) {
//                /*Lost Focus*/
//                switch (lnIndex) {
//                    case 10:/*company name*/
//                        oTrans.setInsContact(pnContact, "sRemarksx", lsValue);
//                        break;
//                }
//                loadContctPerson();
//            } else {
//                txtContact.selectAll();
//            }
//        };
//
//    /***************************/
//    /*initialize address fields*/
//    /***************************/
//    private void initAddressInfo() {
//        /*Address FOCUSED PROPERTY*/
//        cmpnyInfo01.focusedProperty().addListener(address_Focus);
//        cmpnyInfo02.focusedProperty().addListener(address_Focus);
//        cmpnyInfo03.focusedProperty().addListener(address_Focus);
//
//        /*addressin key */
//        cmpnyInfo05.setOnKeyPressed(this::companyinfo_KeyPressed);
//        cmpnyInfo06.setOnKeyPressed(this::companyinfo_KeyPressed);
//
//    }
//
//    /*******************************************/
//    /*initialize value to data                 */
//    /*serves also as lost focus FOR addressinfo*/
//    /*******************************************/
//        final ChangeListener<? super Boolean> address_Focus = (o, ov, nv) -> {
//            if (!pbLoaded) {
//                return;
//            }
//
//            TextField cmpnyInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
//            int lnIndex = Integer.parseInt(cmpnyInfo.getId().substring(9, 11));
//            String lsValue = cmpnyInfo.getText();
//            JSONObject jsonObject = new JSONObject();
//            if (lsValue == null) {
//                return;
//            }
//            if (!nv) {
//                /*Lost Focus*/
//                switch (lnIndex) {
//                    case 2:
//                        /*houseno*/
//                        oTrans.setAddress(pnAddress, 3, lsValue);
//                        break;
//                    case 3:/*address*/
//                        oTrans.setAddress(pnAddress, 4, lsValue);
//                        break;
//                }
//                txtField03.setText( cmpnyInfo02.getText() + " " +  cmpnyInfo03.getText() + " " +  cmpnyInfo06.getText() + " " +  cmpnyInfo05.getText());
//            } else {
//                cmpnyInfo.selectAll();
//            }
//        };
//    /********************************************/
//    /*initialize keypress for companyinfo fields*/
//    /********************************************/
//        private void companyinfo_KeyPressed(KeyEvent event) {
//        TextField cmpnyInfo = (TextField) event.getSource();
//        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(9, 11));
//        String lsValue = (cmpnyInfo.getText()==null?"":cmpnyInfo.getText());
//        JSONObject poJson;
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex) {
//                    case 5:
//                        /*search town*/
//                        poJson = new JSONObject();
//                        poJson = oTrans.SearchTownAddress(pnAddress, lsValue, false);
//                        System.out.println("poJson = " + poJson.toJSONString());
//                        if ("error".equalsIgnoreCase(poJson.get("result").toString())) {
//                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                            cmpnyInfo05.clear();
//                        }
//                        cmpnyInfo05.setText((String) poJson.get("sTownName") + ", " + (String) poJson.get("sProvName"));
//                        break;
//                    case 6:
//                        /*search barnagay*/
//                        poJson = new JSONObject();
//                        poJson = oTrans.SearchBarangayAddress(pnAddress, lsValue, false);
//                        System.out.println("poJson = " + poJson.toJSONString());
//                        if ("error".equalsIgnoreCase(poJson.get("result").toString())) {
//                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                            cmpnyInfo06.clear();
//                        }
//                        cmpnyInfo06.setText((String) poJson.get("sBrgyName"));
//                        break;
//                }
//                txtField03.setText( cmpnyInfo02.getText() + " " +  cmpnyInfo03.getText() + " " +  cmpnyInfo06.getText() + " " +  cmpnyInfo05.getText());
//            case ENTER:
//        }
//            switch (event.getCode()) {
//                case ENTER:
//                    CommonUtils.SetNextFocus(cmpnyInfo);
//                case DOWN:
//                    CommonUtils.SetNextFocus(cmpnyInfo);
//                    break;
//                case UP:
//                    CommonUtils.SetPreviousFocus(cmpnyInfo);
//            }
//        }
//    /***********************************/
//    /*initialize loadcontactperson data*/
//    /***********************************/
//    private void loadContctPerson() {
//        contact_data.clear();
//        for (int lnCtr = 0; lnCtr < oTrans.getInsContactList().size(); lnCtr++) {
//            contact_data.add(new ModelInstitutionalContactPerson(String.valueOf(lnCtr + 1),
//                    oTrans.getInsContact(lnCtr, "sCPerson1").toString(),
//                    oTrans.getInsContact(lnCtr, "sCPPosit1").toString(),
//                    oTrans.getInsContact(lnCtr, "sMobileNo").toString(),
//                    oTrans.getInsContact(lnCtr, "sTelNoxxx").toString(),
//                    oTrans.getInsContact(lnCtr, "sFaxNoxxx").toString(),
//                    oTrans.getInsContact(lnCtr, "sEMailAdd").toString(),
//                    oTrans.getInsContact(lnCtr, "sAccount1").toString(),
//                    oTrans.getInsContact(lnCtr, "sAccount2").toString(),
//                    oTrans.getInsContact(lnCtr, "sAccount3").toString(),
//                    oTrans.getInsContact(lnCtr, "sRemarksx").toString(),
//                    oTrans.getInsContact(lnCtr, 13).toString(),
//                    oTrans.getInsContact(lnCtr, "cRecdStat").toString()));
//        }
//    }
//
//    /***********************************/
//    /*initialize loadcontactperson grid*/
//    /***********************************/
//    private void initContctPersonGrid() {
//        indexContact01.setStyle("-fx-alignment: CENTER;");
//        indexContact02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        indexContact03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        indexContact04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        indexContact05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        indexContact06.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//        indexContact07.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
//
//        indexContact01.setCellValueFactory(new PropertyValueFactory<>("index01"));
//        indexContact02.setCellValueFactory(new PropertyValueFactory<>("index02"));
//        indexContact03.setCellValueFactory(new PropertyValueFactory<>("index03"));
//        indexContact04.setCellValueFactory(new PropertyValueFactory<>("index04"));
//        indexContact05.setCellValueFactory(new PropertyValueFactory<>("index05"));
//        indexContact06.setCellValueFactory(new PropertyValueFactory<>("index06"));
//        indexContact07.setCellValueFactory(new PropertyValueFactory<>("index11"));
//        tblContact.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
//            TableHeaderRow header = (TableHeaderRow) tblContact.lookup("TableHeaderRow");
//            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
//                header.setReordering(false);
//            });
//        });
//        tblContact.setItems(contact_data);
//        tblContact.getSelectionModel().select(pnContact + 1);
//        tblContact.autosize();
//
//        getContactSelectedItem();
//    }
//
//    /************************/
//    /*initialize clickbutton*/
//    /************************/
//    private void ClickButton() {
//        btnSave.setOnAction(this::handleButtonAction);
//        btnClose.setOnAction(this::handleButtonAction);
//        btnAddInsContact.setOnAction(this::handleButtonAction);
//        btnDelInsContact.setOnAction(this::handleButtonAction);
//    }
//
//    /*************************************/
//    /*initialize handlebuttonaction event*/
//    /*************************************/
//    private void handleButtonAction(ActionEvent event) {
//        Object source = event.getSource();
//        if (source instanceof Button) {
//            Button clickedButton = (Button) source;
//            unloadForm appUnload = new unloadForm();
//            switch (clickedButton.getId()) {
//                case "btnClose":
//
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
////                            clearAllFields();
//                        pnEditMode = EditMode.UNKNOWN;
//                        appUnload.unloadForm(AnchorMain, oApp, "Client Transactions Company");
//                    }
//                    break;
//                case "btnSave":
//                    oTrans.setAddress(pnAddress, "cPrimaryx", Logical.YES);
//
//                    oTrans.getModel().setClientType("0");
//                    JSONObject saveResult = oTrans.saveRecord();
//                    if ("success".equals((String) saveResult.get("result"))) {
//                        System.err.println((String) saveResult.get("message"));
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                        String dataToSend = cmpnyInfo01.getText();
//
//                        System.out.println("Record saved successfully.");
//                        if(parentController != null){
//                            loadform.useParentController(GlobalVariables.sClientID);
//                        }
//                        appUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
//
//                    } else {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                        System.out.println("Record not saved successfully.");
//                        System.out.println((String) saveResult.get("message"));
//                    }
//
//                    break;
//                case "btnAddInsContact":
//                    JSONObject addInsContct = oTrans.addInsContact();
//
//                    System.out.println((String) addInsContct.get("message"));
//                    if ("error".equals((String) addInsContct.get("result"))) {
//                        ShowMessageFX.Information((String) addInsContct.get("message"), "Computerized Acounting System", pxeModuleName);
//                        break;
//                    }
//
//                    clearContactperson();
//                    pnContact = oTrans.getInsContactList().size() - 1;
//
//                    loadContctPerson();
//                    tblContact.getSelectionModel().select(pnContact + 1);
//                    break;
//                case "btnDelInsContact":
//                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to remove ?") == true){
//                        oTrans.getInsContactList().remove(pnContact);
//                        if(oTrans.getInsContactList().size() <= 0){
//                            oTrans.addInsContact();
//                        }
//
//                        pnContact = oTrans.getInsContactList().size()-1;
//                        loadContctPerson();
//                        clearContactperson();
//                    }
//                    break;
//            }
//        }
//    }
//
//    /******************************************/
//    /*initialize clear contactperson textfield*/
//    /******************************************/
//    private void clearContactperson() {
//        txtContact01.clear();
//        txtContact02.clear();
//        txtContact03.clear();
//        txtContact04.clear();
//        txtContact05.clear();
//        txtContact06.clear();
//        txtContact07.clear();
//        txtContact08.clear();
//        txtContact09.clear();
//        txtContact10.clear();
//
//        cbContact01.setSelected(false);
//        cbContact02.setSelected(false);
////        txtContact01.requestFocus();
//    }
//    /***********************************/
//    /*initialize tblcontact click event*/
//    /***********************************/
//    @FXML
//    private void tblContact_Clicked(MouseEvent event) {
//        pnContact = tblContact.getSelectionModel().getSelectedIndex();
//        System.out.println("pnContact = " + pnContact);
//        getContactSelectedItem();
//    }
//    /**********************************************************/
//    /*initialize txtfield to load data after table click event*/
//    /**********************************************************/
//    private void getContactSelectedItem() {
//        txtContact01.setText(oTrans.getInsContact(pnContact, 3) == null || oTrans.getInsContact(pnContact, 3).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 3));
//        txtContact02.setText(oTrans.getInsContact(pnContact, 4) == null || oTrans.getInsContact(pnContact, 4).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 4));
//        txtContact03.setText(oTrans.getInsContact(pnContact, 9) == null || oTrans.getInsContact(pnContact, 9).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 9));
//        txtContact04.setText(oTrans.getInsContact(pnContact, 10) == null || oTrans.getInsContact(pnContact, 10).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 10));
//        txtContact05.setText(oTrans.getInsContact(pnContact, 11) == null || oTrans.getInsContact(pnContact, 11).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 11));
//        txtContact06.setText(oTrans.getInsContact(pnContact, 5) == null || oTrans.getInsContact(pnContact, 5).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 5));
//        txtContact07.setText(oTrans.getInsContact(pnContact, 6) == null || oTrans.getInsContact(pnContact, 6).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 6));
//        txtContact08.setText(oTrans.getInsContact(pnContact, 7) == null || oTrans.getInsContact(pnContact, 7).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 7));
//        txtContact09.setText(oTrans.getInsContact(pnContact, 8) == null || oTrans.getInsContact(pnContact, 8).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 8));
//        txtContact10.setText(oTrans.getInsContact(pnContact, 12) == null || oTrans.getInsContact(pnContact, 12).toString().isEmpty() ? "" : (String) oTrans.getInsContact(pnContact, 12));
//
//        if (cbContact01.isSelected()){
//            lblStatus.setText("ACTIVE");
//        }else{
//            lblStatus.setText("INACTIVE");
//        }
//
//        cbContact01.setSelected((!"0".equals(oTrans.getInsContact(pnContact, 14).toString())));
//        cbContact02.setSelected((oTrans.getInsContact(pnContact, 13).toString() == "0"?false:true));
//
//    }
//
//    /**********************************/
//    /*initialize cRecdStat click event*/
//    /**********************************/
//    @FXML
//    private void CheckContact01_Clicked(MouseEvent event) {
//        boolean isChecked = cbContact01.isSelected();
////        oTrans.setMobile(pnMobile, "cPrimaryx", (isChecked)? "1":"0");
//
//        for (int lnCtr = 0; lnCtr < oTrans.getInsContactList().size(); lnCtr++){
//            if(pnContact == lnCtr){
//                if(isChecked){
//                    oTrans.setInsContact(pnContact, "cRecdStat", "1");
//                }else{
//                    oTrans.setInsContact(lnCtr, "cRecdStat", "0");
//                }
//            }else{
//                oTrans.setInsContact(lnCtr, "cRecdStat", "0");
//            }
//
//        }
//        loadContctPerson();
//    }
//
//    /**********************************/
//    /*initialize cRecdStat click event*/
//    /**********************************/
//    @FXML
//    private void CheckContact02_Clicked(MouseEvent event) {
//        boolean isChecked = cbContact02.isSelected();
//        for (int lnCtr = 0; lnCtr < oTrans.getInsContactList().size(); lnCtr++){
//            if(pnContact == lnCtr){
//                if(isChecked){
//                    oTrans.setInsContact(pnContact, "cPrimaryx", "1");
//                }else{
//                    oTrans.setInsContact(lnCtr, "cPrimaryx", "0");
//                }
//            }else{
//                oTrans.setInsContact(lnCtr, "cPrimaryx", "0");
//            }
//        }
//        loadContctPerson();
//    }
//    void loadReturn(String lsValue) {
//        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
//    }
    }
}
