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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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
import javafx.scene.input.KeyCode;
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
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.services.PurchaseOrderReturnControllers;
import org.guanzon.cas.purchasing.status.PurchaseOrderReturnStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import javafx.animation.PauseTransition;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReturn;
import javafx.util.Pair;
import java.util.ArrayList;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.constant.UserRight;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * FXML Controller class
 *
 * @author Team 2 : Arsiela & Aldrich
 */
public class PurchaseOrderReturn_ConfirmationMCController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "Purchase Order Return Confirmation MC";
    static PurchaseOrderReturn poPurchaseReturnController;
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

    private int pnAttachment;

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    private Object lastFocusedTextField = null;
    private Object previousSearchedTextField = null;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail;

    @FXML
    private HBox hbButtons, hboxid;

    @FXML
    private Label lblSource, lblStatus;

    @FXML
    private Button btnUpdate, btnSearch, btnSave, btnCancel, btnConfirm, btnVoid, btnPrint, btnReturn, btnHistory, btnRetrieve, btnClose;

    @FXML
    private TextField tfSearchSupplier, tfSearchReferenceNo, tfTransactionNo, tfSupplier, tfReferenceNo, tfPOReceivingNo,
            tfTotal, tfEngineNo, tfFrameNo, tfPlateNo, tfReturnQuantity, tfColor,
            tfInventoryType, tfMeasure, tfCost, tfBrand, tfModel, tfModelVariant, tfReceiveQuantity;

    @FXML
    private DatePicker dpTransactionDate;

    @FXML
    private TextArea taRemarks;

    @FXML
    private TableView tblViewDetails, tblViewPuchaseOrderReturn;

    @FXML
    private TableColumn tblRowNoDetail, tblEngineNoDetail, tblFrameNoDetail, tblPlateNoDetail,
            tblDescriptionDetail, tblCostDetail, tblReceiveQuantityDetail, tblReturnQuantityDetail, tblTotalDetail,
            tblRowNo, tblSupplier, tblDate, tblReferenceNo;

    @FXML
    private Pagination pgPagination;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        poPurchaseReturnController = new PurchaseOrderReturnControllers(oApp, null).PurchaseOrderReturn();
        poJSON = new JSONObject();
        poJSON = poPurchaseReturnController.InitTransaction(); // Initialize transaction
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
            poPurchaseReturnController.Master().setIndustryId(psIndustryId);
            poPurchaseReturnController.Master().setCompanyId(psCompanyId);
            poPurchaseReturnController.setIndustryId(psIndustryId);
            poPurchaseReturnController.setCompanyId(psCompanyId);
            poPurchaseReturnController.setCategoryId(psCategoryId);
            poPurchaseReturnController.initFields();
            loadRecordSearch();
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
                        poJSON = poPurchaseReturnController.printRecord(() -> {
                            if (isPrinted) {
                                JFXUtil.disableAllHighlightByColor(tblViewPuchaseOrderReturn, "#A7C7E7", highlightedRowsMain);
                                poPurchaseReturnController.resetMaster();
                                poPurchaseReturnController.Detail().clear();
                                pnEditMode = EditMode.UNKNOWN;
                                clearTextFields();
                                initButton(pnEditMode);
                            }
                            Platform.runLater(() -> {
                                try {
                                    if (!isPrinted) {
                                        poPurchaseReturnController.OpenTransaction(poPurchaseReturnController.PurchaseOrderReturnList(pnMain).getTransactionNo());
                                    }
                                    loadRecordMaster();
                                    loadTableDetail();
                                } catch (CloneNotSupportedException ex) {
                                    Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (SQLException ex) {
                                    Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (GuanzonException ex) {
                                    Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                isPrinted = false;
                            });
                        });
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
                        poJSON = poPurchaseReturnController.OpenTransaction(psTransactionNo);
                        poJSON = poPurchaseReturnController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }

                        pnEditMode = poPurchaseReturnController.getEditMode();
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
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            JFXUtil.disableAllHighlightByColor(tblViewPuchaseOrderReturn, "#A7C7E7", highlightedRowsMain);
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        retrievePOR();
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poPurchaseReturnController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poPurchaseReturnController.AddDetail();
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON = poPurchaseReturnController.OpenTransaction(psTransactionNo);
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poPurchaseReturnController.Master().getTransactionStatus().equals(PurchaseOrderReturnStatus.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poPurchaseReturnController.ConfirmTransaction("Confirmed");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                                JFXUtil.highlightByKey(tblViewPuchaseOrderReturn, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                                // Print Transaction Prompt
                                loJSON = poPurchaseReturnController.OpenTransaction(psTransactionNo);
                                loadRecordMaster();
                                isPrinted = false;
                                if ("success".equals(loJSON.get("result"))) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to print this transaction?")) {
                                        isPrinted = true;
                                        btnPrint.fire();
                                    }
                                }
                                if (!isPrinted) {
                                    JFXUtil.disableAllHighlightByColor(tblViewPuchaseOrderReturn, "#A7C7E7", highlightedRowsMain);
                                }
                            }
                        } else {
                            return;
                        }

                        break;

                    case "btnConfirm":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to confirm transaction?") == true) {
                            poJSON = poPurchaseReturnController.ConfirmTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewPuchaseOrderReturn, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewPuchaseOrderReturn, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnVoid":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to void transaction?") == true) {
                            if (PurchaseOrderReturnStatus.CONFIRMED.equals(poPurchaseReturnController.Master().getTransactionStatus())) {
                                poJSON = poPurchaseReturnController.CancelTransaction("Cancel");
                            } else {
                                poJSON = poPurchaseReturnController.VoidTransaction("Void");
                            }
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewPuchaseOrderReturn, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewPuchaseOrderReturn, String.valueOf(pnMain + 1), "#FAA0A0", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnReturn":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to return transaction?") == true) {
                            poJSON = poPurchaseReturnController.ReturnTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewPuchaseOrderReturn, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewPuchaseOrderReturn, String.valueOf(pnMain + 1), "#FAC898", highlightedRowsMain);
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
                    poPurchaseReturnController.resetMaster();
                    poPurchaseReturnController.Detail().clear();
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();

                    poPurchaseReturnController.Master().setIndustryId(psIndustryId);
                    poPurchaseReturnController.Master().setCompanyId(psCompanyId);
                    poPurchaseReturnController.Master().setCategoryCode(psCategoryId);
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnPrint", "btnAddAttachment", "btnRemoveAttachment",
                        "btnArrowRight", "btnArrowLeft", "btnRetrieve")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                initButton(pnEditMode);

                if (lsButton.equals("btnUpdate")) {
                    if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !"".equals(poPurchaseReturnController.Detail(pnDetail).getStockId())) {
                        tfReturnQuantity.requestFocus();
                    } else {
                        tfEngineNo.requestFocus();
                    }
                }

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void retrievePOR() {
        poJSON = new JSONObject();
        poJSON = poPurchaseReturnController.loadPurchaseOrderReturn("confirmation", psSupplierId, tfSearchReferenceNo.getText());
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
                    poJSON = poPurchaseReturnController.Master().setRemarks(lsValue);
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
                case "tfFrameNo":
                case "tfEngineNo":
                case "tfPlateNo":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReturnController.Detail(pnDetail).setStockId("");
                    }
                    break;
                case "tfReturnQuantity":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (poPurchaseReturnController.Detail(pnDetail).getQuantity() != null
                            && !"".equals(poPurchaseReturnController.Detail(pnDetail).getQuantity())) {
                        if (poPurchaseReturnController.getReceiveQty(pnDetail).intValue() < Integer.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Return quantity cannot be greater than the order quantity.");
                            poPurchaseReturnController.Detail(pnDetail).setQuantity(0);
                            tfReturnQuantity.requestFocus();
                            break;
                        }
                    }

                    poJSON = poPurchaseReturnController.Detail(pnDetail).setQuantity((Integer.valueOf(lsValue)));
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

    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
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
                case "tfSearchSupplier":
                    if (lsValue.equals("")) {
                        psSupplierId = "";
                    }
                    break;
                case "tfSearchReferenceNo":
                    break;
            }
            if (lsTxtFieldID.equals("tfSearchSupplier")
                    || lsTxtFieldID.equals("tfSearchReferenceNo")) {
                loadRecordSearch();
            }
        }
    };

    public void moveNext() {
        int lnReceiveQty = poPurchaseReturnController.Detail(pnDetail).getQuantity().intValue();
        apDetail.requestFocus();
        int lnNewvalue = poPurchaseReturnController.Detail(pnDetail).getQuantity().intValue();
        if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                && poPurchaseReturnController.Detail(pnDetail).getStockId() != null
                && !"".equals(poPurchaseReturnController.Detail(pnDetail).getStockId()))) {
            tfReturnQuantity.requestFocus();
        } else {
            pnDetail = JFXUtil.moveToNextRow(tblViewDetails);
            loadRecordDetail();
            if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !poPurchaseReturnController.Detail(pnDetail).getStockId().equals("")) {
                tfReturnQuantity.requestFocus();
            } else {
                tfEngineNo.requestFocus();
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

            TableView<?> currentTable = tblViewDetails;
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
                        case "tfEngineNo":
                        case "tfReturnQuantity":
                            int lnReceiveQty = poPurchaseReturnController.Detail(pnDetail).getQuantity().intValue();
                            apDetail.requestFocus();
                            int lnNewvalue = poPurchaseReturnController.Detail(pnDetail).getQuantity().intValue();
                            if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                                    && poPurchaseReturnController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poPurchaseReturnController.Detail(pnDetail).getStockId()))) {
                                tfReturnQuantity.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
                                loadRecordDetail();
                                if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !poPurchaseReturnController.Detail(pnDetail).getStockId().equals("")) {
                                    tfReturnQuantity.requestFocus();
                                } else {
                                    tfEngineNo.requestFocus();
                                }
                                event.consume();
                            }
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfEngineNo":
                        case "tfReturnQuantity":
                            moveNext();
                            event.consume();
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfSearchSupplier":
                            poJSON = poPurchaseReturnController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                psSupplierId = "";
                                break;
                            } else {
                                psSupplierId = poPurchaseReturnController.Master().getSupplierId();
                            }
                            retrievePOR();
                            loadRecordSearch();
                            return;
                        case "tfSearchReferenceNo":
                            retrievePOR();
                            return;
                        case "tfEngineNo":
                            poJSON = poPurchaseReturnController.SearchEngineNo(lsValue, pnDetail);
                            lnRow = (int) poJSON.get("row");
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                if (pnDetail != lnRow) {
                                    pnDetail = lnRow;
                                    loadRecordDetail();
                                    tfReturnQuantity.requestFocus();
                                    return;
                                }
                                tfEngineNo.setText("");
                                break;
                            }
                            loadTableDetail();

                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(event1 -> {
                                    tfReturnQuantity.requestFocus();
                                });
                                delay.play();
                            });
                            break;

                        case "tfFrameNo":
                            poJSON = poPurchaseReturnController.SearchFrameNo(lsValue, pnDetail);
                            lnRow = (int) poJSON.get("row");
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                if (pnDetail != lnRow) {
                                    pnDetail = lnRow;
                                    loadRecordDetail();
                                    tfReturnQuantity.requestFocus();
                                    return;
                                }
                                tfFrameNo.setText("");
                                break;
                            }
                            loadTableDetail();

                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(event1 -> {
                                    tfReturnQuantity.requestFocus();
                                });
                                delay.play();
                            });
                            break;
                        case "tfPlateNo":
                            poJSON = poPurchaseReturnController.SearchPlateNo(lsValue, pnDetail);
                            lnRow = (int) poJSON.get("row");
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                if (pnDetail != lnRow) {
                                    pnDetail = lnRow;
                                    loadRecordDetail();
                                    tfReturnQuantity.requestFocus();
                                    return;
                                }
                                tfPlateNo.setText("");
                                break;
                            }
                            loadTableDetail();

                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(event1 -> {
                                    tfReturnQuantity.requestFocus();
                                });
                                delay.play();
                            });
                            break;
                    }
                    break;
                default:
                    break;
            }
        } catch (GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    boolean pbSuccess = true;

    private void datepicker_Action(ActionEvent event) {
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "success");

        try {
            Object source = event.getSource();
            if (source instanceof DatePicker) {
                DatePicker datePicker = (DatePicker) source;
                String inputText = datePicker.getEditor().getText();
                SimpleDateFormat sdfFormat = new SimpleDateFormat(SQLUtil.FORMAT_SHORT_DATE);
                LocalDate currentDate = null;
                LocalDate selectedDate = null;
                String lsServerDate = "";
                String lsTransDate = "";
                String lsSelectedDate = "";
                String lsReceivingDate = "";
                LocalDate receivingDate = null;
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
                        if (poPurchaseReturnController.getEditMode() == EditMode.ADDNEW
                                || poPurchaseReturnController.getEditMode() == EditMode.UPDATE) {
                            lsServerDate = sdfFormat.format(oApp.getServerDate());
                            lsTransDate = sdfFormat.format(poPurchaseReturnController.Master().getTransactionDate());
                            lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                            currentDate = LocalDate.parse(lsServerDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                            selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                            if (selectedDate.isAfter(currentDate)) {
                                poJSON.put("result", "error");
                                poJSON.put("message", "Future dates are not allowed.");
                                pbSuccess = false;
                            }
                            if (poPurchaseReturnController.Master().getSourceNo() != null && !"".equals(poPurchaseReturnController.Master().getSourceNo())) {
                                lsReceivingDate = sdfFormat.format(poPurchaseReturnController.Master().PurchaseOrderReceivingMaster().getTransactionDate());
                                receivingDate = LocalDate.parse(lsReceivingDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                                if (selectedDate.isBefore(receivingDate)) {
                                    poJSON.put("result", "error");
                                    poJSON.put("message", "Transaction date cannot be before the receiving date.");
                                    pbSuccess = false;
                                }
                            } else {
                                if (pbSuccess && !lsServerDate.equals(lsSelectedDate) && pnEditMode == EditMode.ADDNEW) {
                                    poJSON.put("result", "error");
                                    poJSON.put("message", "Select PO Receiving before changing the transaction date.");
                                    pbSuccess = false;
                                }
                            }
                            if (pbSuccess && ((poPurchaseReturnController.getEditMode() == EditMode.UPDATE && !lsTransDate.equals(lsSelectedDate))
                                    || !lsServerDate.equals(lsSelectedDate))) {
                                pbSuccess = false;
                                if (oApp.getUserLevel() <= UserRight.ENCODER) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Change in Transaction Date Detected\n\n"
                                            + "If YES, please seek approval to proceed with the new selected date.\n"
                                            + "If NO, the previous transaction date will be retained.") == true) {
                                        poJSON = ShowDialogFX.getUserApproval(oApp);
                                        if (!"success".equals((String) poJSON.get("result"))) {
                                            pbSuccess = false;
                                        } else {
                                            if(Integer.parseInt(poJSON.get("nUserLevl").toString())<= UserRight.ENCODER){
                                                poJSON.put("result", "error");
                                                poJSON.put("message", "User is not an authorized approving officer.");
                                                pbSuccess = false;
                                            } else {
                                                poPurchaseReturnController.Master().setTransactionDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
                                            }
                                        }
                                    } else {
                                        pbSuccess = false;
                                    }
                                } else {
                                    poPurchaseReturnController.Master().setTransactionDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
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
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadTableMain() {
        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewPuchaseOrderReturn.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
//                Thread.sleep(1000);

                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    main_data.clear();
                    JFXUtil.disableAllHighlight(tblViewPuchaseOrderReturn, highlightedRowsMain);
                    if (poPurchaseReturnController.getPurchaseOrderReturnCount() > 0) {
                        //pending
                        //retreiving using column index
                        for (int lnCtr = 0; lnCtr <= poPurchaseReturnController.getPurchaseOrderReturnCount() - 1; lnCtr++) {
                            try {
                                main_data.add(new ModelPurchaseOrderReturn_Main(String.valueOf(lnCtr + 1),
                                        String.valueOf(poPurchaseReturnController.PurchaseOrderReturnList(lnCtr).Supplier().getCompanyName()),
                                        String.valueOf(poPurchaseReturnController.PurchaseOrderReturnList(lnCtr).getTransactionDate()),
                                        String.valueOf(poPurchaseReturnController.PurchaseOrderReturnList(lnCtr).getTransactionNo())
                                ));
                            } catch (SQLException ex) {
                                Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            } catch (GuanzonException ex) {
                                Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            }

                            if (poPurchaseReturnController.PurchaseOrderReturnList(lnCtr).getTransactionStatus().equals(PurchaseOrderReturnStatus.CONFIRMED)) {
                                JFXUtil.highlightByKey(tblViewPuchaseOrderReturn, String.valueOf(lnCtr + 1), "#C1E1C1", highlightedRowsMain);
                            }
                        }
                    }

                    if (pnMain < 0 || pnMain
                            >= main_data.size()) {
                        if (!main_data.isEmpty()) {
                            /* FOCUS ON FIRST ROW */
                            tblViewPuchaseOrderReturn.getSelectionModel().select(0);
                            tblViewPuchaseOrderReturn.getFocusModel().focus(0);
                            pnMain = tblViewPuchaseOrderReturn.getSelectionModel().getSelectedIndex();

                        }
                    } else {
                        /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                        tblViewPuchaseOrderReturn.getSelectionModel().select(pnMain);
                        tblViewPuchaseOrderReturn.getFocusModel().focus(pnMain);
                    }
//                    if (poPurchaseReturnController.getPurchaseOrderReturnCount() < 1) {
//                        JFXUtil.loadTab(pgPagination, main_data.size(), ROWS_PER_PAGE, tblViewPuchaseOrderReturn, filteredData);
//                    }
                    JFXUtil.loadTab(pgPagination, main_data.size(), ROWS_PER_PAGE, tblViewPuchaseOrderReturn, filteredData);
                });

                return null;
            }

            @Override
            protected void succeeded() {
                placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed
                if (main_data == null || main_data.isEmpty()) {
                    tblViewPuchaseOrderReturn.setPlaceholder(placeholderLabel);
                } else {
                    tblViewPuchaseOrderReturn.toFront();
                }
                progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblViewPuchaseOrderReturn.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
    }

    public void loadRecordSearch() {
        try {
            lblSource.setText(poPurchaseReturnController.Master().Company().getCompanyName() + " - " + poPurchaseReturnController.Master().Industry().getDescription());

            if (psSupplierId.equals("")) {
                tfSearchSupplier.setText("");
            } else {
                tfSearchSupplier.setText(poPurchaseReturnController.Master().Supplier().getCompanyName());
            }
            try {
                if (tfSearchReferenceNo.getText() == null || tfSearchReferenceNo.getText().equals("")) {
                    tfSearchReferenceNo.setText("");
                } else {

                }
            } catch (Exception e) {
                tfSearchReferenceNo.setText("");
            }

        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordDetail() {

        try {
            if (pnDetail < 0 || pnDetail > poPurchaseReturnController.getDetailCount() - 1) {
                return;
            }
            boolean lbDisable = poPurchaseReturnController.Detail(pnDetail).getEditMode() == EditMode.ADDNEW;

            JFXUtil.setDisabled(!lbDisable, tfEngineNo, tfFrameNo, tfPlateNo);

            tfEngineNo.setText(poPurchaseReturnController.Detail(pnDetail).InventorySerial().getSerial01());
            tfFrameNo.setText(poPurchaseReturnController.Detail(pnDetail).InventorySerial().getSerial02());
            tfPlateNo.setText(poPurchaseReturnController.Detail(pnDetail).InventorySerialRegistration().getPlateNoP());
            tfBrand.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Brand().getDescription());
            tfModel.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Model().getDescription());
            tfModelVariant.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Variant().getDescription());
            tfColor.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Color().getDescription());
            tfInventoryType.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().InventoryType().getDescription());
            tfMeasure.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Measure().getDescription());

            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReturnController.Detail(pnDetail).getUnitPrce(), true));
            tfReceiveQuantity.setText(String.valueOf(poPurchaseReturnController.getReceiveQty(pnDetail).intValue()));
            tfReturnQuantity.setText(String.valueOf(poPurchaseReturnController.Detail(pnDetail).getQuantity().intValue()));

            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadRecordMaster() {
        boolean lbIsReprint = poPurchaseReturnController.Master().getPrint().equals("1") ? true : false;
        if (lbIsReprint) {
            btnPrint.setText("Reprint");
        } else {
            btnPrint.setText("Print");
        }

        try {
            Platform.runLater(() -> {
                boolean lbPrintStat = pnEditMode == EditMode.READY;
                String lsActive = poPurchaseReturnController.Master().getTransactionStatus();
                String lsStat = "UNKNOWN";
                switch (lsActive) {
                    case PurchaseOrderReturnStatus.POSTED:
                        lsStat = "POSTED";
                        break;
                    case PurchaseOrderReturnStatus.PAID:
                        lsStat = "PAID";
                        break;
                    case PurchaseOrderReturnStatus.CONFIRMED:
                        lsStat = "CONFIRMED";
                        break;
                    case PurchaseOrderReturnStatus.OPEN:
                        lsStat = "OPEN";
                        break;
                    case PurchaseOrderReturnStatus.RETURNED:
                        lsStat = "RETURNED";
                        break;
                    case PurchaseOrderReturnStatus.VOID:
                        lsStat = "VOIDED";
                        lbPrintStat = false;
                        break;
                    case PurchaseOrderReturnStatus.CANCELLED:
                        lsStat = "CANCELLED";
                        break;
                    default:
                        lsStat = "UNKNOWN";
                        break;

                }
                lblStatus.setText(lsStat);
                JFXUtil.setButtonsVisibility(lbPrintStat, btnPrint);
            });

            poPurchaseReturnController.computeFields();

            // Transaction Date
            tfTransactionNo.setText(poPurchaseReturnController.Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poPurchaseReturnController.Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));

            tfSupplier.setText(poPurchaseReturnController.Master().Supplier().getCompanyName());
            tfReferenceNo.setText(poPurchaseReturnController.Master().PurchaseOrderReceivingMaster().getReferenceNo());
            tfPOReceivingNo.setText(poPurchaseReturnController.Master().getSourceNo());
            taRemarks.setText(poPurchaseReturnController.Master().getRemarks());

            tfTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReturnController.Master().getTransactionTotal().doubleValue(), true));

            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadTableDetailFromMain() {
        try {
            poJSON = new JSONObject();

            ModelPurchaseOrderReturn_Main selected = (ModelPurchaseOrderReturn_Main) tblViewPuchaseOrderReturn.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                JFXUtil.disableAllHighlightByColor(tblViewPuchaseOrderReturn, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblViewPuchaseOrderReturn, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);
                psTransactionNo = poPurchaseReturnController.PurchaseOrderReturnList(pnMain).getTransactionNo();
                poJSON = poPurchaseReturnController.OpenTransaction(psTransactionNo);
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
            }
            Platform.runLater(() -> {
                loadTableDetail();
            });

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadTableDetail() {
        pbEntered = false;
        // Setting data to table detail

        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewDetails.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

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
                            lnCtr = poPurchaseReturnController.getDetailCount() - 1;
                            while (lnCtr >= 0) {
                                if (poPurchaseReturnController.Detail(lnCtr).getStockId() == null || poPurchaseReturnController.Detail(lnCtr).getStockId().equals("")) {
                                    poPurchaseReturnController.Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poPurchaseReturnController.getDetailCount() - 1) >= 0) {
                                if (poPurchaseReturnController.Detail(poPurchaseReturnController.getDetailCount() - 1).getStockId() != null && !poPurchaseReturnController.Detail(poPurchaseReturnController.getDetailCount() - 1).getStockId().equals("")) {
                                    poPurchaseReturnController.AddDetail();
                                }
                            }

                            if ((poPurchaseReturnController.getDetailCount() - 1) < 0) {
                                poPurchaseReturnController.AddDetail();
                            }
                        }

                        double lnTotal = 0.0;
                        for (lnCtr = 0; lnCtr < poPurchaseReturnController.getDetailCount(); lnCtr++) {
                            try {

                                lnTotal = poPurchaseReturnController.Detail(lnCtr).getUnitPrce().doubleValue() * poPurchaseReturnController.Detail(lnCtr).getQuantity().intValue();
                            } catch (Exception e) {
                            }
                            String lsCSPlateNo = "";
                            if (poPurchaseReturnController.Detail(lnCtr).InventorySerialRegistration().getPlateNoP() != null
                                    && !"".equals(poPurchaseReturnController.Detail(lnCtr).InventorySerialRegistration().getPlateNoP())) {
                                lsCSPlateNo = poPurchaseReturnController.Detail(lnCtr).InventorySerialRegistration().getPlateNoP();
                            }
                            details_data.add(
                                    new ModelPurchaseOrderReturn_Detail(String.valueOf(lnCtr + 1),
                                            String.valueOf(poPurchaseReturnController.Detail(lnCtr).InventorySerial().getSerial01()),
                                            String.valueOf(poPurchaseReturnController.Detail(lnCtr).InventorySerial().getSerial02()),
                                            String.valueOf(lsCSPlateNo),
                                            String.valueOf(poPurchaseReturnController.Detail(lnCtr).Inventory().getDescription()),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReturnController.Detail(lnCtr).getUnitPrce(), true)),
                                            String.valueOf(poPurchaseReturnController.getReceiveQty(lnCtr).intValue()),
                                            String.valueOf(poPurchaseReturnController.Detail(lnCtr).getQuantity().intValue()),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal, true)))
                            );
                        }

                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                tblViewDetails.getSelectionModel().select(0);
                                tblViewDetails.getFocusModel().focus(0);
                                pnDetail = tblViewDetails.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            tblViewDetails.getSelectionModel().select(pnDetail);
                            tblViewDetails.getFocusModel().focus(pnDetail);
                            loadRecordDetail();
                        }
                        loadRecordMaster();
                    } catch (SQLException ex) {
                        Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(PurchaseOrderReturn_ConfirmationMCController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    }
                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewDetails.setPlaceholder(placeholderLabel);
                } else {
                    tblViewDetails.toFront();
                }
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewDetails.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background

    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate);

    }

    public void initTextFields() {
        Platform.runLater(() -> {
            JFXUtil.setVerticalScroll(taRemarks);
        });
        JFXUtil.setFocusListener(txtField_Focus, tfSearchSupplier, tfSearchReferenceNo);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtDetail_Focus, tfFrameNo, tfEngineNo, tfPlateNo, tfReturnQuantity, tfReturnQuantity);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);

        CustomCommonUtil.inputIntegersOnly(tfReceiveQuantity, tfReturnQuantity);
        CustomCommonUtil.inputDecimalOnly(tfCost);
    }

    public void initTableOnClick() {

        tblViewDetails.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewDetails.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !poPurchaseReturnController.Detail(pnDetail).getStockId().equals("")) {
                        tfReturnQuantity.requestFocus();
                    } else {
                        tfEngineNo.requestFocus();
                    }
                }
            }
        });

        tblViewPuchaseOrderReturn.setOnMouseClicked(event -> {
            pnMain = tblViewPuchaseOrderReturn.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableDetailFromMain();
                    pnEditMode = poPurchaseReturnController.getEditMode();
                    initButton(pnEditMode);
                }
            }
        });

        tblViewPuchaseOrderReturn.setRowFactory(tv -> new TableRow<ModelPurchaseOrderReturn_Main>() {
            @Override
            protected void updateItem(ModelPurchaseOrderReturn_Main item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle(""); // Reset for empty rows
                } else {
                    String key = item.getIndex01(); // defines the ReferenceNo
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
        });

        tblViewDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewDetails, tblViewPuchaseOrderReturn); // need to use computed-size in min-width of the column to work
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
        JFXUtil.setButtonsVisibility(lbShow3, btnPrint, btnUpdate, btnHistory, btnConfirm, btnVoid);

        //Unkown || Ready
        JFXUtil.setDisabled(!lbShow1, apMaster, apDetail);
        JFXUtil.setButtonsVisibility(lbShow4, btnClose);
        JFXUtil.setButtonsVisibility(false, btnReturn);

        switch (poPurchaseReturnController.Master().getTransactionStatus()) {
            case PurchaseOrderReturnStatus.CONFIRMED:
                JFXUtil.setButtonsVisibility(false, btnConfirm);
                if (poPurchaseReturnController.Master().isProcessed()) {
                    JFXUtil.setButtonsVisibility(false, btnUpdate, btnVoid);
                } else {
                    JFXUtil.setButtonsVisibility(lbShow3, btnReturn);
                }
                break;
            case PurchaseOrderReturnStatus.POSTED:
            case PurchaseOrderReturnStatus.PAID:
            case PurchaseOrderReturnStatus.RETURNED:
                JFXUtil.setButtonsVisibility(false, btnConfirm, btnUpdate, btnReturn, btnVoid);
                break;
            case PurchaseOrderReturnStatus.VOID:
            case PurchaseOrderReturnStatus.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnConfirm, btnUpdate, btnReturn, btnVoid, btnPrint);
                break;
        }
    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail, tblReceiveQuantityDetail, tblReturnQuantityDetail);
        JFXUtil.setColumnLeft(tblEngineNoDetail, tblFrameNoDetail, tblDescriptionDetail, tblPlateNoDetail);
        JFXUtil.setColumnRight(tblCostDetail, tblTotalDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewDetails);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);

        SortedList<ModelPurchaseOrderReturn_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewDetails.comparatorProperty());
        tblViewDetails.setItems(sortedData);
        tblViewDetails.autosize();
    }

    public void initMainGrid() {
        JFXUtil.setColumnCenter(tblRowNo, tblDate, tblReferenceNo);
        JFXUtil.setColumnLeft(tblSupplier);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewPuchaseOrderReturn);

        filteredData = new FilteredList<>(main_data, b -> true);
        tblViewPuchaseOrderReturn.setItems(filteredData);

    }

    private void tableKeyEvents(KeyEvent event) {
        if (details_data.size() > 0) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblViewDetails":
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
