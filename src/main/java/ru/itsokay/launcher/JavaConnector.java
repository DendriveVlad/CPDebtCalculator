package ru.itsokay.launcher;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import ru.itsokay.launcher.Party.Member;
import ru.itsokay.launcher.Party.Product;

import java.io.*;

public class JavaConnector {
    Launcher mainClass;

    public boolean ResizePressed = false;
    public String ResizeCoordinate;

    public double fsHeight;
    public double fsWidth;
    public double fsX;
    public double fsY;

    public JavaConnector(Launcher cls) {
        mainClass = cls;
    }

    // Вывод сообщений в консоль из жс
    public void test(String a) {
        System.out.println(a);
    }

    // Блокировка движения окна при нажатии на кнопки
    public void blockMove() {
        mainClass.moveAccess = false;
    }  // Запрет движения окна при нажатии на кнопки

    // закрытие приложения
    public void close() {
        mainClass.base.end();
        mainClass.stage.close();
    }  // Кнопка закрытия

    // свёртывание приложения
    public void minimize() {
        mainClass.stage.setIconified(true);
    }  // Кнопка сворачивания

    public void resize(String c) {
        ResizePressed = !ResizePressed;
        ResizeCoordinate = c;
    }

    public void fullScreen() {
        if (!mainClass.fullScreen) {
            fsHeight = mainClass.stage.getHeight();
            fsWidth = mainClass.stage.getWidth();
            fsX = mainClass.stage.getX();
            fsY = mainClass.stage.getY();
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            mainClass.stage.setHeight(screenBounds.getHeight());
            mainClass.stage.setWidth(screenBounds.getWidth());
            mainClass.stage.setX(0);
            mainClass.stage.setY(0);
            mainClass.fullScreen = true;
        } else {
            mainClass.stage.setHeight(fsHeight);
            mainClass.stage.setWidth(fsWidth);
            mainClass.stage.setX(fsX);
            mainClass.stage.setY(fsY);
            mainClass.fullScreen = false;
            mainClass.webEngine.executeScript("exitFullScreen()");
        }
    }

    public void goUp() {
        String page = (String) mainClass.webEngine.executeScript("getPage()");
        boolean lw = (boolean) mainClass.webEngine.executeScript("lil_window");
        if (lw) return;
        switch (page) {
            case "Addons" -> mainClass.webEngine.executeScript("changePage('Settings')");
            case "Play" -> mainClass.webEngine.executeScript("changePage('Addons')");
            case "Settings" -> mainClass.webEngine.executeScript("changePage('Play')");
        }
    }

    public void goDown() {
        String page = (String) mainClass.webEngine.executeScript("getPage()");
        boolean lw = (boolean) mainClass.webEngine.executeScript("lil_window");
        if (lw) return;
        switch (page) {
            case "Addons" -> mainClass.webEngine.executeScript("changePage('Play')");
            case "Play" -> mainClass.webEngine.executeScript("changePage('Settings')");
            case "Settings" -> mainClass.webEngine.executeScript("changePage('Addons')");
        }
    }

    // запуск игры? Не работает!
    public void play() throws IOException {
        ProcessBuilder pb = new ProcessBuilder("C:/Users/dendr/AppData/Roaming/.minecraft/versions/1.19", "-jar", "1.19.jar");
        pb.directory(new File("preferred/working/directory"));
        Process p = pb.start();
    }

    // Подгрузка (Чтение) HTML-файла и отправка его в жс
    public String loadPage(String page) throws FileNotFoundException {
        StringBuilder html = new StringBuilder();
        if (this.mainClass.party.isEmpty()) page = "FindData";
        FileReader fr = new FileReader("src/main/resources/ru/itsokay/launcher/html/pages/" + page + ".html");
        try {
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                html.append(line);
            }
            br.close();
            return html.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPartyData(String dataType) {
        StringBuilder result = new StringBuilder();
        switch (dataType) {
            case "products" -> {
//                this.mainClass.party.getProducts();
                for (String[] product : this.mainClass.party.getProducts()) {
                    result.append(product[0]).append("&").append(product[1]).append("&").append(product[2]).append("%");
                }
            }
            case "members" -> {
                for (String[] member : this.mainClass.party.getMembers()) {
                    result.append(member[0]).append("&").append(member[1]).append("&").append(member[2]).append("%");
                }
            }
            case "results" -> {
                for (Member member : this.mainClass.party.getJustMembers()) {
                    result.append(member.getMember()[0]).append("&").
                            append(this.mainClass.party.getSummaryDebtForMember(member)).append("%");
                }
            }
        }
        return String.valueOf(result);
    }

    public String getMemberExcepts(int memberIndex) {
        if (memberIndex < 0) memberIndex = this.mainClass.party.getJustMembers().size() - 1;
        String result = "";
        for (Product p : this.mainClass.party.getJustMembers().get(memberIndex).getExcepts())
            result += p.getProduct()[0] + ", ";
        if (result.isEmpty()) return ".";
        return result.substring(0, result.length() - 2) + ".";
    }

    public String isProductSelectedByMember(int memberIndex, int productIndex) {
        if (this.mainClass.party.getJustMembers().get(memberIndex).getExcepts().contains(this.mainClass.party.getJustProducts().get(productIndex)))
            return "selected";
        else return "";
    }

    public void addProduct(String name, int cost, int number) {
        this.mainClass.base.insert("products",
                name + "&" + cost + "&" + number);
        this.mainClass.party.addProduct(new Product(name, cost, number));
    }

    public void editProduct(int index, String name, int cost, int number) {
        this.mainClass.base.update("products",
                name + "&" + cost + "&" + number,
                index);
        this.mainClass.party.editProduct(index, name, cost, number);
    }

    public void removeProduct(int index) {
        this.mainClass.base.delete("products", index);
        this.mainClass.party.removeProduct(index);
    }

    public void addMember(String name, int paid, String excepts) {
        this.mainClass.base.insert("members",
                name + "&" + paid + "&" + excepts);
        this.mainClass.party.addMember(new Member(name, paid, excepts, this.mainClass.party.getJustProducts()));
    }

    public void editMember(int index, String name, int paid, String excepts) {
        this.mainClass.base.update("members",
                name + "&" + paid + "&" + excepts,
                index);
        this.mainClass.party.editMember(index, name, paid, excepts);
    }

    public void removeMember(int index) {
        this.mainClass.base.delete("members", index);
        this.mainClass.party.removeMember(index);
    }

    public String getStats() {
        String result = this.mainClass.party.getJustMembers().size() + "&" +
                this.mainClass.party.getProducts().size() + "&" +
                this.mainClass.party.getSummaryCost() + "&" +
                this.mainClass.party.getSummaryDebt();
        return result;
    }
}
