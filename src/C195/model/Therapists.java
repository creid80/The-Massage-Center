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

    private int therapistID;
    private String therapistName;

    private static ObservableList<Therapists> allTherapists = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates a therapist object.
     * @param therapistID The therapist ID.
     * @param therapistName The therapist name.
     */
    public Therapists(int therapistID, String therapistName) {

        this.therapistID = therapistID;
        this.therapistName = therapistName;
    }

    /**This method returns the therapist ID.
     * @return The therapist ID.
     */
    public int getTherapistID() { return therapistID; }

    /**This method returns the therapist name.
     * @return The therapist name.
     */
    public String getTherapistName() { return therapistName; }

    /**This method queries the database for all therapists, and adds each therapist object to the
     * ObservableList allTherapists.
     */
    public static void therapistsQuery() throws SQLException {

        String sql = "SELECT Therapist_ID, Therapist_Name FROM THERAPISTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            int therapistID = rs.getInt("Therapist_ID");
            String therapist = rs.getString("Therapist_Name");

            Therapists newTherapist = new Therapists(therapistID, therapist);

            allTherapists.add(newTherapist);
        }
    }

    /**This method returns the observable list allTherapists.
     * @return The observable list allTheras.
     */
    public static ObservableList<Therapists> getAllTherapists() {
        return allTherapists;
    }

    /**This method overrides the toString method and returns the therapist name.
     * @return The therapist name.
     */
    @Override
    public String toString() { return therapistName; }
}
