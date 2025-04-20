package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 勤怠修正申請エンティティ
 * 従業員が勤怠記録の修正を申請し、承認者が承認・却下を行うための情報を管理します。
 * <p>
 * このクラスは以下の主要な情報を保持します：
 * - 修正対象の勤怠記録への参照
 * - 申請者と承認者の情報
 * - 修正前後の時刻
 * - 申請の状態管理（申請中/承認済/却下）
 */
@Entity
@Table(name = "correction_requests")
@Data  // Lombokによるゲッター、セッター、equals、hashCode、toStringの自動生成
@NoArgsConstructor  // パラメータなしコンストラクタを生成
@AllArgsConstructor // 全フィールドを引数に持つコンストラクタを生成
@Builder // ビルダーパターンによるインスタンス生成をサポート
public class CorrectionRequest {

    /**
     * 修正申請ID - データベース内で自動生成される一意の識別子
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 修正対象の勤怠記録
     * - FetchType.LAZYにより、必要時のみデータを取得
     * - attendance_idカラムを外部キーとして使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_id")
    private AttendanceRecord attendanceRecord;

    /**
     * 申請者情報
     * - 修正を申請した従業員の参照
     * - user_idカラムを外部キーとして使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 修正前の時刻
     * - 現在の打刻記録の時刻
     * - データ整合性のため必須フィールド
     */
    @Column(name = "original_time", nullable = false)
    private LocalDateTime originalTime;

    /**
     * 修正後の時刻
     * - 従業員が希望する修正後の時刻
     * - データ整合性のため必須フィールド
     */
    @Column(name = "corrected_time", nullable = false)
    private LocalDateTime correctedTime;

    /**
     * 修正種別 - 修正対象の打刻種類を指定
     * データ整合性のため必須フィールド
     */
    @Column(nullable = false)
    private String type;

    /**
     * 修正理由 - 申請者による説明
     * データ整合性のため必須フィールド
     */
    @Column(nullable = false)
    private String reason;

    /**
     * 申請のステータス
     * デフォルトは"申請中"で、承認処理により変更される
     */
    @Column(nullable = false)
    private String status = "申請中";

    /**
     * 承認者情報
     * - 申請を処理する管理者への参照
     * - approver_idカラムを外部キーとして使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    /**
     * 承認処理日時
     * 申請が承認または却下された時点で設定
     */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * 承認者コメント
     * 承認または却下の理由を記録
     */
    @Column(name = "approver_comment")
    private String approverComment;

    /**
     * 申請作成日時
     * インスタンス生成時に自動的に現在時刻が設定される
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}