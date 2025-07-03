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
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import org.guanzon.cas.inv.warehouse.status.StockRequestStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class InvRequest_ConfirmationMcController implements Initializable, ScreenInterface{
    @FXML
    private AnchorPane AnchorMain;
    @FXML
    private String psFormName = "Inv Stock Request Confirmation Mc";
    
    unloadForm poUnload = new unloadForm();
    private InvWarehouseControllers invRequestController;
        private GRiderCAS poApp;
        private String psIndustryID = "";
        private String psCompanyID = "";
        private String psBranchCode = "";
        private String psCategoryID = "";
        private LogWrapper logWrapper;
        private int pnTblInvDetailRow = -1;
        private int pnEditMode;
        private TextField activeField;
        private JSONObject poJSON;
        
        private  String brandID,categID; 
        private String brandDesc;
                             
        private ObservableList<ModelInvOrderDetail> invOrderDetail_data = FXCollections.observableArrayList();
        
        @FXML
        private TextField tfTransactionNo,tfBrand,tfModel,tfInvType,
                tfVariant,tfColor,tfROQ,tfClassification,tfQOH,tfReferenceNo,tfReservationQTY,tfOrderQuantity,tfSearchTransNo,tfSearchReferenceNo;
       

        @FXML
        private Label lblTransactionStatus,lblSource;

        @FXML
        private TextArea taRemarks;

        @FXML
        private TableView<ModelInvOrderDetail>tblViewOrderDetails;

        @FXML
        private Button btnClose,btnSave,btnCancel,btnVoid,btnBrowse,btnUpdate,btnRetrieve;

        @FXML
        private TableColumn<ModelInvOrderDetail, String> tblBrandDetail, tblModelDetail,tblVariantDetail,tblColorDetail,tblInvTypeDetail,tblROQDetail,tblClassificationDetail,tblQOHDetail,tblReservationQtyDetail,tblOrderQuantityDetail;

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
            
            JSONObject loJSON = new JSONObject();
            loJSON = invRequestController.StockRequest().InitTransaction();
            if (!"success".equals(loJSON.get("result"))) {
                    ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
                }
            tblViewOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);

             Platform.runLater((() -> {
                    //BOTH NULL
                   
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

                initButtonsClickActions();
                initTextFieldFocus();
                initTextAreaFocus();
                initTextFieldKeyPressed();
                initDatePickerActions();          
                initTableInvDetail();

                tblViewOrderDetails.setOnMouseClicked(this::tblViewOrderDetails_Clicked);
                initButtons(EditMode.UNKNOWN);
                initFields(EditMode.UNKNOWN);
                
                        
                }catch(ExceptionInInitializerError ex) {
                Logger.getLogger(InvStockRequest_EntryMcController.class.getName()).log(Level.SEVERE, null, ex);

            }
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
            try {
                
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
                poJSON =invRequestController.StockRequest().SearchBranch(lsStatus, true);   
                poJSON =invRequestController.StockRequest().SearchIndustry(lsStatus, true); 
                poJSON =invRequestController.StockRequest().SearchCategory(lsStatus, true); 
                categID = (String) poJSON.get("categID");  
                System.out.println("Category id"+categID);
                System.out.println("Categ id sa inv" + invRequestController.StockRequest().Master().getCategoryId());
                lblTransactionStatus.setText(lsStatus);
                dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                        SQLUtil.dateFormat(invRequestController.StockRequest().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
               
                tfReferenceNo.setText(invRequestController.StockRequest().Master().getReferenceNo());   

                taRemarks.setText(invRequestController.StockRequest().Master().getRemarks());

            } catch (SQLException | GuanzonException e) {
                ShowMessageFX.Error(getStage(), e.getMessage(), "Error",psFormName);
                System.exit(1);
            }
         }
          private void initDatePickerActions() {
            dpTransactionDate.setOnAction(e -> {
                if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                    if (dpTransactionDate.getValue() != null) {
                        invRequestController.StockRequest().Master().setTransactionDate(SQLUtil.toDate(dpTransactionDate.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
                    }
                }
            });
          }
        private void loadDetail() {
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
                    
                    String lsOrderQuantity = "0";
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
                            int quantity = (int) invRequestController.StockRequest().Detail(lnCntr).getValue("nQuantity");
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
                        //save transact is error
                          loJSON = invRequestController.StockRequest().SaveTransaction();
                            if (!"success".equals((String)loJSON.get("result"))) {
                                ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                loadTableInvDetail();

                                return;
                            }
                        
                        ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                        loJSON = invRequestController.StockRequest().OpenTransaction(invRequestController.StockRequest().Master().getTransactionNo());

                        if ("success".equals(loJSON.get("result")) && invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.OPEN)
                                && ShowMessageFX.YesNo(null, psFormName, "Do you want to confirm this transaction?")) {
                            if ("success".equals((loJSON = invRequestController.StockRequest().ConfirmTransaction("Confirmed")).get("result"))) {
                                ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                            }
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


                            }else{
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
                case "btnClose":
                    if (ShowMessageFX.YesNo("Are you sure you want to close this form?", psFormName, null)) {
                        if (poUnload != null) {
                            poUnload.unloadForm(AnchorMain, poApp, psFormName);
                        } else {
                            ShowMessageFX.Warning("Please notify the system administrator to configure the null value at the close button.", "Warning", null);
                        }
                    }
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
            }
            initButtons(pnEditMode);
            initFields(pnEditMode);
            }catch (CloneNotSupportedException | ExceptionInInitializerError | SQLException | GuanzonException | ParseException | NullPointerException e) {
                ShowMessageFX.Error(getStage(), e.getMessage(), "Error",psFormName);
                System.exit(1);
            }
        }

        private void clearDetailFields() {
            /* Detail Fields*/
            CustomCommonUtil.setText("", tfBrand, tfModel,
                    tfColor, tfReservationQTY, tfQOH,tfInvType, tfVariant,tfROQ,tfClassification);
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
                    
                    for (int i = 0; i < detailCount; i++) {
                        Model_Inv_Stock_Request_Detail detail = invRequestController.StockRequest().Detail(i);
                       
                        detailsList.add(new ModelInvOrderDetail(
                                detail.Inventory().Brand().getDescription(),
                                detail.Inventory().Model().getDescription(),
                                detail.Inventory().Variant().getDescription(),
                                detail.Inventory().Color().getDescription(),
                                detail.Inventory().InventoryType().getDescription(),
                                String.valueOf(detail.getRecommendedOrder()),
                                detail.getClassification(),
                                String.valueOf(detail.getQuantityOnHand()),
                                String.valueOf(detail.getReservedOrder()),
                                String.valueOf(detail.getQuantity()),
                                "",
                                ""

                        ));
                    }

                    Platform.runLater(() -> {
                        invOrderDetail_data.setAll(detailsList); // ObservableList<ModelInvOrderDetail>
                        tblViewOrderDetails.setItems(invOrderDetail_data);
                        reselectLastRow();
                        initFields(pnEditMode);
                    });

                    return detailsList;

                } catch (Exception ex) {
                    Logger.getLogger(InvStockRequest_EntryMcController.class.getName()).log(Level.SEVERE, null, ex);
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

            // for disabling textfield
        private void initFields(int fnEditMode) {
            boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
            /*Master Fields */
            CustomCommonUtil.setDisable(!lbShow,
                    dpTransactionDate, tfTransactionNo, taRemarks,
                     tfReferenceNo);
            CustomCommonUtil.setDisable(!lbShow,
                     tfOrderQuantity);
            CustomCommonUtil.setDisable(true,
                    tfInvType,tfVariant,tfColor,tfReservationQTY,
                    tfQOH,tfROQ,tfClassification,tfModel,tfBrand);
            if (!tfReferenceNo.getText().isEmpty()) {
                dpTransactionDate.setDisable(!lbShow);
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
                    tfOrderQuantity,tfSearchTransNo
                    );

            loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
        }  
        private void initButtonsClickActions() {
            List<Button> buttons = Arrays.asList( btnSave, btnCancel,
                    btnClose,btnVoid,btnBrowse,btnUpdate,btnRetrieve);

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
                                invRequestController.StockRequest().setTransactionStatus("102");
                                loJSON = invRequestController.StockRequest().searchTransaction();
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfSearchTransNo.setText("");
                                    break;
                                }
                                
                                tfSearchTransNo.setText(invRequestController.StockRequest().Master().getTransactionNo());
                                //loadTableListInformation();
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
                       
                      
                    case DOWN:
                        setOrderQuantityToDetail(tfOrderQuantity.getText());
                        if ("tfOrderQuantity".equals(sourceField.getId())) {
                            if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                                pnTblInvDetailRow++;
                            }
                        }
                        CommonUtils.SetNextFocus(sourceField);
                        loadTableInvDetailAndSelectedRow();
                        event.consume(); // Consume event after handling focus
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
            if (Integer.parseInt(fsValue) <= 0) {
                ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);
                fsValue = "0";

            }
            if (tfOrderQuantity.isFocused()) {
                if (tfBrand.getText().isEmpty()) {
                    ShowMessageFX.Warning("Invalid action, Please enter brand first. ", psFormName, null);
                    fsValue = "0";
                }
                if (!tfBrand.getText().isEmpty() && tfModel.getText().isEmpty()) {
                    ShowMessageFX.Warning("Invalid action, Please enter brand first then model. ", psFormName, null);
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
            invRequestController.StockRequest().Detail(pnTblInvDetailRow).setQuantity(Integer.valueOf(fsValue));

        }

        private void initTableInvDetail() {

            tblBrandDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
            tblModelDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
            tblVariantDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
            tblColorDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
            tblInvTypeDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
            tblROQDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
            tblClassificationDetail.setCellValueFactory(new PropertyValueFactory<>("index07"));
            tblQOHDetail.setCellValueFactory(new PropertyValueFactory<>("index08"));
            tblReservationQtyDetail.setCellValueFactory(new PropertyValueFactory<>("index09"));
            tblOrderQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index10"));
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

        private void initButtons(int fnEditMode) {
           boolean lbShow = (pnEditMode == EditMode.UPDATE);

        btnClose.setVisible(!lbShow);
        btnClose.setManaged(!lbShow);

        CustomCommonUtil.setVisible(lbShow, btnSave, btnCancel);
        CustomCommonUtil.setManaged(lbShow, btnSave, btnCancel);

        CustomCommonUtil.setVisible(false,  btnVoid);
        CustomCommonUtil.setManaged(false,   btnVoid);

        
        
        if (fnEditMode == EditMode.READY) {
            switch (invRequestController.StockRequest().Master().getTransactionStatus()) {
                case StockRequestStatus.OPEN:
                    CustomCommonUtil.setVisible(true, btnSave, btnVoid);
                    CustomCommonUtil.setManaged(true, btnSave, btnVoid);
                    break;
                case StockRequestStatus.CONFIRMED:
                    CustomCommonUtil.setVisible(true, btnSave, btnVoid);
                    CustomCommonUtil.setManaged(true, btnSave, btnVoid);
                    break;
               
            }
        }
        }

      private void initDetailFocus() {
           if (pnEditMode == EditMode.UPDATE) {
            if (pnTblInvDetailRow >= 0) {
                if (!tfBrand.getText().isEmpty()) {
                    tfOrderQuantity.requestFocus();
                }
            }
        }
        }

           private void initTextFieldFocus() {
        List<TextField> loTxtField = Arrays.asList(tfReferenceNo, tfOrderQuantity);
        loTxtField.forEach(tf -> tf.focusedProperty().addListener(txtField_Focus));
         tfBrand.setOnMouseClicked(e -> activeField = tfBrand);
         tfModel.setOnMouseClicked(e -> activeField = tfModel);
    }  



}
