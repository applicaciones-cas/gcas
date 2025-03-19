/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.mysql.cj.x.protobuf.MysqlxCrud.DataModel;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
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
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
import org.guanzon.cas.purchasing.services.PurchaseOrderReceivingModels;
import org.json.simple.JSONObject;
import org.junit.Assert;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "Purchase Order Receiving Entry";
    static PurchaseOrderReceiving oTrans;
    public int pnEditMode;

    private String lsCompanyId = "";
    private String lsSupplierId = "";

    private ObservableList<ModelDeliveryAcceptance_Detail> details_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Main> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelDeliveryAcceptance_Main> filteredData;
    private FilteredList<ModelDeliveryAcceptance_Detail> filteredDataDetail;

    private final Set<Integer> highlightedRows = new HashSet<>();
    private TextField lastFocusedTextField = null;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private AnchorPane apMainAnchor;

    @FXML
    private AnchorPane apBrowse;

    @FXML
    private AnchorPane apButton;

    @FXML
    private HBox hbButtons;

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnNew;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnPrint;

    @FXML
    private Button btnHistory;

    @FXML
    private Button btnRetrieve;

    @FXML
    private Button btnClose;

    @FXML
    private HBox hboxid;

    @FXML
    private AnchorPane apMaster;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField tfTransactionNo;

    @FXML
    private DatePicker dpTransactionDate;

    @FXML
    private TextField tfIndustry;

    @FXML
    private TextField tfCompany;

    @FXML
    private TextField tfSupplier;

    @FXML
    private TextField tfTrucking;

    @FXML
    private TextArea taRemarks;

    @FXML
    private DatePicker dpReferenceDate;

    @FXML
    private TextField tfReferenceNo;

    @FXML
    private TextField tfTerm;

    @FXML
    private TextField tfDiscountRate;

    @FXML
    private TextField tfDiscountAmount;

    @FXML
    private TextField tfTotal;

    @FXML
    private AnchorPane apDetail;

    @FXML
    private TextField tfOrderNo;

    @FXML
    private TextField tfBarcode;

    @FXML
    private TextField tfSupersede;

    @FXML
    private TextField tfDescription;

    @FXML
    private TextField tfBrand;

    @FXML
    private TextField tfModel;

    @FXML
    private TextField tfColor;

    @FXML
    private TextField tfInventoryType;

    @FXML
    private TextField tfMeasure;

    @FXML
    private DatePicker dpExpiryDate;

    @FXML
    private TextField tfCost;

    @FXML
    private TextField tfOrderQuantity;

    @FXML
    private TextField tfReceiveQuantity;

    @FXML
    private TableView tblViewOrderDetails;

    @FXML
    private TableColumn tblRowNoDetail;

    @FXML
    private TableColumn tblOrderNoDetail;

    @FXML
    private TableColumn tblBarcodeDetail;

    @FXML
    private TableColumn tblDescriptionDetail;

    @FXML
    private TableColumn tblCostDetail;

    @FXML
    private TableColumn tblOrderQuantityDetail;

    @FXML
    private TableColumn tblReceiveQuantityDetail;

    @FXML
    private TableColumn tblTotalDetail;

    @FXML
    private TableView tblViewPuchaseOrder;

    @FXML
    private TableColumn tblRowNo;

    @FXML
    private TableColumn tblSupplier;

    @FXML
    private TableColumn tblDate;

    @FXML
    private TableColumn tblReferenceNo;
    @FXML
    private Pagination pgPagination;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            oTrans = new PurchaseOrderReceivingControllers(oApp, null).PurchaseOrderReceiving();
            poJSON = new JSONObject();
            poJSON = oTrans.InitTransaction(); // Initialize transaction
            if (!"success".equals((String) poJSON.get("result"))) {
                System.err.println((String) poJSON.get("message"));
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }

            poJSON = oTrans.NewTransaction();
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

            loadRecordMaster();
            loadTableDetail();

            pgPagination.setPageCount(1);

            pnEditMode = oTrans.getEditMode();
            initButton(pnEditMode);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    public void showSerialDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Serial.fxml"));
            DeliveryAcceptance_SerialController controller = new DeliveryAcceptance_SerialController();
            loader.setController(controller);

            if (controller != null) {
                System.out.println("Controller loaded successfully: " + controller.getClass().getName());
            } else {
                System.out.println("Controller is null!");
            }
            Parent root = loader.load();

            // Handle drag events for the undecorated window
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
            // Create a new Stage
            Stage dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.setTitle("");
            dialogStage.setScene(new Scene(root));
            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        poJSON = new JSONObject();
        String tabText = "";

        try {
            Object source = event.getSource();
            if (source instanceof Button) {
                Button clickedButton = (Button) source;
                String lsButton = clickedButton.getId();
                switch (lsButton) {
                    case "btnBrowse":
                        poJSON = oTrans.searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        pnEditMode = oTrans.getEditMode();
                        break;

                    case "btnPrint":
                        poJSON = oTrans.printRecord(false);
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, (String) poJSON.get("message"));
                        }

                        break;
                    case "btnClose":
                        unloadForm appUnload = new unloadForm();
                        if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?") == true) {
                            appUnload.unloadForm(apMainAnchor, oApp, pxeModuleName);
                        } else {
                            return;
                        }
                    case "btnNew":
                        poJSON = oTrans.NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        if (!lsCompanyId.isEmpty()) {
                            oTrans.SearchCompany(lsCompanyId, true);
                        }
                        if (!lsSupplierId.isEmpty()) {
                            oTrans.SearchSupplier(lsSupplierId, true);
                        }
                        pnEditMode = oTrans.getEditMode();
                        break;
                    case "btnUpdate":
                        poJSON = oTrans.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = oTrans.getEditMode();
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
                            System.out.println("No TextField is currently focused.");
                        }
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            //get last retrieved Company and Supplier
                            lsCompanyId = oTrans.Master().getCompanyId();
                            lsSupplierId = oTrans.Master().getSupplierId();

                            clearTextFields();
                            //Call new transaction
                            btnNew.fire();
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        //Retrieve data from purchase order to table main
                        retrievePO();
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = oTrans.SaveTransaction();
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                //get last retrieved Company and Supplier
                                lsCompanyId = oTrans.Master().getCompanyId();
                                lsSupplierId = oTrans.Master().getSupplierId();

                                clearTextFields();
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
                initButton(pnEditMode);
                loadRecordMaster();
                loadTableDetail();
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrievePO() {
        poJSON = new JSONObject();
        poJSON = oTrans.getApprovedPurchaseOrder();
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            return;
        }

        loadTableMain();
        String lsMessage = "";
        poJSON.put("result", "success");

        if (oTrans.Master().getIndustryId().equals("")) {
            poJSON.put("result", "error");
            lsMessage = "Industry";
        }
        if (oTrans.Master().getCompanyId().equals("")) {
            poJSON.put("result", "error");
            lsMessage += lsMessage.isEmpty() ? "Company" : " & Company";
        }
        if (oTrans.Master().getSupplierId().equals("")) {
            poJSON.put("result", "error");
            lsMessage += lsMessage.isEmpty() ? "Supplier" : " & Supplier";
        }

        if ("success".equals((String) poJSON.get("result"))) {
            poJSON = oTrans.getApprovedPurchaseOrder();
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
                    poJSON = oTrans.Master().setRemarks(lsValue);
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
        try {

            if (!nv) {
                /*Lost Focus*/
                switch (lsTxtFieldID) {
                    case "tfBarcode":
                        //if value is blank then reset
                        if (lsValue.equals("")) {
                            if (oTrans.Detail(pnDetail).Inventory().getBarCode() != null && !oTrans.Detail(pnDetail).Inventory().getBarCode().equals("")) {
                                poJSON = oTrans.Detail(pnDetail).setStockId("");
                                if ("error".equals((String) poJSON.get("result"))) {
                                    System.err.println((String) poJSON.get("message"));
                                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                    return;
                                }
                            }
                        }

                        break;
                    case "tfSupersede":
                        //if value is blank then reset
                        if (lsValue.equals("")) {
                            if (oTrans.Detail(pnDetail).Inventory().getSupersededId() != null && !oTrans.Detail(pnDetail).Inventory().getSupersededId().equals("")) {
                                poJSON = oTrans.Detail(pnDetail).setStockId("");
                                if ("error".equals((String) poJSON.get("result"))) {
                                    System.err.println((String) poJSON.get("message"));
                                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                    return;
                                }
                            }
                        }

                        break;
                    case "tfDescription":
                        //if value is blank then reset
                        if (lsValue.equals("")) {
                            if (oTrans.Detail(pnDetail).Inventory().getDescription() != null && !oTrans.Detail(pnDetail).Inventory().getDescription().equals("")) {
                                poJSON = oTrans.Detail(pnDetail).setStockId("");
                                if ("error".equals((String) poJSON.get("result"))) {
                                    System.err.println((String) poJSON.get("message"));
                                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                    return;
                                }
                            }
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
                        poJSON = oTrans.Detail(pnDetail).setUnitPrce((Double.valueOf(lsValue.replace(",", ""))));
                        if ("error".equals((String) poJSON.get("result"))) {
                            System.err.println((String) poJSON.get("message"));
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }

                        break;

                    case "tfOrder":
                        if (lsValue.isEmpty()) {
                            lsValue = "0";
                        }
                        if (Integer.parseInt(lsValue) < 0) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                            return;
                        }
                        poJSON = oTrans.Detail(pnDetail).setOrderQty((Integer.valueOf(lsValue)));
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
                        if (Integer.parseInt(lsValue) < 0) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Invalid Downpayment Amount");
                            return;
                        }

                        poJSON = oTrans.Detail(pnDetail).setQuantity((Integer.valueOf(lsValue)));
                        oTrans.Detail(pnDetail).getQuantity();
                        if ("error".equals((String) poJSON.get("result"))) {
                            System.err.println((String) poJSON.get("message"));
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        break;
                }

                loadTableDetail();

            }
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
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
                        poJSON = oTrans.Master().setCompanyId("");
                    }
                    break;
                case "tfSupplier":
                    if (lsValue.isEmpty()) {
                        poJSON = oTrans.Master().setSupplierId("");
                    }
                    break;
                case "tfTrucking":
                    if (lsValue.isEmpty()) {
                        poJSON = oTrans.Master().setTruckingId("");
                    }
                    break;
                case "tfAreaRemarks":
                    break;
                case "tfTerm":
                    if (lsValue.isEmpty()) {
                        poJSON = oTrans.Master().setTermCode("");
                    }
                    break;
                case "tfReferenceNo":
                    if (!lsValue.isEmpty()) {
                        poJSON = oTrans.Master().setReferenceNo(lsValue);
                    } else {
                        poJSON = oTrans.Master().setReferenceNo("");
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
                    poJSON = oTrans.Master().setDiscountRate((Double.valueOf(lsValue)));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    poJSON = oTrans.computeDiscount(oTrans.Master().getDiscountRate().doubleValue());
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
                    poJSON = oTrans.Master().setDiscount(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    poJSON = oTrans.computeDiscountRate(oTrans.Master().getDiscount().doubleValue());
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
                            poJSON = oTrans.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfCompany.setText("");
                                break;
                            }
                            if (!oTrans.Master().getSupplierId().equals("")) {
                                retrievePO();
                            }
                            break;

                        case "tfSupplier":
                            poJSON = oTrans.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupplier.setText("");
                                break;
                            }
                            if (!oTrans.Master().getCompanyId().equals("")) {
                                retrievePO();
                            }
                            break;
                        case "tfTrucking":
                            poJSON = oTrans.SearchTrucking(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfTrucking.setText("");
                                break;
                            }
                            break;
                        case "tfTerm":
                            poJSON = oTrans.SearchTerm(lsValue, false);
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
                            poJSON = oTrans.SearchBarcode(lsValue, true, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfBarcode.setText("");
                                break;
                            }
                            break;

                        case "tfDescription":
                            poJSON = oTrans.SearchDescription(lsValue, true, pnDetail);

                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfDescription.setText("");
                                break;
                            }
                            break;
                        case "tfSupersede":
                            poJSON = oTrans.SearchSupersede(lsValue, true, pnDetail);
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
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initTextFields() {

        tfTransactionNo.focusedProperty().addListener(txtMaster_Focus);
        tfIndustry.focusedProperty().addListener(txtMaster_Focus);
        tfCompany.focusedProperty().addListener(txtMaster_Focus);
        tfSupplier.focusedProperty().addListener(txtMaster_Focus);
        tfTrucking.focusedProperty().addListener(txtMaster_Focus);
        taRemarks.focusedProperty().addListener(txtArea_Focus);
        tfReferenceNo.focusedProperty().addListener(txtMaster_Focus);
        tfTerm.focusedProperty().addListener(txtMaster_Focus);
        tfDiscountRate.focusedProperty().addListener(txtMaster_Focus);
        tfDiscountAmount.focusedProperty().addListener(txtMaster_Focus);
//        tfTotal.focusedProperty().addListener(txtMaster_Focus);

//        tfOrderNo.focusedProperty().addListener(txtDetail_Focus);
        tfBarcode.focusedProperty().addListener(txtDetail_Focus);
        tfSupersede.focusedProperty().addListener(txtDetail_Focus);
        tfDescription.focusedProperty().addListener(txtDetail_Focus);
        tfBrand.focusedProperty().addListener(txtDetail_Focus);
        tfModel.focusedProperty().addListener(txtDetail_Focus);
        tfColor.focusedProperty().addListener(txtDetail_Focus);
        tfInventoryType.focusedProperty().addListener(txtDetail_Focus);
        tfMeasure.focusedProperty().addListener(txtDetail_Focus);
        tfCost.focusedProperty().addListener(txtDetail_Focus);
        tfOrderQuantity.focusedProperty().addListener(txtDetail_Focus);
        tfReceiveQuantity.focusedProperty().addListener(txtDetail_Focus);

        tfCompany.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupplier.setOnKeyPressed(this::txtField_KeyPressed);
        tfTrucking.setOnKeyPressed(this::txtField_KeyPressed);
        tfTerm.setOnKeyPressed(this::txtField_KeyPressed);
        tfOrderNo.setOnKeyPressed(this::txtField_KeyPressed);
        tfBarcode.setOnKeyPressed(this::txtField_KeyPressed);
        tfDescription.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupersede.setOnKeyPressed(this::txtField_KeyPressed);

    }

    private void datePicker_Focus(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!newValue) { // Lost focus
            DatePicker datePicker = (DatePicker) ((ReadOnlyBooleanProperty) observable).getBean();
            LocalDate selectedDate = datePicker.getValue();

            if (selectedDate != null) {
                System.out.println("Selected date: " + selectedDate);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate localDate = LocalDate.parse(selectedDate.toString(), formatter);
                String formattedDate = formatter.format(selectedDate);

                switch (datePicker.getId()) {
                    case "dpTransactionDate":
                        oTrans.Master().setTransactionDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        break;
                    case "dpReferenceDate":
                        oTrans.Master().setReferenceDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        break;
                    case "dpExpiryDate":
                        oTrans.Detail(pnDetail).setExpiryDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        break;
                    default:
                        System.out.println("Unknown DatePicker: " + datePicker.getId());
                        break;
                }

                datePicker.setValue(localDate);
            }
        }
    }

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

        dpTransactionDate.focusedProperty().addListener(this::datePicker_Focus);
        dpReferenceDate.focusedProperty().addListener(this::datePicker_Focus);
        dpExpiryDate.focusedProperty().addListener(this::datePicker_Focus);

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
//        tblViewOrderDetails.setItems(details_data);
//        tblViewOrderDetails.autosize();
//        
        filteredDataDetail = new FilteredList<>(details_data, b -> true);
        autoSearch(tfOrderNo);

        SortedList<ModelDeliveryAcceptance_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewOrderDetails.comparatorProperty());
        tblViewOrderDetails.setItems(sortedData);
        tblViewOrderDetails.autosize();
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
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        filteredData = new FilteredList<>(main_data, b -> true);
        SortedList<ModelDeliveryAcceptance_Main> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblViewPuchaseOrder.comparatorProperty());
        tblViewPuchaseOrder.setItems(sortedData);

//        tblViewPuchaseOrder.setItems(main_data);
        tblViewPuchaseOrder.autosize();
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
            tfBarcode.setText(oTrans.Detail(pnDetail).Inventory().getBarCode());
            tfDescription.setText(oTrans.Detail(pnDetail).Inventory().getDescription());
            tfSupersede.setText(oTrans.Detail(pnDetail).Supersede().getBarCode());
            tfBrand.setText(oTrans.Detail(pnDetail).Inventory().Brand().getDescription());
            tfModel.setText(oTrans.Detail(pnDetail).Inventory().Model().getDescription());
            tfColor.setText(oTrans.Detail(pnDetail).Inventory().Color().getDescription());
            tfInventoryType.setText(oTrans.Detail(pnDetail).Inventory().InventoryType().getDescription());
            tfMeasure.setText(oTrans.Detail(pnDetail).Inventory().Measure().getDescription());

            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(oTrans.Detail(pnDetail).getUnitPrce()));

            tfOrderQuantity.setText(String.valueOf(oTrans.Detail(pnDetail).getOrderQty().intValue()));

            tfReceiveQuantity.setText(String.valueOf(oTrans.Detail(pnDetail).getQuantity()));

        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadRecordMaster() {
        try {
            String lsActive = oTrans.Master().getTransactionStatus();
            switch (lsActive) {
                case "0":
                    lblStatus.setText("OPEN");
                    break;
                case "1":
                    lblStatus.setText("CLOSED");
                    break;
                case "2":
                    lblStatus.setText("POSTED");
                    break;
                case "3":
                    lblStatus.setText("CANCELLED");
                    break;
                default:
                    lblStatus.setText("UNKNOWN");
                    break;
            }

            oTrans.computeFields();
            oTrans.Master().setBranchCode(oApp.getBranchCode());
            oTrans.Master().setTransactionDate(oApp.getServerDate());
            oTrans.Master().setIndustryId(oApp.getIndustry());

            // Transaction Date
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(oTrans.Master().getTransactionDate());
            if (!lsTransactionDate.equals("")) {
                Object loTransDate = oTrans.Master().getTransactionDate();
                if (loTransDate == null) {
                    dpTransactionDate.setValue(LocalDate.now());
                } else if (loTransDate instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) loTransDate;
                    LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                    dpTransactionDate.setValue(localDate);
                } else if (loTransDate instanceof Date) {
                    Date sqlDate = (Date) loTransDate;
                    LocalDate localDate = sqlDate.toLocalDate();
                    dpTransactionDate.setValue(localDate);
                }
            }
            //ReferenceDate
            String lsReferenceDate = CustomCommonUtil.formatDateToShortString(oTrans.Master().getReferenceDate());
            if (!oTrans.Master().getReferenceDate().equals("")) {
                Object loReferenceDate = oTrans.Master().getReferenceDate();
                if (loReferenceDate == null) {
                    dpReferenceDate.setValue(LocalDate.now());
                } else if (loReferenceDate instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) loReferenceDate;
                    LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                    dpReferenceDate.setValue(localDate);
                } else if (loReferenceDate instanceof Date) {
                    Date sqlDate = (Date) loReferenceDate;
                    LocalDate localDate = sqlDate.toLocalDate();
                    dpReferenceDate.setValue(localDate);
                } else {
                }
            }

            tfTransactionNo.setText(oTrans.Master().getTransactionNo());
            tfIndustry.setText(oTrans.Master().Industry().getDescription());
            tfCompany.setText(oTrans.Master().Company().getCompanyName());
            tfSupplier.setText(oTrans.Master().Supplier().getCompanyName());
            tfTrucking.setText(oTrans.Master().Trucking().getCompanyName());
            tfTerm.setText(oTrans.Master().Term().getDescription());
            tfReferenceNo.setText(oTrans.Master().getReferenceNo());
            taRemarks.setText(oTrans.Master().getRemarks());

            tfDiscountRate.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(oTrans.Master().getDiscountRate().doubleValue())));
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(oTrans.Master().getDiscount().doubleValue())));
            tfTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(oTrans.Master().getTransactionTotal().doubleValue())));
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    EventHandler<KeyEvent> tableScrollHandler = event -> {
        if (event.isAltDown()) {
            TableView<?> focusedTable = getFocusedTable();
            if (focusedTable != null) {
                switch (event.getCode()) {
                    case UP:
                        scrollTable(focusedTable, -1);
                        event.consume(); // Prevent default behavior
                        break;
                    case DOWN:
                        scrollTable(focusedTable, 1);
                        event.consume(); // Prevent default behavior
                        break;
                    default:
                        break;
                }
            }
        }
    };

    private TableView<?> getFocusedTable() {
        if (tblViewPuchaseOrder.isFocused()) {
            return tblViewPuchaseOrder;
        } else if (tblViewOrderDetails.isFocused()) {
            return tblViewOrderDetails;
        }
        return null; // No table has focus
    }

    private void scrollTable(TableView<?> table, int direction) {
        int rowCount = table.getItems().size();
        if (rowCount == 0) {
            return;
        }

        int currentIndex = table.getSelectionModel().getSelectedIndex();
        int newIndex = currentIndex + direction;

        // Ensure the index is within bounds
        if (newIndex >= 0 && newIndex < rowCount) {
            table.getSelectionModel().clearAndSelect(newIndex);
            table.scrollTo(newIndex);
        }
    }

    private void moveToNextRow(TableView<?> table, TablePosition<?, ?> focusedCell) {
        int nextRow = (focusedCell.getRow() + 1) % table.getItems().size();
        table.getSelectionModel().select(nextRow);
    }

    private void handleTabKey(KeyEvent event) {
        if (event.getCode().toString().equals("TAB")) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();

            if (focusedCell != null) {
                switch (currentTable.getId()) {
                    case "tblViewOrderDetails":
                        System.out.println("Tab pressed in Table 1");
                        moveToNextRow(tblViewOrderDetails, focusedCell);
                        break;
                    case "tblViewPuchaseOrder":
                        System.out.println("Tab pressed in Table 2");
                        moveToNextRow(tblViewPuchaseOrder, focusedCell);
                        break;
                    default:
                        System.out.println("Unknown Table");
                        break;
                }
                event.consume();
            }
        }
    }

    public void initTableOnClick() {

        tblViewOrderDetails.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                pnDetail = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
                loadTableDetail();
            }
        });

        tblViewPuchaseOrder.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                pnMain = tblViewPuchaseOrder.getSelectionModel().getSelectedIndex();
                if (pnMain >= 0) {
                    loadTableDetailFromMain();
                }
            }
        });

        tblViewOrderDetails.setRowFactory(tv -> new TableRow<ModelDeliveryAcceptance_Detail>() {
            @Override
            protected void updateItem(ModelDeliveryAcceptance_Detail item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle(""); // Reset for empty rows
                } else if (highlightedRows.contains(getIndex())) {
                    setStyle("-fx-background-color: #FFCCCC;"); // Light red for specific rows
                } else {
                    setStyle(""); // Default style
                }
            }
        });

        tblViewPuchaseOrder.setOnKeyPressed(tableScrollHandler);
        tblViewOrderDetails.setOnKeyPressed(tableScrollHandler);

        tblViewPuchaseOrder.addEventFilter(KeyEvent.KEY_PRESSED, this::handleTabKey);
        tblViewOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::handleTabKey);

    }

    public void loadTableMain() {
        // Setting data to table detail

        main_data.clear();

        String lsMainDate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Define the format

        try {
            if (!oTrans.Master().getTransactionDate().equals("")) {
                Object loDate = oTrans.Master().getTransactionDate();
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

        //pending
        //retreiving using column index
        for (int lnCtr = 0; lnCtr <= oTrans.getPurchaseOrderCount() - 1; lnCtr++) {
            try {
                main_data.add(new ModelDeliveryAcceptance_Main(String.valueOf(lnCtr + 1),
                        String.valueOf(oTrans.PurchaseOrderList(lnCtr).Supplier().getCompanyName()),
                        String.valueOf(oTrans.PurchaseOrderList(lnCtr).getTransactionDate()),
                        String.valueOf(oTrans.PurchaseOrderList(lnCtr).getTransactionNo())
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
//                loadTableDetailFromMain();

            }
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblViewPuchaseOrder.getSelectionModel().select(pnMain);
            tblViewPuchaseOrder.getFocusModel().focus(pnMain);
//            loadTableDetailFromMain();
        }
        loadTab();

    }

    public void loadTableDetailFromMain() {
        try {
            if (oTrans.getEditMode() == EditMode.ADDNEW || oTrans.getEditMode() == EditMode.UPDATE) {
                poJSON = new JSONObject();
                poJSON = oTrans.addPurchaseOrderToPORDetail(oTrans.PurchaseOrderList(pnMain).getTransactionNo());
                if ("error".equals((String) poJSON.get("message"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }

                loadTableDetail();
            }

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadTableDetail() {
        // Setting data to table detail
        loadRecordMaster();
        int lnCtr;
        details_data.clear();

        try {

            lnCtr = oTrans.getDetailCount() - 1;
            while (lnCtr > 0) {
                if (oTrans.Detail(lnCtr).getStockId() == null || oTrans.Detail(lnCtr).getStockId().equals("")) {
                    oTrans.Detail().remove(lnCtr);
                }
                lnCtr--;
            }

            if ((oTrans.getDetailCount() - 1) >= 0) {
                if (oTrans.Detail(oTrans.getDetailCount() - 1).getStockId() != null && !oTrans.Detail(oTrans.getDetailCount() - 1).getStockId().equals("")) {
                    oTrans.AddDetail();
                }
            }

            double lnTotal = 0.0;
            for (lnCtr = 0; lnCtr < oTrans.getDetailCount(); lnCtr++) {
                try {

                    lnTotal = oTrans.Detail(lnCtr).getUnitPrce().doubleValue() * oTrans.Detail(lnCtr).getQuantity();

                } catch (Exception e) {

                }

                details_data.add(
                        new ModelDeliveryAcceptance_Detail(String.valueOf(lnCtr + 1),
                                String.valueOf(oTrans.Detail(lnCtr).getOrderNo()),
                                String.valueOf(oTrans.Detail(lnCtr).Inventory().getBarCode()),
                                String.valueOf(oTrans.Detail(lnCtr).Inventory().getDescription()),
                                String.valueOf(oTrans.Detail(lnCtr).getUnitPrce()),
                                String.valueOf(oTrans.Detail(lnCtr).getOrderQty().intValue()),
                                String.valueOf(oTrans.Detail(lnCtr).getQuantity()),
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
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        // Manage visibility and managed state of other buttons
//        btnBrowse.setVisible(!lbShow); // Requires no change to state
        btnNew.setVisible(!lbShow);
//        btnRetrieve.setVisible(!lbShow);
        btnClose.setVisible(!lbShow);

        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnCancel.setVisible(lbShow);

//        btnBrowse.setManaged(!lbShow);
        btnNew.setManaged(!lbShow);
//        btnRetrieve.setManaged(!lbShow);
        btnClose.setManaged(!lbShow);

        btnSearch.setManaged(lbShow);
        btnSave.setManaged(lbShow);
        btnCancel.setManaged(lbShow);

        boolean lbShow2 = fnValue == EditMode.READY;

        btnUpdate.setVisible(lbShow2);
        btnPrint.setVisible(lbShow2);
        btnHistory.setVisible(lbShow2);

        btnUpdate.setManaged(lbShow2);
        btnPrint.setManaged(lbShow2);
        btnHistory.setManaged(lbShow2);

        btnClose.setVisible(lbShow2);
        btnClose.setManaged(lbShow2);

//        apBrowse.setDisable(lbShow); // no usage
        apMaster.setDisable(!lbShow);
        apDetail.setDisable(!lbShow);
//        apTable.setDisable(!lbShow); // disable upon for viewing?
//        if (Integer.valueOf(oTrans.getMasterModel().getTransactionStatus()) != 0) {
//            btnVoid.setDisable(false);
//        } else {
//            btnVoid.setDisable(true);
//        }
//        oTrans.setTransType("SP");
    }
//

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

    private void changeTableViewDetail(int index, int limit) {
        tblViewOrderDetails.getSelectionModel().clearSelection();
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, details_data.size());
        try {
            int minIndex = Math.min(toIndex, details_data.size());
            SortedList<ModelDeliveryAcceptance_Detail> sortedData = new SortedList<>(
                    FXCollections.observableArrayList(filteredDataDetail.subList(Math.min(fromIndex, minIndex), minIndex)));
            sortedData.comparatorProperty().bind(tblViewOrderDetails.comparatorProperty());

            tblViewOrderDetails.setItems(FXCollections.observableArrayList(filteredDataDetail.subList(fromIndex, toIndex)));
        } catch (Exception e) {
        }

        tblViewOrderDetails.scrollTo(0);
    }

    public void highlight(TableView<ModelDeliveryAcceptance_Detail> table, int rowIndex) {
        highlightedRows.add(rowIndex);
        table.refresh(); // Refresh to apply changes
    }

    // Method to remove highlight from a specific row in a given TableView
    public void disableHighlight(TableView<ModelDeliveryAcceptance_Detail> table, int rowIndex) {
        highlightedRows.remove(rowIndex);
        table.refresh();
    }

    private void autoSearch(TextField txtField) {
        txtField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredDataDetail.setPredicate(orders -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare order no. and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                return (orders.getIndex02().toLowerCase().contains(lowerCaseFilter)); // Does not match.   
            });
//            changeTableViewDetail(0, details_data.size());
        });
    }
}
