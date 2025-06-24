/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.TAB;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import ph.com.guanzongroup.cas.cashflow.CheckPrinting;
import ph.com.guanzongroup.cas.cashflow.Disbursement;
import ph.com.guanzongroup.cas.cashflow.status.DisbursementStatic;

/**
 * FXML Controller class
 *
 * @author User
 */
public class CheckAssignmentController implements Initializable {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private final String pxeModuleName = "Check Assignment";
    private CheckPrinting poCheckPrintingController;

    public int pnEditMode;
    private String psTransactionNo = "";

    @FXML
    private AnchorPane AnchorMain, apMaster;
    @FXML
    private StackPane StackPane;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnAssign, btnPrintCheck, btnClose;
    @FXML
    private AnchorPane AnchorInputs;
    @FXML
    private TextField tfDVNo, tfCheckNo;
    @FXML
    private DatePicker dpCheckDate;
    @FXML
    private TextArea taRemarks;
    @FXML
    private CheckBox chbkApplyToAll;

    public void setTransaction(String fsValue) {
        psTransactionNo = fsValue;
    }

    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    public void setCheckPrinting(CheckPrinting foValue) {
        poCheckPrintingController = foValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CustomCommonUtil.setDropShadow(AnchorMain, StackPane);
//        if (!psTransactionNo.isEmpty()) {
        initAll();
        loadRecordMaster();
//        }
    }

    private void initAll() {
        initButtonsClickActions();
        initTextAreaFields();
        initCheckBox();
        initDatePicker();
        clearFields();
        pnEditMode = EditMode.UNKNOWN;
        initFields(pnEditMode);
        initButton(pnEditMode);
    }

    private void loadRecordMaster() {
        tfDVNo.setText(poCheckPrintingController.Master().getTransactionNo() != null ? poCheckPrintingController.Master().getTransactionNo() : "");
        tfCheckNo.setText(poCheckPrintingController.Master().getTransactionNo() != null ? poCheckPrintingController.Master().getTransactionNo() : "");

        dpCheckDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(SQLUtil.dateFormat(poCheckPrintingController.Master().getTransactionDate(), SQLUtil.FORMAT_SHORT_DATE)));
        taRemarks.setText(poCheckPrintingController.Master().getTransactionNo() != null ? poCheckPrintingController.Master().getTransactionNo() : "");
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnAssign, btnPrintCheck, btnClose);
        buttons.forEach(button -> button.setOnAction(this::cmdButton_Click));
    }

    private void cmdButton_Click(ActionEvent event) {
        poJSON = new JSONObject();
        String lsButton = ((Button) event.getSource()).getId();
        switch (lsButton) {
            case "btnAssign":
                break;
            case "btnPrintCheck":
                break;
            case "btnClose":
                if (ShowMessageFX.YesNo("Are you sure want to close this form?", pxeModuleName, null)) {
                    CommonUtils.closeStage(btnClose);
                } else {
                    return;
                }
                break;
            default:
                ShowMessageFX.Warning("Please contact admin to assist about no button available", pxeModuleName, null);
                break;
        }
        initFields(pnEditMode);
        initButton(pnEditMode);
    }

    private void initTextAreaFields() {
        //Initialise  TextArea Focus
        taRemarks.focusedProperty().addListener(txtArea_Focus);
        //Initialise  TextArea KeyPressed
        taRemarks.setOnKeyPressed(event -> txtArea_KeyPressed(event));
    }

    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        TextArea txtArea = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsID = (txtArea.getId());
        String lsValue = txtArea.getText();

        if (lsValue == null) {
            return;
        }
        poJSON = new JSONObject();
        if (!nv) {
            switch (lsID) {
                case "taDVRemarks":
                    poCheckPrintingController.Master().setRemarks(lsValue);
                    break;
            }
        } else {
            txtArea.selectAll();
        }
    };

    private void txtArea_KeyPressed(KeyEvent event) {
        TextArea txtArea = (TextArea) event.getSource();
        String lsID = txtArea.getId();
        if ("taRemarks".equals(lsID)) {
            switch (event.getCode()) {
                case TAB:
                case ENTER:
                case DOWN:
                    CommonUtils.SetNextFocus(txtArea);
                    event.consume();
                    break;
                case UP:
                    CommonUtils.SetPreviousFocus(txtArea);
                    event.consume();
                    break;
                default:
                    break;
            }
        }
    }

    private void initCheckBox() {
        chbkApplyToAll.setOnAction(event -> {
            if ((pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE)) {
                poCheckPrintingController.Master().setBankPrint(chbkApplyToAll.isSelected() == true ? "1" : "0");
            }
        });
    }

    private void initDatePicker() {
        dpCheckDate.setOnAction(e -> {
//            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
//                LocalDate selectedLocalDate = dpCheckDate.getValue();
//                LocalDate checkDate = new java.sql.Date(poCheckPrintingController.CheckPayments().getModel().getCheckDate().getTime()).toLocalDate();
//                String psOldDate = CustomCommonUtil.formatLocalDateToShortString(checkDate); // abang lang ito
//                if (selectedLocalDate != null) {
//                    poCheckPrintingController.Master().CheckPayments()..setCheckDate(SQLUtil.toDate(selectedLocalDate.toString(), SQLUtil.FORMAT_SHORT_DATE));
//                }
//            }
//        }
        }
        );
    }

    private void clearFields() {
        JFXUtil.setValueToNull(null, dpCheckDate);
        JFXUtil.clearTextFields(apMaster);
        CustomCommonUtil.setSelected(false, chbkApplyToAll);
    }

    private void initFields(int fnEditMode) {
        boolean lbShow = (fnEditMode == EditMode.UPDATE);
        JFXUtil.setDisabled(!lbShow, apMaster);

    }

    private void initButton(int fnEditMode) {
        boolean lbShow = (pnEditMode == EditMode.UPDATE);
        JFXUtil.setButtonsVisibility(!lbShow, btnClose);
        JFXUtil.setButtonsVisibility(lbShow, btnAssign);
        JFXUtil.setButtonsVisibility(false, btnPrintCheck);
        if (fnEditMode == EditMode.READY) {
            switch (poCheckPrintingController.Master().getTransactionStatus()) {
                case DisbursementStatic.VERIFIED:
                    JFXUtil.setButtonsVisibility(true, btnPrintCheck);
                    break;
            }
        }
    }

}
