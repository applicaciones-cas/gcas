/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSalesInquiry_Detail;
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
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;
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
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.control.ComboBox;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.constant.UserRight;
import ph.com.guanzongroup.cas.sales.t1.SalesInquiry;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;

/**
 * FXML Controller class
 *
 * @author User
 */
public class SalesInquiry_EntryMCController implements Initializable {

    private GRiderCAS oApp;
    static SalesInquiry poSalesInquiryController;
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
    private ObservableList<ModelSalesInquiry_Detail> details_data = FXCollections.observableArrayList();

    private FilteredList<ModelSalesInquiry_Detail> filteredDataDetail;
    List<Pair<String, String>> plOrderNoPartial = new ArrayList<>();
    List<Pair<String, String>> plOrderNoFinal = new ArrayList<>();

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();
    ObservableList<String> TransactionType = FXCollections.observableArrayList(
            "ALL",
            "Cache Payable",
            "PRF"
    );
    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apTransactionInfo, apMaster, apDetail;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private HBox hbButtons, hboxid;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnHistory, btnRetrieve, btnClose;
    @FXML
    private TextField tfTransactionNo, tfBranch, tfSalesPerson, tfInquirySource, tfClient, tfAddress, tfInquiryStatus, tfContactNo, tfBrand, tfModel, tfColor, tfBarcode, tfModelVariant;
    @FXML
    private DatePicker dpTransactionDate, dpTargetDate;
    @FXML
    private ComboBox cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView tblViewTransDetailList;
    @FXML
    private TableColumn tblRowNoDetail, tblBrandDetail, tblDescriptionDetail;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        psIndustryId = isGeneral ? "" : psIndustryId;

        poSalesInquiryController = new SalesControllers(oApp, null).SalesInquiry();
        poJSON = new JSONObject();
        poJSON = poSalesInquiryController.InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            System.err.println((String) poJSON.get("message"));
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
        initComboBoxes();
        initTextFields();
        initDatePickers();
        initDetailsGrid();
        initTableOnClick();
        clearTextFields();

        Platform.runLater(() -> {
            poSalesInquiryController.Master().setIndustryId(psIndustryId);
//            poSalesInquiryController.Master().setCompanyID(psCompanyId);
            poSalesInquiryController.setIndustryId(psIndustryId);
            poSalesInquiryController.setCompanyId(psCompanyId);
            poSalesInquiryController.setCategoryId(psCategoryId);
            poSalesInquiryController.initFields();
            btnNew.fire();
        });

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);

        JFXUtil.initKeyClickObject(apMainAnchor, lastFocusedTextField, previousSearchedTextField);
    }

    @FXML
    private void cmdComboBox_Click(ActionEvent event) {
        try {
            Object source = event.getSource();
            if (source instanceof ComboBox) {
                ComboBox<?> comboBox = (ComboBox<?>) source;
                String comboId = comboBox.getId();
                Object selectedValue = comboBox.getValue();

                if (selectedValue == null) {
                    ShowMessageFX.Warning(null, pxeModuleName, "No value selected in " + comboId);
                    return;
                }

                switch (comboId) {
                    case "cmbClientType":
                        //selectedValue
                        break;
                    case "cmbInquiryType":
                        break;
                    case "cmbPurchaseType":
                        break;
                    case "cmbCategoryType":
                        break;
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "ComboBox with id " + comboId + " is not registered.");
                        break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
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
                        poSalesInquiryController.setTransactionStatus(SalesInquiryStatic.OPEN);
                        poJSON = poSalesInquiryController.searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        JFXUtil.showRetainedHighlight(false, tblViewTransDetailList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                        pnEditMode = poSalesInquiryController.getEditMode();
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
                        poJSON = poSalesInquiryController.OpenTransaction(poSalesInquiryController.Master().getTransactionNo());
                        poJSON = poSalesInquiryController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poSalesInquiryController.getEditMode();
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
                            JFXUtil.showRetainedHighlight(false, tblViewTransDetailList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                            pnEditMode = EditMode.UNKNOWN;
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
//                        retrievePayables(false);
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poSalesInquiryController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poSalesInquiryController.AddDetail();
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON = poSalesInquiryController.OpenTransaction(poSalesInquiryController.Master().getTransactionNo());
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poSalesInquiryController.Master().getTransactionStatus().equals(SalesInquiryStatic.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poSalesInquiryController.ConfirmTransaction("Confirmed");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                                JFXUtil.disableAllHighlightByColor(tblViewTransDetailList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.showRetainedHighlight(true, tblViewTransDetailList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                                btnNew.fire();
                            }
                        } else {
                            return;
                        }
                        return;
                    case "btnNew":
                        //Clear data
                        poSalesInquiryController.resetMaster();
                        clearTextFields();
                        poJSON = poSalesInquiryController.NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        poSalesInquiryController.initFields();
                        pnEditMode = poSalesInquiryController.getEditMode();
                        JFXUtil.showRetainedHighlight(false, tblViewTransDetailList, "#A7C7E7", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain, true);
                        break;

                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                        break;
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnSave", "btnConfirm", "btnReturn", "btnVoid", "btnCancel")) {
                    poSalesInquiryController.resetMaster();
                    poSalesInquiryController.Detail().clear();
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();

                    poSalesInquiryController.Master().setIndustryId(psIndustryId);
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnAddAttachment", "btnRemoveAttachment",
                        "btnArrowRight", "btnArrowLeft", "btnRetrieve", "btnClose")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                initButton(pnEditMode);

//                if (lsButton.equals("btnUpdate")) {
//                    if (poSalesInquiryController.Detail(pnDetail).getSourceNo() != null && !poSalesInquiryController.Detail(pnDetail).getSourceNo().equals("")) {
//                        tfAppliedAmtDetail.requestFocus();
//                    } else {
//                        tfSourceNo.requestFocus();
//                    }
//                }
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
                    poJSON = poSalesInquiryController.Master().setRemarks(lsValue);
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
                    if (poSalesInquiryController.Detail(pnDetail).getAppliedAmount() != null
                            && !"".equals(poSalesInquiryController.Detail(pnDetail).getAppliedAmount())) {
                        if (poSalesInquiryController.Detail(pnDetail).getTransactionTotal().doubleValue() < Double.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Applied Amount cannot be greater than the transaction total");
                            poSalesInquiryController.Detail(pnDetail).setAppliedAmount(0.0000);
                            tfAppliedAmtDetail.requestFocus();
                            break;
                        }
                    }

                    poJSON = poSalesInquiryController.Detail(pnDetail).setAppliedAmount((Double.valueOf(lsValue)));
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
                        poJSON = poSalesInquiryController.Master().setSOANumber(lsValue);
                    } else {
                        poJSON = poSalesInquiryController.Master().setSOANumber("");
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
                            if (poSalesInquiryController.Master().getCompanyId() != null && !"".equals(poSalesInquiryController.Master().getCompanyId())) {
                                if (poSalesInquiryController.getDetailCount() > 1) {
                                    if (!pbKeyPressed) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the company name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                            poSalesInquiryController.removeDetails();
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
                        poJSON = poSalesInquiryController.Master().setCompanyId("");
                    }
                    break;
                case "tfClient":
                    if (lsValue.isEmpty()) {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            if (poSalesInquiryController.Master().getClientId() != null && !"".equals(poSalesInquiryController.Master().getClientId())) {
                                if (poSalesInquiryController.getDetailCount() > 1) {
                                    if (!pbKeyPressed) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the supplier name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                            poSalesInquiryController.removeDetails();
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
                        poJSON = poSalesInquiryController.Master().setClientId("");
                    }
                    break;
                case "tfIssuedTo":
                    if (lsValue.isEmpty()) {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            if (poSalesInquiryController.Master().getIssuedTo() != null && !"".equals(poSalesInquiryController.Master().getIssuedTo())) {
                                if (poSalesInquiryController.getDetailCount() > 1) {
                                    if (!pbKeyPressed) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the payee name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                            poSalesInquiryController.removeDetails();
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
                        poJSON = poSalesInquiryController.Master().setIssuedTo("");
                    }
                    break;
                case "tfDiscountAmount":
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (Double.valueOf(lsValue) > 0.00) {
                        if (poSalesInquiryController.Master().getTransactionTotal().doubleValue() < Double.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Discount amount cannot be greater than the transaction total.");
                            poSalesInquiryController.Master().setDiscountAmount(0.0000);
                            tfDiscountAmount.setText("0.0000");
                            tfDiscountAmount.requestFocus();
                            break;
                        }
                    }

                    poJSON = poSalesInquiryController.Master().setDiscountAmount((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfFreight":
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (Double.valueOf(lsValue) > 0.00) {
                        if (poSalesInquiryController.Master().getTransactionTotal().doubleValue() < Double.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Freight amount cannot be greater than the transaction total.");
                            poSalesInquiryController.Master().setFreightAmount(0.0000);
                            tfFreight.setText("0.0000");
                            tfFreight.requestFocus();
                            break;
                        }
                    }

                    poJSON = poSalesInquiryController.Master().setDiscountAmount((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
            }

            System.out.println("Company : " + poSalesInquiryController.Master().getCompanyId());
            System.out.println("Supplier : " + poSalesInquiryController.Master().getClientId());
            System.out.println("Payee : " + poSalesInquiryController.Master().getIssuedTo());
            loadRecordMaster();
        }
    };

    public void moveNext() {
        double ldblAppliedAmt = poSalesInquiryController.Detail(pnDetail).getAppliedAmount().doubleValue();
        apDetail.requestFocus();
        double ldblNewValue = poSalesInquiryController.Detail(pnDetail).getAppliedAmount().doubleValue();
        if (ldblAppliedAmt != ldblNewValue && (ldblAppliedAmt > 0
                && poSalesInquiryController.Detail(pnDetail).getSourceNo() != null
                && !"".equals(poSalesInquiryController.Detail(pnDetail).getSourceNo()))) {
            tfAppliedAmtDetail.requestFocus();
        } else {
            pnDetail = JFXUtil.moveToNextRow(tblViewTransDetailList);
            loadRecordDetail();
            if (poSalesInquiryController.Detail(pnDetail).getSourceNo() != null && !poSalesInquiryController.Detail(pnDetail).getSourceNo().equals("")) {
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
                            double ldblAppliedAmt = poSalesInquiryController.Detail(pnDetail).getAppliedAmount().doubleValue();
                            apDetail.requestFocus();
                            double ldblNewValue = poSalesInquiryController.Detail(pnDetail).getAppliedAmount().doubleValue();
                            if (ldblAppliedAmt != ldblNewValue && (ldblAppliedAmt > 0
                                    && poSalesInquiryController.Detail(pnDetail).getSourceNo() != null
                                    && !"".equals(poSalesInquiryController.Detail(pnDetail).getSourceNo()))) {
                                tfAppliedAmtDetail.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
                                loadRecordDetail();
                                if (poSalesInquiryController.Detail(pnDetail).getSourceNo() != null && !poSalesInquiryController.Detail(pnDetail).getSourceNo().equals("")) {
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
//                            if (poSalesInquiryController.Master().getCompanyId() == null
//                                    || "".equals(poSalesInquiryController.Master().getCompanyId())) {
//                                ShowMessageFX.Warning(null, pxeModuleName, "Company Name is not set.");
//                                return;
//                            }
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poSalesInquiryController.getDetailCount() > 1) {
                                    pbKeyPressed = true;
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the supplier name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                        poSalesInquiryController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                    pbKeyPressed = false;
                                }
                            }

                            poJSON = poSalesInquiryController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfClient.setText("");
                                break;
                            }
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    if (!"".equals(poSalesInquiryController.Master().getClientId())) {
                                        retrievePayables(false);
                                    }
                                });
                                delay.play();
                            });
                            loadRecordMaster();
                            return;
                        case "tfCompany":
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poSalesInquiryController.getDetailCount() > 1) {
                                    pbKeyPressed = true;
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the company name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                        poSalesInquiryController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                    pbKeyPressed = false;
                                }
                            }

                            poJSON = poSalesInquiryController.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfClient.setText("");
//                                psSupplierId = "";
                                break;
                            }
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    if (!"".equals(poSalesInquiryController.Master().getCompanyId())) {
                                        retrievePayables(false);
                                    }
                                });
                                delay.play();
                            });
                            loadRecordMaster();
                            return;
                        case "tfIssuedTo":
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poSalesInquiryController.getDetailCount() > 1) {
                                    pbKeyPressed = true;
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the payee name?\nPlease note that doing so will delete all SOA details.\n\nDo you wish to proceed?") == true) {
                                        poSalesInquiryController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        loadRecordMaster();
                                        return;
                                    }
                                    pbKeyPressed = false;
                                }
                            }

                            poJSON = poSalesInquiryController.SearchPayee(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfIssuedTo.setText("");
                                break;
                            }
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    if (!"".equals(poSalesInquiryController.Master().getIssuedTo())) {
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
                        if (poSalesInquiryController.getEditMode() == EditMode.ADDNEW
                                || poSalesInquiryController.getEditMode() == EditMode.UPDATE) {
                            lsServerDate = sdfFormat.format(oApp.getServerDate());
                            lsTransDate = sdfFormat.format(poSalesInquiryController.Master().getTransactionDate());
                            lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                            currentDate = LocalDate.parse(lsServerDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                            selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                            if (selectedDate.isAfter(currentDate)) {
                                JFXUtil.setJSONError(poJSON, "Future dates are not allowed.");
                                pbSuccess = false;
                            }

//                            if (poSalesInquiryController.Master().getSourceNo() != null && !"".equals(poSalesInquiryController.Master().getSourceNo())) {
//                                lsReceivingDate = sdfFormat.format(poSalesInquiryController.Master().PurchaseOrderReceivingMaster().getTransactionDate());
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
                            if (pbSuccess && ((poSalesInquiryController.getEditMode() == EditMode.UPDATE && !lsTransDate.equals(lsSelectedDate))
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

                                            if (Integer.parseInt(poJSON.get("nUserLevl").toString()) <= UserRight.ENCODER) {
                                                poJSON.put("result", "error");
                                                poJSON.put("message", "User is not an authorized approving officer.");
                                                pbSuccess = false;
                                            } else {
                                                poSalesInquiryController.Master().setTransactionDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
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
                    case "dpTargetDate":
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadRecordDetail() {
        tfBrand.setText("");
        tfModel.setText("");
        tfColor.setText("");
        tfBarcode.setText("");
        tfModelVariant.setText("");
    }

    private void loadRecordMaster() {
        boolean lbDisable = pnEditMode == EditMode.UPDATE;
        JFXUtil.setDisabled(lbDisable, tfCompany, tfClient, tfIssuedTo);

        Platform.runLater(() -> {
            String lsActive = pnEditMode == EditMode.UNKNOWN ? "-1" : poSalesInquiryController.Master().getTransactionStatus();
            boolean lbPrintStat = pnEditMode == EditMode.READY && !SalesInquiryStatic.VOID.equals(lsActive);

            Map<String, String> statusMap = new HashMap<>();
            statusMap.put(SalesInquiryStatic.OPEN, "OPEN");
            statusMap.put(SalesInquiryStatic.PAID, "PAID");
            statusMap.put(SalesInquiryStatic.CONFIRMED, "CONFIRMED");
            statusMap.put(SalesInquiryStatic.VOID, "VOIDED");
            statusMap.put(SalesInquiryStatic.CANCELLED, "CANCELLED");

            String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN");
            lblStatus.setText(lsStat);
            JFXUtil.setButtonsVisibility(lbPrintStat);
        });

        tfTransactionNo.setText("");
        dpTransactionDate.setValue(null);
        tfBranch.setText("");
        tfSalesPerson.setText("");
        tfInquirySource.setText("");
        cmbClientType.getSelectionModel().clearSelection();
        tfClient.setText("");
        tfAddress.setText("");
        taRemarks.setText("");
        tfInquiryStatus.setText("");
        cmbInquiryType.getSelectionModel().clearSelection();
        cmbPurchaseType.getSelectionModel().clearSelection();
        cmbCategoryType.getSelectionModel().clearSelection();
        dpTargetDate.setValue(null);
        tfContactNo.setText("");
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
                            lnCtr = poSalesInquiryController.getDetailCount() - 1;
                            while (lnCtr >= 0) {
                                if (poSalesInquiryController.Detail(lnCtr).getSourceNo() == null || "".equals(poSalesInquiryController.Detail(lnCtr).getSourceNo())) {
                                    poSalesInquiryController.Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poSalesInquiryController.getDetailCount() - 1) >= 0) {
                                if (poSalesInquiryController.Detail(poSalesInquiryController.getDetailCount() - 1).getSourceNo() != null
                                        && !"".equals(poSalesInquiryController.Detail(poSalesInquiryController.getDetailCount() - 1).getSourceNo())) {
                                    poSalesInquiryController.AddDetail();
                                }
                            }

                            if ((poSalesInquiryController.getDetailCount() - 1) < 0) {
                                poSalesInquiryController.AddDetail();
                            }
                        }

                        String lsReferenceNo = "";
                        for (lnCtr = 0; lnCtr < poSalesInquiryController.getDetailCount(); lnCtr++) {
                            switch (poSalesInquiryController.Detail(lnCtr).getSourceCode()) {
                                case SOATaggingStatic.PaymentRequest:
                                    lsReferenceNo = poSalesInquiryController.Detail(lnCtr).PaymentRequestMaster().getSeriesNo();
                                    break;
                                case SOATaggingStatic.CachePayable:
                                    lsReferenceNo = poSalesInquiryController.Detail(lnCtr).CachePayableMaster().getReferNo();
                                    break;
                            }

                            details_data.add(
                                    new ModelSalesInquiry_Detail(String.valueOf(lnCtr + 1),
                                            String.valueOf(poSalesInquiryController.Detail(lnCtr).getSourceNo()),
                                            String.valueOf(poSalesInquiryController.Detail(lnCtr).getSourceCode()),
                                            String.valueOf(lsReferenceNo),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poSalesInquiryController.Detail(lnCtr).getCreditAmount(), true)),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poSalesInquiryController.Detail(lnCtr).getDebitAmount(), true)),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poSalesInquiryController.Detail(lnCtr).getAppliedAmount(), true))
                                    ));
                            lsReferenceNo = "";
                        }

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

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpTargetDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate, dpTargetDate);
    }

    private void initComboBoxes() {
        // Set the items of the ComboBox to the list of genders

        cmbClientType.setItems(TransactionType);
        cmbClientType.getSelectionModel().select(0);

        cmbInquiryType.setItems(TransactionType);
        cmbInquiryType.getSelectionModel().select(0);

        cmbPurchaseType.setItems(TransactionType);
        cmbPurchaseType.getSelectionModel().select(0);

        cmbCategoryType.setItems(TransactionType);
        cmbCategoryType.getSelectionModel().select(0);

        JFXUtil.initComboBoxCellDesignColor("#FF8201", cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType);
    }

    public void initTextFields() {
        Platform.runLater(() -> {
            JFXUtil.setVerticalScroll(taRemarks);
        });
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfSalesPerson, tfInquirySource, tfClient, tfAddress, tfContactNo);
        JFXUtil.setFocusListener(txtDetail_Focus, tfBrand, tfModel, tfColor);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);
        //use util to validate the contact number
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

        switch (poSalesInquiryController.Master().getTransactionStatus()) {
            case SalesInquiryStatic.PAID:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
            case SalesInquiryStatic.VOID:
            case SalesInquiryStatic.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
        }
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

    public void initTableOnClick() {
        tblViewTransDetailList.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewTransDetailList.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    if (poSalesInquiryController.Detail(pnDetail).getSourceNo() != null && !poSalesInquiryController.Detail(pnDetail).getSourceNo().equals("")) {
                        tfAppliedAmtDetail.requestFocus();
                    } else {
                        tfReferenceNo.requestFocus();
                    }
                }
            }
        });

        tblViewTransDetailList.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetailList); // need to use computed-size in min-width of the column to work
    }

    public void clearTextFields() {
        JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField, dpTransactionDate);
        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse);
    }

    private void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail);
        JFXUtil.setColumnLeft(tblBrandDetail, tblDescriptionDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetailList);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);
        SortedList<ModelSalesInquiry_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewTransDetailList.comparatorProperty());
        tblViewTransDetailList.setItems(sortedData);
        tblViewTransDetailList.autosize();
    }

}
