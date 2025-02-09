/**
 * 
 */
function passacc(aaa) {

	if (aaa == 'true'){
		document.getElementById("newP").value = true;	//th:fieldに対して反映させることができる
		document.getElementById("newP2").value = true;
	} else {
		document.getElementById("newP").value = false;
		document.getElementById("newP2").value = false;
	}
}	

document.addEventListener('DOMContentLoaded', function() {
    // .accordion-group内のinputタグの値が全て空の場合非表示にする
    var accordionGroups = document.querySelectorAll('.accordion-group');
    accordionGroups.forEach(function(group) {
        var inputs = group.querySelectorAll('input');
        var isEmpty = Array.from(inputs).every(function(input) {
            return input.value === '';
        });
        if (isEmpty) {
            group.style.display = 'none';
        } else {
            group.previousElementSibling.classList.add('active');
        }
    });

    // .accordion-labelをクリックするたびに、その次の.accordion-groupを開け閉めする
    var accordionLabels = document.querySelectorAll('.accordion-label');
    accordionLabels.forEach(function(label) {
        label.addEventListener('click', function() {
            this.classList.toggle('active');
            this.nextElementSibling.style.display = this.nextElementSibling.style.display === 'none' ? '' : 'none';
        });
    });
});

