/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import java.time.Duration;
import java.time.LocalDateTime;
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
    @Column(name="departureTime")
    private LocalDateTime departureTime;
    @Column(name="delay")
    private Duration delay;
    @Column(name="pricePerKm")
    private float pricePerKm;
    public Schedule() {
        
    }
    public Schedule(Route route, LocalDateTime depTime, float pricePerKm) {
        this.route = route;
        this.departureTime = depTime;
        this.pricePerKm = pricePerKm;
        this.delay = Duration.ZERO;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setDelay(Duration delay) {
        this.delay = delay;
    }
    public void setPricePerKm(float pricePerKm) {
        this.pricePerKm = pricePerKm;
    }
    public void setRoute(Route  route) {
        this.route = route;
    }
    public void setDepartureTime(LocalDateTime date) {
        this.departureTime = date;
    }
    public Route getRoute() {
        return route;
    }
    public int getId() {
        return this.id;
    }
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    public Duration getDelay() {
        return this.delay;
    }
    public float getPricePerKm() {
        return pricePerKm;
    }
}
