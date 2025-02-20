/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelAddress;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelEmail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInstitutionalContactPerson;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelMobile;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSocialMedia;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
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
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.client.Client;
import org.guanzon.cas.client.Client_Master;
import org.guanzon.cas.parameter.services.ParamControllers;

import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class ClientMasterParameterController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Client";
    private GRider oApp;
    private Client oTrans;
    private Client poTrans;
    private int pnEditMode;

    private String oTransnox = "";

    private boolean state = false;
    private boolean pbLoaded = false;

    @FXML
    private AnchorPane AnchorMain, anchorPersonal, anchorAddress, anchorMobile, anchorEmail,
            anchorSocial, anchorContctPerson, anchorOtherInfo;

    @FXML
    private HBox hbButtons;

    @FXML
    private TabPane tabpane01;

    @FXML
    private Tab tabIndex01, tabIndex02, tabIndex03, tabIndex04, tabIndex05, tabIndex06, tabIndex07;

    @FXML
    private TableView tblAddress, tblMobile, tblEmail, tblSocMed, tblContact;

    @FXML
    private Label lblAddressStat, lblMobileStat, lblEmailStat, lblSocMedStat, lblContactPersonStat;

    @FXML
    private Text lblAddressType;

    @FXML
    private DatePicker txtField07, personalinfo07;

    @FXML
    private ComboBox cmbField01, txtField12, txtField13, personalinfo09, personalinfo10,
            cmbMobile02, cmbMobile01, cmbEmail01, cmbSocMed01, cmbSearch;

    @FXML
    private TextField txtField01, txtField02, txtField03, txtField04, txtField05, txtField06,
                txtField08, txtField09, txtField10, txtField11, personalinfo01, personalinfo02,
                personalinfo03, personalinfo04, personalinfo05, personalinfo06, personalinfo08,
                personalinfo11, personalinfo12, AddressField01, AddressField02, AddressField03,
                AddressField04, AddressField05, AddressField06, txtMobile01, mailFields01,
                txtSocial01, txtContact01, txtContact02, txtContact03, txtContact04, txtContact05,
                txtContact06, txtContact07, txtContact08, txtContact09, personalinfo13,
                personalinfo14, personalinfo15, txtSeeks99;

    @FXML
    private TextArea txtSocial02, txtContact10;

    @FXML
    private Button btnBrowse, btnNew, btnSave, btnUpdate, btnSearch, btnCancel, btnClose,
            btnAddAddress, btnDelAddress, btnAddMobile, btnDelMobile, btnAddEmail, btnDelEmail,
            btnAddSocMed, btnDelSocMed, btnAddInsContact, btnDelContPerson;

    @FXML
    private CheckBox cbAddress01, cbAddress02, cbAddress03, cbAddress04, cbAddress05, cbAddress06,
            cbAddress07, cbAddress08, cbMobileNo01, cbMobileNo02, cbEmail01, cbEmail02,
            cbSocMed01, cbContact01, cbContact02;

    @FXML
    private TableColumn indexAddress01, indexAddress02, indexAddress03, indexAddress04, indexAddress05,
            indexMobileNo01, indexMobileNo02, indexMobileNo03, indexMobileNo04,
            indexEmail01, indexEmail02, indexEmail03, indexSocMed01, indexSocMed02,
            indexSocMed03, indexSocMed04, indexContact01, indexContact02, indexContact03,
            indexContact04, indexContact05, indexContact06, indexContact07;

    private ObservableList<ModelMobile> data = FXCollections.observableArrayList();
    private ObservableList<ModelEmail> email_data = FXCollections.observableArrayList();
    private ObservableList<ModelSocialMedia> social_data = FXCollections.observableArrayList();
    private ObservableList<ModelAddress> address_data = FXCollections.observableArrayList();
    private ObservableList<ModelInstitutionalContactPerson> contact_data = FXCollections.observableArrayList();

    ObservableList<String> mobileType = FXCollections.observableArrayList("Mobile No", "Tel No", "Fax No");
    ObservableList<String> mobileOwn = FXCollections.observableArrayList("Personal", "Office", "Others");
    ObservableList<String> EmailOwn = FXCollections.observableArrayList("Personal", "Office", "Others");
    ObservableList<String> socialTyp = FXCollections.observableArrayList("Facebook", "Instagram", "Twitter");

    // Create a list of genders
    ObservableList<String> genders = FXCollections.observableArrayList(
            "Male",
            "Female",
            "Other"
    );
    // Create a list of civilStatuses    
    ObservableList<String> civilStatuses = FXCollections.observableArrayList(
            "Single",
            "Married",
            "Divorced",
            "Widowed"
    );

    // Create a list of clientType    
    ObservableList<String> clientType = FXCollections.observableArrayList(
            "Company",
            "Individual"
    );

    private int pnMobile = 0;
    private int pnEmail = 0;
    private int pnSocMed = 0;
    private int pnAddress = 0;
    private int pnContact = 0;

    /**
     * Initializes the controller class.
     */
    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;

    }

    public void setTransaction(String fsValue) {
        oTransnox = fsValue;
    }

    public void setState(boolean fsValue) {
        state = fsValue;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        pnEditMode = EditMode.UNKNOWN;
        initializeObject();
        initComboBoxes();
        ClickButton();
        initClientType();
        
        pbLoaded = true;

    }
    private void initializeObject() {
        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
        oTrans = new Client(oApp, "",logwrapr);
        oTrans.Master().setClientType("0");
        poTrans = new Client(oApp, "",logwrapr);
        poTrans.Master().setRecordStatus("0123");
        oTrans.Master().setRecordStatus("0123");
    }
    
    
    void loadReturn(String lsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    private void ClickButton() {
        btnBrowse.setOnAction(this::handleButtonAction);
        btnCancel.setOnAction(this::handleButtonAction);
        btnNew.setOnAction(this::handleButtonAction);
        btnSave.setOnAction(this::handleButtonAction);
        btnUpdate.setOnAction(this::handleButtonAction);
        btnAddMobile.setOnAction(this::handleButtonAction);
        btnAddSocMed.setOnAction(this::handleButtonAction);      
        btnAddAddress.setOnAction(this::handleButtonAction);     
        btnAddEmail.setOnAction(this::handleButtonAction);        
        btnAddInsContact.setOnAction(this::handleButtonAction);
        btnClose.setOnAction(this::handleButtonAction);
        
        btnDelAddress.setOnAction(this::handleButtonAction);     
        btnDelMobile.setOnAction(this::handleButtonAction);        
        btnDelEmail.setOnAction(this::handleButtonAction);
        btnDelSocMed.setOnAction(this::handleButtonAction);
        btnDelContPerson.setOnAction(this::handleButtonAction);
    }
    private void handleButtonAction(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            unloadForm appUnload = new unloadForm();
            JSONObject poJSON;
            poJSON = new JSONObject();
            switch (clickedButton.getId()) {
                case "btnClose":
                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
                        appUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
                    }
                    break;
                case "btnBrowse":
                    String lsValue = (txtSeeks99.getText() == null) ? "" : txtSeeks99.getText();
                    poJSON = oTrans.Master().searchRecord(lsValue, false);
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
                        txtSeeks99.clear();
                        break;
                    }
                    pnEditMode = EditMode.READY;
                    
                    LoadRecord();
                    
//                    initTabAnchor();
                    break;
                case "btnCancel":
                    break;
                case "btnNew":
                    break;
                case "btnSave":
                    break;
                case "btnUpdate":
                    break;
                    
                    
                /*ADD*/
                case "btnAddMobile":
                    break;
                case "btnAddSocMed":
                    break;
                case "btnAddAddress":
                    break;
                case "btnAddEmail":
                    break;
                case "btnAddInsContact":
                    break;
                    
                /*DELETE*/
                case "btnDelMobile":
                    break;
                case "btnDelSocMed":
                    break;
                case "btnDelAddress":
                    break;
                case "btnDelEmail":
                    break;
                case "btnDelInsContact":
                    break;    
                    
                    
                    
                    
//                    
////                    clearAllFields();
////                    txtField02.requestFocus();
////                    JSONObject poJSON = oParameters.Category().newRecord();
////                    pnEditMode = EditMode.READY;
////                    if ("success".equals((String) poJSON.get("result"))) {
////                        pnEditMode = EditMode.ADDNEW;
////                        initButton(pnEditMode);
////                        initTabAnchor();
////                        loadRecord();
////                    } else {
////                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
////                        initTabAnchor();
////                    }
//                    break;
//                case "btnBrowse":
////                    String lsValue = (txtSeeks01.getText() == null) ? "" : txtSeeks01.getText();
////                    poJSON = oParameters.Category().searchRecord(lsValue, false);
////                    if ("error".equals((String) poJSON.get("result"))) {
////                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
////                        txtSeeks01.clear();
////                        break;
////                    }
////                    pnEditMode = EditMode.READY;
////                    loadRecord();
////                    initTabAnchor();
//                    break;
//                case "btnUpdate":
////                    poJSON = oParameters.Category().updateRecord();
////                    if ("error".equals((String) poJSON.get("result"))) {
////                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
////                        break;
////                    }
////                    pnEditMode = oParameters.Category().getEditMode();
////                    initButton(pnEditMode);
////                    initTabAnchor();
//                    break;
//                case "btnCancel":
////                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
////                        clearAllFields();
////                        initializeObject();
////                        pnEditMode = EditMode.UNKNOWN;
////                        initButton(pnEditMode);
////                        initTabAnchor();
////                    }
//                    break;
//                case "btnSave":
////                    oParameters.Category().getModel().setModifyingId(oApp.getUserID());
////                    oParameters.Category().getModel().setModifiedDate(oApp.getServerDate());
////                    JSONObject saveResult = oParameters.Category().saveRecord();
////                    if ("success".equals((String) saveResult.get("result"))) {
////                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
////                        pnEditMode = EditMode.UNKNOWN;
////                        initButton(pnEditMode);
////                        clearAllFields();
////                    } else {
////                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
////                    }
//                    break;
//                case "btnActivate":
////                    String Status = oParameters.Category().getModel().getRecordStatus();
////                    JSONObject poJsON;
////
////                    switch (Status) {
////                        case "0":
////                            if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to Activate this Parameter?") == true) {
////                                poJsON = oParameters.Category().postTransaction();
////                                ShowMessageFX.Information((String) poJsON.get("message"), "Computerized Accounting System", pxeModuleName);
////                                loadRecord();
////                            }
////                            break;
////                        case "1":
////                            if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to Deactivate this Parameter?") == true) {
////                                poJsON = oParameters.Category().voidTransaction();
////                                ShowMessageFX.Information((String) poJsON.get("message"), "Computerized Accounting System", pxeModuleName);
////                                loadRecord();
////                            }
////                            break;
////                        default:
////
////                            break;
////
////                    }
//                    break;
            }
        }
    }
    private void LoadRecord(){
        MasterRecord();
        oTrans.OpenClientAddress(oTrans.Master().getModel().getClientId());
        AddressRecord();
//        loadAddress();
//        loadMobile();
//        loadEmail();
//        loadContctPerson();
//        initContctPersonGrid();
//        initMobileGrid();
//        initEmailGrid();
//        initAddressGrid();
//        loadSocialMedia();
//        initSocialMediaGrid();
//        
//        retrieveDetails();
//        personalinfo07.setValue(LocalDate.now());
    }
    private void MasterRecord(){
        if(pnEditMode == EditMode.READY || 
                pnEditMode == EditMode.ADDNEW || 
                pnEditMode == EditMode.UPDATE){
            personalinfo01.setText((String) oTrans.Master().getModel().getCompanyName());
            personalinfo02.setText((String) oTrans.Master().getModel().getLastName());
            personalinfo03.setText((String) oTrans.Master().getModel().getFirstName());
            personalinfo04.setText((String) oTrans.Master().getModel().getMiddleName());
            personalinfo05.setText((String) oTrans.Master().getModel().getSuffixName());
            personalinfo12.setText((String) oTrans.Master().getModel().getMothersMaidenName());
            personalinfo13.setText((String) oTrans.Master().getModel().getTaxIdNumber());
            personalinfo14.setText((String) oTrans.Master().getModel().getLTOClientId());
            personalinfo15.setText((String) oTrans.Master().getModel().getPhNationalId());
            personalinfo06.setText((String) oTrans.Master().getModel().Citizenship().getNationality());
            personalinfo08.setText((String) oTrans.Master().getModel().BirthTown().getTownName());
            
            System.out.println("SPOUSE == " + oTrans.Master().getModel().getSpouseId());
//            personalinfo11.setText((oTrans.Master().getModel().getSpouseId() == null) ? "" : oTrans.Master().getModel().getSpouseId());
            JSONObject poJSON;
            poJSON = new JSONObject();
            poJSON = poTrans.Master().searchRecordSpouse(oTrans.Master().getModel().getSpouseId(), true);
            if ("success".equals((String) poJSON.get("result"))) {
                personalinfo11.setText((poTrans.Master().getModel().getCompanyName()== null) ? "" : poTrans.Master().getModel().getCompanyName());
            }
            
            if(oTrans.Master().getModel().getGender() != null && !oTrans.Master().getModel().getGender().trim().isEmpty()){
                personalinfo09.getSelectionModel().select(Integer.parseInt((String) oTrans.Master().getModel().getGender()));
                txtField13.getSelectionModel().select(personalinfo09.getSelectionModel().getSelectedIndex());
            }
            
            if(oTrans.Master().getModel().getCivilStatus() != null && !oTrans.Master().getModel().getCivilStatus().trim().isEmpty()){
                personalinfo10.getSelectionModel().select(Integer.parseInt((String) oTrans.Master().getModel().getCivilStatus()));
                txtField12.getSelectionModel().select(personalinfo10.getSelectionModel().getSelectedIndex());
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Parse the formatted date string into a LocalDate object
            if(oTrans.Master().getModel().getBirthDate() != null && !oTrans.Master().getModel().getBirthDate().toString().trim().isEmpty()){
                LocalDate localbdate = LocalDate.parse(oTrans.Master().getModel().getBirthDate().toString(), formatter);

                // Set the value of the DatePicker to the parsed LocalDate
                personalinfo07.setValue(localbdate);
                txtField07.setValue(localbdate);
            }

            txtField01.setText((String) oTrans.Master().getModel().getClientId());
            txtField02.setText((String) oTrans.Master().getModel().getCompanyName());
//            txtField06.setText((oTrans.Master().getModel().Spouse().getCompanyName() == null)? "" : oTrans.Master().getModel().Spouse().getCompanyName());
            txtField05.setText((String) oTrans.Master().getModel().getMothersMaidenName());
            txtField09.setText((String) oTrans.Master().getModel().Citizenship().getNationality());
            txtField08.setText((String) oTrans.Master().getModel().BirthTown().getTownName());
            
            if(!oTrans.Master().getModel().getClientType().trim().isEmpty() && oTrans.Master().getModel().getClientType()!= null){
                cmbField01.getSelectionModel().select(Integer.parseInt((String) oTrans.Master().getModel().getClientType()));
            }
            
//            if(!address_data.isEmpty()){
//                for(int lnctr = 0; lnctr < oTrans.getAddressList().size(); lnctr++){    
//                    if(oTrans.getAddress(lnctr, "cPrimaryx").equals("1")){
//                        String lsAddress = oTrans.getAddress(lnctr).getHouseNo() + " " + oTrans.getAddress(lnctr).getAddress() +
//                                " " + (String) oTrans.getAddress(lnctr, 21) + ", " + (String)  oTrans.getAddress(lnctr, 20)+ ", " + (String)  oTrans.getAddress(lnctr, 22);
//                        txtField03.setText(lsAddress);
//                    }
//                }
//            }
            
//            if(!data.isEmpty()){
//                for(int lnctr = 0; lnctr < oTrans.getMobileList().size(); lnctr++){
//                    if(oTrans.getMobile(lnctr, "cPrimaryx").equals("1")){
//                        txtField10.setText((String) oTrans.getMobile(lnctr, "sMobileNo"));
//                    }
//                }
//            }
//            
//            if(!email_data.isEmpty()){
//                for(int lnctr = 0; lnctr < oTrans.getEmailList().size(); lnctr++){
//                    if(oTrans.getEmail(lnctr, "cPrimaryx").equals("1")){
//                        txtField11.setText((String) oTrans.getEmail(lnctr, "sEMailAdd"));
//                    }
//                }
//            }
//            
//            if(!contact_data.isEmpty()){
//                for(int lnctr = 0; lnctr < oTrans.getInsContactList().size(); lnctr++){
//                    if(oTrans.getInsContact(lnctr, "cPrimaryx").equals("1")){
//                        txtField04.setText((String) oTrans.getInsContact(lnctr, "sCPerson1"));
//                    }
//                }
//            }
        }
    }
    
        private void initComboBoxes(){
        // Set the items of the ComboBox to the list of genders
        personalinfo09.setItems(genders);
        personalinfo09.getSelectionModel().select(0);
        
        personalinfo09.setOnAction(event -> {
            oTrans.Master().getModel().setGender(String.valueOf(personalinfo09.getSelectionModel().getSelectedIndex()));
        });
  
        // Set the items of the ComboBox to the list of genders
        personalinfo10.setItems(civilStatuses);
        personalinfo10.getSelectionModel().select(0);
        
        personalinfo10.setOnAction(event -> {
            oTrans.Master().getModel().setCivilStatus(String.valueOf(personalinfo10.getSelectionModel().getSelectedIndex()));
        });
        
        txtField12.setItems(civilStatuses);
        txtField13.setItems(genders);
        
        txtField12.getSelectionModel().select(0);
        txtField13.getSelectionModel().select(0);
        
        cmbSearch.setItems(clientType);
        cmbSearch.getSelectionModel().select(0);
        cmbField01.setItems(clientType);
        cmbField01.getSelectionModel().select(0);
        
        cmbField01.setOnAction(event -> {
        oTrans.Master().getModel().setClientType(String.valueOf(cmbField01.getSelectionModel().getSelectedIndex()));
        initClientType();
        });
    }
        
    private void initClientType() {
        if (cmbField01.getSelectionModel().getSelectedIndex() == 0) {
            tabIndex03.setDisable(true);
            tabIndex04.setDisable(true);
            tabIndex05.setDisable(true);
            tabIndex06.setDisable(false);
        } else {
            tabIndex06.setDisable(true);
            tabIndex03.setDisable(false);
            tabIndex04.setDisable(false);
            tabIndex05.setDisable(false);
        }
        Integer lsValue = cmbField01.getSelectionModel().getSelectedIndex();
        disablefields(lsValue);
    }
    
    private void disablefields(int fsValue) {
        boolean lbShow = (fsValue == 0);
        
        // Arrays of TextFields grouped by sections
        TextField[][] allFields = {
            // Text fields related to specific sections
            {personalinfo02, personalinfo03, personalinfo04, personalinfo05, personalinfo06, personalinfo08
            , personalinfo11, personalinfo12,AddressField05,AddressField06,AddressField05},
        };
        personalinfo09.setDisable(lbShow);
        personalinfo10.setDisable(lbShow);
        
        cbAddress03.setVisible(lbShow);
        cbAddress04.setVisible(lbShow);
        cbAddress05.setVisible(lbShow);
        cbAddress06.setVisible(lbShow);
        cbAddress07.setVisible(lbShow);
        cbAddress08.setVisible(lbShow);
        lblAddressType.setVisible(lbShow);
        // Loop through each array of TextFields and clear them
        for (TextField[] fields : allFields) {
            for (TextField field : fields) {
                field.setDisable(lbShow);
            }
        }
        personalinfo01.setDisable(!lbShow);
    }
    private void AddressRecord() {
        System.out.println("nagload and ledger");
        address_data.clear();

        if (oTrans.getAddressCount()>= 0) {
            for (int lnCtr = 0; lnCtr < oTrans.getAddressCount(); lnCtr++) {
                System.out.println("Processing Address at Index: " + lnCtr);
//                 String lsTown = (String)oTrans.getAddress(lnCtr, 20) + ", " + (String)oTrans.getAddress(lnCtr, 22);
                // Debugging individual components
                System.out.println(" ");
                System.out.println("house No: " + oTrans.Address(lnCtr).getModel().getHouseNo());
                System.out.println("Address: " + oTrans.Address(lnCtr).getModel().getAddress());
                System.out.println("Town: " + oTrans.Address(lnCtr).getModel().Town().getTownName());
                System.out.println("------------------------------------------------------------------------");

                address_data.add(new ModelAddress(   
                        String.valueOf(lnCtr + 1),
                        (String)oTrans.Address(lnCtr).getModel().getHouseNo(),
                        oTrans.Address(lnCtr).getModel().getAddress(),
                        oTrans.Address(lnCtr).getModel().Town().getTownName()
                ));
            }
        } else {
            ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
        }
    }
    
    
    
    
    
}
