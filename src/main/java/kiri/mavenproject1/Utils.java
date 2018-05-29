/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author User
 */
public class Utils {
    public static void showMessage(Component owner, String message, String title,boolean isError) {
        int dialogType = isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(owner, message, title, dialogType);
    }
    public static void traceAllErrors(Throwable exc) {
        for (int i=0; i<10; i++) {
                System.out.println("Error: "+exc.getMessage());
                exc = exc.getCause();
                if (exc==null)
                    break;
            }
    } 
}
