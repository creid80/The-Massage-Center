package C195.controller;

import C195.model.Countries;
import C195.model.FLDivision;
import C195.utilities.JDBC;
import C195.utilities.Validate;
import javafx.application.Platform;
import javafx.collections.ObservableList;
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

/**This class is a controller for the addCust Graphical User Interface scene.*/
public class addClientForm implements Initializable {

    public ComboBox<String> menu;
    public String selected;

    public TextField addClientName;
    public TextField addClientPhone;
    public TextField addClientAddress;
    public TextField addClientCity;
    public TextField addClientCounty;
    public ComboBox<Countries> addClientCountry;
    public ComboBox<FLDivision> addClientState;
    public TextField addClientPostal;

    private int newDivID = 0;

    private ObservableList<Countries> allCountries = Countries.getAllcountries();
    private ObservableList<FLDivision> allDiv = FLDivision.getAllFLDiv();


    /**This method initializes the addCust scene. It checks whether the country list is empty, and if not,
     * clears it before populating it.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        if (allCountries.isEmpty()) {
            try {
                Countries.countriesQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            allCountries.clear();
            try {
                Countries.countriesQuery();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        addClientCountry.setItems(allCountries);
    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    /**This method checks whether a country has been selected. Then it clears the division combo box, before
     * populating it with the correct divisions.
     */
    public void onClientCountry(ActionEvent actionEvent) throws SQLException {

        Countries selectedC = addClientCountry.getValue();
        if(selectedC != null) {

            int selectedID = selectedC.getCountryID();
            allDiv.clear();
            FLDivision.divisionQuery(selectedID);
            addClientState.setItems(allDiv);
        }
    }

    /**This method checks whether a division has been selected and assigns it to the correct variable.*/
    public void onClientState(ActionEvent actionEvent) {

        FLDivision selected = addClientState.getValue();
        if(selected != null) {
            newDivID = selected.getDivID();
        }
    }

    /**This method checks that the address is valid and formats the address according to the country selection.
     * Next, if all input is valid, it inserts the new customer into the database. Then an alert notifies
     * the user that the insert was or was not successful.
     */
    public void onClientSave(ActionEvent actionEvent) throws SQLException, IOException {

        String address = addClientAddress.getText();
        if(address.isEmpty()) { Validate.blankAlert("Address"); return; }

        String city = addClientCity.getText();
        if(city.isEmpty()) { Validate.blankAlert("City"); return; }

        String newCustAddress;
        if(addClientCountry.getValue().getName().equalsIgnoreCase("UK")) {
            String county = addClientCounty.getText();
            if(county.isEmpty()) { Validate.blankAlert("County"); return; }
            newCustAddress = address + ", " + city + ", " + county;
        }
        else {
            newCustAddress = address + ", " + city;
        }

        String newCustName = addClientName.getText();
        String newCustPostal = addClientPostal.getText();
        String newCustPhone = addClientPhone.getText();

        LocalDateTime newCustCreateDate = LocalDateTime.now();
        String newCustCreatedBy = getUser();
        LocalDateTime newCustLastUpdate = LocalDateTime.now();


        if ((Validate.isValidLength("Name", newCustName, 50)) &&
                (Validate.isValidLength("Address", newCustAddress, 100)) &&
                (Validate.isValidLength("Postal Code", newCustPostal, 50)) &&
                (Validate.isValidLength("Phone Number", newCustPhone, 50))) {

            if (newDivID == 0) { Validate.blankAlert("Country and State/Province"); return;}

            String sql = "INSERT INTO customers(Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By," +
                    "Last_Update, Last_Updated_By, Division_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, newCustName);
            ps.setString(2, newCustAddress);
            ps.setString(3, newCustPostal);
            ps.setString(4, newCustPhone);
            ps.setTimestamp(5, Timestamp.valueOf(newCustCreateDate));
            ps.setString(6, newCustCreatedBy);
            ps.setTimestamp(7, Timestamp.valueOf(newCustLastUpdate));
            String newCustUpdatedBy = " ";
            ps.setString(8, newCustUpdatedBy);
            ps.setInt(9, newDivID);
            int rowsEffected = ps.executeUpdate();
            System.out.println(rowsEffected);

            if(rowsEffected == 1) {
                Validate.successAlert("customer", "added");
            }
            else {
                Validate.genAlert();
            }

            menuSelection("View All Clients", actionEvent);
        }
    }

    /**This method loads the allCust scene in the Graphical User Interface.*/
    public void onClientCancel(ActionEvent actionEvent) throws IOException {
        menuSelection("View All Clients", actionEvent);
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }
}
