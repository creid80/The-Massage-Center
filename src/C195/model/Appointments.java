package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**@author Carol Reid*/

/**This class creates Appointments.*/
public class Appointments {

    private int apptID;
    private String title;
    private String desc;
    private String loc;
    private String contName;
    private String type;
    private LocalDateTime startLDT;
    private LocalDateTime endLDT;
    private int custID;
    private int userID;
    private int contID;

    private static ObservableList<Appointments> allAppts = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates an appointment object.
     * @param apptID The objects ID.
     * @param title The objects title.
     * @param desc The objects description.
     * @param loc The objects location.
     * @param contName The objects contact name.
     * @param type The objects type.
     * @param startLDT The objects start time.
     * @param endLDT The objects end time.
     * @param custID The objects customer ID.
     * @param userID The objects user ID.
     * @param contID The objects contact ID.
     */
    public Appointments(int apptID, String title, String desc, String loc, String contName,String type, LocalDateTime startLDT, LocalDateTime endLDT,
                        int custID, int userID, int contID) throws SQLException {

        this.apptID = apptID;
        this.title = title;
        this.desc = desc;
        this.loc = loc;
        this.contName = contName;
        this.type = type;
        this.startLDT = startLDT;
        this.endLDT = endLDT;
        this.custID = custID;
        this.userID = userID;
        this.contID = contID;
    }

    /**This method returns the appointment ID.
     * @return The appointment ID.
     */
    public int getApptID() { return apptID; }

    /**This method sets the title.
     * @param title The title.
     */
    public void setTitle(String title) { this.title = title; }

    /**This method returns the title.
     * @return The title.
     */
    public String getTitle() { return title; }

    /**This method returns the description.
     * @return The description.
     */
    public String getDesc() { return desc; }

    /**This method returns the location.
     * @return The location.
     */
    public String getLoc() { return loc; }

    /**This method returns the contact name.
     * @return The contact name.
     */
    public String getContName() { return contName; }

    /**This method sets the type.
     * @param type The type.
     */
    public void setType(String type) { this.type = type; }

    /**This method returns the type.
     * @return The type.
     */
    public String getType() { return type; }

    /**This method returns the start LocalDateTime.
     * @return The start LocalDateTime.
     */
    public LocalDateTime getStartLDT() { return startLDT; }

    /**This method returns the end LocalDateTime.
     * @return The end LocalDateTime.
     */
    public LocalDateTime getEndLDT() { return endLDT; }

    /**This method sets the customer ID.
     * @param custID The customer ID.
     */
    public void setCustID(int custID) { this.custID = custID; }

    /**This method returns the customer ID.
     * @return The customer ID.
     */
    public int getCustID() { return custID; }

    /**This method gets the user ID.
     * @return The user ID.
     */
    public int getUserID() { return userID; }

    /**This method gets the contact ID.
     * @return The contact ID.
     */
    public int getContID() { return contID; }

    /**This method queries the database for all appointments, and adds each appointment object to the
     * ObservableList allAppts.
     */
    public static void tableQueryA() throws SQLException {

        String sql = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Description, \n" +
                "appointments.Location, contacts.Contact_Name, appointments.Type, appointments.Start, \n" +
                "appointments.End, appointments.Customer_ID, appointments.User_ID , contacts.Contact_ID\n" +
                "FROM appointments, contacts\n" +
                "WHERE contacts.Contact_ID = appointments.Contact_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            int apptId = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String desc = rs.getString("Description");
            String loc = rs.getString("Location");
            String contName = rs.getString("Contact_Name");
            String type = rs.getString("Type");
            Timestamp start = rs.getTimestamp("Start");
            LocalDateTime startLDT = start.toLocalDateTime();
            Timestamp end = rs.getTimestamp("End");
            LocalDateTime endLDT = end.toLocalDateTime();
            int custID = rs.getInt("Customer_ID");
            int userID = rs.getInt("User_ID");
            int contID = rs.getInt("Contact_ID");

            Appointments newAppt = new Appointments(apptId, title, desc, loc, contName, type, startLDT, endLDT, custID, userID, contID);

            allAppts.add(newAppt);
        }
    }

    /**This method returns the observable list allAppts.
     * @return The observable list allAppts.
     */
    public static ObservableList<Appointments> getAllAppts() { return allAppts; }
}