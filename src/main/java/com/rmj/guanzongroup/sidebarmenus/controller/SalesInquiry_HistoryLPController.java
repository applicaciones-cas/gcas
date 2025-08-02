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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.base.GRiderCAS;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;
import ph.com.guanzongroup.cas.sales.t1.status.SalesInquiryStatic;

/**
 *
 * @author Arsiela
 */
public class SalesInquiry_HistoryLPController implements Initializable, ScreenInterface {

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
    private String psSearchClientId = "";
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
    private TextField tfSearchClient, tfSearchReferenceNo, tfTransactionNo, tfBranch, tfSalesPerson, tfInquirySource, tfClient, tfAddress, tfInquiryStatus, tfContactNo, tfBrand, tfModel, tfColor, tfBarcode, tfDescription;
    @FXML
    private TextArea taRemarks;
    @FXML
    private ComboBox cmbClientType, cmbInquiryType, cmbPurchaseType;
    ObservableList<String> ClientType = ModelSalesInquiry_Detail.ClientType;
    ObservableList<String> InquiryType = ModelSalesInquiry_Detail.InquiryType;
    ObservableList<String> PurchaseType = ModelSalesInquiry_Detail.PurchaseType;
    ObservableList<String> CategoryType = ModelSalesInquiry_Detail.CategoryType;
    @FXML
    private DatePicker dpTransactionDate, dpTargetDate;
    @FXML
    private TableView tblViewTransDetails;
    @FXML
    private TableColumn tblRowNoDetail, tblBarcodeDetail, tblDescriptionDetail;

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
                String lsActive = pnEditMode == EditMode.UNKNOWN ? "-1" : poSalesInquiryController.SalesInquiry().Master().getTransactionStatus();
                Map<String, String> statusMap = new HashMap<>();
                statusMap.put(SalesInquiryStatic.QUOTED, "QUOTED");
                statusMap.put(SalesInquiryStatic.SALE, "SALE");
                statusMap.put(SalesInquiryStatic.CONFIRMED, "CONFIRMED");
                statusMap.put(SalesInquiryStatic.OPEN, "OPEN");
                statusMap.put(SalesInquiryStatic.VOID, "VOIDED");
                statusMap.put(SalesInquiryStatic.CANCELLED, "CANCELLED");
                statusMap.put(SalesInquiryStatic.LOST, "LOST");
                String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN"); //default
                lblStatus.setText(lsStat);

                switch (poSalesInquiryController.SalesInquiry().Master().getInquiryStatus()) {
                    case "0":
                        tfInquiryStatus.setText("OPEN");
                        break;
                    default:
                        tfInquiryStatus.setText("");
                        break;
                }
            });

            // Transaction Date
            tfTransactionNo.setText(poSalesInquiryController.SalesInquiry().Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poSalesInquiryController.SalesInquiry().Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));
            String lsTargetDate = CustomCommonUtil.formatDateToShortString(poSalesInquiryController.SalesInquiry().Master().getTargetDate());
            dpTargetDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTargetDate, "yyyy-MM-dd"));

            tfBranch.setText(poSalesInquiryController.SalesInquiry().Master().Branch().getBranchName());
            tfSalesPerson.setText(poSalesInquiryController.SalesInquiry().Master().SalesPerson().getCompanyName());
            tfInquirySource.setText(poSalesInquiryController.SalesInquiry().Master().Source().getCompanyName());

            tfClient.setText(poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());
            tfAddress.setText(poSalesInquiryController.SalesInquiry().Master().ClientAddress().getAddress());
            tfContactNo.setText(poSalesInquiryController.SalesInquiry().Master().ClientMobile().getMobileNo());

            taRemarks.setText(poSalesInquiryController.SalesInquiry().Master().getRemarks());

            if (pnEditMode != EditMode.UNKNOWN) {
                cmbInquiryType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getSourceCode()));
                cmbPurchaseType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getPurchaseType()));
                if (poSalesInquiryController.SalesInquiry().Master().getClientId() != null && !"".equals(poSalesInquiryController.SalesInquiry().Master().getClientId())) {
                    cmbClientType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().Client().getClientType()));
                } else {
                    cmbClientType.getSelectionModel().select(Integer.parseInt(poSalesInquiryController.SalesInquiry().Master().getClientType()));
                }
            } else {
                cmbInquiryType.getSelectionModel().select(0);
                cmbPurchaseType.getSelectionModel().select(0);
                cmbClientType.getSelectionModel().select(0);
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
            tfBarcode.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Inventory().getBarCode());
            tfDescription.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Inventory().getDescription());

            tfBrand.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Brand().getDescription());
            tfModel.setText(poSalesInquiryController.SalesInquiry().Detail(pnDetail).Model().getDescription());
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
                if (JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId(), null, "")) {
                    tfBarcode.requestFocus();
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
                    if (JFXUtil.isObjectEqualTo(poSalesInquiryController.SalesInquiry().Detail(pnDetail).getStockId(), null, "")) {
                        tfBarcode.requestFocus();
                    }
                }
            }
        });

        tblViewTransDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewTransDetails); // need to use computed-size in min-width of the column to work
    }

    public void loadTableDetail() {
        pbEntered = false;

        JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
        tblViewTransDetails.setPlaceholder(loading.loadingPane);
        loading.progressIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    int lnCtr;
                    details_data.clear();
                    try {

                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            poSalesInquiryController.SalesInquiry().loadDetail();
                        }
                        poSalesInquiryController.SalesInquiry().sortPriority();
                        String lsBarcode = "";
                        String lsDescription = "";
                        for (lnCtr = 0; lnCtr < poSalesInquiryController.SalesInquiry().getDetailCount(); lnCtr++) {
                            if (poSalesInquiryController.SalesInquiry().Detail(lnCtr).getStockId() != null) {
                                lsBarcode = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Inventory().getBarCode();
                                lsDescription = poSalesInquiryController.SalesInquiry().Detail(lnCtr).Inventory().getDescription();
                            }
                            details_data.add(
                                    new ModelSalesInquiry_Detail(
                                            String.valueOf(poSalesInquiryController.SalesInquiry().Detail(lnCtr).getPriority()),
                                            String.valueOf(lsBarcode),
                                            lsDescription
                                    ));
                            lsBarcode = "";
                            lsDescription = "";
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
                            JFXUtil.selectAndFocusRow(tblViewTransDetails, 0);
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
                        psSearchClientId = "";
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
                                psSearchClientId = "";
                                break;
                            }
                            psSearchClientId = poSalesInquiryController.SalesInquiry().Master().getClientId();
                            loadRecordSearch();
                            return;
                        case "tfSearchReferenceNo":
                            poJSON = poSalesInquiryController.SalesInquiry().searchTransaction(psIndustryId, psCompanyId, psCategoryId, tfSearchClient.getText(), tfSearchReferenceNo.getText());
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSearchReferenceNo.setText("");
                                break;
                            } else {
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
        JFXUtil.setComboBoxItems(new JFXUtil.Pairs<>(ClientType, cmbClientType), new JFXUtil.Pairs<>(InquiryType, cmbInquiryType),
                new JFXUtil.Pairs<>(PurchaseType, cmbPurchaseType)
        );
        JFXUtil.initComboBoxCellDesignColor("#FF8201", cmbClientType, cmbInquiryType, cmbPurchaseType);
    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat("MM/dd/yyyy",dpTransactionDate, dpTargetDate);
    }

    public void initTextFields() {
        JFXUtil.setFocusListener(txtField_Focus, tfSearchClient, tfSearchReferenceNo);
        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);
    }

    private void initButton(int fnValue) {
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);

        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(lbShow2, btnHistory);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);

        JFXUtil.setDisabled(true, taRemarks, apMaster, apDetail);
    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail);
        JFXUtil.setColumnLeft(tblBarcodeDetail, tblDescriptionDetail);
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
            tfSearchClient.setText(psSearchClientId.equals("") ? "" : poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());

            tfSearchReferenceNo.setText("");
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void clearTextFields() {
        psSearchClientId = "";
        JFXUtil.clearTextFields(apMaster, apDetail, apBrowse);
    }
}
