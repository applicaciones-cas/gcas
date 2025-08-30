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
import java.util.Date;
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
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
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
import org.guanzon.cas.purchasing.status.PurchaseOrderStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.sales.services.SalesReservationControllers;
import ph.com.guanzongroup.cas.sales.constant.Sales_Reservation_Static;

/**
 * FXML Controller class
 *
 * @author user
 */
public class SalesReservation_ConfirmationSPMCController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private SalesReservationControllers poSalesReservationControllers;
    private String psFormName = "Sales Reservation Confirmation SPMC";
    private LogWrapper logWrapper;
    private JSONObject poJSON;
    
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    
    private int pnEditMode;
    private int pnSourceRow = -1;
    private int pnDetailRow = -1;
    private String prevCustomer ="";
    private String psOldDate = "";
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
    // SEARCH Fields
    // ──────────────────────────────
    @FXML private TextField tfsTransactionNo;
    @FXML private TextField tfsCustomerName;

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
    @FXML private Button btnUpdate;
    @FXML private Button btnSearch;
    @FXML private Button btnSave;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;
    @FXML private Button btnReturn;
    @FXML private Button btnVoid;
    @FXML private Button btnRetrieve;
    @FXML private Button btnHistory;
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
    @FXML private TextField tfBarcode;
    @FXML private TextField tfDescription;
    @FXML private TextField tfBrand;
    @FXML private TextField tfModel;
    @FXML private TextField tfMeasure;
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
    @FXML private TableColumn  tblSCustomerName;
    @FXML private TableColumn  tblSAmount;

    // ──────────────────────────────
    // Pagination
    // ──────────────────────────────
    @FXML private Pagination pagination;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//         initObject();
         initButton(EditMode.UNKNOWN);
         ClickButton();
         initFields();
         initTableSourceList();
         initTableDetailList();
         pagination.setPageCount(0);
         initDatePickerActions();
        // TODO
    }
    
    private void initObject(){
        try {
            poSalesReservationControllers = new SalesReservationControllers(poApp, logWrapper);
            poSalesReservationControllers.SalesReservation().setTransactionStatus(Sales_Reservation_Static.OPEN + Sales_Reservation_Static.CONFIRMED);
            poSalesReservationControllers.SalesReservation().setWithUI(true);
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
                    Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }));
                pnEditMode =  poSalesReservationControllers.SalesReservation().getEditMode();
                poSalesReservationControllers.SalesReservation().Master().setIndustryID(psIndustryID);
                poSalesReservationControllers.SalesReservation().Master().setCompanyID(psCompanyID);
                poSalesReservationControllers.SalesReservation().setCategoryCd(psCategoryID);
                System.out.println("psIndustryID : " + psIndustryID);
                System.out.println("psCompanyID : " + psIndustryID);
                
                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
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
            tfBarcode,
            tfDescription,
            tfUnitPrice,
            tfQuantity,
            tfDownPayment,
            tfMeasure,
            tfInvType,
            tfCategory,
            tfColor,
            tfsTransactionNo,
            tfsCustomerName
        };

        Node[] txtAreaInputs = {
            taRemarks,
            taNotes
        };

        TextInputControl[] keyPressFields = {
            tfTransactionNo,
            tfCustomerName,
            tfAmountPaid,
            tfQuantity,
            tfModel,
            tfBrand,
            tfBarcode,
            tfDescription,
            tfsTransactionNo,
            tfsCustomerName
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
         for (Node txtAreaInput : txtAreaInputs) {
            txtAreaInput.setOnKeyPressed(this::txtArea_KeyPressed);
        }
        if (tblSourceList.getItems().isEmpty()) {
            pagination.setVisible(false);
            pagination.setManaged(false);
        }
        tblSourceList.setOnMouseClicked(this::tblSourceList_Clicked);
        tblDetailList.setOnMouseClicked(this::tblDetail_Clicked);
        tblDetailList.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        
    }
    private void tableKeyEvents(KeyEvent event) {
        if (detail_data.size() > 0) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblDetailList":
                    if (focusedCell != null) {
                        switch (event.getCode()) {
                            case TAB:
                            case DOWN:
                                pnDetailRow = JFXUtil.moveToNextRow(currentTable);
                                break;
                            case UP:
                                pnDetailRow = JFXUtil.moveToPreviousRow(currentTable);
                                break;
                            default:
                                break;
                        }
                        loadRecordDetail();
                        event.consume();
                    }
                    break;
            }
        }
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
                    case "tfBarcode":
                    case "tfDescription":
                    case "tfModel":
                        loadTableDetailList();
                        break;
                    case "tfQuantity":
                        poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).setQuantity(Double.parseDouble(lsValue));
                        tfQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue, false));
//                        detail_data.get(pnDetailRow).setIndex04(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue, false));
                        loadTableDetailList();
                        break;
                    default:
                        break;
                }
                
                tblDetailList.refresh();
                
            } catch (Exception e) {
                System.err.println("Error processing input [" + lsTextFieldID + "]: " + e.getMessage());
            }
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
                           case "tfsCustomerName":
                                initObject();
                                poJSON = poSalesReservationControllers.SalesReservation().SearchClient(lsValue, false);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfsCustomerName.selectAll();
                                    break;
                                }
                                tfsCustomerName.setText( poSalesReservationControllers.SalesReservation().Master().Client_Master().getCompanyName());
                                
                                loadTableSourceList();
                                break;
                            
                            case "tfBrand":
                                poJSON = poSalesReservationControllers.SalesReservation().SearchBrand(lsValue, false, pnDetailRow
                                );
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfBrand.setText("");
                                    break;
                                }
                                tfBrand.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Brand().getDescription());
                                tfModel.requestFocus();
                                
                                break;
                            case "tfModel":
                                poJSON = poSalesReservationControllers.SalesReservation().SearchModel(lsValue, false, pnDetailRow);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
//                                    
                                    if (poJSON.get("tableRow") != null) {
                                            pnDetailRow = (int) poJSON.get("tableRow");
                                            fakeClickOnTable(tblDetailList, pnDetailRow);
                                            loadRecordDetail();
                                    }
                                    tfModel.setText("");
                                }
                                tfModel.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Model().getDescription());
                                tfQuantity.requestFocus();
                                
                                break;
                            case "tfBarcode":
                                poJSON = poSalesReservationControllers.SalesReservation().SearchBarcode(lsValue, false, pnDetailRow);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
//                                    
                                    if (poJSON.get("tableRow") != null) {
                                            pnDetailRow = (int) poJSON.get("tableRow");
                                            fakeClickOnTable(tblDetailList, pnDetailRow);
                                            loadRecordDetail();
                                    }
                                    tfModel.setText("");
                                }
                                tfBarcode.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Model().getDescription());
                                tfQuantity.requestFocus();
                                
                                break;
                            case "tfDescription":
                                poJSON = poSalesReservationControllers.SalesReservation().SearchDescription(lsValue, false, pnDetailRow);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
//                                    
                                    if (poJSON.get("tableRow") != null) {
                                            pnDetailRow = (int) poJSON.get("tableRow");
                                            fakeClickOnTable(tblDetailList, pnDetailRow);
                                            loadRecordDetail();
                                    }
                                    tfModel.setText("");
                                }
                                tfDescription.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Model().getDescription());
                                tfQuantity.requestFocus();
                                
                                break;
                            case "tfQuantity":
                                CommonUtils.SetNextFocus((TextField) event.getSource());
//                                fakeClickOnTable(tblDetailList, pnDetailRow + 1);
                                    
                                break;
                            case "tfsTransactionNo":
                                initObject();
                                loadTableSourceList();
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
            Logger.getLogger(SalesReservation_ConfirmationSPMCController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void txtArea_KeyPressed(KeyEvent event) {
        TextArea lsTxtArea = (TextArea) event.getSource();
        String lsTxtAreaID = ((TextArea) event.getSource()).getId();
        String lsValue = "";
        if (lsTxtArea.getText() == null) {
            lsValue = "";
        } else {
            lsValue = lsTxtArea.getText();
        }
        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                        switch (lsTxtAreaID) {
                            case "taRemarks":
                                tfModel.requestFocus();
                                break;
                            case "taNotes":
                                fakeClickOnTable(tblDetailList, pnDetailRow + 1);
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
        } catch (ExceptionInInitializerError | NullPointerException   ex) {
            Logger.getLogger(SalesReservation_ConfirmationSPMCController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void fakeClickOnTable(TableView<?> tableView, int rowIndex) {
    if (rowIndex < 0 || rowIndex >= tableView.getItems().size()) {
        return; // Index out of bounds
    }

    // Select the row
    tableView.getSelectionModel().clearAndSelect(rowIndex);
    tableView.scrollTo(rowIndex);

    // Fire the selection change listener manually
    if (tableView.getOnMouseClicked() != null) {
        MouseEvent fakeClick = new MouseEvent(
                MouseEvent.MOUSE_CLICKED,
                0, 0, 0, 0,
                MouseButton.PRIMARY,
                1,
                false, false, false, false,
                true, false, false,
                true, false, false,
                null
        );
        tableView.getOnMouseClicked().handle(fakeClick);
        tfQuantity.requestFocus();
        
    }
}
    
    private void ClickButton() {
        Button[] buttons = {
            btnUpdate,
            btnSearch,
            btnSave,
            btnCancel,
            btnConfirm,
            btnReturn,
            btnVoid,
            btnRetrieve,
            btnHistory,
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
                    case "btnConfirm":
                        if (ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to confirm transaction?")) {
                            
                            poJSON = poSalesReservationControllers.SalesReservation().checkExistingTrans(
                                    poSalesReservationControllers.SalesReservation().Master().getSourceCode(),
                                    poSalesReservationControllers.SalesReservation().Master().getSourceNo());
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                return;
                            }
                            
                            poJSON = poSalesReservationControllers.SalesReservation().ConfirmTransaction("");
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                return;
                            }
                            source_data.get(tblSourceList.getSelectionModel().getSelectedIndex()).
                                                setIndex06(Sales_Reservation_Static.highlighter.default_green);
                                                tblSourceList.refresh();
                        }
                        clearMaster();
                        clearDetail();
                        detail_data.clear();
                        initButton(EditMode.UNKNOWN);
                        break;
//                    
                    case "btnUpdate":
                        poJSON = poSalesReservationControllers.SalesReservation().validateConfirmedTransactionApproval();
                       if("error".equals(poJSON.get("result"))){
                           ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                           return;
                       }
                
                        poJSON = poSalesReservationControllers.SalesReservation().UpdateTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                            return;
                        }
                        loadTableDetailList();
                        pnEditMode = poSalesReservationControllers.SalesReservation().getEditMode();
                        initButton(pnEditMode);
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.YesNo("Are you sure you want to cancel?", "Computerized Acounting System", psFormName)) {
                            clearMaster();
                            clearDetail();
                            detail_data.clear();
                            pnEditMode = EditMode.UNKNOWN;
                            initButton(pnEditMode);
                        }
                        break;
                    case "btnSave":
                        
                        String toConfirm = poSalesReservationControllers.SalesReservation().Master().getTransactionNo();
                        if (ShowMessageFX.YesNo("Are you sure you want to save the transaction?",
                                "Computerized Acounting System", psFormName)) {
                            poJSON = poSalesReservationControllers.SalesReservation().validateDetails();
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                return;
                            }
                            
                            poJSON = poSalesReservationControllers.SalesReservation().SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                return;
                            }
                            
                            if(pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE){
                                for (int x = 0; x < source_data.size(); x++) {
                                        source_data.get(tblSourceList.getSelectionModel().getSelectedIndex()).
                                                setIndex06(Sales_Reservation_Static.highlighter.default_green);
                                                tblSourceList.refresh();
                                }
                            }
                            
                            ShowMessageFX.Information((String) poJSON.get("message"), psFormName, null);
                            poJSON = poSalesReservationControllers.SalesReservation().OpenTransaction(toConfirm);
                            if (!poSalesReservationControllers.SalesReservation().Master().getTransactionStatus().equals(Sales_Reservation_Static.CONFIRMED)) {
                                if (ShowMessageFX.YesNo("Do you want to Confirm Transaction?",
                                        "Computerized Acounting System", psFormName)) {
                                    if (!"success".equals((String) poJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                        return;
                                    }
                                    
                                    poJSON = poSalesReservationControllers.SalesReservation().ConfirmTransaction("");
                                    if (!"success".equals((String) poJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                        clearMaster();
                                        clearDetail();
                                        detail_data.clear();
                                        pnEditMode = EditMode.READY;
                                        initButton(pnEditMode);
                                        return;
                                    }
                                    ShowMessageFX.Information((String) poJSON.get("message"), psFormName, null);
                                }
                            }
                        }
                        initObject();
                        clearMaster();
                        clearDetail();
                        detail_data.clear();
                        initButton(EditMode.UNKNOWN);   
                        break;
                    case "btnRetrieve":
                        clearMaster();
                        clearDetail();
                        detail_data.clear();
                        initObject();
                        loadTableSourceList();
                        tblSourceList.refresh();
                        initButton(EditMode.UNKNOWN); 
                        break;
                    case "btnSearch":
                        initObject();
                        loadTableSourceList();
                        break;   
                    case "btnVoid":
                        if (ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to void transaction?")) {
                            poJSON = poSalesReservationControllers.SalesReservation().VoidTransaction("");
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                return;
                            }
                            source_data.get(tblSourceList.getSelectionModel().getSelectedIndex()).
                                                setIndex06(Sales_Reservation_Static.highlighter.default_red);
                                                tblSourceList.refresh();

                            clearMaster();
                            clearDetail();
                            detail_data.clear();
                            initButton(EditMode.UNKNOWN);
                        }
                        break;
                }
            } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
                Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }   
    }
    
    private void initTableSourceList() {
        tblSRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblSTransNo.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblSTranDate.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblSCustomerName.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblSAmount.setCellValueFactory(new PropertyValueFactory<>("index05"));
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
                        String lsStatus = item.getIndex06();
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
            dpTransaction.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(
                    poSalesReservationControllers.SalesReservation().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
            dpExpedtedDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(
                    poSalesReservationControllers.SalesReservation().Master().getExpectedDate(), SQLUtil.FORMAT_SHORT_DATE)));
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private void loadRecordDetail(){
        try {
            tfBarcode.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getBarCode()!= null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getBarCode() : "");
            tfDescription.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getDescription() : "");
            
            tfBrand.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Brand().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Brand().getDescription() : "");
            tfModel.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Model().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Model().getDescription() : "");
            tfColor.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Color().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Color().getDescription() : "");
            
            tfCategory.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Category().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Category().getDescription() : "");
            tfInvType.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().InventoryType().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().InventoryType().getDescription() : "");
            tfMeasure.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Measure().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().Measure().getDescription() : "");
            
            tfUnitPrice.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getCost(), true));
            tfDownPayment.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).getMinimumDown(), true));
            tfQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).getQuantity(), false));
           
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
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
                        poJSON = poSalesReservationControllers.SalesReservation().getReservationList(tfsTransactionNo.getText(), poSalesReservationControllers.SalesReservation().Master().getClientID());
                        if ("success".equals(poJSON.get("result"))) {
                            if (poSalesReservationControllers.SalesReservation().getSalesReservationCount() > 0) {
                                for (int lnCntr = 0; lnCntr < poSalesReservationControllers.SalesReservation().getSalesReservationCount(); lnCntr++) {
                                    
                                    String Status = Sales_Reservation_Static.highlighter.default_default;
                                    String MasterStatus = poSalesReservationControllers.SalesReservation().poSalesReservationMasterList(lnCntr).getTransactionStatus();
                                    switch (MasterStatus) {
                                        case Sales_Reservation_Static.OPEN:
                                            Status = Sales_Reservation_Static.highlighter.default_default;
                                            break;
                                        case Sales_Reservation_Static.CONFIRMED:
                                            Status = Sales_Reservation_Static.highlighter.default_green;
                                            break;
                                        default:
                                            throw new AssertionError();
                                    }
                                    source_data.add(new ModelSalesReservationSource(
                                            String.valueOf(lnCntr + 1),
                                            poSalesReservationControllers.SalesReservation().poSalesReservationMasterList(lnCntr).getTransactionNo(),
                                            SQLUtil.dateFormat(poSalesReservationControllers.SalesReservation().poSalesReservationMasterList(lnCntr).getTransactionDate(),SQLUtil.FORMAT_SHORT_DATE),
                                            poSalesReservationControllers.SalesReservation().poSalesReservationMasterList(lnCntr).Client_Master().getCompanyName(),
                                           CustomCommonUtil.setIntegerValueToDecimalFormat(
                                                    poSalesReservationControllers.SalesReservation().poSalesReservationMasterList(lnCntr).getAmountPaid(), true),
                                            Status, "", "", "", ""));
                                }
                            } else {
                                source_data.clear();
                            }
                        }
//                        showRetainedHighlight(true);
                        if (source_data.isEmpty()) {
                            tblSourceList.setPlaceholder(new Label("NO RECORD TO LOAD"));
                            ShowMessageFX.Warning("NO RECORD TO LOAD", psFormName, null);
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
            
            pnSourceRow = tblSourceList.getSelectionModel().getSelectedIndex();
            if (event.getClickCount() == 2) {
               
                ModelSalesReservationSource selectedItem = (ModelSalesReservationSource) tblSourceList.getSelectionModel().getSelectedItem();
                clearDetail();
                if (selectedItem != null) {
                try {
                    String lsTransactionNo = selectedItem.getIndex02();
                    poJSON = poSalesReservationControllers.SalesReservation().InitTransaction();
                    if ("success".equals((String) poJSON.get("result"))) {
                        poJSON = poSalesReservationControllers.SalesReservation().OpenTransaction(lsTransactionNo);
                        if ("success".equals((String) poJSON.get("result"))) {
                            source_data.get(tblSourceList.getSelectionModel().getSelectedIndex()).setIndex06(Sales_Reservation_Static.highlighter.default_blue);
                            loadRecordMaster();
                            loadTableDetailList();
                            tblSourceList.refresh();
                            pnEditMode = poSalesReservationControllers.SalesReservation().getEditMode();
                        } else {
                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                            pnEditMode = EditMode.UNKNOWN;
                        }
                        initButton(pnEditMode);

                    }
                } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                    Logger.getLogger(SalesReservation_ConfirmationCarController.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
                }
            
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
                            lnCtr = poSalesReservationControllers.SalesReservation().getDetailCount();
                            if (lnCtr > 0) {
                                String lsSourceNo = poSalesReservationControllers.SalesReservation().Master().getSourceNo();
                                poSalesReservationControllers.SalesReservation().AddDetail();
                            }
                        }
                        double lnNetTotal = 0.0000;
                        for (lnCtr = 0; lnCtr < poSalesReservationControllers.SalesReservation().getDetailCount(); lnCtr++) {

                                double unitprice = Double.parseDouble(poSalesReservationControllers.SalesReservation().Detail(lnCtr).Inventory().getCost().toString());
                                lnNetTotal = poSalesReservationControllers.SalesReservation().Detail(lnCtr).getQuantity() * unitprice;
                                detail_data.add(
                                        new ModelSalesReservationDetail(String.valueOf(lnCtr + 1),
                                                poSalesReservationControllers.SalesReservation().Detail(lnCtr).Inventory().getDescription(),
                                                "F",
                                                CustomCommonUtil.setIntegerValueToDecimalFormat(poSalesReservationControllers.SalesReservation().Detail(lnCtr).getQuantity(),false),
                                                CustomCommonUtil.setIntegerValueToDecimalFormat(poSalesReservationControllers.SalesReservation().Detail(lnCtr).Inventory().getCost(), true),
                                                "0.0000",
                                                CustomCommonUtil.setIntegerValueToDecimalFormat(lnNetTotal, true)
                                        ));

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
                        Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
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
                clearDetail();
                if (selectedItem != null) {
                    if (pnDetailRow >= 0) {
                        loadRecordDetail();
                        if (event.getClickCount() == 2) {
                            tfQuantity.requestFocus();
                        }
                    }
                }
            
        }
    }
    
    private void initButton(int fnEditMode) {
        boolean lbShow = (pnEditMode == EditMode.UPDATE);
        tfQuantity.setEditable(lbShow);
        tfBrand.setEditable(lbShow);
        taNotes.setEditable(lbShow);
        taRemarks.setEditable(lbShow);
        dpTransaction.setDisable(!lbShow);
        dpExpedtedDate.setDisable(!lbShow);
        btnReturn.setVisible(false);
        btnReturn.setManaged(false);
        
        btnClose.setVisible(!lbShow);
        btnClose.setManaged(!lbShow);

        CustomCommonUtil.setVisible(lbShow, btnSave, btnCancel);
        CustomCommonUtil.setManaged(lbShow, btnSave, btnCancel);

        CustomCommonUtil.setVisible(false, btnConfirm, btnVoid, btnUpdate);
        CustomCommonUtil.setManaged(false, btnConfirm , btnVoid, btnUpdate);

        btnHistory.setVisible(fnEditMode != EditMode.UNKNOWN);
        btnHistory.setManaged(fnEditMode != EditMode.UNKNOWN);
//        
        if (fnEditMode == EditMode.READY) {
            try {
                switch (poSalesReservationControllers.SalesReservation().Master().getTransactionStatus()) {
                    case Sales_Reservation_Static.OPEN:
                        CustomCommonUtil.setVisible(true, btnConfirm, btnVoid, btnUpdate);
                        CustomCommonUtil.setManaged(true, btnConfirm, btnVoid, btnUpdate);
                        break;
                    case Sales_Reservation_Static.CONFIRMED:
                        CustomCommonUtil.setVisible(true,  btnVoid, btnUpdate,btnHistory);
                        CustomCommonUtil.setManaged(true,  btnVoid, btnUpdate,btnHistory);
                        break;
                }
            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(SalesReservation_ConfirmationCarController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    private void clearMaster() {
        lblStatus.setText("UNKNOWN");
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
        
            TextInputControl[] txtFieldInputs = {
            tfBrand,
            tfModel,
            tfMeasure,
            tfInvType,
            tfCategory,
            tfColor,
            tfUnitPrice,
            tfDownPayment,
            tfQuantity,
            tfBarcode,
            tfDescription
        };

        for (TextInputControl txtInput : txtFieldInputs) {
            txtInput.clear();
        }
        taNotes.clear();
//        detail_data.clear();
    }
    
    private void initDatePickerActions() {
        dpTransaction.setOnAction(e -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                try {
                    LocalDate selectedLocalDate = dpTransaction.getValue();
                    LocalDate transactionDate = new java.sql.Date(poSalesReservationControllers.SalesReservation().Master().getTransactionDate().getTime()).toLocalDate();
                    if (selectedLocalDate == null) {
                        return;
                    }
                    
                    LocalDate dateNow = LocalDate.now();
                    psOldDate = CustomCommonUtil.formatLocalDateToShortString(transactionDate);
                    
                    boolean approved = true;
                    if (pnEditMode == EditMode.UPDATE) {
                        psOldDate = CustomCommonUtil.formatLocalDateToShortString(transactionDate);
                        if (selectedLocalDate.isAfter(dateNow)) {
                            ShowMessageFX.Warning("Invalid to future date.", psFormName, null);
                            approved = false;
                        }
                        
//                        if (selectedLocalDate.isBefore(transactionDate) && lsReferNo.isEmpty()) {
//                            ShowMessageFX.Warning("Invalid to backdate. Please enter a reference number first.", psFormName, null);
//                            approved = false;
//                        }
                        if (selectedLocalDate.isBefore(transactionDate)) {
                            boolean proceed = ShowMessageFX.YesNo(
                                    "You are changing the transaction date\n"
                                            + "If YES, seek approval to proceed with the changed date.\n"
                                            + "If NO, the transaction date will be remain.",
                                    psFormName, null
                            );
                            if (proceed) {
                                if (poApp.getUserLevel() <= UserRight.ENCODER) {
                                    poJSON = ShowDialogFX.getUserApproval(poApp);
                                    if (!"success".equals((String) poJSON.get("result"))) {
                                        approved = false;
                                        return;
                                    } else {
                                        if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                                            ShowMessageFX.Warning("User is not an authorized approving officer..", psFormName, null);
                                            approved = false;
                                            return;
                                        }
                                    }
                                }
                            } else {
                                approved = false;
                            }
                        }
                    }
                    if (pnEditMode == EditMode.ADDNEW) {
                        if (selectedLocalDate.isAfter(dateNow)) {
                            ShowMessageFX.Warning("Invalid to future date.", psFormName, null);
                            approved = false;
                        }
//                        if (selectedLocalDate.isBefore(dateNow) && lsReferNo.isEmpty()) {
//                            ShowMessageFX.Warning("Invalid to backdate. Please enter a reference number first.", psFormName, null);
//                            approved = false;
//                        }
                        
                        if (selectedLocalDate.isBefore(dateNow)) {
                            boolean proceed = ShowMessageFX.YesNo(
                                    "You selected a backdate with a reference number.\n\n"
                                            + "If YES, seek approval to proceed with the backdate.\n"
                                            + "If NO, the transaction date will be reset to today.",
                                    "Backdate Confirmation", null
                            );
                            if (proceed) {
                                if (poApp.getUserLevel() <= UserRight.ENCODER) {
                                    poJSON = ShowDialogFX.getUserApproval(poApp);
                                    if (!"success".equals((String) poJSON.get("result"))) {
                                        approved = false;
                                        return;
                                    } else {
                                        if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                                            ShowMessageFX.Warning("User is not an authorized approving officer..", psFormName, null);
                                            approved = false;
                                            return;
                                        }
                                    }
                                }
                            } else {
                                approved = false;
                            }
                        }
                    }
                    if (approved) {
                        poSalesReservationControllers.SalesReservation().Master().setTransactionDate(
                                SQLUtil.toDate(selectedLocalDate.toString(), SQLUtil.FORMAT_SHORT_DATE));
                    } else {
                        if (pnEditMode == EditMode.ADDNEW) {
                            dpTransaction.setValue(dateNow);
                            poSalesReservationControllers.SalesReservation().Master().setTransactionDate(
                                    SQLUtil.toDate(dateNow.toString(), SQLUtil.FORMAT_SHORT_DATE));
                        } else if (pnEditMode == EditMode.UPDATE) {
                            poSalesReservationControllers.SalesReservation().Master().setTransactionDate(
                                    SQLUtil.toDate(psOldDate, SQLUtil.FORMAT_SHORT_DATE));
                        }
                        
                    }
                    dpTransaction.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                            SQLUtil.dateFormat(poSalesReservationControllers.SalesReservation().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
                } catch (SQLException ex) {
                    Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (GuanzonException ex) {
                    Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

        dpExpedtedDate.setOnAction(e
                -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (dpExpedtedDate.getValue() != null) {
                    try {
                        LocalDate selectedLocalDate = dpExpedtedDate.getValue();
                        Date selectedDate = SQLUtil.toDate(selectedLocalDate.toString(), SQLUtil.FORMAT_SHORT_DATE);
                        Date transactionDate = poSalesReservationControllers.SalesReservation().Master().getTransactionDate();
                        LocalDate transactionLocalDate = LocalDate.now();
                        
                        if (selectedDate.before(transactionDate)) {
                            ShowMessageFX.Warning("Please select an expected  date that is on or after the transaction date.", "Invalid Expected Date", null);
                            dpExpedtedDate.setValue(transactionLocalDate);
                            poSalesReservationControllers.SalesReservation().Master().setExpectedDate(transactionDate);
                            return;
                        }
                        
                        poSalesReservationControllers.SalesReservation().Master().setExpectedDate(selectedDate);
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(SalesReservation_ConfirmationSPMCController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        );
    }
    
}
