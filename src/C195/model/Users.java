package C195.model;

import C195.utilities.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**@author Carol Reid*/

/**This class creates Users.*/
public class Users {

    private int userID;
    private String userName;
    private String password;

    private static ObservableList<Users> allUsers = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates a user object.
     * @param userID The user ID.
     * @param userName The user name.
     * @param password The user password.
     */
    public Users(int userID, String userName, String password) {

        this.userID = userID;
        this.userName = userName;
        this.password = password;
    }

    /**This method returns the user ID.
     * @return The user ID.
     */
    public int getUserID() { return userID; }

    /**This method returns the user name.
     * @return The user name.
     */
    public String getUserName() { return userName; }

    /**This method returns the user password.
     * @return The user password.
     */
    public String getPassword() { return password; }

    /**This method queries the database for all users, and adds each user object to the
     * ObservableList allUsers.
     */
    public static void usersQuery() throws SQLException {

        String sql = "SELECT User_ID, User_Name, Password FROM Users;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String password = rs.getString("Password");

            Users newUser = new Users(userID, userName, password);

            allUsers.add(newUser);
        }
    }

    /**This method returns the observable list allUsers.
     * @return The observable list allUsers.
     */
    public static ObservableList<Users> getAllUsers() { return allUsers; }

    /**This method overrides the toString method and returns the user name.
     * @return The user name.*/
    @Override
    public String toString() {
        return userName;
    }
}
