package ru.itsokay.launcher.Party;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Product {
    private String name;
    // Стоимость продукта
    private int cost;
    // Количество купленных продуктов
    private int number;
    // Кто не относится к продукту
    private List<Member> ignore;

    public Product(String name, int cost, int number) {
        this.name = name;
        this.cost = cost;
        this.number = number;
        this.ignore = new ArrayList<>();
    }

    public void update(String name, int cost, int number) {
        this.name = name;
        this.cost = cost;
        this.number = number;
        this.ignore.clear();
    }

    public void addIgnore(Member member) {
        this.ignore.add(member);
    }

    public void clearIgnore() {
        this.ignore.clear();
    }


    public String[] getProduct() {
        String[] result = new String[3];
        result[0] = name;
        result[1] = String.valueOf(cost);
        result[2] = String.valueOf(number);
        return result;
    }

    public int getFinalCost() {
        return this.cost * this.number;
    }

    public int getCostForEach(int membersCount) {
        return getFinalCost() / (membersCount - this.ignore.size());
    }

    public String toString() {
        return this.name;
    }

    String[] getReadableFormat() {
        return new String[] {name, String.valueOf(cost), String.valueOf(number)};
    }
}
