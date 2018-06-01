/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.awt.Component;
import java.util.List;
import javax.swing.JOptionPane;
import kiri.mavenproject1.entities.RailwaySystem;
import kiri.mavenproject1.entities.Route;
import kiri.mavenproject1.entities.Station;
import kiri.mavenproject1.entities.Train;
import kiri.mavenproject1.entities.TrainType;
import kiri.mavenproject1.entities.User;

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
    public static Station[] toStationArray(List<Station> list) {
        Station[] res = new Station[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    private RailwaySystem[] toBranchesArray(List<RailwaySystem> list) {
        RailwaySystem[] res = new RailwaySystem[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    public static Train[] toTrainArray(List<Train> list) {
        Train[] res = new Train[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    public static TrainType[] toTrainTypeArray(List<TrainType> list) {
        TrainType[] res = new TrainType[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    public static Route[] toRouteArray(List<Route> list) {
        Route[] res = new Route[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    public static User[] toUserArray(List<User> list) {
        User[] res = new User[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
}
