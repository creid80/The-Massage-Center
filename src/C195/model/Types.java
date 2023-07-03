package C195.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**@author Carol Reid*/

/**This class creates Types.*/
public class Types {

    private String typeAppt;
    private int totalAppt;

    private static ObservableList<Types> allTypes = FXCollections.observableArrayList();

    /**This method is a constructor. This method creates a type object.
     * @param typeAppt The type.
     */
    public Types(String typeAppt) {
        this.typeAppt = typeAppt;
        totalAppt = 0;
    }

    /**This method returns the type.
     * @return The type.
     */
    public String getTypeAppt() { return typeAppt; }

    /**This method sets the total appointments.
     * @param totalAppt The total appointments.
     */
    public void setTotalAppt(int totalAppt) { this.totalAppt = totalAppt; }

    /**This method returns the total appointments.
     * @return The total appointments.
     */
    public int getTotalAppt() { return totalAppt; }

    /**This method returns the observable list allTypes.
     * @return The observable list allTypes.
     */
    public static ObservableList<Types> getAllTypes() { return allTypes; }
}
