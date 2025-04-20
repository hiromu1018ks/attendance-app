package com.example.attendanceapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWTトークンを使用した認証フィルター
 * リクエストごとにJWTトークンの検証と認証を行います
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWTトークンの生成と検証を行うユーティリティ
     */
    private final JwtUtil jwtUtil;

    /**
     * ユーザー情報をデータベースから取得するサービス
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * リクエストごとに実行される認証フィルター処理
     *
     * @param request     HTTPリクエスト
     * @param response    HTTPレスポンス
     * @param filterChain フィルターチェーン
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Authorizationヘッダーからトークンを取得
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String employeeNumber;

        // Bearer トークンが存在しない場合は、次のフィルターに処理を委譲
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer "以降の文字列をトークンとして取得
        token = authHeader.substring(7);
        // トークンから職員番号を抽出
        employeeNumber = jwtUtil.extractEmployeeNumber(token);

        // 職員番号が存在し、まだ認証が行われていない場合
        if (employeeNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // データベースからユーザー情報を取得
            UserDetails userDetails = userDetailsService.loadUserByUsername(employeeNumber);

            // トークンが有効な場合、認証情報を設定
            if (jwtUtil.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // リクエストの詳細情報を認証トークンに設定
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 認証情報をSecurityContextに設定
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 次のフィルターに処理を委譲
        filterChain.doFilter(request, response);
    }
}