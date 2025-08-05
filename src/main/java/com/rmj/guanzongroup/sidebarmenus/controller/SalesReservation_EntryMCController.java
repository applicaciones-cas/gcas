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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    private static final int ROWS_PER_PAGE = 50;
    
    private ObservableList<ModelSalesReservationSource> source_data = FXCollections.observableArrayList();
    
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
    @FXML private TableView<ModelSalesReservationDetail> tblDetailList;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDRowNo;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDStockID;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDClassify;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDIssued;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDCancelled;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDUnitPrice;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDMinDown;
    @FXML private TableColumn<ModelSalesReservationDetail, String> tblDTotalAmount;

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
            tfAmountPaid
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
                    case "tfBrand":

                        break;
                    case "tfModel":

                        break;
                    default:
                        break;
                }
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

                        break;
                    case "taNotes":

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
                            case "tfSupplier":
                                
                                break;
                            case "tfDestination":
                               
                                break;
                            case "tfTerm":
                                
                                break;
                            case "tfBrand":
                               
                                break;
                            case "tfModel":
                              
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
        } catch (ExceptionInInitializerError | NullPointerException ex) {
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
                        pnEditMode = EditMode.READY;
                        initButton(pnEditMode);
                        break;
                    case "btnNew":
                        poJSON = poSalesReservationControllers.SalesReservation().initFields();
                        if("error".equals(poJSON.get("result"))){
                            ShowMessageFX.Error((String) poJSON.get("message"), psFormName, null);
                            break;
                        }
                        poSalesReservationControllers.SalesReservation().NewTransaction();
                        loadRecordMaster();
                        pnEditMode =poSalesReservationControllers.SalesReservation().getEditMode();
                        initButton(pnEditMode);
                        break;
                    case "btnBrowse":
//                        poSalesReservationControllers.SalesReservation().Master().setIndustryID(psIndustryID);
//                        poSalesReservationControllers.SalesReservation().setCompanyID(psCompanyID);
//                        poSalesReservationControllers.SalesReservation().setBranchCode(poApp.getBranchCode());
//                        poSalesReservationControllers.SalesReservation().setTransactionStatus(Sales_Reservation_Static.OPEN);
                        String ClientID = poSalesReservationControllers.SalesReservation().Master().getClientID();
                        poJSON = poSalesReservationControllers.SalesReservation().SearchTransaction(tfCustomerName.getText().toString(),ClientID);
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning( (String) poJSON.get("message"), psFormName,null);
                            return;
                        }
                        
                        pnEditMode = EditMode.READY;
                        initButton(pnEditMode);
                        break;
                    case "btnUpdate":
                        pnEditMode = EditMode.UPDATE;
                        initButton(pnEditMode);
                        break;
                    case "btnCancel":
                        pnEditMode = EditMode.READY;
                        initButton(pnEditMode);
                        break;
                    case "btnSave":
                        break;
                    case "btnRetrieve":
                        loadTableSourceList();
                        break;
                }
            } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
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
    }
    
    private void initTableDetailList() {
        tblDRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblDStockID.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblDClassify.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblDIssued.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblDCancelled.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblDUnitPrice.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblDMinDown.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblDTotalAmount.setCellValueFactory(new PropertyValueFactory<>("index08"));

        tblDetailList.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblDetailList.lookup("TableHeaderRow");
            if (header != null) {
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            }
        });
    }
    
    
    private void loadRecordMaster(){
        try {
            tfTransactionNo.setText(poSalesReservationControllers.SalesReservation().Master().getTransactionNo());
            dpTransaction.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poSalesReservationControllers.SalesReservation().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
            dpExpedtedDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poSalesReservationControllers.SalesReservation().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SalesReservation_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void loadRecordDetail(){
        try {
            tfTransactionNo.setText(poSalesReservationControllers.SalesReservation().Master().getTransactionNo());
            dpTransaction.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poSalesReservationControllers.SalesReservation().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
            dpExpedtedDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poSalesReservationControllers.SalesReservation().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
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
                                    JSONObject obj = (JSONObject) requestObj;
                                    ModelSalesReservationSource loMain = new ModelSalesReservationSource(
                                            String.valueOf(source_data.size() + 1),
                                            obj.get("sTransNox") != null ? obj.get("sTransNox").toString() : "",
                                            obj.get("dTransact") != null ? obj.get("dTransact").toString() : "",
                                            obj.get("source") != null ? obj.get("source").toString() : ""
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
    
    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        btnRetrieve.setVisible(lbShow);
        btnRetrieve.setManaged(lbShow);
        btnCancel.setVisible(lbShow);
        btnCancel.setManaged(lbShow);
        btnSave.setVisible(lbShow);
        btnSave.setManaged(lbShow);
        btnUpdate.setVisible(!lbShow);
        btnUpdate.setManaged(!lbShow);
        btnBrowse.setVisible(!lbShow);
        btnBrowse.setManaged(!lbShow);
        btnNew.setVisible(!lbShow);
        btnNew.setManaged(!lbShow);
        btnClose.setVisible(true);
        btnClose.setManaged(true);
    }
    
    
}
