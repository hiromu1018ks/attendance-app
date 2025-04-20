package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 休暇申請を表すエンティティクラス。
 * 社員の休暇申請情報を管理し、申請から承認までのワークフローを追跡します。
 * このクラスは休暇管理システムの中核として機能し、以下の情報を管理します：
 * - 申請者と承認者の情報
 * - 休暇の期間と種別
 * - 申請状態と承認情報
 */
@Entity
@Table(name = "leave_applications")
@Data           // Lombokによるゲッター、セッター、equals、hashCode、toStringの自動生成
@NoArgsConstructor  // 引数なしコンストラクタの自動生成
@AllArgsConstructor // 全フィールドを引数に持つコンストラクタの自動生成
@Builder           // ビルダーパターンの実装を自動生成
public class LeaveApplication {
    /**
     * 休暇申請を一意に識別するID
     * 自動採番される主キー
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 休暇を申請した社員
     * 多対1の関係で、パフォーマンス最適化のため遅延ロードを使用
     * 必要時のみユーザー情報をロード
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 休暇の種別
     * システムで管理される休暇の種類を指定
     * 例: 年次有給休暇、代休、特別休暇など
     */
    @Column(nullable = false)
    private String type;

    /**
     * 休暇の開始日
     * 日付のみを保持し、時刻は含まない
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * 休暇の終了日
     * 開始日と同じ、もしくはそれ以降の日付
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * 休暇取得の時間帯区分
     * 午前のみ、午後のみ、または終日の指定が可能
     * 半日休暇などの管理に使用
     */
    @Column(name = "part_day_type")
    private String partDayType;

    /**
     * 休暇申請の理由
     * 任意入力項目として、申請者が休暇の理由を記載
     */
    @Column
    private String reason;

    /**
     * 申請のステータス
     * 申請のライフサイクルを管理
     * デフォルト: "申請中"
     * 遷移: "承認済" または "却下" に変更される
     */
    @Column(nullable = false)
    private String status = "申請中";

    /**
     * 申請を承認/却下した承認者
     * 多対1の関係で、パフォーマンス最適化のため遅延ロード
     * 承認プロセス完了時に設定
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    /**
     * 申請が承認または却下された日時
     * 承認/却下時に自動的に設定
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 承認者からのコメント
     * 承認または却下の理由や条件を記録
     */
    @Column(name = "approver_comment")
    private String approverComment;

    /**
     * 申請が作成された日時
     * レコード作成時に自動的に現在時刻が設定される
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}