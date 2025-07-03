                              /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;


import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import org.guanzon.cas.inv.warehouse.status.StockRequestStatus;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvOrderDetail;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TablePosition;
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
import javafx.util.Duration;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.LogWrapper;
import org.json.simple.parser.ParseException;


/**
 *
 * @author User
 */
public class InvStockRequest_EntrySpController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private InvWarehouseControllers invRequestController;
    private TextField activeField;
    private int pnTblInvDetailRow = -1;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    private JSONObject poJSON;
    private int pnEditMode;
    private LogWrapper logWrapper;
   

    String brandID;
    String brandDesc;
    private ObservableList<ModelInvOrderDetail> invOrderDetail_data = FXCollections.observableArrayList();
    

    //FXML elements
    @FXML
    private TextArea taRemarks;
   @FXML
        private Button btnNew,btnClose,btnSave,btnSearch,btnCancel,btnVoid,btnBrowse;
    @FXML
    private TableView<ModelInvOrderDetail> tblViewOrderDetails;
    @FXML
        private TableColumn<ModelInvOrderDetail, String> tblBrandDetail, tblModelDetail,tblVariantDetail,tblColorDetail,tblInvTypeDetail,
            tblROQDetail,tblClassificationDetail,tblQOHDetail,
            tblReservationQtyDetail,tblOrderQuantityDetail,tblDescriptionDetail,tblBarCodeDetail;
    @FXML
    private DatePicker dpTransactionDate;
    @FXML
        private TextField tfTransactionNo,tfBrand,tfModel,tfInvType,
                tfVariant,tfColor,tfROQ,tfClassification,tfQOH,tfReferenceNo,tfReservationQTY,tfOrderQuantity,tfDescription,tfBarCode;
        
    @FXML
    private Label lblTransactionStatus, lblSource; //check this
    @FXML
    private String psFormName = "Inv Stock Request MC";
    
    @FXML
    private AnchorPane AnchorMain;
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
    
    //Initialize
    @Override
    public void initialize(URL url, ResourceBundle rb) { //step 1 and 2
        invRequestController = new InvWarehouseControllers(poApp, logWrapper);
        invRequestController.StockRequest().setTransactionStatus("017"); //check
        JSONObject loJSON = new JSONObject();
        loJSON = invRequestController.StockRequest().InitTransaction(); //allows us to access InvWarehouseModels for us to access master and detail
        
        if (!"success".equals(loJSON.get("result"))) {
            ShowMessageFX.Warning((String) loJSON.get("message"), "Search Information", null);
        }
        tblViewOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
       
        Platform.runLater((() -> {
            invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
//            invRequestController.StockRequest().Master().setCompanyID(psCompanyID);

        }));
        
        initButtonsClickActions();
        initTextFieldFocus();
        initTextAreaFocus();
        initTextFieldKeyPressed();
        initDatePickerActions();          
        initTableInvDetail();
        tblViewOrderDetails.setOnMouseClicked(this::tblViewOrderDetails_Clicked);
        initFields(EditMode.UNKNOWN);
        initButtons(EditMode.UNKNOWN); 
     
    }

    //buttons section
    public void handleButtonAction(ActionEvent event) {
        try {
            JSONObject loJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            
            
            switch (lsButton) {
                //step 3
                case "btnNew":
                    clearDetailFields();
                    clearMasterFields();
                    invOrderDetail_data.clear();
                    loJSON = invRequestController.StockRequest().NewTransaction();
                    if ("success".equals((String) loJSON.get("result"))) {
                        invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                        invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                        //invRequestController.StockRequest().Master().setBranchCode(psBranchCode); 
                        loadMaster();
                        pnTblInvDetailRow = -1;
                        pnEditMode = invRequestController.StockRequest().getEditMode();
                        loadTableInvDetail();
                    } else {
                        ShowMessageFX.Warning((String) loJSON.get("message"), "Warning", null);
                    }
                    break;
                
                    //step 4    
                case "btnSearch":
                    if (activeField == null) {
                        ShowMessageFX.Warning("Please select a searchable field first", psFormName, null);
                        return;
                    }
                    
                    String loTextFieldId = activeField.getId();
                    String lsValue = activeField.getText().trim();
                    
                    switch (loTextFieldId) {
                        case "tfBrand":
                              if (pnTblInvDetailRow < 0) {
                                      ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                      clearDetailFields();
                                      break;
                                  }
                            loJSON = invRequestController.StockRequest().SearchBrand(lsValue, false,psIndustryID);
                            
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
                                tfDescription.requestFocus();
                            }
                            loadTableInvDetail();
                            break;

                        case "tfBarcode":
                                if (pnTblInvDetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                    poJSON = invRequestController.StockRequest().SearchBarcode(lsValue, true, pnTblInvDetailRow,brandID,psIndustryID
                                );
                                
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfBarCode.setText("");
                                    if (poJSON.get("tableRow") != null) {
                                        pnTblInvDetailRow = (int) loJSON.get("tableRow");
                                    } else {
                                        break;
                                    }
                                }
                                if ("success".equals(poJSON.get("result"))) {
                                          // Get current quantity
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
                               
                                break;
                        
                         case "tfDescription":
                                if (pnTblInvDetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                poJSON = invRequestController.StockRequest().SearchBarcodeDescription(lsValue, false, pnTblInvDetailRow,psIndustryID,brandID
                                );
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfDescription.setText("");
                                    if (poJSON.get("tableRow") != null) {
                                        pnTblInvDetailRow = (int) poJSON.get("tableRow");
                                    } else {
                                        break;
                                    }
                                }
                                if ("success".equals(poJSON.get("result"))) {
                                          // Get current quantity
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
                                break;
                         }
                    break;
                    
                case "btnSave":

                    if (!ShowMessageFX.YesNo(null, psFormName, "Are you sure you want to save?")) {
                        return;
                    }
                    // Validate required text fields
                    if (tfReferenceNo.getText().trim().isEmpty() ||
                        tfBrand.getText().trim().isEmpty() ||
                        tfDescription.getText().trim().isEmpty()) {

                        ShowMessageFX.Warning("Please fill in all required fields: Brand, Description, Inventory values, and Reference Number.", psFormName, null);
                        return;
                    }
//                    prevSupplier = invRequestController.StockRequest().Detail(pnEditMode).getSupplierID();
                    // Validate at least one valid stock item
                    int detailCount = invRequestController.StockRequest().getDetailCount();
                    if (detailCount == 0) {
                        ShowMessageFX.Warning("Your order is empty. Please add at least one item.", psFormName, null);
                        return;
                    }
                    boolean hasValidItem = false; // True if at least one valid item exists
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

                    // If no valid items exist, prevent saving
                    if (!hasValidItem) {
                        ShowMessageFX.Warning("Your order must have at least one valid item with a Stock ID and quantity greater than 0.", psFormName, null);
                        return;
                    }

                    // Backend validations for EditMode
                    if (pnEditMode == EditMode.UPDATE && (invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.CONFIRMED))) {
                        if (!"success".equals((loJSON = ShowDialogFX.getUserApproval(poApp)).get("result"))) {
                            ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                            return;
                        }
                    }

                    // Assign modification details for Update Mode
                    if (pnEditMode == EditMode.UPDATE) {
                        invRequestController.StockRequest().Master().setModifiedDate(poApp.getServerDate());
                        invRequestController.StockRequest().Master().setModifyingId(poApp.getUserID());
                    }
                    // Assign modification date to all details
                    for (int lnCntr = 0; lnCntr < detailCount; lnCntr++) {
                        invRequestController.StockRequest().Detail(lnCntr).setModifiedDate(poApp.getServerDate());
                    }
                    // Save Transaction
                    loJSON = invRequestController.StockRequest().isDetailHasZeroQty(); //step 13
                    if (!"success".equals((String) loJSON.get("result"))) {
                        if (!ShowMessageFX.YesNo((String) loJSON.get("message"), psFormName, null)) {
                            pnTblInvDetailRow = (int) loJSON.get("tableRow");
                            loadTableInvDetail();
                            loadDetail();
                            initDetailFocus();
                            return;
                        }
                    }
                    loJSON = invRequestController.StockRequest().SaveTransaction(); //step 12
                    if (!"success".equals((String) loJSON.get("result"))) {
                        ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                        loadTableInvDetail();
                        return;
                    }
                    ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                    loJSON = invRequestController.StockRequest().OpenTransaction(invRequestController.StockRequest().Master().getTransactionNo());
                    // Confirmation Prompt
                    if ("success".equals(loJSON.get("result")) && invRequestController.StockRequest().Master().getTransactionStatus().equals(StockRequestStatus.OPEN)
                            && ShowMessageFX.YesNo(null, psFormName, "Do you want to confirm this transaction?")) {
                        if ("success".equals((loJSON = invRequestController.StockRequest().ConfirmTransaction("Confirmed")).get("result"))) {
                            ShowMessageFX.Information((String) loJSON.get("message"), psFormName, null);
                        }
                    }

                    Platform.runLater(() -> btnNew.fire());
                    break;

                case "btnCancel": //step 14

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

                case "btnTransHistory":
                    break;
                

            }
            initButtons(pnEditMode);
            initFields(pnEditMode);
        } catch (CloneNotSupportedException | ExceptionInInitializerError | SQLException | GuanzonException | ParseException | NullPointerException e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", psFormName);
        }
    }

    //functions
     private void loadRecordSearch() {
        try {
            
            lblSource.setText(invRequestController.StockRequest().Master().Company().getCompanyName() + " - " + invRequestController.StockRequest().Master().Industry().getDescription());
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(InvStockRequest_EntrySpController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
     
     
   private void clearDetailFields() {
            /* Detail Fields*/
            CustomCommonUtil.setText("", tfBrand, tfModel,
                    tfColor, tfReservationQTY, tfQOH,tfInvType, tfVariant,tfROQ,tfClassification,
                    tfDescription,tfBarCode);
           CustomCommonUtil.setText("0", tfOrderQuantity);
        }    

    private void clearMasterFields() {
        /* Master Fields*/
        pnTblInvDetailRow = -1;
        dpTransactionDate.setValue(null);
        
        taRemarks.setText("");
        CustomCommonUtil.setText("",  tfReferenceNo, tfTransactionNo);
        

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
                case StockRequestStatus.CANCELLED:
                    lsStatus = "CANCELLED";
                    break;
                case StockRequestStatus.VOID:
                    lsStatus = "VOID";
                    break;
            }
            
            lblTransactionStatus.setText(lsStatus); //step 15-16
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(
                    SQLUtil.dateFormat(invRequestController.StockRequest().Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
            tfReferenceNo.setText(invRequestController.StockRequest().Master().getReferenceNo());

            taRemarks.setText(invRequestController.StockRequest().Master().getRemarks());

           
        } catch (Exception e) {
            ShowMessageFX.Error(getStage(), e.getMessage(), "Error", psFormName);
        }

    }
    //step 7
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
                                detail.Inventory().getInventoryTypeId(),
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
                    Logger.getLogger(InvStockRequest_EntrySpController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void loadTableInvDetailAndSelectedRow() {
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

    private void loadDetail() {
            try {
                if (pnTblInvDetailRow >= 0) {

                    String lsBrand = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription() != null) {
                        lsBrand = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription();
                    }
                    tfBrand.setText(lsBrand);
                    
                    String lsDescription = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription() != null) {
                        lsDescription = invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().getDescription();
                    }
                    tfDescription.setText(lsDescription);
                    
                    String lsBarCode = "";
                    if (invRequestController.StockRequest().Detail(pnTblInvDetailRow).Inventory().Brand().getDescription() != null) {
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

    // Method to reselect the last clicked row
    private void reselectLastRow() {
        if (pnTblInvDetailRow >= 0 && pnTblInvDetailRow < tblViewOrderDetails.getItems().size()) {
            tblViewOrderDetails.getSelectionModel().clearAndSelect(pnTblInvDetailRow);
            tblViewOrderDetails.getSelectionModel().focus(pnTblInvDetailRow); // Scroll to the selected row if needed
        }
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
                        case "taRemarks":
                            CommonUtils.SetNextFocus(sourceField);
                            break;

                        case "tfOrderQuantity": //step 8
                            setOrderQuantityToDetail(lsValue); 
                            if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                                pnTblInvDetailRow++;
                            }
                            CommonUtils.SetNextFocus(sourceField);
                            loadTableInvDetailAndSelectedRow();
                            break;
                        case "tfBarcode":
                             if (pnTblInvDetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                
                                    loJSON = invRequestController.StockRequest().SearchBarcode(lsValue, true, pnTblInvDetailRow,brandID,psIndustryID
                                );
                                
                                if ("error".equals(loJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) loJSON.get("message"), psFormName, null);
                                    tfBarCode.setText("");
                                    if (loJSON.get("tableRow") != null) {
                                        pnTblInvDetailRow = (int) loJSON.get("tableRow");
                                    } else {
                                        break;
                                    }
                                }
                                if ("success".equals(poJSON.get("result"))) {
                                          // Get current quantity
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

                        case "tfDescription":
                           if (pnTblInvDetailRow < 0) {
                                    ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                    clearDetailFields();
                                    break;
                                }
                                poJSON = invRequestController.StockRequest().SearchBarcodeDescription(lsValue, false, pnTblInvDetailRow,psIndustryID,brandID
                                );
                                if ("error".equals(poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), psFormName, null);
                                    tfDescription.setText("");
                                    if (poJSON.get("tableRow") != null) {
                                        pnTblInvDetailRow = (int) poJSON.get("tableRow");
                                    } else {
                                        break;
                                    }
                                }
                                if ("success".equals(poJSON.get("result"))) {
                                          // Get current quantity
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
                                break;
                        case "tfBrand":
                           if (pnTblInvDetailRow < 0) {
                                      ShowMessageFX.Warning("Invalid row to update.", psFormName, null);
                                      clearDetailFields();
                                      break;
                                  }
                            loJSON = invRequestController.StockRequest().SearchBrand(lsValue, false,psIndustryID);
                            
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
                                tfDescription.requestFocus();
                            }
                            loadTableInvDetail();
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
                        loadTableInvDetailAndSelectedRow();
                    }

                    CommonUtils.SetPreviousFocus(sourceField);
                    event.consume();
                    break;

                case DOWN:
                     setOrderQuantityToDetail(lsValue);
                    if ("tfOrderQuantity".equals(fieldId)) {

                        if (!invOrderDetail_data.isEmpty() && pnTblInvDetailRow < invOrderDetail_data.size() - 1) {
                            pnTblInvDetailRow++; //step 10, move to next row
                        }
                        loadTableInvDetailAndSelectedRow();
                    }

                    CommonUtils.SetNextFocus(sourceField);
                    event.consume();
                    break;

                default:
                    break;
            }

        } catch (Exception ex) {
            Logger.getLogger(InvStockRequest_EntrySpController.class.getName()).log(Level.SEVERE, null, ex);
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
                        loadDetail(); //populate fields
                        initDetailFocus();
                    }
                }
            }
        }
    }

    private void tableKeyEvents(KeyEvent event) {
        TableView<?> currentTable = (TableView<?>) event.getSource();
        TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();

        if (focusedCell != null && "tblViewOrderDetails".equals(currentTable.getId())) {
            switch (event.getCode()) {
                case TAB:
                case DOWN:
                    
                    if (pnEditMode != EditMode.ADDNEW || pnEditMode != EditMode.UPDATE) {
                        pnTblInvDetailRow = moveToNextRow(currentTable, focusedCell);
                    }
                    break;
                case UP:

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
    private void setOrderQuantityToDetail(String fsValue) {
        if (fsValue.isEmpty()) {
            fsValue = "0";
        }
        if (Integer.parseInt(fsValue) < 0) { //step 9 validation
            ShowMessageFX.Warning("Invalid Order Quantity", psFormName, null);
            fsValue = "0";
        }
        if (tfOrderQuantity.isFocused()) {
            if (tfBrand.getText().isEmpty()) {
                ShowMessageFX.Warning("Invalid action, Please enter barcode first. ", psFormName, null);
                tfBrand.requestFocus();
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
    private void initTextFieldFocus() {
    List<TextField> searchableFields = Arrays.asList(tfBrand,tfBarCode, tfDescription);
    searchableFields.forEach(tf -> {
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) activeField = tf;
        });
        tf.focusedProperty().addListener(txtField_Focus);
    });
    }
    private void initFields(int fnEditMode) {
            boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
            /*Master Fields */
            CustomCommonUtil.setDisable(!lbShow,
                    dpTransactionDate, tfTransactionNo, taRemarks,
                     tfReferenceNo);
            CustomCommonUtil.setDisable(!lbShow,
                    tfBrand, tfModel,tfDescription,tfBarCode, tfOrderQuantity);
            CustomCommonUtil.setDisable(true,
                    tfInvType,tfVariant,tfColor,tfReservationQTY,
                    tfQOH,tfROQ,tfClassification);
            if (!tfReferenceNo.getText().isEmpty()) {
                dpTransactionDate.setDisable(!lbShow);
            }
        } 

     private void initButtonsClickActions() {
            List<Button> buttons = Arrays.asList( btnNew, btnSearch, btnSave, btnCancel,
                    btnClose,btnVoid,btnBrowse);

            buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
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
                    invRequestController.StockRequest().Master().setReferenceNo(lsValue);
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
                    invRequestController.StockRequest().Master().setRemarks(lsValue); //step 11
                    break;
            }
        } else {
            loTextArea.selectAll();
        }
    };

        private void initTextFieldKeyPressed() {
        List<TextField> loTxtField = Arrays.asList(tfBarCode, tfBrand,
                tfDescription,tfOrderQuantity);

        loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
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

    private void initButtons(int fnEditMode) {
            boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
            CustomCommonUtil.setVisible(!lbShow ,btnClose, btnNew);
            CustomCommonUtil.setManaged(!lbShow ,btnClose, btnNew);

            CustomCommonUtil.setVisible(lbShow, btnSearch, btnSave, btnCancel,btnVoid);
            CustomCommonUtil.setManaged(lbShow, btnSearch, btnSave, btnCancel,btnVoid);



            if (fnEditMode == EditMode.READY) {
                switch (invRequestController.StockRequest().Master().getTransactionStatus()) {
                    case StockRequestStatus.OPEN:
                    case StockRequestStatus.CONFIRMED:               
                }

            }
        }
    
    private void initDetailFocus() {
        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
            if (pnTblInvDetailRow >= 0) {
                boolean isSourceNotEmpty = !invRequestController.StockRequest().Master().getSourceNo().isEmpty();
                tfBrand.setDisable(isSourceNotEmpty);
                tfBarCode.setDisable(isSourceNotEmpty);
                tfDescription.setDisable(isSourceNotEmpty);
                if (isSourceNotEmpty && tfBrand.getText().isEmpty()) {
                   
                    tfOrderQuantity.requestFocus();
                }else {
                        tfBrand.requestFocus();
                }if(isSourceNotEmpty && tfBrand.getText().isEmpty()){
                 tfBarCode.requestFocus();
                }
            }

        }
    }
  }
