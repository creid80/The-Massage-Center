package C195.controller;

import C195.model.Clients;
import C195.utilities.Validate;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import static C195.helper.Menu.menuSelection;
import static C195.utilities.Validate.isValidCDelete;

/**@author Carol Reid*/

/**This class is a controller for the allClient Graphical User Interface scene.*/
public class allClientForm implements Initializable {

    public ComboBox<String> menu;

    public String selected;
    public TableView clientTable;
    public TableColumn clientIDcol;
    public TableColumn nameCol;
    public TableColumn addressCol;
    public TableColumn postalCol;
    public TableColumn phoneCol;
    public TableColumn divCol;

    private ObservableList<Clients> allClients = Clients.getAllClients();

    private static Clients mClient = null;

    public static Clients getmClient(){return mClient;}

    /**This method initializes the allClient scene and populates the client table.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        if (allClients.isEmpty()) {
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            allClients.clear();
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        clientTable.setItems(allClients);
        clientIDcol.setCellValueFactory(new PropertyValueFactory<>("clientID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCol.setCellValueFactory(new PropertyValueFactory<>("postal"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divCol.setCellValueFactory(new PropertyValueFactory<>("divID"));

        clientTable.getSortOrder().add(clientIDcol);
        clientTable.sort();
    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    /**This method loads the addClient scene when the add button is clicked.*/
    public void onAddClient(ActionEvent actionEvent) throws IOException {

        menuSelection("Add A Client", actionEvent);
    }

    /**This method checks whether a client has been selected to be modified. Then it loads the
     * modClient scene.
     */
    public void onModClient(ActionEvent actionEvent) throws IOException {

        mClient = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if (mClient == null) {
            Validate.noSelectionAlert(" client");
        }
        else {

            Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/modClient.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1100, 600);
            stage.setTitle("Modify A Client");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        }
    }

    /**This method checks whether a client has been selected to be deleted. Then it validates
     * that the client can be deleted before refreshing the client table.
     */
    public void onDelClient(ActionEvent actionEvent) throws SQLException {

        mClient = (Clients) clientTable.getSelectionModel().getSelectedItem();

        if (mClient == null) {
            Validate.noSelectionAlert(" client");
        }
        else {
            isValidCDelete(mClient.getClientID());
        }

        allClients.clear();
        Clients.tableQueryC();
        clientTable.setItems(allClients);

    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) {
        Platform.exit();
    }
}
