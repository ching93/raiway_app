package kiri.mavenproject1;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import kiri.mavenproject1.entities.*;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class AdminPage extends JDialog {
    DBHandle handle;
    RouteStationsPanel routeStationsPnl;
    DateTimeBox trainDepBox;
    // Класс для вывода узла марштрута
    class RouteNodePanel extends JPanel {
        private JComboBox<Station> stationCombo;
        private final DateTimeBox stayTimeBox;
        private final DateTimeBox timeToComeBox;
        private RouteStation routeStation;
        public RouteNodePanel(List<Station> stations, Station currentStation, RouteStation routeStation) {
            this.setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
            stationCombo = new JComboBox<>();
            this.add(stationCombo);
            stayTimeBox = new DateTimeBox(1);
            timeToComeBox = new DateTimeBox(1);
            this.add(new LabeledComponent("Станция",stationCombo));
            this.add(new LabeledComponent("Время стоянки",stayTimeBox));
            this.add(new LabeledComponent("Время в пути",timeToComeBox));
            setRouteStation(routeStation,stations);
            this.validate();
        }
        public void setFirst() {
            stayTimeBox.setEnabled(false);
            timeToComeBox.setEnabled(false);
        }
        public void setLast() {
            stayTimeBox.setEnabled(false);
        }
        public void setSelectAction(ActionListener al) {
            for (ActionListener l: stationCombo.getActionListeners())
                stationCombo.removeActionListener(l);
            this.stationCombo.addActionListener(al);
        }
        // Изменения узла маршрута, описываемого данным компонентом
        // availables - спсиок станций, доступных из данной станции в маршруте
        public void setRouteStation(RouteStation rs, List<Station> availables) {
            this.routeStation = rs;
            this.stayTimeBox.setDuration(rs.getStayTime());
            this.timeToComeBox.setDuration(rs.getTimeToCome());
            updateStations(availables);
            if (rs.getStation()!=null)
                this.stationCombo.setSelectedItem((Station)rs.getStation());
        }
        public void updateValues(List<Station> stations, Station currentStation, Route route, int order) {
            updateStations(stations);
            stationCombo.setSelectedItem((Station)currentStation);
            routeStation.setRoute(route);
            routeStation.setStationOrder(order);
        }
        public void updateValues(List<Station> stations, Route route, int order) {
            updateStations(stations);
            routeStation.setRoute(route);
            routeStation.setStationOrder(order);
        }
        public void updateValues(List<Station> stations, int order) {
            updateStations(stations);
            routeStation.setStationOrder(order);
        }
        public void updateStations(List<Station> stations) {
            Station selected = (Station)stationCombo.getSelectedItem();
            stationCombo.removeAllItems();
            stations.forEach((station) -> {
                stationCombo.addItem(station);
                if (station == selected)
                    stationCombo.setSelectedItem(station);
            });
        }
        public int getOrder() {
            return this.routeStation.getStationOrder();
        }
        public void setStation(Station s) {
            this.routeStation.setStation(s);
        }
        public RouteStation getRouteStation() throws ParseException {
            this.routeStation.setTimeToCome(timeToComeBox.getDuration());
            this.routeStation.setStation((Station)this.stationCombo.getSelectedItem());
            this.routeStation.setStayTime(stayTimeBox.getDuration());
            System.out.println("added routeStation "+routeStation.toString());
            return routeStation;
        }
    }
    // Класс-контейнер для узлов машрута
    class RouteStationsPanel extends JPanel {
        private List<RailwaySystem> railwaySystem;
        private ArrayList<RouteNodePanel> nodes;
        private Route currentRoute;
        // Задается текущий маршрут, система ж\д веток, с помощью которых строится маршрут
        public RouteStationsPanel(Route route,List<RailwaySystem> railwaySystem) throws ParseException {
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.railwaySystem = railwaySystem;
            nodes = new ArrayList<RouteNodePanel>();
            if (route!=null) {
                this.setRailwaySystem(railwaySystem);
                this.setRoute(route);
            }
        }
        // При изменении станции узла обновляется список доступых значений для следующего узла
        // Если в обновленном списке присутствует ранне выбранный узел, то он сохраняется таковым
        private void setAvailableStation(ActionEvent evt) {
            try {
                JComboBox<Station> src = (JComboBox<Station>)evt.getSource();
                RouteNodePanel node = (RouteNodePanel)(((Component)src.getParent()).getParent());
                Station station = (Station)src.getSelectedItem();
                node.setStation(station);
                RouteStationsPanel parent = (RouteStationsPanel)node.getParent();
                for (RouteNodePanel cnode: parent.nodes)
                    if (cnode.getOrder()==node.getOrder()+1)
                        cnode.updateStations(this.getSuitableFor(station));
                System.out.print("set station");
            }
            catch (Throwable exc) {
                System.out.println(exc.getMessage());
            }
        }
        // Добавление узла
        public void addNode(RouteStation rs, boolean validate) throws ParseException {
            int lastIndex = this.nodes.size()-1;
            RouteNodePanel node;
            if (lastIndex==-1) {
                if (rs == null) {
                    rs = new RouteStation();
                    rs.setTimeToCome(Duration.ZERO);
                    rs.setStayTime(Duration.ZERO);
                    rs.setRoute(currentRoute);
                    rs.setStation(null);
                    rs.setStationOrder(1);
                }
                System.out.println("added rs with 1st order");
                node = new RouteNodePanel(getSuitableFor(null),null,rs);
            } else {
                RouteNodePanel lastNode = this.nodes.get(lastIndex);
                RouteStation lastRs = lastNode.getRouteStation();
                List<Station> availables = this.getSuitableFor(lastRs.getStation());
                if (rs == null) {
                    rs = new RouteStation();
                    rs.setRoute(this.currentRoute);
                    rs.setTimeToCome(lastRs.getTimeToCome());
                    rs.setStayTime(lastRs.getStayTime());
                    rs.setStationOrder(lastNode.getOrder()+1);
                }
                System.out.println("added rs with "+rs.getStationOrder()+" order");
                node = new RouteNodePanel(availables,availables.get(0),rs);
            }
            node.setSelectAction((new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    setAvailableStation(evt);
                }
            }));
            this.nodes.add(node);
            this.add(node);
            if (validate)
                this.validate();
        }
        // удаление узла
        public void removeNode() {
            int lastIndex = this.nodes.size()-1;
            if (lastIndex==-1)
                return;
            this.remove(this.nodes.get(lastIndex));
            this.nodes.remove(lastIndex);
        }
        // удаление всех узлов
        public void removeAllNodes() {
            for (int i=0; i<=this.nodes.size();i++)
                removeNode();
        }
        // задание текущего мартрута
        public void setRoute(Route route) throws ParseException {
            this.currentRoute = route;
            List<RouteStation> routeStations = handle.getRouteStationsByRoute(route);
            this.removeAllNodes();
            for (RouteStation rs: routeStations) {
                addNode(rs,false);
            }
            routeStationsPanel.validate();
        }
        // изменение ж\д системы
        public void setRailwaySystem(List<RailwaySystem> railwaySystem) {
            this.railwaySystem = railwaySystem;
        }
        // поиск доступных узлов для станции
        private List<Station> getSuitableFor(Station node) {
            if (node == null) {
                return handle.getStations();
            } else {
                List<Station> result = new ArrayList<>();
                for (RailwaySystem branch: railwaySystem) {
                    if (node.equals(branch.getInStation())) {
                        result.add(branch.getOutStation());
                    }
                    else if (node.equals(branch.getOutStation())) {
                        result.add(branch.getInStation());
                    }
                }
                return result;
            }
        }
        // Извлечение выбранных станций
        public List<Object> getRouteStations() throws ParseException {
            List<Object> result = new ArrayList<>();
            int order = 1;
            for (RouteNodePanel node: nodes) {
                RouteStation rs = node.getRouteStation();
                rs.setStationOrder(order);
                result.add(rs);
                order++;
            }
            return result;
        }
    }
    /**
     * Creates new form NewJFrame
     * @param handle - класс работы с б\д
     * @param owner - владелец данного модального окна
     * @param type - тип модальности
     * @throws java.text.ParseException
     */
    public AdminPage(DBHandle handle, JFrame owner, JDialog.ModalityType type) throws ParseException {
        super(owner,type);
        this.handle = handle;
        initComponents();
        this.routeStationsPanel.setLayout(new BoxLayout(routeStationsPanel,BoxLayout.Y_AXIS));
        trainDepBox = new DateTimeBox(2);
        trainDepBox.setDateTime(LocalDateTime.now());
        this.trainDepPanel.add(trainDepBox);
        this.trainDepPanel.setLayout(new BoxLayout(trainDepPanel,BoxLayout.X_AXIS));
        routeStationsPnl = new RouteStationsPanel((Route)this.routeCombo.getSelectedItem(),handle.getRailwaySystem());
        this.routeStationsPanel.add(this.routeStationsPnl);
        this.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        stationNameBtn = new javax.swing.JButton();
        stationNameBox = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        trainTypeBtn = new javax.swing.JButton();
        trainTypeBox = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        priceCoeffBox = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        TrainAddBtn = new javax.swing.JButton();
        trainCapacityBox = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        trainTypeCombo = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        addBranchBtn = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        distanceBox = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        outStationCombo = new javax.swing.JComboBox<>();
        inStationCombo = new javax.swing.JComboBox<>();
        jPanel7 = new javax.swing.JPanel();
        addRouteBtn = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        routeTrainCombo = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        addScheduleBtn = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        scheduleRouteCombo = new javax.swing.JComboBox<>();
        trainDepPanel = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        pricePerKmBox = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        addRouteNodeBtn = new javax.swing.JButton();
        RemoveRouteNodeBtn = new javax.swing.JButton();
        saveRouteStationsBtn = new javax.swing.JButton();
        routeCombo = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        routeStationsPanel = new javax.swing.JPanel();
        showRoutesBtn = new javax.swing.JButton();
        showRoutesBtn2 = new javax.swing.JButton();
        showRoutesBtn3 = new javax.swing.JButton();
        entityEditBtn = new javax.swing.JButton();

        jLabel1.setText("jLabel1");

        jLabel4.setText("jLabel4");

        jLabel6.setText("jLabel6");

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        stationNameBtn.setText("Добавить");
        stationNameBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stationNameBtnActionPerformed(evt);
            }
        });

        jLabel2.setText("Введите имя станции");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(stationNameBox)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(stationNameBtn))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stationNameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(stationNameBtn)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        trainTypeBtn.setText("Добавить");
        trainTypeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainTypeBtnActionPerformed(evt);
            }
        });

        jLabel7.setText("Введите тип поезда");

        jLabel17.setText("<html>Модификатор цены билета</html>");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(trainTypeBtn)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(trainTypeBox, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(priceCoeffBox))
                        .addContainerGap())
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addGap(0, 49, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(trainTypeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(priceCoeffBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(trainTypeBtn)
                .addGap(21, 21, 21))
        );

        jLabel17.getAccessibleContext().setAccessibleName("<html>Модификатор<br/> цены билета</html>");

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        TrainAddBtn.setText("Добавить");
        TrainAddBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TrainAddBtnActionPerformed(evt);
            }
        });

        jLabel8.setText("Введите параметры поезда");

        trainTypeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainTypeComboActionPerformed(evt);
            }
        });

        jLabel9.setText("Вместимость поезда");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(TrainAddBtn)
                        .addComponent(trainCapacityBox))
                    .addComponent(trainTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trainTypeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addGap(11, 11, 11)
                .addComponent(trainCapacityBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TrainAddBtn)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jPanel3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        addBranchBtn.setText("Добавить");
        addBranchBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBranchBtnActionPerformed(evt);
            }
        });

        jLabel10.setText("Добавление ветки");

        jLabel11.setText("Исходная станция");

        jLabel12.setText("Конечная станция");

        jLabel13.setText("Дистанция");

        outStationCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outStationComboActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(distanceBox)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(addBranchBtn)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(inStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(outStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 10, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(distanceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addBranchBtn)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        addRouteBtn.setText("Добавить");
        addRouteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRouteBtnActionPerformed(evt);
            }
        });

        jLabel14.setText("Добавление маршрута");

        jLabel15.setText("Поезд");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(routeTrainCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(addRouteBtn)
                            .addComponent(jLabel15))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(routeTrainCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(addRouteBtn)
                .addGap(25, 25, 25))
        );

        jPanel8.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        addScheduleBtn.setText("Добавить");
        addScheduleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addScheduleBtnActionPerformed(evt);
            }
        });

        jLabel16.setText("Новое отправление");

        jLabel18.setText("Маршрут");

        trainDepPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout trainDepPanelLayout = new javax.swing.GroupLayout(trainDepPanel);
        trainDepPanel.setLayout(trainDepPanelLayout);
        trainDepPanelLayout.setHorizontalGroup(
            trainDepPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        trainDepPanelLayout.setVerticalGroup(
            trainDepPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
        );

        jLabel19.setText("Время и дата");

        jLabel20.setText("Цена одного км поездки");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scheduleRouteCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(trainDepPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(pricePerKmBox, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(addScheduleBtn))
                            .addComponent(jLabel16)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scheduleRouteCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(trainDepPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addScheduleBtn)
                    .addComponent(pricePerKmBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel2.setAutoscrolls(true);

        jLabel3.setText("Станции маршрута");

        addRouteNodeBtn.setText("Добавить");
        addRouteNodeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRouteNodeBtnActionPerformed(evt);
            }
        });

        RemoveRouteNodeBtn.setText("Удалить");
        RemoveRouteNodeBtn.setActionCommand("");
        RemoveRouteNodeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RemoveRouteNodeBtnActionPerformed(evt);
            }
        });

        saveRouteStationsBtn.setText("Сохранить");
        saveRouteStationsBtn.setActionCommand("");
        saveRouteStationsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveRouteStationsBtnActionPerformed(evt);
            }
        });

        routeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                routeComboActionPerformed(evt);
            }
        });

        jLabel5.setText("Маршрут");

        routeStationsPanel.setForeground(javax.swing.UIManager.getDefaults().getColor("Button.highlight"));

        javax.swing.GroupLayout routeStationsPanelLayout = new javax.swing.GroupLayout(routeStationsPanel);
        routeStationsPanel.setLayout(routeStationsPanelLayout);
        routeStationsPanelLayout.setHorizontalGroup(
            routeStationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        routeStationsPanelLayout.setVerticalGroup(
            routeStationsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 112, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(routeStationsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(saveRouteStationsBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(RemoveRouteNodeBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel5)
                    .addComponent(routeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addRouteNodeBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(routeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addRouteNodeBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RemoveRouteNodeBtn)
                        .addGap(32, 32, 32)
                        .addComponent(saveRouteStationsBtn))
                    .addComponent(routeStationsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(324, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel2);

        showRoutesBtn.setText("Посмотреть маршруты");
        showRoutesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRoutesBtnActionPerformed(evt);
            }
        });

        showRoutesBtn2.setText("редактировать пользователя");
        showRoutesBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRoutesBtn2ActionPerformed(evt);
            }
        });

        showRoutesBtn3.setText("Произвольный запрос");
        showRoutesBtn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRoutesBtn3ActionPerformed(evt);
            }
        });

        entityEditBtn.setText("Редактирование сущностей");
        entityEditBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entityEditBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 792, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(entityEditBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(showRoutesBtn3, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(showRoutesBtn2, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(showRoutesBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 497, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(199, 199, 199))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(showRoutesBtn)
                                        .addComponent(showRoutesBtn2)
                                        .addComponent(showRoutesBtn3)
                                        .addComponent(entityEditBtn))
                                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(29, 29, 29))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stationNameBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stationNameBtnActionPerformed
        try {
            Station station = new Station();
            station.setName(this.stationNameBox.getText());
            handle.addStation(station);
            Utils.showMessage(this,"Новая станция успешно добавлена","",false);
            this.refreshData();
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_stationNameBtnActionPerformed

    private void trainTypeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainTypeBtnActionPerformed
        try {
            TrainType type = new TrainType(this.trainTypeBox.getText());
            type.setPriceCoeff(Float.parseFloat(this.priceCoeffBox.getText()));
            handle.addTrainType(type);
            this.loadTrainTypes(); // refreshData();
            Utils.showMessage(this,"Новый тип поезда успешно добавлен","",false);
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_trainTypeBtnActionPerformed

    private void TrainAddBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TrainAddBtnActionPerformed
        try {
            Train train = new Train();
            train.setType((TrainType)trainTypeCombo.getSelectedItem());
            train.setCapacity(Integer.parseInt(this.trainCapacityBox.getText()));
            handle.addTrain(train);
            this.loadRouteTrains(); // refreshData();
            Utils.showMessage(this,"Поезд успешно добавлен","",false);
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_TrainAddBtnActionPerformed
    // При показе формы обновляем все данные
    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        try {
            this.refreshData();
            this.refreshRoutePanel();
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_formComponentShown
    // Добавление ж\д ветки в б\д
    private void addBranchBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBranchBtnActionPerformed
        try {
            Station in = (Station)inStationCombo.getSelectedItem();                                     
            Station out = (Station)outStationCombo.getSelectedItem();                                          
            float distance = Float.parseFloat(distanceBox.getText());
            RailwaySystem branch = new RailwaySystem(in,out,distance);
            handle.addRailwaySystem(branch);
            this.routeStationsPnl.setRailwaySystem(handle.getRailwaySystem());
            Utils.showMessage(this,"Ж/Д ветка успешно добавлена","",false);
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_addBranchBtnActionPerformed

    private void outStationComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outStationComboActionPerformed
        
    }//GEN-LAST:event_outStationComboActionPerformed
    // добавление нового маршрута в б\д
    private void addRouteNodeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRouteNodeBtnActionPerformed
        try {
            this.routeStationsPnl.addNode(null, true);
            this.validate();
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_addRouteNodeBtnActionPerformed
    // нажатие кнопки удаления узла маршрута
    private void RemoveRouteNodeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RemoveRouteNodeBtnActionPerformed
        try {
            this.routeStationsPnl.removeNode();
            this.validate();
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_RemoveRouteNodeBtnActionPerformed
    // нажатие кнопки добавления узла маршрута
    private void addRouteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRouteBtnActionPerformed
        try {
            Train train = (Train)routeTrainCombo.getSelectedItem();
            Route route = new Route(train);
            handle.addRoute(route);
            routeCombo.addItem(route);
            scheduleRouteCombo.addItem(route);
            Utils.showMessage(this,"Маршрут успешно добавлен","",false);
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_addRouteBtnActionPerformed
    // нажатие кнопки сохранения выбранных узлов маршрута
    private void saveRouteStationsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveRouteStationsBtnActionPerformed
        try {
            this.handle.resetRouteStations(this.routeStationsPnl.getRouteStations());
        } 
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_saveRouteStationsBtnActionPerformed

    private void trainTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainTypeComboActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_trainTypeComboActionPerformed
    // изменение маршрута для редактирования
    private void routeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routeComboActionPerformed
        try {
            Route selected = (Route)this.routeCombo.getSelectedItem();
            this.routeStationsPnl.setRoute(selected);
        } catch (ParseException ex) {
            Utils.showMessage(this,ex.getMessage(),"",true);
        }
    }//GEN-LAST:event_routeComboActionPerformed
    // нажатие кнопки добавления новыого отправления
    private void addScheduleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addScheduleBtnActionPerformed
        try {
            Route route = (Route)scheduleRouteCombo.getSelectedItem();
            Schedule schedule = new Schedule();
            schedule.setRoute(route);
            schedule.setDelay(Duration.ZERO);
            schedule.setPricePerKm(Float.parseFloat(this.pricePerKmBox.getText()));
            schedule.setDepartureTime(trainDepBox.getDateTime());
            handle.addSchedule(schedule);
            Utils.showMessage(this,"Отправление успешно добавлено","",false);
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }//GEN-LAST:event_addScheduleBtnActionPerformed
    // открытие модального окна показа существующих маршрутов
    private void showRoutesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRoutesBtnActionPerformed
        routePage routes = new routePage(handle,this);
        routes.setDefaultCloseOperation(HIDE_ON_CLOSE);
        routes.setVisible(true);
        routes.dispose(); 
    }//GEN-LAST:event_showRoutesBtnActionPerformed

    private void showRoutesBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRoutesBtn2ActionPerformed
        UserManagementPage ump = new UserManagementPage(handle,this);
        ump.setDefaultCloseOperation(HIDE_ON_CLOSE);
        ump.setVisible(true);
        ump.dispose();
    }//GEN-LAST:event_showRoutesBtn2ActionPerformed

    private void showRoutesBtn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRoutesBtn3ActionPerformed
        CustomQueryPage ump = new CustomQueryPage(handle,this);
        ump.setDefaultCloseOperation(HIDE_ON_CLOSE);
        ump.setVisible(true);
        ump.dispose();
    }//GEN-LAST:event_showRoutesBtn3ActionPerformed

    private void entityEditBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entityEditBtnActionPerformed
        BranchEditPage bep = new BranchEditPage(handle,this);
        bep.setDefaultCloseOperation(HIDE_ON_CLOSE);
        bep.setVisible(true);
        bep.dispose();
    }//GEN-LAST:event_entityEditBtnActionPerformed

    /**
     * Обновление всех данных окна
     */
    private void loadTrainTypes() {
        List<TrainType> trainTypes = handle.getTrainTypes();
        for (TrainType type: trainTypes) {
            this.trainTypeCombo.addItem(type);
        }
    }
    private void loadInOutStations() {
        this.inStationCombo.removeAllItems();
        this.outStationCombo.removeAllItems();
        List<Station> stations = handle.getStations();
        for (Station station: stations) {
            this.inStationCombo.addItem(station);
            this.outStationCombo.addItem(station);
        }
    }
    private void loadRouteTrains() {
        this.routeTrainCombo.removeAllItems();
        List<Train> trains = handle.getTrains();
        for (Train train: trains) {
            this.routeTrainCombo.addItem(train);
        }
    }
    private void loadRouteList() {
        this.routeCombo.removeAllItems();
        this.scheduleRouteCombo.removeAllItems();
        List<Route> routes = handle.getRoutes();
        for (Route route: routes) {
            this.routeCombo.addItem(route);
            this.scheduleRouteCombo.addItem(route);
        }
    }
    private void refreshData() {
        try {
            this.trainTypeCombo.removeAllItems();
            loadTrainTypes();
            loadInOutStations();
            loadRouteTrains();
            loadRouteList();
        }
        catch (Throwable exc) {
            Utils.traceAllErrors(exc);
            Utils.showMessage(this,"Ошибка при обновлении","",true);
        }
    }
    // обновление панели резактирования маршрута
    private void refreshRoutePanel() throws ParseException {
        Route route = (Route)routeCombo.getSelectedItem();
        if (route!=null) {
            List<RailwaySystem> railwaySystem = handle.getRailwaySystem();
            routeStationsPnl.setRailwaySystem(railwaySystem);
            routeStationsPnl.setRoute(route);
            this.routeStationsPanel.validate();
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton RemoveRouteNodeBtn;
    private javax.swing.JButton TrainAddBtn;
    private javax.swing.JButton addBranchBtn;
    private javax.swing.JButton addRouteBtn;
    private javax.swing.JButton addRouteNodeBtn;
    private javax.swing.JButton addScheduleBtn;
    private javax.swing.JTextField distanceBox;
    private javax.swing.JButton entityEditBtn;
    private javax.swing.JComboBox<Station> inStationCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<Station> outStationCombo;
    private javax.swing.JTextField priceCoeffBox;
    private javax.swing.JTextField pricePerKmBox;
    private javax.swing.JComboBox<Route> routeCombo;
    private javax.swing.JPanel routeStationsPanel;
    private javax.swing.JComboBox<Train> routeTrainCombo;
    private javax.swing.JButton saveRouteStationsBtn;
    private javax.swing.JComboBox<Route> scheduleRouteCombo;
    private javax.swing.JButton showRoutesBtn;
    private javax.swing.JButton showRoutesBtn2;
    private javax.swing.JButton showRoutesBtn3;
    private javax.swing.JTextField stationNameBox;
    private javax.swing.JButton stationNameBtn;
    private javax.swing.JTextField trainCapacityBox;
    private javax.swing.JPanel trainDepPanel;
    private javax.swing.JTextField trainTypeBox;
    private javax.swing.JButton trainTypeBtn;
    private javax.swing.JComboBox<TrainType> trainTypeCombo;
    // End of variables declaration//GEN-END:variables
}
