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
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import org.guanzon.cas.inv.warehouse.status.StockRequestStatus;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class InvRequest_HistoryMcController implements Initializable, ScreenInterface{
    @FXML
    private String psFormName = "Inv Stock Request History Mc";
    
    @FXML
    private AnchorPane AnchorMain;
    private GRiderCAS poApp;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    private InvWarehouseControllers invRequestController;
    private LogWrapper logWrapper;
    private JSONObject poJSON;
    private int pnTblInvDetailRow = -1;
    private  String brandID,categID; 
    private ObservableList<ModelInvOrderDetail> invOrderDetail_data = FXCollections.observableArrayList();
    private int pnEditMode;
    
    @FXML
    private TextField tfReservationQTY,tfOrderQuantity,tfTransactionNo, tfSupplier, tfDestination, tfReferenceNo,
            tfTerm, tfDiscountRate, tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount, tfNetAmount, tfSearchTransNo;
    @FXML
    private TableColumn<ModelInvOrderDetail, String> tblBrandDetail, tblModelDetail,tblVariantDetail,tblColorDetail,tblInvTypeDetail,tblROQDetail,tblClassificationDetail,tblQOHDetail,tblReservationQtyDetail,tblOrderQuantityDetail;
    @FXML
    private TextField tfBrand,tfModel,tfInvType,
                tfVariant,tfColor,tfROQ,tfClassification,tfQOH;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel,
            btnPrint, btnRetrieve, btnTransHistory, btnClose;
    @FXML
    private Label lblTransactionStatus,lblSource;
    @FXML
    private TableView<ModelInvOrderDetail>tblViewOrderDetails;
    @FXML
    private DatePicker dpTransactionDate;
    @FXML
    private TextArea taRemarks;
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
        JSONObject loJSON = new JSONObject();
        
        try {
            invRequestController = new InvWarehouseControllers(poApp, logWrapper);
            invRequestController.StockRequest().setTransactionStatus(
                    StockRequestStatus.OPEN
                  + StockRequestStatus.CANCELLED
                  + StockRequestStatus.CONFIRMED
                  + StockRequestStatus.PROCESSED
                  + StockRequestStatus.VOID
            );
            loJSON = invRequestController.StockRequest().InitTransaction();
            
            
            Platform.runLater((() -> {
                invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                //invRequestController.StockRequest().Detail().setCategoryCode(psCategoryID);
                loadRecordSearch();
                pnEditMode = EditMode.UNKNOWN;
                System.out.print("initReached...");
                Platform.runLater(() -> btnRetrieve.fire());
                initButtons(pnEditMode);
                initFields(EditMode.UNKNOWN);
                initButtonsClickActions();
                initDatePickerActions();  
                initTableInvDetail();
                initTextFieldKeyPressed();
                System.out.print("initReached...2");
            }));
        } catch (ExceptionInInitializerError ex) {
            Logger.getLogger(InvRequest_HistoryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    private void loadRecordSearch() {
            try {
              
                lblSource.setText(invRequestController.StockRequest().Master().Company().getCompanyName() + " - " + invRequestController.StockRequest().Master().Industry().getDescription());

            } catch (GuanzonException | SQLException ex) {
                Logger.getLogger(InvStockRequest_EntryMcController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    private void txtField_KeyPressed(KeyEvent event){
        TextField sourceField = (TextField) event.getSource();
        String fieldId = sourceField.getId();
        String value = sourceField.getText() == null ? "" : sourceField.getText();
        JSONObject loJSON = new JSONObject();
        try {
            if (event.getCode() == null) return;
            String lsValue = sourceField.getText().trim();
            switch(event.getCode()){
                case TAB:
                case ENTER:
                case F3:
                    switch(fieldId){
                        case "tfSearchTransNo":
                                System.out.print("Enter pressed");
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
            }
        } catch (Exception e) {
        }
    }
    private void handleButtonAction(ActionEvent event){
        System.out.print("handle trigger reached");
        try {
            JSONObject loJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch(lsButton){
                case "btnRetrieve":
                    System.out.print("loaded table this is btnRetrieve");
                    if(tfTransactionNo.getText().isEmpty() && tfReferenceNo.getText().isEmpty()){
                        System.out.print("Empty fields...transaction will load now");
                    }
                    break;
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
            }
            initButtons(pnEditMode);
            initFields(EditMode.UNKNOWN);
        } catch (Exception ex) {
            Logger.getLogger(InvRequest_HistoryMcController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void initTextFieldKeyPressed() {
            List<TextField> loTxtField = Arrays.asList(
                    tfOrderQuantity,tfSearchTransNo
                    );

            loTxtField.forEach(tf -> tf.setOnKeyPressed(event -> txtField_KeyPressed(event)));
        }  
    private void loadTableMain(){
        System.out.print("loaded table");
    }
    private void initButtons(int fnEditMode){
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        CustomCommonUtil.setVisible(true, btnRetrieve, btnBrowse, btnClose);
        CustomCommonUtil.setManaged(true, btnRetrieve, btnBrowse, btnClose);
        
        

    }
    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnBrowse,
                btnRetrieve,btnClose);

        buttons.forEach(button -> button.setOnAction(this::handleButtonAction));
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
    private void initDatePickerActions() {
            dpTransactionDate.setOnAction(e -> {
                if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                    if (dpTransactionDate.getValue() != null) {
                        invRequestController.StockRequest().Master().setTransactionDate(SQLUtil.toDate(dpTransactionDate.getValue().toString(), SQLUtil.FORMAT_SHORT_DATE));
                    }
                }
            });
          }
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
    private void reselectLastRow() {
            if (pnTblInvDetailRow >= 0 && pnTblInvDetailRow < tblViewOrderDetails.getItems().size()) {
                tblViewOrderDetails.getSelectionModel().clearAndSelect(pnTblInvDetailRow);
                tblViewOrderDetails.getSelectionModel().focus(pnTblInvDetailRow); // Scroll to the selected row if needed
            }
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
}
