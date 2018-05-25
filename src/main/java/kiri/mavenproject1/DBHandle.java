/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    HashMap properties;

    public DBHandle() {
        properties = new HashMap();
        try {
            managerFactory = Persistence.createEntityManagerFactory("railway_mysql");
        }
        catch (Throwable exc) {
            System.out.println(exc.getMessage());
        }
    }
    public boolean logIn(String username, String password) {
        try {
            properties.put("javax.persistence.jdbc.user", username);
            properties.put("javax.persistence.jdbc.password", password);
            managerFactory = Persistence.createEntityManagerFactory("railway_mysql",properties);
            
        }
        catch (Throwable exc) {
            return false;
        }
        return true;
    }
    private void InsertEntity(Object e) {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(e);
        System.out.println("Inserted "+e.toString());
        manager.getTransaction().commit();
    }
    private void InsertBatchEntities(List<Object> ee) {
        EntityManager manager = managerFactory.createEntityManager();
        try {
            manager.getTransaction().begin();
            for (Object e: ee) {
                manager.persist(e);
                System.out.println("Inserted "+e.toString());
                manager.flush();
                manager.clear();
            };
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            System.out.println(exc.getMessage());
        }
    }
    public void resetRouteStations(List<Object> ee) {
        // Проверка на цикличность маршрута
        for (int i=1; i<ee.size(); i++) {
            RouteStation rs = (RouteStation)ee.get(i);
            for (int j=0; j<i; j++) {
                RouteStation rs1 = (RouteStation)ee.get(j);
                if (rs1.getStation().getId()==rs.getStation().getId())
                    throw new IllegalArgumentException("Циклический маршрут");
            }
        }
        // Заполняем поле пути до станции от начачальной точки маршрута
        List<RailwaySystem> branches = this.getRailwaySystem();
        RouteStation prevRs = (RouteStation)ee.get(0);
        float distance = 0;
        for (int i=1; i<ee.size(); i++) {
            RouteStation rs = (RouteStation)ee.get(i);
            System.out.println(rs.getStationOrder());
            distance += getDistance(branches, rs.getStation(),prevRs.getStation());
            rs.setTotalDistance(distance);
            prevRs = rs;
        }
        // Удаляем записанную в базе данных маршрут
        EntityManager manager = managerFactory.createEntityManager();
        RouteStation rs = (RouteStation)ee.get(0);
        int route_id = rs.getRoute().getId();
        manager.getTransaction().begin();
        String sql = "DELETE FROM RouteStation rs WHERE rs.route.id='"+route_id+"'";
        System.out.println(sql);
        Query query = manager.createQuery(sql);
        int rows = query.executeUpdate();
        System.out.println("deleted "+rows+" rows");
        manager.getTransaction().commit();
        // Вставляем вычисленный маршрут
        InsertBatchEntities(ee);
    }
    public void addSchedule(Schedule schedule) {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        try {
            manager.persist(schedule);
            manager.flush();
            manager.clear();
            List<RouteStation> rs = this.getRouteStationsByRoute(schedule.getRoute());
            LocalDateTime d = schedule.getDepartureTime();
            
            for (int i=0; i<rs.size(); i++) {
                RouteStation node = rs.get(i);
                TicketPerBranch tpb = new TicketPerBranch();
                tpb.setSchedule(schedule);
                tpb.setStation(node.getStation());
                tpb.setAmount(0);
                manager.persist(tpb);
                manager.flush();
                manager.clear();
            }
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            System.out.println(exc.getMessage());
        }
    }
    private float getDistance(List<RailwaySystem> branches, Station out, Station in) {
        for (RailwaySystem branch: branches) {
            if ((out.getId()==branch.getOutStation().getId() && in.getId()==branch.getInStation().getId()) || 
                    (out.getId()==branch.getInStation().getId() && in.getId()==branch.getOutStation().getId())) {
                    return branch.getDistance();
            }                
        }
        throw new IllegalArgumentException("Несуществующая ветка");
    }
    public List<TrainType> getTrainTypes() {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("SELECT tt FROM TrainType tt");
        List<TrainType> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    public void addTrainType(TrainType trainType) {
        this.InsertEntity(trainType);
    }
    public List<Station> getStations() {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("select t from Station t");
        List<Station> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    public void addStation(Station station) {
        this.InsertEntity(station);
    }
    public List<Train> getTrains() {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("select t from Train t");
        List<Train> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    public void addTrain(Train train) {
        this.InsertEntity(train);
    }
    public List<Route> getRoutes() {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("select t from Route t");
        List<Route> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    public void addRoute(Route route) {
        this.InsertEntity(route);
    }
    public List<RouteStation> getRouteStationsByRoute(Route route) {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        List<RouteStation> result;
        try {
            Query query = manager.createQuery("SELECT rs FROM RouteStation rs WHERE rs.route.id = "+route.getId()+" order by rs.stationOrder");
            result = query.getResultList();
            manager.getTransaction().commit();
        }
        catch (NullPointerException exc) {
            manager.getTransaction().commit();
            throw exc;
        }
        return result;
    }
    public List<RailwaySystem> getRailwaySystem() {
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("SELECT rs FROM RailwaySystem rs");
        List<RailwaySystem> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    public void addRailwaySystem(RailwaySystem rs) {
        this.InsertEntity(rs);
    }
    public PrepareTicketResult prepareBuyTicket(Station depStation, Station arrStation, LocalDateTime leftBorder, LocalDateTime rightBorder) {
        EntityManager manager = managerFactory.createEntityManager();
        // Выбор маршрутов, в которых присутствует начальная станция
        String arrRoutes = "SELECT DISTINCT rs1.route.id FROM RouteStation rs1 WHERE rs1.station.id ='"+arrStation.getId()+"'";
        System.out.println(arrRoutes);
        // и конечная станция,
        String bothRoutes = "SELECT DISTINCT rs.route.id FROM RouteStation rs WHERE rs.station.id = '"+depStation.getId()+"' AND rs.id IN ("+arrRoutes+")";
        System.out.println(bothRoutes);
        // объединяем запрос
        // которые отправляются с укзанном интервале,
        String intervaledSql ="SELECT sh.route.id FROM Schedule sh WHERE sh.departureTime BETWEEN STR_TO_DATE('"+leftBorder.toString()+"') AND STR_TO_DATE('"+leftBorder.toString()+"')"+
                " AND sh.route.id IN ("+bothRoutes+")";
        System.out.println(intervaledSql);
        // у которых есть свободные места
        String hasFreeSpaceSql = "SELECT tpb.schedule.id, MAX(tpb.amount) maxAmount FROM TicketsPerBranch tpb GROUP BY tpb.schedule.id HAVING tpb.schedule.id IN ("+intervaledSql+")";
        System.out.println(hasFreeSpaceSql);
        String scheduleSql = "SELECT sh FROM Schedule sh WHERE sh.id IN ("+hasFreeSpaceSql+")";
        System.out.println(scheduleSql);
        manager.getTransaction().begin();
        Query query = manager.createQuery(scheduleSql);
        List<Schedule> schedules = query.getResultList();
        query = manager.createQuery("SELECT rs FROM RouteStation rs");
        List<RouteStation> allRs = query.getResultList();
        List<RouteStation> arrStations = new ArrayList<>();
        List<RouteStation> depStations = new ArrayList<>();
        for (Schedule schedule: schedules)
            for (RouteStation rs: allRs) {
                if (rs.getRoute().getId() == schedule.getRoute().getId())
                    if (rs.getStation().getId() == depStation.getId())
                        depStations.add(rs);
                    else if (rs.getStation().getId() == arrStation.getId())
                        arrStations.add(rs);
            }
        PrepareTicketResult result = new PrepareTicketResult();
        result.arrStation = arrStations;
        result.depStation = depStations;
        result.schedule = schedules;
        manager.getTransaction().commit();
        return result;
    }
    public List<TicketPerBranch> getBoughtTicketsBySchedule(Schedule sh, EntityManager manager) {
        String sql = "SELECT tpb FROM TicketsPerBranch tpb WHERE tpb.schedule.id = '"+sh.getId()+"'";
        Query query = manager.createQuery(sql);
        List<TicketPerBranch> result = query.getResultList();
        return result;
    }
    class PrepareTicketResult {
        public List<RouteStation> depStation;
        public List<RouteStation> arrStation;
        public List<Schedule> schedule;
        
        public PrepareTicketResult() {
            
        }
    }
}
