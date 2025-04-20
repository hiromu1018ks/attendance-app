package com.example.attendanceapp.controller;

import com.example.attendanceapp.dto.UserInfoResponse;
import com.example.attendanceapp.model.User;
import com.example.attendanceapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ユーザー関連のAPI エンドポイントを提供するコントローラークラス。
 * ユーザー情報の取得や管理に関する機能を提供します。
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    /**
     * ユーザー情報へのデータベースアクセスを提供するリポジトリ。
     * コンストラクタインジェクションによって自動的に注入されます。
     */
    private final UserRepository userRepository;

    /**
     * 現在ログインしているユーザーの情報を取得するエンドポイント。
     *
     * @param authentication Spring Securityが提供する認証情報
     * @return ログインユーザーの基本情報を含むレスポンス
     * @throws RuntimeException ログインユーザーが見つからない場合
     */
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMe(Authentication authentication) {
        // 認証情報から従業員番号を取得
        String employeeNumber = authentication.getName();

        // 従業員番号を使用してユーザー情報をデータベースから検索
        // 見つからない場合は例外をスロー
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("ログインユーザーが見つかりません"));

        // データベースから取得したユーザー情報をレスポンス用DTOに変換
        UserInfoResponse response = new UserInfoResponse(
                user.getId(),                    // ユーザーID
                user.getEmployeeNumber(),        // 従業員番号
                user.getName(),                  // ユーザー名
                user.getRole().getName(),        // 役割名
                user.getDepartment().getName()   // 部署名
        );

        // 正常なレスポンスとしてユーザー情報を返却
        return ResponseEntity.ok(response);
    }
}