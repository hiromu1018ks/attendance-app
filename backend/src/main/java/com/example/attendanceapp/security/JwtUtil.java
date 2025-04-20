package com.example.attendanceapp.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    /**
     * JWTトークンの有効期限（ミリ秒）
     * 1000ミリ秒 × 60秒 × 60分 × 24時間 = 1日
     */
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    /**
     * トークンの署名に使用する秘密鍵
     * TODO 注意: 本番環境では、より長い鍵を使用し、環境変数や設定ファイルで管理すること
     */
    private static final String SECRET_KEY = "my-super-secret-jwt-key-that-is-very-secure";

    /**
     * 秘密鍵をHMAC-SHA256アルゴリズムで暗号化したKey オブジェクト
     * このKeyオブジェクトを使用してトークンの署名と検証を行う
     */
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 指定された職員番号をもとにJWTトークンを生成する
     *
     * @param employeeNumber 職員番号
     * @return 生成されたJWTトークン
     */
    public String generateToken(String employeeNumber) {
        return Jwts.builder()
                // トークンのサブジェクトとして職員番号を設定
                .setSubject(employeeNumber)
                // トークンの発行時刻を現在時刻で設定
                .setIssuedAt(new Date())
                // トークンの有効期限を現在時刻+EXPIRATION_TIMEで設定
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // HMAC-SHA256アルゴリズムと秘密鍵で署名
                .signWith(key, SignatureAlgorithm.HS256)
                // トークンを文字列形式に変換
                .compact();
    }

    /**
     * JWTトークンから職員番号を抽出する
     *
     * @param token JWTトークン
     * @return トークンに含まれる職員番号
     */
    public String extractEmployeeNumber(String token) {
        return Jwts.parserBuilder()
                // 署名の検証に使用する秘密鍵を設定
                .setSigningKey(key)
                .build()
                // トークンを解析してクレーム（トークンに含まれる情報）を取得
                .parseClaimsJwt(token)
                .getBody()
                // クレームからサブジェクト（職員番号）を取得
                .getSubject();
    }

    /**
     * JWTトークンの有効性を検証する
     * トークンの署名が正しく、有効期限が切れていない場合にtrueを返す
     *
     * @param token 検証するJWTトークン
     * @return トークンが有効な場合はtrue、無効な場合はfalse
     */
    public boolean validateToken(String token) {
        try {
            // トークンの解析を試みる
            // 署名が不正な場合や有効期限切れの場合は例外が発生する
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJwt(token);
            return true;
        } catch (JwtException e) {
            // トークンの検証に失敗した場合
            return false;
        }
    }
}