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
@Table(name="Tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @Column(name="price")
    private float price;
    @NotNull
    @ManyToOne
    private Consumer consumer = new Consumer();
    @NotNull
    @ManyToOne(cascade=CascadeType.PERSIST)
    private Schedule schedule = new Schedule();
    @NotNull
    @ManyToOne
    private Station depStation = new Station();
    @NotNull
    @ManyToOne
    private Station arrStation = new Station();
    public Ticket() {
        
    }
    public Ticket(Station depStation, Station arrStation, Schedule schedule, Consumer consumer) {
        this.depStation = depStation;
        this.arrStation = arrStation;
        this.schedule = schedule;
        this.consumer = consumer;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDepStation(Station depStation) {
        this.depStation = depStation;
    }
    public void setArrStation(Station arrStation) {
        this.arrStation = arrStation;
    }
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public int getId() {
        return this.id;
    }
    public Station getDepStation() {
        return depStation;
    }
    public Station getArrStation() {
        return arrStation;
    }
    public Schedule getSchedule() {
        return schedule;
    }
    public Consumer getConsumer() {
        return consumer;
    }
    public float getPrice() {
        return price;
    }
    @Override
    public String toString() {
        return "Билет #"+id+" "+consumer.getLastname();
    }
}
