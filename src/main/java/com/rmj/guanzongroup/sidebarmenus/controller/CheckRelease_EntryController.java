/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.model.Model_Check_Payments;
import ph.com.guanzongroup.cas.check.module.mnv.CheckRelease;
import ph.com.guanzongroup.cas.check.module.mnv.constant.CheckReleaseStatus;
import ph.com.guanzongroup.cas.check.module.mnv.models.Model_Check_Release_Detail;
import ph.com.guanzongroup.cas.check.module.mnv.services.CheckController;

/**
 *
 * @author User
 */
public class CheckRelease_EntryController implements Initializable, ScreenInterface{
    
    private GRiderCAS poApp;
    private LogWrapper poLogWrapper;
    private String psFormName = "Check Release Entry";
    private String psIndustryID;
    private Control lastFocusedControl;
    private CheckRelease poAppController;
    
    private int pnSelectMaster, pnEditMode;
    
    @FXML
    private AnchorPane apMainAnchor, apMaster, apDetail, apCheckDettail, apTransaction;
    
    @FXML
    Label lblStatus;
    
    @FXML
    private TextField tfSearchReceived, tfSearchTransNo, tfTransNo, tfReceivedBy, tfTotal, tfSearchCheckRef, tfPayee, tfParticular,
            tfCheckAmt, tfNote, tfSearchPayee, tfSearchCheck;
    
    @FXML
    private TextArea taRemarks;
    
    @FXML
    private DatePicker dpTransactionDate, dpCheckDate, dpCheckDtFrm, dpCheckDTTo;
    
    @FXML
    private Button btnSearch, btnBrowse, btnNew, btnUpdate, btnSave, btnCancel, btnHistory, btnRetrieve, btnClose;
    
    @FXML
    private TableView<Model_Check_Payments> tblViewMaster;
    
    @FXML
    private TableView<Model_Check_Release_Detail> tblViewDetails;
    
    @FXML
    private TableColumn<Model_Check_Payments, String> tblColNo, tblColTransNo, tblColTransDate, tblColCheckNo, tblColCheckAmt;
    
    @FXML
    private TableColumn<Model_Check_Release_Detail, String> tblColDetailNo, tblColDetailReference, tblColDetailPayee, tblColDetailParticular, tblColDetailCheckDt, tblColDetailCheckNo, tblColDetailAmt;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            poLogWrapper = new LogWrapper(psFormName, psFormName);
            poAppController = new CheckController(poApp, poLogWrapper).CheckRelease();
            poAppController.setTransactionStatus(CheckReleaseStatus.OPEN);

            //initlalize and validate transaction objects from class controller
            if (!isJSONSuccess(poAppController.initTransaction(), psFormName)) {
                unloadForm appUnload = new unloadForm();
                appUnload.unloadForm(apMainAnchor, poApp, psFormName);
            }

            //background thread
            Platform.runLater(() -> {
                
                poAppController.setTransactionStatus("0");
                poAppController.setIndustryID(psIndustryID);
                
                System.err.println("Initialize value : Industry >" + psIndustryID);

            });

            initControlEvents();
        } catch (SQLException | GuanzonException e) {
            Logger.getLogger(CheckDeposit_EntryController.class.getName()).log(Level.SEVERE, null, e);
            poLogWrapper.severe(psFormName + " :" + e.getMessage());
        }
    }

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
    }

    @Override
    public void setCategoryID(String fsValue) {
    }
    
    @FXML
    void ontblMasterClicked(MouseEvent e) {
        pnSelectMaster = tblViewMaster.getSelectionModel().getSelectedIndex();
        if (pnSelectMaster < 0) {
            return;
        }

        if (e.getClickCount() == 1 && !e.isConsumed()) {
            e.consume();
            if (!isJSONSuccess(poAppController.SearchCheckTransaction(tblColTransNo.getCellData(pnSelectMaster), true, true), psFormName)) {
                return;
            }

        }
        return;
    }
    
    @FXML
    void ontblDetailClicked(MouseEvent e) {
        
    }
    
    @FXML
    private void cmdButton_Click(ActionEvent event) {
        try{
            
            //get button id
            String btnID = ((Button) event.getSource()).getId();
            switch (btnID) {
                
                case "btnBrowse":
                    if (lastFocusedControl == null) {
                        ShowMessageFX.Information(null, psFormName,
                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
                        break;
                    }

                    switch (lastFocusedControl.getId()) {
                        
                        case "tfSearchTransNo":
                            if (!isJSONSuccess(poAppController.SearchTransactionMaster(tfSearchTransNo.getText().toString(), true, true), "Initialize Search Check Release Master")) {
                                break;
                            }
                            InitTransactionMaster();
                            break;
                        
                        case "tfSearchReceived":
                            if (!isJSONSuccess(poAppController.SearchTransactionMaster(tfSearchReceived.getText().toString(), true, false), "Initialize Search Check Release Master")) {
                                break;
                            }
                            InitTransactionMaster();
                            break;
                            
                        case "tfSearchPayee":
                            if (!isJSONSuccess(poAppController.SearchCheckTransaction(tfSearchPayee.getText().toString(), true, false), "Initialize Search Check Release Master")) {
                                break;
                            }
                            break;
                            
                        case "tfSearchCheck":
                            if (!isJSONSuccess(poAppController.SearchCheckTransaction(tfSearchCheck.getText().toString(), true, false), "Initialize Search Check Release Master")) {
                                break;
                            }
                            break;
                    }
                    
            }
        }catch(Exception e){
            Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, e);
            poLogWrapper.severe(psFormName + " :" + e.getMessage());
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
    }
    
    private final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        TextField loTextField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextFieldID = loTextField.getId();
        String lsValue = loTextField.getText();
        
        try {
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
        } catch (Exception ex) {
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    };
    
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
            } else if (loControl instanceof ComboBox) {
                ComboBox loControlField = (ComboBox) loControl;
                controllerFocusTracker(loControlField);
            }
        }

        clearAllInputs();
    }
    
    private void clearAllInputs() {

        List<Control> laControls = getAllSupportedControls();

        for (Control loControl : laControls) {
            if (loControl instanceof TextField) {
                ((TextField) loControl).clear();
            } else if (loControl instanceof TextArea) {
                ((TextArea) loControl).clear();
            } else if (loControl != null && loControl instanceof TableView) {
                TableView<?> table = (TableView<?>) loControl;
                if (table.getItems() != null) {
                    table.getItems().clear();
                }

            } else if (loControl instanceof DatePicker) {
                ((DatePicker) loControl).setValue(null);
            } else if (loControl instanceof ComboBox) {
                ((ComboBox) loControl).setItems(null);
            }
        }
        pnEditMode = poAppController.getEditMode();
        initButtonDisplay(poAppController.getEditMode());
        try {
            dpCheckDate.setValue(ParseDate((Date) poApp.getServerDate()));
            dpCheckDtFrm.setValue(ParseDate((Date) poApp.getServerDate()));
            dpCheckDTTo.setValue(ParseDate((Date) poApp.getServerDate()));
        } catch (SQLException ex) {
            Logger.getLogger(CheckDeposit_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initButtonDisplay(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);

        // Always show these buttons
        initButtonControls(true, "btnRetrieve", "btnHistory", "btnClose");

        // Show-only based on mode
        initButtonControls(lbShow, "btnSearch", "btnSave", "btnCancel");
        initButtonControls(!lbShow, "btnBrowse", "btnNew", "btnUpdate");

        apMaster.setDisable(!lbShow);
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

    private void controllerFocusTracker(Control control) {
        control.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                lastFocusedControl = control;
            }
        });
    }
    
    private void InitTransactionMaster(){
        
        lblStatus.setText(CheckReleaseStatus.STATUS.get(Integer.parseInt(poAppController.GetMaster().getTransactionStatus())) == null ? "STATUS"
                    : CheckReleaseStatus.STATUS.get(Integer.parseInt(poAppController.GetMaster().getTransactionStatus())));
        dpTransactionDate.setValue(ParseDate((Date) poAppController.GetMaster().getTransactionDate()));
        tfTransNo.setText(poAppController.GetMaster().getTransactionNo());
        tfReceivedBy.setText(poAppController.GetMaster().getReceivedBy());
        taRemarks.setText(poAppController.GetMaster().getRemarks());
        tfTotal.setText((String.valueOf(poAppController.GetMaster().getTransactionTotal())));
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
    
    private LocalDate ParseDate(Date date) {
        if (date == null) {
            return null;
        }
        Date loDate = new java.util.Date(date.getTime());
        return loDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
    
}
