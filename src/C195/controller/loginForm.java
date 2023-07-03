package C195.controller;

import C195.helper.Menu;
import C195.model.Users;
import C195.utilities.Validate;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**@author Carol Reid*/

/**This class is a controller for the login Graphical User Interface scene.*/
public class loginForm implements Initializable {

    public Label usernameText;
    public Label passwordText;
    public Button login;
    public Button cancel;
    public Label locationText;
    public Label location;
    public Label welcome;
    public TextField username;
    public PasswordField password;

    public static String user;

    private final ObservableList<Users> allUsers = Users.getAllUsers();
    public static ResourceBundle rb = ResourceBundle.getBundle("C195/NAT_fr", Locale.getDefault());

    /**This method initializes the login scene in either French or English, dependent on the locale of the
     * user.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (Locale.getDefault().getLanguage().equals("fr")) {
            welcome.setText(rb.getString("Welcome"));
            welcome.setLayoutX(210);
            usernameText.setText(rb.getString("Username") + ":");
            username.setLayoutX(245);
            passwordText.setText(rb.getString("Password") + ":");
            password.setLayoutX(245);
            locationText.setText(rb.getString("Location") + ":");
            location.setText(Locale.getDefault().getDisplayCountry());
            location.setLayoutX(120);
            login.setText(rb.getString("Login"));
            cancel.setText(rb.getString("Cancel"));
            cancel.setLayoutX(440);
        }
        else {

            welcome.setText("Welcome");
            usernameText.setText("Username");
            passwordText.setText("Password");
            locationText.setText("Location");
            location.setText(Locale.getDefault().getDisplayCountry());
            login.setText("Log" + " " + "In");
            cancel.setText("Cancel");
        }
    }

    /**This method checks whether the username and password textFields are empty, and if they are, an alert
     * in the users language pops up. If the textFields are not empty, it compares the information entered
     * with allUsers. Then it records whether the log in attempt was successful or not.
     */
    public void onLogIn(ActionEvent actionEvent) throws IOException {

        String userN;

        if(username.getText().isEmpty()) {
            if(Locale.getDefault().getLanguage().equals("fr")) {
                Validate.frBlankAlert(rb.getString("Username"));
            }
            else {
                Validate.blankAlert("Username");
            }
            return;
        }
        else {
            userN = username.getText();
        }

        String passW;

        if(password.getText().isEmpty()) {
            if(Locale.getDefault().getLanguage().equals("fr")) {
                Validate.frBlankAlert(rb.getString("Password"));
            }
            else {
                Validate.blankAlert("Password");
            }
            return;
        }
        else {
            passW = password.getText();
        }

        String filename = "login_activity.txt";
        FileWriter fWriter = new FileWriter(filename, true);
        PrintWriter output = new PrintWriter(fWriter);


        for(Users u : allUsers) {

            if ((userN.equals(u.getUserName())) && (passW.equals(u.getPassword()))) {

                output.println("Login successful by: " + u.getUserName() + " at: " + LocalDateTime.now());
                output.close();

                Menu.menuSelection("View All Appointments", actionEvent);
                user = userN;
                return;
            }
        }
        output.println("Login failed at: " + LocalDateTime.now());
        output.close();

        Validate.invalidLogin();
    }

    /**This method returns the current user.*/
    public static String getUser() { return user; }

    /**This method exits the Graphical User Interface.*/
    public void onCancel(ActionEvent actionEvent) { Platform.exit(); }
}
