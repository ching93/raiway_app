/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import kiri.mavenproject1.entities.*;

enum EntityType {BRANCHES, STATIONS, TRAINS, ROUTES, TRAINTYPES, SCHEDULES, ROLES, CREWS };
/**
 *
 * @author User
 */
public class EntityEditModel extends AbstractTableModel  {
        private List<RailwaySystem> branches = null;
        private List<Station> stations = null;
        private List<Train> trains = null;
        private List<Route> routes = null;
        private List<TrainType> trainTypes = null;
        private String[] columnNames;
        private EntityType entityType;
        private List<Boolean> changedRows;
        public EntityEditModel(List<Object> items, EntityType entityType) {
            changedRows = new ArrayList<>();
            this.entityType = entityType;
            switch (entityType) {
                case BRANCHES:
                    this.branches = new ArrayList<>();
                    for (int i=0; i<items.size();i++) {
                        changedRows.add(false);
                        this.branches.add((RailwaySystem)items.get(i));
                    }
                    columnNames = new String[] {"#","Исходная станция","Конечная станция","Дистанция"};
                    break;
                case STATIONS:
                    this.stations = new ArrayList<>();
                    for (int i=0; i<items.size();i++) {
                        changedRows.add(false);
                        this.stations.add((Station)items.get(i));
                    }
                    columnNames = new String[] {"#","Cтанция"};
                    break;
                case TRAINS:
                    this.trains = new ArrayList<>();
                    for (int i=0; i<items.size();i++) {
                        changedRows.add(false);
                        this.trains.add((Train)items.get(i));
                    }
                    columnNames = new String[] {"#","Тип","Вместимость"};
                    break;
                case ROUTES:
                    this.routes = new ArrayList<>();
                    for (int i=0; i<items.size();i++) {
                        changedRows.add(false);
                        this.routes.add((Route)items.get(i));
                    }
                    columnNames = new String[] {"#","Поезд"};
                    break;
                case TRAINTYPES:
                    this.trainTypes = new ArrayList<>();
                    for (int i=0; i<items.size();i++) {
                        changedRows.add(false);
                        this.trainTypes.add((TrainType)items.get(i));
                    }
                    columnNames = new String[] {"#","Тип поезда","Модификатор цены"};
                    break;
            }
        }
        public EntityType getEntityType() {
            return this.entityType;
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex>0)
                return true;
            else
                return false;
        }
        @Override
        public int getRowCount() {
            return this.changedRows.size();
        }
        @Override
        public String getColumnName(int columnIndex) {
            return this.columnNames[columnIndex];
        }
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (entityType) {
                case BRANCHES:
                    return getBranchValueAt(rowIndex,columnIndex);
                case STATIONS:
                    return getStationValueAt(rowIndex,columnIndex);
                case TRAINS:
                    return getTrainValueAt(rowIndex,columnIndex);
                case ROUTES:
                    return getRouteValueAt(rowIndex,columnIndex);
                case TRAINTYPES:
                    return getTrainTypeValueAt(rowIndex,columnIndex);
                default:
                    return "dicks";
            }
        }
        private Object getBranchValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return branches.get(rowIndex).getId();
                case 1:
                    return branches.get(rowIndex).getInStation();
                case 2:
                    return branches.get(rowIndex).getOutStation();
                case 3:
                    return branches.get(rowIndex).getDistance();
                default:
                    return "dicks";
            }
        }
        private Object getStationValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return stations.get(rowIndex).getId();
                case 1:
                    return stations.get(rowIndex).getName();
                default:
                    return "dicks";
            }
        }
        private Object getTrainValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return trains.get(rowIndex).getId();
                case 1:
                    return trains.get(rowIndex).getType();
                case 2:
                    return trains.get(rowIndex).getCapacity();
                default:
                    return "dicks";
            }
        }
        private Object getRouteValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return routes.get(rowIndex).getId();
                case 1:
                    return routes.get(rowIndex).getTrain();
                default:
                    return "dicks";
            }
        }
        private Object getTrainTypeValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return trainTypes.get(rowIndex).getId();
                case 1:
                    return trainTypes.get(rowIndex).getName();
                case 2:
                    return trainTypes.get(rowIndex).getPriceCoeff();
                default:
                    return "dicks";
            }
        }
        
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            switch (entityType) {
                case BRANCHES:
                    setBranchValueAt(value, rowIndex,columnIndex); break;
                case STATIONS:
                    setStationValueAt(value, rowIndex,columnIndex); break;
                case TRAINS:
                    setTrainValueAt(value, rowIndex,columnIndex); break;
                case ROUTES:
                    setRouteValueAt(value, rowIndex,columnIndex); break;
                case TRAINTYPES:
                    setTrainTypeValueAt(value, rowIndex,columnIndex); break;
            }
        }
        private void setBranchValueAt(Object value, int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 1:
                    branches.get(rowIndex).setInStation((Station)value); break;
                case 2:
                    branches.get(rowIndex).setOutStation((Station)value); break;
                case 3:
                    branches.get(rowIndex).setDistance(Float.parseFloat((String)value)); break;
            }
            changedRows.set(rowIndex, true);
        }
        private void setStationValueAt(Object value, int rowIndex, int columnIndex) {
            stations.get(rowIndex).setName((String)value);
            changedRows.set(rowIndex, true);
        }
        private void setTrainValueAt(Object value, int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 1:
                    trains.get(rowIndex).setType((TrainType)value); break;
                case 2:
                    trains.get(rowIndex).setCapacity(Integer.parseInt((String)value)); break;
            }
            changedRows.set(rowIndex, true);
        }
        private void setRouteValueAt(Object value, int rowIndex, int columnIndex) {
            routes.get(rowIndex).setTrain((Train)value);
            changedRows.set(rowIndex, true);
        }
        private void setTrainTypeValueAt(Object value, int rowIndex, int columnIndex) {
             switch (columnIndex) {
                case 1:
                    trainTypes.get(rowIndex).setName((String)value); break;
                case 2:
                    trainTypes.get(rowIndex).setPriceCoeff(Float.parseFloat((String)value)); break;
            }
            changedRows.set(rowIndex, true);
        }
        
        public Iterable<Object> getValuesToUpdate() {
            List<Object> result = new ArrayList<>();
            switch (entityType) {
                case BRANCHES:
                    for (int i=0; i<this.branches.size(); i++)
                        if (branches.get(i).getId()!=-1 && changedRows.get(i)) {
                            result.add(branches.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case STATIONS:
                    for (int i=0; i<this.stations.size(); i++)
                        if (stations.get(i).getId()!=-1 && changedRows.get(i)) {
                            result.add(stations.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case TRAINS:
                    for (int i=0; i<this.trains.size(); i++)
                        if (trains.get(i).getId()!=-1 && changedRows.get(i)) {
                            result.add(trains.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case ROUTES:
                    for (int i=0; i<this.routes.size(); i++)
                        if (routes.get(i).getId()!=-1 && changedRows.get(i)) {
                            result.add(routes.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case TRAINTYPES:
                    for (int i=0; i<this.trainTypes.size(); i++)
                        if (trainTypes.get(i).getId()!=-1 && changedRows.get(i)) {
                            result.add(routes.get(i));
                            changedRows.set(i, false);
                        }
                    break;
            }
            return result;
        }
        public Iterable<Object> getValuesToInsert() {
            List<Object> result = new ArrayList<>();
            switch (entityType) {
                case BRANCHES:
                    for (int i=0; i<this.branches.size(); i++)
                        if (branches.get(i).getId()==-1 && changedRows.get(i)) {
                            result.add(branches.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case STATIONS:
                    for (int i=0; i<this.stations.size(); i++)
                        if (stations.get(i).getId()==-1 && changedRows.get(i)) {
                            result.add(stations.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case TRAINS:
                    for (int i=0; i<this.trains.size(); i++)
                        if (trains.get(i).getId()==-1 && changedRows.get(i)) {
                            result.add(trains.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case ROUTES:
                    for (int i=0; i<this.routes.size(); i++)
                        if (routes.get(i).getId()==-1 && changedRows.get(i)) {
                            result.add(routes.get(i));
                            changedRows.set(i, false);
                        }
                    break;
                case TRAINTYPES:
                    for (int i=0; i<this.trainTypes.size(); i++)
                        if (trainTypes.get(i).getId()==-1 && changedRows.get(i)) {
                            result.add(trainTypes.get(i));
                            changedRows.set(i, false);
                        }
                    break;
            }
            return result;
        }
        
        public void addRow(Object value) {
            switch (this.entityType) {
                case BRANCHES:
                    RailwaySystem rs = (RailwaySystem)value;
                    rs.setId(-1);
                    this.branches.add(rs);
                    break;
                case STATIONS:
                    Station st = (Station)value;
                    st.setId(-1);
                    this.stations.add(st);
                    break;
                case TRAINS:
                    Train t = (Train)value;
                    t.setId(-1);
                    this.trains.add(t);
                    break;
                case ROUTES:
                    Route r = (Route)value;
                    r.setId(-1);
                    this.routes.add(r);
                    break;
                case TRAINTYPES:
                    TrainType tt = (TrainType)value;
                    tt.setId(-1);
                    tt.setName("Тип поезда");
                    tt.setPriceCoeff(1);
                    this.trainTypes.add(tt);
                    break;
            }
            this.changedRows.add(false);
            this.fireTableRowsInserted(this.getRowCount()-1, this.getRowCount()-1);
            
        }
        public void removeRows(int[] rows) {
            switch (this.entityType) {
                case BRANCHES:
                    for (int row: rows)
                        this.branches.remove(row);
                    break;
                case STATIONS:
                    for (int row: rows)
                        this.stations.remove(row);
                    break;
                case TRAINS:
                    for (int row: rows)
                        this.trains.remove(row);
                    break;
                case ROUTES:
                    for (int row: rows)
                        this.routes.remove(row);
                    break;
                case TRAINTYPES:
                    for (int row: rows)
                        this.trainTypes.remove(row);
                    break;
            }
            for (int row: rows) 
                this.changedRows.remove(row);
            this.fireTableRowsDeleted(rows[0], rows[rows.length-1]);
        }
        public List<Object> getRows(int[] rows) {
            List<Object> result = new ArrayList<>();
            switch (this.entityType) {
                case BRANCHES:
                    for (int row: rows) {
                        if (branches.get(row).getId()!=-1)
                            result.add(branches.get(row));
                    }
                    break;
                case STATIONS:
                    for (int row: rows) {
                        if (stations.get(row).getId()!=-1)
                            result.add(stations.get(row));
                    }
                    break;
                case TRAINS:
                    for (int row: rows) {
                        if (trains.get(row).getId()!=-1)
                            result.add(trains.get(row));
                    }
                    break;
                case ROUTES:
                    for (int row: rows) {
                        if (routes.get(row).getId()!=-1)
                            result.add(routes.get(row));
                    }
                    break;
                case TRAINTYPES:
                    for (int row: rows) {
                        if (trainTypes.get(row).getId()!=-1)
                            result.add(trainTypes.get(row));
                    }
                    break;
            }
            return result;
        }
    }
