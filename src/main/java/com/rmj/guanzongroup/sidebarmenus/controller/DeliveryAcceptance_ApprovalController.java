/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Attachment;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.TranslateTransition;
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
import javafx.geometry.Bounds;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
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
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_ApprovalController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "Purchasing Order Receiving Approval";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;

    private String lsCompanyId = "";
    private String lsSupplierId = "";

    private ObservableList<ModelDeliveryAcceptance_Main> main_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Detail> details_data = FXCollections.observableArrayList();
    private final ObservableList<ModelDeliveryAcceptance_Attachment> img_data = FXCollections.observableArrayList();
    private FilteredList<ModelDeliveryAcceptance_Main> filteredData;
    private FilteredList<ModelDeliveryAcceptance_Detail> filteredDataDetail;

    private double mouseAnchorX;
    private double mouseAnchorY;
    private double scaleFactor = 1.0;
    private FileChooser fileChooser;
    private int pnAttachment;

    private int currentIndex = 0;
    double ldstackPaneWidth = 0;
    double ldstackPaneHeight = 0;

    private final Map<Integer, List<String>> highlightedRowsMain = new HashMap<>();
    private final Map<Integer, List<String>> highlightedRowsDetail = new HashMap<>();
    private TextField lastFocusedTextField = null;

    private ChangeListener<String> detailSearchListener;
    private ChangeListener<String> mainSearchListener;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail, apAttachments, apAttachmentButtons;

    @FXML
    private TextField tfSearchIndustry, tfSearchSupplier, tfSearchReferenceNo, tfSearchCompany,
            tfTransactionNo, tfCompany, tfSupplier, tfTerm, tfTrucking, tfReferenceNo,
            tfIndustry, tfDiscountAmount, tfTotal, tfDiscountRate, tfBrand, tfModel,
            tfDescription, tfBarcode, tfColor, tfMeasure, tfInventoryType, tfCost,
            tfOrderQuantity, tfReceiveQuantity, tfOrderNo, tfSupersede, tfAttachmentNo,
            tfAttachmentType;

    @FXML
    private Button btnUpdate, btnSearch, btnSave, btnCancel, btnApprove, btnVoid, btnPrint,
            btnReturn, btnHistory, btnRetrieve, btnClose, btnAddAttachment,
            btnRemoveAttachment, btnArrowLeft, btnArrowRight;
    @FXML
    private TableView tblViewOrderDetails, tblViewPuchaseOrder, tblAttachments;

    @FXML
    private TableColumn tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail,
            tblCostDetail, tblOrderQuantityDetail, tblReceiveQuantityDetail, tblTotalDetail,
            tblRowNo, tblSupplier, tblDate, tblReferenceNo,
            tblRowNoAttachment, tblFileNameAttachment;

    @FXML
    private DatePicker dpReferenceDate, dpTransactionDate, dpExpiryDate;

    @FXML
    private HBox hbButtons;

    @FXML
    private Label lblStatus;

    @FXML
    private TextArea taRemarks;

    @FXML
    private Pagination pgPagination;

    @FXML
    private StackPane stackPane1;

    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        poPurchaseReceivingController = new PurchaseOrderReceivingControllers(oApp, null).PurchaseOrderReceiving();
        poJSON = new JSONObject();
        poJSON = poPurchaseReceivingController.InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            System.err.println((String) poJSON.get("message"));
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
        try {
            poPurchaseReceivingController.Master().setIndustryId(oApp.getIndustry());
            poPurchaseReceivingController.Master().Industry().getDescription();
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        }

        initTextFields();
        initDatePickers();
        initMainGrid();
        initDetailsGrid();
        initAttachmentsGrid();
        initTableOnClick();
        clearTextFields();

        initAttachmentPreviewPane();

        initStackPaneListener();
        loadRecordSearch();

        pgPagination.setPageCount(1);

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
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
                        retrievePOR();
                        disableAllHighlight(tblViewPuchaseOrder, highlightedRowsMain);
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
                                poJSON = poPurchaseReceivingController.InitTransaction(); // Initialize transaction
                                if (!"success".equals((String) poJSON.get("result"))) {
                                    System.err.println((String) poJSON.get("message"));
                                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                }

                                lsCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                                lsSupplierId = poPurchaseReceivingController.Master().getSupplierId();

                                pnEditMode = EditMode.UNKNOWN;
                                clearTextFields();
                            }
                        } else {
                            return;
                        }

                        break;
                    case "btnApprove":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to approve transaction?") == true) {
                            poJSON = poPurchaseReceivingController.ApproveTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                lsCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                                lsSupplierId = poPurchaseReceivingController.Master().getSupplierId();

                                clearTextFields();
                                poPurchaseReceivingController.Detail().clear();
                                pnEditMode = EditMode.UNKNOWN;
                                disableAllHighlightByColor(tblViewPuchaseOrder, "#A7C7E7", highlightedRowsMain);
                                highlight(tblViewPuchaseOrder, pnMain, "#C1E1C1", highlightedRowsMain);
                                loadTableDetail();
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnVoid":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to void transaction?") == true) {
                            poJSON = poPurchaseReceivingController.VoidTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                //get last retrieved Company and Supplier
                                lsCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                                lsSupplierId = poPurchaseReceivingController.Master().getSupplierId();

                                clearTextFields();
                                poPurchaseReceivingController.Detail().clear();
                                pnEditMode = EditMode.UNKNOWN;
                                disableAllHighlightByColor(tblViewPuchaseOrder, "#A7C7E7", highlightedRowsMain);
                                highlight(tblViewPuchaseOrder, pnMain, "#FAA0A0", highlightedRowsMain);
                                loadTableDetail();
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnReturn":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to return transaction?") == true) {
                            poJSON = poPurchaseReceivingController.ReturnTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                //get last retrieved Company and Supplier
                                lsCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                                lsSupplierId = poPurchaseReceivingController.Master().getSupplierId();

                                clearTextFields();
                                poPurchaseReceivingController.Detail().clear();
                                pnEditMode = EditMode.UNKNOWN;
                                disableAllHighlightByColor(tblViewPuchaseOrder, "#A7C7E7", highlightedRowsMain);
                                highlight(tblViewPuchaseOrder, pnMain, "#FAC898", highlightedRowsMain);
                                loadTableDetail();
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnAddAttachment":
                        fileChooser = new FileChooser();
                        fileChooser.setTitle("Choose Image");
                        fileChooser.getExtensionFilters().addAll(
                                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
                        );
                        java.io.File selectedFile = fileChooser.showOpenDialog((Stage) btnAddAttachment.getScene().getWindow());

                        if (selectedFile != null) {
                            // Read image from the selected file
                            Path imgPath = selectedFile.toPath();
                            Image loimage = new Image(Files.newInputStream(imgPath));
                            imageView.setImage(loimage);

                            String imgPath2 = selectedFile.toString();
                            img_data.add(new ModelDeliveryAcceptance_Attachment(String.valueOf(img_data.size()), imgPath2));

                            if (img_data.size() > 1) {
                                pnAttachment = img_data.size() - 1;
                            }
                            loadTableAttachment();

                            tblAttachments.getFocusModel().focus(pnAttachment);
                            tblAttachments.getSelectionModel().select(pnAttachment);

                        }
                        break;
                    case "btnRemoveAttachment":
                        img_data.remove(pnAttachment);
                        if (pnAttachment != 0) {
                            pnAttachment -= 1;
                        }
                        loadTableAttachment();
                        initAttachmentsGrid();
                        break;
                    case "btnArrowRight":
                        slideImage(1);
                        break;
                    case "btnArrowLeft":
                        slideImage(-1);
                        break;

                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                        break;
                }
                initButton(pnEditMode);

                if (lsButton.equals("btnUpdate") || lsButton.equals("btnPrint") || lsButton.equals("btnAddAttachment")
                        || lsButton.equals("btnRemoveAttachment") || lsButton.equals("btnArrowRight")
                        || lsButton.equals("btnArrowLeft") || lsButton.equals("btnVoid") || lsButton.equals("btnRetrieve")
                        || lsButton.equals("btnApprove") || lsButton.equals("btnReturn")) {

                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }

            }
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void tblAttachments_Clicked(MouseEvent event) {
        pnAttachment = tblAttachments.getSelectionModel().getSelectedIndex();
        if (pnAttachment >= 0) {
            loadRecordAttachment();
            resetImageBounds();
        }
    }

    public void retrievePOR() {
        poJSON = new JSONObject();

        String lsMessage = "";
        poJSON.put("result", "success");

        if ("success".equals((String) poJSON.get("result"))) {
            poJSON = poPurchaseReceivingController.loadPurchaseOrderReceiving(true, poPurchaseReceivingController.Master().getReferenceNo());
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

    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {

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
                // remove master transNox if contains blank
                case "tfSearchCompany":
                    if (lsValue.equals("")) {
//                            poJSON = poPurchaseReceivingController.CancelTransaction("");
                    }
                    break;
                case "tfSearchSupplier":
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setStockId("");
                    }
                    break;
                case "tfSearchReferenceNo":
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReceivingController.Detail(pnDetail).setStockId("");
                    }
                    break;
                case "tfAttachmentNo":
                    break;
                case "tfAttachmentType":
                    break;
            }
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
                        case "tfSearchCompany":
                            poJSON = poPurchaseReceivingController.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfCompany.setText("");
                                break;
                            }
                            retrievePOR();
                            loadRecordSearch();
                            return;
                        case "tfSearchSupplier":
                            poJSON = poPurchaseReceivingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupplier.setText("");
                                break;
                            }
                            retrievePOR();
                            loadRecordSearch();
                            return;
                        case "tfSearchReferenceNo":
                            poPurchaseReceivingController.Master().setTransactionNo(lsValue);
                            retrievePOR();
                            return;
                        case "tfCompany":
                            /*search company*/
                            poJSON = poPurchaseReceivingController.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfCompany.setText("");
                                break;
                            }
                            break;

                        case "tfSupplier":
                            poJSON = poPurchaseReceivingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupplier.setText("");
                                break;
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
                        case "tfBarcode":
                            poJSON = poPurchaseReceivingController.SearchBarcode(lsValue, true, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfBarcode.setText("");
                                break;
                            }
                            break;

                        case "tfDescription":
                            poJSON = poPurchaseReceivingController.SearchDescription(lsValue, false, pnDetail);

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
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
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

    public void loadTableMain() {
        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewPuchaseOrder.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        main_data.clear();

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);

                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
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
                    for (int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingCount() - 1; lnCtr++) {
                        try {
                            main_data.add(new ModelDeliveryAcceptance_Main(String.valueOf(lnCtr + 1),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).Supplier().getCompanyName()),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).getTransactionDate()),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).getTransactionNo())
                            ));
                        } catch (SQLException ex) {
                            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (GuanzonException ex) {
                            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
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
                placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed
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

    public void loadRecordSearch() {
        try {

            tfSearchIndustry.setText(poPurchaseReceivingController.Master().Industry().getDescription());
            tfSearchCompany.setText(poPurchaseReceivingController.Master().Company().getCompanyName());
            tfSearchSupplier.setText(poPurchaseReceivingController.Master().Supplier().getCompanyName());
            tfSearchReferenceNo.setText(poPurchaseReceivingController.Master().getReferenceNo());
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadRecordAttachment() {

        if (pnAttachment >= 0) {
            tfAttachmentNo.setText("");
            tfAttachmentType.setText("");
            try {
                String filePath = (String) img_data.get(pnAttachment).getIndex02();

                if (filePath.length() != 0) {
                    Path imgPath = Paths.get(filePath);
                    String convertedPath = imgPath.toUri().toString();
                    Image loimage = new Image(convertedPath);
                    imageView.setImage(loimage);
                    adjustImageSize(loimage);
                    stackPaneClip();
                    stackPaneClip();
                } else {
                    imageView.setImage(null);
                    stackPaneClip();
                }

            } catch (Exception e) {
                imageView.setImage(null);
                stackPaneClip();
            }
        } else {
            imageView.setImage(null);
            stackPaneClip();
            pnAttachment = 0;

        }

    }

    public void loadRecordDetail() {
        try {
            boolean lbFields = (poPurchaseReceivingController.Detail(pnDetail).getOrderNo().equals("") || poPurchaseReceivingController.Detail(pnDetail).getOrderNo() == null);
            tfBarcode.setDisable(!lbFields);
            tfDescription.setDisable(!lbFields);

            if (lbFields) {
                tfBarcode.getStyleClass().remove("DisabledTextField");
                tfDescription.getStyleClass().remove("DisabledTextField");
            } else {
                tfBarcode.getStyleClass().add("DisabledTextField");
                tfDescription.getStyleClass().add("DisabledTextField");
            }

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
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadRecordMaster() {
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
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadTableDetailFromMain() {
        try {
            if (poPurchaseReceivingController.getEditMode() == EditMode.READY || poPurchaseReceivingController.getEditMode() == EditMode.UPDATE) {
                poJSON = new JSONObject();

                poJSON = poPurchaseReceivingController.OpenTransaction(poPurchaseReceivingController.PurchaseOrderReceivingList(pnMain).getTransactionNo());
                if ("error".equals((String) poJSON.get("message"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }

                disableAllHighlightByColor(tblViewPuchaseOrder, "#A7C7E7", highlightedRowsMain);
                highlight(tblViewPuchaseOrder, pnMain, "#A7C7E7", highlightedRowsMain);

                loadTableDetail();
            }

        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void loadTableDetail() {
        // Setting data to table detail
        loadRecordMaster();
        details_data.clear();
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

                    try {
                        
                        if(pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE){
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
                        Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(DeliveryAcceptance_ApprovalController.class.getName()).log(Level.SEVERE, null, ex);
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

    private void loadTableAttachment() {
//        img_data.clear(); should have data from class before calling this clear
//        for (int i = 0; i < img_data.size(); i++) {
//            img_data.add(new ModelDeliveryAcceptance_Attachment(String.valueOf(i), img_data.get(i).getIndex2()));
//        }
        loadRecordAttachment();

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

        dpTransactionDate.focusedProperty().addListener(datepicker_Focus);
        dpReferenceDate.focusedProperty().addListener(datepicker_Focus);
        dpExpiryDate.focusedProperty().addListener(datepicker_Focus);
    }

    public void initTextFields() {
        tfSearchSupplier.focusedProperty().addListener(txtField_Focus);
        tfSearchReferenceNo.focusedProperty().addListener(txtField_Focus);
        tfAttachmentNo.focusedProperty().addListener(txtField_Focus);
        tfAttachmentType.focusedProperty().addListener(txtField_Focus);

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

        tfSearchCompany.setOnKeyPressed(this::txtField_KeyPressed);
        tfSearchSupplier.setOnKeyPressed(this::txtField_KeyPressed);
        tfSearchReferenceNo.setOnKeyPressed(this::txtField_KeyPressed);

        tfCompany.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupplier.setOnKeyPressed(this::txtField_KeyPressed);
        tfTrucking.setOnKeyPressed(this::txtField_KeyPressed);
        tfTerm.setOnKeyPressed(this::txtField_KeyPressed);
        tfOrderNo.setOnKeyPressed(this::txtField_KeyPressed);
        tfBarcode.setOnKeyPressed(this::txtField_KeyPressed);
        tfDescription.setOnKeyPressed(this::txtField_KeyPressed);
        tfSupersede.setOnKeyPressed(this::txtField_KeyPressed);
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
                    List<String> colors = highlightedRowsMain.get(getIndex());
                    if (!colors.isEmpty()) {
                        setStyle("-fx-background-color: " + colors.get(colors.size() - 1) + ";"); // Apply the latest color
                    }
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
                    List<String> colors = highlightedRowsDetail.get(getIndex());
                    if (!colors.isEmpty()) {
                        setStyle("-fx-background-color: " + colors.get(colors.size() - 1) + ";"); // Apply the latest color
                    }
                } else {
                    setStyle(""); // Default style
                }
            }
        });

        tblViewOrderDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
    }

    private void initButton(int fnValue) {

        boolean lbShow1 = (fnValue == EditMode.UPDATE);
        boolean lbShow2 = (fnValue == EditMode.READY || fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        // Manage visibility and managed state of other buttons
        //Update 
        btnSearch.setVisible(lbShow1);
        btnSearch.setManaged(lbShow1);
        btnSave.setVisible(lbShow1);
        btnSave.setManaged(lbShow1);
        btnCancel.setVisible(lbShow1);
        btnCancel.setManaged(lbShow1);

        //Ready || Update
        btnReturn.setVisible(lbShow2);
        btnReturn.setManaged(lbShow2);

        //Ready
        btnPrint.setVisible(lbShow3);
        btnPrint.setManaged(lbShow3);
        btnUpdate.setVisible(lbShow3);
        btnUpdate.setManaged(lbShow3);
        btnHistory.setVisible(lbShow3);
        btnHistory.setManaged(lbShow3);
        btnApprove.setVisible(lbShow3);
        btnApprove.setManaged(lbShow3);
        btnVoid.setVisible(lbShow3);
        btnVoid.setManaged(lbShow3);

        //Unkown || Ready
        btnClose.setVisible(lbShow4);
        btnClose.setManaged(lbShow4);

        btnAddAttachment.setDisable(!lbShow2);
        btnRemoveAttachment.setDisable(!lbShow2);

        apMaster.setDisable(!lbShow1);
        apDetail.setDisable(!lbShow1);
        apAttachments.setDisable(!lbShow1);

        switch (poPurchaseReceivingController.Master().getTransactionStatus()) {
            case PurchaseOrderReceivingStatus.APPROVED:
            case PurchaseOrderReceivingStatus.VOID:
                btnApprove.setVisible(false);
                btnApprove.setManaged(false);
                btnUpdate.setVisible(false);
                btnUpdate.setManaged(false);
                btnReturn.setVisible(false);
                btnReturn.setManaged(false);
                btnVoid.setVisible(false);
                btnVoid.setManaged(false);
                break;
        }
    }

    private void initStackPaneListener() {
        stackPane1.widthProperty().addListener((observable, oldValue, newWidth) -> {
            double computedWidth = newWidth.doubleValue();
            ldstackPaneWidth = computedWidth;

        });
        stackPane1.heightProperty().addListener((observable, oldValue, newHeight) -> {
            double computedHeight = newHeight.doubleValue();
            ldstackPaneHeight = computedHeight;
            loadTableAttachment();
            loadRecordAttachment();
            initAttachmentsGrid();
        });
    }

    private void initAttachmentPreviewPane() {
        stackPane1.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            stackPane1.setClip(new javafx.scene.shape.Rectangle(
                    newBounds.getMinX(),
                    newBounds.getMinY(),
                    newBounds.getWidth(),
                    newBounds.getHeight()
            ));
        });
        imageView.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            scaleFactor = Math.max(0.5, Math.min(scaleFactor * (delta > 0 ? 1.1 : 0.9), 5.0));
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
        });

        imageView.setOnMousePressed((MouseEvent event) -> {
            mouseAnchorX = event.getSceneX() - imageView.getTranslateX();
            mouseAnchorY = event.getSceneY() - imageView.getTranslateY();
        });

        imageView.setOnMouseDragged((MouseEvent event) -> {
            double translateX = event.getSceneX() - mouseAnchorX;
            double translateY = event.getSceneY() - mouseAnchorY;
            imageView.setTranslateX(translateX);
            imageView.setTranslateY(translateY);
        });

        stackPane1.widthProperty().addListener((observable, oldValue, newWidth) -> {
            double computedWidth = newWidth.doubleValue();
            ldstackPaneWidth = computedWidth;

        });
        stackPane1.heightProperty().addListener((observable, oldValue, newHeight) -> {
            double computedHeight = newHeight.doubleValue();
            ldstackPaneHeight = computedHeight;

            //Placed to get height and width of stack pane in computed size before loading the image
            initStackPaneListener();
            initAttachmentsGrid();
        });

    }

    private void adjustImageSize(Image image) {
        double imageRatio = image.getWidth() / image.getHeight();
        double containerRatio = ldstackPaneWidth / ldstackPaneHeight;

        // Unbind before setting new values
        imageView.fitWidthProperty().unbind();
        imageView.fitHeightProperty().unbind();

        if (imageRatio > containerRatio) {
            // Image is wider than container → fit width
            imageView.setFitWidth(ldstackPaneWidth);
            imageView.setFitHeight(ldstackPaneWidth / imageRatio);
        } else {
            // Image is taller than container → fit height
            imageView.setFitHeight(ldstackPaneHeight);
            imageView.setFitWidth(ldstackPaneHeight * imageRatio);
        }

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }

    public void initAttachmentsGrid() {
        /*FOCUS ON FIRST ROW*/
        tblRowNoAttachment.setStyle("-fx-alignment: CENTER;-fx-padding: 0 5 0 5;");
        tblFileNameAttachment.setStyle("-fx-alignment: CENTER;-fx-padding: 0 5 0 5;");

        tblRowNoAttachment.setCellValueFactory(new PropertyValueFactory<ModelDeliveryAcceptance_Attachment, String>("index01"));
        tblFileNameAttachment.setCellValueFactory(new PropertyValueFactory<ModelDeliveryAcceptance_Attachment, String>("index02"));

        tblAttachments.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblAttachments.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        tblAttachments.setItems(img_data);

        if (pnAttachment < 0 || pnAttachment >= img_data.size()) {
            if (!img_data.isEmpty()) {
                /* FOCUS ON FIRST ROW */
                tblAttachments.getSelectionModel().select(0);
                tblAttachments.getFocusModel().focus(0);
                pnAttachment = tblAttachments.getSelectionModel().getSelectedIndex();
            }
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblAttachments.getSelectionModel().select(pnAttachment);
            tblAttachments.getFocusModel().focus(pnAttachment);
        }

    }

    public void slideImage(int direction) {
        currentIndex = pnAttachment;
        int newIndex = currentIndex + direction;

        if (newIndex != -1 && (newIndex <= img_data.size() - 1)) {
            ModelDeliveryAcceptance_Attachment image = img_data.get(newIndex);
            Path filePath = Paths.get(image.getIndex02());
            String convertedPath = filePath.toUri().toString();

            Image newImage = new Image(convertedPath);
            // Create a transition animation
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), imageView);
            slideOut.setByX(direction * -400); // Move left or right

            slideOut.setOnFinished(event -> {
                imageView.setImage(newImage);
                imageView.setTranslateX(direction * 400);
                adjustImageSize(newImage);

                TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), imageView);
                slideIn.setToX(0);
                slideIn.play();
            });

            slideOut.play();

            tblAttachments.getFocusModel().focus(newIndex);
            tblAttachments.getSelectionModel().select(newIndex);
            pnAttachment = newIndex;

            if (isImageViewOutOfBounds(imageView, stackPane1)) {
                resetImageBounds();
            }
        }
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

    }

    private boolean isImageViewOutOfBounds(ImageView imageView, StackPane stackPane) {
        Bounds clipBounds = stackPane.getClip().getBoundsInParent();
        Bounds imageBounds = imageView.getBoundsInParent();

        return imageBounds.getMaxX() < clipBounds.getMinX()
                || imageBounds.getMinX() > clipBounds.getMaxX()
                || imageBounds.getMaxY() < clipBounds.getMinY()
                || imageBounds.getMinY() > clipBounds.getMaxY();
    }

    public void resetImageBounds() {
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        stackPane1.setAlignment(imageView, javafx.geometry.Pos.CENTER);
    }

    private TableView getFocusedTable() {
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

    private void stackPaneClip() {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(
                stackPane1.getWidth() - 8, // Subtract 10 for padding (5 on each side)
                stackPane1.getHeight() - 8 // Subtract 10 for padding (5 on each side)
        );
        clip.setArcWidth(8); // Optional: Rounded corners for aesthetics
        clip.setArcHeight(8);
        clip.setLayoutX(4); // Set padding offset for X
        clip.setLayoutY(4); // Set padding offset for Y
        stackPane1.setClip(clip);

    }

    public void clearTextFields() {
        dpTransactionDate.setValue(null);
        dpReferenceDate.setValue(null);
        dpExpiryDate.setValue(null);

        tfSearchIndustry.clear();
        tfSearchCompany.clear();
        tfSearchSupplier.clear();
        tfSearchReferenceNo.clear();
        tfAttachmentNo.clear();
        tfAttachmentType.clear();

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

        tfAttachmentNo.clear();
        tfAttachmentType.clear();

//        loadRecordMaster();
//        loadTableDetail();
//        loadTableMain();
    }

    public void generateAttachment() {
        img_data.add(new ModelDeliveryAcceptance_Attachment("0", "C:/Users/User/Downloads/a4-blank-template_page-0001.jpg"));

    }

// Generic method to highlight with specific color
    public <T> void highlight(TableView<T> table, int rowIndex, String color, Map<Integer, List<String>> highlightMap) {
        highlightMap.computeIfAbsent(rowIndex, k -> new ArrayList<>()).add(color);
        table.refresh(); // Refresh to apply changes
    }

// Generic method to remove highlight from a specific row
    public <T> void disableHighlight(TableView<T> table, int rowIndex, Map<Integer, List<String>> highlightMap) {
        highlightMap.remove(rowIndex);
        table.refresh();
    }

// Generic method to remove all highlights
    public <T> void disableAllHighlight(TableView<T> table, Map<Integer, List<String>> highlightMap) {
        highlightMap.clear();
        table.refresh();
    }

// Generic method to remove all highlights of a specific color
    public <T> void disableAllHighlightByColor(TableView<T> table, String color, Map<Integer, List<String>> highlightMap) {
        highlightMap.forEach((key, colors) -> colors.removeIf(c -> c.equals(color)));
        highlightMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
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
