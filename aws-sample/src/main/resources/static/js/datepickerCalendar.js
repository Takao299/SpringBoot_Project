$(function(){
	//特定の日付をセット
	//const off = ["0120", "0220", "0320", "0420", "0520", "0620", "0720", "0820", "0920", "1020", "1120", "1220"];

	//Datepicker
	$("#datepicker").datepicker({
		dateFormat: 'yy-mm-dd', //2024-07-31
		minDate: new Date(), //過去を選択できないようにする
		beforeShowDay: function (date) {
			let holiday = JapaneseHolidays.isHoliday(date);
			//臨時営業日に含まれている
			if ( tonday.indexOf(formatDate(date)) !== -1) {
				return [true, ""];
			//臨時休業日に含まれているまたは祝日または定休日の中に､選ばれた日付が含まれているとき  //date.getDay() == 0 || date.getDay() == 6 || off.indexOf(formatDay(date)) !== -1
			}else if(toffday.indexOf(formatDate(date)) !== -1 || holiday || offday.indexOf(date.getDay()) !== -1){
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
		//祝日かどうか
		let holiday = JapaneseHolidays.isHoliday(date); //ここが0、732042778458のような数値を弾いてくれる
		//今日
		let today = new Date();
		//今日と選択された日付を比較 (過去ならマイナス､未来ならプラスになる)
		let is_future = new Date(formatDate(date)) - new Date(formatDate(today)); //alert(is_future < 0);
		//臨時営業日に含まれている
		if ( tonday.indexOf(formatDate(date)) !== -1) {
			selectDay(e);
		//臨時休業日に含まれているまたは祝日または定休日の中に､､選ばれた日付が含まれているとき  //date.getDay() == 0 || date.getDay() == 6 ||  off.indexOf(formatDay(date)) !== -1
		}else if( toffday.indexOf(formatDate(date)) !== -1 || holiday || offday.indexOf(date.getDay()) !== -1 || is_future < 0 ){
			//アラート
			alert("その日は選択できません｡");
			//inputを空に
			$(this).val("");
		}else{
			selectDay(e);
		}
	});
	
	//YY-MM-DDで返す関数定義
	function formatDate(dt) {
		var y = dt.getFullYear();
		var m = ('0' + (dt.getMonth()+1)).slice(-2);
		var d = ('0' + dt.getDate()).slice(-2);
		return (y + '-' + m + '-' + d);
	}
	//MMDDで返す関数定義
	function formatDay(dt) {
		var m = ('0' + (dt.getMonth()+1)).slice(-2);
		var d = ('0' + dt.getDate()).slice(-2);
		return (m + d);
	}
	
	//ajax
	function selectDay(e) {
	    e.preventDefault();  // デフォルトのイベント(ページの遷移やデータ送信など)を無効にする
	    $.ajax({
	      url: $("#datepicker").attr("action"),  // リクエストを送信するURLを指定（action属性のurlを抽出）
	      type: "POST",  // HTTPメソッドを指定（デフォルトはGET）
	      data: {
	        //member: $("#select_member").val(),
	        facility: $("#select_facility").val(), // 送信データ
	        rday: $("#datepicker").val(),
	        _csrf: $("*[name=_csrf]").val()  // CSRFトークンを送信
	        //_csrf: $('input[name="_csrf"]').val()
	        
	      }
	    })
	    .done(function(data) {
			var day = $("#datepicker").val();
			var timeList = [];
			if(data.length==0){
				alert("予約できる時間がありません");
			}
			timeList.push("<option value='0'>時間を選択してください</option>");
			for (var i = 0; i < data.length; i++) {
				var date = new Date([day, data[i].rstart]);
				var time_s = ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2);
				date = new Date([day, data[i].rend]);
				var time_e = ('0' + date.getHours()).slice(-2) + ':' + ('0' + date.getMinutes()).slice(-2);
				var dis = "";
				if(data[i].reserved==true){
					dis = " disabled";
				}
				var item = "<option value="+time_s+","+time_e+dis+">"+time_s+" ～ "+time_e+"</option>";
				//console.log(item);
				timeList.push(item);
			}
			$("#dropdownlist").html(timeList);
			//$("#r-start").val() = null;
			//$("#r-end").val() = null;
			document.getElementById("r-start").value = null;
			document.getElementById("r-end").value = null;
			
	    })
	    .fail(function() {
	      alert("error!");  // 通信に失敗した場合の処理
	    })
  	}
  	
  	// input hiddenに開始時間・終了時間を入れる
  	/*
  	function timeChange(obj) {
		var str = obj.value.split(',');
	    var start = str[0];
	    var end = str[1];
		document.getElementById("r-start").value = start;
		document.getElementById("r-end").value = end;
	}*/
  	
});