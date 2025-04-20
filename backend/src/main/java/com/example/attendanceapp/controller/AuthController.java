package com.example.attendanceapp.controller;

import com.example.attendanceapp.dto.LoginRequest;
import com.example.attendanceapp.dto.LoginResponse;
import com.example.attendanceapp.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証関連のエンドポイントを提供するコントローラー
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    /**
     * Spring Securityの認証マネージャー
     * ユーザーの認証（ログイン）処理を担当
     */
    private final AuthenticationManager authenticationManager;

    /**
     * JWTトークンの生成や検証を行うユーティリティクラス
     */
    private final JwtUtil jwtUtil;

    /**
     * ユーザー情報を取得するためのサービス
     */
    private final UserDetailsService userDetailsService;

    /**
     * ログイン処理を行うエンドポイント
     * 職員番号とパスワードを受け取り、認証が成功した場合はJWTトークンを返す
     *
     * @param request ログインリクエスト（職員番号とパスワードを含む）
     * @return JWTトークンを含むログインレスポンス
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        // 職員番号とパスワードで認証を実行
        // 認証に失敗した場合は例外がスローされる
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmployeeNumber(),
                        request.getPassword()
                )
        );

        // 認証成功後、ユーザー詳細情報を取得
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmployeeNumber());

        // ユーザー名（職員番号）を使用してJWTトークンを生成
        String token = jwtUtil.generateToken(userDetails.getUsername());

        // 生成したトークンを含むレスポンスを返す
        return new LoginResponse(token);
    }
}