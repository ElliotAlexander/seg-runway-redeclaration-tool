package RunwayRedeclarationTool.Controllers;

import RunwayRedeclarationTool.Logger.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class IntegerOnlyTextListener implements ChangeListener {

    private final TextField element;

    public IntegerOnlyTextListener(TextField element){
        this.element = element;
    }


    /**
     * The listener replaces any inputted text of non-numeric format with an empty string.
     *
     * @param observable The ObservableValue which value changed
     * @param oldValue   The old value
     * @param newValue   The new value
     */
    @Override
    public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(oldValue instanceof String || newValue instanceof String) {
            if (!((String)newValue).matches("\\d*")) {
                element.setText(((String)newValue).replaceAll("[^\\d]", ""));
            }
        } else {
            Logger.Log("IntegerOnlyTextListener has been applied to the wrong class.");
        }
    }
}
