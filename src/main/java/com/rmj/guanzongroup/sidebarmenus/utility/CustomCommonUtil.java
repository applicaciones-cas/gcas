/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.rmj.guanzongroup.sidebarmenus.utility;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Pattern;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;

/**
 *
 * @author
 */
/**
 * Utility class providing various common methods for date formatting, text
 * manipulation, and other utilities.
 */
public class CustomCommonUtil {

    /* DATE FORMATTER UTILITY SECTION */
    /**
     * Converts a string representing a date in "yyyy-MM-dd" format to a
     * {@link LocalDate} object.
     *
     * This method takes a date in string format (e.g., "2024-10-01") and
     * converts it to a {@link LocalDate}. It expects the input string to follow
     * the "yyyy-MM-dd" format. If the input cannot be parsed, a
     * {@link DateTimeParseException} will be thrown.
     *
     * @param fsDateValue
     * @param fsPattern The date string in "yyyy-MM-dd" format.
     * @return A {@link LocalDate} object representing the date.
     *
     * <b>Example:</b>
     * <pre>{@code
     * String dateStr = "2024-10-01";
     * LocalDate date = parseDateStringToLocalDate(dateStr, "yyyy-MM-dd");
     * System.out.println(date); // Outputs: 2024-10-01
     * }</pre>
     */
    public static LocalDate parseDateStringToLocalDate(String fsDateValue) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(fsDateValue, dateFormatter);
    }

    /**
     * Converts a {@link Date} object to a string in "yyyy-MM-dd" format.
     *
     * This method formats a {@link Date} object (e.g., from a timestamp) to a
     * string in the "yyyy-MM-dd" format, commonly used for database entries or
     * display.
     *
     * @param foDateValue The {@link Date} object to be formatted.
     * @return A string representing the date in "yyyy-MM-dd" format.
     *
     * <b>Example:</b>
     * <pre>{@code
     * Date now = new Date();
     * String formattedDate = formatDateToShortString(now);
     * System.out.println(formattedDate); // Outputs: Current date in yyyy-MM-dd format
     * }</pre>
     */
    public static String formatDateToShortString(Date foDateValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(foDateValue);
    }

    /**
     * Converts a {@link Date} object to a string in "yyyy-MM-dd" format.
     *
     * This method formats a {@link Date} object (e.g., from a timestamp) to a
     * string in the "yyyy-MM-dd" format, commonly used for database entries or
     * display.
     *
     * @param foLocalDate The {@link Date} object to be formatted.
     * @return A string representing the date in "yyyy-MM-dd" format.
     *
     * <b>Example:</b>
     * <pre>{@code
     * LocalDate now = new LocalDate();
     * String formattedDate = formatLocalDateToShortString(now);
     * System.out.println(formattedDate); // Outputs: Current date in yyyy-MM-dd format
     * }</pre>
     */
    public static String formatLocalDateToShortString(LocalDate foLocalDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return foLocalDate.format(formatter);
    }

    /**
     * Converts a date string from the "MMMM dd, yyyy" format to "yyyy-MM-dd".
     *
     * This method takes a string formatted with the month name (e.g., "October
     * 02, 2024") and converts it into the standard "yyyy-MM-dd" format. If the
     * input string cannot be parsed, it throws a {@link ParseException}.
     *
     * @param fsLongDateString The date string in "MMMM dd, yyyy" format.
     * @return A string representing the date in "yyyy-MM-dd" format.
     * @throws ParseException If the input string cannot be parsed into a valid
     * date.
     *
     * <b>Example:</b>
     * <pre>{@code
     * String dateStr = "October 02, 2024";
     * String formattedDate = convertLongDateStringToShort(dateStr);
     * System.out.println(formattedDate); // Outputs: 2024-10-02
     * }</pre>
     */
    public static String convertLongDateStringToShort(String fsLongDateString) throws ParseException {
        SimpleDateFormat fromUser = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        return myFormat.format(fromUser.parse(fsLongDateString));
    }

    /**
     * Formats a {@link Date} object into a string with the month name and day
     * in the "MMMM dd, yyyy" format.
     *
     * This method converts a {@link Date} object into a more human-readable
     * format, where the month name is displayed in full, such as "October 02,
     * 2024".
     *
     * @param foDateValue The {@link Date} object to be formatted.
     * @return A string representing the date in "MMMM dd, yyyy" format.
     *
     * <b>Example:</b>
     * <pre>{@code
     * Date now = new Date();
     * String formattedDate = formatDateWithMonthName(now);
     * System.out.println(formattedDate); // Outputs: October 02, 2024 (or current date)
     * }</pre>
     */
    public static String formatDateWithMonthName(Date foDateValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy");
        return sdf.format(foDateValue);
    }

    /**
     * Adds a text limiter to a {@link TextField} to restrict its length to a
     * specified maximum number of characters.
     *
     * @param foTextField The {@link TextField} to which the limiter will be
     * applied.
     * @param fnMaxLength The maximum length of text allowed.
     */
    @SuppressWarnings("unchecked")
    public static void setTextFieldValueLimit(TextField foTextField, int fnMaxLength) {
        if (foTextField.getProperties().get("textLimiter") != null) {
            foTextField.textProperty().removeListener((ChangeListener<String>) foTextField.getProperties().get("textLimiter"));
        }

        final boolean[] isUpdating = {false};

        ChangeListener<String> textLimiter = (observable, oldValue, newValue) -> {
            if (isUpdating[0]) {
                return;
            }

            if (newValue.length() > fnMaxLength) {
                isUpdating[0] = true;
                foTextField.setText(oldValue);
                isUpdating[0] = false;
            }
        };

        foTextField.textProperty().addListener(textLimiter);
        foTextField.getProperties().put("textLimiter", textLimiter);
    }

    /**
     * Extracts the first initial from a given full name and appends the last
     * name.
     *
     * This method takes a full name in the format "FirstName LastName" and
     * returns a formatted string containing the first letter of the first name
     * followed by a period and the last name.
     *
     * <p>
     * Example:
     * <pre>{@code
     * String formattedName = formatInitialAndLastName("John Doe");
     * System.out.println(formattedName); // Outputs: J. Doe
     * }</pre>
     *
     * @param fsFullName The full name in "FirstName LastName" format.
     * @return A formatted string with the first initial and last name.
     * @throws IllegalArgumentException If the input is null, empty, or does not
     * contain at least two words.
     */
    public static String formatInitialAndLastName(String fsFullName) {
        if (fsFullName == null || fsFullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or empty.");
        }

        String[] nameParts = fsFullName.trim().split("\\s+"); // Handles multiple spaces
        if (nameParts.length < 2) {
            throw new IllegalArgumentException("Full name must contain at least first and last name.");
        }

        String firstNameInitial = nameParts[0].substring(0, 1);
        String lastName = nameParts[nameParts.length - 1];

        return firstNameInitial + ". " + lastName;
    }

    /**
     * @param foTxtFields The {@link TextField} to apply the behavior to.
     *
     * <b>Example:</b>
     * <pre>{@code
     * TextField textField1 = new TextField();
     * TextField textField2 = new TextField();
     * inputDecimalOnly(textField1,textField2);
     * }</pre>
     */
    public static void inputDecimalOnly(TextField... foTxtFields) {
        Pattern pattern = Pattern.compile("[0-9,.]*");
        for (TextField txtField : foTxtFields) {
            if (txtField != null) {
                txtField.setTextFormatter(new TextFormaterUtil(pattern));
            }
        }
    }

    /**
     * @param foTxtFields The {@link TextField} to apply the behavior to.
     *
     * <b>Example:</b>
     * <pre>{@code
     * TextField textField1 = new TextField();
     * TextField textField2 = new TextField();
     * inputIntegersOnly(textField1,textField2);
     * }</pre>
     */
    public static void inputIntegersOnly(TextField... foTxtFields) {
        Pattern pattern = Pattern.compile("[0-9,]*");
        for (TextField txtField : foTxtFields) {
            if (txtField != null) {
                txtField.setTextFormatter(new TextFormaterUtil(pattern));
            }
        }
    }

    /**
     *
     * @param foTxtFields The {@link TextField} to apply the behavior to.
     *
     * <b>Example:</b>
     * <pre>{@code
     * TextField textField1 = new TextField();
     * TextField textField2 = new TextField();
     * inputLettersOnly(textField1,textField2);
     * }</pre>
     */
    public static void inputLettersOnly(TextField... foTxtFields) {
        Pattern pattern = Pattern.compile("[a-zA-Z]*");
        for (TextField txtField : foTxtFields) {
            if (txtField != null) {
                txtField.setTextFormatter(new TextFormaterUtil(pattern));
            }
        }
    }

    /**
     *
     * @param foTxtFields The {@link TextField} to apply the behavior to.
     *
     * <b>Example:</b>
     * <pre>{@code
     * TextField textField1 = new TextField();
     * TextField textField2 = new TextField();
     * inputLettersAndNumbersOnly(textField1,textField2);
     * }</pre>
     */
    public static void inputLettersAndNumbersOnly(TextField... foTxtFields) {
        Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
        for (TextField txtField : foTxtFields) {
            if (txtField != null) {
                txtField.setTextFormatter(new TextFormaterUtil(pattern));
            }
        }
    }

    /**
     * Formats an integer or numeric value into a decimal format with two
     * decimal places.
     *
     * <p>
     * This method takes an input object, attempts to convert it to a numeric
     * value, and formats it to a string representation with thousands
     * separators and two decimal places.</p>
     *
     * <p>
     * If the input is invalid (e.g., non-numeric), it returns "0.00" as a
     * default value.</p>
     *
     * @param foObject The input object containing a numeric value (Integer,
     * Double, String, etc.).
     * @return A formatted string representation of the number (e.g.,
     * "1,000.00"). Returns "0.00" if the input is invalid or null.
     *
     * <b>Example Usage:</b>
     * <pre>{@code
     * System.out.println(setIntegerValueToDecimalFormat(1000));      // Outputs: "1,000.00"
     * System.out.println(setIntegerValueToDecimalFormat(1234567));  // Outputs: "1,234,567.00"
     * System.out.println(setIntegerValueToDecimalFormat("5000"));   // Outputs: "5,000.00"
     * System.out.println(setIntegerValueToDecimalFormat("abc"));    // Outputs: "0.00" (Invalid input)
     * System.out.println(setIntegerValueToDecimalFormat(null));     // Outputs: "0.00" (Null input)
     * }</pre>
     */
    public static String setIntegerValueToDecimalFormat(Object foObject) {
        DecimalFormat format = new DecimalFormat("#,##0.00");
        try {
            if (foObject != null) {
                return format.format(Double.parseDouble(String.valueOf(foObject)));
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format for input - " + foObject);
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
        return "0.00";
    }

}
