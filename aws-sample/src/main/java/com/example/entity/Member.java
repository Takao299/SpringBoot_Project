package com.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.example.validator.UnusedMailAddress;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@UnusedMailAddress	//重複制限
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "メールアドレスを入力して下さい")
    @Pattern(regexp = "[\\w\\-._]+@[\\w\\-._]+\\.[A-Za-z]+", message = "正しい形式で入力して下さい") //スペースだけの入力等もここで対応
    private String email;

    @Size(min = 2, max = 20)
    @NotNull(message = "名前を入力して下さい") // stripメソッドを用いた制御がされているので半角全角スペース等にも対応している
    private String name;

    private String password;

    private LocalDateTime deleteDateTime;

    @Version
    private Integer version;
}
