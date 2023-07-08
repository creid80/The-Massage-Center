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
    public TextField addApptLoc;
    public ComboBox typeDropBox;

    private String newCreatedBy = " ";
    private int newUserID = 0;
    private int newContID = 0;

    public Clients currCust = new Clients();

    private ObservableList<Clients> allCust = Clients.getAllClients();
    private ObservableList<Users> allUsers = Users.getAllUsers();
    private ObservableList<Therapists> allCont = Therapists.getAllTheras();
    private ObservableList<LocalTime> availETime = apptTime.getAvailEHours();
    private ObservableList<LocalTime> custAppts = apptTime.getClientApptHours();
    private static ObservableList<String> Types = FXCollections.observableArrayList();

    /**This method initializes the addAppt scene and populates the table and combo boxes.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        popcustTable();
        userDropBox.setItems(allUsers);
        therapistDropBox.setItems(allCont);
        copyOfficeHours();
        popTypeCB();

        menu.setItems(C195.helper.Menu.menuItems);
    }

    /**This method checks whether allCust is populated, and if so, clears it and repopulates it.
     * Then it displays the list sorted by the customer ID.
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

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    public void popTypeCB() {
        Types.clear();
        Types.addAll("Acupuncture", "Deep Tissue", "Hot Stone", "Reflexology", "Sports","Swedish");
        typeDropBox.setItems(Types);
    }

    /**This method checks whether this is the first time a customer has been selected. If this is not the
     * first time, it clears the date and time combo boxes.
     */
    public void onClientSelected(MouseEvent mouseEvent) {

        if(currCust.getName().isBlank()) {
            currCust = (Clients) clientTable.getSelectionModel().getSelectedItem();
        }
        else {
            Clients newCust = (Clients) clientTable.getSelectionModel().getSelectedItem();

            if(currCust.getClientID() != newCust.getClientID()) {

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
            currCust = newCust;
        }
    }

    /**This method takes the selected user and assigns it's name and ID to the correct variables.*/
    public void onUserDropBox(ActionEvent actionEvent) {

        Users selectedUser = (Users) userDropBox.getSelectionModel().getSelectedItem();
        newCreatedBy = selectedUser.getUserName();
        newUserID = selectedUser.getUserID();
    }

    /**This method takes the selected contact and stores it. Then it checks whether the date picker has a value,
     * and if so, it clears the date picker and both time combo boxes.
     */
    public void onTherapistDropBox(ActionEvent actionEvent) {

        Therapists selContact = (Therapists) therapistDropBox.getValue();
        newContID = selContact.getTheraID();

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

    /**This method validates that a date and customer have been selected. Then it checks whether the start time
     * is empty, and if not it clears the start and end time combo boxes. Next it checks for appointment times
     * based on the contact and date selected. Then it checks for conflicting appointment times for the selected
     * customer, before populating the start combo box.
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
            Clients selCust = (Clients) clientTable.getSelectionModel().getSelectedItem();
            int sCustID = selCust.getClientID();
            fillClientApptHours(sCustID, pickedDate);
        }

        if(!(availSHours.isEmpty())) {
            availSHours.clear();
            availETime.clear();
        }

        apptAvail = (apptTime.getApptSTimes(newContID, pickedDate));

        if((availSHours.isEmpty()) && (apptAvail)) {
            copyOfficeHours();
        }
        else if((availSHours.isEmpty()) && (!(apptAvail))) {
            noAvailApptAlert();
        }

        if(!(custAppts.isEmpty())) {
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

        Clients newCust = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if(newCust == null) { Validate.blankAlert("Customer"); return; }
        if(userDropBox.getValue() == null) { Validate.blankAlert("User"); return; }
        if(therapistDropBox.getValue() == null) { Validate.blankAlert("Contact"); return; }
        if(apptDatePicker.getValue() == null) { Validate.blankAlert("Date"); return; }
        if(startTime.getValue() == null) { Validate.blankAlert("Start Time"); return; }
        if(endTime.getValue() == null) { Validate.blankAlert("End Time"); return; }

        LocalDateTime newStart = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) startTime.getValue()));
        LocalDateTime newEnd = estToLocal(LocalDateTime.of(apptDatePicker.getValue(), (LocalTime) endTime.getValue()));

        int newCustID = newCust.getClientID();

        String newTitle = addApptTitle.getText();
        String newDesc = addApptDesc.getText();
        String newType = typeDropBox.getValue().toString();
        String newLoc = addApptLoc.getText();

        LocalDateTime newCreateDate = LocalDateTime.now();
        LocalDateTime newLastUpdate = LocalDateTime.now();

        if((Validate.isValidLength("Title", newTitle, 50)) &&
                (Validate.isValidLength("Description", newDesc, 50)) &&
                (Validate.isValidLength("Type", newType, 50)) &&
                (Validate.isValidLength("Location", newLoc, 50))) {

            String sql = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Create_Date, " +
                    "Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES(?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, newTitle);
            ps.setString(2, newDesc);
            ps.setString(3, newLoc);
            ps.setString(4, newType);
            ps.setTimestamp(5, Timestamp.valueOf(newStart));
            ps.setTimestamp(6, Timestamp.valueOf(newEnd));
            ps.setTimestamp(7, Timestamp.valueOf(newCreateDate));
            ps.setString(8, newCreatedBy);
            ps.setTimestamp(9, Timestamp.valueOf(newLastUpdate));
            String newLastUpdatedBy = " ";
            ps.setString(10, newLastUpdatedBy);
            ps.setInt(11, newCustID);
            ps.setInt(12, newUserID);
            ps.setInt(13, newContID);
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