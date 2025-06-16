/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDisbursementVoucher_Main;
import com.rmj.guanzongroup.sidebarmenus.utility.CustomCommonUtil;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.TAB;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONObject;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.json.simple.parser.ParseException;
import ph.com.guanzongroup.cas.cashflow.Disbursement;
import ph.com.guanzongroup.cas.cashflow.model.SelectedITems;
import ph.com.guanzongroup.cas.cashflow.services.CashflowControllers;
import ph.com.guanzongroup.cas.cashflow.status.DisbursementStatic;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DisbursementVoucher_CertificationController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    private static final int ROWS_PER_PAGE = 50;
    private final String pxeModuleName = "Disbursement Voucher Certification";
    private Disbursement poDisbursementController;
    public int pnEditMode;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";
    private String psDVNo = "";
    private String psBankName = "";
    private String psBankAccount = "";
    private int pnRow = -1;
    private double xOffset = 0;
    private double yOffset = 0;

    private unloadForm poUnload = new unloadForm();

    private ObservableList<ModelDisbursementVoucher_Main> main_data = FXCollections.observableArrayList();
    private FilteredList<ModelDisbursementVoucher_Main> filteredMain_Data;

    ArrayList<SelectedITems> getSelectedItems = new ArrayList<>();
    List<Pair<String, String>> plOrderNoPartial = new ArrayList<>();
    List<Pair<String, String>> plOrderNoFinal = new ArrayList<>();

    private final Map<String, List<String>> highlightedRowsMain = new HashMap<>();
    @FXML
    private AnchorPane AnchorMain, apBrowse, apButton;
    @FXML
    private Label lblSource;
    @FXML
    private TextField tfDVNo, tfSupPayeeName, tfBankName, tfBankAccount;
    @FXML
    private Button btnCertify, btnReturn, btnDisapproved, btnRetrieve, btnClose;
    @FXML
    private TableView<ModelDisbursementVoucher_Main> tblVwMain;
    @FXML
    private TableColumn<ModelDisbursementVoucher_Main, String> tblRowNo, tblDVNo, tblDate, tblSupplier, tblPayeeName, tblPaymentForm, tblBankName, tblBankAccount, tblTransAmount;
    @FXML
    private TableColumn<ModelDisbursementVoucher_Main, Boolean> tblCheckBox;
    @FXML
    private CheckBox chckSelectAll;
    @FXML
    private Pagination pagination;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            poDisbursementController = new CashflowControllers(oApp, null).Disbursement();
            poDisbursementController.setTransactionStatus(DisbursementStatic.VERIFIED);
            poJSON = new JSONObject();
            poJSON = poDisbursementController.InitTransaction(); // Initialize transaction
            if (!"success".equals((String) poJSON.get("result"))) {
                System.err.println((String) poJSON.get("message"));
                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
            }
            initAll();
            Platform.runLater(() -> {
                poDisbursementController.Master().setIndustryID(psIndustryId);
                poDisbursementController.Master().setCompanyID(psCompanyId);
                loadRecordSearch();
            });
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_CertificationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initAll() {
        initButtonsClickActions();
        initTextFields();
        initTableMain();
        initTableOnClick();
        initTextFieldsProperty();
        pnEditMode = EditMode.UNKNOWN;
        if (main_data.isEmpty()) {
            Label placeholderLabel = new Label("NO RECORD TO LOAD");
            tblVwMain.setPlaceholder(placeholderLabel);
            pagination.setManaged(false);
            pagination.setVisible(false);
        }
    }

    private void loadRecordSearch() {
        try {
            lblSource.setText(poDisbursementController.Master().Company().getCompanyName() + " - " + poDisbursementController.Master().Industry().getDescription());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(DisbursementVoucher_CertificationController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    private void initButtonsClickActions() {
        List<Button> buttons = Arrays.asList(btnCertify, btnReturn, btnDisapproved, btnRetrieve, btnClose);
        buttons.forEach(button -> button.setOnAction(this::cmdButton_Click));
    }

    private void cmdButton_Click(ActionEvent event) {
        poJSON = new JSONObject();
        String lsButton = ((Button) event.getSource()).getId();

        switch (lsButton) {
            case "btnCertify":
                handleDisbursementAction("certify");
                break;
            case "btnReturn":
                handleDisbursementAction("return");
                break;
            case "btnDisapproved":
                handleDisbursementAction("dissapprove");
                break;
            case "btnRetrieve":
                loadTableMain();

                break;
            case "btnClose":
                if (ShowMessageFX.YesNo("Are you sure you want to close this Tab?", "Close Tab", null)) {
                    poUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
                }
                break;
            default:
                ShowMessageFX.Warning("Please contact admin to assist about no button available", pxeModuleName, null);
                break;
        }
    }

    private void handleDisbursementAction(String action) {
        try {
            ObservableList<ModelDisbursementVoucher_Main> selectedItems = FXCollections.observableArrayList();

            for (ModelDisbursementVoucher_Main item : tblVwMain.getItems()) {
                if (item.getSelect().isSelected()) {
                    selectedItems.add(item);
                }
            }

            if (selectedItems.isEmpty()) {
                ShowMessageFX.Information(null, pxeModuleName, "No items selected to " + action + ".");
                return;
            }

            if (!ShowMessageFX.OkayCancel(null, pxeModuleName, "Are you sure you want to " + action + "?")) {
                return;
            }

            int successCount = 0;
            for (ModelDisbursementVoucher_Main item : selectedItems) {
                String lsDVNO = item.getIndex03();
                String Remarks = action;

                getSelectedItems.add(new SelectedITems(lsDVNO, Remarks));
                successCount++;
            }
            switch (action) {
                case "certify":
                    poJSON = poDisbursementController.CertifyTransaction("Certified", getSelectedItems);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        break;
                    }
                     ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                    break;
                case "return":
                    poJSON = poDisbursementController.ReturnTransaction("Returned", getSelectedItems);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        break;
                    }
                     ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                    break;
                case "dissapprove":
                    poJSON = poDisbursementController.DisapprovedTransaction("Disapproved", getSelectedItems);
                    if (!"success".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                        break;
                    }
                    break;
                default:
                    throw new AssertionError();
            }
            loadTableMain();
        } catch (ParseException | SQLException | GuanzonException | CloneNotSupportedException ex) {
            Logger.getLogger(DisbursementVoucher_CertificationController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    private void initTextFields() {
        //Initialise  TextField KeyPressed
        List<TextField> loTxtFieldKeyPressed = Arrays.asList(tfDVNo, tfSupPayeeName, tfBankAccount, tfBankName);
        loTxtFieldKeyPressed.forEach(tf -> tf.setOnKeyPressed(event -> txtFieldDV_KeyPressed(event)));
    }

    private void txtFieldDV_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        String lsID = (((TextField) event.getSource()).getId());
        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
        poJSON = new JSONObject();

        if (null != event.getCode()) {
            try {
                switch (event.getCode()) {
                    case TAB:
                    case ENTER:
                    case F3:
                        switch (lsID) {
                            case "tfDVNo":
                                break;
                            case "tfSupPayeeName":
                                poJSON = poDisbursementController.SearchPayee(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfSupPayeeName.setText(poDisbursementController.Master().Payee().getPayeeName() != null ? poDisbursementController.Master().Payee().getPayeeName() : "");
                                break;
                            case "tfBankName":
                                poJSON = poDisbursementController.SearchBanks(lsValue, false);
                                if ("error".equals((String) poJSON.get("result"))) {
                                    ShowMessageFX.Warning((String) poJSON.get("message"), pxeModuleName, null);
                                    return;
                                }
                                tfBankName.setText(poDisbursementController.CheckPayments().getModel().Banks().getBankName() != null ? poDisbursementController.CheckPayments().getModel().Banks().getBankName() : "");
                                break;
                            case "tfBankAccount":
                                break;
                        }
                        loadTableMain();
                        event.consume();
                    default:
                        break;

                }

            } catch (GuanzonException | SQLException ex) {
                Logger.getLogger(DisbursementVoucher_CertificationController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void loadTableMain() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblVwMain.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;");

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    try {
                        main_data.clear();
                        plOrderNoFinal.clear();
                        poJSON = poDisbursementController.getDisbursement(psDVNo, psSupplierId, false);
                        if ("success".equals(poJSON.get("result"))) {
                            if (poDisbursementController.getDisbursementMasterCount() > 0) {
                                int checkIndex = 0;
                                int otherIndex = 0;
                                for (int lnCntr = 0; lnCntr < poDisbursementController.getDisbursementMasterCount(); lnCntr++) {
                                    String lsPaymentForm = "";
                                    String lsBankName = "";
                                    String lsBankAccount = "";
                                    String disbursementType = poDisbursementController.poDisbursementMaster(lnCntr).getDisbursementType();
                                    switch (disbursementType) {
                                        case DisbursementStatic.DisbursementType.CHECK:
                                            lsPaymentForm = "CHECK";
//                                            if (checkIndex < poDisbursementController.CheckPayments().getCheckPaymentsCount()) {
//                                                lsBankName = poDisbursementController.CheckPayments().poCheckPayments(checkIndex).Banks().getBankName();
//                                                lsBankAccount = poDisbursementController.CheckPayments().poCheckPayments(checkIndex).getBankAcountID();
//                                            }
                                            checkIndex++; // Move to next CHECK payment
                                            break;

                                        case DisbursementStatic.DisbursementType.DIGITAL_PAYMENT:
                                            lsPaymentForm = "ONLINE PAYMENT";
//                                            if (otherIndex < poDisbursementController.OtherPayments().getOtherPaymentsCount()) {
//                                                lsBankName = poDisbursementController.OtherPayments().poOtherPayments(otherIndex).Banks().getBankName();
//                                                lsBankAccount = poDisbursementController.OtherPayments().poOtherPayments(otherIndex).getBankAccountID();
//                                            }
                                            otherIndex++; // Move to next OTHER payment
                                            break;

                                        case DisbursementStatic.DisbursementType.WIRED:
                                            lsPaymentForm = "BANK TRANSFER";
//                                            if (otherIndex < poDisbursementController.OtherPayments().getOtherPaymentsCount()) {
//                                                lsBankName = poDisbursementController.OtherPayments().poOtherPayments(otherIndex).Banks().getBankName();
//                                                lsBankAccount = poDisbursementController.OtherPayments().poOtherPayments(otherIndex).getBankAccountID();
//                                            }
                                            otherIndex++; // Move to next OTHER payment
                                            break;
                                        default:
                                            lsPaymentForm = "";
                                            break;
                                    }

                                    main_data.add(new ModelDisbursementVoucher_Main(
                                            String.valueOf(lnCntr + 1),
                                            "",
                                            poDisbursementController.poDisbursementMaster(lnCntr).getTransactionNo(),
                                            CustomCommonUtil.formatDateToShortString(poDisbursementController.poDisbursementMaster(lnCntr).getTransactionDate()),
                                            poDisbursementController.poDisbursementMaster(lnCntr).Payee().getPayeeName(),
                                            poDisbursementController.poDisbursementMaster(lnCntr).Payee().getPayeeName(),
                                            lsPaymentForm,
                                            lsBankName,
                                            lsBankAccount,
                                            CustomCommonUtil.setIntegerValueToDecimalFormat(poDisbursementController.poDisbursementMaster(lnCntr).getNetTotal(), true)
                                    ));
                                }
                            }
                        } else {
                            main_data.clear();
//                            }
                        }
                        showRetainedHighlight(true);
                        if (main_data.isEmpty()) {
                            tblVwMain.setPlaceholder(placeholderLabel);
                            ShowMessageFX.Warning(null, pxeModuleName, "No records found");
                            chckSelectAll.setSelected(false);
                        }
                        JFXUtil.loadTab(pagination, main_data.size(), ROWS_PER_PAGE, tblVwMain, filteredMain_Data);
                    } catch (GuanzonException | SQLException ex) {
                        Logger.getLogger(DisbursementVoucher_CertificationController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                return null;
            }

            @Override

            protected void succeeded() {
                btnRetrieve.setDisable(false);
                placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed
                if (main_data == null || main_data.isEmpty()) {
                    tblVwMain.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                } else {
                    pagination.setPageCount(0);
                    pagination.setVisible(true);
                    pagination.setManaged(true);
                    progressIndicator.setVisible(false);
                    progressIndicator.setManaged(false);
                    tblVwMain.toFront();
                }
            }

            @Override
            protected void failed() {
                if (main_data == null || main_data.isEmpty()) {
                    tblVwMain.setPlaceholder(placeholderLabel);
                    pagination.setManaged(false);
                    pagination.setVisible(false);
                }
                btnRetrieve.setDisable(false);
                progressIndicator.setVisible(false);
                progressIndicator.setManaged(false);
                tblVwMain.toFront();
            }
        };
        new Thread(task).start(); // Run task in background
    }

    private void initTableMain() {
        JFXUtil.setColumnCenter(tblRowNo, tblDVNo, tblDate, tblSupplier, tblPayeeName, tblPaymentForm, tblBankName, tblBankAccount);
        JFXUtil.setColumnRight(tblTransAmount);
        JFXUtil.setColumnsIndexAndDisableReordering(tblVwMain);
        tblCheckBox.setCellValueFactory(new PropertyValueFactory<>("select"));

        tblVwMain.getItems().forEach(item -> {
            CheckBox selectCheckBox = item.getSelect();
            selectCheckBox.setOnAction(event -> {
                if (tblVwMain.getItems().stream().allMatch(tableItem -> tableItem.getSelect().isSelected())) {
                    chckSelectAll.setSelected(true);
                } else {
                    chckSelectAll.setSelected(false);
                }
            });
        });
        chckSelectAll.setOnAction(event -> {
            boolean newValue = chckSelectAll.isSelected();
            tblVwMain.getItems().forEach(item -> item.getSelect().setSelected(newValue));
        });

        filteredMain_Data = new FilteredList<>(main_data, b -> true);
        tblVwMain.setItems(filteredMain_Data);
    }

    private void showRetainedHighlight(boolean isRetained) {
        if (isRetained) {
            for (Pair<String, String> pair : plOrderNoPartial) {
                if (!"0".equals(pair.getValue())) {

                    plOrderNoFinal.add(new Pair<>(pair.getKey(), pair.getValue()));
                }
            }
        }
        JFXUtil.disableAllHighlight(tblVwMain, highlightedRowsMain);
        plOrderNoPartial.clear();
        for (Pair<String, String> pair : plOrderNoFinal) {
            if (!"0".equals(pair.getValue())) {
                JFXUtil.highlightByKey(tblVwMain, pair.getKey(), "#A7C7E7", highlightedRowsMain);
            }
        }
    }

    private void initTableOnClick() {
        tblVwMain.setOnMouseClicked(event -> {
            if (tblVwMain.getSelectionModel().getSelectedIndex() >= 0 && event.getClickCount() == 2) {
                try {
                    ModelDisbursementVoucher_Main selected = (ModelDisbursementVoucher_Main) tblVwMain.getSelectionModel().getSelectedItem();
                    if (selected.getIndex03().isEmpty() && selected.getIndex03() == null) {
                        ShowMessageFX.Warning("Invalid to view no transaction no.", pxeModuleName, null);
                        return;
                    }
                    loadDVWindow(selected.getIndex03());
                } catch (SQLException ex) {
                    Logger.getLogger(DisbursementVoucher_CertificationController.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void loadDVWindow(String fsTransactionNo) throws SQLException {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_View.fxml"));
            DisbursementVoucher_ViewController loControl = new DisbursementVoucher_ViewController();
            loControl.setGRider(oApp);
            loControl.setDisbursement(poDisbursementController);
            loControl.setTransaction(fsTransactionNo);
            fxmlLoader.setController(loControl);
            //load the main interface
            Parent parent = fxmlLoader.load();
            parent.setOnMousePressed((MouseEvent event) -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            parent.setOnMouseDragged((MouseEvent event) -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
            //set the main interface as the scene
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            scene.setFill(Color.TRANSPARENT);
            stage.setTitle("");
            stage.showAndWait();
        } catch (IOException e) {
            ShowMessageFX.Warning(e.getMessage(), "Warning", null);
            System.exit(1);
        }
    }

    private void initTextFieldsProperty() {
        tfSupPayeeName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poDisbursementController.Master().setPayeeID("");
                    tfSupPayeeName.setText("");
                    loadTableMain();
                }
            }
        }
        );
        tfBankName.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (newValue.isEmpty()) {
                    poDisbursementController.CheckPayments().getModel().setBankID("");
                    tfBankName.setText("");
                    loadTableMain();
                }
            }
        }
        );
    }
}
