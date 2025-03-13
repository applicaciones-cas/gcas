package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelResultSet;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

public class BranchController implements Initializable, ScreenInterface {

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
//
//    private GRider oApp;
//    private final String pxeModuleName = "Branch";
//    private int pnEditMode;
//    private ParamControllers oParameters;
//    private boolean state = false;
//    private boolean pbLoaded = false;
//    private int pnInventory = 0;
//    private int pnRow = 0;
//    private ObservableList<ModelResultSet> data = FXCollections.observableArrayList();
//
//    @FXML
//    private AnchorPane AnchorMain, AnchorInputs;
//    @FXML
//    private HBox hbButtons;
//
//    @FXML
//    private Button btnBrowse,
//            btnNew,
//            btnSave,
//            btnUpdate,
//            btnCancel,
//            btnActivate,
//            btnClose;
//
//    @FXML
//    private FontAwesomeIconView faActivate;
//
//    @FXML
//    private TextField txtField01,
//            txtField02,
//            txtField03,
//            txtField04,
//            txtField05,
//            txtField06,
//            txtSeeks01;
//
//    @FXML
//    private CheckBox cbField01, cbField02;
//
//    @FXML
//    private TextArea txtField07;
//
//    @Override
//    public void setGRider(GRider foValue) {
//        oApp = foValue;
//    }
//
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        pnEditMode = EditMode.UNKNOWN;
//        initButton(pnEditMode);
//        initializeObject();
//        InitTextFields();
//        ClickButton();
//        initTabAnchor();
//        pbLoaded = true;
//    }
//
//    private void initializeObject() {
//        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
//        oParameters = new ParamControllers(oApp, logwrapr);
//        oParameters.Branch().setRecordStatus("0123");
//    }
//
//    private void ClickButton() {
//        btnBrowse.setOnAction(this::handleButtonAction);
//        btnNew.setOnAction(this::handleButtonAction);
//        btnSave.setOnAction(this::handleButtonAction);
//        btnUpdate.setOnAction(this::handleButtonAction);
//        btnCancel.setOnAction(this::handleButtonAction);
//        btnActivate.setOnAction(this::handleButtonAction);
//        btnClose.setOnAction(this::handleButtonAction);
//    }
//
//    private void handleButtonAction(ActionEvent event) {
//        Object source = event.getSource();
//
//        if (source instanceof Button) {
//            Button clickedButton = (Button) source;
//            unloadForm appUnload = new unloadForm();
//            switch (clickedButton.getId()) {
//                case "btnClose":
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
//                        appUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
//                    }
//                    break;
//                case "btnNew":
//                    clearAllFields();
//                    txtField02.requestFocus();
//                    JSONObject poJSON = oParameters.Branch().newRecord();
//                    pnEditMode = EditMode.READY;
//                    if ("success".equals((String) poJSON.get("result"))) {
//                        pnEditMode = EditMode.ADDNEW;
//                        initButton(pnEditMode);
//                        initTabAnchor();
//                        loadRecord();
//                    } else {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        initTabAnchor();
//                    }
//                    break;
//                case "btnBrowse":
//                    String lsValue = (txtSeeks01.getText() == null) ? "" : txtSeeks01.getText();
//                    poJSON = oParameters.Branch().searchRecord(lsValue, false);
//                    if ("error".equals((String) poJSON.get("result"))) {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        txtSeeks01.clear();
//                        break;
//                    }
//                    pnEditMode = EditMode.READY;
//                    loadRecord();
//                    initTabAnchor();
//                    break;
//                case "btnUpdate":
//                    poJSON = oParameters.Branch().updateRecord();
//                    if ("error".equals((String) poJSON.get("result"))) {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        break;
//                    }
//                    pnEditMode = oParameters.Branch().getEditMode();
//                    initButton(pnEditMode);
//                    initTabAnchor();
//                    break;
//                case "btnCancel":
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
//                        clearAllFields();
//                        initializeObject();
//                        pnEditMode = EditMode.UNKNOWN;
//                        initButton(pnEditMode);
//                        initTabAnchor();
//                    }
//                    break;
//                case "btnSave":
//                    oParameters.Branch().getModel().setModifyingId(oApp.getUserID());
//                    oParameters.Branch().getModel().setModifiedDate(oApp.getServerDate());
//                    JSONObject saveResult = oParameters.Branch().saveRecord();
//                    if ("success".equals((String) saveResult.get("result"))) {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                        pnEditMode = EditMode.UNKNOWN;
//                        initButton(pnEditMode);
//                        clearAllFields();
//                    } else {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                    }
//                    break;
//                case "btnActivate":
//                    String Status = oParameters.Branch().getModel().getRecordStatus();
//                    JSONObject poJsON;
//
//                    switch (Status) {
//                        case "0":
//                            if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to Activate this Parameter?") == true) {
//                                poJsON = oParameters.Branch().activateRecord();
//                                ShowMessageFX.Information((String) poJsON.get("message"), "Computerized Accounting System", pxeModuleName);
//                                loadRecord();
//                            }
//                            break;
//                        case "1":
//                            if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to Deactivate this Parameter?") == true) {
//                                poJsON = oParameters.Branch().deactivateRecord();
//                                ShowMessageFX.Information((String) poJsON.get("message"), "Computerized Accounting System", pxeModuleName);
//                                loadRecord();
//                            }
//                            break;
//                        default:
//
//                            break;
//
//                    }
//                    break;
//            }
//        }
//    }
//
//    private void clearAllFields() {
//        txtField01.clear();
//        txtField02.clear();
//        txtField03.clear();
//        txtField04.clear();
//        txtField05.clear();
//        txtField06.clear();
//        txtField07.clear();
//        txtSeeks01.clear();
//        cbField01.setSelected(false);
//        cbField02.setSelected(false);
//    }
//
//    private void initButton(int fnValue) {
//        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
//
//        btnCancel.setVisible(lbShow);
//        btnCancel.setManaged(lbShow);
//        btnSave.setVisible(lbShow);
//        btnSave.setManaged(lbShow);
//        btnUpdate.setVisible(!lbShow);
//        btnUpdate.setManaged(!lbShow);
//
//        btnBrowse.setVisible(!lbShow);
//        btnBrowse.setManaged(!lbShow);
//        btnNew.setVisible(!lbShow);
//        btnNew.setManaged(!lbShow);
//
//        btnClose.setVisible(true);
//        btnClose.setManaged(true);
//    }
//
//    private void InitTextFields() {
//        txtField01.focusedProperty().addListener(txtField_Focus);
//        txtField02.focusedProperty().addListener(txtField_Focus);
//        txtField03.focusedProperty().addListener(txtField_Focus);
//        txtField04.focusedProperty().addListener(txtField_Focus);
//        txtField05.focusedProperty().addListener(txtField_Focus);
//        txtField06.focusedProperty().addListener(txtField_Focus);
//
//        txtField07.focusedProperty().addListener(txtArea_Focus);
//        txtSeeks01.setOnKeyPressed(this::txtSeeks_KeyPressed);
//
//    }
//
//    private void txtSeeks_KeyPressed(KeyEvent event) {
//        TextField txtField = (TextField) event.getSource();
//        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
//        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
//        JSONObject poJson;
//        poJson = new JSONObject();
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex) {
//                    case 01:
//                        poJson = oParameters.Branch().searchRecord(lsValue, false);
//                        if ("error".equals((String) poJson.get("result"))) {
//                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                            txtSeeks01.clear();
//                            break;
//                        }
//                        txtSeeks01.setText((String) oParameters.Branch().getModel().getBranchName());
//                        pnEditMode = EditMode.READY;
//                        loadRecord();
//                        break;
//                }
//            case ENTER:
//        }
//        switch (event.getCode()) {
//            case ENTER:
//                CommonUtils.SetNextFocus(txtField);
//            case DOWN:
//                CommonUtils.SetNextFocus(txtField);
//                break;
//            case UP:
//                CommonUtils.SetPreviousFocus(txtField);
//        }
//    }
//
//    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
//        if (!pbLoaded) {
//            return;
//        }
//
//        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
//        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
//        String lsValue = txtField.getText();
//
//        if (lsValue == null) {
//            return;
//        }
//
//        if (!nv) {
//            try {
//                switch (lnIndex) {
//                    case 1:
//                        oParameters.Branch().getModel().setBranchCode(lsValue);
//                        break;
//                    case 2:
//                        oParameters.Branch().getModel().setDescription(lsValue);
//                        break;
//                    case 3:
//                        oParameters.Branch().getModel().setDescription(lsValue);
//                        break;
//                    case 4:
//                        oParameters.Branch().getModel().setDescription(lsValue);
//                        break;
//                    case 5:
//                        oParameters.Branch().getModel().setDescription(lsValue);
//                        break;
//                    case 6:
//                        oParameters.Branch().getModel().setDescription(lsValue);
//                        break;
//
//                    default:
//                        break;
//                }
//            } catch (Exception e) {
//                System.err.println("Error processing input: " + e.getMessage());
//            }
//        } else {
//            txtField.selectAll();
//        }
//    };
//
//    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
//        if (!pbLoaded) {
//            return;
//        }
//
//        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
//        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
//        String lsValue = txtField.getText();
//
//        if (lsValue == null) {
//            return;
//        }
//
//        if (!nv) {
//            try {
//                switch (lnIndex) {
//                    case 7:
//                        oParameters.Branch().getModel().setAddress(lsValue);
//                        break;
//                    default:
//                        break;
//                }
//            } catch (Exception e) {
//                System.err.println("Error processing input: " + e.getMessage());
//            }
//        } else {
//            txtField.selectAll();
//        }
//    };
//
//    private void loadRecord() {
//        boolean lbActive = oParameters.Branch().getModel().getRecordStatus() == "1";
//
//        txtField01.setText(oParameters.Branch().getModel().getBranchCode());
//        txtField02.setText(oParameters.Branch().getModel().getBranchName());
//        txtField03.setText(oParameters.Branch().getModel().getDescription());
//        txtField04.setText(oParameters.Branch().getModel().getLandLine());
//        txtField05.setText(oParameters.Branch().getModel().getCompanyId() + " Company name");
//        txtField06.setText(oParameters.Branch().getModel().TownCity().getTownName());
//        txtField07.setText(oParameters.Branch().getModel().getAddress());
//        cbField02.setSelected(oParameters.Branch().getModel().isWarehouse());
//        switch (oParameters.Branch().getModel().getRecordStatus()) {
//            case "1":
//                btnActivate.setText("Deactivate");
//                faActivate.setGlyphName("CLOSE");
//                cbField01.setSelected(true);
//                break;
//            case "0":
//                btnActivate.setText("Activate");
//                faActivate.setGlyphName("CHECK");
//                cbField01.setSelected(false);
//                break;
//        }
//    }
//
//    @FXML
//    void cbField01_Clicked(MouseEvent event) {
//        if (cbField01.isSelected()) {
//            oParameters.Branch().getModel().setRecordStatus("1");
//        } else {
//            oParameters.Branch().getModel().setRecordStatus("0");
//        }
//    }
//
//    @FXML
//    void cbField02_Clicked(MouseEvent event) {
//        if (cbField02.isSelected()) {
//            oParameters.Branch().getModel().isWarehouse(true);
//        } else {
//            oParameters.Branch().getModel().isWarehouse(false);
//        }
//    }
//
//    private void initTabAnchor() {
//        if (AnchorInputs == null) {
//            System.err.println("Error: AnchorInput is not initialized.");
//            return;
//        }
//
//        boolean isEditable = (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
//        AnchorInputs.setDisable(!isEditable);
//    }

}
