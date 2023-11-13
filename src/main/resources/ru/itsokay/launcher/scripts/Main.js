// Отключает вывод контекстного меню ПКМ
document.oncontextmenu = function () {
    return false;
}

// Отключает использование хоткеев с CTRL
document.addEventListener("keydown", function (event) {
    if (event.ctrlKey) {
        event.preventDefault();
    }
});

//////
//let debug_count = "";
//let debug_element = "AddonsInfo";
//
//function debugPlusNum(n) {
//    if (n === "-") {
//        debug_count = debug_count.slice(0, -1);
//    } else if (n === "n") {
//        if (debug_count[0] === "-") {
//            debug_count = debug_count.slice(1);
//        } else {
//            debug_count = "-" + debug_count;
//        }
//    } else {
//        debug_count += n;
//    }
//    document.getElementById("text").textContent = debug_count;
//    info(debug_count);
//}
//
//function debugSubmit() {
//    document.getElementsByClassName(debug_element)[0].style.width = "calc(100% - " + debug_count + "px)"
//}
//////

let serversCount = 3 // Количество спаршеных серверов (пока что здесь просто число, потом будет значение браться с сайта)

let launcherPage = "Play"; // Addons / Play / Settings
function getPage() {
    return launcherPage;
}

let SelectedServer = "Server1"; // Выбранный сервер

let is_closing = false; // Закрывается ли приложение на данный момент, чтобы не дрочили кнопку
let is_minimizing = false; // Тоже самое, что и выше
let page_loading = false;
let on_menu = false;

var closebtn = document.getElementById("close"); // Объект кнопки закрытия приложения
var minimizebtn = document.getElementById("minimize"); // Объект кнопки сворачивания приложения
var lockSpace = document.getElementById("lockSpace"); // При появлении левой минюшки блокирует доступ к остальным кнопкам и затемняет экран
// var play = document.getElementById("play");

// var selected_textarea = null;
// function set_textarea(textarea) {
//     selected_textarea = textarea;
// }

// function key_check(key) {
//     if (key === "+") confirm_product();
//     if (selected_textarea === null) return;
//     text = document.getElementById(selected_textarea).textContent
//     if (key.charCodeAt(0) === 8) {
//         document.getElementById(selected_textarea).textContent = text.slice(0, -1)
//         return;
//     }
//     if ("ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".indexOf(key) === -1 || (selected_textarea.indexOf("INT") > -1 && "0123456789".indexOf(key) < 0)) return;
//     document.getElementById(selected_textarea).textContent += key
//     info(document.getElementById(selected_textarea).textContent)
// }

// Выводит инфу в консоль
function info(message) {
    javaConnector.test(message);
}

// Изменение страницы
function changePage(page) {
    launcherPage = page;
    loadPage();
}

// Выключение выбранного сервера
function SelectSelectedServer() {
    let front = document.getElementById(SelectedServer).getElementsByClassName("ServerFront")[0];
    front.style.left = "-3px";
    front.style.top = "-3px";
    front.style.border = "solid #b9b9b9";
}

// Загрузка страницы
function loadPage() {
    lil_window = false;
    page_loading = true;
    lockSpace.style.background = "rgba(22,22,22,1)";
    setTimeout(function () {
        var page_content = javaConnector.loadPage(launcherPage);
        document.getElementById("launcherPage").innerHTML = "";
        document.getElementById("launcherPage").innerHTML += page_content;
    }, 200);

    // Отображения выбранной страницы в менюшке
    switch (launcherPage) {
        case "Addons":
            data_type = "products";
            document.getElementById("LMenuSelected").style.top = "-35%";
            break;
        case "Play":
            data_type = "results";
            document.getElementById("LMenuSelected").style.top = "45%";
            setTimeout(function () {
                SelectSelectedServer();
            }, 250);
            break;
        case "Settings":
            data_type = "members";
            document.getElementById("LMenuSelected").style.top = "125%";
            break;
    }
    setTimeout(function () {
        if (on_menu) {
            lockSpace.style.background = "rgba(42,42,42,0.5)";
        } else {
            lockSpace.style.background = "rgba(0, 0, 0, 0)";
            if (lockSpace.style.top === "0px" || lockSpace.style.top === "") {
                setTimeout(function () {
                    lockSpace.style.clipPath = null;
                    lockSpace.style.top = "100%";
                }, 100);
            }

        }
        load_data(data_type);
        page_loading = false;
    }, 300);
}

function load_data(data_type) {
    let data = javaConnector.getPartyData(data_type);
    if (data === "") return;
    switch (data_type) {
        case "results":
            let stats = javaConnector.getStats().split("&");
            document.getElementById("Members").innerHTML = document.getElementById("Members").innerHTML.replace("0", stats[0]);
            document.getElementById("Products").innerHTML = document.getElementById("Products").innerHTML.replace("0", stats[1]);
            document.getElementById("Cost").innerHTML = document.getElementById("Cost").innerHTML.replace("0", stats[2]);
            document.getElementById("Debt").innerHTML = document.getElementById("Debt").innerHTML.replace("0", stats[3]);
            for (let rawProduct of data.split("%")) {
                if (rawProduct === "") break;
                let member = rawProduct.split("&");
                document.getElementById("results").innerHTML +=
                    "<div class=\"data\">" +
                    "    <a class=\"member\">" + member[0] + "</a>" +
                    "    <a class=\"debt\">" + member[1] + "</a>" +
                    "</div>";
            }
            break;
        case "products":
            for (let rawProduct of data.split("%")) {
                if (rawProduct === "") break;
                let product = rawProduct.split("&");
                document.getElementById("results").innerHTML +=
                    "<div class=\"data\" onmouseup='remove_object(this)' ondblclick='add_product(this)'>" +
                    "    <a class=\"name\">" + product[0] + "</a>" +
                    "    <a class=\"cost\">" + product[1] + "</a>" +
                    "    <a class=\"number\">" + product[2] + "</a>" +
                    "</div>";
            }
            break;
        case "members":
            for (let rawProduct of data.split("%")) {
                if (rawProduct === "") break;
                let member = rawProduct.split("&");
                document.getElementById("results").innerHTML +=
                    "<div class=\"data\" onmouseup='remove_object(this)' ondblclick='add_member(this)'>" +
                    "    <a class=\"member-name\">" + member[0] + "</a>" +
                    "    <a class=\"paid\">" + member[1] + "</a>" +
                    "    <a class=\"exceptions\">" + member[2] + "</a>" +
                    "</div>";
            }
    }
}

// Блокировка перемещения окна при нажатии на кнопки закрытия или сворачивания
function blockMove() {
    javaConnector.blockMove();
}

// Обработка клика на кнопку закрытия
closebtn.onclick = function (e) {
    if (e.button !== 0 || is_closing) {
        return
    }
    is_closing = true;
    closeAnimation();
}

// Анимация закрытия приложения
function closeAnimation() {
    var HTMLwindow = document.getElementById("window");
    var radius = 140;
    var x = 0;
    var id = setInterval(frame, 15);
    var stopAnimation = 0;
    lockSpace.style.background = "rgba(22,22,22,1)";
    lockSpace.style.top = "30px";

    function frame() {
        if (stopAnimation === 2) {
            close();
            clearInterval(id);
        } else if (radius > 0) {
            HTMLwindow.style.clipPath = `circle(${radius}% at right 21px top 15px)`;
            radius -= Math.exp(2.2 - x / 16);
            x += 0.4;
        } else {
            HTMLwindow.style.visibility = "hidden";
            stopAnimation++;
        }
    }
}

// Закрытие приложения
function close() {
    javaConnector.close();
}

// Оброботка клика на кнопку сворачивания приложения и анимация сворачивания
minimizebtn.onclick = function (e) {
    if (e.button !== 0 || is_minimizing) {
        return
    }
    is_minimizing = true;
    var HTMLwindow = document.getElementById("window");
    var height = 0;
    var x = 0;
    var id = setInterval(frame, 10);
    var stopAnimation = 0;

    function frame() {
        height += Math.exp(2 - x / 16)
        x += 0.5;
        if (stopAnimation === 2) {
            minimize();
            clearInterval(id);
        } else if (height >= 100) {
            HTMLwindow.style.visibility = "hidden";
            HTMLwindow.style.clipPath = null; // удаление clipPath из css для исправления бага
            stopAnimation++;
        } else {
            HTMLwindow.style.clipPath = `inset(0 0 ${height}% 0)`;
        }
    }
}

// Сворачивание приложения
function minimize() {
    document.getElementById("window").style.visibility = "visible";
    javaConnector.minimize();
    is_minimizing = false;
}

// play.onclick = function (e) {
//     if (e.button !== 0) {
//         return
//     }
//     javaConnector.play();
// }

// Обротка закрытия экрана при наведении на левую менюшку
function lockScreen() {
    if (!page_loading) {
        lockSpace.style.background = "rgba(42,42,42,0.5)";
        lockSpace.style.clipPath = `inset(30px 0 0 0)`;
        lockSpace.style.top = "0";
        on_menu = true;
    }
}

// Возобнавление функциональности экрана приложения
function unlockScreen() {
    on_menu = false;
    if (!page_loading) {
        lockSpace.style.background = "rgba(0, 0, 0, 0)";
        setTimeout(function () {
            lockSpace.style.clipPath = null;
            lockSpace.style.top = "100%";
        }, 200);
    }
}

// Сброс выбора предыдущего сервера
function selectServer(serverID) {
    let front = document.getElementById(SelectedServer).getElementsByClassName("ServerFront")[0];
    front.style.left = "0";
    front.style.top = "0";
    front.style.border = null;
    SelectedServer = serverID;
    SelectSelectedServer();
}

// Изменение размера окна
function resizeClick(c) {
    javaConnector.resize(c);
}

// Переход в полный экран при дабл клике
function fullScreen() {
    document.getElementById("ResizeBlocks").style.display = "none";
    document.getElementById("ExitFullScreen").style.display = "block";
    document.getElementById("window").style.borderRadius = "0";
    javaConnector.fullScreen();
}

// Выход из полного экрана
function exitFullScreen() {
    document.getElementById("window").style.borderRadius = null;
    document.getElementById("ResizeBlocks").style.display = null;
    document.getElementById("ExitFullScreen").style.display = "none";
}

// Подключение JSConnector (хз чё это, честно)
var jsConnector = {
    showResult: function (result) {
        document.getElementById('result').innerHTML = result;
    }
};

// Абсолютно бесполезная хрень, тк она не работает. Должна вызывать функции JS из Java.
function getJsConnector() {
    return jsConnector;
}