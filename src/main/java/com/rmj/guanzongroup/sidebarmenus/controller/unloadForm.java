/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rmj.guanzongroup.sidebarmenus.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.guanzon.appdriver.base.GRider;
//import org.rmj.appdriver.GRider;
//import org.rmj.auto.json.TabsStateManager;

/**
 *
 * @author Arsiela UnloadForm: To be called on close button on every opened tab.
 *
 */
public class unloadForm {

    public void unloadForm(AnchorPane AnchorMain, GRider oApp, String sTabTitle) {
        // Get the parent of the TabContent node
        Node tabContent = AnchorMain.getParent();
        Parent tabContentParent = tabContent.getParent();

        // If the parent is a TabPane, you can work with it directly
        if (tabContentParent instanceof TabPane) {
            TabPane tabpane = (TabPane) tabContentParent;
            // Get the list of tabs in the TabPane
            ObservableList<Tab> tabs = tabpane.getTabs();
            int tabsize = tabpane.getTabs().size();
            List<String> tabName = new ArrayList<>();
            // tabName = TabsStateManager.loadCurrentTab();

            // Use an iterator to loop through the tabs and find the one you want to remove
            Iterator<Tab> iterator = tabs.iterator();
            while (iterator.hasNext()) {
                Tab tab = iterator.next();
                if (tab.getText().equals(sTabTitle)) {
                    // Remove the tab using the iterator
                    iterator.remove();

                    if (tabsize == 1) {
                        StackPane myBox = (StackPane) tabpane.getParent();
                        myBox.getChildren().clear();
                        myBox.getChildren().add(getScene("/com/rmj/guanzongroup/sidebarmenus/views/DefaultScreen.fxml", oApp));
                    }

                    if (tabName.size() > 0) {
                        tabName.remove(sTabTitle);
                        // TabsStateManager.saveCurrentTab(tabName);
                    }
                    // TabsStateManager.closeTab(sTabTitle);
                    break;
                }
            }
        }
    }

    public AnchorPane getScene(String fsFormName, GRider oApp) {
        ScreenInterface fxObj = new DefaultScreenController();
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

}
