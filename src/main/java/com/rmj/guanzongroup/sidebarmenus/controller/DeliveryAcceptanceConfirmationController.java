/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import com.rmj.guanzongroup.sidebarmenus.table.model.ModelAttachment;
import com.rmj.guanzongroup.sidebarmenus.table.model.ModelPurchaseOrder;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.guanzon.appdriver.agent.ShowMessageFX;
import org.guanzon.appdriver.base.GRider;
import org.json.simple.JSONObject;

/**
 * FXML Controller class
 *
 * @author User
 */
public class DeliveryAcceptanceConfirmationController implements Initializable, ScreenInterface {

    private final String pxeModuleName = "Delivery Acceptance Confirmation";
    private GRider oApp;
    private JSONObject poJSON;

    private ObservableList<ModelPurchaseOrder> data = FXCollections.observableArrayList();

    private double mouseAnchorX;
    private double mouseAnchorY;
    private double scaleFactor = 1.0;
    private FileChooser fileChooser;
    private int pnAttachmentRow;

    private int currentIndex = 0;
    double ldstackPaneWidth = 0;
    double ldstackPaneHeight = 0;
    private final ObservableList<ModelAttachment> img_data = FXCollections.observableArrayList();

    @FXML
    private AnchorPane apBrowse;
    @FXML
    private AnchorPane apButton;
    @FXML
    private HBox hbButtons;
    @FXML
    private Button btnBrowse;
    @FXML
    private Button btnNew;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnClose;
    @FXML
    private Label lblStatus;
    @FXML
    private TextField txtField01;
    @FXML
    private TextField txtField02;
    @FXML
    private TextField txtField04;
    @FXML
    private TextField txtField05;
    @FXML
    private TextField txtField08;
    @FXML
    private CheckBox cbAdv;
    @FXML
    private TextField txtField11;
    @FXML
    private TextField txtField12;
    @FXML
    private TextField txtField06;
    @FXML
    private TextField txtField081;
    @FXML
    private TextField txtField18;
    @FXML
    private TextField txtField19;
    @FXML
    private TextField txtField25;
    @FXML
    private TextField txtField26;
    @FXML
    private TextField txtField27;
    @FXML
    private TextField txtField17;
    @FXML
    private TextField txtField16;
    @FXML
    private TextField txtField15;
    @FXML
    private TextField txtField20;
    @FXML
    private TextField txtField21;
    @FXML
    private TextField txtField22;
    @FXML
    private TextField txtField23;
    @FXML
    private TextField txtField24;
    @FXML
    private TableView tblViewStock_Request;
    @FXML
    private TableColumn tblindex01;
    @FXML
    private TableColumn tblindex02;
    @FXML
    private TableColumn tblindex03;
    @FXML
    private TableColumn tblindex04;
    @FXML
    private TableView tblViewOrderDetails;
    @FXML
    private TableColumn tblindex01_order_details;
    @FXML
    private TableColumn tblindex02_order_details;
    @FXML
    private TableColumn tblindex04_order_details;
    @FXML
    private TableColumn tblindex05_order_details;
    @FXML
    private TableColumn tblindex06_order_details;
    @FXML
    private TableColumn tblindex07_order_details;
    @FXML
    private TableColumn tblindex08_order_details;
    @FXML
    private TableColumn tblindex12_order_details;
    @FXML
    private TableColumn tblindex09_order_details;
    @FXML
    private TableColumn tblindex09_order_details1;
    @FXML
    private TableColumn tblindex10_order_details;
    @FXML
    private TableColumn tblindex11_order_details;
    @FXML
    private TableColumn tblindex13_order_details;
    @FXML
    private TableView tblAttachments;
    @FXML
    private TableColumn index12;
    @FXML
    private TableColumn index13;
    @FXML
    private Button btnRemoveItem;
    @FXML
    private Button btnAddAttachment;
    @FXML
    private Button btnArrowLeft;
    @FXML
    private StackPane stackPane1;
    @FXML
    private Button btnArrowRight;
    @FXML
    private TextField txtField181;
    @FXML
    private TextField txtField191;
    @FXML
    private TextField txtField171;
    @FXML
    private TextField txtField161;
    @FXML
    private TextField txtField151;
    @FXML
    private TableView tblViewOrderDetails1;
    @FXML
    private TableColumn tblindex02_order_details1;
    @FXML
    private TableColumn tblindex04_order_details1;
    @FXML
    private TableColumn tblindex09_order_details11;
    @FXML
    private TableColumn tblindex09_order_details12;

    @FXML
    private ImageView imageView;

    /**
     * Initializes the controller class.
     */
    private void initAttachmentTableData() {

        stackPane1.widthProperty().addListener((observable, oldValue, newWidth) -> {
            double computedWidth = newWidth.doubleValue();
            ldstackPaneWidth = computedWidth;

        });
        stackPane1.heightProperty().addListener((observable, oldValue, newHeight) -> {
            double computedHeight = newHeight.doubleValue();
            ldstackPaneHeight = computedHeight;

            img_data.add(new ModelAttachment("0", "C:/Users/User/Downloads/a4-blank-template_page-0001.jpg"));
            setSelectedAttachment();
        });
    }

    private void initAttachmentPreviewPane() {
        stackPane1.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
            stackPane1.setClip(new javafx.scene.shape.Rectangle(
                    newBounds.getMinX(),
                    newBounds.getMinY(),
                    newBounds.getWidth(),
                    newBounds.getHeight()
            ));
        });
        imageView.setOnScroll((ScrollEvent event) -> {
            double delta = event.getDeltaY();
            scaleFactor = Math.max(0.5, Math.min(scaleFactor * (delta > 0 ? 1.1 : 0.9), 5.0));
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
        });

        imageView.setOnMousePressed((MouseEvent event) -> {
            mouseAnchorX = event.getSceneX() - imageView.getTranslateX();
            mouseAnchorY = event.getSceneY() - imageView.getTranslateY();
        });

        imageView.setOnMouseDragged((MouseEvent event) -> {
            double translateX = event.getSceneX() - mouseAnchorX;
            double translateY = event.getSceneY() - mouseAnchorY;
            imageView.setTranslateX(translateX);
            imageView.setTranslateY(translateY);
        });

    }

    private void adjustImageSize(Image image) {

        double imageRatio = image.getWidth() / image.getHeight();
        double containerRatio = ldstackPaneWidth / ldstackPaneHeight;

        // Unbind before setting new values
        imageView.fitWidthProperty().unbind();
        imageView.fitHeightProperty().unbind();

        if (imageRatio > containerRatio) {
            // Image is wider than container → fit width
            imageView.setFitWidth(ldstackPaneWidth);
            imageView.setFitHeight(ldstackPaneWidth / imageRatio);
        } else {
            // Image is taller than container → fit height
            imageView.setFitHeight(ldstackPaneHeight);
            imageView.setFitWidth(ldstackPaneHeight * imageRatio);
        }

        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
    }

    private void loadTableAttachment() {
        List<ModelAttachment> tempData = new ArrayList<>();
        for (int i = 0; i < img_data.size(); i++) {
            tempData.add(new ModelAttachment(String.valueOf(i), img_data.get(i).getIndex13()));
        }
        img_data.clear();
        img_data.addAll(tempData);
    }

    private void stackPaneClip() {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(
                stackPane1.getWidth() - 8, // Subtract 10 for padding (5 on each side)
                stackPane1.getHeight() - 8 // Subtract 10 for padding (5 on each side)
        );
        clip.setArcWidth(8); // Optional: Rounded corners for aesthetics
        clip.setArcHeight(8);
        clip.setLayoutX(4); // Set padding offset for X
        clip.setLayoutY(4); // Set padding offset for Y
        stackPane1.setClip(clip);
    }

    private void setSelectedAttachment() {
        if (pnAttachmentRow >= 0) {
            String filePath = (String) img_data.get(pnAttachmentRow).getIndex13();

            if (filePath.length() != 0) {
                Path imgPath = Paths.get(filePath);
                String convertedPath = imgPath.toUri().toString();
                Image loimage = new Image(convertedPath);
                imageView.setImage(loimage);
                adjustImageSize(loimage);

                stackPaneClip();
            } else {
                imageView.setImage(null);
                stackPaneClip();
            }
        } else {
            imageView.setImage(null);
            stackPaneClip();
            pnAttachmentRow = 0;
        }

    }

    public void initAttachmentDetailsGrid2() {
        /*FOCUS ON FIRST ROW*/

        index12.setStyle("-fx-alignment: CENTER;-fx-padding: 0 0 0 5;");
        index13.setStyle("-fx-alignment: CENTER;-fx-padding: 0 0 0 5;");

        index12.setCellValueFactory(new PropertyValueFactory<ModelAttachment, String>("index12"));
        index13.setCellValueFactory(new PropertyValueFactory<ModelAttachment, String>("index13"));

        tblAttachments.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblAttachments.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        tblAttachments.setItems(img_data);

        if (pnAttachmentRow < 0 || pnAttachmentRow >= img_data.size()) {
            if (!img_data.isEmpty()) {
                /* FOCUS ON FIRST ROW */
                tblAttachments.getSelectionModel().select(0);
                tblAttachments.getFocusModel().focus(0);
                pnAttachmentRow = tblAttachments.getSelectionModel().getSelectedIndex();
            }
        } else {
            /* FOCUS ON THE ROW THAT pnRowDetail POINTS TO */
            tblAttachments.getSelectionModel().select(pnAttachmentRow);
            tblAttachments.getFocusModel().focus(pnAttachmentRow);
        }

    }

    public void slideImage(int direction) {
        currentIndex = pnAttachmentRow; //1

        if ((currentIndex != -1 || img_data.size() - 1 > currentIndex)) { //FIX problem here
            int newIndex = currentIndex + direction;
            ModelAttachment image = img_data.get(newIndex);
            Path filePath = Paths.get(image.getIndex13());
            String convertedPath = filePath.toUri().toString();

            Image newImage = new Image(convertedPath);
            // Create a transition animation
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), imageView);
            slideOut.setByX(direction * -400); // Move left or right

            slideOut.setOnFinished(event -> {
                imageView.setImage(newImage);
                imageView.setTranslateX(direction * 400);
                adjustImageSize(newImage);

                TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), imageView);
                slideIn.setToX(0);
                slideIn.play();
            });

            slideOut.play();

            tblAttachments.getFocusModel().focus(newIndex);
            tblAttachments.getSelectionModel().select(newIndex);
            pnAttachmentRow = newIndex;

        }
    }

    public void initDetailsTable() {
        tblindex01.setStyle("-fx-alignment: CENTER;");
        tblindex02.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblindex03.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");
        tblindex04.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 0 0 5;");

        tblindex01.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index01"));
        tblindex02.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index02"));
        tblindex03.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index03"));
        tblindex04.setCellValueFactory(new PropertyValueFactory<ModelPurchaseOrder, String>("index04"));

        tblViewStock_Request.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tblViewStock_Request.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                header.setReordering(false);
            });
        });

        tblViewStock_Request.setItems(data);
    }

    private void initDetailsTableData() {
        data.add(new ModelPurchaseOrder("1", "LP - General Warehouse", "2025-02-11", "M00125000000", "10"
        ));

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initAttachmentPreviewPane();
        initAttachmentDetailsGrid2();
        initDetailsTable();

        initAttachmentTableData();
        initDetailsTableData();

    }

    @FXML
    private void cmdButton_Click(ActionEvent event) {
        String lsButton = ((Button) event.getSource()).getId();
        switch (lsButton) {
            case "btnAddAttachment":
                try {
                fileChooser = new FileChooser();
                fileChooser.setTitle("Choose Image");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif")
                );
                java.io.File selectedFile = fileChooser.showOpenDialog((Stage) btnAddAttachment.getScene().getWindow());

                if (selectedFile != null) {
                    // Read image from the selected file
                    Path imgPath = selectedFile.toPath();
                    Image loimage = new Image(Files.newInputStream(imgPath));
                    imageView.setImage(loimage);

                    String imgPath2 = selectedFile.toString();
                    img_data.add(new ModelAttachment(String.valueOf(img_data.size()), imgPath2));

                    if (img_data.size() > 1) {
                        pnAttachmentRow = img_data.size() - 1;
                    }
                    loadTableAttachment();
                    setSelectedAttachment();

                    tblAttachments.getFocusModel().focus(pnAttachmentRow);
                    tblAttachments.getSelectionModel().select(pnAttachmentRow);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            break;
            case "btnRemoveItem":
                img_data.remove(pnAttachmentRow);
                if (pnAttachmentRow != 0) {
                    pnAttachmentRow -= 1;
                }
                setSelectedAttachment();
                loadTableAttachment();
                initAttachmentDetailsGrid2();
                break;
            case "btnArrowRight":
                slideImage(1);
                break;
            case "btnArrowLeft":
                slideImage(-1);
                break;
            case "btnClose":
                Stage stage = (Stage) btnClose.getScene().getWindow();
                stage.close();
                break;
            default:
                ShowMessageFX.Warning(null, "File Attachment", "Button with name " + lsButton + " not registered.");
                return;
        }
    }

    @FXML
    private void tblAttachments_Clicked(MouseEvent event) {
        pnAttachmentRow = tblAttachments.getSelectionModel().getSelectedIndex();
        if (pnAttachmentRow >= 0) {
            setSelectedAttachment();
        }
    }

    @Override
    public void setGRider(GRider foValue) {
        oApp = foValue;
    }

}
