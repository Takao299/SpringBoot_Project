$(function(){
	//Datepicker
	$("#datepicker").datepicker({
		dateFormat: 'yy-mm-dd', //2024-07-31
		minDate: new Date(), //過去を選択できないようにする
		beforeShowDay: function (date) {
			let holiday = JapaneseHolidays.isHoliday(date);
			//臨時営業・休業日が既にある
			if ( tempday.indexOf(formatDate(date)) !== -1) {
				return [false, "ui-state-disabled"];
			}else{
				return [true, ""];
			}
		}
	});
	//テキスト編集した際のアラート
	$("#datepicker").on("change", function(e){
		//内容を取得
		let val = $(this).val();
		//整形
		let date = new Date(val);
		//今日
		let today = new Date();
		//今日と選択された日付を比較 (過去ならマイナス､未来ならプラスになる 今日は0)
		let is_future = new Date(formatDate(date)) - new Date(formatDate(today)); //alert(is_future < 0);
		
		//臨時営業・休業日が重複しておらず 過去ではなく 4年先以内の"数値"になる
		if ( tempday.indexOf(formatDate(date)) === -1 && is_future >= 0 && is_future < 126316800000 ) {
			//console.log(val);
			//console.log(date);
			//console.log(formatDate(date));
			//console.log(is_future);
		//それ以外のテキスト入力はエラー
		}else{
			//アラート
			alert("その日は選択できません｡");
			//inputを空に
			$(this).val("");
		}
	});
	
	//YY-MM-DDで返す関数定義
	function formatDate(dt) {
		var y = dt.getFullYear();
		var m = ('0' + (dt.getMonth()+1)).slice(-2);
		var d = ('0' + dt.getDate()).slice(-2);
		return (y + '-' + m + '-' + d);
	}

});