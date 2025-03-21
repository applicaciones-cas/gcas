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
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author johndave
 */
public class PurchaseOrder_ConfirmationMCController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order Confirmation MC";
    private LogWrapper logWrapper;
    private int pnEditMode;
    private JSONObject poJSON;
    unloadForm poUnload = new unloadForm();
    private ObservableList<ModelPurchaseOrder> poPurchaseOrder_data = FXCollections.observableArrayList();
    private ObservableList<ModelPurchaseOrderDetail> poDetail_data = FXCollections.observableArrayList();
    private int pnTblPurchaseOrderRow = -1;
    private int pnTblPODetailRow = -1;
    private static final int ROWS_PER_PAGE = 1;
    @FXML
    private AnchorPane AnchorMaster, AnchorDetails, AnchorMain, apBrowse, apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnUpdate, btnSave, btnCancel, btnConfirm, btnReturn, btnVoid, btnPrint,
            btnRetrieve, btnTransHistory, btnClose;
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
    private TextField tfBrand, tfModel, tfVariant, tfInventoryType, tfColor, tfClass, tfAMC, tfROQ,
            tfRO, tfBO, tfQOH, tfCost, tfRequestQuantity, tfOrderQuantity;
    @FXML
    private TextField tfSearchIndustry, tfSearchCompany, tfSearchSupplier, tfSearchReferenceNo;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView<ModelPurchaseOrderDetail> tblVwOrderDetails;
    @FXML
    private TableColumn<ModelPurchaseOrderDetail, String> tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail,
            tblCostDetail, tblROQDetail, tblRequestQuantityDetail, tblOrderQuantityDetail, tblTotalAmountDetail;
    @FXML
    private TableView<ModelPurchaseOrder> tblVwPurchaseOrder;
    @FXML
    private TableColumn<ModelPurchaseOrder, String> tblRowNo, tblTransactionNo, tblDate, tblSupplier;
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
            poPurchasingController.PurchaseOrder().setTransactionStatus("0");
            JSONObject loJSON = new JSONObject();
            loJSON = poPurchasingController.PurchaseOrder().InitTransaction();
            if (!"success".equals(loJSON.get("result"))) {
                ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
            }
            poJSON = poPurchasingController.PurchaseOrder().SearchIndustry("02", true);
            if ("error".equals((String) loJSON.get("result"))) {
                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);

                return;
            }
            String lsIndustryName = "";
            if (poPurchasingController.PurchaseOrder().Master().Industry().getDescription() != null) {
                lsIndustryName = poPurchasingController.PurchaseOrder().Master().Industry().getDescription();
            }
            tblVwOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
            tfSearchIndustry.setText(lsIndustryName);
            initButtonsClickActions();
            initTextFieldFocus();
            initTextAreaFocus();
            initTextFieldKeyPressed();

            initDatePickerActions();
            initTextFieldPattern();
            initTablePurchaseOrder();
            initTablePODetail();
            tblVwPurchaseOrder.setOnMouseClicked(this::tblVwPurchaseOrder_Clicked);
            tblVwOrderDetails.setOnMouseClicked(this::tblVwOrderDetails_Clicked);
            pnEditMode = EditMode.UNKNOWN;
            initButtons(pnEditMode);
            initFields(pnEditMode);
        } catch (ExceptionInInitializerError | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_ConfirmationMPController.class.getName()).log(Level.SEVERE, null, ex);
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

            String lsDestinationName = "";
            if (poPurchasingController.PurchaseOrder().Master().Branch().getBranchName() != null) {
                lsDestinationName = poPurchasingController.PurchaseOrder().Master().Branch().getBranchName();
            }
            tfDestination.setText(lsDestinationName);

            String lsTermCode = "";
            if (poPurchasingController.PurchaseOrder().Master().Term().getDescription() != null) {
                lsTermCode = poPurchasingController.PurchaseOrder().Master().Term().getDescription();
            }
            tfTerm.setText(lsTermCode);

            taRemarks.setText(poPurchasingController.PurchaseOrder().Master().getRemarks());

            dpExpectedDlvrDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                    SQLUtil.dateFormat(poPurchasingController.PurchaseOrder().Master().getExpectedDate(), SQLUtil.FORMAT_SHORT_DATE)));
            tfDiscountRate.setText(poPurchasingController.PurchaseOrder().Master().getDiscount().toString());
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDiscount()));
            if (poPurchasingController.PurchaseOrder().Master().isWithAdvPaym() == true) {
                chkbAdvancePayment.setSelected(true);
            } else {
                chkbAdvancePayment.setSelected(false);
            }
            tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesPercentage()));
            tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesAmount()));
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_ConfirmationMPController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadDetail() {
        try {
            if (pnTblPODetailRow >= 0) {
                tfBrand.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Brand().getDescription());
                tfModel.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Model().getDescription());
                tfVariant.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Model().getDescription());
                tfInventoryType.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().InventoryType().getDescription());
                tfColor.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().Color().getDescription());
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
            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnUpdate, btnSave, btnCancel,
                btnPrint, btnRetrieve, btnTransHistory, btnClose, btnConfirm);

        buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
    }

    private void handleButtonAction(ActionEvent event) {
        try {
            JSONObject loJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch (lsButton) {
                case "btnUpdate":
                    loJSON = poPurchasingController.PurchaseOrder().UpdateTransaction();
                    pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    if ("error".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                    }
                    break;
                case "btnConfirm":
                  try {
                    loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                    if ("success".equals((String) loJSON.get("result"))) {
                        
                        loJSON = poPurchasingController.PurchaseOrder().ConfirmTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                        if (!"success".equals((String) loJSON.get("result"))) {
                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                            break;
                        }
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        clearMasterFields();
                        clearDetailFields();
                        poDetail_data.clear();
                        pnEditMode = EditMode.UNKNOWN;

                        //this code below use to highlight tblpurchase
                        tblVwPurchaseOrder.refresh();
                        poPurchaseOrder_data.get(pnTblPurchaseOrderRow).setIndex05(PurchaseOrderStatus.CONFIRMED);
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(PurchaseOrder_ConfirmationMPController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                case "btnSave":
                    try {
                    if (!ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to save?")) {
                        return;
                    }

                    if (pnEditMode == EditMode.UPDATE && (poPurchasingController.PurchaseOrder().Master().getTransactionStatus().equals(PurchaseOrderStatus.CONFIRMED)
                            || !"success".equals((loJSON = ShowDialogFX.getUserApproval(poApp)).get("result")))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        return;
                    }

                    if (pnEditMode == EditMode.UPDATE) {
                        poPurchasingController.PurchaseOrder().Master().setModifiedDate(poApp.getServerDate());
                        poPurchasingController.PurchaseOrder().Master().setModifyingId(poApp.getUserID());
                        for (int i = 0; i < poPurchasingController.PurchaseOrder().getDetailCount(); i++) {
                            poPurchasingController.PurchaseOrder().Detail(i).setModifiedDate(poApp.getServerDate());
                        }
                    }

                    if (!"success".equals((loJSON = poPurchasingController.PurchaseOrder().SaveTransaction()).get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        return;
                    }

                    ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                    loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());

                    if ("success".equals(loJSON.get("result")) && poPurchasingController.PurchaseOrder().Master().getTransactionStatus().equals(PurchaseOrderStatus.OPEN)
                            && ShowMessageFX.YesNo(null, psFormName, "Do you want to confirm this transaction?")) {
                        if ("success".equals((loJSON = poPurchasingController.PurchaseOrder().ConfirmTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo())).get("result"))) {
                            ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                        }
                    }

                    if (ShowMessageFX.YesNo(null, psFormName, "Do you want to print this transaction?")) {
                        loJSON = poPurchasingController.PurchaseOrder().printTransaction();
                        if ("success".equals(loJSON.get("result"))) {
                            ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                        }
                    }

                    loadMaster();
                    loadDetail();
                    loadTablePODetail();
                    pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();

                } catch (ParseException ex) {
                    Logger.getLogger(PurchaseOrder_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo(null, "Cancel Confirmation", "Are you sure you want to cancel?")) {
                        loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                        if ("success".equals((String) loJSON.get("result"))) {
                            loadMaster();
                            loadDetail();
                            loadTablePODetail();
                            pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                        }
                    }
                    break;
                case "btnPrint":
                    poJSON = poPurchasingController.PurchaseOrder().printTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                    }
                    break;
                case "btnRetrieve":
                    loadTablePurchaseOrder();
                    break;
                case "btnTransHistory":
                    break;
                case "btnReturn":
                    //add your method here

                    //this code below use to highlight tblpurchase
                    tblVwPurchaseOrder.refresh();
                    poPurchaseOrder_data.get(pnTblPurchaseOrderRow).setIndex05(PurchaseOrderStatus.RETURNED);
                    break;
                case "btnVoid":
                          try {
                    loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                    if ("success".equals((String) loJSON.get("result"))) {
                        
                        loJSON = poPurchasingController.PurchaseOrder().VoidTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                        if (!"success".equals((String) loJSON.get("result"))) {
                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                            break;
                        }
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        clearMasterFields();
                        clearDetailFields();
                        poDetail_data.clear();
                        pnEditMode = EditMode.UNKNOWN;

                        //this code below use to highlight tblpurchase
                        tblVwPurchaseOrder.refresh();
                        poPurchaseOrder_data.get(pnTblPurchaseOrderRow).setIndex05(PurchaseOrderStatus.VOID);
                    }
                } catch (ParseException ex) {
                    Logger.getLogger(PurchaseOrder_ConfirmationMPController.class.getName()).log(Level.SEVERE, null, ex);
                }
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
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_ConfirmationMPController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextFieldFocus() {
        List<TextField> loTxtField = Arrays.asList(tfReferenceNo, tfDiscountRate, tfDiscountAmount,
                tfAdvancePRate, tfAdvancePAmount, tfOrderQuantity);
        loTxtField.forEach(tf -> tf.focusedProperty().addListener(txtField_Focus));
    }

    private void initTextAreaFocus() {
        taRemarks.focusedProperty().addListener(txtArea_Focus);
    }
    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        TextField loTextField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextFieldID = loTextField.getId();
        String lsValue = loTextField.getText();
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTextFieldID) {
                case "tfDiscountRate":
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
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00 || Double.parseDouble(lsValue) > 100) {
                        ShowMessageFX.Warning("Invalid Downpayment Rates", psFormName, null);
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesPercentage(Double.valueOf(lsValue.replace(",", "")));
                    tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfAdvancePAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning("Invalid Downpayment Amount", psFormName, null);
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesAmount(Double.valueOf(lsValue.replace(",", "")));
                    tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfOrderQuantity":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    if (Integer.parseInt(lsValue) < 0) {
                        ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);

                        return;
                    }
                    if (pnTblPODetailRow >= 0) {
                        poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).setQuantity(Integer.valueOf(lsValue));
                        tfOrderQuantity.setText(lsValue);
                    } else {
                        lsValue = "0";
                        ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                    }
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
                tfBrand, tfModel,
                tfBO, tfRO,
                tfCost, tfOrderQuantity, tfSearchIndustry, tfSearchCompany, tfSearchSupplier, tfSearchReferenceNo);

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
                            case "tfSearchIndustry":
                                loJSON = poPurchasingController.PurchaseOrder().SearchIndustry(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfSearchIndustry.setText("");
                                    break;
                                }
                                tfSearchIndustry.setText(poPurchasingController.PurchaseOrder().Master().Industry().getDescription());
                                break;
                            case "tfSearchCompany":
                                loJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfCompany.setText("");
                                    break;
                                }
                                tfSearchCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
                                break;
                            case "tfSearchSupplier":
                                loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfSupplier.setText("");
                                    break;
                                }
                                tfSearchSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
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
                            case "tfBrand":
                                loJSON = poPurchasingController.PurchaseOrder().SearchBarcode(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfBrand.setText("");
                                    break;
                                }
                                if (pnTblPODetailRow >= 0) {
                                    tfBrand.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getBarCode());
                                } else {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                }
                                break;
                            case "tfModel":
                                loJSON = poPurchasingController.PurchaseOrder().SearchBarcodeDescription(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfModel.setText("");
                                    break;
                                }
                                if (pnTblPODetailRow >= 0) {
                                    tfModel.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getDescription());
                                } else {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);

                                }
                                break;
                        }
                        loadTablePurchaseOrder();
                        event.consume();
                        CommonUtils.SetNextFocus((TextField) event.getSource());
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
        } catch (ExceptionInInitializerError | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_ConfirmationMPController.class.getName()).log(Level.SEVERE, null, ex);
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
                    poPurchasingController.PurchaseOrder().Master().setTransactionDate(SQLUtil.toDate(dpExpectedDlvrDate.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
                }
            }
        });
    }

    private void clearMasterFields() {
        /* Master Fields*/
        pnTblPODetailRow = -1;
        dpTransactionDate.setValue(null);
        dpExpectedDlvrDate.setValue(null);
        taRemarks.setText("");
        chkbAdvancePayment.setSelected(false);
        CustomCommonUtil.setText("", tfTransactionNo, tfCompany, tfSupplier,
                tfDestination, tfReferenceNo, tfTerm, tfDiscountRate,
                tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount);
    }

    private void clearDetailFields() {
        /* Detail Fields*/
        CustomCommonUtil.setText("", tfBrand, tfModel, tfBrand, tfModel,
                tfColor, tfInventoryType, tfClass,
                tfAMC, tfROQ, tfRO, tfBO, tfQOH,
                tfCost, tfRequestQuantity, tfOrderQuantity);
    }

    private void initButtons(int fnEditMode) {
        boolean lbShow = (pnEditMode == EditMode.UPDATE);

        btnClose.setVisible(!lbShow);
        btnClose.setManaged(!lbShow);

        CustomCommonUtil.setVisible(lbShow, btnSave, btnCancel);
        CustomCommonUtil.setManaged(lbShow, btnSave, btnCancel);

        CustomCommonUtil.setVisible(false, btnConfirm, btnReturn, btnVoid, btnUpdate, btnPrint);
        CustomCommonUtil.setManaged(false, btnConfirm, btnReturn, btnVoid, btnUpdate, btnPrint);

        btnTransHistory.setVisible(fnEditMode != EditMode.UNKNOWN);
        btnTransHistory.setManaged(fnEditMode != EditMode.UNKNOWN);
        if (poPurchasingController.PurchaseOrder().Master().getPrint().equals("1")) {
            btnPrint.setText("Reprint");
        } else {
            btnPrint.setText("Print");
        }
        if (fnEditMode == EditMode.READY) {
            switch (poPurchasingController.PurchaseOrder().Master().getTransactionStatus()) {
                case PurchaseOrderStatus.OPEN:
                    CustomCommonUtil.setVisible(true, btnConfirm, btnReturn, btnVoid, btnUpdate, btnPrint);
                    CustomCommonUtil.setManaged(true, btnConfirm, btnReturn, btnVoid, btnUpdate, btnPrint);
                    break;
                case PurchaseOrderStatus.CONFIRMED:
                    CustomCommonUtil.setVisible(true, btnReturn, btnVoid, btnUpdate, btnPrint);
                    CustomCommonUtil.setManaged(true, btnReturn, btnVoid, btnUpdate, btnPrint);
                    break;
                case PurchaseOrderStatus.APPROVED:
                    btnPrint.setVisible(true);
                    btnPrint.setManaged(true);
                    break;
            }
        }
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.UPDATE);

        /* Master Fields*/
        CustomCommonUtil.setDisable(true, AnchorMaster);
        if (!tfReferenceNo.getText().isEmpty()) {
            dpTransactionDate.setDisable(!lbShow);
        }

        tfOrderQuantity.setDisable(!lbShow);
        if (chkbAdvancePayment.isSelected()) {
            CustomCommonUtil.setDisable(!lbShow, tfAdvancePRate, tfAdvancePAmount);
        }

        if (tblVwPurchaseOrder.getItems().isEmpty()) {
            pagination.setVisible(false);
            pagination.setManaged(false);
        }
    }

    private void loadTablePurchaseOrder() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50); // Set size to 200x200
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER); // Center it

        tblVwPurchaseOrder.setPlaceholder(loadingPane); // Show while loading
        progressIndicator.setVisible(true); // Make sure it's visible

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Simulate loading delay
                    Thread.sleep(1000);

                    poPurchaseOrder_data.clear();
                    JSONObject loJSON = poPurchasingController.PurchaseOrder().getPurchaseOrder();
                    if ("success".equals(loJSON.get("result"))) {
                        for (int lnCntr = 0; lnCntr <= poPurchasingController.PurchaseOrder().getPOMasterCount() - 1; lnCntr++) {
                            poPurchaseOrder_data.add(new ModelPurchaseOrder(
                                    String.valueOf(lnCntr + 1),
                                    poPurchasingController.PurchaseOrder().POMaster(lnCntr).getTransactionNo(),
                                    SQLUtil.dateFormat(poPurchasingController.PurchaseOrder().POMaster(lnCntr).getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE),
                                    poPurchasingController.PurchaseOrder().POMaster(lnCntr).Supplier().getCompanyName(),
                                    "",
                                    "",
                                    "",
                                    "",
                                    "",
                                    ""));
                            tblVwPurchaseOrder.setItems(poPurchaseOrder_data);
                        }
                    }
                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(PurchaseOrder_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);

                if (poPurchaseOrder_data == null || poPurchaseOrder_data.isEmpty()) {
                    tblVwPurchaseOrder.setPlaceholder(new Label("NO RECORD TO LOAD"));
                } else {
                    if (pagination != null) {
                        int pageCount = (int) Math.ceil((double) poPurchaseOrder_data.size() / ROWS_PER_PAGE);
                        pagination.setPageCount(pageCount);
                        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> createPage(newIndex.intValue()));
                    }
                    createPage(0);
                    pagination.setVisible(true);
                    pagination.setManaged(true);
                    tblVwPurchaseOrder.toFront();
                }
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
            }
        };
        new Thread(task).start(); // Run task in background
    }

    private Node createPage(int pageIndex) {
        int totalPages = (int) Math.ceil((double) poPurchaseOrder_data.size() / ROWS_PER_PAGE);
        if (totalPages == 0) {
            totalPages = 1;
        }

        pageIndex = Math.max(0, Math.min(pageIndex, totalPages - 1));
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, poPurchaseOrder_data.size());

        if (!poPurchaseOrder_data.isEmpty()) {
            tblVwPurchaseOrder.setItems(FXCollections.observableArrayList(poPurchaseOrder_data.subList(fromIndex, toIndex)));
        }

        if (pagination != null) { // Replace with your actual Pagination variable
            pagination.setPageCount(totalPages);
            pagination.setCurrentPageIndex(pageIndex);
        }

        return tblVwPurchaseOrder;
    }

    private void initTablePurchaseOrder() {
        tblRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblTransactionNo.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblDate.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblSupplier.setCellValueFactory(new PropertyValueFactory<>("index04"));

        tblVwPurchaseOrder.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblVwPurchaseOrder.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        initTableHighlithers();
    }

    private void initTableHighlithers() {
        tblVwPurchaseOrder.setRowFactory(tv -> new TableRow<ModelPurchaseOrder>() {
            @Override
            protected void updateItem(ModelPurchaseOrder item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    // Assuming empIndex05 corresponds to an employee status
                    String status = item.getIndex05(); // Replace with actual getter
                    switch (status) {
                        case PurchaseOrderStatus.CONFIRMED:
                            setStyle("-fx-background-color: #C1E1C1;");
                            break;
                        case PurchaseOrderStatus.VOID:
                            setStyle("-fx-background-color: #FAA0A0;");
                            break;
                        case PurchaseOrderStatus.RETURNED:
                            setStyle("-fx-background-color: #FAC898");
                        default:
                            setStyle("");
                    }
                    tblVwPurchaseOrder.refresh();
                }
            }
        });
    }

    private void loadTablePODetail() {
        // Configure ProgressIndicator
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50); // Ensure consistent size
        progressIndicator.setStyle("-fx-accent: #FF8201;"); // Set custom progress color

// Center ProgressIndicator inside TableView using StackPane
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER); // Center it
        loadingPane.setStyle("-fx-background-color: transparent;"); // Make background transparent

        tblVwOrderDetails.setPlaceholder(loadingPane); // Show while loading
        progressIndicator.setVisible(true); // Make sure it's visible

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                poDetail_data.clear();
                try {

                    Thread.sleep(300);

                    double grandTotalAmount = 0.0;
                    for (int lnCntr = 0; lnCntr <= poPurchasingController.PurchaseOrder().getDetailCount() - 1; lnCntr++) {
                        double lnTotalAmount = poPurchasingController.PurchaseOrder()
                                .Detail(lnCntr)
                                .Inventory().getCost().doubleValue() * poPurchasingController.PurchaseOrder()
                                        .Detail(lnCntr)
                                        .getQuantity().doubleValue();
                        grandTotalAmount += lnTotalAmount;
                        poDetail_data.add(new ModelPurchaseOrderDetail(
                                String.valueOf(lnCntr + 1),
                                poPurchasingController.PurchaseOrder().Detail(lnCntr).getSouceNo(),
                                poPurchasingController.PurchaseOrder().Detail(lnCntr).Inventory().getBarCode(),
                                poPurchasingController.PurchaseOrder().Detail(lnCntr).Inventory().getDescription(),
                                CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(lnCntr).Inventory().getCost()),
                                "",
                                String.valueOf(poPurchasingController.PurchaseOrder().Detail(lnCntr).InvStockRequestDetail().getQuantity()),
                                String.valueOf(poPurchasingController.PurchaseOrder().Detail(lnCntr).getQuantity()),
                                CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotalAmount),
                                ""
                        ));

                    }
                    tblVwOrderDetails.setItems(poDetail_data);
                    computeTotalAmount(grandTotalAmount);
                    poPurchasingController.PurchaseOrder().Master().setTranTotal(grandTotalAmount);
                    tfTotalAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(grandTotalAmount));

                } catch (GuanzonException | SQLException ex) {
                    Logger.getLogger(PurchaseOrder_EntryMCController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                tblVwOrderDetails.setItems(poDetail_data);
                progressIndicator.setVisible(false); // Hide when done

            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
            }
        };

        new Thread(task).start(); // Run task in background
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

    private void tblVwPurchaseOrder_Clicked(MouseEvent event) {
        JSONObject loJSON = new JSONObject();
        pnTblPurchaseOrderRow = tblVwPurchaseOrder.getSelectionModel().getSelectedIndex();
        if (pnTblPurchaseOrderRow < 0 || pnTblPurchaseOrderRow >= tblVwPurchaseOrder.getItems().size()) {
            ShowMessageFX.Warning("Please select valid purchase order information.", "Warning", null);
            return;
        }
        if (event.getClickCount() == 1) {
            ModelPurchaseOrder loSelectedPurchaseOrder = (ModelPurchaseOrder) tblVwPurchaseOrder.getSelectionModel().getSelectedItem();
            if (loSelectedPurchaseOrder != null) {
                String lsTransactionNo = loSelectedPurchaseOrder.getIndex02();
                try {
                    loJSON = poPurchasingController.PurchaseOrder().InitTransaction();
                    if ("success".equals((String) loJSON.get("result"))) {
                        loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(lsTransactionNo);
                        if ("success".equals((String) loJSON.get("result"))) {
                            loadMaster();
                            initTablePODetail();
                            loadTablePODetail();
                            pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                        } else {
                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                            pnEditMode = EditMode.UNKNOWN;
                        }
                        initButtons(pnEditMode);
                        initFields(pnEditMode);
                    }
                } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                    Logger.getLogger(PurchaseOrder_ConfirmationMPController.class
                            .getName()).log(Level.SEVERE, null, ex);
                    ShowMessageFX.Warning("Error loading data: " + ex.getMessage(), psFormName, null);
                }
            }
        }
    }

    private void tblVwOrderDetails_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.READY) {
            pnTblPODetailRow = tblVwOrderDetails.getSelectionModel().getSelectedIndex();
            ModelPurchaseOrderDetail selectedItem = tblVwOrderDetails.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) {
                clearDetailFields();
                if (selectedItem != null) {
                    if (pnTblPODetailRow >= 0) {
                        loadDetail();
                        if (pnEditMode == EditMode.UPDATE) {
                            if (!tfBrand.getText().isEmpty()) {
                                tfOrderQuantity.requestFocus();
                            }
                        }
                    }
                }
            }
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
}
