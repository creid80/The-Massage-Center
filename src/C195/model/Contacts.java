package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**@author Carol Reid*/

/**This class creates Contacts.*/
public class Contacts {

    private int contID;
    private String contName;

    private static ObservableList<Contacts> allConts = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates a contact object.
     * @param contID The contact ID.
     * @param contName The contact name.
     */
    public Contacts (int contID, String contName) {

        this.contID = contID;
        this.contName = contName;
    }

    /**This method returns the contact ID.
     * @return The contact ID.
     */
    public int getContID() { return contID; }

    /**This method returns the contact name.
     * @return The contact name.
     */
    public String getContName() { return contName; }

    /**This method queries the database for all contacts, and adds each contact object to the
     * ObservableList allConts.
     */
    public static void contactsQuery() throws SQLException {

        String sql = "SELECT Contact_ID, Contact_Name FROM CONTACTS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            int contactID = rs.getInt("Contact_ID");
            String contact = rs.getString("Contact_Name");

            Contacts newContact = new Contacts(contactID, contact);

            allConts.add(newContact);
        }
    }

    /**This method returns the observable list allConts.
     * @return The observable list allConts.
     */
    public static ObservableList<Contacts> getAllConts() {
        return allConts;
    }

    /**This method overrides the toString method and returns the contact name.
     * @return The contact name.
     */
    @Override
    public String toString() { return contName; }
}
