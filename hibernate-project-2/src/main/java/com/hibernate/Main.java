package com.hibernate;

import com.hibernate.dao.*;
import com.hibernate.domain.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


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
