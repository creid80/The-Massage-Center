package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**@author Carol Reid*/

/**This class creates Countries.*/
public class Countries {

    private int countryID;
    private String name;

    private static ObservableList<Countries> allcountries = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates a country object.
     * @param countryID The country ID.
     * @param name The country name.
     */
    public Countries(int countryID, String name) {

        this.countryID = countryID;
        this.name = name;
    }

    /**This method returns the country ID.
     * @return The country ID.*/
    public int getCountryID() { return countryID; }

    /**This method sets the country name.
     * @param name The country name.
     */
    public void setName(String name) { this.name = name; }

    /**This method returns the country name.
     * @return The country name.
     */
    public String getName() { return name; }

    /**This method queries the database for all countries, and adds each country object to the
     * ObservableList allcountries.
     */
    public static void countriesQuery() throws SQLException {

        String sql = "SELECT Country_ID, Country FROM COUNTRIES";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int countryID = rs.getInt("Country_ID");
            String country = rs.getString("Country");

            Countries newCountry = new Countries(countryID, country);

            allcountries.add(newCountry);
        }
    }

    /**This method returns the observable list allcountries.
     * @return The observable list allcountries.
     */
    public static ObservableList<Countries> getAllcountries() { return allcountries;}

    /**This method overrides the toString method and returns the country name.
     * @return The country name.*/
    @Override
    public String toString() { return name; }
}
