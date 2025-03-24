/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import static com.rmj.guanzongroup.sidebarmenus.controller.DeliveryAcceptance_SerialMCController.poPurchaseReceivingController;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_SerialMP;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_SerialMP;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReceiving;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_SerialMPController implements Initializable, ScreenInterface  {
    private GRiderCAS oApp;
    private JSONObject poJSON;
    int pnEntryNo = 0;
    int pnDetail = 0;
    private final String pxeModuleName = "Purchase Order Receiving Serial MP";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;

    private ObservableList<ModelDeliveryAcceptance_SerialMP> details_data = FXCollections.observableArrayList();
    
    @FXML
    private AnchorPane apBrowse, apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnOkay, btnClose;
    @FXML
    private TextField tfIMEI1, tfIMEI2; //, tfLocation;
    @FXML
    private CheckBox cbApplyToAll;
    @FXML
    private TableView<ModelDeliveryAcceptance_SerialMP> tblViewDetail;
    @FXML
    private TableColumn tblRowNoDetail, tblIMEI1Detail, tblIMEI2Detail;

    public void setObject(PurchaseOrderReceiving foObject){
        poPurchaseReceivingController = foObject;
    } 
    
    public void setEntryNo(int entryNo){
        pnEntryNo = entryNo;
    }
    
    private Stage getStage(){
         return (Stage) btnClose.getScene().getWindow();
    }


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        initTextFields();
        initDetailsGrid();
        initTableOnClick();

        loadTableDetail();
    }   
    
    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }   

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            String lsButton = clickedButton.getId();
            switch (lsButton) {
                case "btnOkay":
                    //if the user clicked okay all rows must be fill up else remaining row will be allowed to remain empty.
                    //check for empty serial 1 || serial 2 is empty delete the excess row
                    poJSON = checkSerialNo(lsButton);
                    if("error".equals((String) poJSON.get("result"))){
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                
                    CommonUtils.closeStage(btnClose);
                case "btnClose":
                    //if the user clicked okay all rows must be fill up else remaining row will be allowed to remain empty.
                    //check for empty serial 1 || serial 2 is empty delete the excess row
                    poJSON = checkSerialNo(lsButton);
                    if("error".equals((String) poJSON.get("result"))){
                        return;
                    }
                
                    CommonUtils.closeStage(btnClose);
                    break;
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                    break;
            }
        }
    }
    
    private JSONObject checkSerialNo(String lsButton){
        poJSON = new JSONObject();
        int lnRow = 1;
        String lsMessage = "";
        for(int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount()-1; lnCtr++){
            if(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getEntryNo() == pnEntryNo){
                if(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01() == null || poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01().equals("")){
                    poJSON.put("result", "error");
                    lsMessage = "IMEI 1 at row "+lnRow+" cannot be empty.";
                    
                } 
                if(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02() == null || poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02().equals("")){
                    poJSON.put("result", "error");
                    lsMessage = "IMEI 2 at row "+lnRow+" cannot be empty.";
                }
                
                if(lsButton.equals("btnOkay")){
                    if("error".equals((String) poJSON.get("result"))){
                        poJSON.put("message", lsMessage);
                        return poJSON;
                    }
                } else {
                    if(ShowMessageFX.OkayCancel(null, pxeModuleName, 
                            "There are still remaining rows that have not been filled. Are you sure you want to close without completing them?") == false){
                        poJSON.put("result", "error");
                        return poJSON;
                    } else {
                        poJSON.put("result", "success");
                        return poJSON; 
                    }
                }
                
                lnRow++;
            }
        }
        return poJSON;
    }
    
    @FXML
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof CheckBox) {
            CheckBox checkbox = (CheckBox) source;
            String lsCheckBox = checkbox.getId();
            String lsLocation = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getLocationId();
            
            if(lsLocation == null || lsLocation.equals("")){
                ShowMessageFX.Warning(null, pxeModuleName, "Location cannot be empty.");
                return;
            }
            
            if(lsCheckBox.equals("cbApplyToAll")){
                for(int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount() - 1; lnCtr++){
                    if((lsLocation != null) || !lsLocation.equals("")){
                        poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).setLocationId(lsLocation);
                    }
                }
                loadTableDetail();
            }
        }
    }
    
    final ChangeListener<? super Boolean> txtMaster_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());
        if (lsValue == null) {
            return;
        }
        
        if(pnDetail < 0){
            return;
        }
        
        if(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getEntryNo() != pnEntryNo){
            return;
        }
        
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfIMEI1":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial01(lsValue);
                    break;
                case "tfIMEI2":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial02(lsValue);
                    break;
//                case "tfLocation":
//                    if (lsValue.isEmpty()) {
//                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setLocationId("");
//                    }
//                    break;
            }
            loadTableDetail();
            loadRecordDetail();
        }

    };
    
//    private void txtField_KeyPressed(KeyEvent event) {
//        try {
//            TextField txtField = (TextField) event.getSource();
//            String lsID = (((TextField) event.getSource()).getId());
//            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
//            poJSON = new JSONObject();
//            switch (event.getCode()) {
//                case F3:
//                    switch (lsID) {
//                        case "tfLocation":
//                            /*search location*/
//                            poJSON = poPurchaseReceivingController.SearchLocation(lsValue, false, pnDetail);
//                            if ("error".equals(poJSON.get("result"))) {
//                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
//                                tfLocation.setText("");
//                                break;
//                            }
//                            break;
//                    }
//                    loadRecordDetail();
//                    break;
//                default:
//                    break;
//            }
//
//            switch (event.getCode()) {
//                case ENTER:
//                    CommonUtils.SetNextFocus(txtField);
//                case DOWN:
//                    CommonUtils.SetNextFocus(txtField);
//                    break;
//                case UP:
//                    CommonUtils.SetPreviousFocus(txtField);
//            }
//        } catch (GuanzonException ex) {
//            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SQLException ex) {
//            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    
    public void loadRecordDetail(){
//        try {
            if(pnDetail >= 0) {
                tfIMEI1.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial01());
                tfIMEI2.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial02());
//                tfLocation.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).Location().getDescription());
            }
//        } catch (SQLException ex) {
//            Logger.getLogger(DeliveryAcceptance_SerialMCController.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (GuanzonException ex) {
//            Logger.getLogger(DeliveryAcceptance_SerialMCController.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
    public void loadTableDetail() {
        poJSON = new JSONObject();
        int lnCtr = 0;
        int lnRow = 0;
        String lsLocation = "";
        details_data.clear();
        
        try {
            poJSON = poPurchaseReceivingController.getPurchaseOrderReceivingSerial(pnEntryNo);
            for (lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount() - 1; lnCtr++) {
                if(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getEntryNo() == pnEntryNo){ 
                    if(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).Location().getDescription() != null){
                        lsLocation = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).Location().getDescription();
                    }
                    
                    details_data.add(
                        new ModelDeliveryAcceptance_SerialMP(
                                String.valueOf(lnRow + 1),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01()),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02()),
                                String.valueOf(lnCtr)
                             ));
                    lnRow++;
                }
            }
            
            if (pnDetail < 0 ) {
                if (!details_data.isEmpty()) {
                    /* FOCUS ON FIRST ROW */
                    tblViewDetail.getSelectionModel().select(0);
                    tblViewDetail.getFocusModel().focus(0);
                    ModelDeliveryAcceptance_SerialMP selectedItem = tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex());
                    pnDetail = Integer.valueOf(selectedItem.getIndex04());
                }
            } else {
                TableView<ModelDeliveryAcceptance_SerialMP> tableView = tblViewDetail;
                SelectionModel<ModelDeliveryAcceptance_SerialMP> selectionModel = tableView.getSelectionModel();
                for (ModelDeliveryAcceptance_SerialMP item : tblViewDetail.getItems()) {
                    // Check if the item matches the value of pnDetail
                    if (item.getIndex04() != null && Integer.valueOf(item.getIndex04()) == pnDetail) {
                        selectionModel.select(item);
                        tableView.scrollTo(item);
                        break; 
                    }
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initTableOnClick() {
        tblViewDetail.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                ModelDeliveryAcceptance_SerialMP selectedItem =  (ModelDeliveryAcceptance_SerialMP) tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex());
                pnDetail = Integer.valueOf(selectedItem.getIndex04());
                
                loadRecordDetail();
            }
        });
        
        tblViewDetail.setOnKeyReleased((KeyEvent t) -> {
            ModelDeliveryAcceptance_SerialMP selectedItem ;
            
            KeyCode key = t.getCode();
            switch (key) {
                case DOWN:
                    if (pnDetail == tblViewDetail.getItems().size()) {
                        pnDetail = tblViewDetail.getItems().size();
                    } else {
                        selectedItem = tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex()-1);
                        pnDetail = Integer.valueOf(selectedItem.getIndex04());
                        pnDetail++;
                    }
                    System.out.println("Down " + pnDetail);
                    break;

                case UP:
                    if(pnDetail == 0){
                        pnDetail = 0;
                    } else {
                        selectedItem = tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex()+1);
                        pnDetail = Integer.valueOf(selectedItem.getIndex04());
                        pnDetail--;
                    }
                    System.out.println("UP " + pnDetail);
                    break;
            }
            
            loadRecordDetail();
        });
    }
    
    public void initTextFields() {
        tfIMEI1.focusedProperty().addListener(txtMaster_Focus);
        tfIMEI2.focusedProperty().addListener(txtMaster_Focus);
        
//        tfLocation.focusedProperty().addListener(txtMaster_Focus);
//        tfLocation.setOnKeyPressed(this::txtField_KeyPressed);
        
    }
    
    public void initDetailsGrid() {
        tblRowNoDetail.setStyle("-fx-alignment: CENTER;");
        tblIMEI1Detail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblIMEI2Detail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");

        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblIMEI1Detail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblIMEI2Detail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        
        tblViewDetail.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewDetail.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
        
        tblViewDetail.setItems(details_data);
        tblViewDetail.autosize();
    }
    
}
