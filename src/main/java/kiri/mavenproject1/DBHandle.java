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
import java.util.Comparator;
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
import javax.persistence.Parameter;
/**
 *
 * Класс для работы с б\д
 */

public class DBHandle {
    private EntityManagerFactory managerFactory;
    private EntityManager manager;
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
            if (currentUser!=null)
                isLogged=true;
            else
                isLogged=false;
        }
        catch (Throwable exc) {
            Utils.traceAllErrors(exc);
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
            isLogged=false;
            return false;
        }
        return true;
    }
    private void checkUserRights(int rightToBe) {
        if (currentUser==null && rightToBe<3)
                throw new IllegalArgumentException("Не выполнен вход");
        else if (currentUser.getRole().getId()>rightToBe)
                throw new IllegalArgumentException("Недостаточно прав");
    }
    public void logOut() {
        currentUser=null;
        manager.close();
        isLogged = false;
    }
    /**
     * Авторизация пользователя
     */
    private void setUser() {
        System.out.println(properties.toString());
        manager = managerFactory.createEntityManager();
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
        if (user.getRole()!=null &&user.getRole().getId()<=2)
            checkUserRights(1);
        if (user.getRole()==null) {
            Role role = this.getRoles().get(2);
            user.setRole(role);
        }
        this.InsertEntity(user);
    }
    public void updateUser(User toUpdate) {
        if (this.currentUser!=null && currentUser.getId()==toUpdate.getId()) {
            this.updateEntity(toUpdate);
        }
        else
            throw new IllegalArgumentException("Нельзя сохранить");
    }
    public List<User> getEmployees () {
        checkUserRights(1);
        Query q = manager.createQuery("select u from User u where u.role.id=2");
        List<User> result = q.getResultList();
        return result;
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
        if (user.getRole().getId()<=2)
            checkUserRights(1);
        manager.getTransaction().begin();
        Query q = manager.createQuery("delete from User u where u.id=:user_id");
        q.setParameter("user_id", user.getId());
        q.executeUpdate();
        manager.getTransaction().commit();
    }
    public List<User> getUsers() {
        checkUserRights(1);
        Query q = manager.createQuery("select u from User u");
        List<User> result = q.getResultList();
        return result;
    }
    private void removeEntities(Iterable<Object> entities) {
        try {
            manager.getTransaction().begin();
            for (Object entity: entities) {
                manager.remove(entity);
            }
            manager.flush();
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            throw new IllegalArgumentException("Нельзя удалить объект");
        }
    }
    public List<Ticket> getTicketsOfUser(User user) {
        Query q = manager.createQuery("select t from Ticket t where t.consumer.id = :userId");
        q.setParameter("userId", user.getId());
        List<Ticket> result = q.getResultList();
        return result;
    }
    private void removeEntity(Object entity) {
        try {
            manager.getTransaction().begin();
            manager.remove(entity);
            manager.flush();
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            throw new IllegalArgumentException("Нельзя удалить объект");
        }
    }
    private void updateEntity(Object entity) {
        try {
            manager.getTransaction().begin();
            manager.merge(entity);
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            throw exc;
        }
    }
    private void updateEntities(Iterable<Object> entities) {
        try {
            manager.getTransaction().begin();
            for (Object entity: entities) {
                manager.merge(entity);
                System.out.println("Updated "+entity.toString());
                manager.flush();
            }
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            throw exc;
        }
    }
    /**
     * Загрузить все существующие роли
     * @return 
     */
    public List<Role> getRoles() {
        Query q = manager.createQuery("from Role");
        List<Role> result = q.getResultList();
        if (result.isEmpty()) {
            manager.getTransaction().begin();
            manager.createNativeQuery("insert into Roles (id,name) values (1,'Admin')").executeUpdate();
            manager.createNativeQuery("insert into Roles (id,name) values (2,'Manager')").executeUpdate();
            manager.createNativeQuery("insert into Roles (id,name) values (3,'Customer')").executeUpdate();
            manager.flush();
            manager.createNativeQuery("insert into Users (id,email,firstname,lastname,login,password,role_id) values (0,'no','root','root','root','qwerty1211',1)").executeUpdate();
            manager.flush();
            manager.getTransaction().commit();
            return getRoles();
        }
        else
            return result;
    }
    public boolean isLogged() {
        return isLogged;
    }
    public List<Object[]> performCustomQuery(String query) {
        Query q = manager.createNativeQuery(query);
        List<Object[]> result = q.getResultList();
        return result;
    }
    /**
     * Сохранение сущности в базу данных
     * @param e - Entity
     */
    private void InsertEntity(Object e) {
        try {
            manager.getTransaction().begin();
            manager.persist(e);
            System.out.println("Inserted "+e.toString());
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            throw exc;
        }
    }
    /**
     * Сохранение партии сущностей в б\д
     * @param ee 
     */
    private void InsertEntities(Iterable<Object> ee) {
        try {
            manager.getTransaction().begin();
            for (Object e: ee) {
                manager.persist(e);
                manager.flush();
                System.out.println("Inserted "+e.toString());
            }
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            manager.getTransaction().rollback();
            throw exc;
        }
    }
    public Object getById(Class T, int id) {
        return manager.find(T, id);
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
        // Заполняем поле пути до станции от начачальной точки маршрута
        List<RailwaySystem> branches = this.getRailwaySystem();
        List<Object> toUpdate = new ArrayList<>();
        //List<Object> toInsert = new ArrayList<>();
        RouteStation prevRs = (RouteStation)ee.get(0);
        toUpdate.add(prevRs);
        float distance = 0;
        for (int i=1; i<ee.size(); i++) {
            RouteStation rs = (RouteStation)ee.get(i);
            System.out.println(rs.getStationOrder());
            distance += getDistance(branches, rs.getStation(),prevRs.getStation());
            rs.setTotalDistance(distance);
            prevRs = rs;
            toUpdate.add(rs);
        }
        List<RouteStation> rss = this.getRouteStationsByRoute(((RouteStation)ee.get(0)).getRoute());
        List<Object> toDelete = new ArrayList<>();
        if (rss.size()>toUpdate.size())
            toDelete.addAll(rss.subList(toUpdate.size(), rss.size()));
        this.removeRoutes(toDelete);
        this.updateEntities(toUpdate);
        //this.InsertEntities(toInsert);
    }
    /**
     * Добавление нового отправления
     * Перед добавлением добавляются строки в TicketPerBranch для всех узлов, входящих в маршрут
     * Для упрощения дальнейших запросов пересохраняем расстояния до узла в таблицу TicketPerBranch
     * @param schedule 
     * TODO проверка на возможность отправления по времени
     */
    public void addSchedules(Iterable<Object> schedules) {
        checkUserRights(2);
        manager.getTransaction().begin();
        //List<Object> toUpdate = new ArrayList<>();
        for (Object item: schedules) {
            Schedule schedule = (Schedule)item;
            schedule = manager.merge(schedule);
            manager.flush();
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
                d = d.plus(rs.get(i).getTimeToCome());
                manager.merge(tpb);
            }
            manager.flush();
        }
        manager.getTransaction().commit();
        //this.updateEntities(schedules);
        //this.updateEntities(toUpdate);
    }
    public void addStations(Iterable<Object> stations) {
        this.checkUserRights(2);
        this.updateEntities(stations);
    }
    public void addTrainTypes(Iterable<Object> tt) {
        checkUserRights(2);
        this.updateEntities(tt);
    }
    public void addTrainType(TrainType trainType) {
        checkUserRights(1);
        this.InsertEntity(trainType);
    }
    public void addTrains(Iterable<Object> entities) {
        checkUserRights(2);
        this.updateEntities(entities);
    }
    public void addTrain(Train train) {
        checkUserRights(2);
        this.updateEntity(train);
    }
    public void addRoute(Route route) {
        checkUserRights(2);
        this.updateEntity(route);
    }
    public void addRoutes(Iterable<Object> entities) {
        checkUserRights(2);
        this.updateEntities(entities);
    }
    public void addTrainCrews(Iterable<Object> entities) {
        checkUserRights(2);
        this.updateEntities(entities);
    }
    public void addRoles(Iterable<Object> entities) {
        checkUserRights(1);
        this.updateEntities(entities);
    }
    /**
     * Доабвление новой ж\д ветки
     * @param branches 
     */
    public void addRailwaySystem(Iterable<Object> branches) {
        checkUserRights(2);
        for (Object item: branches) {
            RailwaySystem rs = (RailwaySystem)item;
            if (rs.getInStation().getId()==rs.getOutStation().getId()) {
                throw new IllegalArgumentException("Циклическая ветка");
            }
        }
        this.updateEntities(branches);
    }
    
    public void removeBranches(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
    }
    public void removeStations(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
    }
    public void removeTrainTypes(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
    }
    public void removeTrainCrews(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
    }
    public void removeTrains(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
    }
    public void removeRoles(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
    }
    public void removeSchedules(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
    }
    public void removeRoutes(Iterable<Object> entities) {
        checkUserRights(2);
        this.removeEntities(entities);
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
        manager.clear();
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
        Query query = manager.createQuery("SELECT tt FROM TrainType tt");
        List<TrainType> result = query.getResultList();
        return result;
    }
    public List<Station> getStations() {
        Query query = manager.createQuery("select t from Station t");
        List<Station> result = query.getResultList();
        return result;
    }
    public List<Train> getTrains() {
        Query query = manager.createQuery("select t from Train t");
        List<Train> result = query.getResultList();
        return result;
    }
    public List<Route> getRoutes() {
        Query query = manager.createQuery("select t from Route t");
        List<Route> result = query.getResultList();
        return result;
    }
    public List<TrainCrew> getTrainCrews() {
        Query query = manager.createQuery("select tc from TrainCrew tc");
        List<TrainCrew> result = query.getResultList();
        return result;
    }
    public List<Schedule> getSchedules() {
        Query query = manager.createQuery("select sh from Schedule sh");
        List<Schedule> result = query.getResultList();
        return result;
    }
    /**
     * Поиск станций по маршруту
     * @param route
     * @return 
     */
    public List<RouteStation> getRouteStationsByRoute(Route route) {
        Query query = manager.createQuery("SELECT rs FROM RouteStation rs WHERE rs.route.id = "+route.getId()+" order by rs.stationOrder");
        List<RouteStation> result = query.getResultList();
        return result;
    }
    public List<RouteStation> getFirstLastRouteStations() {
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
        Query query = manager.createQuery("SELECT rs FROM RouteStation rs order by rs.stationOrder");
        List<RouteStation> result = query.getResultList();
        return result;
    }
    /**
     * Узлы ж\д сети
     * @return 
     */
    public List<RailwaySystem> getRailwaySystem() {
        Query query = manager.createQuery("SELECT rs FROM RailwaySystem rs");
        List<RailwaySystem> result = query.getResultList();
        return result;
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
    public List<PrepareTicketResult> prepareBuyTicket(Station depStation, Station arrStation, LocalDateTime leftBorder, LocalDateTime rightBorder, int sortByPrice, int sortByDate) {
        if (currentUser==null)
            throw new IllegalArgumentException("Авторизуйтесь для покупки билета");
        Query query;
        // Выбор маршрутов, в которых присутствует начальная станция
        String arrStationsSql = "SELECT DISTINCT tpb.schedule.id FROM TicketPerBranch tpb WHERE tpb.station.id=:arrStationId";
        // и конечная станция,
        String bothStations = "SELECT DISTINCT tpb1.schedule.id FROM TicketPerBranch tpb1 WHERE tpb1.station.id = :depStationId AND tpb1.schedule.id IN ("+arrStationsSql+")";
        // которые отправляются с укзанном интервале,   
        String intervaledSql ="SELECT sh.id FROM Schedule sh WHERE sh.departureTime BETWEEN :leftBorder AND :rightBorder";
        // у которых есть свободные места
        String priceSql;
        String dateSql;
        String hasFreeSpaceSql = "SELECT tpb2 FROM TicketPerBranch tpb2 WHERE tpb2.schedule.id IN ("+bothStations+") AND tpb2.schedule.id IN ("+intervaledSql+") ORDER BY tpb2.schedule.id, tpb2.totalDistance";
        // объединяем запрос
        query = manager.createQuery(hasFreeSpaceSql);//"SELECT sh FROM Schedule sh WHERE sh.id IN ("+hasFreeSpaceSql+")");
        query.setParameter("leftBorder", leftBorder);
        query.setParameter("rightBorder", rightBorder);
        query.setParameter("depStationId", depStation.getId());
        query.setParameter("arrStationId", arrStation.getId());
        List<TicketPerBranch> tpbs = query.getResultList();
        if (tpbs.isEmpty())
            return null;
        Schedule currentSchedule = tpbs.get(0).getSchedule();
        List<PrepareTicketResult> result = new ArrayList<>();
        int maxFillness = tpbs.get(0).getAmount();
        int rangeBeginIndex=-1;
        boolean skip = false;
        int nextId  = depStation.getId();
        for (int i=0; i<tpbs.size(); i++) {
            if (tpbs.get(i).getStation().getId()!=nextId && !skip)
                continue;
            else if (tpbs.get(i).getStation().getId()==depStation.getId())
                rangeBeginIndex=i;
            skip=true;
            
            if (tpbs.get(i).getAmount()>maxFillness) {
                maxFillness=tpbs.get(i).getAmount();
            }
            if (tpbs.get(i).getStation().getId()==arrStation.getId()) {
                if (maxFillness<=currentSchedule.getTrain().getCapacity()) {
                    PrepareTicketResult res0 = new PrepareTicketResult();
                    res0.depStation = tpbs.get(rangeBeginIndex);
                    res0.arrStation = tpbs.get(i);
                    res0.placesLeft = currentSchedule.getTrain().getCapacity() - maxFillness;
                    res0.price = (res0.arrStation.getTotalDistance()-res0.depStation.getTotalDistance())*currentSchedule.getPricePerKm()
                            *currentSchedule.getTrain().getType().getPriceCoeff();
                    result.add(res0);
                    rangeBeginIndex=-1;
                    maxFillness = tpbs.get(i).getAmount();
                    if (tpbs.size()!=i+1)
                        currentSchedule = tpbs.get(i+1).getSchedule();
                    nextId=depStation.getId();
                    skip = false;
                }
            }
        }
        if (sortByPrice!=0)
            result.sort((PrepareTicketResult o1, PrepareTicketResult o2) -> o1.price > o2.price ? sortByPrice : sortByPrice*(-1));
        if (sortByDate!=0)
            result.sort((PrepareTicketResult o1, PrepareTicketResult o2) -> o1.depStation.getArriveTime().isBefore(o2.depStation.getArriveTime()) ? sortByDate : sortByDate*(-1));
        return result;
    }
    /**
     * Покупка билета
     * по заранее подготовленному запросу
     * @param request 
     */
    public void buyTicket(PrepareTicketResult request, int amount) {
        if (currentUser==null)
            throw new IllegalArgumentException("Авторизуйтесь для покупки билета");
        try {
            String sql = "UPDATE TicketPerBranch tpb SET tpb.amount=tpb.amount+:amount WHERE tpb.schedule.id=:scheduleId AND tpb.arriveTime BETWEEN :depTime AND :arrTime";
            Query query = manager.createQuery(sql);
            query.setParameter("scheduleId", request.arrStation.getSchedule().getId());
            query.setParameter("depTime", request.depStation.getArriveTime());
            query.setParameter("arrTime", request.arrStation.getArriveTime());
            query.setParameter("amount", amount);
            manager.getTransaction().begin();
            query.executeUpdate();
            System.out.println("Tpb is updated");
            manager.flush();
            for (int i=0; i<amount; i++) {
                Ticket ticket = new Ticket(request.depStation.getStation(),request.arrStation.getStation(), request.arrStation.getSchedule(), currentUser, request.price);
                System.out.println(ticket.toString());
                manager.persist(ticket);
            }
            manager.flush();
            System.out.println("Tickets persisted");
            manager.getTransaction().commit();
        }
        catch (Throwable exc) {
            Utils.traceAllErrors(exc);
            throw new IllegalArgumentException("Невозможно купить билет");
        }
        
    }
    public void returnTicket(Ticket ticket) {
        if (LocalDateTime.now().isAfter(ticket.getSchedule().getDepartureTime()))
            throw new IllegalArgumentException("Поезд уже выехал");
        manager.getTransaction().begin();
        String fromSql = "select tpb1.totalDistance from TicketPerBranch tpb1 where tpb1.schedule.id=:scheduleId and tpb1.station.id=:from";
        String toSql = "select tpb1.totalDistance from TicketPerBranch tpb1 where tpb1.schedule.id=:scheduleId and tpb1.station.id=:to";
        Query q = manager.createQuery("UPDATE TicketPerBranch tpb SET tpb.amount=tpb.amount-1 WHERE tpb.schedule.id=:scheduleId AND tpb.totalDistance BETWEEN ("+fromSql+") AND ("+toSql+")");
        q.setParameter("scheduleId", ticket.getSchedule().getId());
        q.setParameter("from", ticket.getDepStation().getId());
        q.setParameter("to", ticket.getDepStation().getId());
        int row = q.executeUpdate();
        manager.flush();
        System.out.println("Удалено "+row+" строк");
        q = manager.createQuery("delete from Ticket t where t.id=:id");
        q.setParameter("id", ticket.getId());
        row = q.executeUpdate();
        System.out.println("Удалено "+row+" строк");
        manager.flush();
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
        int placesLeft;
    }
}
