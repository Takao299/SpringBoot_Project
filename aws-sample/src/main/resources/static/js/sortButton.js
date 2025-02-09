/**
 * 
 */
let ths = document.getElementsByTagName("th");
let page = document.getElementById("pageId").value;
let size = document.getElementById("sizeId").value;
//let sortP = document.getElementById("sortPId").value;
//let sortD = document.getElementById("sortDId").value;
//let element = "";
//console.log('ths.length:'+ths.length);
//console.log('pg:'+pg+' sz:'+sz);
for (var i = 0; i < ths.length; i++) {
	//console.log('name['+i+']:'+ths[i].id);
    ths[i].onclick = event => {
        let element = event.target;
        if (element.classList.contains('asc')) {
            element.classList.replace('asc', 'desc');
            //console.log('asc -> desc');
            //console.log('element:'+element.id);
            //location.href = "member_list/size";
            //location.href = '/member_list?page=0&size=5&sort=name&name.dir=desc' 
        } else if (element.classList.contains('desc')) {
            element.classList.replace('desc', 'asc');
            //console.log('desc -> asc');
        } else {
            element.classList.add('asc');
            //console.log('asc!');
        }
        
    	location.href 
	    = '/aws-sample/member?page='+ page
		+ '&size=' + size
		+ '&sort=' + element.id
		//+ '&' + element.id + '.dir=' + element.className;
		+ ',' + element.className;
        //console.log('element.className:'+element.className);
        //console.log('element.className:'+element.valueName);

        //Array.from(element.parentNode.children)	//他の同列のclassを消去
            //.filter(e => e !== element)
            //.forEach(e => e.classList.remove('asc', 'desc'));
    };
}