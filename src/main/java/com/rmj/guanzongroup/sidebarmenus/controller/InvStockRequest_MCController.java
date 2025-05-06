/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import org.guanzon.cas.inv.warehouse.status.StockRequestStatus;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvOrderDetail;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import java.sql.SQLException;
import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

/**
 *
 * @author PC
 */
public class InvStockRequest_MCController implements Initializable, ScreenInterface{
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
    private String psFormName = "Inventory Request";
    
    private ObservableList<ModelInvOrderDetail> invOrderDetail_data = FXCollections.observableArrayList();
    
    @FXML
    private TextField tfTransactionNo,tfDescription,tfBrand,tfModel,tfCode,
            tfVariant,tfColor,tfCustomerOrder,tfOnHand,tfOrder,tfReferenceNo,tfCurrentInv,tfEstimatedInv,tfQuantity,tfTransactionDate;
    @FXML
    private AnchorPane AnchorMain;
    
    @FXML
    private Label lblTransactionStatus;
    
    @FXML
    private TextArea taRemarks;
    
    @FXML
    private TableView<ModelInvOrderDetail>tblViewOrderDetails;
    
    @FXML
    private Button btnNew,btnClose,btnSave,btnSearch,btnDelete,btnCancel;
            
    @FXML
    private TableColumn<ModelInvOrderDetail, String> tblBrandDetail, tblModelDetail, tblCodeDetail, tblVariantDetail,
            tblColorDetail, tblCustomerOrderDetail, tblOnHandDetail, tblOrderDetail,tblQuantityDetail;
    @FXML
    private HBox hbButtons;
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
    
    @Override//step 1   
    public void initialize(URL url, ResourceBundle rb) {
        try{
        invRequestController = new InvWarehouseControllers(poApp,logWrapper);
        invRequestController.StockRequest().setTransactionStatus("017");
        JSONObject loJSON = new JSONObject();
        loJSON = invRequestController.StockRequest().InitTransaction();
        if (!"success".equals(loJSON.get("result"))) {
                ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
            }
        tblViewOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        initTextFieldFocus();
        initFields(pnEditMode);
         Platform.runLater((() -> {
                invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                
            }));
            Platform.runLater(() -> btnNew.fire());
            initButtonsClickActions();
            initTextFieldFocus();
            initTextAreaFocus();
            initTextFieldKeyPressed();
            initDatePickerActions();          
            initTableInvDetail();
            tblViewOrderDetails.setOnMouseClicked(this::tblViewOrderDetails_Clicked);
            initButtons(pnEditMode);
            initFields(pnEditMode);
        }catch(ExceptionInInitializerError ex) {
            Logger.getLogger(InvStockRequest_MCController.class.getName()).log(Level.SEVERE, null, ex);
        
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

        if (focusedCell != null && "tblVwOrderDetails".equals(currentTable.getId())) {
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
            
            lblTransactionStatus.setText(lsStatus);
            
            lblTransactionStatus.setText(lsStatus);
            tfTransactionDate.setText(SQLUtil.dateFormat(invRequestController, SQLUtil.FORMAT_SHORT_DATE));
            tfReferenceNo.setText(invRequestController.StockRequest().Master().getReferenceNo());   
            taRemarks.setText(invRequestController.StockRequest().Master().getRemarks());
            tfCurrentInv.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(invRequestController.StockRequest().Master().getCurrentInventory()));
            tfEstimatedInv.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(invRequestController.StockRequest().Master().getEstimateInventory()));
            
            
        } catch (SQLException | GuanzonException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error",psFormName);
            System.exit(1);
        }
        
     }
    
    private void loadDetail() {
        try {
            if (pnTblInvDetailRow >= 0) {
                tfDescription.setText(invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().getDescription());
                tfBrand.setText(invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription());
                tfModel.setText(invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Model().getDescription());
                tfColor.setText(invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Color().getDescription());
                tfCode.setText(invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().getModelId());
                tfVariant.setText(invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Variant().getDescription());
                tfCustomerOrder.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getReservedOrder()));
                tfOrder.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getRecommendedOrder()));
                tfOnHand.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantityOnHand()));
                tfQuantity.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(invRequestController.StockRequest().Detail(pnTblInvDetailRow).getQuantity()));
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
            // In your btnNew case, ensure proper initialization
                    case "btnNew":
                        clearDetailFields();
                        clearMasterFields();
                        invOrderDetail_data.clear();
                        loJSON = invRequestController.StockRequest().NewTransaction();
                        if ("success".equals((String) loJSON.get("result"))) {    
                            invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                            invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                            invRequestController.StockRequest().Master().setBranchCode(psBranchCode);
                            loadMaster();
                            pnTblInvDetailRow = -1;
                            pnEditMode = invRequestController.StockRequest().getEditMode();
                            // Add initial empty detail row
                            invRequestController.StockRequest().AddDetail();
                            pnTblInvDetailRow = 0;
                            loadTableInvDetail();
                        }
                        break;
                    case "btnSearch":
                    if (activeField == null) {
                        ShowMessageFX.Warning("Please select a searchable field first", psFormName, null);
                        return;
                    }

                    String loTextFieldId = activeField.getId();
                    String lsValue = activeField.getText().trim();

                    if (loTextFieldId.equals("tfBrand")) {
                        // Ensure we're in add/edit mode
                        if (pnEditMode != EditMode.ADDNEW && pnEditMode != EditMode.UPDATE) {
                            ShowMessageFX.Warning("Please start a new transaction first", psFormName, null);
                            return;
                        }

                        // Add new detail if needed
                        if (pnTblInvDetailRow < 0 || pnTblInvDetailRow >= invRequestController.StockRequest().getDetailCount()) {
                            invRequestController.StockRequest().AddDetail();
                            pnTblInvDetailRow = invRequestController.StockRequest().getDetailCount() - 1;
                        }

                        poJSON = invRequestController.StockRequest().SearchBrand(lsValue, false, pnTblInvDetailRow);
                        if ("error".equals(poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                            tfBrand.setText("");
                            if (poJSON.get("tableRow") != null) {
                                pnTblInvDetailRow = (int) poJSON.get("tableRow");
                            }
                            return;
                        }
                         tfBrand.setText(invRequestController.StockRequest().Detail(pnTblInvDetailRow).Brand().getDescription());
                                if (!tfModel.getText().isEmpty()) {
                                    tfQuantity.requestFocus();
                                }
                        loadTableInvDetail();
                        loadDetail();
                        initDetailFocus();
                        tfQuantity.requestFocus(); // Move focus to quantity after search
                    }
                    break;
                case "btnSave":
                if (!ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to save?")) {
                    return;
                }

                // Validate required text fields
                if (tfCurrentInv.getText().trim().isEmpty() ||
                    tfEstimatedInv.getText().trim().isEmpty() ||
                    tfReferenceNo.getText().trim().isEmpty() ||
                    tfBrand.getText().trim().isEmpty() ||
                    tfDescription.getText().trim().isEmpty()) {

                    ShowMessageFX.Warning("Please fill in all required fields: Brand, Description, Inventory values, and Reference Number.", psFormName, null);
                    return;
                }

                // Validate at least one valid stock item
                int detailCount = invRequestController.StockRequest().getDetailCount();
                if (detailCount == 0) {
                    ShowMessageFX.Warning("Your order is empty. Please add at least one item.", psFormName, null);
                    return;
                }

                boolean hasValidItem = false;
                for (int lnCntr = 0; lnCntr < detailCount; lnCntr++) {
                    int quantity = (int) invRequestController.StockRequest().Detail(lnCntr).getValue("nQuantity");
                    String stockID = (String) invRequestController.StockRequest().Detail(lnCntr).getValue("sStockIDx");

                    // If any stock ID is empty OR quantity is 0, show an error if it's the only item
                    if (detailCount == 1 && (stockID == null || stockID.trim().isEmpty() || quantity == 0)) {
                        ShowMessageFX.Warning("Invalid item in order. Ensure all items have a valid Stock ID and quantity greater than 0.", psFormName, null);
                        return;
                    }

                    if (stockID != null && !stockID.trim().isEmpty() && quantity > 0) {
                        hasValidItem = true;
                    }
                }

                if (!hasValidItem) {
                    ShowMessageFX.Warning("Please ensure at least one valid item with quantity > 0 and a valid Stock ID.", psFormName, null);
                    return;
                }

                // Optional: Approval dialog for update (only if needed based on your logic)
                if (pnEditMode == EditMode.UPDATE && invRequestController.StockRequest().Master().getTransactionStatus().equals("CONFIRMED")) {
                    loJSON = ShowDialogFX.getUserApproval(poApp);
                    if (!"success".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        return;
                    }
                }

                // Assign modification details
                if (pnEditMode == EditMode.UPDATE) {
                    invRequestController.StockRequest().Master().setModifiedDate(poApp.getServerDate());
                    invRequestController.StockRequest().Master().setModifyingId(poApp.getUserID());
                }

                for (int lnCntr = 0; lnCntr < detailCount; lnCntr++) {
                    invRequestController.StockRequest().Detail(lnCntr).setModifiedDate(poApp.getServerDate());
                }

                // Check if any item has zero quantity (backend check)
                loJSON = invRequestController.StockRequest().isDetailHasZeroQty();
                if (!"success".equals((String) loJSON.get("result"))) {
                    if (!ShowMessageFX.YesNo((String) loJSON.get("message"), psFormName, null)) {
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

                // Reopen transaction to refresh view
                loJSON = invRequestController.StockRequest().OpenTransaction(
                    invRequestController.StockRequest().Master().getTransactionNo()
                );

                // Confirm transaction if open and prompt user
                if ("success".equals(loJSON.get("result")) &&
                    invRequestController.StockRequest().Master().getTransactionStatus().equals("OPEN") &&
                    ShowMessageFX.YesNo(null, psFormName, "Do you want to confirm this transaction?")) {

                    loJSON = invRequestController.StockRequest().ConfirmTransaction("Confirmed");
                    if ("success".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                    }
                }



    Platform.runLater(() -> btnNew.fire());
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
        CustomCommonUtil.setText("", tfBrand, tfDescription, tfBrand, tfModel,
                tfColor, tfCustomerOrder, tfOnHand, tfOrder,tfCode, tfVariant);
       
    }
    
    private void clearMasterFields() {
        /* Master Fields*/
        pnTblInvDetailRow = -1;
        tfTransactionDate.setText(null);
        taRemarks.setText("");
        CustomCommonUtil.setText("",  tfReferenceNo,tfCurrentInv,tfEstimatedInv);
        
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
                        if (lastDetail.getStockId()!= null && !lastDetail.getStockId().isEmpty()) {
                            invRequestController.StockRequest().AddDetail();
                            detailCount++;
                        }
                    }
                    double grandTotalAmount = 0.0;
                    List<ModelInvOrderDetail> detailsList = new ArrayList<>();

                    for (int lnCtr = 0; lnCtr < detailCount; lnCtr++) {
                        Model_Inv_Stock_Request_Detail orderDetail = invRequestController.StockRequest().Detail(lnCtr);                        
        
                        detailsList.add(new ModelInvOrderDetail( 
                                orderDetail.Inventory().Brand().getDescription(),                          // Brand
                                orderDetail.Inventory().Model().getDescription(),   
                                orderDetail.Inventory().getModelId(),                  
                                orderDetail.Inventory().Color().getDescription(),                          
                                orderDetail.Inventory().Variant().getDescription(),      
                                CustomCommonUtil.setIntegerValueToDecimalFormat(orderDetail.getReservedOrder()),      
                                CustomCommonUtil.setIntegerValueToDecimalFormat(orderDetail.getRecommendedOrder()),      
                                CustomCommonUtil.setIntegerValueToDecimalFormat(orderDetail.getQuantityOnHand()),
                                CustomCommonUtil.setIntegerValueToDecimalFormat(orderDetail.getQuantity())
                                
                                        
                        ));
                    }
                    final double totalAmountFinal = grandTotalAmount;
                    Platform.runLater(() -> {
                        invOrderDetail_data.setAll(detailsList); // Properly update list
                        tblViewOrderDetails.setItems(invOrderDetail_data);
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            if (totalAmountFinal <= 0.0) {
                                tfCurrentInv.setText("0.00");
                                tfEstimatedInv.setText("0.00");
                       
                            }
                         
                        }
                        reselectLastRow();
                        initFields(pnEditMode);
                    });

                    return detailsList;

                } catch (GuanzonException | SQLException ex) {
                    Logger.getLogger(InvStockRequest_MCController.class.getName()).log(Level.SEVERE, null, ex);
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
                tfTransactionDate, tfTransactionNo, taRemarks,
                 tfReferenceNo);
        
        CustomCommonUtil.setDisable(true, tfCurrentInv, tfEstimatedInv,
                tfModel,tfCode,tfVariant,tfColor,tfCustomerOrder,
                tfOnHand,tfOrder);
        if (!tfReferenceNo.getText().isEmpty()) {
            tfTransactionDate.setDisable(!lbShow);
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
                tfBrand,tfQuantity
                );

        loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
    }  
    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList( btnNew, btnSearch, btnSave, btnCancel,
                btnClose,btnDelete);

        buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
    }    
  private void txtField_KeyPressed(KeyEvent event) {
    TextField sourceField = (TextField) event.getSource();
    String fieldId = sourceField.getId();
    String value = sourceField.getText() == null ? "" : sourceField.getText();

    try {
        if (event.getCode() == null) return;
        String lsValue = sourceField.getText().trim();

        switch (event.getCode()) {
            case TAB:
            case ENTER:
                
            case F3:
                switch (fieldId) {
                    case "tfModel":
                        // Add logic if needed
                        CommonUtils.SetNextFocus(sourceField);
                        break;

                    case "taRemarks":
                        CommonUtils.SetNextFocus(sourceField);
                        break;

                    case "tfQuantity":
                            setOrderQuantityToDetail(lsValue); 
                            if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                                pnTblInvDetailRow++;
                            }
                            CommonUtils.SetNextFocus(sourceField);
                            loadTablePODetailAndSelectedRow();
                            break;

                    case "tfBrand":
                        poJSON = invRequestController.StockRequest().SearchBrand(lsValue, false, pnTblInvDetailRow);
                        if ("error".equals(poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                            tfBrand.setText("");
                            if (poJSON.get("tableRow") != null) {
                                pnTblInvDetailRow = (int) poJSON.get("tableRow");
                            }
                        }
                        loadTableInvDetail();
                        loadDetail();
                        initDetailFocus();
                        break;
                }
             
                        event.consume();
                        break;
            case UP:
                setOrderQuantityToDetail(lsValue);
                if ("tfOrderQuantity".equals(fieldId)) {
                    
                    if (pnTblInvDetailRow > 0 && !invOrderDetail_data.isEmpty()) {
                        pnTblInvDetailRow--;
                    }
                    loadTablePODetailAndSelectedRow();
                }

                CommonUtils.SetPreviousFocus(sourceField);
                event.consume();
                break;

            case DOWN:
                 setOrderQuantityToDetail(lsValue);
                if ("tfOrderQuantity".equals(fieldId)) {
                   
                    if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                        pnTblInvDetailRow++;
                    }
                    loadTablePODetailAndSelectedRow();
                }

                CommonUtils.SetNextFocus(sourceField);
                event.consume();
                break;

            default:
                break;
        }

    } catch (SQLException | GuanzonException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error",psFormName);
            System.exit(1);
        }
}



private void loadTablePODetailAndSelectedRow() {
        if (pnTblInvDetailRow >= 0) {
            Platform.runLater(() -> {
                // Run a delay after the UI thread is free
                PauseTransition delay = new PauseTransition(Duration.millis(10));
                delay.setOnFinished(event -> {
                    Platform.runLater(() -> { // Run UI updates in the next cycle
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
        if (Integer.parseInt(fsValue) < 0) {
            ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);
            fsValue = "0";
        }
        if (tfQuantity.isFocused()) {
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
        tfQuantity.setText(fsValue);
        invRequestController.StockRequest().Detail(pnTblInvDetailRow).setQuantity(Integer.valueOf(fsValue));
    }

    private void initTableInvDetail() {
        tblBrandDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblModelDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblCodeDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblVariantDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblColorDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblCustomerOrderDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblOnHandDetail.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblOrderDetail.setCellValueFactory(new PropertyValueFactory<>("index08"));
        tblQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index09"));
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
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        CustomCommonUtil.setVisible(!lbShow, btnClose, btnNew);
        CustomCommonUtil.setManaged(!lbShow, btnClose, btnNew);

        CustomCommonUtil.setVisible(lbShow, btnSearch, btnSave, btnCancel,btnDelete);
        CustomCommonUtil.setManaged(lbShow, btnSearch, btnSave, btnCancel,btnDelete);

        
        
        if (fnEditMode == EditMode.READY) {
            switch (invRequestController.StockRequest().Master().getTransactionStatus()) {
                case StockRequestStatus.OPEN:
                case StockRequestStatus.CONFIRMED:               
            }

        }
    }
    private void initDatePickerActions() {
        tfTransactionDate.setOnAction(e -> {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (tfTransactionDate.getText()!= null) {
                    invRequestController.StockRequest().Master().setTransactionDate(SQLUtil.toDate(tfTransactionDate.getText().toString(), SQLUtil.FORMAT_SHORT_DATE));
                }
            }
        });
        
    }
    private void initDetailFocus() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            if (pnTblInvDetailRow >= 0) {
                boolean isSourceNotEmpty = !invRequestController.StockRequest().Detail(pnTblInvDetailRow).getTransactionNo().isEmpty();
                
                tfDescription.setDisable(isSourceNotEmpty);
                if (isSourceNotEmpty && tfBrand.getText().isEmpty()) {
                   tfBrand.requestFocus();
                }else{
                    tfQuantity.requestFocus();
                }
            }

        }
    }
    private void initTextFieldFocus() {
    List<TextField> searchableFields = Arrays.asList(tfBrand, tfQuantity);
    searchableFields.forEach(tf -> {
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) activeField = tf;
        });
        tf.focusedProperty().addListener(txtField_Focus);
    });
}  
    
    
    
}
