/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrder;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrderDetail;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.cas.purchasing.services.PurchaseOrderControllers;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class PurchaseOrder_ApprovalController implements Initializable, ScreenInterface {

    private GRiderCAS poApp;
    private PurchaseOrderControllers poPurchasingController;
    private String psFormName = "Purchase Order Approval";
    private LogWrapper logWrapper;
    private int pnEditMode;
    private JSONObject poJSON;
    unloadForm poUnload = new unloadForm();
    private ObservableList<ModelPurchaseOrder> poPurchaseOrder_Data = FXCollections.observableArrayList();
    private ObservableList<ModelPurchaseOrderDetail> poDetail_data = FXCollections.observableArrayList();
    private int pnTblPOMasterRow = -1;
    private int pnTblPODetailRow = 0;
    private int pnPURCHASE_ORDER_PAGE = 10;
    private TextField polastFocusedTextField;
    @FXML
    private AnchorPane apBrowse, AnchorMaster, AnchorDetails, AnchorMain;
    @FXML
    private AnchorPane apButton;
    @FXML
    private Button btnUpdate, btnSave, btnCancel, btnApprove, btnReturn, btnVoid, btnPrint,
            btnRetrieve, btnTransHistory, btnClose;
    @FXML
    private Label lblTransactionStatus;
    @FXML
    private TextField tfSearchIndustry, tfSearchCompany, tfSearchSupplier, tfSearchReferenceNo;
    @FXML
    private TextField tfTransactionNo, tfIndustry, tfCompany, tfSupplier, tfDestination, tfReferenceNo,
            tfTerm, tfDiscountRate, tfDiscountAmount, tfAdvancePRate, tfAdvancePAmount, tfTotalAmount;
    @FXML
    private TextField tfBarcode, tfDescription, tfBrand, tfModel, tfColor, tfCategory, tfInventoryType,
            tfMeasure, tfClass, tfAMC, tfROQ, tfRO, tfBO, tfQOH, tfCost, tfRequestQuantity, tfOrderQuantity;
    @FXML
    private CheckBox cbAdv;
    @FXML
    private TextArea taRemarks;
    @FXML
    private DatePicker dpTransactionDate, dpExpectedDlvrDate;
    @FXML
    private TableView<ModelPurchaseOrderDetail> tblVwOrderDetails;
    @FXML
    private TableColumn<ModelPurchaseOrderDetail, String> tblRowNoDetail, tblOrderNoDetail, tblBarcodeDetail, tblDescriptionDetail,
            tblCostDetail, tblROQDetail, tblRequestQuantityDetail, tblOrderQuantityDetail, tblTotalAmountDetail;
    @FXML
    private TableView<ModelPurchaseOrder> tblVwPurchaseOrder;
    @FXML
    private TableColumn<ModelPurchaseOrder, String> tblRowNo, tblBranchName, tblDate, tblReferenceNo, tblNoOfItems;
    @FXML
    private Pagination pagination;

    @Override
    public void setGRider(GRiderCAS foValue) {
        poApp = foValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
