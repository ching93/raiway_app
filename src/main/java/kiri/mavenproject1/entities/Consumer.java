/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author User
 */
@Entity
@Table(name="Consumers")
public class Consumer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @Column(name="firstname")
    private String firstname;
    @NotNull
    @Column(name="lastname")
    private String lastname;
    @NotNull
    @Column(name="email",unique=true)
    private String email;
    public Consumer() {
        
    }
    public Consumer(String firstname, String lastname, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public int getId() {
        return this.id;
    }
    public String getFirstname() {
        return firstname;
    }
    public String getLastname() {
        return lastname;
    }
    public String getEmail() {
        return email;
    }
    @Override
    public String toString() {
        return firstname+" "+lastname;
    }
}
