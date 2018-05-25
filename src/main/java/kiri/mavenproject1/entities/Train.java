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
    @ManyToOne
    private TrainType trainType = new TrainType();
    @Column(name="capacity")
    private int capacity;
    public Train() {
        
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public void setType(TrainType trainType) {
        this.trainType = trainType;
    }
    public int getId() {
        return this.id;
    }
    public int getCapacity() {
        return this.capacity;
    }
    public TrainType getType() {
        return trainType;
    }
    @Override
    public String toString() {
        return "Train #"+id+" "+trainType.toString();
    }
}
