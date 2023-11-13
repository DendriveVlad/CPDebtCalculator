let lil_window = false;
let error = false;
mouseon = false;
let edit = -1;

function key_check(obj) {
    obj.style.background = "";
    var validChars;
    if (obj.id.indexOf("INT") > -1) {
        validChars = /[^0-9]/gi;
    } else {
        validChars = /[^a-zA-Z0-9а-яА-Я .,:()]/gi;
    }
    if (validChars.test(obj.value) || parseInt(obj.value) === 0) {
        obj.style.background = "#e13333";
        error = true;
    } else {
        obj.style.background = "";
        error = false;
    }
}

function add_product(obj = null) {
    var name = "";
    var cost = "";
    var number = "";
    if (obj) {
        edit = Array.from(document.getElementById("results").getElementsByTagName("*")).indexOf(obj) / 4;
        name = obj.getElementsByClassName("name").item(0).innerHTML;
        cost = obj.getElementsByClassName("cost").item(0).innerHTML;
        number = obj.getElementsByClassName("number").item(0).innerHTML;
        if (lil_window) {
            document.getElementById("pname").value = name;
            document.getElementById("INTpcost").value = cost;
            document.getElementById("INTpnumber").value = number;
            return;
        }
    }
    lil_window = !lil_window;
    let add = document.getElementById("add");
    if (lil_window) {
        const page = javaConnector.loadPage("product");
        add.style.top = "62px";
        add.innerHTML = page;
        document.getElementById("pname").value = name;
        document.getElementById("INTpcost").value = cost;
        document.getElementById("INTpnumber").value = number;
    } else {
        add.style.top = "100%";
        add.innerHTML = "";
        edit = -1;
    }
}

function add_member(obj = null) {
    var name = "";
    var paid = "";
    if (obj) {
        edit = Array.from(document.getElementById("results").getElementsByTagName("*")).indexOf(obj) / 4;
        name = obj.getElementsByClassName("member-name").item(0).innerHTML;
        paid = obj.getElementsByClassName("paid").item(0).innerHTML;
        if (lil_window) {
            document.getElementById("mname").value = name;
            document.getElementById("INTmpaid").value = paid;
            let n = 0;
            for (let product of javaConnector.getPartyData("products").split("%").slice(0, -1)) {
                document.getElementById("mexcept").innerHTML += "<option " + javaConnector.isProductSelectedByMember(edit, n) + " value='" + n + "' onclick=\"except_select(this)\">" + product.split("&")[0] + "</option>";
                n++;
            }
            return;
        }
    }
    lil_window = !lil_window;
    let add = document.getElementById("add");
    if (lil_window) {
        const page = javaConnector.loadPage("member");
        add.style.top = "62px";
        add.innerHTML = page;
        document.getElementById("mname").value = name;
        document.getElementById("INTmpaid").value = paid;
        let n = 0;
        for (let product of javaConnector.getPartyData("products").split("%").slice(0, -1)) {
            if (edit >= 0) document.getElementById("mexcept").innerHTML += "<option " + javaConnector.isProductSelectedByMember(edit, n) + " value='" + n + "' onclick=\"except_select(this)\">" + product.split("&")[0] + "</option>";
            else document.getElementById("mexcept").innerHTML += "<option value='" + n + "' onclick=\"except_select(this)\">" + product.split("&")[0] + "</option>";
            n++;
        }
    } else {
        add.style.top = "100%";
        add.innerHTML = "";
    }
}

function except_select(obj) {
    obj.selected = !obj.selected;

}

function confirm_product() {
    let name = document.getElementById("pname").value.replace(/ +(?= )/g, '').trim();
    if (!name) {
        document.getElementById("pname").style.background = "#e33131";
        error = true;
    }
    let cost = document.getElementById("INTpcost").value;
    if (!cost) {
        document.getElementById("INTpcost").style.background = "#e33131";
        error = true;
    }
    let number = document.getElementById("INTpnumber").value;
    if (error) return;
    if (!number) number = 1;
    if (edit >= 0) {
        javaConnector.editProduct(edit, name, cost, number);
        edit_doc = document.getElementById("results").getElementsByClassName("data").item(edit);
        edit_doc.getElementsByClassName("name").item(0).innerHTML = name;
        edit_doc.getElementsByClassName("cost").item(0).innerHTML = parseInt(cost);
        edit_doc.getElementsByClassName("number").item(0).innerHTML = parseInt(number);
        edit = -1;
        add_product();
        return;
    }
    // for (let i = 0; i < 20; i++) { number = i;
    javaConnector.addProduct(name, cost, number);
    document.getElementById("results").innerHTML +=
        "<div class=\"data\" onmouseup='remove_object(this)' ondblclick='add_product(this)'>" +
        "    <a class=\"name\">" + name + "</a>" +
        "    <a class=\"cost\">" + parseInt(cost) + "</a>" +
        "    <a class=\"number\">" + parseInt(number) + "</a>" +
        "</div>";
    add_product();
    // }
}

function confirm_member() {
    let name = document.getElementById("mname").value.replace(/ +(?= )/g, '').trim();
    if (!name) {
        document.getElementById("mname").style.background = "#e33131";
        error = true;
    }
    let paid = document.getElementById("INTmpaid").value;
    let excepts = "";
    for (let o of document.getElementById("mexcept").options) if (o.selected) excepts += o.value + "&";
    if (error) return;
    if (!paid) paid = 0;
    if (!excepts) excepts = "-1";
    if (edit >= 0) {
        javaConnector.editMember(edit, name, paid, excepts);
        edit_doc = document.getElementById("results").getElementsByClassName("data").item(edit);
        edit_doc.getElementsByClassName("member-name").item(0).innerHTML = name;
        edit_doc.getElementsByClassName("paid").item(0).innerHTML = parseInt(paid);
        if (excepts !== -1) edit_doc.getElementsByClassName("exceptions").item(0).innerHTML = javaConnector.getMemberExcepts(edit);
        else edit_doc.getElementsByClassName("exceptions").item(0).innerHTML = ".";
        edit = -1;
        add_product();
        return;
    }
    // for (let i = 0; i < 20; i++) { number = i;
    javaConnector.addMember(name, paid, excepts);
    if (excepts !== "-1") {
        document.getElementById("results").innerHTML +=
            "<div class=\"data\" onmouseup='remove_object(this)' ondblclick='add_member(this)'>" +
            "    <a class=\"member-name\">" + name + "</a>" +
            "    <a class=\"paid\">" + parseInt(paid) + "</a>" +
            "    <a class=\"exceptions\">" + javaConnector.getMemberExcepts(edit) + "</a>" +
            "</div>";
    } else
        document.getElementById("results").innerHTML +=
            "<div class=\"data\" onmouseup='remove_object(this)' ondblclick='add_member(this)'>" +
            "    <a class=\"member-name\">" + name + "</a>" +
            "    <a class=\"paid\">" + parseInt(paid) + "</a>" +
            "    <a class=\"exceptions\">.</a>" +
            "</div>";
    add_product();
    // }
}

function remove_object(obj) {
    if (window.event.button === 2) {
        switch (getPage()) {
            case "Addons": {
                javaConnector.removeProduct(Array.from(document.getElementById("results").getElementsByTagName("*")).indexOf(obj) / 4);
                obj.remove();
                break;
            }
            case "Settings": {
                javaConnector.removeMember(Array.from(document.getElementById("results").getElementsByTagName("*")).indexOf(obj) / 4);
                obj.remove();
                break;
            }
        }
    }
}
