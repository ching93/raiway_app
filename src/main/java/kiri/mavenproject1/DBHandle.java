/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kiri.mavenproject1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import kiri.mavenproject1.entities.*;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/**
 *
 * Класс для работы с б\д
 */

public class DBHandle {
    private EntityManagerFactory managerFactory;
    private HashMap properties;
    private boolean isLogged = false;
    private String property_path = "C://properties.xml";
    private User currentUser;

    public DBHandle() {
        properties = new HashMap();
        try {
            loadProperties();
        }
        catch (Throwable exc) {
            System.out.println("Can't open property file");
        }
        try {
            System.out.println(properties.toString());
            managerFactory = Persistence.createEntityManagerFactory("railway_oracle",properties);
            System.out.println("Manager created");
            setUser();
        }
        catch (Throwable exc) {
            for (int i=0; i<10; i++) {
                System.out.println("Error: "+exc.getMessage());
                exc = exc.getCause();
                if (exc==null)
                    break;
            }
        } 
    }
    /**
     * Сохранение url в свойствах
     * @param IP
     * @param port 
     */
    private void setUrl(String IP, String port) {
        String url = "jdbc:oracle:thin:SYSTEM/1211@"+IP+":"+port+":xe";
        System.out.println(url);
        this.properties.put("javax.persistence.jdbc.url",url);
    }
    /**
     * Сохранение свойств в файл
     * @throws IOException 
     */
    public void saveProperties() throws IOException {
        Properties props = new Properties();
        if (currentUser!=null) {
            props.put("username", currentUser.getLogin());
            props.put("password", currentUser.getPassword());
        }
        for (Object key: properties.keySet())
            if (key!="javax.persistence.jdbc.url")
                props.put(key, properties.get(key));
        props.storeToXML(new FileOutputStream(property_path),"my properties");
    }
    /**
     * Загрузка свойств из файла
     * @throws IOException 
     */
    public void loadProperties() throws IOException {
        Properties props = new Properties();
        InputStream is = (InputStream)(new FileInputStream(property_path));
        props.loadFromXML(is);
        for (Object key: props.keySet())
            properties.put(key, props.get(key));
        String ip = (String)props.getOrDefault("ip",null);
        String port = (String)props.getOrDefault("port",null);
        if (ip != null && port != null)
            setUrl(ip,port);
    }
    /**
     * Авторизация
     * @param username
     * @param password
     * @param save - если true, то сохранить имя и пароль в файл
     * @return 
     */
    public boolean logIn(String username, String password, boolean save) {
        try {
            properties.put("username", username);
            properties.put("password", password);
            setUser();
            if (currentUser==null)
                throw new IllegalArgumentException("Некорректный логин или пароль");
            this.isLogged = true;
            if (save)
                saveProperties();
        }
        catch (Throwable exc) {
            return false;
        }
        return true;
    }
    private void checkUserRights(int rightToBe) {
        if (currentUser==null)
                throw new IllegalArgumentException("Не выполнен вход");
        else if (currentUser.getRole().getId()>rightToBe)
                throw new IllegalArgumentException("Недостаточно прав");
    }
    public void logOut() {
        currentUser=null;
        isLogged = false;
    }
    /**
     * Авторизация пользователя
     */
    private void setUser() {
        System.out.println(properties.toString());
        EntityManager manager = managerFactory.createEntityManager();
        Query query = manager.createQuery("SELECT u FROM User u WHERE u.login=:username AND u.password=:password");
        query.setParameter("username", (String)properties.get("username"));
        query.setParameter("password", (String)properties.get("password"));
        System.out.println(query.toString());
        currentUser = (User)query.getSingleResult();
        System.out.println(currentUser.toString());
    }
    public User getCurrentUser() {
        return this.currentUser;
    }
    public void restorePassword(String username) {
        User user;
        try {
            EntityManager manager = managerFactory.createEntityManager();
            Query q = manager.createQuery("select u from User u where login=:username");
            q.setParameter("username", username);
            user = (User)q.getSingleResult();
        }
        catch (Throwable exc) {
            System.out.println("Can't create query to DB");
            throw new IllegalArgumentException("Нет такого пользователя");
        }
        String message_body = "Ваш пароль: "+user.getPassword();
        System.out.println(message_body);
        String mail_login = (String)properties.getOrDefault("mail_login",null);
        String mail_password = (String)properties.getOrDefault("mail_password",null);
        if (mail_login==null || mail_password==null)
            throw new IllegalArgumentException("Нет логина или пароли почты");
        System.out.println(mail_login);
        System.out.println(mail_password);
        String host = "smtp.gmail.com";
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587");
        System.out.println(props.toString());
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mail_login, mail_password);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail_login,mail_password));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
            message.setSubject("Восстановление пароля (Railway Application)");
            message.setText(message_body);
            Transport.send(message);
        }
        catch (Throwable exc) {
            System.out.println(exc.getMessage());
            throw new IllegalArgumentException("Ошибка при отправлении сообщения");
        }
    }
    /**
     * Добавление нового пользователя
     * @param user 
     */
    public void addNewUser(User user) {
        checkUserRights(3);
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(user);
        manager.getTransaction().commit();        
    }
    /**
     * Роль авторизованного пользователя
     * @return 
     */
    public Role getUserRole() {
        if (currentUser!=null)
            return currentUser.getRole();
        else
            return null;
    }
    public void removeUser(User user) {
        checkUserRights(1);
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query q = manager.createQuery("delete from User u where u.id=:user_id");
        q.setParameter("user_id", user.getId());
        q.executeUpdate();
        manager.getTransaction().commit();
    }
    public List<User> getUsers() {
        checkUserRights(1);
        EntityManager manager = managerFactory.createEntityManager();
        Query q = manager.createQuery("select u from User u");
        List<User> result = q.getResultList();
        return result;
    }
    public void updateEntity(Object entity) {
        checkUserRights(2);
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        manager.merge(entity);
        manager.getTransaction().commit();
    }
    /**
     * Загрузить все существующие роли
     * @return 
     */
    public List<Role> getRoles() {
        EntityManager manager = managerFactory.createEntityManager();
        Query q = manager.createQuery("from Role");
        List<Role> result = q.getResultList();
        return result;
    }
    public boolean isLogged() {
        return isLogged;
    }
    /**
     * Сохранение сущности в базу данных
     * @param e 
     */
    private void InsertEntity(Object e) {
        checkUserRights(2);
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(e);
        System.out.println("Inserted "+e.toString());
        manager.getTransaction().commit();
    }
    /**
     * Сохранение партии сущностей в б\д
     * @param ee 
     */
    private void InsertBatchEntities(List<Object> ee) {
        checkUserRights(2);
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
    /**
     * Обновление узлов маршрута. Если обнаружена цикличность, выбрасывается ошибка.
     * Перед сохранением высчитывается расстояние до станции от начала маршрута и
     * удаляются узлы, сохраненные перед этим
     * @param ee 
     */
    public void resetRouteStations(List<Object> ee) {
        checkUserRights(2);
        // Проверка на цикличность маршрута
        for (int i=1; i<ee.size(); i++) {
            RouteStation rs = (RouteStation)ee.get(i);
            for (int j=0; j<i; j++) {
                RouteStation rs1 = (RouteStation)ee.get(j);
                if (rs1.getStation().getId()==rs.getStation().getId())
                    throw new IllegalArgumentException("Циклический маршрут");
            }
        }
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
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
        RouteStation rs = (RouteStation)ee.get(0);
        int route_id = rs.getRoute().getId();
        String sql = "DELETE FROM RouteStation rs WHERE rs.route.id='"+route_id+"'";
        System.out.println(sql);
        Query query = manager.createQuery(sql);
        int rows = query.executeUpdate();
        manager.clear();
        System.out.println("deleted "+rows+" rows");
        manager.getTransaction().commit();
        // Вставляем вычисленный маршрут
        InsertBatchEntities(ee);
    }
    /**
     * Добавление нового отправления
     * Перед добавлением добавляются строки в TicketPerBranch для всех узлов, входящих в маршрут
     * Для упрощения дальнейших запросов пересохраняем расстояния до узла в таблицу TicketPerBranch
     * @param schedule 
     * TODO проверка на возможность отправления по времени
     */
    public void addSchedule(Schedule schedule) {
        checkUserRights(2);
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
                tpb.setArriveTime(d);
                tpb.setTotalDistance(node.getTotalDistance());
                manager.persist(tpb);
                manager.flush();
                manager.clear();
                d = d.plus(rs.get(i).getTimeToCome());
            }
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            System.out.println(exc.getMessage());
        }
    }
    /**
     * 
     * @param manager
     * @param sh
     * @param st
     * @return 
     */
    private TicketPerBranch ticketToStationOnSchedule(EntityManager manager, Schedule sh, Station st) {
        String sql = "SELECT tpb FROM TicketPerBranch tpb WHERE tpb.shedule.id=:scheduleId NAD tpb.station.id=stationId";
        Query query = manager.createQuery(sql);
        query.setParameter("scheduleId", sh.getId());
        query.setParameter("stationId", st.getId());
        List<TicketPerBranch> result = query.getResultList();
        if (result.size()>0)
            return result.get(0);
        else
            return null;
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
        checkUserRights(2);
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("SELECT tt FROM TrainType tt");
        List<TrainType> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    public void addTrainType(TrainType trainType) {
        checkUserRights(2);
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
    /**
     * Поиск станций по маршруту
     * @param route
     * @return 
     */
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
    public List<RouteStation> getFirstLastRouteStations() {
        checkUserRights(2);
        EntityManager manager = managerFactory.createEntityManager();
        String sqlFirsts = "select rs from RouteStation rs where rs.stationOrder=1";
        Query q = manager.createQuery(sqlFirsts);
        List<RouteStation> fs = q.getResultList();
        System.out.println("Firsts: "+fs.size());
        String sqlLasts = "select rs from RouteStation rs where rs.stationOrder IN (select COUNT(rs1.route.id) from RouteStation rs1 group by rs1.route.id having rs1.route.id=rs.route.id)";
        Query q1 = manager.createQuery(sqlLasts);
        List<RouteStation> ls = q1.getResultList();
        ls.addAll(fs);
        return ls;
    }
    /**
     * Станции всех маршрутов
     * @return 
     */
    public List<RouteStation> getRouteStations() {
        checkUserRights(2);
        EntityManager manager = managerFactory.createEntityManager();
        Query query = manager.createQuery("SELECT rs FROM RouteStation rs order by rs.stationOrder");
        List<RouteStation> result = query.getResultList();
        return result;
    }
    /**
     * Узлы ж\д сети
     * @return 
     */
    public List<RailwaySystem> getRailwaySystem() {
        checkUserRights(2);
        EntityManager manager = managerFactory.createEntityManager();
        manager.getTransaction().begin();
        Query query = manager.createQuery("SELECT rs FROM RailwaySystem rs");
        List<RailwaySystem> result = query.getResultList();
        manager.getTransaction().commit();
        return result;
    }
    /**
     * Доабвление новой ж\д ветки
     * @param rs 
     */
    public void addRailwaySystem(RailwaySystem rs) {
        this.InsertEntity(rs);
    }
    /**
     * Проверка возможности покупки билета
     * Проверка проводится в несколько шагов:
     * 1. Проверка наличия отправления по маршруту, включаюего обе станции
     * 2. Проверка вхождения во временной интервал
     * 3. Проверка наличия свободных мест - проверяется число купленных билетов на каждый узел маршрута в сравнении с вместимостью поезда
     * Для возможности последнего в таблицу TicketPerBranch при покупке билета в поле amount добавляется значение для всех узлов между станцией отправления и станцией прибытия
     * @param depStation - станция отправления
     * @param arrStation - станция прибытия
     * @param leftBorder - левая граница дат
     * @param rightBorder - правая граница дат
     * @return 
     */
    public List<PrepareTicketResult> prepareBuyTicket(Station depStation, Station arrStation, LocalDateTime leftBorder, LocalDateTime rightBorder) {
        EntityManager manager = managerFactory.createEntityManager();
        Query query;
        // Выбор маршрутов, в которых присутствует начальная станция
        String arrStationsSql = "SELECT DISTINCT tpb.schedule.id FROM TicketPerBranch tpb WHERE tpb.station.id =:arrStationId";
        // закомментированы отладочные строки
        //query = manager.createQuery(arrStationsSql);
        //query.setParameter("arrStationId", arrStation.getId());
        //List<Integer> route_ids = query.getResultList();
        //System.out.println(route_ids.toString());
        // и конечная станция,
        String bothStations = "SELECT DISTINCT tpb1.schedule.id FROM TicketPerBranch tpb1 WHERE tpb1.station.id = :depStationId AND tpb1.schedule.id IN ("+arrStationsSql+")";
        //query = manager.createQuery(bothStations);
        //query.setParameter("arrStationId", arrStation.getId());
        //query.setParameter("depStationId", depStation.getId());
        //route_ids = query.getResultList();
        //System.out.println(route_ids.toString());
        // которые отправляются с укзанном интервале,   
        String intervaledSql ="SELECT sh.id FROM Schedule sh WHERE sh.departureTime BETWEEN :leftBorder AND :rightBorder";
        //query = manager.createQuery(intervaledSql);     
        //query.setParameter("leftBorder", leftBorder);
        //query.setParameter("rightBorder", rightBorder);
        //route_ids = query.getResultList();
        //System.out.println(route_ids.toString());
        // у которых есть свободные места
        String hasFreeSpaceSql = "SELECT tpb2.schedule.id FROM TicketPerBranch tpb2 GROUP BY tpb2.schedule.id, tpb2.schedule.route.train.capacity HAVING tpb2.schedule.id IN ("+bothStations+") AND tpb2.schedule.id IN ("+intervaledSql+") "+
                "AND MAX(tpb2.amount)<tpb2.schedule.route.train.capacity";
        //System.out.println(hasFreeSpaceSql);
        // объединяем запрос
        query = manager.createQuery("SELECT sh FROM Schedule sh WHERE sh.id IN ("+hasFreeSpaceSql+")");
        query.setParameter("leftBorder", leftBorder);
        query.setParameter("rightBorder", rightBorder);
        query.setParameter("depStationId", depStation.getId());
        query.setParameter("arrStationId", arrStation.getId());
        List<Schedule> schedules = query.getResultList();
        if (schedules.size() == 0)
            return null;
        StringBuilder ids = new StringBuilder();
        for (int i=0; i<schedules.size(); i++) {
            ids.append(schedules.get(i).getId());
            if (i!=schedules.size()-1)
                ids.append(", ");
        }
        String scheduleSql = "SELECT tpb FROM TicketPerBranch tpb WHERE tpb.schedule.id IN ("+ids.toString()+") AND tpb.station.id IN (:depStation, :arrStation)";
        System.out.println(scheduleSql);
        query = manager.createQuery(scheduleSql);
        query.setParameter("depStation", depStation.getId());
        query.setParameter("arrStation", arrStation.getId());
        List<TicketPerBranch> scheduleStations = query.getResultList();
        if (scheduleStations.size() == 0)
            return null;
        List<PrepareTicketResult> result = new ArrayList<>();
        for (Schedule schedule: schedules) {
            PrepareTicketResult current = new PrepareTicketResult();
            for (TicketPerBranch tpb: scheduleStations) {
                if (tpb.getSchedule().getId()!=schedule.getId())
                    continue;
                if (tpb.getStation().getId()==depStation.getId())
                    current.depStation = tpb;
                else
                    current.arrStation = tpb;
            }
            float distance = current.arrStation.getTotalDistance() - current.depStation.getTotalDistance();
            current.price = distance*current.arrStation.getSchedule().getPricePerKm();
            result.add(current);
        }
        return result;
    }
    /**
     * Покупка билета
     * по заранее подготовленному запросу
     * @param request 
     */
    public void buyTicket(PrepareTicketResult request) {
        if (currentUser==null)
            throw new IllegalArgumentException("Авторизуйтесь для покупки билета");
        EntityManager manager = managerFactory.createEntityManager();
        String sql = "UPDATE TicketPerBranch tpb SET tpb.amount=tpb.amount+1 WHERE tpb.schedule.id=:schedule_id";
        Query query = manager.createQuery(sql);
        System.out.println("Schedule: "+request.arrStation.getSchedule().getId());
        query.setParameter("schedule_id", request.arrStation.getSchedule().getId());
        manager.getTransaction().begin();
        query.executeUpdate();
        System.out.println("updated");
        manager.flush();
        manager.clear();
        Ticket ticket = new Ticket(request.depStation.getStation(),request.arrStation.getStation(), request.arrStation.getSchedule(), currentUser, request.price);
        System.out.println(ticket.toString());
        manager.persist(ticket);
        System.out.println("ticket persisted");
        manager.getTransaction().commit();
        
    }
    public List<TicketPerBranch> getBoughtTicketsBySchedule(Schedule sh, EntityManager manager) {
        String sql = "SELECT tpb FROM TicketsPerBranch tpb WHERE tpb.schedule.id = '"+sh.getId()+"'";
        Query query = manager.createQuery(sql);
        List<TicketPerBranch> result = query.getResultList();
        return result;
    }
    // Класс для возврашения результата проверки возможности покупки билета
    class PrepareTicketResult {
        public TicketPerBranch depStation;
        public TicketPerBranch arrStation;
        Float price;
    }
}
