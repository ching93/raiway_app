/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import kiri.mavenproject1.entities.Role;
import kiri.mavenproject1.entities.User;

/**
 *
 * @author User
 */
public class UserManagementPage extends JDialog {
    DBHandle handle;
    UserTableModel userModel;
    
    class UserTableModel extends AbstractTableModel {
        private User currentUser;
        private String[] columnNames;
        private String[] rowNames;
        public User getUser() {
            return currentUser;
        }
        public void setUser(User user) {
            this.currentUser = user;
        }
        public UserTableModel(User user) {
            this.currentUser = user;
            columnNames = new String[] { "Поле", "Значение"};
            rowNames = new String[] { "Логин", "Имя","Фамилия", "Почта"};
        }
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex==1;
        }
        @Override
        public int getRowCount() {
            return 4;
        }
        @Override
        public int getColumnCount() {
            return 2;
        }
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex==0) {
                return rowNames[rowIndex];
            }
            else
                switch (rowIndex) {
                    case 0:
                        return currentUser.getLogin();
                    case 1:
                        return currentUser.getFirstname();
                    case 2:
                        return currentUser.getLastname();
                    case 3:
                        return currentUser.getEmail();
                    default:
                        return "Хуй";
                }
        }
        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            String val = (String)value;
            // { "Логин", "Имя","Фамилия", "Почта"}
            switch (rowIndex) {
                case 0:
                    currentUser.setLogin(val); break;
                case 1:
                    currentUser.setFirstname(val); break;
                case 2:
                    currentUser.setLastname(val); break;
                case 3:
                    currentUser.setEmail(val); break;
            }
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
        @Override
        public String getColumnName(int rowIndex) {
            return columnNames[rowIndex];
        }
    }
    /**
     * Creates new form UserManagementPage
     * @param handle
     * @param owner
     */
    public UserManagementPage(DBHandle handle, JDialog owner) {
        super(owner, JDialog.ModalityType.DOCUMENT_MODAL);
        initComponents();
        this.handle = handle;
        loadUsers();
        loadRoles();
        refreshTable();
    }
    public void refreshTable() {
        this.userModel = new UserTableModel((User)this.userListCombo.getSelectedItem());
        this.userTbl.setModel(userModel);
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
        userTbl = new javax.swing.JTable();
        userListCombo = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        applyChangesBtn = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();
        userRoleCombo = new javax.swing.JComboBox<>();
        applyChangesBtn1 = new javax.swing.JToggleButton();

        userTbl.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(userTbl);

        userListCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userListComboActionPerformed(evt);
            }
        });

        jLabel1.setText("выберите пользователя");

        applyChangesBtn.setText("Применить");
        applyChangesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyChangesBtnActionPerformed(evt);
            }
        });

        jLabel2.setText("Изменение прав пользователя");

        applyChangesBtn1.setText("<html>Удалить<br>пользователя</html>");
        applyChangesBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyChangesBtn1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(userListCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(userRoleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(applyChangesBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(applyChangesBtn1, javax.swing.GroupLayout.Alignment.TRAILING)))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(userListCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(userRoleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(applyChangesBtn1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(applyChangesBtn))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void userListComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userListComboActionPerformed
        if (userListCombo.getItemCount()>0) {
            refreshTable();
            userRoleCombo.setSelectedItem(userModel.getUser().getRole());
        }
    }//GEN-LAST:event_userListComboActionPerformed

    private void applyChangesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyChangesBtnActionPerformed
        User user = this.userModel.getUser();
        user.setRole((Role)this.userRoleCombo.getSelectedItem());
        try {
            handle.updateEntity(user);
            Utils.showMessage(this, "данные пользователя успешно обновлены", "", false);
        }
        catch (Throwable exc) {
            Utils.traceAllErrors(exc);
            Utils.showMessage(this, exc.getMessage(), "", true);
        }
    }//GEN-LAST:event_applyChangesBtnActionPerformed

    private void applyChangesBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyChangesBtn1ActionPerformed
        int option = JOptionPane.showConfirmDialog(this,"Вы уверены?", "", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            User user = (User)this.userListCombo.getSelectedItem();
            try {
                handle.removeUser(user);
                this.userListCombo.removeItem(user);
                Utils.showMessage(this, "Пользователь успешно удален", "", false);
                this.loadUsers();
                this.refreshTable();
            } catch (Throwable exc) {
                Utils.showMessage(this, exc.getMessage(), "", true);
            }
        }
    }//GEN-LAST:event_applyChangesBtn1ActionPerformed

    /**
     * @param args the command line arguments
     */
    private void loadUsers() {
        try {
            List<User> users = handle.getUsers();
            this.userListCombo.removeAllItems();
            for (User u: users)
                userListCombo.addItem(u);
            User user = (User)userListCombo.getSelectedItem();
            this.userRoleCombo.setSelectedItem(user.getRole());
        }
        catch (Throwable exc) {
            Utils.showMessage(this, exc.getMessage(), "",true);
        }
    }
    private void loadRoles() {
        try {
            List<Role> roles = handle.getRoles();
            this.userRoleCombo.removeAllItems();
            for (Role role: roles)
                userRoleCombo.addItem(role);
        }
        catch (Throwable exc) {
            Utils.showMessage(this, exc.getMessage(), "",true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton applyChangesBtn;
    private javax.swing.JToggleButton applyChangesBtn1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<User> userListCombo;
    private javax.swing.JComboBox<Role> userRoleCombo;
    private javax.swing.JTable userTbl;
    // End of variables declaration//GEN-END:variables
}
