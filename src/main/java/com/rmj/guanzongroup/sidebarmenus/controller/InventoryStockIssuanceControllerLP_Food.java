package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.controller.ScreenInterface;
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
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Master;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.inv.warehouse.t4.InventoryStockIssuance;
import ph.com.guanzongroup.cas.inv.warehouse.t4.constant.DeliveryIssuanceType;
import ph.com.guanzongroup.cas.inv.warehouse.t4.constant.InventoryStockIssuanceStatus;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Cluster_Delivery_Detail;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Cluster_Delivery_Master;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.Model_Inventory_Transfer_Detail;
import ph.com.guanzongroup.cas.inv.warehouse.t4.model.services.DeliveryIssuanceControllers;

/**
 * FXML Controller class
 *
 * @author User
 */
public class InventoryStockIssuanceControllerLP_Food implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private LogWrapper poLogWrapper;
    private String psFormName = "Issuance Entry";
    private String psIndustryID, psCompanyID, psCategoryID;
    private Control lastFocusedControl;
    private InventoryStockIssuance poAppController;
    private ObservableList<Model_Cluster_Delivery_Master> laTransactionMaster;
    private ObservableList<Model_Cluster_Delivery_Detail> laTransactionDetail;
    private ObservableList<Model_Inventory_Transfer_Detail> laTransactionDetailOther;
    private int pnTransactionStock, pnEditMode, pnTransactionDetail, pnTransactionDetailOther;

    @FXML
    private AnchorPane apMainAnchor;

    @FXML
    private AnchorPane apBrowse, apButton, apMaster, apMasterDelivery, apDetailDelivery, apDelivery, apStockMaster;

    @FXML
    private TextArea taRemarks, taDeliveryRemarks;

    @FXML
    private TextField tfSearchCluster, tfSearchTransNo, tfTransNo, tfPlateNo, tfDriver,
            tfAssistant1, tfAssistant2, tfClusterName, tfTownName, tfBranch,
            tfDelilveryTransNo, tfOrderNo, tfBarcode,
            tfDescription, tfSupersede, tfCost, tfIssuedQty, tfBrand, tfColor, tfMeasure, tfVariant, tfApprovedQty, tfModel, tfSerial, tfInvType, tfOrderQuantity;

    @FXML
    private Label lblSource;

    @FXML
    private Button btnBrowse, btnUpdateDelivery, btnSearch, btnUpdate, btnSave,
            btnCancel, btnHistory, btnRetrieve, btnClose,
            btnSaveDelivery, btnPrintDelivery, btnCancelDelivery, btnNew;

    @FXML
    private Label lblMainStatus, lblDeliveryStatus;
    @FXML
    private DatePicker dpTransactionDate, dpTransactionDeparture, dpDeliveryDate;
    @FXML
    private ComboBox cbDeliveryType;

    @FXML
    private TableView<Model_Cluster_Delivery_Detail> tblViewDeliveryTrans;

    @FXML
    private TableColumn<Model_Cluster_Delivery_Detail, String> tblColDelNo, tblColDelTransNo,
            tblColDelBranch, tblColDelDate, tblColDelItem, tblColDelStatus;

    @FXML
    private TableView<Model_Inventory_Transfer_Detail> tblViewDetails;

    @FXML
    private TableColumn<Model_Inventory_Transfer_Detail, String> tblColDetailNo, tblColDetailOrderNo, tblColDetailSerial,
            tblColDetailBarcode, tblColDetailDescr,
            tblColDetailBrand, tblColDetailVariant, tblColDetailSerialStatus, tblColDetailCost,
            tblColDetailOrderQty, tblColDetailApprovedQty, tblColDetailIssuedQty;

    @FXML
    private TableView<Model_Inv_Stock_Request_Master> tblViewStockMaster;

    @FXML
    private TableColumn<Model_Inv_Stock_Request_Master, String> tblColNo, tblColTransNo, tblColBranch,
            tblColTransDate, tblColItemQty;

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
            poAppController = new DeliveryIssuanceControllers(poApp, poLogWrapper).InventoryStockIssuance();
            poAppController.setTransactionStatus(InventoryStockIssuanceStatus.OPEN);

            //initlalize and validate transaction objects from class controller
            if (!isJSONSuccess(poAppController.initTransaction(), psFormName)) {
                unloadForm appUnload = new unloadForm();
                appUnload.unloadForm(apMainAnchor, poApp, psFormName);
            }

            //background thread
            Platform.runLater(() -> {
                poAppController.setTransactionStatus("0");
                //initialize logged in category
                poAppController.setIndustryID(psIndustryID);
                poAppController.setCompanyID(psCompanyID);
                poAppController.setCategoryID(psCategoryID);
                System.err.println("Initialize value : Industry >" + psIndustryID
                        + "\nCompany :" + psCompanyID
                        + "\nCategory:" + psCategoryID);

                btnNew.fire();
            });
            initializeTableDetail();
            initializeTableDetailOther();
            initControlEvents();
        } catch (SQLException | GuanzonException e) {
            Logger.getLogger(InventoryStockIssuance.class.getName()).log(Level.SEVERE, null, e);
            poLogWrapper.severe(psFormName + " :" + e.getMessage());
        }
    }

    @FXML
    void ontblMasterClicked(MouseEvent e) {
        pnTransactionStock = tblViewStockMaster.getSelectionModel().getSelectedIndex();
        if (pnTransactionStock < 0) {
            return;
        }

        if (e.getClickCount() == 2 && !e.isConsumed()) {

            try {
                e.consume();
                if (!isJSONSuccess(poAppController.requestDetail(pnTransactionStock),
                        "Add Stock Request Detail. ")) {
                    if (ShowMessageFX.OkayCancel(null, psFormName, "Selected Delivery is not yet Saved. Do you want to replace Transaction? ") == true) {
                        if (!isJSONSuccess(poAppController.replaceDetail(pnTransactionDetail, pnTransactionStock),
                                "Replace Stock Request Detail. ")) {
                            return;
                        }
                    }
                }
                reloadTableDetail();
                loadSelectedTransactionDetail(poAppController.getDetailCount());
                reloadTableDetailOther();
            } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                poLogWrapper.severe(psFormName + " :" + ex.getMessage());

            }

        }
        return;
    }

    @FXML
    void ontblDeliveryClicked(MouseEvent e
    ) {
        try {
            pnTransactionDetail = tblViewDeliveryTrans.getSelectionModel().getSelectedIndex() + 1;
            if (pnTransactionDetail <= 0) {
                return;
            }

            reloadTableDetail();
            loadSelectedTransactionDetail(pnTransactionDetail);
            reloadTableDetailOther();
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(InventoryStockIssuance_PostingController.class.getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }

    @FXML
    void ontblDetailClicked(MouseEvent e
    ) {
        try {
            pnTransactionDetailOther = tblViewDetails.getSelectionModel().getSelectedIndex() + 1;
            if (pnTransactionDetailOther <= 0) {
                return;
            }

            reloadTableDetail();
            loadSelectedTransactionDetailOther(pnTransactionDetailOther);
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(InventoryStockIssuance_PostingController.class.getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }

    @FXML
    private void cmdButton_Click(ActionEvent event
    ) {
        try {
            //get button id
            String btnID = ((Button) event.getSource()).getId();
            switch (btnID) {
                case "btnBrowse":
                    if (lastFocusedControl == null) {
                        ShowMessageFX.Information(null, psFormName,
                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
                        return;
                    }
                    switch (lastFocusedControl.getId()) {
                        case "tfSearchTransNo":
                            if (!isJSONSuccess(poAppController.searchTransaction(tfSearchTransNo.getText(), true, true),
                                    "Initialize Browse Transaction")) {
                                return;
                            }
                            getLoadedTransaction();
                            initButtonDisplay(poAppController.getEditMode());
                            break;

                        case "tfSearchCluster":
                            if (!isJSONSuccess(poAppController.searchTransaction(tfSearchCluster.getText(), true, false),
                                    "Initialize Browse Transaction")) {
                                return;
                            }
                            getLoadedTransaction();
                            initButtonDisplay(poAppController.getEditMode());
                            break;
                        default:
                            //Search record
                            if (!isJSONSuccess(poAppController.searchTransaction("", true, true),
                                    "Initialize Browse Transaction")) {
                                return;
                            }
                            getLoadedTransaction();
                            initButtonDisplay(poAppController.getEditMode());
                            break;
                    }
                case "btnSearch":
                    if (lastFocusedControl == null) {
                        ShowMessageFX.Information(null, psFormName,
                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
                        return;
                    }

                    switch (lastFocusedControl.getId()) {
                        case "tfClusterName":
                            if (poAppController.getStockMasterList().size() > 0) {
                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Cluster", "Transaction's already Retrieve. Do you want to reset Transaction? ") == false) {
                                    return;
                                }
                                //reset transaction
                                poAppController.NewTransaction();
                                clearAllInputs();
                                getLoadedTransaction();
                                pnEditMode = poAppController.getEditMode();
                            }
                            if (!isJSONSuccess(poAppController.searchTransactionCluster(tfClusterName.getText(), false),
                                    "Initialize Search Cluster! ")) {
                                return;
                            }
                            loadTransactionMaster();
                            break;

                        case "tfTownName":
                            if (!tfClusterName.getText().isEmpty()) {
                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Town", "Transaction's already Retrieve. Do you want to reset Transaction? ") == false) {
                                    return;
                                }
                            }
                            if (!isJSONSuccess(poAppController.searchTransactionTown(tfTownName.getText(), false),
                                    "Initialize Search Town Name! ")) {
                                return;
                            }
                            loadTransactionMaster();
                            //call reset incase
                            break;

                        case "tfPlateNo":
                            if (!isJSONSuccess(poAppController.searchTransactionPlate(tfPlateNo.getText(), false),
                                    "Initialize Search Plate! ")) {
                                return;
                            }
                            loadTransactionMaster();
                            break;

                        case "tfDriver":
                            if (!isJSONSuccess(poAppController.searchTransactionDriver(tfDriver.getText(), false),
                                    "Initialize Search Driver! ")) {
                                return;
                            }
                            loadTransactionMaster();
                            break;
                        case "tfAssistant1":
                            if (!isJSONSuccess(poAppController.searchTransactionAssistant01(tfAssistant1.getText(), false),
                                    "Initialize Search Assitant 1! ")) {
                                return;
                            }
                            loadTransactionMaster();
                            break;
                        case "tfAssistant2":
                            if (!isJSONSuccess(poAppController.searchTransactionAssistant02(tfAssistant2.getText(), false),
                                    "Initialize Search Assitant 2! ")) {
                                return;
                            }
                            loadTransactionMaster();
                            break;
                        case "tfSerial":
                            if (!isJSONSuccess(poAppController.searchDetailIssuanceSerial(pnTransactionDetail, pnTransactionDetailOther, tfSerial.getText(), true, true),
                                    "Initialize Search Serial")) {
                                return;
                            }
                    }
                    break;

                case "btnNew":
                    if (!isJSONSuccess(poAppController.NewTransaction(), "Initialize New Transaction")) {
                        return;
                    }
                    clearAllInputs();
                    getLoadedTransaction();
                    pnEditMode = poAppController.getEditMode();
                    break;

                case "btnUpdate":
                    if (poAppController.getMaster().getTransactionNo() == null || poAppController.getMaster().getTransactionNo().isEmpty()) {
                        ShowMessageFX.Information("Please load transaction before proceeding..", "Stock Request Issuance", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.UpdateTransaction(), "Initialize Update Transaction")) {
                        return;
                    }
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
                        poAppController = new DeliveryIssuanceControllers(poApp, poLogWrapper).InventoryStockIssuance();
                        poAppController.setTransactionStatus("0");

                        if (!isJSONSuccess(poAppController.initTransaction(), "Initialize Transaction")) {
                            unloadForm appUnload = new unloadForm();
                            appUnload.unloadForm(apMainAnchor, poApp, psFormName);
                        }

                        Platform.runLater(() -> {

                            poAppController.setTransactionStatus("0");
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
                    if (tfClusterName.getText().isEmpty()) {
                        ShowMessageFX.Information(null, psFormName,
                                "Please select a Cluster to Retrieve Stock Request");

                        return;
                    }

                    loadTransactionStockMasterList();
                    getLoadedTransaction();
                    initButtonDisplay(poAppController.getEditMode());
                    break;
                case "btnClose":
                    unloadForm appUnload = new unloadForm();
                    if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?")) {
                        appUnload.unloadForm(apMainAnchor, poApp, psFormName);
                    }
                    break;
                case "btnSaveDelivery":
                    if (tfDelilveryTransNo.getText().isEmpty()) {
                        ShowMessageFX.Information("No Delivery Selected..", "Stock Request Issuance", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.SaveTransactionDelivery(pnTransactionDetail), "Initialize Save Delivery Transaction")) {
                        return;
                    }
                    reloadTableDetail();
                    loadSelectedTransactionDetail(pnTransactionDetail);
                    reloadTableDetailOther();
                    pnEditMode = poAppController.getEditMode();

                    break;

                case "btnCancelDelivery":
                    if (tfDelilveryTransNo.getText().isEmpty()) {
                        ShowMessageFX.Information("No Delivery Selected..", "Stock Request Issuance", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.CancelTransactionDelivery(pnTransactionDetail), "Initialize Cancel Delivery Transaction")) {
                        return;
                    }
                    reloadTableDetail();
                    loadSelectedTransactionDetail(pnTransactionDetail);
//                    loadSelectedTransactionDetailOther(pnTransactionDetail);
                    reloadTableDetailOther();
//                    clearAllInputs();
                    pnEditMode = poAppController.getEditMode();

                    break;
                case "btnUpdateDelivery":
                    if (tfDelilveryTransNo.getText().isEmpty()) {
                        ShowMessageFX.Information("No Delivery Selected..", "Stock Request Issuance", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.getDetail(pnTransactionDetail).InventoryTransfer().UpdateTransaction(), "Initialize Cancel Delivery Transaction")) {
                        return;
                    }
                    reloadTableDetail();
                    loadSelectedTransactionDetail(pnTransactionDetail);
                    reloadTableDetailOther();
                    pnEditMode = poAppController.getEditMode();

                    break;
                case "btnPrintDelivery":
                    if (tfDelilveryTransNo.getText().isEmpty()) {
                        ShowMessageFX.Information("No Delivery Selected..", "Stock Request Issuance", "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.getDetail(pnTransactionDetail).InventoryTransfer().printRecord(), "Initialize Print Delivery Transaction")) {
                        return;
                    }
                    reloadTableDetail();
                    loadSelectedTransactionDetail(pnTransactionDetail);
//                    loadSelectedTransactionDetailOther(pnTransactionDetail);
                    reloadTableDetailOther();
//                    clearAllInputs();
                    pnEditMode = poAppController.getEditMode();

                    break;
            }

            initButtonDisplay(poAppController.getEditMode());

        } catch (Exception e) {
            Logger.getLogger(DeliverySchedule_EntryController.class.getName()).log(Level.SEVERE, null, e);
            poLogWrapper.severe(psFormName + " :" + e.getMessage());
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
//                    
                    case "tfIssuedQty":
                        if (poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetail(pnTransactionDetailOther).getStockId() == null
                                || poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetail(pnTransactionDetailOther).getStockId().isEmpty()) {
                            if (Double.parseDouble(tfIssuedQty.getText()) > 0.0) {
                                tfIssuedQty.setText("0.00");
                                loTextField.requestFocus();
                                ShowMessageFX.Information("Unable to set quantity! No Stock Invetory Detected", psFormName, null);
                            }
                            return;
                        }
                        double lnIssuedQty;
                        try {
                            lnIssuedQty = Double.parseDouble(lsValue);
                        } catch (NumberFormatException e) {
                            lnIssuedQty = 0.0; // default if parsing fails
                            poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetail(pnTransactionDetailOther).setQuantity(lnIssuedQty);
                            reloadTableDetail();
                            loadSelectedTransactionDetail(pnTransactionDetailOther);
                            loTextField.requestFocus();
                        }
                        if (lnIssuedQty < 0.00) {
                            return;
                        }
                        // check if serialized
                        if (poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetail(pnTransactionDetailOther).Inventory().isSerialized()) {
                            // must be whole number AND exactly 1
                            if (lnIssuedQty != 1 || lnIssuedQty % 1 != 0) {
                                ShowMessageFX.Information("Invalid quantity for serialized item", psFormName, null);
                                lnIssuedQty = 1; // force to 1
                                loTextField.setText("1");
                            }
                            if (poAppController.getDetail(pnTransactionDetail).InventoryTransfer()
                                    .getDetail(pnTransactionDetailOther).getSerialID() == null
                                    || poAppController.getDetail(pnTransactionDetail).InventoryTransfer()
                                            .getDetail(pnTransactionDetailOther).getSerialID().isEmpty()) {
                                //Search record and sepate the row
                                if (!isJSONSuccess(poAppController.searchDetailIssuanceSerial(pnTransactionDetail, pnTransactionDetailOther, tfSerial.getText(), true, true),
                                        "Initialize Search Serial")) {
                                    lnIssuedQty = 0;
                                    return;
                                }

                            }
                        }
                        if (lnIssuedQty > poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetail(pnTransactionDetailOther).InventoryStockRequest().getApproved()) {
                            lnIssuedQty = poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetail(pnTransactionDetailOther).InventoryStockRequest().getApproved();
                            ShowMessageFX.Information("Issued Quantity exceed Approved Detected", psFormName, null);
                            loTextField.setText(String.valueOf(lnIssuedQty));
                        }

                        poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetail(pnTransactionDetailOther).setQuantity(lnIssuedQty);

                        reloadTableDetail();
                        loadSelectedTransactionDetail(pnTransactionDetail);
                        reloadTableDetailOther();

                        break;
                }
            } else {
                loTextField.selectAll();
            }
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(InventoryStockIssuance_PostingController.class.getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
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
                            case "tfClusterName":
                                if (!tfClusterName.getText().isEmpty()) {
                                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Cluster", "Transaction's already Retrieve. Do you want to reset Transaction? ") == false) {
                                        return;
                                    }
                                }
                                if (!isJSONSuccess(poAppController.searchTransactionCluster(lsValue, false),
                                        "Initialize Search Cluster! ")) {
                                    return;
                                }
                                loadTransactionMaster();
                                //call reset incase
                                break;

                            case "tfTownName":
                                if (!tfClusterName.getText().isEmpty()) {
                                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Town", "Transaction's already Retrieve. Do you want to reset Transaction? ") == false) {
                                        return;
                                    }
                                }
                                if (!isJSONSuccess(poAppController.searchTransactionTown(lsValue, false),
                                        "Initialize Search Town Name! ")) {
                                    return;
                                }
                                loadTransactionMaster();
                                //call reset incase
                                break;

                            case "tfPlateNo":
                                if (!isJSONSuccess(poAppController.searchTransactionPlate(lsValue, false),
                                        "Initialize Search Plate! ")) {
                                    return;
                                }
                                loadTransactionMaster();
                                break;

                            case "tfDriver":
                                if (!isJSONSuccess(poAppController.searchTransactionDriver(lsValue, false),
                                        "Initialize Search Driver! ")) {
                                    return;
                                }
                                loadTransactionMaster();
                                break;
                            case "tfAssistant1":
                                if (!isJSONSuccess(poAppController.searchTransactionAssistant01(lsValue, false),
                                        "Initialize Search Assitant 1! ")) {
                                    return;
                                }
                                loadTransactionMaster();
                                break;
                            case "tfAssistant2":
                                if (!isJSONSuccess(poAppController.searchTransactionAssistant02(lsValue, false),
                                        "Initialize Search Assitant 2! ")) {
                                    return;
                                }
                                loadTransactionMaster();
                                break;
                            case "tfSerial":
                                //Search record and sepate the row
                                if (!isJSONSuccess(poAppController.searchDetailIssuanceSerial(pnTransactionDetail, pnTransactionDetailOther, tfSerial.getText(), true, true),
                                        "Initialize Search Serial")) {
                                    return;
                                }

                                break;
                            case "tfSearchTransNo":
                                //Search record and sepate the row
                                if (!isJSONSuccess(poAppController.searchTransaction(tfSearchTransNo.getText(), true, true),
                                        "Initialize Browse Transaction")) {
                                    return;
                                }
                                getLoadedTransaction();
                                initButtonDisplay(poAppController.getEditMode());
                                break;

                            case "tfSearchCluster":
                                //Search record and sepate the row
                                if (!isJSONSuccess(poAppController.searchTransaction(tfSearchCluster.getText(), true, false),
                                        "Initialize Browse Transaction")) {
                                    return;
                                }
                                getLoadedTransaction();
                                initButtonDisplay(poAppController.getEditMode());
                                break;
                        }
                }
            }
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }

    private double computeDiscount(double total, double discount) {
        double totalDisc = (discount / 100) * total;
        return totalDisc;
    }

    private void loadDeliveryTypes() {
        List<String> deliveryTypes = DeliveryIssuanceType.DeliveryType;
        cbDeliveryType.setItems(FXCollections.observableArrayList(deliveryTypes));
    }

    private void loadTransactionStockMasterList() {
        StackPane overlay = getOverlayProgress(apStockMaster);
        ProgressIndicator pi = (ProgressIndicator) overlay.getChildren().get(0);
        overlay.setVisible(true);
        pi.setVisible(true);

        Task<ObservableList<Model_Inv_Stock_Request_Master>> loadStockTransaction = new Task<ObservableList<Model_Inv_Stock_Request_Master>>() {
            @Override
            protected ObservableList<Model_Inv_Stock_Request_Master> call() throws Exception {
                if (!isJSONSuccess(poAppController.loadStockTransactionList(),
                        "Initialize : Load of Transaction List")) {
                    return null;
                }

                List<Model_Inv_Stock_Request_Master> rawList = poAppController.getStockMasterList();
                System.out.print("The size of list is " + rawList.size());
                return FXCollections.observableArrayList(new ArrayList<>(rawList));
            }

            @Override
            protected void succeeded() {
                ObservableList<Model_Inv_Stock_Request_Master> laMasterList = getValue();
                tblViewStockMaster.setItems(laMasterList);

                tblColNo.setCellValueFactory(loModel -> {
                    int index = tblViewStockMaster.getItems().indexOf(loModel.getValue()) + 1;
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
                tblColItemQty.setCellValueFactory(loModel -> {
                    return new SimpleStringProperty(String.valueOf(loModel.getValue().getEntryNo()));
                });

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
        Thread thread = new Thread(loadStockTransaction);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTransactionMaster() {
        try {
            lblSource.setText(poAppController.getMaster().Company().getCompanyName() == null ? "" : (poAppController.getMaster().Company().getCompanyName() + " - ")
                    + poAppController.getMaster().Industry().getDescription() == null ? "" : poAppController.getMaster().Industry().getDescription());
            lblMainStatus.setText(InventoryStockIssuanceStatus.STATUS.get(Integer.parseInt(poAppController.getMaster().getTransactionStatus())) == null ? "STATUS"
                    : InventoryStockIssuanceStatus.STATUS.get(Integer.parseInt(poAppController.getMaster().getTransactionStatus())));

            tfTransNo.setText(poAppController.getMaster().getTransactionNo());
            dpTransactionDate.setValue(ParseDate(poAppController.getMaster().getTransactionDate()));
            dpTransactionDeparture.setValue(ParseDate(poAppController.getMaster().getDepartreDate()));
            tfClusterName.setText(poAppController.getMaster().BranchCluster().getClusterDescription());
            tfTownName.setText(poAppController.getMaster().TownCity().getDescription());
            taRemarks.setText(poAppController.getMaster().getRemarks());
//            tfPlateNo.setText(poAppController.getMaster().getRemarks());
            tfDriver.setText(poAppController.getMaster().CompanyDriver().getCompanyName());
            tfAssistant1.setText(poAppController.getMaster().CompanyEmployee01().getCompanyName());
            tfAssistant2.setText(poAppController.getMaster().CompanyEmployee02().getCompanyName());

            cbDeliveryType.getSelectionModel().select(1);
        } catch (SQLException | GuanzonException e) {
            poLogWrapper.severe(psFormName, e.getMessage());
        }
    }

    private void loadSelectedTransactionDetail(int fnRow) throws SQLException, GuanzonException, CloneNotSupportedException {

        int tblIndex = fnRow - 1;
        tfDelilveryTransNo.setText(tblColDelTransNo.getCellData(tblIndex));
        tfBranch.setText(tblColDelBranch.getCellData(tblIndex));
        lblDeliveryStatus.setText(tblColDelStatus.getCellData(tblIndex));

        dpDeliveryDate.setValue(ParseDate(poAppController.getDetail(fnRow).InventoryTransfer().getMaster().getTransactionDate()));
        taDeliveryRemarks.setText(poAppController.getDetail(fnRow).InventoryTransfer().getMaster().getRemarks());
        initButtonDisplayDetail(fnRow, poAppController.getDetail(fnRow).InventoryTransfer().getMaster().getEditMode());
    }

    private void loadSelectedTransactionDetailOther(int fnRow) throws SQLException, GuanzonException, CloneNotSupportedException {

        int tblIndex = fnRow - 1;
        tfOrderNo.setText(tblColDetailOrderNo.getCellData(tblIndex));
        tfSerial.setText(tblColDetailSerial.getCellData(tblIndex));
        tfBarcode.setText(tblColDetailBarcode.getCellData(tblIndex));
        tfDescription.setText(tblColDetailDescr.getCellData(tblIndex));
        tfBrand.setText(tblColDetailBrand.getCellData(tblIndex));
        tfVariant.setText(tblColDetailVariant.getCellData(tblIndex));
        tfCost.setText(tblColDetailCost.getCellData(tblIndex));
        tfOrderQuantity.setText(tblColDetailOrderQty.getCellData(tblIndex));
        tfApprovedQty.setText(tblColDetailApprovedQty.getCellData(tblIndex));
        tfIssuedQty.setText(tblColDetailIssuedQty.getCellData(tblIndex));

        tfSupersede.setText(poAppController.getDetail(pnTransactionDetail).InventoryTransfer()
                .getDetail(fnRow).InventorySupersede().getBarCode());
        tfModel.setText(poAppController.getDetail(pnTransactionDetail).InventoryTransfer()
                .getDetail(fnRow).Inventory().Model().getDescription());
        tfColor.setText(poAppController.getDetail(pnTransactionDetail).InventoryTransfer()
                .getDetail(fnRow).Inventory().Color().getDescription());
        tfMeasure.setText(poAppController.getDetail(pnTransactionDetail).InventoryTransfer()
                .getDetail(fnRow).Inventory().Measure().getDescription());
        tfInvType.setText(poAppController.getDetail(pnTransactionDetail).InventoryTransfer()
                .getDetail(fnRow).Inventory().InventoryType().getDescription());

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
        loadDeliveryTypes();
        initButtonDisplay(poAppController.getEditMode());
    }

    private void initButtonDisplayDetail(int EntryNo, int fnEditMode) {

        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);
        // Show-only based on mode
        initButtonControls(lbShow, "btnSaveDelivery");
        initButtonControls(!lbShow, "btnUpdateDelivery", "btnPrintDelivery", "btnCancelDelivery");

        apDetailDelivery.setDisable(!lbShow);
    }

    private void initButtonDisplay(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);

        // Always show these buttons
        initButtonControls(true,  "btnRetrieve", "btnHistory", "btnClose");

        // Show-only based on mode
        initButtonControls(lbShow, "btnSearch", "btnSave", "btnCancel");
        initButtonControls(!lbShow, "btnBrowse","btnNew", "btnUpdate");

        apMaster.setDisable(!lbShow);
        apMasterDelivery.setDisable(!lbShow);
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

    private void initializeTableDetailOther() {
        if (laTransactionDetailOther == null) {
            laTransactionDetailOther = FXCollections.observableArrayList();

            tblViewDetails.setItems(laTransactionDetailOther);

            tblColDetailCost.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");
            tblColDetailOrderQty.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");
            tblColDetailApprovedQty.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");
            tblColDetailIssuedQty.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");

            tblColDetailNo.setCellValueFactory((loModel) -> {
                int index = tblViewDetails.getItems().indexOf(loModel.getValue()) + 1;
                return new SimpleStringProperty(String.valueOf(index));
            });

            tblColDetailOrderNo.setCellValueFactory((loModel) -> new SimpleStringProperty(loModel.getValue().getOrderNo()));

            tblColDetailSerial.setCellValueFactory(loModel -> {
                try {
                    String s1 = loModel.getValue().InventorySerial().getSerial01();
                    String s2 = loModel.getValue().InventorySerial().getSerial02();

                    String xserialname = (s1 != null && !s1.isEmpty() ? s1 : "")
                            + ((s1 != null && !s1.isEmpty() && s2 != null && !s2.isEmpty()) ? " / " : "")
                            + (s2 != null && !s2.isEmpty() ? s2 : "");

                    return new SimpleStringProperty(xserialname);
                } catch (SQLException | GuanzonException ex) {
                    Logger.getLogger(InventoryStockIssuance_PostingController.class
                            .getName()).log(Level.SEVERE, null, ex);
                    poLogWrapper.severe(psFormName + " :" + ex.getMessage());
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

            tblColDetailSerialStatus.setCellValueFactory(loModel -> {
                try {
                    boolean serialized = loModel.getValue().Inventory().isSerialized();
                    return new SimpleStringProperty(serialized ? "Yes" : "No");
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

            tblColDetailOrderQty.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(String.valueOf(loModel.getValue().InventoryStockRequest().getQuantity()));
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });
            tblColDetailApprovedQty.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(String.valueOf(loModel.getValue().InventoryStockRequest().getApproved() - loModel.getValue().InventoryStockRequest().getIssued()));
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });
            tblColDetailIssuedQty.setCellValueFactory((loModel) -> new SimpleStringProperty(String.valueOf(loModel.getValue().getQuantity())));

        }
    }

    private void reloadTableDetailOther()
            throws SQLException, CloneNotSupportedException, GuanzonException {
        List<Model_Inventory_Transfer_Detail> rawDetail = poAppController.getDetail(pnTransactionDetail).InventoryTransfer().getDetailList();
        laTransactionDetailOther.setAll(rawDetail);

        // Restore or select last row
        int indexToSelect = (pnTransactionDetailOther >= 1 && pnTransactionDetailOther < laTransactionDetailOther.size())
                ? pnTransactionDetailOther - 1
                : laTransactionDetailOther.size() - 1;

        tblViewDetails.getSelectionModel().select(indexToSelect);

        pnTransactionDetailOther = tblViewDetails.getSelectionModel().getSelectedIndex() + 1; // Not focusedIndex
        tblViewDetails.refresh();
        loadSelectedTransactionDetailOther(pnTransactionDetailOther);

    }

    private void initializeTableDetail() {
        if (laTransactionDetail == null) {
            laTransactionDetail = FXCollections.observableArrayList();

            tblViewDeliveryTrans.setItems(laTransactionDetail);

            tblColDelItem.setStyle("-fx-alignment: CENTER; -fx-padding: 0 5 0 0;");

            tblColDelNo.setCellValueFactory((loModel) -> {
                int index = tblViewDeliveryTrans.getItems().indexOf(loModel.getValue()) + 1;
                return new SimpleStringProperty(String.valueOf(index));
            });

            tblColDelTransNo.setCellValueFactory((loModel) -> new SimpleStringProperty(loModel.getValue().getReferNo()));
            tblColDelBranch.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().Branch().getBranchName());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDelDate.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(SQLUtil.dateFormat(
                            loModel.getValue().InventoryTransfer().getMaster().getTransactionDate(), SQLUtil.FORMAT_LONG_DATE));

                } catch (SQLException | GuanzonException | CloneNotSupportedException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });
            tblColDelItem.setCellValueFactory((loModel) -> new SimpleStringProperty(String.valueOf(loModel.getValue().getNoOfItem())));

            tblColDelStatus.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(
                            InventoryStockIssuanceStatus.STATUS.get(
                                    Integer.parseInt(loModel.getValue().InventoryTransfer().getMaster().getTransactionStatus())));
                } catch (SQLException | GuanzonException | CloneNotSupportedException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

        }
    }

    private void reloadTableDetail() {
        List<Model_Cluster_Delivery_Detail> rawDetail = poAppController.getDetailList();
        laTransactionDetail.setAll(rawDetail);

        // Restore or select last row
        int indexToSelect = (pnTransactionDetail >= 1 && pnTransactionDetail < laTransactionDetail.size())
                ? pnTransactionDetail - 1
                : laTransactionDetail.size() - 1;

        tblViewDeliveryTrans.getSelectionModel().select(indexToSelect);

        pnTransactionDetail = tblViewDeliveryTrans.getSelectionModel().getSelectedIndex() + 1; // Not focusedIndex
        tblViewDeliveryTrans.refresh();
    }

    private void getLoadedTransaction() throws SQLException, GuanzonException, CloneNotSupportedException {
        clearAllInputs();
        loadTransactionMaster();
        reloadTableDetail();
        loadSelectedTransactionDetail(pnTransactionDetail);
        reloadTableDetailOther();
    }

    private boolean isJSONSuccess(JSONObject loJSON, String fsModule) {
        String result = (String) loJSON.get("result");
        if ("error".equals(result)) {
            String message = (String) loJSON.get("message");
            poLogWrapper.severe(psFormName + " :" + message);
            Platform.runLater(() -> {
                if (message != null) {
                    ShowMessageFX.Warning(null, psFormName, fsModule + ": " + message);
                }
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
