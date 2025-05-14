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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PurchaseOrder_HistoryAppliancesController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order History Appliances";
    private LogWrapper logWrapper;
    private int pnEditMode;
    unloadForm poUnload = new unloadForm();
    private JSONObject poJSON;
    private ObservableList<ModelPurchaseOrderDetail> detail_data = FXCollections.observableArrayList();
    private int pnTblDetailRow = -1;

    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psSupplierID = "";
    private String psCategoryID = "";
    private String psReferID = "";
    @FXML
    private AnchorPane AnchorMaster, AnchorDetails, AnchorMain, apBrowse, apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnPrint, btnTransHistory, btnClose;
    @FXML
    private TextField tfSearchSupplier, tfSearchReferenceNo;
    @FXML
    private TextField tfTransactionNo, tfSupplier, tfDestination, tfReferenceNo,
            tfTerm, tfDiscountRate, tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount, tfNetAmount;
    @FXML
    private Label lblTransactionStatus, lblSource;
    @FXML
    private CheckBox chkbAdvancePayment;
    @FXML
    private DatePicker dpTransactionDate, dpExpectedDlvrDate;
    @FXML
    private TextField tfBrand, tfModel, tfBarcode, tfDescription, tfVariant, tfInventoryType, tfColor, tfClass, tfAMC, tfROQ,
            tfRO, tfBO, tfQOH, tfCost, tfRequestQuantity, tfOrderQuantity;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView<ModelPurchaseOrderDetail> tblVwOrderDetails;
    @FXML
    private TableColumn<ModelPurchaseOrderDetail, String> tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail,
            tblCostDetail, tblROQDetail, tblRequestQuantityDetail, tblOrderQuantityDetail, tblTotalAmountDetail;

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
    public void setCategoryID(String fsValue) {
        psCategoryID = fsValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            poPurchasingController = new PurchaseOrderControllers(poApp, logWrapper);
            poPurchasingController.PurchaseOrder().setTransactionStatus(
                    PurchaseOrderStatus.OPEN
                    + PurchaseOrderStatus.CONFIRMED
                    + PurchaseOrderStatus.RETURNED
                    + PurchaseOrderStatus.APPROVED
                    + PurchaseOrderStatus.CANCELLED
                    + PurchaseOrderStatus.VOID
                    + PurchaseOrderStatus.RETURNED
                    + PurchaseOrderStatus.POSTED
                    + PurchaseOrderStatus.PROCESSED);

            poJSON = poPurchasingController.PurchaseOrder().InitTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                ShowMessageFX.Warning((String) poJSON.get("message"), "Search Information", null);
            }

            Platform.runLater((() -> {
                poPurchasingController.PurchaseOrder().Master().setIndustryID(psIndustryID);
                poPurchasingController.PurchaseOrder().Master().setCompanyID(psCompanyID);
                poPurchasingController.PurchaseOrder().Master().setCategoryCode(psCategoryID);
                loadRecordSearch();
            }));

            initButtonsClickActions();
            initTextFieldKeyPressed();
            initTextFieldsProperty();
            initTableDetail();
            tblVwOrderDetails.setOnMouseClicked(this::tblVwDetail_Clicked);
            pnEditMode = EditMode.UNKNOWN;
            initButtons(pnEditMode);
        } catch (ExceptionInInitializerError ex) {
            Logger.getLogger(PurchaseOrder_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadRecordSearch() {
        try {
            lblSource.setText(poPurchasingController.PurchaseOrder().Master().Company().getCompanyName() + " - " + poPurchasingController.PurchaseOrder().Master().Industry().getDescription());
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadRecordMaster() {
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
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getAdditionalDiscount()));
            if (poPurchasingController.PurchaseOrder().Master().getWithAdvPaym() == true) {
                chkbAdvancePayment.setSelected(true);
            } else {
                chkbAdvancePayment.setSelected(false);
            }
            tfAdvancePRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesPercentage()));
            tfAdvancePAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getDownPaymentRatesAmount()));
            tfTotalAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getTranTotal()));
            tfNetAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Master().getNetTotal()));
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void loadRecordDetail() {
        try {
            if (pnTblDetailRow >= 0) {
                String lsBrand = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Brand().getDescription() != null) {
                    lsBrand = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Brand().getDescription();
                }
                tfBrand.setText(lsBrand);

                String lsModel = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Model().getDescription() != null) {
                    lsModel = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Model().getDescription();
                }
                tfModel.setText(lsModel);
                String lsBarcode = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().getBarCode() != null) {
                    lsBarcode = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().getBarCode();
                }
                tfBarcode.setText(lsBarcode);

                String lsDescription = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().getDescription() != null) {
                    lsDescription = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().getDescription();
                }
                tfDescription.setText(lsDescription);

                String lsVariant = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Variant().getDescription() != null) {
                    lsVariant = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Variant().getDescription();
                }
                tfVariant.setText(lsVariant);

                String lsInventoryType = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().InventoryType().getDescription() != null) {
                    lsInventoryType = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().InventoryType().getDescription();
                }
                tfInventoryType.setText(lsInventoryType);

                String lsColor = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Color().getDescription() != null) {
                    lsColor = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).Inventory().Color().getDescription();
                }
                tfColor.setText(lsColor);

                String lsClass = "";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InventoryMaster().getInventoryClassification() != null) {
                    lsClass = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InventoryMaster().getInventoryClassification();
                }
                tfClass.setText(lsClass);

                String lsAMC = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InventoryMaster().getAverageCost() != 0) {
                    lsAMC = CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InventoryMaster().getAverageCost());
                }
                tfAMC.setText(lsAMC);

                tfROQ.setText("0");

                String lsRO = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getReceived() != 0) {
                    lsRO = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getReceived());
                }
                tfRO.setText(lsRO);

                String lsBO = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getBackOrder() != 0) {
                    lsBO = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getBackOrder());
                }
                tfBO.setText(lsBO);

                String lsQOH = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getQuantityOnHand() != 0) {
                    lsQOH = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getQuantityOnHand());
                }
                tfQOH.setText(lsQOH);

                String lsCost = "0.00";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).getUnitPrice() != null) {
                    lsCost = CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).getUnitPrice());
                }
                tfCost.setText(lsCost);

                //                int lnRequestQuantity = 0;
//                lnRequestQuantity = poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getApproved() - (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getPurchase() + poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getIssued());
                tfRequestQuantity.setText(String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).InvStockRequestDetail().getApproved()));

                String lsOrderQuantity = "0";
                if (poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).getQuantity() != null) {
                    lsOrderQuantity = String.valueOf(poPurchasingController.PurchaseOrder().Detail(pnTblDetailRow).getQuantity());
                }
                tfOrderQuantity.setText(lsOrderQuantity);
            }
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_EntryMPController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(
                btnPrint, btnTransHistory, btnClose, btnBrowse);
        buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
    }

    private void handleButtonAction(ActionEvent event) {
        try {
            poJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch (lsButton) {
                case "btnBrowse":
                    poJSON = poPurchasingController.PurchaseOrder().SearchTransaction("",
                            psSupplierID,
                            psReferID);
                    if ("success".equals((String) poJSON.get("result"))) {
                        clearDetailFields();
                        pnTblDetailRow = -1;
                        loadRecordMaster();
                        loadRecordDetail();
                        loadTableDetail();
                        pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                    } else {
                        ShowMessageFX.Warning((String) poJSON.get("message"), "Search Information", null);
                    }
                    break;
                case "btnPrint":
                    poJSON = poPurchasingController.PurchaseOrder().printTransaction();
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), "Print Purchase Order", null);
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
            Logger.getLogger(PurchaseOrder_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTextFieldKeyPressed() {
        List<TextField> loTxtField = Arrays.asList(tfSearchSupplier,
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
        poJSON = new JSONObject();
        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                    case F3:
                        switch (txtFieldID) {
                            case "tfSearchSupplier":
                                poJSON = poPurchasingController.PurchaseOrder().SearchSupplier(lsValue, false);
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
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
            Logger.getLogger(PurchaseOrder_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearMasterFields() {
        /* Master Fields*/
        pnTblDetailRow = -1;
        dpTransactionDate.setValue(null);
        dpExpectedDlvrDate.setValue(null);
        taRemarks.setText("");
        CustomCommonUtil.setSelected(false, chkbAdvancePayment);
        CustomCommonUtil.setText("", tfTransactionNo, tfSupplier,
                tfDestination, tfReferenceNo, tfTerm, tfDiscountRate,
                tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount);
    }

    private void clearDetailFields() {
        /* Detail Fields*/
        CustomCommonUtil.setText("", tfBrand, tfModel, tfBarcode, tfDescription, tfBrand, tfVariant,
                tfColor, tfInventoryType, tfClass,
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

    private void loadTableDetail() {
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
                    List<ModelPurchaseOrderDetail> detailsList = new ArrayList<>();

                    for (int lnCtr = 0; lnCtr < detailCount; lnCtr++) {
                        Model_PO_Detail orderDetail = poPurchasingController.PurchaseOrder().Detail(lnCtr);
                        double lnTotalAmount = orderDetail.Inventory().getCost().doubleValue() * orderDetail.getQuantity().doubleValue();
                        int lnRequestQuantity = 0;
                        String status = "0";
                        int lnTotalQty = 0;
                        lnRequestQuantity = poPurchasingController.PurchaseOrder().Detail(lnCtr).InvStockRequestDetail().getApproved();
                        lnTotalQty = (poPurchasingController.PurchaseOrder().Detail(lnCtr).InvStockRequestDetail().getPurchase()
                                + poPurchasingController.PurchaseOrder().Detail(lnCtr).InvStockRequestDetail().getIssued()
                                + poPurchasingController.PurchaseOrder().Detail(lnCtr).InvStockRequestDetail().getCancelled());
                        if (!poPurchasingController.PurchaseOrder().Detail(lnCtr).getSouceNo().isEmpty()) {
                            if (lnRequestQuantity != lnTotalQty) {
                                status = "1";
                            }
                        }
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
                                status
                        ));
                    }
                    Platform.runLater(() -> {
                        detail_data.setAll(detailsList); // Properly update list
                        tblVwOrderDetails.setItems(detail_data);
                    });

                    return detailsList;

                } catch (GuanzonException | SQLException ex) {
                    Logger.getLogger(PurchaseOrder_HistoryAppliancesController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void initTableDetail() {
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
        initTableHighlithers();

    }

    private void initTableHighlithers() {
        tblVwOrderDetails.setRowFactory(tv -> new TableRow<ModelPurchaseOrderDetail>() {
            @Override
            protected void updateItem(ModelPurchaseOrderDetail item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    String status = item.getIndex10(); // Replace with actual getter
                    switch (status) {
                        case "1":
                            setStyle("-fx-background-color: #f44336;");
                            break;
                        default:
                            setStyle("");
                    }
                    tblVwOrderDetails.refresh();
                }
            }
        });
    }

    private void tblVwDetail_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.READY) {
            pnTblDetailRow = tblVwOrderDetails.getSelectionModel().getSelectedIndex();

            ModelPurchaseOrderDetail selectedItem = tblVwOrderDetails.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 1) {
                clearDetailFields();
                if (selectedItem != null) {
                    if (pnTblDetailRow >= 0) {
                        loadRecordDetail();
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
                        pnTblDetailRow = moveToNextRow(currentTable, focusedCell);
                        break;
                    case UP:
                        pnTblDetailRow = moveToPreviousRow(currentTable, focusedCell);
                        break;
                    default:
                        return; // Ignore other keys
                }

                loadRecordDetail();
                event.consume();
            }

        }
    }

    private void initTextFieldsProperty() {
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
