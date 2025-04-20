package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 勤怠記録を管理するエンティティクラス。
 * 職員の出退勤時刻、勤務日、修正情報などを保持します。
 * <p>
 * このクラスは以下の主要な機能を提供します：
 * - 日次の勤怠データの記録
 * - 出退勤時刻の管理
 * - 勤務時間の補正情報の保持
 * - 打刻修正履歴の追跡
 */
@Entity
@Table(name = "attendance_records")
@Data               // Lombokによるゲッター、セッター、equals、hashCode、toStringの自動生成
@NoArgsConstructor  // 引数なしコンストラクタの自動生成
@AllArgsConstructor // 全フィールドを引数に持つコンストラクタの自動生成
@Builder           // ビルダーパターンの実装を自動生成
public class AttendanceRecord {

    /**
     * レコードの一意識別子
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 勤怠記録の対象となる職員
     * - 遅延ロードを使用し、必要な時のみUserエンティティを取得
     * - データベースの参照整合性を保持
     * - user_idカラムを外部キーとして使用
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 勤務日
     * この日付は必須項目です
     */
    @Column(nullable = false)
    private LocalDate date;

    /**
     * 出勤時刻
     * 打刻時に記録される実際の出勤時刻です
     */
    @Column(name = "clock_in")
    private LocalDateTime clockIn;

    /**
     * 退勤時刻
     * 打刻時に記録される実際の退勤時刻です
     */
    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    /**
     * 打刻修正フラグ
     * true: 打刻修正申請により修正された記録
     * false: 通常の打刻記録
     */
    @Column(name = "is_corrected", nullable = false)
    private Boolean isCorrected = false;

    /**
     * 勤務時間の補正率
     * 時間外勤務や休日勤務などの割増率を示します
     * - 通常勤務: 1.00
     * - 時間外勤務: 1.25（25%増）
     * - 深夜勤務: 1.35（35%増）
     * - 休日勤務: 1.35（35%増）
     */
    @Column(name = "correction_rate")
    private BigDecimal correctionRate;

    /**
     * レコードの作成日時
     * - システムによって自動的に設定
     * - レコードの監査証跡として使用
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}