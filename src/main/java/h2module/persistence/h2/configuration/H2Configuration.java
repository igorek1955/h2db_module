package h2module.persistence.h2.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * configuration file for h2 in-memory database. No properties required (in application.properties)
 * by default uses update - dll. In order to create or remove tables use schema.sql or change
 * h2EntityManagerFactory properties to ("hibernate.hbm2ddl.auto", "create-drop")
 *
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "h2module.persistence.h2.repository",
        entityManagerFactoryRef = "h2EntityManagerFactory",
        transactionManagerRef = "h2TransactionManager"
)
public class H2Configuration {

    @Bean(name = "h2DataSourceProperties")
    public DataSourceProperties h2DataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl("jdbc:h2:file:./persistent_h2/h2");
        properties.setDriverClassName("org.h2.Driver");

        return properties;
    }

    @Bean(name = "h2DataSource")
    public DataSource h2DataSource(@Qualifier("h2DataSourceProperties") DataSourceProperties h2DataSourceProperties) {
        return h2DataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean(name = "h2EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean h2EntityManagerFactory(
            EntityManagerFactoryBuilder h2EntityManagerFactoryBuilder, @Qualifier("h2DataSource") DataSource h2DataSource) {

        Map<String, String> h2JpaProperties = new HashMap<>();
        h2JpaProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        h2JpaProperties.put("hibernate.hbm2ddl.auto", "update");

        return h2EntityManagerFactoryBuilder
                .dataSource(h2DataSource)
                .packages("h2module.persistence.h2.model")
                .persistenceUnit("h2DataSource")
                .properties(h2JpaProperties)
                .build();
    }

    @Bean(name = "h2TransactionManager")
    public PlatformTransactionManager h2TransactionManager(
            @Qualifier("h2EntityManagerFactory") EntityManagerFactory h2EntityManagerFactory) {
        return new JpaTransactionManager(h2EntityManagerFactory);
    }
}