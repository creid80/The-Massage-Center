package C195.utilities;

import C195.model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.sql.*;
import java.time.*;
import java.util.Collections;

/**@author Carol Reid*/

/**This class contains methods needed to populate the start and end appointment time combo boxes.*/
public class apptTime {

    public static ObservableList<LocalTime> officeHours = FXCollections.observableArrayList();
    public static ObservableList<LocalTime> availSHours = FXCollections.observableArrayList();
    private static ObservableList<LocalTime> availEHours = FXCollections.observableArrayList();
    private static ObservableList<LocalTime> clientApptHours = FXCollections.observableArrayList();


    /**This method adds all possible starting times within the open business hours to the observable list
     * officeHours.
     */
    public static void fillOfficeHours() {

        for (int i = 8; i < 22; ++i) {

            LocalTime newTime1 = LocalTime.of(i, 00);
            LocalTime newTime2 = LocalTime.of(i, 15);
            LocalTime newTime3 = LocalTime.of(i, 30);
            LocalTime newTime4 = LocalTime.of(i, 45);
            officeHours.addAll(newTime1, newTime2, newTime3, newTime4);
        }
    }

    /**This method clears clientApptHours if it's not empty. Then it filters appointments by the selected
     * client ID, and that list is filtered by the selected date. The appointment times are then converted
     * to Eastern Standard Time, before they are compared to officeHours times and added to the
     * clientApptHours observable list.
     * @param clientID The selected client ID.
     * @param startTime The selected date.
     * There is a lambda expressions within this method:
     * Appointments -> Appointments.getClientID() == clientID
     * Sets the filter predicate for fList.
     * For every appointment it checks whether the clientID is the same as the variable clientID.
     */
    public static void fillClientApptHours(int clientID, LocalDate startTime) {

        if(!(clientApptHours.isEmpty())) {
            clientApptHours.clear();
        }

        FilteredList<Appointments> fList = new FilteredList<>(Appointments.getAllAppts());
        fList.setPredicate(Appointments -> Appointments.getClientID() == clientID);

        FilteredList<Appointments> fList2 = new FilteredList<>(fList);
        fList2.setPredicate(Appointments -> Appointments.getStartLDT().toLocalDate().isEqual(startTime));

        for(Appointments a : fList2) {
             for(LocalTime lt : officeHours){

                LocalTime sTime = localToEST(a.getStartLDT()).toLocalTime();
                LocalTime eTime = localToEST(a.getEndLDT()).toLocalTime();

                if((lt.equals(sTime)) || ((lt.isAfter(sTime)) && (lt.isBefore(eTime)))) {
                    clientApptHours.add(lt);
                }
            }
        }
    }

    /**This method returns the observable list clientApptHours.
     * @return The observable list clientApptHours.
     */
    public static ObservableList<LocalTime> getClientApptHours() { return clientApptHours; }

    /**This method returns the observable list availEHours.
     * @return The observable list availEHours.
     */
    public static ObservableList<LocalTime> getAvailEHours() { return availEHours; }

    /**This method queries the database for all start and end times of appointments with the selected
     * therapist ID. Each time is converted from UTC to EST before being compared to the selected date.
     * Then if filterSOfficeHours is true and it is the last appointment provided by the query, it returns
     * true indicating that there are available appointments. If filterSOfficeHours is false
     * and it is the last appointment provided by the query, it returns false, indicating that there are no
     * available appointments. Otherwise, if no appointments are returned by the query, it returns true,
     * indicating that there are available appointment times.
     * @param newTherapistID The selected therapist ID.
     * @param newLocalDate The selected date.
     * @return True if appointment times are available, False if there are no times available.
     */
    public static boolean getApptSTimes(int newTherapistID, LocalDate newLocalDate) throws SQLException {

        String sql = "SELECT Start, End FROM appointments WHERE Therapist_ID = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, newTherapistID);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Timestamp startTime = rs.getTimestamp("Start");
            LocalDateTime startLocal = localToEST(startTime.toLocalDateTime());
            LocalTime startEST = startLocal.toLocalTime();
            Timestamp endTime = rs.getTimestamp("End");
            LocalDateTime endLocal = localToEST(endTime.toLocalDateTime());
            LocalTime endEST = endLocal.toLocalTime();

            if (newLocalDate.compareTo(startLocal.toLocalDate()) == 0) {
                if(filterSOfficeHours(startEST, endEST)) {
                    if(rs.isLast()) {
                        return true;
                    }
                }
                else {
                    if(rs.isLast()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**This method converts localLDT from the users default time zone to Eastern Standard Time.
     * @param localLDT The time that will be converted.
     * @return The time after being converted to EST.
     */
    public static LocalDateTime localToEST (LocalDateTime localLDT) {

        ZonedDateTime newLocalZDT = localLDT.atZone(ZoneId.systemDefault());
        ZonedDateTime newESTZDT = newLocalZDT.withZoneSameInstant(ZoneId.of("US/Eastern"));

        return newESTZDT.toLocalDateTime();
    }

    /**This method converts estLDT from Eastern Standard Time to the users default time zone.
     * @param estLDT The time that will be converted.
     * @return The time after being converted to the users default time zone.
     */
    public static LocalDateTime estToLocal (LocalDateTime estLDT) {

        ZonedDateTime newESTZDT = estLDT.atZone(ZoneId.of("US/Eastern"));
        ZonedDateTime newLocalZDT = newESTZDT.withZoneSameInstant(ZoneId.systemDefault());

        return newLocalZDT.toLocalDateTime();
    }

    /**This method populates availSHours with all open office hours if it is empty. Then it compares the
     * startLT and endLT times with the hours in officeHours. If the time matches the startLT or is between
     * the startLT and endLT, it is removed from the availSHours list. If availSHours and custApptHours are
     * empty there are no appointments available and returns false, otherwise it returns true.
     * @param startLT The appointment start time.
     * @param endLT The appointment end time.
     * @return True if there are available appointments, false if there are not.
     */
    public static boolean filterSOfficeHours (LocalTime startLT, LocalTime endLT) {

        if (availSHours.isEmpty()) {
            copyOfficeHours();
        }

        for (LocalTime officeHour : officeHours) {

            if (((officeHour.equals(startLT)) || ((startLT.isBefore(officeHour)) && (endLT.isAfter(officeHour))))) {

                for (int i = 0; i < officeHours.size(); ++i) {

                    if (i < availSHours.size()) {

                        if (availSHours.get(i).equals(officeHour)) {
                            availSHours.remove(i);
                        }
                    }
                    else {
                        if((availSHours.isEmpty()) && (clientApptHours.isEmpty())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**This method gets all office hours if the availSHours list is empty but the clientApptHours list is not.
     * Then it removes the times that matches the clientApptHours from availSHours.
     */
    public static void removeCAppt() {

        if((availSHours.isEmpty()) && (!(clientApptHours.isEmpty()))) {
            copyOfficeHours();
        }

        for(LocalTime lT : clientApptHours) {
            for(int i = 0; i < officeHours.size(); i++) {
                if(i < availSHours.size()) {
                    if(lT.equals(availSHours.get(i))) {
                        availSHours.remove(i);
                    }
                }
            }
        }
    }

    /**This method clears the observable list availSHours if it is populated, before adding all officeHours.*/
    public static void copyOfficeHours() {

        if (!(availSHours.isEmpty())) {
            availSHours.clear();
        }

        availSHours.addAll(officeHours);
    }

    /**This method clears the observable list availEHours if it is populated. Then it filters the officeHours
     * list and the availSHours list, by the pickedStart time. It then iterates through each list and if the
     * item's at the same index match, they are added to the availEHours list. Otherwise, if the availSHours
     * filtered list is smaller than the officeHours filtered list and the item's at the same index do not
     * match, the last accessed officeHour is added before breaking out of the method.
     * @param pickedStart The selected start time.
     */
    public static void filterEOfficeHours (LocalTime pickedStart) {

        if(!(availEHours.isEmpty())) {
            availEHours.clear();
        }

        FilteredList<LocalTime> copyOH = new FilteredList<>(officeHours);
        copyOH.setPredicate(LocalTime -> LocalTime.isAfter(pickedStart));

        FilteredList<LocalTime> copyASH = new FilteredList<>(availSHours);
        copyASH.setPredicate(LocalTime -> LocalTime.isAfter(pickedStart));

        for (int i = 0; i < 1; ++i) {
            for (int j = 0; j < copyASH.size(); ++j) {
                if (copyASH.get(j) == copyOH.get(j)) {
                    availEHours.add(copyASH.get(j));
                }
                else if ((copyASH.size() < copyOH.size()) && (copyASH.get(j) != copyOH.get(j))) {
                    availEHours.add(copyOH.get(j));
                    break;
                }
            }
        }
    }

    /**This method is used to add the times assigned to the appointment being modified back to availSHours.
     * This method iterates through officeHours and if the time matches the apptStart or is between apptStart
     * and apptEnd, it is added to availSHours. If availSHours is not null, the collection is sorted after
     * the time is added. Then the filterEOfficeHours method is called with the apptStart. While iterating
     * through the availSHours, if the availSHours time is after the last availEHours time, then it is added
     * to availEHours.
     * @param apptStart The appointment start time being modified.
     * @param apptEnd The appointment end time being modified.
     */
    public static void addCurrAppt(LocalTime apptStart, LocalTime apptEnd) {

        for(LocalTime t : officeHours) {
            if((t.equals(apptStart)) || ((t.isAfter(apptStart)) && (t.isBefore(apptEnd)))) {
                if (availSHours != null) {
                    availSHours.add(t);
                    Collections.sort(availSHours);
                } else {
                    availSHours.add(t);
                }
            }
        }
        filterEOfficeHours(apptStart);
        for(LocalTime sT : availSHours) {
            while(sT.isAfter(availEHours.get(availEHours.size() - 1))) {
                availEHours.add(sT);
            }
        }
    }
}
