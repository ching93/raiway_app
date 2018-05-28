/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
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
    
    class ResultTableModel extends AbstractTableModel {
        private List<PrepareTicketResult> results;
        private String[] columnNames = new String[] {"X","Маршрут","Время отправления","Время прибытия","Время в пути","Цена"};
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
            if (columnIndex==0)
                return Boolean.class;
            else if(columnIndex==5)
                return Float.class;
            else
                return String.class;
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0;
        }
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            selectedRow = rowIndex;
            //this.fireTableCellUpdated(rowIndex, columnIndex);
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
            return 6;
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
                    return current.arrStation.getSchedule().getDepartureTime().toString();
                case 3:
                    return current.arrStation.getArriveTime().toString();
                case 4:
                    Duration time = Duration.between(current.depStation.getArriveTime(), current.arrStation.getArriveTime());
                    long hours = time.toHours();
                    long minutes = time.minusHours(hours).toMinutes();
                    return hours+"часов "+minutes+"минут";
                case 5:
                    return current.price;
                
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
        if (handle.isLogged())
            this.logOutBtn.setEnabled(false);
        try {
            Role userRole = handle.getUserRole();
            if (userRole!=null & userRole.getId()==1)
                this.adminPageBtn.setEnabled(true);
            showStations();
        }
        catch (Throwable exc) {
            this.messageLbl.setText(exc.getMessage());
            for (int i=0; i<10; i++) {
                System.out.println("Error: "+exc.getMessage());
                exc = exc.getCause();
                if (exc==null)
                    break;
            }
        }        
    }
    private void showStations() {
        List<Station> stations = handle.getStations();
        depStationCombo.removeAllItems();
        arrStationCombo.removeAllItems();
        for (Station station: stations) {
            depStationCombo.addItem(station);
            arrStationCombo.addItem(station);
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

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        depStationCombo = new javax.swing.JComboBox<>();
        arrStationCombo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        leftBorderPnl = new javax.swing.JPanel();
        rightBorderPnl = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        applyBtn = new javax.swing.JButton();
        buyBtn1 = new javax.swing.JButton();
        messageLbl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        resultTable = new javax.swing.JTable();
        logOutBtn = new javax.swing.JButton();
        adminPageBtn = new javax.swing.JButton();
        signInBtn = new javax.swing.JButton();
        logInBtn = new javax.swing.JButton();

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
            .addGap(0, 216, Short.MAX_VALUE)
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
            .addGap(0, 231, Short.MAX_VALUE)
        );
        rightBorderPnlLayout.setVerticalGroup(
            rightBorderPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel5.setText("По");

        applyBtn.setText("Показать");
        applyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel1))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(depStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(arrStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(leftBorderPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rightBorderPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel2)
                        .addGap(179, 179, 179)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(applyBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(applyBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(depStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(arrStationCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(rightBorderPnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(leftBorderPnl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel5))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        buyBtn1.setText("Купить");
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
        logOutBtn.setEnabled(false);
        logOutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logOutBtnActionPerformed(evt);
            }
        });

        adminPageBtn.setText("<html>Панель<br>администратора</html>");
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(buyBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 953, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(messageLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logInBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(signInBtn))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(adminPageBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logOutBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logInBtn)
                        .addGap(18, 18, 18)
                        .addComponent(signInBtn))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(adminPageBtn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(24, 24, 24)
                        .addComponent(logOutBtn)
                        .addGap(386, 386, 386))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(messageLbl, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buyBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void applyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyBtnActionPerformed
        Station depStation = (Station)this.depStationCombo.getSelectedItem();                                      
        Station arrStation = (Station)this.arrStationCombo.getSelectedItem();
        LocalDateTime leftBorder = this.leftBorderBox.getDateTime();
        LocalDateTime rightBorder = this.rightBorderBox.getDateTime();
        List<PrepareTicketResult> result = handle.prepareBuyTicket(depStation, arrStation, leftBorder, rightBorder);
        if (result == null || result.size()==0) {
            this.messageLbl.setText("Не найдено отправлений с заданными параметрами");
            return;
        }
        ResultTableModel model = new ResultTableModel(result);
        this.resultTable.setModel(model);
        this.resultTable.validate();
    }//GEN-LAST:event_applyBtnActionPerformed

    private void buyBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buyBtn1ActionPerformed
        PrepareTicketResult result = ((ResultTableModel)this.resultTable.getModel()).getSelectedResult();
        if (result == null)
            this.messageLbl.setText("Не выбрано значение");
        else
            try {
                handle.buyTicket(result);
                this.messageLbl.setText("Билет успешно куплен");
            }
        catch (Throwable exc) {
            this.messageLbl.setText(exc.getMessage());
            for (int i=0; i<10; i++) {
                System.out.println("Error: "+exc.getMessage());
                exc = exc.getCause();
                if (exc==null)
                    break;
            }
        }
            
    }//GEN-LAST:event_buyBtn1ActionPerformed

    private void adminPageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminPageBtnActionPerformed
        AdminPage tp;
        try {
            tp = new AdminPage(handle,this,JDialog.ModalityType.DOCUMENT_MODAL);
            tp.setVisible(true);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            this.messageLbl.setText(ex.getMessage());
        }
    }//GEN-LAST:event_adminPageBtnActionPerformed

    private void logOutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logOutBtnActionPerformed
        handle.logOut();
        JOptionPane.showMessageDialog(this, "Вы вышли из профиля", "Выход из профиля", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_logOutBtnActionPerformed

    private void signInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signInBtnActionPerformed
        signInPage page = new signInPage(handle,this);
        page.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        page.setVisible(true);
    }//GEN-LAST:event_signInBtnActionPerformed

    private void logInBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logInBtnActionPerformed
        this.logIn();
        if (handle.isLogged())
            this.logOutBtn.setEnabled(false);
        else
            this.logOutBtn.setEnabled(true);
    }//GEN-LAST:event_logInBtnActionPerformed
    
    private void logIn() {
        AuthPage authPage = new AuthPage(handle,this);
        authPage.setDefaultCloseOperation(HIDE_ON_CLOSE);
        authPage.setVisible(true);
        try {
            if (authPage.isLogged()) {
                this.showStations();
                if (handle.getUserRole().getId()==1)
                    this.adminPageBtn.setEnabled(true);
                else this.adminPageBtn.setEnabled(false);
                this.messageLbl.setText("Вы успешно вошли");
            }
            else
                this.messageLbl.setText("Неправильный логин или пароль");
        }
        catch (Throwable exc) {
            this.messageLbl.setText(exc.getMessage());
        }
        authPage.dispose(); 
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton adminPageBtn;
    private javax.swing.JButton applyBtn;
    private javax.swing.JComboBox<Station> arrStationCombo;
    private javax.swing.JButton buyBtn1;
    private javax.swing.JComboBox<Station> depStationCombo;
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
    private javax.swing.JLabel messageLbl;
    private javax.swing.JTable resultTable;
    private javax.swing.JPanel rightBorderPnl;
    private javax.swing.JButton signInBtn;
    // End of variables declaration//GEN-END:variables
    }
