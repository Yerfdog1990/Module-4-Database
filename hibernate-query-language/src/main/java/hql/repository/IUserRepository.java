package hql.repository;

import hql.model.SchoolUser;

public interface IUserRepository {

  // Basic CRUD operation abstract methods
  void create(SchoolUser user);

  void update(SchoolUser user);

  SchoolUser read(SchoolUser user);

  void delete(SchoolUser user);

  SchoolUser findById(int id);

  SchoolUser findByName(String name);

  SchoolUser findByEmail(String email);

  SchoolUser findByPhone(String phone);

  SchoolUser findByAddress(String address);
}
/*
NB: It's generally not recommended to use HQL (Hibernate Query Language) for basic CRUD operations (Create, Read, Update, Delete) when working with individual entities.
Here's why:

1. For Creating/Saving Entities:
   - Use `session.persist()` or `session.save()` instead of HQL INSERT
   - These methods properly handle ID generation
   - They ensure the entity is properly managed by the persistence context
   - They trigger the appropriate lifecycle events

2. For Updating Entities:
   - Use `session.merge()` or `session.update()` instead of HQL UPDATE
   - These methods maintain the persistence context
   - They properly handle dirty checking
   - They trigger entity lifecycle events and cascading operations

3. For Reading Entities:
   - Use `session.get()` or `session.find()` for single entity by ID
   - Use `session.createQuery()` for complex queries and multiple entities
   - Choose between:
      * Simple ID lookup: session.get(Entity.class, id)
      * Natural key lookup: session.byNaturalId()
      * Complex conditions: HQL/JPQL queries
      * Dynamic filters: Criteria API
   - Consider these factors:
      * Performance (especially with large datasets)
      * Need for eager/lazy loading
      * Join requirements
      * Pagination needs
      * Caching strategy
   - Best practices:
      * Use fetch joins to avoid N+1 problems
      * Select only needed columns for DTOs
      * Add proper indexes on frequently queried columns
      * Use parameters to prevent SQL injection

4. For Deleting Entities:
   - Use `session.remove()` or `session.delete()` instead of HQL DELETE
   - These methods ensure proper cascade operations
   - They trigger entity lifecycle events
   - They maintain referential integrity

HQL should primarily be used for:
- Complex queries involving multiple entities
- Bulk operations affecting multiple records at once
- Custom queries with complex conditions
- Aggregate operations
- Queries that need to join multiple tables

Using Hibernate's entity management methods (`persist`, `merge`, `remove`) provides better:
- Transaction management
- Cache management
- Entity lifecycle event handling
- Cascade operations
- Performance optimization
- Data consistency

Testing CRUD operations is still very important, even when using Hibernate's entity management methods.
Here's why:
1. Data Integrity Verification
    - Tests ensure that entities are properly saved with all their attributes
    - Verify that generated IDs are working correctly
    - Confirm that relationships between entities are maintained

2. Business Logic Validation
    - Tests verify that any business rules or constraints are enforced
    - Ensure that validation annotations (like @Email``@Pattern`) work as expected
    - Check that unique constraints are respected

3. Integration Testing
    - Verify proper integration with the database
    - Ensure correct configuration of Hibernate
    - Test transaction management
    - Validate proper schema generation

4. Custom Query Testing
    - Test custom finder methods (like `findByName``findByEmail`)
    - Verify that queries return expected results
    - Check handling of null results or multiple results

5. Edge Cases
    - Test behavior with invalid data
    - Verify error handling
    - Check boundary conditions

HQL (Hibernate Query Language) is recommended in several specific scenarios:

1. Complex Queries
   - When you need to join multiple entities
   - For queries with complex WHERE clauses
   - When you need to fetch specific properties rather than entire entities
```java
"SELECT u.name, u.email FROM SchoolUser u WHERE u.population > :minPopulation"
```

2. Aggregate Operations
   - For calculating counts, sums, averages, etc.
   - When grouping data
```java
"SELECT AVG(u.population), u.physicalAddress FROM SchoolUser u GROUP BY u.physicalAddress"
```

3. Bulk Operations
   - When updating multiple records simultaneously
   - For batch deletions
```java
"UPDATE SchoolUser SET population = population * 1.1 WHERE population < :threshold"
   "DELETE FROM SchoolUser WHERE population = 0"
```

4. Dynamic Queries
   - When query conditions need to be built dynamically
   - For flexible search functionality
```java
StringBuilder query = new StringBuilder("FROM SchoolUser u WHERE 1=1");
   if (hasNameFilter) query.append(" AND u.name LIKE :name");
   if (hasPopulationFilter) query.append(" AND u.population > :minPopulation");
```

5. Projections and DTOs
   - When you need to map results to custom objects
   - For selecting specific columns instead of whole entities
```java
"SELECT new dto.UserSummary(u.name, u.email, u.population) FROM SchoolUser u"
```

6. **Subqueries**
   - When you need to use nested queries
```java
"FROM SchoolUser u WHERE u.population > (SELECT AVG(population) FROM SchoolUser)"
```

7. **Custom Ordering and Filtering**
   - For complex ORDER BY clauses
   - When implementing pagination
```java
"FROM SchoolUser ORDER BY population DESC"
```

8. **Report Generation**
   - When creating summary reports
   - For complex data analysis
```java
"SELECT u.physicalAddress, COUNT(u), SUM(u.population) FROM SchoolUser u GROUP BY u.physicalAddress"
```


Remember to:
- Use parameters (`:paramName`) instead of string concatenation for security
- Consider performance implications for complex queries
- Use fetch joins when needed to avoid N+1 problems
- Consider using Criteria API for very complex dynamic queries
- Use appropriate indexes on your database columns

Avoid HQL for:
- Simple CRUD operations on single entities
- Basic ID-based lookups
- Operations that can be handled by Hibernate's entity management methods
 */
