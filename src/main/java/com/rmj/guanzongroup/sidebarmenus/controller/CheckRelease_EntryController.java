/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.LogWrapper;
import ph.com.guanzongroup.cas.check.module.mnv.CheckRelease;
import ph.com.guanzongroup.cas.check.module.mnv.constant.CheckReleaseStatus;
import ph.com.guanzongroup.cas.check.module.mnv.services.CheckController;

/**
 *
 * @author User
 */
public class CheckRelease_EntryController implements Initializable, ScreenInterface{
    
    private GRiderCAS poApp;
    private LogWrapper poLogWrapper;
    private String psFormName = "Check Release Entry";
    private String psIndustryID;
    private Control lastFocusedControl;
    private CheckRelease poAppController;
    
    @FXML
    AnchorPane apMaster, apDetail, apCheckDettail, apTransaction;
    
    @FXML
    TextField tfSearchReceived, tfSearchTransNo, tfTransNo, tfReceivedBy, taRemarks, tfTotal, tfSearchCheckRef, tfPayee, tfParticular,
            tfCheckAmt, tfNote, tfSearchPayee, tfSearchCheck;
    
    @FXML
    DatePicker dpTransactionDate, dpCheckDate, dpCheckDtFrm, dpCheckDTTo;
    
    @FXML
    Button btnSearch, btnBrowse, btnNew, btnUpdate, btnSave, btnCancel, btnHistory, btnRetrieve, btnClose;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            poLogWrapper = new LogWrapper(psFormName, psFormName);
            poAppController = new CheckController(poApp, poLogWrapper).CheckRelease();
            poAppController.setTransactionStatus(CheckReleaseStatus.OPEN);

            //initlalize and validate transaction objects from class controller
            if (!isJSONSuccess(poAppController.initTransaction(), psFormName)) {
                unloadForm appUnload = new unloadForm();
                appUnload.unloadForm(apMainAnchor, poApp, psFormName);
            }

            //background thread
            Platform.runLater(() -> {
                poAppController.setTransactionStatus("0");
                //initialize logged in category
                poAppController.setIndustryID(psIndustryID);
                System.err.println("Initialize value : Industry >" + psIndustryID);

            });

        } catch (SQLException | GuanzonException e) {
            Logger.getLogger(CheckDeposit_EntryController.class.getName()).log(Level.SEVERE, null, e);
            poLogWrapper.severe(psFormName + " :" + e.getMessage());
        }
    }

    @Override
    public void setGRider(GRiderCAS foValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setIndustryID(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setCompanyID(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void setCategoryID(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
