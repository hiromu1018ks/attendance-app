package com.example.attendanceapp.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWTトークンの生成、検証、データ抽出を行うユーティリティクラス
 * Spring Securityと連携して認証機能を提供します
 */
@Component
public class JwtUtil {
    /**
     * JWTトークンの有効期限（ミリ秒）
     * 1000ミリ秒 × 60秒 × 60分 × 24時間 = 1日
     * トークンの有効期限が切れると、ユーザーは再度ログインが必要になります
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * トークンの署名に使用する秘密鍵
     * この鍵を使用してトークンの改ざんを防止します
     * TODO 重要: 本番環境では、環境変数や設定ファイルで管理する必要があります
     */
    private static final String SECRET_KEY = "my-super-secret-jwt-key-that-is-very-secure";

    /**
     * 秘密鍵をHMAC-SHA256アルゴリズムで暗号化したKeyオブジェクト
     * JWTの署名生成と検証に使用されます
     * アプリケーション起動時に1回だけ生成され、以降は再利用されます
     */
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 職員番号からJWTトークンを生成します
     * トークンには以下の情報が含まれます：
     * - サブジェクト：職員番号
     * - 発行時刻：現在時刻
     * - 有効期限：現在時刻から24時間後
     * - 署名：HMAC-SHA256アルゴリズムによる署名
     *
     * @param employeeNumber 職員番号
     * @return 生成されたJWTトークン（文字列形式）
     */
    public String generateToken(String employeeNumber) {
        return Jwts.builder()
                .setSubject(employeeNumber)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWTトークンから職員番号を抽出します
     * トークンの署名を検証し、有効な場合のみ職員番号を返します
     * 署名が無効な場合はJwtExceptionがスローされます
     *
     * @param token 検証するJWTトークン
     * @return トークンから抽出した職員番号
     * @throws JwtException トークンが無効な場合
     */
    public String extractEmployeeNumber(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWTトークンの有効性を検証します
     * 以下の条件をすべて満たす場合にtrueを返します：
     * - トークンの署名が正しい
     * - トークンの有効期限が切れていない
     * - トークンの形式が正しい
     *
     * @param token 検証するJWTトークン
     * @return トークンが有効な場合はtrue、無効な場合はfalse
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            // トークンの検証に失敗した場合（署名が不正、有効期限切れなど）
            return false;
        }
    }
}