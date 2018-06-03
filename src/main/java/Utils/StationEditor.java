/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import kiri.mavenproject1.entities.Station;

/**
 *
 * @author User
 */
public class StationEditor extends DefaultCellEditor {
    public StationEditor(Station[] items) {
        super(new JComboBox(items));
    }
}
