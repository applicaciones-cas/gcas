/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import javafx.scene.layout.AnchorPane;
import org.json.simple.JSONObject;
/**
 *
 * @author User
 */
public class InvRequest_Roq_ConfirmationMcSpController implements Initializable, ScreenInterface{
    @FXML
    private AnchorPane AnchorMain;
    @FXML
    private String psFormName = "Inv Stock Request ROQ Confirmation Mc Sp";
    
    
    private GRiderCAS poApp;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    private InvWarehouseControllers invRequestController;
    private LogWrapper logWrapper;
    
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
       
    }
}
