/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.tf to change this license
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

/**
 * FXML Controller class
 *
 * @author User
 */
public class PurchaseOrder_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order";
    private LogWrapper logWrapper;
    private int pnEditMode;
    private JSONObject poJSON;
    private ObservableList<ModelPurchaseOrder> poApprovedStockRequest_data = FXCollections.observableArrayList();
    private ObservableList<ModelPurchaseOrderDetail> poDetail_data = FXCollections.observableArrayList();
    private int pnTblStockRequestRow = 0;
    private int pnTblPODetailRow = 0;
    private int pnSTOCK_REQUEST_PAGE = 0;
    @FXML
    private AnchorPane apBrowse, AnchorMaster, AnchorDetails;
    @FXML
    private AnchorPane apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel,
            btnPrint, btnRetrieve, btnTransHistory, btnClose;
    @FXML
    private Label lblTransactionStatus;
    @FXML
    private CheckBox chkbAdvancePayment;
    @FXML
    private TextField tfTransactionNo, tfIndustry, tfCompany, tfSupplier, tfAdvancePAmount,
            tfReferenceNo, tfTerm, tfDiscountAmount, tfTotalAmount,
            tfDestination, tfAdvancePRate, tfDiscountRate, tfDescription,
            tfModel, tfClass, tfAMC, tfROQ, tfBarcode, tfCategory, tfColor, tfMeasure, tfBO, tfRO,
            tfQOH, tfInventoryType, tfCost, tfRequestQuantity, tfOrderQuantity, tfBrand;
    @FXML
    private TextArea taRemarks;
    @FXML
    private DatePicker dpTransactionDate, dpExpectedDlvrDate;
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
        poPurchasingController = new PurchaseOrderControllers(poApp, logWrapper);
        JSONObject loJSON = new JSONObject();
        loJSON = poPurchasingController.PurchaseOrder().InitTransaction();
        if (!"success".equals(loJSON.get("result"))) {
            ShowMessageFX.Warning(null, "Search Information", (String) loJSON.get("message"));
        }

        Platform.runLater(() -> btnNew.fire());
        initButtonsClickActions();
        initTextFieldFocus();
        initTextAreaFocus();
        initTextFieldKeyPressed();
        initCheckBoxActions();
        initDatePickerActions();
        initTextFieldPattern();
        initTableStockRequest();
        loadTableStockRequestPagination();
        initTablePODetail();
        tblVwStockRequest.setOnMouseClicked(this::tblVwStockRequest_Clicked);
        tblVwOrderDetails.setOnMouseClicked(this::tblVwOrderDetails_Clicked);
        initButtons(pnEditMode);
        initFields(pnEditMode);
    }

    private void loadMaster() {
        tfTransactionNo.setText(poPurchasingController.PurchaseOrder().Master().getTransactionNo());

        String lsStatus = "";
        switch (poPurchasingController.PurchaseOrder().Master().getTransactionStatus()) {
            case PurchaseOrderStatus.OPEN:
                lsStatus = "OPEN";
                break;
            case PurchaseOrderStatus.CONFIRMED:
                lsStatus = "CONFIRMED";
                break;
            case PurchaseOrderStatus.PROCESSED:
                lsStatus = "APPROVED";
                break;
            case PurchaseOrderStatus.RETURN:
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

        taRemarks.setText(poPurchasingController.PurchaseOrder().Master().getRemarks());

        dpExpectedDlvrDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                SQLUtil.dateFormat(poPurchasingController.PurchaseOrder().Master().getExpectedDate(), SQLUtil.FORMAT_SHORT_DATE)));
        tfDiscountRate.setText(poPurchasingController.PurchaseOrder().Master().getDiscount().toString());
        tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDiscount()));
        tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesPercentage()));
        tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesAmount()));
        taRemarks.setText(poPurchasingController.PurchaseOrder().Master().getRemarks());
        tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesAmount()));

    }

    // this is for detail fields
    private void loadDetail() {
        tfBarcode.setText("");
        String lsDescription = "";
        tfDescription.setText(lsDescription);
        tfBrand.setText("");
        tfModel.setText("");
        tfColor.setText("");
        tfCategory.setText("");
        tfInventoryType.setText("");
        tfMeasure.setText("");
        tfClass.setText("");
        tfAMC.setText("");
        tfROQ.setText("");
        tfRO.setText("");
        tfBO.setText("");
        tfQOH.setText("");
        tfCost.setText("");
        tfRequestQuantity.setText("");
        tfOrderQuantity.setText("");
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
                    loJSON = poPurchasingController.PurchaseOrder().searchTransaction("", true);
                    if ("success".equals((String) loJSON.get("result"))) {
                        loadMaster();
                        loadDetail();
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    } else {
                        ShowMessageFX.Warning(null, "Search Information", (String) loJSON.get("message"));
                    }
                    break;
                case "btnNew":
                    clearFields();
                    poPurchasingController.PurchaseOrder().Master().setIndustryID(poApp.getIndustry());
                    loJSON = poPurchasingController.PurchaseOrder().NewTransaction();
                    if ("success".equals((String) loJSON.get("result"))) {
                        loadMaster();
                        loadDetail();
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
                    break;
                case "btnSearch":
                    if (event.getSource() == btnSearch) {
                        Node focusedNode = btnSearch.getScene().getFocusOwner(); // Get the currently focused node
                        if (focusedNode instanceof TextField) {
                            TextField focusedTextField = (TextField) focusedNode;
                            String loTextFieldId = focusedTextField.getId(); // Get the ID of the focused TextField
                            String value = focusedTextField.getText(); // Get the value of the focused TextField

                            // You can now process based on the text field ID
                            switch (loTextFieldId) {
                                case "tfCompany":
                                    System.out.println("Searching by Company: " + value);
                                    break;
                                case "tfSupplier":
                                    System.out.println("Searching by Supplier: " + value);
                                    break;
                                case "tfDestination":
                                    System.out.println("Searching by Destination: " + value);
                                    break;
                                case "tfTerm":
                                    System.out.println("Searching by Term: " + value);
                                    break;
                                case "tfBarcode":
                                    System.out.println("Searching by Barcode: " + value);
                                    break;
                                case "tfDescription":
                                    System.out.println("Searching by Description: " + value);
                                    break;
                                default:
                                    System.out.println("Unknown TextField");
                            }
                        } else {
                            ShowMessageFX.Warning(null, psFormName, "No TextField is currently focused to search");
                        }
                    }
                    break;
                case "btnSave":
                    if (ShowMessageFX.YesNo(null, "Save confirmation", "Are you sure, do you want to save?")) {
                        loJSON = poPurchasingController.PurchaseOrder().SaveTransaction();
                        if ("success".equals((String) loJSON.get("result"))) {
                            ShowMessageFX.Information(null, psFormName, (String) loJSON.get("message"));
                            //transcode
                            loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                            if ("success".equals((String) loJSON.get("result"))) {
                                loadMaster();
                                loadDetail();
                                pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                            }
                            if (ShowMessageFX.YesNo(null, "Confirmation", "Are you sure, do you want to confirm this information?")) {

                            }
                        } else {
                            ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                            return;
                        }
                    } else {
                        return;
                    }
                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo(null, "Cancel Confirmation", "Are you sure you want to cancel?")) {
                        if (pnEditMode == EditMode.ADDNEW) {
                            clearFields();
                            pnEditMode = EditMode.UNKNOWN;
                        } else {
                            loJSON = poPurchasingController.PurchaseOrder().OpenTransaction(poPurchasingController.PurchaseOrder().Master().getTransactionNo());
                            if ("success".equals((String) loJSON.get("result"))) {
                                loadMaster();
                                loadDetail();
                                pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                            }
                        }
                    }
                    break;
                case "btnPrint":
                    break;
                case "btnRetrieve":
                    break;
                case "btnTransHistory":
                    break;
                case "btnClose":
                    if (ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to close this form?")) {
                        CommonUtils.closeStage(btnClose);
                    }
                    break;
                default:
                    ShowMessageFX.Warning(null, psFormName, "Please contact admin to assist about no button available");
                    break;
            }
            initButtons(pnEditMode);
            initFields(pnEditMode);
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextFieldFocus() {
        List<TextField> loTxtField = Arrays.asList(tfReferenceNo, tfDiscountRate, tfDiscountAmount,
                tfAdvancePRate, tfAdvancePAmount, tfCost, tfOrderQuantity);
        loTxtField.forEach(tf -> tf.focusedProperty().addListener(txtField_Focus));
    }

    private void initTextAreaFocus() {
        taRemarks.focusedProperty().addListener(txtField_Focus);
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
                case "tfReferenceNo":
                    poPurchasingController.PurchaseOrder().Master().setReference(lsValue);
                    break;
                case "tfDiscountRate":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue) < 0.00 || Double.parseDouble(lsValue) > 100) {
                        ShowMessageFX.Warning(null, psFormName, "Invalid Downpayment Rates");
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesPercentage(Double.valueOf(lsValue));
                    tfDiscountRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfDiscountAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, psFormName, "Invalid Downpayment Amount");
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesPercentage(Double.valueOf(lsValue.replace(",", "")));
                    tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfAdvancePRate":
                    break;
                case "tfAdvancePAmount":
                    break;
                case "tfCost":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, psFormName, "Invalid Cost Amount");
                        return;
                    }
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
        List<TextField> loTxtField = Arrays.asList(tfAdvancePAmount, tfCompany, tfSupplier,
                tfReferenceNo, tfTerm, tfDiscountRate, tfDiscountAmount, tfTotalAmount,
                tfDestination, tfAdvancePRate, tfDescription,
                tfBarcode, tfBO, tfRO,
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
                                        ShowMessageFX.Warning("message", psFormName, "");
                                        tfCompany.setText("");
                                        break;
                                    }
                                    tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
                                    break;

                                case "tfSupplier":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning("message", psFormName, "");
                                        tfSupplier.setText("");
                                        break;
                                    }
                                    tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                    break;
                                case "tfDestination":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchDestination(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning("message", psFormName, "");
                                        tfDestination.setText("");
                                        break;
                                    }
                                    tfDestination.setText(poPurchasingController.PurchaseOrder().Master().Branch().getBranchName());
                                    break;
                                case "tfTerm":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchTerm(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning("message", psFormName, "");
                                        tfTerm.setText("");
                                        break;
                                    }
                                    tfTerm.setText(poPurchasingController.PurchaseOrder().Master().Term().getDescription());
                                    break;
                                case "tfBarcode":
//                                loJSON = poPurchasingController.PurchaseOrder().searchRecord(lsValue, true);
//                                if (!"error".equals(loJSON.get("result"))) {
//                                    tfBarcode.setText(poPurchasingController.PurchaseOrder().Company().getModel().getCompanyName());
//                                } else {
//                                    ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
//                                    tfBarcode.setText("");
//                                    return;
//                                }
                                    break;
                                case "tfDescription":
//                                loJSON = poPurchasingController.PurchaseOrder().searchRecord(lsValue, true);
//                                if (!"error".equals(loJSON.get("result"))) {
//                                    tfDescription.setText(poPurchasingController.PurchaseOrder().Company().getModel().getCompanyName());
//                                } else {
//                                    ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
//                                    tfDescription.setText("");
//                                    return;
//                                }
                                    break;
                            }
//                        loadApprovedStockRequest();
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
                Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
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
                    poPurchasingController.PurchaseOrder().Master().setTransactionDate(SQLUtil.toDate(dpExpectedDlvrDate.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
                }
            }
        });
    }

    private void initCheckBoxActions() {
        if (chkbAdvancePayment.isSelected()) {
            chkbAdvancePayment.setSelected(true);
        } else {
            chkbAdvancePayment.setSelected(false);
        }
        initFields(pnEditMode);
    }

    private void clearFields() {
        /* Master Fields*/
        tfTransactionNo.setText("");
        dpTransactionDate.setValue(null);
        tfCompany.setText("");
        tfSupplier.setText("");
        tfDestination.setText("");
        tfAdvancePAmount.setText("");
        dpExpectedDlvrDate.setValue(null);
        tfReferenceNo.setText("");
        taRemarks.setText("");
        tfTerm.setText("");
        tfDiscountRate.setText("");
        tfDiscountRate.setText("");
        chkbAdvancePayment.setSelected(false);
        tfAdvancePRate.setText("");
        tfDiscountAmount.setText("");
        tfDiscountAmount.setText("");
        /* Detail Fields*/
        tfBarcode.setText("");
        tfDescription.setText("");
        tfBrand.setText("");
        tfModel.setText("");
        tfColor.setText("");
        tfCategory.setText("");
        tfInventoryType.setText("");
        tfMeasure.setText("");
        tfClass.setText("");
        tfAMC.setText("");
        tfROQ.setText("");
        tfRO.setText("");
        tfBO.setText("");
        tfQOH.setText("");
        tfCost.setText("");
        tfRequestQuantity.setText("");
        tfOrderQuantity.setText("");
    }

    private void clearTables() {

    }

    private void initButtons(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);

        btnBrowse.setVisible(!lbShow);
        btnBrowse.setManaged(!lbShow);
        btnClose.setVisible(!lbShow);
        btnClose.setManaged(!lbShow);
        btnNew.setVisible(!lbShow);
        btnNew.setManaged(!lbShow);
        btnUpdate.setVisible(!lbShow);
        btnUpdate.setManaged(!lbShow);

        btnSearch.setVisible(lbShow);
        btnSearch.setManaged(lbShow);
        btnSave.setVisible(lbShow);
        btnSave.setManaged(lbShow);
        btnCancel.setVisible(lbShow);
        btnCancel.setManaged(lbShow);

        btnPrint.setVisible(false);
        btnPrint.setManaged(false);

        btnTransHistory.setVisible(fnEditMode != EditMode.ADDNEW && fnEditMode != EditMode.UNKNOWN);
        btnTransHistory.setManaged(fnEditMode != EditMode.ADDNEW && fnEditMode != EditMode.UNKNOWN);

        switch (poPurchasingController.PurchaseOrder().Master().getTransactionStatus()) {
            case PurchaseOrderStatus.OPEN:
            case PurchaseOrderStatus.PROCESSED:
                btnPrint.setVisible(true);
                btnPrint.setManaged(true);
                break;
            case PurchaseOrderStatus.CANCELLED:
            case PurchaseOrderStatus.VOID:
                btnUpdate.setVisible(false);
                btnUpdate.setManaged(false);
                btnSearch.setVisible(false);
                btnSearch.setManaged(false);
                btnSave.setVisible(false);
                btnSave.setManaged(false);
                btnCancel.setVisible(false);
                btnCancel.setManaged(false);
                break;
        }
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);

        /* Master Fields*/
        AnchorMaster.setDisable(!lbShow);
        AnchorDetails.setDisable(!lbShow);

        tfDiscountRate.setDisable(true);
        tfDiscountAmount.setDisable(true);

        if (!tfReferenceNo.getText().isEmpty()) {
            dpTransactionDate.setDisable(!lbShow);
        }

        if (chkbAdvancePayment.isSelected()) {
            tfAdvancePRate.setDisable(!lbShow);
            tfAdvancePAmount.setDisable(!lbShow);
        }
    }

    private void loadTableStockRequest() {

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

        if (tblVwStockRequest != null) {
            tblVwStockRequest.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
                TableHeaderRow header = (TableHeaderRow) tblVwStockRequest.lookup("TableHeaderRow");
                if (header != null) {
                    header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                        header.setReordering(false);
                    });
                }
            });

            if (poApprovedStockRequest_data != null) {
                tblVwStockRequest.setItems(poApprovedStockRequest_data);
            } else {
                tblVwStockRequest.setItems(FXCollections.observableArrayList());
            }

            tblVwStockRequest.autosize();
        }
    }

    private void loadTableStockRequestPagination() {
//        List<String> lsTestDummy = new ArrayList<>();
//        int lnRowPerPage = 50;
//        int totalPages = (int) Math.ceil((double) lsTestDummy.size() / lnRowPerPage);
//        pagination.setMaxPageIndicatorCount(Math.min(totalPages, 5)); // Show max 5 indicators
    }

    private void loadTablePODetail() {

    }

    private void initTablePODetail() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            tblVwOrderDetails.setEditable(true);
        } else {
            tblVwOrderDetails.setEditable(false);
        }
        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblOrderNoDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblBarcodeDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblDescriptionDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblCostDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblROQDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblRequestQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblOrderQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index08"));
        tblTotalAmountDetail.setCellValueFactory(new PropertyValueFactory<>("index09"));
        tblCostDetail.setCellValueFactory(new PropertyValueFactory<>("index10"));

        if (tblVwStockRequest != null) {
            tblVwStockRequest.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
                TableHeaderRow header = (TableHeaderRow) tblVwStockRequest.lookup("TableHeaderRow");
                if (header != null) {
                    header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                        header.setReordering(false);
                    });
                }
            });
            tblVwOrderDetails.setItems(poDetail_data);
            tblVwOrderDetails.getSelectionModel().select(pnTblStockRequestRow + 1);
            tblVwOrderDetails.autosize();
        }
    }

    private void tblVwStockRequest_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            pnTblStockRequestRow = tblVwStockRequest.getSelectionModel().getSelectedIndex();
            if (pnTblStockRequestRow < 0 || pnTblStockRequestRow >= tblVwStockRequest.getItems().size()) {
                ShowMessageFX.Warning(null, "Warning", "Please select valid approved stock request information.");
                return;
            }
            if (event.getClickCount() == 1) {

            }
        }
    }

    private void tblVwOrderDetails_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            pnTblPODetailRow = tblVwOrderDetails.getSelectionModel().getSelectedIndex();
            if (pnTblPODetailRow < 0 || pnTblPODetailRow >= tblVwOrderDetails.getItems().size()) {
                ShowMessageFX.Warning(null, "Warning", "Please select valid order item information.");
                return;
            }
            if (event.getClickCount() == 2) {

            }
        }
    }
}
