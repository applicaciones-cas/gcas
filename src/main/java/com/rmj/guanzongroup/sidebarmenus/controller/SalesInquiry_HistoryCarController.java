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
import java.util.List;
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
import javafx.scene.control.Pagination;
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
import ph.com.guanzongroup.cas.cashflow.status.SOATaggingStatus;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import javafx.animation.PauseTransition;
import javafx.util.Pair;
import java.util.ArrayList;
import ph.com.guanzongroup.cas.cashflow.SOATagging;
import ph.com.guanzongroup.cas.cashflow.status.SOATaggingStatic;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.constant.UserRight;
import org.guanzon.appdriver.base.GRiderCAS;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.SalesInquiry;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela
 */
public class SalesInquiry_HistoryCarController  implements Initializable, ScreenInterface {
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
    private String psClientId = "";

    private ObservableList<ModelSalesInquiry_Detail> details_data = FXCollections.observableArrayList();
    private FilteredList<ModelSalesInquiry_Detail> filteredDataDetail;

    private Object lastFocusedTextField = null;
    private Object previousSearchedTextField = null;
    private boolean pbEntered = false;
    
    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apTransactionInfo, apMaster, apDetail;
    @FXML
    private HBox hbButtons, hboxid;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private Button btnBrowse, btnHistory, btnClose;
    @FXML
    private TextField tfSearchClient, tfSearchReferenceNo, tfTransactionNo, tfBranch, tfSalesPerson, tfReferralAgent, tfInquirySource, tfClient, tfAddress, tfInquiryStatus, tfContactNo, tfBrand, tfModel, tfColor, tfModelVariant;
    @FXML
    private TextArea taRemarks;
    @FXML
    private ComboBox cmbClientType, cmbInquiryType, cmbPurchaseType, cmbCategoryType;
    ObservableList<String> ClientType = FXCollections.observableArrayList(
            "Individual",
            "Corporate",
            "Institution"
    );
    ObservableList<String> InquiryType = FXCollections.observableArrayList(
            "Walk-in",
            "Referral",
            "Activity"
    );
    ObservableList<String> PurchaseType = FXCollections.observableArrayList(
            "Cash",
            "Cash Balance",
            "Term",
            "Installment",
            "Finance",
            "Insurance"
    );
    ObservableList<String> CategoryType = FXCollections.observableArrayList(
            "New",
            "Sold/Repo",
            "BNOS",
            "Demo"
    );
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
            poSalesInquiryController.SalesInquiry().setIndustryId(psIndustryId);
            poSalesInquiryController.SalesInquiry().setCompanyId(psCompanyId);
            poSalesInquiryController.SalesInquiry().setCategoryId(psCategoryId);
            loadRecordSearch();
        });
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

        Object source = event.getSource();
        if (source instanceof Button) {
            try {
                Button clickedButton = (Button) source;
                String lsButton = clickedButton.getId();
                switch (lsButton) {
                    case "btnBrowse":
                        poJSON = poSalesInquiryController.SalesInquiry().searchTransaction(psIndustryId, psCompanyId, psCategoryId, tfSearchClient.getText(), tfSearchReferenceNo.getText());
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();
                        psClientId = poSalesInquiryController.SalesInquiry().Master().getClientId();
                        
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
                    default:
                        ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                        break;
                }
                
                if (lsButton.equals("btnPrint")) {
                } else {
                    loadRecordMaster();
                    loadTableDetail();
                }
                
                initButton(pnEditMode);
            } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public void loadRecordMaster() {

        try {

            Platform.runLater(() -> {
                String lsActive = poSalesInquiryController.SalesInquiry().Master().getTransactionStatus();
                String lsStat = "UNKNOWN";
                switch (lsActive) {
                    case SalesInquiryStatic.POSTED:
                        lsStat = "POSTED";
                        break;
                    case SalesInquiryStatic.PAID:
                        lsStat = "PAID";
                        break;
                    case SalesInquiryStatic.CONFIRMED:
                        lsStat = "CONFIRMED";
                        break;
                    case SalesInquiryStatic.OPEN:
                        lsStat = "OPEN";
                        break;
                    case SalesInquiryStatic.VOID:
                        lsStat = "VOIDED";
                        break;
                    case SalesInquiryStatic.CANCELLED:
                        lsStat = "CANCELLED";
                        break;
                    default:
                        lsStat = "UNKNOWN";
                        break;

                }
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
            
            tfClient.setText(poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());
            tfAddress.setText(poSalesInquiryController.SalesInquiry().Master().ClientAddress().getAddress());
            tfContactNo.setText(poSalesInquiryController.SalesInquiry().Master().ClientMobile().getMobileNo());
            
            tfSalesPerson.setText(poSalesInquiryController.SalesInquiry().Master().SalesPerson().getCompanyName());
            tfReferralAgent.setText(poSalesInquiryController.SalesInquiry().Master().ReferralAgent().getCompanyName());
            taRemarks.setText(poSalesInquiryController.SalesInquiry().Master().getRemarks());
            
            cmbInquiryType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getSourceCode()));
            cmbPurchaseType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getPurchaseType()));
            cmbClientType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().Client().getClientType()));
            cmbCategoryType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getCategoryType()));

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
                    if (poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId() != null && !poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId().equals("")) {
                        tfBrand.requestFocus();
                    } else {
                        tfBrand.requestFocus();
                    }
                }
            }
        });

        tblViewTransDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetails); // need to use computed-size in min-width of the column to work
    }

    public void loadTableDetail() {
        pbEntered = false;
        // Setting data to table detail

        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewTransDetails.setPlaceholder(loadingPane);
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

                        String lsBrandId = "";
                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poSalesInquiryController.SalesInquiry().getDetailCount() - 1;
                            while (lnCtr >= 0) {
                                if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getModelId()== null || poSalesInquiryController.SalesInquiry().Detail(lnCtr).getModelId().equals("")) {
                                    if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getBrandId() != null
                                            || !"".equals(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getBrandId())) {
                                        lsBrandId = poSalesInquiryController.SalesInquiry().Detail(lnCtr).getBrandId();
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
                        }

                        double lnTotal = 0.0;
                        for (lnCtr = 0; lnCtr < poSalesInquiryController.SalesInquiry().getDetailCount(); lnCtr++) {
                            String lsBrand = "";
                            if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).Brand().getDescription() != null) {
                                lsBrand = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Brand().getDescription();
                            }
                            String lsModelVariant = " ";
                            if( poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId() != null && !"".equals( poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId())){
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
                                            lsDescription
                                    ));
                        }

                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                tblViewTransDetails.getSelectionModel().select(0);
                                tblViewTransDetails.getFocusModel().focus(0);
                                pnDetail = tblViewTransDetails.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            tblViewTransDetails.getSelectionModel().select(pnDetail);
                            tblViewTransDetails.getFocusModel().focus(pnDetail);
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
                    tblViewTransDetails.setPlaceholder(placeholderLabel);
                } else {
                    tblViewTransDetails.toFront();
                }
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewTransDetails.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
    }

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
                case "tfSearchClient":
                    if (lsValue.equals("")) {
                        psClientId = "";
                    }
                    break;

            }
            if (lsTxtFieldID.equals("tfSearchClient") || lsTxtFieldID.equals("tfSearchReferenceNo")) {
                loadRecordSearch();
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
    
    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            switch (event.getCode()) {
                case F3:
                    switch (lsID) {
                        case "tfSearchClient":
                            poJSON = poSalesInquiryController.SalesInquiry().SearchClient(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSearchClient.setText("");
                                psClientId = "";
                                break;
                            } else {
                                psClientId = poSalesInquiryController.SalesInquiry().Master().getClientId();
                            }
                            loadRecordSearch();
                            return;
                        case "tfSearchReferenceNo":
                            poJSON = poSalesInquiryController.SalesInquiry().searchTransaction(psIndustryId, psCompanyId, psCategoryId, tfSearchClient.getText(), tfSearchReferenceNo.getText());
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSearchReferenceNo.setText("");
                                break;
                            } else {
                                psClientId = poSalesInquiryController.SalesInquiry().Master().getClientId();
                                pnEditMode = poSalesInquiryController.SalesInquiry().getEditMode();
                                loadRecordMaster();
                                loadTableDetail();
                                initButton(pnEditMode);
                            }
                            loadRecordSearch();
                            return;

                    }
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
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initComboBoxes() {
        // Set the items of the ComboBox to the list of genders
        cmbClientType.setItems(ClientType);
        cmbClientType.getSelectionModel().select(0);
        cmbInquiryType.setItems(InquiryType);
        cmbInquiryType.getSelectionModel().select(0);
        cmbPurchaseType.setItems(PurchaseType);
        cmbPurchaseType.getSelectionModel().select(0);
        cmbCategoryType.setItems(CategoryType);
        cmbCategoryType.getSelectionModel().select(0);
        
        JFXUtil.initComboBoxCellDesignColor(cmbClientType, "#FF8201");
        JFXUtil.initComboBoxCellDesignColor(cmbInquiryType, "#FF8201");
        JFXUtil.initComboBoxCellDesignColor(cmbPurchaseType, "#FF8201");
        JFXUtil.initComboBoxCellDesignColor(cmbCategoryType, "#FF8201");
        
        cmbClientType.setOnAction(event -> {
            if (ClientType.size() > 0) {
                try {
                    int selectedIndex = cmbClientType.getSelectionModel().getSelectedIndex();
                    poSalesInquiryController.SalesInquiry().Master().setClientType(String.valueOf(selectedIndex));
                   cmbClientType.getSelectionModel().select(selectedIndex);
                } catch (Exception e) {
                }
            }
        });
        
        cmbInquiryType.setOnAction(event -> {
            if (PurchaseType.size() > 0) {
                try {
                    int selectedIndex = cmbInquiryType.getSelectionModel().getSelectedIndex();
                    poSalesInquiryController.SalesInquiry().Master().setSourceCode(String.valueOf(selectedIndex));
                   cmbInquiryType.getSelectionModel().select(selectedIndex);
                } catch (Exception e) {
                }
            }
        });
        
        
        cmbPurchaseType.setOnAction(event -> {
            if (InquiryType.size() > 0) {
                try {
                    int selectedIndex = cmbPurchaseType.getSelectionModel().getSelectedIndex();
                    poSalesInquiryController.SalesInquiry().Master().setPurchaseType(String.valueOf(selectedIndex));
                   cmbPurchaseType.getSelectionModel().select(selectedIndex);
                } catch (Exception e) {
                }
            }
        });
        
        
        cmbCategoryType.setOnAction(event -> {
            if (CategoryType.size() > 0) {
                try {
                    int selectedIndex = cmbCategoryType.getSelectionModel().getSelectedIndex();
                    poSalesInquiryController.SalesInquiry().Master().setCategoryType(String.valueOf(selectedIndex));
                   cmbCategoryType.getSelectionModel().select(selectedIndex);
                } catch (Exception e) {
                }
            }
        });
    }
    
    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate, dpTargetDate);
    }

    public void initTextFields() {
        Platform.runLater(() -> {
            JFXUtil.setVerticalScroll(taRemarks);
        });
        
        JFXUtil.setFocusListener(txtField_Focus, tfSearchClient, tfSearchReferenceNo);
        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);
    }

    private void initButton(int fnValue) {
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);

        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(lbShow2,  btnHistory);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);

        JFXUtil.setDisabled(true, taRemarks, apMaster, apDetail);

    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail);
        JFXUtil.setColumnLeft(tblBrandDetail, tblDescriptionDetail);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewTransDetails);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);
        SortedList<ModelSalesInquiry_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewTransDetails.comparatorProperty());
        tblViewTransDetails.setItems(sortedData);
        tblViewTransDetails.autosize();
    }
    
    public void loadRecordSearch() {
        try {
            lblSource.setText(poSalesInquiryController.SalesInquiry().Master().Company().getCompanyName() + " - " + poSalesInquiryController.SalesInquiry().Master().Industry().getDescription());
        
            if (psClientId.equals("")) {
                tfSearchClient.setText("");
            } else {
                tfSearchClient.setText(poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());
            }

            try {
                if (tfSearchReferenceNo.getText() == null || tfSearchReferenceNo.getText().equals("")) {
                    tfSearchReferenceNo.setText("");
                }
            } catch (Exception e) {
                tfSearchReferenceNo.setText("");
            }
        
            
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void clearTextFields() {
        JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField, dpTransactionDate);
        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse);
    }
}
