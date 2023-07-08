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

/**This class is a controller for the allCust Graphical User Interface scene.*/
public class allCustForm implements Initializable {

    public ComboBox<String> menu;

    public String selected;
    public TableView custTable;
    public TableColumn custIDcol;
    public TableColumn nameCol;
    public TableColumn addressCol;
    public TableColumn postalCol;
    public TableColumn phoneCol;
    public TableColumn divCol;

    private ObservableList<Clients> allCust = Clients.getAllClients();

    private static Clients mCustomer = null;

    public static Clients getmCustomer(){return mCustomer;}

    /**This method initializes the allCust scene and populates the customer table.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        if (allCust.isEmpty()) {
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            allCust.clear();
            try {
                Clients.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        custTable.setItems(allCust);
        custIDcol.setCellValueFactory(new PropertyValueFactory<>("custID"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        postalCol.setCellValueFactory(new PropertyValueFactory<>("postal"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        divCol.setCellValueFactory(new PropertyValueFactory<>("divID"));

        custTable.getSortOrder().add(custIDcol);
        custTable.sort();
    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    /**This method loads the addCust scene when the add button is clicked.*/
    public void onAddCust(ActionEvent actionEvent) throws IOException {

        menuSelection("Add A Customer", actionEvent);
    }

    /**This method checks whether a customer has been selected to be modified. Then it loads the
     * modCust scene.
     */
    public void onModCust(ActionEvent actionEvent) throws IOException {

        mCustomer = (Clients) custTable.getSelectionModel().getSelectedItem();

        if (mCustomer == null) {
            Validate.noSelectionAlert(" customer");
        }
        else {

            Parent root = FXMLLoader.load(Objects.requireNonNull(allApptForm.class.getResource("/C195/view/modCust.fxml")));
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1100, 600);
            stage.setTitle("Modify A Customer");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        }
    }

    /**This method checks whether a customer has been selected to be deleted. Then it validates
     * that the customer can be deleted before refreshing the customer table.
     */
    public void onDelCust(ActionEvent actionEvent) throws SQLException {

        mCustomer = (Clients) custTable.getSelectionModel().getSelectedItem();

        if (mCustomer == null) {
            Validate.noSelectionAlert(" customer");
        }
        else {
            isValidCDelete(mCustomer.getClientID());
        }

        allCust.clear();
        Clients.tableQueryC();
        custTable.setItems(allCust);

    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) {
        Platform.exit();
    }
}
