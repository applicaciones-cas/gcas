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
import java.text.ParseException;
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
import javafx.scene.control.ProgressIndicator;
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
import org.json.simple.JSONArray;
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
    unloadForm poUnload = new unloadForm();
    private ObservableList<ModelPurchaseOrder> poApprovedStockRequest_data = FXCollections.observableArrayList();
    private ObservableList<ModelPurchaseOrderDetail> poDetail_data = FXCollections.observableArrayList();
    private int pnTblStockRequestRow = -1;
    private int pnTblPODetailRow = 0;
    private int pnSTOCK_REQUEST_PAGE = 10;
    @FXML
    private AnchorPane apBrowse, AnchorMaster, AnchorDetails, apTableDetailLoading, apTableStockRequestLoading, AnchorMain;
    @FXML
    private ProgressIndicator piTableDetailLoading, piTableStockRequestLoading;
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
        try {
            poPurchasingController = new PurchaseOrderControllers(poApp, logWrapper);
            JSONObject loJSON = new JSONObject();
            loJSON = poPurchasingController.PurchaseOrder().InitTransaction();
            if (!"success".equals(loJSON.get("result"))) {
                ShowMessageFX.Warning(null, "Search Information", (String) loJSON.get("message"));
            }

            poJSON = poPurchasingController.PurchaseOrder().SearchIndustry(poApp.getIndustry(), true);
            if ("error".equals((String) loJSON.get("result"))) {
                ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                return;
            }
            String lsIndustryName = "";
            if (poPurchasingController.PurchaseOrder().Master().Industry().getDescription() != null) {
                lsIndustryName = poPurchasingController.PurchaseOrder().Master().Industry().getDescription();
            }
            tfIndustry.setText(lsIndustryName);
            loadTableStockRequest();
            Platform.runLater(() -> btnNew.fire());
            initButtonsClickActions();
            initTextFieldFocus();
            initTextAreaFocus();
            initTextFieldKeyPressed();
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
            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        try {
            tfBarcode.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getBarCode());
            tfDescription.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getDescription());
//            tfBrand.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Brand().getDescription());
//            tfModel.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Model().getDescription());
//            tfColor.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Color().getDescription());
//            tfCategory.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Category().getDescription());
//            tfInventoryType.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryType().getDescription());
//            tfMeasure.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Measure().getDescription());
//            tfClass.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getInventoryClassification());
//            tfAMC.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getAverageCost()));
            tfROQ.setText("0");
            tfRO.setText("0");
//            tfBO.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getBackOrderQuantity()));
//            tfQOH.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).InventoryMaster().getQuantityOnHand()));
//            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getUnitPrice()));
            tfRequestQuantity.setText("0");
            tfOrderQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).getQuantity()));
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
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
                    loJSON = poPurchasingController.PurchaseOrder().searchTransaction("", true);
                    if ("success".equals((String) loJSON.get("result"))) {
                        loadMaster();
                        loadDetail();
                        loadTablePODetail();
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    } else {
                        ShowMessageFX.Warning(null, "Search Information", (String) loJSON.get("message"));
                    }
                    break;
                case "btnNew":
                    clearFields();
                    loJSON = poPurchasingController.PurchaseOrder().NewTransaction();
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
                    break;
                case "btnSearch":
                    if (event.getSource() == btnSearch) {
                        Node focusedNode = btnSearch.getScene().getFocusOwner();
                        if (focusedNode instanceof TextField) {
                            TextField focusedTextField = (TextField) focusedNode;
                            String loTextFieldId = focusedTextField.getId();
                            String lsValue = "";
                            if (focusedTextField.getText() == null) {
                                lsValue = "";
                            } else {
                                lsValue = focusedTextField.getText();
                            }

                            // You can now process based on the text field ID
                            switch (loTextFieldId) {
                                case "tfCompany":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfCompany.setText("");
                                        break;
                                    }
                                    tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
                                    break;
                                case "tfSupplier":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfSupplier.setText("");
                                        break;
                                    }
                                    tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                    break;
                                case "tfDestination":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchDestination(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfDestination.setText("");
                                        break;
                                    }
                                    tfDestination.setText(poPurchasingController.PurchaseOrder().Master().Branch().getBranchName());
                                    break;
                                case "tfTerm":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfCompany.setText("");
                                        break;
                                    }
                                    tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Term().getDescription());
                                    break;
                                case "tfBarcode":
                                    break;
                                case "tfDescription":
                                    break;
                                default:
                                    System.out.println("Unknown TextField");
                            }
                            loadTableStockRequest();
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
                                loadTablePODetail();
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
                            poJSON = poPurchasingController.PurchaseOrder().SearchIndustry(poApp.getIndustry(), true);
                            if ("error".equals((String) loJSON.get("result"))) {
                                ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
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
                            loadTableStockRequest();
                            poDetail_data.clear();
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
                    break;
                case "btnPrint":
                    break;
                case "btnRetrieve":
                    loadTableStockRequest();
                    break;
                case "btnTransHistory":
                    break;
                case "btnClose":
                    if (ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to close this form?")) {
                        if (poUnload != null) {
                            poUnload.unloadForm(AnchorMain, poApp, psFormName);
                        } else {
                            ShowMessageFX.Warning(null, "Warning", "Please notify the system administrator to configure the null value at the close button.");
                        }
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
                case "tfReferenceNo":
                    poPurchasingController.PurchaseOrder().Master().setReference(lsValue);
                    break;
                case "tfDiscountRate":
                    break;
                case "tfDiscountAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue) < 0.00) {
                        ShowMessageFX.Warning(null, psFormName, "Invalid Discount Amount");
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
                        ShowMessageFX.Warning(null, psFormName, "Invalid Downpayment Rates");
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
                        ShowMessageFX.Warning(null, psFormName, "Invalid Downpayment Amount");
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Master().setDownPaymentRatesAmount(Double.valueOf(lsValue.replace(",", "")));
                    tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfCost":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, psFormName, "Invalid Cost Amount");
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).setUnitPrice(Double.valueOf(lsValue.replace(",", "")));
                    tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfOrderQuantity":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    if (Integer.parseInt(lsValue) < 0) {
                        ShowMessageFX.Warning(null, psFormName, "Invalid Order Quantity");
                        return;
                    }
                    poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).setQuantity(Integer.valueOf(lsValue));
                    tfOrderQuantity.setText(lsValue);
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
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfCompany.setText("");
                                        break;
                                    }
                                    tfCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
                                    break;

                                case "tfSupplier":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfSupplier.setText("");
                                        break;
                                    }
                                    tfSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                    break;
                                case "tfDestination":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchDestination(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfDestination.setText("");
                                        break;
                                    }
                                    tfDestination.setText(poPurchasingController.PurchaseOrder().Master().Branch().getBranchName());
                                    break;
                                case "tfTerm":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchTerm(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfTerm.setText("");
                                        break;
                                    }
                                    tfTerm.setText(poPurchasingController.PurchaseOrder().Master().Term().getDescription());
                                    break;
                                case "tfBarcode":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchBarcode(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfBarcode.setText("");
                                        break;
                                    }
                                    tfBarcode.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getBarCode());
                                    break;
                                case "tfDescription":
                                    loJSON = poPurchasingController.PurchaseOrder().SearchBarcodeDescription(lsValue, false);
                                    if ("error".equals(loJSON.get("result"))) {
                                        ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                        tfDescription.setText("");
                                        break;
                                    }
                                    tfDescription.setText(poPurchasingController.PurchaseOrder().Detail(pnTblPODetailRow).Inventory().getDescription());
                                    break;
                            }
                            loadTableStockRequest();
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

        if (tblVwStockRequest.getItems().isEmpty()) {
            pagination.setVisible(false);
            pagination.setManaged(false);
        }

        piTableDetailLoading.setVisible(false);
        piTableDetailLoading.setManaged(false);
        piTableStockRequestLoading.setVisible(false);
        piTableDetailLoading.setManaged(false);
        apTableDetailLoading.setVisible(false);
        apTableDetailLoading.setManaged(false);
        apTableStockRequestLoading.setVisible(false);
        apTableStockRequestLoading.setManaged(false);
    }

    private void loadTableStockRequest() {
        try {
            JSONObject loJSON = poPurchasingController.PurchaseOrder().getApprovedStockRequests();
            if ("success".equals(loJSON.get("result"))) {
                JSONArray ljaApprovedRequests = (JSONArray) loJSON.get("data");
                poApprovedStockRequest_data.clear();
                for (Object requestObj : ljaApprovedRequests) {
                    JSONObject obj = (JSONObject) requestObj;
                    ModelPurchaseOrder loPOModel = new ModelPurchaseOrder(
                            String.valueOf(poApprovedStockRequest_data.size() + 1), // Auto-increment row number
                            obj.get("sBranchNm") != null ? obj.get("sBranchNm").toString() : "",
                            obj.get("dTransact") != null ? obj.get("dTransact").toString() : "",
                            obj.get("sReferNox") != null ? obj.get("sReferNox").toString() : "",
                            obj.get("total_details") != null ? obj.get("total_details").toString() : "",
                            obj.get("sTransNox") != null ? obj.get("sTransNox").toString() : "",
                            "",
                            "",
                            "",
                            ""
                    );
                    poApprovedStockRequest_data.add(loPOModel);
                }
                tblVwStockRequest.setItems(poApprovedStockRequest_data);
                loadTableStockRequestPagination(); // Call pagination
            }
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
//    private void loadTableStockRequest() {
//        poApprovedStockRequest_data.clear();
//        try {
//            JSONObject loJSON = poPurchasingController.PurchaseOrder().getApprovedStockRequests();
//            if ("success".equals(loJSON.get("result"))) {
//                for (int lnCntr = 0; lnCntr <= poPurchasingController.PurchaseOrder().getInventoryStockRequestCount() - 1; lnCntr++) {
//                    poApprovedStockRequest_data.add(new ModelPurchaseOrder(
//                            String.valueOf(lnCntr + 1),
//                            poPurchasingController.PurchaseOrder().InventoryStockRequestMaster().Branch().getBranchName(),
//                            //                                CustomCommonUtil.convertLongDateStringToShort(SQLUtil.dateFormat(poPurchasingController.PurchaseOrder().InventoryStockRequestMaster().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)),
//                            "",
//                            poPurchasingController.PurchaseOrder().InventoryStockRequestMaster().getReferenceNo(),
//                            String.valueOf(poPurchasingController.PurchaseOrder().InventoryStockRequestMaster().getEntryNo()),
//                            poPurchasingController.PurchaseOrder().InventoryStockRequestMaster().getTransactionNo(),
//                            "",
//                            "",
//                            "",
//                            ""
//                    ));
//                    tblVwStockRequest.setItems(poApprovedStockRequest_data);
//                }
//            }
//        } catch (SQLException | GuanzonException ex) {
//            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

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
    }

    private void loadTableStockRequestPagination() {
        int totalItems = poApprovedStockRequest_data.size();
        int totalPages = (int) Math.ceil((double) totalItems / pnSTOCK_REQUEST_PAGE);

        pagination.setPageCount(totalPages);
        pagination.setMaxPageIndicatorCount(Math.min(5, totalPages));

    }

    private void loadTablePODetail() {
        poDetail_data.clear();
        try {
            for (int lnCntr = 0; lnCntr <= poPurchasingController.PurchaseOrder().getDetailCount() - 1; lnCntr++) {
                poDetail_data.add(new ModelPurchaseOrderDetail(
                        String.valueOf(lnCntr + 1),
                        poPurchasingController.PurchaseOrder().Detail(lnCntr).getSouceNo(),
                        poPurchasingController.PurchaseOrder().Detail(lnCntr).Inventory().getBarCode(),
                        poPurchasingController.PurchaseOrder().Detail(lnCntr).Inventory().getDescription(),
                        String.valueOf(poPurchasingController.PurchaseOrder().Detail(lnCntr).Inventory().getCost()),
                        "", "", "", "", ""
                ));
            }
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblVwOrderDetails.setItems(poDetail_data);
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
            if (pnTblStockRequestRow < 0 || pnTblStockRequestRow >= tblVwStockRequest.getItems().size()) {
                ShowMessageFX.Warning(null, "Warning", "Please select valid approved stock request information.");
                return;
            }
            if (event.getClickCount() == 1) {
                ModelPurchaseOrder loSelectedStockRequest = (ModelPurchaseOrder) tblVwStockRequest.getSelectionModel().getSelectedItem();
                if (loSelectedStockRequest != null) {
                    String lsTransactionNo = loSelectedStockRequest.getIndex06();
                    boolean alreadyExists = poDetail_data.stream()
                            .anyMatch(detail -> detail.getIndex02().equals(lsTransactionNo));
                    if (alreadyExists) {
                        ShowMessageFX.Warning(null, "Warning", "This stock request has already been selected.");
                        return;
                    }
                    try {
                        JSONObject loJSON = poPurchasingController.PurchaseOrder().addStockRequestOrdersToPODetail(lsTransactionNo);
                        if ("success".equals(loJSON.get("result"))) {
                            if (poPurchasingController.PurchaseOrder().getDetailCount() > 0) {
                                poDetail_data.clear();
                                for (int lnCtr = 0; lnCtr < poPurchasingController.PurchaseOrder().getDetailCount() - 1; lnCtr++) {
                                    poDetail_data.add(new ModelPurchaseOrderDetail(
                                            String.valueOf(lnCtr + 1),
                                            poPurchasingController.PurchaseOrder().Detail(lnCtr).getSouceNo(),
                                            poPurchasingController.PurchaseOrder().Detail(lnCtr).Inventory().getBarCode(),
                                            poPurchasingController.PurchaseOrder().Detail(lnCtr).Inventory().getDescription(),
                                            String.valueOf(poPurchasingController.PurchaseOrder().Detail(lnCtr).Inventory().getCost()),
                                            "", "", "", "", ""
                                    ));
                                }
                                tblVwOrderDetails.setItems(poDetail_data); // Update table
                            }
                        } else {
                            ShowMessageFX.Warning(null, psFormName, loJSON.get("message").toString());

                        }
                    } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                        Logger.getLogger(PurchaseOrder_EntryController.class
                                .getName()).log(Level.SEVERE, null, ex);
                        ShowMessageFX.Warning(null, psFormName, "Error loading data: " + ex.getMessage());
                    }
                }

            }
        }
    }

    private void tblVwOrderDetails_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.READY) {
            pnTblPODetailRow = tblVwOrderDetails.getSelectionModel().getSelectedIndex();

            if (pnTblPODetailRow < 0 || pnTblPODetailRow >= tblVwOrderDetails.getItems().size()) {
                ShowMessageFX.Warning(null, "Warning", "Please select valid order item information.");
                return;
            }
            ModelPurchaseOrderDetail selectedItem = tblVwOrderDetails.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) {
                if (selectedItem != null) {
                    loadDetail();
                    if (!tfBarcode.getText().isEmpty()) {
//                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                        tfOrderQuantity.requestFocus();
//                        }
                    }
                }
            }
        }
    }
}
