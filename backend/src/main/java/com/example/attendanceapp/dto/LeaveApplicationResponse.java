package com.example.attendanceapp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 休暇申請のレスポンスDTOクラス
 * このクラスは、休暇申請に関する情報をクライアントに返却するためのデータ構造を定義しています。
 * 各フィールドは休暇申請の詳細（期間、時刻、種類、理由、承認状況など）を表します。
 */
@Data
public class LeaveApplicationResponse {

    /**
     * 休暇申請の一意識別子
     * 各休暇申請を識別するためのユニークなIDです。
     */
    private Long id;

    /**
     * 休暇の開始日付
     * 休暇が開始される日付を保持します。
     */
    private LocalDate startDate;

    /**
     * 休暇の終了日付
     * 休暇が終了する日付を保持します。
     */
    private LocalDate endDate;

    /**
     * 休暇の開始時刻
     * 部分休暇の際に、具体的な休暇開始の時刻を記録します。
     */
    private LocalTime startTime;

    /**
     * 休暇の終了時刻
     * 部分休暇の際に、具体的な休暇終了の時刻を記録します。
     */
    private LocalTime endTime;

    /**
     * 部分休暇の種類
     * 例：午前半休、午後半休など、1日の一部のみ休暇を取得する場合に指定されます。
     */
    private String partDayType;

    /**
     * 休暇の種類
     * 例：年次有給休暇、病気休暇など、休暇の種類を示します。
     */
    private String type;

    /**
     * 休暇申請の理由
     * 休暇申請の正当性や背景を説明するためのテキスト情報です。
     */
    private String reason;

    /**
     * 休暇申請の状況
     * 申請中、承認済み、却下など、現在の休暇申請の状態を表します。
     */
    private String status;

    /**
     * 承認者からのコメント
     * 承認プロセスにおいて、承認者が残したコメント（理由や指摘事項など）を保持します。
     */
    private String approverComment;

    /**
     * 休暇申請が承認された日時
     * 申請が承認されたタイミングの日時を記録します。
     */
    private LocalDateTime approvedAt;

    /**
     * 休暇の期間（分単位）
     * 申請された休暇の合計時間を分単位で示します。
     */
    private Integer durationMinutes;

    /**
     * 休暇申請が作成された日時
     * システム上で休暇申請が登録されたタイミングの日時を保持します。
     */
    private LocalDateTime createdAt;
}