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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
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
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.model.Model_PO_Detail;
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
public class PurchaseOrder_EntryMPController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order MP";
    private LogWrapper logWrapper;
    private int pnEditMode;
    private JSONObject poJSON;
    unloadForm poUnload = new unloadForm();
    private boolean isNewUpdate = false;
    private ObservableList<ModelPurchaseOrder> poApprovedStockRequest_data = FXCollections.observableArrayList();
    private ObservableList<ModelPurchaseOrderDetail> poDetail_data = FXCollections.observableArrayList();
    private int pnTblStockRequestRow = -1;
    private int pnTblPODetailRow = -1;
    private int pnSTOCK_REQUEST_PAGE = 50;
    private String prevCompany = "";
    private String prevSupplier = "";
    private TextField activeField;
    private String psIndustryID = "";
    private String psCompanyID = "";
    @FXML
    private AnchorPane AnchorMaster, AnchorDetails, AnchorMain, apBrowse, apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel,
            btnPrint, btnRetrieve, btnTransHistory, btnClose;
    @FXML
    private TextField tfTransactionNo, tfSupplier, tfDestination, tfReferenceNo,
            tfTerm, tfDiscountRate, tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount;
    @FXML
    private Label lblTransactionStatus, lblSource;
    @FXML
    private CheckBox chkbAdvancePayment;
    @FXML
    private DatePicker dpTransactionDate, dpExpectedDlvrDate;
    @FXML
    private TextField tfBrand, tfModel, tfVariant, tfInventoryType, tfColor, tfClass, tfAMC, tfROQ,
            tfRO, tfBO, tfQOH, tfCost, tfRequestQuantity, tfOrderQuantity;
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

    @Override
    public void setIndustryID(String fsValue) {
        psIndustryID = fsValue;
    }

    @Override
    public void setCompanyID(String fsValue) {
        psCompanyID = fsValue;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            poPurchasingController = new PurchaseOrderControllers(poApp, logWrapper);
            poPurchasingController.PurchaseOrder().setTransactionStatus("017");
            JSONObject loJSON = new JSONObject();
            loJSON = poPurchasingController.PurchaseOrder().InitTransaction();
            if (!"success".equals(loJSON.get("result"))) {
                ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
            }

            tblVwOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
            Platform.runLater((() -> {
                poPurchasingController.PurchaseOrder().Master().setIndustryID(psIndustryID);
                poPurchasingController.PurchaseOrder().Master().setCompanyID(psCompanyID);
                loadRecordSearch();
            }));
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
        } catch (ExceptionInInitializerError ex) {
            Logger.getLogger(PurchaseOrder_EntryMPController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadRecordSearch() {
        try {
            lblSource.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName() + " - " + poPurchasingController.PurchaseOrder().Master().Industry().getDescription());
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_EntryMPController.class.getName()).log(Level.SEVERE, null, ex);
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

        if (focusedCell != null && "tblVwOrderDetails".equals(currentTable.getId())) {
            switch (event.getCode()) {
                case TAB:
                case DOWN:
                    pnTblPODetailRow = pnTblPODetailRow;
                    if (pnEditMode != EditMode.ADDNEW || pnEditMode != EditMode.UPDATE) {
                        pnTblPODetailRow = moveToNextRow(currentTable, focusedCell);
                    }
                    break;
                case UP:
                    pnTblPODetailRow = pnTblPODetailRow;
                    if (pnEditMode != EditMode.ADDNEW || pnEditMode != EditMode.UPDATE) {
                        pnTblPODetailRow = moveToPreviousRow(currentTable, focusedCell);
                    }
                    break;
                default:
                    return;
            }
            currentTable.getSelectionModel().select(pnTblPODetailRow);
            currentTable.getFocusModel().focus(pnTblPODetailRow);
            loadDetail();
            initDetailFocus();
            event.consume();
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
//            tfCompany.setText(lsCompanyName);

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
            Logger.getLogger(PurchaseOrder_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadDetail() {
        try {
            if (pnTblPODetailRow >= 0) {
                String lsBrand = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Brand().getDescription() != null) {
                    lsBrand = poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Brand().getDescription();
                }
                tfBrand.setText(lsBrand);

                String lsModel = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Model().getDescription() != null) {
                    lsModel = poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Model().getDescription();
                }
                tfModel.setText(lsModel);

                String lsVariant = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Variant().getDescription() != null) {
                    lsVariant = poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Variant().getDescription();
                }
                tfVariant.setText(lsVariant);

                String lsInventoryType = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().InventoryType().getDescription() != null) {
                    lsInventoryType = poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().InventoryType().getDescription();
                }
                tfInventoryType.setText(lsInventoryType);

                String lsColor = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Color().getDescription() != null) {
                    lsColor = poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Color().getDescription();
                }
                tfColor.setText(lsColor);

                String lsClass = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getInventoryClassification() != null) {
                    lsClass = poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getInventoryClassification();
                }
                tfClass.setText(lsClass);

                String lsAMC = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getAverageCost() != 0) {
                    lsAMC = CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getAverageCost());
                }
                tfAMC.setText(lsAMC);

                tfROQ.setText("0");

                String lsRO = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getReceived() != 0) {
                    lsRO = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getReceived());
                }
                tfRO.setText(lsRO);

                String lsBO = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getBackOrder() != 0) {
                    lsBO = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getBackOrder());
                }
                tfBO.setText(lsBO);

                String lsQOH = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getQuantityOnHand() != 0) {
                    lsQOH = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getQuantityOnHand());
                }
                tfQOH.setText(lsQOH);

                String lsCost = "0.00";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getUnitPrice() != null) {
                    lsCost = CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getUnitPrice());
                }
                tfCost.setText(lsCost);

                int lnRequestQuantity = 0;
                lnRequestQuantity = poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getApproved() - (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getPurchase() + poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InvStockRequestDetail().getIssued());
                tfRequestQuantity.setText(String.valueOf(lnRequestQuantity));

                String lsOrderQuantity = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getQuantity() != null) {
                    lsOrderQuantity = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getQuantity());
                }
                tfOrderQuantity.setText(lsOrderQuantity);
            }
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
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
                        tblVwStockRequest.getSelectionModel().clearSelection(pnTblPODetailRow);
                        pnTblPODetailRow = -1;
                        loadMaster();
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                        loadDetail();
                        loadTablePODetail();
                        selectTheExistedDetailFromStockRequest();
                        if (!tfCost.getText().isEmpty() && !tfSupplier.getText().isEmpty()) {
                            loadTableStockRequest();
                        }
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
                    }
                    break;
                case "btnNew":
                    clearDetailFields();
                    clearMasterFields();
                    poDetail_data.clear();
                    loJSON = poPurchasingController.PurchaseOrder().NewTransaction();
                    if ("success".equals((String) loJSON.get("result"))) {
                        poPurchasingController.PurchaseOrder().Master().setCompanyID(prevCompany);
                        poPurchasingController.PurchaseOrder().Master().setSupplierID(prevSupplier);
                        poPurchasingController.PurchaseOrder().Master().setIndustryID(poApp.getIndustry());
                        poPurchasingController.PurchaseOrder().Master().setDestinationID(poPurchasingController.PurchaseOrder().Master().Branch().getBranchCode());
                        poPurchasingController.PurchaseOrder().Master().setInventoryTypeCode(poPurchasingController.PurchaseOrder().getInventoryTypeCode());
                        loadMaster();
                        pnTblPODetailRow = - 1;
                        isNewUpdate = true;
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                        loadTablePODetail();
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                    }
                    break;
                case "btnUpdate":
                    loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                    if (!"success".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                        break;
                    }
                    pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    loadMaster();
                    loJSON = poPurchasingController.PurchaseOrder().UpdateTransaction();
                    if ("error".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                        break;
                    }
                    isNewUpdate = true;
                    pnTblPODetailRow = - 1;
                    pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    loadTablePODetail();
                    selectTheExistedDetailFromStockRequest();
                    if (!tfCost.getText().isEmpty() && !tfSupplier.getText().isEmpty()) {
                        loadTableStockRequest();
                    }
                    break;
                case "btnSearch":
                    if (activeField != null) {
                        String loTextFieldId = activeField.getId();
                        String lsValue = activeField.getText().trim();
                        switch (loTextFieldId) {
//                            case "tfCompany":
//                                if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
//                                    if (!isExchangingCompany()) {
//                                        return;
//                                    }
//                                }
//                                loJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
//                                if ("error".equals(loJSON.get("result"))) {
//                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
//                                    tfCompany.setText("");
//                                    break;
//                                }
//                                tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
//                                if (tfCompany.getText().isEmpty()) {
//                                    tfCompany.requestFocus();
//                                }
//                                if (!tfCompany.getText().isEmpty() && !tfSupplier.getText().isEmpty()) {
//                                    loadTableStockRequest();
//                                }
//                                selectTheExistedDetailFromStockRequest();
//                                break;
                            case "tfSupplier":
                                if (isNewUpdate) {
                                    if (!isExchangingSupplier()) {
                                        return;
                                    }
                                }
                                loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfSupplier.setText("");
                                    break;
                                }
                                tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                if (tfSupplier.getText().isEmpty()) {
                                    tfSupplier.requestFocus();
                                }
//                                if (!tfCompany.getText().isEmpty() && !tfSupplier.getText().isEmpty()) {
//                                    loadTableStockRequest();
//                                }
                                selectTheExistedDetailFromStockRequest();
                                break;
                            case "tfDestination":
                                loJSON = poPurchasingController.PurchaseOrder().SearchDestination(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfDestination.setText("");
                                    break;
                                }
                                tfDestination.setText(poPurchasingController.PurchaseOrder().Master().Branch().getBranchName());
                                if (tfDestination.getText().isEmpty()) {
                                    tfDestination.requestFocus();
                                }
                                selectTheExistedDetailFromStockRequest();
                                break;
                            case "tfTerm":
                                loJSON = poPurchasingController.PurchaseOrder().SearchTerm(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfTerm.setText("");
                                    break;
                                }
                                tfTerm.setText(poPurchasingController.PurchaseOrder().Master().Term().getDescription());
                                if (tfTerm.getText().isEmpty()) {
                                    tfTerm.requestFocus();
                                }
                                selectTheExistedDetailFromStockRequest();
                                break;
                            case "tfBrand":
                                if (pnTblPODetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                loJSON = poPurchasingController.PurchaseOrder().SearchBrand(lsValue, false, pnTblPODetailRow
                                );
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfBrand.setText("");
                                    break;
                                }
                                tfBrand.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Brand().getDescription());
                                if (!tfModel.getText().isEmpty()) {
                                    tfOrderQuantity.requestFocus();
                                } else {
                                    tfModel.requestFocus();
                                }
                                loadTablePODetail();
                                selectTheExistedDetailFromStockRequest();
                                break;
                            case "tfModel":
                                if (pnTblPODetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                loJSON = poPurchasingController.PurchaseOrder().SearchModel(lsValue, false, pnTblPODetailRow);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfModel.setText("");
                                    break;
                                }
                                loadDetail();
                                if (!tfModel.getText().isEmpty()) {
                                    tfOrderQuantity.requestFocus();
                                }
                                loadTablePODetail();
                                selectTheExistedDetailFromStockRequest();
                                break;
                            default:
                                System.out.println("Unknown TextField");
                        }
                    }
                    break;
                case "btnSave":
                    if (!ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to save?")) {
                        return;
                    }
                    prevCompany = poPurchasingController.PurchaseOrder().Master().getCompanyID();
                    prevSupplier = poPurchasingController.PurchaseOrder().Master().getSupplierID();

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
                            isNewUpdate = false;
                            clearDetailFields();
                            clearMasterFields();
                            poDetail_data.clear();
                            tblVwOrderDetails.getItems().clear();
                            pnEditMode = EditMode.UNKNOWN;
                            prevCompany = poPurchasingController.PurchaseOrder().Master().getCompanyID();
                            prevSupplier = poPurchasingController.PurchaseOrder().Master().getSupplierID();
                            loJSON = poPurchasingController.PurchaseOrder().SearchIndustry(poApp.getIndustry(), true);
                            if ("error".equals((String) loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                return;
                            }
                            String lsIndustryName = "";
                            if (poPurchasingController.PurchaseOrder().Master().Industry().getDescription() != null) {
                                lsIndustryName = poPurchasingController.PurchaseOrder().Master().Industry().getDescription();
                            }
//                            tfIndustry.setText(lsIndustryName);
//                            if (!tfCompany.getText().isEmpty()) {
//                                loJSON = poPurchasingController.PurchaseOrder().SearchCompany(poPurchasingController.PurchaseOrder().Master().getCompanyID(), true);
//                                if ("error".equals((String) loJSON.get("result"))) {
//                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
//                                    return;
//                                }
//                            }
//
//                            String lsCompanyName = "";
//                            if (poPurchasingController.PurchaseOrder().Master().Company().getCompanyName() != null) {
//                                lsCompanyName = poPurchasingController.PurchaseOrder().Master().Company().getCompanyName();
//                            }
//                            tfCompany.setText(lsCompanyName);
//
                            if (!tfSupplier.getText().isEmpty()) {
                                loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(poPurchasingController.PurchaseOrder().Master().getSupplierID(), true);
                                if ("error".equals((String) loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    return;
                                }
                            }
                            String lsSupplierName = "";
                            if (poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName() != null) {
                                lsSupplierName = poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName();
                            }
                            tfSupplier.setText(lsSupplierName);
                            pnTblStockRequestRow = -1;
                            tblVwStockRequest.getItems().clear();
                            tblVwStockRequest.setPlaceholder(new Label("NO RECORD TO LOAD"));
                            if (!tfSupplier.getText().isEmpty()) {
                                loadTableStockRequest();
                            }
                        } else {
                            clearMasterFields();
                            clearDetailFields();
                            poDetail_data.clear();
                            loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                            if ("success".equals((String) loJSON.get("result"))) {
                                pnTblPODetailRow = -1;
                                loadMaster();
                                clearDetailFields();
                                pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                                loadTablePODetail();
                            }
                        }
                    }
                    tblVwOrderDetails.getSelectionModel().clearSelection();
                    tblVwStockRequest.getSelectionModel().clearSelection();
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
//                    if (tfCompany.getText().isEmpty()) {
//                        ShowMessageFX.Warning("Invalid to retrieve stock request, company is empty.", psFormName, null);
//                        return;
//                    }
                    if (tfSupplier.getText().isEmpty()) {
                        ShowMessageFX.Warning("Invalid to retrieve stock request, supplier is empty.", psFormName, null);
                        return;
                    }
                    if (!tfSupplier.getText().isEmpty() && !tfSupplier.getText().isEmpty()) {
                        loadTableStockRequest();
                    }
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
        } catch (CloneNotSupportedException | ExceptionInInitializerError | SQLException | GuanzonException | ParseException | NullPointerException ex) {
            Logger.getLogger(PurchaseOrder_EntryMPController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextFieldFocus() {
        List<TextField> loTxtField = Arrays.asList(tfReferenceNo, tfDiscountRate, tfDiscountAmount,
                tfAdvancePRate, tfAdvancePAmount, tfCost, tfOrderQuantity);
        loTxtField.forEach(tf -> tf.focusedProperty().addListener(txtField_Focus));

        tfBrand.setOnMouseClicked(e -> activeField = tfBrand);
        tfModel.setOnMouseClicked(e -> activeField = tfModel);
//        tfIndustry.setOnMouseClicked(e -> activeField = tfIndustry);
//        tfCompany.setOnMouseClicked(e -> activeField = tfCompany);
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
        List<TextField> loTxtField = Arrays.asList(tfAdvancePAmount, tfSupplier,
                tfReferenceNo, tfTerm, tfDiscountRate, tfDiscountAmount, tfTotalAmount,
                tfDestination, tfAdvancePRate,
                tfBrand, tfModel,
                tfBO, tfRO,
                tfCost, tfOrderQuantity);

        loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
    }

    private void txtField_KeyPressed(KeyEvent event) {
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
//                            case "tfCompany":
//                                if (!isExchangingCompany()) {
//                                    return;
//                                }
//                                loJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
//                                if ("error".equals(loJSON.get("result"))) {
//                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
//                                    tfCompany.setText("");
//                                    break;
//                                }
//
//                                tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
//                                if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
//                                    clearDetailFields();
//                                    loadTablePODetail();
//                                }
//                                if (!tfCompany.getText().isEmpty() && !tfSupplier.getText().isEmpty()) {
//                                    loadTableStockRequest();
//                                }
//                                selectTheExistedDetailFromStockRequest();
//                                break;
                            case "tfSupplier":
                                if (isNewUpdate) {
                                    if (!isExchangingSupplier()) {
                                        return;
                                    }
                                }
                                loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfSupplier.setText("");
                                    break;
                                }

                                tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                if (!tfSupplier.getText().isEmpty()) {
                                    loadTableStockRequest();
                                }
                                selectTheExistedDetailFromStockRequest();
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
                                selectTheExistedDetailFromStockRequest();
                                break;
                            case "tfBrand":
                                if (pnTblPODetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                loJSON = poPurchasingController.PurchaseOrder().SearchBrand(lsValue, false, pnTblPODetailRow);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfBrand.setText("");
                                    break;
                                }
                                tfBrand.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Brand().getDescription());
                                if (!tfModel.getText().isEmpty()) {
                                    tfOrderQuantity.requestFocus();
                                } else {
                                    tfModel.requestFocus();
                                }
                                loadTablePODetail();
                                selectTheExistedDetailFromStockRequest();
                                break;
                            case "tfModel":
                                if (pnTblPODetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                try {
                                    loJSON = poPurchasingController.PurchaseOrder().SearchModel(lsValue, false, pnTblPODetailRow);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        tfModel.setText("");
                                        break;
                                    }
                                    loadDetail();
                                    if (!tfModel.getText().isEmpty()) {
                                        tfOrderQuantity.requestFocus();
                                    }
                                    loadTablePODetail();
                                    selectTheExistedDetailFromStockRequest();
                                } catch (SQLException | GuanzonException | NullPointerException ex) {
                                    System.err.println("error: " + ex);
                                }
                                break;
                        }
                        switch (txtFieldID) {
                            case "tfSupplier":
                            case "tfDestination":
                            case "tfTerm":
                            case "tfAdvancePAmount":
                            case "tfAdvancePRate":
                            case "tfDiscountRate":
                            case "tfDiscountAmount":
                                CommonUtils.SetNextFocus((TextField) event.getSource());
                                break;
                            case "tfOrderQuantity":
                                setOrderQuantityToDetail(lsValue);
                                if (!poDetail_data.isEmpty() && pnTblPODetailRow < poDetail_data.size() - 1) {
                                    pnTblPODetailRow++;
                                }
                                CommonUtils.SetNextFocus((TextField) event.getSource());
                                loadTablePODetailAndSelectedRow();
                                break;
                        }
                        event.consume();
                        break;
                    case UP:
                        setOrderQuantityToDetail(lsValue);
                        if (!lsTxtField.equals("tfBrand") && !lsTxtField.equals("tfModel")) {
                            if (pnTblPODetailRow > 0 && !poDetail_data.isEmpty()) {
                                pnTblPODetailRow--;
                            }
                        }

                        // Prevent going from 'tfOrderQuantity' to 'taRemarks'
                        if (!lsTxtField.equals("tfBrand") && !lsTxtField.equals("tfOrderQuantity")) {
                            CommonUtils.SetPreviousFocus((TextField) event.getSource());
                        }
                        loadTablePODetailAndSelectedRow();
                        event.consume();
                        break;
                    case DOWN:
                        setOrderQuantityToDetail(lsValue);
                        if ("tfOrderQuantity".equals(lsTxtField.getId())) {
                            if (!poDetail_data.isEmpty() && pnTblPODetailRow < poDetail_data.size() - 1) {
                                pnTblPODetailRow++;
                            }
                        }
                        CommonUtils.SetNextFocus(lsTxtField);
                        loadTablePODetailAndSelectedRow();
                        event.consume(); // Consume event after handling focus
                        break;
                    default:
                        break;

                }
            }
        } catch (ExceptionInInitializerError | SQLException | GuanzonException | NullPointerException ex) {
            Logger.getLogger(PurchaseOrder_EntryMPController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setOrderQuantityToDetail(String fsValue) {
        if (fsValue.isEmpty()) {
            fsValue = "0";
        }
        if (Integer.parseInt(fsValue) < 0) {
            ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);
            fsValue = "0";
        }
        if (pnTblPODetailRow < 0) {
            fsValue = "0";
            ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
            clearDetailFields();
            int detailCount = poPurchasingController.PurchaseOrder().getDetailCount();
            pnTblPODetailRow = detailCount > 0 ? detailCount - 1 : 0;
        }
        tfOrderQuantity.setText(fsValue);
        poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).setQuantity(Integer.valueOf(fsValue));
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
        isNewUpdate = false;
        dpTransactionDate.setValue(null);
        dpExpectedDlvrDate.setValue(null);
        taRemarks.setText("");
        CustomCommonUtil.setSelected(false, chkbAdvancePayment);
        CustomCommonUtil.setText("", tfTransactionNo,
                tfDestination, tfReferenceNo, tfTerm);
        CustomCommonUtil.setText("0.00", tfTotalAmount, tfAdvancePAmount, tfAdvancePRate,
                tfDiscountAmount, tfDiscountRate);
    }

    private void clearDetailFields() {
        /* Detail Fields*/
        CustomCommonUtil.setText("", tfBrand, tfModel, tfVariant, tfInventoryType, tfColor, tfClass, tfAMC, tfROQ,
                tfRO, tfBO, tfQOH, tfRequestQuantity);
        tfCost.setText("0.00");
        CustomCommonUtil.setText("0", tfOrderQuantity);
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
                tfBrand, tfModel, tfOrderQuantity);

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
//        tfCompany.setDisable(fnEditMode == EditMode.UPDATE);
        tfSupplier.setDisable(fnEditMode == EditMode.UPDATE);
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
                        selectTheExistedDetailFromStockRequest();
                    });

                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(PurchaseOrder_EntryMPController.class
                            .getName()).log(Level.SEVERE, null, ex);
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
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);
        progressIndicator.setStyle("-fx-accent: #FF8201;");

        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setStyle("-fx-background-color: transparent;");

        tblVwOrderDetails.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Task<List<ModelPurchaseOrderDetail>> task = new Task<List<ModelPurchaseOrderDetail>>() {
            @Override
            protected List<ModelPurchaseOrderDetail> call() throws Exception {
                try {
                    int detailCount = poPurchasingController.PurchaseOrder().getDetailCount();
                    if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)) {
                        Model_PO_Detail lastDetail = poPurchasingController.PurchaseOrder().Detail(detailCount - 1);
                        if (lastDetail.getStockID() != null && !lastDetail.getStockID().isEmpty()) {
                            poPurchasingController.PurchaseOrder().AddDetail();
                            detailCount++;
                        }
                    }
                    double grandTotalAmount = 0.0;
                    List<ModelPurchaseOrderDetail> detailsList = new ArrayList<>();

                    for (int lnCtr = 0; lnCtr < detailCount; lnCtr++) {
                        Model_PO_Detail orderDetail = poPurchasingController.PurchaseOrder().Detail(lnCtr);
                        double lnTotalAmount = orderDetail.Inventory().getCost().doubleValue() * orderDetail.getQuantity().doubleValue();
                        grandTotalAmount += lnTotalAmount;
                        int lnRequestQuantity = 0;
                        lnRequestQuantity = orderDetail.InvStockRequestDetail().getApproved() - (orderDetail.InvStockRequestDetail().getPurchase() + orderDetail.InvStockRequestDetail().getIssued());
                        detailsList.add(new ModelPurchaseOrderDetail(
                                String.valueOf(lnCtr + 1),
                                orderDetail.getSouceNo(),
                                orderDetail.Inventory().getBarCode(),
                                orderDetail.Inventory().getDescription(),
                                CustomCommonUtil.setIntegerValueToDecimalFormat(orderDetail.getUnitPrice()),
                                "",
                                String.valueOf(lnRequestQuantity),
                                String.valueOf(orderDetail.getQuantity()),
                                CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotalAmount),
                                ""
                        ));
                    }

                    final double totalAmountFinal = grandTotalAmount;
                    Platform.runLater(() -> {
                        poDetail_data.setAll(detailsList); // Properly update list
                        tblVwOrderDetails.setItems(poDetail_data);
                        computeTotalAmount(totalAmountFinal);
                        poPurchasingController.PurchaseOrder().Master().setTranTotal(totalAmountFinal);
                        tfTotalAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(totalAmountFinal));
                        reselectLastRow();
                    });

                    return detailsList;

                } catch (GuanzonException | SQLException ex) {
                    Logger.getLogger(PurchaseOrder_EntryMPController.class.getName()).log(Level.SEVERE, null, ex);
                    return null;
                }
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
            }
        };

        new Thread(task).start();
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

        // Prevent column reordering
        tblVwOrderDetails.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblVwOrderDetails.lookup("TableHeaderRow");
            if (header != null) {
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            }
        });
    }

// Method to reselect the last clicked row
    private void reselectLastRow() {
        if (pnTblPODetailRow >= 0 && pnTblPODetailRow < tblVwOrderDetails.getItems().size()) {
            tblVwOrderDetails.getSelectionModel().clearAndSelect(pnTblPODetailRow);
            tblVwOrderDetails.getSelectionModel().focus(pnTblPODetailRow); // Scroll to the selected row if needed
        }
    }

    private void computeTotalAmount(double fnGrandTotal) {
        double amount = (Double.parseDouble(tfAdvancePRate.getText().replace(",", "")) / 100) * fnGrandTotal;
        tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(amount));
        double advpercentage = (Double.parseDouble(tfAdvancePAmount.getText().replace(",", "")) / fnGrandTotal) * 100;
        tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(advpercentage));
        poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesPercentage(advpercentage);
        poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesAmount(amount);

    }

    private void initTextFieldsProperty() {
        tfSupplier.textProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        if (newValue.isEmpty()) {
                            if (isNewUpdate) {
                                if (!isExchangingSupplier()) {
                                    return;
                                }
                            }
                            poPurchasingController.PurchaseOrder().Master().setSupplierID("");
                            poPurchasingController.PurchaseOrder().Master().setAddressID("");
                            poPurchasingController.PurchaseOrder().Master().setContactID("");
                            tfSupplier.setText("");
                            tblVwStockRequest.getItems().clear();
                            poApprovedStockRequest_data.clear();
                            tblVwStockRequest.setPlaceholder(new Label("NO RECORD TO LOAD"));
                        }
                    }
                }
                );
    }

    private boolean isExchangingSupplier() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            boolean isHaveQuantityAndStockId = false;
            if (poPurchasingController.PurchaseOrder().getDetailCount() >= 1) {
                if (poPurchasingController.PurchaseOrder().Detail(0).getStockID() != null && poPurchasingController.PurchaseOrder().Detail(0).getQuantity() != null) {
                    if (!poPurchasingController.PurchaseOrder().Detail(0).getStockID().isEmpty()
                            || !poPurchasingController.PurchaseOrder().Detail(0).getQuantity().equals(0)) {
                        isHaveQuantityAndStockId = true;
                    }
                }
            }
            if (isHaveQuantityAndStockId) {
                if (ShowMessageFX.YesNo("PO Details have already items, are you sure you want to change supplier?", psFormName, null)) {
                    int detailCount = poPurchasingController.PurchaseOrder().getDetailCount();
                    for (int lnCtr = detailCount - 1; lnCtr >= 0; lnCtr--) {
                        if (poPurchasingController.PurchaseOrder().Detail(lnCtr).getSouceNo().isEmpty()
                                && poPurchasingController.PurchaseOrder().Detail(lnCtr).getStockID().isEmpty()
                                && poPurchasingController.PurchaseOrder().Detail(lnCtr).getQuantity().equals(0)) {
                            continue; // Skip deleting this row
                        }
                        poPurchasingController.PurchaseOrder().Detail().remove(lnCtr);
                    }
                    pnTblPODetailRow = -1;
                    pnTblStockRequestRow = -1;
                    tblVwStockRequest.getSelectionModel().clearSelection();
                    clearDetailFields();
                    loadTablePODetail();
                } else {
                    try {
                        poJSON = new JSONObject();
                        poJSON = poPurchasingController.PurchaseOrder().SearchSupplier(poPurchasingController.PurchaseOrder().Master().getSupplierID(), true);
                        if (!"success".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                            return false;
                        }
                        tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                        selectTheExistedDetailFromStockRequest();
                        return false;

                    } catch (ExceptionInInitializerError | SQLException | GuanzonException ex) {
                        Logger.getLogger(PurchaseOrder_EntryMPController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return true;
    }

    private void tblVwStockRequest_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            pnTblStockRequestRow = tblVwStockRequest.getSelectionModel().getSelectedIndex();
            if (event.getClickCount() == 2) {
                if (!tfSupplier.getText().isEmpty()) {
                    ModelPurchaseOrder loSelectedStockRequest = (ModelPurchaseOrder) tblVwStockRequest.getSelectionModel().getSelectedItem();
                    if (loSelectedStockRequest != null) {
                        String lsTransactionNo = loSelectedStockRequest.getIndex06();
                        try {
                            JSONObject loJSON = poPurchasingController.PurchaseOrder().addStockRequestOrdersToPODetail(lsTransactionNo);
                            if ("success".equals(loJSON.get("result"))) {
                                if (poPurchasingController.PurchaseOrder().getDetailCount() > 0) {
                                    pnTblPODetailRow = poPurchasingController.PurchaseOrder().getDetailCount() - 1;
                                    loadTablePODetailAndSelectedRow();
                                    poApprovedStockRequest_data.get(pnTblStockRequestRow).setIndex07(PurchaseOrderStatus.CONFIRMED);
                                    selectTheExistedDetailFromStockRequest();
                                }
                            } else {
                                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);

                            }
                        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                            Logger.getLogger(PurchaseOrder_EntryController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                            ShowMessageFX.Warning("Error loading data: " + ex.getMessage(), psFormName, null);
                        }
                    }
                } else {
                    ShowMessageFX.Warning("Can't allow to add this transaction if the company or supplier is empty.", psFormName, null);
                }
            }
        }
    }

    private void loadTablePODetailAndSelectedRow() {
        if (pnTblPODetailRow >= 0) {
            Platform.runLater(() -> {
                // Run a delay after the UI thread is free
                PauseTransition delay = new PauseTransition(Duration.millis(10));
                delay.setOnFinished(event -> {
                    Platform.runLater(() -> { // Run UI updates in the next cycle
                        loadTablePODetail();
                    });
                });
                delay.play();
            });
            loadDetail();
            initDetailFocus();
        }
    }

    private void selectTheExistedDetailFromStockRequest() {
        if (!poApprovedStockRequest_data.isEmpty()) {
            Set<String> existingDetailIds = poDetail_data.stream()
                    .map(ModelPurchaseOrderDetail::getIndex02)
                    .collect(Collectors.toSet());

            for (ModelPurchaseOrder master : poApprovedStockRequest_data) {
                master.setIndex07(existingDetailIds.contains(master.getIndex06()) ? PurchaseOrderStatus.CONFIRMED : PurchaseOrderStatus.OPEN);
            }
            tblVwStockRequest.refresh();
        }
    }

    private void tblVwOrderDetails_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.READY) {
            pnTblPODetailRow = tblVwOrderDetails.getSelectionModel().getSelectedIndex();
            ModelPurchaseOrderDetail selectedItem = tblVwOrderDetails.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 1) {
                clearDetailFields();
                if (selectedItem != null) {
                    if (pnTblPODetailRow >= 0) {
                        loadDetail();
                        initDetailFocus();
                    }
                }
            }
        }
    }

    private void initDetailFocus() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            if (pnTblPODetailRow >= 0) {
                boolean isSourceNotEmpty = !poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getSouceNo().isEmpty();
                tfBrand.setDisable(isSourceNotEmpty);
                tfModel.setDisable(isSourceNotEmpty);
                if (isSourceNotEmpty && !tfBrand.getText().isEmpty()) {
                    tfOrderQuantity.requestFocus();
                } else {
                    if (!tfModel.getText().isEmpty() && (pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.ADDNEW)) {
                        tfOrderQuantity.requestFocus();
                    } else {
                        tfBrand.requestFocus();
                    }
                }
            }

        }
    }
}
