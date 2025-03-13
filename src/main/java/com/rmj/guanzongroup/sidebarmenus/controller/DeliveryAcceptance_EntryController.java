/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReceiving;
import org.guanzon.cas.purchasing.services.PurchaseOrderReceivingControllers;
import org.guanzon.cas.purchasing.services.PurchaseOrderReceivingModels;
import org.json.simple.JSONObject;
import org.junit.Assert;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;

    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "Purchase Order Receiving Entry";
    static PurchaseOrderReceiving oTrans;
    public int pnEditMode;

    private ObservableList<ModelDeliveryAcceptance_Detail> details_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Main> main_data = FXCollections.observableArrayList();

    @FXML
    private AnchorPane apMainAnchor;

    @FXML
    private AnchorPane apBrowse;

    @FXML
    private AnchorPane apButton;

    @FXML
    private HBox hbButtons;

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnHistory;

    @FXML
    private Button btnRetrieve;

    @FXML
    private Button btnClose;

    @FXML
    private HBox hboxid;

    @FXML
    private AnchorPane apMaster;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField tfTransactionNo;

    @FXML
    private DatePicker dpTransactionDate;

    @FXML
    private TextField tfIndustry;

    @FXML
    private TextField tfCompany;

    @FXML
    private TextField tfSupplier;

    @FXML
    private TextField tfTrucking;

    @FXML
    private TextArea taRemarks;

    @FXML
    private DatePicker dpReferenceDate;

    @FXML
    private TextField tfReferenceNo;

    @FXML
    private TextField tfTerm;

    @FXML
    private TextField tfDiscountRate;

    @FXML
    private TextField tfDiscountAmount;

    @FXML
    private TextField tfTotal;

    @FXML
    private AnchorPane apDetail;

    @FXML
    private TextField tfOrderNo;

    @FXML
    private TextField tfBarcode;

    @FXML
    private TextField tfSupersede;

    @FXML
    private TextField tfDescription;

    @FXML
    private TextField tfBrand;

    @FXML
    private TextField tfModel;

    @FXML
    private TextField tfColor;

    @FXML
    private TextField tfInventoryType;

    @FXML
    private TextField tfMeasure;

    @FXML
    private DatePicker dpExpiryDate;

    @FXML
    private TextField tfCost;

    @FXML
    private TextField tfOrderQuantity;

    @FXML
    private TextField tfReceiveQuantity;

    @FXML
    private TableView tblViewOrderDetails;

    @FXML
    private TableColumn tblRowNoDetail;

    @FXML
    private TableColumn tblOrderNoDetail;

    @FXML
    private TableColumn tblBarcodeDetail;

    @FXML
    private TableColumn tblDescriptionDetail;

    @FXML
    private TableColumn tblCostDetail;

    @FXML
    private TableColumn tblOrderQuantityDetail;

    @FXML
    private TableColumn tblReceiveQuantityDetail;

    @FXML
    private TableColumn tblTotalDetail;

    @FXML
    private TableView tblViewStock_Request;

    @FXML
    private TableColumn tblRowNo;

    @FXML
    private TableColumn tblSupplier;

    @FXML
    private TableColumn tblDate;

    @FXML
    private TableColumn tblReferenceNo;

    /**
     * Initializes the controller class.
     */
    @FXML
    private void cmdButton_Click(ActionEvent event) {
        JSONObject loJson = new JSONObject();
        String tabText = "";
        try {
            Object source = event.getSource();
            if (source instanceof Button) {
                Button clickedButton = (Button) source;
                String lsButton = clickedButton.getId();
                switch (lsButton) {
                    case "btnBrowse":
//                    poJSON = oTrans.searchTransaction("sTransNox", "", pnIndex == 99);
//                    pnEditMode = EditMode.READY;
//
//                    //start
//                    if ("error".equalsIgnoreCase(poJSON.get("result").toString())) {
//
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        txtField01.requestFocus();
//                        pnEditMode = EditMode.UNKNOWN;
//                        return;
//                    } else {
//                        loadRecord();
//                    }
                        break;

                    case "btnPrint":
//                    poJSON = oTrans.printRecord();
//                    if ("error".equals((String) poJSON.get("result"))) {
//                        Assert.fail((String) poJSON.get("message"));
//                    }

                        break;
                    case "btnClose":
                        unloadForm appUnload = new unloadForm();
                        if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?") == true) {
                            appUnload.unloadForm(apMainAnchor, oApp, pxeModuleName);

                        } else {
                            return;
                        }

//                case "btnBrowse":
//                    break;
                    case "btnNew":
                        poJSON = oTrans.NewTransaction();
                        pnEditMode = oTrans.Master().getEditMode();
                        oTrans.searchTransaction("", true);

                        if ("error".equals((String) poJSON.get("result"))) {
                            System.err.println((String) poJSON.get("message"));
                            pnEditMode = EditMode.UNKNOWN;
                            return;
                        }
                        break;

                    case "btnUpdate":
                        pnEditMode = oTrans.Master().getEditMode();

                        poJSON = oTrans.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            System.err.println((String) poJSON.get("message"));
                            pnEditMode = EditMode.UNKNOWN;
                            return;
                        }
                        break;
                    case "btnSearch":
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            oTrans = new PurchaseOrderReceivingControllers(oApp, null).PurchaseOrderReceiving();

                            pnEditMode = EditMode.UNKNOWN;
//                        clearFields();
                            break;
                        } else {
                            break;
                        }
//                case "btnPrint":
//                    break;
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        break;
                    case "btnSave":
//                    oTrans.Master().getModel().setCompanyName(oTrans.Master().getModel().getCompanyName());
//
//                    loJSON = oTrans.Master().isEntryOkay();
//                    if ("error".equals((String) loJSON.get("result"))) {
//                        ShowMessageFX.Information((String) loJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        break;
//                    }
//                    if (pnEditMode == EditMode.UPDATE) {
//                        oTrans.Update();
//                    }
//
//                    loJSON = oTrans.Save();
//
//                    if ("error".equals((String) loJSON.get("result"))) {
//                        ShowMessageFX.Information((String) loJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        break;
//                    } else {
//                        ShowMessageFX.OkayCancel((String) loJSON.get("message"), "", "Successfully saved!");
//                    }
                        break;
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");

                        break;
                }
                initButton(pnEditMode);
            }

        } catch (Exception e) {

        }
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
//        if (!pbLoaded) {
//            return;
//        }
        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = (txtField.getId());
        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }
        JSONObject loJSON;
        if (!nv) {
            /*Lost Focus*/
            lsValue = lsValue.trim();
            switch (lsID) {

                case "taRemarks"://Remarks
                    loJSON = oTrans.Master().setRemarks(lsValue);
                    if ("error".equals((String) loJSON.get("result"))) {
                        System.err.println((String) loJSON.get("message"));
                        ShowMessageFX.Information(null, pxeModuleName, (String) loJSON.get("message"));
                        return;
                    }
                    break;
            }
            loadRecordDetail();
        } else {
            txtField.selectAll();
        }
    };

    final ChangeListener<? super Boolean> txtDetail_Focus = (o, ov, nv) -> {

        JSONObject loJSON;
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());

        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfOrderNo":
                    // identify here if existing in records or not

                    loJSON = oTrans.Detail(pnDetail).setOrderNo(lsValue);
                    if ("error".equals((String) loJSON.get("result"))) {
                        System.err.println((String) loJSON.get("message"));
                        ShowMessageFX.Information(null, pxeModuleName, (String) loJSON.get("message"));
                        return;
                    }
                    break;
                case "tfBarcode":
                    // identify here if existing in records or not
//                    oTrans.Detail(pnDetail).Inventory().openRecord(lsValue);
//                    
//                    loJSON = oTrans.Detail(pnDetail).setOrderNo(lsValue);
//                    if ("error".equals((String) loJSON.get("result"))) {
//                        System.err.println((String) loJSON.get("message"));
//                        ShowMessageFX.Information(null, pxeModuleName, (String) loJSON.get("message"));
//                        return;
//                    }
                    break;
                case "tfSupersede":
                    // identify here if existing in records or not
                    break;
                case "tfDescription":
                    // identify here if existing in records or not

                    break;
                case "tfCost":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                        return;
                    }
                    try {
                        //   oTrans.Detail(pnDetail).Inventory().setCost((Double.valueOf(lsValue.replace(",", ""))));
                    } catch (Exception e) {

                    }
                    break;
                case "tfReceiveQuantity":

                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                        return;
                    }
                    oTrans.Detail(pnDetail).setQuantity((Integer.valueOf(lsValue.replace(",", ""))));
                    break;
            }

            loadRecordMaster();
            loadRecordDetail();
        }
    };

    final ChangeListener<? super Boolean> txtMaster_Focus = (o, ov, nv) -> {
        JSONObject loJSON;
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());

        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {

                case "tfCompany":
//                    loJSON = oTrans.Master().setIndustryId(lsValue);
//                    if ("error".equals((String) loJSON.get("result"))) {
//                        System.err.println((String) loJSON.get("message"));
//                        ShowMessageFX.Information(null, pxeModuleName, (String) loJSON.get("message"));
//                        return;
//                    }
                    break;
                case "tfSupplier":
                    break;
                case "tfTrucking":
                    break;
                case "tfAreaRemarks":
                    break;
                case "tfReferenceNo":
                    break;
                case "tfTerm":
                    break;
                case "tfDiscountRate":
                    break;
                case "tfDiscountAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                        return;
                    }
                    oTrans.Master().setDiscount(Double.valueOf(lsValue.replace(",", "")));
                    tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lsValue));
                    break;
                case "tfTotal":
                    break;

            }

//            loadRecordMaster();
//            loadRecordDetail();
        }
    };

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            JSONObject loJSON = new JSONObject();
            switch (event.getCode()) {
                case F3:
                    switch (lsID) {
                        case "tfCompany":
                            /*search company*/
                            loJSON = new JSONObject();
                            loJSON = oTrans.SearchCompany(lsValue, false);

                            oTrans.Master().getCompanyId();

                            if ("error".equals(loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), pxeModuleName, "");
                                tfCompany.setText("");
                                break;
                            }

                            tfCompany.setText(oTrans.Master().Company().getCompanyName());

                            break;

                        case "tfSupplier":
                            loJSON = oTrans.SearchSupplier(lsValue, false);
                            if ("error".equals(loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), pxeModuleName, "");
                                tfSupplier.setText("");
                                break;
                            }
                            tfSupplier.setText(oTrans.Master().Supplier().getCompanyName());
                            break;
                        case "tfTrucking":
                            loJSON = oTrans.SearchTrucking(lsValue, false);
                            if ("error".equals(loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), pxeModuleName, "");
                                tfTrucking.setText("");
                                break;
                            }
                            tfTrucking.setText(oTrans.Master().Trucking().getCompanyName());
                            break;
                        case "tfTerm":
                            loJSON = oTrans.SearchTerm(lsValue, false);
                            if ("error".equals(loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), pxeModuleName, "");
                                tfTerm.setText("");
                                break;
                            }
                            tfTerm.setText(oTrans.Master().Term().getDescription());
                            break;
                        case "tfOrderNo":

                            break;
                        case "tfBarcode":
                            loJSON = oTrans.SearchBarcode(lsValue, true, pnMain);
                            if ("error".equals(loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), pxeModuleName, "");
                                tfBarcode.setText("");
                                break;
                            }

                            tfBarcode.setText(oTrans.Detail(pnDetail).Inventory().getBarCode());

                            break;

                        case "tfDescription": {
                            loJSON = oTrans.SearchDescription(lsValue, true, pnMain);
                        }
                        if ("error".equals(loJSON.get("result"))) {
                            ShowMessageFX.Warning((String) loJSON.get("message"), pxeModuleName, "");
                            tfDescription.setText("");
                            break;
                        }
                        tfDescription.setText(oTrans.Detail(pnDetail).Inventory().getDescription());
                        break;

                        case "tfSupersede":
                            loJSON = oTrans.SearchSupersede(lsValue, true, pnMain);
                            if ("error".equals(loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), pxeModuleName, "");
                                tfSupersede.setText("");
                                break;
                            }
                            tfSupersede.setText(oTrans.Detail(pnDetail).Supersede().getBriefDescription());

                            break;

                    }
            }

            switch (event.getCode()) {
                case ENTER:
                    CommonUtils.SetNextFocus(txtField);
                case DOWN:
                    CommonUtils.SetNextFocus(txtField);
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(txtField);
            }
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initTextFields() {

        tfTransactionNo.focusedProperty().addListener(txtMaster_Focus);
        tfIndustry.focusedProperty().addListener(txtMaster_Focus);
        tfCompany.focusedProperty().addListener(txtMaster_Focus);
        tfSupplier.focusedProperty().addListener(txtMaster_Focus);
        tfTrucking.focusedProperty().addListener(txtMaster_Focus);
        taRemarks.focusedProperty().addListener(txtArea_Focus);
        tfReferenceNo.focusedProperty().addListener(txtMaster_Focus);
        tfTerm.focusedProperty().addListener(txtMaster_Focus);
        tfDiscountRate.focusedProperty().addListener(txtMaster_Focus);
        tfDiscountAmount.focusedProperty().addListener(txtMaster_Focus);
        tfTotal.focusedProperty().addListener(txtMaster_Focus);

        tfOrderNo.focusedProperty().addListener(txtDetail_Focus);
        tfBarcode.focusedProperty().addListener(txtDetail_Focus);
        tfSupersede.focusedProperty().addListener(txtDetail_Focus);
        tfDescription.focusedProperty().addListener(txtDetail_Focus);
        tfBrand.focusedProperty().addListener(txtDetail_Focus);
        tfModel.focusedProperty().addListener(txtDetail_Focus);
        tfColor.focusedProperty().addListener(txtDetail_Focus);
        tfInventoryType.focusedProperty().addListener(txtDetail_Focus);
        tfMeasure.focusedProperty().addListener(txtDetail_Focus);
        tfCost.focusedProperty().addListener(txtDetail_Focus);
        tfOrderQuantity.focusedProperty().addListener(txtDetail_Focus);
        tfReceiveQuantity.focusedProperty().addListener(txtDetail_Focus);

        tfCompany.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupplier.setOnKeyPressed(this::txtField_KeyPressed);
        tfTrucking.setOnKeyPressed(this::txtField_KeyPressed);
        tfTerm.setOnKeyPressed(this::txtField_KeyPressed);
        tfOrderNo.setOnKeyPressed(this::txtField_KeyPressed);
        tfBarcode.setOnKeyPressed(this::txtField_KeyPressed);
        tfDescription.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupersede.setOnKeyPressed(this::txtField_KeyPressed);

    }

    private void datePicker_Focus(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) { // Lost focus
            DatePicker datePicker = (DatePicker) ((ReadOnlyBooleanProperty) observable).getBean();
            LocalDate selectedDate = datePicker.getValue();

            if (selectedDate != null) {
                System.out.println("Selected date: " + selectedDate);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(selectedDate.toString(), formatter);
                String formattedDate = formatter.format(selectedDate);

                switch (datePicker.getId()) {
                    case "dpTransactionDate":
                        oTrans.Master().setTransactionDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        break;
                    case "dpReferenceDate":
                        oTrans.Master().setRefernceDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        break;
                    case "dpExpiryDate":
                        oTrans.Detail(pnDetail).setExpiryDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        break;
                    default:
                        System.out.println("Unknown DatePicker: " + datePicker.getId());
                        break;
                }

                datePicker.setValue(localDate);
//                datePicker.setValue(CustomCommonUtil.parseDateStringToLocalDate(oTrans.Master().getTransactionDate().toString(), SQLUtil.FORMAT_SHORT_DATE));
            }
        }
    }

    private void setDatePickerFormat(DatePicker datePicker) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });
    }

    public void initDatePickers() {
        setDatePickerFormat(dpTransactionDate);
        setDatePickerFormat(dpReferenceDate);
        setDatePickerFormat(dpExpiryDate);

        dpTransactionDate.focusedProperty().addListener(this::datePicker_Focus);
        dpReferenceDate.focusedProperty().addListener(this::datePicker_Focus);
        dpExpiryDate.focusedProperty().addListener(this::datePicker_Focus);

    }

    public void initDetailsGrid() {

        tblRowNoDetail.setStyle("-fx-alignment: CENTER;");
        tblOrderNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblBarcodeDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblDescriptionDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblCostDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblOrderQuantityDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblReceiveQuantityDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblTotalDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblOrderNoDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblBarcodeDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblDescriptionDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblCostDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblOrderQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblReceiveQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblTotalDetail.setCellValueFactory(new PropertyValueFactory<>("index08"));

        tblViewOrderDetails.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewOrderDetails.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblViewOrderDetails.setItems(details_data);
        tblViewOrderDetails.autosize();
    }

    public void initMainGrid() {
        tblRowNo.setStyle("-fx-alignment: CENTER;");
        tblSupplier.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblDate.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblReferenceNo.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        tblRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblSupplier.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblDate.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblReferenceNo.setCellValueFactory(new PropertyValueFactory<>("index04"));

        tblViewStock_Request.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewStock_Request.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        tblViewStock_Request.setItems(main_data);
        tblViewStock_Request.autosize();
    }

    public void clearTextFields() {
        tfTransactionNo.clear();
        tfIndustry.clear();
        tfCompany.clear();
        tfSupplier.clear();
        tfTrucking.clear();
        taRemarks.clear();
        tfReferenceNo.clear();
        tfTerm.clear();
        tfDiscountRate.clear();
        tfDiscountAmount.clear();
        tfTotal.clear();
        tfOrderNo.clear();
        tfBarcode.clear();
        tfSupersede.clear();
        tfDescription.clear();
        tfBrand.clear();
        tfModel.clear();
        tfColor.clear();
        tfInventoryType.clear();
        tfMeasure.clear();
        tfCost.clear();
        tfOrderQuantity.clear();
        tfReceiveQuantity.clear();
    }

    public void loadRecordDetail() {
        //        tfBrand.setText(oTrans.Detail(pnDetail).Inventory().Brand().getDescription());
//        tfModel.setText(oTrans.Detail(pnDetail).Inventory().Model().getDescription());
//        tfColor.setText(oTrans.Detail(pnDetail).Inventory().Color().getDescription());
//        tfInventoryType.setText(oTrans.Detail(pnDetail).Inventory().InventoryType().getDescription());
//        tfMeasure.setText(oTrans.Detail(pnDetail).Inventory().Measure().getMeasureName());
        try {
            tfCost.setText(String.valueOf(oTrans.Detail(pnDetail).Inventory().getCost()));
        } catch (Exception ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }

        tfOrderQuantity.setText(String.valueOf(oTrans.Detail(pnDetail).getOrderQty()));
        tfReceiveQuantity.setText(String.valueOf(oTrans.Detail(pnDetail).getQuantity()));

//        oTrans.Detail(pnDetail).getExpiryDate();
//        oTrans.Detail(pnDetail).setExpiryDate(expiryDate);
    }

    public void loadRecordMaster() {
        String lsActive = oTrans.Master().getTransactionStatus();

        switch (lsActive) {
            case "0":
                lblStatus.setText("OPEN");
                break;
            case "1":
                lblStatus.setText("CLOSED");
                break;
            case "2":
                lblStatus.setText("POSTED");
                break;
            case "3":
                lblStatus.setText("CANCELLED");
                break;
            default:
                lblStatus.setText("UNKNOWN");
                break;
        }

        oTrans.Master().setBranchCode(oApp.getBranchCode());
        tfTransactionNo.setText(oTrans.Master().getTransactionNo());
        try {
            oTrans.Master().setTransactionDate(oApp.getServerDate());
        } catch (Exception e) {
        }

        // Transaction Date
        try {
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(oTrans.Master().getTransactionDate());
            if (!lsTransactionDate.equals("")) {
                Object loTransDate = oTrans.Master().getTransactionDate();
                if (loTransDate == null) {
                    dpTransactionDate.setValue(LocalDate.now());
                } else if (loTransDate instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) loTransDate;
                    LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                    dpTransactionDate.setValue(localDate);
                } else if (loTransDate instanceof Date) {
                    Date sqlDate = (Date) loTransDate;
                    LocalDate localDate = sqlDate.toLocalDate();
                    dpTransactionDate.setValue(localDate);
                } else {
                }
            }
        } catch (Exception e) {

        }

        try {
            oTrans.Master().setIndustryId(oApp.getIndustry());
            tfIndustry.setText(oTrans.Master().Industry().getDescription());
        } catch (Exception e) {
            Assert.fail();
        }
        tfCompany.setText(oTrans.Master().getCompanyId());
        try {
            tfSupplier.setText(oTrans.Master().Supplier().getCompanyName());
        } catch (Exception e) {

        }

        tfTrucking.setText(oTrans.Master().getTruckingId());

        //ReferenceDate
        try {
            String lsReferenceDate = CustomCommonUtil.formatDateToShortString(oTrans.Master().getRefernceDate());
            if (!oTrans.Master().getRefernceDate().equals("")) {
                Object loReferenceDate = oTrans.Master().getRefernceDate();
                if (loReferenceDate == null) {
                    dpReferenceDate.setValue(LocalDate.now());
                } else if (loReferenceDate instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) loReferenceDate;
                    LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                    dpReferenceDate.setValue(localDate);
                } else if (loReferenceDate instanceof Date) {
                    Date sqlDate = (Date) loReferenceDate;
                    LocalDate localDate = sqlDate.toLocalDate();
                    dpReferenceDate.setValue(localDate);
                } else {
                }
            }

        } catch (Exception e) {
        }

        tfReferenceNo.setText(oTrans.Master().getReferenceNo());
        tfTerm.setText(oTrans.Master().getTermCode());

        try {
            tfDiscountRate.setText(String.valueOf(oTrans.Master().getDiscountRate()));
        } catch (Exception e) {
        }
        try {
            tfDiscountAmount.setText(String.valueOf(oTrans.Master().getDiscount()));
        } catch (Exception e) {
        }
        taRemarks.setText(oTrans.Master().getRemarks());

    }

    public void initTableOnClick() {
        tblViewOrderDetails.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                pnDetail = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
                loadTableDetail();
            }
        });

        tblViewStock_Request.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                pnMain = tblViewStock_Request.getSelectionModel().getSelectedIndex();
                if (pnMain >= 0) {
                    loadTableMain();
                }
            }
        });

    }

    public void loadTableMain() {
        // Setting data to table detail

        int lnCtr;
        int lnCtr2 = 0;
        main_data.clear();

        String lsMainDate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Define the format

        if (!oTrans.Master().getTransactionDate().equals("")) {
            Object loDate = oTrans.Master().getTransactionDate();
            if (loDate == null) {
                lsMainDate = LocalDate.now().format(formatter); // Convert to String

            } else if (loDate instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) loDate;
                LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();

                lsMainDate = localDate.format(formatter);
            } else if (loDate instanceof Date) {
                Date sqlDate = (Date) loDate;
                LocalDate localDate = sqlDate.toLocalDate();

                lsMainDate = localDate.format(formatter);
            } else {
            }
        }

//        if (oTrans.Master(). >= 0) {
//            for (lnCtr = 0; lnCtr < oTrans.Master(); lnCtr++) {
//
//                main_data.add(new ModelDeliveryAcceptance_Main(String.valueOf(lnCtr + 1),
//                        (String) oTrans.Master().Supplier().getCompanyName(),
//                        lsMainDate,
//                        (String) oTrans.Master().getReferenceNo()
//                ));
//                lnCtr2 += 1;
//
//            }
//        }
        if (pnMain < 0 || pnMain
                >= main_data.size()) {
            if (!main_data.isEmpty()) {
                /* FOCUS ON FIRST ROW */
                tblViewStock_Request.getSelectionModel().select(0);
                tblViewStock_Request.getFocusModel().focus(0);
                pnMain = tblViewStock_Request.getSelectionModel().getSelectedIndex();
                loadRecordDetail();

            }
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblViewStock_Request.getSelectionModel().select(pnMain);
            tblViewStock_Request.getFocusModel().focus(pnMain);
            loadRecordDetail();
        }

    }

    public void loadTableDetail() {
        // Setting data to table detail
        loadTableMain();

        int lnCtr;
        int lnCtr2 = 0;
        details_data.clear();

        if (oTrans.Detail().size() >= 0) {
            for (lnCtr = 0; lnCtr < oTrans.Detail().size(); lnCtr++) {

                new PurchaseOrderReceivingModels(oApp).PurchaseOrderReceivingDetails();

                try {
                    details_data.add(
                            new ModelDeliveryAcceptance_Detail(String.valueOf(lnCtr + 1),
                                    String.valueOf(oTrans.Detail(lnCtr2).getOrderNo()),
                                    String.valueOf(oTrans.Detail(lnCtr2).Inventory().getBarCode()),
                                    String.valueOf(oTrans.Detail(lnCtr2).Inventory().getDescription()),
                                    String.valueOf(oTrans.Detail(lnCtr2).Inventory().getCost()),
                                    String.valueOf(oTrans.Detail(lnCtr2).getOrderQty()),
                                    String.valueOf(oTrans.Detail(lnCtr2).getQuantity()),
                                    String.valueOf(10) //identify total
                            ));
                } catch (SQLException ex) {
                    Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (GuanzonException ex) {
                    Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                }
                lnCtr2 += 1;

            }
        }

        if (pnDetail < 0 || pnDetail
                >= details_data.size()) {
            if (!details_data.isEmpty()) {
                /* FOCUS ON FIRST ROW */
                tblViewOrderDetails.getSelectionModel().select(0);
                tblViewOrderDetails.getFocusModel().focus(0);
                pnDetail = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
                loadRecordDetail();

            }
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblViewOrderDetails.getSelectionModel().select(pnDetail);
            tblViewOrderDetails.getFocusModel().focus(pnDetail);
            loadRecordDetail();
        }

    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        // Manage visibility and managed state of other buttons
//        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
//        btnRetrieve.setVisible(!lbShow);
        btnClose.setVisible(!lbShow);

        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnCancel.setVisible(lbShow);

//        btnBrowse.setManaged(!lbShow);
        btnNew.setManaged(!lbShow);
//        btnRetrieve.setManaged(!lbShow);
        btnClose.setManaged(!lbShow);

        btnSearch.setManaged(lbShow);
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);

        boolean lbShow2 = fnValue == EditMode.READY;

        btnUpdate.setVisible(lbShow2);
        btnPrint.setVisible(lbShow2);
        btnHistory.setVisible(lbShow2);

        btnUpdate.setManaged(lbShow2);
        btnPrint.setManaged(lbShow2);
        btnHistory.setManaged(lbShow2);

        btnClose.setVisible(lbShow2);
        btnClose.setManaged(lbShow2);

//        apBrowse.setDisable(lbShow); // no usage
        apMaster.setDisable(!lbShow);
        apDetail.setDisable(!lbShow);
//        apTable.setDisable(!lbShow); // disable upon for viewing?
//        if (Integer.valueOf(oTrans.getMasterModel().getTransactionStatus()) != 0) {
//            btnVoid.setDisable(false);
//        } else {
//            btnVoid.setDisable(true);
//        }
//        oTrans.setTransType("SP");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        oTrans = new PurchaseOrderReceivingControllers(oApp, null).PurchaseOrderReceiving();

        JSONObject loJSON = oTrans.InitTransaction(); // Initialize transaction
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
        }

        try {
            loJSON = oTrans.NewTransaction();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!"success".equals((String) loJSON.get("result"))) {
            System.err.println((String) loJSON.get("message"));
        }

        oTrans.Detail().size();

        initTextFields();
        initDatePickers();
        initMainGrid();
        initDetailsGrid();
        initTableOnClick();
        clearTextFields();

        loadRecordMaster();

        pnEditMode = oTrans.getEditMode();
        initButton(pnEditMode);

    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

}
