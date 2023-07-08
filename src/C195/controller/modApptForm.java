package C195.controller;

import C195.model.Appointments;
import C195.model.Therapists;
import C195.model.Clients;
import C195.model.Users;
import C195.utilities.JDBC;
import C195.utilities.Validate;
import C195.utilities.apptTime;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

import static C195.helper.Menu.menuSelection;
import static C195.utilities.apptTime.*;

/**@author Carol Reid*/

/**This class is a controller for the modAppt Graphical User Interface scene.*/
public class modApptForm implements Initializable {

    public ComboBox<String> menu;
    public String selected;

    public TableView clientTable;
    public TableColumn clientIDCol;
    public TableColumn nameCol;
    public TableColumn addressCol;
    public TableColumn postalCol;
    public TableColumn phoneCol;
    public TableColumn divCol;
    public ComboBox userDropBox;
    public ComboBox therapistDropBox;
    public DatePicker apptDatePicker;
    public ComboBox startTime;
    public ComboBox endTime;
    public TextField modApptTitle;
    public TextField modApptDesc;
    public ComboBox modApptType;
    public TextField modApptLoc;


    private int newApptID = 0;
    private String newLastUpdatedBy = " ";
    private int newContID = 0;

    private LocalDate setDate;
    private LocalTime setStart;
    private LocalTime setEnd;
    private String setType;

    private Appointments modAppt = allApptForm.getMAppointment();
    private ObservableList<Clients> allCust = Clients.getAllClients();
    private ObservableList<Users> allUsers = Users.getAllUsers();
    private ObservableList<Therapists> allCont = Therapists.getAllTheras();
    private ObservableList<LocalTime> availETime = apptTime.getAvailEHours();
    private ObservableList<LocalTime> custAppts = getClientApptHours();
    private static ObservableList<String> Types = FXCollections.observableArrayList();

    /**This method initializes the modAppt scene with the data passed through the modAppt object. It calls
     * methods to populate the customer table, contact drop box and start time combo box. It adds all
     * office hours to availSTime by calling copyOfficeHours. Then it uses the start time assigned to modAppt
     * to find other appointments that the customer has that day by calling the fillCustApptHours method.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        popcustTable();

        String userN = "";
        int userID = modAppt.getUserID();
        for(Users u : Users.getAllUsers()) {
            if(userID == u.getUserID()) {
                userN = u.getUserName();
            }
        }
        userDropBox.setValue(userN);
        userDropBox.setItems(allUsers);

        popcontDropBox();
        copyOfficeHours();

        setDate = modAppt.getStartLDT().toLocalDate();
        apptDatePicker.setValue(setDate);
        fillClientApptHours(modAppt.getClientID(), setDate);

        try {
            popStartTime();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        setType = modAppt.getType();
        popTypeCB();

        modApptTitle.setText(String.valueOf(modAppt.getTitle()));
        modApptDesc.setText(String.valueOf(modAppt.getNote()));
        modApptLoc.setText(String.valueOf(modAppt.getLoc()));

        newApptID = modAppt.getApptID();
    }

    /**This method populates the customer table and selects the customer who's ID matches the custID
     * assigned to the modAppt object.
     */
    public void popcustTable() {

        if (allCust.isEmpty()) {
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            allCust.clear();
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        int selectedCust = modAppt.getClientID();
        for (Clients cust : Clients.getAllClients() ) {
            if(cust.getClientID() == selectedCust) {
                clientTable.getSelectionModel().select(cust);
            }
        }

        clientTable.setItems(allCust);
        clientIDCol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCol.setCellValueFactory(new PropertyValueFactory<>("postal"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divCol.setCellValueFactory(new PropertyValueFactory<>("divID"));

        clientTable.getSortOrder().add(clientIDCol);
        clientTable.sort();
    }

    /**This method populates the contact combo box and selects the contact assigned to the modAppt object.*/
    public void popcontDropBox() {

        String setCont = modAppt.getTherapistName();
        therapistDropBox.setItems(allCont);
        therapistDropBox.setValue(setCont);
    }

    /**This method sets the start time and end time combo boxes to the times assigned to the modAppt object.
     * Then it checks for available start times based on the contact and date. If the assigned customer has
     * other appointments that day, the times are removed from the list. Next, the current appointment time
     * is added to the list, and finally, the available end time combo box is populated.
     */
    public void popStartTime() throws SQLException {

        LocalDateTime officeStart = localToEST(modAppt.getStartLDT());
        setStart = officeStart.toLocalTime();
        startTime.setValue(setStart);

        LocalDateTime officeEnd = localToEST(modAppt.getEndLDT());
        setEnd = officeEnd.toLocalTime();
        endTime.setValue(setEnd);

        newContID = modAppt.getTherapistID();

        boolean apptAvail = (apptTime.getApptSTimes(newContID, setDate));

        if (!(custAppts.isEmpty())) {
            removeCAppt();
        }

        addCurrAppt(setStart, setEnd);
        startTime.setItems(availSHours);

        filterEOfficeHours(setStart);
        if(availETime.get(availETime.size() - 1).equals(LocalTime.of(21,45))) {
            availETime.add(LocalTime.of(22,00));
        }

        endTime.setItems(availETime);
    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    public void popTypeCB() {
       Types.clear();
           Types.addAll("Acupuncture", "Deep Tissue", "Hot Stone", "Reflexology", "Sports", "Swedish");
           modApptType.setItems(Types);
           modApptType.setValue(setType);
    }

    /**This method checks whether the selected customer ID matches the modAppt object's assigned customer ID.
     * If it does not match, the contact, date, start time, and end time are all cleared.
     */
    public void onClientSelected(MouseEvent mouseEvent) {

        Clients newCust = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if(newCust.getClientID() != modAppt.getClientID()) {

            if (apptDatePicker.getValue() != null) {

                therapistDropBox.setItems(allCont);
                apptDatePicker.setValue(null);
                availSHours.clear();
                availETime.clear();
                startTime.setItems(null);
                startTime.setValue(null);
                endTime.setItems(null);
                endTime.setValue(null);
            }
        }
    }

    /**This method gets the selected user and stores it.*/
    public void onUserDropBox(ActionEvent actionEvent) {

        Users selectedUser = (Users) userDropBox.getSelectionModel().getSelectedItem();
        newLastUpdatedBy = selectedUser.getUserName();
    }

    /**This method checks whether the selected contact ID matches the modAppt objects's assigned contact ID.
     * If it does not match, the date, start time, and end time are all cleared.
     */
    public void onTherapistDropBox(ActionEvent actionEvent) {

        Therapists selContact = (Therapists) therapistDropBox.getValue();
        newContID = selContact.getTheraID();

        if (apptDatePicker.getValue() != null) {

            apptDatePicker.setValue(null);
            availSHours.clear();
            custAppts.clear();
            availETime.clear();
            startTime.setItems(null);
            startTime.setValue(null);
            endTime.setItems(null);
            endTime.setValue(null);
        }
    }

    /**This method gets the selected date and after clearing the available start times, customer appointments,
     * and available end times it checks for available start times based on the contact and date. After getting
     * the selected customer, it checks for other appointments that day, and the times are removed from the list.
     * Next, the current appointment time is added to the list. If availStimes is empty but there are available
     * appointment times, add all office hours to availStimes. If the selected date and either the contact or
     * customer are the same as the original data assigned to the modAppt object, add the current appointment times.
     */
    public void onDatePicked(ActionEvent actionEvent) throws SQLException {

        boolean apptAvail;

        LocalDate pickedDate = apptDatePicker.getValue();
        if(pickedDate == null) {
            return;
        }

        if(!(availSHours.isEmpty())) {
            availSHours.clear();
            custAppts.clear();
            availETime.clear();
        }

        apptAvail = (apptTime.getApptSTimes(newContID, pickedDate));

        Clients modCust = (Clients) clientTable.getSelectionModel().getSelectedItem();

        fillClientApptHours(modCust.getClientID(), pickedDate);
        if(!(custAppts.isEmpty())) {
            removeCAppt();
        }

        if((availSHours.isEmpty()) && (apptAvail)) {
            copyOfficeHours();
        }

        if((pickedDate.isEqual(setDate)) && ((modCust.getClientID() == modAppt.getClientID()) ||
                (newContID == modAppt.getTherapistID()))) {

            addCurrAppt(setStart, setEnd);
        }

        startTime.setItems(availSHours);
    }

    /**This method checks whether the start time has been selected. Then it clears the end time list before
     * populating it. If the time of 21:45 is present in the list, then it adds the final available time 22:00.
     * If the start time is 21:45 then it adds 22:00 to the list.
     */
    public void onStartTime(ActionEvent actionEvent) {

        LocalTime pickedStart = (LocalTime) startTime.getValue();
        if(pickedStart == null) {
            return;
        }

        availETime.clear();
        filterEOfficeHours(pickedStart);

        if ((!(availETime.isEmpty())) && (availETime.get(availETime.size() - 1).equals(LocalTime.of(21, 45)))) {
                availETime.add(LocalTime.of(22, 00));
        }
        else if(pickedStart.equals(LocalTime.of(21, 45))) {
            availETime.add(LocalTime.of(22, 00));
        }

        endTime.setItems(availETime);
    }

    /**This method checks whether the end time has been selected. If the time of 21:45 is present in the list,
     * then it adds the final available time 22:00;
     */
    public void onEndTime(ActionEvent actionEvent) {

        if (endTime.getValue() == null) {
            return;
        }
        if(availETime.get(availETime.size() - 1).equals(LocalTime.of(21,45))) {
            availETime.add(LocalTime.of(22,00));
        }
    }

    /**This method gets all of the data entered, checks if they are valid, and updates the appointment in the
     * database. An alert then notifies the user on whether the update was successful or not.
     */
    public void onModAppt(ActionEvent actionEvent) throws SQLException, IOException {

        Clients newCust = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if(newCust == null) { Validate.blankAlert("Customer"); return; }
        if(userDropBox.getValue() == null) { Validate.blankAlert("User"); return; }
        if(therapistDropBox.getValue() == null) { Validate.blankAlert("Contact"); return; }
        if(apptDatePicker.getValue() == null) { Validate.blankAlert("Date"); return; }
        if(startTime.getValue() == null) { Validate.blankAlert("Start Time"); return; }
        if(endTime.getValue() == null) { Validate.blankAlert("End Time"); return; }
        if(modApptType.getValue() == null) { Validate.blankAlert("Type"); return; }

        LocalDateTime newStart = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) startTime.getValue()));
        LocalDateTime newEnd = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) endTime.getValue()));

        int newCustID = newCust.getClientID();

        String newTitle = modApptTitle.getText();
        String newDesc = modApptDesc.getText();
        String newType = modApptType.getValue().toString();
        String newLoc = modApptLoc.getText();

        LocalDateTime newLastUpdate = LocalDateTime.now();

        if((Validate.isValidLength("Title", newTitle, 50)) &&
                (Validate.isValidLength("Description", newDesc, 50)) &&
                (Validate.isValidLength("Location", newLoc, 50))) {

            String sql = "UPDATE APPOINTMENTS SET Title = ?, Description = ?, Location = ?, Type = ?, " +
                    "\nStart = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, " +
                    "\nContact_ID = ?" +
                    "\nWHERE Appointment_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, newTitle);
            ps.setString(2, newDesc);
            ps.setString(3, newLoc);
            ps.setString(4, newType);
            ps.setTimestamp(5, Timestamp.valueOf(newStart));
            ps.setTimestamp(6, Timestamp.valueOf(newEnd));
            ps.setTimestamp(7, Timestamp.valueOf(newLastUpdate));
            ps.setString(8, newLastUpdatedBy);
            ps.setInt(9, newCustID);
            ps.setInt(10, newContID);
            ps.setInt(11, newApptID);
            int rowsEffected = ps.executeUpdate();

            if(rowsEffected == 1) {
                Validate.successAlert("appointment", "modified");
            }
            else {
                Validate.genAlert();
            }

            menuSelection("View All Appointments", actionEvent);
        }
    }

    /**This method loads the allAppt scene in the Graphical User Interface.*/
    public void onModApptCan(ActionEvent actionEvent) throws IOException {

        menuSelection("View All Appointments", actionEvent);
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }

    public void onModApptType(ActionEvent actionEvent) {
    }
}