/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.table.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Map;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.swing.JRViewer;
import org.guanzon.appdriver.agent.ShowMessageFX;

/**
 *
 * @author User
 */
public class ReportPrinter {
    private boolean isReportRunning = false;
    private String reportTitle = "";
    public boolean isReportRunning(){
        return isReportRunning;
    }

    public boolean loadAndShowReport(String reportFilePath, Map<String, Object> params, List<?> dataSourceList, String windowTitle) {
        // Check if the report is already running
        reportTitle = windowTitle;
        if (isReportRunning) {
            showWarning("Report already running. Please close the existing report before opening a new one.");
            return false;
        }

        try {
            // Create a data source from the provided data
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(dataSourceList);

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportFilePath, params, dataSource);

            // Show the report
            if (jasperPrint != null) {                
                isReportRunning = true;
                JRViewer jrViewer = new JRViewer(jasperPrint);
                jrViewer.setFitPageZoomRatio();

// Create a new Stage for the JasperViewer
                Stage stage = new Stage();
                stage.setTitle(windowTitle);
                stage.initModality(Modality.APPLICATION_MODAL); // Optional: Block input to other windows
                stage.initStyle(StageStyle.UTILITY); // Use UTILITY style for close button

                BorderPane root = new BorderPane();
                SwingNode swingNode = new SwingNode();
                createAndSetSwingContent(swingNode, jrViewer);
                root.setCenter(swingNode);

// Get screen bounds
                Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                int width = (int) (screenBounds.getWidth() * 0.6); // 60% of screen width
                int height = (int) (screenBounds.getHeight() * 0.82); // 82% of screen height

                Scene scene = new Scene(root, width, height); // Set preferred size
                stage.setScene(scene);

// Center the stage on the screen
                stage.setX((screenBounds.getWidth() - width) / 2);
                stage.setY((screenBounds.getHeight() - height) / 2);
//                stage.setAlwaysOnTop(true);

                stage.setOnCloseRequest(event -> isReportRunning = false); // Reset the flag when the window is closed

                stage.show();
            }
        } catch (JRException ex) {
            ex.printStackTrace();
            showWarning("Error filling report: " + ex.getMessage());
            isReportRunning = false; // Reset the flag in case of error
            return false;
        }

        return true;
    }

    private void createAndSetSwingContent(SwingNode swingNode, JRViewer jrViewer) {
        swingNode.setContent(jrViewer);
    }
    public void showWarning(String message) {
        ShowMessageFX.Information(null, message, "Computerized Acounting System", reportTitle);
    }
}
