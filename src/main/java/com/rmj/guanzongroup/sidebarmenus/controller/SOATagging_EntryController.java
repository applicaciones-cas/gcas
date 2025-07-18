/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSOATagging_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSOATagging_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
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
import javafx.scene.layout.HBox;
import ph.com.guanzongroup.cas.cashflow.status.SOATaggingStatus;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import javafx.animation.PauseTransition;
import javafx.util.Pair;
import java.util.ArrayList;
import ph.com.guanzongroup.cas.cashflow.SOATagging;
import ph.com.guanzongroup.cas.cashflow.status.SOATaggingStatic;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.control.ComboBox;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.constant.UserRight;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;

/**
 * FXML Controller class
 *
 * @author Aldrich & Arsiela Team 2 06102025
 */
public class SOATagging_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    static SOATagging poSOATaggingController;
    private JSONObject poJSON;
    public int pnEditMode;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private String pxeModuleName = "";
    private boolean isGeneral = false;
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private boolean pbEntered = false;
    boolean pbKeyPressed = false;
    private ObservableList<ModelSOATagging_Main> main_data = FXCollections.observableArrayList();
    private ObservableList<ModelSOATagging_Detail> details_data = FXCollections.observableArrayList();

    private FilteredList<ModelSOATagging_Main> filteredData;
    private FilteredList<ModelSOATagging_Detail> filteredDataDetail;
    List<Pair<String, String>> plOrderNoPartial = new ArrayList<>();
    List<Pair<String, String>> plOrderNoFinal = new ArrayList<>();

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail, apMainList;
    @FXML
    private HBox hbButtons, hboxid;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnHistory, btnRetrieve, btnClose;
    @FXML
    private TextField tfTransactionNo, tfSOANo, tfClient, tfIssuedTo, tfTransactionTotal, tfVatAmount, tfNonVatSales, tfZeroVatSales, tfVatExemptSales,
            tfNetTotal, tfCompany, tfDiscountAmount, tfFreight, tfSourceNo, tfSourceCode, tfReferenceNo, tfCreditAmount, tfDebitAmount, tfAppliedAmtDetail;
    @FXML
    private DatePicker dpTransactionDate, dpReferenceDate;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView tblViewTransDetailList, tblViewMainList;
    @FXML
    private TableColumn tblRowNoDetail, tblSourceNoDetail, tblSourceCodeDetail, tblReferenceNoDetail, tblCreditAmtDetail, tblDebitAmtDetail, tblAppliedAmtDetail, tblRowNo, tblTransType, tblSupplier, tblDate, tblReferenceNo;
    @FXML
    private Pagination pgPagination;
    @FXML
    private ComboBox cmbTransType;
    ObservableList<String> TransactionType = FXCollections.observableArrayList(
            "ALL",
            "Cache Payable",
            "PRF"
    );

    public void setTabTitle(String lsTabTitle, boolean isGeneral) {
        this.pxeModuleName = lsTabTitle;
        this.isGeneral = isGeneral;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        psIndustryId = isGeneral ? "" : psIndustryId;

        poSOATaggingController = new CashflowControllers(oApp, null).SOATagging();
        poJSON = new JSONObject();
        poJSON = poSOATaggingController.InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            System.err.println((String) poJSON.get("message"));
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
        initComboBoxes();
        initTextFields();
        initDatePickers();
        initMainGrid();
        initDetailsGrid();
        initTableOnClick();
        clearTextFields();

        Platform.runLater(() -> {
            poSOATaggingController.Master().setIndustryId(psIndustryId);
//            poSOATaggingController.Master().setCompanyID(psCompanyId);
            poSOATaggingController.setIndustryId(psIndustryId);
            poSOATaggingController.setCompanyId(psCompanyId);
            poSOATaggingController.setCategoryId(psCategoryId);
            poSOATaggingController.initFields();
            loadRecordSearch();
            btnNew.fire();
        });

        pgPagination.setPageCount(1);

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);

        JFXUtil.initKeyClickObject(apMainAnchor, lastFocusedTextField, previousSearchedTextField);
    }

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

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        poJSON = new JSONObject();
        String tabText = "";

        try {
            Object source = event.getSource();
            if (source instanceof Button) {
                Button clickedButton = (Button) source;
                String lsButton = clickedButton.getId();
                switch (lsButton) {
                    case "btnBrowse":
                        poSOATaggingController.setTransactionStatus(SOATaggingStatus.OPEN);
                        poJSON = poSOATaggingController.searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                        pnEditMode = poSOATaggingController.getEditMode();
                        break;
                    case "btnClose":
                        unloadForm appUnload = new unloadForm();
                        if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?") == true) {
                            appUnload.unloadForm(apMainAnchor, oApp, pxeModuleName);
                        } else {
                            return;
                        }
                        break;
                    case "btnUpdate":
                        poJSON = poSOATaggingController.OpenTransaction(poSOATaggingController.Master().getTransactionNo());
                        poJSON = poSOATaggingController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poSOATaggingController.getEditMode();
                        break;
                    case "btnSearch":
                        String lsMessage = "Focus a searchable textfield to search";
                        if ((lastFocusedTextField.get() != null)) {
                            if (lastFocusedTextField.get() instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField.get();
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apBrowse, apMaster, apDetail).contains(tf.getId())) {

                                    if (lastFocusedTextField.get() == previousSearchedTextField.get()) {
                                        break;
                                    }
                                    previousSearchedTextField.set(lastFocusedTextField.get());
                                    // Create a simulated KeyEvent for F3 key press
                                    JFXUtil.makeKeyPressed(tf, KeyCode.F3);
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
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                            pnEditMode = EditMode.UNKNOWN;
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        retrievePayables(false);
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poSOATaggingController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poSOATaggingController.AddDetail();
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON = poSOATaggingController.OpenTransaction(poSOATaggingController.Master().getTransactionNo());
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poSOATaggingController.Master().getTransactionStatus().equals(SOATaggingStatus.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poSOATaggingController.ConfirmTransaction("Confirmed");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.showRetainedHighlight(true, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                                btnNew.fire();
                            }
                        } else {
                            return;
                        }
                        return;
                    case "btnNew":
                        //Clear data
                        poSOATaggingController.resetMaster();
                        clearTextFields();
                        poJSON = poSOATaggingController.NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        poSOATaggingController.initFields();
                        pnEditMode = poSOATaggingController.getEditMode();
                        JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                        break;

                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                        break;
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnSave", "btnConfirm", "btnReturn", "btnVoid", "btnCancel")) {
                    poSOATaggingController.resetMaster();
                    poSOATaggingController.Detail().clear();
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();

                    poSOATaggingController.Master().setIndustryId(psIndustryId);
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnAddAttachment", "btnRemoveAttachment",
                        "btnArrowRight", "btnArrowLeft", "btnRetrieve", "btnClose")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                initButton(pnEditMode);

                if (lsButton.equals("btnUpdate")) {
                    if (poSOATaggingController.Detail(pnDetail).getSourceNo() != null && !poSOATaggingController.Detail(pnDetail).getSourceNo().equals("")) {
                        tfAppliedAmtDetail.requestFocus();
                    } else {
                        tfSourceNo.requestFocus();
                    }
                }

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadHighlightFromDetail() {
        try {
            String lsTransNoBasis = "";
            for (int lnCtr = 0; lnCtr < poSOATaggingController.getDetailCount(); lnCtr++) {
                switch (poSOATaggingController.Detail(lnCtr).getSourceCode()) {
                    case SOATaggingStatic.PaymentRequest:
                        lsTransNoBasis = poSOATaggingController.Detail(lnCtr).PaymentRequestMaster().getTransactionNo();
                        break;
                    case SOATaggingStatic.CachePayable: {
                        lsTransNoBasis = poSOATaggingController.Detail(lnCtr).CachePayableMaster().getTransactionNo();
                    }
                    break;
                }
                if (!JFXUtil.isObjectEqualTo(poSOATaggingController.Detail(lnCtr).getAppliedAmount(), null, "")) {
                    if (poSOATaggingController.Detail(lnCtr).getAppliedAmount().doubleValue() > 0.0000) {
                        plOrderNoPartial.add(new Pair<>(lsTransNoBasis, "1"));
                    } else {
                        plOrderNoPartial.add(new Pair<>(lsTransNoBasis, "0"));
                    }
                }
            }
            for (Pair<String, String> pair : plOrderNoPartial) {
                if (!"".equals(pair.getKey()) && pair.getKey() != null) {
                    JFXUtil.highlightByKey(tblViewMainList, pair.getKey(), "#A7C7E7", highlightedRowsMain);
                }
            }
            JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, false);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrievePayables(boolean isInReferenceNo) {
        poJSON = new JSONObject();
        String lsTransType = "ALL";
        switch(cmbTransType.getSelectionModel().getSelectedIndex()){
            case 1:
                lsTransType = SOATaggingStatic.CachePayable;
                break;
            case 2:
                lsTransType = SOATaggingStatic.PaymentRequest;
                break;
        }
        
        if (isInReferenceNo) {
            poJSON = poSOATaggingController.loadPayables(tfClient.getText(), tfCompany.getText(), tfIssuedTo.getText(), tfReferenceNo.getText(), lsTransType);
        } else {
            //general
            poJSON = poSOATaggingController.loadPayables(tfClient.getText(), tfCompany.getText(), tfIssuedTo.getText(),  "", lsTransType);
        }

        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        } else {
            Platform.runLater(() -> {
                loadTableMain();
            });
        }
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = (txtField.getId());
        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }
        poJSON = new JSONObject();
        if (!nv) {
            /*Lost Focus*/
            lsValue = lsValue.trim();
            switch (lsID) {
                case "taRemarks"://Remarks
                    poJSON = poSOATaggingController.Master().setRemarks(lsValue);
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                    break;
            }
        } else {
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtDetail_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());

        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfAppliedAmtDetail":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (poSOATaggingController.Detail(pnDetail).getAppliedAmount() != null
                            && !"".equals(poSOATaggingController.Detail(pnDetail).getAppliedAmount())) {
                        if (poSOATaggingController.Detail(pnDetail).getTransactionTotal().doubleValue() < Double.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Applied Amount cannot be greater than the transaction total");
                            poSOATaggingController.Detail(pnDetail).setAppliedAmount(0.0000);
                            tfAppliedAmtDetail.requestFocus();
                            break;
                        }
                    }

                    poJSON = poSOATaggingController.Detail(pnDetail).setAppliedAmount((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    if (pbEntered) {
                        moveNext();
                        pbEntered = false;
                    }
                    break;
            }
            Platform.runLater(() -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                delay.setOnFinished(event -> {
                    loadTableDetail();
                });
                delay.play();
            });
        }

    };

    final ChangeListener<? super Boolean> txtMaster_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());

        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfSOANo":
                    if (!lsValue.isEmpty()) {
                        poJSON = poSOATaggingController.Master().setSOANumber(lsValue);
                    } else {
                        poJSON = poSOATaggingController.Master().setSOANumber("");
                    }
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        tfSOANo.setText("");
                        break;
                    }
                    break;
                case "tfCompany":
                    if (lsValue.isEmpty()) {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            if (poSOATaggingController.Master().getCompanyId() != null && !"".equals(poSOATaggingController.Master().getCompanyId())) {
                                if (poSOATaggingController.getDetailCount() > 1) {
                                    if (!pbKeyPressed) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the company name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                            poSOATaggingController.removeDetails();
                                            JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                                            loadTableDetail();
                                        } else {
                                            loadRecordMaster();
                                            return;
                                        }
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                }
                            }
                        }
                        poJSON = poSOATaggingController.Master().setCompanyId("");
                    }
                    break;
                case "tfClient":
                    if (lsValue.isEmpty()) {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            if (poSOATaggingController.Master().getClientId() != null && !"".equals(poSOATaggingController.Master().getClientId())) {
                                if (poSOATaggingController.getDetailCount() > 1) {
                                    if (!pbKeyPressed) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the supplier name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                            poSOATaggingController.removeDetails();
                                            JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                                            loadTableDetail();
                                        } else {
                                            loadRecordMaster();
                                            return;
                                        }
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                }
                            }
                        }
                        poJSON = poSOATaggingController.Master().setClientId("");
                    }
                    break;
                case "tfIssuedTo":
                    if (lsValue.isEmpty()) {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            if (poSOATaggingController.Master().getIssuedTo() != null && !"".equals(poSOATaggingController.Master().getIssuedTo())) {
                                if (poSOATaggingController.getDetailCount() > 1) {
                                    if (!pbKeyPressed) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the payee name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                            poSOATaggingController.removeDetails();
                                            JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                                            loadTableDetail();
                                        } else {
                                            loadRecordMaster();
                                            return;
                                        }
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                }
                            }
                        }
                        poJSON = poSOATaggingController.Master().setIssuedTo("");
                    }
                    break;
                case "tfDiscountAmount":
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (Double.valueOf(lsValue) > 0.00) {
                        if (poSOATaggingController.Master().getTransactionTotal().doubleValue() < Double.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Discount amount cannot be greater than the transaction total.");
                            poSOATaggingController.Master().setDiscountAmount(0.0000);
                            tfDiscountAmount.setText("0.0000");
                            tfDiscountAmount.requestFocus();
                            break;
                        }
                    }

                    poJSON = poSOATaggingController.Master().setDiscountAmount((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfFreight":
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (Double.valueOf(lsValue) > 0.00) {
                        if (poSOATaggingController.Master().getTransactionTotal().doubleValue() < Double.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Freight amount cannot be greater than the transaction total.");
                            poSOATaggingController.Master().setFreightAmount(0.0000);
                            tfFreight.setText("0.0000");
                            tfFreight.requestFocus();
                            break;
                        }
                    }

                    poJSON = poSOATaggingController.Master().setDiscountAmount((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
            }
            
            System.out.println("Company : " + poSOATaggingController.Master().getCompanyId());
            System.out.println("Supplier : " + poSOATaggingController.Master().getClientId());
            System.out.println("Payee : " + poSOATaggingController.Master().getIssuedTo());
            loadRecordMaster();
        }
    };

    public void moveNext() {
        double ldblAppliedAmt = poSOATaggingController.Detail(pnDetail).getAppliedAmount().doubleValue();
        apDetail.requestFocus();
        double ldblNewValue = poSOATaggingController.Detail(pnDetail).getAppliedAmount().doubleValue();
        if (ldblAppliedAmt != ldblNewValue && (ldblAppliedAmt > 0
                && poSOATaggingController.Detail(pnDetail).getSourceNo() != null
                && !"".equals(poSOATaggingController.Detail(pnDetail).getSourceNo()))) {
            tfAppliedAmtDetail.requestFocus();
        } else {
            pnDetail = JFXUtil.moveToNextRow(tblViewTransDetailList);
            loadRecordDetail();
            if (poSOATaggingController.Detail(pnDetail).getSourceNo() != null && !poSOATaggingController.Detail(pnDetail).getSourceNo().equals("")) {
                tfAppliedAmtDetail.requestFocus();
            } else {
                tfReferenceNo.requestFocus();
            }
        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            int lnRow = pnDetail;

            TableView<?> currentTable = tblViewTransDetailList;
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();

            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEntered = true;
                    CommonUtils.SetNextFocus(txtField);
                    event.consume();
                    break;
                case UP:
                    switch (lsID) {
                        case "tfReferenceNo":
                        case "tfAppliedAmtDetail":
                            double ldblAppliedAmt = poSOATaggingController.Detail(pnDetail).getAppliedAmount().doubleValue();
                            apDetail.requestFocus();
                            double ldblNewValue = poSOATaggingController.Detail(pnDetail).getAppliedAmount().doubleValue();
                            if (ldblAppliedAmt != ldblNewValue && (ldblAppliedAmt > 0
                                    && poSOATaggingController.Detail(pnDetail).getSourceNo() != null
                                    && !"".equals(poSOATaggingController.Detail(pnDetail).getSourceNo()))) {
                                tfAppliedAmtDetail.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
                                loadRecordDetail();
                                if (poSOATaggingController.Detail(pnDetail).getSourceNo() != null && !poSOATaggingController.Detail(pnDetail).getSourceNo().equals("")) {
                                    tfAppliedAmtDetail.requestFocus();
                                } else {
                                    tfReferenceNo.requestFocus();
                                }
                                event.consume();
                            }
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfReferenceNo":
                        case "tfAppliedAmtDetail":
                            moveNext();
                            event.consume();
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfReferenceNo":
                            retrievePayables(true);
                            break;
                        case "tfClient":
//                            if (poSOATaggingController.Master().getCompanyId() == null
//                                    || "".equals(poSOATaggingController.Master().getCompanyId())) {
//                                ShowMessageFX.Warning(null, pxeModuleName, "Company Name is not set.");
//                                return;
//                            }
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poSOATaggingController.getDetailCount() > 1) {
                                    pbKeyPressed = true;
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the supplier name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                        poSOATaggingController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                    pbKeyPressed = false;
                                }
                            }

                            poJSON = poSOATaggingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfClient.setText("");
                                break;
                            }
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    if (!"".equals(poSOATaggingController.Master().getClientId())) {
                                        retrievePayables(false);
                                    }
                                });
                                delay.play();
                            });
                            loadRecordMaster();
                            return;
                        case "tfCompany":
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poSOATaggingController.getDetailCount() > 1) {
                                    pbKeyPressed = true;
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the company name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                        poSOATaggingController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                    pbKeyPressed = false;
                                }
                            }

                            poJSON = poSOATaggingController.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfClient.setText("");
//                                psSupplierId = "";
                                break;
                            }
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    if (!"".equals(poSOATaggingController.Master().getCompanyId())) {
                                        retrievePayables(false);
                                    }
                                });
                                delay.play();
                            });
                            loadRecordMaster();
                            return;
                        case "tfIssuedTo":
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poSOATaggingController.getDetailCount() > 1) {
                                    pbKeyPressed = true;
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the payee name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                        poSOATaggingController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                    pbKeyPressed = false;
                                }
                            }

                            poJSON = poSOATaggingController.SearchPayee(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfIssuedTo.setText("");
                                break;
                            }
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    if (!"".equals(poSOATaggingController.Master().getIssuedTo())) {
                                        retrievePayables(false);
                                    }
                                });
                                delay.play();
                            });
                            loadRecordMaster();
                            return;
                    }
                    break;
                default:
                    break;
            }
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    boolean pbSuccess = true;

    private void datepicker_Action(ActionEvent event) {
        poJSON = new JSONObject();
        JFXUtil.setJSONSuccess(poJSON, "success");

        try {
            Object source = event.getSource();
            if (source instanceof DatePicker) {
                DatePicker datePicker = (DatePicker) source;
                String inputText = datePicker.getEditor().getText();
                SimpleDateFormat sdfFormat = new SimpleDateFormat(SQLUtil.FORMAT_SHORT_DATE);
                LocalDate currentDate = null;
                LocalDate selectedDate = null;
                LocalDate receivingDate = null;
                String lsServerDate = "";
                String lsTransDate = "";
                String lsSelectedDate = "";
                String lsReceivingDate = "";

                JFXUtil.JFXUtilDateResult ldtResult = JFXUtil.processDate(inputText, datePicker);
                poJSON = ldtResult.poJSON;
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    loadRecordMaster();
                    return;
                }
                if (JFXUtil.isObjectEqualTo(inputText, null, "", "1900-01-01")) {
                    return;
                }
                selectedDate = ldtResult.selectedDate;

                switch (datePicker.getId()) {
                    case "dpTransactionDate":
                        if (poSOATaggingController.getEditMode() == EditMode.ADDNEW
                                || poSOATaggingController.getEditMode() == EditMode.UPDATE) {
                            lsServerDate = sdfFormat.format(oApp.getServerDate());
                            lsTransDate = sdfFormat.format(poSOATaggingController.Master().getTransactionDate());
                            lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                            currentDate = LocalDate.parse(lsServerDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                            selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                            if (selectedDate.isAfter(currentDate)) {
                                JFXUtil.setJSONError(poJSON, "Future dates are not allowed.");
                                pbSuccess = false;
                            }

//                            if (poSOATaggingController.Master().getSourceNo() != null && !"".equals(poSOATaggingController.Master().getSourceNo())) {
//                                lsReceivingDate = sdfFormat.format(poSOATaggingController.Master().PurchaseOrderReceivingMaster().getTransactionDate());
//                                receivingDate = LocalDate.parse(lsReceivingDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
//                                if (selectedDate.isBefore(receivingDate)) {
//                                    JFXUtil.setJSONError(poJSON, "Transaction date cannot be before the receiving date.");
//                                    pbSuccess = false;
//                                }
//                            } else {
//                                if (pbSuccess && !lsServerDate.equals(lsSelectedDate) && pnEditMode == EditMode.ADDNEW) {
//                                    JFXUtil.setJSONError(poJSON, "Select PO Receiving before changing the transaction date.");
//                                    pbSuccess = false;
//                                }
//                            }
                            if (pbSuccess && ((poSOATaggingController.getEditMode() == EditMode.UPDATE && !lsTransDate.equals(lsSelectedDate))
                                    || !lsServerDate.equals(lsSelectedDate))) {
                                pbSuccess = false;
                                if (ShowMessageFX.YesNo(null, pxeModuleName, "Change in Transaction Date Detected\n\n"
                                        + "If YES, please seek approval to proceed with the new selected date.\n"
                                        + "If NO, the previous transaction date will be retained.") == true) {
                                    if (oApp.getUserLevel() <= UserRight.ENCODER) {
                                        poJSON = ShowDialogFX.getUserApproval(oApp);
                                        if (!"success".equals((String) poJSON.get("result"))) {
                                            pbSuccess = false;
                                        } else {
                                            
                                            if(Integer.parseInt(poJSON.get("nUserLevl").toString())<= UserRight.ENCODER){
                                                poJSON.put("result", "error");
                                                poJSON.put("message", "User is not an authorized approving officer.");
                                                pbSuccess = false;
                                            } else {
                                                poSOATaggingController.Master().setTransactionDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
                                            }
                                        }
                                    }
                                } else {
                                    pbSuccess = false;
                                }
                            }

                            if (pbSuccess) {

                            } else {
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                }
                            }
                            pbSuccess = false; //Set to false to prevent multiple message box: Conflict with server date vs transaction date validation
                            loadRecordMaster();
                            pbSuccess = true; //Set to original value

                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadTableMain() {
        // Setting data to table detail
        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewMainList.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
//                Thread.sleep(1000);

                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    main_data.clear();
                    JFXUtil.disableAllHighlight(tblViewMainList, highlightedRowsMain);
                    String lsPayeeName = "";
                    String lsTransNo = "";
                    String lsTransDate = "";
                    String lsTransNoBasis = "";
                    String lsTransType = "";
                    //retreiving using column index
                    for (int lnCtr = 0; lnCtr <= poSOATaggingController.getPayablesCount() - 1; lnCtr++) {
                        try {
                            switch (poSOATaggingController.PayableType(lnCtr)) {
                                case SOATaggingStatic.PaymentRequest:
                                    lsPayeeName = poSOATaggingController.PaymentRequestList(lnCtr).Payee().getPayeeName();
                                    lsTransNo = poSOATaggingController.PaymentRequestList(lnCtr).getSeriesNo();
                                    lsTransDate = String.valueOf(poSOATaggingController.PaymentRequestList(lnCtr).getTransactionDate());
                                    lsTransNoBasis = poSOATaggingController.PaymentRequestList(lnCtr).getTransactionNo();
                                    lsTransType = "PRF";
                                    break;
                                case SOATaggingStatic.CachePayable:
                                    lsPayeeName = poSOATaggingController.CachePayableList(lnCtr).Client().getCompanyName();
                                    lsTransNo = poSOATaggingController.CachePayableList(lnCtr).getReferNo();
                                    lsTransDate = String.valueOf(poSOATaggingController.CachePayableList(lnCtr).getTransactionDate());
                                    lsTransNoBasis = poSOATaggingController.CachePayableList(lnCtr).getTransactionNo();
                                    lsTransType = "Cache Payable";
                                    break;
                            }

                            main_data.add(new ModelSOATagging_Main(String.valueOf(lnCtr + 1),
                                    lsTransType,
                                    lsPayeeName,
                                    lsTransDate,
                                    lsTransNo,
                                    lsTransNoBasis
                            ));
                        } catch (SQLException | GuanzonException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        }
                    }

                    loadHighlightFromDetail();
                    if (pnMain < 0 || pnMain
                            >= main_data.size()) {
                        if (!main_data.isEmpty()) {
                            /* FOCUS ON FIRST ROW */
                            JFXUtil.selectAndFocusRow(tblViewMainList, 0);
                            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();

                        }
                    } else {
                        /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                        JFXUtil.selectAndFocusRow(tblViewMainList, pnMain);
                    }

                    JFXUtil.loadTab(pgPagination, main_data.size(), ROWS_PER_PAGE, tblViewMainList, filteredData);
                });

                return null;
            }

            @Override
            protected void succeeded() {
                loading.placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed
                if (main_data == null || main_data.isEmpty()) {
                    tblViewMainList.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblViewMainList.toFront();
                }
                loading.progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblViewMainList.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
    }

    public void loadRecordSearch() {
        try {
            if (poSOATaggingController.Master().Industry().getDescription() != null && !"".equals(poSOATaggingController.Master().Industry().getDescription())) {
                lblSource.setText(poSOATaggingController.Master().Industry().getDescription());
            } else {
                lblSource.setText("General");
            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordDetail() {
        try {
            if (pnDetail < 0 || pnDetail > poSOATaggingController.getDetailCount() - 1) {
                return;
            }

            String lsReferenceDate = "1900-01-01";
            String lsReferenceNo = "";
            switch (poSOATaggingController.Detail(pnDetail).getSourceCode()) {
                case SOATaggingStatic.PaymentRequest:
                    lsReferenceNo = poSOATaggingController.Detail(pnDetail).PaymentRequestMaster().getSeriesNo();
                    lsReferenceDate = CustomCommonUtil.formatDateToShortString(poSOATaggingController.Detail(pnDetail).PaymentRequestMaster().getTransactionDate());
                    break;
                case SOATaggingStatic.CachePayable:
                    lsReferenceNo = poSOATaggingController.Detail(pnDetail).CachePayableMaster().getReferNo();
                    lsReferenceDate = CustomCommonUtil.formatDateToShortString(poSOATaggingController.Detail(pnDetail).CachePayableMaster().getTransactionDate());
                    break;
            }
            boolean lbDisable = lsReferenceNo != null && "".equals(lsReferenceNo);
            JFXUtil.setDisabled(!lbDisable, tfReferenceNo);

            tfSourceNo.setText(poSOATaggingController.Detail(pnDetail).getSourceNo());
            tfSourceCode.setText(poSOATaggingController.Detail(pnDetail).getSourceCode());
            tfReferenceNo.setText(lsReferenceNo);
            dpReferenceDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsReferenceDate, "yyyy-MM-dd"));
            tfCreditAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Detail(pnDetail).getCreditAmount(), true));
            tfDebitAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Detail(pnDetail).getDebitAmount(), true));
            tfAppliedAmtDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Detail(pnDetail).getAppliedAmount(), true));
            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadRecordMaster() {
        try {
            boolean lbDisable = pnEditMode == EditMode.UPDATE;
            JFXUtil.setDisabled(lbDisable, tfCompany, tfClient, tfIssuedTo);

            Platform.runLater(() -> {
                String lsActive = pnEditMode == EditMode.UNKNOWN ? "-1" : poSOATaggingController.Master().getTransactionStatus();
                boolean lbPrintStat = pnEditMode == EditMode.READY && !SOATaggingStatus.VOID.equals(lsActive);

                Map<String, String> statusMap = new HashMap<>();
                statusMap.put(SOATaggingStatus.OPEN, "OPEN");
                statusMap.put(SOATaggingStatus.PAID, "PAID");
                statusMap.put(SOATaggingStatus.CONFIRMED, "CONFIRMED");
                statusMap.put(SOATaggingStatus.RETURNED, "RETURNED");
                statusMap.put(SOATaggingStatus.VOID, "VOIDED");
                statusMap.put(SOATaggingStatus.CANCELLED, "CANCELLED");

                String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN");
                lblStatus.setText(lsStat);
                JFXUtil.setButtonsVisibility(lbPrintStat);
            });

            poSOATaggingController.computeFields();

            tfTransactionNo.setText(poSOATaggingController.Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poSOATaggingController.Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
            tfSOANo.setText(poSOATaggingController.Master().getSOANumber());
            tfCompany.setText(poSOATaggingController.Master().Company().getCompanyName());
            tfClient.setText(poSOATaggingController.Master().Supplier().getCompanyName());
            tfIssuedTo.setText(poSOATaggingController.Master().Payee().getPayeeName());
            taRemarks.setText(poSOATaggingController.Master().getRemarks());

            tfTransactionTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getTransactionTotal(), true));
            tfVatAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getVatAmount(), true));
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getDiscountAmount(), true));
            tfFreight.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getFreightAmount(), false));
            tfNonVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getZeroRatedVat(), true)); //As per ma'am she
            tfZeroVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getZeroRatedVat(), true));
            tfVatExemptSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getVatExempt(), true));
            tfNetTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Master().getNetTotal(), true));
            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadTableDetailFromMain() {
        try {
            poJSON = new JSONObject();
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                ModelSOATagging_Main selected = (ModelSOATagging_Main) tblViewMainList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                    pnMain = pnRowMain;
                    switch (poSOATaggingController.PayableType(pnMain)) {
                        case SOATaggingStatic.PaymentRequest:
                            poJSON = poSOATaggingController.addPayablesToSOADetail(
                                    poSOATaggingController.PaymentRequestList(pnMain).getTransactionNo(),
                                    poSOATaggingController.PayableType(pnMain));
                            break;
                        case SOATaggingStatic.CachePayable:
                            poJSON = poSOATaggingController.addPayablesToSOADetail(
                                    poSOATaggingController.CachePayableList(pnMain).getTransactionNo(),
                                    poSOATaggingController.PayableType(pnMain));
                            break;
                    }

                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                }
                Platform.runLater(() -> {
                    loadTableDetail();
                });
            } else {
                ShowMessageFX.Warning(null, pxeModuleName, "Data can only be viewed when in ADD or UPDATE mode.");
            }

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadTableDetail() {
        pbEntered = false;
        // Setting data to table detail

        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewTransDetailList.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                Platform.runLater(() -> {
                    details_data.clear();
                    plOrderNoPartial.clear();

                    int lnCtr;
                    try {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poSOATaggingController.getDetailCount() - 1;
                            while (lnCtr >= 0) {
                                if (poSOATaggingController.Detail(lnCtr).getSourceNo() == null || "".equals(poSOATaggingController.Detail(lnCtr).getSourceNo())) {
                                    poSOATaggingController.Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poSOATaggingController.getDetailCount() - 1) >= 0) {
                                if (poSOATaggingController.Detail(poSOATaggingController.getDetailCount() - 1).getSourceNo() != null
                                        && !"".equals(poSOATaggingController.Detail(poSOATaggingController.getDetailCount() - 1).getSourceNo())) {
                                    poSOATaggingController.AddDetail();
                                }
                            }

                            if ((poSOATaggingController.getDetailCount() - 1) < 0) {
                                poSOATaggingController.AddDetail();
                            }
                        }

                        String lsReferenceNo = "";
                        for (lnCtr = 0; lnCtr < poSOATaggingController.getDetailCount(); lnCtr++) {
                            switch (poSOATaggingController.Detail(lnCtr).getSourceCode()) {
                                case SOATaggingStatic.PaymentRequest:
                                    lsReferenceNo = poSOATaggingController.Detail(lnCtr).PaymentRequestMaster().getSeriesNo();
                                    break;
                                case SOATaggingStatic.CachePayable:
                                    lsReferenceNo = poSOATaggingController.Detail(lnCtr).CachePayableMaster().getReferNo();
                                    break;
                            }

                            details_data.add(
                                    new ModelSOATagging_Detail(String.valueOf(lnCtr + 1),
                                            String.valueOf(poSOATaggingController.Detail(lnCtr).getSourceNo()),
                                            String.valueOf(poSOATaggingController.Detail(lnCtr).getSourceCode()),
                                            String.valueOf(lsReferenceNo),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Detail(lnCtr).getCreditAmount(), true)),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Detail(lnCtr).getDebitAmount(), true)),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Detail(lnCtr).getAppliedAmount(), true))
                                    ));
                            lsReferenceNo = "";
                        }
                        JFXUtil.showRetainedHighlight(false, tblViewMainList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                        loadHighlightFromDetail();

                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                JFXUtil.selectAndFocusRow(tblViewTransDetailList, 0);
                                pnDetail = tblViewTransDetailList.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            JFXUtil.selectAndFocusRow(tblViewTransDetailList, pnDetail);
                            loadRecordDetail();
                        }
                        loadRecordMaster();
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (SQLException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetailList.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblViewTransDetailList.toFront();
                }
                loading.progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetailList.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background

    }

    private void initComboBoxes() {
        // Set the items of the ComboBox to the list of genders
        cmbTransType.setItems(TransactionType);
        cmbTransType.getSelectionModel().select(0);
        cmbTransType.setOnAction(event -> {
            retrievePayables(false);
        });
    }
    
    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpReferenceDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate, dpReferenceDate);
    }

    public void initTextFields() {
        Platform.runLater(() -> {
            JFXUtil.setVerticalScroll(taRemarks);
        });
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfCompany, tfClient, tfIssuedTo, tfSOANo, tfDiscountAmount);
        JFXUtil.setFocusListener(txtDetail_Focus, tfSourceNo, tfSourceCode, tfReferenceNo, tfAppliedAmtDetail);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);
        JFXUtil.setCommaFormatter(tfVatAmount, tfDiscountAmount, tfZeroVatSales, tfNonVatSales, tfVatExemptSales, tfAppliedAmtDetail);
    }

    public void initTableOnClick() {
        tblViewTransDetailList.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewTransDetailList.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    if (poSOATaggingController.Detail(pnDetail).getSourceNo() != null && !poSOATaggingController.Detail(pnDetail).getSourceNo().equals("")) {
                        tfAppliedAmtDetail.requestFocus();
                    } else {
                        tfReferenceNo.requestFocus();
                    }
                }
            }
        });

        tblViewMainList.setOnMouseClicked(event -> {
            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableDetailFromMain();

                    initButton(pnEditMode);
                }
            }
        });

        JFXUtil.applyRowHighlighting(tblViewMainList, item -> ((ModelSOATagging_Main) item).getIndex05(), highlightedRowsMain);
        tblViewTransDetailList.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetailList, tblViewMainList); // need to use computed-size in min-width of the column to work
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);

        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(!lbShow, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSearch, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(lbShow2, btnUpdate, btnHistory);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);

        JFXUtil.setDisabled(!lbShow, taRemarks, apMaster, apDetail);

        switch (poSOATaggingController.Master().getTransactionStatus()) {
            case SOATaggingStatus.PAID:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
            case SOATaggingStatus.VOID:
            case SOATaggingStatus.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
        }
    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail);
        JFXUtil.setColumnLeft(tblSourceNoDetail, tblSourceCodeDetail, tblReferenceNoDetail);
        JFXUtil.setColumnRight(tblCreditAmtDetail, tblDebitAmtDetail, tblAppliedAmtDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetailList);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);
        SortedList<ModelSOATagging_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewTransDetailList.comparatorProperty());
        tblViewTransDetailList.setItems(sortedData);
        tblViewTransDetailList.autosize();
    }

    public void initMainGrid() {
        JFXUtil.setColumnCenter(tblRowNo,tblTransType,tblDate, tblReferenceNo);
        JFXUtil.setColumnLeft(tblSupplier);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewMainList);

        filteredData = new FilteredList<>(main_data, b -> true);
        tblViewMainList.setItems(filteredData);

    }

    private void tableKeyEvents(KeyEvent event) {
        if (details_data.size() > 0) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblViewTransDetailList":
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

    public void clearTextFields() {
        JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField, dpTransactionDate);
        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse);
    }

}
