/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReceiving;
import org.guanzon.cas.purchasing.services.PurchaseOrderReceivingControllers;
import org.guanzon.cas.purchasing.status.PurchaseOrderReceivingStatus;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_EntryCarController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "Purchasing Order Receiving Entry Car";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;

    private String lsCompanyId = "";
    private String lsSupplierId = "";

    private ObservableList<ModelDeliveryAcceptance_Detail> details_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Main> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelDeliveryAcceptance_Main> filteredData;
    private FilteredList<ModelDeliveryAcceptance_Detail> filteredDataDetail;

    private final Map<Integer, String> highlightedRowsMain = new HashMap<>();
    private final Map<Integer, String> highlightedRowsDetail = new HashMap<>();
    private TextField lastFocusedTextField = null;

    private double xOffset = 0;
    private double yOffset = 0;

    private Stage dialogStage = null;
    private ChangeListener<String> detailSearchListener;
    private ChangeListener<String> mainSearchListener;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail;

    @FXML
    private HBox hbButtons, hboxid;

    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnPrint, btnHistory, btnRetrieve, btnClose, btnSerials;

    @FXML
    private Label lblStatus;

    @FXML
    private TextField tfTransactionNo, tfIndustry, tfCompany, tfSupplier, tfTrucking, tfReferenceNo, tfTerm, tfDiscountRate,
            tfDiscountAmount, tfTotal, tfOrderNo, tfBrand, tfModel, tfColor, tfInventoryType,
            tfMeasure, tfCost, tfOrderQuantity, tfReceiveQuantity, tfModelVariant; //tfBarcode, tfSupersede, tfDescription,;

    @FXML
    private CheckBox cbPreOwned;

    @FXML
    private TextArea taRemarks;

    @FXML
    private DatePicker dpTransactionDate, dpReferenceDate;

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

            loadRecordMaster();
            loadTableDetail();

            pgPagination.setPageCount(1);

            pnEditMode = poPurchaseReceivingController.getEditMode();
            initButton(pnEditMode);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
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
                        poJSON = poPurchaseReceivingController.searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        poPurchaseReceivingController.resetOthers();
                        pnEditMode = poPurchaseReceivingController.getEditMode();
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
                            closeSerialDialog();
                            appUnload.unloadForm(apMainAnchor, oApp, pxeModuleName);
                        } else {
                            return;
                        }
                        break;
                    case "btnSerials":
                        showSerialDialog();
                        return;
                    case "btnNew":
                        poJSON = poPurchaseReceivingController.NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        if (!lsCompanyId.isEmpty()) {
                            poPurchaseReceivingController.SearchCompany(lsCompanyId, true);
                        }
                        if (!lsSupplierId.isEmpty()) {
                            poPurchaseReceivingController.SearchSupplier(lsSupplierId, true);
                        }
                        poPurchaseReceivingController.resetOthers();
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
                            lsCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                            lsSupplierId = poPurchaseReceivingController.Master().getSupplierId();

                            poPurchaseReceivingController.Detail().clear();
                            pnEditMode = EditMode.UNKNOWN;
                            clearTextFields();
                            loadTableDetail();
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
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                //get last retrieved Company and Supplier
                                lsCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                                lsSupplierId = poPurchaseReceivingController.Master().getSupplierId();

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
                if (lsButton.equals("btnUpdate") || lsButton.equals("btnPrint") || lsButton.equals("btnRetrieve") || lsButton.equals("btnCancel")) {

                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeSerialDialog() {
        if (dialogStage != null && dialogStage.isShowing()) {
            dialogStage.close();
            dialogStage = null;
        } else {
        }
    }

    public void showSerialDialog() {
        poJSON = new JSONObject();
        try {
            if (poPurchaseReceivingController.Detail(pnDetail).getQuantity().intValue() == 0) {
                ShowMessageFX.Warning(null, pxeModuleName, "Received quantity cannot be empty.");
                return;
            }

            //Populate Purchase Order Receiving Detail
            poJSON = poPurchaseReceivingController.getPurchaseOrderReceivingSerial(pnDetail + 1);
            if ("error".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                return;
            }

//             Check if the dialog is already open
            if (dialogStage != null) {
                if (dialogStage.isShowing()) {
                    dialogStage.toFront();
                    return;
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_SerialCAR.fxml"));
            DeliveryAcceptance_SerialCarController controller = new DeliveryAcceptance_SerialCarController();
            loader.setController(controller);

            if (controller != null) {
                controller.setGRider(oApp);
                controller.setObject(poPurchaseReceivingController);
                controller.setEntryNo(pnDetail + 1);
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

            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UNDECORATED);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle("Inventory Serial");
            dialogStage.setScene(new Scene(root));

            // Clear the reference when closed
            dialogStage.setOnHidden(event -> dialogStage = null);
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrievePO() {
        poJSON = new JSONObject();
        poJSON = poPurchaseReceivingController.getApprovedPurchaseOrder();
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            return;
        }

        loadTableMain();
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
                case "tfBrand":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setStockId("");
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setBrandId("");
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setModelVariantId("");
                    }
                case "tfModel":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setStockId("");
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setModelVariantId("");
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
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Discount Rate");
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
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid Discount Amount");
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
                                break;
                            }
                            if (!poPurchaseReceivingController.Master().getSupplierId().equals("")) {
                                retrievePO();
                            }
                            break;

                        case "tfSupplier":
                            poJSON = poPurchaseReceivingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupplier.setText("");
                                break;
                            }
                            if (!poPurchaseReceivingController.Master().getCompanyId().equals("")) {
                                retrievePO();
                            }
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
                            break;
                        case "tfOrderNo":

                            break;
                        case "tfBrand":
                            poJSON = poPurchaseReceivingController.SearchBrand(lsValue, false, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfBrand.setText("");
                                break;
                            }
                            break;
                        case "tfModel":
                            poJSON = poPurchaseReceivingController.SearchModel(lsValue, false, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfModel.setText("");
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
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    ChangeListener<Boolean> datepicker_Focus = (observable, oldValue, newValue) -> {
        poJSON = new JSONObject();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (!newValue) { // Lost focus
                DatePicker datePicker = (DatePicker) ((javafx.beans.property.ReadOnlyBooleanProperty) observable).getBean();
                String lsID = datePicker.getId();
                LocalDate selectedDate = datePicker.getValue();
                LocalDate localbdate = LocalDate.parse(selectedDate.toString(), formatter);
                String formattedDate = formatter.format(selectedDate);
                LocalDate currentDate = LocalDate.now();

                LocalDate localDate = (selectedDate != null) ? LocalDate.parse(selectedDate.toString(), formatter) : null;
                switch (lsID) {
                    case "dpTransactionDate":
                        if (selectedDate.isAfter(currentDate)) {
                            poJSON.put("result", "error");
                            poJSON.put("message", "Future dates are not allowed.");
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        } else {
                            poPurchaseReceivingController.Master().setTransactionDate((SQLUtil.toDate(formattedDate, "yyyy-MM-dd")));
                            if (localDate != null) {
                                datePicker.setValue(localDate);
                            }
                        }
                        break;
                    case "dpReferenceDate":
                        if (selectedDate.isAfter(currentDate)) {
                            poJSON.put("result", "error");
                            poJSON.put("message", "Future dates are not allowed.");
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        } else {
                            poPurchaseReceivingController.Master().setReferenceDate((SQLUtil.toDate(formattedDate, "yyyy-MM-dd")));
                            if (localDate != null) {
                                datePicker.setValue(localDate);
                            }
                        }
                        break;
                    default:
                        System.out.println("Unknown DatePicker.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void loadRecordDetail() {
        try {
            boolean lbFields = (poPurchaseReceivingController.Detail(pnDetail).getOrderNo().equals("") || poPurchaseReceivingController.Detail(pnDetail).getOrderNo() == null);
            tfBrand.setDisable(!lbFields);
            tfModel.setDisable(!lbFields);

            if (lbFields) {
                tfBrand.getStyleClass().remove("DisabledTextField");
                tfModel.getStyleClass().remove("DisabledTextField");
            } else {
                tfBrand.getStyleClass().add("DisabledTextField");
                tfModel.getStyleClass().add("DisabledTextField");
            }

            if (poPurchaseReceivingController.Detail(pnDetail).getStockId() != null && !poPurchaseReceivingController.Detail(pnDetail).getStockId().equals("")) {
                poPurchaseReceivingController.Detail(pnDetail).setBrandId(poPurchaseReceivingController.Detail(pnDetail).Inventory().getBrandId());
                poPurchaseReceivingController.Detail(pnDetail).setModelVariantId(poPurchaseReceivingController.Detail(pnDetail).Inventory().getVariantId());
            }

            tfBrand.setText(poPurchaseReceivingController.Detail(pnDetail).Brand().getDescription());
            tfModelVariant.setText(poPurchaseReceivingController.Detail(pnDetail).ModelVariant().getDescription());

            tfModel.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Model().getDescription());
            tfColor.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Color().getDescription());
            tfInventoryType.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().InventoryType().getDescription());
            tfMeasure.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Measure().getDescription());

            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(pnDetail).getUnitPrce()));
            tfOrderQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getOrderQty().intValue()));
            tfReceiveQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity()));

        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadRecordMaster() {
        boolean lbDisable = pnEditMode == EditMode.UPDATE;
        if (lbDisable) {
            tfCompany.setDisable(lbDisable);
            tfCompany.setDisable(lbDisable);
            tfCompany.getStyleClass().add("DisabledTextField");
            tfSupplier.getStyleClass().add("DisabledTextField");
        } else {
            tfCompany.setDisable(lbDisable);
            tfCompany.setDisable(lbDisable);
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
            poPurchaseReceivingController.Master().setBranchCode(oApp.getBranchCode());
            poPurchaseReceivingController.Master().setTransactionDate(oApp.getServerDate());
            poPurchaseReceivingController.Master().setIndustryId(oApp.getIndustry());

            // Transaction Date
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getTransactionDate());
            if (!lsTransactionDate.equals("")) {
                Object lpoPurchaseReceivingControllerDate = poPurchaseReceivingController.Master().getTransactionDate();
                if (lpoPurchaseReceivingControllerDate == null) {
                    dpTransactionDate.setValue(LocalDate.now());
                } else if (lpoPurchaseReceivingControllerDate instanceof Timestamp) {
                    Timestamp timestamp = (Timestamp) lpoPurchaseReceivingControllerDate;
                    LocalDate localDate = timestamp.toLocalDateTime().toLocalDate();
                    dpTransactionDate.setValue(localDate);
                } else if (lpoPurchaseReceivingControllerDate instanceof Date) {
                    Date sqlDate = (Date) lpoPurchaseReceivingControllerDate;
                    LocalDate localDate = sqlDate.toLocalDate();
                    dpTransactionDate.setValue(localDate);
                }
            }
            //ReferenceDate
            String lsReferenceDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getReferenceDate());
            if (!poPurchaseReceivingController.Master().getReferenceDate().equals("")) {
                Object loReferenceDate = poPurchaseReceivingController.Master().getReferenceDate();
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
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadTableMain() {
        // Setting data to table detail
        main_data.clear();
        String lsMainDate = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Define the format

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

        //pending
        //retreiving using column index
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

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadTableDetail() {
        // Setting data to table detail
        loadRecordMaster();
        int lnCtr;
        details_data.clear();
        disableAllHighlight(tblViewOrderDetails, highlightedRowsDetail);

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
                lnTotal = poPurchaseReceivingController.Detail(lnCtr).getUnitPrce().doubleValue() * poPurchaseReceivingController.Detail(lnCtr).getQuantity().doubleValue();

                if ((!poPurchaseReceivingController.Detail(lnCtr).getOrderNo().equals("") && poPurchaseReceivingController.Detail(lnCtr).getOrderNo() != null)
                        && poPurchaseReceivingController.Detail(lnCtr).getOrderQty().intValue() != poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue()) {
                    highlight(tblViewOrderDetails, lnCtr, "#FAA0A0", highlightedRowsDetail);
                }

                if (poPurchaseReceivingController.Detail(lnCtr).getOrderNo() != null && !poPurchaseReceivingController.Detail(lnCtr).getOrderNo().equals("")) {
                    cbPreOwned.setSelected(poPurchaseReceivingController.Detail(lnCtr).PurchaseOrderMaster().getPreOwned());
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
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void clearTextFields() {

        dpTransactionDate.setValue(null);
        dpReferenceDate.setValue(null);

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
        tfModelVariant.clear();
        tfBrand.clear();
        tfModel.clear();
        tfColor.clear();
        tfInventoryType.clear();
        tfMeasure.clear();
        tfCost.clear();
        tfOrderQuantity.clear();
        tfReceiveQuantity.clear();

        cbPreOwned.setSelected(false);

        loadRecordMaster();
        loadTableDetail();
        loadTableMain();
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

        tfModelVariant.focusedProperty().addListener(txtDetail_Focus);
        tfCost.focusedProperty().addListener(txtDetail_Focus);
        tfOrderQuantity.focusedProperty().addListener(txtDetail_Focus);
        tfReceiveQuantity.focusedProperty().addListener(txtDetail_Focus);

        tfCompany.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupplier.setOnKeyPressed(this::txtField_KeyPressed);
        tfTrucking.setOnKeyPressed(this::txtField_KeyPressed);
        tfTerm.setOnKeyPressed(this::txtField_KeyPressed);
        tfBrand.setOnKeyPressed(this::txtField_KeyPressed);
        tfModel.setOnKeyPressed(this::txtField_KeyPressed);
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

        dpTransactionDate.focusedProperty().addListener(datepicker_Focus);
        dpReferenceDate.focusedProperty().addListener(datepicker_Focus);
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

//        
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

        if (tblViewPuchaseOrder != null) {
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

            tblViewPuchaseOrder.setItems(main_data);
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
        btnSerials.setVisible(lbShow);
        btnSerials.setManaged(lbShow);
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

        apMaster.setDisable(!lbShow);
        apDetail.setDisable(!lbShow);

        switch (poPurchaseReceivingController.Master().getTransactionStatus()) {
            case PurchaseOrderReceivingStatus.APPROVED:
            case PurchaseOrderReceivingStatus.VOID:
                btnUpdate.setVisible(false);
                btnUpdate.setManaged(false);
                break;
        }
    }

    private void loadTab() {
        int totalPage = (int) (Math.ceil(main_data.size() * 1.0 / ROWS_PER_PAGE));
        pgPagination.setPageCount(totalPage);
        pgPagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pgPagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
    }

    private TableView<?> getFocusedTable() {
        if (tblViewPuchaseOrder.isFocused()) {
            return tblViewPuchaseOrder;
        } else if (tblViewOrderDetails.isFocused()) {
            return tblViewOrderDetails;
        }
        return null; // No table has focus
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
                txtField.textProperty().removeListener(mainSearchListener);
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
