package com.codegym;

import com.codegym.dao.CityDAO;
import com.codegym.dao.CountryDAO;
import com.codegym.domain.City;
import com.codegym.domain.Country;
import com.codegym.domain.CountryLanguage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

public class Main {
  private final SessionFactory sessionFactory;
  private final RedisClient redisClient;

  private final ObjectMapper mapper;

  private final CityDAO cityDAO;
  private final CountryDAO countryDAO;

  public Main() {
    sessionFactory = prepareRelationalDb();
    cityDAO = new CityDAO(sessionFactory);
    countryDAO = new CountryDAO(sessionFactory);

    redisClient = prepareRedisClient();
    mapper = new ObjectMapper();
  }

  private RedisClient prepareRedisClient() {
    return null;
  }

  private SessionFactory prepareRelationalDb() {
    try {
      // Explicitly load the MySQL driver
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Error loading MySQL driver", e);
    }

    final SessionFactory sessionFactory;
    Properties properties = new Properties();
    properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
    properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
    properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/world");
    properties.put(Environment.USER, "yerfdog");
    properties.put(Environment.PASS, "Cyril@2019");
    properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    properties.put(Environment.HBM2DDL_AUTO, "validate");
    properties.put(Environment.STATEMENT_BATCH_SIZE, "100");

    sessionFactory =
            new Configuration()
                    .addAnnotatedClass(City.class)
                    .addAnnotatedClass(Country.class)
                    .addAnnotatedClass(CountryLanguage.class)
                    .addProperties(properties)
                    .buildSessionFactory();
    return sessionFactory;
  }

  private void shutdown() {
    if (sessionFactory != null) {
      sessionFactory.close();
    }
    if (redisClient != null) {
      redisClient.shutdown();
    }
  }

  private List<City> fetchData(Main main) {
    try (Session session = main.sessionFactory.getCurrentSession()) {
      List<City> allCities = new ArrayList<>();
      session.beginTransaction();

      int totalCount = main.cityDAO.getTotalCount();
      int step = 500;
      for (int i = 0; i < totalCount; i += step) {
        allCities.addAll(main.cityDAO.getItems(i, step));
      }
      session.getTransaction().commit();
      return allCities;
    }
  }

  public static void main(String[] args) {
    Main main = new Main();
    List<City> allCities = main.fetchData(main);
    main.shutdown();
  }
}