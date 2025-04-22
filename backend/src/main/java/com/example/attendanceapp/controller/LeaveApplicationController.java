package com.example.attendanceapp.controller;

import com.example.attendanceapp.dto.LeaveApplicationRequest;
import com.example.attendanceapp.service.LeaveApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // このクラスがREST APIのエンドポイントとして定義されていることを示す
@RequestMapping("/api/leaves") // このコントローラが "/api/leaves" というベースURLでアクセスされるエンドポイントを持つことを定義
@RequiredArgsConstructor // Lombokがfinalフィールドに対してコンストラクタを自動生成し、依存性注入を容易にする
public class LeaveApplicationController {

    // 休暇申請のビジネスロジックを実装するサービスクラスへの依存性を宣言
    private final LeaveApplicationService leaveApplicationService;

    /**
     * HTTP POST要求により休暇申請の処理を実行するメソッド
     *
     * @param request        クライアントから受け取った休暇申請のリクエストデータ
     * @param authentication 現在認証されているユーザーの認証情報
     *                       <p>
     *                       このメソッドはリクエストボディから取得した休暇申請情報をバリデーションし、
     *                       認証情報から従業員番号を抽出して、休暇申請サービスに申請内容と従業員番号を渡すことで処理を委譲します。
     */
    @PostMapping
    public void applyLeave(@RequestBody @Valid LeaveApplicationRequest request, Authentication authentication) {
        // 認証情報から従業員番号を取得。ここではユーザ名を従業員番号として扱う前提です。
        String employeeNumber = authentication.getName();
        // 休暇申請サービスのapplyLeaveメソッドを呼び出して、休暇申請処理を委譲。
        leaveApplicationService.applyLeave(request, employeeNumber);
    }
}