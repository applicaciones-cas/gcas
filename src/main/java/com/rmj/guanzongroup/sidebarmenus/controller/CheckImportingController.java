/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelCheckImporting;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.io.File;
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
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import static org.apache.poi.hssf.usermodel.HeaderFooter.file;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.CheckImporting;
import static ph.com.guanzongroup.cas.cashflow.CheckImporting.importToList;
import ph.com.guanzongroup.cas.cashflow.Disbursement;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;
import ph.com.guanzongroup.cas.cashflow.status.DisbursementStatic;

/**
 * FXML Controller class
 *
 * @author
 */
public class CheckImportingController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    private final String pxeModuleName = "Check Importing";
    private CheckImporting poCheckImporting;

    public int pnEditMode;
    private double xOffset = 0;
    private double yOffset = 0;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSearchBankID = "";
    private String psImportingFilePath = "";

    private unloadForm poUnload = new unloadForm();

    private ObservableList<ModelCheckImporting> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelCheckImporting> filteredMain_Data;

//    List<Pair<String, String>> plOrderNoPartial = new ArrayList<>();
//    List<Pair<String, String>> plOrderNoFinal = new ArrayList<>();
    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();

    @FXML
    private AnchorPane AnchorMain, apBrowse, apButton;
    @FXML
    private Label lblSource;
    @FXML
    private TextField tfSearchBankName;
    @FXML
    private Button btnImportFile, btnSave, btnCancel, btnClose;
    @FXML
    private TableView tblVwMain;
    @FXML
    private TableColumn tblRowNo, tblDVNo, tblDVDate, tblBankName, tblBankRefNo, tblDateReceived, tblCheckNo, tblCheckDate, tblCheckPrintStatus;
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
            poCheckImporting = new CashflowControllers(oApp, null).CheckImporting();
            poCheckImporting.setTransactionStatus(DisbursementStatic.VERIFIED);
            poJSON = new JSONObject();
            poJSON = poCheckImporting.InitTransaction();
            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }
            initAll();
            Platform.runLater(() -> {
                poCheckImporting.Master().setIndustryID(psIndustryId);
                poCheckImporting.Master().setCompanyID(psCompanyId);
                loadRecordSearch();
            });
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(CheckImportingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initAll() {
        initButtonsClickActions();
        initTextFields();
        initTableMain();
        pnEditMode = EditMode.UNKNOWN;
        initButtons(pnEditMode);
        initTextFieldsProperty();
    }

    private void loadRecordSearch() {
        try {
            lblSource.setText(poCheckImporting.Master().Company().getCompanyName() + " - " + poCheckImporting.Master().Industry().getDescription());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(CheckImportingController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnImportFile, btnSave, btnCancel, btnClose);
        buttons.forEach(button -> button.setOnAction(this::cmdButton_Click));
    }

    private void cmdButton_Click(ActionEvent event) {
        try {
            poJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();

            switch (lsButton) {
                case "btnImportFile":
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Import File");
                    FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv");
                    FileChooser.ExtensionFilter excelFilter = new FileChooser.ExtensionFilter("Excel Files (*.xlsx, *.xls)", "*.xlsx", "*.xls");

                    fileChooser.getExtensionFilters().addAll(csvFilter, excelFilter);
                    Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    File selectedFile = fileChooser.showOpenDialog(currentStage);
                    
                    try {
                    List<CheckImporting.CheckRequest> data = poCheckImporting.importToList(selectedFile.toPath());
                    ObservableList<CheckImporting.CheckRequest> items = FXCollections.observableArrayList(data);
//                        items.forEach(System.out::println);     // relies on CheckRequest.toString()
                
                        /* 2.  Custom formatting -------------------------------------------- */
                        items.forEach(cr -> System.out.printf(
                                "Voucher: %-10s  Date: %-10s  Amount: %10s  Payee: %s%n",
                                cr.getVoucherNo(),
                                cr.getCheckDate(),
                                cr.getAmount(),
                                cr.getPayeeName()));    

                        /* 3.  If you just want a single dump --------------------------------
 * (.toString() on ObservableList calls Collection.toString()) */
                        System.out.println(items.get(1).getVoucherNo());
                        List<String> voucherNos = new ArrayList<>();
                        for (CheckImporting.CheckRequest cr : items) {
                            voucherNos.add(cr.getVoucherNo());
                        }
                        System.out.println(voucherNos);
                        poJSON = poCheckImporting.getDVwithAuthorizeCheckPayment(voucherNos);
                         
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            return;
                        }
                        

                } catch (Exception ex) {
        //            showAlert("Import failed", ex.getMessage());
                    ex.printStackTrace();
                }

                    if (selectedFile != null) {
                        psImportingFilePath = selectedFile.getAbsolutePath();
                        System.out.println("Imported File: " + psImportingFilePath);
                        poJSON = poCheckImporting.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                            return;
                        }
                        pnEditMode = poCheckImporting.getEditMode();
                    } else {
                        ShowMessageFX.Warning("No file selected.", pxeModuleName, null);
                        psImportingFilePath = "";
                    }

                    break;
                case "btnSave":
                    if (!ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to save the transaction?")) {
                        return;
                    }
                    if (pnEditMode == EditMode.UPDATE) {
                        poCheckImporting.Master().setModifiedDate(oApp.getServerDate());
                        poCheckImporting.Master().setModifyingId(oApp.getUserID());
                    }
                    poJSON = poCheckImporting.SaveTransaction();
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        return;
                    }
                    psImportingFilePath = "";
                    pnEditMode = poCheckImporting.getEditMode();
                    ShowMessageFX.Information((String) poJSON.get("message"), pxeModuleName, null);
                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo("Do you want to disregard changes?", pxeModuleName, null)) {
                        psImportingFilePath = "";
                        loadTableMain();
                        pnEditMode = EditMode.UNKNOWN;
                        break;
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
            initButtons(pnEditMode);
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(CheckImportingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextFields() {
        //Initialise  TextField KeyPressed
        List<TextField> loTxtFieldKeyPressed = Arrays.asList(tfSearchBankName, tfSearchBankName);
        loTxtFieldKeyPressed.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
    }

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        String lsID = (((TextField) event.getSource()).getId());
        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
        poJSON = new JSONObject();
        if (null != event.getCode()) {
            try {
                switch (event.getCode()) {
                    case F3:
                        switch (lsID) {
                            case "tfSearchBankName":
                                if (!isExchangingBankName()) {
                                    return;
                                }
                                poJSON = poCheckImporting.SearchBanks(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
//                                tfSearchBankName.setText(poCheckImporting.CheckPayments().getModel().Banks().getBankName() != null ? poCheckImporting.CheckPayments().getModel().Banks().getBankName() : "");
//                                psSearchBankID = poCheckImporting.CheckPayments().getModel().getBankID();
                                break;
                        }
                        CommonUtils.SetNextFocus((TextField) event.getSource());
                        loadTableMain();
                        event.consume();
                    default:
                        break;
                }
            } catch (GuanzonException | SQLException ex) {
                Logger.getLogger(CheckImportingController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void loadTableMain() {
//        ProgressIndicator progressIndicator = new ProgressIndicator();
//        progressIndicator.setMaxHeight(50);
//        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
//        StackPane loadingPane = new StackPane(progressIndicator);
//        loadingPane.setAlignment(Pos.CENTER);
//        tblVwMain.setPlaceholder(loadingPane);
//        progressIndicator.setVisible(true);
//
//        Label placeholderLabel = new Label("NO RECORD TO LOAD");
//        placeholderLabel.setStyle("-fx-font-size: 10px;");
//
//        Task<Void> task = new Task<Void>() {
//            @Override
//            protected Void call() throws Exception {
//                Platform.runLater(() -> {
//                    try {
//                        main_data.clear();
//                        plOrderNoFinal.clear();
//                        poJSON = poCheckImportingController.getDisbursementForCheckPrinting(psSearchBankID, psSearchBankAccountID, psSearchDVDateFrom, psSearchDVDateTo);
//                        if ("success".equals(poJSON.get("result"))) {
//                            if (poCheckImportingController.getDisbursementMasterCount() > 0) {
//                                int checkIndex = 0;
//                                int otherIndex = 0;
//                                for (int lnCntr = 0; lnCntr < poCheckImportingController.getDisbursementMasterCount(); lnCntr++) {
//
//
//
//                                    main_data.add(new ModelCheckPrinting(
//                                            String.valueOf(lnCntr + 1),
//                                            "",
//                                            poCheckImportingController.poDisbursementMaster(lnCntr).getTransactionNo(),
//                                            CustomCommonUtil.formatDateToShortString(poCheckImportingController.poDisbursementMaster(lnCntr).getTransactionDate()),
//                                            poCheckImportingController.poDisbursementMaster(lnCntr).Payee().getPayeeName(),
//                                            poCheckImportingController.poDisbursementMaster(lnCntr).Payee().getPayeeName(),
//                                            poCheckImportingController.poDisbursementMaster(lnCntr).CheckPayments().Banks().getBankName(),
//                                            poCheckImportingController.poDisbursementMaster(lnCntr).CheckPayments().Bank_Account_Master().getAccountNo(),
//                                            poCheckImportingController.poDisbursementMaster(lnCntr).CheckPayments().getCheckNo(),
//                                            CustomCommonUtil.formatDateToShortString(poCheckImportingController.poDisbursementMaster(lnCntr).getTransactionDate()),
//                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poCheckImportingController.poDisbursementMaster(lnCntr).getNetTotal(), true)
//                                    ));
//                                }
//                            }
//                        } else {
//                            main_data.clear();
////                            }
//                        }
//                        showRetainedHighlight(true);
//                        if (main_data.isEmpty()) {
//                            tblVwMain.setPlaceholder(placeholderLabel);
//                            ShowMessageFX.Warning(null, pxeModuleName, "No records found");
//                            chckSelectAll.setSelected(false);
//                            listOfDVToAssign.clear();
//                        }
//                        JFXUtil.loadTab(pagination, main_data.size(), ROWS_PER_PAGE, tblVwMain, filteredMain_Data);
//                        initButtons();
//                    } catch (GuanzonException | SQLException ex) {
//                        Logger.getLogger(CheckImportingController.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                });
//                return null;
//            }
//
//            @Override
//
//            protected void succeeded() {
//                btnRetrieve.setDisable(false);
//                placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed
//                if (main_data == null || main_data.isEmpty()) {
//                    tblVwMain.setPlaceholder(placeholderLabel);
//                    pagination.setManaged(false);
//                    pagination.setVisible(false);
//                } else {
//                    pagination.setPageCount(0);
//                    pagination.setVisible(true);
//                    pagination.setManaged(true);
//                    progressIndicator.setVisible(false);
//                    progressIndicator.setManaged(false);
//                    tblVwMain.toFront();
//                }
//            }
//
//            @Override
//            protected void failed() {
//                if (main_data == null || main_data.isEmpty()) {
//                    tblVwMain.setPlaceholder(placeholderLabel);
//                    pagination.setManaged(false);
//                    pagination.setVisible(false);
//                }
//                btnRetrieve.setDisable(false);
//                progressIndicator.setVisible(false);
//                progressIndicator.setManaged(false);
//                tblVwMain.toFront();
//            }
//        };
//        new Thread(task).start(); // Run task in background
    }

    private void initTableMain() {
        JFXUtil.setColumnCenter(tblRowNo, tblDVNo, tblDVDate, tblBankName, tblBankRefNo, tblDateReceived, tblCheckNo, tblCheckDate, tblCheckPrintStatus);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwMain);

        filteredMain_Data = new FilteredList<>(main_data, b -> true);
        tblVwMain.setItems(filteredMain_Data);
    }

    private void initButtons(int fnEditMode) {
        JFXUtil.setButtonsVisibility(fnEditMode != EditMode.UPDATE, btnClose, btnImportFile);
        JFXUtil.setButtonsVisibility(!psImportingFilePath.isEmpty() && fnEditMode == EditMode.UPDATE, btnSave, btnCancel);
    }

    private void initTextFieldsProperty() {
        tfSearchBankName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    if (!isExchangingBankName()) {
                        return;
                    }
//                    poCheckImporting.CheckPayments().getModel().setBankID("");
//                    poCheckImporting.CheckPayments().getModel().setBankAcountID("");
                    tfSearchBankName.setText("");
                    psSearchBankID = "";
                    loadTableMain();
                }
            }
        }
        );
    }

    private void showRetainedHighlight(boolean isRetained) {
//        if (isRetained) {
//            for (Pair<String, String> pair : plOrderNoPartial) {
//                if (!"0".equals(pair.getValue())) {
//                    plOrderNoFinal.add(new Pair<>(pair.getKey(), pair.getValue()));
//                }
//            }
//        }
//        JFXUtil.disableAllHighlightByColor(tblVwList, "#A7C7E7", highlightedRowsMain);
//        plOrderNoPartial.clear();
//        for (Pair<String, String> pair : plOrderNoFinal) {
//            if (!"0".equals(pair.getValue())) {
//                JFXUtil.highlightByKey(tblVwList, pair.getKey(), "#A7C7E7", highlightedRowsMain);
//            }
//        }
    }

    private boolean isExchangingBankName() {
        if (pnEditMode == EditMode.UPDATE) {
            boolean isHaveSourceNo = false;
            if (!main_data.isEmpty()) {
                isHaveSourceNo = true;
            }
            if (isHaveSourceNo) {
                if (ShowMessageFX.YesNo("Check Importing have already items, are you sure you want to change bank name?", pxeModuleName, null)) {
                    psImportingFilePath = "";
                    pnEditMode = EditMode.UNKNOWN;
                    loadTableMain();
                } else {
                    if (psSearchBankID.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
