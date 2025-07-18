package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelAPPaymentAdjustment;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.TAB;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.UserRight;
import ph.com.guanzongroup.cas.cashflow.APPaymentAdjustment;
import ph.com.guanzongroup.cas.cashflow.status.APPaymentAdjustmentStatus;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;

public class APPaymentAdjustment_ConfirmationController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    static APPaymentAdjustment poAPPaymentAdjustmentController;
    private JSONObject poJSON;
    public int pnEditMode;

    private String pxeModuleName = "";
    private boolean isGeneral = false;
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psSupplierId = "";
    private String psSearchCompanyId = "";
    private String psSearchSupplierId = "";
    private String psTransactionNo = "";
    private static final int ROWS_PER_PAGE = 50;
    int pnMain = 0;
    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();
    private boolean pbEntered = false;
    private ObservableList<ModelAPPaymentAdjustment> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelAPPaymentAdjustment> filteredData;
    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster;
    @FXML
    private HBox hbButtons, hboxid;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private Button btnUpdate, btnSearch, btnSave, btnCancel, btnConfirm, btnVoid, btnReturn, btnHistory, btnRetrieve, btnClose;
    @FXML
    private TextField tfSearchSupplier, tfSearchReferenceNo, tfSearchCompany, tfTransactionNo, tfClient, tfIssuedTo, tfCreditAmount, tfDebitAmount, tfReferenceNo, tfCompany;
    @FXML
    private DatePicker dpTransactionDate;
    @FXML
    private TextArea taRemarks;
    @FXML
    private TableView tblViewMainList;
    @FXML
    private TableColumn tblRowNo, tblSupplier, tblDate, tblReferenceNo;
    @FXML
    private Pagination pgPagination;

    public void setTabTitle(String lsTabTitle, boolean isGeneral) {
        this.pxeModuleName = lsTabTitle;
        this.isGeneral = isGeneral;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        psIndustryId = isGeneral ? "" : psIndustryId;

        poJSON = new JSONObject();
        poAPPaymentAdjustmentController = new CashflowControllers(oApp, null).APPaymentAdjustment();
        poAPPaymentAdjustmentController.initialize(); // Initialize transaction
        initTextFields();
        initDatePickers();
        clearTextFields();
        initMainGrid();
        initTableOnClick();
        pgPagination.setPageCount(1);
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        Platform.runLater(() -> {
            poAPPaymentAdjustmentController.getModel().setIndustryId(psIndustryId);
            poAPPaymentAdjustmentController.getModel().setCompanyId(psSearchCompanyId);
            poAPPaymentAdjustmentController.setIndustryId(psIndustryId);
            poAPPaymentAdjustmentController.setCompanyId(psSearchCompanyId);
            loadRecordSearch();
        });
        JFXUtil.initKeyClickObject(apMainAnchor, lastFocusedTextField, previousSearchedTextField);
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
        //Company is not autoset
    }

    @Override
    public void setCategoryID(String fsValue) {
        //No Category
    }

    private void goToPageBasedOnSelectedRow(String pnRowMain) {
        int realIndex = Integer.parseInt(pnRowMain);
        if (realIndex == -1) {
            return; // Not found
        }

        int targetPage = realIndex / ROWS_PER_PAGE;
        int indexInPage = realIndex % ROWS_PER_PAGE;

        initMainGrid();
        int totalPage = (int) (Math.ceil(main_data.size() * 1.0 / ROWS_PER_PAGE));
        pgPagination.setPageCount(totalPage);
        pgPagination.setCurrentPageIndex(targetPage);
        JFXUtil.changeTableView(targetPage, ROWS_PER_PAGE, tblViewMainList, main_data.size(), filteredData);

    }

    public void loadTableDetailFromMain() {
        try {
            poJSON = new JSONObject();

            ModelAPPaymentAdjustment selected = (ModelAPPaymentAdjustment) tblViewMainList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                int pnRowMain = Integer.parseInt(selected.getIndex01()) - 1;
                pnMain = pnRowMain;
                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnRowMain + 1), "#A7C7E7", highlightedRowsMain);

                poJSON = poAPPaymentAdjustmentController.OpenTransaction(poAPPaymentAdjustmentController.APPaymentAdjustmentList(pnMain).getTransactionNo());
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    return;
                }
                goToPageBasedOnSelectedRow(String.valueOf(pnMain));
                loadRecordMaster();
            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void initTableOnClick() {
        tblViewMainList.setOnMouseClicked(event -> {
            pnMain = tblViewMainList.getSelectionModel().getSelectedIndex();
            if (pnMain >= 0) {
                if (event.getClickCount() == 2) {
                    loadTableDetailFromMain();
                    pnEditMode = poAPPaymentAdjustmentController.getEditMode();
                    initButton(pnEditMode);
                }
            }
        });
        JFXUtil.applyRowHighlighting(tblViewMainList, item -> ((ModelAPPaymentAdjustment) item).getIndex01(), highlightedRowsMain);
        JFXUtil.adjustColumnForScrollbar(tblViewMainList);
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = txtField.getId();
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            int lnRow = pnMain;

            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEntered = true;
                    CommonUtils.SetNextFocus(txtField);
                    event.consume();
                    break;
                case F3:
                    switch (lsID) {
                        case "tfSearchCompany":
                            poJSON = poAPPaymentAdjustmentController.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSearchCompany.setText("");
                                psSearchCompanyId = "";
                                break;
                            }
                            psSearchCompanyId = poAPPaymentAdjustmentController.getModel().getCompanyId();
                            loadRecordSearch();
                            retrieveAPAdjustment();
                            return;
                        case "tfSearchSupplier":
                            poJSON = poAPPaymentAdjustmentController.SearchClient(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSearchSupplier.setText("");
                                psSearchSupplierId = "";
                                break;
                            }
                            psSearchSupplierId = poAPPaymentAdjustmentController.getModel().getClientId();
                            loadRecordSearch();
                            retrieveAPAdjustment();
                            return;
                        case "tfSearchReferenceNo":
                            retrieveAPAdjustment();
                            return;
                        case "tfCompany":
                            poJSON = poAPPaymentAdjustmentController.SearchCompany(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfCompany.setText("");
                                psCompanyId = "";
                                break;
                            }
                            psCompanyId = poAPPaymentAdjustmentController.getModel().getCompanyId();
                            loadRecordMaster();
                            break;
                        case "tfClient":
                            poJSON = poAPPaymentAdjustmentController.SearchClient(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfClient.setText("");
                                psSupplierId = "";
                                break;
                            }
                            psSupplierId = poAPPaymentAdjustmentController.getModel().getClientId();
                            loadRecordMaster();
                            break;
                        case "tfIssuedTo":
                            poJSON = poAPPaymentAdjustmentController.SearchPayee(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfIssuedTo.setText("");
                                break;
                            }
                            loadRecordMaster();
                            break;

                    }
                    break;
            }
        } catch (ExceptionInInitializerError ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadRecordSearch() {
        try {
            poAPPaymentAdjustmentController.getModel().setIndustryId(psIndustryId);
            if(poAPPaymentAdjustmentController.getModel().Industry().getDescription() != null && !"".equals(poAPPaymentAdjustmentController.getModel().Industry().getDescription())){
                lblSource.setText(poAPPaymentAdjustmentController.getModel().Industry().getDescription());
            } else {
                lblSource.setText("General");
            }
            tfSearchSupplier.setText(psSearchSupplierId.equals("") ? "" : poAPPaymentAdjustmentController.getModel().Supplier().getCompanyName());
            tfSearchCompany.setText(psSearchCompanyId.equals("") ? "" : poAPPaymentAdjustmentController.getModel().Company().getCompanyName());
            JFXUtil.updateCaretPositions(apBrowse);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }
    final ChangeListener<? super Boolean> txtMaster_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = txtPersonalInfo.getId();
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());

        if (lsValue == null) {
            return;
        }

        if (!nv) {
            /* Lost Focus */
            switch (lsTxtFieldID) {
                case "tfSearchSupplier":
                    if (lsValue.equals("")) {
                        psSearchSupplierId = "";
                    }
                    loadRecordSearch();
                    break;
                case "tfSearchCompany":
                    if (lsValue.equals("")) {
                        psSearchCompanyId = "";
                    }
                    loadRecordSearch();
                    break;
                case "tfSearchReferenceNo":
                    break;
                case "tfCompany":
                    if (lsValue.isEmpty()) {
                        poJSON = poAPPaymentAdjustmentController.getModel().setCompanyId("");
                        psCompanyId = "";
                    }
                    break;
                case "tfClient":
                    if (lsValue.isEmpty()) {
                        poJSON = poAPPaymentAdjustmentController.getModel().setClientId("");
                        psSupplierId = "";
                    }
                    break;
                case "tfIssuedTo":
                    if (lsValue.isEmpty()) {
                        poJSON = poAPPaymentAdjustmentController.getModel().setIssuedTo("");
                    }
                    break;
                case "tfReferenceNo":
                    if (!lsValue.isEmpty()) {
                        poJSON = poAPPaymentAdjustmentController.getModel().setReferenceNo(lsValue);
                    } else {
                        poJSON = poAPPaymentAdjustmentController.getModel().setReferenceNo("");
                    }
                    if ("error".equals(poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        tfReferenceNo.setText("");
                        break;
                    }
                    break;
                case "tfCreditAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (poAPPaymentAdjustmentController.getModel().getCreditAmount() != null
                            && !"".equals(poAPPaymentAdjustmentController.getModel().getCreditAmount())) {
                        if (Double.valueOf(lsValue) < 0.00) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Credit amount cannot be lesser than 0.0000");
                            poAPPaymentAdjustmentController.getModel().setCreditAmount(0.0000);
                            tfCreditAmount.setText("0.0000");
                            tfCreditAmount.requestFocus();
                            break;
                        }

                        if (Double.valueOf(lsValue) > 0.00) {
                            if (poAPPaymentAdjustmentController.getModel().getDebitAmount().doubleValue() > 0.0000) {
                                ShowMessageFX.Warning(null, pxeModuleName, "Debit and credit amounts cannot both have values at the same time.");
                                poAPPaymentAdjustmentController.getModel().setCreditAmount(0.0000);
                                tfCreditAmount.setText("0.0000");
                                tfCreditAmount.requestFocus();
                                break;
                            }
                        }
                    }

                    poJSON = poAPPaymentAdjustmentController.getModel().setCreditAmount((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;
                case "tfDebitAmount":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    lsValue = JFXUtil.removeComma(lsValue);
                    if (poAPPaymentAdjustmentController.getModel().getDebitAmount() != null
                            && !"".equals(poAPPaymentAdjustmentController.getModel().getDebitAmount())) {
                        if (Double.valueOf(lsValue) < 0.00) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Debit amount cannot be lesser than 0.0000");
                            poAPPaymentAdjustmentController.getModel().setDebitAmount(0.0000);
                            tfDebitAmount.setText("0.0000");
                            tfDebitAmount.requestFocus();
                            break;
                        }

                        if (Double.valueOf(lsValue) > 0.00) {
                            if (poAPPaymentAdjustmentController.getModel().getCreditAmount().doubleValue() > 0.0000) {
                                ShowMessageFX.Warning(null, pxeModuleName, "Debit and credit amounts cannot both have values at the same time.");
                                poAPPaymentAdjustmentController.getModel().setDebitAmount(0.0000);
                                tfDebitAmount.setText("0.0000");
                                tfDebitAmount.requestFocus();
                                break;
                            }
                        }
                    }

                    poJSON = poAPPaymentAdjustmentController.getModel().setDebitAmount((Double.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    }
                    break;

            }
            if (!JFXUtil.isObjectEqualTo(lsTxtFieldID, "tfSearchSupplier", "tfSearchCompany", "tfSearchReferenceNo")) {
                loadRecordMaster();
            }

        }
    };
    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = txtField.getId();
        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }

        poJSON = new JSONObject();

        if (!nv) {
            /* Lost Focus */
            lsValue = lsValue.trim();
            switch (lsID) {
                case "taRemarks":  // Remarks
                    poJSON = poAPPaymentAdjustmentController.getModel().setRemarks(lsValue);
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
                    break;
            }
//            loadRecordMaster();
        }
    };

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
                LocalDate selectedDate = null;
                String lsServerDate = "";
                String lsTransDate = "";
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
//                selectedDate = ldtResult.selectedDate;
                switch (datePicker.getId()) {
                    case "dpTransactionDate":
                        if (poAPPaymentAdjustmentController.getEditMode() == EditMode.ADDNEW
                                || poAPPaymentAdjustmentController.getEditMode() == EditMode.UPDATE) {
                            lsServerDate = sdfFormat.format(oApp.getServerDate());
                            lsTransDate = sdfFormat.format(poAPPaymentAdjustmentController.getModel().getTransactionDate());
                            lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                            currentDate = LocalDate.parse(lsServerDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));
                            selectedDate = LocalDate.parse(lsSelectedDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                            if (selectedDate.isAfter(currentDate)) {
                                poJSON.put("result", "error");
                                poJSON.put("message", "Future dates are not allowed.");
                                pbSuccess = false;
                            }

                            if (pbSuccess && ((poAPPaymentAdjustmentController.getEditMode() == EditMode.UPDATE && !lsTransDate.equals(lsSelectedDate))
                                    || !lsServerDate.equals(lsSelectedDate))) {
                                if (oApp.getUserLevel() <= UserRight.ENCODER) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Change in Transaction Date Detected\n\n"
                                            + "If YES, please seek approval to proceed with the new selected date.\n"
                                            + "If NO, the previous transaction date will be retained.") == true) {
                                        poJSON = ShowDialogFX.getUserApproval(oApp);
                                        if (!"success".equals((String) poJSON.get("result"))) {
                                            pbSuccess = false;
                                        } else {
                                            if(Integer.parseInt(poJSON.get("nUserLevl").toString())<= UserRight.ENCODER){
                                                poJSON.put("result", "error");
                                                poJSON.put("message", "User is not an authorized approving officer.");
                                                pbSuccess = false;
                                            }
                                        }
                                    } else {
                                        pbSuccess = false;
                                    }
                                }
                            }

                            if (pbSuccess) {
                                poAPPaymentAdjustmentController.getModel().setTransactionDate((SQLUtil.toDate(lsSelectedDate, SQLUtil.FORMAT_SHORT_DATE)));
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initTextFields() {
        Platform.runLater(() -> {
            JFXUtil.setVerticalScroll(taRemarks);
        });
        JFXUtil.setCommaFormatter(tfDebitAmount, tfCreditAmount);
        JFXUtil.setFocusListener(txtMaster_Focus, tfReferenceNo, tfCompany, tfClient, tfIssuedTo, tfCreditAmount, tfDebitAmount,
                tfSearchCompany, tfSearchSupplier);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apMaster, apBrowse);
    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate);
    }

    public void clearTextFields() {
        psSearchCompanyId = "";
        psCompanyId = "";
        psSearchSupplierId = "";
        psSupplierId = "";

        JFXUtil.setValueToNull(previousSearchedTextField, lastFocusedTextField, dpTransactionDate);
        JFXUtil.clearTextFields(apMaster);
    }

    public void initMainGrid() {
        JFXUtil.setColumnCenter(tblRowNo, tblDate, tblReferenceNo);
        JFXUtil.setColumnLeft(tblSupplier);
        JFXUtil.setColumnsIndexAndDisableReordering(tblViewMainList);

        filteredData = new FilteredList<>(main_data, b -> true);
        tblViewMainList.setItems(filteredData);
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

                    if (poAPPaymentAdjustmentController.getAPPaymentAdjustmentCount() > 0) {
                        //retreiving using column index
                        for (int lnCtr = 0; lnCtr <= poAPPaymentAdjustmentController.getAPPaymentAdjustmentCount() - 1; lnCtr++) {
                            try {
                                main_data.add(new ModelAPPaymentAdjustment(String.valueOf(lnCtr + 1),
                                        String.valueOf(poAPPaymentAdjustmentController.APPaymentAdjustmentList(lnCtr).Supplier().getCompanyName()),
                                        String.valueOf(poAPPaymentAdjustmentController.APPaymentAdjustmentList(lnCtr).getTransactionDate()),
                                        String.valueOf(poAPPaymentAdjustmentController.APPaymentAdjustmentList(lnCtr).getTransactionNo())
                                ));
                            } catch (SQLException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            } catch (GuanzonException ex) {
                                Logger.getLogger(getClass().getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                            }

                            if (poAPPaymentAdjustmentController.APPaymentAdjustmentList(lnCtr).getTransactionStatus().equals(APPaymentAdjustmentStatus.CONFIRMED)) {
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

    public void loadRecordMaster() {
        try {
            tfTransactionNo.setText(poAPPaymentAdjustmentController.getModel().getTransactionNo());
            Platform.runLater(() -> {
                String lsActive = pnEditMode == EditMode.UNKNOWN ? "-1" : poAPPaymentAdjustmentController.getModel().getTransactionStatus();
                Map<String, String> statusMap = new HashMap<>();
                statusMap.put(APPaymentAdjustmentStatus.OPEN, "OPEN");
                statusMap.put(APPaymentAdjustmentStatus.PAID, "PAID");
                statusMap.put(APPaymentAdjustmentStatus.CONFIRMED, "CONFIRMED");
                statusMap.put(APPaymentAdjustmentStatus.RETURNED, "RETURNED");
                statusMap.put(APPaymentAdjustmentStatus.VOID, "VOIDED");
                statusMap.put(APPaymentAdjustmentStatus.CANCELLED, "CANCELLED");

                String lsStat = statusMap.getOrDefault(lsActive, "UNKNOWN");
                lblStatus.setText(lsStat);
            });
            // Transaction Date
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poAPPaymentAdjustmentController.getModel().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));

            tfClient.setText(poAPPaymentAdjustmentController.getModel().Supplier().getCompanyName());
            taRemarks.setText(poAPPaymentAdjustmentController.getModel().getRemarks());
            tfIssuedTo.setText(poAPPaymentAdjustmentController.getModel().Payee().getPayeeName());
            tfCreditAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poAPPaymentAdjustmentController.getModel().getCreditAmount().doubleValue(), true));
            tfDebitAmount.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poAPPaymentAdjustmentController.getModel().getDebitAmount().doubleValue(), true));
            tfReferenceNo.setText(poAPPaymentAdjustmentController.getModel().getReferenceNo());
            tfCompany.setText(poAPPaymentAdjustmentController.getModel().Company().getCompanyName());

            poAPPaymentAdjustmentController.computeFields();
            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
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
                    case "btnUpdate":
                        poJSON = poAPPaymentAdjustmentController.OpenTransaction(poAPPaymentAdjustmentController.getModel().getTransactionNo());
                        poJSON = poAPPaymentAdjustmentController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }

                        psSupplierId = poAPPaymentAdjustmentController.getModel().getClientId();
                        psCompanyId = poAPPaymentAdjustmentController.getModel().getCompanyId();
                        pnEditMode = poAPPaymentAdjustmentController.getEditMode();
                        break;
                    case "btnSearch":
                        String lsMessage = "Focus a searchable textfield to search";
                        if ((lastFocusedTextField.get() != null)) {
                            if (lastFocusedTextField.get() instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField.get();
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apMaster).contains(tf.getId())) {
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
                            JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                            //Clear data
                            poAPPaymentAdjustmentController.resetMaster();
                            clearTextFields();
                            poAPPaymentAdjustmentController.getModel().setIndustryId(psIndustryId);
                            pnEditMode = EditMode.UNKNOWN;
                            break;
                        } else {
                            return;
                        }
                    case "btnHistory":
                        break;
                    case "btnRetrieve":
                        retrieveAPAdjustment();
                        break;
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poAPPaymentAdjustmentController.getModel().setClientId(psSupplierId);
                            poAPPaymentAdjustmentController.getModel().setCompanyId(psCompanyId);
                            poJSON = poAPPaymentAdjustmentController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                //reshow the highlight
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON = poAPPaymentAdjustmentController.OpenTransaction(poAPPaymentAdjustmentController.getModel().getTransactionNo());
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poAPPaymentAdjustmentController.getModel().getTransactionStatus().equals(APPaymentAdjustmentStatus.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poAPPaymentAdjustmentController.ConfirmTransaction("Confirmed");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                                pnEditMode = poAPPaymentAdjustmentController.getEditMode();
                            }
                        } else {
                            return;
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
                    case "btnConfirm":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Are you sure you want to confirm transaction?") == true) {
                            poJSON = poAPPaymentAdjustmentController.ConfirmTransaction("");
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
                    case "btnVoid":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to void transaction?") == true) {
                            if (poAPPaymentAdjustmentController.getModel().getTransactionStatus().equals(APPaymentAdjustmentStatus.OPEN)) {
                                poJSON = poAPPaymentAdjustmentController.VoidTransaction("Void");
                            } else {
                                poJSON = poAPPaymentAdjustmentController.CancelTransaction("Cancel");
                            }
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#FAA0A0", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;
                    case "btnReturn":
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to return transaction?") == true) {
                            poJSON = poAPPaymentAdjustmentController.ReturnTransaction("");
                            if ("error".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));
                                JFXUtil.disableAllHighlightByColor(tblViewMainList, "#A7C7E7", highlightedRowsMain);
                                JFXUtil.highlightByKey(tblViewMainList, String.valueOf(pnMain + 1), "#FAC898", highlightedRowsMain);
                            }
                        } else {
                            return;
                        }
                        break;

                    default:
                        break;
                }

                if (JFXUtil.isObjectEqualTo(lsButton, "btnSave", "btnConfirm", "btnReturn", "btnVoid", "btnCancel")) {
                    poAPPaymentAdjustmentController.resetMaster();
                    pnEditMode = EditMode.UNKNOWN;
                    clearTextFields();
                }

                if (lsButton.equals("btnRetrieve")) {
                } else {
                    loadRecordMaster();
                }
                initButton(pnEditMode);
            }
        } catch (ParseException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrieveAPAdjustment() {
        poJSON = new JSONObject();
        poAPPaymentAdjustmentController.setRecordStatus(APPaymentAdjustmentStatus.OPEN + "" + APPaymentAdjustmentStatus.CONFIRMED);
        poJSON = poAPPaymentAdjustmentController.loadAPPaymentAdjustment(tfSearchCompany.getText(), tfSearchSupplier.getText(), tfSearchReferenceNo.getText());
        if (!"success".equals((String) poJSON.get("result"))) {
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        } else {
            loadTableMain();
        }
    }

    private void initButton(int fnValue) {

        boolean lbShow1 = (fnValue == EditMode.UPDATE);
//        boolean lbShow2 = (fnValue == EditMode.READY || fnValue == EditMode.UPDATE);
        boolean lbShow3 = (fnValue == EditMode.READY);
        boolean lbShow4 = (fnValue == EditMode.UNKNOWN || fnValue == EditMode.READY);
        // Manage visibility and managed state of other buttons
        //Update 
        JFXUtil.setButtonsVisibility(lbShow1, btnSearch, btnSave, btnCancel);

        //Ready
        JFXUtil.setButtonsVisibility(lbShow3, btnUpdate, btnHistory, btnConfirm, btnVoid);

        //Unkown || Ready
        JFXUtil.setDisabled(!lbShow1, apMaster);
        JFXUtil.setButtonsVisibility(lbShow4, btnClose);
        JFXUtil.setButtonsVisibility(false, btnReturn);

        switch (poAPPaymentAdjustmentController.getModel().getTransactionStatus()) {
            case APPaymentAdjustmentStatus.CONFIRMED:
                JFXUtil.setButtonsVisibility(false, btnConfirm);
                if (poAPPaymentAdjustmentController.getModel().isProcessed()) {
                    JFXUtil.setButtonsVisibility(false, btnUpdate, btnVoid);
                } else {
//                    JFXUtil.setButtonsVisibility(lbShow3, btnReturn);
                }
                break;
            case APPaymentAdjustmentStatus.PAID:
            case APPaymentAdjustmentStatus.RETURNED:
                JFXUtil.setButtonsVisibility(false, btnConfirm, btnUpdate, btnReturn, btnVoid);
                break;
            case APPaymentAdjustmentStatus.VOID:
            case APPaymentAdjustmentStatus.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnConfirm, btnUpdate, btnReturn, btnVoid);
                break;
        }
    }

}
