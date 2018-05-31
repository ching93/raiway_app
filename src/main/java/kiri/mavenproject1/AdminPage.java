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
                if (availables.size()==0)
                    throw new IllegalArgumentException("Нет доступных веток из данной станции");
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
        jPanel7 = new javax.swing.JPanel();
        addRouteBtn = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
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

        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        addRouteBtn.setText("Добавить");
        addRouteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRouteBtnActionPerformed(evt);
            }
        });

        jLabel14.setText("Добавление маршрута");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addContainerGap(99, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(addRouteBtn)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addGap(26, 26, 26)
                .addComponent(addRouteBtn)
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
                .addContainerGap()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 383, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showRoutesBtn)
                    .addComponent(showRoutesBtn2)
                    .addComponent(showRoutesBtn3)
                    .addComponent(entityEditBtn))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
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
            Route route = new Route();
            handle.addRoute(route);
            routeCombo.addItem(route);
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
    // изменение маршрута для редактирования
    private void routeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_routeComboActionPerformed
        try {
            Route selected = (Route)this.routeCombo.getSelectedItem();
            this.routeStationsPnl.setRoute(selected);
        } catch (ParseException ex) {
            Utils.showMessage(this,ex.getMessage(),"",true);
        }
    }//GEN-LAST:event_routeComboActionPerformed

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
    
    private void loadInOutStations() {
        List<Station> stations = handle.getStations();
    }
    private void loadRouteList() {
        this.routeCombo.removeAllItems();
        List<Route> routes = handle.getRoutes();
        for (Route route: routes) {
            this.routeCombo.addItem(route);
        }
    }
    private void refreshData() {
        try {
            loadInOutStations();
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
    private javax.swing.JButton addRouteBtn;
    private javax.swing.JButton addRouteNodeBtn;
    private javax.swing.JButton entityEditBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<Route> routeCombo;
    private javax.swing.JPanel routeStationsPanel;
    private javax.swing.JButton saveRouteStationsBtn;
    private javax.swing.JButton showRoutesBtn;
    private javax.swing.JButton showRoutesBtn2;
    private javax.swing.JButton showRoutesBtn3;
    // End of variables declaration//GEN-END:variables
}
