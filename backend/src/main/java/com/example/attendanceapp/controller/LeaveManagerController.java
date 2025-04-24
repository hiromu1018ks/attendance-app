package com.example.attendanceapp.controller;

import com.example.attendanceapp.dto.ApprovalRequest;
import com.example.attendanceapp.dto.LeaveApplicationResponse;
import com.example.attendanceapp.repository.UserRepository;
import com.example.attendanceapp.service.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/leaves")
@RequiredArgsConstructor
@PreAuthorize("hasRole('課長')")
public class LeaveManagerController {

    private final LeaveApplicationService leaveApplicationService;
    private final UserRepository userRepository;

    @GetMapping
    public List<LeaveApplicationResponse> getPending(Authentication authentication) {
        String managerNumber = authentication.getName();
        return leaveApplicationService.getPendingApprovals(managerNumber);
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
