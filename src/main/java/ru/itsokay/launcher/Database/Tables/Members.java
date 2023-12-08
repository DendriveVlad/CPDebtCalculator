package ru.itsokay.launcher.Database.Tables;


import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class Members {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    public Members() {}

    @Column(name = "rawdata")
    private String rawdata;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setRawdata(String rawdata) {
        this.rawdata = rawdata;
    }

    public String getRawdata() {
        return rawdata;
    }
}
