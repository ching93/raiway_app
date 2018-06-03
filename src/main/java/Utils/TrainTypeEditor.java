/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import kiri.mavenproject1.entities.TrainType;

/**
 *
 * @author User
 */
public class TrainTypeEditor extends DefaultCellEditor {
    public TrainTypeEditor(TrainType[] items) {
        super(new JComboBox(items));
    }
}
