package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.controller.DeliverySchedule_EntryController;
import com.rmj.guanzongroup.sidebarmenus.controller.ScreenInterface;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import javafx.concurrent.Task;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import org.guanzon.appdriver.base.GuanzonException;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.inv.warehouse.t4.InventoryStockIssuanceNeo;
import ph.com.guanzongroup.cas.inv.warehouse.t4.constant.DeliveryIssuanceType;
import ph.com.guanzongroup.cas.inv.warehouse.t4.constant.InventoryStockIssuanceStatus;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Inventory_Transfer_Detail;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Inventory_Transfer_Master;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.services.DeliveryIssuanceControllers;

/**
 * FXML Controller class
 *
 * @author User
 */
public class InventoryStockIssuanceNeoController_Approval implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private LogWrapper poLogWrapper;
    private String psFormName = "Issuance Approval";
    private String psIndustryID, psCompanyID, psCategoryID;
    private Control lastFocusedControl;
    private InventoryStockIssuanceNeo poAppController;
    private ObservableList<Model_Inventory_Transfer_Master> laTransactionMaster;
    private ObservableList<Model_Inventory_Transfer_Detail> laTransactionDetail;
    private int pnSelectMaster, pnEditMode, pnCTransactionDetail;

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
                case "tfDiscountRate":
                    if (lsValue.isEmpty()) {
                        ShowMessageFX.Information("Invalid freight amount", psFormName, null);
                        loTextField.requestFocus();
                        return;
                    }

                    poAppController.getMaster().setFreight(Double.parseDouble(lsValue));
                    break;

                case "tfDiscountAmount":
                    if (lsValue.isEmpty()) {
                        ShowMessageFX.Information("Invalid discount amount", psFormName, null);
                        loTextField.requestFocus();
                        return;
                    }

                    poAppController.getMaster().setDiscount(Double.parseDouble(lsValue));
                    break;
            }
            getLoadedTransaction();
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
            tfVariant, tfMeasure, tfInvType, tfCost, tfIssuedQty;

    @FXML
    Button btnSearch, btnUpdate, btnPrint, btnVoid, btnSave, btnCancel, btnHistory, btnRetrieve, btnClose;

    @FXML
    TableView<Model_Inventory_Transfer_Master> tblViewMaster;

    @FXML
    TableView<Model_Inventory_Transfer_Detail> tblViewDetails;

    @FXML
    TableColumn<Model_Inventory_Transfer_Master, String> tblColNo, tblColTransNo, tblColTransDate, tblColBranch;

    @FXML
    TableColumn<Model_Inventory_Transfer_Detail, String> tblColDetailNo, tblColDetailOrderNo, tblColDetailSerial, tblColDetailBarcode, tblColDetailDescr,
            tblColDetailBrand, tblColDetailVariant, tblColDetailCost, tblColDetailOrderQty;

    @FXML
    Label lblSource, lblStatus;

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

        try {
            poLogWrapper = new LogWrapper(psFormName, psFormName);
            poAppController = new DeliveryIssuanceControllers(poApp, poLogWrapper).InventoryStockIssuanceNeo();
            poAppController.setTransactionStatus(InventoryStockIssuanceStatus.OPEN);

            //initlalize and validate transaction objects from class controller
            if (!isJSONSuccess(poAppController.initTransaction(), psFormName)) {
                unloadForm appUnload = new unloadForm();
                appUnload.unloadForm(apMainAnchor, poApp, psFormName);
            }

            //background thread
            Platform.runLater(() -> {

                poAppController.setTransactionStatus("10");
                //initialize logged in category
                poAppController.setIndustryID(psIndustryID);
                poAppController.setCompanyID(psCompanyID);
                poAppController.setCategoryID(psCategoryID);
                System.err.println("Initialize value : Industry >" + psIndustryID
                        + "\nCompany :" + psCompanyID
                        + "\nCategory:" + psCategoryID);

            });

            initializeTableDetail();
            initControlEvents();
        } catch (SQLException | GuanzonException e) {
            Logger.getLogger(InventoryStockIssuanceNeo.class.getName()).log(Level.SEVERE, null, e);
            poLogWrapper.severe(psFormName + " :" + e.getMessage());
        }
    }

    @FXML
    void ontblMasterClicked(MouseEvent e) {
        pnSelectMaster = tblViewMaster.getSelectionModel().getSelectedIndex();
        if (pnSelectMaster < 0) {
            return;
        }

        if (e.getClickCount() == 1 && !e.isConsumed()) {
            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to disregard changes?") != true) {
                    return;
                }
            }
            try {
                e.consume();
                if (!isJSONSuccess(poAppController.OpenTransaction(tblColTransNo.getCellData(pnSelectMaster)), psFormName)) {
                    ShowMessageFX.Information("Failed to open transaction", psFormName, null);
                    return;
                }

                getLoadedTransaction();
            } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                poLogWrapper.severe(psFormName + " :" + ex.getMessage());

            }

        }
        return;
    }

    @FXML
    void ontblDetailClicked(MouseEvent e) {
        pnCTransactionDetail = tblViewDetails.getSelectionModel().getSelectedIndex() + 1;
        loadSelectedDetail();
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        try {
            //get button id
            String btnID = ((Button) event.getSource()).getId();
            String transactionStatus = poAppController.getMaster().getTransactionStatus();
            switch (btnID) {
                case "btnSearch":
                    if (lastFocusedControl == null) {
                        ShowMessageFX.Information(null, psFormName,
                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
                        return;
                    }

                    switch (lastFocusedControl.getId()) {

                        case "tfClusterName":
                            if (!isJSONSuccess(poAppController.searchTransactionDestination(tfClusterName.getText(), false),
                                    "Initialize Search Destination! ")) {
                                return;
                            }
                            tfClusterName.setText(poAppController.getMaster().BranchDestination().getBranchName());
                            break;
                        case "tfTrucking":
                            if (!isJSONSuccess(poAppController.searchTransactionTrucking(tfTrucking.getText(), false),
                                    "Initialize Search Trucking! ")) {
                                return;
                            }
                            tfTrucking.setText(poAppController.getMaster().TruckingCompany().getCompanyName());
                            break;
                        case "tfSearchSerial":
                            if (pnCTransactionDetail > 0) {
                                if (!isJSONSuccess(poAppController.searchDetailByIssuance(pnCTransactionDetail, tfSearchSerial.getText(), false, true),
                                        "Initialize Search Serial! ")) {
                                    return;
                                }
                                reloadTableDetail();
                            }
                            break;
                        case "tfSearchBarcode":
                            if (pnCTransactionDetail > 0) {
                                if (!isJSONSuccess(poAppController.searchDetailByIssuance(pnCTransactionDetail, tfSearchBarcode.getText(), true, false),
                                        "Initialize Search Barcode! ")) {
                                    return;
                                }

                            }
                            break;
                        case "tfSearchDescription":
                            if (pnCTransactionDetail > 0) {
                                if (!isJSONSuccess(poAppController.searchDetailByIssuance(pnCTransactionDetail, tfSearchDescription.getText(), false, false),
                                        "Initialize Search Description! ")) {
                                    return;
                                }

                            }
                            break;
                        case "tfSupersede":
                            if (pnCTransactionDetail > 0) {
                                if (!isJSONSuccess(poAppController.searchDetailByBarcode(pnCTransactionDetail, tfSupersede.getText(), true),
                                        "Initialize Search Supersede! ")) {
                                    return;
                                }

                            }
                            break;
                    }
                    break;

                case "btnUpdate":
                    if (poAppController.getMaster().getTransactionNo() == null || poAppController.getMaster().getTransactionNo().isEmpty()) {
                        ShowMessageFX.Information("Please load transaction before proceeding..", "Stock Request Issuance Approval", "");
                        return;
                    }

                    if (!transactionStatus.equals(InventoryStockIssuanceStatus.OPEN)) {
                        ShowMessageFX.Information("Transaction already " + transactionStatus, "Stock Request Issuance Approval", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.UpdateTransaction(), "Initialize UPdate Transaction")) {
                        return;
                    }
                    getLoadedTransaction();
                    pnEditMode = poAppController.getEditMode();
                    break;

                case "btnPrint":
                    if (poAppController.getMaster().getTransactionNo() == null || poAppController.getMaster().getTransactionNo().isEmpty()) {
                        ShowMessageFX.Information("Please load transaction before proceeding..", "Stock Request Issuance Approval", "");
                        return;
                    }
                    if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to print the transaction ?") == true) {
                        if (!isJSONSuccess(poAppController.printRecord(),
                                "Initialize Print Transaction")) {
                            return;
                        }
                    }
                    //refresh ui 
                    clearAllInputs();
                    reloadTableDetail();

                    pnEditMode = poAppController.getEditMode();
                    break;

                case "btnVoid":
                    if (poAppController.getMaster().getTransactionNo().isEmpty()) {
                        ShowMessageFX.Information("Please load transaction before proceeding..", "Stock Request Issuance Approval", "");
                        break;
                    }

                    if (transactionStatus.equals(InventoryStockIssuanceStatus.VOID)) {
                        ShowMessageFX.Information("Transaction already " + transactionStatus, "Stock Request Issuance Approval", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.VoidTransaction(), "Initialize Void Transaction")) {
                        return;
                    }

                    clearAllInputs();
                    getLoadedTransaction();
                    pnEditMode = poAppController.getEditMode();
                    break;

                case "btnSave":
                    if (tfTransNo.getText().isEmpty()) {
                        ShowMessageFX.Information("Please load transaction before proceeding..", "Stock Request Issuance", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.SaveTransaction(), "Initialize Save Transaction")) {
                        return;
                    }
                    reloadTableDetail();
                    clearAllInputs();
                    pnEditMode = poAppController.getEditMode();

                    break;

                case "btnCancel":
                    if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to disregard changes?") == true) {
                        poAppController = new DeliveryIssuanceControllers(poApp, poLogWrapper).InventoryStockIssuanceNeo();
                        poAppController.setTransactionStatus(InventoryStockIssuanceStatus.OPEN);

                        if (!isJSONSuccess(poAppController.initTransaction(), "Initialize Transaction")) {
                            unloadForm appUnload = new unloadForm();
                            appUnload.unloadForm(apMainAnchor, poApp, psFormName);
                        }

                        Platform.runLater(() -> {

                            poAppController.setTransactionStatus("10");
                            poAppController.getMaster().setIndustryId(psIndustryID);
                            poAppController.setIndustryID(psIndustryID);
                            poAppController.setCompanyID(psCompanyID);
                            poAppController.setCategoryID(psCategoryID);
                            clearAllInputs();
                        });
                        pnEditMode = poAppController.getEditMode();
                        break;
                    }
                    break;

                case "btnHistory":
                    ShowMessageFX.Information(null, psFormName,
                            "This feature is under development and will be available soon.\nThank you for your patience!");
                    break;

                case "btnRetrieve":
                    if (lastFocusedControl == null) {
                        ShowMessageFX.Information(null, psFormName,
                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
                        return;
                    }

                    switch (lastFocusedControl.getId()) {
                        case "tfSearchSourceno":
                            if (!tfTransNo.getText().isEmpty()) {
                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
                                    return;
                                }
                            }
                            if (!isJSONSuccess(poAppController.searchTransaction(tfSearchSourceno.getText(), true, true),
                                    "Initialize Search Source No! ")) {
                                return;
                            }

                            tfSearchSourceno.setText(poAppController.getMaster().Branch().getBranchName());
                            loadTransactionMasterList(tfSearchSourceno.getText(), "e.sBranchNm");
                            getLoadedTransaction();
                            initButtonDisplay(poAppController.getEditMode());
                            break;
                        case "tfSeacrchTransNo":
                            if (!tfTransNo.getText().isEmpty()) {
                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
                                    return;
                                }
                            }
                            if (!isJSONSuccess(poAppController.searchTransaction(tfSeacrchTransNo.getText(), true, true),
                                    "Initialize Search Transaction! ")) {
                                return;
                            }

                            tfSeacrchTransNo.setText(poAppController.getMaster().getTransactionNo());

                            loadTransactionMasterList(tfSeacrchTransNo.getText(), "a.sTransNox");
                            getLoadedTransaction();
                            initButtonDisplay(poAppController.getEditMode());
                            break;
                    }
                    break;

                case "btnClose":
                    unloadForm appUnload = new unloadForm();
                    if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?")) {
                        appUnload.unloadForm(apMainAnchor, poApp, psFormName);
                    }
                    break;
            }

            initButtonDisplay(poAppController.getEditMode());

        } catch (Exception e) {
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
        try {
            if (null != event.getCode()) {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                        switch (txtFieldID) {
                            case "tfDiscountRate":
                                if (lsValue.isEmpty()) {
                                    ShowMessageFX.Information("Imvalid freight amount", psFormName, null);
                                    loTxtField.requestFocus();
                                    return;
                                }

                                poAppController.getMaster().setFreight(Double.parseDouble(lsValue));
                                break;
                            case "tfDiscountAmount":
                                if (lsValue.isEmpty()) {
                                    ShowMessageFX.Information("Imvalid discount amount", psFormName, null);
                                    loTxtField.requestFocus();
                                    return;
                                }

                                poAppController.getMaster().setDiscount(Double.parseDouble(lsValue));
                                break;
                        }
                    case F3:
                        switch (txtFieldID) {
                            case "tfSearchSourceno":
                                if (!tfTransNo.getText().isEmpty()) {
                                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
                                        return;
                                    }
                                }
                                if (!isJSONSuccess(poAppController.searchTransaction(tfSearchSourceno.getText(), true, true),
                                        "Initialize Search Source No! ")) {
                                    return;
                                }

                                tfSearchSourceno.setText(poAppController.getMaster().Branch().getBranchName());
                                loadTransactionMasterList(tfSearchSourceno.getText(), "e.sBranchNm");
                                initButtonDisplay(poAppController.getEditMode());
                                break;
                            case "tfSeacrchTransNo":
                                if (!tfTransNo.getText().isEmpty()) {
                                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
                                        return;
                                    }
                                }
                                if (!isJSONSuccess(poAppController.searchTransaction(tfSeacrchTransNo.getText(), true, true),
                                        "Initialize Search Transaction! ")) {
                                    return;
                                }

                                tfSeacrchTransNo.setText(poAppController.getMaster().getTransactionNo());
                                loadTransactionMasterList(tfSeacrchTransNo.getText(), "a.sTransNox");
                                initButtonDisplay(poAppController.getEditMode());
                                break;
                            case "tfClusterName":
                                if (!isJSONSuccess(poAppController.searchTransactionDestination(tfClusterName.getText(), false),
                                        "Initialize Search Destination! ")) {
                                    return;
                                }
                                tfClusterName.setText(poAppController.getMaster().BranchDestination().getBranchName());
                                break;
                            case "tfTrucking":
                                if (!isJSONSuccess(poAppController.searchTransactionTrucking(tfTrucking.getText(), false),
                                        "Initialize Search Trucking! ")) {
                                    return;
                                }
                                tfTrucking.setText(poAppController.getMaster().TruckingCompany().getCompanyName());
                                break;
                            case "tfSearchSerial":
                                if (pnCTransactionDetail > 0) {
                                    if (!isJSONSuccess(poAppController.searchDetailByIssuance(pnCTransactionDetail, tfSearchSerial.getText(), false, true),
                                            "Initialize Search Serial! ")) {
                                        return;
                                    }
                                    reloadTableDetail();
                                }
                                break;
                            case "tfSearchBarcode":
                                if (pnCTransactionDetail > 0) {
                                    if (!isJSONSuccess(poAppController.searchDetailByIssuance(pnCTransactionDetail, tfSearchBarcode.getText(), true, false),
                                            "Initialize Search Barcode! ")) {
                                        return;
                                    }

                                }
                                break;
                            case "tfSearchDescription":
                                if (pnCTransactionDetail > 0) {
                                    if (!isJSONSuccess(poAppController.searchDetailByIssuance(pnCTransactionDetail, tfSearchDescription.getText(), false, false),
                                            "Initialize Search Description! ")) {
                                        return;
                                    }

                                }
                                break;
                            case "tfSupersede":
                                if (pnCTransactionDetail > 0) {
                                    if (!isJSONSuccess(poAppController.searchDetailByBarcode(pnCTransactionDetail, tfSupersede.getText(), true),
                                            "Initialize Search Supersede! ")) {
                                        return;
                                    }

                                }
                                break;
                        }
                        break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }

    private void loadDeliveryTypes() {
        List<String> deliveryTypes = DeliveryIssuanceType.DeliveryType;
        cbDelType.setItems(FXCollections.observableArrayList(deliveryTypes));
    }

    private void loadTransactionMasterList(String value, String fsColumn) {
        StackPane overlay = getOverlayProgress(apMaster);
        ProgressIndicator pi = (ProgressIndicator) overlay.getChildren().get(0);
        overlay.setVisible(true);
        pi.setVisible(true);

        Task<ObservableList<Model_Inventory_Transfer_Master>> loadTransaction = new Task<ObservableList<Model_Inventory_Transfer_Master>>() {
            @Override
            protected ObservableList<Model_Inventory_Transfer_Master> call() throws Exception {
                if (!isJSONSuccess(poAppController.loadTransactionList(value, fsColumn),
                        "Initialize : Load of Transaction List")) {
                    return null;
                }

                List<Model_Inventory_Transfer_Master> rawList = poAppController.getMasterList();
                System.out.print("The size of list is " + rawList.size());
                return FXCollections.observableArrayList(new ArrayList<>(rawList));
            }

            @Override
            protected void succeeded() {
                ObservableList<Model_Inventory_Transfer_Master> laMasterList = getValue();
                tblViewMaster.setItems(laMasterList);

                tblColNo.setCellValueFactory(loModel -> {
                    int index = tblViewMaster.getItems().indexOf(loModel.getValue()) + 1;
                    return new SimpleStringProperty(String.valueOf(index));
                });
                tblColTransNo.setCellValueFactory(loModel -> {
                    return new SimpleStringProperty(String.valueOf(loModel.getValue().getTransactionNo()));
                });
                tblColTransDate.setCellValueFactory(loModel -> {
                    return new SimpleStringProperty(String.valueOf(loModel.getValue().getTransactionDate()));
                });
                tblColBranch.setCellValueFactory(loModel -> {
                    try {
                        return new SimpleStringProperty(String.valueOf(loModel.getValue().Branch().getBranchName()));
                    } catch (Exception e) {
                        poLogWrapper.severe(psFormName, e.getMessage());
                        return new SimpleStringProperty("");
                    }
                });

                getLoadedTransaction();

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
        Thread thread = new Thread(loadTransaction);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTransactionMaster() {
        try {
            lblSource.setText(poAppController.getMaster().Company().getCompanyName() == null ? "" : (poAppController.getMaster().Company().getCompanyName() + " - ")
                    + poAppController.getMaster().Industry().getDescription() == null ? "" : poAppController.getMaster().Industry().getDescription());
            lblStatus.setText(InventoryStockIssuanceStatus.STATUS.get(Integer.parseInt(poAppController.getMaster().getTransactionStatus())) == null ? "STATUS"
                    : InventoryStockIssuanceStatus.STATUS.get(Integer.parseInt(poAppController.getMaster().getTransactionStatus())));

            tfTransNo.setText(poAppController.getMaster().getTransactionNo());
            dpTransactionDate.setValue(ParseDate(poAppController.getMaster().getTransactionDate()));
            tfClusterName.setText(poAppController.getMaster().getDestination());
            tfTrucking.setText(poAppController.getMaster().getTruckId());
            tfDiscountRate.setText(String.valueOf(poAppController.getMaster().getDiscount()));
            tfDiscountAmount.setText(String.valueOf(poAppController.getMaster().getDiscount()));
            tfTotal.setText(String.valueOf(poAppController.getMaster().getTransactionTotal()));
        } catch (SQLException | GuanzonException e) {
            poLogWrapper.severe(psFormName, e.getMessage());
        }
    }

    private void loadSelectedTableItem(int fnRow, TableView<?> ftblSrc, HashMap<String, Object> fmapObject) {
        //iterate to table columns
        for (TableColumn<?, ?> column : ftblSrc.getColumns()) {
            String lscolumnID = column.getId(); //get column id
            Object loField = fmapObject.get(lscolumnID); //get assigned object and validate type of object that will display the cell value

            //display value to field if object is not empty
            if (loField instanceof TextField) {
                if (loField != null) {
                    ((TextField) loField).setText(column.getCellData(fnRow) == null ? "" : column.getCellData(fnRow).toString());
                }
            }

            if (loField instanceof DatePicker) {
                if (loField != null) {
                    ((DatePicker) loField).setValue(CustomCommonUtil.parseDateStringToLocalDate(
                            column.getCellData(fnRow) == null ? "" : column.getCellData(fnRow).toString(), "yyyy-MM-dd"));
                }
            }
        }
    }

    private void loadSelectedDetail() {
        try {
            HashMap<String, Object> loMapFields = new HashMap<>();
            loMapFields.put("tblColDetailSerial", tfSearchSerial);
            loMapFields.put("tblColDetailBarcode", tfSearchBarcode);
            loMapFields.put("tblColDetailDescr", tfSearchDescription);
            loMapFields.put("tblColDetailBrand", tfBrand);
            loMapFields.put("tblColDetailVariant", tfVariant);
            loMapFields.put("tblColDetailCost", tfCost);
            loMapFields.put("tblColDetailOrderQty", tfIssuedQty);

            loadSelectedTableItem(pnCTransactionDetail - 1, tblViewDetails, loMapFields);

            tfColor.setText(poAppController.getDetail(pnCTransactionDetail).Inventory().Color().getDescription());
            tfMeasure.setText(poAppController.getDetail(pnCTransactionDetail).Inventory().Measure().getDescription());
            tfInvType.setText(poAppController.getDetail(pnCTransactionDetail).Inventory().InventoryType().getDescription());
            tfSupersede.setText(poAppController.getDetail(pnCTransactionDetail).InventorySupersede().getBarCode());

        } catch (SQLException | GuanzonException e) {
            poLogWrapper.severe(psFormName, e.getMessage());
        }
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
            } else if (loControl instanceof ComboBox) {
                ComboBox loControlField = (ComboBox) loControl;
                controllerFocusTracker(loControlField);
            }
        }
        clearAllInputs();
        loadDeliveryTypes();
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
            } else if (loControl != null && (loControl instanceof TableView)) {
                TableView<?> table = (TableView<?>) loControl;
                if (table.getItems() != null) {
                    table.getItems().clear();
                }
            } else if (loControl != null && (loControl instanceof ComboBox)) {
                ComboBox cbox = (ComboBox) loControl;
                if (cbox.getItems() != null) {
                    cbox.getItems().clear();
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
        initButtonControls(lbShow, "btnSave", "btnCancel");
        initButtonControls(!lbShow, "btnNew", "btnUpdate", "btnPrint", "btnVoid");

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

    private void initializeTableDetail() {
        if (laTransactionDetail == null) {
            laTransactionDetail = FXCollections.observableArrayList();

            tblViewDetails.setItems(laTransactionDetail);

            tblColDetailCost.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");
            tblColDetailOrderQty.setStyle("-fx-alignment: CENTER; -fx-padding: 0 5 0 0;");

            tblColDetailNo.setCellValueFactory((loModel) -> {
                int index = tblViewDetails.getItems().indexOf(loModel.getValue()) + 1;
                return new SimpleStringProperty(String.valueOf(index));
            });

            tblColDetailOrderNo.setCellValueFactory((loModel) -> new SimpleStringProperty(loModel.getValue().getOrderNo()));

            tblColDetailSerial.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().InventorySerial().getSerial01()
                            + " " + loModel.getValue().InventorySerial().getSerial02());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDetailBarcode.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().Inventory().getBarCode());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDetailDescr.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().Inventory().getDescription());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDetailBrand.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().Inventory().Brand().getDescription());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDetailVariant.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().Inventory().Variant().getDescription());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDetailCost.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(String.valueOf(loModel.getValue().Inventory().getCost()));
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDetailOrderQty.setCellValueFactory((loModel) -> new SimpleStringProperty(String.valueOf(loModel.getValue().getQuantity())));

        }
    }

    private void reloadTableDetail() {
        List<Model_Inventory_Transfer_Detail> rawDetail = poAppController.getDetailList();
        laTransactionDetail.setAll(rawDetail);

        // Restore or select last row
        int indexToSelect = (pnCTransactionDetail >= 0 && pnCTransactionDetail < laTransactionDetail.size())
                ? pnCTransactionDetail
                : laTransactionDetail.size() - 1;

        tblViewDetails.getSelectionModel().select(indexToSelect);

        pnCTransactionDetail = tblViewDetails.getSelectionModel().getSelectedIndex() + 1; // Not focusedIndex
        tblViewDetails.refresh();
    }

    private void getLoadedTransaction() {
        loadTransactionMaster();
        reloadTableDetail();
        loadSelectedDetail();
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

    private LocalDate ParseDate(Date date) {
        if (date == null) {
            return null;
        }
        Date loDate = new java.util.Date(date.getTime());
        return loDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
