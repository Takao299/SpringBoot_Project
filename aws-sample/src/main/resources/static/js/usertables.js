$(function() {
  $("#user-table").dataTable({
    // DataTablesを日本語化する
    language: {
      url: "/webjars/datatables-plugins/i18n/ja.json",
      "buttons": {
        "csv": "CSV"
      }
    },
    // 各種ボタンを有効化する
    dom: "Bfrtip",
    buttons: ["excelHtml5", "csvHtml5", "print"]
  });
});
