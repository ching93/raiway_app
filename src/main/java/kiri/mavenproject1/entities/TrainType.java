/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import javax.persistence.*;
import javax.validation.constraints.*;

/**
 *
 * @author User
 */
@Entity
@Table(name="TrainTypes",uniqueConstraints=@UniqueConstraint(columnNames="name"))
public class TrainType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @Column(name="name")
    private String name;
    @NotNull
    @Column(name="priceCoeff")
    private float priceCoeff = 1;
    public TrainType() {
        
    }
    public TrainType(String name) {
        setName(name);
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPriceCoeff(float priceCoeff) {
        this.priceCoeff = priceCoeff;
    }
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public float getPriceCoeff() {
        return this.priceCoeff;
    }
    @Override
    public String toString() {
        return getName();
    }
}
