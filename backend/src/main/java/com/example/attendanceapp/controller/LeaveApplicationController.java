package com.example.attendanceapp.controller;

import com.example.attendanceapp.dto.ApprovalRequest;
import com.example.attendanceapp.dto.LeaveApplicationRequest;
import com.example.attendanceapp.dto.LeaveApplicationResponse;
import com.example.attendanceapp.dto.LeaveBalanceResponse;
import com.example.attendanceapp.service.LeaveApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    // このエンドポイントはHTTP GETリクエストを処理し、現在認証済みのユーザー自身の休暇申請一覧を取得するために使用されます。
    public List<LeaveApplicationResponse> getOwnApplications(Authentication authentication) {
        // 認証情報からユーザー名を取得します。ここではユーザー名が従業員番号として扱われます。
        String employeeNumber = authentication.getName();
        // 取得した従業員番号を用いて、休暇申請サービスから該当ユーザーの休暇申請情報を取得し、返却します。
        return leaveApplicationService.getOwnApplications(employeeNumber);
    }

    @GetMapping("/balance")
    // このエンドポイントは、HTTP GETリクエストにより休暇残高の情報を取得するために使用されます。
    public LeaveBalanceResponse getBalance(Authentication authentication) {
        // 認証情報から、現在ログインしているユーザーの識別子（従業員番号）を取得します。
        String employeeNumber = authentication.getName();
        // 取得した従業員番号を使用して、休暇申請サービスからそのユーザーの休暇残高情報を取得し、
        // 結果としてLeaveBalanceResponseオブジェクトを返却します。
        return leaveApplicationService.getLeaveBalance(employeeNumber);
    }

    @PostMapping("/{id}/approve")
    // このエンドポイントは、休暇申請を承認するために使用されます。
    // クライアントは申請IDと、承認時のコメントを含むリクエストボディを送信します。
    public void approve(@PathVariable Long id,
                        // 承認時のコメントなどの情報を含むリクエストボディを取得します。
                        @RequestBody ApprovalRequest request,
                        // 現在認証されているユーザーの認証情報を受け取ります。
                        Authentication authentication) {
        // 認証情報から承認者の識別番号（ここではユーザー名＝社員番号として扱う）を取得します。
        String approverNumber = authentication.getName();
        // LeaveApplicationServiceのapproveLeaveメソッドを呼び出し、指定された申請ID、
        // 承認者の識別番号、及びリクエスト内のコメントを渡すことで、休暇申請の承認処理を実行します。
        leaveApplicationService.approveLeave(id, approverNumber, request.getComment());
    }

    @PostMapping("/{id}/reject")
    // このエンドポイントは、休暇申請を却下するために使用されます。
    // クライアントは申請IDと、却下理由などのコメントを含むリクエストボディを送信します。
    public void reject(@PathVariable Long id,
                       // 却下時のコメントなどの情報を含むリクエストボディを受け取ります。
                       @RequestBody ApprovalRequest request,
                       // 現在認証されているユーザーの認証情報を引数として受け取ります。
                       Authentication authentication) {
        // 認証情報から却下処理を行うユーザーの識別番号（ユーザー名＝社員番号）を取得します。
        String approverNumber = authentication.getName();
        // LeaveApplicationServiceのrejectLeaveメソッドを呼び出し、指定された申請ID、
        // 承認者の識別番号、及びリクエスト内のコメントを渡すことで、休暇申請の却下処理を実行します。
        leaveApplicationService.rejectLeave(id, approverNumber, request.getComment());
    }
}