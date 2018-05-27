/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1.entities;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 * @author User
 */
@Entity
@Table(name="TicketsPerBranch",uniqueConstraints={@UniqueConstraint(columnNames={"station_id","schedule_id"})})
public class TicketPerBranch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @NotNull
    @Column(name="amount")
    private int amount;
    @NotNull
    @ManyToOne(cascade=CascadeType.REMOVE)
    private Schedule schedule = new Schedule();
    @Column(name="arriveTime")
    private LocalDateTime arriveTime;
    @NotNull
    @Column(name="totalDistance")
    private float totalDistance = 0;
    @NotNull
    @ManyToOne
    private Station station = new Station();
    public TicketPerBranch() {
        
    }
    public TicketPerBranch(Schedule schedule, Station station, int amount) {
        this.schedule = schedule;
        this.station = station;
        this.amount = amount;
    }
    public void setTotalDistance(float totalDistance) {
        this.totalDistance = totalDistance;
    }
    public float getTotalDistance() {
        return this.totalDistance;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
    public void setArriveTime(LocalDateTime arriveTime) {
        this.arriveTime = arriveTime;
    }
    public void setStation(Station station) {
        this.station = station;
    }
    public int getId() {
        return this.id;
    }
    public int getAmount() {
        return this.amount;
    }
    public Schedule getSchedule() {
        return this.schedule;
    }
    public LocalDateTime getArriveTime() {
        return arriveTime;
    }
    public Station getStation() {
        return this.station;
    }
    @Override
    public String toString() {
        return "Отправление #"+schedule.getId()+" Станция #"+station.getId();
    }
}
