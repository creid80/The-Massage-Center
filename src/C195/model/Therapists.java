package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**@author Carol Reid*/

/**This class creates Therapists.*/
public class Therapists {

    private int theraID;
    private String theraName;

    private static ObservableList<Therapists> allTheras = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates a therapist object.
     * @param theraID The therapist ID.
     * @param theraName The therapist name.
     */
    public Therapists(int theraID, String theraName) {

        this.theraID = theraID;
        this.theraName = theraName;
    }

    /**This method returns the therapist ID.
     * @return The therapist ID.
     */
    public int getTheraID() { return theraID; }

    /**This method returns the therapist name.
     * @return The therapist name.
     */
    public String getTheraName() { return theraName; }

    /**This method queries the database for all therapists, and adds each therapist object to the
     * ObservableList allTheraps.
     */
    public static void therapistsQuery() throws SQLException {

        String sql = "SELECT Therapist_ID, Therapist_Name FROM THERAPISTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            int therapistID = rs.getInt("Therapist_ID");
            String therapist = rs.getString("Therapist_Name");

            Therapists newTherapist = new Therapists(therapistID, therapist);

            allTheras.add(newTherapist);
        }
    }

    /**This method returns the observable list allTheras.
     * @return The observable list allTheras.
     */
    public static ObservableList<Therapists> getAllTheras() {
        return allTheras;
    }

    /**This method overrides the toString method and returns the therapist name.
     * @return The therapist name.
     */
    @Override
    public String toString() { return theraName; }
}
