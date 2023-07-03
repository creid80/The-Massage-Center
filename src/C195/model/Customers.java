package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**@author Carol Reid*/

/**This class creates Customers.*/
public class Customers {

    private int custID;
    private String name;
    private String address;
    private String postal;
    private String phone;
    private int divID;
    private String divName;
    private int countryID;
    private String countryName;

    private static ObservableList<Customers> allCust = FXCollections.observableArrayList();

    /**This method is the default constructor. This method creates a customer object with default values.*/
    public Customers() {

        custID = 0;
        name = " ";
        address = " ";
        postal = " ";
        phone = " ";
        divID = 0;
        divName = " ";
        countryID = 0;
        countryName = " ";
    }

    /**This method is a constructor. This method creates a customer object.
     * @param custID The customer ID.
     * @param name The customer name.
     * @param address The customer address.
     * @param postal The customer postal code.
     * @param phone The customer phone number.
     * @param divID The customer division ID.
     * @param divName The customer division name.
     * @param countryID The customer country ID.
     * @param countryName The customer name.
     */
    public Customers(int custID, String name, String address, String postal, String phone, int divID,
                     String divName, int countryID, String countryName) {

        this.custID = custID;
        this.name = name;
        this.address = address;
        this.postal = postal;
        this.phone = phone;
        this.divID = divID;
        this.divName = divName;
        this.countryID = countryID;
        this.countryName = countryName;
    }


    /**This method sets the customer ID.
     * @param custID The customer ID.
     */
    public void setCustID(int custID) { this.custID = custID; }

    /**This method returns the customer ID.
     * @return The customer ID.
     */
    public int getCustID() { return custID; }

    /**This method sets the customer name.
     * @param name The customer name.
     */
    public void setName(String name) { this.name = name; }

    /**This method returns the customer name.
     * @return The customer name.
     */
    public String getName() { return name; }

    /**This method sets the customer phone number.
     * @param phone The customer phone number.
     */
    public void setPhone(String phone) { this.phone = phone; }

    /**This method returns the customer phone number.
     * @return The customer phone number.
     */
    public String getPhone() { return phone; }

    /**This method sets the customer address.
     * @param address The customer address.
     */
    public void setAddress(String address) { this.address = address; }

    /**This method returns the customer address.
     * @return The customer address.
     */
    public String getAddress() { return address; }

    /**This method sets the customer postal code.
     * @param postal The customer postal code.
     */
    public void setPostal(String postal) { this.postal = postal; }

    /**This method returns the customer postal code.
     * @return The customer postal code.
     */
    public String getPostal() { return postal; }

    /**This method returns the customer country ID.
     * @return The customer country ID.
     */
    public int getCountryID() { return countryID; }

    /**This method returns the customer country name.
     * @return The customer country name.
     */
    public String getCountryName() { return countryName; }

    /**This method returns the customer division ID.
     * @return The customer division ID.
     */
    public int getDivID() { return divID; }

    /**This method returns the customer division name.
     * @return The customer division name.
     */
    public String getDivName() { return divName; }

    /**This method queries the database for all customers, and adds each customer object to the
     * ObservableList allCust.
     */
    public static void tableQueryC() throws SQLException {

        String sql = "SELECT c.Customer_ID, c.Customer_Name, c.Address, c.Postal_Code, " +
                "\nc.Phone, c.Division_ID, fld.Division, fld.Country_ID, cs.Country" +
        "\nFROM customers AS c" +
        "\nJOIN" +
        "\nfirst_level_divisions AS fld" +
        "\nON c.Division_ID = fld.Division_ID" +
        "\nJOIN" +
        "\ncountries AS cs" +
        "\nON fld.Country_ID = cs.Country_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int custID = rs.getInt("Customer_ID");
            String name = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String postal = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            int divID = rs.getInt("Division_ID");
            String divName = rs.getString("Division");
            int countryID = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");

            Customers newCust = new Customers(custID, name, address, postal, phone, divID, divName, countryID, countryName);

            allCust.add(newCust);
        }
    }

    /**This method returns the observable list allCust.
     * @return The observable list allCust.
     */
    public static ObservableList<Customers> getAllCust() {
        return allCust;
    }
}
