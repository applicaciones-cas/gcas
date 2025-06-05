/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.SQLUtil;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class APPaymentAdjustment_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    int pnJEDetail = 0;
    int pnDetail = 0;
    int pnMain = 0;
    private final String pxeModuleName = "AP Payment Adjustment Entry";
    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private Object lastFocusedTextField = null;
    private Object previousSearchedTextField = null;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initTextFields();
        initDatePickers();
        clearTextFields();
    }
    final ChangeListener<? super Boolean> txtMaster_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = txtPersonalInfo.getId();
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());
        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;

        if (lsValue == null) {
            return;
        }

        if (!nv) {
            /* Lost Focus */
            switch (lsTxtFieldID) {
                case "tfCost": {
                    // Process the cost field value
                    poJSON.put("Cost", lsValue);
                    System.out.println("Field tfCost updated with value: " + lsValue);
                    break;
                }
            }
        }
    };

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = txtField.getId();
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            int lnRow = pnDetail;

            switch (event.getCode()) {
                case F3:
                    switch (lsID) {
                        case "tfSearchSupplier": {
                            // Perform supplier search logic here
                            System.out.println("Searching supplier with value: " + lsValue);
                            poJSON.put("SearchSupplier", lsValue);
                            break;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea txtField = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = txtField.getId();
        String lsValue = txtField.getText();

        lastFocusedTextField = txtField;
        previousSearchedTextField = null;

        if (lsValue == null) {
            return;
        }

        poJSON = new JSONObject();

        if (!nv) {
            /* Lost Focus */
            lsValue = lsValue.trim();
            switch (lsID) {
                case "taRemarks": { // Remarks
                    poJSON.put("Remarks", lsValue);
                    System.out.println("Field taRemarks updated with value: " + lsValue);
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
                lastFocusedTextField = datePicker;
                previousSearchedTextField = null;

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
//                lsTransDate = sdfFormat.format(poPurchaseReceivingController.Master().getTransactionDate());
                lsSelectedDate = sdfFormat.format(SQLUtil.toDate(inputText, SQLUtil.FORMAT_SHORT_DATE));
                currentDate = LocalDate.parse(lsServerDate, DateTimeFormatter.ofPattern(SQLUtil.FORMAT_SHORT_DATE));

                switch (datePicker.getId()) {
                    case "dpTransactionDate": {
                        poJSON.put("TransactionDate", lsSelectedDate);
                        System.out.println("Transaction Date updated with value: " + lsSelectedDate);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initTextFields() {
        JFXUtil.setCommaFormatter(tfDebitAmount, tfCreditAmount);
        JFXUtil.setFocusListener(txtMaster_Focus, tfCreditAmount, tfDebitAmount);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apMaster);
    }

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate);
        JFXUtil.setActionListener(this::datepicker_Action, dpTransactionDate);
    }

    public void clearTextFields() {
        dpTransactionDate.setValue(null);
        JFXUtil.clearTextFields(apMaster);
    }

    public void loadRecordMaster() {
        tfTransactionNo.setText("");
        dpTransactionDate.setValue(null);
        tfClient.setText("");
        taRemarks.setText("");
        tfIssuedTo.setText("");
        tfCreditAmount.setText("");
        tfDebitAmount.setText("");
        tfReferenceNo.setText("");
        tfCompany.setText("");
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
                        break;
                    case "btnNew":
                        break;
                    case "btnUpdate":
                        break;
                    case "btnSearch":
                        String lsMessage = "Focus a searchable textfield to search";
                        if ((lastFocusedTextField != null)) {
                            if (lastFocusedTextField instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField;
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apMaster).contains(tf.getId())) {
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
                    case "btnSave":
                        break;
                    case "btnCancel":
                        break;
                    case "btnHistory":
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
            }
        } catch (Exception e) {

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
        psCompanyId = fsValue;
    }

    @Override
    public void setCategoryID(String fsValue) {
        psCategoryId = fsValue;
    }

}
