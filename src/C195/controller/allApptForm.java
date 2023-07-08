package C195.controller;

import C195.model.Appointments;
import C195.utilities.Validate;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.ResourceBundle;

import static C195.helper.Menu.menuSelection;

/**@author Carol Reid*/

/**This class is a controller for the allAppt Graphical User Interface scene.*/
public class allApptForm implements Initializable {

    public ComboBox<String> menu;
    public String selected;

    public TableView apptTable;
    public TableColumn ApptIDCol;
    public TableColumn titleCol;
    public TableColumn descCol;
    public TableColumn locCol;
    public TableColumn therapistCol;
    public TableColumn typeCol;
    public TableColumn startCol;
    public TableColumn endCol;
    public TableColumn clientIDCol;
    public TableColumn userIDCol;
    public RadioButton thisMonthRadio;
    public RadioButton thisWeekRadio;
    public RadioButton viewAllRadio;

    private ObservableList<Appointments> allAppts = Appointments.getAllAppts();
    private ObservableList<Appointments> fAppts = FXCollections.observableArrayList();

    private static Appointments mAppointment = null;

    public static Appointments getMAppointment() { return mAppointment; }


    /**This method initializes the allAppt scene and compares the users local date and time to determine
     * whether there is an appointment scheduled to start within the next 15 minutes.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        LocalDateTime myLDT = LocalDateTime.now(ZoneId.systemDefault());

        if (allAppts.isEmpty()) {
            try {
                Appointments.tableQueryA();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            allAppts.clear();
            try {
                Appointments.tableQueryA();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        menu.setItems(C195.helper.Menu.menuItems);

        apptTable.setItems(allAppts);
        ApptIDCol.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<>("desc"));
        locCol.setCellValueFactory(new PropertyValueFactory<>("loc"));
        therapistCol.setCellValueFactory(new PropertyValueFactory<>("contName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("startLDT"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("endLDT"));
        clientIDCol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));

        apptTable.getSortOrder().add(ApptIDCol);
        apptTable.sort();

        apptCheck(myLDT);
    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    /**This method filter's all appointments by the users current date. If there is an appointment scheduled
     * to start within the next 15 minutes, an alert will notify the user. Otherwise an alert indicating
     * that there are no upcoming appointments will pop up.
     */
    public void apptCheck(LocalDateTime myLDT) {

        FilteredList<Appointments> tFList = new FilteredList<>(allAppts);
        tFList.setPredicate(Appointments -> Appointments.getStartLDT().toLocalDate().isEqual(myLDT.toLocalDate()));

        LocalDateTime myLDT2 = myLDT.plusMinutes(15);

        if(tFList.isEmpty()) {
            Validate.noApptAlert();
        }
        else {
            for (Appointments a : tFList) {
                if ((a.getStartLDT().isEqual(myLDT)) || ((a.getStartLDT().isAfter(myLDT)) && (a.getStartLDT().isBefore(myLDT2))) ||
                        (a.getStartLDT().isEqual(myLDT2))) {

                    Validate.apptAlert(a.getApptID(), a.getStartLDT());
                    return;
                }
            }
            Validate.noApptAlert();
        }
    }

    /**This method gets the users current month and year and filters all appointments by both dates. */
    public void onThisMonth(ActionEvent actionEvent) {

        int currMonth = LocalDateTime.now().getMonthValue();
        int currYear = LocalDateTime.now().getYear();
        FilteredList<Appointments> fList = new FilteredList<>(Appointments.getAllAppts());
        fList.setPredicate(Appointments -> Appointments.getStartLDT().getMonthValue() == currMonth);
        fList.setPredicate(Appointments -> Appointments.getStartLDT().getYear() == currYear);

        apptTable.setItems(fList);
    }

    /**This method gets the users current year, month, day of month, and day of week. Then all appointments
     * that are equal to, or between, the beginning and end of the week are added to the fAppts list.
     */
    public void onThisWeek(ActionEvent actionEvent) {

        int currYear = LocalDateTime.now().getYear();
        int currMonth = LocalDateTime.now().getMonthValue();
        int currDayOfM = LocalDateTime.now().getDayOfMonth();
        int currDayOfW = LocalDateTime.now().getDayOfWeek().getValue();
        int beginWeekD = currDayOfM - currDayOfW;
        int endWeekD = currDayOfM + (6 - currDayOfW);

        if(!(fAppts.isEmpty())) {
            fAppts.clear();
        }

        for(Appointments a : allAppts) {
            if((a.getStartLDT().getYear() == currYear) && (a.getStartLDT().getMonthValue() == currMonth)) {
                if((a.getStartLDT().getDayOfMonth() >= beginWeekD) && (a.getStartLDT().getDayOfMonth() <= endWeekD)) {
                    fAppts.add(a);
                }
            }
        }

        apptTable.setItems(fAppts);
    }

    /**This method populates the appointment table with all of the appointments.*/
    public void viewAll(ActionEvent actionEvent) { apptTable.setItems(allAppts); }

    /**This method loads the addAppt scene when the add button is clicked.*/
    public void onAddAppt(ActionEvent actionEvent) throws IOException {

        menuSelection("Add An Appointment", actionEvent);
    }

    /**This method checks whether an appointment has been selected to be modified. Then it loads the
     * modAppt scene.
     */
    public void onModAppt(ActionEvent actionEvent) throws IOException {

        mAppointment = (Appointments) apptTable.getSelectionModel().getSelectedItem();

        if (mAppointment == null) {
            Validate.noSelectionAlert("n appointment");
        }
        else {

            Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/modAppt.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1100, 600);
            stage.setTitle("Modify An Appointment");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        }
    }

    /**This method checks whether an appointment has been selected to be deleted. Then it asks the user to
     * confirm that they would like to delete the appointment. If it is confirmed and successful the user
     * is notified before the allAppt scene is loaded.
     */
    public void onDelAppt(ActionEvent actionEvent) throws SQLException {

        mAppointment = (Appointments) apptTable.getSelectionModel().getSelectedItem();

        if (mAppointment == null) {
            Validate.noSelectionAlert("n appointment");
        }
        else {
            if (Validate.deleteConfirm("appointment")) {
                if (Validate.deleteAQuery(mAppointment.getApptID()) == 1) {
                    Validate.successAlert("appointment with the ID: " + mAppointment.getApptID(), "deleted");

                    allAppts.clear();
                    Appointments.tableQueryA();
                    apptTable.setItems(allAppts);
                }
                else {
                    Validate.genAlert();
                }
            }
        }
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }
}