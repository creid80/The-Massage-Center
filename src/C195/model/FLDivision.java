package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**@author Carol Reid*/

/**This class creates FLDivision.*/
public class FLDivision {

    private int divID;
    private String name;
    private int countryID;

    private static ObservableList<FLDivision> allFLDiv = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates a division object.
     * @param divID The division ID.
     * @param name The division name.
     * @param countryID The division country ID.
     */
    public FLDivision(int divID, String name, int countryID) {

        this.divID = divID;
        this.name = name;
        this.countryID = countryID;
    }

    /**This method returns the division ID.
     * @return The division ID.
     */
    public int getDivID() { return divID; }

    /**This method sets the division name.
     * @param name The division name.
     */
    public void setName(String name) { this.name = name; }

    /**This method returns the division name.
     * @return The division name.
     */
    public String getName() { return name; }

    /**This method returns the division country ID.
     * @return The division country ID.
     */
    public int getCountryID() { return countryID; }

    /**This method queries the database for all division, and adds each division object to the
     * ObservableList allFLDiv.
     */
    public static void divisionQuery(int selectedCountry) throws SQLException {

        String sql = "SELECT Division_ID, Division FROM FIRST_LEVEL_DIVISIONS WHERE Country_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, selectedCountry);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            int divID = rs.getInt("Division_ID");
            String name = rs.getString("Division");

            FLDivision newDiv = new FLDivision(divID, name, selectedCountry);

            allFLDiv.add(newDiv);
        }
    }

    /**This method returns the observable list allFLDiv.
     * @return The observable list allFLDiv.
     */
    public static ObservableList<FLDivision> getAllFLDiv() {return allFLDiv;}

    /**This method overrides the toString method and returns the division name.
     * @return The division name.
     */
    @Override
    public String toString() { return name; }
}
