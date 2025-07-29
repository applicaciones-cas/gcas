/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelSalesInquiry_Detail;

import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.parser.ParseException;
import javafx.animation.PauseTransition;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicReference;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import org.guanzon.appdriver.base.GRiderCAS;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela
 */
public class SalesInquiry_EntryCarController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    int pnDetail = 0;
    boolean lsIsSaved = false;
    private final String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    static SalesControllers poSalesInquiryController;
    public int pnEditMode;
    boolean pbKeyPressed = false;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";

    private ObservableList<ModelSalesInquiry_Detail> details_data = FXCollections.observableArrayList();
    private FilteredList<ModelSalesInquiry_Detail> filteredDataDetail;

    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();
    private boolean pbEntered = false;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apTransactionInfo, apMaster, apDetail;
    @FXML
    private HBox hbButtons, hboxid;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnHistory, btnRetrieve, btnClose;
    @FXML
    private TextField tfTransactionNo, tfBranch, tfSalesPerson, tfReferralAgent, tfInquirySource, tfClient, tfAddress, tfInquiryStatus, tfContactNo, tfBrand, tfModel, tfColor, tfModelVariant;
    @FXML
    private TextArea taRemarks;
    @FXML
    private ComboBox cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType;
    ObservableList<String> ClientType = ModelSalesInquiry_Detail.ClientType;
    ObservableList<String> InquiryType = ModelSalesInquiry_Detail.InquiryType;
    ObservableList<String> PurchaseType = ModelSalesInquiry_Detail.PurchaseType;
    ObservableList<String> CategoryType = ModelSalesInquiry_Detail.CategoryType;
    private final JFXUtil.RowDragLock dragLock = new JFXUtil.RowDragLock(true);
    @FXML
    private DatePicker dpTransactionDate, dpTargetDate;
    @FXML
    private TableView tblViewTransDetails;
    @FXML
    private TableColumn tblRowNoDetail, tblBrandDetail, tblDescriptionDetail;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        poSalesInquiryController = new SalesControllers(oApp, null);
        poJSON = new JSONObject();
        poJSON = poSalesInquiryController.SalesInquiry().InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            System.err.println((String) poJSON.get("message"));
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
        initComboBoxes();
        initTextFields();
        initDatePickers();
        initDetailsGrid();
        initTableOnClick();
        clearTextFields();
        pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();
        initButton(pnEditMode);

        Platform.runLater(() -> {
            poSalesInquiryController.SalesInquiry().Master().setIndustryId(psIndustryId);
            poSalesInquiryController.SalesInquiry().Master().setCompanyId(psCompanyId);
            poSalesInquiryController.SalesInquiry().Master().setCategoryCode(psCategoryId);
            poSalesInquiryController.SalesInquiry().setIndustryId(psIndustryId);
            poSalesInquiryController.SalesInquiry().setCompanyId(psCompanyId);
            poSalesInquiryController.SalesInquiry().setCategoryId(psCategoryId);
            poSalesInquiryController.SalesInquiry().setWithUI(true);
            loadRecordSearch();

            btnNew.fire();
        });
        JFXUtil.initKeyClickObject(apMainAnchor, lastFocusedTextField, previousSearchedTextField); // for btnSearch Reference
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
                        poSalesInquiryController.SalesInquiry().setTransactionStatus(SalesInquiryStatic.OPEN);
                        poJSON = poSalesInquiryController.SalesInquiry().searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();
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
                        poSalesInquiryController.SalesInquiry().resetMaster();
                        poSalesInquiryController.SalesInquiry().Detail().clear();
                        clearTextFields();

                        poJSON = poSalesInquiryController.SalesInquiry().NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }

                        poSalesInquiryController.SalesInquiry().initFields();
                        pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();

                        break;
                    case "btnUpdate":
                        poJSON = poSalesInquiryController.SalesInquiry().OpenTransaction(poSalesInquiryController.SalesInquiry().Master().getTransactionNo());
                        poJSON = poSalesInquiryController.SalesInquiry().UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();
                        break;
                    case "btnSearch":
                        String lsMessage = "Focus a searchable textfield to search";
                        if ((lastFocusedTextField.get() != null)) {
                            if (lastFocusedTextField.get() instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField.get();
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apMaster, apDetail).contains(tf.getId())) {
                                    if (lastFocusedTextField.get() == previousSearchedTextField.get()) {
                                        break;
                                    }
                                    previousSearchedTextField.set(lastFocusedTextField.get());
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
                            //Clear data
                            poSalesInquiryController.SalesInquiry().resetMaster();
                            poSalesInquiryController.SalesInquiry().Detail().clear();
                            clearTextFields();

                            poSalesInquiryController.SalesInquiry().Master().setIndustryId(psIndustryId);
                            poSalesInquiryController.SalesInquiry().Master().setCompanyId(psCompanyId);
                            poSalesInquiryController.SalesInquiry().Master().setCategoryCode(psCategoryId);
                            poSalesInquiryController.SalesInquiry().initFields();
                            pnEditMode = EditMode.UNKNOWN;

                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poSalesInquiryController.SalesInquiry().SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poSalesInquiryController.SalesInquiry().AddDetail();
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON = poSalesInquiryController.SalesInquiry().OpenTransaction(poSalesInquiryController.SalesInquiry().Master().getTransactionNo());
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poSalesInquiryController.SalesInquiry().Master().getTransactionStatus().equals(SalesInquiryStatic.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poSalesInquiryController.SalesInquiry().ConfirmTransaction("");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

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

                if (lsButton.equals("btnPrint")) { //|| lsButton.equals("btnCancel")
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                initButton(pnEditMode);

                if (lsButton.equals("btnUpdate")) {
                    if (poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId() != null && !"".equals(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId())) {
                        tfBrand.requestFocus();
                    } else {
                        tfBrand.requestFocus();
                    }
                }

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (ParseException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadRecordMaster() {
        boolean lbDisable = pnEditMode == EditMode.ADDNEW;
        JFXUtil.setDisabled(!lbDisable, tfClient, tfSalesPerson, cmbClientType, cmbCategoryType);
        try {
            Platform.runLater(() -> {
                String lsActive = pnEditMode == EditMode.UNKNOWN ? "-1" : poSalesInquiryController.SalesInquiry().Master().getTransactionStatus();
                Map<String, String> statusMap = new HashMap<>();
                statusMap.put(SalesInquiryStatic.POSTED, "POSTED");
                statusMap.put(SalesInquiryStatic.PAID, "PAID");
                statusMap.put(SalesInquiryStatic.CONFIRMED, "CONFIRMED");
                statusMap.put(SalesInquiryStatic.OPEN, "OPEN");
                statusMap.put(SalesInquiryStatic.VOID, "VOIDED");
                statusMap.put(SalesInquiryStatic.CANCELLED, "CANCELLED");
                String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN"); //default
                lblStatus.setText(lsStat);
            });

            // Transaction Date
            tfTransactionNo.setText(poSalesInquiryController.SalesInquiry().Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poSalesInquiryController.SalesInquiry().Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
            String lsTargetDate = CustomCommonUtil.formatDateToShortString(poSalesInquiryController.SalesInquiry().Master().getTargetDate());
            dpTargetDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTargetDate, "yyyy-MM-dd"));

            tfBranch.setText(poSalesInquiryController.SalesInquiry().Master().Branch().getBranchName());
            tfInquiryStatus.setText(poSalesInquiryController.SalesInquiry().Master().getInquiryStatus());//TODO
            tfInquirySource.setText("");

            tfClient.setText(poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());
            tfAddress.setText(poSalesInquiryController.SalesInquiry().Master().ClientAddress().getAddress());
            tfContactNo.setText(poSalesInquiryController.SalesInquiry().Master().ClientMobile().getMobileNo());

            tfSalesPerson.setText(poSalesInquiryController.SalesInquiry().Master().SalesPerson().getCompanyName());
            tfReferralAgent.setText(poSalesInquiryController.SalesInquiry().Master().ReferralAgent().getCompanyName());
            taRemarks.setText(poSalesInquiryController.SalesInquiry().Master().getRemarks());

            if (pnEditMode != EditMode.UNKNOWN) {
                cmbInquiryType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getSourceCode()));
                cmbPurchaseType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getPurchaseType()));
                if (poSalesInquiryController.SalesInquiry().Master().getClientId() != null && !"".equals(poSalesInquiryController.SalesInquiry().Master().getClientId())) {
                    cmbClientType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().Client().getClientType()));
                } else {
                    cmbClientType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getClientType()));
                }
                cmbCategoryType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getCategoryType()));
            } else {
                cmbInquiryType.getSelectionModel().select(0);
                cmbPurchaseType.getSelectionModel().select(0);
                cmbClientType.getSelectionModel().select(0);
                cmbCategoryType.getSelectionModel().select(0);
            }

            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadRecordDetail() {
        try {
            if (pnDetail < 0 || pnDetail > poSalesInquiryController.SalesInquiry().getDetailCount() - 1) {
                return;
            }

            tfBrand.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Brand().getDescription());
            tfModel.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Model().getDescription());
            tfModelVariant.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).ModelVariant().getDescription());
            tfColor.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Color().getDescription());
            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tableKeyEvents(KeyEvent event) {
        if (details_data.size() > 0) {
            TableView currentTable = (TableView) event.getSource();
            TablePosition focusedCell = currentTable.getFocusModel().getFocusedCell();
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
                if (poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId() != null && !poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId().equals("")) {
                    tfBrand.requestFocus();
                } else {
                    tfBrand.requestFocus();
                }
                event.consume();
            }
        }
    }

    public void initTableOnClick() {
        tblViewTransDetails.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewTransDetails.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    if (!JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getBrandId(), null, "")) {
                        if (!JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getModelId(), null, "")) {
                            tfColor.requestFocus();
                        } else {
                            tfModel.requestFocus();
                        }
                    } else {
                        tfBrand.requestFocus();
                    }
                }
            }
        });

        tblViewTransDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetails); // need to use computed-size in min-width of the column to work
        JFXUtil.enableRowDragAndDrop(tblViewTransDetails, item -> ((ModelSalesInquiry_Detail) item).index01Property(),
                item -> ((ModelSalesInquiry_Detail) item).index03Property(),
                item -> ((ModelSalesInquiry_Detail) item).index04Property(), dragLock, index -> {

                    for (ModelSalesInquiry_Detail d : details_data) {
                        String brand = d.getIndex04();
                        String model = d.getIndex05();
                        String color = d.getIndex06();
                        String variant = d.getIndex07();
                        String priorityStr = d.getIndex01();
                        for (int i = 0, n = poSalesInquiryController.SalesInquiry().getDetailCount(); i < n; i++) {
                            try {
                                if (!brand.equals(poSalesInquiryController.SalesInquiry().Detail(i).getBrandId())
                                || !model.equals(poSalesInquiryController.SalesInquiry().Detail(i).getModelId())
                                || !color.equals(poSalesInquiryController.SalesInquiry().Detail(i).getColorId())
                                || !variant.equals(poSalesInquiryController.SalesInquiry().Detail(i).ModelVariant().getVariantId())) {
                                    continue;
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(SalesInquiry_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (GuanzonException ex) {
                                Logger.getLogger(SalesInquiry_EntryMCController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            try {
                                /*System.out.println(d.getIndex02() +" - "+priorityStr);*/
                                poSalesInquiryController.SalesInquiry().Detail(i).setPriority(Integer.parseInt(priorityStr));
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid priority: " + priorityStr);
                            }
                            break;
                        }
                    }
                    pnDetail = index;
                    loadTableDetail();
                });
    }

    public void loadTableDetail() {
        pbEntered = false;
        // Setting data to table detail
        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewTransDetails.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
//                Thread.sleep(1000);
                // contains try catch, for loop of loading data to observable list until loadTab()
                Platform.runLater(() -> {
                    int lnCtr;
                    details_data.clear();
                    try {

                        String lsBrandId = "";
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poSalesInquiryController.SalesInquiry().getDetailCount() - 1;
                            while (lnCtr >= 0) {

                                if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getModelId() == null || poSalesInquiryController.SalesInquiry().Detail(lnCtr).getModelId().equals("")) {
                                    if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getBrandId() != null
                                            && !"".equals(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getBrandId())) {
                                        lsBrandId = poSalesInquiryController.SalesInquiry().Detail(lnCtr).getBrandId();
                                    }
                                    if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getEditMode() == EditMode.UPDATE) {
                                        poSalesInquiryController.SalesInquiry().removeDetail(poSalesInquiryController.SalesInquiry().Detail(lnCtr));
                                    }
                                    poSalesInquiryController.SalesInquiry().Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poSalesInquiryController.SalesInquiry().getDetailCount() - 1) >= 0) {
                                if (poSalesInquiryController.SalesInquiry().Detail(poSalesInquiryController.SalesInquiry().getDetailCount() - 1).getModelId() != null && !poSalesInquiryController.SalesInquiry().Detail(poSalesInquiryController.SalesInquiry().getDetailCount() - 1).getModelId().equals("")) {
                                    poSalesInquiryController.SalesInquiry().AddDetail();
                                }
                            }

                            if ((poSalesInquiryController.SalesInquiry().getDetailCount() - 1) < 0) {
                                poSalesInquiryController.SalesInquiry().AddDetail();
                            }

                            //Set brand Id to last row
                            if (!lsBrandId.isEmpty()) {
                                poSalesInquiryController.SalesInquiry().Detail(poSalesInquiryController.SalesInquiry().getDetailCount() - 1).setBrandId(lsBrandId);
                            }

                            poSalesInquiryController.SalesInquiry().sortPriority();
                        }

                        double lnTotal = 0.0;
                        for (lnCtr = 0; lnCtr < poSalesInquiryController.SalesInquiry().getDetailCount(); lnCtr++) {
                            String lsBrand = "";
                            if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).Brand().getDescription() != null) {
                                lsBrand = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Brand().getDescription();
                            }
                            String lsModelVariant = " ";
                            if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId() != null && !"".equals(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId())) {
                                lsModelVariant = " " + poSalesInquiryController.SalesInquiry().Detail(lnCtr).ModelVariant().getDescription() + " ";
                            }
                            String lsModel = "";
                            if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).Model().getDescription() != null) {
                                lsModel = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Model().getDescription();
                            }
                            String lsColor = "";
                            if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).Color().getDescription() != null) {
                                lsColor = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Color().getDescription();
                            }
                            String lsDescription = lsModel
                                    + lsModelVariant
                                    + lsColor;
                            details_data.add(
                                    new ModelSalesInquiry_Detail(
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getPriority()),
                                            String.valueOf(lsBrand),
                                            lsDescription,
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getBrandId()),
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getModelId()),
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getColorId()),
                                            poSalesInquiryController.SalesInquiry().Detail(lnCtr).ModelVariant().getVariantId()
                                    ));
                        }

                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                JFXUtil.selectAndFocusRow(tblViewTransDetails, 0);
                                pnDetail = tblViewTransDetails.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            JFXUtil.selectAndFocusRow(tblViewTransDetails, pnDetail);
                            loadRecordDetail();
                        }
                        loadRecordMaster();
                    } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    }

                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetails.setPlaceholder(loading.placeholderLabel);
                } else {
                    tblViewTransDetails.toFront();
                }
                loading.progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetails.setPlaceholder(loading.placeholderLabel);
                }
                loading.progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
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
                    poJSON = poSalesInquiryController.SalesInquiry().Master().setRemarks(lsValue);
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
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfBrand":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poSalesInquiryController.SalesInquiry().Detail(pnDetail).setBrandId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setStockId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setModelId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setColorId("");
                    }
                    break;
                case "tfModel":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setModelId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setStockId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setColorId("");
                    }
                    break;
                case "tfColor":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setColorId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Detail(pnDetail).setStockId("");
                    }
                    if (pbEntered) {
                        moveNext(false);
                        pbEntered = false;
                    }
                    break;
            }
            Platform.runLater(() -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                delay.setOnFinished(event -> {
                    loadTableDetail();
                });
                delay.play();
            });
        }
    };

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
                case "tfSalesPerson":
                    if (lsValue.isEmpty()) {
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setSalesMan("");
                    }
                    break;
                case "tfReferralAgent":
                    if (lsValue.isEmpty()) {
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setAgentId("");
                    }
                    break;
                case "tfInquirySource":
                    if (lsValue.isEmpty()) {
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setSourceNo("");
                    }
                    break;
                case "tfClient":
                    if (lsValue.isEmpty()) {
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setClientId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setAddressId("");
                        poJSON = poSalesInquiryController.SalesInquiry().Master().setContactId("");
                    }
                    break;

            }

            loadRecordMaster();

        }

    };

    public void moveNext(boolean isUp) {
        String lsBrand = poSalesInquiryController.SalesInquiry().Detail(pnDetail).getBrandId();
        apDetail.requestFocus();
        String ldblNewValue = poSalesInquiryController.SalesInquiry().Detail(pnDetail).getBrandId();
        pnDetail = isUp ? JFXUtil.moveToPreviousRow(tblViewTransDetails) : JFXUtil.moveToNextRow(tblViewTransDetails);
        loadRecordDetail();
        if (!JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getBrandId(), null, "")) {
            if (!JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getModelId(), null, "")) {
                tfColor.requestFocus();
            } else {
                tfModel.requestFocus();
            }
        } else {
            tfBrand.requestFocus();
        }
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            int lnRow = pnDetail;

            TableView<?> currentTable = tblViewTransDetails;
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();

            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEntered = true;
                    CommonUtils.SetNextFocus(txtField);
                    event.consume();
                    break;
                case UP:
                    switch (lsID) {
                        case "tfBrand":
                        case "tfModel":
                        case "tfColor":
                            moveNext(true);
                            event.consume();
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfBrand":
                        case "tfModel":
                        case "tfColor":
                            moveNext(false);
                            event.consume();
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfClient":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchClient(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfClient.setText("");
                                break;
                            }
                            loadRecordMaster();
                            return;
                        case "tfSalesPerson":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchSalesPerson(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSalesPerson.setText("");
                                break;
                            }
                            loadRecordMaster();
                            return;
                        case "tfReferralAgent":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchReferralAgent(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfReferralAgent.setText("");
                                break;
                            }
                            loadRecordMaster();
                            return;
                        case "tfBrand":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchBrand(lsValue, false, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfBrand.setText("");
                                break;
                            }
                            loadTableDetail();
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    tfModel.requestFocus();
                                });
                                delay.play();
                            });

                            break;
                        case "tfModel":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchModel(lsValue, false, pnDetail);
                            lnRow = (int) poJSON.get("row");
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfModel.setText("");
                                break;
                            }
                            loadTableDetail();
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(e -> {
                                    tfColor.requestFocus();
                                });
                                delay.play();
                            });
                            break;
                        case "tfColor":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchColor(lsValue, false, pnDetail);
                            lnRow = (int) poJSON.get("row");
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfModel.setText("");
                                break;
                            }
                            loadTableDetail();
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
                LocalDate currentDate = null, selectedDate = null, receivingDate = null;
                String lsServerDate = "", lsTransDate = "", lsSelectedDate = "", lsReceivingDate = "";

                JFXUtil.JFXUtilDateResult ldtResult = JFXUtil.processDate(inputText, datePicker);
                poJSON = ldtResult.poJSON;
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    loadRecordMaster();
                    return;
                }
                if (JFXUtil.isObjectEqualTo(inputText, null, "", "1900-01-01")) {
                    return;
                }
                selectedDate = ldtResult.selectedDate;

                switch (datePicker.getId()) {
                    case "dpTargetDate":
                        if (poSalesInquiryController.SalesInquiry().getEditMode() == EditMode.ADDNEW
                                || poSalesInquiryController.SalesInquiry().getEditMode() == EditMode.UPDATE) {
                            lsServerDate = sdfFormat.format(oApp.getServerDate());
                            lsTransDate = sdfFormat.format(poSalesInquiryController.SalesInquiry().Master().getTransactionDate());
                            lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                            currentDate = LocalDate.parse(lsTransDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                            selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                            if (selectedDate.isBefore(currentDate)) {
                                JFXUtil.setJSONError(poJSON, "Target date cannot be before the transaction date.");
                                pbSuccess = false;
                            } else {
                                poSalesInquiryController.SalesInquiry().Master().setTargetDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
                            }
                            if (pbSuccess) {
                            } else {
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                }
                            }
                            pbSuccess = false; //Set to false to prevent multiple message box: Conflict with server date vs transaction date validation
                            loadRecordMaster();
                            pbSuccess = true; //Set to original value

                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    final EventHandler<ActionEvent> comboBoxActionListener = event -> {
        Object source = event.getSource();
        @SuppressWarnings("unchecked")
        ComboBox<?> cb = (ComboBox<?>) source;

        String cbId = cb.getId();
        int selectedIndex = cb.getSelectionModel().getSelectedIndex();
        switch (cbId) {
            case "cmbClientType":
                //if client type is changed then remove the client 
                if (!poSalesInquiryController.SalesInquiry().Master().getClientType().equals(selectedIndex)) {
                    poSalesInquiryController.SalesInquiry().Master().setClientId("");
                    poSalesInquiryController.SalesInquiry().Master().setAddressId("");
                    poSalesInquiryController.SalesInquiry().Master().setContactId("");
                }
                poSalesInquiryController.SalesInquiry().Master().setClientType(String.valueOf(selectedIndex));
                break;
            case "cmbInquiryType":
                poSalesInquiryController.SalesInquiry().Master().setSourceCode(String.valueOf(selectedIndex));
                break;
            case "cmbPurchaseType":
                poSalesInquiryController.SalesInquiry().Master().setPurchaseType(String.valueOf(selectedIndex));
                break;
            case "cmbCategoryType":
                poSalesInquiryController.SalesInquiry().Master().setCategoryType(String.valueOf(selectedIndex));
                break;
            default:
                System.out.println("⚠ Unrecognized ComboBox ID: " + cbId);
                break;
        }
        loadRecordMaster();
    };

    private void initComboBoxes() {
        // Set the items of the ComboBox to the list of genders
        JFXUtil.setComboBoxItems(new JFXUtil.Pairs<>(ClientType, cmbClientType), new JFXUtil.Pairs<>(InquiryType, cmbInquiryType),
                new JFXUtil.Pairs<>(PurchaseType, cmbPurchaseType), new JFXUtil.Pairs<>(CategoryType, cmbCategoryType)
        );
        JFXUtil.setComboBoxActionListener(comboBoxActionListener, cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType);
        JFXUtil.initComboBoxCellDesignColor("#FF8201", cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType);
    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpTargetDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate, dpTargetDate);
    }

    public void initTextFields() {
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtMaster_Focus, tfClient, tfSalesPerson, tfReferralAgent, tfInquirySource);
        JFXUtil.setFocusListener(txtDetail_Focus, tfBrand, tfModel, tfColor);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);
        dragLock.isEnabled = lbShow;
        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(!lbShow, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSearch, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(lbShow2, btnUpdate, btnHistory);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);

        JFXUtil.setDisabled(!lbShow, taRemarks, apMaster, apDetail);

        switch (poSalesInquiryController.SalesInquiry().Master().getTransactionStatus()) {
            case SalesInquiryStatic.PAID:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
            case SalesInquiryStatic.VOID:
            case SalesInquiryStatic.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
        }
    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail);
        JFXUtil.setColumnLeft(tblBrandDetail, tblDescriptionDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetails);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);
//        SortedList<ModelSalesInquiry_Detail> sortedData = new SortedList<>(filteredDataDetail);
//        sortedData.comparatorProperty().bind(tblViewTransDetails.comparatorProperty());
        tblViewTransDetails.setItems(details_data);
        tblViewTransDetails.autosize();
    }

    public void loadRecordSearch() {
        try {
            lblSource.setText(poSalesInquiryController.SalesInquiry().Master().Company().getCompanyName() + " - " + poSalesInquiryController.SalesInquiry().Master().Industry().getDescription());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void clearTextFields() {
        JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField);
        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse);
    }
}
