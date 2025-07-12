/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvOrderDetail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvTableListInformation;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import org.guanzon.cas.inv.warehouse.status.StockRequestStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class InvRequest_UpdateAppliancesController implements Initializable, ScreenInterface{
    @FXML
    private String psFormName = "Inv Stock Request Update Appliances";
   @FXML
        private AnchorPane AnchorMain,AnchorDetailMaster;
        unloadForm poUnload = new unloadForm();
        private InvWarehouseControllers invRequestController;
        private GRiderCAS poApp;
        private String psIndustryID = "";
        private String psCompanyID = "";
        private String psBranchCode = "";
        private String psCategoryID = "";
        private String psOldDate = "";
        private String psReferID = "";
        private String psTransNo = "";
        private LogWrapper logWrapper;
        private int pnTblInvDetailRow = -1;
        private int pnTblInformationRow = -1;
        private int pnEditMode;
        private TextField activeField;
        private JSONObject poJSON;
        
        private  String brandID,categID; 
        private String brandDesc;
        
                             
        private ObservableList<ModelInvOrderDetail> invOrderDetail_data = FXCollections.observableArrayList();
        private ObservableList<ModelInvTableListInformation> tableListInformation_data = FXCollections.observableArrayList();
        @FXML
        private TextField tfTransactionNo,tfBrand,tfModel,tfInvType,
                tfVariant,tfColor,tfROQ,tfClassification,tfQOH,tfReferenceNo,tfReservationQTY,
                tfOrderQuantity,tfSearchTransNo,tfSearchReferenceNo,tfBarCode,tfDescription;
       

        @FXML
        private Label lblTransactionStatus,lblSource;

        @FXML
        private TextArea taRemarks;

        @FXML
        private TableView<ModelInvOrderDetail>tblViewOrderDetails;
        
        @FXML
        private TableView<ModelInvTableListInformation> tableListInformation;

        @FXML
        private Button btnClose,btnSave,btnCancel,btnBrowse,btnUpdate,btnRetrieve,btnNew;

        @FXML
        private TableColumn<ModelInvOrderDetail, String> tblBrandDetail, tblModelDetail,tblVariantDetail,
                tblColorDetail,tblInvTypeDetail,tblROQDetail,tblClassificationDetail,
                tblQOHDetail,tblReservationQtyDetail,tblOrderQuantityDetail,tblDescriptionDetail,tblBarCodeDetail;
        
        @FXML
        private TableColumn<ModelInvTableListInformation, String> tblTransactionNo,tblReferenceNo,tblTransactionDate;

        @Override
        public void setGRider(GRiderCAS foValue){
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

        private Stage getStage(){
            return (Stage) AnchorMain.getScene().getWindow();   
        }
        @FXML
        private DatePicker dpTransactionDate;

        @Override
        public void initialize(URL url, ResourceBundle rb) {
            try{
                
            invRequestController = new InvWarehouseControllers(poApp,logWrapper);
            invRequestController.StockRequest().setTransactionStatus(StockRequestStatus.OPEN);
            
            poJSON = invRequestController.StockRequest().InitTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning((String) poJSON.get("message"), "Search Information", null);
                }
            

             Platform.runLater((() -> {
                    //BOTH NULL
                   
                    try {
                        //set edit mode to new transaction temporily to assign industry and company
                        invRequestController.StockRequest().NewTransaction();
                        invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                        invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                        invRequestController.StockRequest().Master().setCategoryId(psCategoryID);
                        loadRecordSearch();
                        
                        //reset the transaction
                        invRequestController.StockRequest().InitTransaction();
                    } catch (CloneNotSupportedException e) {
                        ShowMessageFX.Warning((String) e.getMessage(), "Search Information", null);
                    }
                }));
                tblViewOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
                initTextFieldPattern();
                initButtonsClickActions();
                initTextFieldFocus();
                initTextAreaFocus();
                initTextFieldKeyPressed();
                initTextFieldPattern();
                initDatePickerActions();
                initTableList();
                initTextFieldsProperty();
                initTableInvDetail();
                tableListInformation.setOnMouseClicked(this::tableListInformation_Clicked);
                tblViewOrderDetails.setOnMouseClicked(this::tblViewOrderDetails_Clicked);
                initButtons(EditMode.UNKNOWN);
                initFields(EditMode.UNKNOWN);
                
                        
                }catch(ExceptionInInitializerError ex) {
                Logger.getLogger(InvStockRequest_EntryMcController.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
        private void initTextFieldsProperty() {
        tfSearchTransNo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    //loadTableList();
                }

            }
        });
        tfSearchReferenceNo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    invRequestController.StockRequest().Master().setReferenceNo("");
                    tfSearchReferenceNo.setText("");
                    //loadTableList();
                }
            }
        });
    }
        private void loadRecordSearch() {
            try {
              
                lblSource.setText(invRequestController.StockRequest().Master().Company().getCompanyName() + " - " + invRequestController.StockRequest().Master().Industry().getDescription());

            } catch (GuanzonException | SQLException ex) {
                Logger.getLogger(InvStockRequest_EntryMcController.class.getName()).log(Level.SEVERE, null, ex);
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

            if (focusedCell != null && "tblViewOrderDetails".equals(currentTable.getId())) {
                switch (event.getCode()) {
                    case TAB:
                    case DOWN:
                        pnTblInvDetailRow = pnTblInvDetailRow;
                        if (pnEditMode != EditMode.ADDNEW || pnEditMode != EditMode.UPDATE) {
                            pnTblInvDetailRow = moveToNextRow(currentTable, focusedCell);
                        }
                        break;
                    case UP:
                        pnTblInvDetailRow = pnTblInvDetailRow;
                        if (pnEditMode != EditMode.ADDNEW || pnEditMode != EditMode.UPDATE) {
                            pnTblInvDetailRow = moveToPreviousRow(currentTable, focusedCell);
                        }
                        break;
                    default:
                        return;
                }
                currentTable.getSelectionModel().select(pnTblInvDetailRow);
                currentTable.getFocusModel().focus(pnTblInvDetailRow);
                loadDetail();
                initDetailFocus();
                event.consume();
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
          private void initDatePickerActions() {
             dpTransactionDate.setOnAction(e -> {
                if (pnEditMode == EditMode.ADDNEW|| pnEditMode == EditMode.UPDATE) {
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
        private void loadDetail() {
            try {
                 int detailCount = invRequestController.StockRequest().getDetailCount();
                    if (pnTblInvDetailRow < 0 || pnTblInvDetailRow >= detailCount) {
                        clearDetailFields();
                        return;
                    }
                if (pnTblInvDetailRow >= 0) {

                    
                    String lsBrand = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription() != null) {
                        lsBrand = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription();
                    }
                    tfBrand.setText(lsBrand);
                    
                   String lsDescription = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().getDescription() != null) {
                        lsDescription = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().getDescription();
                    }
                    tfDescription.setText(lsDescription);
                    
                    String lsBarCode = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().getBarCode() != null) {
                        lsBarCode = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().getBarCode();
                    }
                    tfBarCode.setText(lsBarCode);

                    
                    String lsModel = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Model().getDescription() != null) {
                        lsModel = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Model().getDescription();
                    }
                    tfModel.setText(lsModel);

                   

                    String lsVariant = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Variant().getDescription()!= null) {
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
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getClassification()!=null) {
                        lsClassification = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getClassification());
                    }
                    tfClassification.setText(lsClassification);
                    
                    String lsOnHand = "0";
                     
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantityOnHand()!= 0) {
                        lsOnHand = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantityOnHand());
                    }
                    tfQOH.setText(lsOnHand);
                    
                    String lsReservationQTY = "0";
                    
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getReservedOrder()!= 0) {
                        lsReservationQTY = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getReservedOrder());
                    }
                    tfReservationQTY.setText(lsReservationQTY);
                    
                    String lsOrderQuantity = "0.0";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity() != 0) {
                        lsOrderQuantity = String.valueOf(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity());
                    }
                    tfOrderQuantity.setText(lsOrderQuantity);

                }
            } catch (SQLException | GuanzonException e) {
                ShowMessageFX.Error(getStage(), e.getMessage(), "Error",psFormName);
                System.exit(1);
            }
        }

        private void handleButtonAction(ActionEvent event) {
            try{
            JSONObject loJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId(); 
            switch (lsButton) {

                        case "btnBrowse":
                            invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                            invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                            invRequestController.StockRequest().Master().setCategoryId(psCategoryID);
                            
                            invRequestController.StockRequest().setTransactionStatus("102");
                            loJSON = invRequestController.StockRequest().searchTransaction();
                           

                            if (!"error".equals((String) loJSON.get("result"))) {
                                tblViewOrderDetails.getSelectionModel().clearSelection(pnTblInvDetailRow);
                                pnTblInvDetailRow = -1;
                                loadMaster();
                                pnEditMode = invRequestController.StockRequest().getEditMode();
                                loadTableInvDetail();
                                loadDetail();
                                
                                
                            } else {
                                ShowMessageFX.Warning((String) loJSON.get("message"), "Browse", null);
                            }
                            break;
                        case "btnRetrieve":
                            invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                            invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                            invRequestController.StockRequest().Master().setCategoryId(psCategoryID);
                            invRequestController.StockRequest().setTransactionStatus("102");
                            loadTableList();
                            break;
                     case "btnUpdate":
                        poJSON = invRequestController.StockRequest().UpdateTransaction();
                        pnEditMode = invRequestController.StockRequest().getEditMode();

                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), "Warning", null);
                        }

                        clearDetailFields();
                        loadTableInvDetail();

                        if (tblViewOrderDetails.getItems().size() > 0) {
                            Platform.runLater(() -> {
                                tblViewOrderDetails.getSelectionModel().select(0);
                                pnTblInvDetailRow = 0; 
                                loadDetail();

                               
                            });
                        }

                        initFields(pnEditMode);
                        tableListInformation.toFront();
                        break;

                       case "btnSave":
                            if (!ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to save?")) {
                                return;
                            }

                            int detailCount = invRequestController.StockRequest().getDetailCount();
                            boolean hasValidItem = false;

                            if (detailCount == 0) {
                                ShowMessageFX.Warning("Your order is empty. Please add at least one item.", psFormName, null);
                                return;
                            }

                            for (int lnCntr = 0; lnCntr <= detailCount - 1; lnCntr++) {
                                double quantity = ((Number) invRequestController.StockRequest().Detail(lnCntr).getValue("nQuantity")).doubleValue();
                                String stockID = (String) invRequestController.StockRequest().Detail(lnCntr).getValue("sStockIDx");

                                if (detailCount == 1 && (stockID == null || stockID.trim().isEmpty() || quantity == 0)) {
                                    ShowMessageFX.Warning("Invalid item in order. Ensure all items have a valid Stock ID and quantity greater than 0.", psFormName, null);
                                    return;
                                }
                                hasValidItem = true;
                            }

                            if (!hasValidItem) {
                                ShowMessageFX.Warning("Your order must have at least one valid item with a Stock ID and quantity greater than 0.", psFormName, null);
                                return;
                            }

                            if (pnEditMode == EditMode.UPDATE && invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.CONFIRMED)) {
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

                            loJSON = invRequestController.StockRequest().SaveTransaction();
                            if (!"success".equals((String) loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                loadTableInvDetail();
                                return;
                            }

                            ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);

                            if (ShowMessageFX.YesNo(null, psFormName, "Do you want to confirm this transaction now?")) {
                                try {
                                    JSONObject approvalJSON = ShowDialogFX.getUserApproval(poApp);
                                    if (!"success".equals((String) approvalJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) approvalJSON.get("message"), psFormName, null);
                                        return;
                                    }

                                    loJSON = invRequestController.StockRequest().ConfirmTransaction("Confirmed");
                                    if (!"success".equals((String) loJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                        return;
                                    }

                                    ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);

                                    clearAllTables();
                                    clearDetailFields();
                                    clearMasterFields();
                                    invOrderDetail_data.clear();
                                    pnEditMode = EditMode.UNKNOWN;
                                    pnTblInvDetailRow = -1;
                                    tblViewOrderDetails.getSelectionModel().clearSelection();

                                    Platform.runLater(() -> btnNew.fire());
                                } catch (ParseException ex) {
                                    Logger.getLogger(InvRequest_EntryMcGeneralController.class.getName()).log(Level.SEVERE, null, ex);
                                    ShowMessageFX.Error("Error confirming transaction", psFormName, null);
                                }
                            } else {
                                // Just saved without confirmation - keep data but reload
                                if (pnEditMode == EditMode.ADDNEW) {
                                    // For new transactions, clear after save
                                    clearAllTables();
                                    clearDetailFields();
                                    clearMasterFields();
                                    pnEditMode = EditMode.UNKNOWN;
                                    pnTblInvDetailRow = -1;
                                } else {
                                    // For updates, reload the saved transaction
                                    loJSON = invRequestController.StockRequest().OpenTransaction(
                                        invRequestController.StockRequest().Master().getTransactionNo());
                                    if ("success".equals((String) loJSON.get("result"))) {
                                        pnTblInvDetailRow = -1;
                                        loadMaster();
                                        loadTableInvDetail();
                                        pnEditMode = invRequestController.StockRequest().getEditMode();
                                    }
                                }
                                tblViewOrderDetails.getSelectionModel().clearSelection();
                            }
                            break;

                
               case "btnCancel":
                        if (ShowMessageFX.YesNo(null, "Cancel Confirmation", "Are you sure you want to cancel?")) {
                           
                            invOrderDetail_data.clear();
                            tableListInformation_data.clear();

                           
                            invRequestController.StockRequest().InitTransaction();

                            
                            clearAllTables();
                            clearDetailFields();
                            clearMasterFields();

                            
                            pnEditMode = EditMode.UNKNOWN;
                            pnTblInvDetailRow = -1;
                            pnTblInformationRow = -1;

                            
                            tblViewOrderDetails.refresh();
                            tableListInformation.refresh();
                                    
                            invRequestController.StockRequest().setTransactionStatus(StockRequestStatus.OPEN);
                            invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                            invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                        }
                        break;
                 case "btnNew":
                    clearAllTables();
                    clearDetailFields();
                    clearMasterFields();
                    invOrderDetail_data.clear();
                    loJSON = invRequestController.StockRequest().NewTransaction();
                    if ("success".equals((String) loJSON.get("result"))) {
                        invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                        invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                        invRequestController.StockRequest().Master().setBranchCode(poApp.getBranchCode()); 
                        invRequestController.StockRequest().Master().setCategoryId(psCategoryID); 
                        
                        loadMaster();
                        pnTblInvDetailRow = 0;
                        pnEditMode = invRequestController.StockRequest().getEditMode();
                        loadTableInvDetail();
                        loadTableInvDetailAndSelectedRow();
                      Platform.runLater(() -> {
                        tblViewOrderDetails.getSelectionModel().select(0);
                        tfBrand.requestFocus();
                    });

                        
                        
                       
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
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
            }catch (CloneNotSupportedException | ExceptionInInitializerError | SQLException | GuanzonException | NullPointerException e) {
                ShowMessageFX.Error(getStage(), e.getMessage(), "Error",psFormName);
                System.exit(1);
            }
        }
        private void loadTableList() {
        btnRetrieve.setDisable(true);
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50); // Set size to 200x200
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER); // Center it

        tableListInformation.setPlaceholder(loadingPane); // Show while loading
        progressIndicator.setVisible(true); // Make sure it's visible

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    tableListInformation_data.clear();
                    JSONObject poJSON = invRequestController.StockRequest().getTableListInformation(psTransNo,psReferID);
                    if ("success".equals(poJSON.get("result"))) {
                        if (invRequestController.StockRequest().getINVMasterCount() > 0) {
                            for (int lnCntr = 0; lnCntr <= invRequestController.StockRequest().getINVMasterCount() - 1; lnCntr++) {
                                tableListInformation_data.add(new ModelInvTableListInformation(
                                        invRequestController.StockRequest().INVMaster(lnCntr).getTransactionNo(),
                                        invRequestController.StockRequest().INVMaster(lnCntr).getReferenceNo(),
                                        SQLUtil.dateFormat(invRequestController.StockRequest().INVMaster(lnCntr).getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE),
                                        
                                        ""));
                            }
                        } else {
                            tableListInformation_data.clear();
                        }
                    }

                    Platform.runLater(() -> {
                        if (tableListInformation_data.isEmpty()) {
                            tableListInformation.setPlaceholder(new Label("NO RECORD TO LOAD"));
                            tableListInformation.setItems(FXCollections.observableArrayList(tableListInformation_data));
                        } else {
                            tableListInformation.setItems(FXCollections.observableArrayList(tableListInformation_data));
                        }
                    });

                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(InvRequest_ConfirmationMcController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                btnRetrieve.setDisable(false);
                if (tableListInformation_data == null || tableListInformation_data.isEmpty()) {
                    tableListInformation.setPlaceholder(new Label("NO RECORD TO LOAD"));
                } 
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                btnRetrieve.setDisable(false);
            }
        };
        new Thread(task).start(); // Run task in background
    }
        private void clearDetailFields() {
            /* Detail Fields*/
            CustomCommonUtil.setText("", tfBrand, tfModel,
                    tfColor, tfReservationQTY, tfQOH,tfInvType, 
                    tfVariant,tfROQ,tfClassification,tfBarCode,tfDescription);
           CustomCommonUtil.setText("0", tfOrderQuantity);
        }           

        private void clearMasterFields() {
            /* Master Fields*/
            pnTblInvDetailRow = -1;
            dpTransactionDate.setValue(null);
            taRemarks.setText("");
            CustomCommonUtil.setText("",  tfReferenceNo,tfTransactionNo);

        }
          //to go back to last selected row
        private void reselectLastRow() {
            if (pnTblInvDetailRow >= 0 && pnTblInvDetailRow < tblViewOrderDetails.getItems().size()) {
                tblViewOrderDetails.getSelectionModel().clearAndSelect(pnTblInvDetailRow);
                tblViewOrderDetails.getSelectionModel().focus(pnTblInvDetailRow); // Scroll to the selected row if needed
            }
        }


   private void loadTableInvDetail() {
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
                    if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)) {
                        Model_Inv_Stock_Request_Detail lastDetail = invRequestController.StockRequest().Detail(detailCount - 1);
                        if (lastDetail.getStockId() != null && !lastDetail.getStockId().isEmpty()) {
                            invRequestController.StockRequest().AddDetail();
                            detailCount++;
                        }
                    }   
                   
                    List<ModelInvOrderDetail> detailsList = new ArrayList<>();
                    
                    for (int lnCtr = 0; lnCtr < detailCount; lnCtr++) {
                        Model_Inv_Stock_Request_Detail detail = invRequestController.StockRequest().Detail(lnCtr);
                            
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
                    invOrderDetail_data.setAll(detailsList); // ObservableList<ModelInvOrderDetail>
                    tblViewOrderDetails.setItems(invOrderDetail_data);
                    reselectLastRow();
                    initFields(pnEditMode);
                });

                    return detailsList;

                } catch (GuanzonException | SQLException ex) {
                    Logger.getLogger(InvStockRequest_EntryMcSpController.class.getName()).log(Level.SEVERE, null, ex);
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
     
        final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        TextField loTextField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextFieldID = loTextField.getId();
        String lsValue = loTextField.getText();
        if (lsValue == null) {
            return;
        }
        poJSON = new JSONObject();
        if (!nv) {
            /*Lost Focus*/
            switch (lsTextFieldID) {
                case "tfReferenceNo":
                    invRequestController.StockRequest().Master().setReferenceNo(lsValue);
                    break;
                case "tfOrderQuantity":
                    break;
                case "tfSearchReferenceNo":
                     psReferID = tfSearchReferenceNo.getText();
                    //loadTableList();
                    break;
            }
        } else {
            loTextField.selectAll();
        }
    };


            
         private void initFields(int fnEditMode) {
          
        boolean lbShow = (fnEditMode == EditMode.UPDATE ||fnEditMode == EditMode.ADDNEW);
        boolean lbNew = (fnEditMode == EditMode.ADDNEW);
        
        
        /* Master Fields*/
        if (invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.OPEN)||
            invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.CONFIRMED)) {
            CustomCommonUtil.setDisable(!lbShow, AnchorDetailMaster);
            CustomCommonUtil.setDisable(!lbNew,
                    dpTransactionDate, taRemarks,tfReferenceNo);


            CustomCommonUtil.setDisable(true,
                    tfInvType,tfReservationQTY
                    ,tfQOH,tfROQ,tfClassification,tfVariant,tfColor,tfBrand,tfModel,tfDescription,tfBarCode);
            CustomCommonUtil.setDisable(!lbShow, tfOrderQuantity);
            CustomCommonUtil.setDisable(!lbNew, tfBrand,tfDescription,tfBarCode,tfModel);
            
            
        } else {
            CustomCommonUtil.setDisable(true, AnchorDetailMaster);
        }
        
    }


        private void initTextAreaFocus() {
            taRemarks.focusedProperty().addListener(txtArea_Focus);
        }

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
                        invRequestController.StockRequest().Master().setRemarks(lsValue);
                        break;
                }
            } else {
                loTextArea.selectAll();
            }
        };

        private void initTextFieldKeyPressed() {
            List<TextField> loTxtField = Arrays.asList(
                    tfOrderQuantity,tfSearchTransNo,tfBrand,tfBarCode,tfDescription,tfModel
                    );

            loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
        }  
        private void initButtonsClickActions() {
            List<Button> buttons = Arrays.asList( btnSave, btnCancel,
                    btnClose,btnBrowse,btnUpdate,btnRetrieve,btnNew);

            buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
        }    
        private void txtField_KeyPressed(KeyEvent event) {
          TextField sourceField = (TextField) event.getSource();
          String fieldId = sourceField.getId();
          String value = sourceField.getText() == null ? "" : sourceField.getText();
          JSONObject loJSON = new JSONObject();
          try {
              if (event.getCode() == null) return;
              String lsValue = sourceField.getText().trim();

              switch (event.getCode()) {
                  case TAB:
                  case ENTER:
                  case F3:
                      switch (fieldId) {
                                case "tfSearchTransNo":
                                    System.out.print("Company ID" + psCompanyID);
                                    invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                                    invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                                    invRequestController.StockRequest().Master().setCategoryId(psCategoryID);
                                    invRequestController.StockRequest().setTransactionStatus("102");
                                    poJSON = invRequestController.StockRequest().searchTransaction();
                                    if (!"error".equals((String) poJSON.get("result"))) {
                                        pnTblInvDetailRow = -1;
                                        loadMaster();
                                        pnEditMode = invRequestController.StockRequest().getEditMode();
                                        loadDetail();
                                        loadTableInvDetail();
                                        initButtons(pnEditMode);
                                    } else {
                                        ShowMessageFX.Warning((String) poJSON.get("message"), "Search Information", null);
                                    }
                                    break;
                                    case "tfBrand":
                              if (pnTblInvDetailRow < 0) {
                                      ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                      clearDetailFields();
                                      break;
                                  }
                            loJSON = invRequestController.StockRequest().SearchBrand(lsValue, false);
                            
                            if ("error".equals(loJSON.get("result"))) {
                                          ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                          tfBrand.setText("");
                                          tfBrand.requestFocus();
                                          break;
                                      }
                            
                            brandID  = (String) loJSON.get("brandID");
                         
                            brandDesc = (String) loJSON.get("brandDesc");
                            tfBrand.setText(brandDesc);
                            
                            if (!tfBarCode.getText().isEmpty()||!tfDescription.getText().isEmpty()) {
                                tfOrderQuantity.requestFocus();
                            }else{
                                tfModel.requestFocus();
                            }
                            loadTableInvDetail();
                            break;
                             case "tfModel":
                                     if (pnTblInvDetailRow < 0) {
                                        ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                        clearDetailFields();
                                        break;
                                    }
                                   
                                    poJSON = invRequestController.StockRequest().SearchModel(lsValue, false, brandID,pnTblInvDetailRow);

                                     if ("error".equals(poJSON.get("result"))) {
                                        ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                        tfModel.setText("");
                                        if (poJSON.get("tableRow") != null) {
                                            pnTblInvDetailRow = (int) poJSON.get("tableRow");
                                        } else {
                                            break;
                                        }
                                    }

                                    if ("success".equals(poJSON.get("result"))) {
                                       
                                        double currentQty = 0;
                                        try {
                                            currentQty = invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity();
                                        } catch (Exception e) {
                                            currentQty = 0;
                                        }
                                        double newQty = currentQty + 1;
                                        tfOrderQuantity.setText(String.valueOf(newQty));
                                        invRequestController.StockRequest().Detail(pnTblInvDetailRow).setQuantity(newQty);
                                    }
                                    loadTableInvDetail();
                                    loadDetail();
                                    initDetailFocus();
                                    //selectTheExistedDetailFromStockRequest();
                                    break;
                        case "tfBarCode":
                                if (pnTblInvDetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                    poJSON = invRequestController.StockRequest().SearchBarcode(lsValue, true, pnTblInvDetailRow,brandID
                                );
                                
                               if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                tfBarCode.setText("");
                                if (poJSON.get("tableRow") != null) {
                                    pnTblInvDetailRow = (int) poJSON.get("tableRow");
                                }
                                break;
                            }

                            if ("success".equals(poJSON.get("result"))) {
                                double currentQty = 0.0;
                                try {
                                    currentQty = invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity();
                                } catch (Exception e) {
                                    currentQty = 0.0;
                                }
                                double newQty = currentQty + 1;
                                tfOrderQuantity.setText(String.valueOf(newQty));
                                invRequestController.StockRequest().Detail(pnTblInvDetailRow).setQuantity(newQty);
                            }


                            loadTableInvDetail();
                            loadDetail();
                            initDetailFocus();
                            break;
                        
                         case "tfDescription":
                                if (pnTblInvDetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                poJSON = invRequestController.StockRequest().SearchBarcodeDescription(lsValue, false, pnTblInvDetailRow,brandID
                                );
                                if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                tfDescription.setText("");
                                if (poJSON.get("tableRow") != null) {
                                    pnTblInvDetailRow = (int) poJSON.get("tableRow");
                                }
                                break;
                            }

                            if ("success".equals(poJSON.get("result"))) {
                                double currentQty = 0.0;
                                try {
                                    currentQty = invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity();
                                } catch (Exception e) {
                                    currentQty = 0.0;
                                }
                                double newQty = currentQty + 1;
                                tfOrderQuantity.setText(String.valueOf(newQty));
                                invRequestController.StockRequest().Detail(pnTblInvDetailRow).setQuantity(newQty);
                            }


                            loadTableInvDetail();
                            loadDetail();
                            initDetailFocus();
                            break;
                         
                                }
                              event.consume();
                               switch (fieldId) {
                                    case "tfSearchTransNo":
                                        CommonUtils.SetNextFocus((TextField) event.getSource());
                                        break;
                                    case "tfOrderQuantity":
                                        setOrderQuantityToDetail(tfOrderQuantity.getText());
                                        if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                                            pnTblInvDetailRow++;
                                        }
                                        CommonUtils.SetNextFocus((TextField) event.getSource());
                                        loadTableInvDetailAndSelectedRow();
                                        break;
                                }
                                event.consume();
                                break;
                                
                case UP:
                        setOrderQuantityToDetail(tfOrderQuantity.getText());

                        if (fieldId.equals("tfOrderQuantity")) {
                            if (pnTblInvDetailRow > 0 && !invOrderDetail_data.isEmpty()) {
                                pnTblInvDetailRow--;
                            }
                        }

                       
                        switch (fieldId) {
                            case "tfModel":
                                tfBrand.requestFocus();
                                break;
                            case "tfBarCode":
                                tfDescription.requestFocus();
                                break;
                            case "tfDescription":
                                tfBarCode.requestFocus();
                                break;
                            default:
                                CommonUtils.SetPreviousFocus((TextField) event.getSource());
                        }

                        loadTableInvDetailAndSelectedRow();
                        event.consume();
                        break;


                    case DOWN:
                        setOrderQuantityToDetail(lsValue);
                        if ("tfBrand".equals(fieldId)) {
                            tfModel.requestFocus();
                        } else if ("tfModel".equals(fieldId)) {
                            tfBarCode.requestFocus();
                        }else if ("tfBarCode".equals(fieldId)) {
                            tfDescription.requestFocus();
                        } else if ("tfDescription".equals(fieldId)) {
                            tfOrderQuantity.requestFocus();
                        }else if("tfOrderQuantity".equals(fieldId)) {
                            if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                                pnTblInvDetailRow++;
                            }
                            CommonUtils.SetNextFocus(sourceField);
                        loadTableInvDetailAndSelectedRow();
                        }
                        
                        event.consume();
                        break;

                    default:
                        break;

                
            }
              
          } catch (Exception e) {
                  ShowMessageFX.Error(getStage(), e.getMessage(), "Error",psFormName);
                  System.exit(1);
              }
      }
       


   private void loadTableInvDetailAndSelectedRow() {
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
                initDetailFocus();
            }
        }
    
    private void setOrderQuantityToDetail(String fsValue) {
            if (fsValue.isEmpty()) {
                fsValue = "0";
            }
            if (Double.parseDouble(fsValue) < 0) {
                ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);
                fsValue = "0";

            }
            if (tfOrderQuantity.isFocused()) {
                if (tfBarCode.getText().isEmpty()) {
                    ShowMessageFX.Warning("Invalid action, Please enter BarCode first. ", psFormName, null);
                    fsValue = "0";
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
        private void initTableList() {
        
        tblTransactionNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblReferenceNo.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblTransactionDate.setCellValueFactory(new PropertyValueFactory<>("index03"));

        tableListInformation.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tableListInformation.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
    }
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

        //step 6-7
        private void tblViewOrderDetails_Clicked(MouseEvent event) {
             if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE || pnEditMode == EditMode.READY) {
                int selectedIndex = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < invRequestController.StockRequest().getDetailCount()) {
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
        }
        private void tableListInformation_Clicked(MouseEvent event) {
        poJSON = new JSONObject();
        pnTblInformationRow = tableListInformation.getSelectionModel().getSelectedIndex();
        if (pnTblInformationRow < 0 || pnTblInformationRow >= tableListInformation.getItems().size()) {
            ShowMessageFX.Warning("Please select valid information List.", "Warning", null);
            return;
        }

        if (event.getClickCount() == 2) {
            ModelInvTableListInformation loSelectedInformation = (ModelInvTableListInformation) tableListInformation.getSelectionModel().getSelectedItem();
            if (loSelectedInformation != null) {
                String lsTransactionNo = loSelectedInformation.getIndex01();
                try {
                    poJSON = invRequestController.StockRequest().InitTransaction();
                    if ("success".equals((String) poJSON.get("result"))) {
                        poJSON = invRequestController.StockRequest().OpenTransaction(lsTransactionNo);
                        if ("success".equals((String) poJSON.get("result"))) {
                            loadMaster();
                            initTableInvDetail();
                            loadTableInvDetail();
                            pnTblInvDetailRow = -1;
                            clearDetailFields();
                            pnEditMode = invRequestController.StockRequest().getEditMode();
                        } else {
                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                            pnEditMode = EditMode.UNKNOWN;
                        }
                        initButtons(pnEditMode);
                        initFields(pnEditMode);

                    }
                } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                    Logger.getLogger(InvRequest_ConfirmationMcController.class
                            .getName()).log(Level.SEVERE, null, ex);
                    ShowMessageFX.Warning("Error loading data: " + ex.getMessage(), psFormName, null);
                }
            }
        }
    }
        private void initButtons(int fnEditMode) {
         boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
         CustomCommonUtil.setVisible(!lbShow ,btnClose, btnNew);
         CustomCommonUtil.setManaged(!lbShow ,btnClose, btnNew);

    

        CustomCommonUtil.setVisible(lbShow, btnSave, btnCancel);
        CustomCommonUtil.setManaged(lbShow, btnSave, btnCancel);

        CustomCommonUtil.setVisible(false, btnUpdate);
        CustomCommonUtil.setManaged(false, btnUpdate);

        
        if (fnEditMode == EditMode.READY) {
            switch (invRequestController.StockRequest().Master().getTransactionStatus()) {
                case StockRequestStatus.OPEN:
                    CustomCommonUtil.setVisible(true,  btnUpdate);
                    CustomCommonUtil.setManaged(true,  btnUpdate);
                    break;
                case StockRequestStatus.CONFIRMED:
                    CustomCommonUtil.setVisible(true,btnUpdate);
                    CustomCommonUtil.setManaged(true, btnUpdate);
                    break;
               
            }
        }
    }

      private void initDetailFocus() {
            if (pnEditMode == EditMode.ADDNEW ) {
                if (pnTblInvDetailRow >= 0) {
                    boolean isSourceNotEmpty = !invRequestController.StockRequest().Master().getSourceNo().isEmpty();
                    tfBrand.setDisable(isSourceNotEmpty);
                    tfBarCode.setDisable(isSourceNotEmpty);
                     tfDescription.setDisable(isSourceNotEmpty);
                     tfModel.setDisable(isSourceNotEmpty);
                    if (isSourceNotEmpty && !tfBrand.getText().isEmpty()) {
                        tfOrderQuantity.requestFocus();
                    } else {
                        if (!tfBarCode.getText().isEmpty() && (pnEditMode == EditMode.ADDNEW)) {
                            tfOrderQuantity.requestFocus();
                        } else {
                            tfBrand.requestFocus();
                        }
                    }
                }

            }else if (pnEditMode == EditMode.UPDATE ) {
                if (pnTblInvDetailRow >= 0) {
                    boolean isSourceNotEmpty = !invRequestController.StockRequest().Master().getSourceNo().isEmpty();
                    
                    if (isSourceNotEmpty && !tfBrand.getText().isEmpty()) {
                        tfOrderQuantity.requestFocus();
                    } else {
                        if (!tfBarCode.getText().isEmpty() && (pnEditMode == EditMode.UPDATE )) {
                            tfOrderQuantity.requestFocus();
                        }
                    }
                }

            }
        }

           private void initTextFieldFocus() {
        List<TextField> loTxtField = Arrays.asList(tfReferenceNo, tfOrderQuantity,tfSearchReferenceNo,tfOrderQuantity,tfBrand,tfDescription,tfModel);
        loTxtField.forEach(tf -> tf.focusedProperty().addListener(txtField_Focus));
         tfBrand.setOnMouseClicked(e -> activeField = tfBrand);
         tfBarCode.setOnMouseClicked(e -> activeField = tfBarCode);
         tfDescription.setOnMouseClicked(e -> activeField = tfDescription);
         tfModel.setOnMouseClicked(e -> activeField = tfModel);
    }  


        private void clearAllTables() {
   
    invOrderDetail_data.clear();
    tableListInformation_data.clear();
    
    
    Platform.runLater(() -> {
        tblViewOrderDetails.getItems().clear();
        tableListInformation.getItems().clear();
        
        tblViewOrderDetails.getSelectionModel().clearSelection();
        tableListInformation.getSelectionModel().clearSelection();
        
        
        tblViewOrderDetails.setPlaceholder(new Label("NO RECORD TO LOAD"));
        tableListInformation.setPlaceholder(new Label("NO RECORD TO LOAD"));
      
        tblViewOrderDetails.refresh();
        tableListInformation.refresh();
    });
}
       private void initTextFieldPattern() {
        
        CustomCommonUtil.inputDecimalOnly(tfOrderQuantity);
    }
}