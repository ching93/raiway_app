/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import java.util.Date;
import javax.validation.constraints.*;

/**
 *
 * @author User
 */
@Entity
@Table(name="RouteStations",uniqueConstraints={@UniqueConstraint(columnNames={"route_id","stationOrder"})})
public class RouteStation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @Column(name="stationOrder")
    private int stationOrder;
    @NotNull
    @ManyToOne(cascade=CascadeType.REMOVE)
    private Route route = new Route();
    @NotNull
    @ManyToOne
    private Station station = new Station();
    @Column(name="arriveTime")
    private Duration timeToCome;
    @Column(name="stayTime")
    private Duration stayTime;
    @NotNull
    @Column(name="totalDistance")
    private float totalDistance = 0;
    public RouteStation() {
        
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }
    public float getTotalDistance() {
        return this.totalDistance;
    }
    public void setStationOrder(int stationOrder) {
        this.stationOrder = stationOrder;
    }
    public void setRoute(Route  route) {
        this.route = route;
    }
    public void setStation(Station  station) {
        this.station = station;
    }
    public void setTimeToCome(Duration timeToCome) {
        this.timeToCome = timeToCome;
    }
    public void setStayTime(Duration stayTime) {
        this.stayTime = stayTime;
    }
    public Route getRoute() {
        return route;
    }
    public Station getStation() {
        return station;
    }
    public int getId() {
        return this.id;
    }
    public int getStationOrder() {
        return this.stationOrder;
    }
    public Duration getTimeToCome() {
        return timeToCome;
    }
    public Duration getStayTime() {
        return stayTime;
    }
}
