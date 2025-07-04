/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.utility;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableColumn;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Pagination;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import javafx.util.StringConverter;
import org.apache.poi.ss.formula.functions.T;
import org.json.simple.JSONObject;

/**
 * Date : 4/28/2025
 *
 * @author Aldrich
 */
public class JFXUtil {

    public static void adjustColumnForScrollbar(TableView<?>... tableViews) {
        for (TableView<?> tableView : tableViews) {
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

                if (vScrollBar == null || tableView.getColumns().isEmpty()) {
                    return;
                }

                TableColumn<?, ?> foundColumn = null;
                for (TableColumn<?, ?> column : tableView.getColumns()) {
                    double minWidth = column.getMinWidth();

                    // Safely compare with USE_COMPUTED_SIZE
                    if ((minWidth) == 0) {
                        foundColumn = column;
                        break;
                    }
                }

                if (foundColumn == null) {
                    System.err.println("NO COLUMN WITH minWidth == 0 (USE_COMPUTED_SIZE) found in table: " + tableView.getId());
                    return;
                }

                final TableColumn<?, ?> targetColumn = foundColumn;
                // Optional debug log
                vScrollBar.visibleProperty().addListener((observable, oldValue, newValue) -> {
                    Platform.runLater(() -> {
                        double scrollBarWidth = newValue ? vScrollBar.getWidth() : 0;
                        double remainingWidth = tableView.getWidth() - scrollBarWidth;

                        double totalFixedWidth = tableView.getColumns().stream()
                                .filter(col -> col != targetColumn)
                                .mapToDouble(TableColumn::getWidth)
                                .sum();

                        double newWidth = Math.max(0, remainingWidth - totalFixedWidth);
                        targetColumn.setPrefWidth(newWidth - 5);
                    });
                });
            });
        }
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

    public static void setDatePickerNextFocusByEnter(DatePicker... datePickers) {
        for (DatePicker datePicker : datePickers) {
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
    }

    public static void initComboBoxCellDesignColor(String hexColor, ComboBox<?>... comboBoxes) {
        for (ComboBox<?> comboBox : comboBoxes) {
            initComboBoxCellDesignColor(comboBox, hexColor);
        }
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

    public static void setDatePickerFormat(DatePicker... datePickers) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (DatePicker datePicker : datePickers) {
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
        pgPagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            changeTableView(newValue.intValue(), ROWS_PER_PAGE, tbl, tbldata_list_size, filteredData);
            tbl.scrollTo(0);
        });
    }

    public static void changeTableView(int index, int limit, TableView tbl, int tbldata_list_size, FilteredList filteredData) {
        tbl.getSelectionModel().clearSelection();
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, tbldata_list_size);
        int minIndex = Math.min(toIndex, tbldata_list_size);
        try {
            SortedList<T> sortedData = new SortedList<>(
                    FXCollections.observableArrayList(filteredData.subList(Math.min(fromIndex, minIndex), minIndex)));
            sortedData.comparatorProperty().bind(tbl.comparatorProperty());
        } catch (Exception e) {
        }
        try {
            tbl.setItems(FXCollections.observableArrayList(filteredData.subList(fromIndex, toIndex)));
        } catch (Exception e) {

        }
    }

    public static class StageManager {

        private Stage dialog;
        private EventHandler<WindowEvent> onHiddenHandler; // Store handler
        private final xyOffset xyOffset = new xyOffset();
        Scene scene = null;
        Parent root = null;

        public void showDialog(Stage parentStage, URL fxmlurl,
                Object controller,
                String lsDialogTitle,
                boolean enableWindowDrag,
                boolean enableblock,
                boolean stayOnTop
        ) throws IOException {

            FXMLLoader loader = new FXMLLoader(fxmlurl);
            loader.setController(controller);

            root = loader.load();

            root.setOnMousePressed(event -> {
                xyOffset.x = event.getSceneX();
                xyOffset.y = event.getSceneY();
            });

            if (enableWindowDrag) {
                root.setOnMouseDragged(event -> {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setX(event.getScreenX() - xyOffset.x);
                    stage.setY(event.getScreenY() - xyOffset.y);
                });
            }

            dialog = new Stage();
            dialog.initStyle(StageStyle.UNDECORATED);
            if (enableblock) {
                dialog.initModality(Modality.WINDOW_MODAL);
                if (parentStage != null) {
                    dialog.initOwner(parentStage); // sets the blocking owner
                }
            }

            if (stayOnTop) {
                dialog.setAlwaysOnTop(true);
            }

            scene = new Scene(root);

            dialog.setTitle(lsDialogTitle);
            dialog.setScene(scene);

            // Attach stored onHiddenHandler if available
            if (onHiddenHandler != null) {
                dialog.setOnHidden(onHiddenHandler);
                onHiddenHandler = null; // Clear after assigning
            }

            dialog.show();
            dialog.toFront();
        }

        public void setOnHidden(EventHandler<WindowEvent> handler) {
            onHiddenHandler = handler;
        }

        public Scene getScene() {
            return scene;
        }

        public Parent getRoot() {
            return root;
        }

        public void closeSerialDialog() {
            if (dialog != null) {
                dialog.close();
                dialog = null;
            }
        }
    }

    private static class xyOffset {

        double x, y;
    }

    public static void stackPaneClip(StackPane stackPane1) {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(
                stackPane1.getWidth() - 8,
                stackPane1.getHeight() - 8
        );
        clip.setArcWidth(8);
        clip.setArcHeight(8);
        clip.setLayoutX(4);
        clip.setLayoutY(4);
        stackPane1.setClip(clip);
    }

    public static boolean isImageViewOutOfBounds(ImageView imageView, StackPane stackPane) {
        Bounds clipBounds = stackPane.getClip().getBoundsInParent();
        Bounds imageBounds = imageView.getBoundsInParent();

        return imageBounds.getMaxX() < clipBounds.getMinX()
                || imageBounds.getMinX() > clipBounds.getMaxX()
                || imageBounds.getMaxY() < clipBounds.getMinY()
                || imageBounds.getMinY() > clipBounds.getMaxY();
    }

    public static void resetImageBounds(ImageView imageView, StackPane stackPane1) {
        imageView.setScaleX(1.0);
        imageView.setScaleY(1.0);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        stackPane1.setAlignment(imageView, javafx.geometry.Pos.CENTER);
    }

    public static class ImageViewer {

        public double ldstackPaneWidth = 0;
        public double ldstackPaneHeight = 0;
        public double mouseAnchorX;
        public double mouseAnchorY;
        public double scaleFactor = 1.0;

        public void initAttachmentPreviewPane(StackPane stackPane, ImageView imageView) {
            stackPane.layoutBoundsProperty().addListener((observable, oldBounds, newBounds) -> {
                stackPane.setClip(new javafx.scene.shape.Rectangle(
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

            stackPane.widthProperty().addListener((observable, oldValue, newWidth) -> {
                ldstackPaneWidth = newWidth.doubleValue();
            });
        }
    }

    public static void adjustImageSize(Image image, ImageView imageView, double ldstackPaneWidth, double ldstackPaneHeight) {
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
        //used for removal
        for (TextField tf : textFields) {
            if (tf.getStyleClass().contains(lsCssClassName)) {
                return true;
            }
        }
        return false;
    }

    public static void setDisabled(boolean disable, Node... nodes) {
        for (Node node : nodes) {
            node.setDisable(disable);
            if (node instanceof TextField) {
                if (!disable) {
                    while (node.getStyleClass().contains("DisabledTextField")) {
                        node.getStyleClass().remove("DisabledTextField");
                    }
                } else {
                    node.getStyleClass().add("DisabledTextField");
                }
            }
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

    public static List<String> getTextFieldsIDWithPrompt(String lsPromptMsg, AnchorPane... panes) {
        List<String> results = new ArrayList<>();
        for (AnchorPane pane : panes) {
            for (Node node : pane.getChildren()) {
                collectTextFieldIDs(node, lsPromptMsg, results);
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

    private static class scrollOffset {

        double y;
        int caretPos;
    }

    public static void setVerticalScroll(TextArea textArea) {
        textArea.applyCss();
        textArea.layout();

        textArea.setStyle(
                "-fx-font-size: 10pt;"
                + "-fx-border-radius: 2px;"
                + "-fx-opacity: 1.0;"
                + "-fx-background-color: grey, white;"
                + "-fx-text-fill: black;"
                + "-fx-border-color: grey;"
        );

        // Access the internal ScrollPane
        ScrollPane scrollPane = (ScrollPane) textArea.lookup(".scroll-pane");
        if (scrollPane != null) {
            scrollPane.setStyle(
                    "-fx-focus-color: -fx-focus-color;"
                    + "-fx-vbar-policy: as-needed;"
                    + "-fx-background-color: transparent;"
                    + "-fx-opacity: 1.0;"
            );

            // Track vertical scroll position
            final scrollOffset xyOffset = new scrollOffset();
            final scrollOffset state = new scrollOffset();

            scrollPane.vvalueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.intValue() == 0) {

                } else {
                    xyOffset.y = newVal.doubleValue();
                    System.out.println(xyOffset.y);
                }
            });
            textArea.caretPositionProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal.intValue() <= 0) {

                } else {
                    state.caretPos = newVal.intValue();
                }
            });
            // Restore scroll position on focus lost
            textArea.focusedProperty().addListener((obs, oldVal, isFocused) -> {
                if (isFocused) {
                    textArea.setStyle(
                            "-fx-background-color: orange, white;"
                            + "-fx-text-fill: black;"
                            + "-fx-border-color: orange;"
                    );

                } else {
                    textArea.setStyle(
                            "-fx-background-color: grey, white;"
                            + "-fx-text-fill: black;"
                            + "-fx-border-color: grey;"
                    );
                }
            });
        }

        textArea.getStyleClass().add("custom-text-area");
    }

    public static class LoadScreenComponents {

        public final ProgressIndicator progressIndicator;
        public final StackPane loadingPane;
        public final Label placeholderLabel;

        public LoadScreenComponents(ProgressIndicator pi, StackPane sp, Label lbl) {
            this.progressIndicator = pi;
            this.loadingPane = sp;
            this.placeholderLabel = lbl;
        }
    }

    //JFXUtil.LoadScreenComponents loading = JFXUtil.createLoadingComponents();
    //tblViewDetails.setPlaceholder(loading.loadingPane);
    public static LoadScreenComponents createLoadingComponents() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxHeight(50);
        progressIndicator.setStyle("-fx-progress-color: #FF8201;");
        progressIndicator.setVisible(true);

        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setAlignment(Pos.CENTER);

        Label placeholderLabel = new Label("NO RECORD TO LOAD");
        placeholderLabel.setStyle("-fx-font-size: 10px;");

        return new LoadScreenComponents(progressIndicator, loadingPane, placeholderLabel);
    }

    public static String getFormattedClassTitle(Class<?> javaclass) {
        String className = javaclass.getSimpleName();

        if (className.endsWith("Controller")) {
            className = className.substring(0, className.length() - "Controller".length());
        }

        // Handle specific company renaming
        className = className.replace("MonarchFood", "MF");
        className = className.replace("MonarchHospitality", "MH");

        // Replace underscores with space
        className = className.replace("_", " ");

        // Add space before capital letters, but preserve acronyms
        className = className.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
        className = className.replaceAll("(?<=[A-Z])(?=[A-Z][a-z])", " ");

        className = className.trim();

        // Special replacements after spacing
        className = className.replace("SP Car", "SPCar");
        className = className.replace("SP MC", "SPMC");

        return className;
    }

    public static String getFormattedFXMLTitle(String fxmlPath) {
        // Extract the FXML file name without extension
        String fileName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1, fxmlPath.lastIndexOf('.'));

        // Remove common suffixes like "Controller"
        if (fileName.endsWith("Controller")) {
            fileName = fileName.substring(0, fileName.length() - "Controller".length());
        }

        // Handle specific company renaming
        fileName = fileName.replace("MonarchFood", "MF");
        fileName = fileName.replace("MonarchHospitality", "MH");

        // Replace underscores with space
        fileName = fileName.replace("_", " ");

        // Add space before capital letters, keeping acronyms intact
        fileName = fileName.replaceAll("(?<=[a-z])(?=[A-Z])", " ");
        fileName = fileName.replaceAll("(?<=[A-Z])(?=[A-Z][a-z])", " ");

        // Trim and apply custom replacements
        fileName = fileName.trim();
        fileName = fileName.replace("SP Car", "SPCar");
        fileName = fileName.replace("SP MC", "SPMC");

        return fileName;
    }

    //JFXUtil.getFormattedClassTitle(this.getClass());
    public static <T> void selectAndFocusRow(TableView<T> tableView, int index) {
        tableView.getSelectionModel().select(index);
        tableView.getFocusModel().focus(index);
    }

    public static void setValueToNull(Object... items) {
        for (Object item : items) {
            if (item instanceof Node) {
                Node node = (Node) item;

                if (node instanceof TextInputControl) {
                    ((TextInputControl) node).clear();
                } else if (node instanceof ComboBox) {
                    ((ComboBox<?>) node).setValue(null);
                } else if (node instanceof CheckBox) {
                    ((CheckBox) node).setSelected(false);
                } else if (node instanceof DatePicker) {
                    ((DatePicker) node).setValue(null);
                } else {
                }
            } else if (item instanceof AtomicReference) {
                ((AtomicReference<?>) item).set(null);
            } else {
            }
        }
    }

    public static String safeString(Object value) {
        return value != null ? value.toString() : "";
    }

    public static TextFieldControlInfo getControlInfo(Observable o) {
        if (o instanceof ReadOnlyProperty) {
            Object bean = ((ReadOnlyProperty<?>) o).getBean();
            if (bean instanceof TextInputControl) {
                TextInputControl control = (TextInputControl) bean;
                String id = control.getId();
                String value = control.getText() != null ? control.getText() : "";
                return new TextFieldControlInfo(id, value, control);
            }
        }
        return null;
    }

    public static class TextFieldControlInfo {

        public final String lsID;
        public final String lsTxtValue;
        public final TextInputControl txtField;

        public TextFieldControlInfo(String id, String value, TextInputControl control) {
            this.lsID = id;
            this.lsTxtValue = value;
            this.txtField = control;
        }
    }

    //JFXUtil.TextFieldControlInfo txtcontrol = JFXUtil.getControlInfo((Observable) o);
    public static void setActionListener(EventHandler<ActionEvent> handler, Node... nodes) {
        for (Node node : nodes) {
            if (node instanceof ComboBoxBase) {
                ((ComboBoxBase<?>) node).setOnAction(handler);
            } else if (node instanceof TextField) {
                ((TextField) node).setOnAction(handler);
            }
        }
    }

    public static void setJSONSuccess(JSONObject json, String message) {
        json.put("result", "success");
        json.put("message", message);
    }

    public static void setJSONError(JSONObject json, String message) {
        json.put("result", "error");
        json.put("message", message);
    }

    public static boolean isJSONSuccess(JSONObject json) {
        return ("success".equals((String) json.get("result"))) ? true : false;
    }

    public static String getJSONMessage(JSONObject json) {
        return (String) json.get("message");
    }

    private static class CommaFormater {

        boolean isUpdating;
        AtomicBoolean isAdjusting;
        int newCaretPos;
    }
//    private static boolean isUpdating = false;
//   private static  AtomicBoolean isAdjusting = new AtomicBoolean(false);
//    private static int newCaretPos = 0;

    public static void setCommaFormatter(TextField... textFields) {

        DecimalFormat finalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        finalFormat.setGroupingUsed(true);
        finalFormat.setMinimumFractionDigits(2);
        finalFormat.setMaximumFractionDigits(2);

        for (TextField textField : textFields) {
            final CommaFormater data = new CommaFormater();
            data.isUpdating = false;
            data.isAdjusting = new AtomicBoolean(false);
            data.newCaretPos = 0;
            // Disables other character
            UnaryOperator<TextFormatter.Change> filter = change -> {
                String newText = change.getControlNewText();
                if (!newText.matches("[\\d,\\.]*")) {
                    return null;
                }

                long dotCount = newText.chars().filter(c -> c == '.').count();
                if (dotCount > 1) {
                    return null;
                }

                return change;
            };
            textField.setTextFormatter(new TextFormatter<>(filter));
            // Real-time formatting
            textField.textProperty().addListener((obs, oldValue, newValue) -> {
                if (data.isAdjusting.get() == true) {
                    return;
                }
                try {
                    if (data.isUpdating) {
                        return;
                    }
                    data.isUpdating = true;
                    String clean = newValue.replaceAll(",", "");
                    if (clean.isEmpty() || clean.equals(".") || clean.matches("0*\\.0*")) {
                        data.isUpdating = false;
                        return;
                    }
                    try {
                        String integerPart = clean;
                        String decimalPart = "";
                        int dotIndex = clean.indexOf(".");
                        if (dotIndex >= 0) {
                            integerPart = clean.substring(0, dotIndex);
                            decimalPart = clean.substring(dotIndex);
                        }
                        long integerVal = integerPart.isEmpty() ? 0 : Long.parseLong(integerPart);
                        String formattedInteger = NumberFormat.getIntegerInstance(Locale.US).format(integerVal);
                        String formatted = formattedInteger + decimalPart;
                        Platform.runLater(() -> {
                            data.isAdjusting.set(true);
                            int originalCaretPos = textField.getCaretPosition();
                            textField.setText(formatted);
                            int offset = formatted.length() - newValue.length();
                            data.newCaretPos = originalCaretPos + offset;
                            data.newCaretPos = Math.max(0, Math.min(formatted.length(), data.newCaretPos));
                            data.isAdjusting.set(false);
                        });
                        Platform.runLater(() -> {
                            textField.positionCaret(data.newCaretPos);
                        });
                    } catch (Exception e) {
                    }
                    data.isUpdating = false;
                } catch (Exception e) {
                    data.isUpdating = false;
                }

            });
        }
    }

    public static class MonthYearPicker {

        public static class Picker {

            public final TextField textField;
            public final Popup popup;
            public final Label yearLabel;
            public final GridPane monthGrid;
            public int selectedYear;
            public int selectedMonth;
            public final Consumer<YearMonth> onDateSelected;

            public Picker(TextField textField, Consumer<YearMonth> onDateSelected) {

                this.textField = textField;
                this.onDateSelected = onDateSelected;
                this.popup = new Popup();
                this.popup.setAutoHide(true);

                selectedYear = YearMonth.now().getYear();
                selectedMonth = YearMonth.now().getMonthValue();

                textField.setPromptText("MM/YYYY");
//                textField.setEditable(false);

                VBox popupContent = new VBox(10);
                popupContent.setPadding(new Insets(10));
                popupContent.getStyleClass().add("popup-content");

                yearLabel = new Label(String.valueOf(selectedYear));
                yearLabel.getStyleClass().add("year-label");

                Button btnPrev = new Button("<");
                Button btnNext = new Button(">");
                btnPrev.getStyleClass().add("year-button");
                btnNext.getStyleClass().add("year-button");

                btnPrev.setOnAction(e -> {
                    selectedYear--;
                    yearLabel.setText(String.valueOf(selectedYear));
                    refreshMonthSelection();
                });

                btnNext.setOnAction(e -> {
                    selectedYear++;
                    yearLabel.setText(String.valueOf(selectedYear));
                    refreshMonthSelection();
                });

                HBox yearControls = new HBox(10, btnPrev, yearLabel, btnNext);
                yearControls.setAlignment(Pos.CENTER);
                yearControls.getStyleClass().add("year-bar");
                monthGrid = new GridPane();
                monthGrid.setHgap(2);
                monthGrid.setVgap(2);
                monthGrid.setAlignment(Pos.CENTER);

                Month[] months = Month.values();
                for (int i = 0; i < months.length; i++) {
                    Button btn = new Button(months[i].getDisplayName(java.time.format.TextStyle.SHORT, Locale.ENGLISH));
                    btn.getStyleClass().add("month-button");
                    int monthValue = i + 1;
                    btn.setOnAction(e -> {
                        selectedMonth = monthValue;
                        updateTextFieldAndNotify();
                        refreshMonthSelection();
                        popup.hide();
                        textField.getParent().requestFocus();

                    });
                    monthGrid.add(btn, i % 3, i / 3);
                }

                popupContent.getChildren().addAll(yearControls, monthGrid);
                popup.getContent().add(popupContent);

                // Load CSS
                popupContent.getStylesheets().add(
                        MonthYearPicker.class.getResource("/com/rmj/guanzongroup/sidebarmenus/css/StyleSheet.css").toExternalForm()
                );
                textField.setOnMouseClicked(e -> {
                    if (!popup.isShowing()) {
                        Bounds bounds = textField.localToScreen(textField.getBoundsInLocal());
                        double x = bounds.getMinX();
                        double y = bounds.getMaxY();
                        popup.show(textField, x - 13, y - 7);
                    } else {
                        popup.hide();
                    }
                });

                popup.setOnHiding(e -> {
                    updateTextFieldAndNotify();
                });

                // Initialize textField with current date
                updateTextFieldAndNotify();
                refreshMonthSelection();
            }

            public void updateTextFieldAndNotify() {
                YearMonth ym = YearMonth.of(selectedYear, selectedMonth);
                textField.setText(String.format("%02d/%d", ym.getMonthValue(), ym.getYear()));
                if (onDateSelected != null) {
                    onDateSelected.accept(ym);
                }
            }

            public void refreshMonthSelection() {
                monthGrid.getChildren().forEach(node -> node.getStyleClass().remove("selected-month"));
                for (javafx.scene.Node node : monthGrid.getChildren()) {
                    if (node instanceof Button) {
                        Button btn = (Button) node;
                        // Get the month value by matching the displayed short text to Month enum correctly
                        String shortMonthName = btn.getText();
                        Month m = Month.from(
                                java.time.format.DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH)
                                        .parse(shortMonthName)
                        );
                        if (m.getValue() == selectedMonth) {
                            btn.getStyleClass().add("selected-month");
                            break;
                        }
                    }
                }
                yearLabel.setText(String.valueOf(selectedYear));
            }

            public void setYearMonth(YearMonth ym) {
                selectedYear = ym.getYear();
                selectedMonth = ym.getMonthValue();
                updateTextFieldAndNotify();
                refreshMonthSelection();
            }

            public YearMonth getYearMonth() {
                return YearMonth.of(selectedYear, selectedMonth);
            }

            public void clear() {
                selectedYear = 0;
                selectedMonth = 0;
                textField.clear();
                monthGrid.getChildren().forEach(node -> node.getStyleClass().remove("selected-month"));
            }
        }

        public static Picker setupMonthYearPicker(TextField textField, Consumer<YearMonth> onDateSelected) {
            return new Picker(textField, onDateSelected);
        }
    }

    public static void makeKeyPressed(Node targetNode, KeyCode keyCode) {
        if (targetNode == null || keyCode == null) {
            return;
        }
        KeyEvent keyEvent = new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "", // character
                "", // text
                keyCode,
                false, // shiftDown
                false, // controlDown
                false, // altDown
                false // metaDown
        );
        targetNode.fireEvent(keyEvent);
    }

    public static void setKeyEventFilter(EventHandler<KeyEvent> handler, Node... nodes) {
        if (handler == null || nodes == null) {
            return;
        }

        for (Node node : nodes) {
            if (node != null) {
                node.addEventFilter(KeyEvent.KEY_PRESSED, handler);
            }
        }
    }

    public static void focusFirstTextField(final AnchorPane anchorPane) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                TextField textField = findFirstTextField(anchorPane);
                if (textField != null) {
                    textField.requestFocus();
                }
            }
        });
    }

    private static TextField findFirstTextField(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof TextField) {
                return (TextField) node;
            } else if (node instanceof Parent) {
                TextField result = findFirstTextField((Parent) node);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public static <T> void applyRowHighlighting(
            final TableView<T> tableView,
            final Function<T, String> keyExtractor,
            final Map<String, List<String>> highlightMap) {

        tableView.setRowFactory(new javafx.util.Callback<TableView<T>, TableRow<T>>() {
            @Override
            public TableRow<T> call(final TableView<T> tv) {
                return new TableRow<T>() {
                    @Override
                    protected void updateItem(T item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setStyle(""); // Reset style
                        } else {
                            String key = keyExtractor.apply(item);
                            if (highlightMap.containsKey(key)) {
                                List<String> colors = highlightMap.get(key);
                                if (!colors.isEmpty()) {
                                    setStyle("-fx-background-color: " + colors.get(colors.size() - 1) + ";");
                                }
                            } else {
                                setStyle(""); // Default
                            }
                        }
                    }
                };
            }
        });
    }

    public static String removeComma(String numberStr) {
        if (numberStr == null || numberStr.equals("")) {
            return "0";
        }
        String result = numberStr.replace(",", "");
        return result.isEmpty() ? "0" : result;
    }

    public static void showRetainedHighlight(boolean isRetained, TableView<?> tblView, String color, List<Pair<String, String>> plOrderNoPartial, List<Pair<String, String>> plOrderNoFinal,
            Map<String, List<String>> highlightedRows, boolean resetpartial) {
        if (isRetained) {
            for (Pair<String, String> pair : plOrderNoPartial) {
                if (!"0".equals(pair.getValue())) {
                    plOrderNoFinal.add(new Pair<>(pair.getKey(), pair.getValue()));
                }
            }
        }
        if (resetpartial) {
            disableAllHighlightByColor(tblView, color, highlightedRows);
            plOrderNoPartial.clear();
        }
        for (Pair<String, String> pair : plOrderNoFinal) {
            if (!"0".equals(pair.getValue())) {
                highlightByKey(tblView, pair.getKey(), color, highlightedRows);
            }
        }
    }

    public static void removeNoByKey(List<Pair<String, String>> plOrderNoPartial, List<Pair<String, String>> plOrderNoFinal, String lsNo) {
        removeFromListByKey(plOrderNoPartial, lsNo);
        removeFromListByKey(plOrderNoFinal, lsNo);
    }

    private static void removeFromListByKey(List<Pair<String, String>> list, String key) {
        Iterator<Pair<String, String>> iterator = list.iterator();
        while (iterator.hasNext()) {
            Pair<String, String> pair = iterator.next();
            if (pair.getKey().equals(key)) {
                iterator.remove();
            }
        }
    }

    private static void setKeyEvent(Scene scene, AtomicReference<Object> lastFocusedTextField, AtomicReference<Object> previousSearchedTextField) {
        scene.focusOwnerProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                if (newNode instanceof Button) {
                } else {
                    lastFocusedTextField.set(newNode);
                    previousSearchedTextField.set(null);
                }
            }
        });
    }

    public static void initKeyClickObject(AnchorPane ap, AtomicReference<Object> lastFocusedTextField, AtomicReference<Object> previousSearchedTextField) {
        AnchorPane root = (AnchorPane) ap;
        Scene scene = root.getScene();
        if (scene != null) {
            setKeyEvent(scene, lastFocusedTextField, previousSearchedTextField);
        } else {
            root.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    setKeyEvent(newScene, lastFocusedTextField, previousSearchedTextField);
                }
            });
        }
    }

    public static void setCheckboxHoverCursor(Parent... anchorpane) {
        for (Parent container : anchorpane) {
            applyToCheckBoxes(container);
        }
    }

    private static void applyToCheckBoxes(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof CheckBox) {
                final CheckBox checkBox = (CheckBox) node;
                checkBox.hoverProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal) {
                        checkBox.setCursor(checkBox.isDisabled() ? Cursor.DEFAULT : Cursor.HAND);
                    } else {
                        checkBox.setCursor(Cursor.DEFAULT);
                    }
                });
            } else if (node instanceof Parent) {
                applyToCheckBoxes((Parent) node); // recursively check inner containers
            }
        }
    }

    public static boolean isGeneralFXML(String fxmlPath) {
        String fileName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1, fxmlPath.lastIndexOf('.'));

        int underscoreIndex = fileName.indexOf('_');

        if (underscoreIndex == -1) {
            return true;
        }

        String suffix = fileName.substring(underscoreIndex + 1);

        String[] generalSuffixes = {
            "Entry", "Confirmation", "History"
        };
        for (String general : generalSuffixes) {
            if (suffix.equals(general)) {
                return true;
            }
        }
        return false;
    }

    public static String formatForMessageBox(String message, int maxLinewidth) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String[] words = message.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > maxLinewidth) {
                result.append(line.toString().trim()).append("\n");
                line.setLength(0);
            }
            line.append(word).append(" ");
        }

        if (line.length() > 0) {
            result.append(line.toString().trim());
        }

        return result.toString();
    }
}
