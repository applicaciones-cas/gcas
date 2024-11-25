/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DashboardController1 implements Initializable {

    private GRider oApp;
    private Stage stage;
    private String stringMenu;
    private String lastClickedButton = "";
    String isSelected = "-fx-background-color: #FF8201;";    
    String notSelected = "-fx-background-color: #bfbfbf;";
    @FXML
    private AnchorPane MainAnchor, anchorIconMenu, anchorSubMenu;
    @FXML
    private BorderPane main_container;
    @FXML
    private StackPane top_navbar;
    @FXML
    private Pane btnMin;
    @FXML
    private TreeView tvChild;
    @FXML
    private Pane btnClose;
    @FXML
    private VBox nav_bar;
    @FXML
    private Label lblMaps;
    @FXML
    private Button btnDashboard, btnDashboard1;
    @FXML
    private ToggleButton btnDashboardx;
    @FXML
    private ToggleButton btnItemManagement;
    @FXML
    private ToggleButton btnOrder;
    @FXML
    private ToggleButton btnWayBill;
    @FXML
    private ToggleButton btnPickup;
    @FXML
    private ToggleButton btnClient;
    @FXML
    private ToggleButton btnQA;
    @FXML
    private ToggleButton btnRatings;
    @FXML
    private ToggleButton btnReports;
    @FXML
    private Label AppUser;
    @FXML
    private Label DateAndTime;
    @FXML
    private StackPane workingSpace;
    @FXML
    private Pane view;

    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initMenu();
        ClickButton();
        lblMaps.setText("");
    }

    /*Handle button click*/
    private void ClickButton() {
        btnDashboard.setOnAction(this::handleButtonAction);
        btnDashboard1.setOnAction(this::handleButtonAction);
    }

    private void handleButtonAction(ActionEvent event) {
        Object source = event.getSource();
        JSONObject poJSON;

        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            switch (clickedButton.getId()) {
                
                case "btnDashboard":
                    handleButtonClick("btnDashboard", this::dashboardMenu);
                    
                    break;
                case "btnDashboard1":
                    handleButtonClick("btnDashboard1", this::dashboardMenu1);
                    break;
            }
        }
    }

    private void handleButtonClick(String buttonId, Runnable menuAction) {
        if (anchorSubMenu.isVisible() && lastClickedButton.equals(buttonId)) {
            // Hide the menu if the same button is clicked while the menu is visible
            anchorSubMenu.setVisible(false);
            lastClickedButton = ""; // Reset the last clicked button
        } else {
            // Show the menu and execute the corresponding action
            anchorSubMenu.setVisible(true);
            menuAction.run();
            lastClickedButton = buttonId; // Update the last clicked button
        }
    }

    @FXML
    private void handleButtonMinimizeClick(MouseEvent event) {
    }

    @FXML
    private void handleButtonCloseClick(MouseEvent event) {
    }

    @FXML
    private void switchDashboard(ActionEvent event) {

    }

    @FXML
    private void switchItem(ActionEvent event) {
    }

    @FXML
    private void switchOrder(ActionEvent event) {
    }

    @FXML
    private void switchWayBill(ActionEvent event) {
    }

    @FXML
    private void switchPickup(ActionEvent event) {
    }

    @FXML
    private void switchClient(ActionEvent event) {
    }

    @FXML
    private void switchQA(ActionEvent event) {
    }

    @FXML
    private void switchRatings(ActionEvent event) {
    }

    @FXML
    private void switchReports(ActionEvent event) {
    }

    private void initMenu() {
        anchorSubMenu.setVisible(false);
    }

    private void dashboardMenu() {
        JSONArray laMaster, laDetail, laSubDetail;
        JSONObject loMaster, loDetail, loSubDetail;

        laMaster = new JSONArray();

        // Sales Inquiry
        loMaster = new JSONObject();
        loMaster.put("parent", "Sales Inquiry");
        laMaster.add(loMaster);

        // Sales Reservation with Subcategories
        laDetail = new JSONArray();

        // Reservation Payment (No subcategory)
        loDetail = new JSONObject();
        loDetail.put("parent", "Reservation Payment");
        laDetail.add(loDetail);

        // Release with Subcategories
        loDetail = new JSONObject();
        loDetail.put("parent", "Release");
        laSubDetail = new JSONArray();

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Release Document 1");
        laSubDetail.add(loSubDetail);

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Release Document 2");
        laSubDetail.add(loSubDetail);

        loDetail.put("child", laSubDetail); // Add subcategories to Release
        laDetail.add(loDetail);

        // Delivery with Subcategories
        loDetail = new JSONObject();
        loDetail.put("parent", "Delivery");
        laSubDetail = new JSONArray();

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Delivery Route 1");
        laSubDetail.add(loSubDetail);

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Delivery Route 2");
        laSubDetail.add(loSubDetail);

        loDetail.put("child", laSubDetail); // Add subcategories to Delivery
        laDetail.add(loDetail);

        // Add Sales Reservation as a parent with its children
        loMaster = new JSONObject();
        loMaster.put("parent", "Sales Reservation");
        loMaster.put("child", laDetail);
        laMaster.add(loMaster);

        // Pass JSON to dissectJSON
        dissectJSON(laMaster.toJSONString());
    }

    private void dashboardMenu1() {
        JSONArray laMaster, laDetail, laSubDetail;
        JSONObject loMaster, loDetail, loSubDetail;

        laMaster = new JSONArray();

        // Sales Inquiry
        loMaster = new JSONObject();
        loMaster.put("parent", "Sales Summary");
        laMaster.add(loMaster);

        // Sales Reservation with Subcategories
        laDetail = new JSONArray();

        // Reservation Payment (No subcategory)
        loDetail = new JSONObject();
        loDetail.put("parent", "Reservation Summary");
        laDetail.add(loDetail);

        // Release with Subcategories
        loDetail = new JSONObject();
        loDetail.put("parent", "Release");
        laSubDetail = new JSONArray();

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Release Document 1");
        laSubDetail.add(loSubDetail);

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Release Document 2");
        laSubDetail.add(loSubDetail);

        loDetail.put("child", laSubDetail); // Add subcategories to Release
        laDetail.add(loDetail);

        // Delivery with Subcategories
        loDetail = new JSONObject();
        loDetail.put("parent", "Delivery");
        laSubDetail = new JSONArray();

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Delivery Route 1");
        laSubDetail.add(loSubDetail);

        loSubDetail = new JSONObject();
        loSubDetail.put("parent", "Delivery Route 2");
        laSubDetail.add(loSubDetail);

        loDetail.put("child", laSubDetail); // Add subcategories to Delivery
        laDetail.add(loDetail);

        // Add Sales Reservation as a parent with its children
        loMaster = new JSONObject();
        loMaster.put("parent", "Sales Reservation");
        loMaster.put("child", laDetail);
        laMaster.add(loMaster);

        // Pass JSON to dissectJSON
        dissectJSON(laMaster.toJSONString());
    }

    private void dissectJSON(String fsValue) {
        JSONParser loParser = new JSONParser();

        //convert string to JSONArray
        JSONArray laMaster;
        try {
            laMaster = (JSONArray) loParser.parse(fsValue);

            JSONArray laDetail;
            JSONArray laDetail2;
            JSONObject loParent;
            JSONObject loDetail;
            //we know that content of the json array is always a json object
            //with parent and child keys
            TreeItem<String> root = new TreeItem<>("root");

            for (int lnCtr = 0; lnCtr <= laMaster.size() - 1; lnCtr++) {
                //convert the content to JSON
                loParent = (JSONObject) laMaster.get(lnCtr);
                TreeItem<String> parentnode = new TreeItem<>();

                parentnode.setValue((String) loParent.get("parent"));

                if (loParent.containsKey("child")) {
                    if (loParent.get("child") instanceof String
                            || loParent.get("child") instanceof Double
                            || loParent.get("child") instanceof Integer) {
                        parentnode.setValue((String) loParent.get("child"));
                    } else {
                        laDetail = (JSONArray) loParent.get("child");

                        //loop tayo sa laman ng array
                        for (int x = 0; x <= laDetail.size() - 1; x++) {
                            loDetail = (JSONObject) laDetail.get(x);

                            TreeItem<String> child = new TreeItem<>();

                            if (loDetail.containsKey("link")) {
                                child.setValue((String) loDetail.get("parent"));
                            } else {
                                child.setValue((String) loDetail.get("parent"));
                            }

                            if (loDetail.containsKey("child")) {

                                if (loDetail.get("child") instanceof String
                                        || loDetail.get("child") instanceof Double
                                        || loDetail.get("child") instanceof Integer) {
                                    child.setValue(String.valueOf(loDetail.get("child")));
                                } else {
                                    laDetail2 = (JSONArray) loDetail.get("child");
                                    TreeItem<String> detail = new TreeItem<>();
                                    for (int y = 0; y <= laDetail2.size() - 1; y++) {
                                        TreeItem<String> subdetail = new TreeItem<>();
                                        if (laDetail2.get(y) instanceof String
                                                || laDetail2.get(y) instanceof Double
                                                || laDetail2.get(y) instanceof Integer) {
                                            subdetail.setValue(String.valueOf(laDetail2.get(y)));
                                        }
                                        detail.getChildren().add(subdetail);
                                    }
                                    child.getChildren().addAll(detail.getChildren());
                                }
                            }
                            parentnode.getChildren().add(child);
                        }

                    }
                }
                root.getChildren().add(parentnode);
            }

            tvChild.setRoot(root);
            tvChild.setShowRoot(false);
            tvChild.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
                @Override
                public void changed(ObservableValue<? extends TreeItem<String>> observable,
                        TreeItem<String> oldValue, TreeItem<String> newValue) {
                    // newValue represents the selected itemTree
                    if (newValue.isLeaf()) {
                        System.out.println(newValue.getValue());
                    }
                }

            });

        } catch (org.json.simple.parser.ParseException ex) {
//            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}
