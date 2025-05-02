/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.utility;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Pagination;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.apache.poi.ss.formula.functions.T;
import org.json.simple.JSONObject;

/**
 * Date : 4/28/2025
 *
 * @author Aldrich
 */
public class JFXUtil {

    public static <T> void adjustColumnForScrollbar(TableView<?> tableView, int columnIndex) {
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (!(newSkin instanceof TableViewSkin<?>)) {
                return;
            }

            TableViewSkin<?> skin = (TableViewSkin<?>) newSkin;
            VirtualFlow<?> flow = skin.getChildren().stream()
                    .filter(node -> node instanceof VirtualFlow<?>)
                    .map(node -> (VirtualFlow<?>) node)
                    .findFirst().orElse(null);

            if (flow == null) {
                return;
            }

            ScrollBar vScrollBar = flow.getChildrenUnmodifiable().stream()
                    .filter(node -> node instanceof ScrollBar && ((ScrollBar) node).getOrientation() == Orientation.VERTICAL)
                    .map(node -> (ScrollBar) node)
                    .findFirst().orElse(null);

            if (vScrollBar == null || tableView.getColumns().isEmpty() || columnIndex < 0 || columnIndex >= tableView.getColumns().size()) {
                return;
            }

            TableColumn<?, ?> targetColumn = tableView.getColumns().get(columnIndex);

            vScrollBar.visibleProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    double scrollBarWidth = newValue ? vScrollBar.getWidth() : 0;
                    double remainingWidth = tableView.getWidth() - scrollBarWidth;

                    double totalFixedWidth = tableView.getColumns().stream()
                            .filter(col -> col != targetColumn)
                            .mapToDouble(col -> ((TableColumn<?, ?>) col).getWidth())
                            .sum();

                    double newWidth = Math.max(0, remainingWidth - totalFixedWidth);
                    targetColumn.setPrefWidth(newWidth - 5);
                });
            });
        });
    }

    public static <T> void highlightByKey(TableView<T> table, String key, String color, Map<String, List<String>> highlightMap) {
        List<String> colors = highlightMap.computeIfAbsent(key, k -> new ArrayList<>());
        if (!colors.contains(color)) {
            colors.add(color);
            table.refresh();
        }
    }

    public static <T> void disableHighlightByKey(TableView<T> table, String key, Map<String, List<String>> highlightMap) {
        highlightMap.remove(key);
        table.refresh();

    }

    public static <T> void disableAllHighlight(TableView<T> table, Map<String, List<String>> highlightMap) {
        highlightMap.clear();
        table.refresh();
    }

    public static <T> void disableAllHighlightByColor(TableView<T> table, String color, Map<String, List<String>> highlightMap) {
        highlightMap.forEach((key, colors) -> colors.removeIf(c -> c.equals(color)));
        highlightMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        table.refresh();
    }

    public static <T> void setDatePickerNextFocusByEnter(DatePicker datePicker) {
        datePicker.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                Node source = (Node) event.getSource();
                source.fireEvent(new KeyEvent(
                        KeyEvent.KEY_PRESSED,
                        "",
                        "",
                        KeyCode.TAB,
                        false,
                        false,
                        false,
                        false
                ));
                event.consume();
            }
        });
    }

    public static <T> void initComboBoxCellDesignColor(ComboBox<T> comboBox, String hexcolor) {
//      #FF8201
        comboBox.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());

                    boolean isSelected = item.equals(comboBox.getValue());

                    // Apply initial style
                    if (isSelected) {
                        setStyle("-fx-background-color: " + hexcolor + "; -fx-text-fill: white;");
                    } else {
                        setStyle("");
                    }

                    hoverProperty().addListener((obs, wasHovered, isNowHovered) -> {
                        if (isNowHovered && !isEmpty() && getItem() != null) {
                            if (getItem().toString().equals(comboBox.getValue() != null ? comboBox.getValue().toString() : "")) {
                                setStyle("-fx-background-color: " + hexcolor + "; -fx-text-fill: white;");

                            } else {
                                setStyle("-fx-background-color: " + hexcolor + "; -fx-text-fill: black;");

                            }
                        } else if (!isEmpty() && getItem() != null) {
                            // If not hovered, reset style based on selection
                            if (getItem().toString().equals(comboBox.getValue() != null ? comboBox.getValue().toString() : "")) {
                                setStyle("-fx-background-color: " + hexcolor + "; -fx-text-fill: white;");
                            } else {
                                setStyle("");
                            }
                        }
                    });

                    setOnMouseExited(e -> {
                        if (item.equals(comboBox.getValue())) {
                            setStyle("-fx-background-color: " + hexcolor + "; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    });
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
    }

    public static <T> void setDatePickerFormat(DatePicker datePicker) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        datePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? date.format(formatter) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? LocalDate.parse(string, formatter) : null;
            }
        });
    }

    public static void updateCaretPositions(AnchorPane anchorPane) {
        List<TextField> textFields = getAllTextFields(anchorPane);
        for (TextField textField : textFields) {
            String text = textField.getText();
            if (text != null && !"".equals(text)) {
                Pos alignment = textField.getAlignment();
                if (alignment == Pos.CENTER_RIGHT || alignment == Pos.BASELINE_RIGHT
                        || alignment == Pos.TOP_RIGHT || alignment == Pos.BOTTOM_RIGHT) {
                    textField.positionCaret(0); // Caret at start
                } else {
                    if (textField.isFocused()) {
                        textField.positionCaret(text.length()); // Caret at end if focused
                    } else {
                        textField.positionCaret(0); // Caret at start if not focused
                    }
                }
            }
        }
    }

    private static List<TextField> getAllTextFields(Parent parent) {
        List<TextField> textFields = new ArrayList<>();

        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                textFields.add((TextField) node);
            } else if (node instanceof DatePicker) {
                // Try to find the internal TextField of DatePicker
                Node datePickerEditor = ((DatePicker) node).lookup(".text-field");
                if (datePickerEditor instanceof TextField) {
                    textFields.add((TextField) datePickerEditor);
                }
            } else if (node instanceof Parent) {
                textFields.addAll(getAllTextFields((Parent) node));
            }
        }
        return textFields;
    }

    public static int moveToNextRow(TableView table) {
        TableView<?> currentTable = table;
        TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
        if (focusedCell != null) {
            int nextRow = (focusedCell.getRow() + 1) % table.getItems().size();
            table.getSelectionModel().select(nextRow);
            return nextRow;
        } else {
            return 0;
        }
    }

    public static int moveToPreviousRow(TableView table) {
        TableView<?> currentTable = table;
        TablePosition<?, ?> focusedCell = currentTable.getFocusModel().getFocusedCell();
        if (focusedCell != null) {
            int previousRow = (focusedCell.getRow() - 1 + table.getItems().size()) % table.getItems().size();
            table.getSelectionModel().select(previousRow);
            return previousRow;
        } else {
            return 0;
        }

    }

    public static void loadTab(Pagination pgPagination, int tbldata_list_size, int ROWS_PER_PAGE, TableView tbl, FilteredList filteredData) {
        int totalPage = (int) (Math.ceil(tbldata_list_size * 1.0 / ROWS_PER_PAGE));
        pgPagination.setPageCount(totalPage);
        pgPagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE, tbl, tbldata_list_size, filteredData);
        pgPagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE, tbl, tbldata_list_size, filteredData));
    }

    private static void changeTableView(int index, int limit, TableView tbl, int tbldata_list_size, FilteredList filteredData) {
        tbl.getSelectionModel().clearSelection();
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, tbldata_list_size);
        int minIndex = Math.min(toIndex, tbldata_list_size);
        SortedList<T> sortedData = new SortedList<>(
                FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tbl.comparatorProperty());
        try {
            tbl.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
        } catch (Exception e) {

        }

        tbl.scrollTo(0);
    }

    public void showDialog(String lsFxml,
            Object controller,
            String lsDialogTitle
    ) throws IOException {

        // no need to set dialogstage null or not as background is locked upon opening
        FXMLLoader loader = new FXMLLoader(getClass().getResource(lsFxml));
        loader.setController(controller);

        Parent root = loader.load();

        final xyOffset xyOffset = new xyOffset();
        root.setOnMousePressed(event -> {
            xyOffset.x = event.getSceneX();
            xyOffset.y = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setX(event.getScreenX() - xyOffset.x);
            stage.setY(event.getScreenY() - xyOffset.y);
        });

        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(lsDialogTitle);
        dialog.setScene(new Scene(root));
        dialog.show();

    }

    private static class xyOffset {

        double x, y;
    }

    public void stackPaneClip(StackPane stackPane1) {
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

    public boolean isImageViewOutOfBounds(ImageView imageView, StackPane stackPane) {
        Bounds clipBounds = stackPane.getClip().getBoundsInParent();
        Bounds imageBounds = imageView.getBoundsInParent();

        return imageBounds.getMaxX() < clipBounds.getMinX()
                || imageBounds.getMinX() > clipBounds.getMaxX()
                || imageBounds.getMaxY() < clipBounds.getMinY()
                || imageBounds.getMinY() > clipBounds.getMaxY();
    }

    public void resetImageBounds(ImageView imageView, StackPane stackPane1) {
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        stackPane1.setAlignment(imageView, javafx.geometry.Pos.CENTER);
    }

    public void adjustImageSize(Image image, ImageView imageView, int ldstackPaneWidth, int ldstackPaneHeight) {
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

    public static void setColumnCenter(TableColumn... columns) {
        for (TableColumn column : columns) {
            column.setStyle("-fx-alignment: CENTER;");
        }
    }

    public static void setColumnLeft(TableColumn... columns) {
        for (TableColumn column : columns) {
            column.setStyle("-fx-alignment: CENTER-LEFT;-fx-padding: 0 5 0 5;");
        }
    }

    public static void setColumnRight(TableColumn... columns) {
        for (TableColumn column : columns) {
            column.setStyle("-fx-alignment: CENTER-RIGHT;-fx-padding: 0 5 0 5;");
        }
    }

    public static void setColumnsIndexAndDisableReordering(TableView<?> tableView) {
        int counter = 1;
        for (Object obj : tableView.getColumns()) {
            if (obj instanceof TableColumn) {
                @SuppressWarnings("unchecked")
                TableColumn<?, ?> column = (TableColumn<?, ?>) obj;
                String indexName = String.format("index%02d", counter);
                column.setCellValueFactory(new PropertyValueFactory(indexName));
                counter++;
            }
        }

        tableView.widthProperty().addListener((ObservableValue<? extends Number> source, Number oldWidth, Number newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            if (header != null) {
                header.reorderingProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
                    header.setReordering(false);
                });
            }
        });

    }

    public static void clearTextFields(AnchorPane... anchorPanes) {
        for (AnchorPane pane : anchorPanes) {
            clearTextInputsRecursive(pane);
        }
    }

// Helper method to recursively find and clear TextFields/TextAreas
    private static void clearTextInputsRecursive(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof TextInputControl) {
                ((TextInputControl) node).clear();
            } else if (node instanceof Parent) {
                clearTextInputsRecursive((Parent) node); // Recursively check child nodes
            }
        }
    }

    public static void setButtonsVisibility(boolean visible, Button... buttons) {
        for (Button btn : buttons) {
            btn.setVisible(visible);
            btn.setManaged(visible);
        }
    }

    public static void AddStyleClass(String lsCssClassName, TextField... textFields) {
        for (TextField tf : textFields) {
            tf.getStyleClass().add(lsCssClassName);
        }
    }

    public static void RemoveStyleClass(String lsCssClassName, TextField... textFields) {
        for (TextField tf : textFields) {
            tf.getStyleClass().remove(lsCssClassName);
        }
    }

    public static boolean isTextFieldContainsStyleClass(String lsCssClassName, TextField... textFields) {
        for (TextField tf : textFields) {
            if (tf.getStyleClass().contains("DisabledTextField")) {
                return true;
            }
        }
        return false;
    }

    public static void setDisabled(boolean disable, Node... nodes) {
        for (Node node : nodes) {
            node.setDisable(disable);
        }
    }

    public static void setFocusListener(ChangeListener<? super Boolean> listener, Node... nodes) {
        for (Node node : nodes) {
            if (node instanceof Control) {
                ((Control) node).focusedProperty().addListener(listener);
            }
        }
    }

    public static JFXUtilDateResult processDate(String inputText, DatePicker datePicker) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JSONObject poJSON = new JSONObject();
        LocalDate selectedDate = null;

        if (inputText != null && !inputText.trim().isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(inputText, DateTimeFormatter.ofPattern("yyyy-M-d"));
                datePicker.setValue(parsedDate);
                datePicker.getEditor().setText(formatter.format(parsedDate));
                inputText = datePicker.getEditor().getText();
            } catch (DateTimeParseException ignored) {
            }
        }

        if (inputText != null && !inputText.trim().isEmpty()) {
            try {
                selectedDate = LocalDate.parse(inputText, formatter);
                datePicker.setValue(selectedDate);
            } catch (Exception ex) {
                poJSON.put("result", "error");
                poJSON.put("message", "Invalid date format. Please use yyyy-mm-dd format.");
                return new JFXUtilDateResult("", selectedDate, poJSON);
            }
        } else {
            selectedDate = datePicker.getValue();
        }
        poJSON.put("result", "success");
        poJSON.put("message", "success");
        return new JFXUtilDateResult(inputText, selectedDate, poJSON);
    }

    public static class JFXUtilDateResult {

        public String inputText;
        public LocalDate selectedDate;
        public JSONObject poJSON;

        public JFXUtilDateResult(String inputText, LocalDate selectedDate, JSONObject poJSON) {
            this.inputText = inputText;
            this.selectedDate = selectedDate;
            this.poJSON = poJSON;
        }
    }

    public static boolean isObjectEqualTo(Object source, Object... others) {
        if (source == null && others != null) {
            for (Object other : others) {
                if (other == null) {
                    return true;
                }
            }
            return false;
        }

        for (Object other : others) {
            if (source != null && source.equals(other)) {
                return true;
            }
        }
        return false;
    }

    public static void setKeyPressedListener(EventHandler<KeyEvent> listener, AnchorPane... anchorPanes) {
        for (AnchorPane pane : anchorPanes) {
            for (Node node : pane.getChildrenUnmodifiable()) {
                if (node instanceof TextField) {
                    ((TextField) node).setOnKeyPressed(listener);
                } else if (node instanceof Parent) {
                    // Recursively check for nested TextFields
                    applyListenerToNestedTextFields((Parent) node, listener);
                }
            }
        }
    }

    private static void applyListenerToNestedTextFields(Parent parent, EventHandler<KeyEvent> listener) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof TextField) {
                ((TextField) child).setOnKeyPressed(listener);
            } else if (child instanceof Parent) {
                applyListenerToNestedTextFields((Parent) child, listener);
            }
        }
    }

    public static List<String> getTextFieldsIDWithPrompt(String lsprompt, AnchorPane... panes) {
        List<String> results = new ArrayList<>();
        for (AnchorPane pane : panes) {
            for (Node node : pane.getChildren()) {
                collectTextFieldIDs(node, lsprompt, results);
            }
        }
        return results;
    }

    private static void collectTextFieldIDs(Node node, String lsprompt, List<String> results) {
        if (node instanceof TextField) {
            TextField tf = (TextField) node;
            String prompt = tf.getPromptText();
            if (prompt != null && prompt.contains(lsprompt) && tf.getId() != null) {
                results.add(tf.getId());
            }
        } else if (node instanceof Pane) {
            Pane pane = (Pane) node;
            for (Node child : pane.getChildren()) {
                collectTextFieldIDs(child, lsprompt, results);
            }
        } else if (node instanceof Group) {
            Group group = (Group) node;
            for (Node child : group.getChildren()) {
                collectTextFieldIDs(child, lsprompt, results);
            }
        }
    }

}
