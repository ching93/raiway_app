/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.text.ParseException;

/**
 *
 * @author User
 */
public class Main {
    public static void main(String []args) throws ParseException {
        try {
            DBHandle h = new DBHandle();
            TicketPage jf = new TicketPage(h);
            jf.setVisible(true);
        }
        catch (Throwable exc) {
            for (int i=0; i<10; i++) {
                System.out.println("Error: "+exc.getMessage());
                exc = exc.getCause();
                if (exc==null)
                    break;
            }
        }        
    }    
}
