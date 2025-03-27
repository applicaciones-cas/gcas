/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrder;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrderDetail;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.services.PurchaseOrderControllers;
import org.guanzon.cas.purchasing.status.PurchaseOrderStatus;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PurchaseOrder_EntryMonarchHospitalityController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order MH";
    private LogWrapper logWrapper;
    private int pnEditMode;
    private JSONObject poJSON;
    unloadForm poUnload = new unloadForm();
    private ObservableList<ModelPurchaseOrder> poApprovedStockRequest_data = FXCollections.observableArrayList();
    private ObservableList<ModelPurchaseOrderDetail> poDetail_data = FXCollections.observableArrayList();
    private int pnTblStockRequestRow = -1;
    private int pnTblPODetailRow = -1;
    private int pnSTOCK_REQUEST_PAGE = 50;
    private TextField activeField;
    @FXML
    private AnchorPane AnchorMaster, AnchorDetails, AnchorMain, apBrowse, apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel,
            btnPrint, btnRetrieve, btnTransHistory, btnClose;
    @FXML
    private TextField tfTransactionNo, tfIndustry, tfCompany, tfSupplier, tfDestination, tfReferenceNo,
            tfTerm, tfDiscountRate, tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount;
    @FXML
    private Label lblTransactionStatus;
    @FXML
    private CheckBox chkbAdvancePayment;
    @FXML
    private DatePicker dpTransactionDate, dpExpectedDlvrDate;
    @FXML
    private TextField tfBarcode, tfDescription, tfBrand, tfModel, tfColor, tfCategory, tfInventoryType,
            tfMeasure, tfClass, tfAMC, tfROQ, tfRO, tfBO, tfQOH, tfCost, tfRequestQuantity, tfOrderQuantity;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView<ModelPurchaseOrderDetail> tblVwOrderDetails;
    @FXML
    private TableColumn<ModelPurchaseOrderDetail, String> tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail,
            tblCostDetail, tblROQDetail, tblRequestQuantityDetail, tblOrderQuantityDetail, tblTotalAmountDetail;
    @FXML
    private TableView<ModelPurchaseOrder> tblVwStockRequest;
    @FXML
    private TableColumn<ModelPurchaseOrder, String> tblRowNo, tblBranchName, tblDate, tblReferenceNo, tblNoOfItems;
    @FXML
    private AnchorPane apTableStockRequestLoading, apTableDetailLoading;
    @FXML
    private ProgressIndicator piTableStockRequestLoading, piTableDetailLoading;
    @FXML
    private Pagination pagination;

    @Override
    public void setGRider(GRiderCAS foValue) {
        poApp = foValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            poPurchasingController = new PurchaseOrderControllers(poApp, logWrapper);
            poPurchasingController.PurchaseOrder().setTransactionStatus("01");
            JSONObject loJSON = new JSONObject();
            loJSON = poPurchasingController.PurchaseOrder().InitTransaction();
            if (!"success".equals(loJSON.get("result"))) {
                ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
            }
            poJSON = poPurchasingController.PurchaseOrder().SearchIndustry(poApp.getIndustry(), true);
            if ("error".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);

                return;
            }
            String lsIndustryName = "";
            if (poPurchasingController.PurchaseOrder().Master().Industry().getDescription() != null) {
                lsIndustryName = poPurchasingController.PurchaseOrder().Master().Industry().getDescription();
            }
            tblVwOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
            tfIndustry.setText(lsIndustryName);
            Platform.runLater(() -> btnNew.fire());
            initButtonsClickActions();
            initTextFieldFocus();
            initTextAreaFocus();
            initTextFieldKeyPressed();
            initTextFieldsProperty();
            initCheckBoxActions();
            initDatePickerActions();
            initTextFieldPattern();
            initTableStockRequest();
            initTablePODetail();
            tblVwStockRequest.setOnMouseClicked(this::tblVwStockRequest_Clicked);
            tblVwOrderDetails.setOnMouseClicked(this::tblVwOrderDetails_Clicked);
            initButtons(pnEditMode);
            initFields(pnEditMode);
        } catch (ExceptionInInitializerError | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_EntryMonarchHospitalityController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int moveToNextRow(TableView<?> table, TablePosition<?, ?> focusedCell) {
        if (table.getItems().isEmpty()) {
            return -1; // No movement possible
        }
        int nextRow = (focusedCell.getRow() + 1) % table.getItems().size();
        table.getSelectionModel().select(nextRow);
        return nextRow;
    }

    private int moveToPreviousRow(TableView<?> table, TablePosition<?, ?> focusedCell) {
        if (table.getItems().isEmpty()) {
            return -1; // No movement possible
        }
        int previousRow = (focusedCell.getRow() - 1 + table.getItems().size()) % table.getItems().size();
        table.getSelectionModel().select(previousRow);
        return previousRow;
    }

    private void tableKeyEvents(KeyEvent event) {
        TableView<?> currentTable = (TableView<?>) event.getSource();
        TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
        if (focusedCell != null) {
            if ("tblVwOrderDetails".equals(currentTable.getId())) {
                switch (event.getCode()) {
                    case TAB:
                    case DOWN:
                        pnTblPODetailRow = moveToNextRow(currentTable, focusedCell);
                        break;
                    case UP:
                        pnTblPODetailRow = moveToPreviousRow(currentTable, focusedCell);
                        break;
                    default:
                        return; // Ignore other keys
                }

                loadDetail();
                event.consume();
            }

        }
    }

    private void loadMaster() {
        try {
            tfTransactionNo.setText(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
            String lsStatus = "";
            switch (poPurchasingController.PurchaseOrder().Master().getTransactionStatus()) {
                case PurchaseOrderStatus.OPEN:
                    lsStatus = "OPEN";
                    break;
                case PurchaseOrderStatus.CONFIRMED:
                    lsStatus = "CONFIRMED";
                    break;
                case PurchaseOrderStatus.APPROVED:
                    lsStatus = "APPROVED";
                    break;
                case PurchaseOrderStatus.RETURNED:
                    lsStatus = "RETURNED";
                    break;
                case PurchaseOrderStatus.CANCELLED:
                    lsStatus = "CANCELLED";
                    break;
                case PurchaseOrderStatus.VOID:
                    lsStatus = "VOID";
                    break;
            }

            lblTransactionStatus.setText(lsStatus);
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                    SQLUtil.dateFormat(poPurchasingController.PurchaseOrder().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));

            String lsCompanyName = "";
            if (poPurchasingController.PurchaseOrder().Master().Company().getCompanyName() != null) {
                lsCompanyName = poPurchasingController.PurchaseOrder().Master().Company().getCompanyName();
            }
            tfCompany.setText(lsCompanyName);

            String lsSupplierName = "";
            if (poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName() != null) {
                lsSupplierName = poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName();
            }
            tfSupplier.setText(lsSupplierName);

            String lsDestinationName = "";
            if (poPurchasingController.PurchaseOrder().Master().Branch().getBranchName() != null) {
                lsDestinationName = poPurchasingController.PurchaseOrder().Master().Branch().getBranchName();
            }
            tfDestination.setText(lsDestinationName);
            tfReferenceNo.setText(poPurchasingController.PurchaseOrder().Master().getReference());
            String lsTermCode = "";
            if (poPurchasingController.PurchaseOrder().Master().Term().getDescription() != null) {
                lsTermCode = poPurchasingController.PurchaseOrder().Master().Term().getDescription();
            }
            tfTerm.setText(lsTermCode);

            taRemarks.setText(poPurchasingController.PurchaseOrder().Master().getRemarks());

            dpExpectedDlvrDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                    SQLUtil.dateFormat(poPurchasingController.PurchaseOrder().Master().getExpectedDate(), SQLUtil.FORMAT_SHORT_DATE)));
            tfDiscountRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDiscount()));
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDiscount()));

            if (poPurchasingController.PurchaseOrder().Master().getWithAdvPaym() == true) {
                chkbAdvancePayment.setSelected(true);
            } else {
                chkbAdvancePayment.setSelected(false);
            }
            tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesPercentage()));
            tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesAmount()));
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_EntryMonarchHospitalityController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadDetail() {
        try {
            if (pnTblPODetailRow >= 0) {
                tfBarcode.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getBarCode());
                tfDescription.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getDescription());
                tfBrand.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Brand().getDescription());
                tfModel.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Model().getDescription());
                tfColor.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Color().getDescription());
                tfCategory.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Category().getDescription());
                tfInventoryType.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().InventoryType().getDescription());
                tfMeasure.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Measure().getDescription());
                tfClass.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getInventoryClassification());
                tfAMC.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getAverageCost()));
                tfROQ.setText("0");
                tfRO.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getReceived()));
                tfBO.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getBackOrder()));
                tfQOH.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getQuantityOnHand()));
                tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getCost()));
                tfRequestQuantity.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getQuantity()));
                tfOrderQuantity.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getQuantity()));
            }
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_ConfirmationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel,
                btnPrint, btnRetrieve, btnTransHistory, btnClose);

        buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
    }

    private void handleButtonAction(ActionEvent event) {
        try {
            JSONObject loJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch (lsButton) {
                case "btnBrowse":
                    loJSON = poPurchasingController.PurchaseOrder().searchTransaction("",
                            poPurchasingController.PurchaseOrder().Master().getIndustryID(),
                            poPurchasingController.PurchaseOrder().Master().getCompanyID(),
                            poPurchasingController.PurchaseOrder().Master().getSupplierID(),
                            "");
                    if (!"error".equals((String) loJSON.get("result"))) {
                        loadMaster();
                        loadDetail();
                        loadTablePODetail();
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
                    }
                    break;
                case "btnNew":
                    clearDetailFields();
                    clearMasterFields();
                    poDetail_data.clear();
                    loJSON = poPurchasingController.PurchaseOrder().NewTransaction();
                    poPurchasingController.PurchaseOrder().Master().setIndustryID(poApp.getIndustry());
                    poPurchasingController.PurchaseOrder().Master().setDestinationID(poPurchasingController.PurchaseOrder().Master().Branch().getBranchCode());
                    if ("success".equals((String) loJSON.get("result"))) {
                        loadMaster();
                        loadDetail();
                        loadTablePODetail();
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                    }
                    break;
                case "btnUpdate":
                    loJSON = poPurchasingController.PurchaseOrder().UpdateTransaction();
                    pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    if ("error".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                    }
                    loadTablePODetail();
                    break;
                case "btnSearch":
                    if (activeField != null) {
                        JSONObject poJSON = new JSONObject();

                        String loTextFieldId = activeField.getId().toString();
                        String lsValue = activeField.getText().trim();
                        switch (loTextFieldId) {
                            case "tfCompany":
                                poJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfCompany.setText("");
                                    break;
                                }
                                tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
                                tfCompany.requestFocus();
                                break;
                            case "tfSupplier":
                                poJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfSupplier.setText("");
                                    break;
                                }
                                tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                tfSupplier.requestFocus();
                                break;
                            case "tfDestination":
                                poJSON = poPurchasingController.PurchaseOrder().SearchDestination(lsValue, false);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfDestination.setText("");
                                    break;
                                }
                                tfDestination.setText(poPurchasingController.PurchaseOrder().Master().Branch().getBranchName());
                                tfDestination.requestFocus();
                                break;
                            case "tfTerm":
                                poJSON = poPurchasingController.PurchaseOrder().SearchTerm(lsValue, false);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfTerm.setText("");
                                    break;
                                }
                                tfTerm.setText(poPurchasingController.PurchaseOrder().Master().Term().getDescription());
                                tfTerm.requestFocus();
                                break;
                            case "tfBarcode":
                                if (pnTblPODetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                poJSON = poPurchasingController.PurchaseOrder().SearchBrand(lsValue, false, pnTblPODetailRow);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfBarcode.setText("");
                                    break;
                                }
                                tfBarcode.setText(poPurchasingController.PurchaseOrder().Master().Inventory().getBarCode());

                                tfOrderQuantity.requestFocus();
                                loadDetail();
                                loadTablePODetail();
                                break;
                            case "tfDescription":
                                if (pnTblPODetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                poJSON = poPurchasingController.PurchaseOrder().SearchBarcodeDescription(lsValue, false, pnTblPODetailRow);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfDescription.setText("");
                                    break;
                                }
                                tfDescription.setText(poPurchasingController.PurchaseOrder().Master().Inventory().getDescription());

                                tfOrderQuantity.requestFocus();
                                loadDetail();
                                loadTablePODetail();
                                break;

                            default:
                                System.out.println("Unknown TextField");
                        }
                        loadTableStockRequest();

                    }
                    break;
                case "btnSave":

                    if (!ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to save?")) {
                        return;
                    }

                    // Validate Detail Count Before Backend Processing
                    int detailCount = poPurchasingController.PurchaseOrder().getDetailCount();
                    boolean hasValidItem = false; // True if at least one valid item exists

                    if (detailCount == 0) {
                        ShowMessageFX.Warning("Your order is empty. Please add at least one item.", psFormName, null);
                        return;
                    }

                    for (int lnCntr = 0; lnCntr <= detailCount - 1; lnCntr++) {
                        int quantity = (int) poPurchasingController.PurchaseOrder().Detail(lnCntr).getValue("nQuantity");
                        String stockID = (String) poPurchasingController.PurchaseOrder().Detail(lnCntr).getValue("sStockIDx");

                        // If any stock ID is empty OR quantity is 0, show an error and prevent saving
                        if (detailCount == 1) {
                            if (stockID == null || stockID.trim().isEmpty() || quantity == 0) {
                                ShowMessageFX.Warning("Invalid item in order. Ensure all items have a valid Stock ID and quantity greater than 0.", psFormName, null);
                                return;
                            }
                        }

                        hasValidItem = true;
                    }

                    // If no valid items exist, prevent saving
                    if (!hasValidItem) {
                        ShowMessageFX.Warning("Your order must have at least one valid item with a Stock ID and quantity greater than 0.", psFormName, null);
                        return;
                    }

                    // Backend validations for EditMode
                    if (pnEditMode == EditMode.UPDATE && (poPurchasingController.PurchaseOrder().Master().getTransactionStatus().equals(PurchaseOrderStatus.CONFIRMED))) {
                        if (!"success".equals((loJSON = ShowDialogFX.getUserApproval(poApp)).get("result"))) {
                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                            return;
                        }
                    }

                    // Assign modification details for Update Mode
                    if (pnEditMode == EditMode.UPDATE) {
                        poPurchasingController.PurchaseOrder().Master().setModifiedDate(poApp.getServerDate());
                        poPurchasingController.PurchaseOrder().Master().setModifyingId(poApp.getUserID());
                    }

                    // Assign modification date to all details
                    for (int lnCntr = 0; lnCntr < detailCount; lnCntr++) {
                        poPurchasingController.PurchaseOrder().Detail(lnCntr).setModifiedDate(poApp.getServerDate());
                    }

                    // Save Transaction
                    if (!"success".equals((loJSON = poPurchasingController.PurchaseOrder().SaveTransaction()).get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        loadTablePODetail();
                        return;
                    }

                    ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                    loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());

                    // Confirmation Prompt
                    if ("success".equals(loJSON.get("result")) && poPurchasingController.PurchaseOrder().Master().getTransactionStatus().equals(PurchaseOrderStatus.OPEN)
                            && ShowMessageFX.YesNo(null, psFormName, "Do you want to confirm this transaction?")) {
                        if ("success".equals((loJSON = poPurchasingController.PurchaseOrder().ConfirmTransaction("Confirmed")).get("result"))) {
                            ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                        }
                    }

                    // Print Transaction Prompt
                    if (ShowMessageFX.YesNo(null, psFormName, "Do you want to print this transaction?")) {
                        loJSON = poPurchasingController.PurchaseOrder().printTransaction();
                        if (!"success".equals((String) loJSON.get("result"))) {
                            ShowMessageFX.Warning((String) loJSON.get("message"), "Print Purchase Order", null);
                        }
                    }

                    Platform.runLater(() -> btnNew.fire());

                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo(null, "Cancel Confirmation", "Are you sure you want to cancel?")) {
                        if (pnEditMode == EditMode.ADDNEW) {
                            clearDetailFields();
                            clearMasterFields();
                            poDetail_data.clear();
                            pnEditMode = EditMode.UNKNOWN;
                            loJSON = poPurchasingController.PurchaseOrder().SearchIndustry(poApp.getIndustry(), true);
                            if ("error".equals((String) loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                return;
                            }
                            String lsIndustryName = "";
                            if (poPurchasingController.PurchaseOrder().Master().Industry().getDescription() != null) {
                                lsIndustryName = poPurchasingController.PurchaseOrder().Master().Industry().getDescription();
                            }
                            tfIndustry.setText(lsIndustryName);
                            String lsCompanyName = "";
                            if (poPurchasingController.PurchaseOrder().Master().Company().getCompanyName() != null) {
                                lsCompanyName = poPurchasingController.PurchaseOrder().Master().Company().getCompanyName();
                            }
                            tfCompany.setText(lsCompanyName);

                            String lsSupplierName = "";
                            if (poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName() != null) {
                                lsSupplierName = poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName();
                            }
                            tfSupplier.setText(lsSupplierName);
                        } else {
                            loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                            if ("success".equals((String) loJSON.get("result"))) {
                                loadMaster();
                                loadDetail();
                                loadTablePODetail();
                                pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                            }
                        }
                    }
                    if (pnTblStockRequestRow >= 0) {
                        tblVwStockRequest.refresh();
                        poApprovedStockRequest_data.get(pnTblStockRequestRow).setIndex07(PurchaseOrderStatus.OPEN);
                    }
                    break;
                case "btnPrint":
                    loJSON = poPurchasingController.PurchaseOrder().printTransaction();
                    if ("error".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                    }
                    break;
                case "btnRetrieve":
                    if (poPurchasingController.PurchaseOrder().Master().getIndustryID().equals("")) {
                        ShowMessageFX.Warning("Invalid to retrieve stock request, industy is empty.", psFormName, null);
                        return;
                    }
                    if (tfCompany.getText().isEmpty()) {
                        ShowMessageFX.Warning("Invalid to retrieve stock request, company is empty.", psFormName, null);
                        return;
                    }
                    if (tfSupplier.getText().isEmpty()) {
                        ShowMessageFX.Warning("Invalid to retrieve stock request, supplier is empty.", psFormName, null);
                        return;
                    }
                    loadTableStockRequest();
                    break;
                case "btnTransHistory":
                    break;
                case "btnClose":
                    if (ShowMessageFX.YesNo("Are you sure you want to close this form?", psFormName, null)) {
                        if (poUnload != null) {
                            poUnload.unloadForm(AnchorMain, poApp, psFormName);
                        } else {
                            ShowMessageFX.Warning("Please notify the system administrator to configure the null value at the close button.", "Warning", null);
                        }
                    }
                    break;
                default:
                    ShowMessageFX.Warning("Please contact admin to assist about no button available", psFormName, null);
                    break;
            }
            initButtons(pnEditMode);
            initFields(pnEditMode);
        } catch (CloneNotSupportedException | ExceptionInInitializerError | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(PurchaseOrder_EntryMonarchHospitalityController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextFieldFocus() {
        List<TextField> loTxtField = Arrays.asList(tfReferenceNo, tfDiscountRate, tfDiscountAmount,
                tfAdvancePRate, tfAdvancePAmount, tfCost, tfOrderQuantity);
        loTxtField.forEach(tf -> tf.focusedProperty().addListener(txtField_Focus));

        tfBrand.setOnMouseClicked(e -> activeField = tfBrand);
        tfModel.setOnMouseClicked(e -> activeField = tfModel);
        tfIndustry.setOnMouseClicked(e -> activeField = tfIndustry);
        tfCompany.setOnMouseClicked(e -> activeField = tfCompany);
        tfSupplier.setOnMouseClicked(e -> activeField = tfSupplier);
        tfDestination.setOnMouseClicked(e -> activeField = tfDestination);
        tfTerm.setOnMouseClicked(e -> activeField = tfTerm);
    }

    private void initTextAreaFocus() {
        taRemarks.focusedProperty().addListener(txtArea_Focus);
    }
    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        TextField loTextField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextFieldID = loTextField.getId();
        String lsValue = loTextField.getText();
        double lnGrandTotal = Double.parseDouble(tfTotalAmount.getText().replace(",", ""));
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTextFieldID) {
                case "tfReferenceNo":
                    poPurchasingController.PurchaseOrder().Master().setReference(lsValue);
                    break;
                case "tfDiscountRate":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue) < 0.00 || Double.parseDouble(lsValue) > 100.00) {
                        ShowMessageFX.Warning("Invalid Discount Rate", psFormName, null);
                        return;
                    }
                    tfDiscountRate.setText(lsValue);
                    break;
                case "tfDiscountAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue) < 0.00) {
                        ShowMessageFX.Warning("Invalid Discount Amount", psFormName, null);
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Master().setDiscount(Double.valueOf(lsValue));
                    tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfAdvancePRate":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue) < 0.00 || Double.parseDouble(lsValue) > 100) {
                        ShowMessageFX.Warning("Invalid Advance Downpayment Rates", psFormName, null);
                        lsValue = "0.00";
                    }
                    double lnAdvPercentageR = Double.parseDouble(lsValue.replace(",", ""));
                    double lnAmountR = (lnAdvPercentageR / 100) * lnGrandTotal;

                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesPercentage(lnAdvPercentageR);
                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesAmount(lnAmountR);
                    tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnAdvPercentageR));
                    tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnAmountR));

                    break;
                case "tfAdvancePAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.0) {
                        ShowMessageFX.Warning("Invalid Advance Downpayment Amount", psFormName, null);
                        lsValue = "0.00";
                    }
                    double lnAmountA = Double.parseDouble(lsValue.replace(",", ""));
                    double lnAdvPercentageA = (lnAmountA / lnGrandTotal) * 100;

                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesPercentage(lnAdvPercentageA);
                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesAmount(lnAmountA);
                    tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnAdvPercentageA));
                    tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnAmountA));

                    break;
                case "tfOrderQuantity":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    if (Integer.parseInt(lsValue) < 0) {
                        ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);
                        lsValue = "0";
                    }
                    if (pnTblPODetailRow >= 0) {
                        poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).setQuantity(Integer.valueOf(lsValue));
                    } else {
                        lsValue = "0";
                        ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                        clearDetailFields();
                    }
                    tfOrderQuantity.setText(lsValue);
                    loadTablePODetail();
                    break;
            }
        } else {
            loTextField.selectAll();
        }
    };
    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea loTextArea = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextAreaID = loTextArea.getId();
        String lsValue = loTextArea.getText();
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTextAreaID) {
                case "taRemarks":
                    poPurchasingController.PurchaseOrder().Master().setRemarks(lsValue);
                    break;
            }
        } else {
            loTextArea.selectAll();
        }
    };

    private void initTextFieldKeyPressed() {
        List<TextField> loTxtField = Arrays.asList(tfAdvancePAmount, tfCompany, tfSupplier,
                tfReferenceNo, tfTerm, tfDiscountRate, tfDiscountAmount, tfTotalAmount,
                tfDestination, tfAdvancePRate,
                tfBarcode, tfDescription,
                tfBO, tfRO,
                tfCost, tfOrderQuantity);

        loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
    }

    private void txtField_KeyPressed(KeyEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            TextField lsTxtField = (TextField) event.getSource();
            String txtFieldID = ((TextField) event.getSource()).getId();
            String lsValue = "";
            if (lsTxtField.getText() == null) {
                lsValue = "";
            } else {
                lsValue = lsTxtField.getText();
            }
            JSONObject loJSON = new JSONObject();
            try {
                if (null != event.getCode()) {
                    switch (event.getCode()) {
                        case TAB:
                        case ENTER:
                        case F3:
                            switch (txtFieldID) {
                                case "tfCompany":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        tfCompany.setText("");
                                        break;
                                    }
                                    tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
                                    break;
                                case "tfSupplier":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        tfSupplier.setText("");
                                        break;
                                    }
                                    tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                    break;
                                case "tfDestination":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchDestination(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        tfDestination.setText("");
                                        break;
                                    }
                                    tfDestination.setText(poPurchasingController.PurchaseOrder().Master().Branch().getBranchName());
                                    break;
                                case "tfTerm":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchTerm(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        tfTerm.setText("");
                                        break;
                                    }
                                    tfTerm.setText(poPurchasingController.PurchaseOrder().Master().Term().getDescription());
                                    break;
                                case "tfBarcode":
                                    if (pnTblPODetailRow < 0) {
                                        ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                        clearDetailFields();
                                        break;
                                    }
                                    loJSON = poPurchasingController.PurchaseOrder().SearchBarcode(lsValue, false, pnTblPODetailRow);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        tfBarcode.setText("");
                                        break;
                                    }
                                    if (pnTblPODetailRow >= 0) {
                                        tfBarcode.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getBarCode());
                                    }

                                    tfOrderQuantity.requestFocus();

                                    loadDetail();
                                    loadTablePODetail();
                                    break;
                                case "tfDescription":
                                    if (pnTblPODetailRow < 0) {
                                        ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                        clearDetailFields();
                                        break;
                                    }
                                    loJSON = poPurchasingController.PurchaseOrder().SearchBarcodeDescription(lsValue, false, pnTblPODetailRow);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        tfDescription.setText("");
                                        break;
                                    }
                                    if (pnTblPODetailRow >= 0) {
                                        tfDescription.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getDescription());
                                    } else {
                                        ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                        tfDescription.setText("");
                                    }
                                    tfOrderQuantity.requestFocus();
                                    loadDetail();
                                    loadTablePODetail();
                                    break;
                            }
                            loadTableStockRequest();
                            event.consume();
                            switch (txtFieldID) {
                                case "tfCompany":
                                case "tfSupplier":
                                case "tfDestination":
                                case "tfTerm":
                                case "tfAdvancePAmount":
                                case "tfAdvancePRate":
                                case "tfDiscountRate":
                                case "tfDiscountAmount":
                                case "tfOrderQuantity":
                                    CommonUtils.SetNextFocus((TextField) event.getSource());
                                    break;
                            }
                            break;
                        case UP:
                            event.consume();
                            CommonUtils.SetPreviousFocus((TextField) event.getSource());
                            break;
                        case DOWN:
                            event.consume();
                            CommonUtils.SetNextFocus((TextField) event.getSource());
                            break;
                        default:
                            break;

                    }
                }
            } catch (ExceptionInInitializerError | SQLException | CloneNotSupportedException | GuanzonException ex) {
                Logger.getLogger(PurchaseOrder_EntryMonarchHospitalityController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initTextFieldPattern() {
        CustomCommonUtil.inputDecimalOnly(tfDiscountRate, tfDiscountAmount, tfAdvancePRate,
                tfAdvancePAmount, tfCost);
        CustomCommonUtil.inputIntegersOnly(tfOrderQuantity);
    }

    private void initDatePickerActions() {
        dpTransactionDate.setOnAction(e -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (dpTransactionDate.getValue() != null) {
                    poPurchasingController.PurchaseOrder().Master().setTransactionDate(SQLUtil.toDate(dpTransactionDate.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
                }
            }
        });
        dpExpectedDlvrDate.setOnAction(e -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (dpExpectedDlvrDate.getValue() != null) {
                    poPurchasingController.PurchaseOrder().Master().setExpectedDate(SQLUtil.toDate(dpExpectedDlvrDate.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
                }
            }
        });
    }

    private void initCheckBoxActions() {
        chkbAdvancePayment.setOnAction(event -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (tfTotalAmount.getText().isEmpty()
                        || Double.parseDouble(tfTotalAmount.getText().replace(",", "")) > 0.00
                        || Double.parseDouble(tfTotalAmount.getText().replace(",", "")) > 0.0) {
                    if (chkbAdvancePayment.isSelected()) {
                        chkbAdvancePayment.setSelected(true);
                        poPurchasingController.PurchaseOrder().Master().setWithAdvPaym(true);
                    } else {
                        poPurchasingController.PurchaseOrder().Master().setWithAdvPaym(false);
                        chkbAdvancePayment.setSelected(false);
                    }
                } else {
                    ShowMessageFX.Warning("Advance payment cannot be entered until the total amount is greater than 0.00.", psFormName, null);
                    poPurchasingController.PurchaseOrder().Master().setWithAdvPaym(false);
                    chkbAdvancePayment.setSelected(false);
                }
                initFields(pnEditMode);
            }
        });
    }

    private void clearMasterFields() {
        /* Master Fields*/
        pnTblPODetailRow = -1;
        dpTransactionDate.setValue(null);
        dpExpectedDlvrDate.setValue(null);
        taRemarks.setText("");
        CustomCommonUtil.setSelected(false, chkbAdvancePayment);
        CustomCommonUtil.setText("", tfTransactionNo, tfCompany, tfSupplier,
                tfDestination, tfReferenceNo, tfTerm, tfDiscountRate,
                tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount);
    }

    private void clearDetailFields() {
        /* Detail Fields*/
        CustomCommonUtil.setText("", tfBarcode, tfDescription, tfBrand, tfModel,
                tfColor, tfCategory, tfInventoryType, tfMeasure, tfClass,
                tfAMC, tfROQ, tfRO, tfBO, tfQOH,
                tfCost, tfRequestQuantity, tfOrderQuantity);
    }

    private void initButtons(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        CustomCommonUtil.setVisible(!lbShow, btnBrowse, btnClose, btnNew);
        CustomCommonUtil.setManaged(!lbShow, btnBrowse, btnClose, btnNew);

        CustomCommonUtil.setVisible(lbShow, btnSearch, btnSave, btnCancel);
        CustomCommonUtil.setManaged(lbShow, btnSearch, btnSave, btnCancel);

        CustomCommonUtil.setVisible(false, btnUpdate, btnPrint);
        CustomCommonUtil.setManaged(false, btnUpdate, btnPrint);

        btnTransHistory.setVisible(fnEditMode != EditMode.ADDNEW && fnEditMode != EditMode.UNKNOWN);
        btnTransHistory.setManaged(fnEditMode != EditMode.ADDNEW && fnEditMode != EditMode.UNKNOWN);
        if (poPurchasingController.PurchaseOrder().Master().getPrint().equals("1")) {
            btnPrint.setText("Reprint");
        } else {
            btnPrint.setText("Print");
        }
        if (fnEditMode == EditMode.READY) {
            switch (poPurchasingController.PurchaseOrder().Master().getTransactionStatus()) {
                case PurchaseOrderStatus.OPEN:
                case PurchaseOrderStatus.CONFIRMED:
                case PurchaseOrderStatus.RETURNED:
                    CustomCommonUtil.setVisible(true, btnPrint, btnUpdate);
                    CustomCommonUtil.setManaged(true, btnPrint, btnUpdate);
                    break;
                case PurchaseOrderStatus.APPROVED:
                    btnPrint.setVisible(true);
                    btnPrint.setManaged(true);
                    break;
            }

        }
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
        /*Master Fields */
        CustomCommonUtil.setDisable(!lbShow,
                dpTransactionDate, tfDestination, taRemarks,
                dpExpectedDlvrDate, tfReferenceNo, tfTerm,
                chkbAdvancePayment);
        CustomCommonUtil.setDisable(!lbShow,
                tfBarcode, tfDescription, tfOrderQuantity);

        CustomCommonUtil.setDisable(true, tfDiscountRate, tfDiscountAmount,
                tfAdvancePRate, tfAdvancePAmount);
        if (!tfReferenceNo.getText().isEmpty()) {
            dpTransactionDate.setDisable(!lbShow);
        }
        if (chkbAdvancePayment.isSelected()) {
            CustomCommonUtil.setDisable(!lbShow, tfAdvancePRate, tfAdvancePAmount);
        }
        if (tblVwStockRequest.getItems().isEmpty()) {
            pagination.setVisible(false);
            pagination.setManaged(false);
        }
        CustomCommonUtil.setVisible(false, piTableDetailLoading, piTableStockRequestLoading, apTableDetailLoading, apTableStockRequestLoading);
        CustomCommonUtil.setManaged(false, piTableDetailLoading, piTableStockRequestLoading, apTableDetailLoading, apTableStockRequestLoading);
    }

    private void loadTableStockRequest() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50); // Set size to 200x200
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER); // Center it

        tblVwStockRequest.setPlaceholder(loadingPane); // Show while loading
        progressIndicator.setVisible(true); // Make sure it's visible
        progressIndicator.setManaged(true); // Make sure it's visible

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Simulate loading delay
                    poApprovedStockRequest_data.clear();
                    JSONObject poJSON = poPurchasingController.PurchaseOrder().getApprovedStockRequests();

                    if ("success".equals(poJSON.get("result"))) {
                        JSONArray approvedRequests = (JSONArray) poJSON.get("data");
                        poApprovedStockRequest_data.clear();  // Ensure old data is removed

                        if (approvedRequests != null && !approvedRequests.isEmpty()) {
                            for (Object requestObj : approvedRequests) {
                                JSONObject obj = (JSONObject) requestObj;
                                ModelPurchaseOrder loApprovedStockRequest = new ModelPurchaseOrder(
                                        String.valueOf(poApprovedStockRequest_data.size() + 1),
                                        obj.get("sBranchNm") != null ? obj.get("sBranchNm").toString() : "",
                                        obj.get("dTransact") != null ? obj.get("dTransact").toString() : "",
                                        obj.get("sReferNox") != null ? obj.get("sReferNox").toString() : "",
                                        obj.get("total_details") != null ? obj.get("total_details").toString() : "",
                                        obj.get("sTransNox") != null ? obj.get("sTransNox").toString() : "",
                                        "0",
                                        "",
                                        "",
                                        ""
                                );
                                poApprovedStockRequest_data.add(loApprovedStockRequest);
                            }
                        } else {
                            // Ensure poApprovedStockRequest_data remains empty
                            poApprovedStockRequest_data.clear();
                        }
                    }

                    Platform.runLater(() -> {
                        if (poApprovedStockRequest_data.isEmpty()) {
                            tblVwStockRequest.setPlaceholder(new Label("NO RECORD TO LOAD"));
                            tblVwStockRequest.setItems(FXCollections.observableArrayList(poApprovedStockRequest_data));
                        } else {
                            tblVwStockRequest.setItems(FXCollections.observableArrayList(poApprovedStockRequest_data));
                        }
                    });

                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                if (poApprovedStockRequest_data == null || poApprovedStockRequest_data.isEmpty()) {
                    tblVwStockRequest.setPlaceholder(new Label("NO RECORD TO LOAD"));
                } else {
                    if (pagination != null) {
                        pagination.setPageCount((int) Math.ceil((double) poApprovedStockRequest_data.size() / pnSTOCK_REQUEST_PAGE));
                        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
                            createPage(newIndex.intValue());
                        });
                    }
                    createPage(0);
                    pagination.setVisible(true);
                    pagination.setManaged(true);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    tblVwStockRequest.toFront();
                }
            }

            @Override
            protected void failed() {
                pagination.setVisible(true);
                pagination.setManaged(true);
                progressIndicator.setVisible(false);
                progressIndicator.setManaged(false);
                tblVwStockRequest.toFront();
            }
        };
        new Thread(task).start(); // Run task in background
    }

    private Node createPage(int pageIndex) {
        int totalPages = (int) Math.ceil((double) poApprovedStockRequest_data.size() / pnSTOCK_REQUEST_PAGE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        pageIndex = Math.max(0, Math.min(pageIndex, totalPages - 1));
        int fromIndex = pageIndex * pnSTOCK_REQUEST_PAGE;
        int toIndex = Math.min(fromIndex + pnSTOCK_REQUEST_PAGE, poApprovedStockRequest_data.size());

        if (!poApprovedStockRequest_data.isEmpty()) {
            tblVwStockRequest.setItems(FXCollections.observableArrayList(poApprovedStockRequest_data.subList(fromIndex, toIndex)));
        }

        if (pagination != null) { // Replace with your actual Pagination variable
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(pageIndex);
        }

        return tblVwStockRequest;
    }

    private void initTableStockRequest() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            tblVwStockRequest.setEditable(true);
        } else {
            tblVwStockRequest.setEditable(false);
        }
        // Set cell value factories
        tblRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblBranchName.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblDate.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblReferenceNo.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblNoOfItems.setCellValueFactory(new PropertyValueFactory<>("index05"));

        tblVwStockRequest.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblVwStockRequest.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        initTableHighlithers();
    }

    private void initTableHighlithers() {
        tblVwStockRequest.setRowFactory(tv -> new TableRow<ModelPurchaseOrder>() {
            @Override
            protected void updateItem(ModelPurchaseOrder item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    // Assuming empIndex05 corresponds to an employee status
                    String status = item.getIndex07(); // Replace with actual getter
                    switch (status) {
                        case PurchaseOrderStatus.CONFIRMED:
                            setStyle("-fx-background-color: #A7C7E7;");
                            break;
                        default:
                            setStyle("");
                    }
                    tblVwStockRequest.refresh();
                }
            }
        });
    }

    private void loadTablePODetail() {
        // Configure ProgressIndicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);
        progressIndicator.setStyle("-fx-accent: #FF8201;");

        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setStyle("-fx-background-color: transparent;");

        tblVwOrderDetails.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                poDetail_data.clear();
                try {
                    if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                        int lnCntr;
                        lnCntr = poPurchasingController.PurchaseOrder().getDetailCount() - 1;
                        while (lnCntr > 0) {
                            if (poPurchasingController.PurchaseOrder().Detail(lnCntr).getStockID() == null
                                    || poPurchasingController.PurchaseOrder().Detail(lnCntr).getStockID().equals("")) {
                                poPurchasingController.PurchaseOrder().Detail().remove(lnCntr);
                            }
                            lnCntr--;
                        }
                        if ((poPurchasingController.PurchaseOrder().getDetailCount() - 1) >= 0) {
                            if (poPurchasingController.PurchaseOrder().Detail(poPurchasingController.PurchaseOrder().getDetailCount() - 1).getStockID() != null
                                    && !poPurchasingController.PurchaseOrder().Detail(poPurchasingController.PurchaseOrder().getDetailCount() - 1).getStockID().equals("")) {
                                poPurchasingController.PurchaseOrder().AddDetail();
                            }
                        }
                    }

                    double grandTotalAmount = 0.0;
                    for (int lnCtr = 0; lnCtr <= poPurchasingController.PurchaseOrder().getDetailCount() - 1; lnCtr++) {
                        double lnTotalAmount = poPurchasingController.PurchaseOrder()
                                .Detail(lnCtr)
                                .Inventory().getCost().doubleValue() * poPurchasingController.PurchaseOrder()
                                        .Detail(lnCtr)
                                        .getQuantity().doubleValue();
                        grandTotalAmount += lnTotalAmount;
                        poDetail_data.add(new ModelPurchaseOrderDetail(
                                String.valueOf(lnCtr + 1),
                                poPurchasingController.PurchaseOrder().Detail(lnCtr).getSouceNo(),
                                poPurchasingController.PurchaseOrder().Detail(lnCtr).Inventory().getBarCode(),
                                poPurchasingController.PurchaseOrder().Detail(lnCtr).Inventory().getDescription(),
                                CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(lnCtr).Inventory().getCost()),
                                "",
                                String.valueOf(poPurchasingController.PurchaseOrder().Detail(lnCtr).InvStockRequestDetail().getQuantity()),
                                String.valueOf(poPurchasingController.PurchaseOrder().Detail(lnCtr).getQuantity()),
                                CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotalAmount),
                                ""
                        ));

                    }
                    tblVwOrderDetails.setItems(poDetail_data);
                    computeTotalAmount(grandTotalAmount);
                    poPurchasingController.PurchaseOrder().Master().setTranTotal(grandTotalAmount);
                    tfTotalAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(grandTotalAmount));

                } catch (GuanzonException | SQLException ex) {
                    Logger.getLogger(PurchaseOrder_EntryController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override

            protected void succeeded() {
                tblVwOrderDetails.setItems(poDetail_data);
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
            }
        };

        if (!poApprovedStockRequest_data.isEmpty()) {
            if (!poDetail_data.isEmpty()) {
                for (ModelPurchaseOrderDetail detail : poDetail_data) {
                    String listPODetail = detail.getIndex02();

                    for (ModelPurchaseOrder master : poApprovedStockRequest_data) {
                        if (listPODetail.equals(master.getIndex06())) {
                            if (!master.getIndex07().equals(PurchaseOrderStatus.CONFIRMED)) {
                                master.setIndex07(PurchaseOrderStatus.CONFIRMED);
                            }
                            break; // Exit inner loop once matched
                        }
                    }
                }
            } else {
                for (ModelPurchaseOrder master : poApprovedStockRequest_data) {
                    master.setIndex07(PurchaseOrderStatus.OPEN);
                }
            }
            tblVwStockRequest.refresh(); // Refresh only once after updates
        }
        new Thread(task).start(); // Run task in background
    }

    private void initTextFieldsProperty() {
        tfCompany.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poPurchasingController.PurchaseOrder().Master().setCompanyID("");
                    tfCompany.setText("");
                    loadTableStockRequest();
                }

            }
        });
        tfSupplier.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poPurchasingController.PurchaseOrder().Master().setSupplierID("");
                    poPurchasingController.PurchaseOrder().Master().setAddressID("");
                    poPurchasingController.PurchaseOrder().Master().setContactID("");
                    tfSupplier.setText("");
                    loadTableStockRequest();
                }
            }
        });
    }

    private void computeTotalAmount(double fnGrandTotal) {
        double amount = (Double.parseDouble(tfAdvancePRate.getText().replace(",", "")) / 100) * fnGrandTotal;
        tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(amount));
        double advpercentage = (Double.parseDouble(tfAdvancePAmount.getText().replace(",", "")) / fnGrandTotal) * 100;
        tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(advpercentage));
        poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesPercentage(advpercentage);
        poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesAmount(amount);

    }

    private void initTablePODetail() {
        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblOrderNoDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblBarcodeDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblDescriptionDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblCostDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblROQDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblRequestQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblOrderQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index08"));
        tblTotalAmountDetail.setCellValueFactory(new PropertyValueFactory<>("index09"));

        tblVwOrderDetails.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblVwOrderDetails.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
    }

    private void tblVwStockRequest_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            pnTblStockRequestRow = tblVwStockRequest.getSelectionModel().getSelectedIndex();
            if (event.getClickCount() == 2) {
                ModelPurchaseOrder loSelectedStockRequest = (ModelPurchaseOrder) tblVwStockRequest.getSelectionModel().getSelectedItem();
                if (loSelectedStockRequest != null) {
                    String lsTransactionNo = loSelectedStockRequest.getIndex06();
                    try {
                        JSONObject loJSON = poPurchasingController.PurchaseOrder().addStockRequestOrdersToPODetail(lsTransactionNo);
                        if ("success".equals(loJSON.get("result"))) {
                            if (poPurchasingController.PurchaseOrder().getDetailCount() > 0) {
                                loadTablePODetail();
                                tblVwStockRequest.refresh();
                                poApprovedStockRequest_data.get(pnTblStockRequestRow).setIndex07(PurchaseOrderStatus.CONFIRMED);
                            }
                        } else {
                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);

                        }
                    } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                        Logger.getLogger(PurchaseOrder_EntryMonarchHospitalityController.class
                                .getName()).log(Level.SEVERE, null, ex);
                        ShowMessageFX.Warning("Error loading data: " + ex.getMessage(), psFormName, null);
                    }
                }

            }
        }
    }

    private void tblVwOrderDetails_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.READY) {
            pnTblPODetailRow = tblVwOrderDetails.getSelectionModel().getSelectedIndex();
            ModelPurchaseOrderDetail selectedItem = tblVwOrderDetails.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) {
                clearDetailFields();
                if (selectedItem != null) {
                    if (pnTblPODetailRow >= 0) {
                        loadDetail();
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            if (!tfBarcode.getText().isEmpty()) {
                                tfOrderQuantity.requestFocus();
                            } else {
                                tfBarcode.requestFocus();
                            }
                        }
                    }
                }
            }
        }
    }
}
