package com.epam.course.java.se.datasource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Arrays;

public class PostgresqlDataSourceTest extends AbstractDbDataSourceTest {
    private static DataSource ds;

    private static PostgresProcess process;


    @BeforeClass
    public static void startup() throws IOException {
        // define of retreive db name and credentials
        final String name = "yourDbname";
        final String username = "yourUser";
        final String password = "youPassword";

        // starting Postgres
        final PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        final PostgresConfig config = PostgresConfig.defaultWithDbName(name, username, password);
        // pass info regarding encoding, locale, collate, ctype, instead of setting global environment settings
        config.getAdditionalInitDbParams().addAll(Arrays.asList(
                "-E", "UTF-8",
                "--locale=en_US.UTF-8",
                "--lc-collate=en_US.UTF-8",
                "--lc-ctype=en_US.UTF-8"
        ));
        PostgresExecutable exec = runtime.prepare(config);
        process = exec.start();

//        final PGSimpleDataSource ds = new PGSimpleDataSource();
        final PGPoolingDataSource ds = new PGPoolingDataSource();
        ds.setServerName(config.net().host());
        ds.setPortNumber(config.net().port());
        ds.setDatabaseName(config.storage().dbName());
        ds.setUser(config.credentials().username());
        ds.setPassword(config.credentials().password());
        ds.setMaxConnections(4);
        ds.setInitialConnections(2);
        PostgresqlDataSourceTest.ds = ds;
    }

    @AfterClass
    public static void teardown() {
        process.stop();
    }



    @Override
    protected DataSource getDataSource() {
        return ds;
    }
}
