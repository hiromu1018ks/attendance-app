package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 休暇申請を表すエンティティクラス。
 * 社員の休暇申請情報を管理し、申請から承認までのワークフローを追跡します。
 */
@Entity
@Table(name = "leave_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveApplication {
    /** 休暇申請を一意に識別するID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 
     * 休暇を申請した社員
     * 多対1の関係で、遅延ロードを使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 休暇の種別
     * 例: 年次有給休暇、代休、特別休暇など
     */
    @Column(nullable = false)
    private String type;

    /** 休暇の開始日 */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** 休暇の終了日 */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * 休暇取得の時間帯区分
     * 午前、午後、または終日を指定可能
     */
    @Column(name = "part_day_type")
    private String partDayType;

    /** 休暇申請の理由（任意） */
    @Column
    private String reason;

    /**
     * 申請のステータス
     * デフォルト値: "申請中"
     * その他の値: "承認済"、"却下"など
     */
    @Column(nullable = false)
    private String status = "申請中";

    /**
     * 申請を承認/却下した承認者
     * 多対1の関係で、遅延ロードを使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    /** 申請が承認または却下された日時 */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /** 承認者からのコメント */
    @Column(name = "approver_comment")
    private String approverComment;

    /** 
     * 申請が作成された日時
     * デフォルト値: 現在時刻
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}