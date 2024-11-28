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
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
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
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DashboardController implements Initializable {

    private GRider oApp;
    private String lastClickedButton = "";
    private String lastClickedBtnRighNav = "";
    private int notificationCount = 0;
    private ToggleGroup toggleGroup;
    private static ToggleButton[] navButtons;
    private static Tooltip[] navTooltip;

    private ToggleGroup toggleGroupLowerBtn;
    private static ToggleButton[] navButtonsLowerBtn;
    private static Tooltip[] navTooltipLowerBtn;

    private ToggleGroup toggleGroupRightNav;
    private static ToggleButton[] navButtonsRightNav;
    private static Tooltip[] navTooltipRightNav;

    private int targetTabIndex = -1;
    private int intIndex = -1;
    List<String> tabName = new ArrayList<>();
    String sformname = "";
    @FXML
    private TabPane tabpane;
    @FXML
    private AnchorPane anchorSubMenu, anchorSpace, MainAnchor, anchorSubMenuNotif, badgeNotification, badgeAddtoCart;
    @FXML
    private TreeView<String> tvChild;
    @FXML
    private TreeView<String> tvChild1;
    @FXML
    StackPane workingSpace;
    @FXML
    private Button btnClose, btnMinimize;
    @FXML
    private ToggleButton btnInventory,
            btnPurchasing,
            btnSales,
            btnServiceRepair,
            btnAccountsPayable,
            btnAccountsReceivable,
            btnGeneralAccounting,
            btnParameters,
            btnHelp,
            btnLogout,
            btnNotification,
            btnAddToCart;

    @FXML
    private Pane pane;

    @FXML
    private Label DateAndTime, AppUser, lblAddToCartCount, lblNotifCount;

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
        ToggleGroupControlLowerBtn();
        ToggleGroupControlRightNav();
        checkDepartment();
        ClickButton();
        getTime();
        setPane();
        loadUserInfo();
        notificationChecker();
    }

    /** PANE **/
    @FXML
    private void pane(ActionEvent event) {
        anchorSubMenu.setVisible(false);
        for (int i = 0; i < navButtons.length; i++) {
            navButtons[i].setSelected(false); // Set each button's selected state to false
        }

        anchorSubMenuNotif.setVisible(false);
        for (int i = 0; i < navButtonsRightNav.length; i++) {
            navButtonsRightNav[i].setSelected(false); // Set each button's selected state to false
        }
    }
    
    /** ACTION EVENTS **/
    @FXML
    private void switchInventory(ActionEvent event) {
        toggleSubmenu("Dashboard Section", "switchInventory", 0);
        dashboardMenu01();
    }

    @FXML
    private void switchNotification(ActionEvent event) {
        toggleSubmenuRightBtn("Notification Section", "switchNotification", 0);
        RightNavNotif();
    }

    @FXML
    private void switchItem(ActionEvent event) {
        toggleSubmenu("Item Section", "switchItem", 1);
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
    private void switchLogout(ActionEvent event) {
//        toggleSubmenu("Logout", "switchLogout", 7);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("GUANZON GROUP OF COMPANY");
        alert.setHeaderText("Are you sure you want to logout?");

        // Show the alert and wait for a response
        Optional<ButtonType> result = alert.showAndWait();

        // Check the response
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Close the current window
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            // If Cancel is clicked, reset the selection
            btnLogout.setSelected(false);
        }
    }

    @FXML
    private void switchReports(ActionEvent event) {
        toggleSubmenu("Reports Section", "switchReports", 8);
    }

    /** SET TAB TITLE **/
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

    /** PANE LOAD **/
    public void setPane() {
        pane.setOnMouseClicked(event -> {
            // Check if the click occurred on the tabs area (not the content area)
            System.out.println("pane clicked at: " + event.getSceneX() + ", " + event.getSceneY());

            // Hide the sub-menu
            anchorSubMenu.setVisible(false);
            anchorSubMenuNotif.setVisible(false);

            // Assuming navButtons is an array or List of buttons in Java
            for (int i = 0; i < navButtons.length; i++) {
                navButtons[i].setSelected(false); // Set each button's selected state to false
            }
            for (int i = 0; i < navButtonsRightNav.length; i++) {
                navButtonsRightNav[i].setSelected(false); // Set each button's selected state to false
            }

            // Perform other actions on click if needed
        });
    }

    /** TAB PANE **/
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

            anchorSubMenuNotif.setVisible(false);
            for (int i = 0; i < navButtonsRightNav.length; i++) {
                navButtonsRightNav[i].setSelected(false); // Set each button's selected state to false
            }
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

    /** SCREEN INTERFACE **/
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

    /** STAB PANE LOAD **/
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

    //TAB CLOSE
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
    
    /** LOAD ANIMATE ANCHOR **/
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
    
    /** CREATE CONTEXT MENU **/
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

    /** SHOW MESSAGE **/
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

    /** CLOSE SELECTED TAB **/
    private void closeSelectTabs(TabPane tabPane, Tab tab) {
        if (showMessage()) {
            Tabclose(tabPane);
            tabName.remove(tab.getText());
            tabPane.getTabs().remove(tab);
        }
    }
    
    /**CLoad Main Screen if no tab remain **/
    public void Tabclose(TabPane tabpane) {
        int tabsize = tabpane.getTabs().size();
        if (tabsize == 0) {
            setScene(loadAnimateAnchor("Dashboard.fxml"));
        }
    }
    
    /**CLOSE OTHER TAB **/
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
    
    /**CLOSE ALL TAB **/
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
    
    /**INITIALIZE SUB MENU VISIBILITY**/
    private void initMenu() {
        anchorSubMenu.setVisible(false);
        anchorSubMenuNotif.setVisible(false);
    }
    
    /**TOGGLE SUBMENU ON LEFT BUTTON**/
    private void toggleSubmenu(String sectionName, String buttonId, Integer btnIndex) {
        // Check if the submenu is visible and the same button is clicked
        boolean isSameButton = anchorSubMenu.isVisible() && lastClickedButton.equals(buttonId);
        anchorSubMenu.setVisible(!isSameButton);
        navButtons[btnIndex].setSelected(!isSameButton);
        lastClickedButton = isSameButton ? "" : buttonId;
    }
    
    /**TOGGLE SUBMENU ON RIGHT BUTTON**/
    private void toggleSubmenuRightBtn(String sectionName, String buttonId, Integer btnIndex) {
        // Check if the submenu is visible and the same button is clicked
        boolean isSameButton = anchorSubMenuNotif.isVisible() && lastClickedBtnRighNav.equals(buttonId);
        anchorSubMenuNotif.setVisible(!isSameButton);
        navButtonsRightNav[btnIndex].setSelected(!isSameButton);
        lastClickedBtnRighNav = isSameButton ? "" : buttonId;
    }
    
    /**STATIC DATA (JSON ON LEFT NAVIGATION)**/
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
    /**STATIC DATA (JSON ON LEFT NAVIGATION)**/
    private void dashboardMenu01() {
        JSONArray laMaster, laDetail;
        JSONObject loMaster, loDetail;
        laMaster = new JSONArray();
        laDetail = new JSONArray();

        // Add "Sales Replacement" only if the department is not 26
        if (!"029".equals(oApp.getDepartment())) {
            loDetail = new JSONObject();
            loDetail.put("parent", "Sales Replacement");
            laDetail.add(loDetail);
        }

        // Add "Additional Give" menu item
        loDetail = new JSONObject();
        loDetail.put("parent", "Additional Give");
        laDetail.add(loDetail);

        // Create the "Sales" parent with its children
        loMaster = new JSONObject();
        loMaster.put("parent", "Sales");
        loMaster.put("child", laDetail);

        // Add "Sales" to the master list
        laMaster.add(loMaster);

        dissectJSON(laMaster.toJSONString());
    }
    /**STATIC DATA (JSON ON RIGHT NAVIGATION)**/
    private void RightNavNotif() {
        JSONArray laMaster, laDetail;
        JSONObject loMaster, loDetail;
        laMaster = new JSONArray();
        laDetail = new JSONArray();

        // Add "Sales Replacement" only if the department is not 26
        if (!"029".equals(oApp.getDepartment())) {
            loDetail = new JSONObject();
            loDetail.put("parent", "Sales");
            laDetail.add(loDetail);
        }

        // Add "Additional Give" menu item
        loDetail = new JSONObject();
        loDetail.put("parent", "PO Receiving");
        laDetail.add(loDetail);

        // Create the "Sales" parent with its children
        loMaster = new JSONObject();
        loMaster.put("parent", "Monthly Payment");
        loMaster.put("child", laDetail);

        // Add "Sales" to the master list
        laMaster.add(loMaster);

        dissectJSONRightNav(laMaster.toJSONString());
    }
    
    /**LOAD THE FORM BASE FROM THE JSON ON LEFT NAVIGATION**/
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
//                                intIndex = 0;
                                break;
                            case "Additional Give":
                                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml";
//                                intIndex = 0;
                                break;
                        }

                        // Add logic to load the form    
                        if (oApp != null) {
                            boolean isNewTab = (checktabs(SetTabTitle(sformname)) == 1);
                                if (isNewTab) setScene2(loadAnimate(sformname)); 
                                else System.out.println("THIS FORM IS ALREADY OPENED");
                                anchorSubMenu.setVisible(false);
                            for (ToggleButton navButton : navButtons) {
                                navButton.setSelected(false);
                            }
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
            ex.printStackTrace(); //Log the full exception for easier debugging
        }
    }

    /**SET SCENE FOR WORKPLACE - STACKPANE - TABPANE**/
    public void setScene2(TabPane foPane) {
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }

    
    /**LOAD THE FORM BASE FROM THE JSON ON RIGHT NAVIGATION**/
    private void dissectJSONRightNav(String fsValue) {
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

            // Ensure tvChild1 is not null before interacting with it
            if (tvChild1 != null) {
                tvChild1.setRoot(root);
                tvChild1.setShowRoot(false);
                tvChild1.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null && newValue.isLeaf() && newValue.getValue() != null && !newValue.getValue().isEmpty()) {
                        System.out.println("Selected: " + newValue.getValue());

                        switch (newValue.getValue()) {
                            case "Sales Replacement":
                                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml";
//                                intIndex = 0;
                                break;
                            case "Additional Give":
                                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml";
//                                intIndex = 0;
                                break;
                        }
                        // Add logic to load the form
                        if (oApp != null) {
                            boolean isNewTab = (checktabs(SetTabTitle(sformname)) == 1);
                                if (isNewTab) setScene2(loadAnimate(sformname)); 
                                else System.out.println("THIS FORM IS ALREADY OPENED");
                                anchorSubMenuNotif.setVisible(false);
                            for (ToggleButton navButtonsRightNav : navButtonsRightNav) {
                                navButtonsRightNav.setSelected(false);
                            }
                            pane.requestFocus();
                        }
                    } else {
                        // Handle the case where newValue is null, empty, or not a leaf
                        System.out.println("Invalid selection or empty value.");
                    }
                });
            } else {
                System.err.println("tvChild1 is not initialized.");
            }

        } catch (Exception ex) {
            ex.printStackTrace(); // Log the full exception for easier debugging
        }
    }

    /**INITIALIZE TOGGLE GROUP LEFT NAVIGATION**/
    private void ToggleGroupControl() {
        toggleGroup = new ToggleGroup();
        navButtons = new ToggleButton[]{
            btnInventory,
            btnPurchasing,
            btnSales,
            btnServiceRepair,
            btnAccountsPayable,
            btnAccountsReceivable,
            btnGeneralAccounting,
            btnParameters
        };

        // Tooltip texts for each button
        String[] tooltipTexts = {
            "Inventory",
            "Purchasing",
            "Sales",
            "Service Repair",
            "Accounts Payable",
            "Accounts Receivable",
            "General Accounting",
            "Parameters"
        };

        // Assign tooltips to buttons
        for (int i = 0; i < navButtons.length; i++) {
            if (navButtons[i].isVisible()) { // Skip setting tooltip for hidden buttons
                navButtons[i].setTooltip(new Tooltip(tooltipTexts[i]));
                navButtons[i].setToggleGroup(toggleGroup);
            }
        }
    }

    /**INITIALIZE TOGGLE GROUP RIGHT NAVIGATION**/
    private void ToggleGroupControlRightNav() {
        toggleGroupRightNav = new ToggleGroup();
        navButtonsRightNav = new ToggleButton[]{
            btnNotification,
            btnAddToCart
        };

        // Tooltip texts for each button
        String[] tooltipTexts = {
            "Notification",
            "Add To Cart"
        };

        // Assign tooltips and toggle group in a loop
        for (int i = 0; i < navButtonsRightNav.length; i++) {
            navButtonsRightNav[i].setTooltip(new Tooltip(tooltipTexts[i]));
            navButtonsRightNav[i].setToggleGroup(toggleGroupRightNav);
        }
    }

    /**INITIALIZE TOGGLE GROUP LOWER BUTTON **/
    private void ToggleGroupControlLowerBtn() {
        toggleGroupLowerBtn = new ToggleGroup();
        navButtonsLowerBtn = new ToggleButton[]{
            btnHelp,
            btnLogout
        };

        // Tooltip texts for each button
        String[] tooltipTexts = {
            "Help",
            "Logout"
        };

        // Assign tooltips and toggle group in a loop
        for (int i = 0; i < navButtonsLowerBtn.length; i++) {
            navButtonsLowerBtn[i].setTooltip(new Tooltip(tooltipTexts[i]));
            navButtonsLowerBtn[i].setToggleGroup(toggleGroupLowerBtn);
        }
    }

    /**GET DEPARTMENT**/ 
    private void checkDepartment() {
        // Validate and hide btnSales if department is 026
        if ("022".equals(oApp.getDepartment())) { // Ensure the department is compared correctly
            btnSales.setVisible(false);
            btnSales.setManaged(false);  // Hide the button
        }
    }

    /**INITIALIZE CLICK BUTTON**/
    private void ClickButton() {
        btnClose.setOnAction(this::handleButtonAction);
        btnMinimize.setOnAction(this::handleButtonAction);
    }

    /** HANDLE BUTTON ACTION **/
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


    
    /**GET TIME**/
    private void getTime() {
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

            String formattedTime = timeFormat.format(date);
            String formattedDate = dateFormat.format(date);

            DateAndTime.setText(formattedDate + " || " + formattedTime);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }
    /**LOAD USER INFO**/
    private void loadUserInfo() {
        AppUser.setText(oApp.getLogName() + " || " + oApp.getDivisionName());
    }
    
    
    private void notificationChecker(){
    // Setup the ScheduledService to check for notifications periodically
        ScheduledService<Void> service = new ScheduledService<Void>() {  // Explicit type argument here
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {  // Explicit type argument here
                    @Override
                    protected Void call() {
                        checkNotifications();
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.minutes(1)); // Runs every minute (adjust as needed)
        service.start(); 
        
    }
    private void checkNotifications() {
        // Simulate the logic to check notifications (replace with actual logic)
        notificationCount += (int) (Math.random() * 5);  // Adds 0-4 random notifications

        // Update the label's text on the JavaFX Application Thread
        Platform.runLater(() -> {
            lblNotifCount.setText( String.valueOf(notificationCount));
            lblAddToCartCount.setText(String.valueOf(notificationCount));
        });
    }
}