/**
 * 
 */
//ブラウザバックでブラウザにファイル参照情報が残っているので初期化する
window.onload = function(){
	const newdt = new DataTransfer();
	document.getElementById("inputFile").files = newdt.files;
}

var fileList = [];
let fileindex = 0;
function loadImage(obj) {
	
	var fields = document.getElementsByTagName("figure").length;
    if(obj.files.length + fields > 3){
    	alert("登録できるファイルは3枚までです。");
    	//inputfile.value = "";
    	dtupdate();
    	return false;
    };
    
   for(i = 0; i < obj.files.length; i++){
        fileList.push(obj.files[i]);
       
        var fileReader = new FileReader();
        fileReader.onload = (function (e) {
            var field = document.getElementById("imgPreviewField");
            var div = document.createElement("div");
            var figure = document.createElement("figure");
            var rmBtn = document.createElement("button");
            var img = new Image();
            img.src = e.target.result;
            rmBtn.type = "button";
            rmBtn.name = fileindex;
            //rmBtn.value = "削除";
            //rmBtn.class = "btn btn-outline-secondary btn-sm";
            rmBtn.className = ('btn btn-outline-secondary btn-sm');
            rmBtn.textContent = "削除";
            var str = "img-" + String(rmBtn.name);
            rmBtn.onclick = (function () {
				delete fileList[rmBtn.name];
				dtupdate()
                document.getElementById(str).remove();
            });
            div.setAttribute("id", str);
            div.setAttribute("class", "item");
            div.appendChild(figure);
            figure.appendChild(img);
            div.appendChild(rmBtn);
            field.appendChild(div);
            fileindex++;
        });
        fileReader.readAsDataURL(obj.files[i]);
   }
   dtupdate();
}

function dtupdate() {
   const dt = new DataTransfer();
   console.log('fileList');
   fileList.forEach((value, index) => {
    	console.log('Index: ' + index + ' Value: ' + value);
    	dt.items.add(value);
   });
   document.getElementById("inputFile").files = dt.files;
   //document.querySelector('multiple').files = dt.files;
}	

//var af_count = 0;
function deleteForm(id) {
	
	document.getElementById("dlt"+ id).value = 'do_delete';
	//document.getElementById("dlt"+ id).value = id;
	//af_count++;
	
	const element = document.getElementById(id); 
	element.remove();
}

//function download(dlimg) {
    //location.href = '/facility/download/'+dlimg;
//}
