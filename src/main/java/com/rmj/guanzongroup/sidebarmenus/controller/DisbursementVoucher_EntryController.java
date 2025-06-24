/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDisbursementVoucher_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDisbursementVoucher_Main;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelJournalEntry_Detail;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.Pair;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.appdriver.constant.UserRight;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.cashflow.Disbursement;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;
import ph.com.guanzongroup.cas.cashflow.status.DisbursementStatic;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DisbursementVoucher_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    private int pnMain = 0;
    private int pnDetailDV = 0;
    private boolean lsIsSaved = false;
    private final String pxeModuleName = "Disbursement Voucher";
    private Disbursement poDisbursementController;
    public int pnEditMode;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psTransactionNo = "";
    private String psTransactionType = "";
    private String psOldDate = "";

    private unloadForm poUnload = new unloadForm();
    private ObservableList<ModelDisbursementVoucher_Detail> detailsdv_data = FXCollections.observableArrayList();
    private FilteredList<ModelDisbursementVoucher_Detail> filteredDataDetailDV;

    private ObservableList<ModelDisbursementVoucher_Main> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelDisbursementVoucher_Main> filteredMain_Data;

    private ObservableList<ModelJournalEntry_Detail> journal_data = FXCollections.observableArrayList();
    private FilteredList<ModelJournalEntry_Detail> filteredJournal_Data;

    private Object lastFocusedTextField = null;
    private Object previousSearchedTextField = null;
    private boolean pbEnteredDV = false;
    private boolean pbEnteredJournal = false;

    List<Pair<String, String>> plOrderNoPartial = new ArrayList<>();
    List<Pair<String, String>> plOrderNoFinal = new ArrayList<>();

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    private final Map<Integer, List<String>> highlightedRowsDetail = new HashMap<>();

    private ChangeListener<String> detailSearchListener;
    private ChangeListener<String> mainSearchListener;

    ObservableList<String> cTransactionType = FXCollections.observableArrayList(DisbursementStatic.SourceCode.ACCOUNTS_PAYABLE, // "SOA"
            DisbursementStatic.SourceCode.PAYMENT_REQUEST, // "PRF"
            DisbursementStatic.SourceCode.CASH_PAYABLE,
            "ALL");
    ObservableList<String> cPaymentMode = FXCollections.observableArrayList(
            "CHECK", "WIRED", "DIGITAL PAYMENT");
    ObservableList<String> cDisbursementMode = FXCollections.observableArrayList("DELIVER", "PICK-UP");
    ObservableList<String> cPayeeType = FXCollections.observableArrayList("INDIVIDUAL", "CORPORATION");
    ObservableList<String> cClaimantType = FXCollections.observableArrayList("AUTHORIZED REPRESENTATIVE", "PAYEE");
    ObservableList<String> cOtherPayment = FXCollections.observableArrayList("FLOATING");
    ObservableList<String> cOtherPaymentBTransfer = FXCollections.observableArrayList("FLOATING");
    /* DV  & Journal */
    @FXML
    private TabPane tabPaneMain;
    @FXML
    private AnchorPane AnchorMain, apButton;
    @FXML
    private Tab tabDetails, tabJournal;
    @FXML
    private Label lblSource;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnHistory, btnRetrieve, btnClose;

    /*DV Master*/
    @FXML
    private AnchorPane apDVMaster1, apDVMaster2, apDVMaster3;
    @FXML
    private ComboBox<String> cmbTransactionType;
    @FXML
    private TabPane tabPanePaymentMode;
    @FXML
    private TextField tfDVTransactionNo, tfSupplier, tfVoucherNo;
    @FXML
    private ComboBox<String> cmbPaymentMode;
    @FXML
    private TableView tblVwList;
    @FXML
    private TableColumn tblRowNo, tblTransactionType, tblDueDate, tblRefNo, tblAmountMain;
    @FXML
    private Pagination pagination;

    /*DV Master Payment Mode Tabs */
    @FXML
    private Tab tabCheck, tabBankTransfer, tabOnlinePayment;

    @FXML
    private DatePicker dpDVTransactionDate;
    @FXML
    private Label lblDVTransactionStatus;
    @FXML
    private TextField tfVatableSales, tfVatRate, tfVatAmountMaster, tfVatZeroRatedSales, tfVatExemptSales, tfTotalAmount, tfLessWHTax, tfTotalNetAmount;
    @FXML
    private TextArea taDVRemarks;

    /*DV Master Payment Mode Tabs */
 /*DV Master Payment Mode Tabs  = Check*/
    @FXML
    private AnchorPane apMasterDVCheck;
    @FXML
    private TextField tfPayeeName, tfCheckNo, tfCheckAmount, tfBankNameCheck, tfBankAccountCheck;
    @FXML
    private DatePicker dpCheckDate;
    @FXML
    private CheckBox chbkPrintByBank;
    @FXML
    private ComboBox<String> cmbPayeeType, cmbDisbursementMode, cmbClaimantType;
    @FXML
    private TextField tfAuthorizedPerson;
    @FXML
    private CheckBox chbkIsCrossCheck, chbkIsPersonOnly;

    /*DV Master Payment Mode Tabs  = Bank Transfer /Other Payment*/
    @FXML
    private AnchorPane apMasterDVBTransfer;
    @FXML
    private TextField tfPaymentAmountBTransfer, tfSupplierBank, tfSupplierAccountNoBTransfer, tfBankTransReferNo, tfBankNameBTransfer, tfBankAccountBTransfer;
    @FXML
    private ComboBox<String> cmbOtherPaymentBTransfer;

    /*DV Master Payment Mode Tabs  = Online Payment/Other Payment*/
    @FXML
    private AnchorPane apMasterDVOp;
    @FXML
    private TextField tfPaymentAmount, tfSupplierServiceName, tfSupplierAccountNo, tfPaymentReferenceNo, tfBankNameOnlinePayment, tfBankAccountOnlinePayment;
    @FXML
    private ComboBox<String> cmbOtherPayment;

    /*DV Detail*/
    @FXML
    private AnchorPane apDVDetail;
    @FXML
    private TextField tfRefNoDetail, tfParticularsDetail, tfAccountCodeDetail, tfPurchasedAmountDetail, tfTaxCodeDetail, tfTaxRateDetail, tfTaxAmountDetail, tfNetAmountDetail;
    @FXML
    private CheckBox chbkVatClassification;
    @FXML
    private TableView tblVwDetails;
    @FXML
    private TableColumn tblDVRowNo, tblReferenceNo, tblAccountCode, tblTransactionTypeDetail, tblParticulars, tblPurchasedAmount, tblTaxCode, tblTaxAmount, tblNetAmount;

    /*Journal Master */
    @FXML
    private AnchorPane apJournalMaster, apJournalDetails;
    @FXML
    private TextField tfJournalTransactionNo, tfAccountCode, tfAccountDescription, tfCreditAmount, tfDebitAmount, tfTotalDebitAmount, tfTotalCreditAmount;
    @FXML
    private DatePicker dpJournalTransactionDate, dpReportMonthYear;
    @FXML
    private TextArea taJournalRemarks;
    @FXML
    private Label lblJournalTransactionStatus;
    @FXML
    private TableView tblVwJournalDetails;
    @FXML
    private TableColumn tblJournalRowNo, tblJournalAccountCode, tblJournalAccountDescription, tblJournalDebitAmount, tblJournalCreditAmount, tblJournalReportMonthYear;

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
        psIndustryId = fsValue;
    }

    @Override
    public void setCompanyID(String fsValue) {
        psCompanyId = fsValue;
    }

    @Override
    public void setCategoryID(String fsValue) {
        psCategoryId = fsValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            poDisbursementController = new CashflowControllers(oApp, null).Disbursement();
            poJSON = new JSONObject();
            poJSON = poDisbursementController.InitTransaction(); // Initialize transaction
            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }
            initAll();
            Platform.runLater(() -> {
                poDisbursementController.Master().setIndustryID(psIndustryId);
                poDisbursementController.Master().setCompanyID(psCompanyId);
                poDisbursementController.setIndustryID(psIndustryId);
                poDisbursementController.setCompanyID(psCompanyId);
                loadRecordSearch();
                cmbTransactionType.getSelectionModel().select(DisbursementStatic.SourceCode.LOAD_ALL);
                psTransactionType = DisbursementStatic.SourceCode.LOAD_ALL;
                btnNew.fire();
            });
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initAll() {
        initButtonsClickActions();
        initTextFieldsDV();
        initTextFieldsJournal();
        initTextAreaFields();
        initComboBox();
        initCheckBox();
        initDatePicker();
        initTableDetailDV();
        initTableMain();
        initTableDetailJournal();
        initTableOnClick();
        initTextFieldsProperty();
        clearFields();
        pnEditMode = EditMode.UNKNOWN;
        initFields(pnEditMode);
        initButton(pnEditMode);
        pagination.setPageCount(0);
    }

    private void loadRecordSearch() {
        try {
            lblSource.setText(poDisbursementController.Master().Company().getCompanyName() + " - " + poDisbursementController.Master().Industry().getDescription());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnRetrieve, btnHistory, btnClose);
        buttons.forEach(button -> button.setOnAction(this::cmdButton_Click));
    }

    private void cmdButton_Click(ActionEvent event) {
        try {
            poJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch (lsButton) {
                case "btnBrowse":
                    poDisbursementController.setTransactionStatus(DisbursementStatic.OPEN);
                    poJSON = poDisbursementController.SearchTransaction("");
                    if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                    showRetainedHighlight(false);
                    pnEditMode = poDisbursementController.getEditMode();
                    psSupplierId = poDisbursementController.Master().getPayeeID();
                    loadTableDetailDV();
                    break;
                case "btnNew":
                    clearFields();
                    poDisbursementController.initFields();
                    poJSON = poDisbursementController.NewTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    poDisbursementController.Master().setVoucherNo(poDisbursementController.getVoucherNo());
                    poDisbursementController.Master().setDisbursementType(DisbursementStatic.DisbursementType.CHECK);
                    poDisbursementController.Master().setPayeeID(psSupplierId);
                    CustomCommonUtil.switchToTab(tabDetails, tabPaneMain);
                    CustomCommonUtil.switchToTab(tabCheck, tabPanePaymentMode);
                    loadTableDetailDV();
                    pnEditMode = poDisbursementController.getEditMode();
                    break;
                case "btnUpdate":
                    poJSON = poDisbursementController.UpdateTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    pnEditMode = poDisbursementController.getEditMode();
                    loadTableDetailDV();
                    break;
                case "btnSearch":
                    String lsMessage = "Focus a searchable textfield to search";
                    if ((lastFocusedTextField != null)) {
                        if (lastFocusedTextField instanceof TextField) {
                            TextField tf = (TextField) lastFocusedTextField;
                            if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apDVDetail, apDVDetail, apDVMaster2, apMasterDVCheck, apMasterDVOp, apDVDetail).contains(tf.getId())) {
                                if (lastFocusedTextField == previousSearchedTextField) {
                                    break;
                                }
                                previousSearchedTextField = lastFocusedTextField;
                                KeyEvent keyEvent = new KeyEvent(
                                        KeyEvent.KEY_PRESSED,
                                        "",
                                        "",
                                        KeyCode.F3,
                                        false, false, false, false
                                );
                                tf.fireEvent(keyEvent);
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, lsMessage);
                            }
                        } else {
                            ShowMessageFX.Information(null, pxeModuleName, lsMessage);
                        }
                    } else {
                        ShowMessageFX.Information(null, pxeModuleName, lsMessage);
                    }
                    break;
                case "btnSave":
                    if (!ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to save the transaction?")) {
                        return;
                    }
                    if (!isSavingValid()) {
                        return;
                    }
                    if (pnEditMode == EditMode.UPDATE) {
                        poDisbursementController.Master().setModifiedDate(oApp.getServerDate());
                        poDisbursementController.Master().setModifyingId(oApp.getUserID());
                    }
                    poJSON = poDisbursementController.SaveTransaction();
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    ShowMessageFX.Information((String) poJSON.get("message"), pxeModuleName, null);
                    poJSON = poDisbursementController.OpenTransaction(poDisbursementController.Master().getTransactionNo());
                    if ("success".equals(poJSON.get("result")) && poDisbursementController.Master().getTransactionStatus().equals(DisbursementStatic.OPEN)
                            && ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to verify this transaction?")) {
                        if ("success".equals((poJSON = poDisbursementController.VerifyTransaction("Verified")).get("result"))) {
                            ShowMessageFX.Information((String) poJSON.get("message"), pxeModuleName, null);
                        }
                    }
                    System.out.println("EditMode: " + poDisbursementController.getEditMode());
                    Platform.runLater(() -> btnNew.fire());
                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo("Do you want to disregard changes?", pxeModuleName, null)) {
                        psSupplierId = poDisbursementController.Master().getPayeeID();
                        poDisbursementController.resetMaster();
                        poDisbursementController.resetOthers();
                        poDisbursementController.Detail().clear();
                        poDisbursementController.Master().setIndustryID(psIndustryId);
                        poDisbursementController.Master().setCompanyID(psCompanyId);
                        poDisbursementController.Master().setPayeeID(psSupplierId);
                        clearFields();
                        loadTableDetailDV();
                        CustomCommonUtil.switchToTab(tabDetails, tabPaneMain);
                        CustomCommonUtil.switchToTab(tabCheck, tabPanePaymentMode);
                        pnEditMode = EditMode.UNKNOWN;
                        break;
                    } else {
                        return;
                    }
                case "btnHistory":
                    ShowMessageFX.Warning("Button History is Underdevelopment.", pxeModuleName, null);
                    break;
                case "btnRetrieve":
                    loadTableMain();
                    break;
                case "btnClose":
                    if (ShowMessageFX.YesNo("Are you sure you want to close this Tab?", "Close Tab", null)) {
                        poUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
                    } else {
                        return;
                    }
                    break;
                default:
                    ShowMessageFX.Warning("Please contact admin to assist about no button available", pxeModuleName, null);
                    break;
            }
            initFields(pnEditMode);
            initButton(pnEditMode);
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isSavingValid() {
        int detailCount = poDisbursementController.getDetailCount();
        boolean hasValidItem = false; // True if at least one valid item exists

        if (detailCount == 0) {
            ShowMessageFX.Warning("Your order is empty. Please add at least one item.", pxeModuleName, null);
            return false;
        }
        for (int lnCntr = 0; lnCntr <= detailCount - 1; lnCntr++) {
            String lsSourceNo = (String) poDisbursementController.Detail(lnCntr).getSourceNo();
            if (detailCount == 1) {
                if (lsSourceNo == null || lsSourceNo.trim().isEmpty()) {
                    ShowMessageFX.Warning("Your order must have at least one valid item with a Reference No.", pxeModuleName, null);
                    return false;
                }
            }
            hasValidItem = true;
        }
        if (!hasValidItem) {
            ShowMessageFX.Warning("Invalid item in disbursement detail. Ensure all items have a valid Source No and Amount greater than 0.0000", pxeModuleName, null);
            return false;
        }

        switch (poDisbursementController.Master().getDisbursementType()) {
            case DisbursementStatic.DisbursementType.CHECK:
                if (tfBankNameCheck.getText().isEmpty()) {
                    ShowMessageFX.Warning("Please enter Bank Name.", pxeModuleName, null);
                    return false;
                }
                if (tfBankAccountCheck.getText().isEmpty()) {
                    ShowMessageFX.Warning("Please enter Bank Account.", pxeModuleName, null);
                    return false;
                }
                if (tfPayeeName.getText().isEmpty()) {
                    ShowMessageFX.Warning("Please enter Payee Name.", pxeModuleName, null);
                    return false;
                }
                if (chbkPrintByBank.isSelected()) {
                    if (cmbPayeeType.getSelectionModel().getSelectedIndex() < 0) {
                        ShowMessageFX.Warning("Please select Payee Type.", pxeModuleName, null);
                        return false;
                    }
                    if (cmbDisbursementMode.getSelectionModel().getSelectedIndex() < 0) {
                        ShowMessageFX.Warning("Please select Disbursement Mode.", pxeModuleName, null);
                        return false;
                    }
                    if (cmbDisbursementMode.getSelectionModel().getSelectedIndex() == 1) {
                        if (cmbClaimantType.getSelectionModel().getSelectedIndex() < 0) {
                            ShowMessageFX.Warning("Please select Claimant Type.", pxeModuleName, null);
                            return false;
                        }
                        if (cmbClaimantType.getSelectionModel().getSelectedIndex() == 0) {
                            if (tfAuthorizedPerson.getText().trim().isEmpty()) {
                                ShowMessageFX.Warning("Please enter Authorized Person.", pxeModuleName, null);
                                return false;
                            }
                        }

                    }

                }

                break;
            case DisbursementStatic.DisbursementType.WIRED:
//                if (tfBankNameBTransfer.getText().isEmpty()) {
//                    ShowMessageFX.Warning("Please enter Bank Name.", pxeModuleName, null);
//                    return false;
//                }
//                if (tfBankAccountBTransfer.getText().isEmpty()) {
//                    ShowMessageFX.Warning("Please enter Bank Account.", pxeModuleName, null);
//                    return false;
//                }
//                if (tfSupplierBank.getText().isEmpty()) {
//                    ShowMessageFX.Warning("Please enter Supplier Bank.", pxeModuleName, null);
//                    return false;
//                }
//                if (tfSupplierAccountNoBTransfer.getText().isEmpty()) {
//                    ShowMessageFX.Warning("Please enter Supplier Account No.", pxeModuleName, null);
//                    return false;
//                }
                break;
            case DisbursementStatic.DisbursementType.DIGITAL_PAYMENT:
//                if (tfBankNameOnlinePayment.getText().isEmpty()) {
//                    ShowMessageFX.Warning("Please enter Bank Name.", pxeModuleName, null);
//                    return false;
//                }
//                if (tfBankAccountOnlinePayment.getText().isEmpty()) {
//                    ShowMessageFX.Warning("Please enter Bank Account.", pxeModuleName, null);
//                    return false;
//                }
//                    if (tfSupplierServiceName.getText().isEmpty()) {
//                        ShowMessageFX.Warning("Please enter Supplier Service Name.", pxeModuleName, null);
//                        return false;
//                    }
//                    if (tfSupplierAccountNo.getText().isEmpty()) {
//                        ShowMessageFX.Warning("Please enter Supplier Account No.", pxeModuleName, null);
//                        return false;
//                    }
                break;
        }
        return true;
    }

    private void showRetainedHighlight(boolean isRetained) {
        if (isRetained) {
            for (Pair<String, String> pair : plOrderNoPartial) {
                if (!"0".equals(pair.getValue())) {
                    plOrderNoFinal.add(new Pair<>(pair.getKey(), pair.getValue()));
                }
            }
        }
        JFXUtil.disableAllHighlightByColor(tblVwList, "#A7C7E7", highlightedRowsMain);
        plOrderNoPartial.clear();
        for (Pair<String, String> pair : plOrderNoFinal) {
            if (!"0".equals(pair.getValue())) {
                JFXUtil.highlightByKey(tblVwList, pair.getKey(), "#A7C7E7", highlightedRowsMain);
            }
        }
    }

    private void loadTableMain() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblVwList.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        poJSON = new JSONObject();
        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
                Platform.runLater(() -> {
                    try {
                        main_data.clear();
                        plOrderNoFinal.clear();
                        poJSON = poDisbursementController.getUnifiedPayments(psTransactionType);
                        if ("success".equals(poJSON.get("result"))) {
                            JSONArray unifiedPayments = (JSONArray) poJSON.get("data");
                            if (unifiedPayments != null && !unifiedPayments.isEmpty()) {
                                for (Object requestObj : unifiedPayments) {
                                    JSONObject obj = (JSONObject) requestObj;
                                    ModelDisbursementVoucher_Main loMain = new ModelDisbursementVoucher_Main(
                                            String.valueOf(main_data.size() + 1),
                                            obj.get("TransactionType") != null ? obj.get("TransactionType").toString() : "",
                                            obj.get("dTransact") != null ? obj.get("dTransact").toString() : "",
                                            obj.get("sTransNox") != null ? obj.get("sTransNox").toString() : "",
                                            obj.get("Balance") != null ? CustomCommonUtil.setIntegerValueToDecimalFormat(obj.get("Balance"), true) : ""
                                    );
                                    main_data.add(loMain);
                                }
                            } else {
                                main_data.clear();
                            }
                        }
                        showRetainedHighlight(true);
                        if (main_data.isEmpty()) {
                            tblVwList.setPlaceholder(placeholderLabel);
                        }
                        JFXUtil.loadTab(pagination, main_data.size(), ROWS_PER_PAGE, tblVwList, filteredMain_Data);
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(DisbursementVoucher_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                return null;
            }

            @Override

            protected void succeeded() {
                btnRetrieve.setDisable(false);
                placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed
                if (main_data == null || main_data.isEmpty()) {
                    tblVwList.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                } else {
                    pagination.setPageCount(0);
                    pagination.setVisible(true);
                    pagination.setManaged(true);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    tblVwList.toFront();
                }
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblVwList.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                }
                btnRetrieve.setDisable(false);
                progressIndicator.setVisible(false);
                progressIndicator.setManaged(false);
                tblVwList.toFront();
            }
        };
        new Thread(task).start(); // Run task in background
    }

    private void initTableMain() {
        JFXUtil.setColumnCenter(tblRowNo, tblTransactionType, tblDueDate, tblRefNo);
        JFXUtil.setColumnRight(tblAmountMain);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwList);

        filteredMain_Data = new FilteredList<>(main_data, b -> true);
        tblVwList.setItems(filteredMain_Data);
    }

    private void loadRecordMasterDV() {
        try {
            poJSON = new JSONObject();
            tfDVTransactionNo.setText(poDisbursementController.Master().getTransactionNo() != null ? poDisbursementController.Master().getTransactionNo() : "");
            dpDVTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
            tfVoucherNo.setText(poDisbursementController.Master().getVoucherNo());
            lblDVTransactionStatus.setText(getStatus(poDisbursementController.Master().getTransactionStatus()));
            cmbPaymentMode.getSelectionModel().select(!poDisbursementController.Master().getDisbursementType().equals("") ? Integer.valueOf(poDisbursementController.Master().getDisbursementType()) : -1);
            switch (poDisbursementController.Master().getDisbursementType()) {
                case DisbursementStatic.DisbursementType.CHECK:
                    poJSON = poDisbursementController.setCheckpayment();
                    if ("error".equals((String) poJSON.get("message"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        break;
                    }
                    loadRecordMasterCheck();
                    break;
                case DisbursementStatic.DisbursementType.WIRED:
                    poJSON = poDisbursementController.setCheckpayment();
                    if ("error".equals((String) poJSON.get("message"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        break;
                    }
//
                    loadRecordMasterBankTransfer();
                    break;
                case DisbursementStatic.DisbursementType.DIGITAL_PAYMENT:
                    loadRecordMasterOnlinePayment();
                    break;
            }
            taDVRemarks.setText(poDisbursementController.Master().getRemarks());
            tfVatableSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATSale(), true));
            tfVatRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATRates(), false));
            tfVatAmountMaster.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATAmount(), true));
            tfVatZeroRatedSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getZeroVATSales(), true));
            tfVatExemptSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATExmpt(), true));
            tfTotalAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getTransactionTotal(), true));
            tfLessWHTax.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getWithTaxTotal(), true));
            tfTotalNetAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getNetTotal(), true));
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getStatus(String lsValueStatus) {
        String lsStatus;
        switch (lsValueStatus) {
            case DisbursementStatic.OPEN:
                lsStatus = "OPEN";
                break;
            case DisbursementStatic.VERIFIED:
                lsStatus = "VERIFIED";
                break;
            case DisbursementStatic.CERTIFIED:
                lsStatus = "CERTIFIED";
                break;
            case DisbursementStatic.CANCELLED:
                lsStatus = "CANCELLED";
                break;
            case DisbursementStatic.AUTHORIZED:
                lsStatus = "AUTHORIZED";
                break;
            case DisbursementStatic.VOID:
                lsStatus = "VOID";
                break;
            case DisbursementStatic.DISAPPROVED:
                lsStatus = "DISAPPROVED";
                break;
            case DisbursementStatic.RETURNED:
                lsStatus = "RETURNED";
                break;
            default:
                lsStatus = "STATUS";
                break;
        }
        return lsStatus;
    }

    private void loadRecordMasterCheck() {
        try {
            tfBankNameCheck.setText(poDisbursementController.CheckPayments().getModel().Banks().getBankName() != null ? poDisbursementController.CheckPayments().getModel().Banks().getBankName() : "");
            tfBankAccountCheck.setText(poDisbursementController.CheckPayments().getModel().getBankAcountID() != null ? poDisbursementController.CheckPayments().getModel().getBankAcountID() : "");
            tfPayeeName.setText(poDisbursementController.Master().Payee().getPayeeName() != null ? poDisbursementController.Master().Payee().getPayeeName() : "");
            tfCheckNo.setText(poDisbursementController.CheckPayments().getModel().getCheckNo());
            dpCheckDate.setValue(poDisbursementController.CheckPayments().getModel().getCheckDate() != null
                    ? CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.CheckPayments().getModel().getCheckDate(), SQLUtil.FORMAT_SHORT_DATE))
                    : null);
            tfCheckAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.CheckPayments().getModel().getAmount(), true));
            chbkPrintByBank.setSelected(poDisbursementController.Master().getBankPrint().equals(Logical.YES));
            cmbPayeeType.getSelectionModel().select(!poDisbursementController.CheckPayments().getModel().getPayeeType().equals("") ? Integer.valueOf(poDisbursementController.CheckPayments().getModel().getPayeeType()) : -1);
            cmbDisbursementMode.getSelectionModel().select(!poDisbursementController.CheckPayments().getModel().getDesbursementMode().equals("") ? Integer.valueOf(poDisbursementController.CheckPayments().getModel().getDesbursementMode()) : -1);
            cmbClaimantType.getSelectionModel().select(!poDisbursementController.CheckPayments().getModel().getClaimant().equals("") ? Integer.valueOf(poDisbursementController.CheckPayments().getModel().getClaimant()) : -1);
            tfAuthorizedPerson.setText(poDisbursementController.CheckPayments().getModel().getAuthorize() != null ? poDisbursementController.CheckPayments().getModel().getAuthorize() : "");
            chbkIsCrossCheck.setSelected(poDisbursementController.CheckPayments().getModel().isCross());
            chbkIsPersonOnly.setSelected(poDisbursementController.CheckPayments().getModel().isPayee());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadRecordMasterBankTransfer() {
        tfBankNameBTransfer.setText("");
        tfBankAccountBTransfer.setText("");
        tfPaymentAmountBTransfer.setText("");
        tfSupplierBank.setText("");
        tfSupplierAccountNoBTransfer.setText("");
        tfBankTransReferNo.setText("");
        cmbOtherPaymentBTransfer.getSelectionModel().select(null);
    }

    private void loadRecordMasterOnlinePayment() {
        tfPaymentAmount.setText("");
        tfSupplierServiceName.setText("");
        tfSupplierAccountNo.setText("");
        tfPaymentReferenceNo.setText("");
        tfBankNameOnlinePayment.setText("");
        tfBankAccountOnlinePayment.setText("");
        cmbOtherPayment.getSelectionModel().select(null);
    }

    private void loadRecordDetailDV() {
        if (pnDetailDV >= 0) {
            try {
                tfRefNoDetail.setText(poDisbursementController.Detail(pnDetailDV).getSourceNo());
                tfParticularsDetail.setText(poDisbursementController.Detail(pnDetailDV).Particular().getDescription());
                tfAccountCodeDetail.setText(poDisbursementController.Detail(pnDetailDV).getAccountCode());
                tfPurchasedAmountDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(pnDetailDV).getAmount(), true));
                tfTaxCodeDetail.setText(poDisbursementController.Detail(pnDetailDV).TaxCode().getTaxCode());
                tfTaxRateDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(pnDetailDV).getTaxRates(), false));
                tfTaxAmountDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(pnDetailDV).getTaxAmount(), true));
                tfNetAmountDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue()
                        - poDisbursementController.Detail(pnDetailDV).getTaxAmount().doubleValue(), true));
                chbkVatClassification.setSelected(poDisbursementController.Detail(pnDetailDV).isWithVat());

            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(DisbursementVoucher_EntryController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadTableDetailFromMain() {
        poJSON = new JSONObject();
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            pnMain = tblVwList.getSelectionModel().getSelectedIndex();
            ModelDisbursementVoucher_Main selected = (ModelDisbursementVoucher_Main) tblVwList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                    pnMain = pnRowMain;
                    String lsTransactionType = selected.getIndex02();
                    String lsTransactionNo = selected.getIndex04();
                    poJSON = poDisbursementController.addUnifiedPaymentToDisbursement(lsTransactionNo, lsTransactionType);
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
//                    JFXUtil.disableAllHighlightByColor(tblVwList, "#A7C7E7", highlightedRowsMain);
                    JFXUtil.highlightByKey(tblVwList, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                    Platform.runLater(() -> {
                        loadTableDetailDV();
                        initFields(pnEditMode);
                    });

                } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                    Logger.getLogger(DisbursementVoucher_EntryController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void loadTableDetailDV() {
        pbEnteredDV = false;
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblVwDetails.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);
        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    detailsdv_data.clear();
                    int lnCtr;
                    if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                        lnCtr = poDisbursementController.getDetailCount() - 1;
                        if (lnCtr >= 0) {
                            String lsSourceNo = poDisbursementController.Detail(lnCtr).getSourceNo();
                            if (!lsSourceNo.isEmpty()) {
                                try {
                                    poDisbursementController.AddDetail();
                                } catch (CloneNotSupportedException ex) {
                                    Logger.getLogger(DisbursementVoucher_EntryController.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }

                    double lnNetTotal = 0.0000;
                    for (lnCtr = 0; lnCtr < poDisbursementController.getDetailCount(); lnCtr++) {
                        try {
                            lnNetTotal = poDisbursementController.Detail(lnCtr).getAmount().doubleValue() - poDisbursementController.Detail(lnCtr).getTaxAmount().doubleValue();
                            detailsdv_data.add(
                                    new ModelDisbursementVoucher_Detail(String.valueOf(lnCtr + 1),
                                            poDisbursementController.Detail(lnCtr).getSourceNo(),
                                            poDisbursementController.Detail(lnCtr).getAccountCode(),
                                            poDisbursementController.Detail(lnCtr).getInvType(),
                                            poDisbursementController.Detail(lnCtr).Particular().getDescription(),
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(lnCtr).getAmount(), true),
                                            poDisbursementController.Detail(lnCtr).TaxCode().getTaxCode(),
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(lnCtr).getTaxAmount(), true),
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(lnNetTotal, true)
                                    ));

                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(DisbursementVoucher_EntryController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (pnDetailDV < 0 || pnDetailDV
                            >= detailsdv_data.size()) {
                        if (!detailsdv_data.isEmpty()) {
                            tblVwDetails.getSelectionModel().select(0);
                            tblVwDetails.getFocusModel().focus(0);
                            pnDetailDV = tblVwDetails.getSelectionModel().getSelectedIndex();
                            loadRecordDetailDV();
                        }
                    } else {
                        tblVwDetails.getSelectionModel().select(pnDetailDV);
                        tblVwDetails.getFocusModel().focus(pnDetailDV);
                        loadRecordDetailDV();
                    }
                    poJSON = poDisbursementController.computeFields();
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    loadRecordMasterDV();
                });
                return null;
            }

            @Override
            protected void succeeded() {
                if (detailsdv_data == null || detailsdv_data.isEmpty()) {
                    tblVwDetails.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (detailsdv_data == null || detailsdv_data.isEmpty()) {
                    tblVwDetails.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }
        };
        new Thread(task).start();

    }

    private void initTableDetailDV() {
        JFXUtil.setColumnCenter(tblDVRowNo, tblReferenceNo, tblTransactionTypeDetail, tblAccountCode, tblParticulars, tblTaxCode);
        JFXUtil.setColumnRight(tblTaxAmount, tblPurchasedAmount, tblNetAmount);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwDetails);
        filteredDataDetailDV = new FilteredList<>(detailsdv_data, b -> true);

        SortedList<ModelDisbursementVoucher_Detail> sortedData = new SortedList<>(filteredDataDetailDV);
        sortedData.comparatorProperty().bind(tblVwDetails.comparatorProperty());
        tblVwDetails.setItems(sortedData);
        tblVwDetails.autosize();
    }

    private void initTableOnClick() {
        tblVwDetails.setOnMouseClicked(event -> {
            if (!detailsdv_data.isEmpty()) {
                if (event.getClickCount() == 1) {
                    pnDetailDV = tblVwDetails.getSelectionModel().getSelectedIndex();
                    switch (poDisbursementController.Detail(pnDetailDV).getSourceCode()) {
                        case DisbursementStatic.SourceCode.ACCOUNTS_PAYABLE:
                            initDetailClickFocus(true);
                            break;
                        case DisbursementStatic.SourceCode.PAYMENT_REQUEST:
                            initDetailClickFocus(false);
                            break;
                        case DisbursementStatic.SourceCode.CASH_PAYABLE:
                            initDetailClickFocus(true);
                            break;
                        default:
                            loadRecordDetailDV();
                            initFields(pnEditMode);
                            break;
                    }

                }
            }
        });

        tblVwList.setOnMouseClicked(event -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                pnMain = tblVwList.getSelectionModel().getSelectedIndex();
                if (pnMain >= 0) {
                    if (event.getClickCount() == 2) {
                        loadTableDetailFromMain();
                    }
                }
            }
        });

        tblVwList.setRowFactory(tv -> new TableRow<ModelDisbursementVoucher_Main>() {
            @Override
            protected void updateItem(ModelDisbursementVoucher_Main item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    String key = item.getIndex01();
                    if (highlightedRowsMain.containsKey(key)) {
                        List<String> colors = highlightedRowsMain.get(key);
                        if (!colors.isEmpty()) {
                            setStyle("-fx-background-color: " + colors.get(colors.size() - 1) + ";"); // Apply latest color
                        }
                    } else {
                        setStyle(""); // Default style
                    }
                }
            }
        }
        );

        tblVwDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblVwList, tblVwDetails, tblVwJournalDetails);
    }

    private void tableKeyEvents(KeyEvent event) {
        if (!detailsdv_data.isEmpty()) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblVwDetails":
                    if (focusedCell != null) {
                        switch (event.getCode()) {
                            case UP:
                                pnDetailDV = JFXUtil.moveToPreviousRow(currentTable);
                                break;
                            case TAB:
                            case DOWN:
                                pnDetailDV = JFXUtil.moveToNextRow(currentTable);
                                break;
                            default:
                                break;
                        }
                        loadRecordDetailDV();
                        initFields(pnEditMode);
                        event.consume();
                    }
                    break;
            }
        }
    }

    private void initTextFieldsDV() {
        //Initialise  TextField Focus
        JFXUtil.setFocusListener(txtDetailDV_Focus, tfPurchasedAmountDetail);
        JFXUtil.setFocusListener(txtMasterCheck_Focus, tfAuthorizedPerson);
        JFXUtil.setFocusListener(txtMasterBankTransfer_Focus, tfBankNameBTransfer, tfBankAccountBTransfer, tfSupplierBank, tfSupplierAccountNoBTransfer);
        JFXUtil.setFocusListener(txtMasterOnlinePayment_Focus, tfBankNameOnlinePayment, tfBankAccountOnlinePayment, tfSupplierServiceName, tfSupplierAccountNo);

        //Initialise  TextField KeyPressed
        List<TextField> loTxtFieldKeyPressed = Arrays.asList(tfSupplier, tfPayeeName, tfBankNameCheck, tfBankAccountCheck, tfPurchasedAmountDetail, tfTaxCodeDetail, tfParticularsDetail, tfAuthorizedPerson);
        loTxtFieldKeyPressed.forEach(tf -> tf.setOnKeyPressed(event -> txtFieldDV_KeyPressed(event)));
    }

    final ChangeListener<? super Boolean> txtDetailDV_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());

        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;

        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfPurchasedAmountDetail":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.0000";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    poDisbursementController.Detail(pnDetailDV).setAmount(Double.valueOf(lsValue));
                    if (pbEnteredDV) {
                        moveNextDV();
                        pbEnteredDV = false;
                    }
                    break;
            }
        }
    };
    final ChangeListener<? super Boolean> txtMasterCheck_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtMasterCheck = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtMasterCheck.getId());
        String lsValue = (txtMasterCheck.getText() == null ? "" : txtMasterCheck.getText());

        lastFocusedTextField = txtMasterCheck;
        previousSearchedTextField = null;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfAuthorizedPerson":
                    poDisbursementController.CheckPayments().getModel().setAuthorize(lsValue);
                    break;
            }
        }
    };
    final ChangeListener<? super Boolean> txtMasterOnlinePayment_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtMasterOnlinePayment = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtMasterOnlinePayment.getId());
        String lsValue = (txtMasterOnlinePayment.getText() == null ? "" : txtMasterOnlinePayment.getText());

        lastFocusedTextField = txtMasterOnlinePayment;
        previousSearchedTextField = null;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfBankNameOnlinePayment":
                    break;
                case "tfBankAccountOnlinePayment":
                    break;
                case "tfSupplierServiceName":
                    break;
                case "tfSupplierAccountNo":
                    break;
            }
        }
    };
    final ChangeListener<? super Boolean> txtMasterBankTransfer_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtMasterBankTransfer = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtMasterBankTransfer.getId());
        String lsValue = (txtMasterBankTransfer.getText() == null ? "" : txtMasterBankTransfer.getText());

        lastFocusedTextField = txtMasterBankTransfer;
        previousSearchedTextField = null;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfBankNameBTransfer":
                    break;
                case "tfBankAccountBTransfer":
                    break;
                case "tfSupplierBank":
                    break;
                case "tfSupplierAccountNoBTransfer":
                    break;
            }
        }
    };

    private void txtFieldDV_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        String lsID = (((TextField) event.getSource()).getId());
        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
        poJSON = new JSONObject();

        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                        pbEnteredDV = true;
                        CommonUtils.SetNextFocus(txtField);
                        switch (lsID) {
                            case "tfPurchasedAmountDetail":
                            case "tfTaxCodeDetail":
                            case "tfParticularsDetail":
                                tfPurchasedAmountDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(pnDetailDV).getAmount(), true));
                                pnDetailDV = JFXUtil.moveToNextRow(tblVwDetails);
                                moveNextDV();
                                event.consume();
                                break;
                        }
                        event.consume();
                        break;
                    case F3:
                        switch (lsID) {
                            case "tfSupplier":
                                if (!isExchangingSupplier()) {
                                    return;
                                }
                                poJSON = poDisbursementController.SearchPayee(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfSupplier.setText(poDisbursementController.Master().Payee().getPayeeName() != null ? poDisbursementController.Master().Payee().getPayeeName() : "");
                                tfPayeeName.setText(poDisbursementController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? poDisbursementController.Master().Payee().getPayeeName() : "");
                                loadTableMain();
                                break;
                            case "tfBankNameCheck":
                                poJSON = poDisbursementController.SearchBanks(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }

                                tfBankNameCheck.setText(poDisbursementController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? (poDisbursementController.CheckPayments().getModel().Banks().getBankName() != null ? poDisbursementController.CheckPayments().getModel().Banks().getBankName() : "") : "");
                                break;
                            case "tfBankAccountCheck":
                                poJSON = poDisbursementController.SearchBankAccount(lsValue, poDisbursementController.CheckPayments().getModel().getBankID(), false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfBankAccountCheck.setText(poDisbursementController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? (poDisbursementController.CheckPayments().getModel().Bank_Account_Master().getAccountNo() != null ? poDisbursementController.CheckPayments().getModel().Bank_Account_Master().getAccountNo() : "") : "");
                                break;
                            case "tfPayeeName":
                                poJSON = poDisbursementController.SearchPayee(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }

                                tfPayeeName.setText(poDisbursementController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? poDisbursementController.Master().Payee().getPayeeName() : "");
                                break;
                            case "tfParticularsDetail":
                                poJSON = poDisbursementController.SearchParticular(lsValue, pnDetailDV, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                Platform.runLater(() -> {
                                    PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                    delay.setOnFinished(event1 -> {
                                        pnDetailDV = JFXUtil.moveToNextRow(tblVwDetails);
                                        moveNextDV();
                                    });
                                    delay.play();
                                });
                                loadTableDetailDV();
                                break;
                            case "tfAuthorizedPerson":
                                break;
                            case "tfTaxCodeDetail":
                                poJSON = poDisbursementController.SearchTaxCode(lsValue, pnDetailDV, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                Platform.runLater(() -> {
                                    PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                    delay.setOnFinished(event1 -> {
                                        pnDetailDV = JFXUtil.moveToNextRow(tblVwDetails);
                                        moveNextDV();
                                    });
                                    delay.play();
                                });
                                loadTableDetailDV();
                                break;
                        }
                        CommonUtils.SetNextFocus((TextField) event.getSource());
                        event.consume();
                        break;
                    case UP:
                        switch (lsID) {
                            case "tfPurchasedAmountDetail":
                            case "tfTaxCodeDetail":
                            case "tfParticularsDetail":
                                pnDetailDV = JFXUtil.moveToPreviousRow(tblVwDetails);
                                movePreviousDV();
                                event.consume();
                                break;
                        }
                        event.consume();
                        break;
                    case DOWN:
                        switch (lsID) {
                            case "tfPurchasedAmountDetail":
                            case "tfTaxCodeDetail":
                            case "tfParticularsDetail":
                                pnDetailDV = JFXUtil.moveToNextRow(tblVwDetails);
                                moveNextDV();
                                event.consume();
                                break;
                        }
                        event.consume();
                        break;
                    default:
                        break;

                }
            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void movePreviousDV() {
        if (pnDetailDV >= 0) {
            switch (poDisbursementController.Detail(pnDetailDV).getSourceCode()) {
                case DisbursementStatic.SourceCode.ACCOUNTS_PAYABLE:
                    initDetailTblVwKeyFocus(true, false);
                    break;
                case DisbursementStatic.SourceCode.PAYMENT_REQUEST:
                    initDetailTblVwKeyFocus(false, false);
                    break;
                case DisbursementStatic.SourceCode.CASH_PAYABLE:
                    initDetailTblVwKeyFocus(true, false);
                    break;
                default:
                    loadRecordDetailDV();
                    initFields(pnEditMode);
                    break;
            }
        }
    }

    private void moveNextDV() {
        if (pnDetailDV >= 0) {
            switch (poDisbursementController.Detail(pnDetailDV).getSourceCode()) {
                case DisbursementStatic.SourceCode.ACCOUNTS_PAYABLE:
                    initDetailTblVwKeyFocus(true, true);
                    break;
                case DisbursementStatic.SourceCode.PAYMENT_REQUEST:
                    initDetailTblVwKeyFocus(false, true);
                    break;
                case DisbursementStatic.SourceCode.CASH_PAYABLE:
                    initDetailTblVwKeyFocus(true, true);
                    break;
                default:
                    loadRecordDetailDV();
                    initFields(pnEditMode);
                    break;
            }
        }
    }

    private void initDetailClickFocus(boolean isSOACache) {
        String lsSourceNo = poDisbursementController.Detail(pnDetailDV).getSourceNo();
        double amount = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
        String lsTaxCode = poDisbursementController.Detail(pnDetailDV).getTAxCode();
        String lsParticular = poDisbursementController.Detail(pnDetailDV).getParticularID();

        loadRecordDetailDV();
        initFields(pnEditMode);
        if (lsSourceNo.isEmpty()) {
            return;
        }
        if (isSOACache) {
            if (!lsTaxCode.isEmpty() && amount > 0.0000) {
                tfParticularsDetail.requestFocus();
            } else if (lsTaxCode.isEmpty() && lsParticular.isEmpty()) {
                tfParticularsDetail.requestFocus();
            } else if (!lsTaxCode.isEmpty() && amount <= 0.0000 && !lsParticular.isEmpty()) {
                tfPurchasedAmountDetail.requestFocus();
            } else {
                tfTaxCodeDetail.requestFocus();
            }
        } else {
            if (lsTaxCode.isEmpty() && amount > 0.0000) {
                tfTaxCodeDetail.requestFocus();
            } else if (lsTaxCode.isEmpty() && amount <= 0.0000) {
                tfPurchasedAmountDetail.requestFocus();
            }
        }
    }

    private void initDetailTblVwKeyFocus(boolean isSOACache, boolean isNextRow) {
        loadRecordDetailDV();
        initFields(pnEditMode);
        String lsSourceNo = poDisbursementController.Detail(pnDetailDV).getSourceNo();
        double amount = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
        String lsTaxCode = poDisbursementController.Detail(pnDetailDV).getTAxCode();
        String lsParticular = poDisbursementController.Detail(pnDetailDV).getParticularID();
        if (lsSourceNo.isEmpty()) {
            return;
        }
        if (isSOACache) {
            if (!lsTaxCode.isEmpty() && amount > 0.0000) {
                tfParticularsDetail.requestFocus();
            } else if (lsTaxCode.isEmpty() && lsParticular.isEmpty()) {
                tfParticularsDetail.requestFocus();
            } else if (!lsTaxCode.isEmpty() && amount <= 0.0000 && !lsParticular.isEmpty()) {
                tfPurchasedAmountDetail.requestFocus();
            } else {
                tfTaxCodeDetail.requestFocus();
            }
        } else {
            if (lsTaxCode.isEmpty() && amount > 0.0000) {
                tfTaxCodeDetail.requestFocus();
            } else if (lsTaxCode.isEmpty() && amount <= 0.0000) {
                tfPurchasedAmountDetail.requestFocus();
            }
        }
    }

    private void initTextFieldsJournal() {
        JFXUtil.setFocusListener(txtDetailDV_Focus, tfAccountCode, tfAccountDescription, tfDebitAmount, tfCreditAmount);
        //Initialise  TextField KeyPressed
        List<TextField> loTxtFieldKeyPressed = Arrays.asList(tfAccountCode, tfAccountDescription, tfDebitAmount, tfCreditAmount);
        loTxtFieldKeyPressed.forEach(tf -> tf.setOnKeyPressed(event -> txtFieldJoural_KeyPressed(event)));
    }

    private void txtFieldJoural_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        String lsID = (((TextField) event.getSource()).getId());
        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
        poJSON = new JSONObject();
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEnteredJournal = true;
                    CommonUtils.SetNextFocus(txtField);
                    switch (lsID) {
                        case "tfDebitAmount":
                        case "tfCreditAmount":
                            moveNextJournal();
                            event.consume();
                            break;
                    }
                    event.consume();
                    break;
                case F3:
                    switch (lsID) {
                        case "tfAccountCode":
                            break;
                        case "tfAccountDescription":
                            break;
                    }
                    event.consume();
                case UP:
                    switch (lsID) {
                        case "tfDebitAmount":
                        case "tfCreditAmount":
                            movePreviousJournal();
                            event.consume();
                            break;
                    }
                    event.consume();
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfDebitAmount":
                        case "tfCreditAmount":
                            moveNextJournal();
                            event.consume();
                            break;
                    }
                    break;
                default:
                    break;

            }
        }
    }

    private void movePreviousJournal() {
//        double lnPurchaseAmount = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
//        apDVDetail.requestFocus();
//        double lnNewvalue = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
//        if (lnPurchaseAmount != lnNewvalue && (lnPurchaseAmount > 0.0000
//                && poDisbursementController.Detail(pnDetailDV).getSourceNo() != null
//                && !"".equals(poDisbursementController.Detail(pnDetailDV).getSourceNo()))) {
//            tfPurchasedAmountDetail.requestFocus();
//        } else {
//            pnDetailDV = JFXUtil.moveToPreviousRow(tblVwDetails);
//            loadRecordDetailDV();
//            if (poDisbursementController.Detail(pnDetailDV).getSourceNo() != null && !poDisbursementController.Detail(pnDetailDV).getSourceNo().equals("")) {
//                if (poDisbursementController.Detail(pnDetailDV).getTAxCode() != null) {
//                    tfPurchasedAmountDetail.requestFocus();
//                } else {
//                    tfTaxCodeDetail.requestFocus();
//                }
//            } else {
//                tfTaxCodeDetail.requestFocus();
//            }
//        }
    }

    private void moveNextJournal() {
//        double lnPurchaseAmount = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
//        apDVDetail.requestFocus();
//        double lnNewvalue = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
//        if (lnPurchaseAmount != lnNewvalue && (lnPurchaseAmount > 0.0000
//                && poDisbursementController.Detail(pnDetailDV).getSourceNo() != null
//                && !"".equals(poDisbursementController.Detail(pnDetailDV).getSourceNo()))) {
//            tfPurchasedAmountDetail.requestFocus();
//        } else {
//            pnDetailDV = JFXUtil.moveToNextRow(tblVwDetails);
//            loadRecordDetailDV();
//            if (poDisbursementController.Detail(pnDetailDV).getSourceNo() != null && !poDisbursementController.Detail(pnDetailDV).getSourceNo().equals("")) {
//                if (poDisbursementController.Detail(pnDetailDV).getTAxCode() != null) {
//                    tfPurchasedAmountDetail.requestFocus();
//                } else {
//                    tfTaxCodeDetail.requestFocus();
//                }
//            } else {
//                tfTaxCodeDetail.requestFocus();
//            }
//        }
    }

    private void initTableDetailJournal() {
        JFXUtil.setColumnCenter(tblJournalRowNo, tblJournalAccountCode, tblJournalAccountDescription, tblJournalReportMonthYear);
        JFXUtil.setColumnRight(tblJournalCreditAmount, tblJournalDebitAmount);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwJournalDetails);
        filteredJournal_Data = new FilteredList<>(journal_data, b -> true);

        SortedList<ModelJournalEntry_Detail> sortedData = new SortedList<>(filteredJournal_Data);
        sortedData.comparatorProperty().bind(tblVwJournalDetails.comparatorProperty());
        tblVwJournalDetails.setItems(sortedData);
        tblVwJournalDetails.autosize();
    }

    private void initTextAreaFields() {
        //Initialise  TextArea Focus
        taDVRemarks.focusedProperty().addListener(txtArea_Focus);
        taJournalRemarks.focusedProperty().addListener(txtArea_Focus);
        //Initialise  TextArea KeyPressed
        taDVRemarks.setOnKeyPressed(event -> txtArea_KeyPressed(event));
        taJournalRemarks.setOnKeyPressed(event -> txtArea_KeyPressed(event));
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea txtArea = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = (txtArea.getId());
        String lsValue = txtArea.getText();

        lastFocusedTextField = txtArea;
        previousSearchedTextField = null;

        if (lsValue == null) {
            return;
        }
        poJSON = new JSONObject();
        if (!nv) {
            switch (lsID) {
                case "taDVRemarks":
                    poDisbursementController.Master().setRemarks(lsValue);
                    break;
                case "taJournalRemarks":
                    break;
            }
        } else {
            txtArea.selectAll();
        }
    };

    private void txtArea_KeyPressed(KeyEvent event) {
        TextArea txtArea = (TextArea) event.getSource();
        String lsID = txtArea.getId();
        if ("taDVRemarks".equals(lsID) && "taJournalRemarks".equals(lsID)) {
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                case DOWN:
                    CommonUtils.SetNextFocus(txtArea);
                    event.consume();
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(txtArea);
                    event.consume();
                    break;
                default:
                    break;
            }
        }
    }

    private void initComboBox() {
        // Set Items
        cmbTransactionType.setItems(cTransactionType);
        cmbPaymentMode.setItems(cPaymentMode);
        cmbPayeeType.setItems(cPayeeType);
        cmbDisbursementMode.setItems(cDisbursementMode);
        cmbClaimantType.setItems(cClaimantType);
        cmbOtherPayment.setItems(cOtherPayment);
        cmbOtherPaymentBTransfer.setItems(cOtherPaymentBTransfer);

        //ComboBox Actions
        cmbTransactionType.setOnAction(event -> {
            if (cmbTransactionType.getSelectionModel().getSelectedIndex() >= 0) {
                String selected = String.valueOf(cmbTransactionType.getValue());
                switch (selected) {
                    case DisbursementStatic.SourceCode.PAYMENT_REQUEST: // "PRF"
                        psTransactionType = "PRF";
                        break;
                    case DisbursementStatic.SourceCode.CASH_PAYABLE: // "CP"
                        psTransactionType = "CASH";
                        break;
                    case DisbursementStatic.SourceCode.ACCOUNTS_PAYABLE: // "SOA"
                        psTransactionType = "SOA";
                        break;
                    case DisbursementStatic.SourceCode.LOAD_ALL:
                        psTransactionType = DisbursementStatic.SourceCode.LOAD_ALL;
                        break;
                }
                loadTableMain();
            }
        });

        cmbPaymentMode.setOnAction(e -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbPaymentMode.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.Master().setOldDisbursementType(poDisbursementController.Master().getDisbursementType());
                poDisbursementController.Master().setDisbursementType(String.valueOf(cmbPaymentMode.getSelectionModel().getSelectedIndex()));
                loadRecordMasterDV();
            }
            initFields(pnEditMode);
        });

        cmbPayeeType.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbPayeeType.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.CheckPayments().getModel().setPayeeType(String.valueOf(cmbPayeeType.getSelectionModel().getSelectedIndex()));
                initFields(pnEditMode);
            }
        }
        );
        cmbDisbursementMode.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbDisbursementMode.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.CheckPayments().getModel().setDesbursementMode(String.valueOf(cmbDisbursementMode.getSelectionModel().getSelectedIndex()));
                initFields(pnEditMode);
            }
        }
        );
        cmbClaimantType.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbClaimantType.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.CheckPayments().getModel().setClaimant(String.valueOf(cmbClaimantType.getSelectionModel().getSelectedIndex()));
                initFields(pnEditMode);
            }
        }
        );
        cmbOtherPayment.setOnAction(event
                -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbOtherPayment.getSelectionModel().getSelectedIndex() >= 0) {
            }
        }
        );
        cmbOtherPaymentBTransfer.setOnAction(event
                -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbOtherPaymentBTransfer.getSelectionModel().getSelectedIndex() >= 0) {
            }
        }
        );
    }

    private void initDatePicker() {
        dpCheckDate.setOnAction(e -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                LocalDate selectedLocalDate = dpCheckDate.getValue();
                LocalDate transactionDate = new java.sql.Date(poDisbursementController.CheckPayments().getModel().getCheckDate().getTime()).toLocalDate();
                if (selectedLocalDate == null) {
                    return;
                }

                LocalDate dateNow = LocalDate.now();
                psOldDate = CustomCommonUtil.formatLocalDateToShortString(transactionDate);
                boolean approved = true;
                if (pnEditMode == EditMode.UPDATE) {
                    if (!DisbursementStatic.VERIFIED.equals(poDisbursementController.Master().getTransactionStatus())) {
                        psOldDate = CustomCommonUtil.formatLocalDateToShortString(transactionDate);
                        if (selectedLocalDate.isBefore(dateNow)) {
                            ShowMessageFX.Warning("Invalid to back date.", pxeModuleName, null);
                            approved = false;
                        }
                    }
                }
                if (pnEditMode == EditMode.ADDNEW) {
                    if (selectedLocalDate.isBefore(dateNow)) {
                        ShowMessageFX.Warning("Invalid to back date.", pxeModuleName, null);
                        approved = false;
                    }
                }
                if (approved) {
                    poDisbursementController.CheckPayments().getModel().setCheckDate(
                            SQLUtil.toDate(selectedLocalDate.toString(), SQLUtil.FORMAT_SHORT_DATE));
                } else {
                    if (pnEditMode == EditMode.ADDNEW) {
                        dpCheckDate.setValue(dateNow);
                        poDisbursementController.CheckPayments().getModel().setCheckDate(
                                SQLUtil.toDate(dateNow.toString(), SQLUtil.FORMAT_SHORT_DATE));
                    } else if (pnEditMode == EditMode.UPDATE) {
                        poDisbursementController.CheckPayments().getModel().setCheckDate(
                                SQLUtil.toDate(psOldDate, SQLUtil.FORMAT_SHORT_DATE));
                    }
                }
                dpCheckDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                        SQLUtil.dateFormat(poDisbursementController.CheckPayments().getModel().getCheckDate(), SQLUtil.FORMAT_SHORT_DATE)));
            }
        }
        );
    }

    private void initCheckBox() {
        chbkIsCrossCheck.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)) {
                poDisbursementController.CheckPayments().getModel().isCross(chbkIsCrossCheck.isSelected());
            }
        });
        chbkIsPersonOnly.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)) {
                poDisbursementController.CheckPayments().getModel().isPayee(chbkIsPersonOnly.isSelected());
            }
        });
        chbkPrintByBank.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)) {
                poDisbursementController.Master().setBankPrint(chbkPrintByBank.isSelected() == true ? "1" : "0");
                initFields(pnEditMode);
            }
        });
        chbkVatClassification.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)) {
                if (pnDetailDV >= 0) {
                    poDisbursementController.Detail(pnDetailDV).isWithVat(chbkVatClassification.isSelected() == true);
                }
            }
        });
    }

    private void clearFields() {
        previousSearchedTextField = null;
        lastFocusedTextField = null;
        CustomCommonUtil.setText("", tfDVTransactionNo, tfVoucherNo);
        JFXUtil.setValueToNull(null, dpDVTransactionDate, dpJournalTransactionDate, dpCheckDate, dpReportMonthYear);
        JFXUtil.setValueToNull(null, cmbPaymentMode, cmbPayeeType, cmbDisbursementMode, cmbClaimantType, cmbOtherPayment, cmbOtherPaymentBTransfer);
        JFXUtil.clearTextFields(apDVDetail, apDVMaster2, apDVMaster3, apMasterDVCheck, apMasterDVBTransfer, apMasterDVOp, apJournalMaster, apJournalDetails);
        CustomCommonUtil.setSelected(false, chbkIsCrossCheck, chbkPrintByBank, chbkVatClassification);
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        JFXUtil.setDisabled(!lbShow, apDVMaster1, apDVMaster2, apDVMaster3, apJournalMaster, apJournalDetails);
        JFXUtil.setDisabled(true, apDVDetail, apMasterDVCheck, apMasterDVOp, apMasterDVBTransfer, tfAuthorizedPerson);
        tabJournal.setDisable(tfDVTransactionNo.getText().isEmpty());
        tabCheck.setDisable(true);
        tabOnlinePayment.setDisable(true);
        tabBankTransfer.setDisable(true);
        if (main_data.isEmpty()) {
            Label placeholderLabel = new Label("NO RECORD TO LOAD");
            tblVwList.setPlaceholder(placeholderLabel);
            pagination.setManaged(false);
            pagination.setVisible(false);
        }
        switch (poDisbursementController.Master().getDisbursementType()) {
            case DisbursementStatic.DisbursementType.CHECK:
                tabCheck.setDisable(!lbShow);
                apMasterDVOp.setDisable(!lbShow);
                CustomCommonUtil.switchToTab(tabCheck, tabPanePaymentMode);

                boolean isPrintByBank = chbkPrintByBank.isSelected();
                boolean isDisbursementModeSelected = cmbDisbursementMode.getSelectionModel().getSelectedIndex() == 1;
                boolean isClaimantTypeSelected = cmbClaimantType.getSelectionModel().getSelectedIndex() == 0;

                if (isPrintByBank && isDisbursementModeSelected && isClaimantTypeSelected) {
                    tfAuthorizedPerson.setDisable(!lbShow);
                }

//  loadRecordMasterCheck();
                break;

            case DisbursementStatic.DisbursementType.WIRED:
                tabBankTransfer.setDisable(!lbShow);
                apMasterDVBTransfer.setDisable(!lbShow);
                CustomCommonUtil.switchToTab(tabBankTransfer, tabPanePaymentMode);
//                loadRecordMasterBankTransfer();
                break;
            case DisbursementStatic.DisbursementType.DIGITAL_PAYMENT:
                tabOnlinePayment.setDisable(!lbShow);
                apMasterDVOp.setDisable(!lbShow);
                CustomCommonUtil.switchToTab(tabOnlinePayment, tabPanePaymentMode);
//                loadRecordMasterOnlinePayment();
                break;
        }
        if (pnDetailDV >= 0) {
            if (tfRefNoDetail.getText() != null) {
                if (!tfRefNoDetail.getText().isEmpty()) {
                    JFXUtil.setDisabled(!lbShow, apDVDetail);
                    switch (poDisbursementController.Detail(pnDetailDV).getSourceCode()) {
                        case DisbursementStatic.SourceCode.ACCOUNTS_PAYABLE:
                        case DisbursementStatic.SourceCode.CASH_PAYABLE:
                            tfParticularsDetail.setDisable(!lbShow);
                            break;
                    }
                }
            }
        }

        tfSupplier.setDisable(fnEditMode == EditMode.UPDATE);
    }

    private void initButton(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
        JFXUtil.setButtonsVisibility(!lbShow, btnBrowse, btnClose, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(false, btnUpdate);
        JFXUtil.setButtonsVisibility(fnEditMode != EditMode.ADDNEW && fnEditMode != EditMode.UNKNOWN, btnHistory);
        if (fnEditMode == EditMode.READY) {
            switch (poDisbursementController.Master().getTransactionStatus()) {
                case DisbursementStatic.OPEN:
                case DisbursementStatic.VERIFIED:
                    JFXUtil.setButtonsVisibility(true, btnUpdate);
                    break;
            }
        }
    }

    private boolean isExchangingSupplier() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            boolean isHaveQuantityAndStockId = false;
            if (poDisbursementController.getDetailCount() >= 1) {
                if (poDisbursementController.Detail(0).getSourceNo() != null && poDisbursementController.Detail(0).getAmount().doubleValue() != 0.0000) {
                    isHaveQuantityAndStockId = true;
                }
            }
            if (isHaveQuantityAndStockId) {
                if (ShowMessageFX.YesNo("DV Details have already items, are you sure you want to change supplier?", pxeModuleName, null)) {
                    int detailCount = poDisbursementController.getDetailCount();
                    for (int lnCtr = detailCount - 1; lnCtr >= 0; lnCtr--) {
                        if (poDisbursementController.Detail(lnCtr).getSourceNo().isEmpty()
                                && poDisbursementController.Detail(lnCtr).getAmount().doubleValue() == 0.0000) {
                            continue; // Skip deleting this row
                        }
                        poDisbursementController.Detail().remove(lnCtr);
                    }
                    pnDetailDV = -1;
                    pnMain = -1;
                    tblVwList.getSelectionModel().clearSelection();
                    JFXUtil.clearTextFields(apDVDetail);
                    chbkVatClassification.setSelected(false);
                    loadTableDetailDV();
                } else {
                    if (psSupplierId.isEmpty()) {
                        return false;
                    } else {
                        try {
                            poJSON = new JSONObject();
                            poJSON = poDisbursementController.SearchSupplier(psSupplierId, true);
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                return false;
                            }
                            tfSupplier.setText("");
                            return false;

                        } catch (ExceptionInInitializerError | SQLException | GuanzonException ex) {
                            Logger.getLogger(PurchaseOrder_EntryMPController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }
        }
        if (pnEditMode == EditMode.READY) {
            try {
                if (!tfDVTransactionNo.getText().isEmpty()
                        && Double.parseDouble(tfTotalAmount.getText().replace(",", "")) != 0.0000) {
                    if (ShowMessageFX.YesNo("You have an open transaction. Are you sure you want to change the supplier?", pxeModuleName, null)) {
                        clearFields();
                        detailsdv_data.clear();
                        tblVwDetails.getItems().clear();
                        pnEditMode = EditMode.UNKNOWN;
//                        poDisbursementController.Master().setSupplierID();
                        tfSupplier.setText("");
                        pnDetailDV = -1;
                        tblVwList.getItems().clear();
                        tblVwList.setPlaceholder(new Label("NO RECORD TO LOAD"));
                        initButton(pnEditMode);
                        initFields(pnEditMode);
                        return true;
                    } else {
                        poJSON = poDisbursementController.SearchSupplier(psSupplierId, true);
                        if (!"success".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            return false;
                        }
                        tfSupplier.setText("");
                        return false;

                    }
                }
            } catch (ExceptionInInitializerError | SQLException | GuanzonException ex) {
                Logger.getLogger(DisbursementVoucher_EntryController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }

        return true;
    }

    private void initTextFieldsProperty() {
        tfSupplier.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    if (!isExchangingSupplier()) {
                        return;
                    }
                    poDisbursementController.Master().setPayeeID("");
                    tfSupplier.setText("");
                    psSupplierId = "";
                    loadTableMain();
                }
            }
        }
        );
        tfTaxCodeDetail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    if (pnDetailDV >= 0) {
                        poDisbursementController.Detail(pnDetailDV).setTAxCode("");
                        poDisbursementController.Detail(pnDetailDV).setTaxRates(0.00);
                        poDisbursementController.Detail(pnDetailDV).setTaxAmount(0.0000);
                        tfTaxCodeDetail.setText("");
                        tfTaxRateDetail.setText("0.00");
                        tfTaxAmountDetail.setText("0.0000");
                        loadTableDetailDV();
                    }
                }
            }
        }
        );
        tfParticularsDetail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    if (pnDetailDV >= 0) {
                        poDisbursementController.Detail(pnDetailDV).setParticularID("");
                        tfParticularsDetail.setText("");
                        loadTableDetailDV();
                    }
                }
            }
        }
        );
        tfBankNameCheck.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poDisbursementController.CheckPayments().getModel().setBankID("");
                    poDisbursementController.CheckPayments().getModel().setBankAcountID("");
                    tfBankNameCheck.setText("");
                    tfBankAccountCheck.setText("");
                }
            }
        }
        );
        tfBankAccountCheck.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poDisbursementController.CheckPayments().getModel().setBankAcountID("");
                    tfBankAccountCheck.setText("");
                }
            }
        }
        );
        tfPayeeName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poDisbursementController.CheckPayments().getModel().setPayeeID("");
                    tfPayeeName.setText("");
                }
            }
        }
        );
        tfAuthorizedPerson.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poDisbursementController.CheckPayments().getModel().setAuthorize("");
                    tfAuthorizedPerson.setText("");
                }
            }
        }
        );
    }
}
