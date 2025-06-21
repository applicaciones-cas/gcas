/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDisbursementVoucher_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDisbursementVoucher_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
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
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.Pair;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.CheckPrintingRequest;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;
import ph.com.guanzongroup.cas.cashflow.status.CheckPrintRequestStatus;
import ph.com.guanzongroup.cas.cashflow.status.DisbursementStatic;

/**
 * FXML Controller class
 *
 * @author User
 */
public class CheckPrintRequest_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    private int pnMain = 0;
    private int pnDetail = 0;
    private boolean lsIsSaved = false;
    private final String pxeModuleName = "Check Print Request";
    private CheckPrintingRequest poCheckPrintingRequestController;
    public int pnEditMode;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psTransactionNo = "";
    private String psTransactionType = "";

    private unloadForm poUnload = new unloadForm();
    private ObservableList<ModelDisbursementVoucher_Detail> details_data = FXCollections.observableArrayList();
    private FilteredList<ModelDisbursementVoucher_Detail> filteredDataDetailDV;

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
    ObservableList<String> cPayeeType = FXCollections.observableArrayList("INDIVIDUAL", "CORPORATION");
    @FXML
    private AnchorPane AnchorMain, apBrowse;
    @FXML
    private Label lblSource;
    @FXML
    private TextField tfSearchCompany, tfSearchDepartment, tfSearchBankName, tfSearchBankAccount;
    @FXML
    private AnchorPane apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSave, btnCancel, btnExport, btnRetrieve, btnHistory, btnClose;
    /*Master*/
    @FXML
    private AnchorPane apMaster;
    @FXML
    private DatePicker dpTransactionDate;
    @FXML
    private TextField tfTransactionNo, tfBankName, tfTotalAmount;
    @FXML
    private TextArea taRemarks;
    @FXML
    private Label lblTransactionStatus;
    @FXML
    private CheckBox chbkUploaded;
    /*Detail*/
    @FXML
    private AnchorPane apDetail;
    @FXML
    private TextField tfReferNo, tfCheckNo, tfCheckAmount, tfPayeeNAme, tfDVNo, tfDVAmount;
    @FXML
    private DatePicker dpDVDate, dpCheckDate;
    @FXML
    private ComboBox<String> cmbPayeeType;
    @FXML
    private TextArea taRemarksDetails;
    @FXML
    private TableView tblVwDetail;
    @FXML
    private TableColumn tblRowNoDetail, tblReferNoDetail, tblDVNo, tblDVDate, tblDVAmount, tblCheckNo, tblCheckDate, tblCheckAmount;
    @FXML
    private TableView tblVwMain;
    @FXML
    private TableColumn tblRowNo, tblBankName, tblBankAccount, tblDate, tblReferenceNo;
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
        poCheckPrintingRequestController = new CashflowControllers(oApp, null).CheckPrintingRequest();
        poJSON = new JSONObject();
        poJSON = poCheckPrintingRequestController.InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
        initAll();
        Platform.runLater(() -> {
            poCheckPrintingRequestController.Master().setIndustryID(psIndustryId);
            loadRecordSearch();
            btnNew.fire();
        });
    }

    private void initAll() {
        initButtonsClickActions();
        initTextFields();
        initTextAreaFields();
        initComboBox();
        initTableDetail();
        initTableMain();
        initTableOnClick();
        initTextFieldsProperty();
        clearFields();
        pnEditMode = EditMode.UNKNOWN;
        initFields(pnEditMode);
        initButton(pnEditMode);
        pagination.setPageCount(0);
    }

    private void loadRecordSearch() {
//        lblSource.setText(poCheckPrintingRequestController.CheckPrintingRequest().Master().Company().getCompanyName() + " - " + poCheckPrintingRequestController.CheckPrintingRequest().Master().Industry().getDescription());
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnBrowse, btnNew, btnUpdate, btnSave, btnCancel, btnRetrieve, btnHistory, btnClose, btnExport);
        buttons.forEach(button -> button.setOnAction(this::cmdButton_Click));
    }

    private void cmdButton_Click(ActionEvent event) {
        try {
            poJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch (lsButton) {
                case "btnBrowse":
                    poCheckPrintingRequestController.setTransactionStatus(CheckPrintRequestStatus.OPEN + CheckPrintRequestStatus.CONFIRMED);
                    poJSON = poCheckPrintingRequestController.SearchTransaction("");
                    if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                    showRetainedHighlight(false);
                    pnEditMode = poCheckPrintingRequestController.getEditMode();
                    loadTableDetail();
                    loadRecordDetail();
                    break;
                case "btnNew":
                    clearFields();
                    poJSON = poCheckPrintingRequestController.NewTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }

                    loadTableDetail();
                    pnEditMode = poCheckPrintingRequestController.getEditMode();
                    break;
                case "btnUpdate":
                    poJSON = poCheckPrintingRequestController.UpdateTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    pnEditMode = poCheckPrintingRequestController.getEditMode();
                    loadTableDetail();
                    loadRecordDetail();
                    break;
                case "btnSave":
                    if (!ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to save the transaction?")) {
                        return;
                    }
                    if (!isSavingValid()) {
                        return;
                    }
                    if (pnEditMode == EditMode.UPDATE) {
                        poCheckPrintingRequestController.Master().setModifiedDate(oApp.getServerDate());
                        poCheckPrintingRequestController.Master().setModifyingId(oApp.getUserID());
                    }
                    poJSON = poCheckPrintingRequestController.SaveTransaction();
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    ShowMessageFX.Information((String) poJSON.get("message"), pxeModuleName, null);
                    poJSON = poCheckPrintingRequestController.OpenTransaction(poCheckPrintingRequestController.Master().getTransactionNo());
                    Platform.runLater(() -> btnNew.fire());
                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo("Do you want to disregard changes?", pxeModuleName, null)) {
//                        poCheckPrintingRequestController.CheckPrintingRequest().resetMaster();
//                        poCheckPrintingRequestController.CheckPrintingRequest().resetOthers();
                        poCheckPrintingRequestController.Detail().clear();
                        clearFields();
                        loadTableDetail();
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
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(CheckPrintRequest_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isSavingValid() {

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
        JFXUtil.disableAllHighlightByColor(tblVwMain, "#A7C7E7", highlightedRowsMain);
        plOrderNoPartial.clear();
        for (Pair<String, String> pair : plOrderNoFinal) {
            if (!"0".equals(pair.getValue())) {
                JFXUtil.highlightByKey(tblVwMain, pair.getKey(), "#A7C7E7", highlightedRowsMain);
            }
        }
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
                        poJSON = poCheckPrintingRequestController.getDVwithAuthorizeCheckPayment();
                        if ("success".equals(poJSON.get("result"))) {
                            for (int lnCntr = 0; lnCntr <= poCheckPrintingRequestController.getCheckPaymentCount() - 1; lnCntr++) {

                                main_data.add(new ModelDisbursementVoucher_Main(
                                        String.valueOf(lnCntr + 1),
                                        poCheckPrintingRequestController.CheckPayments(lnCntr).Banks().getBankName(),
                                        poCheckPrintingRequestController.CheckPayments(lnCntr).Bank_Account_Master().getAccountNo(),
                                        CustomCommonUtil.formatDateToShortString(poCheckPrintingRequestController.CheckPayments(lnCntr).getTransactionDate()),
                                        poCheckPrintingRequestController.CheckPayments(lnCntr).getSourceNo()
                                ));
                                if (poCheckPrintingRequestController.CheckPayments(lnCntr).getTransactionStatus().equals(DisbursementStatic.VERIFIED)) {
                                    plOrderNoPartial.add(new Pair<>(String.valueOf(lnCntr + 1), "1"));
                                }

                            }
                        }
                        showRetainedHighlight(true);
                        if (main_data.isEmpty()) {
                            tblVwMain.setPlaceholder(placeholderLabel);
                        }
                        JFXUtil.loadTab(pagination, main_data.size(), ROWS_PER_PAGE, tblVwMain, filteredMain_Data);
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(CheckPrintRequest_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                });
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
        JFXUtil.setColumnCenter(tblRowNo, tblBankName, tblBankAccount, tblDate, tblReferenceNo);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwMain);

        filteredMain_Data = new FilteredList<>(main_data, b -> true);
        tblVwMain.setItems(filteredMain_Data);
    }

    private void loadRecordMaster() {
        try {
            lblTransactionStatus.setText(getStatus(poCheckPrintingRequestController.Master().getTransactionStatus()));
            tfTransactionNo.setText(poCheckPrintingRequestController.Master().getTransactionNo() != null ? poCheckPrintingRequestController.Master().getTransactionNo() : "");
            tfBankName.setText(poCheckPrintingRequestController.Master().Banks().getBankName() != null ? poCheckPrintingRequestController.Master().Banks().getBankName() : "");
            tfTotalAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poCheckPrintingRequestController.Master().getTotalAmount(), true));
            taRemarks.setText(poCheckPrintingRequestController.Master().getRemarks() != null ? poCheckPrintingRequestController.Master().getRemarks() : "");
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(CheckPrintRequest_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getStatus(String lsValueStatus) {
        String lsStatus;
        switch (lsValueStatus) {
            case CheckPrintRequestStatus.OPEN:
                lsStatus = "OPEN";
                break;
            case CheckPrintRequestStatus.CONFIRMED:
                lsStatus = "CONFIRMED";
                break;
            case CheckPrintRequestStatus.VOID:
                lsStatus = "VOID";
                break;
            default:
                lsStatus = "STATUS";
                break;
        }
        return lsStatus;
    }

    private void loadRecordDetail() {
        if (pnDetail >= 0) {
            try {
                tfReferNo.setText(poCheckPrintingRequestController.Detail(pnDetail).getSourceNo());
                tfCheckNo.setText(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().getCheckNo());
                tfCheckAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poCheckPrintingRequestController.Master().getTotalAmount(), true));
                tfPayeeNAme.setText(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().Payee().getPayeeName());
                tfDVNo.setText(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().getTransactionNo());
                tfDVAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().getTransactionTotal(), true));
                dpDVDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
                dpCheckDate.setValue(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().getCheckDate() != null
                        ? CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().getCheckDate(), SQLUtil.FORMAT_SHORT_DATE))
                        : null);

                cmbPayeeType.getSelectionModel().select(!poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().getPayeeType().equals("")
                        ? Integer.valueOf(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().getPayeeType()) : -1);
                taRemarksDetails.setText(poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().getRemarks() != null ? poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().getRemarks() : "");

            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(CheckPrintRequest_EntryController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void loadTableDetailFromMain() {
        poJSON = new JSONObject();
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            pnMain = tblVwMain.getSelectionModel().getSelectedIndex();
            ModelDisbursementVoucher_Main selected = (ModelDisbursementVoucher_Main) tblVwMain.getSelectionModel().getSelectedItem();
            if (selected != null) {
                try {
                    int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                    pnMain = pnRowMain;
                    String lsTransactionNo = selected.getIndex05();
                    poJSON = poCheckPrintingRequestController.addCheckPaymentToCheckPrintRequest(lsTransactionNo);
                    if ("success".equals(poJSON.get("result"))) {
                    } else {
                        if ("true".equals((String) poJSON.get("warning"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            if (ShowMessageFX.YesNo("If you want to change bank name, \n"
                                    + "If YES it will remove details and remove bank name."
                                    + "If NO it will stay details",
                                    pxeModuleName, null)) {
                                int detailCount = poCheckPrintingRequestController.getDetailCount();
                                for (int lnCtr = detailCount - 1; lnCtr >= 0; lnCtr--) {
                                    String sourceNo = poCheckPrintingRequestController.Detail(lnCtr).getSourceNo();
                                    if (sourceNo == null || sourceNo.isEmpty()) {
                                        continue; // Don't remove if SourceNo is null or empty
                                    }
                                    poCheckPrintingRequestController.Detail().remove(lnCtr);
                                }
                                pnDetail = 0;
                                JFXUtil.clearTextFields(apDetail);
                                poCheckPrintingRequestController.Master().setBankID("");
                                tfBankName.setText("");
                                poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().setTransactionDate(null);
                                dpDVDate.setValue(null);
                            } else {

                            }
                        } else {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            if (poJSON.get("tableRow") != null) {
                                pnDetail = (int) poJSON.get("tableRow");
                            } else {
                                return;
                            }
                        }
                    }
//                    JFXUtil.disableAllHighlightByColor(tblVwMain, "#A7C7E7", highlightedRowsMain);
                    JFXUtil.highlightByKey(tblVwMain, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                    Platform.runLater(() -> {
                        loadTableDetail();
                        loadRecordDetail();
                        initFields(pnEditMode);
                    });
                } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                    Logger.getLogger(CheckPrintRequest_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void loadTableDetail() {
        pbEnteredDV = false;
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblVwDetail.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);
        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    details_data.clear();
                    int lnCtr;
                    if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                        lnCtr = poCheckPrintingRequestController.getDetailCount() - 1;
                        if (lnCtr >= 0) {
                            if (poCheckPrintingRequestController.Detail(lnCtr).getSourceNo() != null
                                    && !poCheckPrintingRequestController.Detail(lnCtr).getSourceNo().equals("")) {
                                try {
                                    poCheckPrintingRequestController.AddDetail();

                                } catch (CloneNotSupportedException ex) {
                                    Logger.getLogger(CheckPrintRequest_EntryController.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                    for (lnCtr = 0; lnCtr < poCheckPrintingRequestController.getDetailCount(); lnCtr++) {
                        try {

                            details_data.add(
                                    new ModelDisbursementVoucher_Detail(String.valueOf(lnCtr + 1),
                                            poCheckPrintingRequestController.Detail(lnCtr).getSourceNo(),
                                            poCheckPrintingRequestController.Detail(lnCtr).DisbursementMaster().getTransactionNo(),
                                            CustomCommonUtil.formatDateToShortString(poCheckPrintingRequestController.Detail(lnCtr).DisbursementMaster().getTransactionDate()),
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poCheckPrintingRequestController.Detail(lnCtr).DisbursementMaster().getNetTotal(), true),
                                            poCheckPrintingRequestController.Detail(lnCtr).DisbursementMaster().CheckPayments().getCheckNo(),
                                            poCheckPrintingRequestController.Detail(lnCtr).DisbursementMaster().CheckPayments().getCheckDate() != null
                                            ? CustomCommonUtil.formatDateToShortString(
                                                    poCheckPrintingRequestController.Detail(lnCtr).DisbursementMaster().CheckPayments().getCheckDate())
                                            : "",
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poCheckPrintingRequestController.Detail(lnCtr).DisbursementMaster().CheckPayments().getAmount(), true)
                                    ));

                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(CheckPrintRequest_EntryController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (pnDetail < 0 || pnDetail >= details_data.size()) {
                        if (!details_data.isEmpty()) {
                            tblVwDetail.getSelectionModel().select(0);
                            tblVwDetail.getFocusModel().focus(0);
                            pnDetail = tblVwDetail.getSelectionModel().getSelectedIndex();
                        }
                    } else {
                        tblVwDetail.getSelectionModel().select(pnDetail);
                        tblVwDetail.getFocusModel().focus(pnDetail);
                    }
                    loadRecordMaster();
                });
                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblVwDetail.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblVwDetail.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }
        };
        new Thread(task).start();

    }

    private void initTableDetail() {
        JFXUtil.setColumnCenter(tblRowNoDetail, tblReferNoDetail, tblDVNo, tblDVDate, tblCheckNo, tblCheckDate);
        JFXUtil.setColumnRight(tblDVAmount, tblCheckAmount);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwDetail);
        filteredDataDetailDV = new FilteredList<>(details_data, b -> true);

        SortedList<ModelDisbursementVoucher_Detail> sortedData = new SortedList<>(filteredDataDetailDV);
        sortedData.comparatorProperty().bind(tblVwDetail.comparatorProperty());
        tblVwDetail.setItems(sortedData);
        tblVwDetail.autosize();
    }

    private void initTableOnClick() {
        tblVwDetail.setOnMouseClicked(event -> {
            if (!details_data.isEmpty()) {
                if (event.getClickCount() == 1) {
                    pnDetail = tblVwDetail.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    taRemarksDetails.requestFocus();
                    initFields(pnEditMode);
                }
            }
        });

        tblVwMain.setOnMouseClicked(event -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                pnMain = tblVwMain.getSelectionModel().getSelectedIndex();
                if (pnMain >= 0) {
                    if (event.getClickCount() == 2) {
                        loadTableDetailFromMain();
                    }
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

        tblVwDetail.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblVwMain, tblVwDetail);
    }

    private void tableKeyEvents(KeyEvent event) {
        if (!details_data.isEmpty()) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblVwDetail":
                    if (focusedCell != null) {
                        switch (event.getCode()) {
                            case TAB:
                            case DOWN:
                                pnDetail = JFXUtil.moveToNextRow(currentTable);
                                break;
                            case UP:
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
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

    private void initTextFields() {
        //Initialise  TextField KeyPressed
        List<TextField> loTxtFieldKeyPressed = Arrays.asList(tfSearchCompany, tfSearchDepartment, tfSearchBankName);
        loTxtFieldKeyPressed.forEach(tf -> tf.setOnKeyPressed(event -> txtFieldDV_KeyPressed(event)));
    }

    private void txtFieldDV_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        String lsID = (((TextField) event.getSource()).getId());
        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
        poJSON = new JSONObject();
        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case F3:
                        switch (lsID) {
                            case "tfSearchCompany":
//                                poJSON = poCheckPrintingRequestController.CheckPrintingRequest().(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
//                                tfSearchCompany.setText(poCheckPrintingRequestController.Master().Payee().getPayeeName() != null ? poCheckPrintingRequestController.CheckPrintingRequest().Master().Payee().getPayeeName() : "");
                                loadTableMain();
                                break;
                            case "tfSearchDepartment":
//                                poJSON = poCheckPrintingRequestController.SearchDepartment(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }

//                                tfSearchDepartment.setText(poCheckPrintingRequestController.Master().getDisbursementType().equals(DisbursementStatic.DisbursementType.CHECK) ? poCheckPrintingRequestController.CheckPrintingRequest().Master().Payee().getPayeeName() : "");
                                loadTableMain();
                                break;
                            case "tfSearchBankName":
                                poJSON = poCheckPrintingRequestController.SearchBanks(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfSearchBankName.setText(poCheckPrintingRequestController.Master().Banks().getBankName() != null ? poCheckPrintingRequestController.Master().Banks().getBankName() : "");
                                loadTableMain();
                                break;
                        }
                        event.consume();
                        break;
                    default:
                        break;

                }
            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(CheckPrintRequest_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void movePrevious() {
        apDetail.requestFocus();

        initFields(pnEditMode);
    }

    private void moveNext() {
        apDetail.requestFocus();

        initFields(pnEditMode);
    }

    private void initTextAreaFields() {
        //Initialise  TextArea Focus
        taRemarks.focusedProperty().addListener(txtArea_Focus);
        taRemarksDetails.focusedProperty().addListener(txtArea_Focus);
        //Initialise  TextArea KeyPressed
        taRemarks.setOnKeyPressed(event -> txtArea_KeyPressed(event));
        taRemarksDetails.setOnKeyPressed(event -> txtArea_KeyPressed(event));
    }

    final ChangeListener<? super Boolean> txtArea_Focus = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> o, Boolean ov, Boolean nv) {
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
                        poCheckPrintingRequestController.Master().setRemarks(lsValue);
                        break;
                    case "taRemarksDetails":
                        try {
                        poCheckPrintingRequestController.Detail(pnDetail).DisbursementMaster().CheckPayments().setRemarks(lsValue);

                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(CheckPrintRequest_EntryController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    Platform.runLater(() -> {
                        PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                        delay.setOnFinished(event -> {
                            loadTableDetail();
                        });
                        delay.play();
                    });
                    break;

                }
            } else {
                txtArea.selectAll();
            }
        }
    };

    private void txtArea_KeyPressed(KeyEvent event) {
        TextArea txtArea = (TextArea) event.getSource();
        String lsID = (((TextArea) event.getSource()).getId());
        String lsValue = (txtArea.getText() == null ? "" : txtArea.getText());
        poJSON = new JSONObject();
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEnteredDV = true;
                    CommonUtils.SetNextFocus(txtArea);
                    switch (lsID) {
                        case "taRemarksDetails":
                            moveNext();
                            event.consume();
                            break;
                    }
                    event.consume();
                    break;
                case UP:
                    switch (lsID) {
                        case "taRemarksDetails":
                            movePrevious();
                            event.consume();
                            break;
                    }
                    event.consume();
                    break;
                case DOWN:
                    switch (lsID) {
                        case "taRemarksDetails":
                            moveNext();
                            event.consume();
                            break;
                    }
                    break;
                default:
                    break;

            }
        }
    }

    private void initComboBox() {
        cmbPayeeType.setItems(cPayeeType);

    }

    private void clearFields() {
        previousSearchedTextField = null;
        lastFocusedTextField = null;
        JFXUtil.setValueToNull(null, dpTransactionDate, dpDVDate, dpCheckDate);
        JFXUtil.setValueToNull(null, cmbPayeeType);
        JFXUtil.clearTextFields(apDetail, apMaster);
        CustomCommonUtil.setSelected(false, chbkUploaded);
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        JFXUtil.setDisabled(!lbShow, apMaster);
        JFXUtil.setDisabled(true, apDetail);
        if (main_data.isEmpty()) {
            Label placeholderLabel = new Label("NO RECORD TO LOAD");
            tblVwMain.setPlaceholder(placeholderLabel);
            pagination.setManaged(false);
            pagination.setVisible(false);
        }
        if (pnDetail >= 0) {
            if (tfReferNo.getText() != null) {
                JFXUtil.setDisabled(!lbShow, apDetail);
            }
        }
    }

    private void initButton(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        JFXUtil.setButtonsVisibility(!lbShow, btnBrowse, btnClose, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(false, btnUpdate, btnExport);
        JFXUtil.setButtonsVisibility(fnEditMode != EditMode.ADDNEW && fnEditMode != EditMode.UNKNOWN, btnHistory);
        if (fnEditMode == EditMode.READY) {
            switch (poCheckPrintingRequestController.Master().getTransactionStatus()) {
                case CheckPrintRequestStatus.OPEN:
                    JFXUtil.setButtonsVisibility(true, btnUpdate);
                    break;
                case CheckPrintRequestStatus.CONFIRMED:
                    JFXUtil.setButtonsVisibility(true, btnUpdate, btnExport);
                    break;
            }
        }
    }

    private void initTextFieldsProperty() {
        tfSearchCompany.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
//                    if (!isExchangingSupplier()) {
//                        return;
//                    }
//                    poCheckPrintingRequestController.CheckPrintingRequest().Master().setCompanyID("");
                    tfSearchCompany.setText("");
                    psCompanyId = "";
                    loadTableMain();
                }
            }
        }
        );
        tfSearchDepartment.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
//                    poCheckPrintingRequestController.CheckPrintingRequest().CheckPayments().getModel().setBankID("");
                    tfSearchDepartment.setText("");
                    loadTableMain();
                }
            }
        }
        );
        tfSearchBankName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poCheckPrintingRequestController.Master().setBankID("");
                    tfSearchBankName.setText("");
                    loadTableMain();
                }
            }
        }
        );
    }
}
