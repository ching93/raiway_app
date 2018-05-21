/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.util.List;
import javax.persistence.Entity;
import org.hibernate.TransactionException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import kiri.mavenproject1.entities.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 *
 * @author User
 */
public class DBHandle {
    private EntityManagerFactory managerFactory;
    private Log log;

    public DBHandle() {
        log = LogFactory.getLog("org.hibernate.ejb.HibernatePersistence");
        managerFactory = Persistence.createEntityManagerFactory("railway_mysql");
        
    }
    public void logIn(String username, String password) {
        
    }
    public void InsertEntity(Object e) {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(e);
        manager.getTransaction().commit();
    }
    public List<TrainType> getTrainTypes() {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        manager.create
        Query query = manager.createQuery("select id, name from traintypes");
        List<TrainType> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    public void querySelect(String query) throws TransactionException {
        EntityManager manager = managerFactory.createEntityManager();
        EntityTransaction tx = manager.getTransaction();
        tx.begin();
        
        //ByVal v = manager.createQuery("From Station", Station.class);
    }
}
