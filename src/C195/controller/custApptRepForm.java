package C195.controller;

import C195.helper.Menu;
import C195.model.Appointments;
import C195.model.Customers;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**@author Carol Reid*/

/**This class is a controller for the custApptRep Graphical User Interface scene.*/
public class custApptRepForm implements Initializable {


    public ComboBox<String> menu;
    public String selected;
    public TextField clientSearch;

    public TableView custTable;
    public TableColumn cTCustID;
    public TableColumn cTName;
    public TableColumn cTAddress;
    public TableColumn cTPostal;
    public TableColumn cTPhone;
    public TableColumn cTDiv;

    public TableView apptTable;
    public TableColumn aTApptID;
    public TableColumn aTTitle;
    public TableColumn aTDesc;
    public TableColumn aTType;
    public TableColumn aTStart;
    public TableColumn aTEnd;
    public TableColumn aTContName;

    private ObservableList<Customers> searchClientText = null;

    private ObservableList<Customers> allCust = Customers.getAllCust();

    /**This method initializes the custApptRep scene and populates the customer table.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        if (allCust.isEmpty()) {
            try {
                Customers.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        else {
            allCust.clear();
            try {
                Customers.tableQueryC();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        custTable.setItems(allCust);
        cTCustID.setCellValueFactory(new PropertyValueFactory<>("custID"));
        cTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cTAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        cTPostal.setCellValueFactory(new PropertyValueFactory<>("postal"));
        cTPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cTDiv.setCellValueFactory(new PropertyValueFactory<>("divID"));

        custTable.getSortOrder().add(cTCustID);
        custTable.sort();

    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        Menu.menuSelection(selected, actionEvent);
    }

    public void onClientSearch(ActionEvent actionEvent) {
        String q = clientSearch.getText();
        Customers custID = null;

        if (searchClientText != null) {
            searchClientText.clear();
        }

        searchClientText = Customers.lookupCustomer(q);

        if(q == "") {
            custTable.setItems(allCust);
            return;
        }
        else if (searchClientText.size() == 0) {
            try {
                int ID = Integer.parseInt(q);
                custID = Customers.lookupCustomer(ID);
                if (custID != null) {
                    searchClientText.add(custID);
                }
            }
            catch (NumberFormatException e) {
                //ignore
            }
        }

        if ((searchClientText.size() == 0) && (custID == null)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error alert");
            alert.setHeaderText("Can not find client.");
            alert.setContentText("The client does not exists!");
            alert.showAndWait();
            clientSearch.clear();
        }
        else {
            custTable.setItems(searchClientText);
            clientSearch.setText("");
        }
    }

    /**This method gets the selected customer and filters all appointments by the customer. Then it populates
     * the appointment table with all of the filtered appointments.
     */
    public void onCustSelected(MouseEvent mouseEvent) {

        if(custTable.getSelectionModel().getSelectedItem() != null) {

            Customers sCustomer = (Customers) custTable.getSelectionModel().getSelectedItem();
            int sCustID = sCustomer.getCustID();

            FilteredList<Appointments> fAppt = new FilteredList<>(Appointments.getAllAppts());
            fAppt.setPredicate(Appointments -> Appointments.getCustID() == sCustID);

            apptTable.setItems(fAppt);
            aTApptID.setCellValueFactory(new PropertyValueFactory<>("apptID"));
            aTTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            aTDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
            aTType.setCellValueFactory(new PropertyValueFactory<>("type"));
            aTStart.setCellValueFactory(new PropertyValueFactory<>("startLDT"));
            aTEnd.setCellValueFactory(new PropertyValueFactory<>("endLDT"));
            aTContName.setCellValueFactory(new PropertyValueFactory<>("contName"));

            apptTable.getSortOrder().add(aTStart);
            apptTable.sort();
        }
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }

}
