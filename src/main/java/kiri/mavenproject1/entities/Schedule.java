/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import java.util.Date;

/**
 *
 * @author User
 */
@Entity
@Table(name="Schedules")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @ManyToOne
    private Route route = new Route();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="departureTime")
    private Date departureTime;
    @Column(name="delay")
    private int delay;
    public Schedule() {
        
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDelay(int delay) {
        this.delay = delay;
    }
    public void setRoute(Route  route) {
        this.route = route;
    }
    public Route getRoute() {
        return route;
    }
    public int getId() {
        return this.id;
    }
    public Date getDepartureTime() {
        return departureTime;
    }
    public int getDelay() {
        return this.delay;
    }
}
