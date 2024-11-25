package com.rmj.guanzongroup.sidebarmenus.controller;

import static com.rmj.guanzongroup.sidebarmenus.controller.FXMLDocumentController.setNavButtonsSelected;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DashboardController implements Initializable {

    private GRider oApp;
    private String lastClickedButton = "";
    private ToggleGroup toggleGroup;
    private static ToggleButton[] navButtons;    
    private static Tooltip[] navTooltip;
    private int targetTabIndex = -1;
    private int intIndex = -1;
    List<String> tabName = new ArrayList<>();
    String sformname = "";
    @FXML
    private TabPane tabpane;
    @FXML
    private AnchorPane anchorSubMenu,anchorSpace,MainAnchor;
    @FXML
    private TreeView<String> tvChild;
    @FXML
    StackPane workingSpace;
    @FXML
    private Button btnClose, btnMinimize;
    @FXML
    private ToggleButton btnDashboard, btnItemManagement, btnOrder, btnWayBill, btnPickup, btnClient, btnQA, btnRatings, btnReports;
    @FXML
    private Pane pane;
    @FXML
    private Label DateAndTime;
    
    @FXML
    private StackPane MainStack;
    @FXML
    private BorderPane main_container;

    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMenu();
        ToggleGroupControl();
        ClickButton();
        getTime();
        setPane();
//        btnDashboard.setSelected(false);
//        lblMaps.setText("");
    }

    public String SetTabTitle(String menuaction) {
        switch (menuaction) {
            /**/
            case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml":
                return "Sample Form 1";

            case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml":
                return "Sample Form 2";

            default:
                return null;
        }
    }

    public int checktabs(String tabtitle) {
        for (Tab tab : tabpane.getTabs()) {
            if (tab.getText().equals(tabtitle)) {
                tabpane.getSelectionModel().select(tab);
                return 0;
            }
        }
        return 1;
    }

    private int findTabIndex(String tabText) {
        ObservableList<Tab> tabs = tabpane.getTabs();
        for (int i = 0; i < tabs.size(); i++) {
            if (tabs.get(i).getText().equals(tabText)) {
                return i;
            }
        }
        return -1;
    }

    public void setPane(){
        pane.setOnMouseClicked(event -> {
            // Check if the click occurred on the tabs area (not the content area)
            System.out.println("pane clicked at: " + event.getSceneX() + ", " + event.getSceneY());

            // Hide the sub-menu
            anchorSubMenu.setVisible(false);

            // Assuming navButtons is an array or List of buttons in Java
            for (int i = 0; i < navButtons.length; i++) {
                navButtons[i].setSelected(false); // Set each button's selected state to false
            }

            // Perform other actions on click if needed
        });
    }
    public void setTabPane() {
        // set up the drag and drop listeners on the tab pane
        tabpane.setOnMouseClicked(event -> {
            // Check if the click occurred on the tabs area (not the content area)
            System.out.println("TabPane clicked at: " + event.getSceneX() + ", " + event.getSceneY());

            // Hide the sub-menu
            anchorSubMenu.setVisible(false);

            // Assuming navButtons is an array or List of buttons in Java
            for (int i = 0; i < navButtons.length; i++) {
                navButtons[i].setSelected(false); // Set each button's selected state to false
            }

            // Perform other actions on click if needed
        });

        tabpane.setOnDragDetected(event -> {
            Dragboard db = tabpane.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(tabpane.getSelectionModel().getSelectedItem().getText());
            db.setContent(content);
            event.consume();
        });

        tabpane.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
                event.consume();
            }
        });

        tabpane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasString()) {
                String tabText = db.getString();
                int draggedTabIndex = findTabIndex(tabText);
                double mouseX = event.getX();
                double mouseY = event.getY();
                Bounds headerBounds = tabpane.lookup(".tab-header-area").getBoundsInParent();
                Point2D mouseInScene = tabpane.localToScene(mouseX, mouseY);
                Point2D mouseInHeader = tabpane.sceneToLocal(mouseInScene);
                double tabHeaderHeight = tabpane.lookup(".tab-header-area").getBoundsInParent().getHeight();
                System.out.println("mouseY " + mouseY);
                System.out.println("tabHeaderHeight " + tabHeaderHeight);
                
                targetTabIndex = (int) (mouseX / 180);
                System.out.println("targetTabIndex " + targetTabIndex);
                if (mouseY < tabHeaderHeight) {
                    //if (headerBounds.contains(mouseInHeader)) {
                    System.out.println("mouseInHeader.getX() " + mouseInHeader.getX());
                    System.out.println("headerBounds.getWidth() " + headerBounds.getWidth());
                    System.out.println("tabsize " + tabpane.getTabs().size());
                    System.out.println("tabText " + tabText);
                    System.out.println("draggedTabIndex " + draggedTabIndex);

                    if (draggedTabIndex != targetTabIndex) {
                        Tab draggedTab = tabpane.getTabs().remove(draggedTabIndex);
                        if (targetTabIndex > tabpane.getTabs().size()) {
                            targetTabIndex = tabpane.getTabs().size();
                        }
                        tabpane.getTabs().add(targetTabIndex, draggedTab);
                        tabpane.getSelectionModel().select(draggedTab);
                        success = true;
                    }
                }
            }
            event.setDropCompleted(success);
            event.consume();
        });

        tabpane.setOnDragDone(event -> {
            event.consume();
        });

    }

    private ScreenInterface getController(String fsValue) {
        switch (fsValue) {
            case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml":
                return new SampleForm1Controller();
            case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml":
                return new SampleForm2Controller();
            default:
                return null;
        }
    }

    public TabPane loadAnimate(String fsFormName) {
        //set fxml controller class
        if (tabpane.getTabs().isEmpty()) {
            tabpane = new TabPane();
        }

        setTabPane();
        setPane();
        
        ScreenInterface fxObj = getController(fsFormName);
        fxObj.setGRider(oApp);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxObj.getClass().getResource(fsFormName));
        fxmlLoader.setController(fxObj);

        //Add new tab;
        Tab newTab = new Tab(SetTabTitle(fsFormName));
        newTab.setContent(new javafx.scene.control.Label("Content of Tab " + fsFormName));
        newTab.setContextMenu(createContextMenu(tabpane, newTab, oApp));
        // Attach a context menu to each tab
        tabName.add(SetTabTitle(fsFormName));

        // Save the list of tab IDs to the JSON file
        try {
            Node content = fxmlLoader.load();
            newTab.setContent(content);
            tabpane.getTabs().add(newTab);
            tabpane.getSelectionModel().select(newTab);

            newTab.setOnCloseRequest(event -> {
                if (showMessage()) {
                    tabName.remove(newTab.getText());

                    Tabclose();
                } else {
                    event.consume();
                }

            });

            newTab.setOnSelectionChanged(event -> {
                ObservableList<Tab> tabs = tabpane.getTabs();
                for (Tab tab : tabs) {
                    if (tab.getText().equals(newTab.getText())) {
                        tabName.remove(newTab.getText());
                        tabName.add(newTab.getText());
                        break;
                    }
                }
            });
            return (TabPane) tabpane;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Load Main Screen if no tab remain
    public void Tabclose() {
        int tabsize = tabpane.getTabs().size();
        if (tabsize == 0) {
            setScene(loadAnimateAnchor("Dashboard.fxml"));
        }
    }

    private void setScene(AnchorPane foPane) {
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }

    public AnchorPane loadAnimateAnchor(String fsFormName) {
        System.err.println("fsFormName to close == " + String.valueOf(fsFormName));
        ScreenInterface fxObj = getController(fsFormName);
        System.err.println("fxObj to close == " + String.valueOf(fxObj));
        fxObj.setGRider(oApp);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxObj.getClass().getResource(fsFormName));
        fxmlLoader.setController(fxObj);

        AnchorPane root;
        try {
            root = (AnchorPane) fxmlLoader.load();
            FadeTransition ft = new FadeTransition(Duration.millis(1500));
            ft.setNode(root);
            ft.setFromValue(1);
            ft.setToValue(1);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            ft.play();

            return root;
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

        return null;
    }

    public ContextMenu createContextMenu(TabPane tabPane, Tab tab, GRider oApp) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem closeTabItem = new MenuItem("Close Tab");
        MenuItem closeOtherTabsItem = new MenuItem("Close Other Tabs");
        MenuItem closeAllTabsItem = new MenuItem("Close All Tabs");

        closeTabItem.setOnAction(event -> closeSelectTabs(tabPane, tab));
        closeOtherTabsItem.setOnAction(event -> closeOtherTabs(tabPane, tab));
        closeAllTabsItem.setOnAction(event -> closeAllTabs(tabPane, oApp));

        contextMenu.getItems().add(closeTabItem);
        contextMenu.getItems().add(closeOtherTabsItem);
        contextMenu.getItems().add(closeAllTabsItem);

        tab.setContextMenu(contextMenu);

        closeOtherTabsItem.visibleProperty().bind(Bindings.size(tabPane.getTabs()).greaterThan(1));

        return contextMenu;
    }

    private boolean showMessage() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("Are you sure you want to proceed?");
        alert.setContentText("Choose your option.");

        // Add Yes and No buttons to the alert dialog
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Show the alert and wait for a response
        javafx.scene.control.ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        // Handle the user's response
        return result == buttonTypeYes;
    }

    private void closeSelectTabs(TabPane tabPane, Tab tab) {
        if (showMessage()) {
            Tabclose(tabPane);
            tabName.remove(tab.getText());
            tabPane.getTabs().remove(tab);
        }
    }
    //Load Main Screen if no tab remain

    public void Tabclose(TabPane tabpane) {
        int tabsize = tabpane.getTabs().size();
        if (tabsize == 0) {
            setScene(loadAnimateAnchor("Dashboard.fxml"));
        }
    }

    private void closeOtherTabs(TabPane tabPane, Tab currentTab) {
        if (showMessage()) {
            tabPane.getTabs().removeIf(tab -> tab != currentTab);
            List<String> currentTabNameList = Collections.singletonList(currentTab.getText());
            tabName.retainAll(currentTabNameList);
            for (Tab tab : tabPane.getTabs()) {
                String formName = tab.getText();
            }
        }
    }

    private void closeAllTabs(TabPane tabPane, GRider oApp) {
        if (showMessage()) {
            tabName.clear();
            // Close all tabs using your TabsStateManager
            for (Tab tab : tabPane.getTabs()) {
                String formName = tab.getText();
            }
            tabPane.getTabs().clear();
            StackPane myBox = (StackPane) tabpane.getParent();
            myBox.getChildren().clear();
        }
    }

    /*SET SCENE FOR WORKPLACE - STACKPANE - TABPANE*/
    public void setScene2(TabPane foPane) {
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }

    @FXML
    private void pane(ActionEvent event) {
        anchorSubMenu.setVisible(false);
        for (int i = 0; i < navButtons.length; i++) {
                navButtons[i].setSelected(false); // Set each button's selected state to false
            }
    }

    @FXML
    private void switchDashboard(ActionEvent event) {
        toggleSubmenu("Dashboard Section", "switchDashboard", 0);
//        btnDashboard.setSelected(true);
        dashboardMenu01();
    }

    @FXML
    private void switchItem(ActionEvent event) {
        toggleSubmenu("Item Section", "switchItem", 1);
//        btnItemManagement.setSelected(true);
        dashboardMenu02();
    }

    @FXML
    private void switchOrder(ActionEvent event) {
        toggleSubmenu("Order Section", "switchOrder", 2);
    }

    @FXML
    private void switchWayBill(ActionEvent event) {
        toggleSubmenu("WayBill Section", "switchWayBill", 3);
    }

    @FXML
    private void switchPickup(ActionEvent event) {
        toggleSubmenu("Pickup Section", "switchPickup", 4);
    }

    @FXML
    private void switchClient(ActionEvent event) {
        toggleSubmenu("Client Section", "switchClient", 5);
    }

    @FXML
    private void switchQA(ActionEvent event) {
        toggleSubmenu("QA Section", "switchQA", 6);
    }

    @FXML
    private void switchRatings(ActionEvent event) {
        toggleSubmenu("Ratings Section", "switchRatings", 7);
    }

    @FXML
    private void switchReports(ActionEvent event) {
        toggleSubmenu("Reports Section", "switchReports", 8);
    }

    private void initMenu() {
        anchorSubMenu.setVisible(false);
    }

    private void toggleSubmenu(String sectionName, String buttonId, Integer btnIndex) {
        // Check if the submenu is visible and the same button is clicked
        if (anchorSubMenu.isVisible() && lastClickedButton.equals(buttonId)) {
            // Hide the submenu and reset the last clicked button
            anchorSubMenu.setVisible(false);
            navButtons[btnIndex].setSelected(false); // Set each button's selected state to false
            lastClickedButton = "";

        } else {
            // Show the submenu and update the last clicked button
            anchorSubMenu.setVisible(true);
            lastClickedButton = buttonId;
        }
    }

    private void dashboardMenu02() {

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

    private void dashboardMenu01() {
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

        dissectJSON(laMaster.toJSONString());
    }

    private void dissectJSON(String fsValue) {
        if (fsValue == null || fsValue.isEmpty()) {
            System.err.println("Invalid JSON string.");
            return;
        }

        JSONParser loParser = new JSONParser();
        try {
            JSONArray laMaster = (JSONArray) loParser.parse(fsValue);
            if (laMaster == null) {
                System.err.println("Parsed JSON is empty or invalid.");
                return;
            }

            JSONArray laDetail, laDetail2;
            JSONObject loParent, loDetail, loSubDetail;

            TreeItem<String> root = new TreeItem<>("root");

            for (int lnCtr = 0; lnCtr < laMaster.size(); lnCtr++) {
                loParent = (JSONObject) laMaster.get(lnCtr);
                if (loParent == null || !loParent.containsKey("parent")) {
                    continue; // Skip invalid entries
                }
                TreeItem<String> parentnode = new TreeItem<>(String.valueOf(loParent.get("parent")));

                if (loParent.containsKey("child") && loParent.get("child") instanceof JSONArray) {
                    laDetail = (JSONArray) loParent.get("child");
                    for (int x = 0; x < laDetail.size(); x++) {
                        loDetail = (JSONObject) laDetail.get(x);
                        if (loDetail == null || !loDetail.containsKey("parent")) {
                            continue; // Skip invalid child entries
                        }
                        TreeItem<String> child = new TreeItem<>(String.valueOf(loDetail.get("parent")));
                        if (loDetail.containsKey("child") && loDetail.get("child") instanceof JSONArray) {
                            laDetail2 = (JSONArray) loDetail.get("child");
                            for (int y = 0; y < laDetail2.size(); y++) {
                                // Check for non-null and valid data
                                TreeItem<String> subdetail = new TreeItem<>(String.valueOf(laDetail2.get(y)));
                                child.getChildren().add(subdetail);
                            }
                        }
                        parentnode.getChildren().add(child);
                    }
                }
                root.getChildren().add(parentnode);
            }

            // Ensure tvChild is not null before interacting with it
            if (tvChild != null) {
                tvChild.setRoot(root);
                tvChild.setShowRoot(false);
                tvChild.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.isLeaf() && newValue.getValue() != null && !newValue.getValue().isEmpty()) {
                        System.out.println("Selected: " + newValue.getValue());

                        switch (newValue.getValue()) {
                            case "Sales Replacement":
                                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml";
                                intIndex = 0;
                                break;
                            case "Additional Give":
                                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml";
                                intIndex = 0;
                                break;
                            default:
                                intIndex = -1;
                                throw new AssertionError("Unhandled case: " + newValue.getValue());
                        }

                        // Add logic to load the form
                        if (oApp != null && checktabs(SetTabTitle(sformname)) == 1) {
                            setScene2(loadAnimate(sformname));
                            anchorSubMenu.setVisible(false);
                            navButtons[intIndex].setSelected(false);
                            pane.requestFocus();
                        }
                    } else {
                        // Handle the case where newValue is null, empty, or not a leaf
                        System.out.println("Invalid selection or empty value.");
                    }

                });
            } else {
                System.err.println("tvChild is not initialized.");
            }

        } catch (Exception ex) {
            ex.printStackTrace(); // Log the full exception for easier debugging
        }
    }

    private void ToggleGroupControl() {
    toggleGroup = new ToggleGroup();
    navButtons = new ToggleButton[]{
        btnDashboard,
        btnItemManagement,
        btnOrder,
        btnWayBill,
        btnPickup,
        btnClient,
        btnQA,
        btnRatings,
        btnReports
    };
    
    // Tooltip texts for each button
    String[] tooltipTexts = {
        "Sales",
        "Manage Items",
        "View Orders",
        "View Waybills",
        "Pickup Information",
        "Manage Clients",
        "Quality Assurance",
        "View Ratings",
        "View Reports"
    };

    // Assign tooltips and toggle group in a loop
    for (int i = 0; i < navButtons.length; i++) {
        navButtons[i].setTooltip(new Tooltip(tooltipTexts[i]));
        navButtons[i].setToggleGroup(toggleGroup);
    }
}


    /*Handle button click*/
    private void ClickButton() {
        btnClose.setOnAction(this::handleButtonAction);
        btnMinimize.setOnAction(this::handleButtonAction);
    }

    private void handleButtonAction(ActionEvent event) {
        Object source = event.getSource();
        JSONObject poJSON;

        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            switch (clickedButton.getId()) {
                case "btnClose":
                    Platform.exit();
                    break;
                case "btnMinimize":
                    Stage stage = (Stage) btnMinimize.getScene().getWindow();
                    stage.setIconified(true);
                    break;
                // Add more cases for other buttons if needed
            }
        }
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
}
