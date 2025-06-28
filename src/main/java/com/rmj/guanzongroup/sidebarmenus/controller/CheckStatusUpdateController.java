/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDisbursementVoucher_Main;
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
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.CheckPayments;
import ph.com.guanzongroup.cas.cashflow.Disbursement;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;
import ph.com.guanzongroup.cas.cashflow.status.CheckStatus;
import ph.com.guanzongroup.cas.cashflow.status.DisbursementStatic;

/**
 * FXML Controller class
 *
 * @author User
 */
public class CheckStatusUpdateController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    private int pnMain = 0;
    private final String pxeModuleName = "Check Status Update";
    private CheckPayments poCheckPayments;
    private Disbursement poDisbursementController;
    public int pnEditMode;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psTransactionNo = "";
    private String psTransactionType = "";
    private String psOldDate = "";

    private String psSearchBankName = "";
    private String psSearchBankAccount = "";
    private String psSearchCheckNo = "";

    private unloadForm poUnload = new unloadForm();

    private ObservableList<ModelDisbursementVoucher_Main> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelDisbursementVoucher_Main> filteredMain_Data;

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

    ObservableList<String> cCheckState = FXCollections.observableArrayList(
            "CLEAR", "CANCELLATION", "STALE", "HOLD",
            "BOUNCED / DISCHONORED");
    @FXML
    private AnchorPane AnchorMain, apBrowse, apButton, apMaster;
    @FXML
    private TextField tfSearchBankName, tfSearchBankAccount, tfSearchCheckno;
    @FXML
    private Label lblSource;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnUpdate, btnSave, btnCancel, btnRetrieve, btnHistory, btnClose;
    @FXML
    private DatePicker dpTransactionDate, dpCheckDate, dpClearDate, dpHoldUntil;
    @FXML
    private TextField tfTransactionNo, tfBankName, tfBankAccount, tfCheckAmount, tfPayeeName, tfCheckNo;
    @FXML
    private Label lblRemarks;
    @FXML
    private TextArea taRemarks;
    @FXML
    private ComboBox<String> cmbCheckState;
    @FXML
    private Label lblHoldUntil, lblClearingDate;
    @FXML
    private TableView tblVwMain;
    @FXML
    private TableColumn tblRowNo, tblBankName, tblBankAccount, tblCheckNo, tblReferenceNo;
    @FXML
    private RowConstraints row09;
    @FXML
    private Pagination pagination;

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
            poJSON = poDisbursementController.InitTransaction(); // Initialize transaction
            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }
            initAll();
            Platform.runLater(() -> {
                poDisbursementController.setIndustryID(psIndustryId);
                poDisbursementController.CheckPayments().getModel().setIndustryID(psIndustryId);
                loadRecordSearch();
            });
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadRecordSearch() {
        try {
//            lblSource.setText(poCheckPayments.Master().Company().getCompanyName() + " - " + poCheckPayments.getModel().Industry().getDescription());
            lblSource.setText(poDisbursementController.CheckPayments().getModel().Industry().getDescription());

        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    private void initAll() {
        initButtonsClickActions();
        initTextAreaFields();
        initComboBox();
        initDatePicker();
        initTableMain();
        initTableOnClick();
        initTextFields();
        clearFields();
        initTextFieldsProperty();
        pnEditMode = EditMode.UNKNOWN;
        initFields(pnEditMode);
        initButton(pnEditMode);
        pagination.setPageCount(0);
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnUpdate, btnSave, btnCancel, btnRetrieve, btnHistory, btnClose);
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
                    poJSON = poDisbursementController.setCheckpayment();
                    if ("error".equals((String) poJSON.get("message"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    pnEditMode = poDisbursementController.getEditMode();
                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                        JFXUtil.disableAllHighlightByColor(tblVwMain, "#A7C7E7", highlightedRowsMain);
                        poDisbursementController.resetMaster();
                        poDisbursementController.resetOthers();
                        poDisbursementController.Detail().clear();
                        clearFields();
                        break;
                    } else {
                        return;
                    }
                case "btnHistory":
                    ShowMessageFX.Warning("Button History is Underdevelopment.", pxeModuleName, null);
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
                    pnEditMode = EditMode.UNKNOWN;
                    JFXUtil.disableAllHighlightByColor(tblVwMain, "#A7C7E7", highlightedRowsMain);
                    poDisbursementController.resetMaster();
                    poDisbursementController.resetOthers();
                    poDisbursementController.Detail().clear();
                    clearFields();
                    pagination.toBack();
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
            if (lsButton.equals("btnSave") || lsButton.equals("btnCancel")) {
                pnEditMode = EditMode.UNKNOWN;
                clearFields();
                loadRecordMaster();
            }
            initFields(pnEditMode);
            initButton(pnEditMode);
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(CheckStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isSavingValid() {
        switch (poDisbursementController.CheckPayments().getModel().getTransactionStatus()) {
            case CheckStatus.CANCELLED:
            case CheckStatus.BOUNCED:
            case CheckStatus.STOP_PAYMENT:
                if (taRemarks.getText().trim().isEmpty()) {
                    ShowMessageFX.Warning("Please enter remarks", pxeModuleName, null);
                    return false;
                }
                break;
        }
        return true;
    }

    private void initTextFields() {
        //Initialise  TextField KeyPressed
        List<TextField> loTxtFieldKeyPressed = Arrays.asList(tfSearchBankName, tfSearchBankAccount, tfSearchCheckno);
        loTxtFieldKeyPressed.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
    }

    private void txtField_KeyPressed(KeyEvent event) {
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
                            case "tfSearchCheckno":
                                psSearchCheckNo = tfSearchCheckno.getText();
                                loadTableMain();
                                event.consume();
                                break;
                        }
                        event.consume();
                        break;
                    case F3:
                        switch (lsID) {
                            case "tfSearchBankName":
                                poJSON = poDisbursementController.SearchBanks(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfSearchBankName.setText(poDisbursementController.CheckPayments().getModel().Banks().getBankName() != null ? poDisbursementController.CheckPayments().getModel().Banks().getBankName() : "");
                                psSearchBankName = poDisbursementController.CheckPayments().getModel().getBankID();
                                loadTableMain();
                                break;
                            case "tfSearchBankAccount":
                                poJSON = poDisbursementController.SearchBankAccount(lsValue, poDisbursementController.CheckPayments().getModel().getBankID(), false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfSearchBankAccount.setText(poDisbursementController.CheckPayments().getModel().Bank_Account_Master().getAccountNo() != null ? poDisbursementController.CheckPayments().getModel().Bank_Account_Master().getAccountNo() : "");
                                psSearchBankAccount = poDisbursementController.CheckPayments().getModel().getBankAcountID();
                                loadTableMain();
                                break;

                        }
                        CommonUtils.SetNextFocus((TextField) event.getSource());
                        event.consume();
                        break;
                    default:
                        break;

                }
            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(CheckStatusUpdateController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextAreaFields() {
        //Initialise  TextArea Focus
        taRemarks.focusedProperty().addListener(txtArea_Focus);

        //Initialise  TextArea KeyPressed
        taRemarks.setOnKeyPressed(event -> txtArea_KeyPressed(event));
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
                case "taRemarks":
                    poDisbursementController.CheckPayments().getModel().setRemarks(lsValue);
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

    private void loadRecordMaster() {
        try {
            tfTransactionNo.setText(poDisbursementController.Master().getTransactionNo());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
            poJSON = poDisbursementController.setCheckpayment();
            if ("error".equals((String) poJSON.get("message"))) {
                ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                return;
            }
            loadRecordMasterCheck();
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(CheckStatusUpdateController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadRecordMasterCheck() {
        try {
            tfBankName.setText(poDisbursementController.CheckPayments().getModel().Banks().getBankName() != null ? poDisbursementController.CheckPayments().getModel().Banks().getBankName() : "");
            tfBankAccount.setText(poDisbursementController.CheckPayments().getModel().getBankAcountID() != null ? poDisbursementController.CheckPayments().getModel().getBankAcountID() : "");
            tfPayeeName.setText(poDisbursementController.Master().Payee().getPayeeName() != null ? poDisbursementController.Master().Payee().getPayeeName() : "");
            tfCheckNo.setText(poDisbursementController.CheckPayments().getModel().getCheckNo());
            dpCheckDate.setValue(poDisbursementController.CheckPayments().getModel().getCheckDate() != null
                    ? CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.CheckPayments().getModel().getCheckDate(), SQLUtil.FORMAT_SHORT_DATE))
                    : null);
            tfCheckAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.CheckPayments().getModel().getAmount(), true));
            int selectedItem = -1;
            switch (poDisbursementController.CheckPayments().getModel().getTransactionStatus()) {
                case "2": //CLEAR
                    selectedItem = 0;
                    break;
                case "3"://CANCELLATION
                    selectedItem = 1;
                    break;
                case "4": // STALE
                    selectedItem = 2;
                    break;
                case "5": //HOLD
                    selectedItem = 3;
                    break;
                case "6": //BOUNCED / DISCHONORED
                    selectedItem = 4;
                    break;
            }
            cmbCheckState.getSelectionModel().select(selectedItem);
            switch (poDisbursementController.CheckPayments().getModel().getTransactionStatus()) {
                case CheckStatus.POSTED:
//                    dpClearDate.setValue(poDisbursementController.CheckPayments().getModel().getModifiedDate() != null
//                            ? CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.CheckPayments().getModel().getModifiedDate(), SQLUtil.FORMAT_SHORT_DATE))
//                            : null);
                    break;
                case CheckStatus.STOP_PAYMENT:
//                    dpHoldUntil.setValue(poDisbursementController.CheckPayments().getModel().getModifiedDate() != null
//                            ? CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poDisbursementController.CheckPayments().getModel().getModifiedDate(), SQLUtil.FORMAT_SHORT_DATE))
//                            : null);
                    break;
            }
            taRemarks.setText(poDisbursementController.CheckPayments().getModel().getRemarks());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(CheckStatusUpdateController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initComboBox() {
        cmbCheckState.setItems(cCheckState);
        cmbCheckState.setOnAction(e -> {
            if (pnEditMode == EditMode.UPDATE && cmbCheckState.getSelectionModel().getSelectedIndex() >= 0) {
                String selectedItem = null;
                switch (cmbCheckState.getSelectionModel().getSelectedItem()) {
                    case "CLEAR":
                        selectedItem = "2";
                        break;
                    case "CANCELLATION":
                        selectedItem = "3";
                        break;
                    case "STALE":
                        selectedItem = "4";
                        break;
                    case "HOLD":
                        selectedItem = "5";
                        break;
                    case "BOUNCED / DISCHONORED":
                        selectedItem = "6";
                        break;
                }
                poDisbursementController.CheckPayments().getModel().setTransactionStatus(String.valueOf(selectedItem));
            }
            initFields(pnEditMode);
        });
    }

    private void initDatePicker() {
        dpCheckDate.setOnAction(e -> {
            if (pnEditMode == EditMode.UPDATE) {
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

    private void loadTableMain() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblVwMain.setPlaceholder(loadingPane);
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
                        plOrderNoPartial.clear();
                        poJSON = poDisbursementController.getDisbursementForCheckStatusUpdate(psSearchBankName, psSearchBankAccount, psSearchCheckNo);
                        if ("success".equals(poJSON.get("result"))) {
                            if (poDisbursementController.getDisbursementMasterCount() > 0) {
                                for (int lnCntr = 0; lnCntr <= poDisbursementController.getDisbursementMasterCount() - 1; lnCntr++) {
                                    main_data.add(new ModelDisbursementVoucher_Main(
                                            String.valueOf(lnCntr + 1),
                                            poDisbursementController.poDisbursementMaster(lnCntr).CheckPayments().Banks().getBankName(),
                                            poDisbursementController.poDisbursementMaster(lnCntr).CheckPayments().Bank_Account_Master().getAccountNo(),
                                            poDisbursementController.poDisbursementMaster(lnCntr).CheckPayments().getCheckNo(),
                                            poDisbursementController.poDisbursementMaster(lnCntr).getTransactionNo()
                                    ));
                                    if (poDisbursementController.poDisbursementMaster(lnCntr).CheckPayments().getTransactionStatus().equals(CheckStatus.POSTED)) {
                                        plOrderNoPartial.add(new Pair<>(String.valueOf(lnCntr + 1), "1"));
                                    }
                                }
                            } else {
                                main_data.clear();
                                filteredMain_Data.clear();
                            }
                        }
                        showRetainedHighlight(true);
                        if (main_data.isEmpty() && filteredMain_Data.isEmpty()) {
                            tblVwMain.setPlaceholder(placeholderLabel);
                        }
                        JFXUtil.loadTab(pagination, main_data.size(), ROWS_PER_PAGE, tblVwMain, filteredMain_Data);
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(CheckStatusUpdateController.class
                                .getName()).log(Level.SEVERE, null, ex);
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
                    tblVwMain.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                } else {
                    pagination.setPageCount(0);
                    pagination.setVisible(true);
                    pagination.setManaged(true);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    tblVwMain.toFront();
                }
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblVwMain.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                }
                btnRetrieve.setDisable(false);
                progressIndicator.setVisible(false);
                progressIndicator.setManaged(false);
                tblVwMain.toFront();
            }
        };
        new Thread(task).start(); // Run task in background
    }

    private void initTableMain() {
        JFXUtil.setColumnCenter(tblRowNo, tblBankName, tblBankAccount, tblCheckNo, tblReferenceNo);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwMain);

        filteredMain_Data = new FilteredList<>(main_data, b -> true);
        tblVwMain.setItems(filteredMain_Data);
    }

    private void showRetainedHighlight(boolean isRetained) {
        if (isRetained) {
            for (Pair<String, String> pair : plOrderNoPartial) {
                if (!"0".equals(pair.getValue())) {

                    plOrderNoFinal.add(new Pair<>(pair.getKey(), pair.getValue()));
                }
            }
        }
        JFXUtil.disableAllHighlightByColor(tblVwMain, "#C1E1C1", highlightedRowsMain);
        plOrderNoPartial.clear();
        for (Pair<String, String> pair : plOrderNoFinal) {
            if (!"0".equals(pair.getValue())) {
                JFXUtil.highlightByKey(tblVwMain, pair.getKey(), "#C1E1C1", highlightedRowsMain);
            }
        }
    }

    private void initTableOnClick() {
        tblVwMain.setOnMouseClicked(event -> {
            pnMain = tblVwMain.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableRecordFromMain();
                }
            }
        });

        tblVwMain.setRowFactory(tv -> new TableRow<ModelDisbursementVoucher_Main>() {
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

        JFXUtil.adjustColumnForScrollbar(tblVwMain);
    }

    private void loadTableRecordFromMain() {
        poJSON = new JSONObject();
        ModelDisbursementVoucher_Main selected = (ModelDisbursementVoucher_Main) tblVwMain.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                String lsTransactionNo = selected.getIndex05();
                clearFields();
                poJSON = poDisbursementController.OpenTransaction(lsTransactionNo);
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
                JFXUtil.disableAllHighlightByColor(tblVwMain, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblVwMain, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                pnEditMode = poDisbursementController.getEditMode();
                loadRecordMaster();
                initFields(pnEditMode);
                initButton(pnEditMode);

            } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                Logger.getLogger(CheckStatusUpdateController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void clearFields() {
        previousSearchedTextField = null;
        lastFocusedTextField = null;
        JFXUtil.setValueToNull(null, dpTransactionDate, dpCheckDate, dpClearDate, dpHoldUntil, cmbCheckState);
        JFXUtil.clearTextFields(apMaster);
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.UPDATE);
        JFXUtil.setDisabled(!lbShow, apMaster);
        CustomCommonUtil.setVisible(false, dpClearDate, lblClearingDate, lblHoldUntil, dpHoldUntil, lblRemarks, taRemarks);
        CustomCommonUtil.setManaged(false, dpClearDate, lblClearingDate, lblHoldUntil, dpHoldUntil, lblRemarks, taRemarks);
        switch (poDisbursementController.CheckPayments().getModel().getTransactionStatus()) {
            case CheckStatus.POSTED:
                row09.setPrefHeight(30);
                CustomCommonUtil.setVisible(true, dpClearDate, lblClearingDate);
                CustomCommonUtil.setManaged(true, dpClearDate, lblClearingDate);
                CustomCommonUtil.setDisable(!lbShow, dpClearDate);
                break;
            case CheckStatus.CANCELLED:
            case CheckStatus.BOUNCED:
                CustomCommonUtil.setVisible(true, taRemarks, lblRemarks);
                row09.setPrefHeight(0);
                row09.setMaxHeight(0);
                CustomCommonUtil.setVisible(true, taRemarks, lblRemarks);
                CustomCommonUtil.setManaged(true, taRemarks, lblRemarks);
                break;
            case CheckStatus.STOP_PAYMENT:
                row09.setPrefHeight(30);
                CustomCommonUtil.setVisible(true, dpHoldUntil, lblHoldUntil, taRemarks, lblRemarks);
                CustomCommonUtil.setManaged(true, dpHoldUntil, lblHoldUntil, taRemarks, lblRemarks);
                break;
        }
    }

    private void initButton(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
        JFXUtil.setButtonsVisibility(!lbShow, btnClose);
        JFXUtil.setButtonsVisibility(lbShow, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(false, btnUpdate);
        JFXUtil.setButtonsVisibility(fnEditMode != EditMode.UNKNOWN, btnHistory);

        if (fnEditMode == EditMode.READY) {
            switch (poDisbursementController.CheckPayments().getModel().getTransactionStatus()) {
                case CheckStatus.OPEN:
                case CheckStatus.STOP_PAYMENT:
                    JFXUtil.setButtonsVisibility(true, btnUpdate);
                    break;
            }
        }
    }

    private void initTextFieldsProperty() {
        tfSearchBankName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    tfSearchBankAccount.setText("");
                    psSearchBankName = "";
                    tfSearchBankAccount.setText("");
                    psSearchBankAccount = "";
                    loadTableMain();
                }
            }
        }
        );
        tfSearchBankAccount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    tfSearchBankAccount.setText("");
                    psSearchBankAccount = "";
                    loadTableMain();
                }
            }
        }
        );
        tfSearchCheckno.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    tfSearchCheckno.setText("");
                    psSearchCheckNo = "";
                    loadTableMain();
                }
            }
        }
        );

    }
}
