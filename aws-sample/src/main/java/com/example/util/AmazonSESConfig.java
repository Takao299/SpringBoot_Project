package com.example.util;
/*
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.mail.simplemail.SimpleEmailServiceMailSender;
import org.springframework.mail.MailSender;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
*/
//MailSenderをGmailとAmazonSESとで分けたいのでBeanしない
//@Configuration
public class AmazonSESConfig {

	/*
    @Value("${AWS_ACCESS_KEY_ID:no_access_key}")
    private String AWS_ACCESS_KEY_ID;

    @Value("${AWS_SECRET_ACCESS_KEY:no_secret_access_key}")
    private String AWS_SECRET_ACCESS_KEY;

    //@Value("${AWS_DEFAULT_REGION:no_region}")
    //private String AWS_DEFAULT_REGION;

    //@Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        // AWSの認証情報とリージョンを設定
        // accessKeyとsecretKeyはIAM→アクセス管理→ユーザ→セキュリティ認証情報→アクセスキーで表示されるものを使用する
        return AmazonSimpleEmailServiceClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(AWS_ACCESS_KEY_ID,AWS_SECRET_ACCESS_KEY)))
            .withRegion(Regions.AP_NORTHEAST_3)
            .build();
    }

    //@Bean
    public MailSender mailSender(AmazonSimpleEmailService ses) {
        return new SimpleEmailServiceMailSender(ses);
    }
    */
}
