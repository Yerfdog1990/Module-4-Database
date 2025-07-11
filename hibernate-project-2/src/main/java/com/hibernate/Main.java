package com.hibernate;

import com.hibernate.dao.*;
import com.hibernate.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Main {
    // SessionFactory field
    private static SessionFactory sessionFactory;

    // Dao classes field
    private final ActorDao actorDao;
    private final FilmDao filmDao;
    private final FilmTextDao filmTextDao;
    private final CategoryDao categoryDao;
    private final InventoryDao inventoryDao;
    private final LanguageDao languageDao;
    private final CustomerDao customerDao;
    private final StoreDao storeDao;
    private final StaffDao staffDao;
    private final AddressDao addressDao;
    private final CityDao cityDao;
    private final CountryDao countryDao;
    private final PaymentDao paymentDao;
    private final RentalDao rentalDao;

    // NoArgsConstructor
    public Main() {
        this.actorDao = new ActorDao(getSessionFactory());
        this.filmDao = new FilmDao(getSessionFactory());
        this.filmTextDao = new FilmTextDao(getSessionFactory());
        this.categoryDao = new CategoryDao(getSessionFactory());
        this.inventoryDao = new InventoryDao(getSessionFactory());
        this.languageDao = new LanguageDao(getSessionFactory());
        this.customerDao = new CustomerDao(getSessionFactory());
        this.storeDao = new StoreDao(getSessionFactory());
        this.staffDao = new StaffDao(getSessionFactory());
        this.addressDao = new AddressDao(getSessionFactory());
        this.cityDao = new CityDao(getSessionFactory());
        this.countryDao = new CountryDao(getSessionFactory());
        this.paymentDao = new PaymentDao(getSessionFactory());
        this.rentalDao = new RentalDao(getSessionFactory());
    }

    // Lazy initialization of the SessionFactory field
    public SessionFactory getSessionFactory() {
        try{
            if(sessionFactory == null){
                sessionFactory = new Configuration().configure("hibernate.cfg.xml")
                        .addAnnotatedClass(Actor.class)
                        .addAnnotatedClass(Inventory.class)
                        .addAnnotatedClass(FilmText.class)
                        .addAnnotatedClass(Category.class)
                        .addAnnotatedClass(Film.class)
                        .addAnnotatedClass(Language.class)
                        .addAnnotatedClass(Customer.class)
                        .addAnnotatedClass(Store.class)
                        .addAnnotatedClass(Staff.class)
                        .addAnnotatedClass(Address.class)
                        .addAnnotatedClass(City.class)
                        .addAnnotatedClass(Country.class)
                        .addAnnotatedClass(Payment.class)
                        .addAnnotatedClass(Rental.class)
                        .buildSessionFactory();
            }
        } catch (Exception e){
            System.err.println("Failed to create sessionFactory object." + e);
            throw new ExceptionInInitializerError(e);
        }
        return sessionFactory;
    }
    public static void main(String[] args) {
        Main main = new Main();
        Customer customer = main.createCustomer();
        main.customerReturnInventoryToStore();
        main.customerEventInventory(customer);
        main.newFilmMade();

    }

    private void newFilmMade() {
        try(Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();
            Language language = languageDao.getItems(0, 20).stream().unordered().findAny().get();
            List<Category> categories = categoryDao.getItems(0, 5);
            List<Actor> actors = actorDao.getItems(0, 15);

            Film film = new Film();
            film.setTitle("New Film");
            film.setDescription("New Film Description");
            film.setLanguage(language);
            film.setOriginalLanguage(language);
            film.setActors(new HashSet<>(actors));
            film.setCategories(new HashSet<>(categories));
            film.setRating(Rating.PG);
            film.setSpecialFeatures(Set.of(Feature.values()).stream().findAny().get().getValue());
            film.setLength((short)234);
            film.setReplacementCost(BigDecimal.valueOf(10.0));
            film.setRentalRate(BigDecimal.valueOf(0.0));
            film.setRentalDuration((byte)33);
            film.setYear(Year.now());
            filmDao.save(film);

            FilmText filmText = new FilmText();
            filmText.setFilm(film);
            filmText.setDescription("New Film Description");
            filmText.setTitle("New Film");
            filmTextDao.save(filmText);
            session.getTransaction().commit();
        }
    }

    private void customerEventInventory(Customer customer) {
        try(Session session = getSessionFactory().getCurrentSession()){
            session.beginTransaction();
            Film film = filmDao.getFirstAvailableFilmForRent();
            Store store = storeDao.getItems(0, 1).get(0);
            Inventory inventory = new Inventory();
            inventory.setFilm(film);
            inventory.setStore(store);
            inventoryDao.save(inventory);

            Staff staff = store.getStaff();
            Rental rental = new Rental();
            rental.setCustomer(customer);
            rental.setRentalDate(LocalDateTime.now());
            rental.setInventory(inventory);
            rental.setStaff(staff);
            rentalDao.save(rental);

            Payment payment = new Payment();
            payment.setRental(rental);
            payment.setAmount(BigDecimal.valueOf(10.0));
            payment.setCustomer(customer);
            payment.setStaff(staff);
            payment.setPaymentDate(LocalDateTime.now());
            paymentDao.save(payment);
            session.getTransaction().commit();
        }
    }

    private void customerReturnInventoryToStore() {
        try(Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();
            Rental rental = rentalDao.getAnyReturnedRental();
            rental.setRentalDate(LocalDateTime.now());
            rentalDao.save(rental);
            session.getTransaction().commit();
        }
    }

    private Customer createCustomer() {
        try(Session session = sessionFactory.getCurrentSession()){
            session.beginTransaction();
            Store store = storeDao.getItems(0, 1).get(0);
            City city = cityDao.getByName("Aden");
            Address address = new Address();
            address.setAddress("123 Main St");
            address.setDistrict("District");
            address.setCity(city);
            address.setPhone("+1-234-567-890");
            addressDao.save(address);

            Customer customer = new Customer();
            customer.setFirstName("John");
            customer.setLastName("Doe");
            customer.setEmail("joedoe" + "@gmail.com");
            customer.setAddress(address);
            customer.setIsActive(true);
            customer.setStore(store);
            customerDao.save(customer);

            session.getTransaction().commit();
            return customer;
        }
    }
}
