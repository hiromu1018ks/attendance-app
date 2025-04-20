package com.example.attendanceapp.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Securityの設定を行うクラス
 * アプリケーションのセキュリティポリシーやJWT認証の設定を定義します
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    /**
     * カスタム認証サービス
     * ユーザーの認証情報をデータベースから取得する処理を担当
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * JWTトークンによる認証を行うフィルター
     * リクエストヘッダーからJWTトークンを取得し、その有効性を検証
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * パスワードのハッシュ化に使用するエンコーダーを提供
     * BCryptアルゴリズムを使用してパスワードをセキュアに保存
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Securityの認証マネージャーを提供
     * ユーザー認証の実行を担当
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * セキュリティフィルターチェーンの設定
     * アプリケーションのセキュリティルールを定義
     * <p>
     * - CSRFプロテクションを無効化
     * - セッションレスな認証（JWTを使用）
     * - ログインエンドポイントは認証不要
     * - その他のエンドポイントは認証必須
     * - JWTフィルターを認証フィルターの前に配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CSRFトークンチェックを無効化（JWTを使用するため不要）
                .csrf(AbstractHttpConfigurer::disable)
                // セッション管理の設定
                .sessionManagement(session ->
                        // JWTを使用するためセッションを作成しない
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // URLごとのアクセス制御を設定
                .authorizeHttpRequests(auth -> auth
                        // ログインAPIは認証不要
                        .requestMatchers("/api/auth/login").permitAll()
                        // その他のリクエストは
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}