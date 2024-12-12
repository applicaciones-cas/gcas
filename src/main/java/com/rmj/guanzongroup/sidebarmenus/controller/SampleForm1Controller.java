package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.cas.client.Client;
import org.guanzon.cas.client.Client_Institution_Contact;
import org.json.simple.JSONObject;

public class SampleForm1Controller implements Initializable,ScreenInterface {
    private final String pxeModuleName = "Sample Form 1";
    private GRider oApp;
    private JSONObject poJSON;
    
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
        Client_Institution_Contact loObject = new Client_Institution_Contact();
        loObject.setApplicationDriver(oApp);
        loObject.setWithParentClass(false);
        loObject.initialize();
        
        JSONObject loJSON = loObject.searchRecord("", false);        
        if ("success".equals((String) loJSON.get("result"))){
            System.out.println(loObject.getModel().getClientId());
        } else System.out.println("No record was selected.");
    }    

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
}
