package com.example.service;

import java.util.concurrent.Executors;

import javax.mail.internet.InternetAddress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
public class MailSenderService {
    @Autowired
    MailSender sender;

    //AmazonSESを利用する場合
    //@Value("${email.sender.name:no_email}")
    //private String sesEmail;

    //GmailのSMTPサーバーを利用する場合
    @Value("${spring.mail.username}")
    private String gmailEmail;

    public void sendVerifyMail(String customerEmail, String verificationCode) throws Exception {
    	sendVerifyMail(customerEmail, verificationCode, "new Customer", Title.New);
    }

    // メールを送信するにはこちらのメソッドを実行する
    public void sendVerifyMail(String customerEmail, String verificationCode, String customerName, Title title) throws Exception {
        // InternetAddressを使って「名前 <xxxx@gmail.com>」の形式に整形
        // メールアドレスはAmazonSESのコンソール上で検証済みとなっているアドレスを使用する
        // サンドボックスの制限を解除している場合は任意のメールアドレスを使用可能

    	String fromEmail = gmailEmail;
        //if(sesEmail!=null && sesEmail.equals("no_email")) {
        	//Amazon SESを使用
        	//fromEmail = sesEmail;
        	//AmazonSESConfig amazonSESConfig = new AmazonSESConfig();
        	//sender = amazonSESConfig.mailSender(amazonSESConfig.amazonSimpleEmailService());
        //}

        InternetAddress senderAddress = new InternetAddress(fromEmail, "test_sender", "ISO-2022-JP");
        InternetAddress recieverAddress = new InternetAddress(customerEmail, customerName, "ISO-2022-JP");

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        // 送信先の設定
        mailMessage.setTo(recieverAddress.toString());
        // 送信元の設定
        mailMessage.setFrom(senderAddress.toString());
        // 返信先の設定
        mailMessage.setReplyTo(senderAddress.toString());
        // 件名の設定
        mailMessage.setSubject(title.getTitle());
        // 本文の設定
        mailMessage.setText(verificationCode); //4桁のコードを送るのみ

        //メール送信 時間がかかるのでスレッドを分ける execute(() -> sender.send(mailMessage));
        Executors.newSingleThreadExecutor().execute(new Runnable(){
            @Override
            public void run() {
                sender.send(mailMessage);
            }
        });

    }

    @Getter
    @AllArgsConstructor
    public enum Title {
        New("android学習用＿メールアドレス認証コード"),
        Update("android学習用＿メールアドレス認証コード（変更）");

        private String title;
    }

}