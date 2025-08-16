/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.controller.DeliverySchedule_EntryController;
import com.rmj.guanzongroup.sidebarmenus.controller.ScreenInterface;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.guanzon.appdriver.agent.services.Model;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Inventory_Transfer_Detail;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Inventory_Transfer_Master;

/**
 * FXML Controller class
 *
 * @author User
 */
public class InventoryStockTransferIssuanceNeoController implements Initializable, ScreenInterface {
    
    private GRiderCAS poApp;
    private LogWrapper poLogWrapper;
    private String psFormName = "Issuance without ROQ";
    private String psIndustryID, psCompanyID, psCategoryID;
    private Control lastFocusedControl;
    private Model_Inventory_Transfer_Master poAppController;
    private ObservableList<Model_Inv_Stock_Request_Detail> laTransactionDetail;
    private int pnSelectMaster, pnSelectedDetail, pnSelectedDelivery, pnEditMode, pnCTransactionDetail;
    
    private final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        TextField loTextField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextFieldID = loTextField.getId();
        String lsValue = loTextField.getText();

        if (lsValue == null) {
            return;
        }

        if (!nv) {
            /*Lost Focus*/
            switch (lsTextFieldID) {
                
                
            }
            
            reloadTableDetail();
        } else {
            loTextField.selectAll();
        }
    };
    
    @FXML
    AnchorPane apMainAnchor, apMaster, apDetail, apDelivery;

    @FXML
    TextField tfSearchSourceno, tfSeacrchTransNo, tfTransNo, tfClusterName, tfTrucking, tfDiscountRate, tfDiscountAmount, tfTotal;
    
    @FXML
    DatePicker dpTransactionDate, dpDelDate;
    
    @FXML
    ComboBox cbDelType;
    
    @FXML
    TextArea taRemarks;
    
    @FXML
    TextField tfSearchSerial, tfSearchBarcode, tfSearchDescription, tfSupersede, tfBrand, tfModel, tfColor,
            tfVariant, tfMeasure, tfInvType, tfCost, tfOrderQuantity, tfIssuedQty;
    
    @FXML
    TableView<Model_Inventory_Transfer_Master> tblViewMaster;
    
    @FXML
    TableView<Model_Inventory_Transfer_Detail> tblViewDetails;
    
    @FXML
    TableColumn<?, ?> tblColNo, tblColTransNo, tblColTransDate, tblColBranch;
    
    @FXML
    TableColumn<?, ?> tblColDetailNo, tblColDetailOrderNo, tblColDetailSerial, tblColDetailBarcode, tblColDetailDescr, tblColDetailBrand, tblColDetailVariant, tblColDetailCost, tblColDetailOrderQty;
    
    @FXML
    Label lblSource;
    
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
        
        });
        initControlEvents();
    }
    
    @FXML
    void ontblMasterClicked(MouseEvent e){
        pnSelectMaster = tblViewMaster.getSelectionModel().getSelectedIndex();
    }
    
    @FXML
    void ontblDetailClicked(MouseEvent e){
        pnSelectedDetail = tblViewDetails.getSelectionModel().getSelectedIndex();
        loadSelectedDetail();
    }
    
    @FXML
    private void cmdButton_Click(ActionEvent event) {
        try {
             //get button id
            String btnID = ((Button) event.getSource()).getId();
            switch(btnID){
                case "btnUpdate":
                    break;
                    
                case "btnSearch":
                    break;

                case "btnSave":
                    break;
                    
                case "btnCancel":
                    break;

                case "btnPrint":
                    break;

                case "btnReturn":
                    break;

                case "btnHistory":
                    break;

                case "btnRetrieve":
                    break;

                case "btnClose":
                    break;
            }
            
        } catch (Exception e) {
        }
    }
    
    private void txtField_KeyPressed(KeyEvent event) {
        TextField loTxtField = (TextField) event.getSource();
        String txtFieldID = ((TextField) event.getSource()).getId();
        String lsValue = "";
        if (loTxtField.getText() == null) {
            lsValue = "";
        } else {
            lsValue = loTxtField.getText();
        }
        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                    case F3:
                        switch (txtFieldID) {
                            case "tfSearchSourceno":
                                break;   
                            case "tfSeacrchTransNo":
                                break;
                            case "tfClusterName":
                                break;
                            case "tfTrucking":
                                break;
                            case "tfSearchSerial":
                                break;
                            case "tfSearchBarcode":
                                break;
                            case "tfSearchDescription":
                                break;
                            case "tfSupersede":
                                break;
                        }

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }
    
    private void loadSelectedTableItem(int fnRow, TableView<?> ftblSrc, HashMap<String, Object> fmapObject){
        //iterate to table columns
        for(TableColumn<?, ?> column: ftblSrc.getColumns()){
            String lscolumnID = column.getId(); //get column id
            Object loField = fmapObject.get(lscolumnID); //get assigned object and validate type of object that will display the cell value
            
            //display value to field if object is not empty
            if(loField instanceof TextField){
                if (loField != null) {
                    ((TextField) loField).setText(column.getCellData(fnRow) == null ? "" : column.getCellData(fnRow).toString());
                }
            }
            
            if(loField instanceof DatePicker){
                if (loField != null) {
                    ((DatePicker) loField).setValue(CustomCommonUtil.parseDateStringToLocalDate(
                            column.getCellData(fnRow) == null ? "" : column.getCellData(fnRow).toString(), "yyyy-MM-dd"));
                }
            }
        }
    }
    
    private void loadSelectedDetail(){
        HashMap<String, Object> loMapFields = new HashMap<>();
        loMapFields.put("tblColDetailSerial", tfSearchSerial);
        loMapFields.put("tblColDetailBarcode", tfSearchBarcode);
        loMapFields.put("tblColDetailDescr", tfSearchDescription);
        loMapFields.put("tblColDetailBrand", tfBrand);
        loMapFields.put("tblColDetailVariant", tfVariant);
        loMapFields.put("tblColDetailCost", tfCost);
        loMapFields.put("tblColDetailOrderQty", tfOrderQuantity);
        
        loadSelectedTableItem(pnSelectedDetail, tblViewDetails, loMapFields);
    }
    
    private void initControlEvents() {
        List<Control> laControls = getAllSupportedControls();

        for (Control loControl : laControls) {
            //add more if required
            if (loControl instanceof TextField) {
                TextField loControlField = (TextField) loControl;
                controllerFocusTracker(loControlField);
                loControlField.setOnKeyPressed(this::txtField_KeyPressed);
                loControlField.focusedProperty().addListener(txtField_Focus);
            } else if (loControl instanceof TableView) {
                TableView loControlField = (TableView) loControl;
                controllerFocusTracker(loControlField);
            }
        }
        clearAllInputs();
    }
    
    private void controllerFocusTracker(Control control) {
        control.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                lastFocusedControl = control;
            }
        });
    }
    
    private void clearAllInputs() {
        
        List<Control> laControls = getAllSupportedControls();

        for (Control loControl : laControls) {
            if (loControl instanceof TextField) {
                ((TextField) loControl).clear();
            } else if (loControl != null && loControl instanceof TableView) {
                TableView<?> table = (TableView<?>) loControl;
                if (table.getItems() != null) {
                    table.getItems().clear();
                }

            }
        }
        pnEditMode = poAppController.getEditMode();
        initButtonDisplay(poAppController.getEditMode());
    }
    
    private void initButtonDisplay(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);

        // Always show these buttons
        initButtonControls(true, "btnSearch", "btnRetrieve", "btnHistory", "btnClose");

        // Show-only based on mode
        initButtonControls(lbShow, "btnSave", "btnCancel", "btnReturn");
        initButtonControls(!lbShow, "btnPrint", "btnUpdate");
        apDetail.setDisable(!lbShow);
    }
    
    private void initButtonControls(boolean visible, String... buttonFxIdsToShow) {
        Set<String> showOnly = new HashSet<>(Arrays.asList(buttonFxIdsToShow));

        for (Field loField : getClass().getDeclaredFields()) {
            loField.setAccessible(true);
            String fieldName = loField.getName(); // fx:id

            // Only touch the buttons listed
            if (!showOnly.contains(fieldName)) {
                continue;
            }
            try {
                Object value = loField.get(this);
                if (value instanceof Button) {
                    Button loButton = (Button) value;
                    loButton.setVisible(visible);
                    loButton.setManaged(visible);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                poLogWrapper.severe(psFormName + " :" + e.getMessage());
            }
        }
    }
    
    private void reloadTableDetail() {
//        List<Model_> rawDetail = poAppController.getDetailList();
//        laTransactionDetail.setAll(rawDetail);

        // Restore or select last row
        int indexToSelect = (pnCTransactionDetail >= 0 && pnCTransactionDetail < laTransactionDetail.size())
                ? pnCTransactionDetail
                : laTransactionDetail.size() - 1;
  
        tblViewDetails.getSelectionModel().select(indexToSelect);
    
        pnCTransactionDetail = tblViewDetails.getSelectionModel().getSelectedIndex(); // Not focusedIndex

        tblViewDetails.refresh();
    }
    
    private List<Control> getAllSupportedControls() {
        List<Control> controls = new ArrayList<>();
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof TextField
                        || value instanceof TextArea
                        || value instanceof Button
                        || value instanceof TableView
                        || value instanceof DatePicker
                        || value instanceof ComboBox) {
                    controls.add((Control) value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                poLogWrapper.severe(psFormName + " :" + e.getMessage());
            }
        }
        return controls;
    }
}
