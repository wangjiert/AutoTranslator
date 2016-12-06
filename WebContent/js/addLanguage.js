var xhr;
var body;
var allLanguages;
window.onload = function() {
	allLanguages="";	
	xhr = new XMLHttpRequest();
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = check;
	xhr.send("cmd=queryLanguage");
	body = document.getElementById("body");
}
function check(){
	if (xhr.readyState == 4 && xhr.status == 200) {
		allLanguages = ","+xhr.responseText;
	}
}
function query() {
	var table = document.getElementById("table");
	if(table != null) {
		body.removeChild(table);
	}
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = listLanguage;
	xhr.send("cmd=queryLanguage");
}
function listLanguage() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		var result = xhr.responseText;
		var results = result.split(",");
		var table = document.createElement("table");
		table.id="table";
		table.setAttribute("border","1");
		var tr=document.createElement("tr");
		var th = document.createElement("th");
		th.innerHTML="id";
		tr.appendChild(th);
		th = document.createElement("th");
		th.innerHTML="语言";
		tr.appendChild(th);
		table.appendChild(tr);
		for(var i = 0; i < results.length - 1; i++) {
			 tr=document.createElement("tr");
			 var td=document.createElement("td");
			 td.innerHTML=i;
			 tr.appendChild(td);
			 td=document.createElement("td");
			 td.innerHTML=results[i];
			 tr.appendChild(td);
			 table.appendChild(tr);
		}
		body.appendChild(table);
	}
}
function send() {
	var text = document.getElementById("language").value.replace(/(^\s*)|(\s*$)/g, "");
	if(text==""){
		alert("请输入语言!!!!");
		return;
	}
	if(allLanguages.indexOf("," + text + ",") >= 0){
		alert("语言已存在!!!!");
		return;
	}
	allLanguages += text + ",";
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = onReady;
	xhr.send("cmd=addLanguage&language=" + text);

}
function onReady() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		alert(xhr.responseText);
	}
}