package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.utility.JFXUtil;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DashboardController implements Initializable {

    private final String pxeModuleName = "Computerized Accounting System";
    private GRiderCAS oApp;
    private String lastClickedBtnLeftSideBar = "";
    private String lastClickedBtnRightSideBar = "";
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
//    private Map<TreeItem<String>, String> menuIndustryMap = new HashMap<>();
//    private Map<TreeItem<String>, String> menuCategoryMap = new HashMap<>();
    //test
    private boolean isListenerLeftAdded = false;
    private boolean isListenerRightAdded = false;

    private List<JSONObject> flatMenuItems;
    private int userLevel; // User's access level
    private int targetTabIndex = -1;
    private int intIndex = -1;
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isFromFilter;
    private String psIndustryID = "";
    private String psCompanyID = "";
    private String psCategoryID = "";
    public String psUserIndustryId = "";
    public String psUserCompanyId = "";
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

    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    public void setUserIndustry(String lsIndustryId) {
        psUserIndustryId = lsIndustryId;
    }

    public void setUserCompany(String lsCompanyId) {
        psUserCompanyId = lsCompanyId;
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

            psIndustryID = psUserIndustryId;
            psCompanyID = psUserCompanyId;

            setDropShadowEffectsLeftSideBar(anchorLeftSideBarMenu);
            setDropShadowEffectsRightSideBar(anchorRightSideBarMenu);
            Platform.runLater(() -> {
                AnchorPane root = (AnchorPane) MainAnchor;
                Scene scene = root.getScene();
                if (scene != null) {
                    setKeyEvent(scene);
                } else {
                    root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                        if (newScene != null) {
                            setKeyEvent(newScene);
                        }
                    });
                }
            });

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

                /*Purchase Order*/
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Entry.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Confirmation.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Confirmation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Approval.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Approval";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_History.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order History";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Confirmation LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Approval LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order History LP";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0021";
                    return "Purchase Order MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0021";
                    return "Purchase Order Confirmation MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0021";
                    return "Purchase Order Approval MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0021";
                    return "Purchase Order History MF";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Confirmation MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Approval MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order History MH";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Confirmation SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Approval SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order History SPCar";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Confirmation SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Approval SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order History SPMC";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Confirmation MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Approval MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order History MC";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Confirmation MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Approval MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order History MP";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Confirmation Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Approval Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order History Car";

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Confirmation Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Approval Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order History Appliances";

                /*PURCHASE ORDER RECEIVING*/
                //General
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Receiving Entry";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Receiving Confirmation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Approval.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Receiving Approval";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_History.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Receiving History";

                // Appliances
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Receiving Entry Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Receiving Confirmation Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Receiving History Appliances";

                //Car
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Receiving Entry Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Receiving Confirmation Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Receiving Approval Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Receiving History Car";

                //Motorcyle
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Receiving Entry MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Receiving Confirmation MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Receiving Approval MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Receiving History MC";

                //Mobile Phone
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Receiving Entry MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Receiving Confirmation MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Receiving Approval MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Receiving History MP";

                //Los Pedritos
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving Entry LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving Confirmation LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving Approval LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving History LP";

                //Spare Parts Car
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Receiving Entry SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Receiving Confirmation SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalSPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Receiving Approval SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Receiving History SPCar";

                //Spare Parts Motorcycle
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Receiving Entry SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Receiving Confirmation SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalSPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Receiving Approval SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Receiving History SPMC";

                //Monarch Food
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving Entry MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving Confirmation MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving Approval MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "Purchase Order Receiving History MF";

                //Monarch Hospitality
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Receiving Entry MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Receiving Confirmation MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Receiving Approval MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Receiving History MH";

                /*PURCHASE ORDER RETURN*/
                //General
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Entry.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Return Entry";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Confirmation.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Return Confirmation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_History.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "Purchase Order Return History";

                //Appliances
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Return Entry Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Return Confirmation Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryAppliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "Purchase Order Return History Appliances";

                //Car
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Return Entry Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Return Confirmation Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "Purchase Order Return History Car";

                //Motorcyle
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Return Entry MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Return Confirmation MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "Purchase Order Return History MC";

                //Mobile Phone
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Return Entry MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Return Confirmation MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "Purchase Order Return History MP";

                //Los Pedritos
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Return Entry LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Return Confirmation LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryLP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "Purchase Order Return History LP";

                //Spare Parts Car
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Return Entry SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Return Confirmation SPCar";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "Purchase Order Return History SPCar";

                //Spare Parts Motorcycle
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Return Entry SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Return Confirmation SPMC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "Purchase Order Return History SPMC";

                //Monarch Food
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "Purchase Order Return Entry MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "Purchase Order Return Confirmation MF";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "Purchase Order Return History MF";

                //Monarch Hospitality
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Return Entry MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Return Confirmation MH";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "Purchase Order Return History MH";

                /*PAYMENT REQUEST */
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Entry.fxml":
                    return "Payment Request";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Confirmation.fxml":
                    return "Payment Request Confirmation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_History.fxml":
                    return "Payment Request History";

                /*DISBURSEMENT VOUCHER */
                case "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Entry.fxml":
                    return "Disbursement Voucher";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Verification.fxml":
                    return "Disbursement Voucher Verification";
                case "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Certification.fxml":
                    return "Disbursement Voucher Certification";

                /*CHECK */
                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckAuthorization.fxml":
                    return "Check Authorization";

                /*CHECK PRINT REQUEST*/
                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Entry.fxml":
                    return "Check Print Request";
                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Confirmation.fxml":
                    return "Check Print Request Confirmation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrinting.fxml":
                    return "Check Printing";

                /*AP Payment Adjustment */
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Entry.fxml":
                    psIndustryID = "";
                    return "AP Payment Adjustment Entry";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryAppliances.fxml":
                    psIndustryID = "07";
                    return "AP Payment Adjustment Entry Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryCar.fxml":
                    psIndustryID = "03";
                    return "AP Payment Adjustment Entry Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMC.fxml":
                    psIndustryID = "02";
                    return "AP Payment Adjustment Entry MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMP.fxml":
                    psIndustryID = "01";
                    return "AP Payment Adjustment Entry MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryLP.fxml":
                    psIndustryID = "05";
                    return "AP Payment Adjustment Entry LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMonarch.fxml":
                    psIndustryID = "04";
                    return "AP Payment Adjustment Entry Monarch";

                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Confirmation.fxml":
                    psIndustryID = "";
                    return "AP Payment Adjustment Confirmation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationAppliances.fxml":
                    psIndustryID = "07";
                    return "AP Payment Adjustment Confirmation Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationCar.fxml":
                    psIndustryID = "03";
                    return "AP Payment Adjustment Confirmation Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMC.fxml":
                    psIndustryID = "02";
                    return "AP Payment Adjustment Confirmation MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMP.fxml":
                    psIndustryID = "01";
                    return "AP Payment Adjustment Confirmation MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationLP.fxml":
                    psIndustryID = "05";
                    return "AP Payment Adjustment Confirmation LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMonarch.fxml":
                    psIndustryID = "04";
                    return "AP Payment Adjustment Confirmation Monarch";

                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_History.fxml":
                    psIndustryID = "";
                    return "AP Payment Adjustment History";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryAppliances.fxml":
                    psIndustryID = "07";
                    return "AP Payment Adjustment History Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryCar.fxml":
                    psIndustryID = "03";
                    return "AP Payment Adjustment History Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMC.fxml":
                    psIndustryID = "02";
                    return "AP Payment Adjustment History MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMP.fxml":
                    psIndustryID = "01";
                    return "AP Payment Adjustment History MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryLP.fxml":
                    psIndustryID = "05";
                    return "AP Payment Adjustment History LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMonarch.fxml":
                    psIndustryID = "04";
                    return "AP Payment Adjustment History Monarch";

                /*SOA Tagging*/
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Entry.fxml":
                    psIndustryID = "";
                    return "SOA Tagging Entry";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryAppliances.fxml":
                    psIndustryID = "07";
                    return "SOA Tagging Entry Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryCar.fxml":
                    psIndustryID = "03";
                    return "SOA Tagging Entry Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMC.fxml":
                    psIndustryID = "02";
                    return "SOA Tagging Entry MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMP.fxml":
                    psIndustryID = "01";
                    return "SOA Tagging Entry MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryLP.fxml":
                    psIndustryID = "05";
                    return "SOA Tagging Entry LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMonarch.fxml":
                    psIndustryID = "04";
                    return "SOA Tagging Entry Monarch";

                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Confirmation.fxml":
                    psIndustryID = "";
                    return "SOA Tagging Confirmation";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationAppliances.fxml":
                    psIndustryID = "07";
                    return "SOA Tagging Confirmation Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationCar.fxml":
                    psIndustryID = "03";
                    return "SOA Tagging Confirmation Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMC.fxml":
                    psIndustryID = "02";
                    return "SOA Tagging Confirmation MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMP.fxml":
                    psIndustryID = "01";
                    return "SOA Tagging Confirmation MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationLP.fxml":
                    psIndustryID = "05";
                    return "SOA Tagging Confirmation LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMonarch.fxml":
                    psIndustryID = "04";
                    return "SOA Tagging Confirmation Monarch";

                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_History.fxml":
                    psIndustryID = "";
                    return "SOA Tagging History";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryAppliances.fxml":
                    psIndustryID = "07";
                    return "SOA Tagging History Appliances";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryCar.fxml":
                    psIndustryID = "03";
                    return "SOA Tagging History Car";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMC.fxml":
                    psIndustryID = "02";
                    return "SOA Tagging History MC";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMP.fxml":
                    psIndustryID = "01";
                    return "SOA Tagging History MP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryLP.fxml":
                    psIndustryID = "05";
                    return "SOA Tagging History LP";
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMonarch.fxml":
                    psIndustryID = "04";
                    return "SOA Tagging History Monarch";

                /*SI POSTING */
                //General
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting.fxml":
                    psIndustryID = "";
                    psCategoryID = "0007";
                    return "SI Posting";
                //Appliances
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Appliances.fxml":
                    psIndustryID = "07";
                    psCategoryID = "0002";
                    return "SI Posting Car";
                //Car
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Car.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0005";
                    return "SI Posting Car";
                //MC
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0003";
                    return "SI Posting MC";
                //Mobile Phone
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MP.fxml":
                    psIndustryID = "01";
                    psCategoryID = "0001";
                    return "SI Posting MP";
                //Los Pedritos
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_LP.fxml":
                    psIndustryID = "05";
                    psCategoryID = "0008";
                    return "SI Posting LP";
                //Spare Parts Car
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPCar.fxml":
                    psIndustryID = "03";
                    psCategoryID = "0006";
                    return "SI Posting SPCar";
                //Spare Parts Motorcycle
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPMC.fxml":
                    psIndustryID = "02";
                    psCategoryID = "0004";
                    return "SI Posting SPMC";
                //Monarch Food
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchFood.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0008";
                    return "SI Posting MF";
                //Monarch Hospitality
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchHospitality.fxml":
                    psIndustryID = "04";
                    psCategoryID = "0009";
                    return "SI Posting MH";

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

                /*OTHERS/CLIENTS*/
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

    public String getFormName(String fsTabTitle) {
        switch (fsTabTitle) {

            /*Purchase Order*/
            case "Purchase Order":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Entry.fxml";
            case "Purchase Order Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Confirmation.fxml";
            case "Purchase Order Approval":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Approval.fxml";
            case "Purchase Order History":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_History.fxml";
            case "Purchase Order Receiving":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml";
            case "Purchasing Receiving Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml";

            case "Purchase Order LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryLP.fxml";
            case "Purchase Order Confirmation LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationLP.fxml";
            case "Purchase Order Approval LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalLP.fxml";
            case "Purchase Order History LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryLP.fxml";

            case "Purchase Order MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchFood.fxml";
            case "Purchase Order Confirmation MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchFood.fxml";
            case "Purchase Order Approval MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchFood.fxml";
            case "Purchase Order History MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchFood.fxml";

            case "Purchase Order MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchHospitality.fxml";
            case "Purchase Order Confirmation MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchHospitality.fxml";
            case "Purchase Order Approval MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchHospitality.fxml";
            case "Purchase Order History MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchHospitality.fxml";

            case "Purchase Order SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPCar.fxml";
            case "Purchase Order Confirmation SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPCar.fxml";
            case "Purchase Order Approval SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPCar.fxml";
            case "Purchase Order History SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPCar.fxml";

            case "Purchase Order SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPMC.fxml";
            case "Purchase Order Confirmation SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPMC.fxml";
            case "Purchase Order Approval SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPMC.fxml";
            case "Purchase Order History SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPMC.fxml";

            case "Purchase Order MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMC.fxml";
            case "Purchase Order Confirmation MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMC.fxml";
            case "Purchase Order Approval MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMC.fxml";
            case "Purchase Order History MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMC.fxml";

            case "Purchase Order MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMP.fxml";
            case "Purchase Order Confirmation MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMP.fxml";
            case "Purchase Order Approval MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMP.fxml";
            case "Purchase Order History MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMP.fxml";

            case "Purchase Order Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryCar.fxml";
            case "Purchase Order Confirmation Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationCar.fxml";
            case "Purchase Order Approval Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalCar.fxml";
            case "Purchase Order History Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryCar.fxml";
            /*END PURCHASE ORDER*/

 /*PURCHASE ORDER RECEIVING*/
            // General
            case "Purchase Order Receiving Entry":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml";
            case "Purchase Order Receiving Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml";
            case "Purchase Order Receiving History":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_History.fxml";

            // Appliances
            case "Purchase Order Receiving Entry Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryAppliances.fxml";
            case "Purchase Order Receiving Confirmation Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationAppliances.fxml";
            case "Purchase Order Receiving History Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryAppliances.fxml";

            // Car
            case "Purchase Order Receiving Entry Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryCar.fxml";
            case "Purchase Order Receiving Confirmation Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationCar.fxml";
            case "Purchase Order Receiving History Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryCar.fxml";

            // Motorcycle
            case "Purchase Order Receiving Entry MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMC.fxml";
            case "Purchase Order Receiving Confirmation MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMC.fxml";
            case "Purchase Order Receiving History MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMC.fxml";

            // Mobile Phone
            case "Purchase Order Receiving Entry MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMP.fxml";
            case "Purchase Order Receiving Confirmation MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMP.fxml";
            case "Purchase Order Receiving History MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMP.fxml";

            // Los Pedritos
            case "Purchase Order Receiving Entry LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryLP.fxml";
            case "Purchase Order Receiving Confirmation LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationLP.fxml";
            case "Purchase Order Receiving History LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryLP.fxml";

            // Spare Parts Car
            case "Purchase Order Receiving Entry SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPCar.fxml";
            case "Purchase Order Receiving Confirmation SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPCar.fxml";
            case "Purchase Order Receiving History SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPCar.fxml";

            // Spare Parts Motorcycle
            case "Purchase Order Receiving Entry SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPMC.fxml";
            case "Purchase Order Receiving Confirmation SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPMC.fxml";
            case "Purchase Order Receiving History SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPMC.fxml";

            // Monarch Food
            case "Purchase Order Receiving Entry MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchFood.fxml";
            case "Purchase Order Receiving Confirmation MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchFood.fxml";
            case "Purchase Order Receiving History MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchFood.fxml";

            // Monarch Hospitality
            case "Purchase Order Receiving Entry MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchHospitality.fxml";
            case "Purchase Order Receiving Confirmation MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchHospitality.fxml";
            case "Purchase Order Receiving History MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchHospitality.fxml";
            /*END PURCHASE ORDER RECEIVING*/
 /*PURCHASE ORDER RETURN*/
            // General
            case "Purchase Order Return Entry":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Entry.fxml";
            case "Purchase Order Return Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Confirmation.fxml";
            case "Purchase Order Return History":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_History.fxml";

            // Appliances
            case "Purchase Order Return Entry Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryAppliances.fxml";
            case "Purchase Order Return Confirmation Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationAppliances.fxml";
            case "Purchase Order Return History Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryAppliances.fxml";

            // Car
            case "Purchase Order Return Entry Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryCar.fxml";
            case "Purchase Order Return Confirmation Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationCar.fxml";
            case "Purchase Order Return History Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryCar.fxml";

            // Motorcycle
            case "Purchase Order Return Entry MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMC.fxml";
            case "Purchase Order Return Confirmation MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMC.fxml";
            case "Purchase Order Return History MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMC.fxml";

            // Mobile Phone
            case "Purchase Order Return Entry MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMP.fxml";
            case "Purchase Order Return Confirmation MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMP.fxml";
            case "Purchase Order Return History MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMP.fxml";

            // Los Pedritos
            case "Purchase Order Return Entry LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryLP.fxml";
            case "Purchase Order Return Confirmation LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationLP.fxml";
            case "Purchase Order Return History LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryLP.fxml";

            // Spare Parts Car
            case "Purchase Order Return Entry SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPCar.fxml";
            case "Purchase Order Return Confirmation SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPCar.fxml";
            case "Purchase Order Return History SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPCar.fxml";

            // Spare Parts Motorcycle
            case "Purchase Order Return Entry SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPMC.fxml";
            case "Purchase Order Return Confirmation SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPMC.fxml";
            case "Purchase Order Return History SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPMC.fxml";

            // Monarch Food
            case "Purchase Order Return Entry MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchFood.fxml";
            case "Purchase Order Return Confirmation MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchFood.fxml";
            case "Purchase Order Return History MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchFood.fxml";

            // Monarch Hospitality
            case "Purchase Order Return Entry MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchHospitality.fxml";
            case "Purchase Order Return Confirmation MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchHospitality.fxml";
            case "Purchase Order Return History MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchHospitality.fxml";
            /*END PURCHASE ORDER RETURN*/

 /*START PAYMENT REQUEST */
            case "Payment Request":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Entry.fxml";
            case "Payment Request Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Confirmation.fxml";
            case "Payment Request History":
                return "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_History.fxml";
            /*END PAYMENT REQUEST*/

 /*DISBURSEMENT VOUCHER */
            case "Disbursement Voucher":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Entry.fxml";
            case "Disbursement Voucher Verification":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Verification.fxml";
            case "Disbursement Voucher Certification":
                return "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Certification.fxml";

            /*CHECK */
            case "Check Authorization":
                return "/com/rmj/guanzongroup/sidebarmenus/views/CheckAuthorization.fxml";
            /*CHECK PRINT REQUEST */
            case "Check Print Request":
                return "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Entry.fxml";
            case "Check Print Request Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Confirmation.fxml";
            case "Check Printing":
                return "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrinting.fxml";

            /*AP PAYMENT ADJUSTMENT*/
            case "AP Payment Adjustment Entry":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Entry.fxml";
            case "AP Payment Adjustment Entry Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryAppliances.fxml";
            case "AP Payment Adjustment Entry Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryCar.fxml";
            case "AP Payment Adjustment Entry MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMC.fxml";
            case "AP Payment Adjustment Entry MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMP.fxml";
            case "AP Payment Adjustment Entry LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryLP.fxml";
            case "AP Payment Adjustment Entry Monarch":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMonarch.fxml";
            //Confirmation    
            case "AP Payment Adjustment Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Confirmation.fxml";
            case "AP Payment Adjustment Confirmation Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationAppliances.fxml";
            case "AP Payment Adjustment Confirmation Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationCar.fxml";
            case "AP Payment Adjustment Confirmation MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMC.fxml";
            case "AP Payment Adjustment Confirmation MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMP.fxml";
            case "AP Payment Adjustment Confirmation LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationLP.fxml";
            case "AP Payment Adjustment Confirmation Monarch":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMonarch.fxml";
            //History    
            case "AP Payment Adjustment History":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_History.fxml";
            case "AP Payment Adjustment History Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryAppliances.fxml";
            case "AP Payment Adjustment History Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryCar.fxml";
            case "AP Payment Adjustment History MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMC.fxml";
            case "AP Payment Adjustment History MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMP.fxml";
            case "AP Payment Adjustment History LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryLP.fxml";
            case "AP Payment Adjustment History Monarch":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMonarch.fxml";

            /*SOA TAGGING*/
            case "SOA Tagging Entry":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Entry.fxml";
            case "SOA Tagging Entry Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryAppliances.fxml";
            case "SOA Tagging Entry Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryCar.fxml";
            case "SOA Tagging Entry MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMC.fxml";
            case "SOA Tagging Entry MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMP.fxml";
            case "SOA Tagging Entry LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryLP.fxml";
            case "SOA Tagging Entry Monarch":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMonarch.fxml";
            //Confirmation    
            case "SOA Tagging Confirmation":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Confirmation.fxml";
            case "SOA Tagging Confirmation Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationAppliances.fxml";
            case "SOA Tagging Confirmation Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationCar.fxml";
            case "SOA Tagging Confirmation MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMC.fxml";
            case "SOA Tagging Confirmation MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMP.fxml";
            case "SOA Tagging Confirmation LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationLP.fxml";
            case "SOA Tagging Confirmation Monarch":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMonarch.fxml";
            //History    
            case "SOA Tagging History":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_History.fxml";
            case "SOA Tagging History Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryAppliances.fxml";
            case "SOA Tagging History Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryCar.fxml";
            case "SOA Tagging History MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMC.fxml";
            case "SOA Tagging History MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMP.fxml";
            case "SOA Tagging History LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryLP.fxml";
            case "SOA Tagging History Monarch":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMonarch.fxml";

            /*START SI POSTING */
            // General
            case "SI Posting":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting.fxml";
            // Appliances
            case "SI Posting Appliances":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Appliances.fxml";
            // Car
            case "SI Posting Car":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Car.fxml";
            // Motorcycle
            case "SI Posting MC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MC.fxml";
            // Mobile Phone
            case "SI Posting MP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MP.fxml";
            // Los Pedritos
            case "SI Posting LP":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_LP.fxml";
            // Spare Parts Car
            case "SI Posting SPCar":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPCar.fxml";
            // Spare Parts Motorcycle
            case "SI Posting SPMC":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPMC.fxml";
            // Monarch Food
            case "SI Posting MF":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchFood.fxml";
            // Monarch Hospitality
            case "SI Posting MH":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchHospitality.fxml";
            /*END SI POSTING*/

        }

        return "";
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

                /*PURCHASE ORDER */
                // GENERAL
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Entry.fxml":
                    return new PurchaseOrder_EntryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Confirmation.fxml":
                    return new PurchaseOrder_ConfirmationController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Approval.fxml":
                    return new PurchaseOrder_ApprovalController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_History.fxml":
                    return new PurchaseOrder_HistoryController();
                // Los Pedritos
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryLP.fxml":
                    return new PurchaseOrder_EntryLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationLP.fxml":
                    return new PurchaseOrder_ConfirmationLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalLP.fxml":
                    return new PurchaseOrder_ApprovalLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryLP.fxml":
                    return new PurchaseOrder_HistoryLPController();
                //Monarch Restaurant
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchFood.fxml":
                    return new PurchaseOrder_EntryMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchFood.fxml":
                    return new PurchaseOrder_ConfirmationMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchFood.fxml":
                    return new PurchaseOrder_ApprovalMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchFood.fxml":
                    return new PurchaseOrder_HistoryMonarchFoodController();
                //Monarch Hospitality
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchHospitality.fxml":
                    return new PurchaseOrder_EntryMonarchHospitalityController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchHospitality.fxml":
                    return new PurchaseOrder_ConfirmationMonarchHospitalityController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchHospitality.fxml":
                    return new PurchaseOrder_ApprovalMonarchHospitalityController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchHospitality.fxml":
                    return new PurchaseOrder_HistoryMonarchHospitalityController();
                //Monarch Spare Parts Car
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPCar.fxml":
                    return new PurchaseOrder_EntrySPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPCar.fxml":
                    return new PurchaseOrder_ConfirmationSPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPCar.fxml":
                    return new PurchaseOrder_ApprovalSPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPCar.fxml":
                    return new PurchaseOrder_HistorySPCarController();
                //Monarch Spare Parts MC
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPMC.fxml":
                    return new PurchaseOrder_EntrySPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPMC.fxml":
                    return new PurchaseOrder_ConfirmationSPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPMC.fxml":
                    return new PurchaseOrder_ApprovalSPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPMC.fxml":
                    return new PurchaseOrder_HistorySPMCController();

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMC.fxml":
                    return new PurchaseOrder_EntryMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMC.fxml":
                    return new PurchaseOrder_ConfirmationMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMC.fxml":
                    return new PurchaseOrder_ApprovalMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMC.fxml":
                    return new PurchaseOrder_HistoryMCController();

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMP.fxml":
                    return new PurchaseOrder_EntryMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMP.fxml":
                    return new PurchaseOrder_ConfirmationMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMP.fxml":
                    return new PurchaseOrder_ApprovalMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMP.fxml":
                    return new PurchaseOrder_HistoryMPController();

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryCar.fxml":
                    return new PurchaseOrder_EntryCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationCar.fxml":
                    return new PurchaseOrder_ConfirmationCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalCar.fxml":
                    return new PurchaseOrder_ApprovalCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryCar.fxml":
                    return new PurchaseOrder_HistoryCarController();

                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryAppliances.fxml":
                    return new PurchaseOrder_EntryAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationAppliances.fxml":
                    return new PurchaseOrder_ConfirmationAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalAppliances.fxml":
                    return new PurchaseOrder_ApprovalAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryAppliances.fxml":
                    return new PurchaseOrder_HistoryAppliancesController();

                /*PURCHASE ORDER RECEIVING*/
 /* PURCHASE ORDER RECEIVING */
                // ENTRY
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryAppliances.fxml":
                    return new DeliveryAcceptance_EntryAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml":
                    return new DeliveryAcceptance_EntryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryCar.fxml":
                    return new DeliveryAcceptance_EntryCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMC.fxml":
                    return new DeliveryAcceptance_EntryMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMP.fxml":
                    return new DeliveryAcceptance_EntryMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryLP.fxml":
                    return new DeliveryAcceptance_EntryLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPCar.fxml":
                    return new DeliveryAcceptance_EntrySPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPMC.fxml":
                    return new DeliveryAcceptance_EntrySPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchFood.fxml":
                    return new DeliveryAcceptance_EntryMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchHospitality.fxml":
                    return new DeliveryAcceptance_EntryMonarchHospitalityController();
                // CONFIRMATION
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationAppliances.fxml":
                    return new DeliveryAcceptance_ConfirmationAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml":
                    return new DeliveryAcceptance_ConfirmationController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationCar.fxml":
                    return new DeliveryAcceptance_ConfirmationCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMC.fxml":
                    return new DeliveryAcceptance_ConfirmationMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMP.fxml":
                    return new DeliveryAcceptance_ConfirmationMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationLP.fxml":
                    return new DeliveryAcceptance_ConfirmationLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPCar.fxml":
                    return new DeliveryAcceptance_ConfirmationSPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPMC.fxml":
                    return new DeliveryAcceptance_ConfirmationSPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchFood.fxml":
                    return new DeliveryAcceptance_ConfirmationMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchHospitality.fxml":
                    return new DeliveryAcceptance_ConfirmationMonarchHospitalityController();
                // APPROVAL
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Approval.fxml":
//                    return new DeliveryAcceptance_ApprovalController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalCar.fxml":
//                    return new DeliveryAcceptance_ApprovalCarController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMC.fxml":
//                    return new DeliveryAcceptance_ApprovalMCController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMP.fxml":
//                    return new DeliveryAcceptance_ApprovalMPController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalLP.fxml":
//                    return new DeliveryAcceptance_ApprovalLPController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalSPCar.fxml":
//                    return new DeliveryAcceptance_ApprovalSPCarController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalSPMC.fxml":
//                    return new DeliveryAcceptance_ApprovalSPMCController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMonarchFood.fxml":
//                    return new DeliveryAcceptance_ApprovalMonarchFoodController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMonarchHospitality.fxml":
//                    return new DeliveryAcceptance_ApprovalMonarchHospitalityController();
                // HISTORY
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryAppliances.fxml":
                    return new DeliveryAcceptance_HistoryAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_History.fxml":
                    return new DeliveryAcceptance_HistoryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryCar.fxml":
                    return new DeliveryAcceptance_HistoryCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMC.fxml":
                    return new DeliveryAcceptance_HistoryMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMP.fxml":
                    return new DeliveryAcceptance_HistoryMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryLP.fxml":
                    return new DeliveryAcceptance_HistoryLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPCar.fxml":
                    return new DeliveryAcceptance_HistorySPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPMC.fxml":
                    return new DeliveryAcceptance_HistorySPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchFood.fxml":
                    return new DeliveryAcceptance_HistoryMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchHospitality.fxml":
                    return new DeliveryAcceptance_HistoryMonarchHospitalityController();


                /*PURCHASE ORDER RETURN*/
                //ENTRY
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Entry.fxml":
                    return new PurchaseOrderReturn_EntryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryAppliances.fxml":
                    return new PurchaseOrderReturn_EntryAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryCar.fxml":
                    return new PurchaseOrderReturn_EntryCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMC.fxml":
                    return new PurchaseOrderReturn_EntryMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMP.fxml":
                    return new PurchaseOrderReturn_EntryMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryLP.fxml":
                    return new PurchaseOrderReturn_EntryLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPCar.fxml":
                    return new PurchaseOrderReturn_EntrySPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPMC.fxml":
                    return new PurchaseOrderReturn_EntrySPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchFood.fxml":
                    return new PurchaseOrderReturn_EntryMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchHospitality.fxml":
                    return new PurchaseOrderReturn_EntryMonarchHospitalityController();
//                //CONFIRMATION
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Confirmation.fxml":
                    return new PurchaseOrderReturn_ConfirmationController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationAppliances.fxml":
                    return new PurchaseOrderReturn_ConfirmationAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationCar.fxml":
                    return new PurchaseOrderReturn_ConfirmationCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMC.fxml":
                    return new PurchaseOrderReturn_ConfirmationMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMP.fxml":
                    return new PurchaseOrderReturn_ConfirmationMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationLP.fxml":
                    return new PurchaseOrderReturn_ConfirmationLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPCar.fxml":
                    return new PurchaseOrderReturn_ConfirmationSPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPMC.fxml":
                    return new PurchaseOrderReturn_ConfirmationSPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchFood.fxml":
                    return new PurchaseOrderReturn_ConfirmationMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchHospitality.fxml":
                    return new PurchaseOrderReturn_ConfirmationMonarchHospitalityController();
//                //HISTORY
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_History.fxml":
                    return new PurchaseOrderReturn_HistoryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryAppliances.fxml":
                    return new PurchaseOrderReturn_HistoryAppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryCar.fxml":
                    return new PurchaseOrderReturn_HistoryCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMC.fxml":
                    return new PurchaseOrderReturn_HistoryMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMP.fxml":
                    return new PurchaseOrderReturn_HistoryMPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryLP.fxml":
                    return new PurchaseOrderReturn_HistoryLPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPCar.fxml":
                    return new PurchaseOrderReturn_HistorySPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPMC.fxml":
                    return new PurchaseOrderReturn_HistorySPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchFood.fxml":
                    return new PurchaseOrderReturn_HistoryMonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchHospitality.fxml":
                    return new PurchaseOrderReturn_HistoryMonarchHospitalityController();

                /* PAYMENT REQUEST*/
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Entry.fxml":
                    return new PaymentRequest_EntryController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Confirmation.fxml":
                    return new PaymentRequest_ConfirmationController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_History.fxml":
                    return new PaymentRequest_HistoryController();

                /*DISBURSEMENT VOUCHER */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Entry.fxml":
//                    return new DisbursementVoucher_EntryController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Verification.fxml":
//                    return new DisbursementVoucher_VerificationController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Certification.fxml":
//                    return new DisbursementVoucher_CertificationController();
//
//                /*CHECK */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckAuthorization.fxml":
//                    return new CheckAuthorizationController();
//
//                /*CHECK PRINT REQUEST */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Entry.fxml":
//                    return new CheckPrintRequest_EntryController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Confirmation.fxml":
//                    return new CheckPrintRequest_ConfirmationController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/CheckPrinting.fxml":
//                    return new CheckPrintingController();
                /* SI POSTING */
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting.fxml":
                    return new SIPosting_Controller();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Appliances.fxml":
                    return new SIPosting_AppliancesController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Car.fxml":
                    return new SIPosting_CarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MC.fxml":
                    return new SIPosting_MCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MP.fxml":
                    return new SIPosting_MPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_LP.fxml":
                    return new SIPosting_LPController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPCar.fxml":
                    return new SIPosting_SPCarController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPMC.fxml":
                    return new SIPosting_SPMCController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchFood.fxml":
                    return new SIPosting_MonarchFoodController();
                case "/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchHospitality.fxml":
                    return new SIPosting_MonarchHospitalityController();

                /* AP PAYMENT ADJUSTMENT */
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Entry.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryAppliances.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryCar.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMC.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryLP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMonarch.fxml":
                    return new APPaymentAdjustment_EntryController() {
                        {
                            boolean isGeneral = JFXUtil.isGeneralFXML(fsValue) ? true : false;
                            setTabTitle(JFXUtil.getFormattedFXMLTitle(fsValue), isGeneral);
                        }
                    };
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Confirmation.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationAppliances.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationCar.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMC.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationLP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMonarch.fxml":
                    return new APPaymentAdjustment_ConfirmationController() {
                        {
                            setTabTitle(JFXUtil.getFormattedFXMLTitle(fsValue), JFXUtil.isGeneralFXML(fsValue));
                        }
                    };
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_History.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryAppliances.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryCar.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMC.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryLP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMonarch.fxml":
                    return new APPaymentAdjustment_HistoryController() {
                        {
                            setTabTitle(JFXUtil.getFormattedFXMLTitle(fsValue), JFXUtil.isGeneralFXML(fsValue));
                        }
                    };

                /* SOA TAGGING */
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Entry.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryAppliances.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryCar.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMC.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryLP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMonarch.fxml":
                    return new SOATagging_EntryController() {
                        {
                            setTabTitle(JFXUtil.getFormattedFXMLTitle(fsValue), JFXUtil.isGeneralFXML(fsValue));
                        }
                    };
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Confirmation.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationAppliances.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationCar.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMC.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationLP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMonarch.fxml":
                    return new SOATagging_ConfirmationController() {
                        {
                            setTabTitle(JFXUtil.getFormattedFXMLTitle(fsValue), JFXUtil.isGeneralFXML(fsValue));
                        }
                    };
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_History.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryAppliances.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryCar.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMC.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryLP.fxml":
                case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMonarch.fxml":
                    return new SOATagging_HistoryController() {
                        {
                            setTabTitle(JFXUtil.getFormattedFXMLTitle(fsValue), JFXUtil.isGeneralFXML(fsValue));
                        }
                    };

                /* INVENTORY/INVENTORY/ */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryMaintenance.fxml":
//                    return new InventoryMaintenanceController();

                /* PURCHASING/PURCHASING/ */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm1.fxml":
//                    return new SampleForm1Controller();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/SampleForm2.fxml":
//                    return new SampleForm2Controller();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsAccreditation.fxml":
//                    return new AccountsAccreditationController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/AccountsAccreditationHistory.fxml":
//                    return new AccountsAccreditationHistoryController();
//                 case "/com/rmj/guanzongroup/sidebarmenus/views/PurchasingOrder_History.fxml":
//                 return new PurchasingOrder_HistoryController();
                /* OTHERS/PARAMETERS/ADDRESS */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Barangay.fxml":
//                    return new BarangayController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Country.fxml":
//                    return new CountryController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Province.fxml":
//                    return new ProvinceController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Region.fxml":
//                    return new RegionController();

                /* OTHERS/PARAMETERS/BANKS */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Banks.fxml":
//                    return new BanksController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/BanksBranches.fxml":
//                    return new BanksBranchesController();
//                /* OTHERS/PARAMETERS/CATEGORY */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Category.fxml":
//                    return new CategoryController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel2.fxml":
//                    return new CategoryLevel2Controller();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel3.fxml":
//                    return new CategoryLevel3Controller();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel4.fxml":
//                    return new CategoryLevel4Controller();
//
//                /* OTHERS/PARAMETERS/CATEGORY */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Color.fxml":
//                    return new ColorController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/ColorDetail.fxml":
//                    return new ColorDetailController();
//
//                /* OTHERS/PARAMETERS/COMPANY */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/AffiliatedCompany.fxml":
//                    return new AffiliatedCompanyController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Branch.fxml":
//                    return new BranchController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Company.fxml":
//                    return new CompanyController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Department.fxml":
//                    return new DepartmentController();
//
//                /* OTHERS/PARAMETERS/INVENTORY */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryLocation.fxml":
//                    return new InventoryLocationController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/InventoryType.fxml":
//                    return new InventoryTypeController();
//
//                /* OTHERS/PARAMETERS/LABOR */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Labor.fxml":
//                    return new LaborController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/LaborCategory.fxml":
//                    return new LaborCategoryController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/LaborModel.fxml":
//                    return new LaborModelController();
//
//                /* OTHERS/PARAMETERS/OTHERS */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Brand.fxml":
//                    return new BrandController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Made.fxml":
//                    return new MadeController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Model.fxml":
//                    return new ModelController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Measure.fxml":
//                    return new MeasureController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Relationship.fxml":
//                    return new RelationshipController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Salesman.fxml":
//                    return new SalesmanController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Section.fxml":
//                    return new SectionController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Size.fxml":
//                    return new SizeController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Term.fxml":
//                    return new TermController();
//                case "/com/rmj/guanzongroup/sidebarmenus/views/Warehouse.fxml":
//                    return new WarehouseController();
//
//                /* OTHERS/Clients */
//                case "/com/rmj/guanzongroup/sidebarmenus/views/ClientMasterParameter.fxml":
//                    return new ClientMasterParameterController();
////
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
            if (tabpane.getSelectionModel().getSelectedItem() != null) {
                sformname = getFormName(tabpane.getSelectionModel().getSelectedItem().getText());
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
        psIndustryID = psUserIndustryId;
        psCompanyID = psUserCompanyId;

        setTabPane();
        setPane();

        ScreenInterface fxObj = getController(fsFormName);
        fxObj.setGRider(oApp);
        fxObj.setIndustryID(psIndustryID);
        fxObj.setCompanyID(psCompanyID);
        fxObj.setCategoryID(psCategoryID);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxObj.getClass().getResource(updateFxmlName(fsFormName)));
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

    private String updateFxmlName(String fsFormName) {
        switch (fsFormName) {
            /*AP PAYMENT ADJUSTMENT*/
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryAppliances.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryCar.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMC.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryLP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMonarch.fxml":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Entry.fxml";
            //Confirmation    
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationAppliances.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationCar.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMC.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationLP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMonarch.fxml":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Confirmation.fxml";
            //History    
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryAppliances.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryCar.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMC.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryLP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMonarch.fxml":
                return "/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_History.fxml";

            /*SOA TAGGING*/
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryAppliances.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryCar.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMC.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryLP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMonarch.fxml":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Entry.fxml";
            //Confirmation  
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationAppliances.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationCar.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMC.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationLP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMonarch.fxml":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Confirmation.fxml";
            //History    
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryAppliances.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryCar.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMC.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryLP.fxml":
            case "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMonarch.fxml":
                return "/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_History.fxml";

        }

        return fsFormName;
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

    public ContextMenu createContextMenu(TabPane tabPane, Tab tab, GRiderCAS oApp) {
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

    private void closeAllTabs(TabPane tabPane, GRiderCAS oApp) {
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

    private void logOutCloseAllTabs(TabPane tabPane, GRiderCAS oApp) {
        if (tabPane == null) {
            System.out.println("tabPane is null");
            return;
        }

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

    @FXML
    private void switchLogout(ActionEvent event) {
        if (!LoginControllerHolder.getLogInStatus()) {
            LoginControllerHolder.setLogInStatus(false);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
            return;
        }

        if (!ShowMessageFX.YesNo(null, "GUANZON GROUP OF COMPANIES", "Are you sure you want to logout?")) {
            btnLogout.setSelected(false);
            return;
        }

        if (!tabpane.getTabs().isEmpty()) {
            if (!ShowMessageFX.YesNo(null, "GUANZON GROUP OF COMPANIES", "You have open tabs. Are you sure you want to logout?")) {
                btnLogout.setSelected(false);
                return;
            }

            logOutCloseAllTabs(tabpane, oApp);
        }
        performLogoutCleanup();
    }

    private void performLogoutCleanup() {
        List<ToggleButton> tglButtons = Arrays.asList(btnInventory, btnPurchasing,
                btnSales, btnServiceRepair, btnAccountsReceivable, btnAccountsReceivable,
                btnGeneralAccounting, btnOthers, btnOthers, btnHelp, btnLogout,
                btnSysMonitor, btnAddToCart);

        tglButtons.forEach(btn -> btn.setSelected(false));
        setAnchorPaneVisibleManage(false, anchorRightSideBarMenu);
        setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
        ToggleGroupControlLowerLeftSideBar();
        setScene(loadAnimateAnchor(psDefaultScreenFXML));
        btnLogout.setSelected(false);
        sformname = "";
        LoginControllerHolder.setLogInStatus(false);
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
                + "  {\"access_level\": \"026 01 02 03 04 05 06\", \"menu_name\": \"Inventory\", \"fxml_path\": \"Inventory\", \"controller_path\": \"sample.controller\", \"menu_id\": \"028\", \"menu_parent\": \"\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Inventory Maintenance\", \"fxml_path\": \"Inventory/Inventory Maintenance\", \"controller_path\": \"sample.controller\", \"menu_id\": \"45\", \"menu_parent\": \"028\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Request\", \"fxml_path\": \"Inventory/Request\", \"controller_path\": \"sample.controller\", \"menu_id\": \"029\", \"menu_parent\": \"028\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Regular Stocks\", \"fxml_path\": \"Inventory/Request/Regular Stocks\", \"controller_path\": \"sample.controller\", \"menu_id\": \"030\", \"menu_parent\": \"029\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Inventory/Request/Regular Stocks/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"031\", \"menu_parent\": \"030\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Inventory/Request/Regular Stocks/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"032\", \"menu_parent\": \"030\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"System Recommend\", \"fxml_path\": \"Inventory/Request/System Recommend\", \"controller_path\": \"sample.controller\", \"menu_id\": \"033\", \"menu_parent\": \"029\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Inventory/Request/System Recommend/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"034\", \"menu_parent\": \"033\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Inventory/Request/System Recommend/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"035\", \"menu_parent\": \"033\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"History\", \"fxml_path\": \"Inventory/History\", \"controller_path\": \"sample.controller\", \"menu_id\": \"036\", \"menu_parent\": \"028\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Request\", \"fxml_path\": \"Inventory/History/Request\", \"controller_path\": \"sample.controller\", \"menu_id\": \"037\", \"menu_parent\": \"036\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Regular Stocks\", \"fxml_path\": \"Inventory/History/Request/Regular Stocks\", \"controller_path\": \"sample.controller\", \"menu_id\": \"038\", \"menu_parent\": \"037\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Inventory/History/Request/Regular Stocks/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"039\", \"menu_parent\": \"038\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts \", \"fxml_path\": \"Inventory/History/Request/Regular Stocks/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"040\", \"menu_parent\": \"038\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"System Recommend\", \"fxml_path\": \"Inventory/History/Request/System Recommend\", \"controller_path\": \"sample.controller\", \"menu_id\": \"041\", \"menu_parent\": \"037\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle \", \"fxml_path\": \"Inventory/History/Request/System Recommend/Motorcycle\", \"controller_path\": \"sample.controller\", \"menu_id\": \"042\", \"menu_parent\": \"041\"},\n"
                + "  {\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts \", \"fxml_path\": \"Inventory/History/Request/System Recommend/Spareparts\", \"controller_path\": \"sample.controller\", \"menu_id\": \"043\", \"menu_parent\": \"041\"}\n"
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
        String jsonString = "[{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchasing\",\"fxml_path\":\"Purchasing\",\"controller_path\":\"purchasing.controller\",\"menu_id\":\"001\",\"menu_parent\":\"\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Entry\",\"fxml_path\":\"Entry\",\"controller_path\":\"entry.controller\",\"menu_id\":\"002\",\"menu_parent\":\"001\"},"
                // Purchase Order Entry
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order\",\"fxml_path\":\"Purchase Order\",\"controller_path\":\"po.controller\",\"menu_id\":\"003\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryAppliances.fxml\",\"controller_path\":\"PurchaseOrder_EntryAppliances.controller\",\"menu_id\":\"122\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryCar.fxml\",\"controller_path\":\"PurchaseOrder_EntryCar.controller\",\"menu_id\":\"004\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Entry.fxml\",\"controller_path\":\"PurchaseOrder_Entry.controller\",\"menu_id\":\"005\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryLP.fxml\",\"controller_path\":\"PurchaseOrder_EntryLP.controller\",\"menu_id\":\"006\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchFood.fxml\",\"controller_path\":\"PurchaseOrder_EntryMonarchFood.controller\",\"menu_id\":\"007\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMonarchHospitality.fxml\",\"controller_path\":\"PurchaseOrder_EntryMonarchHospitality.controller\",\"menu_id\":\"008\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMC.fxml\",\"controller_path\":\"PurchaseOrder_EntryMC.controller\",\"menu_id\":\"009\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntryMP.fxml\",\"controller_path\":\"PurchaseOrder_EntryMP.controller\",\"menu_id\":\"010\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPCar.fxml\",\"controller_path\":\"PurchaseOrder_EntrySPCar.controller\",\"menu_id\":\"01 02 03 04 05 06\",\"menu_parent\":\"003\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_EntrySPMC.fxml\",\"controller_path\":\"PurchaseOrder_EntrySPMC.controller\",\"menu_id\":\"012\",\"menu_parent\":\"003\"},"
                // Purchase Order Receiving Entry
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order Receiving\",\"fxml_path\":\"Purchase Order Receiving\",\"controller_path\":\"po.controller\",\"menu_id\":\"013\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryAppliances.fxml\",\"controller_path\":\"DeliveryAcceptance_EntryAppliances.controller\",\"menu_id\":\"119\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryCar.fxml\",\"controller_path\":\"DeliveryAcceptance_EntryCar.controller\",\"menu_id\":\"014\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Entry.fxml\",\"controller_path\":\"DeliveryAcceptance_Entry.controller\",\"menu_id\":\"015\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryLP.fxml\",\"controller_path\":\"DeliveryAcceptance_EntryLP.controller\",\"menu_id\":\"016\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchFood.fxml\",\"controller_path\":\"DeliveryAcceptance_EntryMonarchFood.controller\",\"menu_id\":\"017\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMonarchHospitality.fxml\",\"controller_path\":\"DeliveryAcceptance_EntryMonarchHospitality.controller\",\"menu_id\":\"018\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMC.fxml\",\"controller_path\":\"DeliveryAcceptance_EntryMC.controller\",\"menu_id\":\"019\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntryMP.fxml\",\"controller_path\":\"DeliveryAcceptance_EntryMP.controller\",\"menu_id\":\"020\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPCar.fxml\",\"controller_path\":\"DeliveryAcceptance_SPCar.controller\",\"menu_id\":\"021\",\"menu_parent\":\"013\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_EntrySPMC.fxml\",\"controller_path\":\"DeliveryAcceptance_EntrySPMC.controller\",\"menu_id\":\"022\",\"menu_parent\":\"013\"},"
                // Purchase Order Return Entry
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order Return\",\"fxml_path\":\"Purchase Order Return\",\"controller_path\":\"po.controller\",\"menu_id\":\"086\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryAppliances.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntryAppliances.controller\",\"menu_id\":\"087\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryCar.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntryCar.controller\",\"menu_id\":\"088\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Entry.fxml\",\"controller_path\":\"PurchaseOrderReturn_Entry.controller\",\"menu_id\":\"089\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryLP.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntryLP.controller\",\"menu_id\":\"090\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchFood.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntryMonarchFood.controller\",\"menu_id\":\"091\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMonarchHospitality.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntryMonarchHospitality.controller\",\"menu_id\":\"092\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMC.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntryMC.controller\",\"menu_id\":\"093\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntryMP.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntryMP.controller\",\"menu_id\":\"094\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPCar.fxml\",\"controller_path\":\"PurchaseOrderReturn_SPCar.controller\",\"menu_id\":\"095\",\"menu_parent\":\"086\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_EntrySPMC.fxml\",\"controller_path\":\"PurchaseOrderReturn_EntrySPMC.controller\",\"menu_id\":\"096\",\"menu_parent\":\"086\"},"
                // Purchase Order Confirmation
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Confirmation\",\"fxml_path\":\"Confirmation\",\"controller_path\":\"confirmation.controller\",\"menu_id\":\"023\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order\",\"fxml_path\":\"Purchase Order\",\"controller_path\":\"po.controller\",\"menu_id\":\"024\",\"menu_parent\":\"023\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationAppliances.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationAppliances.controller\",\"menu_id\":\"123\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationCar.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationCar.controller\",\"menu_id\":\"025\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Confirmation.fxml\",\"controller_path\":\"PurchaseOrder_Confirmation.controller\",\"menu_id\":\"026\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationLP.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationLP.controller\",\"menu_id\":\"027\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchFood.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationMonarchFood.controller\",\"menu_id\":\"028\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMonarchHospitality.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationMonarchHospitality.controller\",\"menu_id\":\"029\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMC.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationMC.controller\",\"menu_id\":\"030\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationMP.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationMP.controller\",\"menu_id\":\"031\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPCar.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationSPCar.controller\",\"menu_id\":\"032\",\"menu_parent\":\"024\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ConfirmationSPMC.fxml\",\"controller_path\":\"PurchaseOrder_ConfirmationSPMC.controller\",\"menu_id\":\"033\",\"menu_parent\":\"024\"},"
                // Purchase Order Receiving Confirmation
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order Receiving\",\"fxml_path\":\"Purchase Order Receiving\",\"controller_path\":\"po.controller\",\"menu_id\":\"034\",\"menu_parent\":\"023\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationAppliances.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationAppliances.controller\",\"menu_id\":\"120\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationCar.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationCar.controller\",\"menu_id\":\"035\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Confirmation.fxml\",\"controller_path\":\"DeliveryAcceptance_Confirmation.controller\",\"menu_id\":\"036\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationLP.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationLP.controller\",\"menu_id\":\"037\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchFood.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationMonarchFood.controller\",\"menu_id\":\"038\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMonarchHospitality.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationMonarchHospitality.controller\",\"menu_id\":\"039\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMC.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationMC.controller\",\"menu_id\":\"040\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationMP.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationMP.controller\",\"menu_id\":\"041\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPCar.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationSPCar.controller\",\"menu_id\":\"042\",\"menu_parent\":\"034\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationSPMC.fxml\",\"controller_path\":\"DeliveryAcceptance_ConfirmationSPMC.controller\",\"menu_id\":\"043\",\"menu_parent\":\"034\"},"
                // Purchase Order Return Confirmation
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order Return\",\"fxml_path\":\"Purchase Order Return\",\"controller_path\":\"po.controller\",\"menu_id\":\"097\",\"menu_parent\":\"023\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationAppliances.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationAppliances.controller\",\"menu_id\":\"098\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationCar.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationCar.controller\",\"menu_id\":\"099\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_Confirmation.fxml\",\"controller_path\":\"PurchaseOrderReturn_Confirmation.controller\",\"menu_id\":\"100\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationLP.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationLP.controller\",\"menu_id\":\"101\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchFood.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationMonarchFood.controller\",\"menu_id\":\"102\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMonarchHospitality.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationMonarchHospitality.controller\",\"menu_id\":\"103\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMC.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationMC.controller\",\"menu_id\":\"104\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationMP.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationMP.controller\",\"menu_id\":\"105\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPCar.fxml\",\"controller_path\":\"PurchaseOrderReturn_SPCar.controller\",\"menu_id\":\"106\",\"menu_parent\":\"097\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_ConfirmationSPMC.fxml\",\"controller_path\":\"PurchaseOrderReturn_ConfirmationSPMC.controller\",\"menu_id\":\"107\",\"menu_parent\":\"097\"},"
                // Purchase Order Approval
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Approval\",\"fxml_path\":\"Approval\",\"controller_path\":\"approval.controller\",\"menu_id\":\"044\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order\",\"fxml_path\":\"Purchase Order\",\"controller_path\":\"po.controller\",\"menu_id\":\"045\",\"menu_parent\":\"044\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalAppliances.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalAppliances.controller\",\"menu_id\":\"124\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalCar.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalCar.controller\",\"menu_id\":\"046\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_Approval.fxml\",\"controller_path\":\"PurchaseOrder_Approval.controller\",\"menu_id\":\"047\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalLP.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalLP.controller\",\"menu_id\":\"048\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchFood.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalMonarchFood.controller\",\"menu_id\":\"049\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMonarchHospitality.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalMonarchHospitality.controller\",\"menu_id\":\"050\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMC.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalMC.controller\",\"menu_id\":\"051\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalMP.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalMP.controller\",\"menu_id\":\"052\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPCar.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalSPCar.controller\",\"menu_id\":\"053\",\"menu_parent\":\"045\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_ApprovalSPMC.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalSPMC.controller\",\"menu_id\":\"054\",\"menu_parent\":\"045\"},"
                // + "{\"access_level\":\"01 02 03 04 05 06\",\"menu_name\":\"Purchase Order
                // Receiving\",\"fxml_path\":\"Purchase Order
                // Receiving\",\"controller_path\":\"po.controller\",\"menu_id\":\"055\",\"menu_parent\":\"044\"},"
                // +
                // "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalCar.fxml\",\"controller_path\":\"DeliveryAcceptance_ApprovalCar.controller\",\"menu_id\":\"056\",\"menu_parent\":\"055\"},"
                // + "{\"access_level\":\"01 02 03 04 05
                // 06\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_Approval.fxml\",\"controller_path\":\"DeliveryAcceptance_Approval.controller\",\"menu_id\":\"057\",\"menu_parent\":\"055\"},"
                // + "{\"access_level\":\"05\",\"menu_name\":\"Los
                // Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalLP.fxml\",\"controller_path\":\"DeliveryAcceptance_ApprovalLP.controller\",\"menu_id\":\"058\",\"menu_parent\":\"055\"},"
                // + "{\"access_level\":\"04\",\"menu_name\":\"Monarch
                // Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMonarchFood.fxml\",\"controller_path\":\"DeliveryAcceptance_ApprovalMonarchFood.controller\",\"menu_id\":\"059\",\"menu_parent\":\"055\"},"
                // + "{\"access_level\":\"04\",\"menu_name\":\"Monarch
                // Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMonarchHospitality.fxml\",\"controller_path\":\"DeliveryAcceptance_ApprovalMonarchHospitality.controller\",\"menu_id\":\"060\",\"menu_parent\":\"055\"},"
                // +
                // "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMC.fxml\",\"controller_path\":\"DeliveryAcceptance_ApprovalMC.controller\",\"menu_id\":\"061\",\"menu_parent\":\"055\"},"
                // + "{\"access_level\":\"01\",\"menu_name\":\"Mobile
                // Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalMP.fxml\",\"controller_path\":\"PurchaseOrder_ApprovalMP.controller\",\"menu_id\":\"062\",\"menu_parent\":\"055\"},"
                // + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts
                // Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalSPCar.fxml\",\"controller_path\":\"DeliveryAcceptance_ApprovalSPCar.controller\",\"menu_id\":\"063\",\"menu_parent\":\"055\"},"
                // + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts
                // Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ApprovalSPMC.fxml\",\"controller_path\":\"DeliveryAcceptance_ApprovalSPMC.controller\",\"menu_id\":\"064\",\"menu_parent\":\"055\"},"

                // Purchase Order History
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"History\",\"fxml_path\":\"History\",\"controller_path\":\"history.controller\",\"menu_id\":\"065\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order\",\"fxml_path\":\"Purchase Order\",\"controller_path\":\"po.controller\",\"menu_id\":\"066\",\"menu_parent\":\"065\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryAppliances.fxml\",\"controller_path\":\"PurchaseOrder_HistoryAppliances.controller\",\"menu_id\":\"125\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryCar.fxml\",\"controller_path\":\"PurchaseOrder_HistoryCar.controller\",\"menu_id\":\"067\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_History.fxml\",\"controller_path\":\"PurchaseOrder_History.controller\",\"menu_id\":\"068\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryLP.fxml\",\"controller_path\":\"PurchaseOrder_HistoryLP.controller\",\"menu_id\":\"069\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchFood.fxml\",\"controller_path\":\"PurchaseOrder_HistoryMonarchFood.controller\",\"menu_id\":\"070\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMonarchHospitality.fxml\",\"controller_path\":\"PurchaseOrder_HistoryMonarchHospitality.controller\",\"menu_id\":\"071\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMC.fxml\",\"controller_path\":\"PurchaseOrder_HistoryMC.controller\",\"menu_id\":\"072\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistoryMP.fxml\",\"controller_path\":\"PurchaseOrder_HistoryMP.controller\",\"menu_id\":\"073\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPCar.fxml\",\"controller_path\":\"PurchaseOrder_HistorySPCar.controller\",\"menu_id\":\"074\",\"menu_parent\":\"066\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrder_HistorySPMC.fxml\",\"controller_path\":\"PurchaseOrder_HistorySPMC.controller\",\"menu_id\":\"075\",\"menu_parent\":\"066\"},"
                // Purchase Order Receiving History
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order Receiving\",\"fxml_path\":\"Purchase Order Receiving\",\"controller_path\":\"po.controller\",\"menu_id\":\"076\",\"menu_parent\":\"065\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryAppliances.fxml\",\"controller_path\":\"DeliveryAcceptance_HistoryAppliances.controller\",\"menu_id\":\"121\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryCar.fxml\",\"controller_path\":\"DeliveryAcceptance_HistoryCar.controller\",\"menu_id\":\"077\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_History.fxml\",\"controller_path\":\"DeliveryAcceptance_History.controller\",\"menu_id\":\"078\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryLP.fxml\",\"controller_path\":\"DeliveryAcceptance_HistoryLP.controller\",\"menu_id\":\"079\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchFood.fxml\",\"controller_path\":\"DeliveryAcceptance_HistoryMonarchFood.controller\",\"menu_id\":\"080\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMonarchHospitality.fxml\",\"controller_path\":\"DeliveryAcceptance_HistoryMonarchHospitality.controller\",\"menu_id\":\"081\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMC.fxml\",\"controller_path\":\"DeliveryAcceptance_HistoryMC.controller\",\"menu_id\":\"082\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistoryMP.fxml\",\"controller_path\":\"DeliveryAcceptance_HistoryMP.controller\",\"menu_id\":\"083\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPCar.fxml\",\"controller_path\":\"DeliveryAcceptance_HistorySPCar.controller\",\"menu_id\":\"084\",\"menu_parent\":\"076\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_HistorySPMC.fxml\",\"controller_path\":\"DeliveryAcceptance_HistorySPMC.controller\",\"menu_id\":\"085\",\"menu_parent\":\"076\"},"
                // Purchase Order Return History
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"Purchase Order Return\",\"fxml_path\":\"Purchase Order Return\",\"controller_path\":\"po.controller\",\"menu_id\":\"108\",\"menu_parent\":\"065\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryAppliances.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistoryAppliances.controller\",\"menu_id\":\"109\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryCar.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistoryCar.controller\",\"menu_id\":\"110\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"01 02 03 04 05 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_History.fxml\",\"controller_path\":\"PurchaseOrderReturn_History.controller\",\"menu_id\":\"111\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryLP.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistoryLP.controller\",\"menu_id\":\"112\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchFood.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistoryMonarchFood.controller\",\"menu_id\":\"113\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMonarchHospitality.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistoryMonarchHospitality.controller\",\"menu_id\":\"114\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMC.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistoryMC.controller\",\"menu_id\":\"115\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistoryMP.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistoryMP.controller\",\"menu_id\":\"116\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Spare Parts Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPCar.fxml\",\"controller_path\":\"PurchaseOrderReturn_SPCar.controller\",\"menu_id\":\"117\",\"menu_parent\":\"108\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Spare Parts Motorycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PurchaseOrderReturn_HistorySPMC.fxml\",\"controller_path\":\"PurchaseOrderReturn_HistorySPMC.controller\",\"menu_id\":\"118\",\"menu_parent\":\"108\"}"
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
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Sales\", \"fxml_path\": \"Sales\", \"controller_path\": \"sample.controller\", \"menu_id\": \"001\", \"menu_parent\": \"\", \"level\": 0},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Sales\", \"fxml_path\": \"Sales/Sales\", \"controller_path\": \"sample.controller\", \"menu_id\": \"002\", \"menu_parent\": \"001\", \"level\": 1},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/Sales/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"003\", \"menu_parent\": \"002\", \"level\": 2},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/Sales/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"004\", \"menu_parent\": \"002\", \"level\": 2},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Inquiry\", \"fxml_path\": \"Sales/Inquiry\", \"controller_path\": \"sample.controller\", \"menu_id\": \"005\", \"menu_parent\": \"001\", \"level\": 1},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/Inquiry/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"006\", \"menu_parent\": \"005\", \"level\": 2},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/Inquiry/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"007\", \"menu_parent\": \"005\", \"level\": 2},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"History\", \"fxml_path\": \"Sales/History\", \"controller_path\": \"sample.controller\", \"menu_id\": \"008\", \"menu_parent\": \"001\", \"level\": 1},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Sales\", \"fxml_path\": \"Sales/History/Sales\", \"controller_path\": \"sample.controller\", \"menu_id\": \"013\", \"menu_parent\": \"008\", \"level\": 2},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/History/Inquiry/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"014\", \"menu_parent\": \"013\", \"level\": 3},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/History/Inquiry/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"015\", \"menu_parent\": \"013\", \"level\": 3}"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Inquiry\", \"fxml_path\": \"Sales/History/Inquiry\", \"controller_path\": \"sample.controller\", \"menu_id\": \"009\", \"menu_parent\": \"008\", \"level\": 2},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Motorcycle\", \"fxml_path\": \"Sales/History/Sales/Motorcycle\", \"controller_path\": \"\", \"menu_id\": \"01 02 03 04 05 06\", \"menu_parent\": \"009\", \"level\": 3},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Spareparts\", \"fxml_path\": \"Sales/History/Sales/Spareparts\", \"controller_path\": \"\", \"menu_id\": \"012\", \"menu_parent\": \"009\", \"level\": 3},"
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
//        String jsonString = "["
//                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Accounts\", \"fxml_path\": \"Accounts\", \"controller_path\": \"\", \"menu_id\": \"001\", \"menu_parent\": \"\"},"
//                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Accounts Payable\", \"fxml_path\": \"Accounts/Accounts Payable\", \"controller_path\": \"\", \"menu_id\": \"002\", \"menu_parent\": \"001\"},"
//                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Accounts Receivable\", \"fxml_path\": \"Accounts/Accounts Receivable\", \"controller_path\": \"\", \"menu_id\": \"003\", \"menu_parent\": \"001\"}"
//                + "]";
        String jsonString = "["
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Accounts Payables\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"001\",\"menu_parent\":\"\"},"
                //Entry
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Entry\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"002\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Payment Request\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Entry.fxml\",\"controller_path\":\"PaymentRequestEntry.controller\",\"menu_id\":\"003\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Disbursement Voucher\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Entry.fxml\",\"controller_path\":\"DisbursementVoucher_EntryController\",\"menu_id\":\"009\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Check Print Request\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Entry.fxml\",\"controller_path\":\"CheckPrintRequest_EntryController\",\"menu_id\":\"017\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Check Printing\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/CheckPrinting.fxml\",\"controller_path\":\"CheckPrintingController\",\"menu_id\":\"018\",\"menu_parent\":\"002\"},"
                //AP Adjustment
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"AP Adjustment\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"019\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Entry.fxml\",\"controller_path\":\"APPaymentAdjustmentEntry.controller\",\"menu_id\":\"020\",\"menu_parent\":\"019\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryAppliances.fxml\",\"controller_path\":\"APPaymentAdjustmentEntry.controller\",\"menu_id\":\"021\",\"menu_parent\":\"019\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryCar.fxml\",\"controller_path\":\"APPaymentAdjustmentEntry.controller\",\"menu_id\":\"022\",\"menu_parent\":\"019\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMC.fxml\",\"controller_path\":\"APPaymentAdjustmentEntry.controller\",\"menu_id\":\"023\",\"menu_parent\":\"019\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMP.fxml\",\"controller_path\":\"APPaymentAdjustmentEntry.controller\",\"menu_id\":\"024\",\"menu_parent\":\"019\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryLP.fxml\",\"controller_path\":\"APPaymentAdjustmentEntry.controller\",\"menu_id\":\"025\",\"menu_parent\":\"019\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_EntryMonarch.fxml\",\"controller_path\":\"APPaymentAdjustmentEntry.controller\",\"menu_id\":\"026\",\"menu_parent\":\"019\"},"
                //SOA TAGGING
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"SOA Tagging\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"043\",\"menu_parent\":\"002\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Entry.fxml\",\"controller_path\":\"SOATaggingEntry.controller\",\"menu_id\":\"044\",\"menu_parent\":\"043\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryAppliances.fxml\",\"controller_path\":\"SOATaggingEntry.controller\",\"menu_id\":\"045\",\"menu_parent\":\"043\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryCar.fxml\",\"controller_path\":\"SOATaggingEntry.controller\",\"menu_id\":\"046\",\"menu_parent\":\"043\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMC.fxml\",\"controller_path\":\"SOATaggingEntry.controller\",\"menu_id\":\"047\",\"menu_parent\":\"043\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMP.fxml\",\"controller_path\":\"SOATaggingEntry.controller\",\"menu_id\":\"048\",\"menu_parent\":\"043\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryLP.fxml\",\"controller_path\":\"SOATaggingEntry.controller\",\"menu_id\":\"049\",\"menu_parent\":\"043\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_EntryMonarch.fxml\",\"controller_path\":\"SOATaggingEntry.controller\",\"menu_id\":\"050\",\"menu_parent\":\"043\"},"
                //Confirmation
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Confirmation\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"004\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Payment Request\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_Confirmation.fxml\",\"controller_path\":\"PaymentRequestConfirmation.controller\",\"menu_id\":\"005\",\"menu_parent\":\"004\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Check Print Request Confirmation\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/CheckPrintRequest_Confirmation.fxml\",\"controller_path\":\"CheckPrintRequest_ConfirmationController\",\"menu_id\":\"016\",\"menu_parent\":\"004\"},"
                //AP Adjustment
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"AP Adjustment\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"027\",\"menu_parent\":\"004\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_Confirmation.fxml\",\"controller_path\":\"APPaymentAdjustmentConfirmation.controller\",\"menu_id\":\"028\",\"menu_parent\":\"027\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationAppliances.fxml\",\"controller_path\":\"APPaymentAdjustmentConfirmation.controller\",\"menu_id\":\"029\",\"menu_parent\":\"027\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationCar.fxml\",\"controller_path\":\"APPaymentAdjustmentConfirmation.controller\",\"menu_id\":\"030\",\"menu_parent\":\"027\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMC.fxml\",\"controller_path\":\"APPaymentAdjustmentConfirmation.controller\",\"menu_id\":\"031\",\"menu_parent\":\"027\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMP.fxml\",\"controller_path\":\"APPaymentAdjustmentConfirmation.controller\",\"menu_id\":\"032\",\"menu_parent\":\"027\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationLP.fxml\",\"controller_path\":\"APPaymentAdjustmentConfirmation.controller\",\"menu_id\":\"033\",\"menu_parent\":\"027\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_ConfirmationMonarch.fxml\",\"controller_path\":\"APPaymentAdjustmentConfirmation.controller\",\"menu_id\":\"034\",\"menu_parent\":\"027\"},"
                //SOA TAGGING
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"SOA Tagging\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"051\",\"menu_parent\":\"004\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_Confirmation.fxml\",\"controller_path\":\"SOATaggingConfirmation.controller\",\"menu_id\":\"052\",\"menu_parent\":\"051\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationAppliances.fxml\",\"controller_path\":\"SOATaggingConfirmation.controller\",\"menu_id\":\"053\",\"menu_parent\":\"051\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationCar.fxml\",\"controller_path\":\"SOATaggingConfirmation.controller\",\"menu_id\":\"054\",\"menu_parent\":\"051\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMC.fxml\",\"controller_path\":\"SOATaggingConfirmation.controller\",\"menu_id\":\"055\",\"menu_parent\":\"051\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMP.fxml\",\"controller_path\":\"SOATaggingConfirmation.controller\",\"menu_id\":\"056\",\"menu_parent\":\"051\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationLP.fxml\",\"controller_path\":\"SOATaggingConfirmation.controller\",\"menu_id\":\"057\",\"menu_parent\":\"051\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_ConfirmationMonarch.fxml\",\"controller_path\":\"SOATaggingConfirmation.controller\",\"menu_id\":\"058\",\"menu_parent\":\"051\"},"
                //Verification
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Verification\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"010\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Disbursement Voucher\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Verification.fxml\",\"controller_path\":\"DisbursementVoucher_VerificationController\",\"menu_id\":\"011\",\"menu_parent\":\"010\"},"
                //Cerification
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Certification\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"012\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Disbursement Voucher\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_Certification.fxml\",\"controller_path\":\"DisbursementVoucher_CertificationController\",\"menu_id\":\"013\",\"menu_parent\":\"012\"},"
                //Approval
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Approval\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml\",\"controller_path\":\"PaymentRequest.controller\",\"menu_id\":\"006\",\"menu_parent\":\"001\"},"
                //Authorize
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Authorize\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"014\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Check Authorization\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/CheckAuthorization.fxml\",\"controller_path\":\"CheckAuthorizationController\",\"menu_id\":\"015\",\"menu_parent\":\"014\"},"
                //History
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"History\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest.fxml\",\"controller_path\":\"PaymentRequest.controller\",\"menu_id\":\"007\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Payment Request\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/PaymentRequest_History.fxml\",\"controller_path\":\"PaymentRequestHistoryController\",\"menu_id\":\"008\",\"menu_parent\":\"007\"}"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"Disbursement Voucher\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/DisbursementVoucher_History.fxml\",\"controller_path\":\"DisbursementVoucher_HistoryController\",\"menu_id\":\"016\",\"menu_parent\":\"007\"},"
                //AP Adjustment
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"AP Adjustment\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"035\",\"menu_parent\":\"007\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_History.fxml\",\"controller_path\":\"APPaymentAdjustmentHistory.controller\",\"menu_id\":\"036\",\"menu_parent\":\"035\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryAppliances.fxml\",\"controller_path\":\"APPaymentAdjustmentHistory.controller\",\"menu_id\":\"037\",\"menu_parent\":\"035\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryCar.fxml\",\"controller_path\":\"APPaymentAdjustmentHistory.controller\",\"menu_id\":\"038\",\"menu_parent\":\"035\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMC.fxml\",\"controller_path\":\"APPaymentAdjustmentHistory.controller\",\"menu_id\":\"039\",\"menu_parent\":\"035\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMP.fxml\",\"controller_path\":\"APPaymentAdjustmentHistory.controller\",\"menu_id\":\"040\",\"menu_parent\":\"035\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryLP.fxml\",\"controller_path\":\"APPaymentAdjustmentHistory.controller\",\"menu_id\":\"041\",\"menu_parent\":\"035\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/APPaymentAdjustment_HistoryMonarch.fxml\",\"controller_path\":\"APPaymentAdjustmentHistory.controller\",\"menu_id\":\"042\",\"menu_parent\":\"035\"},"
                //SOA TAGGING
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"SOA Tagging\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"059\",\"menu_parent\":\"007\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_History.fxml\",\"controller_path\":\"SOATaggingHistory.controller\",\"menu_id\":\"060\",\"menu_parent\":\"059\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryAppliances.fxml\",\"controller_path\":\"SOATaggingHistory.controller\",\"menu_id\":\"061\",\"menu_parent\":\"059\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryCar.fxml\",\"controller_path\":\"SOATaggingHistory.controller\",\"menu_id\":\"062\",\"menu_parent\":\"059\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMC.fxml\",\"controller_path\":\"SOATaggingHistory.controller\",\"menu_id\":\"063\",\"menu_parent\":\"059\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMP.fxml\",\"controller_path\":\"SOATaggingHistory.controller\",\"menu_id\":\"064\",\"menu_parent\":\"059\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryLP.fxml\",\"controller_path\":\"SOATaggingHistory.controller\",\"menu_id\":\"065\",\"menu_parent\":\"059\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SOATagging_HistoryMonarch.fxml\",\"controller_path\":\"SOATaggingHistory.controller\",\"menu_id\":\"066\",\"menu_parent\":\"059\"},"
                //SI Posting
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"SI Posting\",\"fxml_path\":\"\",\"controller_path\":\"\",\"menu_id\":\"067\",\"menu_parent\":\"001\"},"
                + "{\"access_level\":\"07\",\"menu_name\":\"Appliances\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Appliances.fxml\",\"controller_path\":\"SIPosting_Appliances.controller\",\"menu_id\":\"068\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"01 02 03 04 05 00 06 07\",\"menu_name\":\"General\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting.fxml\",\"controller_path\":\"SIPosting.controller\",\"menu_id\":\"069\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"Car\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_Car.fxml\",\"controller_path\":\"SIPosting_Car.controller\",\"menu_id\":\"070\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"03\",\"menu_name\":\"SPCar\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPCar.fxml\",\"controller_path\":\"SIPosting_SPCar.controller\",\"menu_id\":\"071\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"Motorcycle\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MC.fxml\",\"controller_path\":\"SIPosting_MC.controller\",\"menu_id\":\"072\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"02\",\"menu_name\":\"SPMC\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_SPMC.fxml\",\"controller_path\":\"SIPosting_SPMC.controller\",\"menu_id\":\"073\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"01\",\"menu_name\":\"Mobile Phone\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MP.fxml\",\"controller_path\":\"SIPosting_MP.controller\",\"menu_id\":\"074\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"04\",\"menu_name\":\"Monarch Hospitality\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchHospitality.fxml\",\"controller_path\":\"SIPosting_MonarchHospitality.controller\",\"menu_id\":\"075\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Monarch Restaurant\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_MonarchFood.fxml\",\"controller_path\":\"SIPosting_MonarchFood.controller\",\"menu_id\":\"076\",\"menu_parent\":\"067\"},"
                + "{\"access_level\":\"05\",\"menu_name\":\"Los Pedritos\",\"fxml_path\":\"/com/rmj/guanzongroup/sidebarmenus/views/SIPosting_LP.fxml\",\"controller_path\":\"SIPosting_LP.controller\",\"menu_id\":\"077\",\"menu_parent\":\"067\"}"
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
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Parameters\", \"fxml_path\": \"\", \"controller_path\": \"sample.controller\", \"menu_id\": \"001\", \"menu_parent\": \"\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Address\", \"fxml_path\": \"\", \"controller_path\": \"sample.controller\", \"menu_id\": \"002\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Barangay\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/Barangay.fxml\", \"controller_path\": \"sample.controller\", \"menu_id\": \"003\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Country\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/Country.fxml\", \"controller_path\": \"\", \"menu_id\": \"004\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Province\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/Province.fxml\", \"controller_path\": \"\", \"menu_id\": \"005\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Region\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/Region.fxml\", \"controller_path\": \"\", \"menu_id\": \"006\", \"menu_parent\": \"002\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Banks\", \"fxml_path\": \"\", \"controller_path\": \"\", \"menu_id\": \"007\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Banks\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/Banks.fxml\", \"controller_path\": \"\", \"menu_id\": \"008\", \"menu_parent\": \"007\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Banks Branches\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/BanksBranches.fxml\", \"controller_path\": \"\", \"menu_id\": \"009\", \"menu_parent\": \"007\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Category\", \"fxml_path\": \"\", \"controller_path\": \"\", \"menu_id\": \"010\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Category\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/Category.fxml\", \"controller_path\": \"\", \"menu_id\": \"01 02 03 04 05 06\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Category Level 2\", \"fxml_path\": \"/com/rmj/guanzongroup/sidebarmenus/views/CategoryLevel2.fxml\", \"controller_path\": \"\", \"menu_id\": \"012\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Category Level 3\", \"fxml_path\": \"Parameters/Category/Category Level 3\", \"controller_path\": \"\", \"menu_id\": \"013\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Category Level 4\", \"fxml_path\": \"Parameters/Category/Category Level 4\", \"controller_path\": \"\", \"menu_id\": \"014\", \"menu_parent\": \"010\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Color\", \"fxml_path\": \"Parameters/Color\", \"controller_path\": \"\", \"menu_id\": \"015\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Color\", \"fxml_path\": \"Parameters/Color/Color\", \"controller_path\": \"\", \"menu_id\": \"016\", \"menu_parent\": \"015\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Color Detail\", \"fxml_path\": \"Parameters/Color/Color Detail\", \"controller_path\": \"\", \"menu_id\": \"017\", \"menu_parent\": \"015\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Company\", \"fxml_path\": \"Parameters/Company\", \"controller_path\": \"\", \"menu_id\": \"018\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Affiliated Company\", \"fxml_path\": \"Parameters/Company/Affiliated Company\", \"controller_path\": \"\", \"menu_id\": \"019\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Branch\", \"fxml_path\": \"Parameters/Company/Branch\", \"controller_path\": \"\", \"menu_id\": \"020\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Company\", \"fxml_path\": \"Parameters/Company/Company\", \"controller_path\": \"\", \"menu_id\": \"021\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Department\", \"fxml_path\": \"Parameters/Company/Department\", \"controller_path\": \"\", \"menu_id\": \"022\", \"menu_parent\": \"018\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Inventory\", \"fxml_path\": \"Parameters/Inventory\", \"controller_path\": \"\", \"menu_id\": \"023\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Inventory Location\", \"fxml_path\": \"Parameters/Inventory/Inventory Location\", \"controller_path\": \"\", \"menu_id\": \"024\", \"menu_parent\": \"023\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Inventory Type\", \"fxml_path\": \"Parameters/Inventory/Inventory Type\", \"controller_path\": \"\", \"menu_id\": \"025\", \"menu_parent\": \"023\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Labor\", \"fxml_path\": \"Parameters/Labor\", \"controller_path\": \"\", \"menu_id\": \"026\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Labor\", \"fxml_path\": \"Parameters/Labor/Labor\", \"controller_path\": \"\", \"menu_id\": \"027\", \"menu_parent\": \"026\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Labor Category\", \"fxml_path\": \"Parameters/Labor/Labor Category\", \"controller_path\": \"\", \"menu_id\": \"028\", \"menu_parent\": \"026\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Labor Model\", \"fxml_path\": \"Parameters/Labor/Labor Model\", \"controller_path\": \"\", \"menu_id\": \"029\", \"menu_parent\": \"026\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Others\", \"fxml_path\": \"Parameters/Others\", \"controller_path\": \"\", \"menu_id\": \"030\", \"menu_parent\": \"001\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Brand\", \"fxml_path\": \"Parameters/Others/Brand\", \"controller_path\": \"\", \"menu_id\": \"031\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Made\", \"fxml_path\": \"Parameters/Others/Made\", \"controller_path\": \"\", \"menu_id\": \"032\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Model\", \"fxml_path\": \"Parameters/Others/Model\", \"controller_path\": \"\", \"menu_id\": \"033\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Measure\", \"fxml_path\": \"Parameters/Others/Measure\", \"controller_path\": \"\", \"menu_id\": \"034\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Relationship\", \"fxml_path\": \"Parameters/Others/Relationship\", \"controller_path\": \"\", \"menu_id\": \"035\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Salesman\", \"fxml_path\": \"Parameters/Others/Salesman\", \"controller_path\": \"\", \"menu_id\": \"036\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Section\", \"fxml_path\": \"Parameters/Others/Section\", \"controller_path\": \"\", \"menu_id\": \"037\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Size\", \"fxml_path\": \"Parameters/Others/Size\", \"controller_path\": \"\", \"menu_id\": \"038\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Term\", \"fxml_path\": \"Parameters/Others/Term\", \"controller_path\": \"\", \"menu_id\": \"039\", \"menu_parent\": \"030\"},"
                + "{\"access_level\": \"01 02 03 04 05 06\", \"menu_name\": \"Warehouse\", \"fxml_path\": \"Parameters/Others/Warehouse\", \"controller_path\": \"\", \"menu_id\": \"040\", \"menu_parent\": \"030\"}"
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
        String userDepartment = psUserIndustryId;
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
//            menuIndustryMap.clear();
//            menuCategoryMap.clear();

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
//                String lsIndustryCode = String.valueOf(loParent.get("industry_code"));
//                String lsCategoryCode = String.valueOf(loParent.get("category_code"));

                TreeItem<String> parentNode = new TreeItem<>(parentName);
                menuLocationMap.put(parentNode, location); // Store location
//                menuIndustryMap.put(parentNode, lsIndustryCode); // Store industry code
//                menuCategoryMap.put(parentNode, lsCategoryCode); // Store category code

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
//            String lsIndustryCode = String.valueOf(loDetail.get("industry_code"));
//            String lsCategoryCode = String.valueOf(loDetail.get("category_code"));

            TreeItem<String> childNode = new TreeItem<>(parentName);
            menuLocationMap.put(childNode, location);
//            menuIndustryMap.put(childNode, lsIndustryCode); // Store industry code
//            menuCategoryMap.put(childNode, lsCategoryCode); // Store category code

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

        // Get the location directly from menuLocationMap
        sformname = menuLocationMap.getOrDefault(newValue, "");
//        psIndustryID = menuIndustryMap.getOrDefault(newValue, "");
//        psCategoryID = menuCategoryMap.getOrDefault(newValue, "");
        if (oApp != null) {
            boolean isNewTab = (checktabs(SetTabTitle(sformname)) == 1);
            if (isNewTab) {
                if (!sformname.isEmpty() && sformname.contains(".fxml")) {

                    System.out.println("industry: " + psIndustryID);
                    System.out.println("category: " + psCategoryID);
                    setScene2(loadAnimate(sformname));
                } else {
                    ShowMessageFX.Warning("This form is currently unavailable.", "Computerized Accounting System", pxeModuleName);
                }
            } else {
                ShowMessageFX.Warning("This form is already active.", "Computerized Accounting System", pxeModuleName);
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
                                        ShowMessageFX.Warning("This form is currently unavailable.", "Computerized Accounting System", pxeModuleName);
                                    }
                                } else {
                                    ShowMessageFX.Warning("This form is already active.", "Computerized Accounting System", pxeModuleName);
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
        try {
            AppUser.setText(oApp.getLogName() + " || " + getAllIndustries(oApp.getIndustry()));
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void setKeyEvent(Scene scene) {
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F12) {
                if (LoginControllerHolder.getLogInStatus()) {
                    //check here if the user level is supervisor
                    //check if the current tab is not entry
                    if (LoginControllerHolder.getLogInStatus()) {
                        Tab currentTab = tabpane.getSelectionModel().getSelectedItem();
                        if (currentTab != null) {
                            try {
                                if (!sformname.contains("PurchaseOrder")) {
                                    if (!sformname.contains("DeliveryAcceptance_History")) {
                                        return;
                                    }
                                }
                                if (oApp.isMainOffice()) {
                                    loadSelectIndustryAndCompany();
                                }
                            } catch (IOException e) {
                                ShowMessageFX.Warning("Unable to load selection window.", "Error", e.getMessage());
                            }
                        }
                    }
                }
            }

        }
        );
    }

    private void loadSelectIndustryAndCompany() throws IOException {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/com/rmj/guanzongroup/sidebarmenus/views/SelectIndustryCompany.fxml"));
            SelectIndustryCompany loControl = new SelectIndustryCompany();
            loControl.setGRider(oApp);
            loControl.setOldIndsutryID(psUserIndustryId);
            loControl.setOldCompanyID(psUserCompanyId);
            loControl.setOldCategoryID(psCategoryID);
            fxmlLoader.setController(loControl);

            //get industry of current opend form
            SetTabTitle(sformname);
            String lsOldForm = getFormIndustry(psIndustryID, psCategoryID);
            String lsOldCompany = psCompanyID;
            //load the main interface
            Parent parent = fxmlLoader.load();

            parent.setOnMousePressed((MouseEvent event) -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            parent.setOnMouseDragged((MouseEvent event) -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            //set the main interface as the scene/*
            Scene scene = new Scene(parent);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            scene.setFill(Color.TRANSPARENT);
            stage.setTitle("");
            stage.showAndWait();
            if (loControl.isFromFilter()) {
                psIndustryID = loControl.getSelectedIndustryID();
                psCompanyID = loControl.getSelectedCompanyID();
                psCategoryID = loControl.getSelectedCategoryID();
                String lsIndustry = getFormIndustry(psIndustryID, psCategoryID);
                //change form name base on selected industry
                //  /com/rmj/guanzongroup/sidebarmenus/views/DeliveryAcceptance_ConfirmationCar.fxml

                System.out.println("OLD : " + sformname);
                String originalString = sformname;
                String updatedString = originalString.replace(lsOldForm + ".fxml", lsIndustry + ".fxml");

                // Print the updated string
                System.out.println(originalString);
                System.out.println(updatedString);
                sformname = updatedString;

                System.out.println("NEW : " + sformname);
                if (oApp != null) {
                    boolean isNewTab = (checktabs(SetTabTitle(sformname)) == 1);
                    if (isNewTab || !lsOldCompany.equals(psCompanyID)) {
                        if (!sformname.isEmpty() && sformname.contains(".fxml")) {
                            setScene2(loadAnimateExchange(sformname));
                        } else {
                            ShowMessageFX.Warning("This form is currently unavailable.", "Computerized Accounting System", pxeModuleName);
                        }
                    } else {
                        ShowMessageFX.Warning("This form is already active.", "Computerized Accounting System", pxeModuleName);
                    }
                    setAnchorPaneVisibleManage(false, anchorLeftSideBarMenu);
                    for (ToggleButton navButton : toggleBtnLeftUpperSideBar) {
                        navButton.setSelected(false);
                    }
                    pane.requestFocus();
                }

                isFromFilter = loControl.isFromFilter();
            }
        } catch (IOException e) {
            ShowMessageFX.Warning(e.getMessage(), "Warning", null);
            System.exit(1);

        }
    }

    private String getFormIndustry(String industryId, String categoryId) {
        String concatName = "";
        switch (industryId) {
            case "01":
                concatName = "MP";
                break;
            case "02":
                if ("0003".equals(categoryId)) {
                    concatName = "MC";   // Motorcycle
                }
                if ("0004".equals(categoryId)) {
                    concatName = "SPMC"; // Spare Parts
                }
            case "03":
                if ("0005".equals(categoryId)) {
                    concatName = "Car";   // Vehicle
                }
                if ("0006".equals(categoryId)) {
                    concatName = "SPCar"; // Spare Parts
                }
                break;
            case "04":
                if ("0021".equals(categoryId)) {
                    concatName = "MonarchFood";  // Food Service
                }
                if ("0009".equals(categoryId)) {
                    concatName = "MonarchHospitality";  // Hospitality
                }
                break;
            case "05":
                concatName = "LP";
                break;
            case "07":
                concatName = "Appliances";
                break;
            case "00":
                concatName = "";
                break;
            default:
                concatName = "";
                break;
        }
        return concatName;
    }

    public TabPane loadAnimateExchange(String fsFormName) {
        setTabPane();
        setPane();

        ScreenInterface fxObj = getController(fsFormName);
        fxObj.setGRider(oApp);
        fxObj.setCompanyID(psCompanyID);
        fxObj.setIndustryID(psIndustryID);
        fxObj.setCategoryID(psCategoryID);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(fxObj.getClass().getResource(fsFormName));
        fxmlLoader.setController(fxObj);

        try {
            Node content = fxmlLoader.load();
            Tab selectedTab = tabpane.getSelectionModel().getSelectedItem();

            if (selectedTab != null) {
                // Update title and content of the selected tab
                String newTitle = SetTabTitle(fsFormName);

                // Update tab name in the tracking list
                int index = tabName.indexOf(selectedTab.getText());
                if (index != -1) {
                    tabName.set(index, newTitle);
                }

                selectedTab.setText(newTitle);
                selectedTab.setContent(content);
                selectedTab.setContextMenu(createContextMenu(tabpane, selectedTab, oApp));

                selectedTab.setOnCloseRequest(event -> {
                    if (ShowMessageFX.YesNo(null, "Close Tab", "Are you sure, do you want to close tab?")) {
                        tabName.remove(selectedTab.getText());
                        Tabclose();
                    } else {
                        event.consume();
                    }
                });

                selectedTab.setOnSelectionChanged(event -> {
                    ObservableList<Tab> tabs = tabpane.getTabs();
                    for (Tab tab : tabs) {
                        if (tab.getText().equals(selectedTab.getText())) {
                            tabName.remove(selectedTab.getText());
                            tabName.add(selectedTab.getText());
                            break;
                        }
                    }
                });
            }

            return tabpane;

        } catch (IOException e) {
            ShowMessageFX.Warning(e.getMessage(), "FXML Load Error", null);
            return null;
        }
    }

    private String getAllIndustries(String industryid) throws SQLException {
        String industryname = "";
        String lsSQL = "SELECT * FROM industry";
        lsSQL = MiscUtil.addCondition(lsSQL, "cRecdStat = " + SQLUtil.toSQL(Logical.YES));
        ResultSet loRS = oApp.executeQuery(lsSQL);

        while (loRS.next()) {
            String id = loRS.getString("sIndstCdx");
            String description = loRS.getString("sDescript");

            if (industryid.equals(id)) {
                industryname = description;
            }
        }

        MiscUtil.close(loRS);
        return industryname;

    }

    public void changeUserInfo(String industryid) {
        try {
            AppUser.setText(oApp.getLogName() + " || " + getAllIndustries(industryid));
        } catch (SQLException ex) {
            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
