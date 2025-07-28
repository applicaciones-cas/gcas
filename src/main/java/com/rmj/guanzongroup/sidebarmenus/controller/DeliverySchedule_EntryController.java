package com.rmj.guanzongroup.sidebarmenus.controller;

import java.lang.reflect.Field;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import javafx.scene.control.ListCell;
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
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.inv.warehouse.t4.DeliverySchedule;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.services.DeliveryScheduleControllers;
import ph.com.guanzongroup.cas.inv.warehouse.t4.constant.DeliveryScheduleStatus;
import ph.com.guanzongroup.cas.inv.warehouse.t4.constant.DeliveryScheduleTruck;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Delivery_Schedule_Detail;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Delivery_Schedule_Master;
import ph.com.guanzongroup.cas.inv.warehouse.t4.parameter.model.Model_Branch_Others;

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

    private String psClusterNameOld = "";

    private int pnClusterDetail = -1;
    private int pnTransaction = -1;
    private int pnBranchList = -1;
    private int pgTransactionMax = 50;
    private int pgBranchMax = 50;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton,
            apCenter, apDetailField, apMaster,
            apDetail, apDetailTable, apTransactionTable, apBranchTable;
    @FXML
    private TextField tfSearchCluster, tfTransactionNo, tfClusterName, tfAllocation;
    @FXML
    private ComboBox cbTruckSize;
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
    private TableView<Model_Delivery_Schedule_Detail> tblClusterDetail;
    @FXML
    private TableView<Model_Delivery_Schedule_Master> tblTransaction;
    @FXML
    private TableView<Model_Branch_Others> tblBranchList;
    @FXML
    private TableColumn<Model_Delivery_Schedule_Master, String> tblColDeliveryNo, tblColDeliveryTransaction, tblColDeliveryDate, tblColDeliveryScheduledDate; //Transaction List
    @FXML
    private TableColumn<Model_Delivery_Schedule_Detail, String> tblColDetailNo, tblColDetailName, tblColDetailTruckSize, tblColDetailAllocation;//Detail Table
    @FXML
    private TableColumn<Model_Branch_Others, String> tblColBranchNo, tblColBranchName, tblColBranchAddress;//BranchList
    @FXML
    private Pagination pgTransaction, pgBranchList;

    //FOR OVERLAY
    private int currentBranchLoadVersion = 0;
    private Task<Void> currentBranchLoadTask;
    private ProgressIndicator piIndicator;
    private StackPane spOverlayIndicator;

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
            poLogWrapper = new LogWrapper(psFormName, psFormName);
            poAppController = new DeliveryScheduleControllers(poApp, poLogWrapper).DeliverySchedule();
            poAppController.setTransactionStatus(DeliveryScheduleStatus.OPEN);
            if (!isJSONSuccess(poAppController.initTransaction(), "Initialize Transaction")) {
                unloadForm appUnload = new unloadForm();
                appUnload.unloadForm(apMainAnchor, poApp, psFormName);
            }

            Platform.runLater(() -> {
                poAppController.getMaster().setIndustryId(psIndustryID);
                poAppController.setIndustryID(psIndustryID);
                poAppController.setCompanyID(psCompanyID);
                poAppController.setCategoryID(psCategoryID);
                System.err.println("Initialize value : Industry >" + psIndustryID
                        + "\nCompany :" + psCompanyID
                        + "\nCategory:" + psCategoryID);
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
                    if (!isJSONSuccess(poAppController.UpdateTransaction(), "Initialize Update Transaction")) {
                        return;
                    }
                    pnEditMode = poAppController.getEditMode();
                    break;
                case "btnSearch":
                case "btnNew":
                    if (!isJSONSuccess(poAppController.newTransaction(), "Initialize New Transaction")) {
                        return;
                    }
                    clearAllInputs();
                    loadTransactionMaster();
                    loadTableTransactionDetail();
                    pnEditMode = poAppController.getEditMode();
                    break;
                case "btnSave":
                case "btnCancel":
                    if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to disregard changes?") == true) {
                        poAppController = new DeliveryScheduleControllers(poApp, poLogWrapper).DeliverySchedule();
                        poAppController.setTransactionStatus(DeliveryScheduleStatus.OPEN);

                        if (!isJSONSuccess(poAppController.initTransaction(), "Initialize Transaction")) {
                            unloadForm appUnload = new unloadForm();
                            appUnload.unloadForm(apMainAnchor, poApp, psFormName);
                        }

                        Platform.runLater(() -> {
                            poAppController.getMaster().setIndustryId(psIndustryID);
                            poAppController.setIndustryID(psIndustryID);
                            poAppController.setCompanyID(psCompanyID);
                            poAppController.setCategoryID(psCategoryID);
                            clearAllInputs();
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

        try {
            loadSelectedTransactionDetail(pnClusterDetail);
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
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
            if (loControl instanceof TextField) {
                ((TextField) loControl).clear();
            } else if (loControl instanceof TextArea) {
                ((TextArea) loControl).clear();
            } else if (loControl instanceof TableView) {
                ((TableView) loControl).getItems().clear();
            } else if (loControl instanceof DatePicker) {
                ((DatePicker) loControl).setValue(null);
            } else if (loControl instanceof ComboBox) {
                ((ComboBox) loControl).setItems(null);
            }
        }
        cbTruckSize.setItems(FXCollections.observableArrayList(DeliveryScheduleTruck.SIZE));
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
        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                    case F3:
                        switch (txtFieldID) {

                            //Search Pane
                            case "tfSearchCluster":
                                if (!isJSONSuccess(poAppController.searchTransaction(lsValue, false),
                                        "Unable to  Search Transaction!")) {
                                }
                                return;

                            //Detail Pane
                            case "tfClusterName":
                                if (!isJSONSuccess(poAppController.searchClusterBranch(pnClusterDetail, lsValue, true),
                                        "Unable to Search Cluster! ")) {
                                }
                                loadTableTransactionDetail();
                                loadSelectedTransactionDetail(pnClusterDetail);
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
            Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
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
                    CommonUtils.SetNextFocus((TextArea) event.getSource());
                    return;
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
                            if (!isJSONSuccess(poAppController.searchTransaction(SQLUtil.dateFormat(ldDateValue, SQLUtil.FORMAT_SHORT_DATE), true, true),
                                    "Unable to  Search Transaction!! BY Date")) {
                            }
                            return;
                        case "dpSearchScheduleDate":
                            if (!isJSONSuccess(poAppController.searchTransaction(SQLUtil.dateFormat(ldDateValue, SQLUtil.FORMAT_SHORT_DATE), false, true),
                                    "Unable to  Search Transaction!! BY Schedule Date")) {
                            }
                    }
            }
        }
    }

    private boolean isJSONSuccess(JSONObject loJSON, String fsModule) {
        String result = (String) loJSON.get("result");
        if ("error".equals(result)) {
            String message = (String) loJSON.get("message");
            poLogWrapper.severe(message);
            ShowMessageFX.Warning(null, psFormName, fsModule + ": " + message);
            return false;
        }
        return true;

    }

    private void loadTransactionMaster() {
        tfTransactionNo.setText(poAppController.getMaster().getTransactionNo());
        dpTransactionDate.setValue(ParseDate(poAppController.getMaster().getTransactionDate()));
        dpScheduleDate.setValue(ParseDate(poAppController.getMaster().getScheduleDate()));
        taRemarks.setText(poAppController.getMaster().getRemarks());
        lblStatus.setText(DeliveryScheduleStatus.STATUS.get(Integer.parseInt(poAppController.getMaster().getTransactionStatus())));

    }

    private LocalDate ParseDate(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void loadSelectedTransactionDetail(int fnRow) throws SQLException, GuanzonException, CloneNotSupportedException {
        tfClusterName.setText(tblColDetailName.getCellData(fnRow));
        taNotes.setText(poAppController.getDetail(fnRow).getRemarks());
        tfAllocation.setText(tblColDetailAllocation.getCellData(fnRow));
        cbTruckSize.getSelectionModel().select(Integer.parseInt(poAppController.getDetail(fnRow).getTruckSize()));

        if (tfClusterName.getText() != null && tfClusterName.getText().isEmpty()) {
            tblBranchList.getItems().clear();
            psClusterNameOld = tfClusterName.getText();
            loadBranch(fnRow);
            return;
        }
        Set<Integer> enabledTruckSizes = new HashSet<>();
        for (int lnCBDelivery = 0;
                lnCBDelivery < poAppController.getDetail(fnRow).BranchCluster().getBranchClusterDeliverysCount();
                lnCBDelivery++) {
            int lnEnableList = poAppController.getDetail(fnRow)
                    .BranchCluster().BranchClusterDelivery(lnCBDelivery).getTruckSize();

            enabledTruckSizes.add(lnEnableList); // Collect enabled truck size index
        }
        cbTruckSize.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String fsItem, boolean isEmpty) {
                super.updateItem(fsItem, isEmpty);
                if (isEmpty || fsItem == null) {
                    setText(null);
                    setDisable(false);
                } else {
                    setText(fsItem);
                    int lnIndex = FXCollections.observableArrayList(DeliveryScheduleTruck.SIZE).indexOf(fsItem);
                    boolean isEnabled = enabledTruckSizes.contains(lnIndex);
                    setDisable(!isEnabled);
                    setOpacity(isEnabled ? 1.0 : 0.5);
                }
            }
        });

        int selectedIndex = -1;
        try {
            selectedIndex = Integer.parseInt(poAppController.getDetail(fnRow).getTruckSize());
        } catch (NumberFormatException e) {
            // Invalid input; selectedIndex remains -1
        }

        if (enabledTruckSizes.contains(selectedIndex)) {
            cbTruckSize.getSelectionModel().select(selectedIndex);
            tfAllocation.setText(
                    poAppController.getDetail(fnRow)
                            .BranchCluster().BranchClusterDelivery(selectedIndex)
                            .getAllocation().toString()
            );
        } else {
            // Fallback to first enabled index
            for (int lnRow = 0; lnRow < cbTruckSize.getItems().size(); lnRow++) {
                if (enabledTruckSizes.contains(lnRow)) {
                    cbTruckSize.getSelectionModel().select(lnRow);
                    tfAllocation.setText(
                            poAppController.getDetail(fnRow).BranchCluster().BranchClusterDelivery(lnRow)
                                    .getAllocation().toString());
                    poAppController.getDetail(fnRow).setTruckSize(String.valueOf(lnRow));
                    loadTableTransactionDetail();
                    break;
                }
            }
        }
        if (tfClusterName.getText() != null && !tfClusterName.getText().equals(psClusterNameOld)) {
            psClusterNameOld = tfClusterName.getText();
            loadBranch(fnRow);
        }
    }

    private void loadTableTransactionDetail() {
        ObservableList<Model_Delivery_Schedule_Detail> items
                = FXCollections.observableArrayList(poAppController.getDetailList());

        tblClusterDetail.setItems(items);
        tblClusterDetail.getSelectionModel().select(pnClusterDetail >= 0 ? pnClusterDetail : items.size() - 1);
        pnClusterDetail = tblClusterDetail.getSelectionModel().getFocusedIndex();

        tblColDetailNo.setCellValueFactory(loModel -> {
            int index = tblClusterDetail.getItems().indexOf(loModel.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(index));
        });

        tblColDetailName.setCellValueFactory(loModel -> {
            try {
                return new SimpleStringProperty(loModel.getValue().BranchCluster().getClusterDescription());
            } catch (SQLException | GuanzonException ex) {
                Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                return new SimpleStringProperty("");
            }
        });
        tblColDetailTruckSize.setCellValueFactory(loModel
                -> new SimpleStringProperty(DeliveryScheduleTruck.SIZE.get(Integer.parseInt(loModel.getValue().getTruckSize()))));
        tblColDetailAllocation.setCellValueFactory(loModel -> {
            try {
                int truckSizeIndex = Integer.parseInt(loModel.getValue().getTruckSize());
                Object loAllocation = 0;
                if (loModel.getValue().BranchCluster().getBranchClusterDeliverysCount() > 0) {
                    loAllocation = loModel.getValue()
                            .BranchCluster().BranchClusterDeliveryTruck(truckSizeIndex)
                            .getAllocation().toString();
                } else {
                    loAllocation = 0;
                }
                return new SimpleStringProperty(loAllocation != null ? loAllocation.toString() : "0");
            } catch (Exception e) {
                e.printStackTrace();
                return new SimpleStringProperty("0");
            }
        }
        );
    }

    private void loadBranch(int fnSelectedRow) throws CloneNotSupportedException {
        StackPane overlay = getOverlayProgress(apBranchTable);
        overlay.setVisible(true);
        piIndicator.setVisible(true);

        // Increment version to invalidate previous tasks
        final int taskVersion = ++currentBranchLoadVersion;

        // Cancel previous task if still running
        if (currentBranchLoadTask != null && currentBranchLoadTask.isRunning()) {
            currentBranchLoadTask.cancel(true);
        }

        tblClusterDetail.setDisable(true);
        currentBranchLoadTask = new Task<Void>() {
            private ObservableList<Model_Branch_Others> laBranchList;

            @Override
            protected Void call() throws Exception {
                if (isCancelled()) {
                    return null;
                }

                if (!isJSONSuccess(poAppController.LoadBranchOthers(fnSelectedRow),
                        "Initialize : Load of Branch List")) {
                    return null;
                }

                if (isCancelled()) {
                    return null;
                }

                // Clone the list 
                List<Model_Branch_Others> rawList = poAppController.getDeliveryBranchOtherList(fnSelectedRow);
                laBranchList = FXCollections.observableArrayList(new ArrayList<>(rawList));
                return null;
            }

            @Override
            protected void succeeded() {
                if (taskVersion != currentBranchLoadVersion) {
                    return; // Ignore outdated task
                }
                overlay.setVisible(false);
                piIndicator.setVisible(false);

                // Clear and update table
                tblBranchList.getItems().clear();
                tblBranchList.setItems(laBranchList);

                tblColBranchNo.setCellValueFactory(loModel -> {
                    int index = tblBranchList.getItems().indexOf(loModel.getValue()) + 1;
                    return new SimpleStringProperty(String.valueOf(index));
                });

                tblColBranchName.setCellValueFactory(loModel -> {
                    try {
                        return new SimpleStringProperty(loModel.getValue().Branch().getBranchName());
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                        return new SimpleStringProperty("");
                    }
                });

                tblColBranchAddress.setCellValueFactory(loModel -> {
                    try {
                        return new SimpleStringProperty(loModel.getValue().Branch().getAddress());
                    } catch (SQLException | GuanzonException ex) {
                        Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                        return new SimpleStringProperty("");
                    }
                });

                tblClusterDetail.setDisable(false);
            }

            @Override
            protected void failed() {
                overlay.setVisible(false);
                piIndicator.setVisible(false);
                tblClusterDetail.setDisable(false);
            }

            @Override
            protected void cancelled() {
                overlay.setVisible(false);
                tblClusterDetail.setDisable(false);
            }
        };

        Thread thread = new Thread(currentBranchLoadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTransaction() {
        ObservableList<Model_Delivery_Schedule_Master> laMasterList
                = FXCollections.observableArrayList(poAppController.getMasterList());

        tblTransaction.setItems(laMasterList);

        tblColDeliveryNo.setCellValueFactory(loModel -> {
            int index = tblTransaction.getItems().indexOf(loModel.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(index));
        });

        tblColDeliveryTransaction.setCellValueFactory(loModel
                -> new SimpleStringProperty(loModel.getValue().getTransactionNo()));
        tblColDeliveryDate.setCellValueFactory(loModel
                -> new SimpleStringProperty(SQLUtil.dateFormat(loModel.getValue().getTransactionDate(), SQLUtil.FORMAT_LONG_DATE)));
        tblColDeliveryScheduledDate.setCellValueFactory(loModel
                -> new SimpleStringProperty(SQLUtil.dateFormat(loModel.getValue().getScheduleDate(), SQLUtil.FORMAT_LONG_DATE)));

    }

    private StackPane getOverlayProgress(AnchorPane foAnchorPane) {
        // Check if overlay already exists
        for (Node node : foAnchorPane.getChildren()) {
            if (node instanceof StackPane) {
                StackPane existingStack = (StackPane) node;
                for (Node child : existingStack.getChildren()) {
                    if (child instanceof ProgressIndicator) {
                        piIndicator = (ProgressIndicator) child;
                        spOverlayIndicator = existingStack;
                        return spOverlayIndicator;
                    }
                }
            }
        }
        if (piIndicator == null) {
            piIndicator = new ProgressIndicator();
            piIndicator.setMaxSize(50, 50);
            piIndicator.setVisible(false);
            piIndicator.setStyle("-fx-progress-color: orange;");
        }

        if (spOverlayIndicator == null) {
            spOverlayIndicator = new StackPane();
            spOverlayIndicator.setPickOnBounds(false); // Let clicks through
            spOverlayIndicator.getChildren().add(piIndicator);

            // Anchor it to fill the parent
            AnchorPane.setTopAnchor(spOverlayIndicator, 0.0);
            AnchorPane.setBottomAnchor(spOverlayIndicator, 0.0);
            AnchorPane.setLeftAnchor(spOverlayIndicator, 0.0);
            AnchorPane.setRightAnchor(spOverlayIndicator, 0.0);

            foAnchorPane.getChildren().add(spOverlayIndicator);
        }

        return spOverlayIndicator;

    }
}
