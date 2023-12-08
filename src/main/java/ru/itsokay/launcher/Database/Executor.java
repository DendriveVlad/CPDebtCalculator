package ru.itsokay.launcher.Database;

import jakarta.persistence.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.itsokay.launcher.Database.Tables.Members;
import ru.itsokay.launcher.Database.Tables.Products;

import java.util.List;

public class Executor {
    private Connector connection;

    public Executor() {
        this.connection = new Connector();
    }

    public void insert(String table, String data) {
        Transaction trans = connection.getsSession().beginTransaction();
        switch (table.toLowerCase()) {
            case "products" -> {
                Products p = new Products();
                p.setRawdata(data);
                connection.getsSession().persist(p);
            }
            case "members" -> {
                Members m = new Members();
                m.setRawdata(data);
                connection.getsSession().persist(m);
            }
        }
        trans.commit();
    }

    public void update(String table, String data, int id) {
        Transaction trans = connection.getsSession().beginTransaction();

        Query query = connection.getsSession().createQuery("FROM " + table);
        switch (table.toLowerCase()) {
            case "products" -> {
                List<Products> rs = query.getResultList();
                Products pf = rs.get(id);
                pf.setRawdata(data);
                connection.getsSession().merge(pf);
            } case "members" -> {
                List<Members> rs = query.getResultList();
                Members pf = rs.get(id);
                pf.setRawdata(data);
                connection.getsSession().merge(pf);
            }
        }

        trans.commit();
    }

    public void delete(String table, int id) {
        Transaction trans = connection.getsSession().beginTransaction();

        Query query = connection.getsSession().createQuery("FROM " + table);
        switch (table.toLowerCase()) {
            case "products" -> {
                List<Products> rs = query.getResultList();
                connection.getsSession().remove(rs.get(id));
            } case "members" -> {
                List<Members> rs = query.getResultList();
                connection.getsSession().remove(rs.get(id));
            }
        }

        trans.commit();
    }

    public void clear() {
        Transaction trans = connection.getsSession().beginTransaction();

        Query query = connection.getsSession().createQuery("DELETE FROM Products");
        query.executeUpdate();
        query = connection.getsSession().createQuery("DELETE FROM Members");
        query.executeUpdate();

        trans.commit();
    }

    public String select(String table) {
        Query query = connection.getsSession().createQuery("FROM " + table);
        StringBuilder result = new StringBuilder();
        switch (table.toLowerCase()) {
            case "products" -> {
                List<Products> rs = query.getResultList();
                for (Products p : rs) {
                    result.append(p.getRawdata()).append("%");
                }
            } case "members" -> {
                List<Members> rs = query.getResultList();
                for (Members p : rs) {
                    result.append(p.getRawdata()).append("%");
                }
            }
        }
        return result.toString();
    }

    public void end() {
        connection.getsSession().close();
    }

    public static void main(String[] args) {
        Executor x = new Executor();
        x.select("Products");
    }
}
