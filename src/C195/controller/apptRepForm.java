package C195.controller;

import C195.model.Appointments;
import C195.model.Types;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ResourceBundle;

import static C195.helper.Menu.menuSelection;

/**@author Carol Reid*/

/**This class is a controller for the apptRep Graphical User Interface scene.*/
public class apptRepForm implements Initializable {

    public ComboBox<String> menu;
    public String selected;

    public ComboBox year;
    public ComboBox month;
    public TableView apptTypeTable;
    public TableColumn apptType;
    public TableColumn apptTotal;

    public int selectedY;
    public LocalDate selectedD;

    private static ObservableList<Integer> allYears = FXCollections.observableArrayList();
    private static ObservableList<String> allMonths = FXCollections.observableArrayList();
    private static ObservableList<Types> allTypes = Types.getAllTypes();
    public BarChart<String, Integer> typeBarChart;


    /**This method initializes the apptRep scene and populates the type table with appointments from the
     * user's current year and month.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        LocalDate currD = LocalDate.now();
        getApptT(currD);

        apptTypeTable.setItems(allTypes);
        apptType.setCellValueFactory(new PropertyValueFactory<>("typeAppt"));
        apptTotal.setCellValueFactory(new PropertyValueFactory<>("totalAppt"));

        getYears();
        getMonths();
        year.setItems(allYears);
        year.setValue(currD.getYear());
        month.setItems(allMonths);
        month.setValue(currD.getMonth());

        CategoryAxis xAxis = new CategoryAxis();

        xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(
                "Treatments")));
        xAxis.setLabel("Treatments");

//Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total");

        typeBarChart.setTitle(String.valueOf(currD.getMonth()));

        int count = 1;

        for (Types t: allTypes) {

            //Prepare XYChart.Series objects by setting data
            if (count == 1) {
                XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
                series1.setName("Acupuncture");
                series1.getData().add(new XYChart.Data("Treatments", t.getTotalAppt()));
                typeBarChart.getData().add(series1);
            }
            else if (count == 2) {
                XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
                series2.setName("Deep Tissue");
                series2.getData().add(new XYChart.Data("Treatments", t.getTotalAppt()));
                typeBarChart.getData().add(series2);
            }
            else if (count == 3) {
                XYChart.Series<String, Integer> series3 = new XYChart.Series<>();
                series3.setName("Hot Stone");
                series3.getData().add(new XYChart.Data("Treatments", t.getTotalAppt()));
                typeBarChart.getData().add(series3);
            }
            else if (count == 4) {
                XYChart.Series<String, Integer> series4 = new XYChart.Series<>();
                series4.setName("Reflexology");
                series4.getData().add(new XYChart.Data("Treatments", t.getTotalAppt()));
                typeBarChart.getData().add(series4);
            }
            else if (count == 5) {
                XYChart.Series<String, Integer> series5 = new XYChart.Series<>();
                series5.setName("Sports");
                series5.getData().add(new XYChart.Data("Treatments", 1.0));
                typeBarChart.getData().add(series5);
            }
            else if (count == 6) {
                XYChart.Series<String, Integer> series6 = new XYChart.Series<>();
                series6.setName("Swedish");
                series6.getData().add(new XYChart.Data("Treatments", 0.0));
                typeBarChart.getData().add(series6);
            }
            count += 1;
            //typeBarChart.getData().addAll(series1, series2, series3, series4, series5, series6);
        }
        //Group root = new Group(typeBarChart);
    }

    /**This method takes the selected menu item, and passes it to the Menu.menuSelection method. */
    public void onMenu(ActionEvent actionEvent) throws IOException {

        selected = menu.getValue();
        menuSelection(selected, actionEvent);
    }

    /**This method creates a HashSet that holds all of the years that have a scheduled appointment.*/
    public static void getYears() {

        if(!(allYears.isEmpty())) {
            allYears.clear();
        }

        HashSet<Integer> yearHash = new HashSet<>();

        for(Appointments a : Appointments.getAllAppts()) {
            int year = a.getStartLDT().getYear();
            yearHash.add(year);
        }
        allYears.addAll(yearHash);
    }

    /**This method populates the allMonths list.*/
    public static void getMonths() {

        allMonths.addAll("January", "February", "March", "April", "May", "June", "July", "August",
                "September", "October", "November", "December");
    }

    /**This method clears allTypes if it is not empty. Next, it adds all unique appointment types to a HashSet
     * if the appointment start time matches the selected year and month. Then it adds the unique types to
     * allTypes before checking how many of the filtered appointments have each type.
     */
    public static void getApptT(LocalDate selectedD) {

        int sYear = selectedD.getYear();
        int sMonth = selectedD.getMonthValue();
        int tCount;

        if(allTypes != null) {
            allTypes.clear();
        }

        /*
        HashSet<String> apptHash = new HashSet<>();

        for(Appointments a : Appointments.getAllAppts()) {

            if((a.getStartLDT().getYear() == sYear) && (a.getStartLDT().getMonthValue() == sMonth)) {
                apptHash.add(a.getType());
            }
        }

        for(String uS : apptHash) {
            Types newType = new Types(uS);
            allTypes.add(newType);
        }

         */

        for(Types t : allTypes) {
            tCount = 0;
            for (Appointments a : Appointments.getAllAppts()) {

                if ((a.getStartLDT().getYear() == sYear) && (a.getStartLDT().getMonthValue() == sMonth) &&
                        (a.getType().equalsIgnoreCase(t.getTypeAppt()))) {

                    tCount += 1;
                }
               t.setTotalAppt(tCount);
            }
            /*
            if(t.getTypeAppt().equalsIgnoreCase("Acupuncture")) {
                XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
                series1.setName("Acupuncture");
                series1.getData().add(new XYChart.Data("Treatments", t.getTotalAppt()));
            }
            else if(t.getTypeAppt().equalsIgnoreCase("Deep Tissue")) {
                XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
                series2.setName("Deep Tissue");
                series2.getData().add(new XYChart.Data("Treatments", t.getTotalAppt()));
            }
            else if(t.getTypeAppt().equalsIgnoreCase("Hot Stone")) {
                XYChart.Series<String, Integer> series3 = new XYChart.Series<>();
                series3.setName("Hot Stone");
                series3.getData().add(new XYChart.Data("Treatments", 4.0));
            }
            else if(t.getTypeAppt().equalsIgnoreCase("Reflexology")) {
                XYChart.Series<String, Integer> series4 = new XYChart.Series<>();
                series4.setName("Reflexology");
                series4.getData().add(new XYChart.Data("Treatments", 6.0));
            }
            else if(t.getTypeAppt().equalsIgnoreCase("Sports")) {
                XYChart.Series<String, Integer> series5 = new XYChart.Series<>();
                series5.setName("Sports");
                series5.getData().add(new XYChart.Data("Treatments", 1.0));
            }
            else if(t.getTypeAppt().equalsIgnoreCase("Swedish")) {
                XYChart.Series<String, Integer> series6 = new XYChart.Series<>();
                series6.setName("Swedish");
                series6.getData().add(new XYChart.Data("Treatments", 0.0));

             */
            //}
        }
    }

    /**This method gets the selected year, clears allMonths before repopulating it.*/
    public void onYear(ActionEvent actionEvent) {

        selectedY = (int) year.getValue();

        allMonths.clear();
        getMonths();
        month.setItems(allMonths);
        month.setValue(null);
    }

    /**This method gets the selected month name and determines the number of month. Then it creates a
     * localDateTime from the selected year and month, that it passes to the getApptT method.
     */
    public void onMonth(ActionEvent actionEvent) {

        if(month.getValue() == null) {
            return;
        }
        String selectedM = (String) month.getSelectionModel().getSelectedItem();
        int selM = 0;

        for(int i = 0; i < 12; i++) {
            if(selectedM.equalsIgnoreCase(allMonths.get(i))) {
                selM = i + 1;
                break;
            }
        }
        selectedD = LocalDate.of(selectedY, selM, 1);

        getApptT(selectedD);

        apptTypeTable.setItems(allTypes);

    }

    /**This method exits the Graphical User Interface.*/
   public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }

}