/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelStockRequest;
import com.rmj.guanzongroup.sidebarmenus.table.model.ReportPrinter;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import static javafx.scene.input.KeyCode.DOWN;
import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCode.F3;
import static javafx.scene.input.KeyCode.UP;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.inv.Inventory;
import org.guanzon.cas.inv.warehouse.StockRequest;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class InventoryRequestSP_ROQController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Inventory Request SP ROQ";

    private GRider oApp;
    private int pnEditMode;
    private StockRequest oTrans;
    private ParamControllers oParameters;
    private boolean pbLoaded = false;
    private ObservableList<ModelStockRequest> ROQData = FXCollections.observableArrayList();
    private ObservableList<ModelStockRequest> orderData = FXCollections.observableArrayList();
    ReportPrinter printer = new ReportPrinter();
    String category = System.getProperty("store.inventory.industry");

    @FXML
    private AnchorPane anchorMain, anchorMaster, anchorDetails, anchorTable;

    @FXML
    private HBox hbButtons;

    @FXML
    private Button btnBrowse, btnNew, btnSave, btnUpdate, btnSearch, btnPrint, btnAddItem,
            btnDelItem, btnApprove, btnCancelTrans, btnCancel, btnClose, btnStatistic;

    @FXML
    private TextField txtField01, txtField02, txtField03, txtField04, txtField05, txtField06,
            txtField07, txtField08, txtField09, txtField10, txtField11, txtField12,
            txtField14, txtField15, txtField16, txtField17, txtField18, txtField19,
            txtField20, txtField21,txtField22, txtField23;

    @FXML
    private DatePicker dpField01;

    @FXML
    private TextArea txtArea01;

    @FXML
    private Label lblStatus;

    @FXML
    private TableView tblDetails, tblDetailsROQ;

    @FXML
    private TableColumn index01, index02, index03, index04, index05, index06,
            index07, index08, index09, index10, index11;

    @FXML
    private TableColumn indexROQ01, indexROQ02, indexROQ03, indexROQ04, indexROQ05,
            indexROQ06, indexROQ07, indexROQ08, indexROQ09;

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        initializeObject();
        ClickButton();
        initTabAnchor();
        InitTextFields();
        clearAllFields();
        initTblDetails();
        initTblDetailsROQ();
        pbLoaded = true;
    }

    private void initializeObject() {
        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
        oTrans = new StockRequest();
//        oTrans.setApplicationDriver(oApp);
//        oTrans.setWithParentClass(false);
//        oTrans.setLogWrapper(logwrapr);
//        oTrans.initialize();
        oParameters = new ParamControllers(oApp, logwrapr);
    }

    private void InitTextFields() {
        txtArea01.focusedProperty().addListener(txtArea_Focus);
// Define arrays for text fields with focusedProperty listeners
        TextField[] focusTextFields = {
            txtField01, txtField02, txtField03, txtField04, txtField05, txtField06,
            txtField07, txtField08, txtField09, txtField10, txtField11, txtField12,
            txtField14, txtField15, txtField16, txtField17, txtField18, txtField19,
            txtField20, txtField21};

// Add the listener to each text field in the focusTextFields array
        for (TextField textField : focusTextFields) {
            textField.focusedProperty().addListener(txtField_Focus);
        }

// Define arrays for text fields with setOnKeyPressed handlers
        TextField[] keyPressedTextFields = {
            txtField03, txtField04, txtField05, txtField09, txtField11, txtField14, txtField08
        };

// Set the same key pressed event handler for each text field in the keyPressedTextFields array
        for (TextField textField : keyPressedTextFields) {
            textField.setOnKeyPressed(this::txtField_KeyPressed);
        }

//        lblStatus.setText(chkField04.isSelected() ? "ACTIVE" : "INACTIVE");        
    }
    final ChangeListener<? super Boolean> txtArea_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextArea txtArea = (TextArea) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtArea.getId().substring(7, 9));
        String lsValue = (txtArea.getText() == null ? "" : txtArea.getText());
        JSONObject jsonObject = new JSONObject();
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lnIndex) {
                case 1:
                    break;
            }
        }
    };

    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
        JSONObject jsonObject = new JSONObject();
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lnIndex) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    break;
                case 5:
                    break;
                case 8:
                    break;
            }

        } else {
            txtField.selectAll();
        }
    };

    private void txtField_KeyPressed(KeyEvent event) {
        TextField txtField = (TextField) event.getSource();
        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
        JSONObject poJson;
        switch (event.getCode()) {
            case ENTER:
            case F3:
                switch (lnIndex) {
                    case 3:
                        break;
                    case 4:
                        break;
                    case 8:
                        break;

                }
        }
        switch (event.getCode()) {
            case DOWN:
                CommonUtils.SetNextFocus(txtField);
                break;
            case UP:
                CommonUtils.SetPreviousFocus(txtField);
        }
    }

    private void initTabAnchor() {
        boolean pbValue = pnEditMode == EditMode.ADDNEW
                || pnEditMode == EditMode.UPDATE;
        anchorMaster.setDisable(!pbValue);
        anchorDetails.setDisable(!pbValue);
        anchorTable.setDisable(!pbValue);
        if (pnEditMode == EditMode.READY) {
            anchorTable.setDisable(false);
            btnStatistic.setDisable(false);
        }
    }

    private void ClickButton() {
        btnCancel.setOnAction(this::handleButtonAction);
        btnNew.setOnAction(this::handleButtonAction);
        btnSave.setOnAction(this::handleButtonAction);
        btnUpdate.setOnAction(this::handleButtonAction);
        btnClose.setOnAction(this::handleButtonAction);
        btnBrowse.setOnAction(this::handleButtonAction);
        btnAddItem.setOnAction(this::handleButtonAction);
        btnDelItem.setOnAction(this::handleButtonAction);
        btnPrint.setOnAction(this::handleButtonAction);
        btnCancelTrans.setOnAction(this::handleButtonAction);
        btnApprove.setOnAction(this::handleButtonAction);
    }

    private void handleButtonAction(ActionEvent event) {
        Object source = event.getSource();

        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            unloadForm appUnload = new unloadForm();
            switch (clickedButton.getId()) {
                case "btnClose":
                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
                        appUnload.unloadForm(anchorMain, oApp, pxeModuleName);
                    }
                    break;
                case "btnNew":
                    try {
                    JSONObject poJSON;
                    poJSON = oTrans.NewTransaction();
                    if ("success".equals((String) poJSON.get("result"))) {
                        pnEditMode = oTrans.getEditMode();
                        initButton(pnEditMode);
//                        loadDetails();
txtField01.setText(oTrans.Master().getTransactionNo());
                        initTabAnchor();
                        
                    } else {
                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
                        System.out.println((String) poJSON.get("message"));
                        initTabAnchor();
                    }
                    } catch (CloneNotSupportedException | ExceptionInInitializerError e) {
                    System.err.println(MiscUtil.getException(e));
                }
                    break;
                case "btnBrowse":
                    break;
                case "btnUpdate":

                    break;
                case "btnCancel":
                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
                        clearAllFields();
                        initializeObject();
                        pnEditMode = EditMode.UNKNOWN;
                        initButton(pnEditMode);
                        initTabAnchor();
                    }
                    break;
                case "btnSave":
                    break;
                case "btnAddItem":
                    break;
                case "btnDelItem":
                    break;
                case "btnPrint":
                    if (pnEditMode == 1 && ShowMessageFX.YesNo("Do you want to print this record?", "Computerized Accounting System", pxeModuleName)) {
                        loadPrint();
                    }
                    break;

            }
        }
    }

    private boolean loadPrint() {
        JSONObject loJSON = new JSONObject();
//        if (oTrans.getModel().getBarCode() == null) {
//            ShowMessageFX.Warning("Unable to print transaction.", "Warning", "No record loaded.");
//            loJSON.put("result", "error");
//            loJSON.put("message", "Model Master is null");
//            return false;
//        }

        // Prepare report parameters
        Map<String, Object> params = new HashMap<>();
        params.put("sPrintdBy", "Printed By: " + oApp.getLogName());
//      params.put("sReportDt", CommonUtils.xsDateLong(oApp.getServerDate()));
        params.put("sReportNm", pxeModuleName);
        params.put("sReportDt", CommonUtils.xsDateMedium((Date) oApp.getServerDate()));
        params.put("sBranchNm", oApp.getBranchName());
        params.put("sAddressx", oApp.getAddress());
//        params.put("sTransNox", oTrans.getMasterModel().getTransactionNumber());
//        params.put("sTranDte", CommonUtils.xsDateMedium((Date) oTrans.getMasterModel().getTransaction()));
//        params.put("sRemarks", oTrans.getMasterModel().getRemarks());
//        params.put("status", oTrans.getMasterModel().getTransactionStatus());
//      params.put("sTranType", "Unprcd Qty");
//      params.put("sTranQty", "Cancel");

        // Define report file paths
        String sourceFileName = oApp.getReportPath() + "InventoryRequestROQ.jasper";
        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ROQData);

        return printer.loadAndShowReport(sourceFileName, params, ROQData, pxeModuleName);
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);

// Manage visibility and managed state of buttons
        btnCancel.setVisible(lbShow);
        btnSearch.setVisible(lbShow);
        btnSave.setVisible(lbShow);
        btnAddItem.setVisible(lbShow);
        btnDelItem.setVisible(lbShow);

        btnCancel.setManaged(lbShow);
        btnSearch.setManaged(lbShow);
        btnSave.setManaged(lbShow);
        btnAddItem.setManaged(lbShow);
        btnDelItem.setManaged(lbShow);

// Manage visibility and managed state of other buttons
        btnBrowse.setVisible(!lbShow);
        btnNew.setVisible(!lbShow);
        btnClose.setVisible(!lbShow);
        btnBrowse.setManaged(!lbShow);
        btnNew.setManaged(!lbShow);
        btnClose.setManaged(!lbShow);

        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
        btnAddItem.setVisible(lbShow);
        btnAddItem.setManaged(lbShow);
        btnDelItem.setVisible(false);
        btnDelItem.setManaged(false);
        boolean isVisible = (fnValue == 1);
        btnCancelTrans.setVisible(isVisible);
        btnCancelTrans.setManaged(isVisible);
        btnApprove.setVisible(isVisible);
        btnApprove.setManaged(isVisible);

    }

    @FXML
    private void tblDetails_Clicked(MouseEvent event) {
    }

    @FXML
    private void tblDetailsROQ_Clicked(MouseEvent event) {
    }

    private void initTblDetails() {
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index06.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index07.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index08.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index09.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index10.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index11.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));
        index04.setCellValueFactory(new PropertyValueFactory<>("index04"));
        index05.setCellValueFactory(new PropertyValueFactory<>("index05"));
        index06.setCellValueFactory(new PropertyValueFactory<>("index06"));
        index07.setCellValueFactory(new PropertyValueFactory<>("index07"));
        index08.setCellValueFactory(new PropertyValueFactory<>("index08"));
        index09.setCellValueFactory(new PropertyValueFactory<>("index09"));
        index10.setCellValueFactory(new PropertyValueFactory<>("index10"));
        index11.setCellValueFactory(new PropertyValueFactory<>("index11"));

        tblDetails.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof TableViewSkin) {
                TableViewSkin<?> skin = (TableViewSkin<?>) newValue;
                VirtualFlow<?> virtualFlow = (VirtualFlow<?>) skin.getChildren().get(1);

                // Add listener for horizontal scrollbar visibility
                ScrollBar hScrollBar = (ScrollBar) virtualFlow.lookup(".scroll-bar:horizontal");
                if (hScrollBar != null) {
                    hScrollBar.visibleProperty().addListener((obs, wasVisible, isVisible) -> {
                        System.out.println("visible? == " + isVisible);
                        if (isVisible) {
                            System.out.println("visible? == true");
                            index11.setMinWidth(81);
                            index11.setMaxWidth(81);
                        } else {

                            System.out.println("visible? == false");
                            index11.setMinWidth(95);
                            index11.setMaxWidth(95);
                        }

                    });
                }
            }
        });
//        tblDetails.setItems(R1data);
        tblDetails.autosize();
    }

    private void initTblDetailsROQ() {
        indexROQ01.setStyle("-fx-alignment: CENTER;");
        indexROQ02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexROQ03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexROQ04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexROQ05.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexROQ06.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexROQ07.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexROQ08.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        indexROQ09.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        indexROQ01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        indexROQ02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        indexROQ03.setCellValueFactory(new PropertyValueFactory<>("index03"));
        indexROQ04.setCellValueFactory(new PropertyValueFactory<>("index04"));
        indexROQ05.setCellValueFactory(new PropertyValueFactory<>("index05"));
        indexROQ06.setCellValueFactory(new PropertyValueFactory<>("index06"));
        indexROQ07.setCellValueFactory(new PropertyValueFactory<>("index07"));
        indexROQ08.setCellValueFactory(new PropertyValueFactory<>("index08"));
        indexROQ09.setCellValueFactory(new PropertyValueFactory<>("index09"));

        tblDetailsROQ.skinProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue instanceof TableViewSkin) {
                TableViewSkin<?> skin = (TableViewSkin<?>) newValue;
                VirtualFlow<?> virtualFlow = (VirtualFlow<?>) skin.getChildren().get(1);

                // Add listener for horizontal scrollbar visibility
                ScrollBar hScrollBar = (ScrollBar) virtualFlow.lookup(".scroll-bar:horizontal");
                if (hScrollBar != null) {
                    hScrollBar.visibleProperty().addListener((obs, wasVisible, isVisible) -> {
                        System.out.println("visible? == " + isVisible);
                        if (isVisible) {
                            System.out.println("visible? == true");
                            index11.setMinWidth(81);
                            index11.setMaxWidth(81);
                        } else {

                            System.out.println("visible? == false");
                            index11.setMinWidth(95);
                            index11.setMaxWidth(95);
                        }

                    });
                }
            }
        });

//        tblDetailsROQ.setItems(R2data);
        tblDetailsROQ.autosize();
    }

    private void clearAllFields() {
// Arrays of TextFields grouped by sections
        TextField[][] allFields = {
            // Text fields related to specific sections
            {txtField01, txtField02, txtField03, txtField04, txtField05, txtField06,
                txtField07, txtField08, txtField09, txtField10, txtField11, txtField12,
                txtField14, txtField15, txtField16, txtField17, txtField18, txtField19,
                txtField20, txtField21, txtField22, txtField23},};

// Loop through each array of TextFields and clear them
        for (TextField[] fields : allFields) {
            for (TextField field : fields) {
                field.clear();
            }
        }
        ROQData.clear();
        orderData.clear();
        txtArea01.clear();
        lblStatus.setText("UNKNOWN");
    }
}
