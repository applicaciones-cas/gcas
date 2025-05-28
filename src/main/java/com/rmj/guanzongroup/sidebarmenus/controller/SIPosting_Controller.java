/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Attachment;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Detail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.TableRow;
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
import java.time.YearMonth;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.constant.DocumentType;
import javafx.util.Pair;

/**
 *
 * @author Arsiela
 */
public class SIPosting_Controller implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnJEDetail = 0;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "SI Posting";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;
    boolean isPrinted = false;
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psBranchId = "";
    private String openedAttachment = "";
    private boolean pbEntered = false;

    private ObservableList<ModelDeliveryAcceptance_Main> main_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Detail> details_data = FXCollections.observableArrayList();
    private ObservableList<ModelDeliveryAcceptance_Detail> JEdetails_data = FXCollections.observableArrayList();
    private final ObservableList<ModelDeliveryAcceptance_Attachment> attachment_data = FXCollections.observableArrayList();
    ObservableList<String> documentType = ModelDeliveryAcceptance_Attachment.documentType;
    private FilteredList<ModelDeliveryAcceptance_Main> filteredData;
    private FilteredList<ModelDeliveryAcceptance_Detail> filteredDataDetail;
    Map<String, String> imageinfo_temp = new HashMap<>();
    List<Pair<String, String>> plOrderNoPartial = new ArrayList<>();
    List<Pair<String, String>> plOrderNoFinal = new ArrayList<>();

    private FileChooser fileChooser;
    private int pnAttachment;

    private int currentIndex = 0;
    boolean lbSelectTabJE = false;

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    private final Map<String, List<String>> highlightedRowsDetail = new HashMap<>();
    private Object lastFocusedTextField = null;
    private Object previousSearchedTextField = null;

    private double xOffset = 0;
    private double yOffset = 0;
    private Stage dialogStage = null;
    private final JFXUtil.ImageViewer imageviewerutil = new JFXUtil.ImageViewer();
    JFXUtil.StageManager stage = new JFXUtil.StageManager();

    JFXUtil.MonthYearPicker.Picker month_year_picker;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail, apJEMaster, apJEDetail, apAttachments;

    @FXML
    private HBox hbButtons;

    @FXML
    private Label lblSource, lblStatus, lblJEStatus;

    @FXML
    private Button btnUpdate, btnSearch, btnSave, btnCancel, btnPost, btnPrint, btnHistory, btnRetrieve, btnClose, btnArrowLeft, btnArrowRight;

    @FXML
    private TextField tfSearchSupplier, tfSearchReferenceNo, tfSearchReceiveBranch, tfTransactionNo, tfSupplier, tfBranch, tfTrucking, tfReferenceNo,
            tfSINo, tfTerm, tfDiscountRate, tfDiscountAmount, tfFreightAmt, tfVatSales, tfVatAmount, tfZeroVatSales, tfVatExemptSales, tfNetTotal,
            tfTransactionTotal, tfOrderNo, tfBarcode, tfDescription, tfSupersede, tfMeasure, tfOrderQuantity, tfReceiveQuantity, tfCost, tfDiscRateDetail,
            tfAddlDiscAmtDetail, tfTotalDetail, tfJETransactionNo, tfJEAcctCode, tfJEAcctDescription, tfReportMonthYear, tfCreditAmt, tfDebitAmt, tfTotalCreditAmt,
            tfTotalDebitAmt, tfAttachmentNo;

    @FXML
    private DatePicker dpTransactionDate, dpReferenceDate, dpExpiryDate, dpJETransactionDate;

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
    private TableColumn tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail, tblCostDetail, tblOrderQuantityDetail, tblReceiveQuantityDetail,
            tblDiscountAmtDetail, tblTotalDetail, tblRowNo, tblSupplier, tblDate, tblReferenceNo, tblJERowNoDetail, tblJEAcctCodeDetail, tblJEAcctDescriptionDetail,
            tblJECreditAmtDetail, tblJEDebitAmtDetail, tblRowNoAttachment, tblFileNameAttachment;

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
            poPurchaseReceivingController.initFields();
            loadRecordSearch();

            AnchorPane root = (AnchorPane) apMainAnchor;
            Scene scene = root.getScene();
            if (scene != null) {
                setKeyEvent(scene);
            } else {
                root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        setKeyEvent(newScene);
                    }
                });
            }
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

    private void initTabSelection() {
        tabJE.setOnSelectionChanged(event -> {
            if (tabJE.isSelected()) {
                if (pnEditMode == EditMode.READY) {
                    lbSelectTabJE = true;
                }
            }
        });
    }

    private void setKeyEvent(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                System.out.println("tested key press");
                if (poPurchaseReceivingController.getEditMode() == EditMode.READY) {
                    showAttachmentlDialog();
                }
            }
        }
        );
    }

    public void showAttachmentlDialog() {
        poJSON = new JSONObject();
        stage.closeSerialDialog();
        openedAttachment = "";
        if (poPurchaseReceivingController.getTransactionAttachmentCount() <= 0) {
            ShowMessageFX.Warning(null, pxeModuleName, "No transaction attachment to load.");
            return;
        }
        openedAttachment = poPurchaseReceivingController.TransactionAttachmentList(pnMain).getModel().getTransactionNo();
        Map<String, Pair<String, String>> data = new HashMap<>();
        data.clear();
        for (int lnCtr = 0; lnCtr < poPurchaseReceivingController.getTransactionAttachmentCount(); lnCtr++) {
            data.put(String.valueOf(lnCtr + 1), new Pair<>(String.valueOf(poPurchaseReceivingController.TransactionAttachmentList(lnCtr).getModel().getFileName()),
                    poPurchaseReceivingController.TransactionAttachmentList(lnCtr).getModel().getDocumentType()));

        }
        AttachmentDialogController controller = new AttachmentDialogController();
        controller.addData(data);
        try {
            stage.showDialog(getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/views/AttachmentDialog.fxml"), controller, "Attachment Dialog", false, false, true);
        } catch (IOException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        lastFocusedTextField = source;
        previousSearchedTextField = null;
        if (source instanceof CheckBox) {
            CheckBox checkedBox = (CheckBox) source;
            switch (checkedBox.getId()) {
                case "cbVatInclusive":
//                    poPurchaseReceivingController.Master().se
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
                    case "btnPrint":
                        poJSON = poPurchaseReceivingController.printRecord(() -> {
                            if (isPrinted) {
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                poPurchaseReceivingController.resetMaster();
                                poPurchaseReceivingController.resetOthers();
                                poPurchaseReceivingController.Detail().clear();
                                imageView.setImage(null);
                                pnEditMode = EditMode.UNKNOWN;
                                clearTextFields();
                                initButton(pnEditMode);
                            }
                            Platform.runLater(() -> {
                                try {
                                    if (!isPrinted) {
                                        poPurchaseReceivingController.OpenTransaction(poPurchaseReceivingController.PurchaseOrderReceivingList(pnMain).getTransactionNo());
                                        poPurchaseReceivingController.loadAttachments();
                                    }
                                    loadRecordMaster();
                                    loadTableDetail();
                                    loadTableAttachment();
                                } catch (CloneNotSupportedException ex) {
                                    Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (SQLException ex) {
                                    Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (GuanzonException ex) {
                                    Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                isPrinted = false;
                            });

                        });
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
                        if ((lastFocusedTextField != null)) {
                            if (lastFocusedTextField instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField;
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apBrowse, apMaster, apDetail,
                                        apJEMaster, apJEDetail).contains(tf.getId())) {
                                    if (lastFocusedTextField == previousSearchedTextField) {
                                        break;
                                    }
                                    previousSearchedTextField = lastFocusedTextField;
                                    // Create a simulated KeyEvent for F3 key press
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
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        //Retrieve data from purchase order to table main

                        retrievePOR();
                        JFXUtil.disableAllHighlight(tblViewMainList, highlightedRowsMain);
                        showRetainedHighlight(false);
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

                                // Print Transaction Prompt
                                loJSON = poPurchaseReceivingController.OpenTransaction(poPurchaseReceivingController.Master().getTransactionNo());
                                poPurchaseReceivingController.loadAttachments();
                                loadRecordMaster();
                                isPrinted = false;
                                if ("success".equals(loJSON.get("result"))) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to print this transaction?")) {
                                        isPrinted = true;
                                        btnPrint.fire();
                                    }
                                }
                                if (!isPrinted) {
                                    JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                }
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
                                plOrderNoPartial.add(new Pair<>(String.valueOf(pnMain + 1), "1"));
                                showRetainedHighlight(true);
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

                if (lsButton.equals("btnSave") || lsButton.equals("btnCancel")) {
                    poPurchaseReceivingController.resetMaster();
                    poPurchaseReceivingController.resetOthers();
                    poPurchaseReceivingController.Detail().clear();
                    imageView.setImage(null);
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnPrint", "btnArrowRight", "btnArrowLeft", "btnRetrieve")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                    loadTableAttachment();
                }
                initButton(pnEditMode);
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException | ParseException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;

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
                    poJSON = poPurchaseReceivingController.Master().setDiscountRate((Double.valueOf(lsValue.replace(",", "")) / 100.00));
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

                    poJSON = poPurchaseReceivingController.computeDiscountRate(Double.valueOf(lsValue.replace(",", "")));
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        break;
                    }
                    poJSON = poPurchaseReceivingController.Master().setFreight(Double.valueOf(lsValue.replace(",", "")));
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

        lastFocusedTextField = txtField;
        previousSearchedTextField = null;

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
        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;
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
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setUnitPrce((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfDiscRateDetail":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setDiscount((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfAddlDiscAmtDetail":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setAdditionalDiscount((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;

                case "tfJEAcctCode":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setUnitPrce((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfJEAcctDescription":
                    if (lsValue.isEmpty()) {
                        lsValue = "0.00";
                    }
                    poJSON = poPurchaseReceivingController.Detail(pnDetail).setDiscount((Double.valueOf(lsValue.replace(",", ""))));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfReportMonthYear":
                    String value = tfReportMonthYear.getText().trim();
                    boolean lbproceed = false;
                    if (value.matches("\\d{1,2}/\\d{4}")) {
                        try {
                            int month = Integer.parseInt(value.split("/")[0]);
                            int year = Integer.parseInt(value.split("/")[1]);
                            if (month >= 1 && month <= 12) {
                                month_year_picker.selectedMonth = month;
                                month_year_picker.selectedYear = year;

                                tfReportMonthYear.setText(String.format("%02d/%d", month, year));
                                month_year_picker.refreshMonthSelection();
                                if (month_year_picker.onDateSelected != null) {
                                    month_year_picker.onDateSelected.accept(YearMonth.of(year, month));
                                }
                            } else {
                                lbproceed = true;
                            }
                        } catch (NumberFormatException ignored) {
                            lbproceed = true;
                        }
                    } else {
                        lbproceed = true;
                    }
                    if (lbproceed) {
                        ShowMessageFX.Warning(null, pxeModuleName, "Invalid input");
                    }
                    month_year_picker.popup.hide();
                    break;
                case "tfCreditAmt":
                    break;
                case "tfDebitAmt":
                    break;
            }
            if (JFXUtil.isObjectEqualTo(lsTxtFieldID, "tfJEAcctCode", "tfJEAcctDescription", "tfReportMonthYear", "tfReportMonthYear",
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
        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;
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
        int lnReceiveQty = Integer.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity().toString());
        apDetail.requestFocus();
        int lnNewvalue = Integer.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity().toString());
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

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            int lnRow = pnDetail;

            TableView<?> currentTable = tblViewTransDetailList;

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
                        case "tfTotalDetail":
                            int lnReceiveQty = Integer.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity().toString());
                            apDetail.requestFocus();
                            int lnNewvalue = Integer.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity().toString());
                            if (lnReceiveQty != lnNewvalue && (lnReceiveQty > 0
                                    && poPurchaseReceivingController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poPurchaseReceivingController.Detail(pnDetail).getStockId()))) {
                                tfCost.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
                                loadRecordDetail();
                                tfCost.requestFocus();
                                event.consume();
                            }
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfCost":
                        case "tfDiscRateDetail":
                        case "tfAddlDiscAmtDetail":
                        case "tfTotalDetail":
                            moveNext();
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
                    }
                    break;
                default:
                    break;
            }

        } catch (GuanzonException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (SQLException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    ChangeListener<Boolean> datepicker_Focus = (observable, oldValue, newValue) -> {
        poJSON = new JSONObject();
        JFXUtil.setJSONSuccess(poJSON, "success");
        try {
            if (!newValue) { // Lost focus
                DatePicker datePicker = (DatePicker) ((javafx.beans.property.ReadOnlyBooleanProperty) observable).getBean();
                String lsID = datePicker.getId();
                String inputText = datePicker.getEditor().getText();
                LocalDate currentDate = LocalDate.now();
                LocalDate selectedDate = null;

                lastFocusedTextField = datePicker;
                previousSearchedTextField = null;

                JFXUtil.JFXUtilDateResult ldtResult = JFXUtil.processDate(inputText, datePicker);
                poJSON = ldtResult.poJSON;
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    loadRecordMaster();
                    return;
                }
                selectedDate = ldtResult.selectedDate;

                String formattedDate = selectedDate.toString();

                switch (lsID) {
                    case "dpTransactionDate":
                        if (selectedDate == null) {
                            break;
                        }
                        if (selectedDate.isAfter(currentDate)) {
                            JFXUtil.setJSONError(poJSON, "Future dates are not allowed.");
                        } else {
                            poPurchaseReceivingController.Master().setTransactionDate((SQLUtil.toDate(formattedDate, "yyyy-MM-dd")));
                        }
                        break;
                    case "dpReferenceDate":
                        if (selectedDate == null) {
                            break;
                        }
                        if (selectedDate.isAfter(currentDate)) {
                            JFXUtil.setJSONError(poJSON, "Future dates are not allowed.");
                        } else {
                            poPurchaseReceivingController.Master().setReferenceDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        }
                        break;
                    case "dpExpiryDate":
                        if (selectedDate == null) {
                            break;
                        }
                        if (selectedDate.isBefore(currentDate)) {
                            JFXUtil.setJSONError(poJSON, "The selected date cannot be earlier than the current date.");
                        } else {
                            poPurchaseReceivingController.Detail(pnDetail).setExpiryDate(SQLUtil.toDate(formattedDate, "yyyy-MM-dd"));
                        }
                        break;
                    default:

                        break;
                }
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    // datePicker.requestFocus();
                }
                if (JFXUtil.isObjectEqualTo(lsID, "dpJETransactionDate")) {
                    loadRecordJEMaster();
                } else {
                    Platform.runLater(() -> {
                        if (lsID.equals("dpExpiryDate")) {
                            loadRecordDetail();
                        } else {
                            loadRecordMaster();
                        }
                    });
                }

                datePicker.getEditor().setText(formattedDate);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void showRetainedHighlight(boolean isRetained) {
        if (isRetained) {
            for (Pair<String, String> pair : plOrderNoPartial) {
                if (!"0".equals(pair.getValue())) {
                    plOrderNoFinal.add(new Pair<>(pair.getKey(), pair.getValue()));
                }
            }
        }
        JFXUtil.disableAllHighlightByColor(tblViewMainList, "#C1E1C1", highlightedRowsMain);
        plOrderNoPartial.clear();
        for (Pair<String, String> pair : plOrderNoFinal) {
            if (!"0".equals(pair.getValue())) {
                JFXUtil.highlightByKey(tblViewMainList, pair.getKey(), "#C1E1C1", highlightedRowsMain);
            }
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
                    plOrderNoFinal.clear();

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
                                Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            } catch (GuanzonException ex) {
                                Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            }

                            if (poPurchaseReceivingController.PurchaseOrderReceivingList(lnCtr).getTransactionStatus().equals(PurchaseOrderReceivingStatus.POSTED)) {
                                plOrderNoPartial.add(new Pair<>(String.valueOf(lnCtr + 1), "1"));
                            }
                        }
                        showRetainedHighlight(true);
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
//            lblSource.setText(poPurchaseReceivingController.Master().Company().getCompanyName() + " - " + poPurchaseReceivingController.Master().Industry().getDescription());
            lblSource.setText(poPurchaseReceivingController.Master().Company().getCompanyName());
            if (psSupplierId.equals("")) {
                tfSearchSupplier.setText("");
            } else {
                tfSearchSupplier.setText(poPurchaseReceivingController.Master().Supplier().getCompanyName());
            }
            if (psBranchId.equals("")) {
                tfSearchReceiveBranch.setText("");
            } else {
                tfSearchReceiveBranch.setText(poPurchaseReceivingController.Master().Branch().getBranchName());
            }
            try {
                if (tfSearchReferenceNo.getText() == null || tfSearchReferenceNo.getText().equals("")) {
                    tfSearchReferenceNo.setText("");
                } else {

                }
            } catch (Exception e) {
                tfSearchReferenceNo.setText("");
            }

        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
//        tfJEAcctCode.setText("");
//        tfJEAcctDescription.setText("");
        //tfReportMonthYear.setText(""); //

        month_year_picker.setYearMonth(month_year_picker.getYearMonth());
//        tfCreditAmt.setText("");
//        tfDebitAmt.setText("");
    }

    public void loadRecordDetail() {

        try {
            if (pnDetail < 0 || pnDetail > poPurchaseReceivingController.getDetailCount() - 1) {
                return;
            }
            // Expiry Date
            String lsExpiryDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Detail(pnDetail).getExpiryDate());
            dpExpiryDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsExpiryDate, "yyyy-MM-dd"));

            tfBarcode.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().getBarCode());
            tfDescription.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().getDescription());
            tfSupersede.setText(poPurchaseReceivingController.Detail(pnDetail).Supersede().getBarCode());
            tfMeasure.setText(poPurchaseReceivingController.Detail(pnDetail).Inventory().Measure().getDescription());
            tfOrderNo.setText(poPurchaseReceivingController.Detail(pnDetail).getOrderNo());
            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(pnDetail).getUnitPrce()));
//            tfDiscRateDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(pnDetail).getDiscount().doubleValue()*100));
//            Platform.runLater(() -> {
//                double lnValue = 0.00;
//                if(poPurchaseReceivingController.Detail(pnDetail).getDiscount() != null){
//                    lnValue = poPurchaseReceivingController.Detail(pnDetail).getDiscount().doubleValue();
//                }
//                if (!Double.isNaN(lnValue)) {
//                    tfDiscRateDetail.setText(String.format("%.2f", (lnValue*100.00)));
//                } else {
//                    tfDiscRateDetail.setText(String.format("%.2f", 0.00));
//                }
//            });
//            
//            tfAddlDiscAmtDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(pnDetail).getAdditionalDiscount()));
            tfDiscRateDetail.setText("0.00");
            tfAddlDiscAmtDetail.setText("0.00");
            double lnTotal = 0.00; //TODO
            tfTotalDetail.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal));
            tfOrderQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getOrderQty().intValue()));
            tfReceiveQuantity.setText(String.valueOf(poPurchaseReceivingController.Detail(pnDetail).getQuantity()));
            cbVatable.setSelected(poPurchaseReceivingController.Detail(pnDetail).isVatable());

            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadRecordJEMaster() {
        lblJEStatus.setText("");
        tfJETransactionNo.setText("");
        dpJETransactionDate.setValue(null);
        tfTotalCreditAmt.setText("");
        tfTotalDebitAmt.setText("");
        lblJEStatus.setText("");
        taJERemarks.setText("");

    }

    public void loadRecordMaster() {
        boolean lbIsReprint = poPurchaseReceivingController.Master().getPrint().equals("1") ? true : false;
        if (lbIsReprint) {
            btnPrint.setText("Reprint");
        } else {
            btnPrint.setText("Print");
        }

        try {

            Platform.runLater(() -> {
                String lsActive = poPurchaseReceivingController.Master().getTransactionStatus();
                boolean lbPrintStat = pnEditMode == EditMode.READY && !PurchaseOrderReceivingStatus.VOID.equals(lsActive);

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
                JFXUtil.setButtonsVisibility(lbPrintStat, btnPrint);
            });

            if (poPurchaseReceivingController.Master().getDiscountRate().doubleValue() > 0.00) {
                poPurchaseReceivingController.computeDiscount(poPurchaseReceivingController.Master().getDiscountRate().doubleValue());
            } else {
                if (poPurchaseReceivingController.Master().getDiscount().doubleValue() > 0.00) {
                    poPurchaseReceivingController.computeDiscountRate(poPurchaseReceivingController.Master().getDiscount().doubleValue());
                }
            }
            poPurchaseReceivingController.computeFields();

            // Transaction Date
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
            //ReferenceDate
            String lsReferenceDate = CustomCommonUtil.formatDateToShortString(poPurchaseReceivingController.Master().getReferenceDate());
            dpReferenceDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsReferenceDate, "yyyy-MM-dd"));

            tfTransactionNo.setText(poPurchaseReceivingController.Master().getTransactionNo());

            tfSupplier.setText(poPurchaseReceivingController.Master().Supplier().getCompanyName());
            tfBranch.setText(poPurchaseReceivingController.Master().Branch().getBranchName());
            tfTrucking.setText(poPurchaseReceivingController.Master().Trucking().getCompanyName());
            tfTerm.setText(poPurchaseReceivingController.Master().Term().getDescription());
            tfReferenceNo.setText(poPurchaseReceivingController.Master().getReferenceNo());
            taRemarks.setText(poPurchaseReceivingController.Master().getRemarks());

            Platform.runLater(() -> {
                double lnValue = poPurchaseReceivingController.Master().getDiscountRate().doubleValue();
                if (!Double.isNaN(lnValue)) {
                    tfDiscountRate.setText(String.format("%.2f", (poPurchaseReceivingController.Master().getDiscountRate().doubleValue() * 100.00)));

                } else {
                    tfDiscountRate.setText(String.format("%.2f", 0.00));
                }
            });
            tfDiscountAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReceivingController.Master().getDiscount().doubleValue())));
            tfNetTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReceivingController.Master().getTransactionTotal().doubleValue())));

            tfSINo.setText(poPurchaseReceivingController.Master().getSalesInvoice());
            tfFreightAmt.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReceivingController.Master().getFreight().doubleValue())));

            /* TODO 
            Vat Inclucsive
            cVATaxabl ano ito
            nVATRatex ano ito

            vat sales = computation
            vat amount = computation and column?
            zero vat sales = computation
            vat exempt sales = computation

            net total computation
            Net Total = Transaction total - Discount Rate - Additional Discount - Freight - Vat Sales - Vat Amount - Zero Vat sales - Vat Exempt Sales 
            
            tfNetTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf());
            tfVatAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf());
            tfVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf());
            tfVatExemptSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf());
            tfZeroVatSales.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf());
            cbVatInclusive.setSelected();
             */
            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

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
                if (!openedAttachment.equals(poPurchaseReceivingController.TransactionAttachmentList(pnMain).getModel().getTransactionNo())) {
                    stage.closeSerialDialog();
                }
            } else {
                stage.closeSerialDialog();
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

            if (dialogStage != null) {
                if (dialogStage.isShowing()) {
                    dialogStage.close();
                }
            }

        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadTableJEDetail() {
        loadRecordJEDetail();
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
                        double lnTransactionTotal = 0.00;
                        double lnTotal = 0.00;
                        double lnDiscountAmt = 0.00;
                        for (lnCtr = 0; lnCtr < poPurchaseReceivingController.getDetailCount(); lnCtr++) {
                            try {
                                //TODO
                                lnTotal = poPurchaseReceivingController.Detail(lnCtr).getUnitPrce().doubleValue() * poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue();
                                lnDiscountAmt = 0.00;

                                lnTransactionTotal = lnTransactionTotal
                                        + ((poPurchaseReceivingController.Detail(lnCtr).getUnitPrce().doubleValue() * poPurchaseReceivingController.Detail(lnCtr).getQuantity().intValue())
                                        - lnDiscountAmt);
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
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).Inventory().getBarCode()),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).Inventory().getDescription()),
                                                String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReceivingController.Detail(lnCtr).getUnitPrce())),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getOrderQty().intValue()),
                                                String.valueOf(poPurchaseReceivingController.Detail(lnCtr).getQuantity()),
                                                String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnDiscountAmt)),
                                                String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal)) //identify total
                                        ));
                            }
                        }

                        tfTransactionTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTransactionTotal));

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
                        Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(SIPosting_Controller.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpReferenceDate, dpExpiryDate, dpJETransactionDate);
        JFXUtil.setFocusListener(datepicker_Focus, dpTransactionDate, dpReferenceDate, dpExpiryDate, dpJETransactionDate);
        JFXUtil.setDatePickerNextFocusByEnter(dpTransactionDate, dpReferenceDate, dpExpiryDate, dpJETransactionDate);
    }

    public void initTextFields() {
        JFXUtil.setFocusListener(txtField_Focus, tfSearchReceiveBranch, tfSearchSupplier, tfSearchReferenceNo);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks, taJERemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfReferenceNo, tfSINo, tfTerm, tfDiscountRate, tfDiscountAmount,
                tfVatSales, tfZeroVatSales, tfVatAmount, tfVatExemptSales, tfTotalCreditAmt, tfTotalDebitAmt,
                tfTotalCreditAmt, tfTotalDebitAmt);
        JFXUtil.setFocusListener(txtDetail_Focus, tfCost, tfDiscRateDetail, tfAddlDiscAmtDetail, tfTotalDetail,
                tfJEAcctCode, tfJEAcctDescription, tfReportMonthYear, tfCreditAmt, tfDebitAmt);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apMaster, apDetail, apJEDetail, apBrowse);

        JFXUtil.setCommaFormatter(tfDiscountAmount, tfFreightAmt, tfVatSales,
                tfVatAmount, tfZeroVatSales, tfVatExemptSales, tfCost, tfAddlDiscAmtDetail, tfCreditAmt, tfDebitAmt);
        CustomCommonUtil.inputIntegersOnly(tfReceiveQuantity);
        CustomCommonUtil.inputDecimalOnly(tfDiscountRate, tfDiscRateDetail,
                tfAddlDiscAmtDetail, tfTotalDetail);
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

        month_year_picker = JFXUtil.MonthYearPicker.setupMonthYearPicker(tfReportMonthYear, ym -> {
        });

    }

    public void initTableOnClick() {
        tblViewJEDetails.setOnMouseClicked(event -> {
            pnJEDetail = tblViewJEDetails.getSelectionModel().getSelectedIndex();
            if (pnJEDetail >= 0) {
                loadRecordJEDetail();
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

        tblViewMainList.setRowFactory(tv -> new TableRow<ModelDeliveryAcceptance_Main>() {
            @Override
            protected void updateItem(ModelDeliveryAcceptance_Main item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle(""); // Reset for empty rows
                } else {
                    String key = item.getIndex01(); // defines the ReferenceNo
                    if (highlightedRowsMain.containsKey(key)) {
                        List<String> colors = highlightedRowsMain.get(key);
                        if (!colors.isEmpty()) {
                            setStyle("-fx-background-color: " + colors.get(colors.size() - 1) + ";");
                        }
                    } else {
                        setStyle(""); // Default style
                    }
                }
            }
        });

        tblViewTransDetailList.setRowFactory(tv -> new TableRow<ModelDeliveryAcceptance_Detail>() {
            @Override
            protected void updateItem(ModelDeliveryAcceptance_Detail item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle(""); // Reset for empty rows
                } else {
                    String key = item.getIndex01(); // defines the ReferenceNo
                    if (highlightedRowsDetail.containsKey(key)) {
                        List<String> colors = highlightedRowsDetail.get(key);
                        if (!colors.isEmpty()) {
                            setStyle("-fx-background-color: " + colors.get(colors.size() - 1) + ";");
                        }
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        JFXUtil.setKeyEventFilter(this::tableKeyEvents, tblViewTransDetailList, tblViewJEDetails, tblAttachments);

        JFXUtil.adjustColumnForScrollbar(tblViewMainList, 1);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetailList, 3);
        JFXUtil.adjustColumnForScrollbar(tblAttachments, 1);
        JFXUtil.adjustColumnForScrollbar(tblViewJEDetails, 2);
    }

    private void initButton(int fnValue) {

        boolean lbShow1 = (fnValue == EditMode.UPDATE);
        boolean lbShow2 = (fnValue == EditMode.READY || fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        // Manage visibility and managed state of other buttons
        //Update 
        JFXUtil.setButtonsVisibility(lbShow1, btnSearch, btnSave, btnCancel);

        //Ready
        JFXUtil.setButtonsVisibility(lbShow3, btnPrint, btnUpdate, btnHistory);
        JFXUtil.setButtonsVisibility(false, btnPost);

        //Unkown || Ready
        JFXUtil.setButtonsVisibility(lbShow4, btnClose);
        JFXUtil.setDisabled(!lbShow1, apMaster, apDetail, apAttachments);

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
        JFXUtil.setColumnLeft(tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail);
        JFXUtil.setColumnRight(tblCostDetail, tblDiscountAmtDetail, tblTotalDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetailList);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);

        SortedList<ModelDeliveryAcceptance_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewTransDetailList.comparatorProperty());
        tblViewTransDetailList.setItems(sortedData);
        tblViewTransDetailList.autosize();
    }

    public void initJEDetailsGrid() {
        JFXUtil.setColumnCenter(tblJERowNoDetail);
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
        imageinfo_temp.clear();
        previousSearchedTextField = null;
        lastFocusedTextField = null;

        JFXUtil.setValueToNull(dpTransactionDate, dpReferenceDate, dpExpiryDate);
        psSupplierId = "";
        psBranchId = "";

        JFXUtil.clearTextFields(apBrowse, apMaster, apDetail,
                apJEDetail, apJEMaster, apAttachments);

        cbVatInclusive.setSelected(false);
        cbVatable.setSelected(false);
    }

}
