package ru.itsokay.launcher;

import ru.itsokay.launcher.Party.Member;
import ru.itsokay.launcher.Party.Product;

import java.util.ArrayList;
import java.util.List;

public class PartyProcessor {
    private List<Product> products = new ArrayList<>();
    private List<Member> members = new ArrayList<>();

    public ArrayList<String[]> getProducts() {
        ArrayList<String[]> result = new ArrayList<>();
        for (Product product : products) {
            result.add(product.getProduct());
        }
        return result;
    }

    public ArrayList<String[]> getMembers() {
        ArrayList<String[]> result = new ArrayList<>();
        for (Member member : members) {
            result.add(member.getMember());
        }
        return result;
    }

    public ArrayList<Product> getJustProducts() {
        return (ArrayList<Product>) this.products;
    }

    public ArrayList<Member> getJustMembers() {
        return (ArrayList<Member>) this.members;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void editProduct(int index, String name, int cost, int number) {
        products.get(index).update(name, cost, number);
    }

    public void removeProduct(int index) {
        for (Member m : this.members) m.getExcepts().remove(products.get(index));
        products.remove(index);
    }

    public int getProductsCount() {
        return products.size();
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void editMember(int index, String name, int paid, String exceptions) {
        members.get(index).update(name, paid, exceptions, (ArrayList<Product>) this.products);
    }

    public void removeMember(int index) {
        members.remove(index);
    }

    public int getSummaryCost() {
        int result = 0;
        for (Product p : this.products) {
            result += p.getFinalCost();
        }
        return result;
    }

    public int getSummaryDebtForMember(Member member) {
        int result = 0;
        for (Product p : this.products) if (!member.getExcepts().contains(p))
            result += p.getCostForEach(this.members.size());

        return result - member.getPaid();
    }

    public int getSummaryDebt() {
        int result = getSummaryCost();
        for (Member m : this.members) {
            result -= m.getPaid();
        }
        return result;
    }
}
