package com.rmj.guanzongroup.sidebarmenus.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.cas.controller.unloadForm;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DashboardController implements Initializable {

    private GRider oApp;
    private String lastClickedBtnLeftSideBar = "";
    private String lastClickedBtnRightSideBar = "";
    private String psDefaultScreenFXML = "/com/rmj/guanzongroup/sidebarmenus/views/DefaultScreen.fxml";
    private int notificationCount = 0;
    private int cartCount = 0;

    private ToggleGroup toggleGroup;
    private static ToggleButton[] toggleBtnLeftUpperSideBar;
    private static Tooltip[] sideBarLeftUpperToolTip;

    private ToggleGroup toggleGroupLowerBtn;
    private static ToggleButton[] toggleBtnLeftLowerSideBar;
    private static Tooltip[] sideBarLeftLowerToolTip;

    private ToggleGroup toggleGroupRightSideBar;
    private static ToggleButton[] toggleBtnRightSideBar;
    private static Tooltip[] sideBarRightToolTip;
    private Map<TreeItem<String>, String> menuLocationMap = new HashMap<>();
    private boolean isListenerLeftAdded = false; // Prevent multiple listener additions
    private boolean isListenerRightAdded = false; // Prevent multiple listener additions
//    private JSONArray flatMenuItems;
    private List<JSONObject> flatMenuItems;
    private int userLevel; // User's access level
    private int targetTabIndex = -1;
    private int intIndex = -1;
    List<String> tabName = new ArrayList<>();
    String sformname = "";
    @FXML
    private TabPane tabpane;
    @FXML
    private AnchorPane anchorLeftSideBarMenu, anchorSpace, MainAnchor, anchorRightSideBarMenu, badgeNotification, badgeAddtoCart;
    @FXML
    private TreeView<String> tvLeftSideBar;
    @FXML
    private TreeView<String> tvRightSideBar;
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
        try {
            setScene(loadAnimateAnchor(psDefaultScreenFXML));
            setPane();
            initMenu();
            ToggleGroupControlUpperLeftSideBar();
            ToggleGroupControlLowerLeftSideBar();
            ToggleGroupControlRightSideBar();
            loadUserInfo();
            checkDepartment();
            getTime();
            initButtonClickActions();
            notificationChecker();

            //johndave modified 02-12-2025
            setTreeViewStyle(tvLeftSideBar);
            setTreeViewStyle(tvRightSideBar);

            //johndave modified 02-15-2025
            setDropShadowEffectsLeftSideBar(anchorLeftSideBarMenu);
            setDropShadowEffectsRightSideBar(anchorRightSideBarMenu);

            //johndave modified 02-17-2025
            setAnchorPaneVisibleManage(true, anchorRightSideBarMenu);

            notifMenuItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //johndave modified 02-17-2025
    //disable menu buttons depends on the access_level
    //example initButtonVisible("01", btnParameters, oApp.getDepartment());
    private void initButtonVisible(String fsAccessLevel, ToggleButton foTButton, String fsContainsTo) {
        foTButton.setVisible(fsAccessLevel.contains(fsContainsTo));
        foTButton.setManaged(fsAccessLevel.contains(fsContainsTo));
    }

    //johndave modified 02-17-2025
    private void setAnchorPaneVisibleManage(boolean fbVisibleManage, Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                node.setVisible(fbVisibleManage);
                node.setManaged(fbVisibleManage);
            }
        }
    }

    /**
     * ********************** LOAD CONTENT SECTIONS **************************
     */
    /**
     * PANE *
     */
    @FXML
    private void pane(ActionEvent event) {
        setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
        for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
            toggleBtnLeftUpperSideBar[i].setSelected(false); // Set each button's selected state to false
        }

//        anchorRightSideBarMenu.setVisible(false);
//        for (int i = 0; i < toggleBtnRightSideBar.length; i++) {
//            toggleBtnRightSideBar[i].setSelected(false); // Set each button's selected state to false
//        }
    }

    /**
     * PANE LOAD *
     */
    public void setPane() {
        pane.setOnMouseClicked(event -> {
            // Check if the click occurred on the tabs area (not the content area)
            System.out.println("pane clicked at: " + event.getSceneX() + ", " + event.getSceneY());

            // Hide the sub-menu
            //johndave modified 02-17-2025
            setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);

            // Assuming navButtons is an array or List of buttons in Java
            for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
                toggleBtnLeftUpperSideBar[i].setSelected(false); // Set each button's selected state to false
            }
            for (int i = 0; i < toggleBtnRightSideBar.length; i++) {
                toggleBtnRightSideBar[i].setSelected(false); // Set each button's selected state to false
            }

            // Perform other actions on click if needed
        });
    }

    /**
     * SET TAB TITLE
     *
     *
     * @param menuaction
     */
    public String SetTabTitle(String menuaction) {
        if (menuaction.contains(".fxml")) {
            switch (menuaction) {
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_Entry.fxml":
                    return "Purchase Order";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_History.fxml":
                    return "Purchase Order History";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptanceEntry.fxml":
                    return "Delivery Acceptance";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml":
                    return "Payment Request";
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * SCREEN INTERFACE *
     */
    private ScreenInterface getController(String fsValue) {
        if (fsValue.contains(".fxml")) {
            switch (fsValue) {
                case "/com/rmj/guanzongroup/sidebarmenus/views/DefaultScreen.fxml":
                    return new DefaultScreenController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml":
                    return new SampleForm1Controller();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml":
                    return new SampleForm2Controller();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_Entry.fxml":
//                return new PurchasingOrder_EntryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_History.fxml":
                    return new PurchasingOrder_HistoryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptanceEntry.fxml":
                    return new DeliveryAcceptanceEntryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml":
                    return new PaymentRequestController();
                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * TAB PANE *
     */
    public void setTabPane() {
        // set up the drag and drop listeners on the tab pane
        tabpane.setOnMouseClicked(event -> {
            // Check if the click occurred on the tabs area (not the content area)
            System.out.println("TabPane clicked at: " + event.getSceneX() + ", " + event.getSceneY());

            // Hide the sub-menu
            setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
            // Assuming navButtons is an array or List of buttons in Java
            for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
                toggleBtnLeftUpperSideBar[i].setSelected(false); // Set each button's selected state to false
            }

//            setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
            for (int i = 0; i < toggleBtnRightSideBar.length; i++) {
                toggleBtnRightSideBar[i].setSelected(false); // Set each button's selected state to false
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

    /**
     * STAB PANE LOAD
     *
     *
     * @param fsFormName
     * @return
     */
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
                if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure, do you want to close tab?")) {
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

    private void setScene(AnchorPane foPane) {
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }

    /**
     * SET SCENE FOR WORKPLACE - STACKPANE - TABPANE*
     */
    public void setScene2(TabPane foPane) {
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }

    /**
     * LOAD ANIMATE ANCHOR *
     */
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

    /**
     * CREATE CONTEXT MENU *
     */
//    public ContextMenu createContextMenu(TabPane tabPane, Tab tab, GRider oApp) {
//        ContextMenu contextMenu = new ContextMenu();
//
//        MenuItem closeTabItem = new MenuItem("Close Tab");
//        MenuItem closeOtherTabsItem = new MenuItem("Close Other Tabs");
//        MenuItem closeAllTabsItem = new MenuItem("Close All Tabs");
//
//        closeTabItem.setOnAction(event -> closeSelectTabs(tabPane, tab));
//        closeOtherTabsItem.setOnAction(event -> closeOtherTabs(tabPane, tab));
//        closeAllTabsItem.setOnAction(event -> closeAllTabs(tabPane, oApp));
//
//        contextMenu.getItems().add(closeTabItem);
//        contextMenu.getItems().add(closeOtherTabsItem);
//        contextMenu.getItems().add(closeAllTabsItem);
//
//        tab.setContextMenu(contextMenu);
//
//        closeOtherTabsItem.visibleProperty().bind(Bindings.size(tabPane.getTabs()).greaterThan(1));
//
//        return contextMenu;
//    }
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

    //johndave modified 02-17-2025
    /**
     * CLOSE SELECTED TAB *
     */
    private void closeSelectTabs(TabPane tabPane, Tab currentTab) {
        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure, do you want to close tab?")) {
            // Remove the tab
            if (tabPane.getTabs().removeIf(tab -> tab == currentTab)) {
                if (tabPane.getTabs().isEmpty()) {
                    unloadForm unload = new unloadForm();
                    StackPane myBox = (StackPane) tabPane.getParent();
                    myBox.getChildren().clear();
                    myBox.getChildren().add(unload.getScene(psDefaultScreenFXML, oApp));
                }
            }
            // Remove the tab name from the tracking list
            tabName.remove(currentTab.getText());
        }
    }

    private void closeOtherTabs(TabPane tabPane, Tab currentTab) {
        if (ShowMessageFX.YesNo(null, "Close Other Tab", "Are you sure, do you want to close other tab?")) {
            tabPane.getTabs().removeIf(tab -> tab != currentTab);
            List<String> currentTabNameList = Collections.singletonList(currentTab.getText());
            tabName.retainAll(currentTabNameList);
            for (Tab tab : tabPane.getTabs()) {
                String formName = tab.getText();
            }
        }
    }

    private void closeAllTabs(TabPane tabPane, GRider oApp) {
        // Check if tabPane or other components are null
        if (tabPane == null) {
            System.out.println("tabPane is null");
            return;
        }

        if (ShowMessageFX.YesNo(null, "Close All Tabs", "Are you sure, do you want to close all tabs?")) {
            if (tabName != null) {
                tabName.clear();  // Check if tabName is not null
            } else {
                System.out.println("tabName is null");
            }

            // Close all tabs
            tabPane.getTabs().clear();

            unloadForm unload = new unloadForm();

            // Check if tabPane.getParent() returns a valid parent node
            if (tabPane.getParent() == null) {
                System.out.println("Parent of tabPane is null");
                return;
            }

            StackPane myBox = (StackPane) tabPane.getParent();  // Make sure tabPane's parent is not null
            myBox.getChildren().clear();
            myBox.getChildren().add(unload.getScene(psDefaultScreenFXML, oApp));
        }
    }

//    /**
//     * CLOSE ALL TAB *
//     */
//    private void closeAllTabs(TabPane tabPane, GRider oApp) {
//        if (showMessage()) {
//            tabName.clear();
//            // Close all tabs using your TabsStateManager
//            for (Tab tab : tabPane.getTabs()) {
//                String formName = tab.getText();
//            }
//            tabPane.getTabs().clear();
//            StackPane myBox = (StackPane) tabpane.getParent();
//            myBox.getChildren().clear();
//        }
//    }
    //TAB CLOSE
    public void Tabclose() {
        int tabsize = tabpane.getTabs().size();
        if (tabsize == 1) {
            setScene(loadAnimateAnchor(psDefaultScreenFXML));
        }
    }

    /**
     * CLoad Main Screen if no tab remain *
     */
    public void Tabclose(TabPane tabpane) {
        int tabsize = tabpane.getTabs().size();
        if (tabsize == 1) {
            setScene(loadAnimateAnchor(psDefaultScreenFXML));
        }
    }

    /**
     * INITIALIZE SUB MENU VISIBILITY*
     */
    private void initMenu() {
        setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
        setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
    }

    //johndave modified 02-12-2025
    private void setTreeViewStyle(TreeView treeView) {
        treeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: #DFDFDF; "
                            + "-fx-border-color: #DFDFDF;"
                    ); // Keep default background
                    getStyleClass().add("empty-tree-cell"); // Add class to prevent hover effect
                } else {
                    setText(item);
                    setStyle(""); // Reset style for non-empty items
                    getStyleClass().remove("empty-tree-cell"); // Remove empty class for valid items
                }
            }
        });
    }

    private void setDropShadowEffectsLeftSideBar(AnchorPane anchorPane) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(20); // Adjust blur intensity
        shadow.setWidth(21.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3)); // Adjust transparency

        // Apply shadow only to the right side
        shadow.setOffsetX(2); // Moves shadow to the right
        shadow.setOffsetY(0);

        // Apply to the AnchorPane
        anchorPane.setEffect(shadow);
    }

    private void setDropShadowEffectsRightSideBar(AnchorPane anchorPane) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(20); // Adjust blur intensity
        shadow.setWidth(21.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3)); // Adjust transparency

        // Apply shadow only to the right side
        shadow.setOffsetX(-2); // Moves shadow to the right
        shadow.setOffsetY(0);

        // Apply to the AnchorPane
        anchorPane.setEffect(shadow);
    }

    private void ToggleGroupControlUpperLeftSideBar() {
        toggleGroup = new ToggleGroup();
        toggleBtnLeftUpperSideBar = new ToggleButton[]{
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
        for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
            if (toggleBtnLeftUpperSideBar[i].isVisible()) { // Skip setting tooltip for hidden buttons
                toggleBtnLeftUpperSideBar[i].setTooltip(new Tooltip(tooltipTexts[i]));
                toggleBtnLeftUpperSideBar[i].setToggleGroup(toggleGroup);
            }
        }
    }

    /**
     * INITIALIZE TOGGLE GROUP RIGHT NAVIGATION*
     */
    private void ToggleGroupControlRightSideBar() {
        toggleGroupRightSideBar = new ToggleGroup();
        toggleBtnRightSideBar = new ToggleButton[]{
            btnNotification,
            btnAddToCart
        };

        // Tooltip texts for each button
        String[] tooltipTexts = {
            "Notification",
            "Add To Cart"
        };

        // Assign tooltips and toggle group in a loop
        for (int i = 0; i < toggleBtnRightSideBar.length; i++) {
            toggleBtnRightSideBar[i].setTooltip(new Tooltip(tooltipTexts[i]));
            toggleBtnRightSideBar[i].setToggleGroup(toggleGroupRightSideBar);
        }
    }

    /**
     * INITIALIZE TOGGLE GROUP LOWER BUTTON *
     */
    private void ToggleGroupControlLowerLeftSideBar() {
        toggleGroupLowerBtn = new ToggleGroup();
        toggleBtnLeftLowerSideBar = new ToggleButton[]{
            btnHelp,
            btnLogout
        };

        // Tooltip texts for each button
        String[] tooltipTexts = {
            "Help",
            "Logout"
        };

        // Assign tooltips and toggle group in a loop
        for (int i = 0; i < toggleBtnLeftLowerSideBar.length; i++) {
            toggleBtnLeftLowerSideBar[i].setTooltip(new Tooltip(tooltipTexts[i]));
            toggleBtnLeftLowerSideBar[i].setToggleGroup(toggleGroupLowerBtn);
        }
    }

    /**
     * ACTION EVENTS *
     */
    /*Right Side Bar*/
    @FXML
    private void switchNotification(ActionEvent event) {
        toggleRightSideBarMenuButton("switchNotification", 0);
        notifMenuItems();

    }

    @FXML
    private void switchAddToCart(ActionEvent event) {
        toggleRightSideBarMenuButton("switchAddToCart", 1);
    }

    /*Left Side Bar*/
    @FXML
    private void switchInventory(ActionEvent event) {
        inventoryMenuItems();
        toggleLeftSideBarMenuButton("switchInventory", 0);
        toggleSidebarWidth();
    }

    @FXML
    private void switchPurchasing(ActionEvent event) {
        purchasingMenuItems();
        toggleLeftSideBarMenuButton("switchPurchasing", 1);
        toggleSidebarWidth();
    }

    @FXML
    private void switchSales(ActionEvent event) {
        salesMenuItems();
        toggleLeftSideBarMenuButton("switchSales", 2);
        toggleSidebarWidth();
    }

    @FXML
    private void switchServiceRepair(ActionEvent event) {
        tvLeftSideBar.setRoot(null);
        toggleLeftSideBarMenuButton("switchServiceRepair", 3);

    }

    @FXML
    private void switchAccountsPayable(ActionEvent event) {
        tvLeftSideBar.setRoot(null);
        toggleLeftSideBarMenuButton("switchAccountsPayable", 4);

    }

    @FXML
    private void switchAccountsReceivable(ActionEvent event) {
        tvLeftSideBar.setRoot(null);
        toggleLeftSideBarMenuButton("switchAccountsReceivable", 5);

    }

    @FXML
    private void switchGeneralAccounting(ActionEvent event) {
        tvLeftSideBar.setRoot(null);
        toggleLeftSideBarMenuButton("switchGeneralAccounting", 6);

    }

    @FXML
    private void switchParameters(ActionEvent event) {
        tvLeftSideBar.setRoot(null);
        toggleLeftSideBarMenuButton("switchParameters", 7);
    }

    @FXML
    private void switchHelp(ActionEvent event) {
        openPDF("D:/Help.pdf");
        btnHelp.setSelected(false);
    }

    @FXML
    private void switchLogout(ActionEvent event) {
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

    /**
     * TOGGLE SUBMENU ON LEFT BUTTON*
     */
    private void toggleLeftSideBarMenuButton(String buttonId, Integer btnIndex) {
        System.out.println("Toggling: " + buttonId + " | Last Clicked: " + lastClickedBtnLeftSideBar);
        boolean isNoMenu = false;
        boolean isSameButton = anchorLeftSideBarMenu.isVisible() && lastClickedBtnLeftSideBar.equals(buttonId);

        if (tvLeftSideBar.getRoot() != null) {
            if (!tvLeftSideBar.getRoot().getChildren().isEmpty()) {
                setAnchorPaneVisibleManage(!isSameButton, anchorLeftSideBarMenu);
            } else {
                setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
                ShowMessageFX.Warning(null, "Computerized Accounting System", "No Menu's Available");
                isNoMenu = true;
            }
        } else {
            setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
            ShowMessageFX.Warning(null, "Computerized Accounting System", "No Menu's Available");
            isNoMenu = true;
        }

        for (ToggleButton button : toggleBtnLeftUpperSideBar) {
            button.setSelected(false);
        }
        if (!isNoMenu) {
            toggleBtnLeftUpperSideBar[btnIndex].setSelected(!isSameButton);
            lastClickedBtnLeftSideBar = isSameButton ? "" : buttonId;
        }

    }

    private void toggleRightSideBarMenuButton(String buttonId, Integer btnIndex) {
        System.out.println("Toggling: " + buttonId + " | Last Clicked: " + lastClickedBtnRightSideBar);
        boolean isNoMenu = false;
        boolean isSameButton = anchorRightSideBarMenu.isVisible() && lastClickedBtnRightSideBar.equals(buttonId);
        setAnchorPaneVisibleManage(!isSameButton, anchorRightSideBarMenu);

        if (tvRightSideBar.getRoot() != null) {
            if (!tvRightSideBar.getRoot().getChildren().isEmpty()) {
                setAnchorPaneVisibleManage(!isSameButton, anchorRightSideBarMenu);
            } else {
                setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
                ShowMessageFX.Warning(null, "Computerized Accounting System", "No Menu's Available");
                isNoMenu = true;
            }
        } else {
            setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
            ShowMessageFX.Warning(null, "Computerized Accounting System", "No Menu's Available");
            isNoMenu = true;
        }

        for (ToggleButton button : toggleBtnRightSideBar) {
            button.setSelected(false);
        }
        if (!isNoMenu) {
            toggleBtnRightSideBar[btnIndex].setSelected(!isSameButton);
            lastClickedBtnRightSideBar = isSameButton ? "" : buttonId;
        }
    }

    /**
     * **************** MENU ITEMS SECTIONS *********************************
     */
    private void inventoryMenuItems() {
        String jsonString = "[\n"
                + "  {\"access_level\": \"026 011\", \"menu_name\": \"Inventory\", \"fxml_path\": \"Inventory\", \"controller_path\": \"sample.controller\", \"menu_id\": \"028\", \"menu_parent\": \"\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Request\", \"fxml_path\": \"Inventory/Request\", \"controller_path\": \"sample.controller\", \"menu_id\": \"029\", \"menu_parent\": \"028\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Regular Stocks\", \"fxml_path\": \"Inventory/Request/Regular Stocks\", \"controller_path\": \"sample.controller\", \"menu_id\": \"030\", \"menu_parent\": \"029\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Inventory/Request/Regular Stocks/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"031\", \"menu_parent\": \"030\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Inventory/Request/Regular Stocks/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"032\", \"menu_parent\": \"030\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"System Recommend\", \"fxml_path\": \"Inventory/Request/System Recommend\", \"controller_path\": \"sample.controller\", \"menu_id\": \"033\", \"menu_parent\": \"029\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Inventory/Request/System Recommend/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"034\", \"menu_parent\": \"033\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Inventory/Request/System Recommend/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"035\", \"menu_parent\": \"033\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"History\", \"fxml_path\": \"Inventory/History\", \"controller_path\": \"sample.controller\", \"menu_id\": \"036\", \"menu_parent\": \"028\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Request\", \"fxml_path\": \"Inventory/History/Request\", \"controller_path\": \"sample.controller\", \"menu_id\": \"037\", \"menu_parent\": \"036\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Regular Stocks\", \"fxml_path\": \"Inventory/History/Request/Regular Stocks\", \"controller_path\": \"sample.controller\", \"menu_id\": \"038\", \"menu_parent\": \"037\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Inventory/History/Request/Regular Stocks/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"039\", \"menu_parent\": \"038\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Spareparts \", \"fxml_path\": \"Inventory/History/Request/Regular Stocks/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"040\", \"menu_parent\": \"038\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"System Recommend\", \"fxml_path\": \"Inventory/History/Request/System Recommend\", \"controller_path\": \"sample.controller\", \"menu_id\": \"041\", \"menu_parent\": \"037\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Motorcycle \", \"fxml_path\": \"Inventory/History/Request/System Recommend/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"042\", \"menu_parent\": \"041\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Spareparts \", \"fxml_path\": \"Inventory/History/Request/System Recommend/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"043\", \"menu_parent\": \"041\"}\n"
                + "]";
        JSONParser parser = new JSONParser();
        try {
            try {
                flatMenuItems = (JSONArray) parser.parse(new StringReader(jsonString));
                JSONObject purchasingMainMenu = buildHierarchy("028");
                dissectLeftSideBarJSON(purchasingMainMenu.toJSONString());

            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void purchasingMenuItems() {
        String jsonString = "["
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing\","
                + "\"fxml_path\": \"Purchasing\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"014\","
                + "\"menu_parent\": \"\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Requisition Slip\","
                + "\"fxml_path\": \"Purchasing/Requisition Slip\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"015\","
                + "\"menu_parent\": \"014\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Quotation Request\","
                + "\"fxml_path\": \"Purchasing/Purchasing Quotation Request\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"016\","
                + "\"menu_parent\": \"014\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Quotation\","
                + "\"fxml_path\": \"Purchasing/Purchasing Quotation\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"017\","
                + "\"menu_parent\": \"014\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Order\","
                + "\"fxml_path\": \"Purchasing/Purchasing Order\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"018\","
                + "\"menu_parent\": \"014\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Receiving\","
                + "\"fxml_path\": \"Purchasing/Purchasing Receiving\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"019\","
                + "\"menu_parent\": \"014\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Return\","
                + "\"fxml_path\": \"Purchasing/Purchasing Return\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"020\","
                + "\"menu_parent\": \"014\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"History\","
                + "\"fxml_path\": \"Purchasing/History\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"021\","
                + "\"menu_parent\": \"014\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Requisition Slip\","
                + "\"fxml_path\": \"Purchasing/History/Requisition Slip\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"022\","
                + "\"menu_parent\": \"021\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Quotation Request\","
                + "\"fxml_path\": \"Purchasing/History/Purchasing Quotation Request\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"023\","
                + "\"menu_parent\": \"021\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Quotation\","
                + "\"fxml_path\": \"Purchasing/History/Purchasing Quotation\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"024\","
                + "\"menu_parent\": \"021\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Order\","
                + "\"fxml_path\": \"Purchasing/History/Purchasing Order\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"025\","
                + "\"menu_parent\": \"021\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Receiving\","
                + "\"fxml_path\": \"Purchasing/History/Purchasing Receiving\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"026\","
                + "\"menu_parent\": \"021\""
                + "},"
                + "{"
                + "\"access_level\": \"011\","
                + "\"menu_name\": \"Purchasing Return\","
                + "\"fxml_path\": \"Purchasing/History/Purchasing Return\","
                + "\"controller_path\": \"sample.controller\","
                + "\"menu_id\": \"027\","
                + "\"menu_parent\": \"021\""
                + "}"
                + "]";
        JSONParser parser = new JSONParser();
        try {
            try {
                flatMenuItems = (JSONArray) parser.parse(new StringReader(jsonString));
                JSONObject purchasingMainMenu = buildHierarchy("014");
                System.out.println("json builded:" + purchasingMainMenu.toJSONString());
                dissectLeftSideBarJSON(purchasingMainMenu.toJSONString());

            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void salesMenuItems() {
        // Convert to JSON and process
        String jsonString = "["
                + "{\"access_level\": \"011\", \"menu_name\": \"Sales\", \"fxml_path\": \"Sales\", \"controller_path\": \"sample.controller\", \"menu_id\": \"001\", \"menu_parent\": \"\", \"level\": 0},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Sales\", \"fxml_path\": \"Sales/Sales\", \"controller_path\": \"sample.controller\", \"menu_id\": \"002\", \"menu_parent\": \"001\", \"level\": 1},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/Sales/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"003\", \"menu_parent\": \"002\", \"level\": 2},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/Sales/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"004\", \"menu_parent\": \"002\", \"level\": 2},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inquiry\", \"fxml_path\": \"Sales/Inquiry\", \"controller_path\": \"sample.controller\", \"menu_id\": \"005\", \"menu_parent\": \"001\", \"level\": 1},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/Inquiry/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"006\", \"menu_parent\": \"005\", \"level\": 2},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/Inquiry/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"007\", \"menu_parent\": \"005\", \"level\": 2},"
                + "{\"access_level\": \"011\", \"menu_name\": \"History\", \"fxml_path\": \"Sales/History\", \"controller_path\": \"sample.controller\", \"menu_id\": \"008\", \"menu_parent\": \"001\", \"level\": 1},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Sales\", \"fxml_path\": \"Sales/History/Sales\", \"controller_path\": \"sample.controller\", \"menu_id\": \"013\", \"menu_parent\": \"008\", \"level\": 2},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/History/Inquiry/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"014\", \"menu_parent\": \"013\", \"level\": 3},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/History/Inquiry/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"015\", \"menu_parent\": \"013\", \"level\": 3}"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inquiry\", \"fxml_path\": \"Sales/History/Inquiry\", \"controller_path\": \"sample.controller\", \"menu_id\": \"009\", \"menu_parent\": \"008\", \"level\": 2},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/History/Sales/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"011\", \"menu_parent\": \"009\", \"level\": 3},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/History/Sales/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"012\", \"menu_parent\": \"009\", \"level\": 3},"
                + "]";
        JSONParser parser = new JSONParser();
        try {
            try {
                flatMenuItems = (JSONArray) parser.parse(new StringReader(jsonString));
                JSONObject salesMainMenu = buildHierarchy("001");
                dissectLeftSideBarJSON(salesMainMenu.toJSONString());

            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /*DEPENDS ON DEPARTMENT CODE*/
    //johndave modified 02-15-2025
    public JSONObject buildHierarchy(String menuCode) {
        // Build a map for quick child lookup
        String userDepartment = "011"; // Fetch user's department instead of level
        System.out.println("department: " + userDepartment);
        Map<String, List<JSONObject>> childMap = new HashMap<>();
        JSONObject rootMenuItem = null;

        for (JSONObject item : flatMenuItems) {
            List<String> accessDepartments = getAccessDepartments(item);

            // CONTAINS MATCHING: Allow if the department is listed
            if (!accessDepartments.contains(userDepartment)) {
                continue;
            }

            String menuId = (String) item.get("menu_id");
            String parentId = (String) item.get("menu_parent");

            // Identify the root menu item
            if (menuCode.equals(menuId) && (parentId == null || parentId.isEmpty())) {
                rootMenuItem = item;
            }

            // Group children under their respective parents
            childMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(item);
        }

        // If no root is found, return an empty object
        if (rootMenuItem == null) {
            return new JSONObject();
        }

        return buildSubHierarchy(rootMenuItem, childMap, userDepartment);
    }

    private JSONObject buildSubHierarchy(JSONObject item, Map<String, List<JSONObject>> childMap, String userDepartment) {
        JSONObject node = new JSONObject();
        node.put("menu_id", item.get("menu_id"));
        node.put("menu_name", item.get("menu_name"));
        node.put("menu_parent", item.get("menu_parent"));
        node.put("fxml_path", item.get("fxml_path"));
        node.put("controller_path", item.get("controller_path"));
        node.put("access_level", item.get("access_level"));

        // Retrieve children from the pre-built map
        List<JSONObject> children = childMap.getOrDefault(item.get("menu_id"), Collections.emptyList());

        // Recursively build hierarchy while applying department-based filtering
        JSONArray childrenArray = new JSONArray();
        for (JSONObject child : children) {
            List<String> accessDepartments = getAccessDepartments(child);
            if (accessDepartments.contains(userDepartment)) { // CONTAINS MATCH
                childrenArray.add(buildSubHierarchy(child, childMap, userDepartment));
            }
        }

        if (!childrenArray.isEmpty()) {
            node.put("child", childrenArray);
        } else {
            node.put("child", new JSONArray()); // Ensure an empty array if no children exist
        }

        return node;
    }

    private List<String> getAccessDepartments(JSONObject item) {
        Object accessLevelObj = item.get("access_level");

        if (accessLevelObj == null) {
            return Collections.emptyList(); // Return empty list for invalid entries
        }

        String accessLevelStr = accessLevelObj.toString().trim();
        if (accessLevelStr.isEmpty()) {
            return Collections.emptyList();
        }

        // Split by space and return a list of department codes
        return Arrays.asList(accessLevelStr.split("\\s+"));
    }

    /* USER LEVEL */
    //johndave modified 02-15-2025
//    public JSONObject buildHierarchy(String menuCode) {
//        // Build a map for quick child lookup
//        userLevel = oApp.getUserLevel();
//        Map<String, List<JSONObject>> childMap = new HashMap<>();
//        JSONObject rootMenuItem = null;
//
//        for (JSONObject item : flatMenuItems) {
//            int accessLevel = getAccessLevel(item);
//            if (accessLevel != userLevel) {
//                continue; // STRICT MATCHING: Skip if access level does not match user level
//            }
//            String menuId = (String) item.get("menu_id");
//            String parentId = (String) item.get("menu_parent");
//
//            // Identify the root menu item
//            if (menuCode.equals(menuId) && (parentId == null || parentId.isEmpty())) {
//                rootMenuItem = item;
//            }
//
//            // Group children under their respective parents
//            childMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(item);
//        }
//
//        // If no root is found, return an empty object
//        if (rootMenuItem == null) {
//            return new JSONObject();
//        }
//
//        return buildSubHierarchy(rootMenuItem, childMap);
//    }
//
//    private JSONObject buildSubHierarchy(JSONObject item, Map<String, List<JSONObject>> childMap) {
//        userLevel = oApp.getUserLevel();
//        JSONObject node = new JSONObject();
//        node.put("menu_id", item.get("menu_id"));
//        node.put("menu_name", item.get("menu_name"));
//        node.put("menu_parent", item.get("menu_parent"));
//        node.put("fxml_path", item.get("fxml_path"));
//        node.put("controller_path", item.get("controller_path"));
//        node.put("access_level", item.get("access_level"));
//
//        // Retrieve children from the pre-built map
//        List<JSONObject> children = childMap.getOrDefault(item.get("menu_id"), Collections.emptyList());
//
//        // Recursively build hierarchy while applying STRICT access level filtering
//        JSONArray childrenArray = new JSONArray();
//        for (JSONObject child : children) {
//            int accessLevel = getAccessLevel(child);
//            if (accessLevel == userLevel) { // STRICT MATCH
//                childrenArray.add(buildSubHierarchy(child, childMap));
//            }
//        }
//
//        if (!childrenArray.isEmpty()) {
//            node.put("child", childrenArray);
//        } else {
//            node.put("child", new JSONArray()); // Ensure an empty array if no children exist
//        }
//
//        return node;
//    }
//
//    private int getAccessLevel(JSONObject item) {
//        Object accessLevelObj = item.get("access_level");
//
//        if (accessLevelObj == null) {
//            return -1; // Indicate an invalid level (ensures filtering out)
//        }
//
//        String accessLevelStr = accessLevelObj.toString().trim();
//        if (accessLevelStr.isEmpty()) {
//            return -1; // Skip empty access levels
//        }
//
//        try {
//            return Integer.parseInt(accessLevelStr);
//        } catch (NumberFormatException e) {
//            return -1; // Skip invalid access levels
//        }
//    }
    /*johndave modified 02-14-2025*/
    //    Pag meron ng JSONArray mag dagdag ng parameter
//    private JSONObject buildHierarchy(String menuCode) {
//        for (Object item : flatMenuItems) {
//            JSONObject jsonItem = (JSONObject) item;
//
//            // Look for the root menu item
//            if (jsonNameEquals((String) jsonItem.get("menu_id"), menuCode)
//                    && (jsonItem.get("menu_parent") == null || jsonNameEquals((String) jsonItem.get("menu_parent"), ""))) {
//
//                // Directly return the root object without extra nesting
//                return buildSubHierarchy(jsonItem);
//            }
//        }
//        return new JSONObject(); // Return empty object if no matching root is found
//    }
//
//    private JSONObject buildSubHierarchy(JSONObject item) {
//        JSONObject node = new JSONObject();
//        node.put("menu_id", item.get("menu_id"));
//        node.put("menu_name", item.get("menu_name"));
//        node.put("menu_parent", item.get("menu_parent"));
//        node.put("fxml_path", item.get("fxml_path"));
//        node.put("controller_path", item.get("controller_path"));
//        node.put("access_level", item.get("access_level"));
//
//        List<JSONObject> children = new ArrayList<>();
//        for (Object flatItem : flatMenuItems) {
//            JSONObject childItem = (JSONObject) flatItem;
//            // Check if this item is a child of the current item
//            if (jsonNameEquals((String) childItem.get("menu_parent"), (String) item.get("menu_id"))) {
//                children.add(buildSubHierarchy(childItem)); // Recursively build child hierarchy
//            }
//        }
//
//        if (!children.isEmpty()) {
//            JSONArray childrenArray = new JSONArray();
//            childrenArray.addAll(children);
//            node.put("child", childrenArray);
//        } else {
//            node.put("child", new JSONArray()); // Ensure an empty array if no children exist
//        }
//
//        return node;
//    }
//    private boolean jsonNameEquals(String a, String b) {
//        return a == null ? b == null : a.equalsIgnoreCase(b);
//    }
    private void dissectLeftSideBarJSON(String fsValue) {
        System.out.println("json! " + fsValue);

        if (fsValue == null || fsValue.isEmpty()) {
            System.err.println("Invalid JSON string.");
            return;
        }

        JSONParser loParser = new JSONParser();
        try {
            Object parsedJson = loParser.parse(fsValue);
            JSONArray laMaster;

            // Ensure we have an array
            if (parsedJson instanceof JSONArray) {
                laMaster = (JSONArray) parsedJson;
            } else if (parsedJson instanceof JSONObject) {
                laMaster = new JSONArray();
                laMaster.add(parsedJson);
            } else {
                System.err.println("Invalid JSON format.");
                return;
            }

            TreeItem<String> root = new TreeItem<>("root");
            menuLocationMap.clear(); // Clear previous mappings

            for (Object objMaster : laMaster) {
                if (!(objMaster instanceof JSONObject)) {
                    System.err.println("Skipping invalid entry: " + objMaster);
                    continue;
                }

                JSONObject loParent = (JSONObject) objMaster;
                if (!loParent.containsKey("menu_name")) {
                    continue; // Skip invalid entries
                }

                String parentName = String.valueOf(loParent.get("menu_name"));
                String location = loParent.containsKey("fxml_path") ? String.valueOf(loParent.get("fxml_path")) : "";

                TreeItem<String> parentNode = new TreeItem<>(parentName);
                menuLocationMap.put(parentNode, location); // Store location

                if (loParent.containsKey("child") && loParent.get("child") instanceof JSONArray) {
                    JSONArray laDetail = (JSONArray) loParent.get("child");
                    addChildren(parentNode, laDetail);
                }

                root.getChildren().add(parentNode);
            }

            // Assign tree structure
            if (tvLeftSideBar != null) {
                tvLeftSideBar.setRoot(root);
                tvLeftSideBar.setShowRoot(false);

                if (!isListenerLeftAdded) {
                    isListenerLeftAdded = true;
                    tvLeftSideBar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null) {
                            handleSelection(newValue);
                        }
                    });
                }
            } else {
                System.err.println("tvLeftSideBar is not initialized.");
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    private void addChildren(TreeItem<String> parentNode, JSONArray childrenArray) {
        for (Object obj : childrenArray) {
            JSONObject loDetail = (JSONObject) obj;
            if (loDetail == null || !loDetail.containsKey("menu_name")) {
                continue; // Skip invalid child entries
            }

            String parentName = String.valueOf(loDetail.get("menu_name"));
            String location = loDetail.containsKey("fxml_path") ? String.valueOf(loDetail.get("fxml_path")) : "";

            TreeItem<String> childNode = new TreeItem<>(parentName);
            menuLocationMap.put(childNode, location); // Store location for this node

            // Recursively add more child levels if present
            if (loDetail.containsKey("child") && loDetail.get("child") instanceof JSONArray) {
                JSONArray subChildren = (JSONArray) loDetail.get("child");
                addChildren(childNode, subChildren);
            }

            parentNode.getChildren().add(childNode);
        }
    }

    private void toggleSidebarWidth() {
        if (tvLeftSideBar != null && tvLeftSideBar.getRoot() != null) {
            int calculatedWidth = calculateTreeViewWidth(tvLeftSideBar.getRoot());

            // Expand dynamically based on TreeView size
            Platform.runLater(() -> {
                anchorLeftSideBarMenu.setPrefWidth(calculatedWidth);
            });
        }

    }

    /**
     * Dynamically calculates the required width based on the TreeItems.
     */
    private int calculateTreeViewWidth(TreeItem<String> root) {
        if (root == null) {
            return 200; // Default width if no items exist
        }
        int baseWidth = 200; // Minimum width
        int textPadding = 20; // Small padding for extra space

        int longestTextWidth = getMaxTextWidth(root); // Dynamically get text width

        double parentWidth = anchorLeftSideBarMenu.getParent().getLayoutBounds().getWidth();

        int calculatedWidth = baseWidth + longestTextWidth + textPadding;
        return (int) Math.min(calculatedWidth, parentWidth * 0.9); // Ensure it fits the UI
    }

    private int getMaxTextWidth(TreeItem<String> item) {
        if (item == null) {
            return 0;
        }

        int maxWidth = getTextWidth(item.getValue());

        for (TreeItem<String> child : item.getChildren()) {
            maxWidth = Math.max(maxWidth, getMaxTextWidth(child));
        }

        return maxWidth;
    }

    /**
     * Estimates text width based on character count.
     */
    private int getTextWidth(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int charWidth = 7; // Approximate character width in pixels
        return text.length() * charWidth;
    }

    private void handleSelection(TreeItem<String> newValue) {
        if (newValue == null || !newValue.isLeaf() || newValue.getValue() == null || newValue.getValue().isEmpty()) {
            System.out.println("Invalid selection or empty value.");
            return;
        }

        String selectedMenu = newValue.getValue();
        String sLocation = menuLocationMap.getOrDefault(newValue, ""); // Get location from map

        System.out.println("Selected: " + selectedMenu + " | Location: " + sLocation);

        switch (selectedMenu) {
            case "Motorcycle":     //johndave modified 02-15-2025
                switch (sLocation.toLowerCase()) {
                    case "sales/sales/motorcycle":
                        sformname = sLocation;
                        ShowMessageFX.Information("Navigation", "You selected", sLocation);
                        break;
                    case "sales/inquiry/motorcycle":
                        sformname = sLocation;
                        System.out.println(sformname);
                        ShowMessageFX.Information("test", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Spareparts":     //johndave modified 02-15-2025
                switch (sLocation.toLowerCase()) {
                    case "sales/sales/spareparts":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/motorycle_sales.fxml";
                        ShowMessageFX.Information("Navigation", "You selected", sLocation);
                        break;
                    case "sales/inquiry/spareparts":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/motorycle_inquiry.fxml";
                        ShowMessageFX.Information("test", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Requisition Slip":     //johndave modified 02-15-2025
                switch (sLocation.toLowerCase()) {
                    case "purchasing/requisition slip":
                        sformname = "";
                        ShowMessageFX.Information("Requisition Slip", "You selected", sLocation);
                        break;
                    case "purchasing/history/requisition slip":
                        sformname = "";
                        ShowMessageFX.Information("Requisition Slip", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Purchasing Quotation Request":     //johndave modified 02-15-2025
                switch (sLocation.toLowerCase()) {
                    case "purchasing/purchasing quotation request":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Quotation Request", "You selected", sLocation);
                        break;
                    case "purchasing/history/purchasing quotation request":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Quotation Request", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Purchasing Quotation":     //johndave modified 02-15-2025
                switch (sLocation.toLowerCase()) {
                    case "purchasing/purchasing quotation":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Quotation", "You selected", sLocation);
                        break;
                    case "purchasing/history/purchasing quotation":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Quotation", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Purchasing Order":    //johndave modified 02-15-2025
                switch (sLocation.toLowerCase()) {
                    case "purchasing/purchasing order":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_Entry.fxml";
                        ShowMessageFX.Information("Purchasing Order", "You selected", sLocation);
                        break;
                    case "purchasing/history/purchasing order":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_History.fxml";
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Purchasing Receiving":    //johndave modified 02-15-2025
                switch (sLocation.toLowerCase()) {
                    case "purchasing/purchasing receiving":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Receiving", "You selected", sLocation);
                        break;
                    case "purchasing/history/purchasing receiving":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Receiving", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Purchasing Return":
                switch (sLocation.toLowerCase()) {
                    case "purchasing/purchasing return":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Return", "You selected", sLocation);
                        break;
                    case "purchasing/history/purchasing return":
                        sformname = "";
                        ShowMessageFX.Information("Purchasing Return", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Delivery Acceptance":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptanceEntry.fxml";
                break;
            case "Payment Request":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml";
                break;
            default:
                sformname = "";
                break;
        }

        // Load the corresponding form
        if (oApp != null) {
            boolean isNewTab = (checktabs(SetTabTitle(sformname)) == 1);
            if (isNewTab) {
                if (!sformname.isEmpty() && sformname.contains(".fxml")) {
                    setScene2(loadAnimate(sformname));
                } else {
                    ShowMessageFX.Warning(null, "Computerized Accounting System", "NO FORM NAME");
                }
            } else {
                ShowMessageFX.Warning(null, "Computerized Accounting System", "THIS FORM IS ALREADY OPEN");
            }
            setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
            for (ToggleButton navButton : toggleBtnLeftUpperSideBar) {
                navButton.setSelected(false);
            }
            pane.requestFocus();
        }
    }

    /*LEFT SIDE BAR MENU ITEMS */
    private void notifMenuItems() {
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

        dissectRightSideBarJSON(laMaster.toJSONString());
    }

    private void dissectRightSideBarJSON(String fsValue) {
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

            TreeItem<String> root = new TreeItem<>("root");

            for (Object objMaster : laMaster) {
                JSONObject loParent = (JSONObject) objMaster;
                if (loParent == null || !loParent.containsKey("parent")) {
                    continue; // Skip invalid entries
                }

                TreeItem<String> parentNode = new TreeItem<>(String.valueOf(loParent.get("parent")));

                if (loParent.containsKey("child") && loParent.get("child") instanceof JSONArray) {
                    JSONArray laDetail = (JSONArray) loParent.get("child");
                    addChildren(parentNode, laDetail);
                }

                root.getChildren().add(parentNode);
            }

            // Assign tree structure
            if (tvRightSideBar != null) {
                tvRightSideBar.setRoot(root);
                tvRightSideBar.setShowRoot(false);
                if (!isListenerRightAdded) {
                    isListenerRightAdded = true;
                    tvRightSideBar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
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
                                default:
                                    sformname = "";
                                    break;
                            }

                            // Load the corresponding form
                            if (oApp != null) {
                                boolean isNewTab = (checktabs(SetTabTitle(sformname)) == 1);
                                if (isNewTab) {
                                    if (!sformname.isEmpty() && sformname.contains(".fxml")) {
                                        setScene2(loadAnimate(sformname));
                                    } else {
                                        ShowMessageFX.Warning(null, "Computerized Accounting System", "NO FORM NAME");
                                    }
                                } else {
                                    ShowMessageFX.Warning(null, "Computerized Accounting System", "THIS FORM IS ALREADY OPEN");
                                }
                                setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
                                for (ToggleButton navButton : toggleBtnRightSideBar) {
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
                    System.err.println("tvChild1 is not initialized.");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * INITIALIZE CLICK BUTTON*
     */
    private void initButtonClickActions() {
        btnClose.setOnAction(this::handleButtonAction);
        btnMinimize.setOnAction(this::handleButtonAction);
    }

    /**
     * HANDLE BUTTON ACTION *
     */
    private void handleButtonAction(ActionEvent event) {
        Object source = event.getSource();
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

    /**
     * LOAD USER INFO*
     */
    private void loadUserInfo() {
        AppUser.setText(oApp.getLogName() + " || " + oApp.getDivisionName());
    }

    /**
     * GET DEPARTMENT*
     */
    private void checkDepartment() {
        // Validate and hide btnSales if department is 026
        if ("022".equals(oApp.getDepartment())) { // Ensure the department is compared correctly
            btnSales.setVisible(false);
            btnSales.setManaged(false);  // Hide the button
        }
    }

    /**
     * GET TIME*
     */
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

    private void notificationChecker() {
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
        cartCount += (int) (Math.random() * 5);  // Adds 0-4 random notifications

        // Update the label's text on the JavaFX Application Thread
        Platform.runLater(() -> {
            lblNotifCount.setText(String.valueOf(notificationCount));
            lblAddToCartCount.setText(String.valueOf(cartCount));
        });
    }

    private void openPDF(String filePath) {
        File pdfFile = new File(filePath);

        if (pdfFile.exists()) {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(pdfFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error opening the file.");
                }
            } else {
                System.out.println("Desktop is not supported on this platform.");
            }
        } else {
            System.out.println("File not found: " + pdfFile.getAbsolutePath());
        }
    }
}
