/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Serial;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_SerialMC;
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
public class DeliveryAcceptance_SerialMCController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    int pnEntryNo = 0;
    int pnDetail = 0;
    private final String pxeModuleName = "Purchase Order Receiving Serial MC";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;
    private boolean pbState = true;

    private ObservableList<ModelDeliveryAcceptance_SerialMC> details_data = FXCollections.observableArrayList();

    @FXML
    private AnchorPane apMainAnchor;
    @FXML
    private AnchorPane apBrowse;
    @FXML
    private AnchorPane apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnOkay;
    @FXML
    private Button btnClose;
    @FXML
    private TextField tfEngineNo;
    @FXML
    private TextField tfFrameNo;
    @FXML
    private CheckBox cbApplyToAll;
    @FXML
    private TextField tfLocation;
    @FXML
    private TableView tblViewDetail;

    @FXML
    private TableColumn tblRowNoDetail;

    @FXML
    private TableColumn tblEngineNoDetail;

    @FXML
    private TableColumn tblFrameNoDetail;

    @FXML
    private TableColumn tblLocationDetail;

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
//        loadRecordDetail();
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
                case "btnClose":
                    CommonUtils.closeStage(btnClose);
                    break;
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                    break;
            }
        }
    }
    
    
    @FXML
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof CheckBox) {
            CheckBox checkbox = (CheckBox) source;
            String lsCheckBox = checkbox.getId();
            String lsLocation = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getLocationID();
            if(lsCheckBox.equals("cbApplyToAll")){
                for(int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount() - 1; lnCtr++){
                    if((lsLocation != null) || !lsLocation.equals("")){
                        poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).setLocationID(lsLocation);
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
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfEngineNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial01(lsValue);
                    break;
                case "tfFrameNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial02(lsValue);
                    break;
                case "tfCSNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setConductionStickerNo(lsValue);
                    break;
                case "tfPlateNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setPlateNo(lsValue);
                    break;
                case "tfLocation":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setLocationID("");
                    }
                    break;
            }
            loadTableDetail();
            loadRecordDetail();
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
                    loadRecordDetail();
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
    
    public void loadRecordDetail(){
        try {
            if(pnDetail >= 0) {
                tfEngineNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial01());
                tfFrameNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial02());
                tfLocation.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).Location().getDescription());
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_SerialMCController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_SerialMCController.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                        new ModelDeliveryAcceptance_SerialMC(
                                String.valueOf(lnRow + 1),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01()),
                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02()),
                                String.valueOf(lsLocation)));
                    lnRow++;
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
                ModelDeliveryAcceptance_SerialMC selectedItem =  (ModelDeliveryAcceptance_SerialMC) tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex());
                pnDetail = Integer.valueOf(selectedItem.getIndex01());
                
                loadRecordDetail();
            }
        });
        
        tblViewDetail.setOnKeyReleased((KeyEvent t) -> {
            ModelDeliveryAcceptance_SerialMC selectedItem = (ModelDeliveryAcceptance_SerialMC) tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex());
            
            KeyCode key = t.getCode();
            switch (key) {
                case DOWN:
                case TAB:
                    pnDetail = Integer.valueOf(selectedItem.getIndex01()) - 1;
                    if (pnDetail == tblViewDetail.getItems().size()) {
                        pnDetail = tblViewDetail.getItems().size();
                    } else {
                        pnDetail++;
                    }
                    System.out.println("Down " + pnDetail);
                    break;

                case UP:
                    pnDetail = Integer.valueOf(selectedItem.getIndex01()) + 1;
                    if(pnDetail == 0){
                        pnDetail = 0;
                    } else {
                        pnDetail--;
                    }
                    System.out.println("UP " + pnDetail);
                    break;
            }
            
            loadRecordDetail();
        });
    }
    
    public void initTextFields() {
        tfEngineNo.focusedProperty().addListener(txtMaster_Focus);
        tfFrameNo.focusedProperty().addListener(txtMaster_Focus);
        tfLocation.focusedProperty().addListener(txtMaster_Focus);
        
        tfLocation.setOnKeyPressed(this::txtField_KeyPressed);
        
    }
    
    public void initDetailsGrid() {
        tblRowNoDetail.setStyle("-fx-alignment: CENTER;");
        tblEngineNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblFrameNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblLocationDetail.setStyle("-fx-alignment: CENTER 0 5 0 5;");

        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblEngineNoDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblFrameNoDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblLocationDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        
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
