package com.rmj.guanzongroup.sidebarmenus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javafx.application.Application;
import javafx.stage.Stage;
import org.guanzon.appdriver.base.GRider;

public class LetMeIn extends Application {
    public static void main(String[] args) {
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
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

        GRider instance = new GRider("gRider");

        if (!instance.logUser("gRider", "M001000001")){ 
            System.err.println(instance.getErrMsg());
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
    
}
