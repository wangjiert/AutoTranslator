var xhr = new XMLHttpRequest();
function send() {
	var button = document.getElementById("button");
	button.disabled = true;
	if (button.value == "download") {
		var form = document.createElement("form");
		form.method = "post";
		form.action = "manager.do?cmd=download";
		form.submit();
		button.value = "send";
		button.disabled = false;
		return;
	}
	var file = document.getElementById('fileUpload');
	if (file.value == null || file.value == "") {
		alert("请选择上传文件!!!");
		return;
	}
	var fd = new FormData();
	fd.append("fileUpload0", file.files[0]);
	fd.append("cmd", "xmltoexcel");
	fd.append("fileCount",1);
	xhr.open("POST", "manager.do");
	xhr.onreadystatechange = onReady;
	xhr.send(fd);
}
function onReady() {
	if (xhr.readyState == 4 && xhr.status == 200) {
		var button = document.getElementById("button");
		if (xhr.responseText == "Successed") {
			button.value = "download";
		} else {
			alert(xhr.responseText);
		}
		button.disabled = false;
	}
}