/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSalesInquiry_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSalesInquiry_Main;
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
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 * FXML Controller class
 *
 * @author Team 2 : Arsiela & Aldrich
 */
public class SalesInquiry_ConfirmationAppliancesController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    static SalesControllers poSalesInquiryController;
    public int pnEditMode;
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSearchClientId = "";
    private String psTransactionNo = "";
    private boolean pbEntered = false;

    private ObservableList<ModelSalesInquiry_Main> main_data = FXCollections.observableArrayList();
    private ObservableList<ModelSalesInquiry_Detail> details_data = FXCollections.observableArrayList();

    private FilteredList<ModelSalesInquiry_Main> filteredData;
    private FilteredList<ModelSalesInquiry_Detail> filteredDataDetail;

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();

    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();
    ObservableList<String> ClientType = ModelSalesInquiry_Detail.ClientType;
    ObservableList<String> InquiryType = ModelSalesInquiry_Detail.InquiryType;
    ObservableList<String> PurchaseType = ModelSalesInquiry_Detail.PurchaseType;
    ObservableList<String> CategoryType = ModelSalesInquiry_Detail.CategoryType;
    private final JFXUtil.RowDragLock dragLock = new JFXUtil.RowDragLock(true);
    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail;
    @FXML
    private TextField tfSearchClient, tfSearchReferenceNo, tfTransactionNo, tfBranch, tfSalesPerson, tfInquirySource, tfClient, tfAddress, tfInquiryStatus, tfContactNo, tfBrand, tfModel, tfColor, tfBarcode, tfDescription;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnUpdate, btnSearch, btnSave, btnCancel, btnConfirm, btnVoid, btnHistory, btnRetrieve, btnClose;
    @FXML
    private DatePicker dpTransactionDate, dpTargetDate;
    @FXML
    private ComboBox cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView tblViewTransDetails, tblViewMainList;
    @FXML
    private TableColumn tblRowNoDetail, tblBarcodeDetail, tblDescriptionDetail, tblRowNo, tblClient, tblDate, tblReferenceNo;
    @FXML
    private Pagination pgPagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        poSalesInquiryController = new SalesControllers(oApp, null);
        poJSON = new JSONObject();
        poJSON = poSalesInquiryController.SalesInquiry().InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            System.err.println((String) poJSON.get("message"));
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }

        initTextFields();
        initDatePickers();
        initMainGrid();
        initDetailsGrid();
        initTableOnClick();
        clearTextFields();
        initComboBoxes();
        Platform.runLater(() -> {
            poSalesInquiryController.SalesInquiry().Master().setIndustryId(psIndustryId);
            poSalesInquiryController.SalesInquiry().Master().setCompanyId(psCompanyId);
            poSalesInquiryController.SalesInquiry().Master().setCategoryCode(psCategoryId);
            poSalesInquiryController.SalesInquiry().setIndustryId(psIndustryId);
            poSalesInquiryController.SalesInquiry().setCompanyId(psCompanyId);
            poSalesInquiryController.SalesInquiry().setCategoryId(psCategoryId);
            poSalesInquiryController.SalesInquiry().initFields();
            poSalesInquiryController.SalesInquiry().setWithUI(true);
            loadRecordSearch();
            loadRecordMaster();
        });

        pgPagination.setPageCount(1);

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        JFXUtil.initKeyClickObject(apMainAnchor, lastFocusedTextField, previousSearchedTextField); // for btnSearch Reference
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
                        poJSON = poSalesInquiryController.SalesInquiry().OpenTransaction(poSalesInquiryController.SalesInquiry().Master().getTransactionNo());
                        poJSON = poSalesInquiryController.SalesInquiry().UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();
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
                            JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        retrieveSalesInquiry();
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poSalesInquiryController.SalesInquiry().SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poSalesInquiryController.SalesInquiry().AddDetail();
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON;

                                loJSON = poSalesInquiryController.SalesInquiry().OpenTransaction(psTransactionNo);

                                if ("success".equals(loJSON.get("result"))) {
                                    if (poSalesInquiryController.SalesInquiry().Master().getTransactionStatus().equals(SalesInquiryStatic.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poSalesInquiryController.SalesInquiry().ConfirmTransaction("");
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

                    case "btnConfirm":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to confirm transaction?") == true) {
                            poJSON = poSalesInquiryController.SalesInquiry().ConfirmTransaction("");
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
                    case "btnVoid":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to void transaction?") == true) {
                            if (SalesInquiryStatic.CONFIRMED.equals(poSalesInquiryController.SalesInquiry().Master().getTransactionStatus())) {
                                poJSON = poSalesInquiryController.SalesInquiry().CancelTransaction("");
                            } else {
                                poJSON = poSalesInquiryController.SalesInquiry().VoidTransaction("");
                            }
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#FAA0A0", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                        break;
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnSave", "btnConfirm", "btnReturn", "btnVoid", "btnCancel")) {
                    poSalesInquiryController.SalesInquiry().resetMaster();
                    poSalesInquiryController.SalesInquiry().Detail().clear();
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();

                    poSalesInquiryController.SalesInquiry().Master().setIndustryId(psIndustryId);
                    poSalesInquiryController.SalesInquiry().Master().setCompanyId(psCompanyId);
                    poSalesInquiryController.SalesInquiry().Master().setCategoryCode(psCategoryId);
                    poSalesInquiryController.SalesInquiry().initFields();
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnPrint", "btnAddAttachment", "btnRemoveAttachment",
                        "btnArrowRight", "btnArrowLeft", "btnRetrieve")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                initButton(pnEditMode);
                if (lsButton.equals("btnUpdate")) {
                    if (poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId() == null || "".equals(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId())) {
                        tfBarcode.requestFocus();
                    }
                }
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void retrieveSalesInquiry() {
        poJSON = new JSONObject();
        poJSON = poSalesInquiryController.SalesInquiry().loadSalesInquiry(psIndustryId, tfSearchClient.getText(), tfSearchReferenceNo.getText());
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        } else {
            loadTableMain();
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
                    poJSON = poSalesInquiryController.SalesInquiry().Master().setRemarks(lsValue);
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                    break;
            }
            loadRecordMaster();
        } else {
            txtField.selectAll();
        }
    };

    // Method to handle focus change and track the last focused TextField
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
                case "tfBarcode":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poSalesInquiryController.SalesInquiry().Detail(pnDetail).setBrandId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setStockId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setModelId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setColorId("");
                    }
                    break;
                case "tfDescription":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poSalesInquiryController.SalesInquiry().Detail(pnDetail).setBrandId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setStockId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setModelId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setColorId("");
                    }
                    if (pbEntered) {
                        moveNext(false);
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
                case "tfSalesPerson":
                    if (lsValue.isEmpty()) {
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setSalesMan("");
                    }
                    break;
                case "tfInquirySource":
                    if (lsValue.isEmpty()) {
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setSourceNo("");
                    }
                    break;
                case "tfClient":
                    if (lsValue.isEmpty()) {
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setClientId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setAddressId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setContactId("");
                    }
                    break;

            }

            loadRecordMaster();

        }

    };
    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
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
                case "tfSearchClient":
                    if (lsValue.equals("")) {
                        psSearchClientId = "";
                    }
                    loadRecordSearch();
                    break;
                case "tfSearchReferenceNo":
                    break;
            }
            if (lsTxtFieldID.equals("tfSearchClient")
                    || lsTxtFieldID.equals("tfSearchReferenceNo")) {
                loadRecordSearch();
            }
        }
    };

    public void moveNext(boolean isUp) {
        String lsBrand = poSalesInquiryController.SalesInquiry().Detail(pnDetail).getBrandId();
        apDetail.requestFocus();
        String ldblNewValue = poSalesInquiryController.SalesInquiry().Detail(pnDetail).getBrandId();
        pnDetail = isUp ? JFXUtil.moveToPreviousRow(tblViewTransDetails) : JFXUtil.moveToNextRow(tblViewTransDetails);
        loadRecordDetail();
        if (JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId(), null, "")) {
            tfBarcode.requestFocus();
        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            int lnRow = pnDetail;

            TableView<?> currentTable = tblViewTransDetails;
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
                        case "tfBarcode":
                        case "tfDescription":
                            moveNext(true);
                            event.consume();
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfBarcode":
                        case "tfDescription":
                            moveNext(false);
                            event.consume();
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfSearchClient":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchClient(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSearchClient.setText("");
                                psSearchClientId = "";
                                break;
                            }
                            psSearchClientId = poSalesInquiryController.SalesInquiry().Master().getClientId();
                            loadRecordSearch();
                            retrieveSalesInquiry();
                            return;
                        case "tfSearchReferenceNo":
                            retrieveSalesInquiry();
                            return;
                        case "tfClient":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchClient(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfClient.setText("");
                                break;
                            }
                            loadRecordMaster();
                            return;
                        case "tfSalesPerson":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchSalesPerson(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSalesPerson.setText("");
                                break;
                            }
                            loadRecordMaster();
                            return;
                        case "tfInquirySource":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchReferralAgent(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfInquirySource.setText("");
                                break;
                            }
                            loadRecordMaster();
                            return;
                        case "tfBarcode":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchInventory(lsValue, true, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfBarcode.setText("");
                                break;
                            } else {
                                loadTableDetail();
                                Platform.runLater(() -> {
                                    PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                    delay.setOnFinished(event1 -> {
                                        moveNext(false);
                                    });
                                    delay.play();
                                });
                            }
                            break;
                        case "tfDescription":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchInventory(lsValue, false, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfDescription.setText("");
                                break;
                            } else {
                                loadTableDetail();
                                Platform.runLater(() -> {
                                    PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                    delay.setOnFinished(event1 -> {
                                        moveNext(false);
                                    });
                                    delay.play();
                                });
                            }

                            break;
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
                LocalDate currentDate = null, selectedDate = null, receivingDate = null;
                String lsServerDate = "", lsTransDate = "", lsSelectedDate = "", lsReceivingDate = "";

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
                    case "dpTargetDate":
                        if (poSalesInquiryController.SalesInquiry().getEditMode() == EditMode.ADDNEW
                                || poSalesInquiryController.SalesInquiry().getEditMode() == EditMode.UPDATE) {
                            lsServerDate = sdfFormat.format(oApp.getServerDate());
                            lsTransDate = sdfFormat.format(poSalesInquiryController.SalesInquiry().Master().getTransactionDate());
                            lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                            currentDate = LocalDate.parse(lsTransDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                            selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                            if (selectedDate.isBefore(currentDate)) {
                                JFXUtil.setJSONError(poJSON, "Target date cannot be before the transaction date.");
                                pbSuccess = false;
                            } else {
                                poSalesInquiryController.SalesInquiry().Master().setTargetDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
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

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
//                Thread.sleep(1000);

                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    main_data.clear();
                    JFXUtil.disableAllHighlight(tblViewMainList, highlightedRowsMain);
                    if (poSalesInquiryController.SalesInquiry().getSalesInquiryCount() > 0) {
                        //pending
                        //retreiving using column index
                        for (int lnCtr = 0; lnCtr <= poSalesInquiryController.SalesInquiry().getSalesInquiryCount() - 1; lnCtr++) {
                            try {
                                main_data.add(new ModelSalesInquiry_Main(String.valueOf(lnCtr + 1),
                                        String.valueOf(poSalesInquiryController.SalesInquiry().SalesInquiryList(lnCtr).Client().getCompanyName()),
                                        String.valueOf(poSalesInquiryController.SalesInquiry().SalesInquiryList(lnCtr).getTransactionDate()),
                                        String.valueOf(poSalesInquiryController.SalesInquiry().SalesInquiryList(lnCtr).getTransactionNo())
                                ));
                            } catch (GuanzonException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            }

                            if (poSalesInquiryController.SalesInquiry().SalesInquiryList(lnCtr).getTransactionStatus().equals(SalesInquiryStatic.CONFIRMED)) {
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

                return null;
            }

            @Override
            protected void succeeded() {
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
            lblSource.setText(poSalesInquiryController.SalesInquiry().Master().Company().getCompanyName() + " - " + poSalesInquiryController.SalesInquiry().Master().Industry().getDescription());
            tfSearchClient.setText(psSearchClientId.equals("") ? "" : poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());

            tfSearchReferenceNo.setText("");
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordDetail() {
        try {
            if (pnDetail < 0 || pnDetail > poSalesInquiryController.SalesInquiry().getDetailCount() - 1) {
                return;
            }

            tfBarcode.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Inventory().getBarCode());
            tfDescription.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Inventory().getDescription());

            tfBrand.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Brand().getDescription());
            tfModel.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Model().getDescription());
            tfColor.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Color().getDescription());
            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadRecordMaster() {
        boolean lbDisable = pnEditMode == EditMode.ADDNEW;
        JFXUtil.setDisabled(!lbDisable, tfClient, tfSalesPerson, cmbClientType, cmbCategoryType);
        try {
            Platform.runLater(() -> {
                String lsActive = pnEditMode == EditMode.UNKNOWN ? "-1" : poSalesInquiryController.SalesInquiry().Master().getTransactionStatus();
                Map<String, String> statusMap = new HashMap<>();
                statusMap.put(SalesInquiryStatic.POSTED, "POSTED");
                statusMap.put(SalesInquiryStatic.PAID, "PAID");
                statusMap.put(SalesInquiryStatic.CONFIRMED, "CONFIRMED");
                statusMap.put(SalesInquiryStatic.OPEN, "OPEN");
                statusMap.put(SalesInquiryStatic.VOID, "VOIDED");
                statusMap.put(SalesInquiryStatic.CANCELLED, "CANCELLED");

                String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN"); //default
                lblStatus.setText(lsStat);

                switch (poSalesInquiryController.SalesInquiry().Master().getInquiryStatus()) {
                    case "0":
                        tfInquiryStatus.setText("OPEN");
                        break;
                    default:
                        tfInquiryStatus.setText("");
                        break;
                }
            });

            // Transaction Date
            tfTransactionNo.setText(poSalesInquiryController.SalesInquiry().Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poSalesInquiryController.SalesInquiry().Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
            String lsTargetDate = CustomCommonUtil.formatDateToShortString(poSalesInquiryController.SalesInquiry().Master().getTargetDate());
            dpTargetDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTargetDate, "yyyy-MM-dd"));

            tfBranch.setText(poSalesInquiryController.SalesInquiry().Master().Branch().getBranchName());
            tfInquirySource.setText("");

            tfClient.setText(poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());
            tfAddress.setText(poSalesInquiryController.SalesInquiry().Master().ClientAddress().getAddress());
            tfContactNo.setText(poSalesInquiryController.SalesInquiry().Master().ClientMobile().getMobileNo());

            tfSalesPerson.setText(poSalesInquiryController.SalesInquiry().Master().SalesPerson().getCompanyName());
            tfInquirySource.setText("");
            taRemarks.setText(poSalesInquiryController.SalesInquiry().Master().getRemarks());
            if (pnEditMode != EditMode.UNKNOWN) {
                cmbInquiryType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getSourceCode()));
                cmbPurchaseType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getPurchaseType()));
                if (poSalesInquiryController.SalesInquiry().Master().getClientId() != null && !"".equals(poSalesInquiryController.SalesInquiry().Master().getClientId())) {
                    cmbClientType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().Client().getClientType()));
                } else {
                    cmbClientType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getClientType()));
                }
                cmbCategoryType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getCategoryType()));
            } else {
                cmbInquiryType.getSelectionModel().select(0);
                cmbPurchaseType.getSelectionModel().select(0);
                cmbClientType.getSelectionModel().select(0);
                cmbCategoryType.getSelectionModel().select(0);
            }

            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadTableDetailFromMain() {
        try {
            poJSON = new JSONObject();

            ModelSalesInquiry_Main selected = (ModelSalesInquiry_Main) tblViewMainList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                psTransactionNo = poSalesInquiryController.SalesInquiry().SalesInquiryList(pnMain).getTransactionNo();
                poJSON = poSalesInquiryController.SalesInquiry().OpenTransaction(psTransactionNo);
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
            }
            Platform.runLater(() -> {
                loadTableDetail();
            });

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadTableDetail() {
        pbEntered = false;

        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewTransDetails.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    int lnCtr;
                    details_data.clear();
                    try {

                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poSalesInquiryController.SalesInquiry().getDetailCount() - 1;
                            while (lnCtr >= 0) {
                                if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId() == null || "".equals(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId())) {
                                    poSalesInquiryController.SalesInquiry().Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poSalesInquiryController.SalesInquiry().getDetailCount() - 1) >= 0) {
                                if (poSalesInquiryController.SalesInquiry().Detail(poSalesInquiryController.SalesInquiry().getDetailCount() - 1).getStockId() != null
                                        && !"".equals(poSalesInquiryController.SalesInquiry().Detail(poSalesInquiryController.SalesInquiry().getDetailCount() - 1).getStockId())) {
                                    poSalesInquiryController.SalesInquiry().AddDetail();
                                }
                            }

                            if ((poSalesInquiryController.SalesInquiry().getDetailCount() - 1) < 0) {
                                poSalesInquiryController.SalesInquiry().AddDetail();
                            }
                        }
                        poSalesInquiryController.SalesInquiry().sortPriority();
                        double lnTotal = 0.0;
                        for (lnCtr = 0; lnCtr < poSalesInquiryController.SalesInquiry().getDetailCount(); lnCtr++) {
                            String lsBarcode = "";
                            String lsDescription = "";
                            if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId() != null) {
                                lsBarcode = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Inventory().getBarCode();
                                lsDescription = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Inventory().getDescription();
                            }
                            details_data.add(
                                    new ModelSalesInquiry_Detail(
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getPriority()),
                                            String.valueOf(lsBarcode),
                                            lsDescription,
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId()),
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).Model().getModelId()),
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).Color().getColorId()), ""
                                    ));
                        }

                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                JFXUtil.selectAndFocusRow(tblViewTransDetails, 0);
                                pnDetail = tblViewTransDetails.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            JFXUtil.selectAndFocusRow(tblViewTransDetails, pnDetail);
                            loadRecordDetail();
                        }
                        loadRecordMaster();
                    } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    }
                });
                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetails.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblViewTransDetails.toFront();
                }
                loading.progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetails.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }
        };
        new Thread(task).start(); // Run task in background
    }
    final EventHandler<ActionEvent> comboBoxActionListener = event -> {
        Object source = event.getSource();
        @SuppressWarnings("unchecked")
        ComboBox<?> cb = (ComboBox<?>) source;
        String cbId = cb.getId();
        int selectedIndex = cb.getSelectionModel().getSelectedIndex();
        switch (cbId) {
            case "cmbClientType":
                //if client type is changed then remove the client 
                if (!poSalesInquiryController.SalesInquiry().Master().getClientType().equals(selectedIndex)) {
                    poSalesInquiryController.SalesInquiry().Master().setClientId("");
                    poSalesInquiryController.SalesInquiry().Master().setAddressId("");
                    poSalesInquiryController.SalesInquiry().Master().setContactId("");
                }
                poSalesInquiryController.SalesInquiry().Master().setClientType(String.valueOf(selectedIndex));
                break;
            case "cmbInquiryType":
                poSalesInquiryController.SalesInquiry().Master().setSourceCode(String.valueOf(selectedIndex));
                break;
            case "cmbPurchaseType":
                poSalesInquiryController.SalesInquiry().Master().setPurchaseType(String.valueOf(selectedIndex));
                break;
            case "cmbCategoryType":
                poSalesInquiryController.SalesInquiry().Master().setCategoryType(String.valueOf(selectedIndex));
                break;
            default:
                System.out.println("⚠ Unrecognized ComboBox ID: " + cbId);
                break;
        }
        loadRecordMaster();
    };

    private void initComboBoxes() {
        // Set the items of the ComboBox to the list of genders
        JFXUtil.setComboBoxItems(new JFXUtil.Pairs<>(ClientType, cmbClientType), new JFXUtil.Pairs<>(InquiryType, cmbInquiryType),
                new JFXUtil.Pairs<>(PurchaseType, cmbPurchaseType), new JFXUtil.Pairs<>(CategoryType, cmbCategoryType)
        );
        JFXUtil.setComboBoxActionListener(comboBoxActionListener, cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType);
        JFXUtil.initComboBoxCellDesignColor("#FF8201", cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType);

    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpTargetDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate, dpTargetDate);

    }

    public void initTextFields() {
        JFXUtil.setFocusListener(txtField_Focus, tfSearchClient, tfSearchReferenceNo);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfClient, tfSalesPerson, tfInquirySource, tfInquirySource);
        JFXUtil.setFocusListener(txtDetail_Focus, tfBarcode, tfDescription);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);
    }

    public void initTableOnClick() {

        tblViewTransDetails.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewTransDetails.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    if (JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId(), null, "")) {
                        tfBarcode.requestFocus();
                    }
                }
            }
        });

        tblViewMainList.setOnMouseClicked(event -> {
            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableDetailFromMain();
                    pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();
                    initButton(pnEditMode);
                }
            }
        });

        tblViewTransDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetails, tblViewMainList); // need to use computed-size in min-width of the column to work
        JFXUtil.applyRowHighlighting(tblViewMainList, item -> ((ModelSalesInquiry_Main) item).getIndex01(), highlightedRowsMain);

        JFXUtil.enableRowDragAndDrop(tblViewTransDetails, item -> ((ModelSalesInquiry_Detail) item).index01Property(),
                item -> ((ModelSalesInquiry_Detail) item).index03Property(),
                item -> ((ModelSalesInquiry_Detail) item).index04Property(), dragLock, index -> {

                    for (ModelSalesInquiry_Detail d : details_data) {
                        String brand = d.getIndex04();
                        String model = d.getIndex05();
                        String color = d.getIndex06();
                        String priorityStr = d.getIndex01();
                        for (int i = 0, n = poSalesInquiryController.SalesInquiry().getDetailCount(); i < n; i++) {
                            if (!brand.equals(poSalesInquiryController.SalesInquiry().Detail(i).getStockId())) {
                                continue;
                            }
                            try {
                                /*System.out.println(d.getIndex02() +" - "+priorityStr);*/
                                poSalesInquiryController.SalesInquiry().Detail(i).setPriority(Integer.parseInt(priorityStr));
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid priority: " + priorityStr);
                            }
                            break;
                        }
                    }
                    pnDetail = index;
                    loadTableDetail();
                });
    }

    private void initButton(int fnValue) {
        boolean lbShow1 = (fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        dragLock.isEnabled = lbShow1; // for drag drop 
        // Manage visibility and managed state of other buttons
        //Update 
        JFXUtil.setButtonsVisibility(lbShow1, btnSearch, btnSave, btnCancel);

        //Ready
        JFXUtil.setButtonsVisibility(lbShow3, btnUpdate, btnHistory, btnConfirm, btnVoid);

        //Unkown || Ready
        JFXUtil.setDisabled(!lbShow1, apMaster, apDetail);
        JFXUtil.setButtonsVisibility(lbShow4, btnClose);

        switch (poSalesInquiryController.SalesInquiry().Master().getTransactionStatus()) {
            case SalesInquiryStatic.CONFIRMED:
                JFXUtil.setButtonsVisibility(false, btnConfirm);
                if (poSalesInquiryController.SalesInquiry().Master().isProcessed()) {
                    JFXUtil.setButtonsVisibility(false, btnUpdate, btnVoid);
                }
                break;
            case SalesInquiryStatic.POSTED:
            case SalesInquiryStatic.PAID:
            case SalesInquiryStatic.VOID:
            case SalesInquiryStatic.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnConfirm, btnUpdate, btnVoid);
                break;
        }
    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail);
        JFXUtil.setColumnLeft(tblBarcodeDetail, tblDescriptionDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetails);

        tblViewTransDetails.autosize();
    }

    public void initMainGrid() {
        JFXUtil.setColumnCenter(tblRowNo, tblDate, tblReferenceNo);
        JFXUtil.setColumnLeft(tblClient);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewMainList);

        filteredData = new FilteredList<>(main_data, b -> true);
        tblViewMainList.setItems(filteredData);

    }

    private void tableKeyEvents(KeyEvent event) {
        if (details_data.size() > 0) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblViewTransDetails":
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
                        if (JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId(), null, "")) {
                            tfBarcode.requestFocus();
                        }
                        event.consume();
                    }
                    break;
            }
        }
    }

    public void clearTextFields() {
        psSearchClientId = "";
        JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField);
        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse);
    }

}
