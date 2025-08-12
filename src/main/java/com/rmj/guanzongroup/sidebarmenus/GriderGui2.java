/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmj.guanzongroup.sidebarmenus;

import com.rmj.guanzongroup.sidebarmenus.controller.DashboardController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.guanzon.appdriver.base.GRiderCAS;
import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;
import org.guanzon.appdriver.base.MiscUtil;
import org.json.simple.JSONObject;
import org.junit.Assert;
import ph.com.guanzongroup.cas.sales.t1.SalesInquiry;
import ph.com.guanzongroup.cas.sales.t1.services.SalesControllers;

public class GriderGui2 extends Application {

    static GRiderCAS instance;
    static SalesControllers poSalesInquiryController;
    public final static String pxeMainFormTitle = "Computerized Accounting System";
    public final static String pxeMainForm = "/com/rmj/guanzongroup/sidebarmenus/views/Dashboard.fxml";
//    public final static String pxeStageIcon = "images/icon.png";
    public static GRiderCAS oApp;
    TextField txtTrigger = new TextField();

    @Override
    public void start(Stage stage) throws Exception {
        instance = MiscUtil.Connect();
        poSalesInquiryController = new SalesControllers(instance, null);
        poSalesInquiryController.SalesInquiry().InitTransaction(); // Initialize transaction
        txtTrigger.setPromptText("Press F3 : Search");

        // Add key event handler to trigger on F3
        txtTrigger.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F3) {
                testTransaction();
            }
        });

        // Layout
        StackPane root = new StackPane(txtTrigger);

        // Scene and Stage setup
        Scene scene = new Scene(root, 300, 200);
        stage.setTitle("F3 Test");
        stage.setScene(scene);
        stage.show();
    }

    // The method to be called on button click
    public void testTransaction() {
        String branchCd = instance.getBranchCode();
        JSONObject loJSON;
        try {
            poSalesInquiryController.SalesInquiry().Master().setClientType("1");
            loJSON = poSalesInquiryController.SalesInquiry().SearchClient("", false);
            if (!JFXUtil.isJSONSuccess(loJSON)) {
                ShowMessageFX.Information("", pxeMainFormTitle, JFXUtil.getJSONMessage(loJSON));
            } else {
                txtTrigger.setText(poSalesInquiryController.SalesInquiry().Master().Client().getCompanyName());
            }
        } catch (SQLException ex) {
            Logger.getLogger(TestSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(TestSearch.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }
}
