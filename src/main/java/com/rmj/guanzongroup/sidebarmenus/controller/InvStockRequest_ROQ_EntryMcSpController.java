/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvOrderDetail;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvOrderInformation;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelInvStockRequest;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrder;
import org.guanzon.cas.inv.warehouse.model.Model_Inv_Stock_Request_Detail;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class InvStockRequest_ROQ_EntryMcSpController implements Initializable, ScreenInterface{
    private GRiderCAS poApp;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    private InvWarehouseControllers invRequestController;
    private LogWrapper logWrapper;
    private int pnEditMode;
    private int pnTblInvDetailRow = -1;
    private JSONObject loJSON = new JSONObject();
    
    @FXML
    private AnchorPane AnchorMain; //used for getStage
    @FXML
    private String psFormName = "Inv Stock Request ROQ Entry Mc Sp";
    @FXML
    private TableView <ModelInvOrderInformation> tblViewModelInformation;
    private TableColumn <ModelInvOrderInformation, String> tblBrandDetail, tblBarcodeDetail, 
            tblDescription, tblSize, tblMeasurement, tblCategory, tblMinLevel, tblMaxLevel, tblClass, tblQOH, tblOnTransit, tblReserve, tblBackOrder, tblAMC, tblROQ, tblOrder;       
    private TableView <ModelInvStockRequest> tblViewStockRequest;
    private TableColumn <ModelInvOrderInformation, String> tblNoOI, tblBarcodeOI, tblDescriptionOI, 
            tblBrandOI, tblClassOI, tblQOHOI, tblAMCOI,tblROQOI, tblOrderOI;
    
    @Override
    public void setGRider(GRiderCAS foValue) {
        poApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
        psIndustryID = fsValue;
    }

    @Override
    public void setCompanyID(String fsValue) {
        psCompanyID = fsValue;
    }

    @Override
    public void setCategoryID(String fsValue) {
        psCategoryID = fsValue;
    }
    
    public Stage getStage() {
        return (Stage) AnchorMain.getScene().getWindow();        
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        invRequestController = new InvWarehouseControllers(poApp, logWrapper);
        JSONObject loJSON = new JSONObject();
        loJSON = invRequestController.StockRequest().InitTransaction();
//        inventoryClassification();
//        initTableStockRequest();
    }
//    
//    //init functions
//    private void initTableStockRequest(){
//        if (pnEditMode == EditMode.ADDNEW || pnEditMode == EditMode.UPDATE) {
//            tblViewStockRequest.setEditable(true);
//        } else {
//            tblViewStockRequest.setEditable(false);
//        }
//        
//        
//    }
    //functions
//    private void inventoryClassification(){
//        try {
//            System.out.print( invRequestController.StockRequest().Detail(pnTblInvDetailRow).InvMaster().getInventoryClassification());
//            System.out.print("classification: "+invRequestController.StockRequest().Detail(pnTblInvDetailRow).getClassification());
//            //invRequestController.StockRequest().Detail(pnTblInvDetailRow).InvMaster().
//        } catch (Exception e) {
//        }
//    }
    
}

