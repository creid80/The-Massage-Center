package C195.helper;

import C195.controller.allApptForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static C195.utilities.Validate.pickToMod;

/**@author Carol Reid*/

/**This class helps centralize the navigation through the application.*/
public class Menu {

    public static ObservableList<String> menuItems = FXCollections.observableArrayList("View All Appointments", "Add An Appointment", "Modify An Appointment", "\n", "View All Clients", "Add A Customer", "Modify A Customer", "\n", "Monthly Type Report", "Contact Schedules", "Customer Appointments Report");

    /**This method checks what selection what made and loads the correct scene.*/
    public static void menuSelection(String selected, ActionEvent actionEvent) throws IOException {

        if (!(selected.isEmpty())) {
            switch (selected) {
                case "View All Appointments" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/allAppt.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("View All Appointments");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }
                case "Add An Appointment" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/addAppt.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("Add An Appointment");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }
                case "Modify An Appointment" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/allAppt.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("All Appointments");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();

                    pickToMod("n appointment");
                }
                case "View All Clients" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/allCust.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("All Clients");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }
                case "Add A Customer" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/addCust.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("Add A Customer");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }
                case "Modify A Customer" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/allCust.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("All Clients");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();

                    pickToMod(" customer");

                }
                case "Monthly Type Report" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/apptRep.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("Monthly Report");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }
                case "Contact Schedules" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/schedules.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("Contact Schedules");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }
                case "Customer Appointments Report" -> {

                    Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/custApptRep.fxml")));
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root, 1100, 600);
                    stage.setTitle("Customer Appointments Report");
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.show();
                }
            }
        }
    }
}
