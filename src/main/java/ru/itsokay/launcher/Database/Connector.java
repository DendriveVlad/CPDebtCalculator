package ru.itsokay.launcher.Database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.itsokay.launcher.Database.Tables.Members;
import ru.itsokay.launcher.Database.Tables.Products;

import java.io.File;

public class Connector {

    private static SessionFactory sFactory;
    private Session session;

    public Connector() {
        String FileFolder = null;
        String os = System.getProperty("os.name").toUpperCase();
        if (os.contains("WIN")) FileFolder = System.getenv("APPDATA") + "\\PartyCalc\\";
        if (os.contains("MAC"))
            FileFolder = System.getProperty("user.home") + "/Library/\"Application Support\"/PartyCalc/";
        if (os.contains("NUX")) FileFolder = System.getProperty("user.dir") + ".PartyCalc.";

        if (FileFolder == null) return;
        File dir = new File(FileFolder);
        if (!dir.exists()) if (!dir.mkdir()) return;

        Configuration cfg = new Configuration()
                .setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect")
                .setProperty("hibernate.connection.url", "jdbc:sqlite:" + FileFolder + "base.db")
                .setProperty("hibernate.connection.autocommit", "true")
                .setProperty("hibernate.show_sql", "false")
                .setProperty("hibernate.hbm2ddl.auto", "update")
                .setProperty("hibernate.transaction.jta.platform", "org.hibernate.service.jta.platform.internal.SunOneJtaPlatform")
                .addAnnotatedClass(Products.class)
                .addAnnotatedClass(Members.class);
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();

        sFactory = cfg.buildSessionFactory(serviceRegistry);

        session = sFactory.openSession();
    }

    public Session getsSession() {
        return this.session;
    }
}
