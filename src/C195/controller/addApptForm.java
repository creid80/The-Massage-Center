package C195.controller;

import C195.model.Clients;
import C195.model.Therapists;
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
import static C195.utilities.Validate.noAvailApptAlert;
import static C195.utilities.Validate.noSelectionAlert;
import static C195.utilities.apptTime.*;

/**@author Carol Reid*/

/**This class is a controller for the addAppt Graphical User Interface scene.*/
public class addApptForm implements Initializable {

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
    public TextField addApptTitle;
    public TextField addApptDesc;

    public ComboBox typeDropBox;

    private String newCreatedBy = " ";
    private int newUserID = 0;
    private int newTherapistID = 0;

    public Clients currClient = new Clients();

    private ObservableList<Clients> allClients = Clients.getAllClients();
    private ObservableList<Users> allUsers = Users.getAllUsers();
    private ObservableList<Therapists> allTherapists = Therapists.getAllTherapists();
    private ObservableList<LocalTime> availETime = apptTime.getAvailEHours();
    private ObservableList<LocalTime> clientAppts = apptTime.getClientApptHours();
    private static ObservableList<String> Types = FXCollections.observableArrayList();

    /**This method initializes the addAppt scene and populates the table and combo boxes.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        popclientTable();
        userDropBox.setItems(allUsers);
        therapistDropBox.setItems(allTherapists);
        copyOfficeHours();
        popTypeCB();

        menu.setItems(C195.helper.Menu.menuItems);
    }

    /**This method checks whether allClients is populated, and if so, clears it and repopulates it.
     * Then it displays the list sorted by the client ID.
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

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    // NEED TO FINISH
    public void popTypeCB() {
        Types.clear();
        Types.addAll("Acupuncture", "Deep Tissue", "Hot Stone", "Reflexology", "Sports","Swedish");
        typeDropBox.setItems(Types);
    }

    /**This method checks whether this is the first time a client has been selected. If this is not the
     * first time, it clears the date and time combo boxes.
     */
    public void onClientSelected(MouseEvent mouseEvent) {

        if(currClient.getName().isBlank()) {
            currClient = (Clients) clientTable.getSelectionModel().getSelectedItem();
        }
        else {
            Clients newClient = (Clients) clientTable.getSelectionModel().getSelectedItem();

            if(currClient.getClientID() != newClient.getClientID()) {

                if (apptDatePicker.getValue() != null) {

                    apptDatePicker.setValue(null);
                    availSHours.clear();
                    availETime.clear();
                    startTime.setItems(null);
                    startTime.setValue(null);
                    endTime.setItems(null);
                    endTime.setValue(null);
                }
            }
            currClient = newClient;
        }
    }

    /**This method takes the selected user and assigns it's name and ID to the correct variables.*/
    public void onUserDropBox(ActionEvent actionEvent) {

        Users selectedUser = (Users) userDropBox.getSelectionModel().getSelectedItem();
        newCreatedBy = selectedUser.getUserName();
        newUserID = selectedUser.getUserID();
    }

    /**This method takes the selected therapist and stores it. Then it checks whether the date picker has a value,
     * and if so, it clears the date picker and both time combo boxes.
     */
    public void onTherapistDropBox(ActionEvent actionEvent) {

        Therapists selTherapist = (Therapists) therapistDropBox.getValue();
        newTherapistID = selTherapist.getTherapistID();

        if (apptDatePicker.getValue() != null) {

            apptDatePicker.setValue(null);
            availSHours.clear();
            availETime.clear();
            startTime.setItems(null);
            startTime.setValue(null);
            endTime.setItems(null);
            endTime.setValue(null);
        }
    }

    /**This method validates that a date and client have been selected. Then it checks whether the start time
     * is empty, and if not it clears the start and end time combo boxes. Next it checks for appointment times
     * based on the therapist and date selected. Then it checks for conflicting appointment times for the
     * selected client, before populating the start combo box.
     */
    public void onDatePicked(ActionEvent actionEvent) throws SQLException {

        boolean apptAvail;
        LocalDate pickedDate = apptDatePicker.getValue();

        if(pickedDate == null) {
            return;
        }

        if(clientTable.getSelectionModel().isEmpty()) {
            noSelectionAlert(" customer");
        }
        else {
            Clients selClient = (Clients) clientTable.getSelectionModel().getSelectedItem();
            int sClientID = selClient.getClientID();
            fillClientApptHours(sClientID, pickedDate);
        }

        if(!(availSHours.isEmpty())) {
            availSHours.clear();
            availETime.clear();
        }

        apptAvail = (apptTime.getApptSTimes(newTherapistID, pickedDate));

        if((availSHours.isEmpty()) && (apptAvail)) {
            copyOfficeHours();
        }
        else if((availSHours.isEmpty()) && (!(apptAvail))) {
            noAvailApptAlert();
        }

        if(!(clientAppts.isEmpty())) {
            removeCAppt();
        }

        startTime.setItems(availSHours);
    }

    /**This method checks whether the start time has been selected. Then it clears the end time list before
     * populating it. If the time of 21:45 is present in the list, then it adds the final available time 22:00
     */
    public void onStartTime(ActionEvent actionEvent) {


        LocalTime pickedStart = (LocalTime) startTime.getValue();
        if(pickedStart == null) {
            return;
        }

        availETime.clear();
        filterEOfficeHours(pickedStart);

        if((!(availETime.isEmpty())) && (availETime.get(availETime.size() - 1).equals(LocalTime.of(21,45)))) {
            availETime.add(LocalTime.of(22,00));
        }
        else if(pickedStart.equals(LocalTime.of(21, 45))) {
            availETime.add(LocalTime.of(22, 00));
        }

        endTime.setItems(availETime);
    }

    /**This method gets all of the selections, checks if they are valid, and inserts the new appointment
     * into the database. An alert then notifies the user on whether the insert was successful or not.
     */
    public void onAddAppointment(ActionEvent actionEvent) throws SQLException, IOException {

        Clients newClient = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if(newClient == null) { Validate.blankAlert("Client"); return; }
        if(userDropBox.getValue() == null) { Validate.blankAlert("User"); return; }
        if(therapistDropBox.getValue() == null) { Validate.blankAlert("Therapist"); return; }
        if(apptDatePicker.getValue() == null) { Validate.blankAlert("Date"); return; }
        if(startTime.getValue() == null) { Validate.blankAlert("Start Time"); return; }
        if(endTime.getValue() == null) { Validate.blankAlert("End Time"); return; }

        LocalDateTime newStart = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) startTime.getValue()));
        LocalDateTime newEnd = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) endTime.getValue()));

        int newCustID = newClient.getClientID();

        String newTitle = addApptTitle.getText();
        String newDesc = addApptDesc.getText();
        String newType = typeDropBox.getValue().toString();

        LocalDateTime newCreateDate = LocalDateTime.now();
        LocalDateTime newLastUpdate = LocalDateTime.now();

        if((Validate.isValidLength("Title", newTitle, 50)) &&
                (Validate.isValidLength("Description", newDesc, 150)) &&
                (Validate.isValidLength("Type", newType, 50))) {

            String sql = "INSERT INTO appointments(Title, Description, Type, Start, End, Create_Date, " +
                    "Created_By, Last_Update, Last_Updated_By, Client_ID, User_ID, Therapist_ID) VALUES(?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, newTitle);
            ps.setString(2, newDesc);
            ps.setString(3, newType);
            ps.setTimestamp(4, Timestamp.valueOf(newStart));
            ps.setTimestamp(5, Timestamp.valueOf(newEnd));
            ps.setTimestamp(6, Timestamp.valueOf(newCreateDate));
            ps.setString(7, newCreatedBy);
            ps.setTimestamp(8, Timestamp.valueOf(newLastUpdate));
            String newLastUpdatedBy = " ";
            ps.setString(9, newLastUpdatedBy);
            ps.setInt(10, newCustID);
            ps.setInt(11, newUserID);
            ps.setInt(12, newTherapistID);
            int rowsEffected = ps.executeUpdate();

            if(rowsEffected == 1) {
                Validate.successAlert("appointment", "added");
            }
            else {
                Validate.genAlert();
            }

            menuSelection("View All Appointments", actionEvent);
        }
    }

    /**This method loads the allAppt scene in the Graphical User Interface.*/
    public void onAddApptCan(ActionEvent actionEvent) throws IOException {

        menuSelection("View All Appointments", actionEvent);
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }
}