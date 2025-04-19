package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 勤怠修正申請エンティティ
 * 従業員が勤怠記録の修正を申請し、承認者が承認・却下を行うための情報を管理します。
 */
@Entity
@Table(name = "correction_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrectionRequest {

    /**
     * 修正申請ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 修正対象の勤怠記録
     * 修正申請の対象となる勤怠記録への参照
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private AttendanceRecord attendanceRecord;

    /**
     * 申請者
     * 修正を申請した従業員
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 修正前の時刻
     * 修正対象の元の打刻時刻
     */
    @Column(name = "original_time", nullable = false)
    private LocalDateTime originalTime;

    /**
     * 修正後の時刻
     * 修正を希望する時刻
     */
    @Column(name = "corrected_time", nullable = false)
    private LocalDateTime correctedTime;

    /**
     * 修正種別
     * "clockIn": 出勤時刻の修正
     * "clockOut": 退勤時刻の修正
     */
    @Column(nullable = false)
    private String type;

    /**
     * 修正理由
     * 申請者が入力した修正が必要となった理由
     */
    @Column(nullable = false)
    private String reason;

    /**
     * 申請のステータス
     * "申請中": 承認待ち
     * "承認済": 承認された
     * "却下": 却下された
     */
    @Column(nullable = false)
    private String status = "申請中";

    /**
     * 承認者
     * 申請を承認または却下した管理者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    /**
     * 承認または却下された日時
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 承認者のコメント
     * 承認または却下の理由や備考
     */
    @Column(name = "approver_comment")
    private String approverComment;

    /**
     * 申請が作成された日時
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}