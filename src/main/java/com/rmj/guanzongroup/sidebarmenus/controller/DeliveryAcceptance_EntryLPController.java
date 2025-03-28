/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import static com.rmj.guanzongroup.sidebarmenus.controller.DeliveryAcceptance_EntryCarController.poPurchaseReceivingController;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReceiving;
import org.guanzon.cas.purchasing.services.PurchaseOrderReceivingControllers;
import org.guanzon.cas.purchasing.status.PurchaseOrderReceivingStatus;
import org.json.simple.JSONObject;
import static org.apache.poi.ss.usermodel.TableStyleType.lastColumn;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.ScrollBar;
import javafx.geometry.Orientation;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.time.format.DateTimeParseException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_EntryLPController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "Purchase Order Receiving Entry LP";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;

    private String psCompanyId = "";
    private String psSupplierId = "";

    private ObservableList<ModelDeliveryAcceptance_Detail> details_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Main> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelDeliveryAcceptance_Main> filteredData;
    private FilteredList<ModelDeliveryAcceptance_Detail> filteredDataDetail;

    private final Map<Integer, String> highlightedRowsMain = new HashMap<>();
    private final Map<Integer, String> highlightedRowsDetail = new HashMap<>();
    private TextField lastFocusedTextField = null;

    private ChangeListener<String> detailSearchListener;
    private ChangeListener<String> mainSearchListener;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail;

    @FXML
    private HBox hbButtons, hboxid;

    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnPrint, btnHistory, btnRetrieve, btnClose;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField tfTransactionNo, tfIndustry, tfCompany, tfSupplier, tfTrucking, tfReferenceNo, tfTerm, tfDiscountRate,
            tfDiscountAmount, tfTotal, tfOrderNo, tfBarcode, tfSupersede, tfDescription, tfBrand, tfModel, tfColor, tfInventoryType,
            tfMeasure, tfCost, tfOrderQuantity, tfReceiveQuantity;

    @FXML
    private TextArea taRemarks;

    @FXML
    private DatePicker dpTransactionDate, dpReferenceDate, dpExpiryDate;

    @FXML
    private TableView tblViewOrderDetails, tblViewPuchaseOrder;

    @FXML
    private TableColumn tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail, tblCostDetail, tblOrderQuantityDetail,
            tblReceiveQuantityDetail, tblTotalDetail, tblRowNo, tblSupplier, tblDate, tblReferenceNo;

    @FXML
    private Pagination pgPagination;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            poPurchaseReceivingController = new PurchaseOrderReceivingControllers(oApp, null).PurchaseOrderReceiving();
            poJSON = new JSONObject();
            poJSON = poPurchaseReceivingController.InitTransaction(); // Initialize transaction
            if (!"success".equals((String) poJSON.get("result"))) {
                System.err.println((String) poJSON.get("message"));
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }

            poJSON = poPurchaseReceivingController.NewTransaction();
            if (!"success".equals((String) poJSON.get("result"))) {
                System.err.println((String) poJSON.get("message"));
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }

            initTextFields();
            initDatePickers();
            initMainGrid();
            initDetailsGrid();
            initTableOnClick();
            clearTextFields();

            poPurchaseReceivingController.Master().setBranchCode(oApp.getBranchCode());
            poPurchaseReceivingController.Master().setIndustryId(oApp.getIndustry());
            poPurchaseReceivingController.Master().setTransactionDate(oApp.getServerDate());

            loadRecordMaster();
            loadTableDetail();

            pgPagination.setPageCount(1);

            pnEditMode = poPurchaseReceivingController.getEditMode();
            initButton(pnEditMode);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
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
                        poJSON = poPurchaseReceivingController.searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        pnEditMode = poPurchaseReceivingController.getEditMode();
                        psCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                        psSupplierId = poPurchaseReceivingController.Master().getSupplierId();
                        break;
                    case "btnPrint":
                        poJSON = poPurchaseReceivingController.printRecord();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        }

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
                        poPurchaseReceivingController.resetMaster();
                        poPurchaseReceivingController.resetOthers();
                        poPurchaseReceivingController.Detail().clear();
                        clearTextFields();
                        
                        poJSON = poPurchaseReceivingController.NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        poPurchaseReceivingController.Master().setBranchCode(oApp.getBranchCode());
                        poPurchaseReceivingController.Master().setIndustryId(oApp.getIndustry());
                        poPurchaseReceivingController.Master().setTransactionDate(oApp.getServerDate());

                        if (!psCompanyId.isEmpty()) {
                            poPurchaseReceivingController.SearchCompany(psCompanyId, true);
                        }
                        if (!psSupplierId.isEmpty()) {
                            poPurchaseReceivingController.SearchSupplier(psSupplierId, true);
                        }
                        pnEditMode = poPurchaseReceivingController.getEditMode();
                        break;
                    case "btnUpdate":
                        poJSON = poPurchaseReceivingController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poPurchaseReceivingController.getEditMode();
                        break;
                    case "btnSearch":
                        if (lastFocusedTextField != null) {
                            // Create a simulated KeyEvent for F3 key press
                            KeyEvent keyEvent = new KeyEvent(
                                    KeyEvent.KEY_PRESSED,
                                    "",
                                    "F3",
                                    KeyCode.F3,
                                    false, false, false, false);

                            lastFocusedTextField.fireEvent(keyEvent);
                        } else {
                            ShowMessageFX.Information(null, pxeModuleName, "Focus a searchable textfield to search");
                        }
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            //get last retrieved Company and Supplier
                            psCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                            psSupplierId = poPurchaseReceivingController.Master().getSupplierId();
                            
                            //Clear data
                            poPurchaseReceivingController.resetMaster();
                            poPurchaseReceivingController.resetOthers();
                            poPurchaseReceivingController.Detail().clear();
                            clearTextFields();
                            
                            pnEditMode = EditMode.UNKNOWN;
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        //Retrieve data from purchase order to table main
                        if (mainSearchListener != null) {
                            tfOrderNo.textProperty().removeListener(mainSearchListener);
                            mainSearchListener = null; // Clear reference to avoid memory leaks
                        }
                        retrievePO();
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poPurchaseReceivingController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                //get last retrieved Company and Supplier
                                psCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                                psSupplierId = poPurchaseReceivingController.Master().getSupplierId();

                                //Call new transaction
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
                
                if (lsButton.equals("btnPrint") || lsButton.equals("btnRetrieve") || lsButton.equals("btnCancel")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                
                initButton(pnEditMode);

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void retrievePO() {
        poJSON = new JSONObject();
        String lsMessage = "";
        poJSON.put("result", "success");

        if (poPurchaseReceivingController.Master().getIndustryId().equals("")) {
            poJSON.put("result", "error");
            lsMessage = "Industry";
        }
        if (poPurchaseReceivingController.Master().getCompanyId().equals("")) {
            poJSON.put("result", "error");
            lsMessage += lsMessage.isEmpty() ? "Company" : " & Company";
        }
        if (poPurchaseReceivingController.Master().getSupplierId().equals("")) {
            poJSON.put("result", "error");
            lsMessage += lsMessage.isEmpty() ? "Supplier" : " & Supplier";
        }

        if ("success".equals((String) poJSON.get("result"))) {
            poJSON = poPurchaseReceivingController.getApprovedPurchaseOrder();
            if (!"success".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            } else {
                loadTableMain();
            }
        } else {
            poJSON.put("message", lsMessage + " cannot be empty.");
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = (txtField.getId());
        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }
        poJSON = new JSONObject();
        if (!nv) {
            /*Lost Focus*/
            lsValue = lsValue.trim();
            switch (lsID) {

                case "taRemarks"://Remarks
                    poJSON = poPurchaseReceivingController.Master().setRemarks(lsValue);
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                    break;
            }
            loadRecordMaster();
        } else {
            txtField.selectAll();
        }
    };

    // Method to handle focus change and track the last focused TextField
    final ChangeListener<? super Boolean> txtDetail_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());
        lastFocusedTextField = txtPersonalInfo;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfDescription":
                case "tfBarcode":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setStockId("");
                    }

                    break;
                case "tfSupersede":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setReplaceId("");
                    }

                    break;
                case "tfCost":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                        return;
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setUnitPrce((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }

                    break;
                case "tfReceiveQuantity":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setQuantity((Integer.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                    break;
            }
            loadTableDetail();
        }
    };

    final ChangeListener<? super Boolean> txtMaster_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());
        lastFocusedTextField = txtPersonalInfo;

        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfCompany":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.Master().setCompanyId("");
                    }
                    break;
                case "tfSupplier":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.Master().setSupplierId("");
                    }
                    break;
                case "tfTrucking":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.Master().setTruckingId("");
                    }
                    break;
                case "tfAreaRemarks":
                    break;
                case "tfTerm":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.Master().setTermCode("");
                    }
                    break;
                case "tfReferenceNo":
                    if (!lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.Master().setReferenceNo(lsValue);
                    } else {
                        poJSON = poPurchaseReceivingController.Master().setReferenceNo("");
                    }
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        tfReferenceNo.setText("");
                        break;
                    }
                    break;
                case "tfDiscountRate":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                        return;
                    }
                    poJSON = poPurchaseReceivingController.Master().setDiscountRate((Double.valueOf(lsValue)));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    poJSON = poPurchaseReceivingController.computeDiscount(poPurchaseReceivingController.Master().getDiscountRate().doubleValue());
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    break;
                case "tfDiscountAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    if (Double.parseDouble(lsValue.replace(",", "")) < 0.00) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                        return;
                    }
                    poJSON = poPurchaseReceivingController.Master().setDiscount(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    poJSON = poPurchaseReceivingController.computeDiscountRate(poPurchaseReceivingController.Master().getDiscount().doubleValue());
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    break;

            }

            loadRecordMaster();
        }

    };

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            switch (event.getCode()) {
                case F3:
                    switch (lsID) {
                        case "tfCompany":
                            /*search company*/
                            poJSON = poPurchaseReceivingController.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfCompany.setText("");
                                psCompanyId = "";
                                break;
                            }
                            if (!poPurchaseReceivingController.Master().getSupplierId().equals("")) {
                                retrievePO();
                            }
                            psCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                            break;

                        case "tfSupplier":
                            poJSON = poPurchaseReceivingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupplier.setText("");
                                psSupplierId = "";
                                break;
                            }
                            if (!poPurchaseReceivingController.Master().getCompanyId().equals("")) {
                                retrievePO();
                            }
                            psSupplierId = poPurchaseReceivingController.Master().getSupplierId();
                            break;
                        case "tfTrucking":
                            poJSON = poPurchaseReceivingController.SearchTrucking(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfTrucking.setText("");
                                break;
                            }
                            break;
                        case "tfTerm":
                            poJSON = poPurchaseReceivingController.SearchTerm(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfTerm.setText("");
                                break;
                            }
                            loadRecordMaster();
                            break;
                        case "tfOrderNo":

                            break;
                        case "tfBarcode":
                            poJSON = poPurchaseReceivingController.SearchBarcode(lsValue, true, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfBarcode.setText("");
                                break;
                            }
                            break;

                        case "tfDescription":
                            poJSON = poPurchaseReceivingController.SearchDescription(lsValue, true, pnDetail);

                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfDescription.setText("");
                                break;
                            }
                            break;
                        case "tfSupersede":
                            poJSON = poPurchaseReceivingController.SearchSupersede(lsValue, true, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupersede.setText("");
                                break;
                            }
                            break;
                    }
                    loadRecordMaster();
                    loadTableDetail();
                    break;
                default:
                    break;
            }

            switch (event.getCode()) {
                case ENTER:
                    CommonUtils.SetNextFocus(txtField);
                case DOWN:
                    CommonUtils.SetNextFocus(txtField);
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(txtField);
            }
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void initTextFields() {
        tfCompany.focusedProperty().addListener(txtMaster_Focus);
        tfSupplier.focusedProperty().addListener(txtMaster_Focus);
        tfTrucking.focusedProperty().addListener(txtMaster_Focus);
        taRemarks.focusedProperty().addListener(txtArea_Focus);
        tfReferenceNo.focusedProperty().addListener(txtMaster_Focus);
        tfTerm.focusedProperty().addListener(txtMaster_Focus);
        tfDiscountRate.focusedProperty().addListener(txtMaster_Focus);
        tfDiscountAmount.focusedProperty().addListener(txtMaster_Focus);

        tfBarcode.focusedProperty().addListener(txtDetail_Focus);
        tfSupersede.focusedProperty().addListener(txtDetail_Focus);
        tfDescription.focusedProperty().addListener(txtDetail_Focus);
        tfCost.focusedProperty().addListener(txtDetail_Focus);
        tfReceiveQuantity.focusedProperty().addListener(txtDetail_Focus);

        tfCompany.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupplier.setOnKeyPressed(this::txtField_KeyPressed);
        tfTrucking.setOnKeyPressed(this::txtField_KeyPressed);
        tfTerm.setOnKeyPressed(this::txtField_KeyPressed);
        tfOrderNo.setOnKeyPressed(this::txtField_KeyPressed);
        tfBarcode.setOnKeyPressed(this::txtField_KeyPressed);
        tfDescription.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupersede.setOnKeyPressed(this::txtField_KeyPressed);
        CustomCommonUtil.inputDecimalOnly(tfDiscountRate, tfDiscountAmount, tfCost, tfReceiveQuantity);

    }

    ChangeListener<Boolean> datepicker_Focus = (observable, oldValue, newValue) -> {
        poJSON = new JSONObject();
        try {
            if (!newValue) { // Lost focus
                DatePicker datePicker = (DatePicker) ((javafx.beans.property.ReadOnlyBooleanProperty) observable).getBean();
                String lsID = datePicker.getId();
                String inputText = datePicker.getEditor().getText();
                LocalDate currentDate = LocalDate.now();
                LocalDate selectedDate = null;
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if (inputText != null && !inputText.trim().isEmpty()) {
                    try {
                        LocalDate parsedDate = LocalDate.parse(inputText, DateTimeFormatter.ofPattern("yyyy-M-d"));
                        datePicker.setValue(parsedDate);
                        datePicker.getEditor().setText(formatter.format(parsedDate));
                        inputText = datePicker.getEditor().getText();
                    } catch (DateTimeParseException ignored) {
                    }
                }

                // Check if the user typed something in the text field
                if (inputText != null && !inputText.trim().isEmpty()) {
                    try {
                        selectedDate = LocalDate.parse(inputText, formatter);
                        datePicker.setValue(selectedDate); // Update the DatePicker with the valid date
                    } catch (Exception ex) {
                        poJSON.put("result", "error");
                        poJSON.put("message", "Invalid date format. Please use yyyy-MM-dd.");
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                } else {
                    selectedDate = datePicker.getValue(); // Fallback to selected date if nothing was typed
                }

                String formattedDate = selectedDate.toString();

                switch (lsID) {
                    case "dpTransactionDate":
                        if (selectedDate == null) {
                            break;
                        }
                        if (selectedDate.isAfter(currentDate)) {
                            poJSON.put("result", "error");
                            poJSON.put("message", "Future dates are not allowed.");
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            break;
                        } else {
                            poPurchaseReceivingController.Master().setTransactionDate((SQLUtil.toDate(formattedDate, "yyyy-MM-dd")));
                        }
                        break;
                    case "dpReferenceDate":
                        if (selectedDate == null) {
                            break;
                        }
                        if (selectedDate.isAfter(currentDate)) {
                            poJSON.put("result", "error");
                            poJSON.put("message", "Future dates are not allowed.");
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        } else {
                            poPurchaseReceivingController.Master().setReferenceDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        }
                        break;
                    case "dpExpiryDate":
                        if (selectedDate == null) {
                            break;
                        }
                        if (selectedDate.isBefore(currentDate)) {
                            poJSON.put("result", "error");
                            poJSON.put("message", "The selected date cannot be earlier than the current date.");
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        } else {
                            poPurchaseReceivingController.Detail(pnDetail).setExpiryDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        }
                        break;
                    default:
                        System.out.println("Unknown DatePicker.");
                        break;
                }
                datePicker.getEditor().setText(formattedDate);
                if (lsID.equals("dpExpiryDate")) {
                    loadRecordDetail();
                } else {
                    loadRecordMaster();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    private void setDatePickerFormat(DatePicker datePicker) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });
    }

    public void initDatePickers() {
        setDatePickerFormat(dpTransactionDate);
        setDatePickerFormat(dpReferenceDate);
        setDatePickerFormat(dpExpiryDate);

        dpTransactionDate.focusedProperty().addListener(datepicker_Focus);
        dpReferenceDate.focusedProperty().addListener(datepicker_Focus);
        dpExpiryDate.focusedProperty().addListener(datepicker_Focus);
    }

    public void initDetailsGrid() {

        tblRowNoDetail.setStyle("-fx-alignment: CENTER;");
        tblOrderNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblBarcodeDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblDescriptionDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblCostDetail.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 5;");
        tblOrderQuantityDetail.setStyle("-fx-alignment: CENTER 0 5 0 5;");
        tblReceiveQuantityDetail.setStyle("-fx-alignment: CENTER 0 5 0 5;");
        tblTotalDetail.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 5;");

        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblOrderNoDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblBarcodeDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblDescriptionDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblCostDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblOrderQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));
        tblReceiveQuantityDetail.setCellValueFactory(new PropertyValueFactory<>("index07"));
        tblTotalDetail.setCellValueFactory(new PropertyValueFactory<>("index08"));

        tblViewOrderDetails.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewOrderDetails.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        filteredDataDetail = new FilteredList<>(details_data, b -> true);
        autoSearch(tfOrderNo);

        SortedList<ModelDeliveryAcceptance_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewOrderDetails.comparatorProperty());
        tblViewOrderDetails.setItems(sortedData);
    }

    public void initMainGrid() {
        tblRowNo.setStyle("-fx-alignment: CENTER 0 5 0 5;");
        tblSupplier.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblDate.setStyle("-fx-alignment: CENTER 0 5 0 5;");
        tblReferenceNo.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");

        tblRowNo.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblSupplier.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblDate.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblReferenceNo.setCellValueFactory(new PropertyValueFactory<>("index04"));

        tblViewPuchaseOrder.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewPuchaseOrder.lookup("TableHeaderRow");
            try {
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            } catch (Exception e) {
            }
        });

        filteredData = new FilteredList<>(main_data, b -> true);

        SortedList<ModelDeliveryAcceptance_Main> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblViewPuchaseOrder.comparatorProperty());
        tblViewPuchaseOrder.setItems(sortedData);

    }

    public void clearTextFields() {

        dpTransactionDate.setValue(null);
        dpReferenceDate.setValue(null);
        dpExpiryDate.setValue(null);

        tfTransactionNo.clear();
        tfIndustry.clear();
        tfCompany.clear();
        tfSupplier.clear();
        tfTrucking.clear();
        taRemarks.clear();
        tfReferenceNo.clear();
        tfTerm.clear();
        tfDiscountRate.clear();
        tfDiscountAmount.clear();
        tfTotal.clear();
        tfOrderNo.clear();
        tfBarcode.clear();
        tfSupersede.clear();
        tfDescription.clear();
        tfBrand.clear();
        tfModel.clear();
        tfColor.clear();
        tfInventoryType.clear();
        tfMeasure.clear();
        tfCost.clear();
        tfOrderQuantity.clear();
        tfReceiveQuantity.clear();

        loadRecordMaster();
        loadTableDetail();
        loadTableMain();
    }

    public void loadRecordDetail() {
        try {
            boolean lbFields = (poPurchaseReceivingController.Detail(pnDetail).getOrderNo().equals("") || poPurchaseReceivingController.Detail(pnDetail).getOrderNo() == null);
            tfBarcode.setDisable(!lbFields);
            tfDescription.setDisable(!lbFields);
            if (lbFields) {
                while (tfBarcode.getStyleClass().contains("DisabledTextField") || tfDescription.getStyleClass().contains("DisabledTextField")) {
                    tfBarcode.getStyleClass().remove("DisabledTextField");
                    tfDescription.getStyleClass().remove("DisabledTextField");
                }
            } else {
                tfBarcode.getStyleClass().add("DisabledTextField");
                tfDescription.getStyleClass().add("DisabledTextField");
            }
            // Expiry Date
            String lsExpiryDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Detail(pnDetail).getExpiryDate());
            dpExpiryDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsExpiryDate, "yyyy-MM-dd"));

            tfBarcode.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().getBarCode());
            tfDescription.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().getDescription());
            tfSupersede.setText(poPurchaseReceivingController.Detail(pnDetail).Supersede().getBarCode());
            tfBrand.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Brand().getDescription());
            tfModel.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Model().getDescription());
            tfColor.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Color().getDescription());
            tfInventoryType.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().InventoryType().getDescription());
            tfMeasure.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Measure().getDescription());

            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(pnDetail).getUnitPrce()));
            tfOrderQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getOrderQty().intValue()));
            tfReceiveQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity()));

        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadRecordMaster() {
        boolean lbDisable = pnEditMode == EditMode.UPDATE;
        if (lbDisable) {
            tfCompany.setDisable(lbDisable);
            tfSupplier.setDisable(lbDisable);
            tfCompany.getStyleClass().add("DisabledTextField");
            tfSupplier.getStyleClass().add("DisabledTextField");
        } else {
            tfCompany.setDisable(lbDisable);
            tfSupplier.setDisable(lbDisable);
            tfCompany.getStyleClass().remove("DisabledTextField");
            tfSupplier.getStyleClass().remove("DisabledTextField");
        }

        boolean lbIsReprint = poPurchaseReceivingController.Master().getPrint().equals("1") ? true : false;
        if (lbIsReprint) {
            btnPrint.setText("Reprint");
        } else {
            btnPrint.setText("Print");
        }
        try {

            String lsActive = poPurchaseReceivingController.Master().getTransactionStatus();
            switch (lsActive) {
                case PurchaseOrderReceivingStatus.APPROVED:
                    lblStatus.setText("APPROVE");
                    break;
                case PurchaseOrderReceivingStatus.CANCELLED:
                    lblStatus.setText("CANCELLED");
                    break;
                case PurchaseOrderReceivingStatus.CONFIRMED:
                    lblStatus.setText("CONFIRMED");
                    break;
                case PurchaseOrderReceivingStatus.OPEN:
                    lblStatus.setText("OPEN");
                    break;
                case PurchaseOrderReceivingStatus.RETURNED:
                    lblStatus.setText("RETURNED");
                    break;
                case PurchaseOrderReceivingStatus.VOID:
                    lblStatus.setText("VOID");
                    break;
                default:
                    lblStatus.setText("UNKNOWN");
                    break;
            }

            poPurchaseReceivingController.computeFields();
            // Transaction Date
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
            //ReferenceDate
            String lsReferenceDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getReferenceDate());
            dpReferenceDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsReferenceDate, "yyyy-MM-dd"));

            tfTransactionNo.setText(poPurchaseReceivingController.Master().getTransactionNo());
            tfIndustry.setText(poPurchaseReceivingController.Master().Industry().getDescription());
            tfCompany.setText(poPurchaseReceivingController.Master().Company().getCompanyName());
            tfSupplier.setText(poPurchaseReceivingController.Master().Supplier().getCompanyName());
            tfTrucking.setText(poPurchaseReceivingController.Master().Trucking().getCompanyName());
            tfTerm.setText(poPurchaseReceivingController.Master().Term().getDescription());
            tfReferenceNo.setText(poPurchaseReceivingController.Master().getReferenceNo());
            taRemarks.setText(poPurchaseReceivingController.Master().getRemarks());

            double lnValue = poPurchaseReceivingController.Master().getDiscountRate().doubleValue();
            if (!Double.isNaN(lnValue)) {
                tfDiscountRate.setText((String.valueOf(poPurchaseReceivingController.Master().getDiscountRate().doubleValue())));
            }
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReceivingController.Master().getDiscount().doubleValue())));
            tfTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReceivingController.Master().getTransactionTotal().doubleValue())));
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    private int moveToNextRow(TableView table, TablePosition focusedCell) {
        int nextRow = (focusedCell.getRow() + 1) % table.getItems().size();
        table.getSelectionModel().select(nextRow);
        return nextRow;
    }

    private int moveToPreviousRow(TableView table, TablePosition focusedCell) {
        int previousRow = (focusedCell.getRow() - 1 + table.getItems().size()) % table.getItems().size();
        table.getSelectionModel().select(previousRow);
        return previousRow;
    }

    private void tableKeyEvents(KeyEvent event) {
        TableView<?> currentTable = (TableView<?>) event.getSource();
        TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
        if (focusedCell != null) {
            switch (event.getCode()) {
                case TAB:
                case DOWN:
                    pnDetail = moveToNextRow(currentTable, focusedCell);
                    break;
                case UP:
                    pnDetail = moveToPreviousRow(currentTable, focusedCell);
                    break;

                default:
                    break;
            }
            loadRecordDetail();
            event.consume();
        }
    }

    public void initTableOnClick() {

        tblViewOrderDetails.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                pnDetail = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
                loadRecordDetail();
            }
        });

        tblViewPuchaseOrder.setOnMouseClicked(event -> {
            pnMain = tblViewPuchaseOrder.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    tfOrderNo.setText("");
                    loadTableDetailFromMain();
                    pnEditMode = poPurchaseReceivingController.getEditMode();
                    initButton(pnEditMode);
                }
            }
        });

        tblViewPuchaseOrder.setRowFactory(tv -> new TableRow<ModelDeliveryAcceptance_Main>() {
            @Override
            protected void updateItem(ModelDeliveryAcceptance_Main item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle(""); // Reset for empty rows
                } else if (highlightedRowsMain.containsKey(getIndex())) {
                    setStyle("-fx-background-color: " + highlightedRowsMain.get(getIndex()) + ";");
                } else {
                    setStyle(""); // Default style
                }
            }
        });
        tblViewOrderDetails.setRowFactory(tv -> new TableRow<ModelDeliveryAcceptance_Detail>() {
            @Override
            protected void updateItem(ModelDeliveryAcceptance_Detail item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle(""); // Reset for empty rows
                } else if (highlightedRowsDetail.containsKey(getIndex())) {
                    setStyle("-fx-background-color: " + highlightedRowsDetail.get(getIndex()) + ";");
                } else {
                    setStyle(""); // Default style
                }
            }
        });
        tblViewOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        adjustLastColumnForScrollbar(tblViewOrderDetails); // need to use computed-size last column to work
        adjustLastColumnForScrollbar(tblViewPuchaseOrder);
    }

    public void adjustLastColumnForScrollbar(TableView<?> tableView) {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (!(newSkin instanceof TableViewSkin<?>)) {
                return;
            }

            TableViewSkin<?> skin = (TableViewSkin<?>) newSkin;
            VirtualFlow<?> flow = skin.getChildren().stream()
                    .filter(node -> node instanceof VirtualFlow<?>)
                    .map(node -> (VirtualFlow<?>) node)
                    .findFirst().orElse(null);

            if (flow == null) {
                return;
            }

            ScrollBar vScrollBar = flow.getChildrenUnmodifiable().stream()
                    .filter(node -> node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == Orientation.VERTICAL)
                    .map(node -> (ScrollBar) node)
                    .findFirst().orElse(null);

            if (vScrollBar == null || tableView.getColumns().isEmpty()) {
                return;
            }

            TableColumn<?, ?> lastColumn = (TableColumn<?, ?>) tableView.getColumns()
                    .get(tableView.getColumns().size() - 1);

            vScrollBar.visibleProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    double scrollBarWidth = newValue ? vScrollBar.getWidth() : 0;
                    double remainingWidth = tableView.getWidth() - scrollBarWidth;

                    double totalFixedWidth = tableView.getColumns().stream()
                            .filter(col -> col != lastColumn)
                            .mapToDouble(col -> ((TableColumn<?, ?>) col).getWidth())
                            .sum();

                    double newWidth = Math.max(0, remainingWidth - totalFixedWidth);
                    lastColumn.setPrefWidth(newWidth - 5);
                });
            });
        });
    }

    public void loadTableMain() {
        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewPuchaseOrder.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                Platform.runLater(() -> {
                    main_data.clear();
                    String lsMainDate = "";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Define the format
                    // Simulate loading delay
                    try {
                        if (!poPurchaseReceivingController.Master().getTransactionDate().equals("")) {
                            Object loDate = poPurchaseReceivingController.Master().getTransactionDate();
                            if (loDate == null) {
                                lsMainDate = LocalDate.now().format(formatter); // Convert to String

                            } else if (loDate instanceof Timestamp) {
                                Timestamp timestamp = (Timestamp) loDate;
                                LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();

                                lsMainDate = localDate.format(formatter);
                            } else if (loDate instanceof Date) {
                                Date sqlDate = (Date) loDate;
                                LocalDate localDate = sqlDate.toLocalDate();

                                lsMainDate = localDate.format(formatter);
                            } else {
                            }
                        }
                    } catch (Exception e) {

                    }
                    for (int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderCount() - 1; lnCtr++) {
                        try {
                            main_data.add(new ModelDeliveryAcceptance_Main(String.valueOf(lnCtr + 1),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderList(lnCtr).Supplier().getCompanyName()),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderList(lnCtr).getTransactionDate()),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderList(lnCtr).getTransactionNo())
                            ));
                        } catch (Exception e) {

                        }

                    }
                    if (pnMain < 0 || pnMain
                            >= main_data.size()) {
                        if (!main_data.isEmpty()) {
                            /* FOCUS ON FIRST ROW */
                            tblViewPuchaseOrder.getSelectionModel().select(0);
                            tblViewPuchaseOrder.getFocusModel().focus(0);
                            pnMain = tblViewPuchaseOrder.getSelectionModel().getSelectedIndex();

                        }
                    } else {
                        /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                        tblViewPuchaseOrder.getSelectionModel().select(pnMain);
                        tblViewPuchaseOrder.getFocusModel().focus(pnMain);
                    }
                    loadTab();
                });
                return null;
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                if (main_data == null || main_data.isEmpty()) {
                    tblViewPuchaseOrder.setPlaceholder(placeholderLabel);
                } else {
                    tblViewPuchaseOrder.toFront();
                }
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblViewPuchaseOrder.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background

    }

    public void loadTableDetailFromMain() {
        try {
            if (poPurchaseReceivingController.getEditMode() == EditMode.ADDNEW || poPurchaseReceivingController.getEditMode() == EditMode.UPDATE) {
                poJSON = new JSONObject();
                poJSON = poPurchaseReceivingController.addPurchaseOrderToPORDetail(poPurchaseReceivingController.PurchaseOrderList(pnMain).getTransactionNo());
                if ("error".equals((String) poJSON.get("message"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                } else {
                    disableAllHighlight(tblViewPuchaseOrder, highlightedRowsMain);
                    highlight(tblViewPuchaseOrder, pnMain, "#A7C7E7", highlightedRowsMain);
                }

                loadTableDetail();
            }

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadTableDetail() {
        // Setting data to table detail
        loadRecordMaster();
        disableAllHighlight(tblViewOrderDetails, highlightedRowsDetail);

        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewOrderDetails.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    int lnCtr;
                    details_data.clear();
                    try {

                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poPurchaseReceivingController.getDetailCount() - 1;
                            while (lnCtr > 0) {
                                if (poPurchaseReceivingController.Detail(lnCtr).getStockId() == null || poPurchaseReceivingController.Detail(lnCtr).getStockId().equals("")) {
                                    poPurchaseReceivingController.Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poPurchaseReceivingController.getDetailCount() - 1) >= 0) {
                                if (poPurchaseReceivingController.Detail(poPurchaseReceivingController.getDetailCount() - 1).getStockId() != null && !poPurchaseReceivingController.Detail(poPurchaseReceivingController.getDetailCount() - 1).getStockId().equals("")) {
                                    poPurchaseReceivingController.AddDetail();
                                }
                            }
                        }

                        double lnTotal = 0.0;
                        for (lnCtr = 0; lnCtr < poPurchaseReceivingController.getDetailCount(); lnCtr++) {
                            try {

                                lnTotal = poPurchaseReceivingController.Detail(lnCtr).getUnitPrce().doubleValue() * poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue();
                            } catch (Exception e) {
                            }

                            if ((!poPurchaseReceivingController.Detail(lnCtr).getOrderNo().equals("") && poPurchaseReceivingController.Detail(lnCtr).getOrderNo() != null)
                                    && poPurchaseReceivingController.Detail(lnCtr).getOrderQty().intValue() != poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue()
                                    && poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue() != 0) {
                                highlight(tblViewOrderDetails, lnCtr, "#FAA0A0", highlightedRowsDetail);
                            }

                            details_data.add(
                                    new ModelDeliveryAcceptance_Detail(String.valueOf(lnCtr + 1),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getOrderNo()),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).Inventory().getBarCode()),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).Inventory().getDescription()),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getUnitPrce()),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getOrderQty().intValue()),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getQuantity()),
                                            String.valueOf(lnTotal) //identify total
                                    ));
                        }

                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                tblViewOrderDetails.getSelectionModel().select(0);
                                tblViewOrderDetails.getFocusModel().focus(0);
                                pnDetail = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            tblViewOrderDetails.getSelectionModel().select(pnDetail);
                            tblViewOrderDetails.getFocusModel().focus(pnDetail);
                            loadRecordDetail();
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(DeliveryAcceptance_EntryLPController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    }

                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewOrderDetails.setPlaceholder(placeholderLabel);
                } else {
                    tblViewOrderDetails.toFront();
                }
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewOrderDetails.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);

        // Manage visibility and managed state of other buttons
        btnNew.setVisible(!lbShow);
        btnNew.setManaged(!lbShow);
        btnSearch.setVisible(lbShow);
        btnSearch.setManaged(lbShow);
        btnSave.setVisible(lbShow);
        btnSave.setManaged(lbShow);
        btnCancel.setVisible(lbShow);
        btnCancel.setManaged(lbShow);

        btnUpdate.setVisible(lbShow2);
        btnUpdate.setManaged(lbShow2);
        btnPrint.setVisible(lbShow2);
        btnPrint.setManaged(lbShow2);
        btnHistory.setVisible(lbShow2);
        btnHistory.setManaged(lbShow2);

        btnBrowse.setVisible(lbShow3);
        btnBrowse.setManaged(lbShow3);
        btnClose.setVisible(lbShow3);
        btnClose.setManaged(lbShow3);

        //apMaster.setDisable(!lbShow);
        dpTransactionDate.setDisable(!lbShow);
        dpReferenceDate.setDisable(!lbShow);
        tfTrucking.setDisable(!lbShow);
        taRemarks.setDisable(!lbShow);
        tfReferenceNo.setDisable(!lbShow);
        tfTerm.setDisable(!lbShow);
        tfDiscountRate.setDisable(!lbShow);
        tfDiscountAmount.setDisable(!lbShow);

        apDetail.setDisable(!lbShow);

        switch (poPurchaseReceivingController.Master().getTransactionStatus()) {
            case PurchaseOrderReceivingStatus.APPROVED:
            case PurchaseOrderReceivingStatus.VOID:
                btnUpdate.setVisible(false);
                btnUpdate.setManaged(false);
                break;
        }
    }

//    private void initButton(int fnValue) {
//        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
//        // Manage visibility and managed state of other buttons
//        boolean lbShow3 = fnValue == EditMode.ADDNEW;
//
//        btnBrowse.setVisible(!lbShow3); // Requires no change to state
//
//        btnNew.setVisible(!lbShow);
////        btnRetrieve.setVisible(!lbShow);
//        btnClose.setVisible(!lbShow);
//
//        btnSearch.setVisible(lbShow);
//        btnSave.setVisible(lbShow);
//        btnCancel.setVisible(lbShow);
//
//        btnBrowse.setManaged(!lbShow3);
//
//        btnNew.setManaged(!lbShow);
////        btnRetrieve.setManaged(!lbShow);
//        btnClose.setManaged(!lbShow);
//
//        btnSearch.setManaged(lbShow);
//        btnSave.setManaged(lbShow);
//        btnCancel.setManaged(lbShow);
//
//        boolean lbShow2 = fnValue == EditMode.READY;
//
//        btnUpdate.setVisible(lbShow2);
//        btnPrint.setVisible(lbShow2);
//        btnHistory.setVisible(lbShow2);
//
//        btnUpdate.setManaged(lbShow2);
//        btnPrint.setManaged(lbShow2);
//        btnHistory.setManaged(lbShow2);
//
//        btnClose.setVisible(lbShow2);
//        btnClose.setManaged(lbShow2);
//
////        apBrowse.setDisable(lbShow); // no usage
//        apMaster.setDisable(!lbShow);
//        apDetail.setDisable(!lbShow);
////        apTable.setDisable(!lbShow); // disable upon for viewing?
////        if (Integer.valueOf(poPurchaseReceivingController.getMasterModel().getTransactionStatus()) != 0) {
////            btnVoid.setDisable(false);
////        } else {
////            btnVoid.setDisable(true);
////        }
////        poPurchaseReceivingController.setTransType("SP");
//
//
//    }
    private void loadTab() {
        int totalPage = (int) (Math.ceil(main_data.size() * 1.0 / ROWS_PER_PAGE));
        pgPagination.setPageCount(totalPage);
        pgPagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pgPagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
    }

    private void changeTableView(int index, int limit) {
        tblViewPuchaseOrder.getSelectionModel().clearSelection();
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, main_data.size());
        int minIndex = Math.min(toIndex, main_data.size());
        SortedList<ModelDeliveryAcceptance_Main> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tblViewPuchaseOrder.comparatorProperty());
        try {
            tblViewPuchaseOrder.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
        } catch (Exception e) {

        }

        tblViewPuchaseOrder.scrollTo(0);
    }

    public <T> void highlight(TableView<T> table, int rowIndex, String color, Map<Integer, String> highlightMap) {
        highlightMap.put(rowIndex, color);
        table.refresh(); // Refresh to apply changes
    }

    public <T> void disableHighlight(TableView<T> table, int rowIndex, Map<Integer, String> highlightMap) {
        highlightMap.remove(rowIndex);
        table.refresh();
    }

    public <T> void disableAllHighlight(TableView<T> table, Map<Integer, String> highlightMap) {
        highlightMap.clear();
        table.refresh();
    }

    private void autoSearch(TextField txtField) {
        detailSearchListener = (observable, oldValue, newValue) -> {
            filteredDataDetail.setPredicate(orders -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (mainSearchListener != null) {
                    txtField.textProperty().removeListener(mainSearchListener);
                    mainSearchListener = null; // Clear reference to avoid memory leaks
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return orders.getIndex02().toLowerCase().contains(lowerCaseFilter);
            });
            // If no results and autoSearchMain is enabled, remove listener and trigger autoSearchMain
            if (filteredDataDetail.isEmpty()) {
                txtField.textProperty().removeListener(detailSearchListener);
                filteredData = new FilteredList<>(main_data, b -> true);
                autoSearchMain(txtField); // Trigger autoSearchMain if no results
                SortedList<ModelDeliveryAcceptance_Main> sortedData = new SortedList<>(filteredData);
                sortedData.comparatorProperty().bind(tblViewPuchaseOrder.comparatorProperty());
                tblViewPuchaseOrder.setItems(sortedData);

                String currentText = txtField.getText();
                txtField.setText(currentText + " "); // Add a space
                txtField.setText(currentText);       // Set back to original
            }
        };
        txtField.textProperty().addListener(detailSearchListener);
    }

    private void autoSearchMain(TextField txtField) {
        mainSearchListener = (observable, oldValue, newValue) -> {
            filteredData.setPredicate(orders -> {
                if (newValue == null || newValue.isEmpty()) {
                    if (mainSearchListener != null) {
                        txtField.textProperty().removeListener(mainSearchListener);
                        mainSearchListener = null; // Clear reference to avoid memory leaks
                        initDetailsGrid();
                    }
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return orders.getIndex04().toLowerCase().contains(lowerCaseFilter);
            });
        };
        txtField.textProperty().addListener(mainSearchListener);
    }

}
