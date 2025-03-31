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
import java.net.URL;
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
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
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

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptance_HistoryMonarchHospitalityController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "Purchasing Order Receiving History MH";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;

    private String psCompanyId = "";
    private String psSupplierId = "";

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
    private Button btnPrint, btnHistory, btnRetrieve, btnClose, btnArrowLeft, btnArrowRight;

    @FXML
    private TableView tblViewOrderDetails, tblViewPuchaseOrder, tblAttachments;

    @FXML
    private TableColumn tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail,
            tblCostDetail, tblOrderQuantityDetail, tblReceiveQuantityDetail, tblTotalDetail,
            tblRowNo, tblSupplier, tblDate, tblReferenceNo,
            tblRowNoAttachment, tblFileNameAttachment;

    @FXML
    private DatePicker dpReferenceDate, dpTransactionDate;

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
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

        initTextFields();
        initDatePickers();
        initMainGrid();
        initDetailsGrid();
        initAttachmentsGrid();
        initTableOnClick();
        clearTextFields();

        initAttachmentPreviewPane();

        poPurchaseReceivingController.Master().setBranchCode(oApp.getBranchCode());
        poPurchaseReceivingController.Master().setIndustryId(oApp.getIndustry());
        try {
            poPurchaseReceivingController.Master().setTransactionDate(oApp.getServerDate());
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryMonarchHospitalityController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

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

                    break;
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

            if (lsButton.equals("btnPrint")
                    || lsButton.equals("btnArrowRight")
                    || lsButton.equals("btnArrowLeft") || lsButton.equals("btnRetrieve")) {

            } else {
                loadRecordMaster();
                loadTableDetail();
            }

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
            poJSON = poPurchaseReceivingController.loadPurchaseOrderReceiving("history", psCompanyId, psSupplierId, tfSearchReferenceNo.getText());

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
                case "tfSearchCompany":
                    if (lsValue.equals("")) {
                        psCompanyId = "";
                    }
                    break;
                case "tfSearchSupplier":
                    if (lsValue.equals("")) {
                        psSupplierId = "";
                    }
                    break;
                case "tfSearchReferenceNo":
                    break;
                case "tfAttachmentNo":
                    break;
                case "tfAttachmentType":
                    break;
            }
            if (lsTxtFieldID.equals("tfSearchCompany") || lsTxtFieldID.equals("tfSearchSupplier")
                    || lsTxtFieldID.equals("tfSearchReferenceNo")) {
                loadRecordSearch();
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
                                psCompanyId = "";
                                break;
                            } else {
                                psCompanyId = poPurchaseReceivingController.Master().getCompanyId();
                            }

                            retrievePOR();
                            loadRecordSearch();
                            return;
                        case "tfSearchSupplier":
                            poJSON = poPurchaseReceivingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupplier.setText("");
                                psSupplierId = "";
                                break;
                            } else {
                                psSupplierId = poPurchaseReceivingController.Master().getSupplierId();
                            }
                            retrievePOR();
                            loadRecordSearch();
                            return;
                        case "tfSearchReferenceNo":
                            poPurchaseReceivingController.Master().setTransactionNo(lsValue);
                            retrievePOR();
                            return;

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
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
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
                    for (int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingCount() - 1; lnCtr++) {
                        try {
                            main_data.add(new ModelDeliveryAcceptance_Main(String.valueOf(lnCtr + 1),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).Supplier().getCompanyName()),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).getTransactionDate()),
                                    String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).getTransactionNo())
                            ));
                        } catch (SQLException ex) {
                            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                        } catch (GuanzonException ex) {
                            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
            if (psCompanyId.equals("")) {
                tfSearchCompany.setText("");
            } else {
                tfSearchCompany.setText(poPurchaseReceivingController.Master().Company().getCompanyName());
            }
            if (psSupplierId.equals("")) {
                tfSearchSupplier.setText("");
            } else {
                tfSearchSupplier.setText(poPurchaseReceivingController.Master().Supplier().getCompanyName());
            }
            try {
                if (tfSearchReferenceNo.getText() == null || tfSearchReferenceNo.getText().equals("")) {
                    tfSearchReferenceNo.setText("");
                } else {
                    tfSearchReferenceNo.setText(poPurchaseReceivingController.Master().getTransactionNo());
                }
            } catch (Exception e) {
                tfSearchReferenceNo.setText("");
            }

        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryMonarchHospitalityController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
            if (pnDetail < 0 || pnDetail > poPurchaseReceivingController.getDetailCount() - 1) {
                return;
            }
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
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
            if (poPurchaseReceivingController.Master().getDiscountRate().doubleValue() > 0.00) {
                poPurchaseReceivingController.computeDiscount(poPurchaseReceivingController.Master().getDiscountRate().doubleValue());
            } else {
                if (poPurchaseReceivingController.Master().getDiscount().doubleValue() > 0.00) {
                    poPurchaseReceivingController.computeDiscountRate(poPurchaseReceivingController.Master().getDiscount().doubleValue());
                }
            }
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

            Platform.runLater(() -> {
                double lnValue = poPurchaseReceivingController.Master().getDiscountRate().doubleValue();
                if (!Double.isNaN(lnValue)) {
                    tfDiscountRate.setText((String.valueOf(poPurchaseReceivingController.Master().getDiscountRate().doubleValue())));
                }
            });
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReceivingController.Master().getDiscount().doubleValue())));
            tfTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReceivingController.Master().getTransactionTotal().doubleValue())));
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadTableDetailFromMain() {
        try {
            poJSON = new JSONObject();

            poJSON = poPurchaseReceivingController.OpenTransaction(poPurchaseReceivingController.PurchaseOrderReceivingList(pnMain).getTransactionNo());
            if ("error".equals((String) poJSON.get("message"))) {
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                return;
            }

            if (poPurchaseReceivingController.getEditMode() == EditMode.READY || poPurchaseReceivingController.getEditMode() == EditMode.UPDATE) {
                ModelDeliveryAcceptance_Main selected = (ModelDeliveryAcceptance_Main) tblViewPuchaseOrder.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                    pnMain = pnRowMain;
                    disableAllHighlightByColor(tblViewPuchaseOrder, "#A7C7E7",highlightedRowsMain);
                    highlight(tblViewPuchaseOrder, pnRowMain, "#A7C7E7", highlightedRowsMain);
                }
                loadTableDetail();
            }

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_HistoryMonarchHospitalityController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
                    details_data.clear();
                    int lnCtr;
                    try {
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poPurchaseReceivingController.getDetailCount() - 1;
                            while (lnCtr >= 0) {
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

                            if ((poPurchaseReceivingController.getDetailCount() - 1) < 0) {
                                poPurchaseReceivingController.AddDetail();
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
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(lnCtr).getUnitPrce())),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getOrderQty().intValue()),
                                            String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getQuantity()),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal)) //identify total
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
                        Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(DeliveryAcceptance_HistoryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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

    }

    public void initTextFields() {
        tfSearchCompany.focusedProperty().addListener(txtField_Focus);
        tfSearchSupplier.focusedProperty().addListener(txtField_Focus);
        tfSearchReferenceNo.focusedProperty().addListener(txtField_Focus);
        tfAttachmentNo.focusedProperty().addListener(txtField_Focus);
        tfAttachmentType.focusedProperty().addListener(txtField_Focus);

        tfSearchCompany.setOnKeyPressed(this::txtField_KeyPressed);
        tfSearchSupplier.setOnKeyPressed(this::txtField_KeyPressed);
        tfSearchReferenceNo.setOnKeyPressed(this::txtField_KeyPressed);

    }

    public void initTableOnClick() {
        tblViewOrderDetails.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewOrderDetails.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                }
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

    private void initButton(int fnValue) {

        boolean lbShow1 = (fnValue == EditMode.UPDATE);
        boolean lbShow2 = (fnValue == EditMode.READY || fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        // Manage visibility and managed state of other buttons

        //Ready
        btnPrint.setVisible(lbShow3);
        btnPrint.setManaged(lbShow3);

        btnHistory.setVisible(lbShow3);
        btnHistory.setManaged(lbShow3);

        //Unkown || Ready
        btnClose.setVisible(lbShow4);
        btnClose.setManaged(lbShow4);

        apMaster.setDisable(!lbShow1);
        apDetail.setDisable(!lbShow1);
        apAttachments.setDisable(!lbShow1);

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
        tblOrderQuantityDetail.setStyle("-fx-alignment: CENTER;");
        tblReceiveQuantityDetail.setStyle("-fx-alignment: CENTER;");
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
        tblRowNo.setStyle("-fx-alignment: CENTER;");
        tblSupplier.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblDate.setStyle("-fx-alignment: CENTER;");
        tblReferenceNo.setStyle("-fx-alignment: CENTER;");

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
        if (details_data.size() > 0) {
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
                if (main_data.size() > 0) {
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
