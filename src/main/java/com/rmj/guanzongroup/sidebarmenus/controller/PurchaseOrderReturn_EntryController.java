/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrderReturn_Detail;
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
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
import javafx.scene.layout.StackPane;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.purchasing.services.PurchaseOrderReturnControllers;
import org.guanzon.cas.purchasing.status.PurchaseOrderReturnStatus;
import org.json.simple.JSONObject;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.List;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReturn;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PurchaseOrderReturn_EntryController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    int pnDetail = 0;
    boolean lsIsSaved = false;
    private final String pxeModuleName = "Purchase Order Return Entry";
    static PurchaseOrderReturn poPurchaseReturnController;
    public int pnEditMode;

    private String psIndustryId = "";
    private String psCompanyId = "";
    private String psCategoryId = "";
    private String psSupplierId = "";

    private ObservableList<ModelPurchaseOrderReturn_Detail> details_data = FXCollections.observableArrayList();
    private FilteredList<ModelPurchaseOrderReturn_Detail> filteredDataDetail;

    private final Map<String, List<String>> highlightedRowsDetail = new HashMap<>();
    private Object lastFocusedTextField = null;
    private Object previousSearchedTextField = null;

    @FXML
    private AnchorPane apMainAnchor, apBrowse, apButton, apMaster, apDetail;

    @FXML
    private HBox hbButtons, hboxid;

    @FXML
    private Label lblSource, lblStatus;

    @FXML
    private Button btnBrowse, btnNew, btnUpdate, btnSearch, btnSave, btnCancel, btnPrint, btnHistory, btnClose;

    @FXML
    private TextField tfTransactionNo, tfSupplier, tfReferenceNo, tfPOReceivingNo, tfTotal, tfBarcode, tfDescription,
            tfReturnQuantity, tfBrand, tfModel, tfColor, tfInventoryType, tfMeasure, tfCost, tfReceiveQuantity;

    @FXML
    private DatePicker dpTransactionDate;

    @FXML
    private TextArea taRemarks;

    @FXML
    private TableView tblViewDetails;

    @FXML
    private TableColumn tblRowNoDetail, tblBarcodeDetail, tblDescriptionDetail, tblCostDetail, tblReceiveQuantityDetail,
            tblReturnQuantityDetail, tblTotalDetail;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        poPurchaseReturnController = new PurchaseOrderReturnControllers(oApp, null).PurchaseOrderReturn();
        poJSON = new JSONObject();
        poJSON = poPurchaseReturnController.InitTransaction(); // Initialize transaction
        if (!"success".equals((String) poJSON.get("result"))) {
            System.err.println((String) poJSON.get("message"));
            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
        }
        initTextFields();
        initDatePickers();
        initDetailsGrid();
        initTableOnClick();
        clearTextFields();
        loadRecordMaster();
        loadTableDetail();
        pnEditMode = poPurchaseReturnController.getEditMode();
        initButton(pnEditMode);

        Platform.runLater(() -> {
            poPurchaseReturnController.Master().setIndustryId(psIndustryId);
            poPurchaseReturnController.Master().setCompanyId(psCompanyId);
            poPurchaseReturnController.setIndustryId(psIndustryId);
            poPurchaseReturnController.setCompanyId(psCompanyId);
            poPurchaseReturnController.setCategoryId(psCategoryId);
            loadRecordSearch();

            btnNew.fire();
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

        try {
            Object source = event.getSource();
            if (source instanceof Button) {
                Button clickedButton = (Button) source;
                String lsButton = clickedButton.getId();
                switch (lsButton) {
                    case "btnBrowse":
                        poPurchaseReturnController.setTransactionStatus(PurchaseOrderReturnStatus.RETURNED + "" + PurchaseOrderReturnStatus.OPEN);
                        poJSON = poPurchaseReturnController.searchTransaction();
                        if ("error".equalsIgnoreCase((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfTransactionNo.requestFocus();
                            return;
                        }
                        pnEditMode = poPurchaseReturnController.getEditMode();
                        psSupplierId = poPurchaseReturnController.Master().getSupplierId();

                        break;
                    case "btnPrint":
                        poJSON = poPurchaseReturnController.printRecord(() -> {
                            if (lsIsSaved) {
                                Platform.runLater(() -> {
                                    btnNew.fire();
                                });
                            } else {
                                loadRecordMaster();
                            }
                            lsIsSaved = false;
                        });
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
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
                    case "btnNew":
                        //Clear data
                        poPurchaseReturnController.resetMaster();
                        poPurchaseReturnController.Detail().clear();
                        clearTextFields();

                        poJSON = poPurchaseReturnController.NewTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }

                        poPurchaseReturnController.initFields();
                        pnEditMode = poPurchaseReturnController.getEditMode();

                        break;
                    case "btnUpdate":
                        poJSON = poPurchaseReturnController.OpenTransaction(poPurchaseReturnController.Master().getTransactionNo());
                        poJSON = poPurchaseReturnController.UpdateTransaction();
                        if ("error".equals((String) poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            return;
                        }
                        pnEditMode = poPurchaseReturnController.getEditMode();
                        break;
                    case "btnSearch":
                        String lsMessage = "Focus a searchable textfield to search";
                        if ((lastFocusedTextField != null)) {
                            if (lastFocusedTextField instanceof TextField) {
                                TextField tf = (TextField) lastFocusedTextField;
                                if (JFXUtil.getTextFieldsIDWithPrompt("Press F3: Search", apMaster, apDetail).contains(tf.getId())) {

                                    if (lastFocusedTextField == previousSearchedTextField) {
                                        break;
                                    }
                                    previousSearchedTextField = lastFocusedTextField;
                                    // Create a simulated KeyEvent for F3 key press
                                    KeyEvent keyEvent = new KeyEvent(
                                            KeyEvent.KEY_PRESSED,
                                            "",
                                            "",
                                            KeyCode.F3,
                                            false, false, false, false
                                    );
                                    tf.fireEvent(keyEvent);
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
                            poPurchaseReturnController.resetMaster();
                            poPurchaseReturnController.Detail().clear();
                            clearTextFields();

                            poPurchaseReturnController.Master().setIndustryId(psIndustryId);
                            poPurchaseReturnController.Master().setCompanyId(psCompanyId);
                            poPurchaseReturnController.Master().setCategoryCode(psCategoryId);
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
                            poJSON = poPurchaseReturnController.SaveTransaction();
                            if (!"success".equals((String) poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                poPurchaseReturnController.AddDetail();
                                return;
                            } else {
                                //reshow the highlight
                                ShowMessageFX.Information(null, pxeModuleName, (String) poJSON.get("message"));

                                // Confirmation Prompt
                                JSONObject loJSON = poPurchaseReturnController.OpenTransaction(poPurchaseReturnController.Master().getTransactionNo());
                                if ("success".equals(loJSON.get("result"))) {
                                    if (poPurchaseReturnController.Master().getTransactionStatus().equals(PurchaseOrderReturnStatus.OPEN)) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to confirm this transaction?")) {
                                            loJSON = poPurchaseReturnController.ConfirmTransaction("Confirmed");
                                            if ("success".equals((String) loJSON.get("result"))) {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            } else {
                                                ShowMessageFX.Information((String) loJSON.get("message"), pxeModuleName, null);
                                            }
                                        }
                                    }
                                }

                                // Print Transaction Prompt
                                lsIsSaved = false;
                                loJSON = poPurchaseReturnController.OpenTransaction(poPurchaseReturnController.Master().getTransactionNo());
                                loadRecordMaster();
                                if ("success".equals(loJSON.get("result"))) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName, "Do you want to print this transaction?")) {
                                        lsIsSaved = true;
                                        btnPrint.fire();
                                    } else {
                                        btnNew.fire();
                                    }
                                } else {
                                    btnNew.fire();
                                }

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
                    if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !"".equals(poPurchaseReturnController.Detail(pnDetail).getStockId())) {
                        tfReturnQuantity.requestFocus();
                    } else {
                        tfBarcode.requestFocus();
                    }
                }

            }
        } catch (CloneNotSupportedException | SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (ParseException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
                    poJSON = poPurchaseReturnController.Master().setRemarks(lsValue);
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
        lastFocusedTextField = txtPersonalInfo;
        previousSearchedTextField = null;
        if (lsValue == null) {
            return;
        }
        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfDescription":
                case "tfBarcode":
                    //if value is blank then reset
                    if (lsValue.equals("")) {
                        poJSON = poPurchaseReturnController.Detail(pnDetail).setStockId("");
                    }

                    break;
                case "tfReturnQuantity":
                    if (lsValue.isEmpty()) {
                        lsValue = "0";
                    }
                    if (poPurchaseReturnController.Detail(pnDetail).getQuantity() != null
                            && !"".equals(poPurchaseReturnController.Detail(pnDetail).getQuantity())) {
                        if (poPurchaseReturnController.getReceiveQty(pnDetail).intValue() < Integer.valueOf(lsValue)) {
                            ShowMessageFX.Warning(null, pxeModuleName, "Return quantity cannot be greater than the receive quantity.");
                            poPurchaseReturnController.Detail(pnDetail).setQuantity(0);
                            tfReturnQuantity.requestFocus();
                            break;
                        }
                    }

                    poJSON = poPurchaseReturnController.Detail(pnDetail).setQuantity((Integer.valueOf(lsValue)));
                    if ("error".equals((String) poJSON.get("result"))) {
                        System.err.println((String) poJSON.get("message"));
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
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
        try {
            poJSON = new JSONObject();
            TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
            String lsTxtFieldID = (txtPersonalInfo.getId());
            String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());
            lastFocusedTextField = txtPersonalInfo;
            previousSearchedTextField = null;
            if (lsValue == null) {
                return;
            }
            if (!nv) {
                /*Lost Focus*/
                switch (lsTxtFieldID) {
                    case "tfSupplier":
                        if (lsValue.isEmpty()) {
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poPurchaseReturnController.Master().getSupplierId() != null && !"".equals(poPurchaseReturnController.Master().getSupplierId())) {
                                    if (poPurchaseReturnController.getDetailCount() > 1) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the supplier name? Please note that doing so will delete all transaction details. Do you wish to proceed?") == true) {
                                            poPurchaseReturnController.removeDetails();
                                            loadTableDetail();
                                        } else {
                                            loadRecordMaster();
                                            return;
                                        }
                                    }
                                }
                            }

                            poJSON = poPurchaseReturnController.Master().setSupplierId("");
                            poJSON = poPurchaseReturnController.Master().setSourceNo("");
                        }
                        break;
                    case "tfReferenceNo":
                        if (!lsValue.isEmpty()) {
                        } else {
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poPurchaseReturnController.Master().PurchaseOrderReceivingMaster().getReferenceNo() != null && !"".equals(poPurchaseReturnController.Master().PurchaseOrderReceivingMaster().getReferenceNo())) {
                                    if (poPurchaseReturnController.getDetailCount() > 1) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the reference no? Please note that doing so will delete all transaction details. Do you wish to proceed?") == true) {
                                            poPurchaseReturnController.removeDetails();
                                            loadTableDetail();
                                        } else {
                                            loadRecordMaster();
                                            return;
                                        }
                                    }
                                }
                            }

                            poJSON = poPurchaseReturnController.Master().PurchaseOrderReceivingMaster().setReferenceNo("");
                            poJSON = poPurchaseReturnController.Master().setSourceNo("");
                        }
                        if ("error".equals(poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfReferenceNo.setText("");
                            break;
                        }
                        break;

                    case "tfPOReceivingNo":
                        if (!lsValue.isEmpty()) {
                        } else {
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poPurchaseReturnController.Master().getSourceNo() != null && !"".equals(poPurchaseReturnController.Master().getSourceNo())) {
                                    if (poPurchaseReturnController.getDetailCount() > 1) {
                                        if (ShowMessageFX.YesNo(null, pxeModuleName,
                                                "Are you sure you want to change the reference no? Please note that doing so will delete all transaction details. Do you wish to proceed?") == true) {
                                            poPurchaseReturnController.removeDetails();
                                            loadTableDetail();
                                        } else {
                                            loadRecordMaster();
                                            return;
                                        }
                                    }
                                }
                            }

                            poJSON = poPurchaseReturnController.Master().setSourceNo("");
                        }
                        if ("error".equals(poJSON.get("result"))) {
                            ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                            tfPOReceivingNo.setText("");
                            break;
                        }
                        break;

                }

                loadRecordMaster();

            }
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    };

    private void txtField_KeyPressed(KeyEvent event) {
        try {
            TextField txtField = (TextField) event.getSource();
            String lsID = (((TextField) event.getSource()).getId());
            String lsValue = (txtField.getText() == null ? "" : txtField.getText());
            poJSON = new JSONObject();
            int lnRow = pnDetail;
            TableView currentTable = tblViewDetails;
            TablePosition focusedCell = currentTable.getFocusModel().getFocusedCell();

            switch (event.getCode()) {
                case ENTER:
                    CommonUtils.SetNextFocus(txtField);
                    break;
                case UP:
                    switch (lsID) {
                        case "tfBarcode":
                        case "tfReturnQuantity":
                            int lnQty = Integer.valueOf(poPurchaseReturnController.Detail(pnDetail).getQuantity().toString());
                            apDetail.requestFocus();
                            int lnNewvalue = Integer.valueOf(poPurchaseReturnController.Detail(pnDetail).getQuantity().toString());
                            if (lnQty != lnNewvalue && (lnQty > 0
                                    && poPurchaseReturnController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poPurchaseReturnController.Detail(pnDetail).getStockId()))) {
                                tfReturnQuantity.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToPreviousRow(currentTable);
                                loadRecordDetail();
                                if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !poPurchaseReturnController.Detail(pnDetail).getStockId().equals("")) {
                                    tfReturnQuantity.requestFocus();
                                } else {
                                    tfBarcode.requestFocus();
                                }
                                event.consume();
                            }
                            break;
                    }
                    break;
                case DOWN:
                    switch (lsID) {
                        case "tfBarcode":
                        case "tfReturnQuantity":
                            int lnQty = Integer.valueOf(poPurchaseReturnController.Detail(pnDetail).getQuantity().toString());
                            apDetail.requestFocus();
                            int lnNewvalue = Integer.valueOf(poPurchaseReturnController.Detail(pnDetail).getQuantity().toString());
                            if (lnQty != lnNewvalue && (lnQty > 0
                                    && poPurchaseReturnController.Detail(pnDetail).getStockId() != null
                                    && !"".equals(poPurchaseReturnController.Detail(pnDetail).getStockId()))) {
                                tfReturnQuantity.requestFocus();
                            } else {
                                pnDetail = JFXUtil.moveToNextRow(currentTable);
                                loadRecordDetail();
                                if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !poPurchaseReturnController.Detail(pnDetail).getStockId().equals("")) {
                                    tfReturnQuantity.requestFocus();
                                } else {
                                    tfBarcode.requestFocus();
                                }
                                event.consume();
                            }
                            break;
                        default:
                            break;
                    }
                    break;
                case F3:
                    switch (lsID) {
                        case "tfSupplier":
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poPurchaseReturnController.getDetailCount() > 1) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the supplier name? Please note that doing so will delete all transaction details. Do you wish to proceed?") == true) {
                                        poPurchaseReturnController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        return;
                                    }
                                }
                            }

                            poJSON = poPurchaseReturnController.SearchSupplier(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfSupplier.setText("");
                                psSupplierId = "";
                                break;
                            }
                            psSupplierId = poPurchaseReturnController.Master().getSupplierId();
                            loadRecordMaster();
                            break;
                        case "tfReferenceNo":
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poPurchaseReturnController.getDetailCount() > 1) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the reference no? Please note that doing so will delete all transaction details. Do you wish to proceed?") == true) {
                                        poPurchaseReturnController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        return;
                                    }
                                }
                            }

                            poJSON = poPurchaseReturnController.SearchPOReceiving(lsValue, false);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfReferenceNo.setText("");
                            }
                            loadRecordMaster();
                            break;
                        case "tfPOReceivingNo":
                            if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                                if (poPurchaseReturnController.getDetailCount() > 1) {
                                    if (ShowMessageFX.YesNo(null, pxeModuleName,
                                            "Are you sure you want to change the po receiving no? Please note that doing so will delete all transaction details. Do you wish to proceed?") == true) {
                                        poPurchaseReturnController.removeDetails();
                                        loadTableDetail();
                                    } else {
                                        return;
                                    }
                                }
                            }

                            poJSON = poPurchaseReturnController.SearchPOReceiving(lsValue, true);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfPOReceivingNo.setText("");
                            }
                            loadRecordMaster();
                            break;
                        case "tfBarcode":
                            poJSON = poPurchaseReturnController.SearchBarcode(lsValue, pnDetail);
                            lnRow = (int) poJSON.get("row");
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                if (pnDetail != lnRow) {
                                    pnDetail = lnRow;
                                    loadRecordDetail();
                                    tfReturnQuantity.requestFocus();
                                    return;
                                }
                                tfBarcode.setText("");
                                break;
                            }
                            loadTableDetail();

                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(event1 -> {
                                    tfReturnQuantity.requestFocus();
                                });
                                delay.play();
                            });
                            break;
                        case "tfDescription":
                            poJSON = poPurchaseReturnController.SearchDescription(lsValue, pnDetail);
                            lnRow = (int) poJSON.get("row");
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                if (pnDetail != lnRow) {
                                    pnDetail = lnRow;
                                    loadRecordDetail();
                                    tfReturnQuantity.requestFocus();
                                    return;
                                }
                                tfDescription.setText("");
                                break;
                            }
                            loadTableDetail();
                            Platform.runLater(() -> {
                                PauseTransition delay = new PauseTransition(Duration.seconds(0.50));
                                delay.setOnFinished(event1 -> {
                                    tfReturnQuantity.requestFocus();
                                });
                                delay.play();
                            });
                            break;
                    }
                    break;
                default:
                    break;
            }
        } catch (GuanzonException | SQLException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initTextFields() {
        JFXUtil.setFocusListener(txtMaster_Focus, tfSupplier, tfReferenceNo, tfPOReceivingNo);
        JFXUtil.setFocusListener(txtArea_Focus, taRemarks);
        JFXUtil.setFocusListener(txtDetail_Focus, tfBarcode, tfDescription, tfReturnQuantity);

        JFXUtil.setKeyPressedListener(this::txtField_KeyPressed, apBrowse, apMaster, apDetail);

        CustomCommonUtil.inputIntegersOnly(tfReceiveQuantity, tfReturnQuantity);
        CustomCommonUtil.inputDecimalOnly(tfCost);
    }

    ChangeListener<Boolean> datepicker_Focus = (observable, oldValue, newValue) -> {
        poJSON = new JSONObject();
        poJSON.put("result", "success");
        poJSON.put("message", "success");
        try {

            if (!newValue) { // Lost focus
                DatePicker datePicker = (DatePicker) ((javafx.beans.property.ReadOnlyBooleanProperty) observable).getBean();
                String lsID = datePicker.getId();
                String inputText = datePicker.getEditor().getText();
                LocalDate currentDate = LocalDate.now();
                LocalDate selectedDate = null;

                lastFocusedTextField = datePicker;
                previousSearchedTextField = null;

                JFXUtil.JFXUtilDateResult ldtResult = JFXUtil.processDate(inputText, datePicker);
                poJSON = ldtResult.poJSON;
                if ("error".equals(poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    loadRecordMaster();
                    return;
                }
                selectedDate = ldtResult.selectedDate;

                String formattedDate = selectedDate.toString();

                switch (lsID) {
                    case "dpTransactionDate":
                        if (selectedDate == null) {
                            break;
                        }
                        if (selectedDate.isAfter(currentDate)) {
                            poJSON.put("result", "error");
                            poJSON.put("message", "Future dates are not allowed.");

                        } else {
                            poPurchaseReturnController.Master().setTransactionDate((SQLUtil.toDate(formattedDate, "yyyy-MM-dd")));
                        }
                        break;
                    default:

                        break;
                }
                datePicker.getEditor().setText(formattedDate);
                if ("error".equals((String) poJSON.get("result"))) {
                    ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                    // datePicker.requestFocus();
                }
                Platform.runLater(() -> {
                    loadRecordMaster();
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void initDatePickers() {
        JFXUtil.setDatePickerFormat(dpTransactionDate);
        JFXUtil.setDatePickerNextFocusByEnter(dpTransactionDate);
        JFXUtil.setFocusListener(datepicker_Focus, dpTransactionDate);

    }

    public void initDetailsGrid() {
        JFXUtil.setColumnCenter(tblRowNoDetail, tblReceiveQuantityDetail, tblReturnQuantityDetail);
        JFXUtil.setColumnLeft(tblBarcodeDetail, tblDescriptionDetail);
        JFXUtil.setColumnRight(tblCostDetail, tblTotalDetail);

        JFXUtil.setColumnsIndexAndDisableReordering(tblViewDetails);

        filteredDataDetail = new FilteredList<>(details_data, b -> true);

        SortedList<ModelPurchaseOrderReturn_Detail> sortedData = new SortedList<>(filteredDataDetail);
        sortedData.comparatorProperty().bind(tblViewDetails.comparatorProperty());
        tblViewDetails.setItems(sortedData);
    }

    public void clearTextFields() {
        previousSearchedTextField = null;
        lastFocusedTextField = null;
        dpTransactionDate.setValue(null);

        JFXUtil.clearTextFields(apMaster, apDetail);

        loadRecordMaster();
        loadTableDetail();
    }

    public void loadRecordSearch() {
        try {
            lblSource.setText(poPurchaseReturnController.Master().Company().getCompanyName() + " - " + poPurchaseReturnController.Master().Industry().getDescription());
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }
    }

    public void loadRecordDetail() {
        try {
            if (pnDetail < 0 || pnDetail > poPurchaseReturnController.getDetailCount() - 1) {
                return;
            }
            boolean lbDisable = poPurchaseReturnController.Detail(pnDetail).getEditMode() == EditMode.ADDNEW;
            JFXUtil.setDisabled(!lbDisable, tfBarcode, tfDescription);
            if (lbDisable) {
                while (JFXUtil.isTextFieldContainsStyleClass("DisabledTextField", tfBarcode, tfDescription)) {
                    JFXUtil.AddStyleClass("DisabledTextField", tfBarcode, tfDescription);
                }
            } else {
                JFXUtil.RemoveStyleClass("DisabledTextField", tfBarcode, tfDescription);
            }

            tfBarcode.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().getBarCode());
            tfDescription.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().getDescription());
            tfBrand.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Brand().getDescription());
            tfModel.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Model().getDescription());
            tfColor.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Color().getDescription());
            tfInventoryType.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().InventoryType().getDescription());
            tfMeasure.setText(poPurchaseReturnController.Detail(pnDetail).Inventory().Measure().getDescription());

            tfCost.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReturnController.Detail(pnDetail).getUnitPrce()));
            tfReceiveQuantity.setText(String.valueOf(poPurchaseReturnController.getReceiveQty(pnDetail)));
            tfReturnQuantity.setText(String.valueOf(poPurchaseReturnController.Detail(pnDetail).getQuantity()));

            JFXUtil.updateCaretPositions(apDetail);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
        }

    }

    public void loadRecordMaster() {
        boolean lbDisable = pnEditMode == EditMode.ADDNEW;
        if (!lbDisable) {
            JFXUtil.AddStyleClass("DisabledTextField", tfSupplier, tfReferenceNo, tfPOReceivingNo);
        } else {
            while (JFXUtil.isTextFieldContainsStyleClass("DisabledTextField", tfSupplier, tfReferenceNo, tfPOReceivingNo)) {
                JFXUtil.RemoveStyleClass("DisabledTextField", tfSupplier, tfReferenceNo, tfPOReceivingNo);
            }
        }

        JFXUtil.setDisabled(!lbDisable, tfSupplier, tfReferenceNo, tfPOReceivingNo);

        try {

            Platform.runLater(() -> {
                boolean lbPrintStat = pnEditMode == EditMode.READY;
                String lsActive = poPurchaseReturnController.Master().getTransactionStatus();
                String lsStat = "UNKNOWN";
                switch (lsActive) {
                    case PurchaseOrderReturnStatus.POSTED:
                        lsStat = "POSTED";
                        break;
                    case PurchaseOrderReturnStatus.PAID:
                        lsStat = "PAID";
                        break;
                    case PurchaseOrderReturnStatus.CONFIRMED:
                        lsStat = "CONFIRMED";
                        break;
                    case PurchaseOrderReturnStatus.OPEN:
                        lsStat = "OPEN";
                        break;
                    case PurchaseOrderReturnStatus.RETURNED:
                        lsStat = "RETURNED";
                        break;
                    case PurchaseOrderReturnStatus.VOID:
                        lsStat = "VOIDED";
                        lbPrintStat = false;
                        break;
                    case PurchaseOrderReturnStatus.CANCELLED:
                        lsStat = "CANCELLED";
                        break;
                    default:
                        lsStat = "UNKNOWN";
                        break;

                }
                lblStatus.setText(lsStat);
                JFXUtil.setButtonsVisibility(lbPrintStat, btnPrint);
            });

            poPurchaseReturnController.computeFields();

            // Transaction Date
            tfTransactionNo.setText(poPurchaseReturnController.Master().getTransactionNo());
            String lsTransactionDate = CustomCommonUtil.formatDateToShortString(poPurchaseReturnController.Master().getTransactionDate());
            dpTransactionDate.setValue(CustomCommonUtil.parseDateStringToLocalDate(lsTransactionDate, "yyyy-MM-dd"));

            tfSupplier.setText(poPurchaseReturnController.Master().Supplier().getCompanyName());
            tfReferenceNo.setText(poPurchaseReturnController.Master().PurchaseOrderReceivingMaster().getReferenceNo());
            tfPOReceivingNo.setText(poPurchaseReturnController.Master().getSourceNo());
            taRemarks.setText(poPurchaseReturnController.Master().getRemarks());

            tfTotal.setText(CustomCommonUtil.setIntegerValueToDecimalFormat(Double.valueOf(poPurchaseReturnController.Master().getTransactionTotal().doubleValue())));

            JFXUtil.updateCaretPositions(apMaster);
        } catch (SQLException | GuanzonException ex) {
            Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
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
                if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !poPurchaseReturnController.Detail(pnDetail).getStockId().equals("")) {
                    tfReturnQuantity.requestFocus();
                } else {
                    tfBarcode.requestFocus();
                }
                event.consume();
            }
        }
    }

    public void initTableOnClick() {
        tblViewDetails.setOnMouseClicked(event -> {
            if (details_data.size() > 0) {
                if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                    pnDetail = tblViewDetails.getSelectionModel().getSelectedIndex();
                    loadRecordDetail();
                    if (poPurchaseReturnController.Detail(pnDetail).getStockId() != null && !poPurchaseReturnController.Detail(pnDetail).getStockId().equals("")) {
                        tfReturnQuantity.requestFocus();
                    } else {
                        tfBarcode.requestFocus();
                    }
                }
            }
        });

        tblViewDetails.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        JFXUtil.adjustColumnForScrollbar(tblViewDetails, 2); // need to use computed-size in min-width of the column to work
    }

    public void loadTableDetail() {
        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewDetails.setPlaceholder(loadingPane);
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

                        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
                            lnCtr = poPurchaseReturnController.getDetailCount() - 1;
                            while (lnCtr >= 0) {
                                if (poPurchaseReturnController.Detail(lnCtr).getStockId() == null || poPurchaseReturnController.Detail(lnCtr).getStockId().equals("")) {
                                    poPurchaseReturnController.Detail().remove(lnCtr);
                                }
                                lnCtr--;
                            }

                            if ((poPurchaseReturnController.getDetailCount() - 1) >= 0) {
                                if (poPurchaseReturnController.Detail(poPurchaseReturnController.getDetailCount() - 1).getStockId() != null && !poPurchaseReturnController.Detail(poPurchaseReturnController.getDetailCount() - 1).getStockId().equals("")) {
                                    poPurchaseReturnController.AddDetail();
                                }
                            }

                            if ((poPurchaseReturnController.getDetailCount() - 1) < 0) {
                                poPurchaseReturnController.AddDetail();
                            }
                        }

                        double lnTotal = 0.0;
                        for (lnCtr = 0; lnCtr < poPurchaseReturnController.getDetailCount(); lnCtr++) {
                            try {
                                lnTotal = poPurchaseReturnController.Detail(lnCtr).getUnitPrce().doubleValue() * poPurchaseReturnController.Detail(lnCtr).getQuantity().intValue();
                            } catch (Exception e) {
                            }

                            details_data.add(
                                    new ModelPurchaseOrderReturn_Detail(String.valueOf(lnCtr + 1),
                                            String.valueOf(poPurchaseReturnController.Detail(lnCtr).Inventory().getBarCode()),
                                            String.valueOf(poPurchaseReturnController.Detail(lnCtr).Inventory().getDescription()),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(poPurchaseReturnController.Detail(lnCtr).getUnitPrce())),
                                            String.valueOf(poPurchaseReturnController.getReceiveQty(lnCtr)),
                                            String.valueOf(poPurchaseReturnController.Detail(lnCtr).getQuantity()),
                                            String.valueOf(CustomCommonUtil.setIntegerValueToDecimalFormat(lnTotal)) //identify total
                                    ));
                        }

                        if (pnDetail < 0 || pnDetail
                                >= details_data.size()) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                tblViewDetails.getSelectionModel().select(0);
                                tblViewDetails.getFocusModel().focus(0);
                                pnDetail = tblViewDetails.getSelectionModel().getSelectedIndex();
                                loadRecordDetail();
                            }
                        } else {
                            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
                            tblViewDetails.getSelectionModel().select(pnDetail);
                            tblViewDetails.getFocusModel().focus(pnDetail);
                            loadRecordDetail();
                        }
                        loadRecordMaster();
                    } catch (SQLException | GuanzonException | CloneNotSupportedException ex) {
                        Logger.getLogger(PurchaseOrderReturn_EntryController.class.getName()).log(Level.SEVERE, MiscUtil.getException(ex), ex);
                    }

                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewDetails.setPlaceholder(placeholderLabel);
                } else {
                    tblViewDetails.toFront();
                }
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewDetails.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background
    }

    private void initButton(int fnValue) {
        boolean lbShow = (fnValue == EditMode.ADDNEW || fnValue == EditMode.UPDATE);
        boolean lbShow2 = fnValue == EditMode.READY;
        boolean lbShow3 = (fnValue == EditMode.READY || fnValue == EditMode.UNKNOWN);

        // Manage visibility and managed state of other buttons
        JFXUtil.setButtonsVisibility(!lbShow, btnNew);
        JFXUtil.setButtonsVisibility(lbShow, btnSearch, btnSave, btnCancel);
        JFXUtil.setButtonsVisibility(lbShow2, btnUpdate, btnPrint, btnHistory);
        JFXUtil.setButtonsVisibility(lbShow3, btnBrowse, btnClose);

//        apMaster.setDisable(!lbShow);
        JFXUtil.setDisabled(!lbShow, dpTransactionDate, taRemarks, apDetail);

        switch (poPurchaseReturnController.Master().getTransactionStatus()) {
            case PurchaseOrderReturnStatus.POSTED:
            case PurchaseOrderReturnStatus.PAID:
                JFXUtil.setButtonsVisibility(false, btnUpdate);
                break;
            case PurchaseOrderReturnStatus.VOID:
            case PurchaseOrderReturnStatus.CANCELLED:
                JFXUtil.setButtonsVisibility(false, btnUpdate, btnPrint);
                break;
        }
    }

}
