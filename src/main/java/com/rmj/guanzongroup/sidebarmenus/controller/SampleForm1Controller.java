/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.cas.parameter.Barangay;
import org.json.simple.JSONObject;

public class SampleForm1Controller implements Initializable,ScreenInterface {
    private final String pxeModuleName = "Sample Form 1";
    private GRider oApp;
    
    private int pnEditMode;  
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnUpdate;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnDelItem;
    @FXML
    private Button btnApprove;
    @FXML
    private Button btnCancelTrans;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnClose;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Barangay loBarangay = new Barangay(oApp, false);
        JSONObject loJSON = loBarangay.searchRecord("", false);        
        System.out.println(loBarangay.getModel().getBarangayName());
        System.out.println(loBarangay.getModel().getTownId());
        System.out.println(loBarangay.getModel().getTownName());
        
        loJSON = loBarangay.searchBarangayWithStatus("", false);        
        System.out.println(loBarangay.getModel().getBarangayName());
        System.out.println(loBarangay.getModel().getTownId());
        System.out.println(loBarangay.getModel().getTownName());
    }    

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
}
