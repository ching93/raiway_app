/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author User
 */
public class LabeledComponent extends JPanel {
        private JLabel label;
        private Container comp;
        public LabeledComponent(String label, Container comp) {
            this.label = new JLabel(label);
            this.label.setAlignmentX(Component.TOP_ALIGNMENT);
            this.comp = comp;
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.add(this.label);
            this.add(this.comp);
            this.validate();
        }
}
