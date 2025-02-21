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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DashboardController implements Initializable {

    private GRider oApp;
    private String lastClickedBtnLeftSideBar = "";
    private String lastClickedBtnRightSideBar = "";
//    private String psDefaultScreenFXML = "/com/rmj/guanzongroup/sidebarmenus/views/DefaultScreen.fxml";
    private String psDefaultScreenFXML = "/com/rmj/guanzongroup/sidebarmenus/views/Log_In.fxml";
    private String psDefaultScreenFXML2 = "/com/rmj/guanzongroup/sidebarmenus/views/DefaultScreen.fxml";
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
    private boolean isListenerLeftAdded = false;
    private boolean isListenerRightAdded = false;

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
            btnAccountsReceivable,
            btnGeneralAccounting,
            btnOthers,
            btnHelp,
            btnLogout,
            btnSysMonitor,
            btnAddToCart;

    @FXML
    private Pane pane;

    @FXML
    private Label DateAndTime, AppUser, lblAddToCartCount, lblNotifCount, lblVersion;

    @FXML
    private StackPane MainStack;

    @FXML
    private BorderPane main_container;

    @FXML
    private VBox nav_bar;

    @FXML
    private VBox nav_bar11;

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
            setAppVersion("v1.00.01");
            checkDepartment();
            getTime();
            initButtonClickActions();
            notificationChecker();

            setTreeViewStyle(tvLeftSideBar);
            setTreeViewStyle(tvRightSideBar);

            setDropShadowEffectsLeftSideBar(anchorLeftSideBarMenu);
            setDropShadowEffectsRightSideBar(anchorRightSideBarMenu);

            monitorMenuItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAppVersion(String fsValue) {
        lblVersion.setText("CAS " + fsValue);
    }

    private void initButtonVisible(String fsAccessLevel, ToggleButton foTButton, String fsContainsTo) {
        foTButton.setVisible(fsAccessLevel.contains(fsContainsTo));
        foTButton.setManaged(fsAccessLevel.contains(fsContainsTo));
    }

    private void setAnchorPaneVisibleManage(boolean fbVisibleManage, Node... nodes) {
        for (Node node : nodes) {
            if (node != null) {
                node.setVisible(fbVisibleManage);
                node.setManaged(fbVisibleManage);
            }
        }
    }

    @FXML
    private void pane(ActionEvent event) {
        setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);

        for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
            toggleBtnLeftUpperSideBar[i].setSelected(false); // Set each button's selected state to false
        }

    }

    /**
     * PANE LOAD *
     */
    public void setPane() {
        pane.setOnMouseClicked(event -> {
            setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
            for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
                toggleBtnLeftUpperSideBar[i].setSelected(false);
            }

            for (int i = 0; i < toggleBtnRightSideBar.length; i++) {
                toggleBtnRightSideBar[i].setSelected(false);
            }
        });
    }

    public void setAnchorPane() {
        pane.setOnMouseClicked(event -> {
            setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
            for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
                toggleBtnLeftUpperSideBar[i].setSelected(false);
            }

            for (int i = 0; i < toggleBtnRightSideBar.length; i++) {
                toggleBtnRightSideBar[i].setSelected(false);
            }
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
                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryMaintenance.fxml":
                    return "Inventory Maintenance";

                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsAccreditation.fxml":
                    return "Accounts Accreditation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsAccreditationHistory.fxml":
                    return "Accounts Accreditation History";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_Entry.fxml":
                    return "Purchase Order";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_History.fxml":
                    return "Purchase Order History";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml":
                    return "Purchase Order Receiving";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml":
                    return "Purchasing Receiving Confirmation";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml":
                    return "Payment Request";

                /* OTHERS/PARAMETERS/ADDRESS */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Barangay.fxml":
                    return "Barangay";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Country.fxml":
                    return "Country";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Province.fxml":
                    return "Province";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Region.fxml":
                    return "Region";

                /*PARAMETERS/BANKS*/
                case "/com/rmj/guanzongroup/sidebarmenus/views/Banks.fxml":
                    return "Banks";
                case "/com/rmj/guanzongroup/sidebarmenus/views/BanksBranches.fxml":
                    return "Banks Branches";

                /* OTHERS/PARAMETERS/CATEGORY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Category.fxml":
                    return "Category";
                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel2.fxml":
                    return "Category Level 2";
                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel3.fxml":
                    return "Category Level 3";
                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel4.fxml":
                    return "Category Level 4";

                /* OTHERS/PARAMETERS/COLOR */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Color.fxml":
                    return "Color";
                case "/com/rmj/guanzongroup/sidebarmenus/views/ColorDetail.fxml":
                    return "Color Detail";

                /* OTHERS/PARAMETERS/COMPANY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/AffiliatedCompany.fxml":
                    return "AffiliatedCompany";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Branch.fxml":
                    return "Branch";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Company.fxml":
                    return "Company";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Department.fxml":
                    return "Department";

                /* OTHERS/PARAMETERS/INVENTORY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryLocation.fxml":
                    return "Inventory Location";
                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryType.fxml":
                    return "Inventory Type";
                /* OTHERS/PARAMETERS/LABOR */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Labor.fxml":
                    return "Labor";
                case "/com/rmj/guanzongroup/sidebarmenus/views/LaborCategory.fxml":
                    return "Labor Category";
                case "/com/rmj/guanzongroup/sidebarmenus/views/LaborModel.fxml":
                    return "Labor Model";

                /* OTHERS/PARAMETERS/OTHERS */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Brand.fxml":
                    return "Brand";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Made.fxml":
                    return "Made";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Model.fxml":
                    return "Model";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Measure.fxml":
                    return "Measure";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Relationship.fxml":
                    return "Relationship";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Salesman.fxml":
                    return "Salesman";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Section.fxml":
                    return "Section";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Size.fxml":
                    return "Size";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Term.fxml":
                    return "Term";
                case "/com/rmj/guanzongroup/sidebarmenus/views/Warehouse.fxml":
                    return "Warehouse";

                /* OTHERS/CLIENTS */
                case "/com/rmj/guanzongroup/sidebarmenus/views/ClientMasterParameter.fxml":
                    return "Client";

                /* OTHERS/PARAMETERS/INVENTORY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryParam.fxml":
                    return "Inventory Parameter";
                case "/com/rmj/guanzongroup/sidebarmenus/views/InventorySerialParam.fxml":
                    return "Inventory Serial Parameter";

                /* ACCOUNTS/ACCOUNTS/ACCOUNTS PAYABLE */
                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsPayable.fxml":
                    return "Accounts Payable";

                /* ACCOUNTS/ACCOUNTS/ACCOUNTS RECEIVABLE */
                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsReceivable.fxml":
                    return "Accounts Receivable";

                default:
                    return null;
            }
        }
        return null;
    }

    /**
     * SCREEN INTERFACE *
     */
    public void triggervbox() {
        nav_bar.setDisable(true);
        nav_bar11.setDisable(true);

    }

    public void triggervbox2() {
        setAnchorPaneVisibleManage(true, anchorRightSideBarMenu);
        nav_bar.setDisable(false);
        nav_bar11.setDisable(false);
        setScene(loadAnimateAnchor(psDefaultScreenFXML2));

        toggleGroupLowerBtn = new ToggleGroup();
        toggleBtnLeftLowerSideBar = new ToggleButton[]{
            btnHelp,
            btnLogout
        };

        String[] tooltipTexts = {
            "Help",
            "Logout"
        };

        for (int i = 0; i < toggleBtnLeftLowerSideBar.length; i++) {
            toggleBtnLeftLowerSideBar[i].setTooltip(new Tooltip(tooltipTexts[i]));
            toggleBtnLeftLowerSideBar[i].setToggleGroup(toggleGroupLowerBtn);
        }

    }

    private ScreenInterface getController(String fsValue) {
        if (fsValue.contains(".fxml")) {
            switch (fsValue) {
                case "/com/rmj/guanzongroup/sidebarmenus/views/Log_In.fxml":
                    LoginControllerHolder locontroller = new LoginControllerHolder();
                    locontroller.setMainController(this);
                    return new Log_InController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DefaultScreen.fxml":
                    return new DefaultScreenController();

                /* INVENTORY/INVENTORY/ */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryMaintenance.fxml":
//                    return new InventoryMaintenanceController();

                /* PURCHASING/PURCHASING/ */
                case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml":
                    return new SampleForm1Controller();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml":
                    return new SampleForm2Controller();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_Entry.fxml":
                    return new PurchasingOrder_EntryController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsAccreditation.fxml":
//                    return new AccountsAccreditationController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsAccreditationHistory.fxml":
//                    return new AccountsAccreditationHistoryController();
                // case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_History.fxml":
                // return new PurchasingOrder_HistoryController();

                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml":
                    return new DeliveryAcceptance_EntryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml":
                    return new DeliveryAcceptance_ConfirmationController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml":
                    return new PaymentRequestController();

                /* OTHERS/PARAMETERS/ADDRESS */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Barangay.fxml":
                    return new BarangayController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Country.fxml":
                    return new CountryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Province.fxml":
                    return new ProvinceController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Region.fxml":
                    return new RegionController();

                /* OTHERS/PARAMETERS/BANKS */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Banks.fxml":
                    return new BanksController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/BanksBranches.fxml":
                    return new BanksBranchesController();

                /* OTHERS/PARAMETERS/CATEGORY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Category.fxml":
                    return new CategoryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel2.fxml":
                    return new CategoryLevel2Controller();
                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel3.fxml":
                    return new CategoryLevel3Controller();
                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel4.fxml":
                    return new CategoryLevel4Controller();

                /* OTHERS/PARAMETERS/CATEGORY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Color.fxml":
                    return new ColorController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/ColorDetail.fxml":
                    return new ColorDetailController();

                /* OTHERS/PARAMETERS/COMPANY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/AffiliatedCompany.fxml":
                    return new AffiliatedCompanyController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Branch.fxml":
                    return new BranchController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Company.fxml":
                    return new CompanyController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Department.fxml":
                    return new DepartmentController();

                /* OTHERS/PARAMETERS/INVENTORY */
                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryLocation.fxml":
                    return new InventoryLocationController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryType.fxml":
                    return new InventoryTypeController();

                /* OTHERS/PARAMETERS/LABOR */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Labor.fxml":
                    return new LaborController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/LaborCategory.fxml":
                    return new LaborCategoryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/LaborModel.fxml":
                    return new LaborModelController();

                /* OTHERS/PARAMETERS/OTHERS */
                case "/com/rmj/guanzongroup/sidebarmenus/views/Brand.fxml":
                    return new BrandController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Made.fxml":
                    return new MadeController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Model.fxml":
                    return new ModelController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Measure.fxml":
                    return new MeasureController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Relationship.fxml":
                    return new RelationshipController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Salesman.fxml":
                    return new SalesmanController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Section.fxml":
                    return new SectionController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Size.fxml":
                    return new SizeController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Term.fxml":
                    return new TermController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/Warehouse.fxml":
                    return new WarehouseController();

                /* OTHERS/Clients */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/ClientMasterParameter.fxml":
//                 return new ClientMasterParameterController();
//
//                /* OTHERS/INVENTORY */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryParam.fxml":
//                    return new InventoryParamController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/InventorySerialParam.fxml":
//                    return new InventorySerialParamController();
//
//                /* ACOUNTS/ACOUNTS/ACOUNTS PAYABLE */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsPayable.fxml":
//                    return new AccountsPayableController();
//
//                /* ACOUNTS/ACOUNTS/ACOUNTS RECEIVABLE */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsReceivable.fxml":
//                    return new AccountsReceivableController();
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
        tabpane.setOnMouseClicked(event -> {

            setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
            for (int i = 0; i < toggleBtnLeftUpperSideBar.length; i++) {
                toggleBtnLeftUpperSideBar[i].setSelected(false);
            }

            for (int i = 0; i < toggleBtnRightSideBar.length; i++) {
                toggleBtnRightSideBar[i].setSelected(false);
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
                double tabHeaderHeight = tabpane.lookup(".tab-header-area").getBoundsInParent().getHeight();

                targetTabIndex = (int) (mouseX / 180);
                if (mouseY < tabHeaderHeight) {

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

        Tab newTab = new Tab(SetTabTitle(fsFormName));
        newTab.setContent(new javafx.scene.control.Label("Content of Tab " + fsFormName));
        newTab.setContextMenu(createContextMenu(tabpane, newTab, oApp));

        tabName.add(SetTabTitle(fsFormName));

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

    private void setScene(Pane foPane) {
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
        tabpane.setVisible(false);
        tabpane.setManaged(false);
    }

    public void setScene2(TabPane foPane) {
        workingSpace.getChildren().clear();
        workingSpace.getChildren().add(foPane);
    }

    public Pane loadAnimateAnchor(String fsFormName) {
        ScreenInterface fxObj = getController(fsFormName);
        fxObj.setGRider(oApp);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxObj.getClass().getResource(fsFormName));
        fxmlLoader.setController(fxObj);

        Pane root;
        try {
            root = (Pane) fxmlLoader.load();
            FadeTransition ft = new FadeTransition(Duration.millis(1500));
            ft.setNode(root);
            ft.setFromValue(1);
            ft.setToValue(1);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            ft.play();

            //            if(fsFormName.equals(psDefaultScreenFXML))
//            FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), root);
//            fadeIn.setFromValue(0);
//            fadeIn.setToValue(1);
//            fadeIn.play();
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

    private void closeSelectTabs(TabPane tabPane, Tab currentTab) {
        if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure, do you want to close tab?")) {
            // Remove the tab
            if (tabPane.getTabs().removeIf(tab -> tab == currentTab)) {
                if (tabPane.getTabs().isEmpty()) {
                    unloadForm unload = new unloadForm();
                    StackPane myBox = (StackPane) tabPane.getParent();
                    myBox.getChildren().clear();
                    myBox.getChildren().add(unload.getScene(psDefaultScreenFXML2, oApp));
                }
            }
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
        if (tabPane == null) {
            System.out.println("tabPane is null");
            return;
        }

        if (ShowMessageFX.YesNo(null, "Close All Tabs", "Are you sure, do you want to close all tabs?")) {
            if (tabName != null) {
                tabName.clear();
            } else {
                System.out.println("tabName is null");
            }

            tabPane.getTabs().clear();

            unloadForm unload = new unloadForm();

            if (tabPane.getParent() == null) {
                System.out.println("Parent of tabPane is null");
                return;
            }

            StackPane myBox = (StackPane) tabPane.getParent();
            myBox.getChildren().clear();
            myBox.getChildren().add(unload.getScene(psDefaultScreenFXML2, oApp));
        }
    }

    public void Tabclose() {
        int tabsize = tabpane.getTabs().size();
        if (tabsize == 1) {
            setScene(loadAnimateAnchor(psDefaultScreenFXML2));
        }
    }

    public void Tabclose(TabPane tabpane) {
        int tabsize = tabpane.getTabs().size();
        if (tabsize == 1) {
            setScene(loadAnimateAnchor(psDefaultScreenFXML2));
        }
    }

    private void initMenu() {
        setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
        setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
    }

    private void setTreeViewStyle(TreeView<String> treeView) {
        treeView.setCellFactory(tv -> new TreeCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: #DFDFDF; -fx-border-color: #DFDFDF;");
                    if (!getStyleClass().contains("empty-tree-cell")) {
                        getStyleClass().add("empty-tree-cell");
                    }
                } else {
                    setText(item);
                    setGraphic(getTreeItem().getGraphic());
                    setStyle(null);
                    getStyleClass().remove("empty-tree-cell");

                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 1) {
                            TreeItem<String> treeItem = getTreeItem();
                            if (treeItem != null && !treeItem.isLeaf()) {
                                treeItem.setExpanded(!treeItem.isExpanded());
                                event.consume();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setDropShadowEffectsLeftSideBar(AnchorPane anchorPane) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(20);
        shadow.setWidth(21.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));

        shadow.setOffsetX(2);
        shadow.setOffsetY(0);

        anchorPane.setEffect(shadow);
    }

    private void setDropShadowEffectsRightSideBar(AnchorPane anchorPane) {
        DropShadow shadow = new DropShadow();
        shadow.setRadius(20);
        shadow.setWidth(21.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.3));

        shadow.setOffsetX(-2);
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
            btnAccountsReceivable,
            btnGeneralAccounting,
            btnOthers
        };

        // Tooltip texts for each button
        String[] tooltipTexts = {
            "Inventory",
            "Purchasing",
            "Sales",
            "Service Repair",
            "AR/AP (Accounts Payable and Receivable)",
            "General Accounting",
            "Others"
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
            btnSysMonitor,
            btnAddToCart
        };

        // Tooltip texts for each button
        String[] tooltipTexts = {
            "Sys Monitor",
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

        String[] tooltipTexts = {
            "Help",
            "Exit"
        };

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
    private void switchSysMonitor(ActionEvent event) {
        toggleRightSideBarMenuButton("switchSysMonitor", 0);
        monitorMenuItems();

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
    private void switchAccountsReceivable(ActionEvent event) {
        accountsMenuItems();
        toggleLeftSideBarMenuButton("switchAccountsReceivable", 4);

    }

    @FXML
    private void switchGeneralAccounting(ActionEvent event) {
        tvLeftSideBar.setRoot(null);
        toggleLeftSideBarMenuButton("switchGeneralAccounting", 5);

    }

    @FXML
    private void switchOthers(ActionEvent event) {
        othersMenuItems();
        toggleLeftSideBarMenuButton("switchOthers", 6);
        toggleSidebarWidth();
    }

    @FXML
    private void switchHelp(ActionEvent event) {
        openPDF("D:/Help.pdf");
        btnHelp.setSelected(false);
    }

    @FXML
    private void switchLogout(ActionEvent event) {

        if (LoginControllerHolder.getLogInStatus()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("GUANZON GROUP OF COMPANIES");
            alert.setHeaderText("Are you sure you want to logout?");

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
                setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
                ToggleGroupControlLowerLeftSideBar();
                setScene(loadAnimateAnchor(psDefaultScreenFXML));
                btnLogout.setSelected(false);
                LoginControllerHolder.setLogInStatus(false);
            } else {
                btnLogout.setSelected(false);
            }
        } else {
            LoginControllerHolder.setLogInStatus(false);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }

    }

    /**
     * TOGGLE SUBMENU ON LEFT BUTTON*
     */
    private void toggleLeftSideBarMenuButton(String buttonId, Integer btnIndex) {
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
                + "  {\"access_level\": \"011\", \"menu_name\": \"Inventory Maintenance\", \"fxml_path\": \"Inventory/Inventory Maintenance\", \"controller_path\": \"sample.controller\", \"menu_id\": \"45\", \"menu_parent\": \"028\"},\n"
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
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing\", \"fxml_path\": \"Purchasing\", \"controller_path\": \"sample.controller\", \"menu_id\": \"001\", \"menu_parent\": \"\"},\n"
                //entry
                + "  {\"access_level\": \"011\", \"menu_name\": \"Entry\", \"fxml_path\": \"Purchasing/Entry\", \"controller_path\": \"sample.controller\", \"menu_id\": \"002\", \"menu_parent\": \"001\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Accounts Accreditations\", \"fxml_path\": \"Purchasing/Entry/Accounts Accreditations\", \"controller_path\": \"sample.controller\", \"menu_id\": \"003\", \"menu_parent\": \"002\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Requisition Slip\", \"fxml_path\": \"Purchasing/Entry/Requisition Slip\", \"controller_path\": \"sample.controller\", \"menu_id\": \"004\", \"menu_parent\": \"002\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Quotation Request\", \"fxml_path\": \"Purchasing/Entry/Purchasing Quotation Request\", \"controller_path\": \"sample.controller\", \"menu_id\": \"005\", \"menu_parent\": \"002\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Quotation\", \"fxml_path\": \"Purchasing/Entry/Purchasing Quotation\", \"controller_path\": \"sample.controller\", \"menu_id\": \"006\", \"menu_parent\": \"002\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Order\", \"fxml_path\": \"Purchasing/Entry/Purchasing Order\", \"controller_path\": \"sample.controller\", \"menu_id\": \"007\", \"menu_parent\": \"002\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Receiving\", \"fxml_path\": \"Purchasing/Entry/Purchasing Receiving\", \"controller_path\": \"sample.controller\", \"menu_id\": \"008\", \"menu_parent\": \"002\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Return\", \"fxml_path\": \"Purchasing/Entry/Purchasing Return\", \"controller_path\": \"sample.controller\", \"menu_id\": \"009\", \"menu_parent\": \"002\"},\n"
                //approval
                + "  {\"access_level\": \"011\", \"menu_name\": \"Approval\", \"fxml_path\": \"Purchasing/Approval\", \"controller_path\": \"sample.controller\", \"menu_id\": \"010\", \"menu_parent\": \"001\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Accounts Accreditations\", \"fxml_path\": \"Purchasing/Approval/Accounts Accreditations\", \"controller_path\": \"sample.controller\", \"menu_id\": \"011\", \"menu_parent\": \"010\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Requisition Slip\", \"fxml_path\": \"Purchasing/Approval/Requisition Slip\", \"controller_path\": \"sample.controller\", \"menu_id\": \"012\", \"menu_parent\": \"010\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Quotation Request\", \"fxml_path\": \"Purchasing/Approval/Purchasing Quotation Request\", \"controller_path\": \"sample.controller\", \"menu_id\": \"013\", \"menu_parent\": \"010\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Quotation\", \"fxml_path\": \"PurchasingApproval/Purchasing Quotation\", \"controller_path\": \"sample.controller\", \"menu_id\": \"014\", \"menu_parent\": \"010\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Order\", \"fxml_path\": \"Purchasing/Approval/Purchasing Order\", \"controller_path\": \"sample.controller\", \"menu_id\": \"015\", \"menu_parent\": \"010\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Receiving\", \"fxml_path\": \"Purchasing/Approval/Purchasing Receiving\", \"controller_path\": \"sample.controller\", \"menu_id\": \"016\", \"menu_parent\": \"010\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Return\", \"fxml_path\": \"Purchasing/Approval/Purchasing Return\", \"controller_path\": \"sample.controller\", \"menu_id\": \"017\", \"menu_parent\": \"010\"},\n"
                //history
                + "  {\"access_level\": \"011\", \"menu_name\": \"History\", \"fxml_path\": \"Purchasing/History\", \"controller_path\": \"sample.controller\", \"menu_id\": \"018\", \"menu_parent\": \"001\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Accounts Accreditations\", \"fxml_path\": \"Purchasing/History/Accounts Accreditations\", \"controller_path\": \"sample.controller\", \"menu_id\": \"019\", \"menu_parent\": \"018\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Requisition Slip\", \"fxml_path\": \"Purchasing/History/Requisition Slip\", \"controller_path\": \"sample.controller\", \"menu_id\": \"021\", \"menu_parent\": \"018\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Quotation Request\", \"fxml_path\": \"Purchasing/History/Purchasing Quotation Request\", \"controller_path\": \"sample.controller\", \"menu_id\": \"022\", \"menu_parent\": \"018\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Quotation\", \"fxml_path\": \"Purchasing/History/Purchasing Quotation\", \"controller_path\": \"sample.controller\", \"menu_id\": \"023\", \"menu_parent\": \"018\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Order\", \"fxml_path\": \"Purchasing/History/Purchasing Order\", \"controller_path\": \"sample.controller\", \"menu_id\": \"024\", \"menu_parent\": \"018\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Receiving\", \"fxml_path\": \"Purchasing/History/Purchasing Receiving\", \"controller_path\": \"sample.controller\", \"menu_id\": \"025\", \"menu_parent\": \"018\"},\n"
                + "  {\"access_level\": \"011\", \"menu_name\": \"Purchasing Return\", \"fxml_path\": \"Purchasing/History/Purchasing Return\", \"controller_path\": \"sample.controller\", \"menu_id\": \"026\", \"menu_parent\": \"018\"}\n"
                + "]";

        JSONParser parser = new JSONParser();
        try {
            try {
                flatMenuItems = (JSONArray) parser.parse(new StringReader(jsonString));
                JSONObject purchasingMainMenu = buildHierarchy("001");
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

    private void accountsMenuItems() {
        String jsonString = "["
                + "{\"access_level\": \"011\", \"menu_name\": \"Accounts\", \"fxml_path\": \"Accounts\", \"controller_path\": \"\", \"menu_id\": \"001\", \"menu_parent\": \"\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Accounts Payable\", \"fxml_path\": \"Accounts/Accounts Payable\", \"controller_path\": \"\", \"menu_id\": \"002\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Accounts Receivable\", \"fxml_path\": \"Accounts/Accounts Receivable\", \"controller_path\": \"\", \"menu_id\": \"003\", \"menu_parent\": \"001\"}"
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

    private void othersMenuItems() {
        String jsonString = "["
                + "{\"access_level\": \"011\", \"menu_name\": \"Others\", \"fxml_path\": \"Others\", \"controller_path\": \"sample.controller\", \"menu_id\": \"001\", \"menu_parent\": \"\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Clients\", \"fxml_path\": \"Others/Clients\", \"controller_path\": \"\", \"menu_id\": \"045\", \"menu_parent\": \"001\"}"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory\", \"fxml_path\": \"Others/Inventory\", \"controller_path\": \"\", \"menu_id\": \"042\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory Parameter\", \"fxml_path\": \"Others/Inventory/Inventory Parameter\", \"controller_path\": \"\", \"menu_id\": \"43\", \"menu_parent\": \"042\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory Serial Parameter\", \"fxml_path\": \"Others/Inventory/Inventory Serial Parameter\", \"controller_path\": \"\", \"menu_id\": \"044\", \"menu_parent\": \"042\"}"
                + "{\"access_level\": \"011\", \"menu_name\": \"Parameters\", \"fxml_path\": \"Others/Parameters\", \"controller_path\": \"sample.controller\", \"menu_id\": \"002\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Address\", \"fxml_path\": \"Others/Parameters/Address\", \"controller_path\": \"sample.controller\", \"menu_id\": \"003\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Barangay\", \"fxml_path\": \"Others/Parameters/Address/Barangay\", \"controller_path\": \"sample.controller\", \"menu_id\": \"004\", \"menu_parent\": \"003\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Country\", \"fxml_path\": \"Others/Parameters/Address/Country\", \"controller_path\": \"\", \"menu_id\": \"005\", \"menu_parent\": \"003\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Province\", \"fxml_path\": \"Others/Parameters/Address/Province\", \"controller_path\": \"\", \"menu_id\": \"006\", \"menu_parent\": \"003\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Region\", \"fxml_path\": \"Others/Parameters/Address/Region\", \"controller_path\": \"\", \"menu_id\": \"007\", \"menu_parent\": \"003\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Banks\", \"fxml_path\": \"Others/Parameters/Banks\", \"controller_path\": \"\", \"menu_id\": \"008\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Banks\", \"fxml_path\": \"Others/Parameters/Banks/Banks\", \"controller_path\": \"\", \"menu_id\": \"009\", \"menu_parent\": \"008\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Banks Branches\", \"fxml_path\": \"Others/Parameters/Banks/Banks Branches\", \"controller_path\": \"\", \"menu_id\": \"010\", \"menu_parent\": \"008\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category\", \"fxml_path\": \"Others/Parameters/Category\", \"controller_path\": \"\", \"menu_id\": \"011\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category\", \"fxml_path\": \"Others/Parameters/Category/Category\", \"controller_path\": \"\", \"menu_id\": \"012\", \"menu_parent\": \"011\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category Level 2\", \"fxml_path\": \"Others/Parameters/Category/Category Level 2\", \"controller_path\": \"\", \"menu_id\": \"013\", \"menu_parent\": \"011\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category Level 3\", \"fxml_path\": \"Others/Parameters/Category/Category Level 3\", \"controller_path\": \"\", \"menu_id\": \"014\", \"menu_parent\": \"011\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category Level 4\", \"fxml_path\": \"Others/Parameters/Category/Category Level 4\", \"controller_path\": \"\", \"menu_id\": \"015\", \"menu_parent\": \"011\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Color\", \"fxml_path\": \"Others/Parameters/Color\", \"controller_path\": \"\", \"menu_id\": \"016\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Color\", \"fxml_path\": \"Others/Parameters/Color/Color\", \"controller_path\": \"\", \"menu_id\": \"017\", \"menu_parent\": \"016\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Color Detail\", \"fxml_path\": \"Others/Parameters/Color/Color Detail\", \"controller_path\": \"\", \"menu_id\": \"018\", \"menu_parent\": \"016\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Company\", \"fxml_path\": \"Others/Parameters/Company\", \"controller_path\": \"\", \"menu_id\": \"019\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Affiliated Company\", \"fxml_path\": \"Others/Parameters/Company/Affiliated Company\", \"controller_path\": \"\", \"menu_id\": \"020\", \"menu_parent\": \"019\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Branch\", \"fxml_path\": \"Others/Parameters/Company/Branch\", \"controller_path\": \"\", \"menu_id\": \"021\", \"menu_parent\": \"019\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Company\", \"fxml_path\": \"Others/Parameters/Company/Company\", \"controller_path\": \"\", \"menu_id\": \"022\", \"menu_parent\": \"019\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Department\", \"fxml_path\": \"Others/Parameters/Company/Department\", \"controller_path\": \"\", \"menu_id\": \"023\", \"menu_parent\": \"019\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory\", \"fxml_path\": \"Others/Parameters/Inventory\", \"controller_path\": \"\", \"menu_id\": \"024\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory Location\", \"fxml_path\": \"Others/Parameters/Inventory/Inventory Location\", \"controller_path\": \"\", \"menu_id\": \"025\", \"menu_parent\": \"024\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory Type\", \"fxml_path\": \"Others/Parameters/Inventory/Inventory Type\", \"controller_path\": \"\", \"menu_id\": \"026\", \"menu_parent\": \"024\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor\", \"fxml_path\": \"Others/Parameters/Labor\", \"controller_path\": \"\", \"menu_id\": \"027\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor\", \"fxml_path\": \"Others/Parameters/Labor/Labor\", \"controller_path\": \"\", \"menu_id\": \"028\", \"menu_parent\": \"027\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor Category\", \"fxml_path\": \"Others/Parameters/Labor/Labor Category\", \"controller_path\": \"\", \"menu_id\": \"029\", \"menu_parent\": \"027\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor Model\", \"fxml_path\": \"Others/Parameters/Labor/Labor Model\", \"controller_path\": \"\", \"menu_id\": \"030\", \"menu_parent\": \"027\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Others\", \"fxml_path\": \"Others/Parameters/Others\", \"controller_path\": \"\", \"menu_id\": \"031\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Brand\", \"fxml_path\": \"Others/Parameters/Others/Brand\", \"controller_path\": \"\", \"menu_id\": \"032\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Made\", \"fxml_path\": \"Others/Parameters/Others/Made\", \"controller_path\": \"\", \"menu_id\": \"033\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Model\", \"fxml_path\": \"Others/Parameters/Others/Model\", \"controller_path\": \"\", \"menu_id\": \"034\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Measure\", \"fxml_path\": \"Others/Parameters/Others/Measure\", \"controller_path\": \"\", \"menu_id\": \"035\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Relationship\", \"fxml_path\": \"Others/Parameters/Others/Relationship\", \"controller_path\": \"\", \"menu_id\": \"036\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Salesman\", \"fxml_path\": \"Others/Parameters/Others/Salesman\", \"controller_path\": \"\", \"menu_id\": \"037\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Section\", \"fxml_path\": \"Others/Parameters/Others/Section\", \"controller_path\": \"\", \"menu_id\": \"038\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Size\", \"fxml_path\": \"Others/Parameters/Others/Size\", \"controller_path\": \"\", \"menu_id\": \"039\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Term\", \"fxml_path\": \"Others/Parameters/Others/Term\", \"controller_path\": \"\", \"menu_id\": \"40\", \"menu_parent\": \"031\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Warehouse\", \"fxml_path\": \"Others/Parameters/Others/Warehouse\", \"controller_path\": \"\", \"menu_id\": \"041\", \"menu_parent\": \"031\"}"
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

    private void parametersMenuItems() {
        String jsonString = "["
                + "{\"access_level\": \"011\", \"menu_name\": \"Parameters\", \"fxml_path\": \"Parameters\", \"controller_path\": \"sample.controller\", \"menu_id\": \"001\", \"menu_parent\": \"\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Address\", \"fxml_path\": \"Parameters/Address\", \"controller_path\": \"sample.controller\", \"menu_id\": \"002\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Barangay\", \"fxml_path\": \"Parameters/Address/Barangay\", \"controller_path\": \"sample.controller\", \"menu_id\": \"003\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Country\", \"fxml_path\": \"Parameters/Address/Country\", \"controller_path\": \"\", \"menu_id\": \"004\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Province\", \"fxml_path\": \"Parameters/Address/Province\", \"controller_path\": \"\", \"menu_id\": \"005\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Region\", \"fxml_path\": \"Parameters/Address/Region\", \"controller_path\": \"\", \"menu_id\": \"006\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Banks\", \"fxml_path\": \"Parameters/Banks\", \"controller_path\": \"\", \"menu_id\": \"007\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Banks\", \"fxml_path\": \"Parameters/Banks/Banks\", \"controller_path\": \"\", \"menu_id\": \"008\", \"menu_parent\": \"007\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Banks Branches\", \"fxml_path\": \"Parameters/Banks/Banks Branches\", \"controller_path\": \"\", \"menu_id\": \"009\", \"menu_parent\": \"007\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category\", \"fxml_path\": \"Parameters/Category\", \"controller_path\": \"\", \"menu_id\": \"010\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category\", \"fxml_path\": \"Parameters/Category/Category\", \"controller_path\": \"\", \"menu_id\": \"011\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category Level 2\", \"fxml_path\": \"Parameters/Category/Category Level 2\", \"controller_path\": \"\", \"menu_id\": \"012\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category Level 3\", \"fxml_path\": \"Parameters/Category/Category Level 3\", \"controller_path\": \"\", \"menu_id\": \"013\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Category Level 4\", \"fxml_path\": \"Parameters/Category/Category Level 4\", \"controller_path\": \"\", \"menu_id\": \"014\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Color\", \"fxml_path\": \"Parameters/Color\", \"controller_path\": \"\", \"menu_id\": \"015\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Color\", \"fxml_path\": \"Parameters/Color/Color\", \"controller_path\": \"\", \"menu_id\": \"016\", \"menu_parent\": \"015\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Color Detail\", \"fxml_path\": \"Parameters/Color/Color Detail\", \"controller_path\": \"\", \"menu_id\": \"017\", \"menu_parent\": \"015\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Company\", \"fxml_path\": \"Parameters/Company\", \"controller_path\": \"\", \"menu_id\": \"018\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Affiliated Company\", \"fxml_path\": \"Parameters/Company/Affiliated Company\", \"controller_path\": \"\", \"menu_id\": \"019\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Branch\", \"fxml_path\": \"Parameters/Company/Branch\", \"controller_path\": \"\", \"menu_id\": \"020\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Company\", \"fxml_path\": \"Parameters/Company/Company\", \"controller_path\": \"\", \"menu_id\": \"021\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Department\", \"fxml_path\": \"Parameters/Company/Department\", \"controller_path\": \"\", \"menu_id\": \"022\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory\", \"fxml_path\": \"Parameters/Inventory\", \"controller_path\": \"\", \"menu_id\": \"023\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory Location\", \"fxml_path\": \"Parameters/Inventory/Inventory Location\", \"controller_path\": \"\", \"menu_id\": \"024\", \"menu_parent\": \"023\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Inventory Type\", \"fxml_path\": \"Parameters/Inventory/Inventory Type\", \"controller_path\": \"\", \"menu_id\": \"025\", \"menu_parent\": \"023\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor\", \"fxml_path\": \"Parameters/Labor\", \"controller_path\": \"\", \"menu_id\": \"026\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor\", \"fxml_path\": \"Parameters/Labor/Labor\", \"controller_path\": \"\", \"menu_id\": \"027\", \"menu_parent\": \"026\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor Category\", \"fxml_path\": \"Parameters/Labor/Labor Category\", \"controller_path\": \"\", \"menu_id\": \"028\", \"menu_parent\": \"026\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Labor Model\", \"fxml_path\": \"Parameters/Labor/Labor Model\", \"controller_path\": \"\", \"menu_id\": \"029\", \"menu_parent\": \"026\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Others\", \"fxml_path\": \"Parameters/Others\", \"controller_path\": \"\", \"menu_id\": \"030\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Brand\", \"fxml_path\": \"Parameters/Others/Brand\", \"controller_path\": \"\", \"menu_id\": \"031\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Made\", \"fxml_path\": \"Parameters/Others/Made\", \"controller_path\": \"\", \"menu_id\": \"032\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Model\", \"fxml_path\": \"Parameters/Others/Model\", \"controller_path\": \"\", \"menu_id\": \"033\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Measure\", \"fxml_path\": \"Parameters/Others/Measure\", \"controller_path\": \"\", \"menu_id\": \"034\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Relationship\", \"fxml_path\": \"Parameters/Others/Relationship\", \"controller_path\": \"\", \"menu_id\": \"035\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Salesman\", \"fxml_path\": \"Parameters/Others/Salesman\", \"controller_path\": \"\", \"menu_id\": \"036\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Section\", \"fxml_path\": \"Parameters/Others/Section\", \"controller_path\": \"\", \"menu_id\": \"037\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Size\", \"fxml_path\": \"Parameters/Others/Size\", \"controller_path\": \"\", \"menu_id\": \"038\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Term\", \"fxml_path\": \"Parameters/Others/Term\", \"controller_path\": \"\", \"menu_id\": \"039\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"011\", \"menu_name\": \"Warehouse\", \"fxml_path\": \"Parameters/Others/Warehouse\", \"controller_path\": \"\", \"menu_id\": \"040\", \"menu_parent\": \"030\"}"
                + "]";

        JSONParser parser = new JSONParser();
        try {
            try {
                flatMenuItems = (JSONArray) parser.parse(new StringReader(jsonString));
                JSONObject parametersMenuItems = buildHierarchy("001");
                dissectLeftSideBarJSON(parametersMenuItems.toJSONString());

            } catch (IOException ex) {
                Logger.getLogger(DashboardController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public JSONObject buildHierarchy(String menuCode) {
        String userDepartment = "011";
        Map<String, List<JSONObject>> childMap = new HashMap<>();
        JSONObject rootMenuItem = null;

        for (JSONObject item : flatMenuItems) {
            List<String> accessDepartments = getAccessDepartments(item);

            if (!accessDepartments.contains(userDepartment)) {
                continue;
            }

            String menuId = (String) item.get("menu_id");
            String parentId = (String) item.get("menu_parent");

            if (menuCode.equals(menuId) && (parentId == null || parentId.isEmpty())) {
                rootMenuItem = item;
            }

            childMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(item);
        }

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

        List<JSONObject> children = childMap.getOrDefault(item.get("menu_id"), Collections.emptyList());

        JSONArray childrenArray = new JSONArray();
        for (JSONObject child : children) {
            List<String> accessDepartments = getAccessDepartments(child);
            if (accessDepartments.contains(userDepartment)) {
                childrenArray.add(buildSubHierarchy(child, childMap, userDepartment));
            }
        }

        if (!childrenArray.isEmpty()) {
            node.put("child", childrenArray);
        } else {
            node.put("child", new JSONArray());
        }

        return node;
    }

    private List<String> getAccessDepartments(JSONObject item) {
        Object accessLevelObj = item.get("access_level");

        if (accessLevelObj == null) {
            return Collections.emptyList();
        }

        String accessLevelStr = accessLevelObj.toString().trim();
        if (accessLevelStr.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.asList(accessLevelStr.split("\\s+"));
    }

    private void dissectLeftSideBarJSON(String fsValue) {
        if (fsValue == null || fsValue.isEmpty()) {
            System.err.println("Invalid JSON string.");
            return;
        }

        JSONParser loParser = new JSONParser();
        try {
            Object parsedJson = loParser.parse(fsValue);
            JSONArray laMaster;

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
            menuLocationMap.clear();

            for (Object objMaster : laMaster) {
                if (!(objMaster instanceof JSONObject)) {
                    System.err.println("Skipping invalid entry: " + objMaster);
                    continue;
                }

                JSONObject loParent = (JSONObject) objMaster;
                if (!loParent.containsKey("menu_name")) {
                    continue;
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
                continue;
            }

            String parentName = String.valueOf(loDetail.get("menu_name"));
            String location = loDetail.containsKey("fxml_path") ? String.valueOf(loDetail.get("fxml_path")) : "";

            TreeItem<String> childNode = new TreeItem<>(parentName);
            menuLocationMap.put(childNode, location);

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

            Platform.runLater(() -> {
                anchorLeftSideBarMenu.setPrefWidth(calculatedWidth);
            });
        }

    }

    private int calculateTreeViewWidth(TreeItem<String> root) {
        if (root == null) {
            return 200;
        }
        int baseWidth = 200;
        int textPadding = 20;

        int longestTextWidth = getMaxTextWidth(root);

        double parentWidth = anchorLeftSideBarMenu.getParent().getLayoutBounds().getWidth();

        int calculatedWidth = baseWidth + longestTextWidth + textPadding;
        return (int) Math.min(calculatedWidth, parentWidth * 0.9);
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

        int charWidth = 7;
        return text.length() * charWidth;
    }

    private void handleSelection(TreeItem<String> newValue) {
        if (newValue == null || !newValue.isLeaf() || newValue.getValue() == null || newValue.getValue().isEmpty()) {
            System.out.println("Invalid selection or empty value.");
            return;
        }

        String selectedMenu = newValue.getValue();
        String sLocation = menuLocationMap.getOrDefault(newValue, ""); // Get location from map

        switch (selectedMenu) {
            case "Inventory Maintenance":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/InventoryMaintenance.fxml";
                break;
            case "Motorcycle":
                switch (sLocation.toLowerCase()) {
                    case "sales/sales/motorcycle":
                        sformname = sLocation;
                        ShowMessageFX.Information("Navigation", "You selected", sLocation);
                        break;
                    case "sales/inquiry/motorcycle":
                        sformname = sLocation;
                        ShowMessageFX.Information("test", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Spareparts":
                switch (sLocation.toLowerCase()) {
                    case "sales/sales/spareparts":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/spareparts_sales.fxml";
                        ShowMessageFX.Information("Navigation", "You selected", sLocation);
                        break;
                    case "sales/inquiry/spareparts":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/spareparts_inquiry.fxml";
                        ShowMessageFX.Information("test", "You selected", sLocation);
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
                break;
            case "Requisition Slip":
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
            case "Purchasing Quotation Request":
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
            case "Purchasing Quotation":
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
            case "Purchasing Order":
                switch (sLocation.toLowerCase()) {
                    case "purchasing/entry/purchasing order":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_Entry.fxml";
                        break;
                    case "purchasing/approval/purchasing order":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_Approval.fxml";
                        break;
                    case "purchasing/history/purchasing order":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_History.fxml";
                        break;
                    default:
                        ShowMessageFX.Information("This is another motorycle", "You selected", sLocation);
                        break;
                }
            case "Purchasing Receiving":
                switch (sLocation.toLowerCase()) {
                    case "purchasing/entry/purchasing receiving":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml";
                        ShowMessageFX.Information("Purchasing Receiving", "You selected", sLocation);
                        break;
                    case "purchasing/history/purchasing receiving":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml";
                        ShowMessageFX.Information("Purchasing Receiving History", "You selected", sLocation);
                        break;
                    case "purchasing/approval/purchasing receiving":
                        sformname = "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml";
                        ShowMessageFX.Information("Purchasing Receiving Confirmation", "You selected", sLocation);
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
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml";
                break;
            case "Payment Request":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml";
                break;

            /* OTHERS/PARAMETER/ADDRESS */
            case "Barangay":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Barangay.fxml";
                break;
            case "Country":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Country.fxml";
                break;
            case "Province":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Province.fxml";
                break;
            case "Region":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Region.fxml";
                break;

            /* OTHERS/PARAMETER/BANKS */
            case "Banks":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Banks.fxml";
                break;
            case "Banks Branches":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/BanksBranches.fxml";
                break;

            /* OTHERS/PARAMETER/Category */
            case "Category":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Category.fxml";
                break;
            case "Category Level 2":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel2.fxml";
                break;
            case "Category Level 3":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel3.fxml";
                break;
            case "Category Level 4":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel4.fxml";
                break;

            /* OTHERS/PARAMETER/Color */
            case "Color":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Color.fxml";
                break;
            case "Color Detail":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/ColorDetail.fxml";
                break;

            /* OTHERS/PARAMETER/Company */
            case "Affiliated Company":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/AffiliatedCompany.fxml";
                break;
            case "Branch":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Branch.fxml";
                break;
            case "Company":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Company.fxml";
                break;
            case "Department":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Department.fxml";
                break;

            /* OTHERS/PARAMETER/Inventory */
            case "Inventory Location":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/InventoryLocation.fxml";
                break;
            case "Inventory Type":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/InventoryType.fxml";
                break;

            /* OTHERS/PARAMETER/Labor */
            case "Labor":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Labor.fxml";
                break;
            case "Labor Category":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/LaborCategory.fxml";
                break;
            case "Labor Model":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/LaborModel.fxml";
                break;

            /* OTHERS/PARAMETER/Others */
            case "Brand":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Brand.fxml";
                break;
            case "Made":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Made.fxml";
                break;
            case "Model":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Model.fxml";
                break;
            case "Measure":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Measure.fxml";
                break;
            case "Relationship":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Relationship.fxml";
                break;
            case "Salesman":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Salesman.fxml";
                break;
            case "Section":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Section.fxml";
                break;
            case "Size":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Size.fxml";
                break;
            case "Term":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Term.fxml";
                break;
            case "Warehouse":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/Warehouse.fxml";
                break;

            /* OTHERS/CLIENTS */
            case "Clients":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/ClientMasterParameter.fxml";
                break;

            /* OTHERS/PARAMETER/Color */
            case "Inventory Parameter":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/InventoryParam.fxml";
                break;
            case "Inventory Serial Parameter":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/InventorySerialParam.fxml";
                break;

            /* ACCOUNTS/ACCOUNTS/ACCOUNTS PAYABLE */
            case "Accounts Payable":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/AccountsPayable.fxml";
                break;

            /* ACCOUNTS/ACCOUNTS/ACCOUNTS RECEIVABLE */
            case "Accounts Receivable":
                sformname = "/com/rmj/guanzongroup/sidebarmenus/views/AccountsReceivable.fxml";
                break;

            default:
                sformname = "";
                break;
        }

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

    private void monitorMenuItems() {
        JSONArray laMaster, laDetail;
        JSONObject loMaster, loDetail;
        laMaster = new JSONArray();
        laDetail = new JSONArray();

        if (!"029".equals(oApp.getDepartment())) {
            loDetail = new JSONObject();
            loDetail.put("parent", "Sales");
            laDetail.add(loDetail);
        }

        loDetail = new JSONObject();
        loDetail.put("parent", "PO Receiving");
        laDetail.add(loDetail);

        loMaster = new JSONObject();
        loMaster.put("parent", "Monthly Payment");
        loMaster.put("child", laDetail);

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

            if (tvRightSideBar != null) {
                tvRightSideBar.setRoot(root);
                tvRightSideBar.setShowRoot(false);
                if (!isListenerRightAdded) {
                    isListenerRightAdded = true;
                    tvRightSideBar.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null && newValue.isLeaf() && newValue.getValue() != null && !newValue.getValue().isEmpty()) {
                            switch (newValue.getValue()) {
                                case "Sales Replacement":
                                    sformname = "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml";
                                    break;
                                case "Additional Give":
                                    sformname = "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml";
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
        if ("022".equals(oApp.getDepartment())) {
            btnSales.setVisible(false);
            btnSales.setManaged(false);
        }
    }

    /**
     * GET TIME*
     */
    private void getTime() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            Calendar cal = Calendar.getInstance();
            int second = cal.get(Calendar.SECOND);

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
        ScheduledService<Void> service = new ScheduledService<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() {
                        checkNotifications();
                        return null;
                    }
                };
            }
        };
        service.setPeriod(Duration.minutes(1));
        service.start();
    }

    private void checkNotifications() {
        notificationCount += (int) (Math.random() * 5);
        cartCount += (int) (Math.random() * 5);

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
