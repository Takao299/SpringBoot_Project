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


------ エラー対処 ------
The bean 'awsS3ResourceLoaderProperties', defined in class path resource 
実行時に上記エラーが出た場合、ソース→クリーンアップ