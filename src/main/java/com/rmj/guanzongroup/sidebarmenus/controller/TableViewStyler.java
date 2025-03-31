package com.rmj.guanzongroup.sidebarmenus.controller;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class TableViewStyler {
    private static boolean highlightEnabled = true;

    public static <T> void highlightRow(TableView<T> tableView, int rowIndex) {
        tableView.setRowFactory(tv -> new TableRow<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if ( getIndex() == rowIndex && highlightEnabled) {
                    setStyle("-fx-background-color: lightcoral; -fx-text-fill: white;");
                } else {
                    setStyle(""); // Reset to default
                }
            }
        });
    }

    public static void disableHighlight() {
        highlightEnabled = false;
    }

    public static void enableHighlight() {
        highlightEnabled = true;
    }
}
