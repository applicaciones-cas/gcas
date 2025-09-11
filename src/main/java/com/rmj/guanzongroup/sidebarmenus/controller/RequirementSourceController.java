package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelResultSet;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;

public class RequirementSourceController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private final String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    private int pnEditMode;
    private ObservableList<ModelResultSet> data = FXCollections.observableArrayList();
    private JSONObject poJSON;
    static SalesControllers poController;

    @FXML
    private AnchorPane AnchorMain, apMaster, apBrowse, apMainAnchor;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse, btnNew, btnSave, btnUpdate, btnCancel, btnActivate, btnClose;
    @FXML
    private TextField tfInquiryID, tfDescription; //, tfSearchInquiry;
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
        clearTextFields();
        pnEditMode = poController.RequirementsSource().getEditMode();
        initButton(pnEditMode);
        Platform.runLater(() -> {
//            loadRecordSearch();
            btnNew.fire();
        });
    }

    private void txtField_KeyPressed(KeyEvent event) {
//        try {
//            TextField txtField = (TextField) event.getSource();
//            String lsID = (((TextField) event.getSource()).getId());
//            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
//            poJSON = new JSONObject();
//
//            switch (event.getCode()) {
//                case TAB:
//                case ENTER:
//                    CommonUtils.SetNextFocus(txtField);
//                    event.consume();
//                    break;
//                case F3:
//                    switch (lsID) {
////                        case "tfSearchInquiry": {
////                            poJSON = poController.RequirementsSource().searchRecord(lsValue, false);
////                        }
////                        if ("error".equals(poJSON.get("result"))) {
////                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
////                            tfSearchInquiry.setText("");
////                            break;
////                        }
////                        loadRecordMaster();
////                        loadRecordSearch();
////                        return;
//
//                    }
//                    break;
//                default:
//                    break;
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//        } catch (GuanzonException ex) {
//            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
//        }
    }

//    public void loadRecordSearch() {
//        tfSearchInquiry.setText(poController.RequirementsSource().getModel().getDescription());
//        JFXUtil.updateCaretPositions(apBrowse);
//    }

    public void loadRecordMaster() {

        boolean lbActive = poController.RequirementsSource().getModel().isActive();
        cbActive.setSelected(lbActive);
        if (lbActive) {
            btnActivate.setText("Deactivate");
            faActivate.setGlyphName("CLOSE");
        } else {
            btnActivate.setText("Activate");
            faActivate.setGlyphName("CHECK");
        }
        
        tfInquiryID.setText(poController.RequirementsSource().getModel().getRequirementCode());
        tfDescription.setText(poController.RequirementsSource().getModel().getDescription());
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
                case "tfDescription":
                    poController.RequirementsSource().getModel().setDescription(lsValue);
                    loadRecordMaster();
                    break;
//                case "tfSearchInquiry":
//                    break;
                    
            }

        }
    };

    public void initTextFields() {
        JFXUtil.setFocusListener(txtMaster_Focus, tfDescription); //, tfSearchInquiry);
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
                        poJSON = poController.RequirementsSource().searchRecord("", true);
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfInquiryID.requestFocus();
                            return;
                        }
                        pnEditMode = poController.RequirementsSource().getEditMode();
                        break;
                        
                    case "btnActivate":
                        if (btnActivate.getText().equals("Activate")) {
                            if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to Activate this Parameter?") == true) {
                                poJSON = poController.RequirementsSource().activateRecord();
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
                                poJSON = poController.RequirementsSource().deactivateRecord();
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
                        poController.RequirementsSource().initialize();
                        clearTextFields();
                        pnEditMode = EditMode.UNKNOWN;
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
                        poController.RequirementsSource().initialize();
                        clearTextFields();
                        poJSON = poController.RequirementsSource().newRecord();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poController.RequirementsSource().getEditMode();
                        break;
                    case "btnUpdate":
                        poJSON = poController.RequirementsSource().openRecord(poController.RequirementsSource().getModel().getRequirementCode());
                        poJSON = poController.RequirementsSource().updateRecord();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poController.RequirementsSource().getEditMode();
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            //Clear data
                            poController.RequirementsSource().initialize();
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
                            poJSON = poController.RequirementsSource().saveRecord();
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
    @FXML
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof CheckBox) {
            CheckBox checkedBox = (CheckBox) source;
            switch (checkedBox.getId()) {
//                case "cbRequired":
//                    poController.RequirementsSourcePerGroup().getModel().isRequired(cbRequired.isSelected());
//                    loadRecordMaster();
//                    break;
            }
        }
    }
    private void clearTextFields() {
        JFXUtil.clearTextFields(apMaster, apBrowse);
        btnActivate.setText("Activate");
        cbActive.setSelected(false);
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);
        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(!lbShow, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(lbShow2, btnUpdate, btnActivate);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);
        JFXUtil.setDisabled(!lbShow, apMaster);
    }
}
