package C195.controller;

import C195.model.Clients;
import C195.model.Countries;
import C195.model.FLDivision;
import C195.utilities.JDBC;
import C195.utilities.Validate;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static C195.controller.loginForm.getUser;
import static C195.helper.Menu.menuSelection;

/**@author Carol Reid*/

/**This class is a controller for the modClient Graphical User Interface scene.*/
public class modClientForm implements Initializable {

    public ComboBox<String> menu;
    public String selected;

    public TextField modClientName;
    public TextField modClientPhone;
    public TextField modClientAddress;
    public TextField modClientCity;
    public TextField modClientCounty;
    public TextField modClientPostal;
    public ComboBox modClientCountry;
    public ComboBox modClientState;
    public TextField modClientID;

    private int newDivID = 0;

    private Clients modClient = allClientForm.getmClient();
    private ObservableList<Countries> allCountries = Countries.getAllcountries();
    private ObservableList<FLDivision> allDiv = FLDivision.getAllFLDiv();


    /**This method initializes the modClient scene with the data passed through the modClient object. It populates
     * the country combo box and selects the assigned country. Then it populates the division combo box based
     * on the assigned country, and selects the assigned division. Then it seperates the modClient address
     * into the appropriate textFields based on the assigned country.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        allCountries.clear();
        try {
            Countries.countriesQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        try {
            FLDivision.divisionQuery(modClient.getCountryID());
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        FilteredList<FLDivision> fDiv = new FilteredList<>(FLDivision.getAllFLDiv());
        fDiv.setPredicate(FLDivision -> FLDivision.getCountryID() == modClient.getCountryID());

        modClientID.setText(String.valueOf(modClient.getClientID()));
        modClientName.setText(String.valueOf(modClient.getName()));
        modClientPhone.setText(String.valueOf(modClient.getPhone()));

        String[] fullAddress = modClient.getAddress().split(", ");
        if(fullAddress.length == 1) {
            modClientAddress.setText(fullAddress[0]);
        }
        else {
            modClientAddress.setText(fullAddress[0]);
            modClientCity.setText(fullAddress[1]);
            if (modClient.getCountryName().equalsIgnoreCase("UK")) {
                modClientCounty.setText(fullAddress[2]);
            }
        }
        modClientPostal.setText(String.valueOf(modClient.getPostal()));
        modClientCountry.setItems(allCountries);
        modClientCountry.setValue(modClient.getCountryName());
        modClientState.setItems(fDiv);
        modClientState.setValue(modClient.getDivName());

    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    /**This method gets the selected country and clears the allDiv before repopulating it.*/
    public void onClientCountry(ActionEvent actionEvent) throws SQLException {

        Countries selected = (Countries) modClientCountry.getValue();
        if(selected != null) {

            int selectedID = selected.getCountryID();
            allDiv.clear();
            FLDivision.divisionQuery(selectedID);
            modClientState.setValue(allDiv.get(0));
            modClientState.setItems(allDiv);
        }
    }

    /**This method gets the selected division and stores it.*/
    public void onClientState(ActionEvent actionEvent) {

        FLDivision selected = (FLDivision) modClientState.getValue();
        if(selected != null) {
            newDivID = selected.getDivID();
        }
    }

    /**This method gets all of the data entered, checks if they are valid, and updates the client in the
     * database. An alert then notifies the user on whether the update was successful or not.
     */
    public void onModClientSave(ActionEvent actionEvent) throws SQLException, IOException {

        String newClientName = modClientName.getText();
        if (!(C195.utilities.Validate.isValidLength("Name", newClientName, 50))) { return; }

        String address = modClientAddress.getText();
        if(address.isEmpty()) { Validate.blankAlert("Address"); return; }

        String city = modClientCity.getText();
        if(city.isEmpty()) { Validate.blankAlert("City"); return; }

        String newClientAddress;
        if(modClientCountry.getValue().toString().equalsIgnoreCase("UK")) {
            String county = modClientCounty.getText();
            if(county.isEmpty()) { Validate.blankAlert("County"); return; }
            newClientAddress = address + ", " + city + ", " + county;
        }
        else {
            newClientAddress = address + ", " + city;
        }

        if (!(C195.utilities.Validate.isValidLength("Address", newClientAddress, 100))) { return; }

        String newClientPostal = modClientPostal.getText();
        if (!(C195.utilities.Validate.isValidLength("Postal Code", newClientPostal, 50))) { return; }

        String newClientPhone = modClientPhone.getText();
        if (!(C195.utilities.Validate.isValidLength("Phone Number", newClientPhone, 50))) { return; }

        if (newDivID == 0) { newDivID = modClient.getDivID();}

        LocalDateTime newClientLastUpdate = LocalDateTime.now();
        String newClientUpdatedBy = getUser();

        String sql = "UPDATE clients SET Client_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                     "\nDivision_ID = ?, Last_Update = ?, Last_Updated_By = ?" +
                     "WHERE Client_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, newClientName);
        ps.setString(2, newClientAddress);
        ps.setString(3, newClientPostal);
        ps.setString(4, newClientPhone);
        ps.setInt(5, newDivID);
        ps.setTimestamp(6, Timestamp.valueOf(newClientLastUpdate));
        ps.setString(7, newClientUpdatedBy);
        ps.setInt(8, modClient.getClientID());
        int rowsEffected = ps.executeUpdate();

        if (rowsEffected == 1) {
            Validate.successAlert("client", "updated");
        }
        else {
            Validate.genAlert();
        }

        menuSelection("View All Clients", actionEvent);
    }

    /**This method loads the allClient scene in the Graphical User Interface.*/
    public void onModClientCancel(ActionEvent actionEvent) throws IOException {
        menuSelection("View All Clients", actionEvent);
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }

}

