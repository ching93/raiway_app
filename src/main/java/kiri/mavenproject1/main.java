/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import com.sun.glass.ui.Application;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author User
 */
public class main {
    public static void main(String []args) {
        try {
            DBHandle h = new DBHandle();
            NewJFrame jf = new NewJFrame(h);
            jf.setDefaultCloseOperation(EXIT_ON_CLOSE);
            jf.show();
        }
        catch (Throwable exc) {
            for (int i=0; i<10; i++) {
                System.out.println("Error: "+exc.getMessage());
                exc = exc.getCause();
                if (exc==null)
                    break;
            }
        }
        finally {
            
        }
        
    }
    
}
