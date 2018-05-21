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
import javax.validation.constraints.*;

/**
 *
 * @author User
 */
@Entity
@Table(name="RouteStations",uniqueConstraints=@UniqueConstraint(columnNames={"route_id","station_id"}))
public class RouteStation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @Column(name="stationOrder")
    private int stationOrder;
    @NotNull
    @ManyToOne
    private Route route = new Route();
    @NotNull
    @ManyToOne
    private Station station = new Station();
    @ManyToOne
    private Station  nextStation = new Station();
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="arriveTime")
    private Date arriveTime;
    public RouteStation() {
        
    }
    public void setId(int id) {
        this.id = id;
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
    public void setNextStation(Station nextStation) {
        this.nextStation = nextStation;
    }
    public void setArriveTime(Date arriveTime) {
        this.arriveTime = arriveTime;
    }
    public Route getRoute() {
        return route;
    }
    public Station getStation() {
        return station;
    }
    public Station getNextStation() {
        return nextStation;
    }
    public int getId() {
        return this.id;
    }
    public int getStationOrder() {
        return this.stationOrder;
    }
    public Date getArriveTime() {
        return arriveTime;
    }
}
