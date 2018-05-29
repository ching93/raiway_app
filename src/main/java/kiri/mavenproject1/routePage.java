/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import kiri.mavenproject1.entities.Route;
import kiri.mavenproject1.entities.RouteStation;

/**
 *
 * @author User
 */
public class routePage extends JDialog {
    DBHandle handle;
    
    class RouteTableModel extends AbstractTableModel {
        private List<RouteStation> routeStations;
        private List<RouteStation> arrStations;
        private List<RouteStation> depStations;
        private String[] columnNames;
        int type;
        public RouteTableModel(List<RouteStation> rs, int type) {
            this.routeStations = rs;
            this.type = type;
            if (type==0) {
                // all routes
                columnNames = new String[] {"Маршрут","Начальная станция","Конечная станция","Полное время","Полное расстояние"};
                arrStations = new ArrayList<>();
                depStations = new ArrayList<>();
                for (RouteStation st: rs) {
                    if (st.getStationOrder()==1)
                        depStations.add(st);
                    else
                        arrStations.add(st);
                }
            }
            else {
                // stations of one route
                columnNames = new String[] {"Станция","Время стоянки","Время в пути","Расстояние"};
            }
        }
        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }
        @Override
        public int getRowCount() {
            if (type==0)
                return arrStations.size();
            else
                return routeStations.size();
        }
        @Override
        public int getColumnCount() {
            if (type==0)
                return 5;
            else
                return 4;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (type==0) {
                // {"Маршрут","Начальная станция","Конечная станция","Полное время","Полное расстояние", "Число станций"};
                RouteStation currentDep = depStations.get(rowIndex);
                RouteStation currentArr = arrStations.get(rowIndex);
                switch (columnIndex) {
                    case 0:
                        return currentDep.getRoute().toString();
                    case 1:
                        return currentDep.getStation().toString();
                    case 2:
                        return currentArr.getStation().toString();
                    case 3:
                        return currentArr.getTimeToCome().toMinutes();
                    case 4:
                        return Float.toString(currentArr.getTotalDistance());
                    case 5:
                        return Float.toString(currentArr.getStationOrder());
                }
            }
            else {
                RouteStation current = this.routeStations.get(rowIndex);
                // {"Станция","Время стоянки","Время в пути","Расстояние"};
                switch (columnIndex) {
                    case 0:
                        return current.getStation().toString();
                    case 1:
                        return current.getStayTime().toMinutes()+" минут";
                    case 2:
                        return current.getTimeToCome().toMinutes()+" минут";
                    case 3:
                        float distance = rowIndex > 0 ? routeStations.get(rowIndex).getTotalDistance() - routeStations.get(rowIndex-1).getTotalDistance() :
                                routeStations.get(rowIndex).getTotalDistance();
                        return Float.toString(distance);
                    case 4:
                        return Float.toString(current.getTotalDistance());
                }
            }
            return "nothing";
        }
    }
    /**
     * Creates new form routePage
     * @param handle
     * @param owner
     */
    public routePage(DBHandle handle, JDialog owner) {
        super(owner, JDialog.ModalityType.DOCUMENT_MODAL);
        initComponents();
        this.showTypeGroup.add(this.allRoutes);
        this.showTypeGroup.add(this.oneRoute);
        this.handle = handle;
        this.loadRoutes();
        this.showAllRoutes();
    }
    private void showAllRoutes() {
        try {
            List<RouteStation> rs = handle.getFirstLastRouteStations();
            System.out.println(rs.size());
            RouteTableModel model = new RouteTableModel(rs,0);
            this.routeStationsTbl.setModel(model);
            routeStationsTbl.validate();
        }
        catch (Throwable exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void showOneRoute() {
        try {
            Route route = (Route)this.routeSelectCombo.getSelectedItem();
            List<RouteStation> rs = handle.getRouteStationsByRoute(route);
            RouteTableModel model = new RouteTableModel(rs,1);
            this.routeStationsTbl.setModel(model);
            routeStationsTbl.validate();
        }
        catch (Throwable exc) {
            JOptionPane.showMessageDialog(this, exc.getMessage(), "ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void loadRoutes() {
        this.routeSelectCombo.removeAllItems();
        List<Route> routes = handle.getRoutes();
        for (Route route: routes) {
            this.routeSelectCombo.addItem(route);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        showTypeGroup = new javax.swing.ButtonGroup();
        scrollPane = new javax.swing.JScrollPane();
        routeStationsTbl = new javax.swing.JTable();
        allRoutes = new javax.swing.JRadioButton();
        oneRoute = new javax.swing.JRadioButton();
        routeSelectCombo = new javax.swing.JComboBox<>();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setTitle("Маршруты");

        routeStationsTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scrollPane.setViewportView(routeStationsTbl);

        allRoutes.setSelected(true);
        allRoutes.setText("Все маршруты");
        allRoutes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allRoutesActionPerformed(evt);
            }
        });

        oneRoute.setText("Показ одного маршрута");
        oneRoute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneRouteActionPerformed(evt);
            }
        });

        routeSelectCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                routeSelectComboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1022, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(oneRoute)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(allRoutes)
                        .addGap(90, 90, 90)
                        .addComponent(routeSelectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(allRoutes)
                    .addComponent(routeSelectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 7, Short.MAX_VALUE)
                .addComponent(oneRoute)
                .addGap(18, 18, 18)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void allRoutesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allRoutesActionPerformed
        this.showAllRoutes();
    }//GEN-LAST:event_allRoutesActionPerformed

    private void oneRouteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneRouteActionPerformed
        this.showOneRoute();
    }//GEN-LAST:event_oneRouteActionPerformed

    private void routeSelectComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routeSelectComboActionPerformed
        if (this.oneRoute.isSelected())
            this.showOneRoute();
    }//GEN-LAST:event_routeSelectComboActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton allRoutes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JRadioButton oneRoute;
    private javax.swing.JComboBox<Route> routeSelectCombo;
    private javax.swing.JTable routeStationsTbl;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.ButtonGroup showTypeGroup;
    // End of variables declaration//GEN-END:variables
}
