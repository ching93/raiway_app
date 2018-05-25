/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import javax.persistence.*;
import javax.persistence.Table;

/**
 *
 * @author User
 */
@Entity
@Table(name="Stations",uniqueConstraints=@UniqueConstraint(columnNames="name"))
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @Column(name="name")
    private String name;
    public Station() {
        
    }
    public Station(String name) {
        setName(name);
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    @Override
    public String toString() {
        return getName();
    }
    @Override
    public boolean equals(Object o) {
        Station s = (Station)o;
        if (this.id == s.getId())
            return true;
        else 
            return false;
    }
}
