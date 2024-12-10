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
import org.guanzon.cas.client.Client_Master;
import org.guanzon.cas.parameter.Barangay;
import org.guanzon.cas.parameter.Country;
import org.guanzon.cas.parameter.Province;
import org.guanzon.cas.parameter.TownCity;
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
        Province loObject = new Province();
        loObject.setApplicationDriver(oApp);
        loObject.setWithParentClass(false);
        loObject.initialize();
        
        JSONObject loJSON = loObject.searchProvinceWithStatus("", false);        
        System.out.println(loObject.getModel().getProvinceId());
        System.out.println(loObject.getModel().getProvinceName());
    }    

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
}
