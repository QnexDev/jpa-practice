package com.qnex.jpa.practice.test;

import com.qnex.jpa.practice.H2DataSource;
import com.qnex.jpa.practice.PostgresDataSource;
import com.qnex.jpa.practice.HSQLDBDataSource;
import com.qnex.jpa.practice.util.LoggableTransactionTemplate;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

public class AbstractDataBaseTest {

    private static final Logger LOG = Logger.getLogger(AbstractDataBaseTest.class);

    protected JpaTransactionManager transactionManager;
    protected EntityManagerFactory entityManagerFactory;
    protected TransactionOperations txOperations;
    protected SharedEntityManagerBean entityManagerBean;
    protected JdbcTemplate jdbcTemplate;
    protected DataSource dataSource;
    private DataBaseConfigProvider dbSettingProvider;

    public AbstractDataBaseTest() {
        Class<? extends AbstractDataBaseTest> thisClass = this.getClass();
        if (thisClass.isAnnotationPresent(HSQLDBDataSource.class)) {
            dbSettingProvider = new HSQLDBConfigProvider();
        } else if (thisClass.isAnnotationPresent(PostgresDataSource.class)) {
            dbSettingProvider = new PostgresConfigProvider();
        } else if (thisClass.isAnnotationPresent(H2DataSource.class)) {
            dbSettingProvider = new H2ConfigProvider();
        } else {
            dbSettingProvider = new HSQLDBConfigProvider();
        }
    }

    @Before
    public void init() {
        dataSource = dbSettingProvider.configureDataSource();

        String dataBaseName = dbSettingProvider.getDataBaseName();
        if (StringUtils.hasLength(dataBaseName)) {
            LOG.debug("The current data base is '" + dataBaseName + "'");
        }
        jdbcTemplate = new JdbcTemplate(dataSource);

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPersistenceUnitName("sample-unit");
        entityManagerFactoryBean.setPersistenceXmlLocation("classpath:/META-INF/persistence.xml");
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabasePlatform(dbSettingProvider.getDialect());
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.afterPropertiesSet();


        entityManagerFactory = entityManagerFactoryBean.getObject();

        transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setPersistenceUnitName("sample-unit");
        transactionManager.afterPropertiesSet();
        transactionManager.setJpaDialect(new HibernateJpaDialect());

        txOperations = new LoggableTransactionTemplate(new TransactionTemplate(transactionManager));
        entityManagerBean = new SharedEntityManagerBean();
        entityManagerBean.setEntityManagerFactory(entityManagerFactory);
        entityManagerBean.setPersistenceUnitName("sample-unit");
        entityManagerBean.setSynchronizedWithTransaction(true);
        entityManagerBean.afterPropertiesSet();
    }

    @After
    public void cleanUp() {
        entityManagerFactory.close();
    }

    public static void delay(int millis) {
        try {
            LOG.debug("Starting delay...");
            Thread.sleep(millis);
            LOG.debug("The delay has been finished");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    interface DataBaseConfigProvider {

        DataSource configureDataSource();

        String getDialect();

        String getDataBaseName();
    }

    public class PostgresConfigProvider implements DataBaseConfigProvider {

        @Override
        public DataSource configureDataSource() {
            return providePostgresDataSource();
        }

        @Override
        public String getDialect() {
            return "org.hibernate.dialect.PostgreSQL95Dialect";
        }

        @Override
        public String getDataBaseName() {
            return "Postgres";
        }


        private DataSource providePostgresDataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUsername("postgres");
            dataSource.setUrl("jdbc:postgresql://localhost/jpa_db");
            dataSource.setPassword("stalker2");
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setPoolPreparedStatements(false);
            return dataSource;
        }

    }

    public class HSQLDBConfigProvider implements DataBaseConfigProvider {
        @Override
        public DataSource configureDataSource() {
            return provideEmbeddedDataSource();
        }

        @Override
        public String getDialect() {
            return "org.hibernate.dialect.HSQLDialect";
        }

        @Override
        public String getDataBaseName() {
            return "HSQLDB";
        }

        private DataSource provideEmbeddedDataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUsername("sa");
            dataSource.setUrl("jdbc:hsqldb:mem:mymemdb");
            dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
            dataSource.setPoolPreparedStatements(false);
            return dataSource;
        }


    }

    public class H2ConfigProvider implements DataBaseConfigProvider {
        @Override
        public DataSource configureDataSource() {
            return provideEmbeddedDataSource();
        }

        @Override
        public String getDialect() {
            return "org.hibernate.dialect.H2Dialect";
        }

        @Override
        public String getDataBaseName() {
            return "H2";
        }

        private DataSource provideEmbeddedDataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUsername("sa");
            dataSource.setUrl("jdbc:h2:mem:sample;MODE=Oracle;DEFAULT_LOCK_TIMEOUT=10000;");
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setPoolPreparedStatements(false);
            return dataSource;
        }


    }


}
