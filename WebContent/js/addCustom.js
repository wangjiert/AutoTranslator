var xhr;
var body;
var allCustom;
window.onload = function() {
	allCustom = "";
	body = document.getElementById("body");
	xhr = new XMLHttpRequest();
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = check;
	xhr.send("cmd=queryCustomName");
}
function check() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		allCustom = "," + xhr.responseText;
		xhr.open("POST", "manager.do");
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		xhr.onreadystatechange = listLanguage;
		xhr.send("cmd=queryLanguage");
	}
}
function listLanguage() {
	var select = document.getElementById("language");
	var result = xhr.responseText;
	var results = result.split(",");
	select.size = results.length - 1;
	for (var i = 0; i < results.length - 1; i++) {
		select.options[i] = new Option(results[i], results[i]);
	}
}
function onReady() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		alert(xhr.responseText);
	}
}

function commit() {
	var custom = document.getElementById("custom");
	if(custom.value == "") {
		alert("请输入客户的名字!!!");
		return;
	}
	if(allCustom.indexOf("," + custom.value.replace(/(^\s*)|(\s*$)/g, "") + ",") >= 0) {
		alert("客户名字已存在!!!");
		return;
	}
	allCustom += custom.value.replace(/(^\s*)|(\s*$)/g, "") + ",";
	var language = getLanguage();
	if(language == "") {
		alert("请选择语言!!!");
		return;
	}
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = onReady;
	xhr.send("cmd=addCustom&custom=" + custom.value + "&language="
			+ language);
	language.value = "";
}
function query() {
	var table = document.getElementById("table");
	if (table != null) {
		body.removeChild(table);
	}
	xhr = new XMLHttpRequest();
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = listCustom;
	xhr.send("cmd=queryCustom");
}
function listCustom() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		var results = eval(xhr.responseText);
		var table = document.createElement("table");
		table.id = "table";
		table.setAttribute("border", "1");
		var tr = document.createElement("tr");
		var th = document.createElement("th");
		th.innerHTML = "id";
		tr.appendChild(th);
		th = document.createElement("th");
		th.innerHTML = "客户";
		tr.appendChild(th);
		th = document.createElement("th");
		th.innerHTML = "语言";
		tr.appendChild(th);
		table.appendChild(tr);
		for (var i = 0; i < results.length; i++) {
			tr = document.createElement("tr");
			var td = document.createElement("td");
			td.innerHTML = i;
			tr.appendChild(td);
			td = document.createElement("td");
			td.innerHTML = results[i].customName;
			tr.appendChild(td);
			td = document.createElement("td");
			td.innerHTML = results[i].language;
			tr.appendChild(td);
			table.appendChild(tr);
		}
		body.appendChild(table);
	}
}
function getLanguage() {
	var language="";
	var select = document.getElementById("language");
	for(var i = 0; i < select.length; i++) {
		if(select.options[i].selected){
			language += select.options[i].value + " ";
		}
	}
	return language;
}