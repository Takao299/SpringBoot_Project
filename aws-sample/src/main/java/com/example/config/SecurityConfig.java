package com.example.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.util.AuthAdminUser;
import com.example.util.AuthBusiness;
import com.example.util.AuthFacility;
import com.example.util.AuthMember;
import com.example.util.AuthReservation;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // 「/login」をアクセス可能にします
                .antMatchers("/login","/register","/hello","/android/**").permitAll()
                // 「/adminの配下」は、権限のあるユーザだけアクセス可能にします	//自分の権限を変える場合再ログイン必要
                .antMatchers(HttpMethod.GET, "/adminUser/**").hasAnyAuthority( AuthAdminUser.EDIT_ADMINUSER.name(), AuthAdminUser.VIEW_ADMINUSER.name() )
                .antMatchers(HttpMethod.GET, "/member/**").hasAnyAuthority( AuthMember.EDIT_MEMBER.name(), AuthMember.VIEW_MEMBER.name() )
                .antMatchers(HttpMethod.GET, "/facility/**").hasAnyAuthority( AuthFacility.EDIT_FACILITY.name(), AuthFacility.VIEW_FACILITY.name() )
                .antMatchers(HttpMethod.GET, "/reservation/**").hasAnyAuthority( AuthReservation.EDIT_RESERVATION.name(), AuthReservation.VIEW_RESERVATION.name() )
                .antMatchers(HttpMethod.GET, "/system/**").hasAnyAuthority( AuthBusiness.EDIT_BUSINESS.name(), AuthBusiness.VIEW_BUSINESS.name() )

                .antMatchers(HttpMethod.POST, "/adminUser/**").hasAuthority( AuthAdminUser.EDIT_ADMINUSER.name())
                .antMatchers(HttpMethod.POST, "/member/**").hasAuthority( AuthMember.EDIT_MEMBER.name())
                .antMatchers(HttpMethod.POST, "/facility/**").hasAuthority( AuthFacility.EDIT_FACILITY.name())
                .antMatchers(HttpMethod.POST, "/reservation/**").hasAuthority( AuthReservation.EDIT_RESERVATION.name())
                .antMatchers(HttpMethod.POST, "/system/**").hasAnyAuthority( AuthBusiness.EDIT_BUSINESS.name())
                //メソッド単位で権限制御する方法は@PreAuthorize
                .anyRequest().authenticated() //それ以外は直リンク禁止
            )
	        //androidからのリクエストはCSRF対策を無効化（独自セッションによって管理）
	        .csrf((csrf) -> csrf
	                .ignoringAntMatchers("/android/**")
	        )
            .formLogin(login -> login
            	.loginProcessingUrl("/login") //追加
                .loginPage("/login")
                .defaultSuccessUrl("/",true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .permitAll()
            )
            .rememberMe(me -> me
            	// keyには、好きな文字列
            	.key("Unique and Secret")
            );
        return http.build();
    }

}

/*
                .requestMatchers("/login").permitAll()
                // 「/adminの配下」は、権限のあるユーザだけアクセス可能にします	//自分の権限を変える場合再ログイン必要
                .requestMatchers(HttpMethod.GET, "/adminUser/**").hasAnyAuthority( AuthAdminUser.EDIT_ADMINUSER.name(), AuthAdminUser.VIEW_ADMINUSER.name() )
                .requestMatchers(HttpMethod.GET, "/member/**").hasAnyAuthority( AuthMember.EDIT_MEMBER.name(), AuthMember.VIEW_MEMBER.name() )
                .requestMatchers(HttpMethod.GET, "/facility/**").hasAnyAuthority( AuthFacility.EDIT_FACILITY.name(), AuthFacility.VIEW_FACILITY.name() )
                .requestMatchers(HttpMethod.GET, "/reservation/**").hasAnyAuthority( AuthReservation.EDIT_RESERVATION.name(), AuthReservation.VIEW_RESERVATION.name() )

                .requestMatchers(HttpMethod.POST, "/adminUser/**").hasAuthority( AuthAdminUser.EDIT_ADMINUSER.name())
                .requestMatchers(HttpMethod.POST, "/member/**").hasAuthority( AuthMember.EDIT_MEMBER.name())
                .requestMatchers(HttpMethod.POST, "/facility/**").hasAuthority( AuthFacility.EDIT_FACILITY.name())
                .requestMatchers(HttpMethod.POST, "/reservation/**").hasAuthority( AuthReservation.EDIT_RESERVATION.name())
*/
