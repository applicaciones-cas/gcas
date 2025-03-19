/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Serial;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReceiving;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author Arsiela
 */
public class DeliveryAcceptance_SerialControllerCAR implements Initializable, ScreenInterface{

    private GRiderCAS oApp;
    private JSONObject poJSON;
    int pnEntryNo = 0;
    int pnDetail = 0;
    private final String pxeModuleName = "Purchase Order Receiving Serial";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;
    private boolean pbState = true;
    
    private ObservableList<ModelDeliveryAcceptance_Serial> details_data = FXCollections.observableArrayList();

    @FXML
    private AnchorPane apBrowse, apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnOkay, btnClose;
    @FXML
    private TextField tfEngineNo, tfFrameNo, tfCSNo, tfPlateNo, tfLocation;
    @FXML
    private CheckBox cbApplyToAll;
    @FXML
    private TableView<ModelDeliveryAcceptance_Serial> tblViewSerialList;
    @FXML
    private TableColumn tblRowNoDetail, tblEngineNoDetail, tblFrameNoDetail, tblConductionStickerNoDetail, tblPlateNoDetail, tblLocationDetail;
    
    public void setObject(PurchaseOrderReceiving foObject){
        poPurchaseReceivingController = foObject;
    } 
    
    public void setEntryNo(int entryNo){
        pnEntryNo = entryNo;
    }
    
    public void setState(boolean fbValue){
        pbState = fbValue;
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
//        loadPurchaseOrderReceivingSerial();
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
                    break;
                case "btnClose":
                    CommonUtils.closeStage(btnClose);
                    break;
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                    break;
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
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfEngineNo":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial01("");
                    }
                    break;
                case "tfFrameNo":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial02("");
                    }
                    break;
                case "tfCSNo":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setConductionSticker("");
                    }
                    break;
                case "tfPlateNo":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setPlateNo("");
                    }
                    break;
                case "tfLocation":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setLocationID("");
                    }
                    break;
            }

            loadPurchaseOrderReceivingSerial();
        }

    };

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            switch (event.getCode()) {
                case F3:
                    switch (lsID) {
                        case "tfLocation":
                            /*search location*/
                            poJSON = poPurchaseReceivingController.SearchLocation(lsValue, false, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfLocation.setText("");
                                break;
                            }
                            break;
                    }
                    loadPurchaseOrderReceivingSerial();
                    break;
                default:
                    break;
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
    
    public void loadPurchaseOrderReceivingSerial(){
        try {
            if(pnDetail >= 0) {
                tfEngineNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial01());
                tfFrameNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial02());
                tfCSNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getConductionSticker());
                tfPlateNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getPlateNo());
                tfLocation.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).Location().getDescription());
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_SerialControllerCAR.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_SerialControllerCAR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadTableDetail() {
        poJSON = new JSONObject();
        int lnCtr = 0;
        int lnRow = 0;
        details_data.clear();
        
        try {
            poJSON = poPurchaseReceivingController.getPurchaseOrderReceivingSerial(pnEntryNo);
            for (lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount() - 1; lnCtr++) {
                if(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getEntryNo() == pnEntryNo){ 
                    details_data.add(
                        new ModelDeliveryAcceptance_Serial(
                                String.valueOf(lnRow + 1),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01()),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02()),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getConductionSticker()),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getPlateNo()),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).Location().getDescription()),
                                String.valueOf(lnCtr),
                                String.valueOf(""),
                                String.valueOf(""),
                                String.valueOf("") //identify total
                        ));
                    lnRow++;
                }
            }

//            if (pnDetail < 0 || pnDetail
//                    >= details_data.size()) {
//                if (!details_data.isEmpty()) {
//                    /* FOCUS ON FIRST ROW */
//                    tblViewOrderDetails.getSelectionModel().select(0);
//                    tblViewOrderDetails.getFocusModel().focus(0);
//                    pnDetail = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
//                    loadRecordDetail();
//                }
//            } else {
//                /* FOCUS ON THE ROW THAT pnDetailDetail POINTS TO */
//                tblViewOrderDetails.getSelectionModel().select(pnDetail);
//                tblViewOrderDetails.getFocusModel().focus(pnDetail);
//                loadRecordDetail();
//            }

        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initTableOnClick() {
        tblViewSerialList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                ModelDeliveryAcceptance_Serial selectedItem = tblViewSerialList.getItems().get(tblViewSerialList.getSelectionModel().getSelectedIndex());
                pnDetail = Integer.valueOf(selectedItem.getIndex07());
                
                loadTableDetail();
            }
        });
    }
    
    public void initTextFields() {
        tfEngineNo.focusedProperty().addListener(txtMaster_Focus);
        tfFrameNo.focusedProperty().addListener(txtMaster_Focus);
        tfCSNo.focusedProperty().addListener(txtMaster_Focus);
        tfPlateNo.focusedProperty().addListener(txtMaster_Focus);
        tfLocation.focusedProperty().addListener(txtMaster_Focus);
    }
    
    public void initDetailsGrid() {
        tblRowNoDetail.setStyle("-fx-alignment: CENTER;");
        tblEngineNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblFrameNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblConductionStickerNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblPlateNoDetail.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 5;");
        tblLocationDetail.setStyle("-fx-alignment: CENTER 0 5 0 5;");

        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblEngineNoDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblFrameNoDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblConductionStickerNoDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblPlateNoDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblLocationDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
        
        tblViewSerialList.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewSerialList.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });
    }
    
}
