package org.example;

import java.util.List;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

/**
 * Manages animal entities in the database using Hibernate. Provides functionality for CRUD
 * operations on Animal objects.
 */
public class AnimalManager {
  private SessionFactory sessionFactory;

  /**
   * Initializes the AnimalManager by setting up Hibernate configuration. Creates an in-memory H2
   * database with specified properties.
   */
  public AnimalManager() {
    // Configure hibernate using properties
    Properties properties = new Properties();
    properties.put(Environment.DRIVER, "org.h2.Driver");
    properties.put(Environment.URL, "jdbc:h2:mem:test");
    properties.put(Environment.USER, "sa");
    properties.put(Environment.PASS, "");
    properties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
    properties.put(Environment.SHOW_SQL, "true");
    properties.put(Environment.FORMAT_SQL, "true");
    properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
    properties.put(Environment.HBM2DDL_AUTO, "update");
    sessionFactory =
        new Configuration()
            .setProperties(properties)
            .addAnnotatedClass(Animal.class)
            .buildSessionFactory();
  }

  /**
   * Adds predefined animal records to the database. Creates three animals (Dog, Cat, Lion) with
   * their respective details.
   */
  // Insert objects to the database (DML operation)
  public void addAnimals() {
    Animal animal1 = new Animal(1, "Dog", "Canidae", "puppy");
    Animal animal2 = new Animal(2, "Cat", "Felidae", "kitty");
    Animal animal3 = new Animal(3, "Lion", "Carnivora", "cab");
    try (Session session = sessionFactory.openSession()) {
      // Start a transaction
      System.out.println("Query to insert objects to the database:");
      Transaction transaction = session.beginTransaction();
      session.persist(animal1);
      session.persist(animal2);
      session.persist(animal3);
      transaction.commit();
    }
  }

  // Get all animals from the database
  /**
   * Retrieves all animals from the database.
   *
   * @return List of all Animal objects stored in the database
   */
  // Retrieve all animals from the database (DDL operation)
  public List<Animal> getAnimals() {
    try (Session session = sessionFactory.openSession()) {
      return session.createQuery("from Animal", Animal.class).list();
    }
  }

  /**
   * Removes a specified animal from the database. Only removes the animal if its ID is 1.
   *
   * @param animal The Animal object to be removed
   */
  // Remove animals from the database (DML operation)
  public void removeUser(Animal animal) {
    try (Session session = sessionFactory.openSession()) {
      Transaction transaction = session.beginTransaction();
      session.remove(animal.getId() == 1 ? animal : null);
      transaction.commit();
    }
  }

  // Main method
  /**
   * Main method to demonstrate the functionality of AnimalManager. Adds animals, displays them,
   * removes one, and shows the updated list.
   *
   * @param args Command line arguments (not used)
   */
  public static void main(String[] args) {
    AnimalManager animalManager = new AnimalManager();
    animalManager.addAnimals();
    List<Animal> animals = animalManager.getAnimals();
    System.out.println("\nList of animals before deletion:");
    for (Animal animal : animals) {
      System.out.println(animal);
    }
    animalManager.removeUser(animals.get(0));
    animals = animalManager.getAnimals();
    System.out.println("\nList of animals after deletion:");
    for (Animal animal : animals) {
      System.out.println(animal);
    }
  }
}
