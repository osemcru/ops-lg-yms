package com.frubana.operations.warehouse.change_me.common.configuration;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.List;

import static java.util.Objects.requireNonNull;

/** Databases configuration to use JDBI.
 */
@Configuration
public class JdbiConfiguration {

    /** Method representing a JDBI Bean designed to prepare the JDBI to
     * search for the {@link JdbiPlugin} plugins and {@link RowMapper}
     * Mappers defined in the project to extract the data from the configured
     * database in the given {@link DataSource}.
     *
     * @param ds          Configured {@link DataSource} in another Bean
     *                    cannot be null.
     * @param jdbiPlugins Loaded {@link JdbiPlugin} declared in the POM or
     *                    some Bean cannot be null.
     * @param rowMappers  Loaded {@link RowMapper} defined in some component
     *                    of the project cannot be null.
     * @return The {@link Jdbi} instance used to extract the needed data.
     */
    @Bean
    public Jdbi jdbi(final DataSource ds,
                     final List<JdbiPlugin> jdbiPlugins,
                     final List<RowMapper<?>> rowMappers) {

        requireNonNull(ds);
        requireNonNull(jdbiPlugins);
        requireNonNull(rowMappers);

        TransactionAwareDataSourceProxy proxy =
                new TransactionAwareDataSourceProxy(ds);
        Jdbi jdbi = Jdbi.create(proxy);

        // Register all available plugins
        jdbiPlugins.forEach(jdbi::installPlugin);

        // Register all available rowMappers
        rowMappers.forEach(jdbi::registerRowMapper);

        return jdbi;
    }

    /** Sql plugin needed for the JDBI bean.
     *
     * @return The {@link JdbiPlugin} instance for the project.
     */
    @Bean
    public JdbiPlugin sqlObjectPlugin() { return new SqlObjectPlugin(); }

    /** {@link DataSource} Bean definition to set the database used in the
     * project.
     *
     * @param env Environment definition to extract the properties, this
     *            field is autowired using the properties
     *            defined in the {@link PropertySource} annotation.
     * @return the {@link DataSource} instance for the project.
     */
    @Bean
    @Autowired
    public DataSource getDataSource(final Environment env) {
        requireNonNull(env);
        return DataSourceBuilder.create()
                .driverClassName(env.getProperty("spring.datasource.driverClassName"))
                .url(env.getProperty("spring.datasource.url"))
                .username(env.getProperty("spring.datasource.username"))
                .password(env.getProperty("spring.datasource.password")).build();
    }

}
