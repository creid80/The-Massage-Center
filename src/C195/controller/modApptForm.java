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
    public TextArea modApptNote;
    public ComboBox modApptType;


    private int newApptID = 0;
    private String newLastUpdatedBy = " ";
    private int newTherapistID = 0;

    private LocalDate setDate;
    private LocalTime setStart;
    private LocalTime setEnd;
    private String setType;

    private Appointments modAppt = allApptForm.getMAppointment();
    private ObservableList<Clients> allClients = Clients.getAllClients();
    private ObservableList<Users> allUsers = Users.getAllUsers();
    private ObservableList<Therapists> allTherapists = Therapists.getAllTherapists();
    private ObservableList<LocalTime> availETime = apptTime.getAvailEHours();
    private ObservableList<LocalTime> clientAppts = getClientApptHours();
    private static ObservableList<String> Types = FXCollections.observableArrayList();

    /**This method initializes the modAppt scene with the data passed through the modAppt object. It calls
     * methods to populate the client table, therapist drop box and start time combo box. It adds all
     * office hours to availSTime by calling copyOfficeHours. Then it uses the start time assigned to modAppt
     * to find other appointments that the client has that day by calling the fillClientApptHours method.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        popclientTable();

        String userN = "";
        int userID = modAppt.getUserID();
        for(Users u : Users.getAllUsers()) {
            if(userID == u.getUserID()) {
                userN = u.getUserName();
            }
        }
        userDropBox.setValue(userN);
        userDropBox.setItems(allUsers);

        poptherapistDropBox();
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
        modApptNote.setText(String.valueOf(modAppt.getNote()));

        newApptID = modAppt.getApptID();
    }

    /**This method populates the client table and selects the client who's ID matches the clientID
     * assigned to the modAppt object.
     */
    public void popclientTable() {

        if (allClients.isEmpty()) {
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            allClients.clear();
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        int selectedClient = modAppt.getClientID();
        for (Clients c : Clients.getAllClients() ) {
            if(c.getClientID() == selectedClient) {
                clientTable.getSelectionModel().select(c);
            }
        }

        clientTable.setItems(allClients);
        clientIDCol.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCol.setCellValueFactory(new PropertyValueFactory<>("postal"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divCol.setCellValueFactory(new PropertyValueFactory<>("divID"));

        clientTable.getSortOrder().add(clientIDCol);
        clientTable.sort();
    }

    /**This method populates the therapist combo box and selects the therapist assigned to the modAppt object.*/
    public void poptherapistDropBox() {

        String setTherapist = modAppt.getTherapistName();
        therapistDropBox.setItems(allTherapists);
        therapistDropBox.setValue(setTherapist);
    }

    /**This method sets the start time and end time combo boxes to the times assigned to the modAppt object.
     * Then it checks for available start times based on the therapist and date. If the assigned client has
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

        newTherapistID = modAppt.getTherapistID();

        boolean apptAvail = (apptTime.getApptSTimes(newTherapistID, setDate));

        if (!(clientAppts.isEmpty())) {
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

    /**This method populates the TypeCB combo box. */
    public void popTypeCB() {
       Types.clear();
           Types.addAll("Acupuncture", "Deep Tissue", "Hot Stone", "Reflexology", "Sports", "Swedish");
           modApptType.setItems(Types);
           modApptType.setValue(setType);
    }

    /**This method checks whether the selected client ID matches the modAppt object's assigned client ID.
     * If it does not match, the therapist, date, start time, and end time are all cleared.
     */
    public void onClientSelected(MouseEvent mouseEvent) {

        Clients newClient = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if(newClient.getClientID() != modAppt.getClientID()) {

            if (apptDatePicker.getValue() != null) {

                therapistDropBox.setItems(allTherapists);
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

    /**This method checks whether the selected therapist ID matches the modAppt objects's assigned therapist ID.
     * If it does not match, the date, start time, and end time are all cleared.
     */
    public void onTherapistDropBox(ActionEvent actionEvent) {

        Therapists selTherapist = (Therapists) therapistDropBox.getValue();
        newTherapistID = selTherapist.getTherapistID();

        if (apptDatePicker.getValue() != null) {

            apptDatePicker.setValue(null);
            availSHours.clear();
            clientAppts.clear();
            availETime.clear();
            startTime.setItems(null);
            startTime.setValue(null);
            endTime.setItems(null);
            endTime.setValue(null);
        }
    }

    /**This method gets the selected date and after clearing the available start times, client appointments,
     * and available end times it checks for available start times based on the therapist and date. After getting
     * the selected client, it checks for other appointments that day, and the times are removed from the list.
     * Next, the current appointment time is added to the list. If availStimes is empty but there are available
     * appointment times, add all office hours to availStimes. If the selected date and either the therapist or
     * client are the same as the original data assigned to the modAppt object, add the current appointment times.
     */
    public void onDatePicked(ActionEvent actionEvent) throws SQLException {

        boolean apptAvail;

        LocalDate pickedDate = apptDatePicker.getValue();
        if(pickedDate == null) {
            return;
        }

        if(!(availSHours.isEmpty())) {
            availSHours.clear();
            clientAppts.clear();
            availETime.clear();
        }

        apptAvail = (apptTime.getApptSTimes(newTherapistID, pickedDate));

        Clients modClient = (Clients) clientTable.getSelectionModel().getSelectedItem();

        fillClientApptHours(modClient.getClientID(), pickedDate);
        if(!(clientAppts.isEmpty())) {
            removeCAppt();
        }

        if((availSHours.isEmpty()) && (apptAvail)) {
            copyOfficeHours();
        }

        if((pickedDate.isEqual(setDate)) && ((modClient.getClientID() == modAppt.getClientID()) ||
                (newTherapistID == modAppt.getTherapistID()))) {

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

        Clients newClient = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if(newClient == null) { Validate.blankAlert("Client"); return; }
        if(userDropBox.getValue() == null) { Validate.blankAlert("User"); return; }
        if(therapistDropBox.getValue() == null) { Validate.blankAlert("Therapist"); return; }
        if(apptDatePicker.getValue() == null) { Validate.blankAlert("Date"); return; }
        if(startTime.getValue() == null) { Validate.blankAlert("Start Time"); return; }
        if(endTime.getValue() == null) { Validate.blankAlert("End Time"); return; }
        if(modApptType.getValue() == null) { Validate.blankAlert("Type"); return; }

        LocalDateTime newStart = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) startTime.getValue()));
        LocalDateTime newEnd = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) endTime.getValue()));

        int newClientID = newClient.getClientID();

        String newTitle = modApptTitle.getText();
        String newNote = modApptNote.getText();
        String newType = modApptType.getValue().toString();

        LocalDateTime newLastUpdate = LocalDateTime.now();

        if((Validate.isValidLength("Title", newTitle, 50)) &&
                (Validate.isValidLength("Note", newNote, 150))) {

            String sql = "UPDATE APPOINTMENTS SET Title = ?, Note = ?, Type = ?, " +
                    "\nStart = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Client_ID = ?, " +
                    "\nTherapist_ID = ?" +
                    "\nWHERE Appointment_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, newTitle);
            ps.setString(2, newNote);
            ps.setString(3, newType);
            ps.setTimestamp(4, Timestamp.valueOf(newStart));
            ps.setTimestamp(5, Timestamp.valueOf(newEnd));
            ps.setTimestamp(6, Timestamp.valueOf(newLastUpdate));
            ps.setString(7, newLastUpdatedBy);
            ps.setInt(8, newClientID);
            ps.setInt(9, newTherapistID);
            ps.setInt(10, newApptID);
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