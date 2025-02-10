# SpringBoot_Project

【IDE】
Eclipse IDE for Java Developers
Version: 2023-09 (4.29.0)


------ メール認証について ------
メール認証機能ではGmailのSMTPサーバーを利用しています。
SpringBoot(Webアプリ)のapplicaiton.ymlに、
送信用のメールアドレスとパスワードが必要になります。


------ ページネーション等の機能について ------
会員一覧画面のみ検索、ページネーション、表のソート、件数表示を自作しています。
他ページのCSV・Excel出力、印刷、検索、ページネーション、表のソートはDataTablesライブラリによる機能です。


------ 画像アップロードについて ------
アップロード画像は登録ボタンを押下すると一時保存され、
バリデーションエラーが無い場合永久保存にコピーされます。


------ ブラウザバック等について ------
ブラウザバックにはcsrfトークンで対応し、
重複・同時・連打更新にはversionアノテーションで対応します。


------ ユーザ権限について ------
Postリクエスト、Getリクエストがそれぞれユーザ権限で制御されます。
EditはPost・Get両方が許可され、
ViewはGetのみ許可されます。


------ エラー対処 ------
The bean 'awsS3ResourceLoaderProperties', defined in class path resource 
実行時に上記エラーが出た場合、ソース→クリーンアップ