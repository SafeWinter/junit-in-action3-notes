# 第十九章：数据库应用的测试



> **本章概要**
>
> - 数据库测试面临的难题；
> - 在 `JDBC`、`Spring JDBC`、`Hibernate`、`Spring Hibernate` 中实现测试的具体做法；
> - 数据库测试不同方案的横向对比。

> *Dependency is the key problem in software development at all scales.... Eliminating duplication in programs eliminates dependency.*
> 依赖是软件开发中各个层面的关键问题……消除程序中的重复才是解决之道。
>
> —— **Kent Beck**《*Test-Driven Development: By Example*》



本章的示例代码量很大，但核心知识点却不多。通过依次演示单元测试在四个不同的业务场景（`JDBC`、`Spring JDBC`、`Hibernate`、`Spring Hibernate`）中的具体应用，让大家对数据库测试的基本流程和固有复杂度有一个直观的认识。本章不打算照搬书中的大段代码，仅根据实测过程中的关键知识点进行梳理。

> [!tip]
>
> 本章完整示例代码详见 `GitHub` 官方代码库：[https://github.com/ctudose/junit-in-action-third-edition/tree/master/ch19-databases](https://github.com/ctudose/junit-in-action-third-edition/tree/master/ch19-databases)。



## 19.1 数据库与单元测试的阻抗不匹配问题

持久层难以单元测试，主要体现在三点：

- 单元测试必须隔离运行被测代码；而持久层不得不与数据库进行交互；
- 单元测试必须易于编写和运行；而访问数据库的代码通常较为繁琐；
- 单元测试必须快速执行；数据库访问相对较慢。

类比 `ORM` 中的 **对象-关系阻抗不匹配（*object-relational impedance mismatch*）** 概念，上述问题也可以归入一个新概念：**数据库-单元测试阻抗不匹配（*database unit testing impedance mismatch*）**。



## 19.2 数据库测试的归类问题

数据库测试不是最严格意义上的单元测试，但它既可以视为单元测试，也可以归为集成测试。

作单元测试考虑时，主要是对 `DAO` 层的接口类进行测试。

作集成测试考虑时，主要是将数据库的具体实现作为外部依赖，并通过 `Stub` 桩代码和 `Mock` 对象等方法进行模拟（类似 **门面模式（*facade design pattern*）**）。



## 19.3 数据库单元测试阻抗不匹配的应对方案

对于隔离的阻抗不匹配：抽象出一个 `DAO` 数据访问过渡层，避免在业务层直接访问数据库。

对于实现难度的阻抗不匹配：通过引入 `Spring`、`Hibernate` 以及整合 `Spring` / `Hibernate` 框架，大幅降低测试的实现难度。

对于速度慢的阻抗不匹配：通常无法彻底解决。解决方案主要有两种：

- 随项目内嵌一个轻量数据库（`H2`、`HSQLDB`、`Apache Derby` 等）；
- 在本地模拟一个测试数据库。



## 19.4 利用 JDBC 接口编写测试

主要问题：实现繁琐，代码冗余度高。从数据库连接开始，到完成测试断开连接，必须面面俱到：

```java
public class CountriesDatabaseTest {
    // -- snip --
    @Test
    public void testCountryList() {
        List<Country> countryList = countryDao.getCountryList();
        assertNotNull(countryList);
        assertEquals(expectedCountryList.size(), countryList.size());
        for (int i = 0; i < expectedCountryList.size(); i++) {
            assertEquals(expectedCountryList.get(i), countryList.get(i));
        }
    }
    // -- snip --
}

public class CountryDao {
    private static final String GET_ALL_COUNTRIES_SQL = "select * from country";

    public List<Country> getCountryList() {
        List<Country> countryList = new ArrayList<>();

        try {
            Connection connection = openConnection();
            PreparedStatement statement = connection.prepareStatement(GET_ALL_COUNTRIES_SQL);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                countryList.add(new Country(resultSet.getString(2), resultSet.getString(3)));
            }
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection();
        }

        return countryList;
    }
}

public static Connection openConnection() {
    try {
        Class.forName("org.h2.Driver"); // this is driver for H2
        connection = DriverManager.getConnection("jdbc:h2:~/country", "sa", "");
        return connection;
    } catch (ClassNotFoundException | SQLException e) {
        throw new RuntimeException(e);
    }
}

public static void closeConnection() {
    if (null != connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
```



## 19.5 利用 Spring JDBC 编写测试

优势在于数据源的定义和 `DAO` 层的配置交给 `Spring` 容器，代码更加关注业务逻辑：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:jdbc="http://www.springframework.org/schema/jdbc"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.1.xsd
		http://www.springframework.org/schema/jdbc http://www.springframework.org/schema/jdbc/spring-jdbc-3.1.xsd">

	<jdbc:embedded-database id="dataSource" type="H2">
		<jdbc:script location="classpath:db-schema.sql"/>	
	</jdbc:embedded-database>

    <bean id="countryDao" class="com.manning.junitbook.databases.dao.CountryDao">
        <property name="dataSource" ref="dataSource"/>
    </bean>

	<bean id="countriesLoader" class="com.manning.junitbook.databases.CountriesLoader">
		<property name="dataSource" ref="dataSource"/>
	</bean>
</beans>
```

但弊端在于不够轻量：还需要手动实现 `ORM` 映射，创建 `Mapper` 处理类，并通过 `jdbcTemplate` 实现具体操作：

```java
public class CountryRowMapper implements RowMapper<Country> {
    @Override
    public Country mapRow(ResultSet resultSet, int i) throws SQLException {
        return new Country(resultSet.getString("name"), resultSet.getString("code_name"));
    }
}

// in CountryDao.java:
public class CountryDao extends JdbcDaoSupport {
    public List<Country> getCountryList() {
        return getJdbcTemplate().query("select * from country", new CountryRowMapper());
    }
}
```

测试的具体实现：

```java
// in CountriesDatabaseTest.java
@Test
@DirtiesContext
public void testCountryList() {
    List<Country> countryList = countryDao.getCountryList();
    assertNotNull(countryList);
    assertEquals(expectedCountryList.size(), countryList.size());
    for (int i = 0; i < expectedCountryList.size(); i++) {
        assertEquals(expectedCountryList.get(i), countryList.get(i));
    }
}
```

注意：这里的 `@DirtiesContext` 用于确保测试数据库（即 `H2`）的上下文不受其他用例影响，常适用于上下文状态频繁变更的情况。



## 19.6 利用 Hibernate 编写测试

`Java` 持久化 `API`（`JPA`）是一套规范，它描述了关系型数据的管理方式、客户端操作方法的 `API`，以及对象关系映射（`ORM`）的元数据标准。`Hibernate` 作为 `Java` 平台的 `ORM` 框架实现了 `JPA` 规范，也是目前最流行的 `JPA` 实现方案。`Hibernate` 的出现甚至早于 `JPA` 规范。

`Hibernate` 的主要优势：

- 开发速度更快：无需手动实现 `RowMapper`；
- `DAO` 数据访问层更加抽象，可移植更强：支持特定类型的 `SQL`，无须直接接触底层 `SQL` 实现；
- 支持缓存管理；
- 支持样板代码生成；

基于纯 `Hibernate` 的测试用例的核心逻辑变化不大：

```java
public class CountriesHibernateTest {

    private EntityManagerFactory emf;
    private EntityManager em;

    private List<Country> expectedCountryList = new ArrayList<>();

    @Test
    public void testCountryList() {
        List<Country> countryList = em.createQuery("select c from Country c").getResultList();
        assertNotNull(countryList);
        assertEquals(COUNTRY_INIT_DATA.length, countryList.size());
        for (int i = 0; i < expectedCountryList.size(); i++) {
            assertEquals(expectedCountryList.get(i), countryList.get(i));
        }

    }
}
```

主要区别在于实体类的相关注解：

```java
@Entity
@Table(name = "COUNTRY")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "CODE_NAME")
    private String codeName;
    // -- snip --
}
```

此外，`Hibernate` 还需要配置一个持久化的 `XML` 节点单元：

```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">

    <persistence-unit name="manning.hibernate">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <class>com.manning.junitbook.databases.model.Country</class>

        <properties>

            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>

            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>

            <property name="hibernate.show_sql" value="true"/>

            <property name="hibernate.hbm2ddl.auto" value="create"/>
        </properties>
    </persistence-unit>

</persistence>
```

其中，`HibernatePersistenceProvider` 是 `JPA` 的 `EntityManager` 实现，即 `Hibernate`。



## 19.7 利用 Spring Hibernate 编写测试

该方案充分利用了 `Spring` 框架的 `IoC` 机制和 `Hibernate` 对 `ORM` 的强大支持，让测试用例可以更加专注于核心逻辑。

主要区别在于同时使用了 `application-context.xml` 和 `persistence.xml` 配置：

```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">

    <persistence-unit name="manning.hibernate">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <class>com.manning.junitbook.databases.model.Country</class>
    </persistence-unit>

</persistence>

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="
            http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd">

    <tx:annotation-driven transaction-manager="txManager"/>

    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="org.h2.Driver"/>
        <property name="url" value="jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"/>
        <property name="username" value="sa"/>
        <property name="password" value=""/>
    </bean>

    <bean id="entityManagerFactory" class="org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean">
        <property name="persistenceUnitName" value="manning.hibernate"/>
        <property name="dataSource" ref="dataSource"/>
        <property name="jpaProperties">
            <props>
                <prop key="hibernate.dialect">org.hibernate.dialect.H2Dialect</prop>
                <prop key="hibernate.show_sql">true</prop>
                <prop key="hibernate.hbm2ddl.auto">create</prop>
            </props>
        </property>
    </bean>

    <bean id="txManager" class="org.springframework.orm.jpa.JpaTransactionManager">
        <property name="entityManagerFactory" ref="entityManagerFactory"/>
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <bean class="com.manning.junitbook.databases.CountryService"/>

</beans>
```

测试用例的书写也略有不同，支持事务注解，写起来也更加简洁：

```java
@ExtendWith(SpringExtension.class)
@ContextConfiguration("classpath:application-context.xml")
public class CountriesHibernateTest {
    @Autowired
    private CountryService countryService;

    @Test
    public void testCountryList() {
        List<Country> countryList = countryService.getAllCountries();
        assertNotNull(countryList);
        assertEquals(COUNTRY_INIT_DATA.length, countryList.size());
        for (int i = 0; i < expectedCountryList.size(); i++) {
            assertEquals(expectedCountryList.get(i), countryList.get(i));
        }
    }
}

public class CountryService {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void clear() {
        em.createQuery("delete from Country c").executeUpdate();
    }

    public List<Country> getAllCountries() {
        return em.createQuery("select c from Country c").getResultList();
    }
}
```

这里的 `@PersistenceContext` 用于注入 `EntityManager` 接口，其具体的 `Hibernate` 实现在上面的 `persistence.xml` 中配置。



## 19.8 四种方案的横向对比

| 应用类型           | 特征梳理                                                     |
| ------------------ | ------------------------------------------------------------ |
| `JDBC`             | 测试需要编写 SQL 脚本；<br/>数据库之间不可移植；<br/>对应用的操作具有完全控制权；<br/>需手动与数据库交互，包括：<br/>- 创建和打开连接；<br/>- 指定、准备和执行语句；<br/>- 遍历结果集；<br/>- 每次迭代都需要处理异常；<br/>- 关闭连接； |
| `Spring JDBC`      | 测试需要编写 SQL 脚本；<br/>数据库之间不可移植；<br/>需要处理由 Spring 负责的行映射和上下文配置；<br/>控制应用程序对数据库执行的查询；<br/>减少与数据库交互的手动操作：<br/>\- 无需自行创建/打开/关闭连接；<br/>\- 无需准备和执行语句；<br/>\- 无需处理异常； |
| `Hibernate`        | 无需编写 SQL 脚本；<br/>仅使用可移植的 JPQL；<br/>开发者只需编写 Java 代码；<br/>无需将查询结果列映射到对象字段，反之亦然；<br/>通过更改 Hibernate 配置和数据库方言，实现数据库之间的可移植性；<br/>通过 Java 代码处理数据库配置。 |
| `Spring Hibernate` | 无需编写 SQL 脚本；<br/>仅使用可移植的 JPQL；<br/>开发者只需编写 Java 代码；<br/>无需将查询结果列映射到对象字段，反之亦然；<br/>通过更改 Hibernate 配置和数据库方言，实现数据库之间的可移植性；<br/>数据库配置由 Spring 根据应用上下文中的信息进行处理。 |



## 19.9 本章小结

本章依次从 `JDBC`、`Spring JDBC`、`Hibernate`、`Spring Hibernate` 四个场景反复演示了 `Country` 实体类的列表查询接口的单元测试方法，旨在说明数据库与单元测试之间固有的阻抗不匹配问题，以及目前所能提供的解决方案。由于代码完整，这四个场景略加调整就可直接用于实际项目，因此颇有参考价值。

另外，由于演示代码过多，相关的底层原理介绍得很少。对数据持久化感兴趣的朋友可以另行参考作者专门写的另一本书《*Java Persistence with Spring Data and Hibernate*》（Manning, 2023.1）。