/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.rmj.marketplace.model.ScreenInterface;
//import org.rmj.appdriver.GRider;
//import org.rmj.appdriver.SQLUtil;

/**
 *
 * @author user
 */
public class FXMLDocumentController implements Initializable {
    private GRider oApp;
    private static ToggleButton[] navButtons;
    private static Tooltip[] navTooltip;
    private String lsTitle = "";
    @FXML
    private Pane btnMin;
    @FXML
    private Pane btnClose;
    @FXML
    private StackPane workingSpace;
    @FXML
    private VBox nav_bar;
    @FXML
    private Button drawer;
    @FXML
    private ToggleButton btnItemManagement;
    @FXML
    private ToggleButton btnOrder;
    @FXML
    private ToggleButton btnClient;
    @FXML
    private ToggleButton btnQA;
    @FXML
    private ToggleButton btnDashboard;
    @FXML
    private ToggleButton btnPickup;
    @FXML
    private ToggleButton btnWayBill;
    @FXML
    private ToggleButton btnRatings;
    @FXML
    private ToggleButton btnReports;
    @FXML
    private ToggleButton btnParameters;
    @FXML
    private FontAwesomeIconView drawer_icon;
    public static ToggleGroup drawer_button;
    private TranslateTransition openNav;
    private TranslateTransition closeNav;
    private TranslateTransition closeFastNav;
    @FXML
    private TreeView tvChild;
   @FXML
    private AnchorPane child_sidebar;
    public static void setNavButtonsSelected(int pnValue){
        navButtons[pnValue].setSelected(true);
    }
    AnchorPane item_management,order_processing,client_info,question_answer;
    @FXML
    private AnchorPane MainAnchor;
    @FXML
    private BorderPane main_container;
    @FXML
    private StackPane top_navbar;
    @FXML
    private Pane hamburger;
    @FXML
    private StackPane hamburger__inner;
    @FXML
    private Label DateAndTime;
    @FXML
    private Pane view;
    @FXML
    private Label AppUser;
    @FXML
    private Label AppDepartment;
    @FXML
    private void drawerClick(MouseEvent event) {  
//      drawerOpen();
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        getTime();
//        getEmployee();
//        setScene(loadAnimate("/org/rmj/marketplace/view/Dashboard.fxml"));
//        setScene(loadAnimate("/org/rmj/marketplace/view/MainDashboard.fxml"));
        openNav = new TranslateTransition(Duration.millis(100), child_sidebar);
//        openNav.setToX(child_sidebar.getTranslateX()-child_sidebar.getWidth());
        closeNav = new TranslateTransition(Duration.millis(100), child_sidebar);
        closeFastNav = new TranslateTransition(Duration.millis(.1), child_sidebar);
        initToggleGroup();
        openDrawerAlignment();
    } 
    private void initToggleGroup(){
        drawer_button = new ToggleGroup();
        navButtons = new ToggleButton[]{
            btnDashboard,
            btnItemManagement,
            btnOrder,  
            btnWayBill,
            btnPickup,
            btnClient,
            btnQA,
            btnRatings,
            btnReports,
            btnParameters
        };
        for (ToggleButton navButton : navButtons) {
            navButton.setToggleGroup(drawer_button);
        }
        navButtons[0].setSelected(true);
//        drawerOpen();
    }
    
    private void tooltip (){
        navTooltip = new Tooltip[]{
            new Tooltip("DASHBOARD"),
            new Tooltip("ITEM MANAGEMENT"),
            new Tooltip("ORDER PROCESSING"),
            new Tooltip("WAY BILL"),
            new Tooltip("PICK UP"),
            new Tooltip("CLIENT INFO"),
            new Tooltip("QUESTION AND ANSWER"),
            new Tooltip("RATINGS AND REVIEWS"),
            new Tooltip("STANDARD REPORTS"),
            new Tooltip("PARAMETERS")
        };
        for(int t = 0; t < navTooltip.length; t++){
            hackTooltipStartTiming(navTooltip[t]);
            navButtons[t].setTooltip(navTooltip[t]);
        }
    }
    

    public void setGRider(GRider foValue) {
        oApp = foValue;
    }
    public void drawerOpen(String title) {
//        System.out.println("width = " + child_sidebar.getWidth());
        System.out.println("title = " + title);
        System.out.println("lsTitle = " + lsTitle);
        if(title.equalsIgnoreCase(lsTitle)){
            
            lsTitle = title;
            closeDrawerAlignment();
            closeNav.play();
        }else{
            lsTitle = title;
            openDrawerAlignment();
            openNav.play(); 
        }
//       if ((child_sidebar.getWidth()) <= 1.0 ) {
//            openDrawerAlignment();
//            openNav.play(); 
//        } else {
//            closeDrawerAlignment();
//            closeNav.play();
//        }
    }
   
    private void setScene(AnchorPane foPane){
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }
    @FXML
    private void switchItem(ActionEvent event) throws IOException {
        setNavButtonsSelected(1);
        child02();
        drawerOpen("ItemManagement");
//        setScene(loadAnimate("/org/rmj/marketplace/view/ItemManagement.fxml"));
    }
//    
    @FXML
    private void switchDashboard(ActionEvent event) throws IOException {
        setNavButtonsSelected(0);
        child01();
        drawerOpen("Dashbord");
//        setScene(loadAnimate("/org/rmj/marketplace/view/Dashboard.fxml"));
//        setScene(loadAnimate("/org/rmj/marketplace/view/MainDashboard.fxml"));
    }
    
    
    @FXML
    private void handleButtonCloseClick(MouseEvent event) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleButtonMinimizeClick(MouseEvent event) {
        Stage stage = (Stage) btnMin.getScene().getWindow();
        stage.setIconified(true);
    }
   
   
//   private AnchorPane loadAnimate(String fsFormName){
//       
//        ScreenInterface fxObj = getController(fsFormName);
//        fxObj.setGRider(oApp);
//        
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(fxObj.getClass().getResource(fsFormName));
//        fxmlLoader.setController(fxObj);      
//   
//        System.out.println(getController(fsFormName));
//        AnchorPane root;
//        try {
//            root = (AnchorPane) fxmlLoader.load();
//            FadeTransition ft = new FadeTransition(Duration.millis(1500));
//            ft.setNode(root);
//            ft.setFromValue(1);
//            ft.setToValue(1);
//            ft.setCycleCount(1);
//            ft.setAutoReverse(false);
//            ft.play();
//
//            return root;
//        } catch (IOException ex) {
//            System.err.println(ex.getMessage());
//        }
//        return null;
//    }
   public void openDrawerAlignment(){
        for (ToggleButton navButton : navButtons) {
            navButton.setTooltip(null);
            navButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            navButton.setAlignment(Pos.BASELINE_LEFT);
        }
        child_sidebar.setVisible(true);
        child_sidebar.setPrefWidth(190);
        
        System.out.println("openDrawerAlignment = " + child_sidebar.getWidth());
   }
   
   public void closeDrawerAlignment(){
        for (ToggleButton navButton : navButtons) {
            navButton.setTooltip(null);
            navButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            navButton.setAlignment(Pos.BASELINE_LEFT);
        }
        child_sidebar.setVisible(false);
        child_sidebar.setPrefWidth(0);
        
        System.out.println("closeDrawerAlignment = " + child_sidebar.getWidth());
        tooltip();
   }
   
   private void getTime(){
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {            
        Calendar cal = Calendar.getInstance();
        int second = cal.get(Calendar.SECOND);        
        String temp = "" + second;
        
        Date date = new Date();
        String strTimeFormat = "hh:mm:";
        String strDateFormat = "MMMM dd, yyyy";
        String secondFormat = "ss";
        
        DateFormat timeFormat = new SimpleDateFormat(strTimeFormat + secondFormat);
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        
        String formattedTime= timeFormat.format(date);
        String formattedDate= dateFormat.format(date);
        
        DateAndTime.setText(formattedDate+ " || " + formattedTime);
        }),
         new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        
    }
    public static void hackTooltipStartTiming(Tooltip tooltip) {
        try {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);
            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);
            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(100)));
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
    }
    
    
    private  void child01(){
        
        JSONArray laMaster, laDetail, laData;
        JSONObject loMaster, loDetail;
        
        laMaster = new JSONArray();
        
        laDetail = new JSONArray();
        loMaster = new JSONObject();
        
        
        loMaster = new JSONObject();
        loMaster.put("parent", "Sales InInquiry");
        laMaster.add(loMaster);
        

       
        loDetail = new JSONObject();
        loDetail.put("parent", "Reservation Payment");
        laDetail.add(loDetail);
        
        loDetail = new JSONObject();
        loDetail.put("parent", "Release");
        laDetail.add(loDetail);
        
        loDetail = new JSONObject();
        loDetail.put("parent", "Delivery");
        laDetail.add(loDetail);
        
        loMaster.put("parent", "Sales Reservation");
        loMaster.put("child", laDetail);          
        laMaster.add(loMaster);
        

        dissectJSON(laMaster.toJSONString()); 
    }
    
    private  void child02(){
        
        JSONArray laMaster, laDetail, laData;
        JSONObject loMaster, loDetail;
        
        laMaster = new JSONArray();
        
        laDetail = new JSONArray();
        loMaster = new JSONObject();
        
        
        loDetail = new JSONObject();
        loDetail.put("parent", "Sales Replacement");
        laDetail.add(loDetail);
        
        loDetail = new JSONObject();
        loDetail.put("parent", "Additional Give");
        laDetail.add(loDetail);
        
        loMaster.put("parent", "Sales");
        loMaster.put("child", laDetail);
        laMaster.add(loMaster);
        
        
//        laMaster = new JSONArray();
//        loMaster.put("parent", "Sales Delivery");      
//        laMaster.add(loMaster);
        dissectJSON(laMaster.toJSONString()); 
    }
    private void dissectJSON(String fsValue){
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

            for (int lnCtr = 0; lnCtr <= laMaster.size() - 1; lnCtr++){
                //convert the content to JSON
                loParent = (JSONObject) laMaster.get(lnCtr);
                TreeItem<String> parentnode = new TreeItem<>();
                
                parentnode.setValue((String) loParent.get("parent"));

                if (loParent.containsKey("child")){
                    if (loParent.get("child") instanceof String ||
                        loParent.get("child") instanceof Double ||
                        loParent.get("child") instanceof Integer){                        
                        parentnode.setValue((String) loParent.get("child"));
                    } else {
                        laDetail = (JSONArray) loParent.get("child");

                        //loop tayo sa laman ng array
                        for (int x = 0; x <= laDetail.size() - 1; x++){
                            loDetail = (JSONObject) laDetail.get(x);
                            
                            TreeItem<String> child = new TreeItem<>();
                            
                            if (loDetail.containsKey("link")){                                
                                child.setValue((String) loDetail.get("parent"));
                            } else {
                                child.setValue((String) loDetail.get("parent"));
                            }
                            
                            if (loDetail.containsKey("child")){
                                 
                                if (loDetail.get("child") instanceof String ||
                                    loDetail.get("child") instanceof Double ||
                                    loDetail.get("child") instanceof Integer){
                                    child.setValue(String.valueOf(loDetail.get("child")));
                                } else {
                                    laDetail2 = (JSONArray) loDetail.get("child");
                                    TreeItem<String> detail = new TreeItem<>();
                                    for (int y = 0; y <= laDetail2.size() - 1; y++){
                                        TreeItem<String> subdetail = new TreeItem<>();
                                        if (laDetail2.get(y) instanceof String ||
                                            laDetail2.get(y) instanceof Double ||
                                            laDetail2.get(y) instanceof Integer){
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
                    if(newValue.isLeaf()){
                        System.out.println(newValue.getValue());
                    }
                }

            });
            
        } catch (org.json.simple.parser.ParseException ex) {
//            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
}
