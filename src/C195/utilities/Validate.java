package C195.utilities;

import C195.controller.Operator;
import C195.model.Appointments;
import C195.model.Customers;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static C195.controller.loginForm.rb;

/**@author Carol Reid*/

/**This method is used to validate input and provide alert messages.*/
public class Validate {

    /**This method presents an error alert in French, indicating that the textField is empty.
     * There is a lambda expression within this method:
     * appendOperator = (a, b, c) -> a + field + " " + b + " " + c + ".";
     * It Concatenates variable's a("The"), field, b("is"), and c("blank").
     * @param field The name of the textField that is blank.
     */
    public static void frBlankAlert(String field) {

        Alert blank = new Alert(Alert.AlertType.ERROR);
        blank.setTitle(rb.getString("Error_Alert"));
        Operator<String> appendOperator = (a, b, c) -> a + " " + field + " " + b + " " + c + ".";
        blank.setHeaderText(
                appendOperator.process(rb.getString("The"), rb.getString("is"), rb.getString("blank")));
        blank.setContentText(rb.getString("Please_enter") + " " + field + ".");
        blank.showAndWait();
    }

    /**This method checks the users default locale, and presents an invalid log in alert in the correct language.*/
    public static void invalidLogin() {

        Alert invalid = new Alert(Alert.AlertType.ERROR);

        if (Locale.getDefault().getLanguage().equals("fr")) {
            invalid.setTitle(rb.getString("Error_Alert"));
            invalid.setHeaderText(rb.getString("Invalid"));
            invalid.setContentText(rb.getString("try_Again"));
            invalid.showAndWait();
        }
        else {
            invalid.setTitle("Error Alert");
            invalid.setHeaderText("Invalid Username or Password.");
            invalid.setContentText("Please try again.");
            invalid.showAndWait();
        }
    }

    /**This method alerts the user of an upcoming appointment.
     * @param apptID The upcoming appointments ID.
     * @param apptLDT The upcoming appointments start time.
     */
    public static void apptAlert(int apptID, LocalDateTime apptLDT) {

        Alert appt = new Alert(Alert.AlertType.INFORMATION);
        appt.setTitle("Upcoming Appointment");
        appt.setHeaderText("Appointment Number: " + apptID);
        appt.setContentText("Is scheduled to start at: " + apptLDT);
        appt.showAndWait();
    }

    /**This method alerts the user of no upcoming appointments.*/
    public static void noApptAlert() {

        Alert nAppt = new Alert(Alert.AlertType.INFORMATION);
        nAppt.setTitle("No Upcoming Appointments");
        nAppt.setHeaderText("There are no appointments scheduled within the next 15 minutes.");
        nAppt.showAndWait();
    }

    /**This method checks whether textF is blank, then checks whether textF length is less then the maxLength.
     * If it is more, an alert notifies the user and returns false, otherwise it returns true.
     * @param label The name of the textField being checked.
     * @param textF The string entered in the textField.
     * @param maxLength The maximum length that the string can be.
     * @return True if textF is not blank and is less then the maxLength, otherwise false.*/
    public static boolean isValidLength(String label, String textF, int maxLength) {

        int tLength = textF.length();

        if (tLength == 0) {
            blankAlert(label);
            return false;
        }

        if (tLength > maxLength) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error alert");
            alert.setHeaderText("The " + label + " entered is too long.");
            alert.setContentText("Please enter a value less than " + maxLength + " characters long.");
            alert.showAndWait();

            return false;
        }
        return true;
    }

    /**This method presents an error alert in English, indicating that the textField is empty.
     * @param field The name of the textField that is blank.
     */
    public static void blankAlert(String field) {

        Alert blank = new Alert(Alert.AlertType.ERROR);
        blank.setTitle("Error alert");
        blank.setHeaderText("The " + field + " is blank.");
        blank.setContentText("Please enter a valid " + field + ".");
        blank.showAndWait();
    }

    /**This method filters allAppts by the selected customer ID. If no appointments are found, and the
     * deleteConfirm alert is confirmed, it deletes the customer from the database. If successful, an alert
     * notifies the user. Otherwise if appointments matching the selected customer are found, an alert
     * notifies the user that the customer can not be deleted.
     * @param checkCustID The selected customer ID.
     */
    public static void isValidCDelete(int checkCustID) throws SQLException {

        FilteredList<Appointments> appCustID = new FilteredList<>(Appointments.getAllAppts());
        appCustID.setPredicate(Appointments -> Appointments.getCustID() == checkCustID);

        if (appCustID.isEmpty()) {

            if (deleteConfirm("customer")) {

                if ((deleteCQuery(checkCustID)) == 1) {
                    successAlert("customer with the ID: " + checkCustID, "deleted");
                }
            }
        }
        else {
            cInvalidDelete();
        }

    }

    /**This method notifies the user of a successful action.
     * @param type The type of item the action was performed on.
     * @param action The action being performed.
     */
    public static void successAlert(String type, String action) {

        Alert blank = new Alert(Alert.AlertType.ERROR);
        blank.setTitle("You were successful");
        blank.setHeaderText("Success");
        blank.setContentText("The " + type + " was successfully " + action + ".");
        blank.showAndWait();
    }

    /**This method notifies the user of an invalid customer delete.*/
    public static void cInvalidDelete() {

        Alert blank = new Alert(Alert.AlertType.ERROR);
        blank.setTitle("Error alert");
        blank.setHeaderText("This customer has a scheduled appointment.");
        blank.setContentText("Please delete all appointments and then try again.");
        blank.showAndWait();
    }

    /**This method notifies the user that they must make a selection.
     * @param type The type of item that must be selected to continue.
     */
    public static void noSelectionAlert(String type) {

        Alert blank = new Alert(Alert.AlertType.ERROR);
        blank.setTitle("Error alert");
        blank.setHeaderText("A" + type + " must be selected to continue.");
        blank.setContentText("Please select a" + type + " and try again.");
        blank.showAndWait();
    }

    /**This method deletes the selected customer from the database and returns the number of customers effected.
     * @param dCustID The selected customer ID.
     * @return The number of customers effected.
     */
    public static int deleteCQuery(int dCustID) throws SQLException {

            String sql = "DELETE FROM customers WHERE Customer_ID = ?;";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, dCustID);
            return ps.executeUpdate();
    }

    /**This method deletes the selected appointment from the database and returns the number of appointments
     * effected.
     * @param dApptID The selected appointment ID.
     * @return The number of appointments effected.
     */
    public static int deleteAQuery(int dApptID) throws SQLException {

        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, dApptID);
        return ps.executeUpdate();
    }

    /**This method is used to confirm that the user would like to delete the selected type of data.
     * @param type The type of data being deleted.
     * @return True if confirmed, false if canceled.
     */
    public static boolean deleteConfirm(String type) {

        Alert delete = new Alert(Alert.AlertType.CONFIRMATION);
        delete.setTitle("Delete Confirmation Alert");
        delete.setHeaderText("Are you sure you want to delete the selected " + type + "?");
        delete.setContentText("");

        Optional<ButtonType> deleteresult = delete.showAndWait();

        if (deleteresult.get() == ButtonType.OK) {
            return true;
        }
        else {
            return false;
        }
    }

    /**This method alerts the user when there are no available appointment times for the customer with the
     * selected contact and date.
     */
    public static void noAvailApptAlert() {

        Alert blank = new Alert(Alert.AlertType.ERROR);
        blank.setTitle("Error alert");
        blank.setHeaderText("There are no available appointment times for that customer, with the selected contact on the selected date.");
        blank.setContentText("Please choose a different contact or date and try again.");
        blank.showAndWait();
    }

    /**This method alerts the user when no selection is made when the modify button is clicked.
     * @param typeToMod The type of data the user is trying to modify.
     */
    public static void pickToMod(String typeToMod) {

        Alert blank = new Alert(Alert.AlertType.INFORMATION);
        blank.setTitle(typeToMod + " Modify");
        blank.setHeaderText("Please select a" + typeToMod + " to modify.");
        blank.setContentText("Then click on the modify button.");
        blank.showAndWait();
    }

    /**This method is a general alert that indicates that something has gone wrong.*/
    public static void genAlert() {

        Alert gen = new Alert(Alert.AlertType.ERROR);
        gen.setTitle("An Error Occurred");
        gen.setHeaderText("Something went wrong.");
        gen.setContentText("Please try again.");
        gen.showAndWait();
    }
}

