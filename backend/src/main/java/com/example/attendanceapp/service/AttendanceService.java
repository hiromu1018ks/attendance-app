package com.example.attendanceapp.service;

import com.example.attendanceapp.dto.MonthlyAttendanceResponse;
import com.example.attendanceapp.model.AttendanceRecord;
import com.example.attendanceapp.model.User;
import com.example.attendanceapp.repository.AttendanceRecordRepository;
import com.example.attendanceapp.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 勤怠管理サービスクラス
 * 従業員の勤怠記録の取得や処理を行うビジネスロジックを提供します
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {
    /**
     * 勤怠記録へのデータアクセスを提供するリポジトリ
     */
    private final AttendanceRecordRepository attendanceRecordRepository;

    /**
     * ユーザー認証・認可に関連する処理を提供するサービス
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * 指定された年月の月次勤怠記録を取得し、給与計算に必要な情報を含めて返却します
     *
     * @param user  対象ユーザー
     * @param year  対象年
     * @param month 対象月
     * @return 日々の勤怠情報と給与計算用の補正情報のリスト
     */
    public List<MonthlyAttendanceResponse> getMonthlyRecords(User user, int year, int month) {
        // 対象月の開始日と終了日を設定
        LocalDate from = LocalDate.of(year, month, 1);
        LocalDate to = from.withDayOfMonth(from.lengthOfMonth());

        // データベースから勤怠記録を取得
        List<AttendanceRecord> records = attendanceRecordRepository.findByUserAndDateBetween(user, from, to);

        // 各勤怠記録を給与計算用の情報を含むDTOに変換
        return records.stream()
                .map(record -> {
                    LocalDateTime in = record.getClockIn();
                    LocalDateTime out = record.getClockOut();

                    // 実労働時間を計算（打刻がない場合は0として扱う）
                    Duration working = (in != null && out != null) ? Duration.between(in, out) : Duration.ZERO;

                    // 給与レート区分のリストを初期化
                    List<MonthlyAttendanceResponse.RateSegment> segments = new ArrayList<>();

                    // 出退勤の打刻が揃っている場合のみ給与レート計算を実施
                    if (in != null && out != null) {
                        LocalDateTime cursor = in;
                        // 15分単位で勤務時間帯を区切って給与レートを判定
                        while (cursor.isBefore(out)) {
                            LocalDateTime next = cursor.plusMinutes(15);
                            if (next.isAfter(out)) next = out;

                            BigDecimal rate;
                            String label;
                            int hour = cursor.getHour();

                            // 深夜時間帯（22:00～翌5:00）の判定
                            if (hour >= 22 || hour < 5) {
                                rate = new BigDecimal("1.50");  // 深夜手当50%増
                                label = "深夜";
                            }
                            // 土日の判定
                            else if (cursor.getDayOfWeek() == DayOfWeek.SATURDAY ||
                                    cursor.getDayOfWeek() == DayOfWeek.SUNDAY) {
                                rate = new BigDecimal("1.35");  // 休日手当35%増
                                label = "土日祝";
                            }
                            // 平日の所定時間外の判定
                            else {
                                rate = new BigDecimal("1.25");  // 時間外手当25%増
                                label = "平日所定外";
                            }

                            // 区間の実時間を計算
                            Duration segmentDuration = Duration.between(cursor, next);
                            segments.add(new MonthlyAttendanceResponse.RateSegment(rate, segmentDuration, label));
                            cursor = next;
                        }

                        // 同一レートの区間をまとめて集計
                        segments = segments.stream()
                                .collect(Collectors.groupingBy(MonthlyAttendanceResponse.RateSegment::rate))
                                .entrySet().stream()
                                .map(e -> {
                                    // 同一レートの時間を合算
                                    Duration total = e.getValue().stream()
                                            .map(MonthlyAttendanceResponse.RateSegment::duration)
                                            .reduce(Duration.ZERO, Duration::plus);
                                    String label = e.getValue().get(0).reasonLabel();
                                    return new MonthlyAttendanceResponse.RateSegment(e.getKey(), total, label);
                                })
                                .toList();
                    }

                    // レスポンスDTOを構築して返却
                    return new MonthlyAttendanceResponse(
                            record.getDate(),
                            in,
                            out,
                            working,
                            segments,
                            record.getIsCorrected()
                    );
                }).toList();
    }
}