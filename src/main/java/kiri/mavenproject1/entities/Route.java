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
@Table(name="Routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @ManyToOne(cascade=CascadeType.REMOVE)
    private Train train = new Train();
    public Route() {
        
    }
    public Route(Train train) {
        this.train = train;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTrain(Train train) {
        this.train = train;
    }
    public int getId() {
        return this.id;
    }
    public Train getTrain() {
        return this.train;
    }
    @Override
    public String toString() {
        return "Маршрут #"+id+" Поезд #"+train.getId();
    }
}
