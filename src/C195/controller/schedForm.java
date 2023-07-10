package C195.controller;

import C195.helper.Menu;
import C195.model.Appointments;
import C195.model.Therapists;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**@author Carol Reid*/

/**This class is a controller for the schedules Graphical User Interface scene.*/
public class schedForm implements Initializable {

    public ComboBox<String> menu;
    public String selected;

    public Therapists sTherapist;
    public LocalDate sDate;

    public TableView schedulesTable;
    public TableColumn schedulesApptID;
    public TableColumn schedulesTitle;
    public TableColumn schedulesNote;
    public TableColumn schedulesType;
    public TableColumn schedulesStart;
    public TableColumn schedulesEnd;
    public TableColumn schedulesClientID;
    public ComboBox therapist;
    public DatePicker date;

    private ObservableList<Therapists> allTherapists = Therapists.getAllTherapists();
    private ObservableList<Appointments> allAppt = Appointments.getAllAppts();


    /**This method initializes the schedules scene and populates the therapist combo box.*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        therapist.setItems(allTherapists);
    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        Menu.menuSelection(selected, actionEvent);
    }

    /**This method gets the selected therapist, then clears the date and schedule table.*/
    public void onTherapist(ActionEvent actionEvent) {

        sTherapist = (Therapists) therapist.getValue();

        if(date.getValue() != null) {
            date.setValue(null);
            schedulesTable.setItems(null);
        }
    }

    /**This method gets the selected date and filters allAppts by the selected therapist and date. Then it
     * populates the schedule table.
     */
    public void onDate(ActionEvent actionEvent) {

        if(date.getValue() == null) {
            return;
        }
        sDate = date.getValue();

        FilteredList<Appointments> fList = new FilteredList<>(allAppt);
        fList.setPredicate(Appointments -> Appointments.getTherapistName().equals(sTherapist.getTherapistName()));

        FilteredList<Appointments> fList2 = new FilteredList<>(fList);
        fList2.setPredicate(Appointments -> Appointments.getStartLDT().toLocalDate().isEqual(sDate));

        schedulesTable.setItems(fList2);
        schedulesApptID.setCellValueFactory(new PropertyValueFactory<>("apptID"));
        schedulesTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        schedulesNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        schedulesType.setCellValueFactory(new PropertyValueFactory<>("type"));
        schedulesStart.setCellValueFactory(new PropertyValueFactory<>("startLDT"));
        schedulesEnd.setCellValueFactory(new PropertyValueFactory<>("endLDT"));
        schedulesClientID.setCellValueFactory(new PropertyValueFactory<>("clientID"));

        schedulesTable.getSortOrder().add(schedulesStart);
        schedulesTable.sort();

    }

    /**This method exits the Graphical User Interface.*/
    public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }
}