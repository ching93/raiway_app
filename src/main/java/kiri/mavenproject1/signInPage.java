/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import kiri.mavenproject1.entities.Role;
import kiri.mavenproject1.entities.User;

/**
 *
 * @author User
 */
public class signInPage extends JDialog {
    private DBHandle handle;
    /**
     * Creates new form signInPage
     */
    public signInPage(DBHandle handle, JFrame owner) {
        super(owner, JDialog.ModalityType.DOCUMENT_MODAL);
        initComponents();
        this.handle = handle;
        loadRoles();
    }
    
    public void loadRoles() {
        Role currentRole = handle.getUserRole();
        if (currentRole != null && currentRole.getId()==1) {
            this.roleBox.setEnabled(true);
            this.roleBox.removeAllItems();
            List<Role> roles = handle.getRoles();
            for (Role role: roles)
                this.roleBox.addItem(role);
        }
        else
            this.roleBox.setEnabled(false);
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
        loginBox = new javax.swing.JTextField();
        cancelBtn = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        firstnameBox = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        lastnameBox = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        roleBox = new javax.swing.JComboBox<>();
        passwordBox = new javax.swing.JPasswordField();
        passwordRepeatBox = new javax.swing.JPasswordField();
        emailBox = new javax.swing.JPasswordField();

        setTitle("Регистрация");

        jLabel1.setText("Введите логин:");

        cancelBtn.setText("Отмена");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        okBtn.setText("Зарегистрироваться");
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        jLabel2.setText("Введите имя:");

        jLabel3.setText("Введите фамилию:");

        jLabel4.setText("Введите пароль:");

        jLabel5.setText("Повторите пароль:");

        jLabel6.setText("Введите почту:");

        jLabel7.setText("Выберите роль:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(lastnameBox, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(firstnameBox, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginBox, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(cancelBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okBtn))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(roleBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(passwordBox)
                    .addComponent(passwordRepeatBox)
                    .addComponent(emailBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(loginBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstnameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lastnameBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordRepeatBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emailBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(roleBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(okBtn)
                    .addComponent(cancelBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelBtnActionPerformed
    
    private User getUser() {
        String login = this.loginBox.getText();
        char[] password = this.passwordBox.getPassword();
        char[] passwordRepeat = this.passwordRepeatBox.getPassword();
        String passwordStr = String.valueOf(password, 0, password.length);
        String passwordRptStr = String.valueOf(passwordRepeat, 0, password.length);
        if (!passwordStr.equals(passwordRptStr)) {
            System.out.println(password);
            System.out.println(passwordRepeat);
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        String firstname = this.firstnameBox.getText();   
        String lastname = this.lastnameBox.getText();   
        String email = this.emailBox.getText();    
        Role role = null;
        if (roleBox.isEnabled())
            role = (Role)roleBox.getSelectedItem();
        User user = new User();
        user.setLogin(login);
        user.setFirstname(firstname);
        user.setLastname(lastname);
        user.setPassword(passwordStr);
        user.setRole(role);
        user.setEmail(email);
        return user;
    }
    
    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        try {
            User user = getUser();
            handle.addNewUser(user);
            Utils.showMessage(this,"Пользователь успешно добавлен","",false);
            this.setVisible(false);
        }
        catch (Throwable exc) {
            Utils.showMessage(this,exc.getMessage(),"",true);
            Utils.traceAllErrors(exc);
        }
    }//GEN-LAST:event_okBtnActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelBtn;
    private javax.swing.JPasswordField emailBox;
    private javax.swing.JTextField firstnameBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JTextField lastnameBox;
    private javax.swing.JTextField loginBox;
    private javax.swing.JButton okBtn;
    private javax.swing.JPasswordField passwordBox;
    private javax.swing.JPasswordField passwordRepeatBox;
    private javax.swing.JComboBox<Role> roleBox;
    // End of variables declaration//GEN-END:variables
}
