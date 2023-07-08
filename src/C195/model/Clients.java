package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**@author Carol Reid*/

/**This class creates Clients.*/
public class Clients {

    private int clientID;
    private String name;
    private String address;
    private String postal;
    private String phone;
    private int divID;
    private String divName;
    private int countryID;
    private String countryName;

    private static ObservableList<Clients> allClients = FXCollections.observableArrayList();
    private static ObservableList<Clients> searchedClientName = FXCollections.observableArrayList();

    /**This method is the default constructor. This method creates a client object with default values.*/
    public Clients() {

        clientID = 0;
        name = " ";
        address = " ";
        postal = " ";
        phone = " ";
        divID = 0;
        divName = " ";
        countryID = 0;
        countryName = " ";
    }

    /**This method is a constructor. This method creates a client object.
     * @param clientID The client ID.
     * @param name The client name.
     * @param address The client address.
     * @param postal The client postal code.
     * @param phone The client phone number.
     * @param divID The client division ID.
     * @param divName The client division name.
     * @param countryID The client country ID.
     * @param countryName The client name.
     */
    public Clients(int clientID, String name, String address, String postal, String phone, int divID,
                   String divName, int countryID, String countryName) {

        this.clientID = clientID;
        this.name = name;
        this.address = address;
        this.postal = postal;
        this.phone = phone;
        this.divID = divID;
        this.divName = divName;
        this.countryID = countryID;
        this.countryName = countryName;
    }


    /**This method sets the client ID.
     * @param clientID The client ID.
     */
    public void setClientID(int clientID) { this.clientID = clientID; }

    /**This method returns the client ID.
     * @return The client ID.
     */
    public int getClientID() { return clientID; }

    /**This method sets the client name.
     * @param name The client name.
     */
    public void setName(String name) { this.name = name; }

    /**This method returns the client name.
     * @return The client name.
     */
    public String getName() { return name; }

    /**This method sets the client phone number.
     * @param phone The client phone number.
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**This method returns the client phone number.
     * @return The client phone number.
     */
    public String getPhone() { return phone; }

    /**This method sets the client address.
     * @param address The client address.
     */
    public void setAddress(String address) { this.address = address; }

    /**This method returns the client address.
     * @return The client address.
     */
    public String getAddress() { return address; }

    /**This method sets the client postal code.
     * @param postal The client postal code.
     */
    public void setPostal(String postal) { this.postal = postal; }

    /**This method returns the client postal code.
     * @return The client postal code.
     */
    public String getPostal() { return postal; }

    /**This method returns the client country ID.
     * @return The client country ID.
     */
    public int getCountryID() { return countryID; }

    /**This method returns the client country name.
     * @return The client country name.
     */
    public String getCountryName() { return countryName; }

    /**This method returns the client division ID.
     * @return The client division ID.
     */
    public int getDivID() { return divID; }

    /**This method returns the client division name.
     * @return The client division name.
     */
    public String getDivName() { return divName; }

    /**This method queries the database for all clients, and adds each client object to the
     * ObservableList allClient.
     */
    public static void tableQueryC() throws SQLException {

        String sql = "SELECT c.Client_ID, c.Client_Name, c.Address, c.Postal_Code, " +
                "\nc.Phone, c.Division_ID, fld.Division, fld.Country_ID, cs.Country" +
        "\nFROM clients AS c" +
        "\nJOIN" +
        "\nfirst_level_divisions AS fld" +
        "\nON c.Division_ID = fld.Division_ID" +
        "\nJOIN" +
        "\ncountries AS cs" +
        "\nON fld.Country_ID = cs.Country_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int clientID = rs.getInt("Client_ID");
            String name = rs.getString("Client_Name");
            String address = rs.getString("Address");
            String postal = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            int divID = rs.getInt("Division_ID");
            String divName = rs.getString("Division");
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");

            Clients newClient = new Clients(clientID, name, address, postal, phone, divID, divName, countryID, countryName);

            allClients.add(newClient);
        }
    }

    /**This method returns the observable list allClients.
     * @return The observable list allClients.
     */
    public static ObservableList<Clients> getAllClients() {
        return allClients;
    }


// NEED TO FINISH DOCUMENTING
    public static ObservableList<Clients> lookupClient(String productName) {

        for (Clients client : allClients) {
            if (client.getName().contains(productName)) {
                searchedClientName.add(client);
            }
        }
        return searchedClientName;
    }

    // NEED TO FINISH DOCUMENTING
    public static Clients lookupClient(int clientId) {

        for (Clients searchId : allClients) {
            if (searchId.getClientID() == clientId) {
                return searchId;
            }
        }
        return null;
    }
}
