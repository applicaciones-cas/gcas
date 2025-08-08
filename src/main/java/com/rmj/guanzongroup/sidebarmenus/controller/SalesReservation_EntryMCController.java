/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSalesReservationDetail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSalesReservationSource;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.UserRight;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.sales.services.SalesReservationControllers;
import ph.com.guanzongroup.cas.sales.status.Sales_Reservation_Static;

/**
 * FXML Controller class
 *
 * @author user
 */
public class SalesReservation_EntryMCController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private SalesReservationControllers poSalesReservationControllers;
    private String psFormName = "Sales Reservation Entry MC";
    private LogWrapper logWrapper;
    private JSONObject poJSON;
    
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    
    private int pnEditMode;
    private int pnSourceRow = -1;
    private int pnDetailRow = -1;
    String prevCustomer ="";
    private static final int ROWS_PER_PAGE = 50;
    
    private ObservableList<ModelSalesReservationSource> source_data = FXCollections.observableArrayList();
    private ObservableList<ModelSalesReservationDetail> detail_data = FXCollections.observableArrayList();
    
    @Override
    public void setGRider(GRiderCAS foValue) {
        poApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
        psIndustryID = fsValue;
    }

    @Override
    public void setCompanyID(String fsValue) {
        psCompanyID = fsValue;
    }

    @Override
    public void setCategoryID(String fsValue) {
        psCategoryID = fsValue;
    }

    // ──────────────────────────────
    // Containers
    // ──────────────────────────────
    @FXML private AnchorPane AnchorMain;
    @FXML private AnchorPane apBrowse;
    @FXML private AnchorPane apButton;
    @FXML private HBox hbButtons;

    // ──────────────────────────────
    // Labels
    // ──────────────────────────────
    @FXML private Label lblSource;
    @FXML private Label lblStatus;

    // ──────────────────────────────
    // Buttons
    // ──────────────────────────────
    @FXML private Button btnBrowse;
    @FXML private Button btnNew;
    @FXML private Button btnUpdate;
    @FXML private Button btnSearch;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnHistory;
    @FXML private Button btnRetrieve;
    @FXML private Button btnClose;

    // ──────────────────────────────
    // Transaction Fields
    // ──────────────────────────────
    @FXML private TextField tfTransactionNo;
    @FXML private DatePicker dpTransaction;
    @FXML private DatePicker dpExpedtedDate;

    @FXML private TextField tfCustomerName;
    @FXML private TextField tfAddress;
    @FXML private TextField tfContact;
    @FXML private TextArea  taRemarks;
    @FXML private TextField tfReference;

    // ──────────────────────────────
    // Financial Fields
    // ──────────────────────────────
    @FXML private TextField tfTotal;
    @FXML private TextField tfAmountPaid;
    @FXML private TextField tfDownPayment;

    // ──────────────────────────────
    // Item / Product Fields
    // ──────────────────────────────
    @FXML private TextField tfBrand;
    @FXML private TextField tfModel;
    @FXML private TextField tfVariant;
    @FXML private TextField tfInvType;
    @FXML private TextField tfCategory;
    @FXML private TextField tfColor;
    @FXML private TextField tfUnitPrice;
    @FXML private TextField tfQuantity;
    @FXML private TextArea  taNotes;

    // ──────────────────────────────
    // Detail Table
    // ──────────────────────────────
    @FXML private TableView tblDetailList;
    @FXML private TableColumn tblDRowNo;
    @FXML private TableColumn tblDStockID;
    @FXML private TableColumn tblDClassify;
    @FXML private TableColumn tblDQty;
    @FXML private TableColumn tblDUnitPrice;
    @FXML private TableColumn tblDMinDown;
    @FXML private TableColumn tblDTotalAmount;

    // ──────────────────────────────
    // Source Table
    // ──────────────────────────────
    @FXML private TableView tblSourceList;
    @FXML private TableColumn tblSRowNo;
    @FXML private TableColumn  tblSTransNo;
    @FXML private TableColumn  tblSTranDate;
    @FXML private TableColumn  tblSSource;

    // ──────────────────────────────
    // Pagination
    // ──────────────────────────────
    @FXML private Pagination pagination;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         initObject();
         initButton(pnEditMode);
         ClickButton();
         initFields();
         initTableSourceList();
         initTableDetailList();
         pagination.setPageCount(0);
        // TODO
    }
    
    private void initObject(){
        try {
            poSalesReservationControllers = new SalesReservationControllers(poApp, logWrapper);
            poSalesReservationControllers.SalesReservation().setTransactionStatus(Sales_Reservation_Static.OPEN);
            poJSON = poSalesReservationControllers.SalesReservation().InitTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
            }

            Platform.runLater((() -> {
                try {
                    poSalesReservationControllers.SalesReservation().setIndustryID(psIndustryID);
                    poSalesReservationControllers.SalesReservation().setCompanyID(psCompanyID);
                    poSalesReservationControllers.SalesReservation().setCategoryCd(psCategoryID);
                    poSalesReservationControllers.SalesReservation().setBranchCode(poApp.getBranchCode());
                    System.out.println("inits : " + psIndustryID + " " +  poSalesReservationControllers.SalesReservation().Master().getIndustryID());
                    //                loadRecordSearch();
                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }));
            Platform.runLater(() -> btnNew.fire());
                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    private void initFields() {
        Node[] txtFieldInputs = {
            tfTransactionNo,
            tfCustomerName,
            tfAddress,
            tfContact,
            tfReference,
            tfTotal,
            tfAmountPaid,
            tfBrand,
            tfModel,
            tfUnitPrice,
            tfQuantity,
            tfDownPayment,
            tfVariant,
            tfInvType,
            tfCategory,
            tfColor
        };

        Node[] txtAreaInputs = {
            taRemarks,
            taNotes
        };

        TextInputControl[] keyPressFields = {
            tfTransactionNo,
            tfCustomerName,
            tfAmountPaid,
            tfQuantity
        };
        /*this is to initialize all text field*/
        for (Node txtInput : txtFieldInputs) {
            txtInput.focusedProperty().addListener(txtField_Focus);
        }
        /*this is to initialize all text area*/
        for (Node txtAreaInput : txtAreaInputs) {
            txtAreaInput.focusedProperty().addListener(txtArea_Focus);
        }
        /*this is to initialize the fields keypressed*/
        for (TextInputControl input : keyPressFields) {
            input.setOnKeyPressed(this::txtField_KeyPressed);
        }
        if (tblSourceList.getItems().isEmpty()) {
            pagination.setVisible(false);
            pagination.setManaged(false);
        }
        tblSourceList.setOnMouseClicked(this::tblSourceList_Clicked);
        tblDetailList.setOnMouseClicked(this::tblDetail_Clicked);
    }
    
    final ChangeListener<Boolean> txtField_Focus = (obs, oldVal, newVal) -> {
        TextField loTextField = (TextField) ((ReadOnlyBooleanPropertyBase) obs).getBean();
        String lsTextFieldID = loTextField.getId();
        String lsValue = loTextField.getText();
        
        if (lsValue == null) {
            return;
        }

        if (!newVal) {
            try {
                switch (lsTextFieldID) {
                    case "tfCustomerName":
                        prevCustomer = tfCustomerName.getText();
                        break;
                    case "tfBrand":

                        break;
                    case "tfQuantity":
                        poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).setQuantity(Double.parseDouble(lsValue));
                        tfQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue, false));
                        detail_data.get(pnDetailRow).setIndex04(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue, false));
                        break;
                    default:
                        break;
                }
                tblDetailList.refresh();
                
            } catch (Exception e) {
                System.err.println("Error processing input [" + lsTextFieldID + "]: " + e.getMessage());
            }
        } else { // focus gained
            loTextField.selectAll();
        }
    };
    
    final ChangeListener<Boolean> txtArea_Focus = (obs, oldVal, newVal) -> {
        TextArea loTextArea = (TextArea) ((ReadOnlyBooleanPropertyBase) obs).getBean();
        String lsTextAreadID = loTextArea.getId();
        String lsValue = loTextArea.getText();
        
        if (lsValue == null) {
            return;
        }

        if (!newVal) {
            try {
                switch (lsTextAreadID) {
                    case "taRemarks":
                         poSalesReservationControllers.SalesReservation().Master().setRemarks(lsValue);
                        break;
                    case "taNotes":
                         poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).setNotes(lsValue);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error processing input [" + lsTextAreadID + "]: " + e.getMessage());
            }
        } else { // focus gained
            loTextArea.selectAll();
        }
    };
    
    private void txtField_KeyPressed(KeyEvent event) {
        TextField lsTxtField = (TextField) event.getSource();
        String txtFieldID = ((TextField) event.getSource()).getId();
        String lsValue = "";
        if (lsTxtField.getText() == null) {
            lsValue = "";
        } else {
            lsValue = lsTxtField.getText();
        }
        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                    case F3:
                        switch (txtFieldID) {
                            case "tfCustomerName":
                                poJSON = poSalesReservationControllers.SalesReservation().SearchClient(lsValue, false);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfCustomerName.selectAll();
                                    break;
                                }
                                tfCustomerName.setText( poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName());
                                tfAddress.setText(poSalesReservationControllers.SalesReservation().Master().Client_Address().getAddress());
//                                if (tfTerm.getText().isEmpty()) {
//                                    tfTerm.requestFocus();
//                                }
                                prevCustomer = tfCustomerName.getText();
                                break;
                            case "tfDestination":
                            case "tfTerm":
                            case "tfBrand":
                            case "tfModel":
                            case "tfQuantity":
                                CommonUtils.SetNextFocus((TextField) event.getSource());
                                break;
                        }
                        
                        break;
                    case F4:
                        break;
                    case UP:
                        break;
                    case DOWN:
                        break;
                    default:
                        break;
                }
            }
        } catch (ExceptionInInitializerError | NullPointerException | SQLException | GuanzonException ex) {
            Logger.getLogger(SalesReservation_EntryMCController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private void ClickButton() {
        Button[] buttons = {
            btnBrowse,
            btnNew,
            btnUpdate,
            btnSearch,
            btnSave,
            btnCancel,
            btnHistory,
            btnRetrieve,
            btnClose
        };
        for (Button btn : buttons) {
            btn.setOnAction(this::handleButtonAction);
        }
    }
    
    private void handleButtonAction(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof Button) {

            try {
                Button clickedButton = (Button) source;
                unloadForm appUnload = new unloadForm();
                switch (clickedButton.getId()) {
                    case "btnClose":
                        if (ShowMessageFX.YesNo("Do you really want to cancel this transaction?"
                                + " \nAny data collected will not be kept.",
                                "Computerized Acounting System", psFormName)) {
                            
                            appUnload.unloadForm(AnchorMain, poApp, psFormName);
                        }
                        break;
                    case "btnNew":
                        poJSON = poSalesReservationControllers.SalesReservation().initFields();
                        if("error".equals(poJSON.get("result"))){
                            ShowMessageFX.Error((String) poJSON.get("message"), psFormName, null);
                            break;
                        }
                        poSalesReservationControllers.SalesReservation().NewTransaction();
                        loadRecordMaster();
                        loadTableDetailList();
                        pnEditMode =poSalesReservationControllers.SalesReservation().getEditMode();
                        initButton(pnEditMode);
                        break;
                    case "btnBrowse":
                         
                        poJSON = poSalesReservationControllers.SalesReservation().SearchTransaction(tfCustomerName.getText());
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning( (String) poJSON.get("message"), psFormName,null);
                            tfCustomerName.setText(prevCustomer);
                            tfCustomerName.selectAll();
                            return;
                        }
                        loadRecordMaster();
                        loadRecordDetail();
                        loadTableDetailList();
                        pnEditMode = EditMode.READY;
                        initButton(pnEditMode);
                        break;
                    case "btnUpdate":
                        poJSON = poSalesReservationControllers.SalesReservation().UpdateTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning( (String) poJSON.get("message"), psFormName,null);
                            return;
                        }
                        
                        pnEditMode = poSalesReservationControllers.SalesReservation().getEditMode();
                        initButton(pnEditMode);
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.YesNo("Are you sure you want to cancel?", "Computerized Acounting System", psFormName)) {
                            clearMaster();
                            clearDetail();
                            detail_data.clear();
                            pnEditMode = EditMode.READY;
                            initButton(pnEditMode);
                        }
                        break;
                    case "btnSave":
                        String toConfirm = poSalesReservationControllers.SalesReservation().Master().getTransactionNo();
                        if (ShowMessageFX.YesNo("Are you sure you want to save the transaction?",
                                "Computerized Acounting System", psFormName)) {
                            poJSON = poSalesReservationControllers.SalesReservation().SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                break;
                            }
                            
                            if(pnEditMode == EditMode.ADDNEW){
                                source_data.get(tblSourceList.getSelectionModel().getSelectedIndex()).setIndex05(Sales_Reservation_Static.highlighter.default_green);
                                tblSourceList.refresh();
                            }
                            
                            ShowMessageFX.Information((String) poJSON.get("message"), psFormName, null);
                        
                            if (ShowMessageFX.YesNo("Do you want to Confirm Transaction?",
                                    "Computerized Acounting System", psFormName)) {
                                poJSON = poSalesReservationControllers.SalesReservation().OpenTransaction(toConfirm);
                                if (!"success".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    break;
                                }
                                poSalesReservationControllers.SalesReservation().setWithUI(true);
                                poJSON = poSalesReservationControllers.SalesReservation().ConfirmTransaction("");
                                if (!"success".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    break;
                                }
                                ShowMessageFX.Information((String) poJSON.get("message"), psFormName, null);
                            }
                        }
                        btnNew.fire();
                        break;
                    case "btnRetrieve":
                        loadTableSourceList();
                        break;
                    case "btnSearch":
                        loadTableSourceList();
                        break;   
                }
            } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
                Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void initTableSourceList() {
        tblSRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblSTransNo.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblSTranDate.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblSSource.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblSourceList.setItems(source_data);
        tblSourceList.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblSourceList.lookup("TableHeaderRow");
            if (header != null) {
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            }
        });
         initTableHighlithers();
    }
    private void initTableHighlithers() {
        tblSourceList.setRowFactory(tv -> {
            return new TableRow<ModelSalesReservationSource>() {
                @Override
                protected void updateItem(ModelSalesReservationSource item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        String lsStatus = item.getIndex05();
                        switch (lsStatus) {
                            case Sales_Reservation_Static.highlighter.default_blue:
                                setStyle("-fx-background-color: #A7C7E7;"); // light blue
                                break;
                            case Sales_Reservation_Static.highlighter.default_orange:
                                setStyle("-fx-background-color: #FFD8A8;"); // Orange: near due
                                break;
                            case Sales_Reservation_Static.highlighter.default_red:
                                setStyle("-fx-background-color: #FAA0A0;"); // Red: overdue
                                break;
                            case Sales_Reservation_Static.highlighter.default_green:
                                setStyle("-fx-background-color: #C1E1C1;"); // ligh green
                                break;
                            default:
                                setStyle("");
                                break;
                        }
                    }
                }
            };
        });
    }
    
    private void initTableDetailList() {
        tblDRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblDStockID.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblDClassify.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblDQty.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblDUnitPrice.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblDMinDown.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblDTotalAmount.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblDetailList.setItems(detail_data);
        tblDetailList.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblDetailList.lookup("TableHeaderRow");
            if (header != null) {
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            }
        });
    }
    
    
    private void loadRecordMaster() {
        try {
            tfTransactionNo.setText(poSalesReservationControllers.SalesReservation().Master().getTransactionNo());
            String lsStatus = "";
            switch (poSalesReservationControllers.SalesReservation().Master().getTransactionStatus()) {
                case Sales_Reservation_Static.OPEN:
                    lsStatus = "OPEN";
                    break;
                case Sales_Reservation_Static.CONFIRMED:
                    lsStatus = "CONFIRMED";
                    break;
                case Sales_Reservation_Static.PAID:
                    lsStatus = "PAID";
                    break;
                case Sales_Reservation_Static.CANCELLED:
                    lsStatus = "VOID";
                    break;
                default:
                    lsStatus = "UNKNOWN";
                    break;
                    
            }
            lblStatus.setText(lsStatus);
            
            tfTransactionNo.setText(poSalesReservationControllers.SalesReservation().Master().getTransactionNo());
           
            tfCustomerName.setText(poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName() != null
                    ? poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName() : "");
            tfCustomerName.setText(poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName() != null
                    ? poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName() : "");
            tfAddress.setText(poSalesReservationControllers.SalesReservation().Master().Client_Address().getAddress() != null
                    ? poSalesReservationControllers.SalesReservation().Master().Client_Address().getAddress() : "");
//            tfContact.setText(poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName() != null 
//                ? poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName() : "");
            tfReference.setText(poSalesReservationControllers.SalesReservation().Master().getReferenceNo() != null
                    ? poSalesReservationControllers.SalesReservation().Master().getReferenceNo() : "");
            tfTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Master().getTransactionTotal(), true));
            tfAmountPaid.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Master().getAmountPaid(), true));
            taRemarks.setText(poSalesReservationControllers.SalesReservation().Master().getRemarks() != null
                    ? poSalesReservationControllers.SalesReservation().Master().getRemarks() : "");
            
            LocalDate today = CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(
                    poSalesReservationControllers.SalesReservation().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE));
            
                    
            dpTransaction.setValue(today);
            dpExpedtedDate.setValue(today.plusMonths(1));
            dpTransaction.valueProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null) {
        // Automatically set dpExpected 1 month after dpTrans
        dpExpedtedDate.setValue(newVal.plusMonths(1));

        // Check if new dpTrans is before today
        if (newVal.isBefore(LocalDate.now())) {
            if (ShowMessageFX.YesNo(
                    "System approval is required for backdated transactions.\nDo you want to proceed?",
                    psFormName, null)) {

                if (poApp.getUserLevel() <= UserRight.ENCODER) {
                    poJSON = ShowDialogFX.getUserApproval(poApp);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(
                                "Approval denied: User is either invalid or not an authorized approving officer.",
                                psFormName, null
                        );
                        dpTransaction.setValue(oldVal); // revert change
                        return;
                    }
                }
            } else {
                dpTransaction.setValue(oldVal); // revert change
            }
        }
    }
});

// Expected Date Change Listener - Allow backdate but limit to 7 days after transaction date
dpExpedtedDate.valueProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null && dpTransaction.getValue() != null) {
        LocalDate transactionDate = dpTransaction.getValue();
        LocalDate maxAllowedBackdate = transactionDate.plusDays(7);

        if (newVal.isBefore(transactionDate)) {
            ShowMessageFX.Warning(
                "Expected date cannot be earlier than the transaction date.",
                psFormName, null
            );
            dpExpedtedDate.setValue(oldVal); // revert change
        }
        if (dpTransaction.getValue().plusDays(7).isAfter(dpExpedtedDate.getValue())) {
            ShowMessageFX.Warning(
                "Expected date can only be set within 7 days after the transaction date.",
                psFormName, null
            );
            dpExpedtedDate.setValue(oldVal); // revert change
        }
    }
});
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void loadRecordDetail(){
        try {
            tfBrand.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Brand().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Brand().getDescription() : "");
            tfModel.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Model().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Model().getDescription() : "");
            tfVariant.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Variant().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Variant().getDescription() : "");
            tfCategory.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Category().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Category().getDescription() : "");
            tfColor.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Color().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Color().getDescription() : "");
            
            tfUnitPrice.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getCost(), true));
            tfDownPayment.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).getMinimumDown(), true));
            tfQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).getQuantity(), false));
           
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadTableSourceList() {
        btnRetrieve.setDisable(true);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50); // Set size to 200x200
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER); // Center it
        tblSourceList.setPlaceholder(loadingPane); // Show while loading
        progressIndicator.setVisible(true); // Make sure it's visible
        progressIndicator.setManaged(true); // Make sure it's visible

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
                Platform.runLater(() -> {
                    try {
                        source_data.clear();
                        poJSON = poSalesReservationControllers.SalesReservation().getUnifiedSource(poSalesReservationControllers.SalesReservation().Master().getClientID());
                        if ("success".equals(poJSON.get("result"))) {
                            JSONArray unifiedSource = (JSONArray) poJSON.get("data");
                            if (unifiedSource != null && !unifiedSource.isEmpty()) {
                                for (Object requestObj : unifiedSource) {
                                    String Status = Sales_Reservation_Static.highlighter.default_default;
                                    JSONObject obj = (JSONObject) requestObj;
                                    ModelSalesReservationSource loMain = new ModelSalesReservationSource(
                                            String.valueOf(source_data.size() + 1),
                                            obj.get("sTransNox") != null ? obj.get("sTransNox").toString() : "",
                                            obj.get("dTransact") != null ? obj.get("dTransact").toString() : "",
                                            obj.get("source") != null ? obj.get("source").toString() : "",
                                            Status
                                    );
                                    source_data.add(loMain);
                                }
                            } else {
                                source_data.clear();
                            }
                        }
//                        showRetainedHighlight(true);
                        if (source_data.isEmpty()) {
                            tblSourceList.setPlaceholder(new Label("NO RECORD TO LOAD"));
                            ShowMessageFX.Warning("No Record Found", psFormName, null);
                            return;
                        }
                        tblSourceList.setItems(source_data);
                        
                        
                        

                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(DisbursementVoucher_EntryController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                });
                return null;
            }

            @Override
            protected void succeeded() {
                btnRetrieve.setDisable(false);
                if (source_data == null || source_data.isEmpty()) {
                    tblSourceList.setPlaceholder(new Label("NO RECORD TO LOAD"));
                } else {
                    if (pagination != null) {
                        pagination.setPageCount((int) Math.ceil((double) source_data.size() / ROWS_PER_PAGE));
                        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                            createPage(newIndex.intValue());
                        });
                    }
                    createPage(0);
                    pagination.setVisible(true);
                    pagination.setManaged(true);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    tblSourceList.toFront();
                }
            }

            @Override
            protected void failed() {
                 btnRetrieve.setDisable(false);
                pagination.setVisible(true);
                pagination.setManaged(true);
                progressIndicator.setVisible(false);
                progressIndicator.setManaged(false);
                tblSourceList.toFront();

            }
        };
        new Thread(task).start(); // Run task in background
    }
    private void tblSourceList_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            pnSourceRow = tblSourceList.getSelectionModel().getSelectedIndex();
            if (event.getClickCount() == 2) {
              
                    ModelSalesReservationSource loSourceSelected = (ModelSalesReservationSource) tblSourceList.getSelectionModel().getSelectedItem();
                    if (loSourceSelected != null) {
                        String lsTransactionNo = loSourceSelected.getIndex02();
                        String lsSource = loSourceSelected.getIndex04();
                        try {
                            poJSON = poSalesReservationControllers.SalesReservation().addSourceToSalesRsvDetail(lsTransactionNo,lsSource);
                            if ("success".equals(poJSON.get("result"))) {
                                if (poSalesReservationControllers.SalesReservation().getDetailCount() > 0) {
                                    pnSourceRow = poSalesReservationControllers.SalesReservation().getDetailCount() - 1;
                                    source_data.get(tblSourceList.getSelectionModel().getSelectedIndex()).setIndex05(Sales_Reservation_Static.highlighter.default_blue);
                                    loadTableDetailAndSelectedRow();
                                    tblSourceList.refresh();
                                }
                            } else {
                                if("true".equals(poJSON.get("ischange"))){
                                     if (ShowMessageFX.YesNo((String) poJSON.get("message"), psFormName, null)) {
                                         btnNew.fire();
                                         poJSON = poSalesReservationControllers.SalesReservation().addSourceToSalesRsvDetail(lsTransactionNo, lsSource);
                                         if ("success".equals(poJSON.get("result"))) {
                                             if (poSalesReservationControllers.SalesReservation().getDetailCount() > 0) {
                                                 pnSourceRow = poSalesReservationControllers.SalesReservation().getDetailCount() - 1;
                                                 source_data.get(tblSourceList.getSelectionModel().getSelectedIndex()).setIndex05(Sales_Reservation_Static.highlighter.default_blue);
                                                 loadTableDetailAndSelectedRow();
                                                 tblSourceList.refresh();
                                             }
                                         }
                                     }
                                }else{
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                }
                            }
                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(PurchaseOrder_EntryMCController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                            ShowMessageFX.Warning("Error loading data: " + ex.getMessage(), psFormName, null);
                        } catch (CloneNotSupportedException ex) {
                            Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
            }
        }
    }
    private void loadTableDetailAndSelectedRow() {
        if (pnDetailRow >= 0) {
            Platform.runLater(() -> {
                // Run a delay after the UI thread is free
                PauseTransition delay = new PauseTransition(Duration.millis(10));
                delay.setOnFinished(event -> {
                    Platform.runLater(() -> { // Run UI updates in the next cycle
                        loadTableDetailList();
                    });
                });
                delay.play();
            });
            loadRecordMaster();
            loadRecordDetail();
//            initDetailFocus();
        }
    }
    
    private Node createPage(int pageIndex) {
        int totalPages = (int) Math.ceil((double) source_data.size() / ROWS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        pageIndex = Math.max(0, Math.min(pageIndex, totalPages - 1));
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, source_data.size());

        if (!source_data.isEmpty()) {
            tblSourceList.setItems(FXCollections.observableArrayList(source_data.subList(fromIndex, toIndex)));
        }

        if (pagination != null) { // Replace with your actual Pagination variable
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(pageIndex);
        }

        return tblSourceList;
    }
    
    
    private void loadTableDetailList() {
//        pbEnteredDV = false;
        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblDetailList.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    try {
                        detail_data.clear();
                        int lnCtr;
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poSalesReservationControllers.SalesReservation().getDetailCount() - 1;
                            if (lnCtr >= 0) {
                                String lsSourceNo = poSalesReservationControllers.SalesReservation().Master().getSourceNo();
                                if (!lsSourceNo.isEmpty() || poSalesReservationControllers.SalesReservation().Master() == null) {
                                    try {
                                        poSalesReservationControllers.SalesReservation().AddDetail();

                                    } catch (CloneNotSupportedException ex) {
                                        Logger.getLogger(DisbursementVoucher_EntryController.class
                                                .getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                        double lnNetTotal = 0.0000;
                        for (lnCtr = 0; lnCtr < poSalesReservationControllers.SalesReservation().getDetailCount(); lnCtr++) {
                            try {
                                double unitprice = Double.parseDouble(poSalesReservationControllers.SalesReservation().Detail(lnCtr).Inventory().getCost().toString());
                                lnNetTotal = poSalesReservationControllers.SalesReservation().Detail(lnCtr).getQuantity() * unitprice;
                                detail_data.add(
                                        new ModelSalesReservationDetail(String.valueOf(lnCtr + 1),
                                                poSalesReservationControllers.SalesReservation().Detail(lnCtr).getStockID(),
                                                "F",
                                                CustomCommonUtil.setIntegerValueToDecimalFormat(poSalesReservationControllers.SalesReservation().Detail(lnCtr).getQuantity(),false),
                                                CustomCommonUtil.setIntegerValueToDecimalFormat(poSalesReservationControllers.SalesReservation().Detail(lnCtr).Inventory().getCost(), true),
                                                "0.0000",
                                                CustomCommonUtil.setIntegerValueToDecimalFormat(lnNetTotal, true)
                                        ));
                            } catch (SQLException | GuanzonException ex) {
                                Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if (pnDetailRow < 0 || pnDetailRow
                                >= detail_data.size()) {
                            if (!detail_data.isEmpty()) {
                                JFXUtil.selectAndFocusRow(tblDetailList, 0);
                                pnDetailRow = tblDetailList.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            JFXUtil.selectAndFocusRow(tblDetailList, pnDetailRow);
                            loadRecordDetail();
                        }
//                        poJSON = poSalesReservationControllers.SalesReservation().computeFields();
//                        if ("error".equals((String) poJSON.get("result"))) {
//                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
//                            return;
//                        }
                        loadRecordDetail();
                    } catch (SQLException ex) {
                        Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                return null;
            }

            @Override
            protected void succeeded() {
                if (detail_data == null || detail_data.isEmpty()) {
                    tblDetailList.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblDetailList.toFront();
                }
                loading.progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                if (detail_data == null || detail_data.isEmpty()) {
                    tblDetailList.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }
        };
        new Thread(task).start();

    }
    
    private void tblDetail_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.READY) {
            pnDetailRow = tblDetailList.getSelectionModel().getSelectedIndex();
            ModelSalesReservationDetail selectedItem = (ModelSalesReservationDetail) tblDetailList.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 1) {
                clearDetail();
                if (selectedItem != null) {
                    if (pnDetailRow >= 0) {
                        loadRecordDetail();
                        tfQuantity.requestFocus();
                    }
                }
            }
        }
    }
    
    
    
    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        btnRetrieve.setVisible(lbShow);
        btnRetrieve.setManaged(lbShow);
        btnCancel.setVisible(lbShow);
        btnCancel.setManaged(lbShow);
        btnSave.setVisible(lbShow);
        btnSave.setManaged(lbShow);
        btnSearch.setVisible(false);
        btnSearch.setManaged(false);
        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
        btnBrowse.setVisible(!lbShow);
        btnBrowse.setManaged(!lbShow);
        btnNew.setVisible(!lbShow);
        btnNew.setManaged(!lbShow);
        btnClose.setVisible(true);
        btnClose.setManaged(true);
        
        tfQuantity.setEditable(lbShow);
        tfBrand.setEditable(lbShow);
        taNotes.setEditable(lbShow);
        taRemarks.setEditable(lbShow);
        dpTransaction.setDisable(!lbShow);
        dpExpedtedDate.setDisable(!lbShow);
        if (pnEditMode == EditMode.READY){
            btnUpdate.setVisible(true);
            btnUpdate.setManaged(true);
        }
        
    }
    
    private void clearMaster() {
        TextInputControl[] txtFieldInputs = {
            tfTransactionNo,
            tfCustomerName,
            tfAddress,
            tfContact,
            tfReference,
            tfTotal,
            tfAmountPaid,
        };

        for (TextInputControl txtInput : txtFieldInputs) {
            txtInput.clear();
        }
        taRemarks.clear();
        dpTransaction.setValue(null);
        dpExpedtedDate.setValue(null);
    }
    private void clearDetail(){
        lblStatus.setText("UNKNOWN");
            TextInputControl[] txtFieldInputs = {
            tfBrand,
            tfModel,
            tfVariant,
            tfInvType,
            tfCategory,
            tfColor,
            tfUnitPrice,
            tfDownPayment,
            tfQuantity
        };

        for (TextInputControl txtInput : txtFieldInputs) {
            txtInput.clear();
        }
        taNotes.clear();
//        detail_data.clear();
    }
    
    
}
