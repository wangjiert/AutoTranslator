var xhr;
var method = "create";
var button;
window.onload = function() {
	button = document.getElementById("commit");
	xhr = new XMLHttpRequest();
	xhr.open("POST", "manager.do");
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	xhr.onreadystatechange = listCustomName;
	xhr.send("cmd=queryCustomName");
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
function radioSelect(radio) {
	if (radio.id == "radio1") {
		method = "create";
	} else {
		method = "merge";
	}
	console.log(method);
}
function send() {
	if(button.value == "download") {
		button.disabled = true;
		var form = document.createElement("form");
		form.method = "post";
		form.action = "manager.do?cmd=download";
		form.submit();
		button.value = "commit";
		button.disabled = false;
		return;
	}
	var select = document.getElementById('select');
	var file = document.getElementById('fileUpload');
	if(file.value == null||file.value ==""){
		alert("请选择上传文件!!!");
		return;
	}
	button.disabled = true;
	var length = file.files.length;
	var fd = new FormData();
	for (var i = 0; i < length; i++) {
		fd.append("fileUpload"+i, file.files[i]);
	}
	fd.append("fileCount",length+"");
	fd.append("cmd", "addData");
	fd.append("method", method);
	fd.append("customName",select.options[select.selectedIndex].text);
	xhr.open("POST", "manager.do");
	xhr.onreadystatechange = onReady;
	xhr.send(fd);
}
function onReady() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		if(xhr.responseText == "Successed") {
			alert("处理完成");
		}
		else if(xhr.responseText == "Successed with comflict") {
			button.value = "download";
		}
		else {
			alert(xhr.responseText);
		}
		button.disabled = false;
	}
}