package com.example.attendanceapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Spring Frameworkの設定クラスであることを示すアノテーション。
// このクラスはアプリケーション全体のCORS設定を担当します。
@Configuration
public class CorsConfig implements WebMvcConfigurer { // Spring MVCの設定をカスタマイズするためにWebMvcConfigurerインターフェースを実装

    /**
     * このメソッドはCORS（クロスオリジンリソースシェアリング）のマッピングを設定するためにオーバーライドされています。
     * CorsRegistryを使用して、どのURLパターンに対してどのオリジンやメソッドが許可されるかを定義します。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // "/api/**" はCORS設定が適用されるパスパターンを示しています。
        // ここでは/api以下のすべてのエンドポイントに対して設定が有効です。
        registry.addMapping("/api/**")
                // 許可するオリジン（アクセス元）の指定。
                // 例：ローカル開発環境で使用される http://localhost:5173 を許可。
                .allowedOrigins("http://localhost:5173")
                // 許可するHTTPメソッドの指定。
                // GET、POST、PUT、DELETE、およびOPTIONSメソッドのリクエストを許可します。
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 許可するHTTPヘッダーの指定。
                // "*"を指定することで、任意のヘッダーを受け入れます。
                .allowedHeaders("*")
                // クレデンシャル情報（クッキー、認証ヘッダーなど）をリクエストに含めることを許可する設定。
                // これにより、セキュリティが必要なリクエストでも資格情報が送信されます。
                .allowCredentials(true);
    }
}