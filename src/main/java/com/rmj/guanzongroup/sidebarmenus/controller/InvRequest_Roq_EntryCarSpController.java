/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvOrderDetail;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
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
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import org.guanzon.cas.inv.warehouse.status.StockRequestStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class InvRequest_Roq_EntryCarSpController implements Initializable, ScreenInterface{
    @FXML
    private AnchorPane AnchorMain;
    
    private String psFormName = "Inv Stock Request ROQ Entry Car Sp";
    
    unloadForm poUnload = new unloadForm();
    private GRiderCAS poApp;
    private JSONObject poJSON;
    private TextField activeField;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    private String psBranchCode = "";
    private String psOldDate = "";
    private InvWarehouseControllers invRequestController;
    private LogWrapper logWrapper;
    private int pnEditMode;
    private int pnTblInvDetailRow = -1;
    private JSONObject loJSON = new JSONObject();
    @FXML
    private Label lblTransactionStatus, lblSource;

    @FXML
    private DatePicker dpTransactionDate;
    @FXML
    private TextArea taRemarks;
    @FXML
    private Button btnNew, btnClose, btnSave, btnSearch, btnCancel, btnVoid, btnBrowse;
    @FXML
    private TextField tfTransactionNo, tfBrand, tfModel, tfInvType,
            tfVariant, tfColor, tfROQ, tfClassification, tfQOH,
            tfReferenceNo, tfReservationQTY, tfOrderQuantity, tfDescription, tfBarCode;
    @FXML
    private TableColumn<ModelInvOrderDetail, String> tblBrandDetail, tblModelDetail, tblVariantDetail, tblColorDetail, tblInvTypeDetail,
            tblROQDetail, tblClassificationDetail, tblQOHDetail,
            tblReservationQtyDetail, tblOrderQuantityDetail, tblDescriptionDetail, tblBarCodeDetail;

    @FXML
    private TableView<ModelInvOrderDetail> tblViewOrderDetails;
    private ObservableList<ModelInvOrderDetail> invOrderDetail_data = FXCollections.observableArrayList();
//    

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

    public Stage getStage() {
        return (Stage) AnchorMain.getScene().getWindow();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        invRequestController = new InvWarehouseControllers(poApp, logWrapper);
        invRequestController.StockRequest().setTransactionStatus("017");
        JSONObject loJSON = new JSONObject();
        loJSON = invRequestController.StockRequest().InitTransaction();
        if (!"success".equals(loJSON.get("result"))) {
            ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
        }
        tfOrderQuantity.setOnKeyPressed(this::txtField_KeyPressed);
        Platform.runLater((() -> {
            try {
                //set edit mode to new transaction temporily to assign industry and company
                invRequestController.StockRequest().NewTransaction();

                invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                loadRecordSearch();

                //reset the transaction
                invRequestController.StockRequest().InitTransaction();
            } catch (CloneNotSupportedException e) {
                ShowMessageFX.Warning((String) e.getMessage(), "Search Information", null);
            }

        }));
//        inventoryClassification();
//        initTableStockRequest();
        Platform.runLater(() -> btnNew.fire());
        initTableInvDetail();
        initButtonsClickActions();
        initTextFieldPattern();
        initDatePickerActions();
        initTextFieldFocus();
        initFields(pnEditMode);
        initButtons(pnEditMode);
        tblViewOrderDetails.setOnMouseClicked(this::tblViewOrderDetails_Clicked);

    }
    //functions
    private void initTableInvDetail() {

        tblBrandDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblDescriptionDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblBarCodeDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblModelDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblVariantDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblColorDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblInvTypeDetail.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblROQDetail.setCellValueFactory(new PropertyValueFactory<>("index08"));
        tblClassificationDetail.setCellValueFactory(new PropertyValueFactory<>("index09"));
        tblQOHDetail.setCellValueFactory(new PropertyValueFactory<>("index10"));
        tblReservationQtyDetail.setCellValueFactory(new PropertyValueFactory<>("index11"));
        tblOrderQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index12"));

        // Prevent column reordering
        tblViewOrderDetails.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewOrderDetails.lookup("TableHeaderRow");
            if (header != null) {
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            }
        });
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnNew, btnSearch, btnSave, btnCancel,
                btnClose, btnVoid, btnBrowse);

        buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
    }
    private void initDetailFocus() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            tfOrderQuantity.requestFocus();
        }
    }
    private void initDatePickerActions() {

        dpTransactionDate.setOnAction(e -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                LocalDate selectedLocalDate = dpTransactionDate.getValue();
                LocalDate transactionDate = new java.sql.Date(invRequestController.StockRequest().Master().getTransactionDate().getTime()).toLocalDate();
                if (selectedLocalDate == null) {
                    return;
                }

                LocalDate dateNow = LocalDate.now();
                psOldDate = CustomCommonUtil.formatLocalDateToShortString(transactionDate);
                String lsReferNo = tfReferenceNo.getText().trim();
                boolean approved = true;
                if (pnEditMode == EditMode.UPDATE) {
                    psOldDate = CustomCommonUtil.formatLocalDateToShortString(transactionDate);
                    if (selectedLocalDate.isAfter(dateNow)) {
                        ShowMessageFX.Warning("Invalid to future date.", psFormName, null);
                        approved = false;
                    }

                    if (selectedLocalDate.isBefore(transactionDate) && lsReferNo.isEmpty()) {
                        ShowMessageFX.Warning("Invalid to backdate. Please enter a reference number first.", psFormName, null);
                        approved = false;
                    }
                    if (selectedLocalDate.isBefore(transactionDate) && !lsReferNo.isEmpty()) {
                        boolean proceed = ShowMessageFX.YesNo(
                                "You are changing the transaction date\n"
                                + "If YES, seek approval to proceed with the changed date.\n"
                                + "If NO, the transaction date will be remain.",
                                psFormName, null
                        );
                        if (proceed) {
                            if (poApp.getUserLevel() <= UserRight.ENCODER) {
                                poJSON = ShowDialogFX.getUserApproval(poApp);
                                if (!"success".equalsIgnoreCase((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    approved = false;
                                }
                            }
                        } else {
                            approved = false;
                        }
                    }
                }
                if (pnEditMode == EditMode.ADDNEW) {
                    if (selectedLocalDate.isAfter(dateNow)) {
                        ShowMessageFX.Warning("Invalid to future date.", psFormName, null);
                        approved = false;
                    }
                    if (selectedLocalDate.isBefore(dateNow) && lsReferNo.isEmpty()) {
                        ShowMessageFX.Warning("Invalid to backdate. Please enter a reference number first.", psFormName, null);
                        approved = false;
                    }

                    if (selectedLocalDate.isBefore(dateNow) && !lsReferNo.isEmpty()) {
                        boolean proceed = ShowMessageFX.YesNo(
                                "You selected a backdate with a reference number.\n\n"
                                + "If YES, seek approval to proceed with the backdate.\n"
                                + "If NO, the transaction date will be reset to today.",
                                "Backdate Confirmation", null
                        );
                        if (proceed) {
                            if (poApp.getUserLevel() <= UserRight.ENCODER) {
                                poJSON = ShowDialogFX.getUserApproval(poApp);
                                if (!"success".equalsIgnoreCase((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    approved = false;
                                }
                            }
                        } else {
                            approved = false;
                        }
                    }
                }
                if (approved) {
                    invRequestController.StockRequest().Master().setTransactionDate(
                            SQLUtil.toDate(selectedLocalDate.toString(), SQLUtil.FORMAT_SHORT_DATE));
                } else {
                    if (pnEditMode == EditMode.ADDNEW) {
                        dpTransactionDate.setValue(dateNow);
                        invRequestController.StockRequest().Master().setTransactionDate(
                                SQLUtil.toDate(dateNow.toString(), SQLUtil.FORMAT_SHORT_DATE));
                    } else if (pnEditMode == EditMode.UPDATE) {
                        invRequestController.StockRequest().Master().setTransactionDate(
                                SQLUtil.toDate(psOldDate, SQLUtil.FORMAT_SHORT_DATE));
                    }

                }
                dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                        SQLUtil.dateFormat(invRequestController.StockRequest().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
            }
        }
        );

    }

    private void initButtons(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        CustomCommonUtil.setVisible(!lbShow, btnClose, btnNew);
        CustomCommonUtil.setManaged(!lbShow, btnClose, btnNew);

        CustomCommonUtil.setVisible(lbShow, btnSearch, btnSave, btnCancel, btnVoid);
        CustomCommonUtil.setManaged(lbShow, btnSearch, btnSave, btnCancel, btnVoid);

        if (fnEditMode == EditMode.READY) {
            switch (invRequestController.StockRequest().Master().getTransactionStatus()) {
                case StockRequestStatus.OPEN:
                case StockRequestStatus.CONFIRMED:
            }

        }
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
        /*Master Fields */
        CustomCommonUtil.setDisable(!lbShow,
                dpTransactionDate, tfTransactionNo, taRemarks,
                tfReferenceNo);
        CustomCommonUtil.setDisable(!lbShow, //only quantity is editable
                tfOrderQuantity);
        CustomCommonUtil.setDisable(true,
                tfInvType, tfVariant, tfColor, tfReservationQTY,
                tfQOH, tfROQ, tfClassification, tfBrand, tfModel, tfDescription, tfBarCode);
        if (!tfReferenceNo.getText().isEmpty()) {
            dpTransactionDate.setDisable(!lbShow);
        }
    }
    private void loadRecordSearch() {
            try {
              
                lblSource.setText(invRequestController.StockRequest().Master().Company().getCompanyName() + " - " + invRequestController.StockRequest().Master().Industry().getDescription());

            } catch (GuanzonException | SQLException ex) {
                Logger.getLogger(InvStockRequest_EntryMcController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    private void clearDetailFields() { //clears detail fields
        /* Detail Fields*/
        CustomCommonUtil.setText("", tfBrand, tfModel,
                tfColor, tfReservationQTY, tfQOH, tfInvType, tfVariant, tfROQ, tfClassification,
                tfDescription, tfBarCode);
        CustomCommonUtil.setText("0", tfOrderQuantity);
    }

    private void clearMasterFields() { //clears master fields
        /* Master Fields*/
        pnTblInvDetailRow = -1;
        dpTransactionDate.setValue(null);

        taRemarks.setText("");
        CustomCommonUtil.setText("", tfReferenceNo, tfTransactionNo);

    }

    public void handleButtonAction(ActionEvent event) { //handles btnNew, btnSearch, etc...
        try {
            JSONObject loJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();

            switch (lsButton) {
                case "btnNew":
                    initTextFieldFocus();
                    System.out.print("New button pressed");
                    clearDetailFields();
                    clearMasterFields();
                    System.out.print("order quantity: " + tfOrderQuantity.getText());
                    invOrderDetail_data.clear();
                    loJSON = invRequestController.StockRequest().NewTransaction();
                    if ("success".equals((String) loJSON.get("result"))) {
                        invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                        invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                        invRequestController.StockRequest().Master().setBranchCode(poApp.getBranchCode());
                        invRequestController.StockRequest().Master().setCategoryId(psCategoryID);
                        invRequestController.StockRequest().getROQItems();
                        loadMaster();
                        loadTableInvDetail();
                        pnTblInvDetailRow = -1;
                        pnEditMode = invRequestController.StockRequest().getEditMode();
                        
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                    }
                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo(null, "Cancel Confirmation", "Are you sure you want to cancel?")) {

                        if (pnEditMode == EditMode.ADDNEW) {
                            clearDetailFields();
                            clearMasterFields();
                            invOrderDetail_data.clear();
                            tblViewOrderDetails.getItems().clear();
                            pnEditMode = EditMode.UNKNOWN;
                            invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                            invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                            pnTblInvDetailRow = -1;

                        } else {
                            clearMasterFields();
                            clearDetailFields();
                            invOrderDetail_data.clear();
                            loJSON = invRequestController.StockRequest().OpenTransaction(invRequestController.StockRequest().Master().getTransactionNo());
                            if ("success".equals((String) loJSON.get("result"))) {
                                pnTblInvDetailRow = -1;
                                loadMaster();
                                clearDetailFields();
                                pnEditMode = invRequestController.StockRequest().getEditMode();
                                loadTableInvDetail();
                            }
                        }
                    }
                    tblViewOrderDetails.getSelectionModel().clearSelection();

                    break;
                  case "btnSave":
                         if (!ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to save?")) {
                        return;
                    }
                    int detailCount = invRequestController.StockRequest().getDetailCount();
                        boolean hasValidItem = false; // True if at least one valid item exists

                        if (detailCount == 0) {
                            ShowMessageFX.Warning("Your order is empty. Please add at least one item.", psFormName, null);
                            return;
                        }
                    for (int lnCntr = 0; lnCntr <= detailCount - 1; lnCntr++) {
                            double quantity = ((Number) invRequestController.StockRequest().Detail(lnCntr).getValue("nQuantity")).doubleValue();
                            String stockID = (String) invRequestController.StockRequest().Detail(lnCntr).getValue("sStockIDx");

                            // If any stock ID is empty OR quantity is 0, show an error and prevent saving
                            if (detailCount == 1) {
                                if (stockID == null || stockID.trim().isEmpty() || quantity == 0) {
                                    ShowMessageFX.Warning("Invalid item in order. Ensure all items have a valid Stock ID and quantity greater than 0.", psFormName, null);
                                    return;
                                }
                            }

                            hasValidItem = true;
                        }
                        if (!hasValidItem) {
                                ShowMessageFX.Warning("Your order must have at least one valid item with a Stock ID and quantity greater than 0.", psFormName, null);
                                return;
                            }

                        if (pnEditMode == EditMode.UPDATE && (invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.CONFIRMED))) {
                                if (!"success".equals((loJSON = ShowDialogFX.getUserApproval(poApp)).get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    return;
                                }
                            }
                        if (pnEditMode == EditMode.UPDATE) {
                            invRequestController.StockRequest().Master().setModifiedDate(poApp.getServerDate());
                            invRequestController.StockRequest().Master().setModifyingId(poApp.getUserID());
                        }
                        for (int lnCntr = 0; lnCntr < detailCount; lnCntr++) {
                            invRequestController.StockRequest().Detail(lnCntr).setModifiedDate(poApp.getServerDate());
                        }
                        loJSON = invRequestController.StockRequest().isDetailHasZeroQty();
                        if (!"success".equals((String)loJSON.get("result"))) {
                            if(!ShowMessageFX.YesNo((String) loJSON.get("message"), psFormName, null)){
                                
                                pnTblInvDetailRow = (int) loJSON.get("tableRow");
                                loadTableInvDetail();
                                loadDetail();
                                initDetailFocus();
                            return;
                            }
                        }
                       // Save the transaction
                            loJSON = invRequestController.StockRequest().SaveTransaction();
                            if (!"success".equals((String) loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                loadTableInvDetail();
                                return;
                            }

                            ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);

                            loJSON = invRequestController.StockRequest().OpenTransaction(invRequestController.StockRequest().Master().getTransactionNo());

                            if ("success".equals(loJSON.get("result")) &&
                                invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.OPEN)) {

                                if (ShowMessageFX.YesNo(null, psFormName, "Do you want to confirm this transaction?")) {
                                    try {
                                        loJSON = invRequestController.StockRequest().ConfirmTransaction("Confirmed");

                                        if (!"success".equals((String) loJSON.get("result"))) {
                                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                            return;
                                        }

                                        loJSON = ShowDialogFX.getUserApproval(poApp);
                                        if (!"success".equals((String) loJSON.get("result"))) {
                                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                            return;
                                        }

                                        ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                                    } catch (ParseException ex) {
                                        Logger.getLogger(InvRequest_Roq_EntryMcController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }

                            Platform.runLater(() -> btnNew.fire());
                            break;
                case "btnVoid":
                    loJSON = invRequestController.StockRequest().VoidTransaction("Voided");
                    if (!"success".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        break;
                    }
                    ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                    clearMasterFields();
                    clearDetailFields();
                    invOrderDetail_data.clear();
                    pnEditMode = EditMode.UNKNOWN;
                    break;
                case "btnBrowse":

                    invRequestController.StockRequest().setTransactionStatus("102");
                    poJSON = invRequestController.StockRequest().searchTransaction();
                    if (!"error".equals((String) poJSON.get("result"))) {

                        pnTblInvDetailRow = -1;
                        loadMaster();
                        pnEditMode = invRequestController.StockRequest().getEditMode();
                        loadDetail();
                        loadTableInvDetail();
                    } else {
                        ShowMessageFX.Warning((String) poJSON.get("message"), "Search Information", null);
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

            }
            initButtons(pnEditMode);
            initFields(pnEditMode);
        } catch (CloneNotSupportedException | ExceptionInInitializerError | SQLException | GuanzonException | ParseException | NullPointerException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", psFormName);
        }

    }

    private void loadMaster() {
        tfTransactionNo.setText(invRequestController.StockRequest().Master().getTransactionNo());
        String lsStatus = "";
        switch (invRequestController.StockRequest().Master().getTransactionStatus()) {
            case StockRequestStatus.OPEN:
                lsStatus = "OPEN";
                break;
            case StockRequestStatus.CONFIRMED:
                lsStatus = "CONFIRMED";
                break;
            case StockRequestStatus.PROCESSED:
                lsStatus = "PROCESSED";
                break;
            case StockRequestStatus.CANCELLED:
                lsStatus = "CANCELLED";
                break;
            case StockRequestStatus.VOID:
                lsStatus = "VOID";
                break;
        }
        
        lblTransactionStatus.setText(lsStatus);
        dpTransactionDate.setOnAction(null);
        dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                SQLUtil.dateFormat(invRequestController.StockRequest().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)
        ));
        initDatePickerActions();
        tfReferenceNo.setText(invRequestController.StockRequest().Master().getReferenceNo());
        taRemarks.setText(invRequestController.StockRequest().Master().getRemarks());
    }

    private void loadDetail() { //loads to text fields 
        try {
            if (pnTblInvDetailRow >= 0) {

                String lsBrand = "";
                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription() != null) {
                    lsBrand = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription();
                }
                tfBrand.setText(lsBrand);

                String lsModel = "";
                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Model().getDescription() != null) {
                    lsModel = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Model().getDescription();
                }
                tfModel.setText(lsModel);

                String lsVariant = "";
                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Variant().getDescription() != null) {
                    lsVariant = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Variant().getDescription();
                }
                tfVariant.setText(lsVariant);

                String lsColor = "";
                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Color().getDescription() != null) {
                    lsColor = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Color().getDescription();
                }
                tfColor.setText(lsColor);

                String lsInvType = "";

                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().InventoryType().getDescription() != null) {
                    lsInvType = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().InventoryType().getDescription();
                }
                tfInvType.setText(lsInvType);

                String lsROQ = "0";
                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getRecommendedOrder() != 0) {
                    lsROQ = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getRecommendedOrder());
                }
                tfROQ.setText(lsROQ);

                String lsClassification = "";
                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getClassification() != null) {
                    lsClassification = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getClassification());
                }
                tfClassification.setText(lsClassification);

                String lsOnHand = "0";

                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantityOnHand() != 0) {
                    lsOnHand = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantityOnHand());
                }
                tfQOH.setText(lsOnHand);

                String lsReservationQTY = "0";

                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getReservedOrder() != 0) {
                    lsReservationQTY = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getReservedOrder());
                }
                tfReservationQTY.setText(lsReservationQTY);

                String lsOrderQuantity = "0";
                if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity() != 0) {
                    lsOrderQuantity = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity());
                }
                tfOrderQuantity.setText(lsOrderQuantity);

            }
        } catch (SQLException | GuanzonException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", psFormName);
            System.exit(1);
        }
    }

    private void tblViewOrderDetails_Clicked(MouseEvent event) {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.READY) {
            pnTblInvDetailRow = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
            ModelInvOrderDetail selectedItem = tblViewOrderDetails.getSelectionModel().getSelectedItem();

            if (event.getClickCount() == 1) {
                clearDetailFields();
                if (selectedItem != null) {
                    if (pnTblInvDetailRow >= 0) {
                        loadDetail();
                        initDetailFocus();
                    }
                }
            }
        }
    }

    private void loadTableInvDetail() {
        invOrderDetail_data.clear();
        tblViewOrderDetails.getItems().clear();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);
        progressIndicator.setStyle("-fx-accent: #FF8201;");

        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        loadingPane.setStyle("-fx-background-color: transparent;");
        
        tblViewOrderDetails.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Task<List<ModelInvOrderDetail>> task = new Task<List<ModelInvOrderDetail>>() {
            @Override
            protected List<ModelInvOrderDetail> call() throws Exception {
                try {
                    
                    int detailCount = invRequestController.StockRequest().getDetailCount();
                    
                    List<ModelInvOrderDetail> detailsList = new ArrayList<>();
                    
                    for (int lnCtr = 0; lnCtr < detailCount; lnCtr++) {
                        Model_Inv_Stock_Request_Detail detail = invRequestController.StockRequest().Detail(lnCtr);
                        System.out.print(detail.Inventory().Brand().getDescription());
                        detailsList.add(new ModelInvOrderDetail(
                                detail.Inventory().Brand().getDescription(), 
                                detail.Inventory().getDescription(), 
                                detail.Inventory().getBarCode(), 
                                detail.Inventory().Model().getDescription(),
                                detail.Inventory().Variant().getDescription(),
                                detail.Inventory().Color().getDescription(),
                                detail.Inventory().InventoryType().getDescription(),
                                String.valueOf(detail.getRecommendedOrder()),
                                detail.getClassification(),
                                String.valueOf(detail.getQuantityOnHand()),
                                String.valueOf(detail.getReservedOrder()),
                                String.valueOf(detail.getQuantity())
                                
                        ));
                    }

                    Platform.runLater(() -> {
                        invOrderDetail_data.setAll(detailsList);
                        tblViewOrderDetails.setItems(invOrderDetail_data);
                        initFields(pnEditMode);
                    });

                    return detailsList;

                } catch (Exception ex) {
                    Logger.getLogger(InvStockRequest_ROQ_EntryMcSpController.class.getName()).log(Level.SEVERE, null, ex);
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
      private void initTextFieldPattern() {
        CustomCommonUtil.inputDecimalOnly(tfOrderQuantity);
      }
    private void setOrderQuantityToDetail(String fsValue) {
            if (fsValue.isEmpty()) {
                fsValue = "0";
            }
            double quantity = Double.parseDouble(fsValue);
            if (Double.parseDouble(fsValue) < 0) {
                ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);
                fsValue = "0";

            }
            if (pnTblInvDetailRow >= 0 && pnTblInvDetailRow < invRequestController.StockRequest().getDetailCount()) {
                    double roq = invRequestController.StockRequest().Detail(pnTblInvDetailRow).getRecommendedOrder();
                    if (quantity > roq) {
                        if (!"success".equals((poJSON = ShowDialogFX.getUserApproval(poApp)).get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                            tfOrderQuantity.setText("0");
                            return;
                        }
                    }
              }
             if (pnTblInvDetailRow < 0) {
                fsValue = "0";
                ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                clearDetailFields();
                int detailCount = invRequestController.StockRequest().getDetailCount();
                pnTblInvDetailRow = detailCount > 0 ? detailCount - 1 : 0;
            }
            tfOrderQuantity.setText(fsValue);
            invRequestController.StockRequest().Detail(pnTblInvDetailRow).setQuantity(Double.valueOf(fsValue));
           
      }

    private void txtField_KeyPressed(KeyEvent event) {
            TextField sourceField = (TextField) event.getSource();
            String fieldId = sourceField.getId();

            try {
                if (event.getCode() == null) return;

                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                    case F3:
                        switch (fieldId) {
                            case "tfOrderQuantity":
                                         setOrderQuantityToDetail(tfOrderQuantity.getText());
                                          if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                                              pnTblInvDetailRow++;
                                          }//step 9W

                                          CommonUtils.SetNextFocus(sourceField);
                                          loadTableInvDetailAndSelectedRow();
                                          break;
                        }
                        event.consume();
                        break;

                    case UP:
                        setOrderQuantityToDetail(tfOrderQuantity.getText());
                        if ("tfOrderQuantity".equals(sourceField.getId())) {
                            if (pnTblInvDetailRow > 0 && !invOrderDetail_data.isEmpty()) {
                                pnTblInvDetailRow--;
                                CommonUtils.SetPreviousFocus((TextField) event.getSource());
                                loadTableInvDetailAndSelectedRow();
                                
                            }
                        }
                        
                        
                        event.consume(); 
                        break;
                    case DOWN:
                      setOrderQuantityToDetail(tfOrderQuantity.getText());
                        if ("tfOrderQuantity".equals(sourceField.getId())) {
                            if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                                pnTblInvDetailRow++;
                            }
                            CommonUtils.SetNextFocus(sourceField);
                             loadTableInvDetailAndSelectedRow();
                        }
                        
                       
                        event.consume(); 
                        
                        break;
                }
            } catch (Exception e) {
                ShowMessageFX.Error(getStage(), e.getMessage(), "Error", psFormName);
            }
        }

    private void loadTableInvDetailAndSelectedRow() { //select row 
        System.out.print("row selected this time...hehe");
        if (pnTblInvDetailRow >= 0) {
            Platform.runLater(() -> {
                PauseTransition delay = new PauseTransition(Duration.millis(10));
                delay.setOnFinished(event -> {
                    Platform.runLater(() -> {
                        loadTableInvDetail();
                    });
                });
                delay.play();
            });
                loadDetail();
            //initDetailFocus();
        }
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
                    invRequestController.StockRequest().Master().setReferenceNo(lsValue);
                    break;
            }
        } else {
            loTextField.selectAll();
        }
    };
    private void initTextFieldKeyPressed() {
            List<TextField> loTxtField = Arrays.asList(
                    tfOrderQuantity
                    );

            loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
        }  
    private void initTextFieldFocus() {
        List<TextField> loTxtField = Arrays.asList(tfReferenceNo, tfOrderQuantity);
        loTxtField.forEach(tf -> tf.focusedProperty().addListener(txtField_Focus));
        Platform.runLater(() -> tfOrderQuantity.requestFocus());
        tfOrderQuantity.setOnMouseClicked(e -> activeField = tfOrderQuantity);

    }
}
