/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.tf to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.guanzon.cas.purchasing.services.PurchaseOrderControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PurchaseOrder_EntryController implements Initializable, ScreenInterface {

    private GRider poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order";
    private LogWrapper logWrapper;
    private int pnEditMode;
    private JSONObject poJSON;
    @FXML
    private AnchorPane apBrowse;
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
    private TableView<?> tblVwOrderDetails;
    @FXML
    private TableColumn<?, ?> tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail,
            tblCostDetail, tblROQDetail, tblRequestQuantityDetail, tblOrderQuantityDetail, tblTotalAmountDetail;

    @FXML
    private TableView<?> tblVwStockRequest;
    @FXML
    private TableColumn<?, ?> tblRowNo, tblBranchName, tblDate, tblReferenceNo, tblNoOfItems;
    @FXML
    private Pagination pagination;

    @Override
    public void setGRider(GRider foValue) {
        poApp = foValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //mag set kanalang here ng industry
//        tfIndustry.setText("");
//        try {
//            poJSON = poPurchasingController.PurchaseOrder().NewTransaction();
//            if ("success".equals((String) poJSON.get("result"))) {
//
//            }
//        } catch (CloneNotSupportedException ex) {
//            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        poPurchasingController = new PurchaseOrderControllers(poApp, logWrapper);
        initButtonsClickActions();
        initTextFieldKeyPressed();
        initCheckBoxActions();
        pnEditMode = EditMode.UNKNOWN;
        initButtons(pnEditMode);
        initFields(pnEditMode);
    }

    // this is for detail fields
    private void loadMaster() {

    }

    // this is for detail fields
    private void loadDetail() {

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
                            System.out.println("No TextField is currently focused.");
                        }
                    }
                    break;
                case "btnSave":
                    if (ShowMessageFX.YesNo(null, "Save confirmation", "Are you sure, do you want to save?")) {
                        loJSON = poPurchasingController.PurchaseOrder().SaveTransaction();
                        if ("success".equals((String) loJSON.get("result"))) {
                            ShowMessageFX.Information(null, psFormName, (String) loJSON.get("message"));
                            try {
                                //transcode
                                loJSON = poPurchasingController.PurchaseOrder().OpenTransaction("TransCode");
                            } catch (CloneNotSupportedException ex) {
                                Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if ("success".equals((String) loJSON.get("result"))) {
                                loadMaster();
                                loadDetail();
                                pnEditMode = poPurchasingController.PurchaseOrder().getEditMode();
                            }
                        } else {
                            ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                            return;
                        }
                    }
                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo(null, "Cancel Confirmation", "Are you sure you want to cancel?")) {
                        if (pnEditMode == EditMode.ADDNEW) {
                            clearFields();
//                        oTrans = new PurchaseOrder(oApp, false, oApp.getBranchCode());
                            pnEditMode = EditMode.UNKNOWN;
                        } else {
                            loJSON = poPurchasingController.PurchaseOrder().OpenTransaction("TransCode");
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
        } catch (CloneNotSupportedException | SQLException ex) {
            Logger.getLogger(PurchaseOrder_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                    case F3:
                        switch (txtFieldID) {
                            case "tfCompany":
//                                loJSON = poPurchasingController.PurchaseOrder().(lsValue, true);
                                if (!"error".equals(loJSON.get("result"))) {
//                                    tfCompany.setText(poPurchasingController().getModel().getCompanyName());
                                } else {
                                    ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                    tfCompany.setText("");
                                    return;
                                }
                                break;
                            case "tfSupplier":
//                                loJSON = poPurchasingController.PurchaseOrder()(lsValue, true);
//                                if (!"error".equals(loJSON.get("result"))) {
//                                    tfSupplier.setText(poPurchasingController.PurchaseOrder().Company().getModel().getCompanyName());
//                                } else {
//                                    ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
//                                    tfSupplier.setText("");
//                                    return;
//                                }
                                break;
                            case "tfDestination":
                                loJSON = poPurchasingController.PurchaseOrder().SearchBranch(lsValue, true);
                                if (!"error".equals(loJSON.get("result"))) {
//                                    tfDestination.setText(poPurchasingController.PurchaseOrder().().getModel().getCompanyName());
                                } else {
                                    ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
                                    tfDestination.setText("");
                                    return;
                                }
                                break;
                            case "tfTerm":
//                                loJSON = poPurchasingController.PurchaseOrder().searchRecord(lsValue, true);
//                                if (!"error".equals(loJSON.get("result"))) {
//                                    tfTerm.setText(poPurchasingController.PurchaseOrder().Company().getModel().getCompanyName());
//                                } else {
//                                    ShowMessageFX.Warning(null, psFormName, (String) loJSON.get("message"));
//                                    tfTerm.setText("");
//                                    return;
//                                }
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
        }
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
        dpTransactionDate.setValue(LocalDate.now());
        tfCompany.setText("");
        tfSupplier.setText("");
        tfDestination.setText("");
        tfAdvancePAmount.setText("0.00");
        dpExpectedDlvrDate.setValue(LocalDate.now());
        tfReferenceNo.setText("0.00");
        taRemarks.setText("");
        tfTerm.setText("0");
        tfDiscountRate.setText("0");
        tfDiscountRate.setText("0.00");
        chkbAdvancePayment.setSelected(false);
        tfAdvancePRate.setText("0");
        tfDiscountAmount.setText("0.00");
        tfDiscountAmount.setText("0.00");
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
        tfAMC.setText("0.00");
        tfROQ.setText("0");
        tfRO.setText("0");
        tfBO.setText("0");
        tfQOH.setText("0");
        tfCost.setText("0.00");
        tfRequestQuantity.setText("0");
        tfOrderQuantity.setText("0");
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
        btnTransHistory.setVisible(false);
        btnTransHistory.setManaged(false);
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
        /* Master Fields*/
        dpTransactionDate.setDisable(lbShow);
        tfCompany.setDisable(lbShow);
        tfSupplier.setDisable(lbShow);
        tfDestination.setDisable(lbShow);
        taRemarks.setDisable(lbShow);
        dpExpectedDlvrDate.setDisable(lbShow);
        tfReferenceNo.setDisable(lbShow);
        tfTerm.setDisable(lbShow);

        //if selected pwede ma edit yung dalawa basta nasa addnew and update siya
        if (chkbAdvancePayment.isSelected()) {
            tfAdvancePRate.setDisable(lbShow);
            tfAdvancePAmount.setDisable(lbShow);
        }

        /* Detail Fields */
        tfBarcode.setDisable(lbShow);
        tfDescription.setDisable(lbShow);
        tfCost.setDisable(lbShow);
        tfOrderQuantity.setDisable(lbShow);

    }

}
