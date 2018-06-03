/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import Utils.DateTimeBox;
import Utils.Utils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import kiri.mavenproject1.DBHandle.PrepareTicketResult;
import kiri.mavenproject1.entities.*;

/**
 *
 * @author User
 */
public class TicketPage extends JFrame {
    private DBHandle handle;
    DateTimeBox leftBorderBox;
    DateTimeBox rightBorderBox;
    ResultTableModel resultModel;
    JComboBox<Station> depStationCombo;
    JComboBox<Station> arrStationCombo;
    
    class ResultTableModel extends AbstractTableModel {
        private List<PrepareTicketResult> results;
        private String[] columnNames = new String[] {"X","Маршрут","Время отправления","Время прибытия","Время в пути","Цена","Осталось мест"};
        private int selectedRow;
        public ResultTableModel(List<PrepareTicketResult> results) {
            this.results = results;
            selectedRow = -1;
        }
        public PrepareTicketResult getSelectedResult() {
            if (selectedRow!=-1)
                return results.get(selectedRow);
            else
                return null;
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return Boolean.class;
                case 5:
                    return Float.class;
                case 6:
                    return Integer.class;
                default:
                    return String.class;
            }
        }
        public void removeAllRows() {
            this.results.removeAll(results);
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            selectedRow = selectedRow == rowIndex ? -1 : rowIndex;
            for (int i=0; i< getRowCount(); i++)
                this.fireTableCellUpdated(i, 0);
        }
        @Override
        public String getColumnName(int columnIndex) {
            return columnNames[columnIndex];
        }
        @Override
        public int getRowCount() {
            return results.size();
        }
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            PrepareTicketResult current = this.results.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    if (selectedRow==-1)
                        return false;
                    else
                        return rowIndex == selectedRow;
                case 1:
                    return current.arrStation.getSchedule().getRoute().toString();
                case 2:
                    return current.depStation.getArriveTime().toString();
                case 3:
                    return current.arrStation.getArriveTime().toString();
                case 4:
                    Duration time = Duration.between(current.depStation.getArriveTime(), current.arrStation.getArriveTime());
                    long hours = time.toHours();
                    long minutes = time.minusHours(hours).toMinutes();
                    return hours+"часов "+minutes+"минут";
                case 5:
                    return current.price;
                case 6:
                    return current.placesLeft;
                
            }
            return "nothing";
        }
    }
    /**
     * Creates new form TicketPage
     * @param handle
     */
    public TicketPage(DBHandle handle) {        
        initComponents();
        this.handle = handle;
        leftBorderBox = new DateTimeBox(2);
        leftBorderBox.setDateTime(LocalDateTime.now());
        this.leftBorderPnl.setLayout(new BoxLayout(leftBorderPnl,BoxLayout.X_AXIS));
        this.rightBorderPnl.setLayout(new BoxLayout(rightBorderPnl,BoxLayout.X_AXIS));
        this.leftBorderPnl.add(leftBorderBox);
        rightBorderBox = new DateTimeBox(2);
        rightBorderBox.setDateTime(LocalDateTime.now().plus(Duration.ofDays(5)));
        this.rightBorderPnl.add(rightBorderBox);
        this.rightBorderPnl.validate();
        depStationPnl.setLayout(new BoxLayout(depStationPnl,BoxLayout.X_AXIS));
        arrStationPnl.setLayout(new BoxLayout(arrStationPnl,BoxLayout.X_AXIS));
        logIn();
    }
    private void showStations() {
        Station[] stations = Utils.toStationArray(handle.getStations());
        depStationPnl.removeAll();
        arrStationPnl.removeAll();
        depStationCombo = new JComboBox(stations);
        arrStationCombo = new JComboBox(stations);
        depStationPnl.add(depStationCombo);
        depStationPnl.validate();
        arrStationPnl.add(arrStationCombo);
        arrStationPnl.validate();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        leftBorderPnl = new javax.swing.JPanel();
        rightBorderPnl = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        applyBtn = new javax.swing.JButton();
        depStationPnl = new javax.swing.JPanel();
        arrStationPnl = new javax.swing.JPanel();
        priceSortBox = new javax.swing.JCheckBox();
        dateSortBox = new javax.swing.JCheckBox();
        buyBtn1 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        logOutBtn = new javax.swing.JButton();
        adminPageBtn = new javax.swing.JButton();
        signInBtn = new javax.swing.JButton();
        logInBtn = new javax.swing.JButton();
        userDataLbl = new javax.swing.JLabel();
        profileBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Покупка билета");

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Исходная станция");

        jLabel2.setText("Конечная станция");

        jLabel3.setText("Интервал дат");

        jLabel4.setText("С");

        leftBorderPnl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout leftBorderPnlLayout = new javax.swing.GroupLayout(leftBorderPnl);
        leftBorderPnl.setLayout(leftBorderPnlLayout);
        leftBorderPnlLayout.setHorizontalGroup(
            leftBorderPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 175, Short.MAX_VALUE)
        );
        leftBorderPnlLayout.setVerticalGroup(
            leftBorderPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        rightBorderPnl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout rightBorderPnlLayout = new javax.swing.GroupLayout(rightBorderPnl);
        rightBorderPnl.setLayout(rightBorderPnlLayout);
        rightBorderPnlLayout.setHorizontalGroup(
            rightBorderPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 196, Short.MAX_VALUE)
        );
        rightBorderPnlLayout.setVerticalGroup(
            rightBorderPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        jLabel5.setText("По");

        applyBtn.setText("Показать");
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBtnActionPerformed(evt);
            }
        });

        depStationPnl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout depStationPnlLayout = new javax.swing.GroupLayout(depStationPnl);
        depStationPnl.setLayout(depStationPnlLayout);
        depStationPnlLayout.setHorizontalGroup(
            depStationPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 163, Short.MAX_VALUE)
        );
        depStationPnlLayout.setVerticalGroup(
            depStationPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 26, Short.MAX_VALUE)
        );

        arrStationPnl.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout arrStationPnlLayout = new javax.swing.GroupLayout(arrStationPnl);
        arrStationPnl.setLayout(arrStationPnlLayout);
        arrStationPnlLayout.setHorizontalGroup(
            arrStationPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 134, Short.MAX_VALUE)
        );
        arrStationPnlLayout.setVerticalGroup(
            arrStationPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 27, Short.MAX_VALUE)
        );

        priceSortBox.setText("Сортировка по цене");

        dateSortBox.setText("Сортировка по дате");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1)
                        .addGap(80, 80, 80)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3)
                        .addGap(205, 205, 205))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(depStationPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(arrStationPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72)
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(leftBorderPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rightBorderPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)))
                .addComponent(applyBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(priceSortBox)
                .addGap(44, 44, 44)
                .addComponent(dateSortBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(applyBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rightBorderPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(leftBorderPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(arrStationPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(depStationPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(priceSortBox)
                            .addComponent(dateSortBox))
                        .addContainerGap(11, Short.MAX_VALUE))))
        );

        buyBtn1.setText("Купить");
        buyBtn1.setEnabled(false);
        buyBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buyBtn1ActionPerformed(evt);
            }
        });

        resultTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Маршрут", "Время отправления", "Время прибытия", "Время в пути"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(resultTable);

        jScrollPane2.setViewportView(jScrollPane1);

        logOutBtn.setText("Выйти");
        logOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutBtnActionPerformed(evt);
            }
        });

        adminPageBtn.setText("Панель администратора");
        adminPageBtn.setEnabled(false);
        adminPageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminPageBtnActionPerformed(evt);
            }
        });

        signInBtn.setText("Зарегистироваться");
        signInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signInBtnActionPerformed(evt);
            }
        });

        logInBtn.setText("Войти");
        logInBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logInBtnActionPerformed(evt);
            }
        });

        profileBtn.setText("Профиль");
        profileBtn.setEnabled(false);
        profileBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 970, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userDataLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(logInBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(adminPageBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(signInBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logOutBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(profileBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(buyBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(userDataLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 475, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(logInBtn)
                .addGap(18, 18, 18)
                .addComponent(signInBtn)
                .addGap(21, 21, 21)
                .addComponent(adminPageBtn)
                .addGap(26, 26, 26)
                .addComponent(logOutBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(profileBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buyBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void showAvailableRoutes() {
        if (resultModel!=null)
            resultModel.removeAllRows();
        Station depStation = (Station)this.depStationCombo.getSelectedItem();                                      
        Station arrStation = (Station)this.arrStationCombo.getSelectedItem();
        LocalDateTime leftBorder = this.leftBorderBox.getDateTime();
        LocalDateTime rightBorder = this.rightBorderBox.getDateTime();
        try {
            int priceSort = this.priceSortBox.isSelected() ? 1 : 0;
            int dateSort = this.dateSortBox.isSelected() ? 1 : 0;
            List<PrepareTicketResult> result = handle.prepareBuyTicket(depStation, arrStation, leftBorder, rightBorder,priceSort,dateSort);
            if (result == null || result.isEmpty())
                Utils.showMessage(this,"Не найдено отправлений с заданными параметрами","",true);
            else {
                resultModel = new ResultTableModel(result);
                this.resultTable.setModel(resultModel);
            }
        }
        catch (Throwable exc) {
            Utils.showMessage(this, exc.getMessage(), "", true);
            Utils.traceAllErrors(exc);
        }
    }
    
    private void buyBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyBtn1ActionPerformed
        PrepareTicketResult result = ((ResultTableModel)this.resultTable.getModel()).getSelectedResult();
        if (result == null)
            Utils.showMessage(this,"Не выбрано значение","",true);
        else
            try {
                String res = JOptionPane.showInputDialog(this, "Введите количество билетов:", "Покупка билета", JOptionPane.OK_CANCEL_OPTION);
                if (res==null)
                    return;
                int amount = Integer.parseInt(res);
                if (amount>result.placesLeft) {
                    Utils.showMessage(this, "Недостаточно мест", "", true);
                    return;
                }
                handle.buyTicket(result,amount);
                result.placesLeft-=amount;
                resultModel.fireTableDataChanged();
                Utils.showMessage(this,"Билет успешно куплен","",false);
            }
        catch (Throwable exc) {
            Utils.showMessage(this, exc.getMessage(), "", true);
            Utils.traceAllErrors(exc);
        }
            
    }//GEN-LAST:event_buyBtn1ActionPerformed

    private void adminPageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminPageBtnActionPerformed
        AdminPage tp;
        try {
            tp = new AdminPage(handle,this,JDialog.ModalityType.DOCUMENT_MODAL);
            tp.setVisible(true);
            showStations();
        } catch (Throwable ex) {
            Utils.traceAllErrors(ex);
            Utils.showMessage(this,ex.getMessage(),"Ошибка",true);
        }
    }//GEN-LAST:event_adminPageBtnActionPerformed

    private void logOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutBtnActionPerformed
        handle.logOut();
        Utils.showMessage(this, "Вы вышли из профиля", "Выход из профиля", false);
        this.logOutBtn.setEnabled(false);
        this.buyBtn1.setEnabled(false);
        this.adminPageBtn.setEnabled(false);
        this.profileBtn.setEnabled(false);
        this.userDataLbl.setText("");
    }//GEN-LAST:event_logOutBtnActionPerformed

    private void signInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signInBtnActionPerformed
        signInPage page = new signInPage(handle,this);
        page.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        page.setVisible(true);
    }//GEN-LAST:event_signInBtnActionPerformed

    private void logInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logInBtnActionPerformed
        AuthPage authPage = new AuthPage(handle,this);
        authPage.setDefaultCloseOperation(HIDE_ON_CLOSE);
        authPage.setVisible(true);
        authPage.dispose(); 
        this.logIn();
    }//GEN-LAST:event_logInBtnActionPerformed

    private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
        showAvailableRoutes();
    }//GEN-LAST:event_applyBtnActionPerformed

    private void profileBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileBtnActionPerformed
        ProfilePage page = new ProfilePage(handle,this);
        page.setDefaultCloseOperation(HIDE_ON_CLOSE);
        page.setVisible(true);
        page.dispose(); 
    }//GEN-LAST:event_profileBtnActionPerformed
    
    private void logIn() {
        try {
            if (handle.isLogged()) {
                this.userDataLbl.setText("Текущий пользователь "+handle.getCurrentUser().getLogin());
                this.showStations();
                this.logOutBtn.setEnabled(true);
                this.buyBtn1.setEnabled(true);
                this.profileBtn.setEnabled(true);
                if (handle.getUserRole().getId()<=2)
                    this.adminPageBtn.setEnabled(true);
                else
                    this.adminPageBtn.setEnabled(false);
                Utils.showMessage(this,"Вы успешно вошли","",false);
            }
            else {
                this.logOutBtn.setEnabled(false);
                this.adminPageBtn.setEnabled(false);
                this.buyBtn1.setEnabled(false);
                this.userDataLbl.setText("");
                if (resultModel!=null) {
                    resultModel.results.clear();
                    this.resultTable.repaint();
                }
            }
        }
        catch (Throwable exc) {
            Utils.traceAllErrors(exc);
            Utils.showMessage(this,exc.getMessage(),"",true);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton adminPageBtn;
    private javax.swing.JButton applyBtn;
    private javax.swing.JPanel arrStationPnl;
    private javax.swing.JButton buyBtn1;
    private javax.swing.JCheckBox dateSortBox;
    private javax.swing.JPanel depStationPnl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel leftBorderPnl;
    private javax.swing.JButton logInBtn;
    private javax.swing.JButton logOutBtn;
    private javax.swing.JCheckBox priceSortBox;
    private javax.swing.JButton profileBtn;
    private javax.swing.JTable resultTable;
    private javax.swing.JPanel rightBorderPnl;
    private javax.swing.JButton signInBtn;
    private javax.swing.JLabel userDataLbl;
    // End of variables declaration//GEN-END:variables
    }
