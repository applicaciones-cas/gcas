package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelResultSet;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSalesInquiry_Detail;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.UserRight;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;

public class RequirementSourcePerGroupController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private final String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    private int pnEditMode;
    private ObservableList<ModelResultSet> data = FXCollections.observableArrayList();
    private JSONObject poJSON;
    static SalesControllers poController;
    ObservableList<String> PurchaseType = ModelSalesInquiry_Detail.PurchaseType;
    ObservableList<String> CustomerGroup = ModelSalesInquiry_Detail.CustomerGroup;

    @FXML
    private AnchorPane AnchorMain, apMaster, apBrowse, apMainAnchor;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnNew, btnSave, btnUpdate, btnCancel, btnActivate, btnClose;
    @FXML
    private TextField tfInquiryID,  tfRequirementSource; //tfSearchInquiry,
    @FXML
    private ComboBox cmbCustomerGroup, cmbPaymentMode;
    @FXML
    private CheckBox cbRequired, cbActive;
    @FXML
    private FontAwesomeIconView faActivate;

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
    }

    @Override
    public void setCompanyID(String fsValue) {
    }

    @Override
    public void setCategoryID(String fsValue) {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        poController = new SalesControllers(oApp, null);
        poJSON = new JSONObject();
        initTextFields();
        initComboBoxes();
        clearTextFields();
        pnEditMode = poController.RequirementsSourcePerGroup().getEditMode();
        initButton(pnEditMode);
        Platform.runLater(() -> {
//            loadRecordSearch();
            btnNew.fire();
        });
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();

            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    CommonUtils.SetNextFocus(txtField);
                    event.consume();
                    break;
                case F3:
                    switch (lsID) {
//                        case "tfSearchInquiry": {
//                            poJSON = poController.RequirementsSourcePerGroup().searchRecord(lsValue, false);
//                        }
//                        if ("error".equals(poJSON.get("result"))) {
//                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
//                            tfSearchInquiry.setText("");
//                            break;
//                        }
//                        loadRecordMaster();
//                        loadRecordSearch();
//                        pnEditMode = poController.RequirementsSourcePerGroup().getEditMode();
//                        initButton(pnEditMode);
//                        return;
                        
                        case "tfRequirementSource": {
                            poJSON = poController.RequirementsSourcePerGroup().SearchRequirmentSource(lsValue, false);
                        }
                        if ("error".equals(poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfRequirementSource.setText("");
                            break;
                        }
                        loadRecordMaster();
//                        loadRecordSearch();
                        return;

                    }
                    break;
                default:
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof CheckBox) {
            CheckBox checkedBox = (CheckBox) source;
            switch (checkedBox.getId()) {
                case "cbRequired":
                    poController.RequirementsSourcePerGroup().getModel().isRequired(cbRequired.isSelected());
                    loadRecordMaster();
                    break;
            }
        }
    }

//    public void loadRecordSearch() {
//        try {
//            tfSearchInquiry.setText(poController.RequirementsSourcePerGroup().getModel().RequirementSource().getDescription());
//            JFXUtil.updateCaretPositions(apBrowse);
//        } catch (GuanzonException | SQLException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    public void loadRecordMaster() {
        try {
            
            boolean lbDisable = pnEditMode == EditMode.ADDNEW;
            JFXUtil.setDisabled(!lbDisable, cmbPaymentMode,cmbCustomerGroup, tfRequirementSource);
            
            boolean lbActive = poController.RequirementsSourcePerGroup().getModel().isActive();
            cbActive.setSelected(lbActive);
            if (lbActive) {
                btnActivate.setText("Deactivate");
                faActivate.setGlyphName("CLOSE");
            } else {
                btnActivate.setText("Activate");
                faActivate.setGlyphName("CHECK");
            }
            
            tfInquiryID.setText(poController.RequirementsSourcePerGroup().getModel().getRequirementId());
            tfRequirementSource.setText(poController.RequirementsSourcePerGroup().getModel().RequirementSource().getDescription());
            cbRequired.setSelected(poController.RequirementsSourcePerGroup().getModel().isRequired());
            if (pnEditMode != EditMode.UNKNOWN) {
                if(poController.RequirementsSourcePerGroup().getModel().getPaymentMode() != null 
                        && !"".equals(poController.RequirementsSourcePerGroup().getModel().getPaymentMode())){
                    cmbPaymentMode.getSelectionModel().select(Integer.parseInt(poController.RequirementsSourcePerGroup().getModel().getPaymentMode()));
                } else {
                    cmbPaymentMode.getSelectionModel().select(0);
                }
                if(poController.RequirementsSourcePerGroup().getModel().getCustomerGroup() != null 
                        && !"".equals(poController.RequirementsSourcePerGroup().getModel().getCustomerGroup())){
                    cmbCustomerGroup.getSelectionModel().select(Integer.parseInt(poController.RequirementsSourcePerGroup().getModel().getCustomerGroup()));
                } else {
                    cmbCustomerGroup.getSelectionModel().select(0);
                }
            } else {
                cmbPaymentMode.getSelectionModel().select(0);
                cmbCustomerGroup.getSelectionModel().select(0);
            }

        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
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
                case "tfRequirementSource":
                    if(lsValue.isEmpty()){
                        poController.RequirementsSourcePerGroup().getModel().setRequirementCode("");
                    }
                    loadRecordMaster();
                    break;
//                case "tfSearchInquiry":
//                    break;
                    
            }

        }
    };

    public void initTextFields() {
        JFXUtil.setFocusListener(txtMaster_Focus, tfRequirementSource);
        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster);
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        poJSON = new JSONObject();

        try {
            Object source = event.getSource();
            if (source instanceof Button) {
                Button clickedButton = (Button) source;
                String lsButton = clickedButton.getId();
                switch (lsButton) {
                    case "btnBrowse":
                        poController.RequirementsSourcePerGroup().setRecordStatus(null);
                        poJSON = poController.RequirementsSourcePerGroup().searchRecord("", true);
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfInquiryID.requestFocus();
                            return;
                        }
                        pnEditMode = poController.RequirementsSourcePerGroup().getEditMode();
                        break;
                    case "btnClose":
                        unloadForm appUnload = new unloadForm();
                        if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?") == true) {
                            appUnload.unloadForm(apMainAnchor, oApp, pxeModuleName);
                        } else {
                            return;
                        }
                        break;
                    case "btnNew":
                        //Clear data
                        poController.RequirementsSourcePerGroup().initialize();
                        clearTextFields();
                        poJSON = poController.RequirementsSourcePerGroup().newRecord();
                        poController.RequirementsSourcePerGroup().getModel().setPaymentMode("0");
                        poController.RequirementsSourcePerGroup().getModel().setCustomerGroup("0");
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poController.RequirementsSourcePerGroup().getEditMode();
                        break;
                    case "btnUpdate":
                        poJSON = poController.RequirementsSourcePerGroup().openRecord(poController.RequirementsSourcePerGroup().getModel().getRequirementId());
                        poJSON = poController.RequirementsSourcePerGroup().updateRecord();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poController.RequirementsSourcePerGroup().getEditMode();
                        break;
                        
                    case "btnActivate":
                        if (btnActivate.getText().equals("Activate")) {
                            if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to Activate this Parameter?") == true) {
                                poJSON = poController.RequirementsSourcePerGroup().activateRecord();
                                if ("error".equals((String) poJSON.get("result"))) {
                                    System.err.println((String) poJSON.get("message"));
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                            } else {
                                return;
                            }
                        } else {
                            if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to Deactivate this Parameter?") == true) {
                                poJSON = poController.RequirementsSourcePerGroup().deactivateRecord();
                                if ("error".equals((String) poJSON.get("result"))) {
                                    System.err.println((String) poJSON.get("message"));
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                            } else {
                                return;
                            }
                        }

                        ShowMessageFX.Information("Record updated successfully", pxeModuleName, null);
                        poController.RequirementsSourcePerGroup().initialize();
                        clearTextFields();
                        pnEditMode = EditMode.UNKNOWN;
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            //Clear data
                            poController.RequirementsSourcePerGroup().initialize();
                            clearTextFields();
                            pnEditMode = EditMode.UNKNOWN;

                            break;
                        } else {
                            return;
                        }
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poController.RequirementsSourcePerGroup().saveRecord();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                btnNew.fire();
                            }
                        } else {
                            return;
                        }
                        break;
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                        break;
                }

                loadRecordMaster();
                initButton(pnEditMode);

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }
    final EventHandler<ActionEvent> comboBoxActionListener = event -> {
        Platform.runLater(() -> {
            Object source = event.getSource();
            @SuppressWarnings("unchecked")
                    ComboBox<?> cb = (ComboBox<?>) source;
            String cbId = cb.getId();
            int selectedIndex = cb.getSelectionModel().getSelectedIndex();
            switch (cbId) {
                case "cmbPaymentMode":
                    poController.RequirementsSourcePerGroup().getModel().setPaymentMode(String.valueOf(selectedIndex));
                    break;
                case "cmbCustomerGroup":
                    poController.RequirementsSourcePerGroup().getModel().setCustomerGroup(String.valueOf(selectedIndex));
                    break;
                default:
                    System.out.println("Unrecognized ComboBox ID: " + cbId);
                    break;
            }
            if (!cbId.equals("cmbCustomerGroup")) {
                loadRecordMaster();
            }
        });
    };
    private void initComboBoxes() {
        JFXUtil.setComboBoxItems(new JFXUtil.Pairs<>(PurchaseType, cmbPaymentMode), new JFXUtil.Pairs<>(CustomerGroup, cmbCustomerGroup));
        JFXUtil.setComboBoxActionListener(comboBoxActionListener, cmbPaymentMode, cmbCustomerGroup);
        JFXUtil.initComboBoxCellDesignColor("#FF8201", cmbPaymentMode, cmbCustomerGroup);
    }

    private void clearTextFields() {
        JFXUtil.clearTextFields(apMaster, apBrowse);
        btnActivate.setText("Activate");
        cbActive.setSelected(false);
        cbRequired.setSelected(false);
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);
        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(!lbShow, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(lbShow2, btnActivate);
        JFXUtil.setButtonsVisibility(lbShow2 && poController.RequirementsSourcePerGroup().getModel().isActive(), btnUpdate);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);
        JFXUtil.setDisabled(!lbShow, apMaster);
        cbRequired.setDisable(!lbShow);
    }
}
