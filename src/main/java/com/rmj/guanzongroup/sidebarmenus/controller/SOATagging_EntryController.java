/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrderReturn_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrderReturn_Main;
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
import org.guanzon.cas.gl.status.SOATaggingStatus;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.status.PurchaseOrderReturnStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import javafx.animation.PauseTransition;
import javafx.util.Pair;
import java.util.ArrayList;
import org.guanzon.cas.gl.SOATagging;
import org.guanzon.cas.gl.services.SOATaggingControllers;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.constant.UserRight;

/**
 * FXML Controller class
 *
 * @author User
 */
public class SOATagging_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    static SOATagging poSOATaggingController;
    public int pnEditMode;
    boolean isPrinted = false;
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psTransactionNo = "";
    private boolean pbEntered = false;

    private ObservableList<ModelPurchaseOrderReturn_Main> main_data = FXCollections.observableArrayList();
    private ObservableList<ModelPurchaseOrderReturn_Detail> details_data = FXCollections.observableArrayList();

    private FilteredList<ModelPurchaseOrderReturn_Main> filteredData;
    private FilteredList<ModelPurchaseOrderReturn_Detail> filteredDataDetail;
    List<Pair<String, String>> plOrderNoPartial = new ArrayList<>();
    List<Pair<String, String>> plOrderNoFinal = new ArrayList<>();

    private int pnAttachment;

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    private Object lastFocusedTextField = null;
    private Object previousSearchedTextField = null;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail, apMainList;

    @FXML
    private HBox hbButtons, hboxid;

    @FXML
    private Label lblSource, lblStatus;

    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnPrint, btnHistory, btnRetrieve, btnClose;

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
    private TableColumn tblRowNoDetail, tblSourceNoDetail, tblSourceCodeDetail, tblReferenceNoDetail, tblCreditAmtDetail, tblDebitAmtDetail, tblAppliedAmtDetail, tblRowNo, tblSupplier, tblDate, tblReferenceNo;

    @FXML
    private Pagination pgPagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        poSOATaggingController = new SOATaggingControllers(oApp, null).SOATagging();
        poJSON = new JSONObject();
        poJSON = poSOATaggingController.InitTransaction(); // Initialize transaction
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

        Platform.runLater(() -> {
            poSOATaggingController.Master().setIndustryId(psIndustryId);
//            poSOATaggingController.Master().setCompanyID(psCompanyId);
            poSOATaggingController.setIndustryId(psIndustryId);
            poSOATaggingController.setCompanyId(psCompanyId);
            poSOATaggingController.setCategoryId(psCategoryId);
            poSOATaggingController.initFields();
//            loadRecordSearch();
        });

        pgPagination.setPageCount(1);

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
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
                    case "btnPrint":
//                        poJSON = poSOATaggingController.printRecord(() -> {
//                            if (isPrinted) {
//                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
//                                poSOATaggingController.resetMaster();
//                                poSOATaggingController.Detail().clear();
//                                pnEditMode = EditMode.UNKNOWN;
//                                clearTextFields();
//                                initButton(pnEditMode);
//                            }
//                            Platform.runLater(() -> {
//                                try {
//                                    if (!isPrinted) {
//                                        poSOATaggingController.OpenTransaction(poSOATaggingController.PurchaseOrderReturnList(pnMain).getTransactionNo());
//                                    }
//                                    loadRecordMaster();
//                                    loadTableDetail();
//                                } catch (CloneNotSupportedException ex) {
//                                    Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, null, ex);
//                                } catch (SQLException ex) {
//                                    Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, null, ex);
//                                } catch (GuanzonException ex) {
//                                    Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//                                isPrinted = false;
//                            });
//                        });
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        }
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
                        poJSON = poSOATaggingController.OpenTransaction(psTransactionNo);
                        poJSON = poSOATaggingController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }

                        pnEditMode = poSOATaggingController.getEditMode();
                        break;
                    case "btnSearch":
                        String lsMessage = "Focus a searchable textfield to search";
                        if ((lastFocusedTextField != null)) {
                            if (lastFocusedTextField instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField;
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apBrowse, apMaster, apDetail).contains(tf.getId())) {

                                    if (lastFocusedTextField == previousSearchedTextField) {
                                        break;
                                    }
                                    previousSearchedTextField = lastFocusedTextField;
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
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        retrievePayables();
                        JFXUtil.disableAllHighlight(tblViewMainList, highlightedRowsMain);
                        JFXUtil.showRetainedHighlight(true, tblViewMainList, "#C1E1C1", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain);
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
                                JSONObject loJSON = poSOATaggingController.OpenTransaction(psTransactionNo);
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poSOATaggingController.Master().getTransactionStatus().equals(SOATaggingStatus.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poSOATaggingController.ConfirmTransaction("Confirmed");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                                // Print Transaction Prompt
                                loJSON = poSOATaggingController.OpenTransaction(psTransactionNo);
                                loadRecordMaster();
                                isPrinted = false;
                                if ("success".equals(loJSON.get("result"))) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to print this transaction?")) {
                                        isPrinted = true;
                                        btnPrint.fire();
                                    }
                                }
                                if (!isPrinted) {
                                    JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                }
                            }
                        } else {
                            return;
                        }

                        break;

                    case "btnConfirm":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to confirm transaction?") == true) {
                            poJSON = poSOATaggingController.ConfirmTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                plOrderNoPartial.add(new Pair<>(String.valueOf(pnMain + 1), "1"));
                                JFXUtil.showRetainedHighlight(true, tblViewMainList, "#C1E1C1", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnVoid":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to void transaction?") == true) {
                            poJSON = poSOATaggingController.VoidTransaction("");
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
                    poSOATaggingController.resetMaster();
                    poSOATaggingController.Detail().clear();
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();

                    poSOATaggingController.Master().setIndustryId(psIndustryId);
//                    poSOATaggingController.Master().setCompanyID(psCompanyId);
//                    poSOATaggingController.Master().setCategoryCode(psCategoryId);
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnPrint", "btnAddAttachment", "btnRemoveAttachment",
                        "btnArrowRight", "btnArrowLeft", "btnRetrieve")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                initButton(pnEditMode);

                if (lsButton.equals("btnUpdate")) {
                    if (poSOATaggingController.Detail(pnDetail).getSourceCode() != null && !poSOATaggingController.Detail(pnDetail).getStockId().equals("")) {
                        tfAppliedAmtDetail.requestFocus();
                    } else {
                        tfSourceNo.requestFocus();
                    }
                }

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void retrievePayables() {
        poJSON = new JSONObject();
        poJSON = poSOATaggingController.loadPurchaseOrderReturn("confirmation", psSupplierId, tfSearchReferenceNo.getText());
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        } else {
            loadTableMain();
        }
        JFXUtil.disableAllHighlight(tblViewMainList, highlightedRowsMain);
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = (txtField.getId());
        String lsValue = txtField.getText();

        lastFocusedTextField = txtField;
        previousSearchedTextField = null;

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
        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfReferenceNo":
                    break;
                case "tfSourceCode":
                    break;
                case "tfSourceNo":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poSOATaggingController.Detail(pnDetail).setStockId("");
                    }
                    break;
                case "tfAppliedAmtDetail":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    if (poSOATaggingController.Detail(pnDetail).getAppliedAmount() != null
                            && !"".equals(poSOATaggingController.Detail(pnDetail).getAppliedAmount())) {
                        if (poSOATaggingController.getReceiveQty(pnDetail).intValue() < Integer.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Return quantity cannot be greater than the order quantity.");
                            poSOATaggingController.Detail(pnDetail).setAppliedAmount(0);
                            tfAppliedAmtDetail.requestFocus();
                            break;
                        }
                    }

                    poJSON = poSOATaggingController.Detail(pnDetail).setQuantity((Integer.valueOf(lsValue)));
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
        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfSOANo":
                    break;
                case "tfVatAmount":
                    break;
                case "tfDiscountAmount":
                    break;
                case "tfFreight":
                    break;
                case "tfNonVatSales":
                    break;
                case "tfZeroVatSales":
                    break;
                case "tfVatExemptSales":
                    break;
            }
//            if (lsTxtFieldID.equals("tfSearchSupplier")
//                    || lsTxtFieldID.equals("tfSearchReferenceNo")) {
////                loadRecordSearch();
//            }
        }
    };

    public void moveNext() {
        int lnReceiveQty = Integer.valueOf(poSOATaggingController.Detail(pnDetail).getQuantity().toString());
        apDetail.requestFocus();
        int lnNewvalue = Integer.valueOf(poSOATaggingController.Detail(pnDetail).getQuantity().toString());
        if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                && poSOATaggingController.Detail(pnDetail).getStockId() != null
                && !"".equals(poSOATaggingController.Detail(pnDetail).getStockId()))) {
            tfAppliedAmtDetail.requestFocus();
        } else {
            pnDetail = JFXUtil.moveToNextRow(tblViewTransDetailList);
            loadRecordDetail();
            if (poSOATaggingController.Detail(pnDetail).getStockId() != null && !poSOATaggingController.Detail(pnDetail).getStockId().equals("")) {
                tfAppliedAmtDetail.requestFocus();
            } else {
                tfSourceNo.requestFocus();
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
                        case "tfSourceNo":
                        case "tfAppliedAmtDetail":
                            int lnReceiveQty = Integer.valueOf(poSOATaggingController.Detail(pnDetail).getQuantity().toString());
                            apDetail.requestFocus();
                            int lnNewvalue = Integer.valueOf(poSOATaggingController.Detail(pnDetail).getQuantity().toString());
                            if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                                    && poSOATaggingController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poSOATaggingController.Detail(pnDetail).getStockId()))) {
                                tfAppliedAmtDetail.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
                                loadRecordDetail();
                                if (poSOATaggingController.Detail(pnDetail).getStockId() != null && !poSOATaggingController.Detail(pnDetail).getStockId().equals("")) {
                                    tfAppliedAmtDetail.requestFocus();
                                } else {
                                    tfSourceNo.requestFocus();
                                }
                                event.consume();
                            }
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfSourceNo":
                        case "tfAppliedAmtDetail":
                            int lnReceiveQty = Integer.valueOf(poSOATaggingController.Detail(pnDetail).getQuantity().toString());
                            apDetail.requestFocus();
                            int lnNewvalue = Integer.valueOf(poSOATaggingController.Detail(pnDetail).getQuantity().toString());
                            if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                                    && poSOATaggingController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poSOATaggingController.Detail(pnDetail).getStockId()))) {
                                tfAppliedAmtDetail.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToNextRow(currentTable);
                                loadRecordDetail();
                                if (poSOATaggingController.Detail(pnDetail).getStockId() != null && !poSOATaggingController.Detail(pnDetail).getStockId().equals("")) {
                                    tfAppliedAmtDetail.requestFocus();
                                } else {
                                    tfSourceNo.requestFocus();
                                }
                                event.consume();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfClient":
                            poJSON = poSOATaggingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                psSupplierId = "";
                                break;
                            } else {
                                psSupplierId = poSOATaggingController.Master().getClientId();
                            }
                            retrievePayables();
//                            loadRecordSearch();
                            return;
                        case "tfCompany":
                            return;
                        case "tfIssuedTo":
                            return;
//                        case "tfSearchReferenceNo":
//                            retrievePayales();
//                            return;
                    }
                    break;
                default:
                    break;
            }
        } catch (GuanzonException ex) {
            Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
                lastFocusedTextField = datePicker;
                previousSearchedTextField = null;

                JFXUtil.JFXUtilDateResult ldtResult = JFXUtil.processDate(inputText, datePicker);
                poJSON = ldtResult.poJSON;
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    loadRecordMaster();
                    return;
                }
                if (inputText == null || "".equals(inputText) || "1900-01-01".equals(inputText)) {
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

                            if (poSOATaggingController.Master().getSourceNo() != null && !"".equals(poSOATaggingController.Master().getSourceNo())) {
                                lsReceivingDate = sdfFormat.format(poSOATaggingController.Master().PurchaseOrderReceivingMaster().getTransactionDate());
                                receivingDate = LocalDate.parse(lsReceivingDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                                if (selectedDate.isBefore(receivingDate)) {
                                    JFXUtil.setJSONError(poJSON, "Transaction date cannot be before the receiving date.");
                                    pbSuccess = false;
                                }
                            } else {
                                if (pbSuccess && !lsServerDate.equals(lsSelectedDate) && pnEditMode == EditMode.ADDNEW) {
                                    JFXUtil.setJSONError(poJSON, "Select PO Receiving before changing the transaction date.");
                                    pbSuccess = false;
                                }
                            }

                            if (pbSuccess && ((poSOATaggingController.getEditMode() == EditMode.UPDATE && !lsTransDate.equals(lsSelectedDate))
                                    || !lsServerDate.equals(lsSelectedDate))) {
                                pbSuccess = false;
                                if (ShowMessageFX.YesNo(null, pxeModuleName, "Change in Transaction Date Detected\n\n"
                                        + "If YES, please seek approval to proceed with the new selected date.\n"
                                        + "If NO, the previous transaction date will be retained.") == true) {
                                    if (oApp.getUserLevel() == UserRight.ENCODER) {
                                        poJSON = ShowDialogFX.getUserApproval(oApp);
                                        if (!"success".equals((String) poJSON.get("result"))) {
                                            pbSuccess = false;
                                        } else {
                                            poSOATaggingController.Master().setTransactionDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));

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
                    case "dpReferenceDate":
                        break;
                    default:

                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadTableMain() {
        // Setting data to table detail
        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewTransDetailList.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
//                Thread.sleep(1000);

                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    main_data.clear();
                    plOrderNoFinal.clear();
                    if (poSOATaggingController.getSOATaggingCount() > 0) {
                        //pending
                        //retreiving using column index
                        for (int lnCtr = 0; lnCtr <= poSOATaggingController.getSOATaggingCount() - 1; lnCtr++) {
                            try {
                                main_data.add(new ModelPurchaseOrderReturn_Main(String.valueOf(lnCtr + 1),
                                        String.valueOf(poSOATaggingController.PurchaseOrderReturnList(lnCtr).Supplier().getCompanyName()),
                                        String.valueOf(poSOATaggingController.PurchaseOrderReturnList(lnCtr).getTransactionDate()),
                                        String.valueOf(poSOATaggingController.PurchaseOrderReturnList(lnCtr).getTransactionNo())
                                ));
                            } catch (SQLException ex) {
                                Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            } catch (GuanzonException ex) {
                                Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            }

                            if (poSOATaggingController.PurchaseOrderReturnList(lnCtr).getTransactionStatus().equals(PurchaseOrderReturnStatus.CONFIRMED)) {
                                plOrderNoPartial.add(new Pair<>(String.valueOf(lnCtr + 1), "1"));
                            }
                        }
                    }
                    JFXUtil.showRetainedHighlight(true, tblViewMainList, "#C1E1C1", plOrderNoPartial, plOrderNoFinal, highlightedRowsMain);

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

    public void loadRecordDetail() {

        if (pnDetail < 0 || pnDetail > poSOATaggingController.getDetailCount() - 1) {
            return;
        }

        boolean lbDisable = poSOATaggingController.Detail(pnDetail).getEditMode() == EditMode.ADDNEW;
//            JFXUtil.setDisabled(!lbDisable, tfSourceNo, tfDescription);
//            if (lbDisable) {
//                while (JFXUtil.isTextFieldContainsStyleClass("DisabledTextField", tfSourceNo, tfDescription)) {
//                    JFXUtil.AddStyleClass("DisabledTextField", tfSourceNo, tfDescription);
//                }
//            } else {
//                JFXUtil.RemoveStyleClass("DisabledTextField", tfSourceNo, tfDescription);
//            }
        tfSourceNo.setText("");
        tfSourceCode.setText("");
        tfReferenceNo.setText("");
        tfIssuedTo.setText("");
        tfCreditAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat("", true));
        tfDebitAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat("", true));
        tfAppliedAmtDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat("", true));
//            tfSourceNo.setText(poSOATaggingController.Detail(pnDetail).Inventory().getBarCode());
//            tfSourceCode.setText(poSOATaggingController.Detail(pnDetail).Inventory().getDescription());
//            tfReferenceNo.setText(poSOATaggingController.Detail(pnDetail).Inventory().Brand().getDescription());
//            tfCreditAmount.setText(poSOATaggingController.Detail(pnDetail).Inventory().Model().getDescription());
//            tfDebitAmount.setText(poSOATaggingController.Detail(pnDetail).Inventory().Color().getDescription());
//            tfAppliedAmtDetail.setText(poSOATaggingController.Detail(pnDetail).Inventory().InventoryType().getDescription());

        JFXUtil.updateCaretPositions(apDetail);

    }

    public void loadRecordMaster() {
//        boolean lbDisable = poSOATaggingController.getEditMode() == EditMode.ADDNEW;
//        if (!lbDisable) {
//            JFXUtil.AddStyleClass("DisabledTextField", tfClient, tfReferenceNo, tfPOReceivingNo);
//        } else {
//            while (JFXUtil.isTextFieldContainsStyleClass("DisabledTextField", tfClient, tfReferenceNo, tfPOReceivingNo)) {
//                JFXUtil.RemoveStyleClass("DisabledTextField", tfClient, tfReferenceNo, tfPOReceivingNo);
//            }
//        }
//        
//        JFXUtil.setDisabled(!lbDisable, tfClient, tfReferenceNo, tfPOReceivingNo);

        boolean lbIsReprint = poSOATaggingController.Master().getPrint().equals("1") ? true : false;
        if (lbIsReprint) {
            btnPrint.setText("Reprint");
        } else {
            btnPrint.setText("Print");
        }

        try {
            Platform.runLater(() -> {
                String lsActive = poSOATaggingController.Master().getTransactionStatus();
                boolean lbPrintStat = pnEditMode == EditMode.READY && !SOATaggingStatus.VOID.equals(lsActive);

                Map<String, String> statusMap = new HashMap<>();
//                statusMap.put(SOATaggingStatus.POSTED, "POSTED");
                statusMap.put(SOATaggingStatus.OPEN, "OPEN");
                statusMap.put(SOATaggingStatus.PAID, "PAID");
                statusMap.put(SOATaggingStatus.CONFIRMED, "CONFIRMED");
                statusMap.put(SOATaggingStatus.RETURNED, "RETURNED");
                statusMap.put(SOATaggingStatus.VOID, "VOIDED");
                statusMap.put(SOATaggingStatus.CANCELLED, "CANCELLED");

                String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN");
                lblStatus.setText(lsStat);
                JFXUtil.setButtonsVisibility(lbPrintStat, btnPrint);
            });

            poSOATaggingController.computeFields();

            tfTransactionNo.setText("");
//            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
            tfSOANo.setText("");
            tfCompany.setText("");
            tfClient.setText("");
//          taRemarks.setText(poSOATaggingController.Master().getRemarks());

            tfTransactionTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat("", true));
            tfVatAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(""));
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(""));
            tfFreight.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(""));
            tfNonVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(""));
            tfZeroVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(""));
            tfVatExemptSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(""));
            tfNetTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat("", true));

            // Transaction Date
//            tfTransactionNo.setText(poSOATaggingController.Master().getTransactionNo());
//            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poSOATaggingController.Master().getTransactionDate());
//            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
//
//            tfClient.setText(poSOATaggingController.Master().Supplier().getCompanyName());
//            tfReferenceNo.setText(poSOATaggingController.Master().PurchaseOrderReceivingMaster().getReferenceNo());
//            tfPOReceivingNo.setText(poSOATaggingController.Master().getSourceNo());
//            taRemarks.setText(poSOATaggingController.Master().getRemarks());
//
//            tfTransactionTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poSOATaggingController.Master().getTransactionTotal().doubleValue())));
            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException ex) {
            Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadTableDetailFromMain() {
        try {
            poJSON = new JSONObject();

            ModelPurchaseOrderReturn_Main selected = (ModelPurchaseOrderReturn_Main) tblViewMainList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                psTransactionNo = poSOATaggingController.PurchaseOrderReturnList(pnMain).getTransactionNo();
                poJSON = poSOATaggingController.OpenTransaction(psTransactionNo);
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
            }
            Platform.runLater(() -> {
                loadTableDetail();
            });

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    details_data.clear();
                    int lnCtr;
                    try {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poSOATaggingController.getDetailCount() - 1;
                            while (lnCtr >= 0) {
                                if (poSOATaggingController.Detail(lnCtr).getStockId() == null || poSOATaggingController.Detail(lnCtr).getStockId().equals("")) {
                                    poSOATaggingController.Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poSOATaggingController.getDetailCount() - 1) >= 0) {
                                if (poSOATaggingController.Detail(poSOATaggingController.getDetailCount() - 1).getStockId() != null && !poSOATaggingController.Detail(poSOATaggingController.getDetailCount() - 1).getStockId().equals("")) {
                                    poSOATaggingController.AddDetail();
                                }
                            }

                            if ((poSOATaggingController.getDetailCount() - 1) < 0) {
                                poSOATaggingController.AddDetail();
                            }
                        }

                        double lnTotal = 0.0;
                        for (lnCtr = 0; lnCtr < poSOATaggingController.getDetailCount(); lnCtr++) {
                            try {

                                lnTotal = poSOATaggingController.Detail(lnCtr).getUnitPrce().doubleValue() * poSOATaggingController.Detail(lnCtr).getQuantity().intValue();
                            } catch (Exception e) {
                            }

                            details_data.add(
                                    new ModelPurchaseOrderReturn_Detail(String.valueOf(lnCtr + 1),
                                            String.valueOf(poSOATaggingController.Detail(lnCtr).Inventory().getBarCode()),
                                            String.valueOf(poSOATaggingController.Detail(lnCtr).Inventory().getDescription()),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poSOATaggingController.Detail(lnCtr).getUnitPrce())),
                                            String.valueOf(poSOATaggingController.getReceiveQty(lnCtr)),
                                            String.valueOf(poSOATaggingController.Detail(lnCtr).getQuantity()),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal)) //identify total
                                    ));
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
                        Logger.getLogger(SOATagging_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpReferenceDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate, dpReferenceDate);
    }

    public void initTextFields() {
        Platform.runLater(() -> {
            JFXUtil.setVerticalScroll(taRemarks);
        });
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfCompany, tfClient, tfIssuedTo);
        JFXUtil.setFocusListener(txtDetail_Focus, tfSourceNo, tfSourceCode, tfReferenceNo, tfAppliedAmtDetail);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);
//        JFXUtil.setCommaFormatter(tfVatAmount, tfDiscountAmount, tfZeroVatSales, tfVatExemptSales);
        CustomCommonUtil.inputDecimalOnly(tfVatAmount, tfDiscountAmount, tfZeroVatSales, tfVatExemptSales);
    }

    public void initTableOnClick() {

        tblViewTransDetailList.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewTransDetailList.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    if (poSOATaggingController.Detail(pnDetail).getStockId() != null && !poSOATaggingController.Detail(pnDetail).getStockId().equals("")) {
                        tfAppliedAmtDetail.requestFocus();
                    } else {
                        tfSourceNo.requestFocus();
                    }
                }
            }
        });

        tblViewMainList.setOnMouseClicked(event -> {
            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableDetailFromMain();
                    pnEditMode = poSOATaggingController.getEditMode();
                    initButton(pnEditMode);
                }
            }
        });

        JFXUtil.applyRowHighlighting(tblViewMainList, item -> ((ModelPurchaseOrderReturn_Main) item).getIndex01(), highlightedRowsMain);
        tblViewTransDetailList.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetailList, tblViewMainList); // need to use computed-size in min-width of the column to work
    }

    private void initButton(int fnValue) {

        boolean lbShow1 = (fnValue == EditMode.UPDATE);
//        boolean lbShow2 = (fnValue == EditMode.READY || fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        // Manage visibility and managed state of other buttons
        //Update 
        JFXUtil.setButtonsVisibility(lbShow1, btnSearch, btnSave, btnCancel);

        //Ready
        JFXUtil.setButtonsVisibility(lbShow3, btnPrint, btnUpdate);

        //Unkown || Ready
        JFXUtil.setDisabled(!lbShow1, apMaster, apDetail);
        JFXUtil.setButtonsVisibility(lbShow4, btnClose);
//        JFXUtil.setButtonsVisibility(false, btnReturn);

        switch (poSOATaggingController.Master().getTransactionStatus()) {
            case PurchaseOrderReturnStatus.CONFIRMED:
//                JFXUtil.setButtonsVisibility(false, btnConfirm);
//                if (poSOATaggingController.Master().isProcessed()) {
//                    JFXUtil.setButtonsVisibility(false, btnUpdate, btnVoid);
//                } else {
//                    JFXUtil.setButtonsVisibility(lbShow3, btnReturn);
//                }
                break;
            case PurchaseOrderReturnStatus.POSTED:
            case PurchaseOrderReturnStatus.PAID:
            case PurchaseOrderReturnStatus.RETURNED:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
            case PurchaseOrderReturnStatus.VOID:
            case PurchaseOrderReturnStatus.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnPrint);
                break;
        }
    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail);
        JFXUtil.setColumnLeft(tblSourceNoDetail, tblSourceCodeDetail, tblReferenceNoDetail);
        JFXUtil.setColumnRight(tblCreditAmtDetail, tblDebitAmtDetail, tblAppliedAmtDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetailList);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);

        SortedList<ModelPurchaseOrderReturn_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewTransDetailList.comparatorProperty());
        tblViewTransDetailList.setItems(sortedData);
        tblViewTransDetailList.autosize();
    }

    public void initMainGrid() {
        JFXUtil.setColumnCenter(tblRowNo, tblDate, tblReferenceNo);
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
        psTransactionNo = "";
        previousSearchedTextField = null;
        lastFocusedTextField = null;
        dpTransactionDate.setValue(null);

        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse);

    }

}
