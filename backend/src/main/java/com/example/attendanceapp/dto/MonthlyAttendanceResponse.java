package com.example.attendanceapp.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 月次勤怠情報のレスポンスDTOクラス
 * 従業員の月次勤怠情報を保持し、クライアントに返すためのデータ構造を定義します
 */
public record MonthlyAttendanceResponse(
        /*
          勤務日
          この勤怠記録が属する日付
         */
        LocalDate date,

        /*
          出勤時刻
          従業員が出勤を記録した日時
         */
        LocalDateTime clockIn,

        /*
          退勤時刻
          従業員が退勤を記録した日時
         */
        LocalDateTime clockOut,

        /*
          実労働時間
          出勤時刻から退勤時刻までの実際の勤務時間
         */
        Duration workingHours,

        /*
          給与レート区分リスト
          勤務時間帯ごとの給与レート情報（深夜勤務手当などを含む）
         */
        List<RateSegment> rateSegments,

        /*
          勤怠修正フラグ
          true: 勤怠データが修正されている
          false: 勤怠データが修正されていない（オリジナルのまま）
         */
        boolean isCorrected
) {
    /**
     * 給与レート区分を表す内部レコードクラス
     */
    public record RateSegment(
            /*
              給与レート
              時給に対する倍率（例：深夜勤務1.25倍など）
             */
            BigDecimal rate,

            /*
              適用時間
              このレートが適用される時間の長さ
             */
            Duration duration,

            /*
              レート区分の理由ラベル
              レートが適用される理由（例：「深夜勤務」「休日勤務」など）
             */
            String reasonLabel
    ) {
    }
}