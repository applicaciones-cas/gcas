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
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.cas.inv.model.Model_Inv_Master;
import org.guanzon.cas.inv.model.Model_Inventory;
import org.guanzon.cas.purchasing.model.Model_PO_Master;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.purchasing.module.mnv.POCancellation;
import ph.com.guanzongroup.cas.purchasing.module.mnv.constant.POCancellationStatus;
import ph.com.guanzongroup.cas.purchasing.module.mnv.models.Model_PO_Cancellation_Detail;
import ph.com.guanzongroup.cas.purchasing.module.mnv.services.POController;

/**
 * FXML Controller class
 *
 * @author User
 */
public class POCancellation_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private LogWrapper poLogWrapper;
    private String psFormName = "PO Cancellation Entry";
    private String psIndustryID;
    private String psCompanyID;
    private String psCategoryID;
    private Control lastFocusedControl;
    private POCancellation poAppController;
    private ObservableList<Model_PO_Master> laPurchaseOrder;
    private ObservableList<Model_PO_Cancellation_Detail> laTransactionDetail;
    private int pnSelectMaster, pnEditMode, pnTransactionDetail;

    private unloadForm poUnload = new unloadForm();

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apMaster, apDetail,
            apButton, apCenter, apTransaction;

    @FXML
    private TextField tfSearchTransaction, tfSearchSupplier, tfSearchReferNo,
            tfTransactionNo, tfSupplier, tfReferenceNo, tfDestination, tfTransactionAmount,
            tfCancelAmount, tfBarcode, tfDescription, tfBrand, tfModel, tfColor, tfCost, tfOrderQty,
            tfServedQty, tfCanceledQty, tfClass, tfAMC, tfROQ, tfCategory, tfInventoryType, tfMeasure,
            tfQuantity;

    @FXML
    private DatePicker dpTransactionDate, dpReferenceDate;

    @FXML
    private Label lblSource, lblStatus;

    @FXML
    private Button btnSearch, btnBrowse, btnNew, btnCancel, btnTag, btnHistory, btnUpdate, btnSave,
            btnRetrieve, btnClose;

    @FXML
    private TextArea taRemarks;

    @FXML
    private TableView<Model_PO_Cancellation_Detail> tblViewDetails;

    @FXML
    private TableColumn<Model_PO_Cancellation_Detail, String> tblColDetailNo, tblColDetailBarcode, tblColDetailDescription,
            tblColDetailOrderQty, tblColDetailCancelQty, tblColDetailCost, tblColDetailTotal;

    @FXML
    private TableView<Model_PO_Master> tblTransaction;

    @FXML
    private TableColumn<Model_PO_Master, String> tblColNo, tblColTransactionNo, tblColDate, tblColReference, tblColItems;

    @FXML
    private Pagination pgTransaction;

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
            poAppController = new POController(poApp, poLogWrapper).POCancellation();
            poAppController.setTransactionStatus(POCancellationStatus.OPEN);

            //initlalize and validate transaction objects from class controller
            if (!isJSONSuccess(poAppController.initTransaction(), psFormName)) {
                unloadForm appUnload = new unloadForm();
                appUnload.unloadForm(apMainAnchor, poApp, psFormName);
            }

            //background thread
            Platform.runLater(() -> {
                poAppController.setTransactionStatus("07");
                //initialize logged in category
                poAppController.setIndustryID(psIndustryID);
                System.err.println("Initialize value : Industry >" + psIndustryID);

                btnNew.fire();
            });
            initializeTableDetail();
            initializeTablePurchase();
            initControlEvents();
        } catch (SQLException | GuanzonException e) {
            Logger.getLogger(POCancellation_EntryController.class.getName()).log(Level.SEVERE, null, e);
            poLogWrapper.severe(psFormName + " :" + e.getMessage());
        }
    }

    @FXML
    void tblTransaction_MouseClicked(MouseEvent e) {
        pnSelectMaster = tblTransaction.getSelectionModel().getSelectedIndex();
        if (pnSelectMaster < 0) {
            return;
        }

        if (e.getClickCount() == 2 && !e.isConsumed()) {
            try {
                if (poAppController.getMaster().getSourceNo() != null || !poAppController.getMaster().getSourceNo().isEmpty()) {
                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
                        return;
                    }
                }
                e.consume();
                if (!isJSONSuccess(poAppController.replaceDetail(pnSelectMaster), psFormName)) {
//                    ShowMessageFX.Information("Failed to add detail", psFormName, null);
                    return;
                }

                getLoadedTransaction();
                initButtonDisplay(poAppController.getEditMode());
            } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {

                poLogWrapper.severe(psFormName + " :" + ex.getMessage());

            }

        }
        return;
    }

    @FXML
    void ontblDetailClicked(MouseEvent e) {
        try {
            pnTransactionDetail = tblViewDetails.getSelectionModel().getSelectedIndex() + 1;
            if (pnTransactionDetail <= 0) {
                return;
            }

            loadSelectedTransactionDetail(pnTransactionDetail);
        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        try {
            //get button id
            String btnID = ((Button) event.getSource()).getId();
            switch (btnID) {
//                case "btnSearch":
//                    if (lastFocusedControl == null) {
//                        ShowMessageFX.Information(null, psFormName,
//                                "Search unavailable. Please ensure a searchable field is selected or focused before proceeding..");
//                        return;
//                    }

//                    switch (lastFocusedControl.getId()) {
//                        case "tfDestination":
//                            if (!isJSONSuccess(poAppController.searchTransactionDestination(tfDestination.getText().trim(), false),
//                                    "Initialize Search Destination! ")) {
//                                return;
//                            }
//                            loadTransactionMaster();
//                            break;
//                        case "tfDepartment":
//                            if (!isJSONSuccess(poAppController.searchTransactionDepartment(tfDepartment.getText().trim(), false),
//                                    "Initialize Search Department! ")) {
//                                return;
//                            }
//                            loadTransactionMaster();
//                            break;
//                        case "tfCheckTransNo":
//                            if (!isJSONSuccess(poAppController.searchDetailByCheck(pnTransactionDetail, tfCheckTransNo.getText(), true),
//                                    "Initialize Search Check! ")) {
//                                return;
//                            }
//                            reloadTableDetail();
//                            loadSelectedTransactionDetail(pnTransactionDetail);
//                            break;
//                        case "tfCheckNo":
//                            if (!isJSONSuccess(poAppController.searchDetailByCheck(pnTransactionDetail, tfCheckNo.getText(), false),
//                                    "Initialize Search Check! ")) {
//                                return;
//                            }
//                            reloadTableDetail();
//                            loadSelectedTransactionDetail(pnTransactionDetail);
//                            break;
//                        case "tfFilterBank":
//                            if (!isJSONSuccess(poAppController.searchTransactionBankFilter(tfFilterBank.getText(), false),
//                                    "Initialize Search Check! ")) {
//                                return;
//                            }
//                            loadRetrieveFilter();
//                            break;
//
//                    }
//                    break;
//
//                case "btnBrowse":
//                    if (lastFocusedControl == null) {
//                        if (!tfTransactionNo.getText().isEmpty()) {
//                            if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                return;
//                            }
//                        }
//                        if (!isJSONSuccess(poAppController.searchTransaction(tfSearchTransNo.getText(), true, true),
//                                "Initialize Search Source No! ")) {
//                            return;
//                        }
//
//                        getLoadedTransaction();
//                        initButtonDisplay(poAppController.getEditMode());
//                        break;
//                    }
//
//                    switch (lastFocusedControl.getId()) {
//                        case "tfSearchTransNo":
//                            if (!tfTransactionNo.getText().isEmpty()) {
//                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                    return;
//                                }
//                            }
//                            if (!isJSONSuccess(poAppController.searchTransaction(tfSearchTransNo.getText(), true, true),
//                                    "Initialize Search Source No! ")) {
//                                return;
//                            }
//
//                            getLoadedTransaction();
//                            initButtonDisplay(poAppController.getEditMode());
//                            break;
//                        case "tfSearchDestination":
//                            if (!tfTransactionNo.getText().isEmpty()) {
//                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                    return;
//                                }
//                            }
//                            if (!isJSONSuccess(poAppController.searchTransaction(tfSearchDestination.getText(), false),
//                                    "Initialize Search Transaction! ")) {
//                                return;
//                            }
//
//                            getLoadedTransaction();
//                            initButtonDisplay(poAppController.getEditMode());
//                            break;
//                        case "dpSearchTransactionDate":
//                            if (!tfTransactionNo.getText().isEmpty()) {
//                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                    return;
//                                }
//                            }
//                            if (!isJSONSuccess(poAppController.searchTransaction(String.valueOf(dpSearchTransactionDate.getValue()), false),
//                                    "Initialize Search Transaction! ")) {
//                                return;
//                            }
//
//                            getLoadedTransaction();
//                            initButtonDisplay(poAppController.getEditMode());
//                            break;
//                        default:
//                            if (!tfTransactionNo.getText().isEmpty()) {
//                                if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                    return;
//                                }
//                            }
//                            if (!isJSONSuccess(poAppController.searchTransaction(tfSearchTransNo.getText(), true, true),
//                                    "Initialize Search Source No! ")) {
//                                return;
//                            }
//
//                            getLoadedTransaction();
//                            initButtonDisplay(poAppController.getEditMode());
//                            break;
//                    }
//                    break;
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
                        ShowMessageFX.Information("Please load transaction before proceeding..", psFormName, "");
                        return;
                    }
                    poAppController.OpenTransaction(poAppController.getMaster().getTransactionNo());
                    if (!isJSONSuccess(poAppController.UpdateTransaction(), "Initialize UPdate Transaction")) {
                        return;
                    }
                    getLoadedTransaction();
                    pnEditMode = poAppController.getEditMode();
                    break;

                case "btnSave":
                    if (tfTransactionNo.getText().isEmpty()) {
                        ShowMessageFX.Information("Please load transaction before proceeding..", psFormName, "");
                        return;
                    }

                    if (!isJSONSuccess(poAppController.SaveTransaction(), "Initialize Save Transaction")) {
                        return;
                    }

                    if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to Confirm transaction?") == true) {
                        if (!isJSONSuccess(poAppController.CloseTransaction(), "Initialize Close Transaction")) {
                            return;
                        }

                    }

                    getLoadedTransaction();
                    pnEditMode = poAppController.getEditMode();

                    break;

                case "btnCancel":
                    if (ShowMessageFX.OkayCancel(null, psFormName, "Do you want to disregard changes?") == true) {
                        poAppController = new POController(poApp, poLogWrapper).POCancellation();
                        poAppController.setTransactionStatus("07");

                        if (!isJSONSuccess(poAppController.initTransaction(), "Initialize Transaction")) {
                            unloadForm appUnload = new unloadForm();
                            appUnload.unloadForm(apMainAnchor, poApp, psFormName);
                        }

                        Platform.runLater(() -> {

                            poAppController.setTransactionStatus("07");
                            poAppController.getMaster().setIndustryId(psIndustryID);
                            poAppController.setIndustryID(psIndustryID);

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
//                    loadRetrieveFilter();
//                    loadTransactionCheckList(String.valueOf(dpFilterFrom.getValue()), String.valueOf(dpFilterThru.getValue()));

                    break;
                case "btnClose":
                    if (ShowMessageFX.YesNo("Are you sure you want to close this form?", psFormName, null)) {
                        if (poUnload != null) {
                            poUnload.unloadForm(apMainAnchor, poApp, psFormName);
                        } else {
                            ShowMessageFX.Warning("Please notify the system administrator to configure the null value at the close button.", "Warning", null);
                        }
                    }
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
//        try {
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
//        } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
//            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
//        }
    };
    private final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea loTextField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTextFieldID = loTextField.getId();
        String lsValue = loTextField.getText();
        if (lsValue == null) {
            return;
        }

        if (!nv) {
            /*Lost Focus*/
            switch (lsTextFieldID) {

                case "taRemarks":
                    poAppController.getMaster().setRemarks(lsValue);
                    loadTransactionMaster();

                    break;

            }
        } else {
            loTextField.selectAll();
        }

    };

    private void txtArea_KeyPressed(KeyEvent event) {
        TextArea loTxtField = (TextArea) event.getSource();
        String txtFieldID = ((TextArea) event.getSource()).getId();
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
                    case UP:
                        CommonUtils.SetPreviousFocus((TextField) event.getSource());
                        return;
                    case DOWN:
                        CommonUtils.SetNextFocus(loTxtField);
                        return;

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
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
//                            case "tfSearchTransNo":
//                                if (!tfTransactionNo.getText().isEmpty()) {
//                                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                        return;
//                                    }
//                                }
//                                if (!isJSONSuccess(poAppController.searchTransaction(tfSearchTransNo.getText(), true, true),
//                                        "Initialize Search Source No! ")) {
//                                    return;
//                                }
//
//                                getLoadedTransaction();
//                                initButtonDisplay(poAppController.getEditMode());
//                                break;
//                            case "tfSearchDestination":
//                                if (!tfTransactionNo.getText().isEmpty()) {
//                                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                        return;
//                                    }
//                                }
//                                if (!isJSONSuccess(poAppController.searchTransaction(tfSearchDestination.getText(), true, false),
//                                        "Initialize Search Transaction! ")) {
//                                    return;
//                                }
//                                getLoadedTransaction();
//                                initButtonDisplay(poAppController.getEditMode());
//                                break;
//                            case "dpSearchTransactionDate":
//                                if (!tfTransactionNo.getText().isEmpty()) {
//                                    if (ShowMessageFX.OkayCancel(null, "Search Transaction! by Trasaction", "Are you sure you want replace loaded Transaction?") == false) {
//                                        return;
//                                    }
//                                }
//                                if (!isJSONSuccess(poAppController.searchTransaction(String.valueOf(dpSearchTransactionDate.getValue()), false, false),
//                                        "Initialize Search Transaction! ")) {
//                                    return;
//                                }
//                                getLoadedTransaction();
//                                initButtonDisplay(poAppController.getEditMode());
//                                break;
//                            case "tfDestination":
//                                if (!isJSONSuccess(poAppController.searchTransactionDestination(tfDestination.getText(), false),
//                                        "Initialize Search Destination! ")) {
//                                    return;
//                                }
//                                loadTransactionMaster();
//                                break;
//                            case "tfDepartment":
//                                if (!isJSONSuccess(poAppController.searchTransactionDepartment(tfDepartment.getText().trim(), false),
//                                        "Initialize Search Department! ")) {
//                                    return;
//                                }
//                                loadTransactionMaster();
//                                break;
//                            case "tfCheckTransNo":
//                                if (!isJSONSuccess(poAppController.searchDetailByCheck(pnTransactionDetail, tfCheckTransNo.getText(), true),
//                                        "Initialize Search Check! ")) {
//                                    return;
//                                }
//                                reloadTableDetail();
//                                loadSelectedTransactionDetail(pnTransactionDetail);
//                                break;
//                            case "tfCheckNo":
//                                if (!isJSONSuccess(poAppController.searchDetailByCheck(pnTransactionDetail, tfCheckNo.getText(), false),
//                                        "Initialize Search Check! ")) {
//                                    return;
//                                }
//                                reloadTableDetail();
//                                loadSelectedTransactionDetail(pnTransactionDetail);
//                                break;
//
//                            case "tfFilterBank":
//                                if (!isJSONSuccess(poAppController.searchTransactionBankFilter(tfFilterBank.getText() != null ? tfFilterBank.getText() : "", false),
//                                        "Initialize Search Check! ")) {
//                                    return;
//                                }
//                                loadRetrieveFilter();
//                                break;
                        }
                    case UP:
                        CommonUtils.SetPreviousFocus((TextField) event.getSource());
                        return;
                    case DOWN:
                        CommonUtils.SetNextFocus(loTxtField);
                        return;

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(DeliverySchedule_EntryController.class
                    .getName()).log(Level.SEVERE, null, ex);
            poLogWrapper.severe(psFormName + " :" + ex.getMessage());
        }
    }

    private void loadTransactionPurchaseList(String fsColumn, String fsValue) {
        StackPane overlay = getOverlayProgress(apTransaction);
        ProgressIndicator pi = (ProgressIndicator) overlay.getChildren().get(0);
        overlay.setVisible(true);
        pi.setVisible(true);

        Task<ObservableList<Model_PO_Master>> loadCheckPayment = new Task<ObservableList<Model_PO_Master>>() {
            @Override
            protected ObservableList<Model_PO_Master> call() throws Exception {
                if (!isJSONSuccess(poAppController.loadPurchaseOrderList(fsColumn, fsValue),
                        "Initialize : Load of Transaction List")) {
                    return null;
                }

                List<Model_PO_Master> rawList = poAppController.getPurchaseOrderList();
                System.out.print("The size of list is " + rawList.size());
                return FXCollections.observableArrayList(new ArrayList<>(rawList));
            }

            @Override
            protected void succeeded() {
                reloadTablePurchase();
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
        Thread thread = new Thread(loadCheckPayment);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadTransactionMaster() {
        try {
            lblSource.setText(poAppController.getMaster().Industry().getDescription() == null ? "" : poAppController.getMaster().Industry().getDescription());
            lblStatus.setText(POCancellationStatus.STATUS.get(Integer.parseInt(poAppController.getMaster().getTransactionStatus())) == null ? "STATUS"
                    : POCancellationStatus.STATUS.get(Integer.parseInt(poAppController.getMaster().getTransactionStatus())));

            tfTransactionNo.setText(poAppController.getMaster().getTransactionNo());
            dpTransactionDate.setValue(ParseDate(poAppController.getMaster().getTransactionDate()));
            dpReferenceDate.setValue(ParseDate(poAppController.getMaster().PurchaseOrderMaster().getTransactionDate()));
            tfReferenceNo.setText(poAppController.getMaster().PurchaseOrderMaster().getReference());
            tfSupplier.setText(poAppController.getMaster().PurchaseOrderMaster().Supplier().getCompanyName());
            tfDestination.setText(poAppController.getMaster().Branch().getBranchName());
            taRemarks.setText(String.valueOf(poAppController.getMaster().getRemarks()));
            tfTransactionAmount.setText(CommonUtils.NumberFormat(poAppController.getMaster().PurchaseOrderMaster().getTranTotal(), "###,##0.0000"));
            tfCancelAmount.setText(CommonUtils.NumberFormat(poAppController.getMaster().getTransactionTotal(), "###,##0.0000"));
        } catch (SQLException | GuanzonException e) {
            poLogWrapper.severe(psFormName, e.getMessage());
        }
    }

    private void loadSelectedTransactionDetail(int fnRow) throws SQLException, GuanzonException, CloneNotSupportedException {

        int tblIndex = fnRow - 1;
        tfBarcode.setText(tblColDetailBarcode.getCellData(tblIndex));
        tfDescription.setText(tblColDetailDescription.getCellData(tblIndex));
        tfOrderQty.setText(tblColDetailOrderQty.getCellData(tblIndex));
        tfQuantity.setText(tblColDetailCancelQty.getCellData(tblIndex));
        tfCost.setText(tblColDetailCost.getCellData(tblIndex));

        Model_Inventory loDetailInventory = poAppController.getDetail(fnRow).Inventory();
        Model_Inv_Master loDetailInventoryMaster = poAppController.getDetail(fnRow).InventoryMaster();
        tfCanceledQty.setText(String.valueOf(poAppController.getDetail(fnRow).PurchaseOrderDetail().getCancelledQuantity()));
        tfBrand.setText(loDetailInventory.Brand().getDescription());
        tfModel.setText(loDetailInventory.Model().getDescription());
        tfColor.setText(loDetailInventory.Model().getDescription());
        tfCategory.setText(loDetailInventory.Category().getDescription());
        tfInventoryType.setText(loDetailInventory.InventoryType().getDescription());
        tfMeasure.setText(loDetailInventory.Measure().getDescription());
        tfClass.setText(loDetailInventoryMaster.getInventoryClassification());
        tfAMC.setText(loDetailInventory.Model().getDescription());
        tfServedQty.setText(loDetailInventory.Model().getDescription());
        recomputeTotal();
    }

    private void recomputeTotal() throws SQLException, GuanzonException {
        double lnTotal = 0.00;
        for (int lnCtr = 1; lnCtr <= poAppController.getDetailCount(); lnCtr++) {
            if (poAppController.getDetail(lnCtr).getOrderNo() == null || poAppController.getDetail(lnCtr).getOrderNo().isEmpty()) {
                continue;
            }
            lnTotal = lnTotal + poAppController.getDetail(lnCtr).getUnitPrice();
        }
        poAppController.getMaster().setTransactionTotal(lnTotal);
        tfTransactionAmount.setText(CommonUtils.NumberFormat(poAppController.getMaster().PurchaseOrderMaster().getTranTotal(), "###,##0.0000"));
        tfCancelAmount.setText(CommonUtils.NumberFormat(poAppController.getMaster().getTransactionTotal(), "###,##0.0000"));
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
            } else if (loControl instanceof TextArea) {
                TextArea loControlField = (TextArea) loControl;
                controllerFocusTracker(loControlField);
                loControlField.setOnKeyPressed(this::txtArea_KeyPressed);
                loControlField.focusedProperty().addListener(txtArea_Focus);
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

    }

    private void initButtonDisplay(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.ADDNEW || fnEditMode == EditMode.UPDATE);

        // Always show these buttons
        initButtonControls(true, "btnRetrieve", "btnHistory", "btnClose");

        // Show-only based on mode
        initButtonControls(lbShow, "btnSearch", "btnSave", "btnTag", "btnCancel");
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

    private void initializeTableDetail() {
        if (laTransactionDetail == null) {
            laTransactionDetail = FXCollections.observableArrayList();

            tblViewDetails.setItems(laTransactionDetail);

            tblColDetailOrderQty.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");
            tblColDetailCancelQty.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");
            tblColDetailCost.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");
            tblColDetailTotal.setStyle("-fx-alignment: CENTER-RIGHT; -fx-padding: 0 5 0 0;");

            tblColDetailNo.setCellValueFactory((loModel) -> {
                int index = tblViewDetails.getItems().indexOf(loModel.getValue()) + 1;
                return new SimpleStringProperty(String.valueOf(index));
            });

            tblColDetailBarcode.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().Inventory().getBarCode());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });

            tblColDetailDescription.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(loModel.getValue().Inventory().getDescription());
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("");
                }
            });
            tblColDetailOrderQty.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(CommonUtils.NumberFormat(loModel.getValue().PurchaseOrderDetail().getQuantity(), "###,##0.0000"));
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("0.0000");
                }
            });
            tblColDetailOrderQty.setCellValueFactory((loModel) -> {
                try {
                    return new SimpleStringProperty(CommonUtils.NumberFormat(loModel.getValue().PurchaseOrderDetail().getQuantity(), "###,##0.0000"));
                } catch (SQLException | GuanzonException e) {
                    poLogWrapper.severe(psFormName, e.getMessage());
                    return new SimpleStringProperty("0.0000");
                }
            });
            tblColDetailCancelQty.setCellValueFactory((loModel) -> {
                return new SimpleStringProperty(CommonUtils.NumberFormat(loModel.getValue().getQuantity(), "###,##0.0000"));
            });

            tblColDetailCost.setCellValueFactory((loModel) -> {
                return new SimpleStringProperty(CommonUtils.NumberFormat(loModel.getValue().getUnitPrice(), "###,##0.0000"));
            });
            tblColDetailTotal.setCellValueFactory((loModel) -> {
                return new SimpleStringProperty(CommonUtils.NumberFormat(loModel.getValue().getUnitPrice() * loModel.getValue().getQuantity(), "###,##0.0000"));
            });

        }
    }

    private void reloadTableDetail() {
        List<Model_PO_Cancellation_Detail> rawDetail = poAppController.getDetailList();
        laTransactionDetail.setAll(rawDetail);

        // Restore or select last row
        int indexToSelect = (pnTransactionDetail >= 1 && pnTransactionDetail < laTransactionDetail.size())
                ? pnTransactionDetail - 1
                : laTransactionDetail.size() - 1;

        tblViewDetails.getSelectionModel().select(indexToSelect);

        pnTransactionDetail = tblViewDetails.getSelectionModel().getSelectedIndex() + 1; // Not focusedIndex
        tblViewDetails.refresh();
    }

    private void initializeTablePurchase() {
        if (laPurchaseOrder == null) {
            laPurchaseOrder = FXCollections.observableArrayList();

            tblTransaction.setItems(laPurchaseOrder);

            tblColNo.setCellValueFactory(loModel -> {
                int index = tblTransaction.getItems().indexOf(loModel.getValue()) + 1;
                return new SimpleStringProperty(String.valueOf(index));
            });
            tblColTransactionNo.setCellValueFactory(loModel -> {
                return new SimpleStringProperty(String.valueOf(loModel.getValue().getTransactionNo()));
            });
            tblColDate.setCellValueFactory(loModel -> {
                return new SimpleStringProperty(String.valueOf(loModel.getValue().getTransactionDate()));
            });
            tblColReference.setCellValueFactory(loModel -> {
                return new SimpleStringProperty(String.valueOf(loModel.getValue().getReference()));
            });
            tblColItems.setCellValueFactory(loModel -> {
                return new SimpleStringProperty(CommonUtils.NumberFormat(loModel.getValue().getEntryNo(), "###,##0.00"));

            });

        }
    }

    private void reloadTablePurchase() {
        List<Model_PO_Master> rawDetail = poAppController.getPurchaseOrderList();
        laPurchaseOrder.setAll(rawDetail);

        // Restore or select last row
        int indexToSelect = (pnSelectMaster >= 1 && pnSelectMaster < laPurchaseOrder.size())
                ? pnSelectMaster
                : laPurchaseOrder.size();

        tblTransaction.getSelectionModel().select(indexToSelect);

        pnSelectMaster = tblTransaction.getSelectionModel().getSelectedIndex(); // Not focusedIndex
        tblTransaction.refresh();
    }

    private void getLoadedTransaction() throws SQLException, GuanzonException, CloneNotSupportedException {
//        clearAllInputs();
        loadTransactionMaster();
        reloadTableDetail();
        loadSelectedTransactionDetail(pnTransactionDetail);
    }

    private boolean isJSONSuccess(JSONObject loJSON, String fsModule) {
        String result = (String) loJSON.get("result");
        String message = (String) loJSON.get("message");

        System.out.println("isJSONSuccess called. Thread: " + Thread.currentThread().getName());

        if ("error".equalsIgnoreCase(result)) {
            poLogWrapper.severe(psFormName + " : " + message);
            if (message != null && !message.trim().isEmpty()) {
                if (Platform.isFxApplicationThread()) {
                    ShowMessageFX.Warning(null, psFormName, fsModule + ": " + message);
                } else {
                    Platform.runLater(() -> ShowMessageFX.Warning(null, psFormName, fsModule + ": " + message));
                }
            }
            return false;
        }

        if ("success".equalsIgnoreCase(result)) {
            if (message != null && !message.trim().isEmpty()) {
                if (Platform.isFxApplicationThread()) {
                    ShowMessageFX.Information(null, psFormName, fsModule + ": " + message);
                } else {
                    Platform.runLater(() -> ShowMessageFX.Information(null, psFormName, fsModule + ": " + message));
                }
            }
            poLogWrapper.info(psFormName + " : Success on " + fsModule);
            return true;
        }

        // Unknown or null result
        poLogWrapper.warning(psFormName + " : Unrecognized result: " + result);
        return false;
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
