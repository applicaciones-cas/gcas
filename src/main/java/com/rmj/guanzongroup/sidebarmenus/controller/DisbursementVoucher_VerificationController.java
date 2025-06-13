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
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.Logical;
import org.guanzon.cas.gl.Disbursement;
import org.guanzon.cas.gl.services.GLControllers;
import org.guanzon.cas.gl.status.DisbursementStatic;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DisbursementVoucher_VerificationController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    private int pnMain = 0;
    private int pnDetailDV = 0;
    private boolean lsIsSaved = false;
    private final String pxeModuleName = "Disbursement Voucher Verification";
    private Disbursement poDisbursementController;
    public int pnEditMode;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psTransactionNo = "";

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

    ObservableList<String> cPaymentMode = FXCollections.observableArrayList(
            "CHECK", "WIRED", "DIGITAL PAYMENT");
    ObservableList<String> cDisbursementMode = FXCollections.observableArrayList("DELIVER", "PICK-UP");
    ObservableList<String> cPayeeType = FXCollections.observableArrayList("INDIVIDUAL", "CORPORATION");
    ObservableList<String> cClaimantType = FXCollections.observableArrayList("AUTHORIZED REPRESENTATIVE", "PAYEE");
    ObservableList<String> cOtherPayment = FXCollections.observableArrayList("FLOATING");
    ObservableList<String> cOtherPaymentBTransfer = FXCollections.observableArrayList("FLOATING");
    /* DV  & Journal */
    @FXML
    private TextField tfSearchTransaction, tfSearchSupplier;
    @FXML
    private TabPane tabPaneMain;
    @FXML
    private AnchorPane AnchorMain, apButton;
    @FXML
    private Tab tabDetails, tabJournal;
    @FXML
    private Label lblSource;
    @FXML
    private AnchorPane apBrowse;

    @FXML
    private Button btnUpdate, btnSave, btnCancel, btnVerify, btnVoid, btnDVCancel, btnRetrieve, btnHistory, btnClose;

    /*DV Master*/
    @FXML
    private AnchorPane apDVMaster1, apDVMaster2, apDVMaster3;
    @FXML
    private TabPane tabPanePaymentMode;
    @FXML
    private TextField tfDVTransactionNo, tfSupplier, tfVoucherNo;
    @FXML
    private ComboBox<String> cmbPaymentMode;
    @FXML
    private TableView tblVwDisbursementVoucher;
    @FXML
    private TableColumn tblRowNo, tblSupplier, tblPaymentForm, tblTransDate, tblReferNo;
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
    private TableColumn tblDVRowNo, tblReferenceNo, tblAccountCode, tblParticulars, tblPurchasedAmount, tblTaxCode, tblTaxAmount, tblNetAmount;

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
            poDisbursementController = new GLControllers(oApp, null).Disbursement();

            poDisbursementController.setTransactionStatus(DisbursementStatic.OPEN
                    + DisbursementStatic.VERIFIED);
            poJSON = new JSONObject();
            poJSON = poDisbursementController.InitTransaction(); // Initialize transaction
            if (!"success".equals((String) poJSON.get("result"))) {
                System.err.println((String) poJSON.get("message"));
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }
            initAll();
            Platform.runLater(() -> {
                poDisbursementController.Master().setIndustryID(psIndustryId);
                poDisbursementController.Master().setCompanyID(psCompanyId);
                loadRecordSearch();
            });
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnUpdate, btnSave, btnCancel, btnVerify, btnVoid, btnDVCancel, btnRetrieve, btnHistory, btnClose);
        buttons.forEach(button -> button.setOnAction(this::cmdButton_Click));
    }

    private void cmdButton_Click(ActionEvent event) {
        try {
            poJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch (lsButton) {
                case "btnUpdate":
                    poJSON = poDisbursementController.UpdateTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    loadRecordDetailDV();
                    pnEditMode = poDisbursementController.getEditMode();
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
                    loadRecordMasterDV();
                    loadRecordDetailDV();
                    loadTableDetailDV();
                    pnEditMode = poDisbursementController.getEditMode();
                    pagination.toBack();
                    break;
                case "btnCancel":
                    if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                        JFXUtil.disableAllHighlightByColor(tblVwDisbursementVoucher, "#A7C7E7", highlightedRowsMain);
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
                case "btnVerify":
                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to confirm transaction?")) {
                        poJSON = poDisbursementController.VerifyTransaction("Verified");
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            return;
                        } else {
                            ShowMessageFX.Information((String) poJSON.get("message"), pxeModuleName, null);
                            JFXUtil.disableAllHighlightByColor(tblVwDisbursementVoucher, "#A7C7E7", highlightedRowsMain);
                            plOrderNoPartial.add(new Pair<>(String.valueOf(pnMain + 1), "1"));
                            showRetainedHighlight(true);
                        }
                    } else {
                        return;
                    }
                    break;
                case "btnDVCancel":
                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to cancel transaction?") == true) {
                        poJSON = poDisbursementController.CancelTransaction("Cancelled");
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            return;
                        } else {
                            ShowMessageFX.Information((String) poJSON.get("message"), pxeModuleName, null);
                            pnEditMode = poDisbursementController.getEditMode();
                            JFXUtil.disableAllHighlightByColor(tblVwDisbursementVoucher, "#C1E1C1", highlightedRowsMain);
                            JFXUtil.highlightByKey(tblVwDisbursementVoucher, String.valueOf(pnMain + 1), "#FAA0A0", highlightedRowsMain);
                        }
                    } else {
                        return;
                    }
                    break;
                case "btnVoid":
                    if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to void transaction?") == true) {
                        poJSON = poDisbursementController.VoidTransaction("Voided");
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            return;
                        } else {
                            ShowMessageFX.Information((String) poJSON.get("message"), pxeModuleName, null);
                            pnEditMode = poDisbursementController.getEditMode();
                            JFXUtil.disableAllHighlightByColor(tblVwDisbursementVoucher, "#A7C7E7", highlightedRowsMain);
                            JFXUtil.highlightByKey(tblVwDisbursementVoucher, String.valueOf(pnMain + 1), "#FAA0A0", highlightedRowsMain);
                        }
                    } else {
                        return;
                    }
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
            if (lsButton.equals("btnSave") || lsButton.equals("btnVerify") || lsButton.equals("btnVoid") || lsButton.equals("btnCancel") || lsButton.equals("btnDVCancel")) {
                pnEditMode = EditMode.UNKNOWN;
                clearFields();
                loadRecordMasterDV();
                loadRecordDetailDV();
            }
            initFields(pnEditMode);
            initButton(pnEditMode);
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isSavingValid() {
        switch (poDisbursementController.Master().getDisbursementType()) {
            case DisbursementStatic.DisbursementType.CHECK:
                if (tfBankNameCheck.getText().isEmpty()) {
                    ShowMessageFX.Warning("Please enter Bank Name.", pxeModuleName, null);
                    return false;
                }
//                if (tfBankAccountCheck.getText().isEmpty()) {
//                    ShowMessageFX.Warning("Please enter Bank Account.", pxeModuleName, null);
//                    return false;
//                }
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
                            if (tfAuthorizedPerson.getText().isEmpty()) {
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
        JFXUtil.disableAllHighlightByColor(tblVwDisbursementVoucher, "#C1E1C1", highlightedRowsMain);
        plOrderNoPartial.clear();
        for (Pair<String, String> pair : plOrderNoFinal) {
            if (!"0".equals(pair.getValue())) {
                JFXUtil.highlightByKey(tblVwDisbursementVoucher, pair.getKey(), "#C1E1C1", highlightedRowsMain);
            }
        }
    }

    private void loadTableMain() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblVwDisbursementVoucher.setPlaceholder(loadingPane);
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
                        poJSON = poDisbursementController.getDisbursement(psTransactionNo, psSupplierId);
                        if ("success".equals(poJSON.get("result"))) {
                            if (poDisbursementController.getDisbursementMasterCount() > 0) {
                                for (int lnCntr = 0; lnCntr <= poDisbursementController.getDisbursementMasterCount() - 1; lnCntr++) {
                                    String lsPaymentForm = "";
                                    switch (poDisbursementController.poDisbursementMaster(lnCntr).getDisbursementType()) {
                                        case DisbursementStatic.DisbursementType.CHECK:
                                            lsPaymentForm = "CHECK";
                                            break;
                                        case DisbursementStatic.DisbursementType.DIGITAL_PAYMENT:
                                            lsPaymentForm = "ONLINE PAYMENT";
                                            break;
                                        case DisbursementStatic.DisbursementType.WIRED:
                                            lsPaymentForm = "BANK TRANSFER";
                                            break;
                                    }
                                    main_data.add(new ModelDisbursementVoucher_Main(
                                            String.valueOf(lnCntr + 1),
                                            poDisbursementController.poDisbursementMaster(lnCntr).Payee().getPayeeName(),
                                            lsPaymentForm,
                                            CustomCommonUtil.formatDateToShortString(poDisbursementController.poDisbursementMaster(lnCntr).getTransactionDate()),
                                            poDisbursementController.poDisbursementMaster(lnCntr).getTransactionNo()
                                    ));
                                    if (poDisbursementController.poDisbursementMaster(lnCntr).getTransactionStatus().equals(DisbursementStatic.VERIFIED)) {
                                        plOrderNoPartial.add(new Pair<>(String.valueOf(lnCntr + 1), "1"));
                                    }
                                }
                            }
                            showRetainedHighlight(true);
                            if (main_data.isEmpty()) {
                                tblVwDisbursementVoucher.setPlaceholder(placeholderLabel);
                            }
                            JFXUtil.loadTab(pagination, main_data.size(), ROWS_PER_PAGE, tblVwDisbursementVoucher, filteredMain_Data);

                        }
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                );
                return null;
            }

            @Override
            protected void succeeded() {
                btnRetrieve.setDisable(false);
                placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed
                if (main_data == null || main_data.isEmpty()) {
                    tblVwDisbursementVoucher.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                } else {
                    pagination.setPageCount(0);
                    pagination.setVisible(true);
                    pagination.setManaged(true);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    tblVwDisbursementVoucher.toFront();
                }
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblVwDisbursementVoucher.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                }
                btnRetrieve.setDisable(false);
                progressIndicator.setVisible(false);
                progressIndicator.setManaged(false);
                tblVwDisbursementVoucher.toFront();
            }
        };
        new Thread(task).start(); // Run task in background
    }

    private void initTableMain() {
        JFXUtil.setColumnCenter(tblRowNo, tblSupplier, tblPaymentForm, tblTransDate, tblReferNo);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwDisbursementVoucher);

        filteredMain_Data = new FilteredList<>(main_data, b -> true);
        tblVwDisbursementVoucher.setItems(filteredMain_Data);
    }

    private void loadRecordMasterDV() {
        tfDVTransactionNo.setText(poDisbursementController.Master().getTransactionNo());
        dpDVTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
        tfVoucherNo.setText(poDisbursementController.Master().getVoucherNo());
        lblDVTransactionStatus.setText(getStatus(poDisbursementController.Master().getTransactionStatus()));
        cmbPaymentMode.getSelectionModel().select(!poDisbursementController.Master().getDisbursementType().equals("") ? Integer.valueOf(poDisbursementController.Master().getDisbursementType()) : -1);
        switch (cmbPaymentMode.getSelectionModel().toString()) {
            case DisbursementStatic.DisbursementType.CHECK:
                loadRecordMasterCheck();
                break;
            case DisbursementStatic.DisbursementType.WIRED:
                loadRecordMasterBankTransfer();
                break;
            case DisbursementStatic.DisbursementType.DIGITAL_PAYMENT:
                loadRecordMasterOnlinePayment();
                break;
        }
        taDVRemarks.setText(poDisbursementController.Master().getRemarks());
        tfVatableSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATSale(), true));
        tfVatRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATSale(), false));
        tfVatAmountMaster.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATSale(), true));
        tfVatZeroRatedSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getZeroVATSales(), true));
        tfVatExemptSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getVATExmpt(), true));
        tfTotalAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getTransactionTotal(), true));
        tfLessWHTax.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getWithTaxTotal(), true));
        tfTotalNetAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Master().getNetTotal(), true));
    }

    private String getStatus(String lsValueStatus) {
        String lsStatus;
        switch (lsValueStatus) {
            case DisbursementStatic.OPEN:
                lsStatus = "OPEN";
                break;
            case DisbursementStatic.CANCELLED:
                lsStatus = "CANCELLED";
                break;
            case DisbursementStatic.PAID:
                lsStatus = "PAID";
                break;
            case DisbursementStatic.POSTED:
                lsStatus = "POSTED";
                break;
            case DisbursementStatic.VERIFIED:
                lsStatus = "VERIFIED";
                break;
            case DisbursementStatic.VOID:
                lsStatus = "VOID";
                break;
            default:
                lsStatus = "STATUS";
                break;
        }
        return lsStatus;
    }

    private void loadRecordMasterCheck() {
        try {

            System.out.println("MASTER EDIT MODE == " + poDisbursementController.Master().getEditMode());
            System.out.println("CheckPayments EDIT MODE == " + poDisbursementController.CheckPayments().getEditMode());
            System.out.println("bank name == " + poDisbursementController.CheckPayments().getModel().Banks().getBankName());
            tfBankNameCheck.setText(poDisbursementController.CheckPayments().getModel().Banks().getBankName());
//            tfBankNameCheck.setText(poDisbursementController.CheckPayments().getModel().Banks().getBankName() != null ? poDisbursementController.CheckPayments().getModel().Banks().getBankName() : "");
            tfBankAccountCheck.setText(poDisbursementController.CheckPayments().getModel().getBankAcountID() != null ? poDisbursementController.CheckPayments().getModel().getBankAcountID() : "");
            tfPayeeName.setText(poDisbursementController.Master().Payee().getPayeeName() != null ? poDisbursementController.Master().Payee().getPayeeName() : "");
            tfCheckNo.setText(poDisbursementController.CheckPayments().getModel().getCheckNo());
            dpCheckDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.CheckPayments().getModel().getCheckDate(), SQLUtil.FORMAT_SHORT_DATE)));
            tfCheckAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.CheckPayments().getModel().getAmount(), true));
            chbkPrintByBank.setSelected(poDisbursementController.Master().getBankPrint().equals(Logical.YES));
            cmbPayeeType.getSelectionModel().select(!poDisbursementController.CheckPayments().getModel().getPayeeType().equals("") ? Integer.valueOf(poDisbursementController.CheckPayments().getModel().getPayeeType()) : -1);
            cmbDisbursementMode.getSelectionModel().select(!poDisbursementController.CheckPayments().getModel().getDesbursementMode().equals("") ? Integer.valueOf(poDisbursementController.CheckPayments().getModel().getDesbursementMode()) : -1);
            cmbClaimantType.getSelectionModel().select(!poDisbursementController.CheckPayments().getModel().getClaimant().equals("") ? Integer.valueOf(poDisbursementController.CheckPayments().getModel().getClaimant()) : -1);
            tfAuthorizedPerson.setText(poDisbursementController.CheckPayments().getModel().getAuthorize() != null ? poDisbursementController.CheckPayments().getModel().getAuthorize() : "");
            chbkIsCrossCheck.setSelected(poDisbursementController.CheckPayments().getModel().isCross() == true);
            chbkIsPersonOnly.setSelected(poDisbursementController.CheckPayments().getModel().isPayee() == true);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, null, ex);
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
                chbkVatClassification.setSelected(poDisbursementController.Detail(pnDetailDV).isWithVat() == true);
            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadTableRecordFromMain() {
        poJSON = new JSONObject();
        pnMain = tblVwDisbursementVoucher.getSelectionModel().getSelectedIndex();
        ModelDisbursementVoucher_Main selected = (ModelDisbursementVoucher_Main) tblVwDisbursementVoucher.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                String lsTransactionNo = selected.getIndex05();
                poJSON = poDisbursementController.OpenTransaction(lsTransactionNo);
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
                JFXUtil.disableAllHighlightByColor(tblVwDisbursementVoucher, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblVwDisbursementVoucher, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                Platform.runLater(() -> {
                    loadTableDetailDV();
                    pnEditMode = poDisbursementController.getEditMode();
                    initFields(pnEditMode);
                    initButton(pnEditMode);
                });
            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(DisbursementVoucher_VerificationController.class.getName()).log(Level.SEVERE, null, ex);
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

                    double lnNetTotal = 0.0000;
                    for (lnCtr = 0; lnCtr < poDisbursementController.getDetailCount(); lnCtr++) {
                        try {
                            lnNetTotal = poDisbursementController.Detail(lnCtr).getAmount().doubleValue() - poDisbursementController.Detail(lnCtr).getTaxAmount().doubleValue();
                            detailsdv_data.add(
                                    new ModelDisbursementVoucher_Detail(String.valueOf(lnCtr + 1),
                                            poDisbursementController.Detail(lnCtr).getSourceNo(),
                                            poDisbursementController.Detail(lnCtr).getAccountCode(),
                                            poDisbursementController.Detail(lnCtr).Particular().getDescription(),
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(lnCtr).getAmount(), true),
                                            poDisbursementController.Detail(lnCtr).TaxCode().getTaxCode(),
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.Detail(lnCtr).getTaxAmount(), true),
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(lnNetTotal, true)
                                    ));

                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(DisbursementVoucher_VerificationController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (pnDetailDV < 0 || pnDetailDV >= detailsdv_data.size()) {
                        if (!detailsdv_data.isEmpty()) {
                            tblVwDetails.getSelectionModel().select(0);
                            tblVwDetails.getFocusModel().focus(0);
                            pnDetailDV = tblVwDetails.getSelectionModel().getSelectedIndex();
                        }
                    } else {
                        tblVwDetails.getSelectionModel().select(pnDetailDV);
                        tblVwDetails.getFocusModel().focus(pnDetailDV);
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
        JFXUtil.setColumnCenter(tblDVRowNo, tblReferenceNo, tblAccountCode, tblParticulars, tblTaxCode);
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
                    loadRecordDetailDV();
                    if (poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue() != 0.0000) {
                        tfTaxCodeDetail.requestFocus();
                    } else {
                        tfPurchasedAmountDetail.requestFocus();
                    }
                    initFields(pnEditMode);
                }
            }
        });

        tblVwDisbursementVoucher.setOnMouseClicked(event -> {
            pnMain = tblVwDisbursementVoucher.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableRecordFromMain();
                }
            }
        });

        tblVwDisbursementVoucher.setRowFactory(tv -> new TableRow<ModelDisbursementVoucher_Main>() {
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
        JFXUtil.adjustColumnForScrollbar(tblVwDisbursementVoucher, tblVwDetails, tblVwJournalDetails);
    }

    private void tableKeyEvents(KeyEvent event) {
        if (!detailsdv_data.isEmpty()) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblVwDetails":
                    if (focusedCell != null) {
                        switch (event.getCode()) {
                            case TAB:
                            case DOWN:
                                pnDetailDV = JFXUtil.moveToNextRow(currentTable);
                                break;
                            case UP:
                                pnDetailDV = JFXUtil.moveToPreviousRow(currentTable);
                                break;
                            default:
                                break;
                        }
                        loadRecordDetailDV();
                        event.consume();
                    }
                    break;
            }
        }
    }

    private void initTextFieldsDV() {
        //Initialise  TextField Focus
        JFXUtil.setFocusListener(txtMasterDV_Focus, tfSearchSupplier, tfSearchTransaction);
        JFXUtil.setFocusListener(txtDetailDV_Focus, tfPurchasedAmountDetail, tfTaxCodeDetail);
        JFXUtil.setFocusListener(txtMasterCheck_Focus, tfPayeeName, tfBankNameCheck, tfBankAccountCheck);
        JFXUtil.setFocusListener(txtMasterBankTransfer_Focus, tfBankNameBTransfer, tfBankAccountBTransfer, tfSupplierBank, tfSupplierAccountNoBTransfer);
        JFXUtil.setFocusListener(txtMasterOnlinePayment_Focus, tfBankNameOnlinePayment, tfBankAccountOnlinePayment, tfSupplierServiceName, tfSupplierAccountNo);

        //Initialise  TextField KeyPressed
        List<TextField> loTxtFieldKeyPressed = Arrays.asList(tfSearchTransaction, tfSearchSupplier, tfPayeeName, tfBankNameCheck, tfBankAccountCheck, tfPurchasedAmountDetail, tfTaxCodeDetail);
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
                case "tfTaxCodeDetail":
                    if (lsValue.equals("")) {
                        poDisbursementController.Detail(pnDetailDV).setTAxCode("");
                        poDisbursementController.Detail(pnDetailDV).setTaxRates(0.0000);
                        poDisbursementController.Detail(pnDetailDV).setTaxAmount(0.0000);
                    }
                    break;
            }
            Platform.runLater(() -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                delay.setOnFinished(event -> {
                    loadTableDetailDV();
                });
                delay.play();
            });
        }
    };
    final ChangeListener<? super Boolean> txtMasterDV_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtMasterDV = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtMasterDV.getId());
        String lsValue = (txtMasterDV.getText() == null ? "" : txtMasterDV.getText());

        lastFocusedTextField = txtMasterDV;
        previousSearchedTextField = null;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfSearchSupplier":
                    if (lsValue.equals("")) {
                        psSupplierId = "";
                    }
                    loadTableMain();
                    break;
                case "tfSearchTransaction":
                    if (lsValue.equals("")) {
                        psTransactionNo = "";
                    }
                    loadTableMain();
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
                case "tfBankNameCheck":
                    if (lsValue.equals("")) {
                        poDisbursementController.CheckPayments().getModel().setBankID("");
                    }
                    break;
                case "tfBankAccountCheck":
                    if (lsValue.equals("")) {
                        poDisbursementController.CheckPayments().getModel().setBankAcountID("");
                    }
                    break;
                case "tfPayeeName":
                    if (lsValue.equals("")) {
                        poDisbursementController.CheckPayments().getModel().setPayeeID("");
                    }
                    break;
                case "tfAuthorizedPerson":
                    if (lsValue.equals("")) {
                        poDisbursementController.CheckPayments().getModel().setAuthorize("");
                    }
                    break;
            }
            loadRecordMasterCheck();
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
            loadRecordMasterOnlinePayment();
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
            loadRecordMasterBankTransfer();
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
                                moveNextDV();
                                event.consume();
                                break;
                        }
                        event.consume();
                        break;
                    case F3:
                        switch (lsID) {
                            case "tfSearchSupplier":
                                poJSON = poDisbursementController.SearchPayee(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfSearchSupplier.setText(poDisbursementController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? poDisbursementController.Master().Payee().getPayeeName() : "");
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
                                poJSON = poDisbursementController.CheckPayments().searchBankAcounts(lsValue);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfBankAccountCheck.setText(poDisbursementController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? (poDisbursementController.CheckPayments().getModel().getBankAcountID() != null ? poDisbursementController.CheckPayments().getModel().getBankAcountID() : "") : "");
                                break;
                            case "tfPayeeName":
                                poJSON = poDisbursementController.SearchPayee(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }

                                tfPayeeName.setText(poDisbursementController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? poDisbursementController.Master().Payee().getPayeeName() : "");
                                break;
                            case "tfAuthorizedPerson":
                                break;
                            case "tfTaxCodeDetail":
                                poJSON = poDisbursementController.SearchTaxCode(lsValue, pnDetailDV, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfTaxCodeDetail.setText(poDisbursementController.Detail(pnDetailDV).TaxCode().getTaxCode() != null ? poDisbursementController.Detail(pnDetailDV).TaxCode().getTaxCode() : "");
                                moveNextDV();
                                break;
                        }
                        event.consume();
                    case UP:
                        switch (lsID) {
                            case "tfPurchasedAmountDetail":
                            case "tfTaxCodeDetail":
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
                                moveNextDV();
                                event.consume();
                                break;
                        }
                        break;
                    default:
                        break;

                }
            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_VerificationController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void movePreviousDV() {
        double lnPurchaseAmount = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
        apDVDetail.requestFocus();
        double lnNewvalue = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
        if (lnPurchaseAmount != lnNewvalue && (lnPurchaseAmount > 0.0000
                && poDisbursementController.Detail(pnDetailDV).getSourceNo() != null
                && !"".equals(poDisbursementController.Detail(pnDetailDV).getSourceNo()))) {
            tfPurchasedAmountDetail.requestFocus();
        } else {
            pnDetailDV = JFXUtil.moveToPreviousRow(tblVwDetails);
            loadRecordDetailDV();
            if (poDisbursementController.Detail(pnDetailDV).getSourceNo() != null && !poDisbursementController.Detail(pnDetailDV).getSourceNo().equals("")) {
                if (poDisbursementController.Detail(pnDetailDV).getTAxCode() != null && !poDisbursementController.Detail(pnDetailDV).getTAxCode().isEmpty()) {
                    tfPurchasedAmountDetail.requestFocus();
                } else {
                    if (chbkVatClassification.isSelected()) {
                        tfTaxCodeDetail.requestFocus();
                    }
                }
            } else {
                if (chbkVatClassification.isSelected()) {
                    tfTaxCodeDetail.requestFocus();
                }
            }
        }
    }

    private void moveNextDV() {
        double lnPurchaseAmount = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
        apDVDetail.requestFocus();
        double lnNewvalue = poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue();
        if (lnPurchaseAmount != lnNewvalue && (lnPurchaseAmount > 0.0000
                && poDisbursementController.Detail(pnDetailDV).getSourceNo() != null
                && !"".equals(poDisbursementController.Detail(pnDetailDV).getSourceNo()))) {
            tfPurchasedAmountDetail.requestFocus();
        } else {
            pnDetailDV = JFXUtil.moveToNextRow(tblVwDetails);
            loadRecordDetailDV();
            if (poDisbursementController.Detail(pnDetailDV).getSourceNo() != null && !poDisbursementController.Detail(pnDetailDV).getSourceNo().equals("")) {
                if (poDisbursementController.Detail(pnDetailDV).getTAxCode() != null && !poDisbursementController.Detail(pnDetailDV).getTAxCode().isEmpty()) {
                    tfPurchasedAmountDetail.requestFocus();
                } else {
                    if (chbkVatClassification.isSelected()) {
                        tfTaxCodeDetail.requestFocus();
                    }
                }
            } else {
                if (chbkVatClassification.isSelected()) {
                    tfTaxCodeDetail.requestFocus();
                }
            }
        }
    }

    private void initTextFieldsJournal() {
        //Initialise  TextField Focus
        tfSupplier.focusedProperty().addListener(txtMasterDV_Focus);

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
        cmbPaymentMode.setItems(cPaymentMode);
        cmbPayeeType.setItems(cPayeeType);
        cmbDisbursementMode.setItems(cDisbursementMode);
        cmbClaimantType.setItems(cClaimantType);
        cmbOtherPayment.setItems(cOtherPayment);
        cmbOtherPaymentBTransfer.setItems(cOtherPaymentBTransfer);

        cmbPaymentMode.setOnAction(e -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbPaymentMode.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.Master().setDisbursementType(String.valueOf(cmbPaymentMode.getSelectionModel().getSelectedIndex()));
                loadRecordMasterDV();
            }
            initFields(pnEditMode);
        });

        cmbPayeeType.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbPayeeType.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.CheckPayments().getModel().setPayeeType(String.valueOf(cmbPayeeType.getSelectionModel().getSelectedIndex()));
            }
        }
        );
        cmbDisbursementMode.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbDisbursementMode.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.CheckPayments().getModel().setDesbursementMode(String.valueOf(cmbDisbursementMode.getSelectionModel().getSelectedIndex()));
            }
        }
        );
        cmbClaimantType.setOnAction(event
                -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) && cmbClaimantType.getSelectionModel().getSelectedIndex() >= 0) {
                poDisbursementController.CheckPayments().getModel().setClaimant(String.valueOf(cmbClaimantType.getSelectionModel().getSelectedIndex()));
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
                LocalDate checkDate = new java.sql.Date(poDisbursementController.CheckPayments().getModel().getCheckDate().getTime()).toLocalDate();
                String psOldDate = CustomCommonUtil.formatLocalDateToShortString(checkDate); // abang lang ito
                if (selectedLocalDate != null) {
                    poDisbursementController.CheckPayments().getModel().setCheckDate(SQLUtil.toDate(selectedLocalDate.toString(), SQLUtil.FORMAT_SHORT_DATE));
                }
            }
        }
        );
    }

    private void initCheckBox() {
        chbkVatClassification.setOnAction(event -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (pnDetailDV >= 0) {
                    // Check for invalid purchase amount first
                    if (poDisbursementController.Detail(pnDetailDV).getAmount().doubleValue() <= 0.0000) {
                        ShowMessageFX.Warning("Invalid to check VAT classification, no purchase amount.", pxeModuleName, null);
                        chbkVatClassification.setSelected(false); // Optional: uncheck the box
                        return;
                    }
                    if (!isUncheckVatSales()) {
                        return;
                    }
                    poDisbursementController.Detail(pnDetailDV).isWithVat(chbkVatClassification.isSelected());
                    loadTableDetailDV();
                    initFields(pnEditMode);
                }
            }
        });

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
            }
        });
    }

    private boolean isUncheckVatSales() {
        if (!poDisbursementController.Detail(pnDetailDV).getTAxCode().isEmpty()) {
            if (ShowMessageFX.YesNo("This detail have already tax code, Are you sure want to change?", pxeModuleName, null)) {
                poDisbursementController.Detail(pnDetailDV).isWithVat(false);
                chbkVatClassification.setSelected(poDisbursementController.Detail(pnDetailDV).isWithVat());
                poDisbursementController.Detail(pnDetailDV).setTAxCode("");
                poDisbursementController.Detail(pnDetailDV).setTaxRates(0.00);
                poDisbursementController.Detail(pnDetailDV).setTaxAmount(0.0000);
                Platform.runLater(() -> {
                    PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                    delay.setOnFinished(event -> {
                        loadTableDetailDV();
                    });
                    delay.play();
                });
                loadRecordDetailDV();
                return false;
            }
        }
        return true;
    }

    private void clearFields() {
        previousSearchedTextField = null;
        lastFocusedTextField = null;
        JFXUtil.setValueToNull(null, dpDVTransactionDate, dpJournalTransactionDate, dpCheckDate, dpReportMonthYear);
        JFXUtil.setValueToNull(null, cmbPaymentMode, cmbPayeeType, cmbDisbursementMode, cmbClaimantType, cmbOtherPayment, cmbOtherPaymentBTransfer);
        JFXUtil.clearTextFields(apDVDetail, apDVMaster1, apDVMaster2, apDVMaster3, apMasterDVCheck, apMasterDVBTransfer, apMasterDVOp, apJournalMaster, apJournalDetails);
        CustomCommonUtil.setSelected(false, chbkIsCrossCheck, chbkPrintByBank, chbkVatClassification);
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.UPDATE);
        JFXUtil.setDisabled(!lbShow, apDVMaster1, apDVMaster2, apDVMaster3, apJournalMaster, apJournalDetails);
        JFXUtil.setDisabled(true, apDVDetail, apMasterDVCheck, apMasterDVOp, apMasterDVBTransfer);
        tfTaxCodeDetail.setDisable(true);
        tabJournal.setDisable(tfDVTransactionNo.getText().isEmpty());
        tabCheck.setDisable(true);
        tabOnlinePayment.setDisable(true);
        tabBankTransfer.setDisable(true);
        if (main_data.isEmpty()) {
            Label placeholderLabel = new Label("NO RECORD TO LOAD");
            tblVwDisbursementVoucher.setPlaceholder(placeholderLabel);
            pagination.setManaged(false);
            pagination.setVisible(false);
        }
        switch (poDisbursementController.Master().getDisbursementType()) {
            case DisbursementStatic.DisbursementType.CHECK:
                tabCheck.setDisable(!lbShow);
                apMasterDVOp.setDisable(!lbShow);
                CustomCommonUtil.switchToTab(tabCheck, tabPanePaymentMode);
                loadRecordMasterCheck();
                break;
            case DisbursementStatic.DisbursementType.WIRED:
                tabBankTransfer.setDisable(!lbShow);
                apMasterDVBTransfer.setDisable(!lbShow);
                CustomCommonUtil.switchToTab(tabBankTransfer, tabPanePaymentMode);
                loadRecordMasterBankTransfer();
                break;
            case DisbursementStatic.DisbursementType.DIGITAL_PAYMENT:
                tabOnlinePayment.setDisable(!lbShow);
                apMasterDVOp.setDisable(!lbShow);
                CustomCommonUtil.switchToTab(tabOnlinePayment, tabPanePaymentMode);
                loadRecordMasterOnlinePayment();
                break;
        }
        if (pnDetailDV >= 0) {
            if (!tfRefNoDetail.getText().isEmpty()) {
                JFXUtil.setDisabled(!lbShow, apDVDetail);
                if (chbkVatClassification.isSelected()) {
                    tfTaxCodeDetail.setDisable(!lbShow);
                }
            }
        }
    }

    private void initButton(int fnEditMode) {
        boolean lbShow = (pnEditMode == EditMode.UPDATE);
        JFXUtil.setButtonsVisibility(!lbShow, btnClose);
        JFXUtil.setButtonsVisibility(lbShow, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(false, btnUpdate, btnDVCancel, btnVoid, btnVerify);
        JFXUtil.setButtonsVisibility(fnEditMode != EditMode.ADDNEW && fnEditMode != EditMode.UNKNOWN, btnHistory);
        if (fnEditMode == EditMode.READY) {
            switch (poDisbursementController.Master().getTransactionStatus()) {
                case DisbursementStatic.OPEN:
                    JFXUtil.setButtonsVisibility(true, btnUpdate, btnVoid, btnVerify);
                    break;
                case DisbursementStatic.VERIFIED:
                    JFXUtil.setButtonsVisibility(true, btnUpdate, btnDVCancel);
                    break;
            }
        }
    }

}
