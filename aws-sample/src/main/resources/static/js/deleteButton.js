
$(function() {
  $("#actDelete").on("click", function(e) {
    e.preventDefault();  // デフォルトのイベント(ページの遷移やデータ送信など)を無効にする
    $.ajax({
      url: $(this).attr("action"),  // リクエストを送信するURLを指定（action属性のurlを抽出）
      type: "POST",  // HTTPメソッドを指定（デフォルトはGET）
      data: {
        id: $("#deleteid").val(),
        _csrf: $("*[name=_csrf]").val()  // CSRFトークンを送信
      }
    })
    .done(function(data) {
		if(data=="member" || data=="facility"){
			//if (confirm('本当に削除してもよろしいですか？')) {
	    		//window.location.href = '/aws-sample/member/'+$("#memberid").val()+'/delete';
	    		//$.post( '/aws-sample/member/delete', 'id='+$("#memberid").val() )
	  		//}
	  		window.location.href = '/aws-sample/'+data;
  		}else{
			alert(data);
		}
    })
    .fail(function() {
      alert("error!");  // 通信に失敗した場合の処理
    })
  });
});

