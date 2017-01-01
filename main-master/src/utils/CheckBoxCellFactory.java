package utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
/**
 * @@author A0121628L
 *
 */
public class CheckBoxCellFactory implements Callback {
    @Override
    public TableCell call(Object param) {
        CheckBoxTableCell<Item,Boolean> checkBoxCell = new CheckBoxTableCell();
        
        return checkBoxCell;
    }
}
