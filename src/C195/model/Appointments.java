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
    private String note;
    private String therapistName;
    private String type;
    private LocalDateTime startLDT;
    private LocalDateTime endLDT;
    private int clientID;
    private int userID;
    private int therapistID;

    private static ObservableList<Appointments> allAppts = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates an appointment object.
     * @param apptID The objects ID.
     * @param title The objects title.
     * @param note The objects note.
     * @param therapistName The objects therapist name.
     * @param type The objects type.
     * @param startLDT The objects start time.
     * @param endLDT The objects end time.
     * @param clientID The objects client ID.
     * @param userID The objects user ID.
     * @param therapistID The objects therapist ID.
     */
    public Appointments(int apptID, String title, String note, String therapistName, String type, LocalDateTime startLDT, LocalDateTime endLDT,
                        int clientID, int userID, int therapistID) throws SQLException {

        this.apptID = apptID;
        this.title = title;
        this.note = note;
        this.therapistName = therapistName;
        this.type = type;
        this.startLDT = startLDT;
        this.endLDT = endLDT;
        this.clientID = clientID;
        this.userID = userID;
        this.therapistID = therapistID;
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

    /**This method returns the note.
     * @return The note.
     */
    public String getNote() { return note; }

    /**This method returns the therapist name.
     * @return The therapist name.
     */
    public String getTherapistName() { return therapistName; }

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

    /**This method sets the client ID.
     * @param clientID The client ID.
     */
    public void setClientID(int clientID) { this.clientID = clientID; }

    /**This method returns the client ID.
     * @return The client ID.
     */
    public int getClientID() { return clientID; }

    /**This method gets the user ID.
     * @return The user ID.
     */
    public int getUserID() { return userID; }

    /**This method gets the therapist ID.
     * @return The therapist ID.
     */
    public int getTherapistID() { return therapistID; }

    /**This method queries the database for all appointments, and adds each appointment object to the
     * ObservableList allAppts.
     */
    public static void tableQueryA() throws SQLException {

        String sql = "SELECT appointments.Appointment_ID, appointments.Title, appointments.Note, \n" +
                "therapists.Therapist_Name, appointments.Type, appointments.Start, \n" +
                "appointments.End, appointments.Clients_ID, appointments.User_ID , contacts.Therapist_ID\n" +
                "FROM appointments, therapists\n" +
                "WHERE therapists.Therapist_ID = appointments.Therapist_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while(rs.next()){
            int apptId = rs.getInt("Appointment_ID");
            String title = rs.getString("Title");
            String note = rs.getString("Note");
            String therapistName = rs.getString("Therapist_Name");
            String type = rs.getString("Type");
            Timestamp start = rs.getTimestamp("Start");
            LocalDateTime startLDT = start.toLocalDateTime();
            Timestamp end = rs.getTimestamp("End");
            LocalDateTime endLDT = end.toLocalDateTime();
            int clientID = rs.getInt("Client_ID");
            int userID = rs.getInt("User_ID");
            int therapistID = rs.getInt("Therapist_ID");

            Appointments newAppt = new Appointments(apptId, title, note, therapistName, type, startLDT, endLDT, clientID, userID, therapistID);

            allAppts.add(newAppt);
        }
    }

    /**This method returns the observable list allAppts.
     * @return The observable list allAppts.
     */
    public static ObservableList<Appointments> getAllAppts() { return allAppts; }
}