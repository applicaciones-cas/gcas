/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPOQuotationRequestSupplier_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPOQuotationRequest_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPOQuotationRequest_Main;
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
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import ph.com.guanzongroup.cas.purchasing.t2.services.QuotationControllers;
import ph.com.guanzongroup.cas.purchasing.t2.status.POQuotationRequestStatus;

/**
 * FXML Controller class
 *
 * @author Team 2 : Arsiela & Aldrich
 */
public class POQuotationRequest_ApprovalController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnSupplier = 0;
    int pnDetail = 0;
    int pnMain = 0;
    private String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    static QuotationControllers poController;
    public int pnEditMode;
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSearchClientId = "";
    private String psTransactionNo = "";
    private boolean pbEntered = false;

    private ObservableList<ModelPOQuotationRequest_Main> main_data = FXCollections.observableArrayList();
    private ObservableList<ModelPOQuotationRequest_Detail> details_data = FXCollections.observableArrayList();
    private ObservableList<ModelPOQuotationRequestSupplier_Detail> supplier_data = FXCollections.observableArrayList();

    private FilteredList<ModelPOQuotationRequest_Main> filteredData;

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();

    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();

    JFXUtil.ReloadableTableTask loadTableDetail, loadTableMain, loadTableSupplier;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apTransactionInfo, apMaster, apDetail, apSupplier;
    @FXML
    private Label lblSource, lblStatus, lblStatusSupplier;
    @FXML
    private TextField tfSearchBranch, tfSearchDepartment, tfSearchCategory, tfSearchReferenceNo, tfTransactionNo, tfBranch, tfDepartment, tfDestination, tfReferenceNo, tfCategory, tfBrand, tfModel, tfBarcode, tfDescription, tfCost, tfQuantity, tfSupplier, tfAddress, tfContactNumber, tfTerm, tfCompany;
    @FXML
    private DatePicker dpSearchTransactionDate, dpTransactionDate, dpExpectedDate;
    @FXML
    private HBox hbButtons, hboxid;
    @FXML
    private Button btnUpdate, btnSearch, btnSave, btnCancel, btnApprove, btnDisapprove, btnPrint, btnHistory, btnRetrieve, btnClose;
    @FXML
    private TabPane tabPane;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView tblViewTransDetails, tblViewSupplier, tblViewMainList;
    @FXML
    private TableColumn tblRowNoDetail, tblBarcodeDetail, tblDescriptionDetail, tblCostDetail, tblQuantityDetail, tblTotalDetail, tblRowNoSupplier, tblSupplier, tblStatus, tblRowNo, tblBranch, tblDepartment, tblDate, tblReferenceNo;
    @FXML
    private CheckBox cbReverse;
    @FXML
    private Pagination pgPagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        poController = new QuotationControllers(oApp, null);
        poJSON = new JSONObject();
        poJSON = poController.POQuotationRequest().InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            System.err.println((String) poJSON.get("message"));
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
        initLoadTable();
        initTextFields();
        initDatePickers();
        initMainGrid();
        initDetailsGrid();
        initSupplierGrid();
        initTableOnClick();
        clearTextFields();
        Platform.runLater(() -> {
            poController.POQuotationRequest().Master().setIndustryId(psIndustryId);
            poController.POQuotationRequest().Master().setCategoryCode(psCategoryId);
            poController.POQuotationRequest().setIndustryId(psIndustryId);
            poController.POQuotationRequest().setCompanyId(psCompanyId);
            poController.POQuotationRequest().setCategoryId(psCategoryId);
//            poController.POQuotationRequest().initFields();
            poController.POQuotationRequest().setWithUI(true);
            loadRecordSearch();
            loadRecordMaster();
        });

        pgPagination.setPageCount(1);

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        JFXUtil.initKeyClickObject(apMainAnchor, lastFocusedTextField, previousSearchedTextField); // for btnSearch Reference
        initTabPane();
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
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof CheckBox) {
            CheckBox checkedBox = (CheckBox) source;
            switch (checkedBox.getId()) {
                case "cbReverse": // this is the id
                    poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).isReverse(checkedBox.isSelected());
                    loadTableSupplier.reload();
                    break;
            }
        }
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        try {
            poJSON = new JSONObject();
            String tabText = "";

            Object source = event.getSource();
            if (source instanceof Button) {
                Button clickedButton = (Button) source;
                String lsButton = clickedButton.getId();
                switch (lsButton) {
                    case "btnClose":
                        unloadForm appUnload = new unloadForm();
                        if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?") == true) {
                            appUnload.unloadForm(apMainAnchor, oApp, pxeModuleName);
                        } else {
                            return;
                        }
                        break;
                    case "btnUpdate":
                        poJSON = poController.POQuotationRequest().OpenTransaction(poController.POQuotationRequest().Master().getTransactionNo());
                        poJSON = poController.POQuotationRequest().UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poController.POQuotationRequest().getEditMode();
                        break;
                    case "btnSearch":
                        JFXUtil.initiateBtnSearch(pxeModuleName, lastFocusedTextField, previousSearchedTextField, apBrowse, apMaster, apDetail, apSupplier);
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        retrievePOQuotationRequest();
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poController.POQuotationRequest().SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poController.POQuotationRequest().AddDetail();
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Approval Prompt
                                JSONObject loJSON;
                                loJSON = poController.POQuotationRequest().OpenTransaction(psTransactionNo);
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poController.POQuotationRequest().Master().getTransactionStatus().equals(POQuotationRequestStatus.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to approve this transaction?")) {
                                            loJSON = poController.POQuotationRequest().ApproveTransaction("");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                            }
                        } else {
                            return;
                        }

                        break;
                    case "btnApprove":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to approve transaction?") == true) {
                            poJSON = poController.POQuotationRequest().ApproveTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnDisapprove":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to disapprove transaction?") == true) {
                            poJSON = poController.POQuotationRequest().CancelTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                        break;
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnSave", "btnApprove", "btnDisapprove", "btnReturn", "btnVoid", "btnCancel")) {
                    poController.POQuotationRequest().resetMaster();
                    poController.POQuotationRequest().Detail().clear();
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();

                    poController.POQuotationRequest().Master().setIndustryId(psIndustryId);
                    poController.POQuotationRequest().setCompanyId(psCompanyId);
                    poController.POQuotationRequest().Master().setCategoryCode(psCategoryId);
//                    poController.POQuotationRequest().initFields();
                }
                String currentTitle = tabPane.getSelectionModel().getSelectedItem().getText();
                switch (currentTitle) {
                    case "Quotation Request Supplier":
                        JFXUtil.clickTabByTitleText(tabPane, "Quotation Request Supplier");
                        break;
                }
                if (JFXUtil.isObjectEqualTo(lsButton, "btnSave", "btnCancel")) {
                    JFXUtil.clickTabByTitleText(tabPane, "Inquiry");
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnPrint", "btnAddAttachment", "btnRemoveAttachment",
                        "btnArrowRight", "btnArrowLeft", "btnRetrieve")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail.reload();
                }
                initButton(pnEditMode);
                if (lsButton.equals("btnUpdate")) {
                    if (poController.POQuotationRequest().Detail(pnDetail).getStockId() == null || "".equals(poController.POQuotationRequest().Detail(pnDetail).getStockId())) {
                        tfBarcode.requestFocus();
                    }
                }
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void retrievePOQuotationRequest() {
        poJSON = new JSONObject();
        poController.POQuotationRequest().setTransactionStatus(POQuotationRequestStatus.APPROVED + POQuotationRequestStatus.CONFIRMED);

        SimpleDateFormat sdfFormat = new SimpleDateFormat(SQLUtil.FORMAT_SHORT_DATE);
        String inputText = JFXUtil.isObjectEqualTo(dpSearchTransactionDate.getEditor().getText(), "") ? "01/01/1900" : dpSearchTransactionDate.getEditor().getText();
        String lsSelectedDate = sdfFormat.format(SQLUtil.toDate(JFXUtil.convertToIsoFormat(inputText), SQLUtil.FORMAT_SHORT_DATE));
        LocalDate selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

        poJSON = poController.POQuotationRequest().loadPOQuotationRequestList(oApp.getBranchName(), poController.POQuotationRequest().getSearchDepartment(),
                poController.POQuotationRequest().getSearchCategory(), java.sql.Date.valueOf(selectedDate),
                tfSearchReferenceNo.getText());
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        } else {
            loadTableMain.reload();
        }
    }

    ChangeListener<Boolean> txtArea_Focus = JFXUtil.FocusListener(TextArea.class,
            (lsID, lsValue) -> {
                /*Lost Focus*/
                lsValue = lsValue.trim();
                switch (lsID) {
                    case "taRemarks"://Remarks
                        poJSON = poController.POQuotationRequest().Master().setRemarks(lsValue);
                        if ("error".equals((String) poJSON.get("result"))) {
                            System.err.println((String) poJSON.get("message"));
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        break;
                }
                loadRecordMaster();
            });

    ChangeListener<Boolean> txtDetail_Focus = JFXUtil.FocusListener(TextField.class,
            (lsID, lsValue) -> {
                /*Lost Focus*/
//                switch (lsID) {
//                    case "tfBrand":
//                        //if value is blank then reset
//                        if (lsValue.equals("")) {
//                            poController.POQuotationRequest().Detail(pnDetail).setBrandId("");
//                        }
//                        break;
//                    case "tfBarcode":
//                        //if value is blank then reset
//                        if (lsValue.equals("")) {
//                            poJSON = poController.POQuotationRequest().Detail(pnDetail).setStockId("");
//                        }
//                        break;
//                    case "tfDescription":
//                        //if value is blank then reset
//                        if (lsValue.equals("")) {
//                            poJSON = poController.POQuotationRequest().Detail(pnDetail).setStockId("");
//                        }
//                        if (pbEntered) {
//                            moveNext(false, true);
//                            pbEntered = false;
//                        }
//                        break;
//                }
//                JFXUtil.runWithDelay(0.50, () -> {
//                    loadTableDetail.reload();
//                });
            });
    ChangeListener<Boolean> txtMaster_Focus = JFXUtil.FocusListener(TextField.class,
            (lsID, lsValue) -> {
                try {
                    /*Lost Focus*/
                    switch (lsID) {
                        case "tfCompany":
                            if (lsValue.isEmpty()) {
                                poJSON = poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).setCompanyId("");
                            }
                            loadRecordSupplier();
                            break;
                        case "tfSupplier":
                            if (lsValue.isEmpty()) {
                                poJSON = poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Supplier().setClientId("");
                            }
                            loadRecordSupplier();
                            break;
                        case "tfTerm":
                            if (lsValue.isEmpty()) {
                                poJSON = poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Term().setTermId("");
                            }
                            loadRecordSupplier();
                            break;
                    }

                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                }
            });

    ChangeListener<Boolean> txtField_Focus = JFXUtil.FocusListener(TextField.class,
            (lsID, lsValue) -> {
                if (lsValue == null) {
                    lsValue = "";
                }
                /*Lost Focus*/
                switch (lsID) {
                    case "tfSearchBranch":
                        poController.POQuotationRequest().setSearchBranch(lsValue);
                        break;
                    case "tfSearchDepartment":
                        poController.POQuotationRequest().setSearchDepartment(lsValue);
                        break;
                    case "tfSearchCategory":
                        poController.POQuotationRequest().setSearchCategory(lsValue);
                        break;
                }
                loadRecordSearch();
            });

    public void moveNextSupplier(boolean isUp, boolean continueNext) {
        try {
            if (continueNext) {
                apSupplier.requestFocus();
                pnSupplier = isUp ? Integer.parseInt(supplier_data.get(JFXUtil.moveToPreviousRow(tblViewSupplier)).getIndex07())
                        : Integer.parseInt(supplier_data.get(JFXUtil.moveToNextRow(tblViewSupplier)).getIndex07());
            }
            loadRecordSupplier();
            JFXUtil.requestFocusNullField(new Object[][]{ // alternative to if , else if
                {poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Company().getCompanyId(), tfCompany},
                {poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Supplier().getClientId(), tfSupplier}, // if null or empty, then requesting focus to the txtfield
                {poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Term().getTermId(), tfTerm}
            }, tfSupplier); // default
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void moveNext(boolean isUp, boolean continueNext) {
        try {
            if (continueNext) {
                apDetail.requestFocus();
                pnDetail = isUp ? Integer.parseInt(details_data.get(JFXUtil.moveToPreviousRow(tblViewTransDetails)).getIndex07())
                        : Integer.parseInt(details_data.get(JFXUtil.moveToNextRow(tblViewTransDetails)).getIndex07());
            }
            loadRecordDetail();
            JFXUtil.requestFocusNullField(new Object[][]{ // alternative to if , else if
                {poController.POQuotationRequest().Detail(pnDetail).Brand().getBrandId(), tfBrand}, // if null or empty, then requesting focus to the txtfield
                {poController.POQuotationRequest().Detail(pnDetail).Inventory().Model().getModelId(), tfModel},
                {poController.POQuotationRequest().Detail(pnDetail).Inventory().getBarCode(), tfBarcode}
            }, tfBrand); // default
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();

            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEntered = true;
                    CommonUtils.SetNextFocus(txtField);
                    event.consume();
                    break;
                case UP:
                    switch (lsID) {
                        case "tfBrand":
                        case "tfBarcode":
                        case "tfDescription":
                            moveNext(true, true);
                            event.consume();
                            break;
                        case "tfCompany":
                        case "tfSupplier":
                        case "tfTerm":
                            moveNextSupplier(true, true);
                            event.consume();
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfBrand":
                        case "tfBarcode":
                        case "tfDescription":
                            moveNext(false, true);
                            event.consume();
                            break;
                        case "tfCompany":
                        case "tfSupplier":
                        case "tfTerm":
                            moveNextSupplier(true, true);
                            event.consume();
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfSearchBranch":
                            poJSON = poController.POQuotationRequest().SearchBranch(lsValue, false, true);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                txtField.setText("");
                                psSearchClientId = "";
                                break;
                            }
                            loadRecordSearch();
                            retrievePOQuotationRequest();
                            return;
                        case "tfSearchDepartment":
                            poJSON = poController.POQuotationRequest().SearchDepartment(lsValue, false, true);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                txtField.setText("");
                                psSearchClientId = "";
                                break;
                            }
                            loadRecordSearch();
                            retrievePOQuotationRequest();
                            return;
                        case "tfSearchCategory":
                            poJSON = poController.POQuotationRequest().SearchCategory(lsValue, false, true);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                txtField.setText("");
                                psSearchClientId = "";
                                break;
                            }
                            loadRecordSearch();
                            retrievePOQuotationRequest();
                            return;
                        case "tfSearchReferenceNo":
                            retrievePOQuotationRequest();
                            return;

                        case "tfSupplier":
                            poJSON = poController.POQuotationRequest().SearchSupplier(lsValue, false, pnSupplier);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                txtField.setText("");
                                break;
                            } else {
                                loadTableSupplier.reload();
                                JFXUtil.runWithDelay(0.50, () -> {
                                    moveNextSupplier(false, true);
                                });
                            }
                            break;
                        case "tfTerm":
                            poJSON = poController.POQuotationRequest().SearchTerm(lsValue, false, pnSupplier);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                txtField.setText("");
                                break;
                            } else {
                                loadTableSupplier.reload();
                                JFXUtil.runWithDelay(0.50, () -> {
                                    moveNextSupplier(false, true);
                                });
                            }
                            break;
                        case "tfCompany":
                            poJSON = poController.POQuotationRequest().SearchCompany(lsValue, false, pnSupplier);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                txtField.setText("");
                                break;
                            } else {
                                loadTableSupplier.reload();
                                JFXUtil.runWithDelay(0.50, () -> {
                                    moveNextSupplier(false, true);
                                });
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
        } catch (GuanzonException | SQLException ex) {
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

                if (JFXUtil.isObjectEqualTo(inputText, null, "", "01/01/1900")) {
                    return;
                }

                switch (datePicker.getId()) {
                    case "dpExpectedDate":
                        String lsServerDate = sdfFormat.format(oApp.getServerDate());
                        String lsTransDate = sdfFormat.format(poController.POQuotationRequest().Master().getTransactionDate());
                        String lsSelectedDate = sdfFormat.format(SQLUtil.toDate(JFXUtil.convertToIsoFormat(inputText), SQLUtil.FORMAT_SHORT_DATE));
                        LocalDate currentDate = LocalDate.parse(lsTransDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                        LocalDate selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                        if (poController.POQuotationRequest().getEditMode() == EditMode.ADDNEW
                                || poController.POQuotationRequest().getEditMode() == EditMode.UPDATE) {
                            if (selectedDate.isBefore(currentDate)) {
                                JFXUtil.setJSONError(poJSON, "Target date cannot be before the transaction date.");
                                pbSuccess = false;
                            } else {
                                poController.POQuotationRequest().Master().setExpectedPurchaseDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
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
                    case "dpSearchTransactionDate":
                        loadRecordSearch();
                        retrievePOQuotationRequest();
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void initTabPane() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
//            try {
            if (newTab != null) {
                String tabTitle = newTab.getText();
                switch (tabTitle) {
                    case "Information":
                        break;
                    case "Quotation Request Supplier":
                        JFXUtil.clearTextFields(apSupplier);
                        loadTableSupplier.reload();
                        break;
                }
            }
//            } catch (SQLException | GuanzonException ex) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
//            }
        });
    }

    public void loadRecordSearch() {
        try {
            if (poController.POQuotationRequest().Master().Industry().getDescription() != null && !"".equals(poController.POQuotationRequest().Master().Industry().getDescription())) {
                lblSource.setText(poController.POQuotationRequest().Master().Industry().getDescription());
            } else {
                lblSource.setText("General");
            }
            tfSearchBranch.setText(poController.POQuotationRequest().getSearchBranch());
            tfSearchDepartment.setText(poController.POQuotationRequest().getSearchDepartment());
            tfSearchCategory.setText(poController.POQuotationRequest().getSearchCategory());

        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordSupplier() {
        try {
            boolean lbShow = poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).getEditMode() == EditMode.UPDATE;
            JFXUtil.setDisabled(lbShow, tfSupplier);
            if (pnSupplier < 0 || pnSupplier > poController.POQuotationRequest().getPOQuotationRequestSupplierCount() - 1) {
                return;
            }
            tfSupplier.setText(poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Supplier().getCompanyName());
            tfAddress.setText(poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).ClientAddress().getAddress());
            tfContactNumber.setText(poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).ClientMobile().getMobileNo());
            cbReverse.setSelected(poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).isReverse());
            tfTerm.setText(poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Term().getDescription());
            tfCompany.setText(poController.POQuotationRequest().POQuotationRequestSupplierList(pnSupplier).Company().getCompanyName());
            JFXUtil.updateCaretPositions(apSupplier);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordDetail() {
        try {

            if (pnDetail < 0 || pnDetail > poController.POQuotationRequest().getDetailCount() - 1) {
                return;
            }
            String lsBrand = "";
            if (!JFXUtil.isObjectEqualTo(poController.POQuotationRequest().Detail(pnDetail).Brand().getDescription(), null, "")) {
                lsBrand = poController.POQuotationRequest().Detail(pnDetail).Brand().getDescription();
            } else {
                lsBrand = poController.POQuotationRequest().Detail(pnDetail).Inventory().Brand().getDescription();
            }
            tfBrand.setText(lsBrand);
            tfModel.setText("");
            tfBarcode.setText(poController.POQuotationRequest().Detail(pnDetail).Inventory().getBarCode());
            tfDescription.setText(poController.POQuotationRequest().Detail(pnDetail).Inventory().getDescription());
            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poController.POQuotationRequest().Detail(pnDetail).Inventory().getSellingPrice(), true));
            tfQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poController.POQuotationRequest().Detail(pnDetail).getQuantity(), false));
            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordMaster() {
        try {

            JFXUtil.setStatusValue(lblStatus, POQuotationRequestStatus.class, pnEditMode == EditMode.UNKNOWN ? "-1" : poController.POQuotationRequest().Master().getTransactionStatus());

            // Transaction Date
            tfTransactionNo.setText(poController.POQuotationRequest().Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poController.POQuotationRequest().Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));

            String lsExpectedDate = CustomCommonUtil.formatDateToShortString(poController.POQuotationRequest().Master().getTransactionDate());
            dpExpectedDate.setValue(JFXUtil.isObjectEqualTo(lsExpectedDate, "1900-01-01") ? null : CustomCommonUtil.parseDateStringToLocalDate(lsExpectedDate, "yyyy-MM-dd"));

            tfReferenceNo.setText(poController.POQuotationRequest().Master().getReferenceNo());
            tfDepartment.setText(poController.POQuotationRequest().Master().Department().getDescription());
            tfDestination.setText(poController.POQuotationRequest().Master().Destination().getBranchName());
            tfCategory.setText(poController.POQuotationRequest().Master().Category2().getDescription());

            taRemarks.setText(poController.POQuotationRequest().Master().getRemarks());
            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadTableDetailFromMain() {
        try {
            poJSON = new JSONObject();

            ModelPOQuotationRequest_Main selected = (ModelPOQuotationRequest_Main) tblViewMainList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                psTransactionNo = poController.POQuotationRequest().POQuotationRequestList(pnMain).getTransactionNo();
                poJSON = poController.POQuotationRequest().OpenTransaction(psTransactionNo);
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
            }
            Platform.runLater(() -> {
                loadTableDetail.reload();
            });

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void initLoadTable() {
        loadTableSupplier = new JFXUtil.ReloadableTableTask(
                tblViewSupplier,
                supplier_data,
                () -> {
                    pbEntered = false;
                    Platform.runLater(() -> {
                        int lnCtr;
                        supplier_data.clear();
                        try {
                            poController.POQuotationRequest().loadPOQuotationRequestSupplierList();
                            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)
                                    && poController.POQuotationRequest().Master().getTransactionStatus() != POQuotationRequestStatus.APPROVED) {
                                poController.POQuotationRequest().ReloadSupplier();
                            }
                            int lnRowCount = 0;
                            for (lnCtr = 0; lnCtr < poController.POQuotationRequest().getPOQuotationRequestSupplierCount(); lnCtr++) {
                                if (poController.POQuotationRequest().POQuotationRequestSupplierList(lnCtr).isReverse()) {
                                    lnRowCount += 1;
                                    supplier_data.add(
                                            new ModelPOQuotationRequestSupplier_Detail(String.valueOf(lnRowCount),
                                                    String.valueOf(poController.POQuotationRequest().POQuotationRequestSupplierList(lnCtr).Supplier().getCompanyName()),
                                                    String.valueOf(""),
                                                    "",
                                                    "",
                                                    "",
                                                    String.valueOf(lnCtr)
                                            ));
                                }
                            }
                            if (pnSupplier < 0 || pnSupplier
                                    >= supplier_data.size()) {
                                if (!supplier_data.isEmpty()) {
                                    /* FOCUS ON FIRST ROW */
                                    JFXUtil.selectAndFocusRow(tblViewSupplier, 0);
                                    int lnRow = Integer.parseInt(details_data.get(0).getIndex07());
                                    pnSupplier = lnRow;
                                    loadRecordSupplier();
                                }
                            } else {
                                /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                                JFXUtil.selectAndFocusRow(tblViewSupplier, pnSupplier);
                                int lnRow = Integer.parseInt(details_data.get(tblViewTransDetails.getSelectionModel().getSelectedIndex()).getIndex07());
                                pnDetail = lnRow;
                                loadRecordSupplier();
                            }
                            loadRecordMaster();
                        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        }
                    });
                });

        loadTableDetail = new JFXUtil.ReloadableTableTask(
                tblViewTransDetails,
                details_data,
                () -> {
                    pbEntered = false;
                    Platform.runLater(() -> {
                        int lnCtr;
                        details_data.clear();
                        try {
                            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)
                                    && !poController.POQuotationRequest().Master().getTransactionStatus().equals(POQuotationRequestStatus.APPROVED)) {
                                poController.POQuotationRequest().ReloadDetail();
                            }
                            String lsBarcode = "";
                            String lsDescription = "";
                            int lnRowCount = 0;
                            for (lnCtr = 0; lnCtr < poController.POQuotationRequest().getDetailCount(); lnCtr++) {
                                if (poController.POQuotationRequest().Detail(lnCtr).isReverse()) {
                                    if (poController.POQuotationRequest().Detail(lnCtr).getStockId() != null) {
                                        lsBarcode = poController.POQuotationRequest().Detail(lnCtr).Inventory().getBarCode();
                                        lsDescription = poController.POQuotationRequest().Detail(lnCtr).getDescription();
                                    }
                                    lnRowCount += 1;
                                    double lnTotal = poController.POQuotationRequest().Detail(lnCtr).getQuantity() * poController.POQuotationRequest().Detail(lnCtr).Inventory().getCost().doubleValue();
                                    details_data.add(
                                            new ModelPOQuotationRequest_Detail(
                                                    String.valueOf(lnRowCount),
                                                    String.valueOf(lsBarcode),
                                                    lsDescription,
                                                    String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poController.POQuotationRequest().Detail(lnCtr).Inventory().getCost(), true)),
                                                    String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poController.POQuotationRequest().Detail(lnCtr).getQuantity(), false)),
                                                    CustomCommonUtil.setIntegerValueToDecimalFormat(String.valueOf(lnTotal), true), String.valueOf(lnCtr)
                                            ));
                                    lsBarcode = "";
                                    lsDescription = "";
                                }
                            }

                            if (pnDetail < 0 || pnDetail
                                    >= details_data.size()) {
                                if (!details_data.isEmpty()) {
                                    /* FOCUS ON FIRST ROW */
                                    JFXUtil.selectAndFocusRow(tblViewTransDetails, 0);
                                    int lnRow = Integer.parseInt(details_data.get(0).getIndex07());
                                    pnDetail = lnRow;
                                    loadRecordDetail();
                                }
                            } else {
                                /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                                JFXUtil.selectAndFocusRow(tblViewTransDetails, pnDetail);
                                int lnRow = Integer.parseInt(details_data.get(tblViewTransDetails.getSelectionModel().getSelectedIndex()).getIndex07());
                                pnDetail = lnRow;
                                loadRecordDetail();
                            }
                            loadRecordMaster();
                        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        }
                    });
                });

        loadTableMain = new JFXUtil.ReloadableTableTask(
                tblViewMainList,
                main_data,
                () -> {
                    Platform.runLater(() -> {
                        main_data.clear();
                        JFXUtil.disableAllHighlight(tblViewMainList, highlightedRowsMain);
                        if (poController.POQuotationRequest().getPOQuotationRequestCount() > 0) {
                            //pending
                            //retreiving using column index
                            for (int lnCtr = 0; lnCtr <= poController.POQuotationRequest().getPOQuotationRequestCount() - 1; lnCtr++) {
                                try {
                                    main_data.add(new ModelPOQuotationRequest_Main(String.valueOf(lnCtr + 1),
                                            String.valueOf(poController.POQuotationRequest().POQuotationRequestList(lnCtr).Branch().getBranchName()),
                                            String.valueOf(poController.POQuotationRequest().POQuotationRequestList(lnCtr).Department().getDescription()),
                                            String.valueOf(poController.POQuotationRequest().POQuotationRequestList(lnCtr).getTransactionDate()),
                                            String.valueOf(poController.POQuotationRequest().POQuotationRequestList(lnCtr).getTransactionNo())
                                    ));
                                } catch (GuanzonException | SQLException ex) {
                                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                                }

                                if (poController.POQuotationRequest().POQuotationRequestList(lnCtr).getTransactionStatus().equals(POQuotationRequestStatus.APPROVED)) {
                                    JFXUtil.highlightByKey(tblViewMainList, String.valueOf(lnCtr + 1), "#C1E1C1", highlightedRowsMain);
                                }
                            }
                        }

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
                });

    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat("MM/dd/yyyy", dpTransactionDate, dpExpectedDate, dpSearchTransactionDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate, dpExpectedDate, dpSearchTransactionDate);

    }

    public void initTextFields() {
        JFXUtil.setFocusListener(txtField_Focus, tfSearchBranch, tfSearchDepartment, tfSearchCategory);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfTransactionNo, tfBranch, tfDepartment, tfDestination, tfReferenceNo, tfCategory,
                tfSupplier, tfTerm, tfCompany);
        JFXUtil.setFocusListener(txtDetail_Focus, tfBrand, tfModel, tfBarcode, tfDescription, tfCost, tfQuantity);
        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail, apSupplier);

        CustomCommonUtil.inputIntegersOnly(tfQuantity);
    }

    public void initTableOnClick() {
        tblViewSupplier.setOnMouseClicked(event -> {
            if (supplier_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    int lnRow = Integer.parseInt(supplier_data.get(tblViewSupplier.getSelectionModel().getSelectedIndex()).getIndex07());
                    pnSupplier = lnRow;
                    moveNextSupplier(false, false);
                }
            }
        });
        tblViewTransDetails.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    int lnRow = Integer.parseInt(details_data.get(tblViewTransDetails.getSelectionModel().getSelectedIndex()).getIndex07());
                    pnDetail = lnRow;
                    moveNext(false, false);
                }
            }
        });

        tblViewMainList.setOnMouseClicked(event -> {
            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableDetailFromMain();
                    pnEditMode = poController.POQuotationRequest().getEditMode();
                    initButton(pnEditMode);
                }
            }
        });

        tblViewTransDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetails, tblViewMainList, tblViewSupplier); // need to use computed-size in min-width of the column to work
        JFXUtil.applyRowHighlighting(tblViewMainList, item -> ((ModelPOQuotationRequest_Main) item).getIndex01(), highlightedRowsMain);
    }

    private void initButton(int fnValue) {

        boolean lbShow1 = (fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        // Manage visibility and managed state of other buttons
        //Update 
        JFXUtil.setButtonsVisibility(lbShow1, btnSearch, btnSave, btnCancel);

        //Ready
        JFXUtil.setButtonsVisibility(lbShow3, btnUpdate, btnHistory, btnApprove, btnDisapprove);

        //Unkown || Ready
        JFXUtil.setDisabled(!lbShow1, apMaster, apDetail, apSupplier);
        JFXUtil.setButtonsVisibility(lbShow4, btnClose);

        switch (poController.POQuotationRequest().Master().getTransactionStatus()) {
            case POQuotationRequestStatus.APPROVED:
                JFXUtil.setButtonsVisibility(false, btnApprove, btnDisapprove);
                if (fnValue == EditMode.UPDATE) {
                    JFXUtil.setDisabled(true, apMaster, apDetail);
                    JFXUtil.setDisabled(false, taRemarks, apSupplier);
                }
                break;
            case POQuotationRequestStatus.CONFIRMED:
                JFXUtil.setButtonsVisibility(false, btnPrint);
                break;
            case POQuotationRequestStatus.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnApprove, btnDisapprove, btnUpdate);
                break;
        }
    }

    public void initSupplierGrid() {
        JFXUtil.setColumnCenter(tblRowNoSupplier);
        JFXUtil.setColumnLeft(tblSupplier, tblStatus);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewSupplier);

        tblViewSupplier.setItems(supplier_data);
    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail, tblRowNoSupplier);
        JFXUtil.setColumnLeft(tblBarcodeDetail, tblDescriptionDetail, tblSupplier, tblStatus);
        JFXUtil.setColumnRight(tblCostDetail, tblQuantityDetail, tblTotalDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetails);

        tblViewTransDetails.setItems(details_data);
        tblViewTransDetails.autosize();
    }

    public void initMainGrid() {
        JFXUtil.setColumnCenter(tblRowNo, tblDate, tblReferenceNo);
        JFXUtil.setColumnLeft(tblBranch, tblDepartment);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewMainList);

        filteredData = new FilteredList<>(main_data, b -> true);
        tblViewMainList.setItems(filteredData);

    }

    private void tableKeyEvents(KeyEvent event) {
        TableView<?> currentTable = (TableView<?>) event.getSource();
        TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
        if (focusedCell == null) {
            return;
        }
        boolean moveDown = event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.DOWN;
        boolean moveUp = event.getCode() == KeyCode.UP;
        int newIndex = 0;

        if (moveDown || moveUp) {
            switch (currentTable.getId()) {
                case "tblViewTransDetailList":
                    if (details_data.isEmpty()) {
                        return;
                    }
                    newIndex = moveDown ? Integer.parseInt(details_data.get(JFXUtil.moveToNextRow(currentTable)).getIndex07())
                            : Integer.parseInt(details_data.get(JFXUtil.moveToPreviousRow(currentTable)).getIndex07());
                    pnDetail = newIndex;
                    loadRecordDetail();
                    break;
                case "tblViewSupplier":
                    if (supplier_data.isEmpty()) {
                        return;
                    }
                    newIndex = moveDown ? Integer.parseInt(supplier_data.get(JFXUtil.moveToNextRow(currentTable)).getIndex07())
                            : Integer.parseInt(supplier_data.get(JFXUtil.moveToPreviousRow(currentTable)).getIndex07());
                    pnSupplier = newIndex;
                    loadRecordSupplier();
                    break;

            }
            event.consume();
        }
    }

    public void clearTextFields() {
        psSearchClientId = "";
        JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField);
        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse, apSupplier);
    }

}
