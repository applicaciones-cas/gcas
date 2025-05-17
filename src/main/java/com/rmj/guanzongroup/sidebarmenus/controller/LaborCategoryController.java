package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelListParameter;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.parameter.services.ParamControllers;
import org.json.simple.JSONObject;

public class LaborCategoryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private final String pxeModuleName = "Labor Category";
    private int pnEditMode;
    private ParamControllers oParameters;
    private boolean pbLoaded = false;
    private ObservableList<ModelListParameter> data = FXCollections.observableArrayList();
    private CachedRowSet cacheLaborList;
    private Integer pnListRow = 0;
    String brand = "";
    @FXML
    private AnchorPane AnchorMain, AnchorInputs;
    @FXML
    private HBox hbButtons;

    @FXML
    private Button btnBrowse,
            btnNew,
            btnSave,
            btnCancel,
            btnClose;

    @FXML
    private TextField txtField01,
            txtField02,
            txtField03,
            txtSeeks01;

    @FXML
    private CheckBox cbField01;

    @FXML
    private TableView tblList;

    @FXML
    private TableColumn index01, index02, index03;

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
    }

    @Override
    public void setCompanyID(String fsValue) {
    }

    @Override
    public void setCategoryID(String fsValue) {
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pnEditMode = EditMode.UNKNOWN;
        initButton(pnEditMode);
        initializeObject();
        InitTextFields();
        ClickButton();
        initTabAnchor();
        initTable();
        pbLoaded = true;
    }

    private void initializeObject() {
        LogWrapper logwrapr = new LogWrapper("CAS", System.getProperty("sys.default.path.temp") + "cas-error.log");
        oParameters = new ParamControllers(oApp, logwrapr);
//        oParameters.LaborCategory().setRecordStatus("0123");
    }

    private void ClickButton() {
        btnBrowse.setOnAction(this::handleButtonAction);
        btnNew.setOnAction(this::handleButtonAction);
        btnSave.setOnAction(this::handleButtonAction);
        btnCancel.setOnAction(this::handleButtonAction);
        btnClose.setOnAction(this::handleButtonAction);
    }

    private void handleButtonAction(ActionEvent event) {
//        Object source = event.getSource();
//
//        if (source instanceof Button) {
//            Button clickedButton = (Button) source;
//            unloadForm appUnload = new unloadForm();
//            switch (clickedButton.getId()) {
//                case "btnClose":
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
//                        appUnload.unloadForm(AnchorMain, oApp, pxeModuleName);
//                    }
//                    break;
//                case "btnNew":
//                    clearAllFields();
//                    txtField02.requestFocus();
//                    JSONObject poJSON = oParameters.LaborModel().newRecord();
//                    pnEditMode = EditMode.READY;
//                    if ("success".equals((String) poJSON.get("result"))) {
//                        pnEditMode = EditMode.ADDNEW;
//                        initButton(pnEditMode);
//                        initTabAnchor();
//
//                        loadRecord();
//                    } else {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        initTabAnchor();
//                    }
//                    break;
//                case "btnBrowse":
//
//                    String lsValue = (txtSeeks01.getText() == null) ? "" : txtSeeks01.getText();
//                    poJSON = oParameters.Category().searchRecord(lsValue, false);
//                    if ("error".equalsIgnoreCase(poJSON.get("result").toString())) {
//                        ShowMessageFX.Information((String) poJSON.get("message"), "Computerized Acounting System", pxeModuleName);
//                        break;
//                    }
//
//                    txtField01.setText((String) oParameters.Category().getModel().getDescription());
//                    oParameters.LaborCategory().getModel().setCategoryID(oParameters.Category().getModel().getCategoryId());
//                    oParameters.LaborCategory().LaborList(oParameters.Category().getModel().getCategoryId());
//                    pnEditMode = EditMode.UPDATE;
//                    initButton(pnEditMode);
//                    LoadList();
//                    initTabAnchor();
//                    break;
//                case "btnCancel":
//                    if (ShowMessageFX.YesNo("Do you really want to cancel this record? \nAny data collected will not be kept.", "Computerized Acounting System", pxeModuleName)) {
//                        clearAllFields();
//                        initializeObject();
//                        pnEditMode = EditMode.UNKNOWN;
//                        initButton(pnEditMode);
//                        initTabAnchor();
//                    }
//                    break;
//                case "btnSave":
//                    System.out.println("model id == " + oParameters.Model().getModel().getModelId());
//                    oParameters.LaborCategory().getModel().setCategoryID(oParameters.Category().getModel().getCategoryId());
//                    oParameters.LaborCategory().getModel().setModifyingId(oApp.getUserID());
//                    oParameters.LaborCategory().getModel().setModifiedDate(oApp.getServerDate());
//                    JSONObject saveResult = oParameters.LaborCategory().saveRecord();
//                    if ("success".equals((String) saveResult.get("result"))) {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                        pnEditMode = EditMode.UNKNOWN;
//                        initButton(pnEditMode);
//                        clearAllFields();
//                    } else {
//                        ShowMessageFX.Information((String) saveResult.get("message"), "Computerized Acounting System", pxeModuleName);
//                    }
//                    break;
//            }
//        }
    }

    private void clearAllFields() {
        txtField01.clear();
        txtField02.clear();
        txtField03.clear();
        txtSeeks01.clear();
        data.clear();
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);

        btnCancel.setVisible(lbShow);
        btnCancel.setManaged(lbShow);
        btnSave.setVisible(lbShow);
        btnSave.setManaged(lbShow);

        btnBrowse.setVisible(!lbShow);
        btnBrowse.setManaged(!lbShow);
        btnNew.setVisible(!lbShow);
        btnNew.setManaged(!lbShow);

        btnClose.setVisible(true);
        btnClose.setManaged(true);
    }

    private void InitTextFields() {
        txtField01.focusedProperty().addListener(txtField_Focus);
        txtField02.focusedProperty().addListener(txtField_Focus);
        txtField03.focusedProperty().addListener(txtField_Focus);
        txtField01.setOnKeyPressed(this::txtField_KeyPressed);
        txtField02.setOnKeyPressed(this::txtField_KeyPressed);
        txtSeeks01.setOnKeyPressed(this::txtSeeks_KeyPressed);
    }

    private void txtField_KeyPressed(KeyEvent event) {
//        TextField txtField = (TextField) event.getSource();
//        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
//        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
//        JSONObject poJson;
//        poJson = new JSONObject();
//
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex) {
//                    case 01:
//                        poJson = oParameters.Category().searchRecord(lsValue, false);
//                        if ("error".equalsIgnoreCase(poJson.get("result").toString())) {
//                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                            break;
//                        }
//                        txtField01.setText((String) oParameters.Category().getModel().getDescription());
//                        oParameters.LaborCategory().getModel().setCategoryID(oParameters.Category().getModel().getCategoryId());
//                        oParameters.LaborCategory().LaborList(oParameters.Category().getModel().getCategoryId());
//                        LoadList();
//                        break;
//
//                }
//            case ENTER:
//        }
//        switch (event.getCode()) {
//            case ENTER:
//                CommonUtils.SetNextFocus(txtField);
//            case DOWN:
//                CommonUtils.SetNextFocus(txtField);
//                break;
//            case UP:
//                CommonUtils.SetPreviousFocus(txtField);
//        }
    }

    private void txtSeeks_KeyPressed(KeyEvent event) {
//        TextField txtField = (TextField) event.getSource();
//        int lnIndex = Integer.parseInt(((TextField) event.getSource()).getId().substring(8, 10));
//        String lsValue = (txtField.getText() == null ? "" : txtField.getText());
//        JSONObject poJson;
//        poJson = new JSONObject();
//
//        switch (event.getCode()) {
//            case F3:
//                switch (lnIndex) {
//                    case 01:
//                        poJson = oParameters.Category().searchRecord(lsValue, false);
//                        if ("error".equalsIgnoreCase(poJson.get("result").toString())) {
//                            ShowMessageFX.Information((String) poJson.get("message"), "Computerized Acounting System", pxeModuleName);
//                            break;
//                        }
//                        txtSeeks01.setText((String) oParameters.Category().getModel().getDescription());
//                        txtField01.setText((String) oParameters.Category().getModel().getDescription());
//                        oParameters.LaborCategory().getModel().setCategoryID(oParameters.Category().getModel().getCategoryId());
//                        oParameters.LaborCategory().LaborList(oParameters.Category().getModel().getCategoryId());
//                        pnEditMode = EditMode.UPDATE;
//                        initButton(pnEditMode);
//                        LoadList();
//                        initTabAnchor();
//                        break;
//                }
//            case ENTER:
//        }
//        switch (event.getCode()) {
//            case ENTER:
//                CommonUtils.SetNextFocus(txtField);
//            case DOWN:
//                CommonUtils.SetNextFocus(txtField);
//                break;
//            case UP:
//                CommonUtils.SetPreviousFocus(txtField);
//        }
    }

    final ChangeListener<? super Boolean> txtField_Focus = (o, ov, nv) -> {
        if (!pbLoaded) {
            return;
        }

        TextField txtField = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        int lnIndex = Integer.parseInt(txtField.getId().substring(8, 10));
        String lsValue = txtField.getText();

        if (lsValue == null) {
            return;
        }

        if (!nv) {
            try {
                switch (lnIndex) {
                    case 3:
                        double amount = Double.parseDouble(lsValue);
                        if (lnIndex == 4) {
//                            oParameters.LaborModel().getModel().setAmount(amount);
                        }
                        txtField.setText(CommonUtils.NumberFormat(amount, "0.00"));
                        break;
                    default:
                        break;
                }
                txtField.selectAll();
            } catch (Exception e) {
                System.err.println("Error processing input: " + e.getMessage());
            }
        } else {
            txtField.selectAll();
        }
    };

    private void loadRecord() {
//        txtField02.setText(oParameters.LaborModel().getModel().Model().getDescription());
//        txtField03.setText(CommonUtils.NumberFormat(oParameters.LaborModel().getModel().getAmount(), "#,##0.00"));

    }

    @FXML
    void cbField01_Clicked(MouseEvent event) {
//        oParameters.LaborCategory().getModel().setRecordStatus(cbField01.isSelected() ? "1" : "0");
    }

    private void initTabAnchor() {
        if (AnchorInputs == null) {
            System.err.println("Error: AnchorInput is not initialized.");
            return;
        }

        boolean isEditable = (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE);
        AnchorInputs.setDisable(!isEditable);
        if (pnEditMode == EditMode.UPDATE) {
            txtField01.setDisable(false);
            txtField02.setDisable(false);
        }
    }

    private void initTable() {
        index01.setStyle("-fx-alignment: CENTER;");
        index02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        index03.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 0 0 5;");

        index01.setCellValueFactory(new PropertyValueFactory<>("index01"));
        index02.setCellValueFactory(new PropertyValueFactory<>("index02"));
        index03.setCellValueFactory(new PropertyValueFactory<>("index03"));

        tblList.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblList.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        tblList.setItems(data);
        tblList.autosize();
    }

    private void LoadList() {
        System.out.println("Loading Labor List...");
        data.clear();

        // ✅ Get cached data from the data handler
//        cacheLaborList = oParameters.LaborCategory().getCachedLaborList();
//
//        if (cacheLaborList == null) {
//            System.out.println("No cached data found! Fetching from database...");
//            oParameters.LaborCategory().LaborList(""); // Reload data if cache is empty
//            return;
//        }
//        try {
//            cacheLaborList.beforeFirst(); // Reset cursor before reading
//            int count = 1; // Initialize counter
//
//            for (; cacheLaborList.next(); count++) {
//                String laborName = cacheLaborList.getString("sLaborNme");
//                String amount = cacheLaborList.getString("nAmountxx");
//                String recordStat = cacheLaborList.getString("cRecdStat");
//
//                System.out.println("Entry No: " + count);
//                System.out.println("Labor Name: " + laborName);
//                System.out.println("Amount: " + amount);
//                System.out.println("Status: " + recordStat);
//
//                data.add(new ModelListParameter(String.valueOf(count), laborName, amount, recordStat));
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    @FXML
    void tblList_Clicked(MouseEvent event) {
        pnListRow = tblList.getSelectionModel().getSelectedIndex();

        if (pnListRow >= 0) {
            try {
                if (cacheLaborList == null) {
                    return;
                }

                cacheLaborList.absolute(pnListRow + 1);
                double newAmount = cacheLaborList.getDouble("nAmountxx");
                txtField03.setText(CommonUtils.NumberFormat(newAmount, "#,##0.00"));
                txtField02.setText(cacheLaborList.getString("sLaborNme"));
                cbField01.setSelected("1".equals(cacheLaborList.getString("cRecdStat")));

                txtField03.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        double updatedAmount = Double.parseDouble(newValue.replace(",", ""));
                        cacheLaborList.updateDouble("nAmountxx", updatedAmount);
                        cacheLaborList.updateRow();

                        data.set(pnListRow, new ModelListParameter(
                                String.valueOf(pnListRow + 1),
                                cacheLaborList.getString("sLaborNme"),
                                CommonUtils.NumberFormat(updatedAmount, "#,##0.00"),
                                cacheLaborList.getString("cRecdStat")
                        ));

                        tblList.refresh();
                    } catch (NumberFormatException | SQLException e) {
                    }
                });

                cbField01.selectedProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        String updatedStatus = newValue ? "1" : "0";
                        cacheLaborList.updateString("cRecdStat", updatedStatus);
                        cacheLaborList.updateRow();

                        data.set(pnListRow, new ModelListParameter(
                                String.valueOf(pnListRow + 1),
                                cacheLaborList.getString("sLaborNme"),
                                CommonUtils.NumberFormat(cacheLaborList.getDouble("nAmountxx"), "#,##0.00"),
                                cacheLaborList.getString("cRecdStat")
                        ));

                        tblList.refresh();
                    } catch (SQLException e) {
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

//    @FXML
//    void tblList_Clicked(MouseEvent event) {
//        int rowIndex = tblList.getSelectionModel().getSelectedIndex();
//        if (rowIndex < 0 || cacheLaborList == null) {
//            return;
//        }
//
//        try {
//            cacheLaborList.absolute(rowIndex + 1);
//            txtField02.setText(cacheLaborList.getString("sLaborNme"));
//            txtField03.setText(CommonUtils.NumberFormat(cacheLaborList.getDouble("nAmountxx"), "#,##0.00"));
//            cbField01.setSelected("1".equals(cacheLaborList.getString("cRecdStat")));
//
//            txtField03.textProperty().addListener((obs, oldVal, newVal) -> updateAmount(rowIndex, newVal));
//            cbField01.selectedProperty().addListener((obs, oldVal, newVal) -> updateStatus(rowIndex, newVal));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateAmount(int rowIndex, String newValue) {
//        try {
//            double updatedAmount = Double.parseDouble(newValue.replace(",", ""));
//            cacheLaborList.updateDouble("nAmountxx", updatedAmount);
//            cacheLaborList.updateRow();
//            refreshRow(rowIndex);
//        } catch (NumberFormatException | SQLException e) {
//        }
//    }
//
//    private void updateStatus(int rowIndex, boolean newValue) {
//        try {
//            cacheLaborList.updateString("cRecdStat", newValue ? "1" : "0");
//            cacheLaborList.updateRow();
//            refreshRow(rowIndex);
//        } catch (SQLException e) {
//        }
//    }
//
//    private void refreshRow(int rowIndex) throws SQLException {
//        data.set(rowIndex, new ModelListParameter(
//                cacheLaborList.getString("sLaborIDx"),
//                cacheLaborList.getString("sLaborNme"),
//                CommonUtils.NumberFormat(cacheLaborList.getDouble("nAmountxx"), "#,##0.00"),
//                cacheLaborList.getString("cRecdStat")
//        ));
//        tblList.refresh();
//    }
}
