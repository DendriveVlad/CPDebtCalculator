package ru.itsokay.launcher.Database.Tables;


import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "rawdata")
    private String rawdata;

    public Products() {
    }

    public String getRawdata() {
        return rawdata;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRawdata(String rawdata) {
        this.rawdata = rawdata;
    }

    public int getId() {
        return id;
    }
}
