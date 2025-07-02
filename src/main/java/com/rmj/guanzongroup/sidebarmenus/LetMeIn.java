package com.rmj.guanzongroup.sidebarmenus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.GRiderCAS;
import org.guanzon.appdriver.base.GuanzonException;

public class LetMeIn extends Application {

    public static void main(String[] args) throws GuanzonException, SQLException {
        String path;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            path = "C:/GGC_Maven_Systems";
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

        GRiderCAS instance = new GRiderCAS("gRider");

        if (!instance.logUser("gRider", "M001000001")) {
            System.err.println(instance.getMessage());
            System.exit(1);
        }

        System.out.println("Connected");

        GriderGui instance_ui = new GriderGui();
        instance_ui.setGRider(instance);

        Application.launch(instance_ui.getClass());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

    private static boolean loadProperties() {
        try {
            Properties po_props = new Properties();
            po_props.load(new FileInputStream(System.getProperty("sys.default.path.config") + "/config/cas.properties"));

            return true;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return false;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
