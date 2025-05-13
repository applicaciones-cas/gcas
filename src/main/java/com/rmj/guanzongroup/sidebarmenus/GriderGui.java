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

public class GriderGui extends Application {

    public final static String pxeMainFormTitle = "Computerized Accounting System";
    public final static String pxeMainForm = "/com/rmj/guanzongroup/sidebarmenus/views/Dashboard.fxml";
//    public final static String pxeStageIcon = "images/icon.png";
    public static GRiderCAS oApp;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader view = new FXMLLoader();
        view.setLocation(getClass().getResource(pxeMainForm));

        DashboardController controller = new DashboardController();
        controller.setGRider(oApp);

        view.setController(controller);
        Parent parent = view.load();
        Scene scene = new Scene(parent);
//        Parent root = FXMLLoader.load(getClass().getResource(pxeMainForm));
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();

        //get the screen size
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
//        stage.getIcons().add(new Image(pxeStageIcon));
        stage.setTitle(pxeMainFormTitle);

        // set stage as maximized but not full screen
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.centerOnScreen();
        stage.show();
    }

    /*Parameters*/
//    public final static String pxeEmployeeIncentives = "EmployeeIncentives.fxml";
//    public final static String pxeAddIncentives = "AddIncentives.fxml";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    public void setGRider(GRiderCAS foValue) {
        oApp = foValue;
    }
}
