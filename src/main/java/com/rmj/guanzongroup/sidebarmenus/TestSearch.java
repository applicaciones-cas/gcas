package com.rmj.guanzongroup.sidebarmenus;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;

public class TestSearch extends Application {

    private static boolean loadProperties() {
        try {
            Properties po_props = new Properties();
            po_props.load(new FileInputStream(System.getProperty("sys.default.path.config") + "/config/cas.properties"));

            System.setProperty("store.branch.code", po_props.getProperty("store.branch.code"));
            System.setProperty("store.inventory.industry", po_props.getProperty("store.inventory.category"));

            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void start(Stage primaryStage) {

    }

// Entry point
    public static void main(String[] args) {
        String path;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "D:/GGC_Maven_Systems";
        } else {
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        System.setProperty("sys.default.path.metadata", System.getProperty("sys.default.path.config") + "/config/metadata/new/");

        if (!loadProperties()) {
            System.err.println("Unable to load config.");
            System.exit(1);
        } else {
            System.out.println("Config file loaded successfully.");
        }

        GRiderCAS instance;
        try {
            instance = new GRiderCAS("gRider");

            if (!instance.logUser("gRider", "M001000001")) {
                System.err.println(instance.getMessage());
                System.exit(1);
            }

            System.out.println("Connected");

            GriderGui2 instance_ui = new GriderGui2();
            instance_ui.setGRider(instance);

            launch(instance_ui.getClass());
        } catch (SQLException ex) {
            Logger.getLogger(TestSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GuanzonException ex) {
            Logger.getLogger(TestSearch.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
