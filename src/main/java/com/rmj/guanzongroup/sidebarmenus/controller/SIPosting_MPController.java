/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Attachment;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Main;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelJournalEntry_Detail;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.stage.FileChooser;
import javafx.util.Duration;
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
import org.json.simple.parser.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.constant.DocumentType;
import javafx.util.Pair;
import javax.script.ScriptException;

/**
 *
 * @author Arsiela & Aldrich Team 2
 */
public class SIPosting_MPController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    static PurchaseOrderReceiving poPurchaseReceivingController;
    private JSONObject poJSON;
    public int pnEditMode;
    private final String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    private static final int ROWS_PER_PAGE = 50;
    int pnJEDetail = 0;
    int pnDetail = 0;
    int pnMain = 0;
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psBranchId = "";
    private String openedAttachment = "";
    private boolean pbEntered = false;

    private ObservableList<ModelDeliveryAcceptance_Main> main_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Detail> details_data = FXCollections.observableArrayList();
    private ObservableList<ModelJournalEntry_Detail> JEdetails_data = FXCollections.observableArrayList();
    private final ObservableList<ModelDeliveryAcceptance_Attachment> attachment_data = FXCollections.observableArrayList();
    ObservableList<String> documentType = ModelDeliveryAcceptance_Attachment.documentType;
    private FilteredList<ModelDeliveryAcceptance_Main> filteredData;
    private FilteredList<ModelDeliveryAcceptance_Detail> filteredDataDetail;
    Map<String, String> imageinfo_temp = new HashMap<>();
    private FileChooser fileChooser;
    private int pnAttachment;

    private int currentIndex = 0;
    boolean lbSelectTabJE = false;

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    private final Map<String, List<String>> highlightedRowsDetail = new HashMap<>();

    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();

    private final JFXUtil.ImageViewer imageviewerutil = new JFXUtil.ImageViewer();
    JFXUtil.StageManager stageAttachment = new JFXUtil.StageManager();
    JFXUtil.StageManager stageSerial = new JFXUtil.StageManager();
    AnchorPane root = null;
    Scene scene = null;
    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail, apJEMaster, apJEDetail, apAttachments;
    @FXML
    private HBox hbButtons;
    @FXML
    private Label lblSource, lblStatus, lblJEStatus;
    @FXML
    private Button btnUpdate, btnSearch, btnSerials, btnSave, btnCancel, btnPost, btnHistory, btnRetrieve, btnClose, btnArrowLeft, btnArrowRight;
    @FXML
    private TextField tfSearchSupplier, tfSearchReceiveBranch, tfSearchReferenceNo, tfTransactionNo, tfSupplier, tfBranch, tfTrucking, tfReferenceNo,
            tfSINo, tfTerm, tfTransactionTotal, tfDiscountRate, tfDiscountAmount, tfFreightAmt, tfVatRate, tfVatSales, tfVatAmount, tfZeroVatSales, tfVatExemptSales,
            tfTaxAmount, tfNetTotal, tfOrderNo, tfBarcode, tfBrand, tfDescription, tfOrderQuantity, tfReceiveQuantity, tfSRPAmount, tfDiscRateDetail, tfAddlDiscAmtDetail, tfCost,
            tfJETransactionNo, tfJEAcctCode, tfJEAcctDescription, tfCreditAmt, tfDebitAmt, tfTotalCreditAmt, tfTotalDebitAmt, tfAttachmentNo;
    @FXML
    private DatePicker dpTransactionDate, dpReferenceDate, dpReportMonthYear, dpJETransactionDate;
    @FXML
    private CheckBox cbVatInclusive, cbVatable;
    @FXML
    private TextArea taRemarks, taJERemarks;
    @FXML
    private TabPane tabPaneForm;
    @FXML
    private Tab tabSIPosting, tabJE, tabAttachments;
    @FXML
    private TableView tblViewTransDetailList, tblViewMainList, tblViewJEDetails, tblAttachments;
    @FXML
    private TableColumn tblRowNoDetail, tblOrderNoDetail, tblBrandDetail, tblDescriptionDetail, tblCostDetail, tblOrderQuantityDetail, tblReceiveQuantityDetail, tblTotalDetail, tblRowNo, tblSupplier, tblDate, tblReferenceNo, tblJERowNoDetail, tblReportMonthYearDetail, tblJEAcctCodeDetail, tblJEAcctDescriptionDetail, tblJECreditAmtDetail, tblJEDebitAmtDetail, tblRowNoAttachment, tblFileNameAttachment;
    @FXML
    private ComboBox cmbAttachmentType;
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

        initTextFields();
        initDatePickers();
        initMainGrid();
        initDetailsGrid();
        initJEDetailsGrid();
        initAttachmentsGrid();
        initTableOnClick();
        initTabSelection();
        clearTextFields();

        Platform.runLater(() -> {
            poPurchaseReceivingController.Master().setIndustryId(psIndustryId);
            poPurchaseReceivingController.Master().setCompanyId(psCompanyId);
            poPurchaseReceivingController.setIndustryId(psIndustryId);
            poPurchaseReceivingController.setCompanyId(psCompanyId);
            poPurchaseReceivingController.setCategoryId(psCategoryId);
            poPurchaseReceivingController.isFinance(true);
            poPurchaseReceivingController.initFields();
            loadRecordSearch();

            TriggerWindowEvent();
        });

        initAttachmentPreviewPane();

        pgPagination.setPageCount(1);

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);

    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
        psIndustryId = fsValue;
    }

    @Override
    public void setCompanyID(String fsValue) {
        psCompanyId = fsValue;
    }

    @Override
    public void setCategoryID(String fsValue) {
        psCategoryId = fsValue;
    }
    ChangeListener<Scene> WindowKeyEvent = (obs, oldScene, newScene) -> {
        if (newScene != null) {
            setKeyEvent(newScene);
        }
    };

    public void TriggerWindowEvent() {
        root = (AnchorPane) apMainAnchor;
        scene = root.getScene();
        if (scene != null) {
            setKeyEvent(scene);
        } else {
            root.sceneProperty().addListener(WindowKeyEvent);
        }
    }

    public void RemoveWindowEvent() {
        root.sceneProperty().removeListener(WindowKeyEvent);
        scene.setOnKeyPressed(null);
        stageAttachment.closeSerialDialog();
    }

    private void populateJE() {
        try {
            JSONObject pnJSON = new JSONObject();
            JFXUtil.setValueToNull(dpJETransactionDate, dpReportMonthYear);
            JFXUtil.clearTextFields(apJEMaster, apJEDetail);
            pnJSON = poPurchaseReceivingController.populateJournal();
            if (JFXUtil.isJSONSuccess(pnJSON)) {
                loadTableJEDetail();
            } else {
                JEdetails_data.clear();
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (ScriptException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initTabSelection() {
        tabJE.setOnSelectionChanged(event -> {
            if (tabJE.isSelected()) {
                lbSelectTabJE = true;
                populateJE();
            }
        });
    }

    private void setKeyEvent(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                System.out.println("tested key press");

                if (JFXUtil.isObjectEqualTo(poPurchaseReceivingController.getEditMode(), EditMode.READY, EditMode.UPDATE)) {
                    showAttachmentDialog();
                }
            }
        }
        );
        scene.focusOwnerProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                if (newNode instanceof Button) {
                } else {
                    lastFocusedTextField.set(newNode);
                    previousSearchedTextField.set(null);
                }
            }
        });
    }

    public void showAttachmentDialog() {
        poJSON = new JSONObject();
        stageAttachment.closeSerialDialog();
        openedAttachment = "";
        if (poPurchaseReceivingController.getTransactionAttachmentCount() <= 0) {
            ShowMessageFX.Warning(null, pxeModuleName, "No transaction attachment to load.");
            return;
        }

        openedAttachment = poPurchaseReceivingController.Master().getTransactionNo();
        Map<String, Pair<String, String>> data = new HashMap<>();
        data.clear();
        for (int lnCtr = 0; lnCtr < poPurchaseReceivingController.getTransactionAttachmentCount(); lnCtr++) {
            data.put(String.valueOf(lnCtr + 1), new Pair<>(String.valueOf(poPurchaseReceivingController.TransactionAttachmentList(lnCtr).getModel().getFileName()),
                    poPurchaseReceivingController.TransactionAttachmentList(lnCtr).getModel().getDocumentType()));

        }
        AttachmentDialogController controller = new AttachmentDialogController();
        controller.addData(data);
        try {
            stageAttachment.showDialog((Stage) btnSave.getScene().getWindow(), getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/views/AttachmentDialog.fxml"), controller, "Attachment Dialog", false, false, true);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void showSerialDialog() {
        try {
            poJSON = new JSONObject();
            stageSerial.closeSerialDialog();
            if (!poPurchaseReceivingController.Detail(pnDetail).isSerialized()) {
                return;
            }

            if (poPurchaseReceivingController.Detail(pnDetail).getQuantity().intValue() == 0) {
                return;
            }
            poJSON = poPurchaseReceivingController.getPurchaseOrderReceivingSerial(pnDetail + 1);
            if ("error".equals((String) poJSON.get("result"))) {
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                return;
            }
            DeliveryAcceptance_SerialMPController controller = new DeliveryAcceptance_SerialMPController();
            if (controller != null) {
                controller.setGRider(oApp);
                controller.setObject(poPurchaseReceivingController);
                controller.setEntryNo(pnDetail + 1);
                controller.isFinancing(true);
            }
            stageSerial.setOnHidden(event -> {
                moveNext();
                Platform.runLater(() -> {
                    loadTableDetail();
                });
            });
            stageSerial.showDialog((Stage) btnSave.getScene().getWindow(), getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_SerialMP.fxml"), controller, "Inventory Serial", true, true, false);

        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof CheckBox) {
            CheckBox checkedBox = (CheckBox) source;
            switch (checkedBox.getId()) {
                case "cbVatInclusive":
                    poPurchaseReceivingController.Master().isVatTaxable(cbVatInclusive.isSelected());
                    loadRecordMaster();
                    break;
                case "cbVatable":
                    poPurchaseReceivingController.Detail(pnDetail).isVatable(cbVatable.isSelected());
                    break;
            }
        }
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
                    case "btnSerials":
                        if (!poPurchaseReceivingController.Detail(pnDetail).isSerialized()) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Selected item is not serialized.");
                            return;
                        }
                        showSerialDialog();
                        return;
                    case "btnClose":
                        unloadForm appUnload = new unloadForm();
                        if (ShowMessageFX.OkayCancel(null, "Close Tab", "Are you sure you want to close this Tab?") == true) {
                            appUnload.unloadForm(apMainAnchor, oApp, pxeModuleName);
                        } else {
                            return;
                        }
                        break;
                    case "btnUpdate":
                        poJSON = poPurchaseReceivingController.OpenTransaction(poPurchaseReceivingController.Master().getTransactionNo());
                        poJSON = poPurchaseReceivingController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        poPurchaseReceivingController.loadAttachments();

                        pnEditMode = poPurchaseReceivingController.getEditMode();
                        break;
                    case "btnSearch":
                        String lsMessage = "Focus a searchable textfield to search";
                        if ((lastFocusedTextField.get() != null)) {
                            if (lastFocusedTextField.get() instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField.get();
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apBrowse, apMaster, apDetail,
                                        apJEMaster, apJEDetail).contains(tf.getId())) {
                                    if (lastFocusedTextField.get() == previousSearchedTextField.get()) {
                                        break;
                                    }
                                    previousSearchedTextField.set(lastFocusedTextField.get());
                                    // Create a simulated KeyEvent for F3 keypress
                                    JFXUtil.makeKeyPressed(tf, KeyCode.F3);
                                } else {
                                    ShowMessageFX.Information(null, pxeModuleName, lsMessage);
                                }
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, lsMessage);
                            }
                        } else {
                            ShowMessageFX.Information(null, pxeModuleName, lsMessage);
                        }
                        break;
                    case "btnCancel":
                        if (ShowMessageFX.OkayCancel(null, pxeModuleName, "Do you want to disregard changes?") == true) {
                            JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        //Retrieve data from purchase order to table main
                        retrievePOR();
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poPurchaseReceivingController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poPurchaseReceivingController.AddDetail();
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON = poPurchaseReceivingController.OpenTransaction(poPurchaseReceivingController.Master().getTransactionNo());
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poPurchaseReceivingController.Master().getTransactionStatus().equals(PurchaseOrderReceivingStatus.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poPurchaseReceivingController.ConfirmTransaction("Confirmed");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }

                        break;
                    case "btnPost":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to post transaction?") == true) {
                            if (!lbSelectTabJE) {
                                ShowMessageFX.Warning(null, pxeModuleName, "Please review and verify all Journal Entry details before posting the transaction.");
                                return;
                            }

                            poJSON = poPurchaseReceivingController.PostTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#C1E1C1", highlightedRowsMain);

                            }
                        } else {
                            return;
                        }
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

                if (JFXUtil.isObjectEqualTo(lsButton, "btnSave", "btnCancel")) {
                    poPurchaseReceivingController.resetMaster();
                    poPurchaseReceivingController.resetOthers();
                    poPurchaseReceivingController.Detail().clear();
                    poPurchaseReceivingController.resetJournal();
                    imageView.setImage(null);
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnArrowRight", "btnArrowLeft", "btnRetrieve")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                    loadTableAttachment();

                    Tab currentTab = tabPaneForm.getSelectionModel().getSelectedItem();
                    if (currentTab.getId().equals("tabJE")) {
                        populateJE();
                    }

                }
                initButton(pnEditMode);
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void retrievePOR() {
        poJSON = new JSONObject();
        poJSON = poPurchaseReceivingController.loadPurchaseOrderReceiving("siposting", psCompanyId, psSupplierId, psBranchId, tfSearchReferenceNo.getText());
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        } else {
            loadTableMain();
        }
    }

    final ChangeListener<? super Boolean> txtMaster_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());

        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
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
                case "tfSINo":
                    if (!lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.Master().setSalesInvoice(lsValue);
                    } else {
                        poJSON = poPurchaseReceivingController.Master().setSalesInvoice("");
                    }
                    loadTableDetail();
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        tfSINo.setText("");
                        break;
                    }
                    break;
                case "tfDiscountRate":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    poJSON = poPurchaseReceivingController.computeDiscount(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    poJSON = poPurchaseReceivingController.Master().setDiscountRate((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    break;
                case "tfDiscountAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }

                    poJSON = poPurchaseReceivingController.computeDiscountRate(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    poJSON = poPurchaseReceivingController.Master().setDiscount(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    break;
                case "tfFreightAmt":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }

                    if (Double.valueOf(lsValue.replace(",", "")) > poPurchaseReceivingController.Master().getTransactionTotal().doubleValue()) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid freight amount");
                        break;
                    }

                    poJSON = poPurchaseReceivingController.Master().setFreight(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    break;
                case "tfTaxAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.0000";
                    }

                    poJSON = poPurchaseReceivingController.Master().setWithHoldingTax(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    break;
                case "tfVatRate":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }

                    if (Double.valueOf(lsValue.replace(",", "")) > 100.00) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid vat rate.");
                        break;
                    }

                    poJSON = poPurchaseReceivingController.Master().setVatRate((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    break;
                case "tfTotalCreditAmt":
                    break;
                case "tfTotalDebitAmt":
                    break;

            }
            if (JFXUtil.isObjectEqualTo(lsTxtFieldID, "tfTotalCreditAmt", "tfTotalDebitAmt")) {
                loadRecordJEMaster();
            } else {
                loadRecordMaster();
            }
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
                        break;
                    }
                    loadRecordMaster();
                    break;
                case "taJERemarks":
                    poJSON = poPurchaseReceivingController.Journal().Master().setRemarks(lsValue);
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    loadRecordJEMaster();
                    break;
            }
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
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/

            switch (lsTxtFieldID) {
                case "tfCost":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    double lnNewVal = Double.valueOf(lsValue);
                    double lnOldVal = poPurchaseReceivingController.Detail(pnDetail).getUnitPrce().doubleValue();

                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setUnitPrce((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }

                    if (pbEntered) {
                        if (lnNewVal != lnOldVal) {
                            if ((Double.valueOf(lsValue) > 0
                                    && poPurchaseReceivingController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poPurchaseReceivingController.Detail(pnDetail).getStockId()))) {
                                moveNext();
                            } else {
                                moveNext();
                            }
                        } else {
                            moveNext();
                        }
                        pbEntered = false;
                    }
                    break;
                case "tfDiscRateDetail":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setDiscountRate((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfAddlDiscAmtDetail":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.0000";
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setDiscountAmount((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;

                case "tfJEAcctCode":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.Journal().Detail(pnJEDetail).setAccountCode(lsValue);
                    }
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfJEAcctDescription":
                    if (lsValue.isEmpty()) {
//                        poJSON = poPurchaseReceivingController.Journal().Detail(pnJEDetail).Account_Chart().setDescription(lsValue);
                        poJSON = poPurchaseReceivingController.Journal().Detail(pnJEDetail).setAccountCode(lsValue);
                    }
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfCreditAmt":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.0000";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    lnNewVal = Double.valueOf(lsValue);
                    lnOldVal = poPurchaseReceivingController.Journal().Detail(pnJEDetail).getCreditAmount();

                    if (poPurchaseReceivingController.Journal().Detail(pnJEDetail).getDebitAmount() > 0.0000 && Double.valueOf(lsValue) > 0) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Debit and credit amounts cannot both have values at the same time.");
                        poPurchaseReceivingController.Journal().Detail(pnJEDetail).setCreditAmount(0.0000);
                        tfCreditAmt.setText("0.0000");
                        tfCreditAmt.requestFocus();
                        break;
                    } else {
                        poJSON = poPurchaseReceivingController.Journal().Detail(pnJEDetail).setCreditAmount((Double.valueOf(lsValue)));
                    }
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }

                    if (pbEntered && lnNewVal > 0) { //unique
                        if (lnNewVal != lnOldVal) {
                            if ((Double.valueOf(lsValue) > 0
                                    && poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode() != null
                                    && !"".equals(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode()))) {
                                moveNextJE(false);
                            } else {
                                moveNextJE(false);
                            }
                        } else {
                            moveNextJE(false);
                        }
                        pbEntered = false;
                    }
                    break;
                case "tfDebitAmt":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.0000";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    lnNewVal = Double.valueOf(lsValue);
                    lnOldVal = poPurchaseReceivingController.Journal().Detail(pnJEDetail).getDebitAmount();
                    if (poPurchaseReceivingController.Journal().Detail(pnJEDetail).getCreditAmount() > 0.0000 && Double.valueOf(lsValue) > 0) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Debit and credit amounts cannot both have values at the same time.");
                        poPurchaseReceivingController.Journal().Detail(pnJEDetail).setDebitAmount(0.0000);
                        tfDebitAmt.setText("0.0000");
                        tfDebitAmt.requestFocus();
                        break;
                    } else {
                        poJSON = poPurchaseReceivingController.Journal().Detail(pnJEDetail).setDebitAmount((Double.valueOf(lsValue)));
                    }
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    if (pbEntered) {
                        if (lnNewVal != lnOldVal) {
                            if ((Double.valueOf(lsValue) > 0
                                    && poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode() != null
                                    && !"".equals(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode()))) {
                                moveNextJE(false);
                            } else {
                                moveNextJE(false);
                            }
                        } else {
                            moveNextJE(false);
                        }
                        pbEntered = false;
                    }
                    break;
            }
            if (JFXUtil.isObjectEqualTo(lsTxtFieldID, "tfJEAcctCode", "tfJEAcctDescription",
                    "tfCreditAmt", "tfDebitAmt")) {
                Platform.runLater(() -> {
                    PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                    delay.setOnFinished(event -> {
                        loadTableJEDetail();
                    });
                    delay.play();
                });
            } else {
                Platform.runLater(() -> {
                    PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                    delay.setOnFinished(event -> {
                        loadTableDetail();
                    });
                    delay.play();
                });
            }
        }

    };

    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfSearchSupplier":
                    if (lsValue.equals("")) {
                        psSupplierId = "";
                    }
                    break;
                case "tfSearchReceiveBranch":
                    if (lsValue.equals("")) {
                        psBranchId = "";
                    }
                    break;
                case "tfSearchReferenceNo":
                    break;
            }
            if (JFXUtil.isObjectEqualTo(lsTxtFieldID, "tfSearchSupplier", "tfSearchReceiveBranch", "tfSearchReferenceNo")) {
                loadRecordSearch();
            }
        }
    };

    public void moveNext() {
        double lnReceiveQty = poPurchaseReceivingController.Detail(pnDetail).getQuantity().doubleValue();
        apDetail.requestFocus();
        double lnNewvalue = poPurchaseReceivingController.Detail(pnDetail).getQuantity().doubleValue();
        if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                && poPurchaseReceivingController.Detail(pnDetail).getStockId() != null
                && !"".equals(poPurchaseReceivingController.Detail(pnDetail).getStockId()))) {
            tfCost.requestFocus();
        } else {
            pnDetail = JFXUtil.moveToNextRow(tblViewTransDetailList);
            loadRecordDetail();
            tfOrderNo.setText("");
            tfCost.requestFocus();
        }
    }

    public void moveNextJE(boolean isUp) {
        apJEDetail.requestFocus();
        pnJEDetail = isUp ? JFXUtil.moveToPreviousRow(tblViewJEDetails) : JFXUtil.moveToNextRow(tblViewJEDetails);
        loadRecordJEDetail();
        if (JFXUtil.isObjectEqualTo(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode(), null, "")) {
            tfJEAcctCode.requestFocus();
        } else {
            if (poPurchaseReceivingController.Journal().Detail(pnJEDetail).getCreditAmount() > 0) {
                tfCreditAmt.requestFocus();
            } else {
                tfDebitAmt.requestFocus();
            }
        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEntered = true;
                    CommonUtils.SetNextFocus(txtField);
                    event.consume();
                    break;
                case UP:
                    switch (lsID) {
                        case "tfCost":
                        case "tfDiscRateDetail":
                        case "tfAddlDiscAmtDetail":
                            double lnReceiveQty = poPurchaseReceivingController.Detail(pnDetail).getQuantity().doubleValue();
                            apDetail.requestFocus();
                            double lnNewvalue = poPurchaseReceivingController.Detail(pnDetail).getQuantity().doubleValue();
                            if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                                    && poPurchaseReceivingController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poPurchaseReceivingController.Detail(pnDetail).getStockId()))) {
                                tfCost.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToPreviousRow(tblViewTransDetailList);
                                loadRecordDetail();
                                tfCost.requestFocus();
                                event.consume();
                            }
                            break;
                        case "tfJEAcctCode":
                        case "tfCreditAmt":
                        case "tfDebitAmt":
                            //focus if either credit or debit
                            // Debit is default to focus
                            moveNextJE(true);
                            event.consume();
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfCost":
                        case "tfDiscRateDetail":
                        case "tfAddlDiscAmtDetail":
                            moveNext();
                            event.consume();
                            break;
                        case "tfJEAcctCode":
                        case "tfCreditAmt":
                        case "tfDebitAmt":
                            moveNextJE(false);
                            event.consume();
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfSearchSupplier":
                            poJSON = poPurchaseReceivingController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                psSupplierId = "";
                                break;
                            } else {
                                psSupplierId = poPurchaseReceivingController.Master().getSupplierId();
                            }
                            retrievePOR();
                            loadRecordSearch();
                            return;
                        case "tfSearchReceiveBranch":
                            poJSON = poPurchaseReceivingController.SearchBranch(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                psBranchId = "";
                                break;
                            } else {
                                psBranchId = poPurchaseReceivingController.Master().getBranchCode();
                            }
                            retrievePOR();
                            loadRecordSearch();
                            return;
                        case "tfSearchReferenceNo":
                            poPurchaseReceivingController.Master().setTransactionNo(lsValue);
                            retrievePOR();
                            return;
                        case "tfTerm":
                            poJSON = poPurchaseReceivingController.SearchTerm(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfTerm.setText("");
                                break;
                            }
                            loadRecordMaster();
                            break;
                        case "tfJEAcctCode":
                            poJSON = poPurchaseReceivingController.Journal().SearchAccountCode(pnJEDetail, lsValue, true, null, null);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfJEAcctCode.setText("");
                                break;
                            }

                            poJSON = poPurchaseReceivingController.checkExistAcctCode(pnJEDetail, poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode());
                            if ("error".equals(poJSON.get("result"))) {
                                int lnRow = (int) poJSON.get("row");
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                if (pnJEDetail != lnRow) {
                                    pnJEDetail = lnRow;
                                    loadTableJEDetail();
                                    return;
                                }
                                break;
                            }
                            loadTableJEDetail();
                            break;
                        case "tfJEAcctDescription":
                            poJSON = poPurchaseReceivingController.Journal().SearchAccountCode(pnJEDetail, lsValue, false, psIndustryId, null);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfJEAcctCode.setText("");
                                break;
                            }

                            poJSON = poPurchaseReceivingController.checkExistAcctCode(pnJEDetail, poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode());
                            if ("error".equals(poJSON.get("result"))) {
                                int lnRow = (int) poJSON.get("row");
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                if (pnJEDetail != lnRow) {
                                    pnJEDetail = lnRow;
                                    loadTableJEDetail();
                                    return;
                                }
                                break;
                            }
                            loadTableJEDetail();
                            break;
                    }
                    break;
                default:
                    break;
            }

        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    boolean pbSuccess = true;

    private void datepicker_Action(ActionEvent event) {
        poJSON = new JSONObject();
        JFXUtil.setJSONSuccess(poJSON, "success");

        try {
            Object source = event.getSource();
            if (source instanceof DatePicker) {
                DatePicker datePicker = (DatePicker) source;
                String inputText = datePicker.getEditor().getText();
                SimpleDateFormat sdfFormat = new SimpleDateFormat(SQLUtil.FORMAT_SHORT_DATE);
                LocalDate currentDate = null;
                LocalDate transactionDate = null;
                LocalDate referenceDate = null;
                LocalDate selectedDate = null;
                String lsServerDate = "";
                String lsTransDate = "";
                String lsRefDate = "";
                String lsSelectedDate = "";

                JFXUtil.JFXUtilDateResult ldtResult = JFXUtil.processDate(inputText, datePicker);
                poJSON = ldtResult.poJSON;
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    loadRecordMaster();
                    return;
                }
                if (inputText == null || "".equals(inputText) || "1900-01-01".equals(inputText)) {
                    return;
                }

                lsServerDate = sdfFormat.format(oApp.getServerDate());
                lsTransDate = sdfFormat.format(poPurchaseReceivingController.Master().getTransactionDate());
                lsRefDate = sdfFormat.format(poPurchaseReceivingController.Master().getReferenceDate());
                lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                currentDate = LocalDate.parse(lsServerDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                transactionDate = LocalDate.parse(lsTransDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                switch (datePicker.getId()) {
                    case "dpTransactionDate":
                        break;
                    case "dpReferenceDate":
                        if (poPurchaseReceivingController.getEditMode() == EditMode.ADDNEW
                                || poPurchaseReceivingController.getEditMode() == EditMode.UPDATE) {

                            if (selectedDate.isAfter(currentDate)) {
                                poJSON.put("result", "error");
                                poJSON.put("message", "Future dates are not allowed.");
                                pbSuccess = false;
                            }

                            if (pbSuccess && (selectedDate.isAfter(transactionDate))) {
                                poJSON.put("result", "error");
                                poJSON.put("message", "Reference date cannot be later than the receiving date.");
                                pbSuccess = false;
                            }

                            if (pbSuccess) {
                                poPurchaseReceivingController.Master().setReferenceDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
                            }
                        }
                        break;
                    case "dpJETransactionDate":
                        break;
                    case "dpReportMonthYear":
                        if (poPurchaseReceivingController.getEditMode() == EditMode.ADDNEW
                                || poPurchaseReceivingController.getEditMode() == EditMode.UPDATE) {

                            if (pbSuccess) {
                                poPurchaseReceivingController.Journal().Detail(pnJEDetail).setForMonthOf((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
                            }
                        }
                        break;
                    default:
                        break;

                }
                if (pbSuccess) {
                } else {
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                }
                pbSuccess = false; //Set to false to prevent multiple message box: Conflict with server date vs transaction date validation
                if (JFXUtil.isObjectEqualTo(datePicker.getId(), "dpJETransactionDate", "dpReportMonthYear")) {
                    loadTableJEDetail();
                } else {
                    loadRecordMaster();
                }
                pbSuccess = true; //Set to original valueF
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadTableMain() {
        // Setting data to table detail
        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewMainList.setPlaceholder(loading.loadingPane);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
//                Thread.sleep(1000);
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    main_data.clear();
                    JFXUtil.disableAllHighlight(tblViewMainList, highlightedRowsMain);
                    if (poPurchaseReceivingController.getPurchaseOrderReceivingCount() > 0) {
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
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            } catch (GuanzonException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            }

                            if (poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).getTransactionStatus().equals(PurchaseOrderReceivingStatus.POSTED)) {
                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(lnCtr + 1), "#C1E1C1", highlightedRowsMain);
                            }
                        }

                    }

                    if (pnMain < 0 || pnMain
                            >= main_data.size()) {
                        if (!main_data.isEmpty()) {
                            /* FOCUS ON FIRST ROW */
                            JFXUtil.selectAndFocusRow(tblViewMainList, 0);
                            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();
                        }
                    } else {
                        /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                        JFXUtil.selectAndFocusRow(tblViewMainList, pnMain);
                    }
                    JFXUtil.loadTab(pgPagination, main_data.size(), ROWS_PER_PAGE, tblViewMainList, filteredData);
                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (main_data == null || main_data.isEmpty()) {
                    tblViewMainList.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblViewMainList.toFront();
                }
                loading.progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblViewMainList.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
    }

    public void loadRecordSearch() {
        try {
            lblSource.setText(poPurchaseReceivingController.Master().Company().getCompanyName());
            tfSearchSupplier.setText(psSupplierId.equals("") ? "" : poPurchaseReceivingController.Master().Supplier().getCompanyName());
            tfSearchReceiveBranch.setText(psBranchId.equals("") ? "" : poPurchaseReceivingController.Master().Branch().getBranchName());
            JFXUtil.updateCaretPositions(apBrowse);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordAttachment(boolean lbloadImage) {
        try {
            if (attachment_data.size() > 0) {
                tfAttachmentNo.setText(String.valueOf(pnAttachment + 1));
                String lsAttachmentType = poPurchaseReceivingController.TransactionAttachmentList(pnAttachment).getModel().getDocumentType();
                if (lsAttachmentType.equals("")) {
                    poPurchaseReceivingController.TransactionAttachmentList(pnAttachment).getModel().setDocumentType(DocumentType.OTHER);
                    lsAttachmentType = poPurchaseReceivingController.TransactionAttachmentList(pnAttachment).getModel().getDocumentType();
                }
                int lnAttachmentType = 0;
                lnAttachmentType = Integer.parseInt(lsAttachmentType);
                cmbAttachmentType.getSelectionModel().select(lnAttachmentType);

                if (lbloadImage) {
                    try {
                        String filePath = (String) attachment_data.get(pnAttachment).getIndex02();
                        String filePath2 = "";
                        if (imageinfo_temp.containsKey((String) attachment_data.get(pnAttachment).getIndex02())) {
                            filePath2 = imageinfo_temp.get((String) attachment_data.get(pnAttachment).getIndex02());
                        } else {
                            // in server
                            filePath2 = "D:\\GGC_Maven_Systems\\temp\\attachments\\" + (String) attachment_data.get(pnAttachment).getIndex02();
                        }
                        if (filePath != null && !filePath.isEmpty()) {
                            Path imgPath = Paths.get(filePath2);
                            String convertedPath = imgPath.toUri().toString();
                            Image loimage = new Image(convertedPath);
                            imageView.setImage(loimage);
                            JFXUtil.adjustImageSize(loimage, imageView, imageviewerutil.ldstackPaneWidth, imageviewerutil.ldstackPaneHeight);
                            Platform.runLater(() -> {
                                JFXUtil.stackPaneClip(stackPane1);
                            });

                        } else {
                            imageView.setImage(null);
                        }

                    } catch (Exception e) {
                        imageView.setImage(null);
                    }
                }
            } else {
                if (!lbloadImage) {
                    imageView.setImage(null);
                    JFXUtil.stackPaneClip(stackPane1);
                    pnAttachment = 0;
                }
            }
        } catch (Exception e) {
        }
    }

    public void loadRecordJEDetail() {
        try {
            //DISABLING
            if (!JFXUtil.isObjectEqualTo(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode(), null, "")) {
                JFXUtil.setDisabled(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getEditMode() != EditMode.ADDNEW, tfJEAcctCode, tfJEAcctDescription);
            } else {
                JFXUtil.setDisabled(false, tfJEAcctCode, tfJEAcctDescription);
            }

            tfJEAcctCode.setText(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode());
            tfJEAcctDescription.setText(poPurchaseReceivingController.Journal().Detail(pnJEDetail).Account_Chart().getDescription());
            String lsReportMonthYear = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getForMonthOf());
            dpReportMonthYear.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsReportMonthYear, "yyyy-MM-dd"));
            tfCreditAmt.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getCreditAmount(), true));
            tfDebitAmt.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getDebitAmount(), true));
            JFXUtil.updateCaretPositions(apJEDetail);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadRecordDetail() {

        try {
            if (pnDetail < 0 || pnDetail > poPurchaseReceivingController.getDetailCount() - 1) {
                return;
            }
            tfBarcode.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().getBarCode());
            tfBrand.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Brand().getDescription());
            tfDescription.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().getDescription());
            tfOrderNo.setText(poPurchaseReceivingController.Detail(pnDetail).getOrderNo());
            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(pnDetail).getUnitPrce(), true));
            Platform.runLater(() -> {
                double lnValue = 0.00;
                lnValue = poPurchaseReceivingController.Detail(pnDetail).getDiscountRate() != null ? poPurchaseReceivingController.Detail(pnDetail).getDiscountRate().doubleValue() : lnValue;
                tfDiscRateDetail.setText(String.format("%.2f", Double.isNaN(lnValue) ? 0.00 : lnValue));
            });
            double ldblDiscountRate = poPurchaseReceivingController.Detail(pnDetail).getUnitPrce().doubleValue()
                    * (poPurchaseReceivingController.Detail(pnDetail).getDiscountRate().doubleValue() / 100);
            tfAddlDiscAmtDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(pnDetail).getDiscountAmount(), true));
            double lnTotal = poPurchaseReceivingController.Detail(pnDetail).getUnitPrce().doubleValue()
                    + poPurchaseReceivingController.Detail(pnDetail).getDiscountAmount().doubleValue()
                    + ldblDiscountRate;
            tfSRPAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal, true));
            tfOrderQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getOrderQty().intValue()));
            tfReceiveQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity().intValue()));
            cbVatable.setSelected(poPurchaseReceivingController.Detail(pnDetail).isVatable());

            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadRecordJEMaster() {
        Platform.runLater(() -> {
            String lsActive = poPurchaseReceivingController.Journal().Master().getEditMode() == EditMode.UNKNOWN ? "-1" : poPurchaseReceivingController.Journal().Master().getTransactionStatus();
            Map<String, String> statusMap = new HashMap<>();
            statusMap.put(PurchaseOrderReceivingStatus.POSTED, "POSTED");
            statusMap.put(PurchaseOrderReceivingStatus.PAID, "PAID");
            statusMap.put(PurchaseOrderReceivingStatus.CONFIRMED, "CONFIRMED");
            statusMap.put(PurchaseOrderReceivingStatus.OPEN, "OPEN");
            statusMap.put(PurchaseOrderReceivingStatus.RETURNED, "RETURNED");
            statusMap.put(PurchaseOrderReceivingStatus.VOID, "VOIDED");
            statusMap.put(PurchaseOrderReceivingStatus.CANCELLED, "CANCELLED");

            String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN");
            lblJEStatus.setText(lsStat);
        });

        if (poPurchaseReceivingController.Journal().Master().getTransactionNo() != null) {
            tfJETransactionNo.setText(poPurchaseReceivingController.Journal().Master().getTransactionNo());
            String lsJETransactionDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Journal().Master().getTransactionDate());
            dpJETransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsJETransactionDate, "yyyy-MM-dd"));

            taJERemarks.setText(poPurchaseReceivingController.Journal().Master().getRemarks());
            double lnTotalDebit = 0;
            double lnTotalCredit = 0;
            for (int lnCtr = 0; lnCtr < poPurchaseReceivingController.Journal().getDetailCount(); lnCtr++) {
                lnTotalDebit += poPurchaseReceivingController.Journal().Detail(lnCtr).getDebitAmount();
                lnTotalCredit += poPurchaseReceivingController.Journal().Detail(lnCtr).getCreditAmount();
            }

            tfTotalCreditAmt.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotalCredit, true));
            tfTotalDebitAmt.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotalDebit, true));
            JFXUtil.updateCaretPositions(apJEMaster);
        }

    }

    public void loadRecordMaster() {
        try {
            Platform.runLater(() -> {
                String lsActive = pnEditMode == EditMode.UNKNOWN ? "-1" : poPurchaseReceivingController.Master().getTransactionStatus();
                Map<String, String> statusMap = new HashMap<>();
                statusMap.put(PurchaseOrderReceivingStatus.POSTED, "POSTED");
                statusMap.put(PurchaseOrderReceivingStatus.PAID, "PAID");
                statusMap.put(PurchaseOrderReceivingStatus.CONFIRMED, "CONFIRMED");
                statusMap.put(PurchaseOrderReceivingStatus.OPEN, "OPEN");
                statusMap.put(PurchaseOrderReceivingStatus.RETURNED, "RETURNED");
                statusMap.put(PurchaseOrderReceivingStatus.VOID, "VOIDED");
                statusMap.put(PurchaseOrderReceivingStatus.CANCELLED, "CANCELLED");

                String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN");
                lblStatus.setText(lsStat);
            });

            if (poPurchaseReceivingController.Master().getDiscountRate().doubleValue() > 0.00) {
                poPurchaseReceivingController.computeDiscount(poPurchaseReceivingController.Master().getDiscountRate().doubleValue());
            } else {
                if (poPurchaseReceivingController.Master().getDiscount().doubleValue() > 0.00) {
                    poPurchaseReceivingController.computeDiscountRate(poPurchaseReceivingController.Master().getDiscount().doubleValue());
                }
            }

            poPurchaseReceivingController.computeFields();

            tfTransactionNo.setText(poPurchaseReceivingController.Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));

            tfSupplier.setText(poPurchaseReceivingController.Master().Supplier().getCompanyName());
            tfBranch.setText(poPurchaseReceivingController.Master().Branch().getBranchName());
            tfTrucking.setText(poPurchaseReceivingController.Master().Trucking().getCompanyName());

            String lsReferenceDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getReferenceDate());
            dpReferenceDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsReferenceDate, "yyyy-MM-dd"));
            tfReferenceNo.setText(poPurchaseReceivingController.Master().getReferenceNo());
            tfSINo.setText(poPurchaseReceivingController.Master().getSalesInvoice());
            tfTerm.setText(poPurchaseReceivingController.Master().Term().getDescription());
            taRemarks.setText(poPurchaseReceivingController.Master().getRemarks());
//
//            tfTransactionTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
//                    poPurchaseReceivingController.Master().getTransactionTotal().doubleValue(), true));

            tfTransactionTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(
                    getGrossTotal(), true));
            Platform.runLater(() -> {
                double lnValue = poPurchaseReceivingController.Master().getDiscountRate().doubleValue();
                tfDiscountRate.setText(String.format("%.2f", Double.isNaN(lnValue) ? 0.00 : lnValue));
                double lnVat = poPurchaseReceivingController.Master().getVatRate().doubleValue();
                tfVatRate.setText(String.format("%.2f", Double.isNaN(lnVat) ? 0.00 : lnVat));

            });
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Master().getDiscount().doubleValue(), true));
            tfFreightAmt.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Master().getFreight().doubleValue()));
            tfTaxAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Master().getWithHoldingTax().doubleValue(), true));
            cbVatInclusive.setSelected(poPurchaseReceivingController.Master().isVatTaxable());
            tfVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Master().getVatSales().doubleValue(), true));
            tfVatAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Master().getVatAmount().doubleValue(), true));
            tfZeroVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Master().getZeroVatSales().doubleValue(), true));
            tfVatExemptSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Master().getVatExemptSales().doubleValue(), true));
            //Net Total = Vat Amount - Tax Amount
            double ldblNetTotal = 0.00;
            if (poPurchaseReceivingController.Master().isVatTaxable()) {
                //Net VAT Amount : VAT Sales - VAT Amount
                //Net Total : VAT Sales - Withholding Tax
                ldblNetTotal = poPurchaseReceivingController.Master().getVatSales().doubleValue() - poPurchaseReceivingController.Master().getWithHoldingTax().doubleValue();
            } else {
                //Net VAT Amount : VAT Sales + VAT Amount
                //Net Total : Net VAT Amount - Withholding Tax
                ldblNetTotal = (poPurchaseReceivingController.Master().getVatSales().doubleValue()
                        + poPurchaseReceivingController.Master().getVatAmount().doubleValue())
                        - poPurchaseReceivingController.Master().getWithHoldingTax().doubleValue();

            }

            tfNetTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(ldblNetTotal, true));
            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    private double getGrossTotal() {
        double ldblGrossTotal = 0.0000;
        for (int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getDetailCount() - 1; lnCtr++) {
            ldblGrossTotal = ldblGrossTotal + (poPurchaseReceivingController.Detail(lnCtr).getUnitPrce().doubleValue()
                    * poPurchaseReceivingController.Detail(lnCtr).getQuantity().doubleValue());
        }
        return ldblGrossTotal;
    }

    private void goToPageBasedOnSelectedRow(String pnRowMain) {

        int realIndex = Integer.parseInt(pnRowMain);

        if (realIndex == -1) {
            return; // Not found
        }
        int targetPage = realIndex / ROWS_PER_PAGE;
        int indexInPage = realIndex % ROWS_PER_PAGE;

        initMainGrid();
        initDetailsGrid();
        int totalPage = (int) (Math.ceil(main_data.size() * 1.0 / ROWS_PER_PAGE));
        pgPagination.setPageCount(totalPage);
        pgPagination.setCurrentPageIndex(targetPage);
        JFXUtil.changeTableView(targetPage, ROWS_PER_PAGE, tblViewMainList, main_data.size(), filteredData);

    }

    public void loadTableDetailFromMain() {
        try {

            poJSON = new JSONObject();

            ModelDeliveryAcceptance_Main selected = (ModelDeliveryAcceptance_Main) tblViewMainList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);

                poJSON = poPurchaseReceivingController.OpenTransaction(poPurchaseReceivingController.PurchaseOrderReceivingList(pnMain).getTransactionNo());
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
                goToPageBasedOnSelectedRow(String.valueOf(pnMain));
                lbSelectTabJE = false;
            }

            poPurchaseReceivingController.loadAttachments();
            if (poPurchaseReceivingController.getTransactionAttachmentCount() > 1) {
                if (!openedAttachment.equals(poPurchaseReceivingController.PurchaseOrderReceivingList(pnMain).getTransactionNo())) {
                    stageAttachment.closeSerialDialog();
                }
            } else {
                stageAttachment.closeSerialDialog();
            }

            Platform.runLater(() -> {
                loadTableDetail();
            });
            tfAttachmentNo.clear();
            cmbAttachmentType.setItems(documentType);

            imageView.setImage(null);
            JFXUtil.stackPaneClip(stackPane1);
            Platform.runLater(() -> {
                loadTableAttachment();
            });

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadTableJEDetail() {
//        pbEntered = false;
        // Setting data to table detail

        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewJEDetails.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    JEdetails_data.clear();
                    int lnCtr;
                    try {

                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poPurchaseReceivingController.Journal().getDetailCount() - 1;
                            while (lnCtr >= 0) {

                                if (JFXUtil.isObjectEqualTo(poPurchaseReceivingController.Journal().Detail(lnCtr).getAccountCode(), null, "")) {
                                    poPurchaseReceivingController.Journal().Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }
                            if ((poPurchaseReceivingController.Journal().getDetailCount() - 1) >= 0) {
                                if (poPurchaseReceivingController.Journal().Detail(poPurchaseReceivingController.Journal().getDetailCount() - 1).getAccountCode() != null
                                        && !poPurchaseReceivingController.Journal().Detail(poPurchaseReceivingController.Journal().getDetailCount() - 1).getAccountCode().equals("")) {
                                    poPurchaseReceivingController.Journal().AddDetail();
                                    poPurchaseReceivingController.Journal().Detail(poPurchaseReceivingController.Journal().getDetailCount() - 1).setForMonthOf(oApp.getServerDate());
                                }
                            }
                            if ((poPurchaseReceivingController.Journal().getDetailCount() - 1) < 0) {
                                poPurchaseReceivingController.Journal().AddDetail();
                                poPurchaseReceivingController.Journal().Detail(poPurchaseReceivingController.Journal().getDetailCount() - 1).setForMonthOf(oApp.getServerDate());
                            }
                        }

                        String lsReportMonthYear = "";
                        String lsAcctCode = "";
                        String lsAccDesc = "";
                        for (lnCtr = 0; lnCtr < poPurchaseReceivingController.Journal().getDetailCount(); lnCtr++) {
                            lsReportMonthYear = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Journal().Detail(lnCtr).getForMonthOf());
                            if (poPurchaseReceivingController.Journal().Detail(lnCtr).getAccountCode() != null) {
                                lsAcctCode = poPurchaseReceivingController.Journal().Detail(lnCtr).getAccountCode();
                            }
                            if (poPurchaseReceivingController.Journal().Detail(lnCtr).Account_Chart().getDescription() != null) {
                                lsAccDesc = poPurchaseReceivingController.Journal().Detail(lnCtr).Account_Chart().getDescription();
                            }

                            JEdetails_data.add(
                                    new ModelJournalEntry_Detail(String.valueOf(lnCtr + 1),
                                            String.valueOf(CustomCommonUtil.parseDateStringToLocalDate(lsReportMonthYear, "yyyy-MM-dd")),
                                            String.valueOf(lsAcctCode),
                                            String.valueOf(lsAccDesc),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Journal().Detail(lnCtr).getCreditAmount(), true)),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Journal().Detail(lnCtr).getDebitAmount(), true))) //identify total
                            );

                            lsReportMonthYear = "";
                            lsAcctCode = "";
                            lsAccDesc = "";
                        }
                        if (pnJEDetail < 0 || pnJEDetail
                                >= JEdetails_data.size()) {
                            if (!JEdetails_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                JFXUtil.selectAndFocusRow(tblViewJEDetails, 0);
                                pnJEDetail = tblViewJEDetails.getSelectionModel().getSelectedIndex();
                                loadRecordJEDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            JFXUtil.selectAndFocusRow(tblViewJEDetails, pnJEDetail);
                            loadRecordJEDetail();
                        }
                        loadRecordJEMaster();
                    } catch (SQLException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (CloneNotSupportedException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    }
                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (JEdetails_data == null || JEdetails_data.isEmpty()) {
                    tblViewJEDetails.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblViewJEDetails.toFront();
                }
                loading.progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (JEdetails_data == null || JEdetails_data.isEmpty()) {
                    tblViewJEDetails.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
    }

    public void loadTableDetail() {
        pbEntered = false;
        // Setting data to table detail
        JFXUtil.disableAllHighlight(tblViewTransDetailList, highlightedRowsDetail);

        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewTransDetailList.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    details_data.clear();
                    int lnCtr;
                    try {
                        double lnTotal = 0.00;
                        double lnDiscountAmt = 0.00;
                        for (lnCtr = 0; lnCtr < poPurchaseReceivingController.getDetailCount(); lnCtr++) {

                            if (JFXUtil.isObjectEqualTo(poPurchaseReceivingController.Master().getSalesInvoice(), null, "")) {
                                poPurchaseReceivingController.Detail(lnCtr).isVatable(false);
                                JFXUtil.setDisabled(true, cbVatable);
                            } else {
                                JFXUtil.setDisabled(false, cbVatable);
                            }

                            try {
                                lnTotal = poPurchaseReceivingController.Detail(lnCtr).getUnitPrce().doubleValue() * poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue();
                                lnDiscountAmt = poPurchaseReceivingController.Detail(lnCtr).getDiscountAmount().doubleValue()
                                        + (lnTotal * (poPurchaseReceivingController.Detail(lnCtr).getDiscountRate().doubleValue() / 100));
                            } catch (Exception e) {
                            }

                            if ((!poPurchaseReceivingController.Detail(lnCtr).getOrderNo().equals("") && poPurchaseReceivingController.Detail(lnCtr).getOrderNo() != null)
                                    && poPurchaseReceivingController.Detail(lnCtr).getOrderQty().intValue() != poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue()
                                    && poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue() != 0) {
                                JFXUtil.highlightByKey(tblViewTransDetailList, String.valueOf(lnCtr + 1), "#FAA0A0", highlightedRowsDetail);
                            }

                            if (poPurchaseReceivingController.Detail(lnCtr).getStockId() != null && !"".equals(poPurchaseReceivingController.Detail(lnCtr).getStockId())) {
                                details_data.add(
                                        new ModelDeliveryAcceptance_Detail(String.valueOf(lnCtr + 1),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getOrderNo()),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).Inventory().Brand().getDescription()),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).Inventory().getDescription()),
                                                String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(lnCtr).getUnitPrce(), true)),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getOrderQty().intValue()),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue()),
                                                //                                                String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnDiscountAmt, true)),
                                                String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal, true)) //identify total
                                        ));
                            }
                        }
                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                JFXUtil.selectAndFocusRow(tblViewTransDetailList, 0);
                                pnDetail = tblViewTransDetailList.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            JFXUtil.selectAndFocusRow(tblViewTransDetailList, pnDetail);
                            loadRecordDetail();
                        }
                        loadRecordMaster();
                    } catch (SQLException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    }
                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetailList.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblViewTransDetailList.toFront();
                }
                loading.progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetailList.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background

    }

    private void loadTableAttachment() {
        imageviewerutil.scaleFactor = 1.0;
        JFXUtil.resetImageBounds(imageView, stackPane1);
        // Setting data to table detail
        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblAttachments.setPlaceholder(loading.loadingPane);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                Platform.runLater(() -> {
                    try {
                        attachment_data.clear();
                        int lnCtr;
                        for (lnCtr = 0; lnCtr < poPurchaseReceivingController.getTransactionAttachmentCount(); lnCtr++) {
                            attachment_data.add(
                                    new ModelDeliveryAcceptance_Attachment(String.valueOf(lnCtr + 1),
                                            String.valueOf(poPurchaseReceivingController.TransactionAttachmentList(lnCtr).getModel().getFileName())
                                    ));
                        }
                        if (pnAttachment < 0 || pnAttachment
                                >= attachment_data.size()) {
                            if (!attachment_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                JFXUtil.selectAndFocusRow(tblAttachments, 0);
                                pnAttachment = 0;
                                loadRecordAttachment(true);
                            } else {
                                tfAttachmentNo.setText("");
                                cmbAttachmentType.getSelectionModel().select(0);
                                loadRecordAttachment(false);
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            JFXUtil.selectAndFocusRow(tblAttachments, pnAttachment);
                            loadRecordAttachment(true);
                        }
                    } catch (Exception e) {

                    }

                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (attachment_data == null || attachment_data.isEmpty()) {
                    tblAttachments.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblAttachments.toFront();
                }
                loading.progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                if (attachment_data == null || attachment_data.isEmpty()) {
                    tblAttachments.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background

    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpReferenceDate, dpJETransactionDate, dpReportMonthYear);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate, dpReferenceDate, dpJETransactionDate, dpReportMonthYear);
    }

    public void initTextFields() {
        JFXUtil.setFocusListener(txtField_Focus, tfSearchReceiveBranch, tfSearchSupplier, tfSearchReferenceNo);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks, taJERemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfReferenceNo, tfSINo, tfTerm, tfDiscountRate, tfDiscountAmount, tfTaxAmount, tfVatRate, tfFreightAmt,
                tfVatSales, tfZeroVatSales, tfVatAmount, tfVatExemptSales, tfTotalCreditAmt, tfTotalDebitAmt, tfTotalCreditAmt, tfTotalDebitAmt);
        JFXUtil.setFocusListener(txtDetail_Focus, tfCost, tfDiscRateDetail, tfAddlDiscAmtDetail, tfSRPAmount,
                tfJEAcctCode, tfJEAcctDescription, tfCreditAmt, tfDebitAmt);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apMaster, apDetail, apJEDetail, apBrowse);

        JFXUtil.setCommaFormatter(tfDiscountAmount, tfFreightAmt, tfVatSales, tfTaxAmount, tfVatAmount,
                tfZeroVatSales, tfVatExemptSales, tfCost, tfAddlDiscAmtDetail, tfCreditAmt,
                tfDebitAmt, tfAddlDiscAmtDetail, tfSRPAmount);

        CustomCommonUtil.inputIntegersOnly(tfReceiveQuantity, tfSINo);
        CustomCommonUtil.inputDecimalOnly(tfDiscountRate, tfDiscRateDetail, tfVatRate);

        // Combobox
        JFXUtil.initComboBoxCellDesignColor(cmbAttachmentType, "#FF8201");
        cmbAttachmentType.setItems(documentType);
        cmbAttachmentType.setOnAction(event -> {
            if (attachment_data.size() > 0) {
                try {
                    int selectedIndex = cmbAttachmentType.getSelectionModel().getSelectedIndex();
                    poPurchaseReceivingController.TransactionAttachmentList(pnAttachment).getModel().setDocumentType("000" + String.valueOf(selectedIndex));
                    cmbAttachmentType.getSelectionModel().select(selectedIndex);
                } catch (Exception e) {
                }
            }
        });
        JFXUtil.setCheckboxHoverCursor(apMaster, apDetail);
    }

    public void initTableOnClick() {
        tblViewJEDetails.setOnMouseClicked(event -> {
            ModelJournalEntry_Detail selected = (ModelJournalEntry_Detail) tblViewJEDetails.getSelectionModel().getSelectedItem();
            if (selected != null) {
                pnJEDetail = Integer.parseInt(selected.getIndex01()) - 1;
                loadRecordJEDetail();
                if (JFXUtil.isObjectEqualTo(poPurchaseReceivingController.Journal().Detail(pnJEDetail).getAccountCode(), null, "")) {
                    tfJEAcctCode.requestFocus();
                } else {
                    if (poPurchaseReceivingController.Journal().Detail(pnJEDetail).getCreditAmount() > 0) {
                        tfCreditAmt.requestFocus();
                    } else {
                        tfDebitAmt.requestFocus();
                    }

                }
            }
        });
        tblAttachments.setOnMouseClicked(event -> {
            pnAttachment = tblAttachments.getSelectionModel().getSelectedIndex();
            if (pnAttachment >= 0) {
                imageviewerutil.scaleFactor = 1.0;
                loadRecordAttachment(true);
                JFXUtil.resetImageBounds(imageView, stackPane1);
            }
        });

        tblViewTransDetailList.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    ModelDeliveryAcceptance_Detail selected = (ModelDeliveryAcceptance_Detail) tblViewTransDetailList.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        pnDetail = Integer.parseInt(selected.getIndex01()) - 1;
                        loadRecordDetail();
                        tfCost.requestFocus();
                    }
                }
            }
        });

        tblViewMainList.setOnMouseClicked(event -> {
            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    tfOrderNo.setText("");
                    loadTableDetailFromMain();
                    pnEditMode = poPurchaseReceivingController.getEditMode();
                    initButton(pnEditMode);
                }
            }
        });
        JFXUtil.applyRowHighlighting(tblViewMainList, item -> ((ModelDeliveryAcceptance_Main) item).getIndex01(), highlightedRowsMain);
        JFXUtil.applyRowHighlighting(tblViewTransDetailList, item -> ((ModelDeliveryAcceptance_Detail) item).getIndex01(), highlightedRowsDetail);
        JFXUtil.setKeyEventFilter(this::tableKeyEvents, tblViewTransDetailList, tblViewJEDetails, tblAttachments);
        JFXUtil.adjustColumnForScrollbar(tblViewMainList, tblViewTransDetailList, tblAttachments, tblViewJEDetails);
    }

    private void initButton(int fnValue) {

        boolean lbShow1 = (fnValue == EditMode.UPDATE);
        boolean lbShow2 = (fnValue == EditMode.READY || fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        // Manage visibility and managed state of other buttons
        //Update 
        JFXUtil.setButtonsVisibility(lbShow1, btnSearch, btnSave, btnCancel, btnSerials);
        //Ready
        JFXUtil.setButtonsVisibility(lbShow3, btnUpdate, btnHistory);
        JFXUtil.setButtonsVisibility(false, btnPost);
        //Unkown || Ready
        JFXUtil.setButtonsVisibility(lbShow4, btnClose);
        JFXUtil.setDisabled(!lbShow1, apMaster, apDetail, apJEDetail, apJEMaster, apAttachments);

        switch (poPurchaseReceivingController.Master().getTransactionStatus()) {
            case PurchaseOrderReceivingStatus.CONFIRMED:
                JFXUtil.setButtonsVisibility(lbShow3, btnPost);

                break;
            case PurchaseOrderReceivingStatus.POSTED:
            case PurchaseOrderReceivingStatus.PAID:
            case PurchaseOrderReceivingStatus.VOID:
            case PurchaseOrderReceivingStatus.CANCELLED:
            case PurchaseOrderReceivingStatus.RETURNED:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
        }
    }

    private void initAttachmentPreviewPane() {
        imageviewerutil.initAttachmentPreviewPane(stackPane1, imageView);
        stackPane1.heightProperty().addListener((observable, oldValue, newHeight) -> {
            double computedHeight = newHeight.doubleValue();
            imageviewerutil.ldstackPaneHeight = computedHeight;
            loadTableAttachment();
            loadRecordAttachment(true);
        });

    }

    public void initAttachmentsGrid() {
        /*FOCUS ON FIRST ROW*/
        JFXUtil.setColumnCenter(tblRowNoAttachment, tblFileNameAttachment);
        JFXUtil.setColumnsIndexAndDisableReordering(tblAttachments);

        tblAttachments.setItems(attachment_data);
    }

    public void slideImage(int direction) {
        if (attachment_data.size() <= 0) {
            return;
        }
        currentIndex = pnAttachment;
        int newIndex = currentIndex + direction;

        if (newIndex != -1 && (newIndex <= attachment_data.size() - 1)) {
            ModelDeliveryAcceptance_Attachment image = attachment_data.get(newIndex);
            String filePath2 = "D:\\GGC_Maven_Systems\\temp\\attachments\\" + image.getIndex02();
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), imageView);
            slideOut.setByX(direction * -400); // Move left or right

            JFXUtil.selectAndFocusRow(tblAttachments, newIndex);
            pnAttachment = newIndex;
            loadRecordAttachment(false);

            // Create a transition animation
            slideOut.setOnFinished(event -> {
                imageView.setTranslateX(direction * 400);
                TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), imageView);
                slideIn.setToX(0);
                slideIn.play();

                loadRecordAttachment(true);
            });

            slideOut.play();
        }
        if (JFXUtil.isImageViewOutOfBounds(imageView, stackPane1)) {
            JFXUtil.resetImageBounds(imageView, stackPane1);
        }
    }

    public void initDetailsGrid() {

        JFXUtil.setColumnCenter(tblRowNoDetail, tblOrderQuantityDetail, tblReceiveQuantityDetail);
        JFXUtil.setColumnLeft(tblOrderNoDetail, tblBrandDetail, tblDescriptionDetail);
        JFXUtil.setColumnRight(tblCostDetail, tblTotalDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetailList);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);

        SortedList<ModelDeliveryAcceptance_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewTransDetailList.comparatorProperty());
        tblViewTransDetailList.setItems(sortedData);
        tblViewTransDetailList.autosize();
    }

    public void initJEDetailsGrid() {
        JFXUtil.setColumnCenter(tblJERowNoDetail, tblReportMonthYearDetail);
        JFXUtil.setColumnLeft(tblJEAcctCodeDetail, tblJEAcctDescriptionDetail);
        JFXUtil.setColumnRight(tblJECreditAmtDetail, tblJEDebitAmtDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewJEDetails);

        tblViewJEDetails.setItems(JEdetails_data);
    }

    public void initMainGrid() {
        JFXUtil.setColumnCenter(tblRowNo, tblDate, tblReferenceNo);
        JFXUtil.setColumnLeft(tblSupplier);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewMainList);

        filteredData = new FilteredList<>(main_data, b -> true);
        tblViewMainList.setItems(filteredData);
    }

    private void tableKeyEvents(KeyEvent event) {
        if (details_data.size() > 0) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            switch (currentTable.getId()) {
                case "tblViewTransDetailList":
                    if (focusedCell != null) {
                        switch (event.getCode()) {
                            case TAB:
                            case DOWN:
                                pnDetail = JFXUtil.moveToNextRow(currentTable);
                                break;
                            case UP:
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
                                break;

                            default:
                                break;
                        }
                        loadRecordDetail();
                        event.consume();
                    }
                    break;
                case "tblAttachments":
                    if (focusedCell != null) {
                        switch (event.getCode()) {
                            case TAB:
                            case DOWN:
                                pnAttachment = JFXUtil.moveToNextRow(currentTable);
                                break;
                            case UP:
                                pnAttachment = JFXUtil.moveToPreviousRow(currentTable);
                                break;

                            default:
                                break;
                        }
                        loadRecordAttachment(true);
                        event.consume();
                    }
                    break;
                case "tblViewJEDetails":
                    if (focusedCell != null) {
                        switch (event.getCode()) {
                            case TAB:
                            case DOWN:
                                pnJEDetail = JFXUtil.moveToNextRow(currentTable);
                                break;
                            case UP:
                                pnJEDetail = JFXUtil.moveToPreviousRow(currentTable);
                                break;

                            default:
                                break;
                        }
                        loadRecordJEDetail();
                        event.consume();
                    }
                    break;
            }

        }
    }

    public void clearTextFields() {
        Platform.runLater(() -> {
            imageinfo_temp.clear();
            JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField, dpTransactionDate, dpReferenceDate, dpReportMonthYear);
            psSupplierId = "";
            psBranchId = "";
            JFXUtil.clearTextFields(apMaster, apDetail, apJEDetail, apJEMaster, apAttachments);

            cbVatInclusive.setSelected(false);
            cbVatable.setSelected(false);
        });
    }
}
