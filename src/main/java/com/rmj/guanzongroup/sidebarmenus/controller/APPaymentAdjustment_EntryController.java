package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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

public class APPaymentAdjustment_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    static APPaymentAdjustment poAPPaymentAdjustmentController;
    private JSONObject poJSON;
    public int pnEditMode;
    private final String pxeModuleName = JFXUtil.getFormattedClassTitle(this.getClass());
    private String psIndustryId = "";
    private String psCompanyId = "";
    private boolean pbEntered = false;
    AtomicReference<Object> lastFocusedTextField = new AtomicReference<>();
    AtomicReference<Object> previousSearchedTextField = new AtomicReference<>();

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster;
    @FXML
    private HBox hbButtons, hboxid;
    @FXML
    private Label lblSource, lblStatus;
    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnHistory, btnClose;
    @FXML
    private TextField tfTransactionNo, tfClient, tfIssuedTo, tfCreditAmount, tfDebitAmount, tfReferenceNo, tfCompany;
    @FXML
    private DatePicker dpTransactionDate;
    @FXML
    private TextArea taRemarks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        poJSON = new JSONObject();
        poAPPaymentAdjustmentController = new CashflowControllers(oApp, null).APPaymentAdjustment();
        poAPPaymentAdjustmentController.initialize(); // Initialize transaction
        poAPPaymentAdjustmentController.initFields();
        initTextFields();
        initDatePickers();
        clearTextFields();
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);

        Platform.runLater(() -> {
            poAPPaymentAdjustmentController.getModel().setIndustryId(psIndustryId);
            poAPPaymentAdjustmentController.getModel().setCompanyId(psCompanyId);
            poAPPaymentAdjustmentController.setIndustryId(psIndustryId);
            poAPPaymentAdjustmentController.setCompanyId(psCompanyId);
            loadRecordSearch();
            btnNew.fire();
        });

        JFXUtil.initKeyClickObject(apMainAnchor, lastFocusedTextField, previousSearchedTextField);
    }

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = txtField.getId();
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();

            switch (event.getCode()) {
                case TAB:
                case ENTER:
                    pbEntered = true;
                    CommonUtils.SetNextFocus(txtField);
                    event.consume();
                    break;
                case F3:
                    switch (lsID) {
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
                                break;
                            }
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
                case "tfCompany":
                    if (lsValue.isEmpty()) {
                        poJSON = poAPPaymentAdjustmentController.getModel().setCompanyId("");
                    }
                    break;
                case "tfClient":
                    if (lsValue.isEmpty()) {
                        poJSON = poAPPaymentAdjustmentController.getModel().setClientId("");
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
            loadRecordMaster();
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
                case "taRemarks": { // Remarks
                    poJSON = poAPPaymentAdjustmentController.getModel().setRemarks(lsValue);
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }
//                    loadRecordMaster();
                    break;
                }
            }
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
                selectedDate = ldtResult.selectedDate;

                lsServerDate = sdfFormat.format(oApp.getServerDate());
//                lsTransDate = sdfFormat.format(poAPPaymentAdjustmentController.Master().getTransactionDate());
                lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                currentDate = LocalDate.parse(lsServerDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

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
                                if (oApp.getUserLevel() == UserRight.ENCODER) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Change in Transaction Date Detected\n\n"
                                            + "If YES, please seek approval to proceed with the new selected date.\n"
                                            + "If NO, the previous transaction date will be retained.") == true) {
                                        poJSON = ShowDialogFX.getUserApproval(oApp);
                                        if (!"success".equals((String) poJSON.get("result"))) {
                                            pbSuccess = false;
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
//                    case "dpTransactionDate": {
//                        poJSON.put("TransactionDate", lsSelectedDate);
//                        System.out.println("Transaction Date updated with value: " + lsSelectedDate);
//                        break;
//                    }
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
        JFXUtil.setFocusListener(txtMaster_Focus, tfReferenceNo, tfCompany, tfClient, tfIssuedTo, tfCreditAmount, tfDebitAmount);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apMaster);
    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate);
    }

    public void clearTextFields() {
        JFXUtil.setValueToNull(lastFocusedTextField, previousSearchedTextField, dpTransactionDate);
        JFXUtil.clearTextFields(apMaster);
    }

    public void loadRecordSearch() {
        try {
            lblSource.setText(poAPPaymentAdjustmentController.getModel().Industry().getDescription());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryCarController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
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
        } catch (SQLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
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
//                        poAPPaymentAdjustmentController.getModel().setTransactionStatus(APPaymentAdjustmentStatus.RETURNED + "" + APPaymentAdjustmentStatus.OPEN);
                        poAPPaymentAdjustmentController.setRecordStatus(APPaymentAdjustmentStatus.OPEN);
                        poJSON = poAPPaymentAdjustmentController.searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        pnEditMode = poAPPaymentAdjustmentController.getEditMode();
                        break;
                    case "btnNew":
                        //Clear data
                        poAPPaymentAdjustmentController.resetMaster();
                        clearTextFields();

                        poJSON = poAPPaymentAdjustmentController.NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        poAPPaymentAdjustmentController.initFields();
                        pnEditMode = poAPPaymentAdjustmentController.getEditMode();
                        break;
                    case "btnUpdate":
                        poJSON = poAPPaymentAdjustmentController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
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
                    case "btnSave":
                        //Validator
                        poJSON = new JSONObject();
                        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure you want to save the transaction?") == true) {
                            poJSON = poAPPaymentAdjustmentController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                return;
                            } else {
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
                                btnNew.fire();
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
                    default:
                        break;
                }

                loadRecordMaster();
                initButton(pnEditMode);
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (GuanzonException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            } catch (ParseException ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);

        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(!lbShow, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSearch, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(lbShow2, btnUpdate, btnHistory);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);

//        apMaster.setDisable(!lbShow);
        JFXUtil.setDisabled(!lbShow, taRemarks);
        JFXUtil.setDisabled(lbShow3, apMaster);

        switch (poAPPaymentAdjustmentController.getModel().getTransactionStatus()) {
            case APPaymentAdjustmentStatus.PAID:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
            case APPaymentAdjustmentStatus.VOID:
            case APPaymentAdjustmentStatus.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
        }
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
        //No category
    }

}
