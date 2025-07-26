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
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
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
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.inv.warehouse.t4.DeliverySchedule;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.services.DeliveryScheduleControllers;
import ph.com.guanzongroup.cas.inv.warehouse.t4.status.DeliveryScheduleStatus;

/**
 *
 * @author 12mnv
 */
public class DeliverySchedule_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    private JSONObject poJSON;
    private LogWrapper poLogWrapper;
    private int pnEditMode;
    private DeliverySchedule poAppController;
    private String psFormName = "Delivery Schedule Entry";

    private int pnClusterDetail = -1;
    private int pnTransaction = -1;
    private int pnBranchList = -1;
    private int pnTblMain_Page = 50;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton,
            apCenter, apDetailField, apMaster,
            apDetail, apDetailTable;
    @FXML
    private TextField tfSearchCluster, tfTransactionNo, tfTruckSize, tfClusterName;
    @FXML
    private DatePicker dpSearchDate, dpSearchScheduleDate, dpTransactionDate,
            dpScheduleDate;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private Button btnNew, btnUpdate, btnSearch, btnSave,
            btnCancel, btnHistory, btnRetrieve,
            btnClose;
    @FXML
    private TextArea taRemarks, taNotes;
    @FXML
    private TableView tblClusterDetail, tblTransaction, tblBranchList;
    @FXML
    private TableColumn tblColDetailNo, tblColDetailName, tblColDetailDeliveryDate, tblColDetailTruckSize;
    @FXML
    private TableColumn tblColDeliveryNo, tblColDeliveryTransaction, tblColDeliveryDate, tblColDeliveryScheduledDate;
    @FXML
    private TableColumn tblColBranchNo, tblColBranchName, tblColBranchAddress;
    @FXML
    private Pagination pgTransaction, pgBranchList;

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
    public void initialize(URL url, ResourceBundle rb) {

        try {
            poAppController = new DeliveryScheduleControllers(poApp, poLogWrapper).DeliverySchedule();
            poAppController.setTransactionStatus(DeliveryScheduleStatus.OPEN);
            poJSON = poAppController.initTransaction();
            if (!"success".equals(poJSON.get("result"))) {
                poLogWrapper.severe("Init failed: " + poJSON.get("message"));
            }

            Platform.runLater(() -> {
                poAppController.getMaster().setIndustryId(psIndustryID);
                poAppController.setIndustryID(psIndustryID);
                poAppController.setCompanyID(psCompanyID);
                poAppController.setCategoryID(psCategoryID);
//            poAppController.initFields();
            });

            initControlEvents();
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(ex.getMessage());
        }
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        try {
            switch (lsButton) {
                case "btnUpdate":
                    poJSON = poAppController.UpdateTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        poLogWrapper.severe((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, psFormName, (String) poJSON.get("message"));
                        return;
                    }
                    pnEditMode = poAppController.getEditMode();
                    break;
                case "btnSearch":
                case "btnNew":
                    poJSON = poAppController.newTransaction();
                    if ("error".equals((String) poJSON.get("result"))) {
                        poLogWrapper.severe((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, psFormName, (String) poJSON.get("message"));
                        return;
                    }
                    return;
                case "btnSave":
                case "btnCancel":
                    if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to disregard changes?") == true) {
                        poAppController = new DeliveryScheduleControllers(poApp, poLogWrapper).DeliverySchedule();
                        poAppController.setTransactionStatus(DeliveryScheduleStatus.OPEN);
                        poJSON = poAppController.initTransaction();
                        if (!"success".equals(poJSON.get("result"))) {
                            poLogWrapper.severe("Init failed: " + poJSON.get("message"));
                        }

                        Platform.runLater(() -> {
                            poAppController.getMaster().setIndustryId(psIndustryID);
                            poAppController.setIndustryID(psIndustryID);
                            poAppController.setCompanyID(psCompanyID);
                            poAppController.setCategoryID(psCategoryID);
                        });
                        pnEditMode = poAppController.getEditMode();
                        break;
                    } else {
                        return;
                    }
                case "btnHistory":
                    ShowMessageFX.Information(null, psFormName,
                            "This feature is under development and will be available soon.\nThank you for your patience!");
                    return;
                case "btnRetrieve":
                case "btnClose":
                    unloadForm appUnload = new unloadForm();
                    if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?") == true) {
                        appUnload.unloadForm(apMainAnchor, poApp, psFormName);
                    } else {
                        return;
                    }
                    break;

                default:
                    ShowMessageFX.Information(null, psFormName,
                            "This feature is under development and will be available soon.\nThank you for your patience!");
            }
            initButtonDisplay(poAppController.getEditMode());
        } catch (GuanzonException | SQLException | CloneNotSupportedException ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(ex.getMessage());

        }
    }

    @FXML
    private void tblTransaction_MouseClicked(MouseEvent event) {
        pnTransaction = tblTransaction.getSelectionModel().getSelectedIndex();
        if (pnTransaction < 0) {
            return;
        }
    }

    @FXML
    private void tblClusterDetail_MouseClicked(MouseEvent event) {
        pnClusterDetail = tblClusterDetail.getSelectionModel().getSelectedIndex();
        if (pnClusterDetail < 0) {
            return;
        }
    }

    @FXML
    private void tblBranchList_MouseClicked(MouseEvent event) {
        pnBranchList = tblBranchList.getSelectionModel().getSelectedIndex();
        if (pnBranchList < 0) {
            return;
        }
    }

    //Fetching All Controller 
    private List<Control> getAllSupportedControls() {
        List<Control> controls = new ArrayList<>();
        for (Field field : getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(this);
                if (value instanceof TextField || value instanceof TextArea || value instanceof Button) {
                    controls.add((Control) value);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return controls;
    }

    private void initControlEvents() {
        List<Control> laControls = getAllSupportedControls();

        for (Control loControl : laControls) {
            //add more if required
            if (loControl instanceof TextField) {
                TextField loControlField = (TextField) loControl;

                loControlField.setOnKeyPressed(this::txtField_KeyPressed);
                loControlField.focusedProperty().addListener(txtField_Focus);

            } else if (loControl instanceof TextArea) {
                TextArea loControlField = (TextArea) loControl;
                loControlField.setOnKeyPressed(this::txtArea_KeyPressed);
                loControlField.focusedProperty().addListener(txtArea_Focus);
            } else if (loControl instanceof DatePicker) {
                DatePicker loControlField = (DatePicker) loControl;
                loControlField.focusedProperty().addListener(dPicker_Focus);
                loControlField.setOnKeyPressed(this::dPicker_KeyPressed);
            }
        }
        clearAllInputs();
    }

    private void clearAllInputs() {
        List<Control> laControls = getAllSupportedControls();

        for (Control loControl : laControls) {
            if (laControls instanceof TextField) {
                ((TextField) laControls).clear();
            } else if (laControls instanceof TextArea) {
                ((TextArea) laControls).clear();
            } else if (laControls instanceof TableView) {
                ((TableView) laControls).getItems().clear();
            } else if (laControls instanceof DatePicker) {
                ((DatePicker) laControls).setValue(null);
            }
        }
        pnEditMode = poAppController.getEditMode();
        initButtonDisplay(poAppController.getEditMode());

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
            }
        }
    }

    private void initButtonDisplay(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);

        // Always show these buttons
        initButtonControls(true, "btnSearch", "btnClose");

        // Show-only based on mode
        initButtonControls(lbShow, "btnRetrieve", "btnSave", "btnCancel");
        initButtonControls(!lbShow, "btnNew", "btnUpdate", "btnHistory");
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

    private void txtField_KeyPressed(KeyEvent event) {
        TextField loTxtField = (TextField) event.getSource();
        String txtFieldID = ((TextField) event.getSource()).getId();
        String lsValue = "";
        if (loTxtField.getText() == null) {
            lsValue = "";
        } else {
            lsValue = loTxtField.getText();
        }
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                case F3:
                    switch (txtFieldID) {

                        //Search Pane
                        case "tfSearchCluster":
                            poJSON = poAppController.searchTransaction(lsValue, true, false);
                            if (!"success".equals(poJSON.get("result"))) {
                                System.err.println("Unable to  Search Transaction! " + poJSON.get("message"));
                            }
                            return;

                        //Detail Pane
                        case "tfClusterName":
//                            poJSON = poAppController.searchDetail(pnClusterDetail, 1, lsValue, true, false);
//                            if (!"success".equals(poJSON.get("result"))) {
//                                System.err.println("Unable to Search Cluster! " + poJSON.get("message"));
//                            }
//                            return;
                        case "tfTruckSize":
//                            poJSON = poAppController.searchDetail(pnClusterDetail, 2, lsValue, true, false);
//                            if (!"success".equals(poJSON.get("result"))) {
//                                System.err.println("Unable to Search Truck Size! " + poJSON.get("message"));
//                            }
//                            return;

                            return;
                        default:
                            CommonUtils.SetPreviousFocus((TextField) event.getSource());
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
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea loTextArea = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextAreaID = loTextArea.getId();
        String lsValue = loTextArea.getText();
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTextAreaID) {
                case "taRemarks":
                    poAppController.getMaster().setRemarks(lsValue);
                    break;

                case "taNotes":
                    poAppController.getDetail(pnClusterDetail).setRemarks(lsValue);
                    break;
            }
        } else {
            loTextArea.selectAll();
        }
    };

    private void txtArea_KeyPressed(KeyEvent event) {
        TextArea loTxtArea = (TextArea) event.getSource();
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                case F3:
                case UP:
                    CommonUtils.SetPreviousFocus((TextArea) event.getSource());
                    return;
                case DOWN:
                    CommonUtils.SetNextFocus(loTxtArea);
                    return;

            }
        }
    }
    final ChangeListener<? super Boolean> dPicker_Focus = (o, ov, nv) -> {
        DatePicker loDatePicker = (DatePicker) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsDatePickerID = loDatePicker.getId();
        LocalDate loValue = loDatePicker.getValue();

        if (loValue == null) {
            return;
        }
        Date ldDateValue = Date.from(loValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (!nv) {
            /*Lost Focus*/
            switch (lsDatePickerID) {
                case "dpTransactionDate":
                    poAppController.getMaster().setTransactionDate((ldDateValue));
                    return;
                case "dpScheduleDate":
                    poAppController.getMaster().setScheduleDate((ldDateValue));
                    return;

            }
        } else {
            loDatePicker.setValue(loValue);
        }
    };

    private void dPicker_KeyPressed(KeyEvent event) {
        DatePicker loDatePicker = (DatePicker) event.getSource();
        String lsDatePickerID = loDatePicker.getId();
        LocalDate loValue = loDatePicker.getValue();

        if (loValue == null) {
            lsDatePickerID = null;
        }
        Date ldDateValue = Date.from(loValue.atStartOfDay(ZoneId.systemDefault()).toInstant());
        if (null != event.getCode()) {
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                case F3:
                    switch (lsDatePickerID) {
                        //retrieve only
                        case "dpSearchDate":
                            poAppController.searchTransaction(SQLUtil.dateFormat(ldDateValue, SQLUtil.FORMAT_SHORT_DATE), true, true);
                            if (!"success".equals(poJSON.get("result"))) {
                                System.err.println("Unable to  Search Transaction! " + poJSON.get("message"));
                            }
                            return;
                        case "dpSearchScheduleDate":
                            poAppController.searchTransaction(SQLUtil.dateFormat(ldDateValue, SQLUtil.FORMAT_SHORT_DATE), false, true);
                            if (!"success".equals(poJSON.get("result"))) {
                                System.err.println("Unable to  Search Transaction! " + poJSON.get("message"));
                            }
                            return;

                    }
            }
        }
    }
}
