package C195.controller;

import C195.helper.Menu;
import C195.model.Appointments;
import C195.model.Clients;
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
public class clientApptRepForm implements Initializable {


    public ComboBox<String> menu;
    public String selected;
    public TextField clientSearch;

    public TableView clientTable;
    public TableColumn cTClientID;
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
    public TableColumn aTTherapistName;

    private ObservableList<Clients> searchClientText = null;

    private ObservableList<Clients> allCust = Clients.getAllClients();

    /**This method initializes the custApptRep scene and populates the customer table.*/
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

        clientTable.setItems(allCust);
        cTClientID.setCellValueFactory(new PropertyValueFactory<>("custID"));
        cTName.setCellValueFactory(new PropertyValueFactory<>("name"));
        cTAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        cTPostal.setCellValueFactory(new PropertyValueFactory<>("postal"));
        cTPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cTDiv.setCellValueFactory(new PropertyValueFactory<>("divID"));

        clientTable.getSortOrder().add(cTClientID);
        clientTable.sort();

    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        Menu.menuSelection(selected, actionEvent);
    }

    public void onClientSearch(ActionEvent actionEvent) {
        String q = clientSearch.getText();
        Clients custID = null;

        if (searchClientText != null) {
            searchClientText.clear();
        }

        searchClientText = Clients.lookupClient(q);

        if(q == "") {
            clientTable.setItems(allCust);
            return;
        }
        else if (searchClientText.size() == 0) {
            try {
                int ID = Integer.parseInt(q);
                custID = Clients.lookupClient(ID);
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
            clientTable.setItems(searchClientText);
            clientSearch.setText("");
        }
    }

    /**This method gets the selected customer and filters all appointments by the customer. Then it populates
     * the appointment table with all of the filtered appointments.
     */
    public void onClientSelected(MouseEvent mouseEvent) {

        if(clientTable.getSelectionModel().getSelectedItem() != null) {

            Clients sCustomer = (Clients) clientTable.getSelectionModel().getSelectedItem();
            int sCustID = sCustomer.getClientID();

            FilteredList<Appointments> fAppt = new FilteredList<>(Appointments.getAllAppts());
            fAppt.setPredicate(Appointments -> Appointments.getClientID() == sCustID);

            apptTable.setItems(fAppt);
            aTApptID.setCellValueFactory(new PropertyValueFactory<>("apptID"));
            aTTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            aTDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
            aTType.setCellValueFactory(new PropertyValueFactory<>("type"));
            aTStart.setCellValueFactory(new PropertyValueFactory<>("startLDT"));
            aTEnd.setCellValueFactory(new PropertyValueFactory<>("endLDT"));
            aTTherapistName.setCellValueFactory(new PropertyValueFactory<>("contName"));

            apptTable.getSortOrder().add(aTStart);
            apptTable.sort();
        }
    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }

}
