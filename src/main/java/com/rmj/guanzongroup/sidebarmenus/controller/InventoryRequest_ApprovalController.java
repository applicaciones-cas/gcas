/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Master;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.inv.warehouse.t4.InventoryRequestApproval;
import ph.com.guanzongroup.cas.inv.warehouse.t4.constant.DeliveryScheduleTruck;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Delivery_Schedule_Master;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.services.DeliveryScheduleControllers;
import ph.com.guanzongroup.cas.inv.warehouse.t4.parameter.model.Model_Branch_Cluster_Delivery;
import ph.com.guanzongroup.cas.inv.warehouse.t4.parameter.model.Model_Branch_Others;

/**
 *
 * @author User
 */
public class InventoryRequest_ApprovalController implements Initializable, ScreenInterface{
    
    private GRiderCAS poApp;
    private LogWrapper poLogWrapper;
    private InventoryRequestApproval poAppController;
    private String psFormName = "Inventory Request Approval";
    private String psIndustryID, psCompanyID, psCategoryID;
    private Control lastFocusedControl;
    private int pnTransaction, pnClusterDetail, pnEditMode;
    
    @FXML
    private AnchorPane apMainAnchor, apButton, apBrowse, apCenter, apDetailField, apMaster, apDetail, apDetailTable, apTransactionTable;

    @FXML
    private Label lblSource;

    @FXML
    private Button btnSearch, btnSave, btnCancel, btnRetrieve, btnClose;

    @FXML
    private TextField tfClusterName, tfBrand, tfModel, tfVariant, tfInventoryType, tfRequestQty, tfApprovedQty, tfQOH, tfColor, tfClassification, tfROQ,
            tfBarcode, tfDescription, tfBranchName, tfCancelQty;

    @FXML
    private TableView<Model_Inv_Stock_Request_Master> tblTransaction;
    
    @FXML
    private TableView<Model_Inv_Stock_Request_Detail> tblRequestDetail;

    @FXML
    private TableColumn<Model_Inv_Stock_Request_Master, String> tblColStockRequestNo, tblColBranch, tblColTransaction, tblColTransactionDate; 
    
    @FXML
    private TableColumn<Model_Inv_Stock_Request_Detail, String> tblColNo, tblColBrand, tblColBarcode, tblColDescription, tblColModel, tblColVariant, tblColColor, tblColQOH,
            tblColRequestQty, tblColCancelQty, tblColApprovedQty ;

    @FXML
    private Pagination pgPagination;

    @FXML
    void cmdButton_Click(ActionEvent event) {
        try{
            //get button id
            String btnID = ((Button) event.getSource()).getId();
            
            //trigger action event of last focused object, based on clicked button
            switch(btnID){
                case "btnSearch":
                    
                    if (lastFocusedControl == null) {
                        ShowMessageFX.Information(null, psFormName,
                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
                        return;
                    }
                    
                    if (lastFocusedControl == null) {
                        ShowMessageFX.Information(null, psFormName,
                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
                        return;
                    }
                    switch (lastFocusedControl.getId()) {
                        //Search Detail 
                        case "tfClusterName":
                            if (!isJSONSuccess(poAppController.searchClusterBranch(tfClusterName.getText(), false), "Initialize Search Cluster")) {
                                return;
                            }
                            
                            loadSelectedBranchClusterDelivery();
                            break;
                        default:
                            ShowMessageFX.Information(null, psFormName,
                                    "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");

                            break;

                    }
                    break;
                    
                case "btnSave":
                    break;
                    
                    case "btnCancel":
                    break;
                    
                    case "btnRetrieve":
                    break;
                    
                    case "btnClose":
                    break;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @FXML
    void tblTransaction_MouseClicked(MouseEvent event) {
        pnTransaction = tblTransaction.getSelectionModel().getSelectedIndex();
        if (pnTransaction < 0) {
            return;
        }
        if (event.getClickCount() == 2 && !event.isConsumed()) {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to disregard changes?") != true) {
                    return;
                }
            }
            

        }
        return;
    }

    @FXML
    void tblRequestDetail_MouseClicked(MouseEvent event) {
        
        
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
            }
        } else {
            loTextField.selectAll();
        }
    };

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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        try{
            //initialize class controller
            poLogWrapper = new LogWrapper(psFormName, psFormName);
            poAppController = new DeliveryScheduleControllers(poApp, poLogWrapper).InventoryRequestApproval();
            
            //initlalize and validate transaction objects from class controller
            if (!isJSONSuccess(poAppController.initTransaction(), psFormName)) {
                unloadForm appUnload = new unloadForm();
                appUnload.unloadForm(apMainAnchor, poApp, psFormName);
            }
            
            //background thread
            Platform.runLater(() -> {

                //initialize logged in category
                poAppController.setIndustryID(psIndustryID);
                poAppController.setCompanyID(psCompanyID);
                poAppController.setCategoryID(psCategoryID);
                System.err.println("Initialize value : Industry >" + psIndustryID
                        + "\nCompany :" + psCompanyID
                        + "\nCategory:" + psCategoryID);
            });
            
            initControlEvents();
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    //Fetching All Controller 
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
    
    private StackPane getOverlayProgress(AnchorPane foAnchorPane) {
        ProgressIndicator localIndicator = null;
        StackPane localOverlay = null;

        // Check if overlay already exists
        for (Node node : foAnchorPane.getChildren()) {
            if (node instanceof StackPane) {
                StackPane stack = (StackPane) node;
                for (Node child : stack.getChildren()) {
                    if (child instanceof ProgressIndicator) {
                        localIndicator = (ProgressIndicator) child;
                        localOverlay = stack;
                        break;
                    }
                }
            }
        }

        if (localIndicator == null) {
            localIndicator = new ProgressIndicator();
            localIndicator.setMaxSize(50, 50);
            localIndicator.setVisible(false);
            localIndicator.setStyle("-fx-progress-color: orange;");
        }

        if (localOverlay == null) {
            localOverlay = new StackPane();
            localOverlay.setPickOnBounds(false); // Let clicks through
            localOverlay.getChildren().add(localIndicator);

            AnchorPane.setTopAnchor(localOverlay, 0.0);
            AnchorPane.setBottomAnchor(localOverlay, 0.0);
            AnchorPane.setLeftAnchor(localOverlay, 0.0);
            AnchorPane.setRightAnchor(localOverlay, 0.0);

            foAnchorPane.getChildren().add(localOverlay);
        }

        return localOverlay;
    }
    
    private boolean isJSONSuccess(JSONObject loJSON, String fsModule) {
        String result = (String) loJSON.get("result");
        if ("error".equals(result)) {
            String message = (String) loJSON.get("message");
            poLogWrapper.severe(psFormName + " :" + message);
            Platform.runLater(() -> {
                ShowMessageFX.Warning(null, psFormName, fsModule + ": " + message);
            });
            return false;
        }
        String message = (String) loJSON.get("message");

        poLogWrapper.severe(psFormName + " :" + message);
        Platform.runLater(() -> {
            if (message != null) {
                ShowMessageFX.Information(null, psFormName, fsModule + ": " + message);
            }
        });
        poLogWrapper.info(psFormName + " : Success on " + fsModule);
        return true;

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
    
    private void initButtonDisplay(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);

        // Always show these buttons
        initButtonControls(true, "btnSearch", "btnRetrieve", "btnClose");

        // Show-only based on mode
        initButtonControls(lbShow, "btnSave", "btnCancel");
        initButtonControls(!lbShow, "btnPrint", "btnUpdate", "btnHistory");
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
    
    private void controllerFocusTracker(Control control) {
        control.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                lastFocusedControl = control;
            }
        });
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
                            case "tfClusterName":
                                if (!isJSONSuccess(poAppController.searchClusterBranch(lsValue, false), " Search Cluster! ")) {
                                    return;
                                }
                                poAppController.loadTransactionList();
                                return;

                            default:
                                CommonUtils.SetNextFocus((TextField) event.getSource());
                                return;
                        }
                    case UP:
                        CommonUtils.SetPreviousFocus((TextField) event.getSource());
                        return;
                    case DOWN:
                        CommonUtils.SetNextFocus(loTxtField);
                        return;

                }
            }
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }
    
    private void loadSelectedBranchClusterDelivery() throws CloneNotSupportedException {
         StackPane overlay = getOverlayProgress(apTransactionTable);
        ProgressIndicator pi = (ProgressIndicator) overlay.getChildren().get(0);
        overlay.setVisible(true);
        pi.setVisible(true);

        Task<ObservableList<Model_Inv_Stock_Request_Master>> loadTransactionTask = new Task<ObservableList<Model_Inv_Stock_Request_Master>>() {
            @Override
            protected ObservableList<Model_Inv_Stock_Request_Master> call() throws Exception {

                if (!isJSONSuccess(poAppController.loadTransactionList(),
                        "Initialize : Load of Transaction List")) {
                    return null;
                }

                List<Model_Inv_Stock_Request_Master> rawList = poAppController.getMasterList();
                return FXCollections.observableArrayList(new ArrayList<>(rawList));
            }

            @Override
            protected void succeeded() {
                
                ObservableList<Model_Inv_Stock_Request_Master> laMasterList = getValue();

                    tblTransaction.setItems(laMasterList);

                    tblColStockRequestNo.setCellValueFactory(loModel -> {
                        int index = tblTransaction.getItems().indexOf(loModel.getValue()) + 1;
                        return new SimpleStringProperty(String.valueOf(index));
                    });

                    tblColTransaction.setCellValueFactory(loModel
                            -> new SimpleStringProperty(loModel.getValue().getTransactionNo()));
                    tblColBranch.setCellValueFactory(loModel
                            -> {
                    try {
                        new SimpleStringProperty(loModel.getValue().Branch().getBranchName());
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(InventoryRequest_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
                        return new SimpleStringProperty("");
                    }
                    
                    return new SimpleStringProperty("");
                }
                    );
                    tblColTransactionDate.setCellValueFactory(loModel
                            -> new SimpleStringProperty(SQLUtil.dateFormat(loModel.getValue().getTransactionDate(), SQLUtil.FORMAT_LONG_DATE)));

                    overlay.setVisible(false);
                    pi.setVisible(false);
            }

            @Override
            protected void failed() {
                overlay.setVisible(false);
                pi.setVisible(false);
                Throwable ex = getException();
                Logger
                        .getLogger(DeliverySchedule_EntryController.class
                                .getName()).log(Level.SEVERE, null, ex);
                poLogWrapper.severe(psFormName + " : " + ex.getMessage());
            }

            @Override
            protected void cancelled() {
                overlay.setVisible(false);
                pi.setVisible(false);
            }
        };

        Thread thread = new Thread(loadTransactionTask);
        thread.setDaemon(true);
        thread.start();
    }
   }
