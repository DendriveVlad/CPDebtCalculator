package ru.itsokay.launcher;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import netscape.javascript.JSObject;
import ru.itsokay.launcher.Database.Connector;
import ru.itsokay.launcher.Database.Executor;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Launcher extends Application {
    // Database
    Executor base = new Executor();

    // Процессор
    public PartyProcessor party = new PartyProcessor(base);

    // Позиция приложения на экране пользователя
    private double xOffset;
    private double yOffset;
    // Разрешение на перемещение экрана
    protected boolean moveAccess = false;
    protected boolean fullScreen = false;

    // Экран
    protected WebEngine webEngine;
    protected Stage stage;

    // Конектор JavaScript к Java
    protected final JavaConnector javaConnector = new JavaConnector(this);

    // Конектор Java к JavaScript
    protected JSObject javascriptConnector;
    boolean ctrl = false;
    boolean shift = false;
    boolean alt = false;

    protected URL url = new File("src/main/resources/ru/itsokay/launcher/html/Main.html").toURI().toURL(); // Подключение к HTML файлу

    public Launcher() throws MalformedURLException {
    }


    // Запуск приложения
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println(new File(".").getCanonicalPath()); // Путь, где запущено приложение.
        this.stage = stage;
        stage.initStyle(StageStyle.TRANSPARENT); // Отключение фона
//        stage.setResizable(false); // Запрет на изменение размера
        final WebView browser = new WebView(); // Браузер
        webEngine = browser.getEngine(); // Веб-ядро

        // Подключение конекторов Java и JavaScript

        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", javaConnector);
                javascriptConnector = (JSObject) webEngine.executeScript("getJsConnector()");
//                checks = new Checks((JSObject) webEngine.executeScript("getChecksJsConnector()"), javaConnector);
            }
        });

        Scene scene = new Scene(browser, 1000, 600);

        // Вызывается при нажатии на ЛКМ
        browser.setOnMousePressed(event -> {
            xOffset = event.getScreenX();
            yOffset = event.getScreenY();
            if (event.getY() <= 30) {
                moveAccess = true;
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        // Вызывается при перемещении мыши с нажатой ЛКМ
        browser.setOnMouseDragged(event -> {
            if (moveAccess) {
                if (fullScreen) {
                    javaConnector.fullScreen();
                    stage.setX(event.getScreenX() - stage.getWidth() / 2);
                    stage.setY(0);
                    xOffset = stage.getX() - event.getScreenX();
                    yOffset = stage.getY() - event.getScreenY();
                }
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            } else if (javaConnector.ResizePressed) {
                if (fullScreen) {
                    javaConnector.fullScreen();
                    return;
                }
                switch (javaConnector.ResizeCoordinate) {
                    // Минутка плохого кода
                    case "a":
                    case "e":
                        if (stage.getWidth() >= 1000) {
                            stage.setWidth(stage.getWidth() + event.getScreenX() - xOffset);
                            if (stage.getWidth() > 1000) {
                                xOffset = event.getScreenX();
                            }
                        }
                        if (stage.getWidth() < 1000) {
                            stage.setWidth(1000);
                        }
                        if (!javaConnector.ResizeCoordinate.equals("a")) {
                            break;
                        }
                    case "n":
                        if (stage.getHeight() >= 600) {
                            stage.setHeight(stage.getHeight() + event.getScreenY() - yOffset);
                            if (stage.getHeight() > 600) {
                                yOffset = event.getScreenY();
                            }
                        }
                        if (stage.getHeight() < 600) {
                            stage.setHeight(600);
                        }
                }
            }
        });

        // Вызывается при отжатии на ЛКМ
        browser.setOnMouseReleased(mouseEvent -> {
            javaConnector.ResizePressed = false;
            if (moveAccess) {
                moveAccess = false;
            }
        });

        // При нажатии Ctrl+W окно будет закрыто с анимацией закрытия
        browser.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case CONTROL -> this.ctrl = true;
                case W -> {
                    if (this.ctrl)
                        webEngine.executeScript("closeAnimation()");
                }
                case R -> {
                    if (this.ctrl) {
                        if (fullScreen)
                            javaConnector.fullScreen();
                        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                        stage.setHeight(600);
                        stage.setWidth(1000);
                        stage.setX(screenBounds.getWidth() / 2 - 500);
                        stage.setY(screenBounds.getHeight() / 2 - 300);
                    }
                }
                case UP -> javaConnector.goUp();
                case DOWN -> javaConnector.goDown();
                case F11 -> javaConnector.fullScreen();
//                case DIGIT0 -> webEngine.executeScript("debugPlusNum('0');");
//                case DIGIT1 -> webEngine.executeScript("debugPlusNum('1');");
//                case DIGIT2 -> webEngine.executeScript("debugPlusNum('2');");
//                case DIGIT3 -> webEngine.executeScript("debugPlusNum('3');");
//                case DIGIT4 -> webEngine.executeScript("debugPlusNum('4');");
//                case DIGIT5 -> webEngine.executeScript("debugPlusNum('5');");
//                case DIGIT6 -> webEngine.executeScript("debugPlusNum('6');");
//                case DIGIT7 -> webEngine.executeScript("debugPlusNum('7');");
//                case DIGIT8 -> webEngine.executeScript("debugPlusNum('8');");
//                case DIGIT9 -> webEngine.executeScript("debugPlusNum('9');");
//                case BACK_SPACE -> webEngine.executeScript("debugPlusNum('-');");
//                case ENTER -> webEngine.executeScript("debugSubmit();");
//                case MINUS -> webEngine.executeScript("debugPlusNum('n');");
//                case PERIOD -> webEngine.executeScript("debugPlusNum('.');");
//                case ENTER -> webEngine.executeScript("key_check('+')");
//                default -> webEngine.executeScript("key_check('" + keyEvent.getCode().getChar() + "')");
            }
        });

        // Отключает нажатие CTRL и SHIFT если их отпустить
        browser.setOnKeyReleased(keyEvent -> {
            switch (keyEvent.getCode()) {
                case CONTROL -> ctrl = false;
                case SHIFT -> this.shift = false;
            }
        });

        // Перехват внепланового закрытия окна через панель задач или Alt+F4 и даже через диспетчер задач
        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            webEngine.executeScript("closeAnimation()");
        });

        browser.setPageFill(Color.TRANSPARENT); // Отключение фона, чтобы наложить поверх HTML
        scene.setFill(Color.TRANSPARENT); // Отключение фона, чтобы наложить поверх HTML
        stage.getIcons().add(new Image("file:src/main/resources/ru/itsokay/launcher/images/ok.png")); // Иконка приложения
        stage.setTitle("Hello!"); // Название приложения
        stage.setScene(scene); // хз
        stage.show(); // Что-то показывает
        webEngine.load(url.toString()); // Подгружает главный HTML файл
        // Исполняет скрипты выбора менюшки и сервера
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (Worker.State.SUCCEEDED == newValue) {
                webEngine.executeScript("loadPage();");
            }
        });


//        if (!checks.checkInternetConnection()) {
//            System.out.println("CONNECTION ERROR");
//        } else {
//            System.out.println("Ok.");
//        }
    }

    public static void main(String[] args) {
        launch();
    }
}