package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 勤怠記録を管理するエンティティクラス。
 * 職員の出退勤時刻、勤務日、修正情報などを保持します。
 */
@Entity
@Table(name = "attendance_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecord {
    /** レコードの一意識別子 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 勤怠記録の対象となる職員
     * 遅延ロードを使用し、必要な時のみUserエンティティを取得します
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
     * 例: 1.25（25%増）、1.35（35%増）など
     */
    @Column(name = "correction_rate")
    private BigDecimal correctionRate;

    /**
     * レコードの作成日時
     * レコード作成時に自動的に現在時刻が設定されます
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}