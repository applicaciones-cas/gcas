/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
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
public class PurchaseOrder_HistorySPCarController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order History SPCar";
    private LogWrapper logWrapper;
    private int pnEditMode;
    private JSONObject poJSON;
    unloadForm poUnload = new unloadForm();
    private ObservableList<ModelPurchaseOrderDetail> poDetail_data = FXCollections.observableArrayList();
    private int pnTblPODetailRow = -1;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psSupplierID = "";
    private String psReferID = "";
    @FXML
    private AnchorPane apBrowse, apButton;
    @FXML
    private AnchorPane AnchorMain;
    @FXML
    private Button btnBrowse, btnPrint, btnTransHistory, btnClose;
    @FXML
    private Label lblTransactionStatus;
    @FXML
    private TextField tfSearchIndustry, tfSearchCompany, tfSearchSupplier, tfSearchReferenceNo;
    @FXML
    private TextField tfTransactionNo, tfIndustry, tfCompany, tfSupplier, tfDestination, tfReferenceNo,
            tfTerm, tfDiscountRate, tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount;
    @FXML
    private TextField tfBarcode, tfDescription, tfBrand, tfModel, tfColor, tfCategory, tfInventoryType,
            tfMeasure, tfClass, tfAMC, tfROQ, tfRO, tfBO, tfQOH, tfCost, tfRequestQuantity, tfOrderQuantity;
    @FXML
    private CheckBox chkbAdvancePayment;
    @FXML
    private TextArea taRemarks;
    @FXML
    private DatePicker dpTransactionDate, dpExpectedDlvrDate;
    @FXML
    private TableView<ModelPurchaseOrderDetail> tblVwOrderDetails;
    @FXML
    private TableColumn<ModelPurchaseOrderDetail, String> tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail,
            tblDescriptionDetail, tblCostDetail, tblROQDetail, tblRequestQuantityDetail, tblOrderQuantityDetail,
            tblTotalAmountDetail;
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
            poPurchasingController.PurchaseOrder().setTransactionStatus("01234567");
            JSONObject loJSON = new JSONObject();
            loJSON = poPurchasingController.PurchaseOrder().InitTransaction();
            if (!"success".equals(loJSON.get("result"))) {
                ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
            }
            poJSON = poPurchasingController.PurchaseOrder().SearchIndustry(poApp.getIndustry(), true);
            if ("error".equals((String) loJSON.get("result"))) {
                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                return;
            }
            tblVwOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
            String lsIndustryName = "";
            if (poPurchasingController.PurchaseOrder().Master().Industry().getDescription() != null) {
                lsIndustryName = poPurchasingController.PurchaseOrder().Master().Industry().getDescription();
            }
            psIndustryID = poPurchasingController.PurchaseOrder().Master().getIndustryID();
            tfSearchIndustry.setText(lsIndustryName);
            initButtonsClickActions();
            initTextFieldKeyPressed();
            initTextFieldsProperty();
            initTablePODetail();
            tblVwOrderDetails.setOnMouseClicked(this::tblVwOrderDetails_Clicked);
            pnEditMode = EditMode.UNKNOWN;
            initButtons(pnEditMode);
        } catch (ExceptionInInitializerError | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_HistorySPCarController.class.getName()).log(Level.SEVERE, null, ex);
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
            tfReferenceNo.setText(poPurchasingController.PurchaseOrder().Master().getReference());
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
            if (poPurchasingController.PurchaseOrder().Master().getWithAdvPaym() == true) {
                chkbAdvancePayment.setSelected(true);
            } else {
                chkbAdvancePayment.setSelected(false);
            }
            tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesPercentage()));
            tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesAmount()));

        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_HistorySPCarController.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(PurchaseOrder_HistorySPCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(
                btnPrint, btnTransHistory, btnClose, btnBrowse);
        buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
    }

    private void handleButtonAction(ActionEvent event) {
        try {
            JSONObject loJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch (lsButton) {
                case "btnBrowse":
                    loJSON = poPurchasingController.PurchaseOrder().searchTransaction("",
                            psIndustryID,
                            psCompanyID,
                            psSupplierID,
                            psReferID);
                    if ("success".equals((String) loJSON.get("result"))) {
                        loadMaster();
                        loadDetail();
                        loadTablePODetail();
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
                    }
                    break;
                case "btnPrint":
                    loJSON = poPurchasingController.PurchaseOrder().printTransaction();
                    if (!"success".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Print Purchase Order", null);
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
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrder_HistoryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextFieldKeyPressed() {
        List<TextField> loTxtField = Arrays.asList(tfSearchIndustry, tfSearchCompany, tfSearchSupplier,
                tfSearchReferenceNo);

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
                                psIndustryID = poPurchasingController.PurchaseOrder().Master().getIndustryID();
                                tfSearchIndustry.setText(poPurchasingController.PurchaseOrder().Master().Industry().getDescription());
                                break;
                            case "tfSearchCompany":
                                loJSON = poPurchasingController.PurchaseOrder().SearchCompany(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfSearchCompany.setText("");
                                    break;
                                }
                                psCompanyID = poPurchasingController.PurchaseOrder().Master().getCompanyID();
                                tfSearchCompany.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName());
                                break;
                            case "tfSearchSupplier":
                                loJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfSearchSupplier.setText("");
                                    break;
                                }
                                psSupplierID = poPurchasingController.PurchaseOrder().Master().getSupplierID();
                                tfSearchSupplier.setText(poPurchasingController.PurchaseOrder().Master().Supplier().getCompanyName());
                                break;
                        }
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
            Logger.getLogger(PurchaseOrder_HistorySPCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        }
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
        CustomCommonUtil.setText("", tfBarcode, tfDescription, tfBrand, tfModel,
                tfColor, tfCategory, tfInventoryType, tfMeasure, tfClass,
                tfAMC, tfROQ, tfRO, tfBO, tfQOH,
                tfCost, tfRequestQuantity, tfOrderQuantity);
    }

    private void initButtons(int fnEditMode) {
        btnPrint.setVisible(false);
        btnPrint.setManaged(false);
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
                case PurchaseOrderStatus.APPROVED:
                case PurchaseOrderStatus.CONFIRMED:
                    btnPrint.setVisible(true);
                    btnPrint.setManaged(true);
                    break;
            }
        }
    }

    private void loadTablePODetail() {
        poDetail_data.clear();
        try {
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
            Logger.getLogger(PurchaseOrder_HistorySPCarController.class
                    .getName()).log(Level.SEVERE, null, ex);
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

    private void tblVwOrderDetails_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.READY) {
            pnTblPODetailRow = tblVwOrderDetails.getSelectionModel().getSelectedIndex();

            ModelPurchaseOrderDetail selectedItem = tblVwOrderDetails.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 2) {
                clearDetailFields();
                if (selectedItem != null) {
                    if (pnTblPODetailRow >= 0) {
                        loadDetail();
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

    private void initTextFieldsProperty() {
        tfSearchCompany.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poPurchasingController.PurchaseOrder().Master().setCompanyID("");
                    tfSearchCompany.setText("");
                    psCompanyID = "";
                }
            }
        });
        tfSearchSupplier.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poPurchasingController.PurchaseOrder().Master().setSupplierID("");
                    poPurchasingController.PurchaseOrder().Master().setAddressID("");
                    poPurchasingController.PurchaseOrder().Master().setContactID("");
                    tfSearchSupplier.setText("");
                    psSupplierID = "";
                }

            }
        });
        tfSearchReferenceNo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poPurchasingController.PurchaseOrder().Master().setReference("");
                    tfSearchReferenceNo.setText("");
                    psReferID = "";
                }
            }
        });
    }
}
