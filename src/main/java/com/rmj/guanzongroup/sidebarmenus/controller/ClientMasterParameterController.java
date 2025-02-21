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
        initTables();
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
            }
        }
    }
    private void initTables(){
        initTableAddress();
        initTableMobile();
        initTableMail();
        initTableSocMed();
    }
    private void LoadRecord(){
        String ID = oTrans.Master().getModel().getClientId();
        
        oTrans.AdressList(ID);
        AddressRecord();
        
        oTrans.MobileList(ID);
        MobileRecord();
        
        oTrans.EmailList(ID);
        MailRecord();
        
        oTrans.SocMedList(ID);
        SocMedRecord();
        
        MasterRecord();
        personalinfo07.setValue(LocalDate.now());
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
                txtField06.setText((poTrans.Master().getModel().getCompanyName()== null) ? "" : poTrans.Master().getModel().getCompanyName());
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
            txtField05.setText((String) oTrans.Master().getModel().getMothersMaidenName());
            txtField09.setText((String) oTrans.Master().getModel().Citizenship().getNationality());
            txtField08.setText((String) oTrans.Master().getModel().BirthTown().getTownName());
            
            if(!oTrans.Master().getModel().getClientType().trim().isEmpty() && oTrans.Master().getModel().getClientType()!= null){
                cmbField01.getSelectionModel().select(Integer.parseInt((String) oTrans.Master().getModel().getClientType()));
            }
            
            if(!address_data.isEmpty()){
               for (int lnCtr = 0; lnCtr < oTrans.getListAddressCount(); lnCtr++) {   
                    if(oTrans.ListAddress(lnCtr).isPrimaryAddress()){
                        String lsAddress = oTrans.ListAddress(lnCtr).getHouseNo() + " " + oTrans.ListAddress(lnCtr).getAddress() +
                                " " + (String) oTrans.ListAddress(lnCtr).Barangay().getBarangayName() + 
                                ", " + (String)  oTrans.ListAddress(lnCtr).Town().getTownName()+ ", " + (String)  oTrans.ListAddress(lnCtr).Town().getZipCode();
                        txtField03.setText(lsAddress);
                        
                    }
                }
            }
            
            if(!data.isEmpty()){
                for (int lnCtr = 0; lnCtr < oTrans.getListMobileCount(); lnCtr++) {   
                    if(oTrans.ListMobile(lnCtr).isPrimaryMobile()){
                        txtField10.setText((String) oTrans.ListMobile(lnCtr).getMobileNo());
                    }
                }
            }
            
            if(!email_data.isEmpty()){
                for(int lnctr = 0; lnctr < oTrans.getListMailCount(); lnctr++){
                    if(oTrans.ListMail(lnctr).isPrimaryEmail()){
                        txtField11.setText((String) oTrans.ListMail(lnctr).getMailAddress());
                    }
                }
            }
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
        
        
        cmbMobile01.setItems(mobileOwn);
        cmbMobile01.getSelectionModel().select(0);
        
        cmbMobile02.setItems(mobileType);
        cmbMobile02.getSelectionModel().select(0);
        
        cmbEmail01.setItems(EmailOwn);
        cmbEmail01.getSelectionModel().select(0);
        
        cmbSocMed01.setItems(socialTyp);
        cmbSocMed01.getSelectionModel().select(0);
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

        if (oTrans.getListAddressCount()>= 0) {
            for (int lnCtr = 0; lnCtr < oTrans.getListAddressCount(); lnCtr++) {
                System.out.println("Processing Address at Index: " + lnCtr);
//                 String lsTown = (String)oTrans.getAddress(lnCtr, 20) + ", " + (String)oTrans.getAddress(lnCtr, 22);
                // Debugging individual components
                System.out.println(" ");
                System.out.println("house No: " + oTrans.ListAddress(lnCtr).getHouseNo());
                System.out.println("Address: " + oTrans.ListAddress(lnCtr).getAddress());
                System.out.println("Town: " + oTrans.ListAddress(lnCtr).Town().getTownName());
                System.out.println("Barangay: " + oTrans.ListAddress(lnCtr).Barangay().getBarangayName());
                System.out.println("------------------------------------------------------------------------");

                address_data.add(new ModelAddress(   
                        String.valueOf(lnCtr + 1),
                        (String)oTrans.ListAddress(lnCtr).getHouseNo(),
                        oTrans.ListAddress(lnCtr).getAddress(),
                        oTrans.ListAddress(lnCtr).Town().getTownName(),
                        oTrans.ListAddress(lnCtr).Barangay().getBarangayName()
                ));
            }
        } else {
            ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
        }
    }
    
    
    public void initTableAddress() {   
        indexAddress01.setStyle("-fx-alignment: CENTER;");
        indexAddress02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexAddress03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexAddress04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexAddress05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        
        indexAddress01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexAddress02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexAddress03.setCellValueFactory(new PropertyValueFactory<>("index03")); 
        indexAddress04.setCellValueFactory(new PropertyValueFactory<>("index04"));  
        indexAddress05.setCellValueFactory(new PropertyValueFactory<>("index05"));  
        
        tblAddress.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblAddress.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblAddress.setItems(address_data);
        tblAddress.getSelectionModel().select(pnAddress + 1);
        tblAddress.autosize();
    }
    
    @FXML
    private void tblAddress_Clicked(MouseEvent event) {
        pnAddress = tblAddress.getSelectionModel().getSelectedIndex(); 
        if(pnAddress >= 0){
            boolean isActive = !"0".equals(oTrans.ListAddress(pnAddress).getRecordStatus());
            tblAddress.getSelectionModel().clearAndSelect(pnAddress);
            AddressField01.setText(oTrans.ListAddress(pnAddress).getHouseNo());
            AddressField02.setText(oTrans.ListAddress(pnAddress).getAddress());
            AddressField03.setText(oTrans.ListAddress(pnAddress).Town().getTownName());
            AddressField04.setText(oTrans.ListAddress(pnAddress).Barangay().getBarangayName());
            AddressField05.setText(String.valueOf(oTrans.ListAddress(pnAddress).getLatitude()));
            AddressField06.setText(String.valueOf(oTrans.ListAddress(pnAddress).getLongitude()));
            cbAddress01.setSelected(isActive);
            cbAddress02.setSelected(oTrans.ListAddress(pnAddress).isPrimaryAddress());
            cbAddress03.setSelected(oTrans.ListAddress(pnAddress).isOfficeAddress());
            cbAddress04.setSelected(oTrans.ListAddress(pnAddress).isBillingAddress());
            cbAddress05.setSelected(oTrans.ListAddress(pnAddress).isShippingAddress());
            cbAddress06.setSelected(oTrans.ListAddress(pnAddress).isProvinceAddress());
            cbAddress07.setSelected(oTrans.ListAddress(pnAddress).isCurrentAddress());
            cbAddress08.setSelected(oTrans.ListAddress(pnAddress).isLTMSAddress());
            lblAddressStat.setText(isActive ? "ACTIVE" : "INACTIVE"); 
        }
    }
    
    private void MobileRecord() {
        data.clear();

        if (oTrans.getListMobileCount()>= 0) {
            for (int lnCtr = 0; lnCtr < oTrans.getListMobileCount(); lnCtr++) {
                System.out.println("Processing Mobile at Index: " + lnCtr);
                System.out.println(" ");
                System.out.println("Mobile No: " + oTrans.ListMobile(lnCtr).getMobileNo());
                System.out.println("Ownership: " + oTrans.ListMobile(lnCtr).getOwnershipType());
                System.out.println("Mobile Type: " + oTrans.ListMobile(lnCtr).getMobileType());
                System.out.println("------------------------------------------------------------------------");

                data.add(new ModelMobile(   
                        String.valueOf(lnCtr + 1),
                        (String)oTrans.ListMobile(lnCtr).getMobileNo(),
                        oTrans.ListMobile(lnCtr).getOwnershipType(),
                        oTrans.ListMobile(lnCtr).getMobileType()
                ));
            }
        } else {
            ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
        }
    }
    
    
    public void initTableMobile() {   
        indexMobileNo01.setStyle("-fx-alignment: CENTER;");
        indexMobileNo02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexMobileNo03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexMobileNo04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        
        indexMobileNo01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexMobileNo02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexMobileNo03.setCellValueFactory(new PropertyValueFactory<>("index03")); 
        indexMobileNo04.setCellValueFactory(new PropertyValueFactory<>("index04"));  
        
        tblMobile.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblMobile.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblMobile.setItems(data);
        tblMobile.getSelectionModel().select(pnMobile + 1);
        tblMobile.autosize();
    }
//    
    @FXML
    private void tblMobile_Clicked(MouseEvent event) {
        pnMobile = tblMobile.getSelectionModel().getSelectedIndex(); 
        if(pnMobile >= 0){
            boolean isActive = !"0".equals(oTrans.ListMobile(pnMobile).getRecordStatus());
            tblMobile.getSelectionModel().clearAndSelect(pnMobile);
            txtMobile01.setText(oTrans.ListMobile(pnMobile).getMobileNo());
            
            cbMobileNo02.setSelected(isActive);
            cbMobileNo01.setSelected(oTrans.ListMobile(pnMobile).isPrimaryMobile());
            
            if(oTrans.ListMobile(pnMobile).getOwnershipType() != null && !oTrans.ListMobile(pnMobile).getOwnershipType().toString().trim().isEmpty()){
                cmbMobile01.getSelectionModel().select(Integer.parseInt(oTrans.ListMobile(pnMobile).getOwnershipType().toString()));
            }
            
            lblMobileStat.setText(isActive ? "ACTIVE" : "INACTIVE"); 
        }
    }
    
    private void MailRecord() {
        email_data.clear();

        if (oTrans.getListMobileCount()>= 0) {
            for (int lnCtr = 0; lnCtr < oTrans.getListMailCount(); lnCtr++) {
                System.out.println("Processing MailRecord at Index: " + lnCtr);
                System.out.println(" ");
                System.out.println("mail No: " + lnCtr);
                System.out.println("Ownership: " + oTrans.ListMail(lnCtr).getOwnershipType());
                System.out.println("Email: " + oTrans.ListMail(lnCtr).getMailAddress());
                System.out.println("------------------------------------------------------------------------");

                email_data.add(new ModelEmail(
                        String.valueOf(lnCtr + 1),
                        oTrans.ListMail(lnCtr).getOwnershipType(),
                        oTrans.ListMail(lnCtr).getMailAddress()
                ));
            }
        } else {
            ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
        }
    }
    
    
    public void initTableMail() {   
        indexEmail01.setStyle("-fx-alignment: CENTER;");
        indexEmail02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexEmail03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        
        indexEmail01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexEmail02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexEmail03.setCellValueFactory(new PropertyValueFactory<>("index03"));  
        
        tblEmail.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblEmail.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblEmail.setItems(email_data);
        tblEmail.getSelectionModel().select(pnEmail + 1);
        tblEmail.autosize();
    }
    
    @FXML
    private void tblEmail_Clicked(MouseEvent event) {
        pnEmail = tblEmail.getSelectionModel().getSelectedIndex(); 
        if(pnEmail >= 0){
            boolean isActive = !"0".equals(oTrans.ListMobile(pnEmail).getRecordStatus());
            tblEmail.getSelectionModel().clearAndSelect(pnEmail);
            mailFields01.setText(oTrans.ListMail(pnEmail).getMailAddress());
            
            cbEmail02.setSelected(isActive);
            cbEmail01.setSelected(oTrans.ListMail(pnEmail).isPrimaryEmail());
            
            if(oTrans.ListMail(pnEmail).getOwnershipType() != null && !oTrans.ListMail(pnMobile).getOwnershipType().toString().trim().isEmpty()){
                cmbEmail01.getSelectionModel().select(Integer.parseInt(oTrans.ListMail(pnMobile).getOwnershipType().toString()));
            }
             lblEmailStat.setText(isActive ? "ACTIVE" : "INACTIVE"); 
        }
    }
    
    private void SocMedRecord() {
        social_data.clear();

        if (oTrans.getListSocMedCount()>= 0) {
            for (int lnCtr = 0; lnCtr < oTrans.getListSocMedCount(); lnCtr++) {
                System.out.println("Processing SocMedRecord at Index: " + lnCtr);
                System.out.println(" ");
                System.out.println("acount No: " + lnCtr);
                System.out.println("acount: " + oTrans.ListSocMed(lnCtr).getAccount());
                System.out.println("soc med type: " + oTrans.ListSocMed(lnCtr).getSocMedType());
                System.out.println("remarks: " + oTrans.ListSocMed(lnCtr).getRemarks());
                System.out.println("------------------------------------------------------------------------");

                social_data.add(new ModelSocialMedia(
                        String.valueOf(lnCtr + 1),
                        oTrans.ListSocMed(lnCtr).getAccount(),
                        oTrans.ListSocMed(lnCtr).getSocMedType(),
                        oTrans.ListSocMed(lnCtr).getRemarks()
                ));
            }
        } else {
            ShowMessageFX.Information("No Record Found!", "Computerized Acounting System", pxeModuleName);
        }
    }
    
    
    public void initTableSocMed() {   
        indexSocMed01.setStyle("-fx-alignment: CENTER;");
        indexSocMed02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexSocMed03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        
        indexSocMed01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexSocMed02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexSocMed03.setCellValueFactory(new PropertyValueFactory<>("index03"));  
        
        tblSocMed.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblSocMed.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblSocMed.setItems(social_data);
        tblSocMed.getSelectionModel().select(pnSocMed + 1);
        tblSocMed.autosize();
    }
    
    @FXML
    private void tblSocMed_Clicked(MouseEvent event) {
        pnSocMed = tblSocMed.getSelectionModel().getSelectedIndex(); 
        if(pnSocMed >= 0){
            boolean isActive = !"0".equals(oTrans.ListSocMed(pnSocMed).getRecordStatus());
            tblSocMed.getSelectionModel().clearAndSelect(pnSocMed);
            txtSocial01.setText(oTrans.ListSocMed(pnSocMed).getAccount());
            txtSocial02.setText(oTrans.ListSocMed(pnSocMed).getRemarks());
            cbSocMed01.setSelected(isActive);
//            cbSocMed01.setSelected(oTrans.ListMail(pnEmail).isPrimaryEmail());
            
            if(oTrans.ListSocMed(pnSocMed).getSocMedType()!= null && !oTrans.ListSocMed(pnSocMed).getSocMedType().toString().trim().isEmpty()){
                cmbEmail01.getSelectionModel().select(Integer.parseInt(oTrans.ListSocMed(pnSocMed).getSocMedType().toString()));
            }
             lblSocMedStat.setText(isActive ? "ACTIVE" : "INACTIVE"); 
        }
    }
    
    
}
    