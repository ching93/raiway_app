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
@Table(name="Routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;
    @Column(name="price")
    private float price;
    public Route() {
        
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setPrice(float price) {
        this.price = price;
    }
    public int getId() {
        return this.id;
    }
    public float getPrice() {
        return this.price;
    }
}
