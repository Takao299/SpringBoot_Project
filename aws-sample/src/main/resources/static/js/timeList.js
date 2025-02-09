
function timeChange(obj) {
	var str = obj.value.split(',');
    var start = str[0];
    var end = str[1];
	document.getElementById("r-start").value = start;
	document.getElementById("r-end").value = end;
}
