/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelDeliveryAcceptance_Serial;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.F3;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.CommonUtils;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.cas.purchasing.controller.PurchaseOrderReceiving;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author Arsiela
 */
public class DeliveryAcceptance_SerialCarController implements Initializable, ScreenInterface {

    private GRiderCAS oApp;
    private JSONObject poJSON;
    int pnEntryNo = 0;
    int pnDetail = -1;
    private final String pxeModuleName = "Purchase Order Receiving Serial Car";
    static PurchaseOrderReceiving poPurchaseReceivingController;
    public int pnEditMode;

    private ObservableList<ModelDeliveryAcceptance_Serial> details_data = FXCollections.observableArrayList();

    @FXML
    private AnchorPane apBrowse, apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnOkay, btnClose;
    @FXML
    private TextField tfEngineNo, tfFrameNo, tfCSNo, tfPlateNo, tfLocation;
    @FXML
    private CheckBox cbApplyToAll;
    @FXML
    private TableView<ModelDeliveryAcceptance_Serial> tblViewDetail;
    @FXML
    private TableColumn tblRowNoDetail, tblEngineNoDetail, tblFrameNoDetail, tblConductionStickerNoDetail, tblPlateNoDetail, tblLocationDetail;
    private final Map<Integer, String> originalValues = new HashMap<>();

    public void setObject(PurchaseOrderReceiving foObject) {
        poPurchaseReceivingController = foObject;
    }

    public void setEntryNo(int entryNo) {
        pnEntryNo = entryNo;
    }

    private Stage getStage() {
        return (Stage) btnClose.getScene().getWindow();
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        initTextFields();
        initDetailsGrid();
        initTableOnClick();

        loadTableDetail();
    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();
        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            String lsButton = clickedButton.getId();
            switch (lsButton) {
                case "btnOkay":
                    //if the user clicked okay all rows must be fill up else remaining row will be allowed to remain empty.
                    //check for empty serial 1 || serial 2 is empty delete the excess row
                    poJSON = checkSerialNo(lsButton);
                    if ("error".equals((String) poJSON.get("result"))) {
                        ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                        return;
                    }

                    CommonUtils.closeStage(btnClose);
                case "btnClose":
                    //if the user clicked okay all rows must be fill up else remaining row will be allowed to remain empty.
                    //check for empty serial 1 || serial 2 is empty delete the excess row
                    poJSON = checkSerialNo(lsButton);
                    if ("error".equals((String) poJSON.get("result"))) {
                        return;
                    }

                    CommonUtils.closeStage(btnClose);
                    break;
                default:
                    ShowMessageFX.Warning(null, pxeModuleName, "Button with name " + lsButton + " not registered.");
                    break;
            }
        }
    }

    private JSONObject checkSerialNo(String lsButton) {
        poJSON = new JSONObject();
        int lnRow = 1;
        String lsMessage = "";
        for (int lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount() - 1; lnCtr++) {
            if (poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getEntryNo() == pnEntryNo) {
                if (poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01() == null || poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01().equals("")) {
                    poJSON.put("result", "error");
                    lsMessage = "Engine No at row " + lnRow + " cannot be empty.";

                }
                if (poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02() == null || poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02().equals("")) {
                    poJSON.put("result", "error");
                    lsMessage = "Frame No at row " + lnRow + " cannot be empty.";
                }
                if (poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getLocationId() == null || poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getLocationId().equals("")) {
                    poJSON.put("result", "error");
                    lsMessage = "Location No at row " + lnRow + " cannot be empty.";
                }

                if (lsButton.equals("btnOkay")) {
                    if ("error".equals((String) poJSON.get("result"))) {
                        poJSON.put("message", lsMessage);
                        return poJSON;
                    }
                } else {
                    if (ShowMessageFX.OkayCancel(null, pxeModuleName,
                            "There are still remaining rows that have not been filled. Are you sure you want to close without completing them?") == false) {
                        poJSON.put("result", "error");
                        return poJSON;
                    } else {
                        poJSON.put("result", "success");
                        return poJSON;
                    }
                }

                lnRow++;
            }
        }
        return poJSON;
    }

    @FXML
    private void cmdCheckBox_Click(ActionEvent event) {
        poJSON = new JSONObject();
        Object source = event.getSource();

        if (source instanceof CheckBox) {
            CheckBox checkbox = (CheckBox) source;
            boolean isChecked = checkbox.isSelected(); // Check if checked or unchecked
            String lsCheckBox = checkbox.getId();
            String lsLocation = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getLocationId();

            if (lsLocation == null || lsLocation.isEmpty()) {
                checkbox.setSelected(false);
                ShowMessageFX.Warning(null, pxeModuleName, "Location cannot be empty.");
                return;
            }

            if (lsCheckBox.equals("cbApplyToAll")) {
                if (isChecked) {
                    // Store original values before modifying
                    for (int lnCtr = 0; lnCtr < poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount(); lnCtr++) {
                        if (!originalValues.containsKey(lnCtr)) { // Store only once
                            originalValues.put(lnCtr, poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getLocationId());
                        }
                        poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).setLocationId(lsLocation);
                    }
                } else {
                    // Revert to original values when checkbox is unchecked
                    for (Map.Entry<Integer, String> entry : originalValues.entrySet()) {
                        poPurchaseReceivingController.PurchaseOrderReceivingSerialList(entry.getKey()).setLocationId(entry.getValue());
                    }
                    originalValues.clear(); // Clear stored values after reverting
                }
                loadTableDetail();
            }
        }
    }

    final ChangeListener<? super Boolean> txtDetail_Focus = (o, ov, nv) -> {
        poJSON = new JSONObject();
        TextField txtPersonalInfo = (TextField) ((ReadOnlyBooleanPropertyBase) o).getBean();
        String lsTxtFieldID = (txtPersonalInfo.getId());
        String lsValue = (txtPersonalInfo.getText() == null ? "" : txtPersonalInfo.getText());
        if (lsValue == null) {
            return;
        }

        if (pnDetail < 0) {
            return;
        }

        if (poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getEntryNo() != pnEntryNo) {
            return;
        }

        if (!nv) {
            /*Lost Focus*/
            switch (lsTxtFieldID) {
                case "tfEngineNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial01(lsValue);
                    break;
                case "tfFrameNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setSerial02(lsValue);
                    break;
                case "tfCSNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setConductionStickerNo(lsValue);
                    break;
                case "tfPlateNo":
                    poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setPlateNo(lsValue);
                    break;
                case "tfLocation":
                    if (lsValue.isEmpty()) {
                        poJSON = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).setLocationId("");
                    }
                    break;
            }
            Platform.runLater(() -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(0.10));
                delay.setOnFinished(event -> {
                    loadTableDetail();
                });
                delay.play();
            });
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
                        case "tfLocation":
                            /*search location*/
                            poJSON = poPurchaseReceivingController.SearchLocation(lsValue, false, pnDetail);
                            if ("error".equals(poJSON.get("result"))) {
                                ShowMessageFX.Warning(null, pxeModuleName, (String) poJSON.get("message"));
                                tfLocation.setText("");
                                break;
                            }
                            break;
                    }
                    loadRecordDetail();
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
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadRecordDetail() {
        try {
            if (pnDetail >= 0) {
                tfEngineNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial01());
                tfFrameNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getSerial02());
                tfCSNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getConductionStickerNo());
                tfPlateNo.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).getPlateNo());
                tfLocation.setText(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(pnDetail).Location().getDescription());
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryAcceptance_SerialCarController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(DeliveryAcceptance_SerialCarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadTableDetail() {

        // Setting data to table detail
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);
        tblViewDetail.setPlaceholder(loadingPane);
        progressIndicator.setVisible(true);

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;"); // Adjust the size as needed

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    poJSON = new JSONObject();
                    int lnCtr = 0;
                    int lnRow = 0;
                    String lsLocation = "";
                    details_data.clear();

                    try {
                        for (lnCtr = 0; lnCtr <= poPurchaseReceivingController.getPurchaseOrderReceivingSerialCount() - 1; lnCtr++) {
                            if (poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getEntryNo() == pnEntryNo) {
                                if (poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).Location().getDescription() != null) {
                                    lsLocation = poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).Location().getDescription();
                                }

                                details_data.add(
                                        new ModelDeliveryAcceptance_Serial(
                                                String.valueOf(lnRow + 1),
                                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial01()),
                                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getSerial02()),
                                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getConductionStickerNo()),
                                                String.valueOf(poPurchaseReceivingController.PurchaseOrderReceivingSerialList(lnCtr).getPlateNo()),
                                                String.valueOf(lsLocation),
                                                String.valueOf(lnCtr),
                                                String.valueOf(""),
                                                String.valueOf(""),
                                                String.valueOf("")
                                        ));
                                lsLocation = "";
                                lnRow++;
                            }
                        }

                        if (pnDetail < 0) {
                            if (!details_data.isEmpty()) {
                                /* FOCUS ON FIRST ROW */
                                tblViewDetail.getSelectionModel().select(0);
                                tblViewDetail.getFocusModel().focus(0);
                                ModelDeliveryAcceptance_Serial selectedItem = tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex());
                                pnDetail = Integer.valueOf(selectedItem.getIndex07());
                                System.out.println("set pndetail" + pnDetail);
                            }
                        } else {
                            TableView<ModelDeliveryAcceptance_Serial> tableView = tblViewDetail;
                            SelectionModel<ModelDeliveryAcceptance_Serial> selectionModel = tableView.getSelectionModel();
                            for (ModelDeliveryAcceptance_Serial item : tblViewDetail.getItems()) {
                                // Check if the item matches the value of pnDetail
                                if (item.getIndex07() != null && Integer.valueOf(item.getIndex07()) == pnDetail) {
                                    selectionModel.select(item);
                                    tblViewDetail.getFocusModel().focus(pnDetail);
//                                    tableView.scrollTo(item);
                                    break;
                                }
                            }
                        }

                        loadRecordDetail();

                    } catch (SQLException ex) {
                        Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (GuanzonException ex) {
                        Logger.getLogger(DeliveryAcceptance_EntryController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });

                return null;
            }

            @Override
            protected void succeeded() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewDetail.setPlaceholder(placeholderLabel);
                } else {
                    tblViewDetail.toFront();
                }
                progressIndicator.setVisible(false);

            }

            @Override
            protected void failed() {
                if (details_data == null || details_data.isEmpty()) {
                    tblViewDetail.setPlaceholder(placeholderLabel);
                }
                progressIndicator.setVisible(false);
            }

        };
        new Thread(task).start(); // Run task in background

    }

    public void adjustLastColumnForScrollbar(TableView<?> tableView) {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (!(newSkin instanceof TableViewSkin<?>)) {
                return;
            }

            TableViewSkin<?> skin = (TableViewSkin<?>) newSkin;
            VirtualFlow<?> flow = skin.getChildren().stream()
                    .filter(node -> node instanceof VirtualFlow<?>)
                    .map(node -> (VirtualFlow<?>) node)
                    .findFirst().orElse(null);

            if (flow == null) {
                return;
            }

            ScrollBar vScrollBar = flow.getChildrenUnmodifiable().stream()
                    .filter(node -> node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == Orientation.VERTICAL)
                    .map(node -> (ScrollBar) node)
                    .findFirst().orElse(null);

            if (vScrollBar == null || tableView.getColumns().isEmpty()) {
                return;
            }

            TableColumn<?, ?> lastColumn = (TableColumn<?, ?>) tableView.getColumns()
                    .get(tableView.getColumns().size() - 1);

            vScrollBar.visibleProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    double scrollBarWidth = newValue ? vScrollBar.getWidth() : 0;
                    double remainingWidth = tableView.getWidth() - scrollBarWidth;

                    double totalFixedWidth = tableView.getColumns().stream()
                            .filter(col -> col != lastColumn)
                            .mapToDouble(col -> ((TableColumn<?, ?>) col).getWidth())
                            .sum();

                    double newWidth = Math.max(0, remainingWidth - totalFixedWidth);
                    lastColumn.setPrefWidth(newWidth - 5);
                });
            });
        });
    }

    private int moveToNextRow(TableView table, TablePosition focusedCell) {
        int nextRow = (focusedCell.getRow() + 1) % table.getItems().size();
        table.getSelectionModel().select(nextRow);
        return nextRow;
    }

    private int moveToPreviousRow(TableView table, TablePosition focusedCell) {
        int previousRow = (focusedCell.getRow() - 1 + table.getItems().size()) % table.getItems().size();
        table.getSelectionModel().select(previousRow);
        return previousRow;
    }

    private void tableKeyEvents(KeyEvent event) {
        if (details_data.size() > 0) {
            TableView<?> currentTable = (TableView<?>) event.getSource();
            TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
            if (focusedCell != null) {
                switch (event.getCode()) {
                    case TAB:
                    case DOWN:
                        pnDetail = moveToNextRow(currentTable, focusedCell);
                        break;
                    case UP:
                        pnDetail = moveToPreviousRow(currentTable, focusedCell);
                        break;

                    default:
                        break;
                }
                loadRecordDetail();
                tfEngineNo.requestFocus();
                event.consume();
            }
        }
    }

    public void initTableOnClick() {
        tblViewDetail.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {  // Detect single click (or use another condition for double click)
                ModelDeliveryAcceptance_Serial selectedItem = tblViewDetail.getItems().get(tblViewDetail.getSelectionModel().getSelectedIndex());
                pnDetail = Integer.valueOf(selectedItem.getIndex07());
                loadRecordDetail();
                tfEngineNo.requestFocus();
            }
        });
        tblViewDetail.addEventFilter(KeyEvent.KEY_PRESSED, this::tableKeyEvents);
        adjustLastColumnForScrollbar(tblViewDetail);
    }

    public void initTextFields() {
        tfEngineNo.focusedProperty().addListener(txtDetail_Focus);
        tfFrameNo.focusedProperty().addListener(txtDetail_Focus);
        tfCSNo.focusedProperty().addListener(txtDetail_Focus);
        tfPlateNo.focusedProperty().addListener(txtDetail_Focus);
        tfLocation.focusedProperty().addListener(txtDetail_Focus);

        tfLocation.setOnKeyPressed(this::txtField_KeyPressed);

    }

    public void initDetailsGrid() {
        tblRowNoDetail.setStyle("-fx-alignment: CENTER;");
        tblEngineNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblFrameNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblConductionStickerNoDetail.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        tblPlateNoDetail.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 5;");
        tblLocationDetail.setStyle("-fx-alignment: CENTER 0 5 0 5;");

        tblRowNoDetail.setCellValueFactory(new PropertyValueFactory<>("index01"));
        tblEngineNoDetail.setCellValueFactory(new PropertyValueFactory<>("index02"));
        tblFrameNoDetail.setCellValueFactory(new PropertyValueFactory<>("index03"));
        tblConductionStickerNoDetail.setCellValueFactory(new PropertyValueFactory<>("index04"));
        tblPlateNoDetail.setCellValueFactory(new PropertyValueFactory<>("index05"));
        tblLocationDetail.setCellValueFactory(new PropertyValueFactory<>("index06"));

        tblViewDetail.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewDetail.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        tblViewDetail.setItems(details_data);
        tblViewDetail.autosize();
    }

}
