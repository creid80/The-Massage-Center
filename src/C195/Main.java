package C195;

import C195.model.Therapists;
import C195.utilities.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.SQLException;

/**@author Carol Reid*/

/** This class launches the application that is used to view, add, modify, and delete clients
 * and appointments.*/
public class Main extends Application {

    /**This method provides the Graphical User Interface with the location of the first view.*/
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("view/login.fxml"));
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    /**This method populates the officeHours, therapists, and user's. It also opens the database connection,
     * launches the Graphical User Interface, and closes the connection upon exiting the program.
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws SQLException {

        C195.utilities.apptTime.fillOfficeHours();
        JDBC.openConnection();
        Therapists.therapistsQuery();
        C195.model.Users.usersQuery();
        launch();

        JDBC.closeConnection();
    }
}
