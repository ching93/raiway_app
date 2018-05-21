/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 *
 * @author User
 */
@Entity
@Table(name="RailwaySystem",uniqueConstraints=@UniqueConstraint(columnNames={"out_station_id","in_station_id"}))
public class RailwaySystem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @Column(name="distance")
    private float distance;
    @ManyToOne
    private Station out_station;
    @ManyToOne
    private Station in_station;
    public RailwaySystem() {

    }
    public void setDistance(float distance) {
        this.distance = distance;
    }
    public float getDistance() {
        return this.distance;
    }
    public void setOutStation(Station out) {
        this.out_station = out;
    }
    public Station getOutStation() {
        return this.out_station;
    }
    public void setInStations(Station in) {
        this.in_station = in;
    }
    public Station getInStations() {
        return this.in_station;
    }
}
