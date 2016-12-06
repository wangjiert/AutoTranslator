var xhr;
var button;
var actionValue;
window.onload = function() {
	xhr = new XMLHttpRequest();
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = listCustomName;
	xhr.send("cmd=queryCustomName");
	button = document.getElementById("button");
	button.addEventListener("click", handle);
	actionValue = "fillExcel";
}
function listCustomName() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		var select = document.getElementById("select");
		var result = xhr.responseText;
		var results = result.split(",");
		for (var i = 0; i < results.length - 1; i++) {
			select.options[i] = new Option(results[i], results[i]);
		}
	}
}

function radioChange(radio) {
	actionValue = radio.value;
}
function downloadFile() {
	button.disabled = true;
	button.value = "正在下载";
	var form = document.createElement("form");
	form.method = "post";
	form.action = "manager.do?cmd=download";
	form.submit();
	button.value = "send";
	button.removeEventListener("click", downloadFile);
	button.addEventListener("click", handle);
	button.disabled = false;
	document.getElementById("radio0").disabled = false;
	document.getElementById("radio1").disabled = false;
}
function onReady() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		button.value = "下载";
		button.removeEventListener("click", handle);
		button.addEventListener("click", downloadFile);
		button.disabled = false;
	}
}
function handle() {
	var file = document.getElementById('fileUpload');
	if(file.value == null||file.value ==""){
		alert("请选择上传文件!!!");
		return;
	}
	var select = document.getElementById('select');
	button.value = "正在上传...";
	button.disabled = true;
	document.getElementById("radio0").disabled = true;
	document.getElementById("radio1").disabled = true;
	var fd = new FormData();
	
	fd.append("fileUpload", file.files[0]);
	fd.append("cmd", actionValue);
	fd.append("customName",select.options[select.selectedIndex].text);
	fd.append("method", "create");
	xhr.open("POST", "manager.do");
	xhr.onreadystatechange = onReady;
	xhr.send(fd);
}