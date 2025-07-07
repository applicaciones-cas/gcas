/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.cas.inv.warehouse.services.InvWarehouseControllers;
import org.guanzon.cas.inv.warehouse.status.StockRequestStatus;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class InvRequest_HistoryMcGeneralController implements Initializable, ScreenInterface{
    @FXML
    private String psFormName = "Inv Stock Request History Mc General";
    
    @FXML
    private AnchorPane AnchorMain;
    
    private GRiderCAS poApp;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    private String psReferID = "";
    private String psTransID = "";
    private InvWarehouseControllers invRequestController;
    private LogWrapper logWrapper;
    private JSONObject poJSON;
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
        
        JSONObject loJSON = new JSONObject();
        
        try {
            invRequestController = new InvWarehouseControllers(poApp, logWrapper);
            invRequestController.StockRequest().setTransactionStatus(
                    StockRequestStatus.OPEN
                  + StockRequestStatus.CANCELLED
                  + StockRequestStatus.CONFIRMED
                  + StockRequestStatus.PROCESSED
                  + StockRequestStatus.VOID
            );
            loJSON = invRequestController.StockRequest().InitTransaction();
            
            
            Platform.runLater((() -> {
                invRequestController.StockRequest().Master().setIndustryId(psIndustryID);
                invRequestController.StockRequest().Master().setCompanyID(psCompanyID);
                //invRequestController.StockRequest().Detail().setCategoryCode(psCategoryID);
                //loadRecordSearch();
            }));
        } catch (Exception e) {
        }
       
    }
    
    private void handleButtonAction(ActionEvent event){
        try {
            poJSON = new JSONObject();
            String lsButton = ((Button) event.getSource()).getId();
            switch(lsButton){
                case "btnRetrieve":
                    //poJSON = invRequestController.StockRequest().SearchTransaction("", psTransID, psReferID);
            }
        } catch (Exception e) {
        }
    }
}
