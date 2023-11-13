package ru.itsokay.launcher.Party;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private String name;
    // Общее число затрат на продукты
    private int paid;
    // Какие продукты человек не использовал/употреблял
    private List<Product> except;

    public Member(String name, int paid, String except, ArrayList<Product> products) {
        this.name = name;
        this.paid = paid;
        this.except = new ArrayList<>();
        if (!except.equals("-1")) for (String p : except.split("&"))
            if (!p.isEmpty()) {
                this.except.add(products.get(Integer.parseInt(p)));
                products.get(Integer.parseInt(p)).addIgnore(this);
            }
    }

    public void update(String name, int paid, String except, ArrayList<Product> products) {
        this.name = name;
        this.paid = paid;
        for (Product p : this.except) {
            p.clearIgnore();
        }
        this.except = new ArrayList<>();
        if (!except.equals("-1")) for (String p : except.split("&")) {
            this.except.add(products.get(Integer.parseInt(p)));
            products.get(Integer.parseInt(p)).addIgnore(this);
        }
    }

    public List<Product> getExcepts() {
        return this.except;
    }

    public int getPaid() {
        return this.paid;
    }

    public String[] getMember() {
        String[] result = new String[3];
        result[0] = name;
        result[1] = String.valueOf(paid);
        result[2] = "";
        for (Product p : this.except) {
            if (!result[2].isEmpty()) result[2] += ", ";
            result[2] += p.getProduct()[0];
        }
        result[2] += ".";
        return result;
    }
}
