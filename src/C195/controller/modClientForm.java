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

/**This class is a controller for the modCust Graphical User Interface scene.*/
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

    private Clients modCust = allClientForm.getmCustomer();
    private ObservableList<Countries> allCountries = Countries.getAllcountries();
    private ObservableList<FLDivision> allDiv = FLDivision.getAllFLDiv();


    /**This method initializes the modCust scene with the data passed through the modCust object. It populates
     * the country combo box and selects the assigned country. Then it populates the division combo box based
     * on the assigned country, and selects the assigned division. Then it seperates the modCust address
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
            FLDivision.divisionQuery(modCust.getCountryID());
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        FilteredList<FLDivision> fDiv = new FilteredList<>(FLDivision.getAllFLDiv());
        fDiv.setPredicate(FLDivision -> FLDivision.getCountryID() == modCust.getCountryID());

        modClientID.setText(String.valueOf(modCust.getClientID()));
        modClientName.setText(String.valueOf(modCust.getName()));
        modClientPhone.setText(String.valueOf(modCust.getPhone()));

        String[] fullAddress = modCust.getAddress().split(", ");
        if(fullAddress.length == 1) {
            modClientAddress.setText(fullAddress[0]);
        }
        else {
            modClientAddress.setText(fullAddress[0]);
            modClientCity.setText(fullAddress[1]);
            if (modCust.getCountryName().equalsIgnoreCase("UK")) {
                modClientCounty.setText(fullAddress[2]);
            }
        }
        modClientPostal.setText(String.valueOf(modCust.getPostal()));
        modClientCountry.setItems(allCountries);
        modClientCountry.setValue(modCust.getCountryName());
        modClientState.setItems(fDiv);
        modClientState.setValue(modCust.getDivName());

    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    /**This method gets the selected country and clears the allDiv before repopulating it.*/
    public void onCustCountry(ActionEvent actionEvent) throws SQLException {

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
    public void onCustState(ActionEvent actionEvent) {

        FLDivision selected = (FLDivision) modClientState.getValue();
        if(selected != null) {
            newDivID = selected.getDivID();
        }
    }

    /**This method gets all of the data entered, checks if they are valid, and updates the customer in the
     * database. An alert then notifies the user on whether the update was successful or not.
     */
    public void onModClientSave(ActionEvent actionEvent) throws SQLException, IOException {

        String newCustName = modClientName.getText();
        if (!(C195.utilities.Validate.isValidLength("Name", newCustName, 50))) { return; }

        String address = modClientAddress.getText();
        if(address.isEmpty()) { Validate.blankAlert("Address"); return; }

        String city = modClientCity.getText();
        if(city.isEmpty()) { Validate.blankAlert("City"); return; }

        String newCustAddress;
        if(modClientCountry.getValue().toString().equalsIgnoreCase("UK")) {
            String county = modClientCounty.getText();
            if(county.isEmpty()) { Validate.blankAlert("County"); return; }
            newCustAddress = address + ", " + city + ", " + county;
        }
        else {
            newCustAddress = address + ", " + city;
        }

        if (!(C195.utilities.Validate.isValidLength("Address", newCustAddress, 100))) { return; }

        String newCustPostal = modClientPostal.getText();
        if (!(C195.utilities.Validate.isValidLength("Postal Code", newCustPostal, 50))) { return; }

        String newCustPhone = modClientPhone.getText();
        if (!(C195.utilities.Validate.isValidLength("Phone Number", newCustPhone, 50))) { return; }

        if (newDivID == 0) { newDivID = modCust.getDivID();}

        LocalDateTime newCustLastUpdate = LocalDateTime.now();
        String newCustUpdatedBy = getUser();

        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, " +
                     "\nDivision_ID = ?, Last_Update = ?, Last_Updated_By = ?" +
                     "WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, newCustName);
        ps.setString(2, newCustAddress);
        ps.setString(3, newCustPostal);
        ps.setString(4, newCustPhone);
        ps.setInt(5, newDivID);
        ps.setTimestamp(6, Timestamp.valueOf(newCustLastUpdate));
        ps.setString(7, newCustUpdatedBy);
        ps.setInt(8, modCust.getClientID());
        int rowsEffected = ps.executeUpdate();

        if (rowsEffected == 1) {
            Validate.successAlert("customer", "updated");
        }
        else {
            Validate.genAlert();
        }

        menuSelection("View All Clients", actionEvent);
    }

    /**This method loads the allCust scene in the Graphical User Interface.*/
    public void onModClientCancel(ActionEvent actionEvent) throws IOException {
        menuSelection("View All Clients", actionEvent);
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }

}

