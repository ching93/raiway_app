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
    private User consumer = new User();
    @NotNull
    @ManyToOne
    private Schedule schedule = new Schedule();
    @NotNull
    @ManyToOne
    private Station depStation = new Station();
    @NotNull
    @ManyToOne
    private Station arrStation = new Station();
    public Ticket() {
        
    }
    public Ticket(Station depStation, Station arrStation, Schedule schedule, User consumer, Float price) {
        this.depStation = depStation;
        this.arrStation = arrStation;
        this.schedule = schedule;
        this.consumer = consumer;
        this.price = price;
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
    public void setConsumer(User consumer) {
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
    public User getConsumer() {
        return consumer;
    }
    public float getPrice() {
        return price;
    }
    @Override
    public String toString() {
        return consumer.getLastname()+" на отправление "+schedule.toString()+"c "+depStation.toString()+" до "+arrStation.toString();
    }
}
