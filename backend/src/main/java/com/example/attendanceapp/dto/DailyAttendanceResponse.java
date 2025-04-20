package com.example.attendanceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 日次勤怠情報のレスポンスDTOクラス
 * 1日の出退勤時刻情報を保持し、クライアントに返すためのデータ構造を定義します
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
@AllArgsConstructor  // 全フィールドを引数に持つコンストラクタを自動生成
public class DailyAttendanceResponse {
    /**
     * 出勤時刻
     * 従業員が出勤を記録した日時を保持します
     */
    private LocalDateTime clockIn;

    /**
     * 退勤時刻
     * 従業員が退勤を記録した日時を保持します
     */
    private LocalDateTime clockOut;
}