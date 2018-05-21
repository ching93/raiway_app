/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import javax.persistence.*;

/**
 *
 * @author User
 */
@Entity
@Table(name="Trains")
public class Train {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @Column(name="name")
    private String name;
    @ManyToOne
    private TrainType trainType = new TrainType();
    public Train() {
        
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setType(TrainType trainType) {
        this.trainType = trainType;
    }
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public TrainType setType() {
        return trainType;
    }
}
