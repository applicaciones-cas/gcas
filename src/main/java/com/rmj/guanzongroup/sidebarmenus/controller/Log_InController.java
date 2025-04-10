/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.LogWrapper;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.Logical;

/**
 * FXML Controller class
 *
 * @author User
 */
public class Log_InController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Log In";
    private GRiderCAS oApp;
    private String psIndustryID = "";
    private String psCompanyID = "";

    private LogWrapper poLogWrapper;
    ObservableList<Industry> industryOptions = FXCollections.observableArrayList();
    ObservableList<Company> companyOptions = FXCollections.observableArrayList();
    @FXML
    private TextField txtField01;
    @FXML
    private Button btnSignIn;
    @FXML
    Label lblCopyright;
    @FXML
    private TextField txtField03;
    @FXML
    private PasswordField txtField02;
    @FXML
    private Button btnEyeIcon;
    @FXML
    private ComboBox cmbIndustry, cmbCompany;

    @Override
    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }

    @Override
    public void setIndustryID(String fsValue) {
        psIndustryID = fsValue;
    }

    @Override
    public void setCompanyID(String fsValue) {
        psIndustryID = fsValue;
    }

    /**
     * Initializes the controller class.
     */
    private DashboardController dashboardController;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DashboardController mainController = LoginControllerHolder.getMainController();
        mainController.triggervbox();

        txtField03.textProperty().bindBidirectional(txtField02.textProperty());
        String year = String.valueOf(Year.now().getValue());
        lblCopyright.setText("© " + year + " Guanzon Group of Companies. All Rights Reserved.");
        loadComboBoxItems();
        initComboBox();

    }

    public void setMainController(DashboardController controller) {
        this.dashboardController = controller;
    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        switch (lsButton) {
            case "btnSignIn":
                DashboardController dashboardController = LoginControllerHolder.getMainController();
                dashboardController.triggervbox2();
                dashboardController.setUserIndustry(psIndustryID);
                dashboardController.setUserCompany(psCompanyID);

                LoginControllerHolder.setLogInStatus(true);
                break;

            case "btnEyeIcon":
                FontAwesomeIconView eyeIcon = new FontAwesomeIconView(FontAwesomeIcon.EYE);
                if (txtField02.isVisible()) {
                    txtField03.setText(txtField02.getText());
                    txtField02.setVisible(false);
                    txtField03.setVisible(true);
                    eyeIcon.setIcon(FontAwesomeIcon.EYE_SLASH);
                    eyeIcon.setStyle("-fx-fill: gray; -glyph-size: 20; ");
                    btnEyeIcon.setGraphic(eyeIcon);
                } else {
                    txtField02.setText(txtField03.getText());
                    txtField03.setVisible(false);
                    txtField02.setVisible(true);
                    eyeIcon.setIcon(FontAwesomeIcon.EYE);
                    eyeIcon.setStyle("-fx-fill: gray; -glyph-size: 20; ");
                    btnEyeIcon.setGraphic(eyeIcon);
                }
                break;
            default:
                break;
        }

    }

    private <T> void initComboBoxCellDesign(ComboBox<T> comboBox, Class<T> modelClass) {
        comboBox.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setStyle("");  // Reset to default style for non-selected items

                if (empty) {
                    setText(null);
                    setStyle("");  // Reset style if the item is empty
                } else {
                    setText(item.toString());  // Display the item text using its toString method

                    // Check if this item is the selected value
                    if (item.equals(comboBox.getValue())) {
                        // Apply the custom background color for the selected item in the list
                        setStyle("-fx-background-color: #FF8201; -fx-text-fill: white;");
                    } else {
                        setStyle("");  // Reset to default style for non-selected items
                    }
                }
            }
        });

        comboBox.setOnShowing(event -> {
            T selectedItem = comboBox.getValue();
            if (selectedItem != null) {
                // Loop through each item and apply style based on selection
                for (int i = 0; i < comboBox.getItems().size(); i++) {
                    T item = comboBox.getItems().get(i);

                    if (item.equals(selectedItem)) {
                        // Apply the custom background color for selected item in the list
                        comboBox.getItems().set(i, item);
                    } else {
                        // Reset the style for non-selected items
                        comboBox.getItems().set(i, item);
                    }
                }
            }
        });

        comboBox.setEditable(true);
        comboBox.getEditor().setEditable(false); // disables manual input
        comboBox.getEditor().setCursor(Cursor.DEFAULT);

        TextField editor = comboBox.getEditor();
        editor.setOnMouseClicked(event -> {
            comboBox.show();                          // always show dropdown
        });
    }

    private void initComboBox() {
        initComboBoxCellDesign(cmbIndustry, Industry.class);
        initComboBoxCellDesign(cmbCompany, Company.class);

        cmbIndustry.setOnAction(event -> {
            try {
                Industry selectedIndustry = (Industry) cmbIndustry.getSelectionModel().getSelectedItem();
                if (selectedIndustry != null) {
                    psIndustryID = (selectedIndustry.getIndustryID());
                    System.out.println("test company id: " + selectedIndustry.getIndustryID());
                }
            } catch (Exception e) {
            }
        });
        cmbCompany.setOnAction(event -> {
            try {
                Company selectedCompany = (Company) cmbCompany.getSelectionModel().getSelectedItem();
                if (selectedCompany != null) {
                    psCompanyID = (selectedCompany.getCompanyId());
                    System.out.println("test company id: " + selectedCompany.getCompanyId());
                }
            } catch (Exception e) {
            }
        });

    }

    private void loadComboBoxItems() {

        try {
            industryOptions = FXCollections.observableArrayList(getAllIndustries());
            companyOptions = FXCollections.observableArrayList(getAllCompanies());
            cmbIndustry.setItems(industryOptions);
            cmbCompany.setItems(companyOptions);

        } catch (SQLException ex) {
//            Logger.getLogger(SelectIndustryCompany.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private List<Company> getAllCompanies() throws SQLException {
        List<Company> companies = new ArrayList<>();
        String lsSQL = "SELECT * FROM company";
        lsSQL = MiscUtil.addCondition(lsSQL, "cRecdStat = " + SQLUtil.toSQL(Logical.YES));
        ResultSet rs = oApp.executeQuery(lsSQL);

        while (rs.next()) {
            String id = rs.getString("sCompnyID");
            String name = rs.getString("sCompnyNm");
            companies.add(new Company(id, name));
        }

        MiscUtil.close(rs);
        return companies;
    }

    private List<Industry> getAllIndustries() throws SQLException {
        List<Industry> industries = new ArrayList<>();
        String lsSQL = "SELECT * FROM industry";
        lsSQL = MiscUtil.addCondition(lsSQL, "cRecdStat = " + SQLUtil.toSQL(Logical.YES));
        ResultSet loRS = oApp.executeQuery(lsSQL);

        while (loRS.next()) {
            String id = loRS.getString("sIndstCdx");
            String description = loRS.getString("sDescript");
            industries.add(new Industry(id, description));
        }

        MiscUtil.close(loRS);
        return industries;

    }

    class Industry {

        private String industryID;
        private String industryName;

        public Industry(String industryID, String industryName) {
            this.industryID = industryID;
            this.industryName = industryName;
        }

        public String getIndustryID() {
            return industryID;
        }

        public String getIndustryName() {
            return industryName;
        }

        @Override
        public String toString() {
            return industryName;
        }
    }

    class Company {

        private String companyID;
        private String companyName;

        public Company(String companyID, String companyName) {
            this.companyID = companyID;
            this.companyName = companyName;
        }

        public String getCompanyId() {
            return companyID;
        }

        public String getCompanyName() {
            return companyName;
        }

        @Override
        public String toString() {
            return companyName;
        }
    }

}
