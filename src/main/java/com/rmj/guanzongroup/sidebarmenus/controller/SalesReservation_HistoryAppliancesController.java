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
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.F4;
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
public class SalesReservation_HistoryAppliancesController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private SalesReservationControllers poSalesReservationControllers;
    private String psFormName = "Sales Reservation History Appliances";
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
    @FXML private Button btnSearch;
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
    @FXML private TextField tfBarcode;
    @FXML private TextField tfDescription;
    @FXML private TextField tfPreOrder;
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
    // SEARCH Fields
    // ──────────────────────────────
    @FXML private TextField tfsTransactionNo;
    @FXML private TextField tfsCustomerName;


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

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initObject();
        initButton(pnEditMode);
        ClickButton();
        initFields();
        initTableDetailList();
        initDatePickerActions();
        // TODO
    }
    
    private void initObject(){
        try {
            poSalesReservationControllers = new SalesReservationControllers(poApp, logWrapper);
            poSalesReservationControllers.SalesReservation().setTransactionStatus(Sales_Reservation_Static.OPEN
                    + Sales_Reservation_Static.CONFIRMED
                    + Sales_Reservation_Static.PAID
                    + Sales_Reservation_Static.CANCELLED
                    + Sales_Reservation_Static.VOID);
            poJSON = poSalesReservationControllers.SalesReservation().InitTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
            }
            
            poSalesReservationControllers.SalesReservation().setIndustryID(psIndustryID);
            poSalesReservationControllers.SalesReservation().setCompanyID(psCompanyID);
            poSalesReservationControllers.SalesReservation().setCategoryCd(psCategoryID);
            poSalesReservationControllers.SalesReservation().setBranchCode(poApp.getBranchCode());
            poSalesReservationControllers.SalesReservation().initFields();
            pnEditMode =  poSalesReservationControllers.SalesReservation().getEditMode();
            lblSource.setText(
                    poSalesReservationControllers.SalesReservation().Master().Company().getCompanyName() + " - "
                    + poSalesReservationControllers.SalesReservation().Master().Industry().getDescription()
            );
                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(SalesReservation_HistoryMonarchFoodController.class.getName()).log(Level.SEVERE, null, ex);
                }
    }
    
    private void initFields() {
        Node[] txtFieldInputs = {
            tfsTransactionNo,
            tfsCustomerName,
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
            tfsTransactionNo,
            tfsCustomerName,
            tfTransactionNo,
            tfCustomerName,
            tfAmountPaid,
            tfQuantity,
            tfModel,
            tfBrand
        };
        for (TextInputControl input : keyPressFields) {
            input.setOnKeyPressed(this::txtField_KeyPressed);
        }

        tblDetailList.setOnMouseClicked(this::tblDetail_Clicked);
        tblDetailList.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        
    }
    
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
                                poJSON = poSalesReservationControllers.SalesReservation().initFields();
                               if ("error".equals(poJSON.get("result"))) {
                                   ShowMessageFX.Error((String) poJSON.get("message"), psFormName, null);
                                   break;
                               }
                               poJSON = poSalesReservationControllers.SalesReservation().SearchTransactionbyFilter(tfsCustomerName.getText(),false);
                               if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                                   ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                   tfCustomerName.setText(prevCustomer);
                                   tfCustomerName.selectAll();
                                   return;
                               }
                               loadRecordMaster();
                               loadTableDetailList();
                               pnEditMode = EditMode.READY;
                               initButton(pnEditMode);
                                break;
                           case "tfsTransactionNo":
                               poJSON = poSalesReservationControllers.SalesReservation().initFields();
                               if ("error".equals(poJSON.get("result"))) {
                                   ShowMessageFX.Error((String) poJSON.get("message"), psFormName, null);
                                   break;
                               }
                               poJSON = poSalesReservationControllers.SalesReservation().SearchTransactionbyFilter(tfsTransactionNo.getText(),true);
                               if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                                   ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                   tfCustomerName.setText(prevCustomer);
                                   tfCustomerName.selectAll();
                                   return;
                               }
                               loadRecordMaster();
                               loadTableDetailList();
                               pnEditMode = EditMode.READY;
                               initButton(pnEditMode);
                               break;
                        }
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
        } catch (ExceptionInInitializerError | NullPointerException | SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(SalesReservation_EntryLPController.class
                    .getName()).log(Level.SEVERE, null, ex);
        } 
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
    
    
    
    private void ClickButton() {
        Button[] buttons = {
            btnBrowse,
            btnSearch,
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
                   
                    case "btnBrowse":
                        poJSON = poSalesReservationControllers.SalesReservation().initFields();
                        if("error".equals(poJSON.get("result"))){
                            ShowMessageFX.Error((String) poJSON.get("message"), psFormName, null);
                            break;
                        }
                        poJSON = poSalesReservationControllers.SalesReservation().SearchTransaction(tfsCustomerName.getText());
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning( (String) poJSON.get("message"), psFormName,null);
                            tfCustomerName.setText(prevCustomer);
                            tfCustomerName.selectAll();
                            return;
                        }
                        loadRecordMaster();
                        loadTableDetailList();
                        pnEditMode = EditMode.READY;
                        initButton(pnEditMode);
                        break;
                    
                    case "btnHistory":
                        
                        break;
                    case "btnSearch":
                        break;   
                }
            } catch (CloneNotSupportedException | SQLException | GuanzonException  ex) {
                Logger.getLogger(SalesReservation_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
            Logger.getLogger(SalesReservation_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private void loadRecordDetail(){
        try {
            tfBarcode.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getBarCode() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getBarCode() : "");
            tfDescription.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getDescription() : "");
            
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
            tfInvType.setText(poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().InventoryType().getDescription() != null
                    ? poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().InventoryType().getDescription() : "");
            
            tfUnitPrice.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).Inventory().getCost(), true));
            tfDownPayment.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).getMinimumDown(), true));
            tfQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    poSalesReservationControllers.SalesReservation().Detail(pnDetailRow).getQuantity(), false));
           
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesReservation_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                        loadRecordDetail();
                    } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                        Logger.getLogger(SalesReservation_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
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
    
    
    
    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.UNKNOWN);
       
        btnSearch.setVisible(true);
        btnSearch.setManaged(true);
       
        btnBrowse.setVisible(!lbShow);
        btnBrowse.setManaged(!lbShow);
        
        btnClose.setVisible(true);
        btnClose.setManaged(true);
        
        btnHistory.setVisible(!lbShow);
        btnHistory.setManaged(!lbShow);
    }
    
    
    private void clearDetail(){
            TextInputControl[] txtFieldInputs = {
            tfBrand,
            tfModel,
            tfVariant,
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
                    Logger.getLogger(SalesReservation_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (GuanzonException ex) {
                    Logger.getLogger(SalesReservation_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
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
                        Logger.getLogger(SalesReservation_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        );
    }
    
}
