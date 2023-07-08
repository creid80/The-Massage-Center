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
import java.lang.reflect.Type;
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

    public int selectedY;
    public LocalDate selectedD;

    private static ObservableList<Integer> allYears = FXCollections.observableArrayList();
    private static ObservableList<String> allMonths = FXCollections.observableArrayList();
    private static ObservableList<Types> allTypes = Types.getAllTypes();

    public BarChart<String, Number> typeBarChart;
    public XYChart.Series<String, Number> series1 = new XYChart.Series<>();
    public XYChart.Series<String, Number> series2 = new XYChart.Series<>();
    public XYChart.Series<String, Number> series3 = new XYChart.Series<>();
    public XYChart.Series<String, Number> series4 = new XYChart.Series<>();
    public XYChart.Series<String, Number> series5 = new XYChart.Series<>();
    public XYChart.Series<String, Number> series6 = new XYChart.Series<>();


//FINISH THIS

    /**This method initializes the apptRep scene and populates the type table with appointments from the
     * user's current year and month.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        menu.setItems(C195.helper.Menu.menuItems);

        LocalDate currD = LocalDate.now();
        getApptT(currD);

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

        int[] totalType = new int[6];
        String[] typeName = new String[6];

        for(int i = 0; i < allTypes.size(); i++) {
            typeName[i] = allTypes.get(i).getTypeAppt();
            totalType[i] = allTypes.get(i).getTotalAppt();
        }

        typeBarChart.setTitle(String.valueOf(currD.getMonth()));

        int i = 0;

        while(i < 6) {

            if (i == 0) {
                series1.setName(typeName[i]);
                series1.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 1) {
                series2.setName(typeName[i]);
                series2.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 2) {
                series3.setName(typeName[i]);
                series3.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 3) {
                series4.setName(typeName[i]);
                series4.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 4) {
                series5.setName(typeName[i]);
                series5.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 5) {
                series6.setName(typeName[i]);
                series6.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            i += 1;
        }

        typeBarChart.getData().addAll(series1, series2, series3, series4, series5, series6);
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



    //FIX THIS

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

        Types acu = new Types("Acupuncture");
        Types dT = new Types("Deep Tissue");
        Types hS = new Types("Hot Stone");
        Types ref = new Types("Reflexology");
        Types sM = new Types("Sports");
        Types swed = new Types("Swedish");

        allTypes.addAll(acu, dT, hS, ref, sM, swed);

        for(Types t : allTypes) {
            tCount = 0;
            System.out.println(t.getTypeAppt());
            for (Appointments a : Appointments.getAllAppts()) {

                if ((a.getStartLDT().getYear() == sYear) && (a.getStartLDT().getMonthValue() == sMonth) &&
                        (a.getType().equalsIgnoreCase(t.getTypeAppt()))) {

                    tCount += 1;
                }
               t.setTotalAppt(tCount);
            }
        }
    }

    /**This method gets the selected year, clears allMonths before repopulating it.*/
    public void onYear(ActionEvent actionEvent) {

        if(year.getValue() == null) {
            return;
        }
        selectedY = (int) year.getValue();

        allMonths.clear();
        getMonths();
        month.setItems(allMonths);
        month.setValue(null);
    }



    // FIX THIS

    /**This method gets the selected month name and determines the number of month. Then it creates a
     * localDateTime from the selected year and month, that it passes to the getApptT method.
     */
    public void onMonth(ActionEvent actionEvent) {

        if(month.getValue() == null) {
            return;
        }
        if(selectedY == 0) {
            selectedY = (int) year.getValue();
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
        System.out.println(selectedM);
        getApptT(selectedD);


        int[] totalType = new int[6];
        String[] typeName = new String[6];

        for(int i = 0; i < allTypes.size(); ++i) {
            typeName[i] = allTypes.get(i).getTypeAppt();

            totalType[i] = allTypes.get(i).getTotalAppt();
            System.out.println(typeName[i] + ": " + totalType[i]);
        }

        typeBarChart.getData().clear();
        series1.getData().remove(0);
        series2.getData().remove(0);
        series3.getData().remove(0);
        series4.getData().remove(0);
        series5.getData().remove(0);
        series6.getData().remove(0);
        typeBarChart.setTitle(String.valueOf(selectedD.getMonth()));



        int i = 0;

        while(i <= 5) {

            if (i == 0) {
                series1.setName(typeName[i]);

                series1.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 1) {
                series2.setName(typeName[i]);
                series2.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 2) {
                series3.setName(typeName[i]);
                series3.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 3) {
                series4.setName(typeName[i]);
                series4.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 4) {
                series5.setName(typeName[i]);
                series5.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            else if(i == 5) {
                series6.setName(typeName[i]);
                series6.getData().add(new XYChart.Data<>("Treatments", totalType[i]));
            }
            i += 1;
        }


        typeBarChart.getData().addAll(series1, series2, series3, series4, series5, series6);
        System.out.println(selectedY);

    }

    /**This method exits the Graphical User Interface.*/
   public void onSignOut(ActionEvent actionEvent) { Platform.exit(); }

}