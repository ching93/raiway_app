/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import kiri.mavenproject1.entities.*;

/**
 *
 * @author User
 */
public class BranchEditPage extends JDialog {
    DBHandle handle;
    EntityEditModel editModel;
    class StationRenderer extends JComboBox implements TableCellRenderer {
        public StationRenderer(Station[] items) {
          super(items);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              super.setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelectedItem(value);
            return this;
          }
    }
    class TrainTypeRenderer extends JComboBox implements TableCellRenderer {
        public TrainTypeRenderer(TrainType[] items) {
          super(items);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              super.setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelectedItem(value);
            return this;
          }
    }
    class TrainRenderer extends JComboBox implements TableCellRenderer {
        public TrainRenderer(Train[] items) {
          super(items);
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
            if (isSelected) {
              setForeground(table.getSelectionForeground());
              super.setBackground(table.getSelectionBackground());
            } else {
              setForeground(table.getForeground());
              setBackground(table.getBackground());
            }
            setSelectedItem(value);
            return this;
          }
    }

    class StationEditor extends DefaultCellEditor {
        public StationEditor(Station[] items) {
            super(new JComboBox(items));
        }
    }
    class TrainTypeEditor extends DefaultCellEditor {
        public TrainTypeEditor(TrainType[] items) {
            super(new JComboBox(items));
        }
    }
    class TrainEditor extends DefaultCellEditor {
        public TrainEditor(Train[] items) {
            super(new JComboBox(items));
        }
    }
    
        
    /**
     * Creates new form BranchEditPage
     * @param handle
     * @param owner
     */
    public BranchEditPage(DBHandle handle, JDialog owner) {
        super(owner,JDialog.ModalityType.DOCUMENT_MODAL);
        initComponents();
        this.handle = handle;
        loadBranchModel();
    }
    private Station[] toStationArray(List<Station> list) {
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
    private Train[] toTrainArray(List<Train> list) {
        Train[] res = new Train[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    private TrainType[] toTrainTypeArray(List<TrainType> list) {
        TrainType[] res = new TrainType[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    private Route[] toRouteArray(List<Route> list) {
        Route[] res = new Route[list.size()];
        for (int i=0; i<list.size(); i++)
            res[i]=list.get(i);
        return res;
    }
    
    private void loadBranchModel() {
        List<RailwaySystem> branches = handle.getRailwaySystem();
        List<Object> items = new ArrayList<>();
        for (int i=branches.size()-1; i>=0; i--)
            items.add((Object)branches.get(i));
        Station[] stations = toStationArray(handle.getStations());
        editModel = new EntityEditModel(items, EntityType.BRANCHES);
        this.resultTbl.setModel(editModel);
        TableColumn col = this.resultTbl.getColumnModel().getColumn(1);
        col.setCellEditor(new StationEditor(stations));
        col.setCellRenderer(new StationRenderer(stations));
        col = this.resultTbl.getColumnModel().getColumn(2);
        col.setCellEditor(new StationEditor(stations));
        col.setCellRenderer(new StationRenderer(stations));
        resultTbl.setRowHeight(25);
    }
    private void loadStationModel() {
        List<Station> stations = handle.getStations();
        List<Object> items = new ArrayList<>();
        for (int i=stations.size()-1; i>=0; i--)
            items.add((Object)stations.get(i));
        editModel = new EntityEditModel(items, EntityType.STATIONS);
        this.resultTbl.setModel(editModel);
    }
    private void loadTrainModel() {
        List<Train> trains = handle.getTrains();
        List<Object> items = new ArrayList<>();
        for (int i=trains.size()-1; i>=0; i--)
            items.add((Object)trains.get(i));
        TrainType[] ttypes = toTrainTypeArray(handle.getTrainTypes());
        editModel = new EntityEditModel(items,EntityType.TRAINS);
        this.resultTbl.setModel(editModel);
        TableColumn col = this.resultTbl.getColumnModel().getColumn(1);
        col.setCellEditor(new TrainTypeEditor(ttypes));
        col.setCellRenderer(new TrainTypeRenderer(ttypes));
        resultTbl.setRowHeight(25);
    }
    private void loadRouteModel() {
        List<Route> routes = handle.getRoutes();
        List<Object> items = new ArrayList<>();
        for (int i=routes.size()-1; i>=0; i--)
            items.add((Object)routes.get(i));
        editModel = new EntityEditModel(items, EntityType.ROUTES);
        this.resultTbl.setModel(editModel);
        Train[] trains = toTrainArray(handle.getTrains());
        TableColumn col = this.resultTbl.getColumnModel().getColumn(1);
        col.setCellEditor(new TrainEditor(trains));
        col.setCellRenderer(new TrainRenderer(trains));
        resultTbl.setRowHeight(25);
    }
    private void loadTrainTypesModel() {
        List<TrainType> trainTypes = handle.getTrainTypes();
        List<Object> items = new ArrayList<>();
        for (int i=trainTypes.size()-1; i>=0; i--)
            items.add((Object)trainTypes.get(i));
        editModel = new EntityEditModel(items,EntityType.TRAINTYPES);
        this.resultTbl.setModel(editModel);
        resultTbl.setRowHeight(25);
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
        resultTbl = new javax.swing.JTable();
        entitySelectCombo = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        AddBtn = new javax.swing.JButton();
        AddBtn1 = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();

        resultTbl.setModel(new javax.swing.table.DefaultTableModel(
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
        resultTbl.setRowHeight(22);
        jScrollPane1.setViewportView(resultTbl);

        entitySelectCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Ветки", "Станции", "Поезда", "Маршруты", "Типы поездов" }));
        entitySelectCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entitySelectComboActionPerformed(evt);
            }
        });

        jLabel1.setText("Сущность для редактирования");

        AddBtn.setText("Добавить");
        AddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddBtnActionPerformed(evt);
            }
        });

        AddBtn1.setText("<html>Удалить<br>выделенное</htm>");
        AddBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddBtn1ActionPerformed(evt);
            }
        });

        saveBtn.setText("<html>Сохранить<br>значения</html>");
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(entitySelectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 249, Short.MAX_VALUE)
                        .addComponent(saveBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(AddBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AddBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(entitySelectCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(saveBtn)
                            .addComponent(AddBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(AddBtn1))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void entitySelectComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entitySelectComboActionPerformed
        switch (this.entitySelectCombo.getSelectedIndex()) {
            case 0:
                loadBranchModel(); break;
            case 1:
                loadStationModel(); break;
            case 2:
                loadTrainModel(); break;
            case 3:
                loadRouteModel(); break;
            case 4:
                loadTrainTypesModel(); break;
        }
    }//GEN-LAST:event_entitySelectComboActionPerformed

    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        try {
            Iterable<Object> toUpdate = editModel.getValuesToUpdate();
            handle.updateEntities(toUpdate);
            Iterable<Object> toInsert = editModel.getValuesToInsert();
            handle.InsertBatchEntities(toInsert);
            Utils.showMessage(this, "Изменения успешно сохранены", "", false);
        } catch (Throwable exc) {
            Utils.showMessage(this, exc.getMessage(), "Ошибка", true);
            Utils.traceAllErrors(exc);
        }
    }//GEN-LAST:event_saveBtnActionPerformed

    private void AddBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBtn1ActionPerformed
        int[] rows = resultTbl.getSelectedRows();
        if (rows.length==0)
            return;
        int res = JOptionPane.showConfirmDialog(this, "Вы уверены?", "Удаление "+Integer.toString(rows.length)+" строк", JOptionPane.OK_CANCEL_OPTION);
        if (res== JOptionPane.OK_OPTION) {
            try {
                List<Object> entities = editModel.getRows(rows);
                handle.removeEntities(entities);
                editModel.removeRows(rows);
                Utils.showMessage(this, "Успешно удалены строки", "", false);
            } catch (Throwable exc) {
                Utils.showMessage(this, "Нельзя удалить", "", true);
                Utils.traceAllErrors(exc);
            }
        }            
    }//GEN-LAST:event_AddBtn1ActionPerformed

    private void AddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddBtnActionPerformed
        switch (editModel.getEntityType()) {
            case BRANCHES:
                RailwaySystem rs = new RailwaySystem();
                List<Station> stations = handle.getStations();
                rs.setDistance(100);
                rs.setInStation(stations.get(0));
                rs.setOutStation(stations.get(1));
                rs.setId(-1);
                editModel.addRow(rs);
                break;
            case STATIONS:
                Station st = new Station();
                st.setName("Имя станции");
                st.setId(-1);
                editModel.addRow(st);
                break;
            case ROUTES:
                Route r = new Route();
                List<Train> trains = handle.getTrains();
                r.setTrain(trains.get(0));
                r.setId(-1);
                editModel.addRow(r);
                break;
            case TRAINS:
                Train t = new Train();
                List<TrainType> types = handle.getTrainTypes();
                t.setType(types.get(0));
                t.setCapacity(100);
                t.setId(-1);
                editModel.addRow(t);
                break;
            case TRAINTYPES:
                TrainType tt = new TrainType();
                tt.setName("Тип поезда");
                tt.setPriceCoeff(1);
                tt.setId(-1);
                editModel.addRow(tt);
                break;
        }
    }//GEN-LAST:event_AddBtnActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AddBtn;
    private javax.swing.JButton AddBtn1;
    private javax.swing.JComboBox<String> entitySelectCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable resultTbl;
    private javax.swing.JButton saveBtn;
    // End of variables declaration//GEN-END:variables
}
