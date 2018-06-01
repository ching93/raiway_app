/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.awt.Component;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import kiri.mavenproject1.entities.*;

enum EntityType {BRANCHES, STATIONS, TRAINS, TRAINTYPES, SCHEDULES, ROLES, TRAINCREWS, ROUTES };
/**
 *
 * @author User
 */
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
class UserRenderer extends JComboBox implements TableCellRenderer {
    public UserRenderer(User[] items) {
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
class RouteRenderer extends JComboBox implements TableCellRenderer {
    public RouteRenderer(Route[] items) {
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
class UserEditor extends DefaultCellEditor {
    public UserEditor(User[] items) {
        super(new JComboBox(items));
    }
}
class RouteEditor extends DefaultCellEditor {
    public RouteEditor(Route[] items) {
        super(new JComboBox(items));
    }
}

public class EntityEditModel extends AbstractTableModel  {
        private List<Object> entities;
        private String[] columnNames;
        private EntityType entityType;
        private List<Boolean> changedRows;
        public EntityEditModel(List<Object> items, EntityType entityType) {
            changedRows = new ArrayList<>();
            this.entityType = entityType;
            this.entities = items;
            for (int i=0; i<entities.size();i++) {
                changedRows.add(false);
            }
            switch (entityType) {
                case BRANCHES:
                    columnNames = new String[] {"#","Исходная станция","Конечная станция","Дистанция"};
                    break;
                case STATIONS:
                    columnNames = new String[] {"#","Cтанция"};
                    break;
                case TRAINS:
                    columnNames = new String[] {"#","Тип","Вместимость"};
                    break;
                case SCHEDULES:
                    columnNames = new String[] {"#","Задержка", "Время отправления","Цена одного километра","Маршрут","Поезд"};
                    break;
                case TRAINTYPES:
                    columnNames = new String[] {"#","Тип поезда","Модификатор цены"};
                    break;
                case TRAINCREWS:
                    columnNames = new String[] {"#","Сотрудник","Поезд"};
                    break;
                case ROUTES:
                    columnNames = new String[] {"#"};
                    break;
                case ROLES:
                    columnNames = new String[] {"#","Название"};
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
                case SCHEDULES:
                    return getScheduleValueAt(rowIndex,columnIndex);
                case TRAINTYPES:
                    return getTrainTypeValueAt(rowIndex,columnIndex);
                case TRAINCREWS:
                    return getTrainCrewValueAt(rowIndex,columnIndex);
                case ROUTES:
                    return getRouteValueAt(rowIndex,columnIndex);
                case ROLES:
                    return getRoleValueAt(rowIndex,columnIndex);
                default:
                    return "dicks";
            }
        }
        private Object getBranchValueAt(int rowIndex, int columnIndex) {
            RailwaySystem branch = (RailwaySystem)entities.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return branch.getId();
                case 1:
                    return branch.getInStation();
                case 2:
                    return branch.getOutStation();
                case 3:
                    return branch.getDistance();
                default:
                    return "th";
            }
        }
        private Object getStationValueAt(int rowIndex, int columnIndex) {
            Station station = (Station)entities.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return station.getId();
                case 1:
                    return station.getName();
                default:
                    return "th";
            }
        }
        private Object getTrainValueAt(int rowIndex, int columnIndex) {
            Train train = (Train)entities.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return train.getId();
                case 1:
                    return train.getType();
                case 2:
                    return train.getCapacity();
                default:
                    return "th";
            }
        }
        private Object getScheduleValueAt(int rowIndex, int columnIndex) {
            Schedule sh = (Schedule)entities.get(rowIndex);
            // {"#","Задержка", "Время отправления","Цена одного километра","Маршрут","Поезд"}
            switch (columnIndex) {
                case 0:
                    return sh.getId();
                case 1:
                    return sh.getDelay().toMinutes();
                case 2:
                    return sh.getDepartureTime();
                case 3:
                    return sh.getPricePerKm();
                case 4:
                    return sh.getRoute();
                case 5:
                    return sh.getTrain();
                default:
                    return "th";
            }
        }
        private Object getTrainTypeValueAt(int rowIndex, int columnIndex) {
            TrainType tt = (TrainType)entities.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return tt.getId();
                case 1:
                    return tt.getName();
                case 2:
                    return tt.getPriceCoeff();
                default:
                    return "th";
            }
        }
        private Object getTrainCrewValueAt(int rowIndex, int columnIndex) {
            TrainCrew tc = (TrainCrew)entities.get(rowIndex);
            // {"#","Сотрудник","Поезд"}
            switch (columnIndex) {
                case 0:
                    return tc.getId();
                case 1:
                    return tc.getConsumer();
                case 2:
                    return tc.getTrain();
                default:
                    return "th";
            }
        }
        private Object getRouteValueAt(int rowIndex, int columnIndex) {
            Route route = (Route)entities.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return route.getId();
                default:
                    return "th";
            }
        }
        private Object getRoleValueAt(int rowIndex, int columnIndex) {
            Role role = (Role)entities.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return role.getId();
                case 1:
                    return role.getName();
                default:
                    return "th";
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
                case SCHEDULES:
                    setScheduleValueAt(value, rowIndex,columnIndex); break;
                case TRAINTYPES:
                    setTrainTypeValueAt(value, rowIndex,columnIndex); break;
                case TRAINCREWS:
                    setTrainCrewValueAt(value, rowIndex,columnIndex); break;
                case ROLES:
                    setRoleValueAt(value, rowIndex,columnIndex); break;
            }
            changedRows.set(rowIndex, true);
        }
        private void setBranchValueAt(Object value, int rowIndex, int columnIndex) {
            RailwaySystem rs = (RailwaySystem)entities.get(rowIndex);
            switch (columnIndex) {
                case 1:
                    rs.setInStation((Station)value); break;
                case 2:
                    rs.setOutStation((Station)value); break;
                case 3:
                    rs.setDistance(Float.parseFloat((String)value)); break;
            }
        }
        private void setStationValueAt(Object value, int rowIndex, int columnIndex) {
            Station st = (Station)entities.get(rowIndex);
            st.setName((String)value);
        }
        private void setTrainValueAt(Object value, int rowIndex, int columnIndex) {
            Train t = (Train)entities.get(rowIndex);
            switch (columnIndex) {
                case 1:
                    t.setType((TrainType)value); break;
                case 2:
                    t.setCapacity(Integer.parseInt((String)value)); break;
            }
        }
        private void setScheduleValueAt(Object value, int rowIndex, int columnIndex) {
            Schedule sh = (Schedule)entities.get(rowIndex);
            // {"#","Задержка", "Время отправления","Цена одного километра","Маршрут","Поезд"}
            switch (columnIndex) {
                case 1:
                    sh.setDelay(Duration.ofMinutes((Long)Long.parseLong((String)value))); break;
                case 2:
                    sh.setDepartureTime(LocalDateTime.parse((String)value)); break;
                case 3:
                    sh.setPricePerKm(Float.parseFloat((String)value)); break;
                case 4:
                    sh.setRoute((Route)value); break;
                case 5:
                    sh.setTrain((Train)value); break;
            }
        }
        private void setTrainTypeValueAt(Object value, int rowIndex, int columnIndex) {
            TrainType tt = (TrainType)entities.get(rowIndex);
             switch (columnIndex) {
                case 1:
                    tt.setName((String)value); break;
                case 2:
                    tt.setPriceCoeff(Float.parseFloat((String)value)); break;
            }
        }
        private void setTrainCrewValueAt(Object value, int rowIndex, int columnIndex) {
            TrainCrew tc = (TrainCrew)entities.get(rowIndex);
            // {"#","Сотрудник","Поезд"}
             switch (columnIndex) {
                case 1:
                    tc.setConsumer((User)value); break;
                case 2:
                    tc.setTrain((Train)value); break;
            }
        }
        private void setRoleValueAt(Object value, int rowIndex, int columnIndex) {
            Role role = (Role)entities.get(rowIndex);
            role.setName((String)value);
        }
        public Iterable<Object> getChangedValues() {
            List<Object> result = new ArrayList<>();
            for (int i=0; i<this.entities.size(); i++)
                if (changedRows.get(i)) {
                    result.add(entities.get(i));
                    changedRows.set(i, false);
                }
            return result;
        }
        
        public void addRow(Object value) {
            switch (this.entityType) {
                case BRANCHES:
                    RailwaySystem rs = (RailwaySystem)value;
                    rs.setId(-1);
                    this.entities.add(rs);
                    break;
                case STATIONS:
                    Station st = (Station)value;
                    st.setId(-1);
                    this.entities.add(st);
                    break;
                case TRAINS:
                    Train t = (Train)value;
                    t.setId(-1);
                    this.entities.add(t);
                    break;
                case SCHEDULES:
                    Schedule sh = (Schedule)value;
                    sh.setId(-1);
                    this.entities.add(sh);
                    break;
                case TRAINTYPES:
                    TrainType tt = (TrainType)value;
                    tt.setId(-1);
                    this.entities.add(tt);
                    break;
                case TRAINCREWS:
                    TrainCrew tc = (TrainCrew)value;
                    tc.setId(-1);
                    this.entities.add(tc);
                    break;
                case ROUTES:
                    Route route = (Route)value;
                    route.setId(-1);
                    this.entities.add(route);
                    break;
                case ROLES:
                    Role role = (Role)value;
                    role.setId(-1);
                    this.entities.add(role);
                    break;
            }
            this.changedRows.add(true);
            this.fireTableRowsInserted(this.getRowCount()-1, this.getRowCount()-1);
            
        }
        public void removeRows(int[] rows) {
            for (int row: rows) {
                this.changedRows.remove(row);
                this.entities.remove(row);
            }
            this.fireTableRowsDeleted(rows[0], rows[rows.length-1]);
        }
        
        public List<Object> getRows(int[] rows) {
            List<Object> result = new ArrayList<>();
            for (int row: rows) {
                    result.add(entities.get(row));
            }
            return result;
        }
    }
