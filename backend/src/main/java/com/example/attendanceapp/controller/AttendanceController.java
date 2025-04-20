package com.example.attendanceapp.controller;

import com.example.attendanceapp.dto.DailyAttendanceResponse;
import com.example.attendanceapp.model.AttendanceRecord;
import com.example.attendanceapp.model.User;
import com.example.attendanceapp.repository.AttendanceRecordRepository;
import com.example.attendanceapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 勤怠管理に関するAPI エンドポイントを提供するコントローラクラス。
 * 出勤・退勤の打刻処理を担当します。
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    /**
     * ユーザー情報へのアクセスを提供するリポジトリ
     * 従業員番号によるユーザー検索に使用します
     */
    private final UserRepository userRepository;

    /**
     * 勤怠記録へのアクセスを提供するリポジトリ
     * 出退勤時刻の登録や検索に使用します
     */
    private final AttendanceRecordRepository attendanceRecordRepository;

    /**
     * 出勤打刻を処理するエンドポイント
     *
     * @param authentication 認証情報。ログインユーザーの従業員番号を取得するために使用
     * @return 処理結果を含むResponseEntity
     * <p>
     * 処理の流れ：
     * 1. 認証情報から従業員番号を取得
     * 2. 従業員番号に対応するユーザー情報を検索
     * 3. 当日の勤怠記録を検索または新規作成
     * 4. 既に出勤打刻がある場合はエラーを返す
     * 5. 出勤時刻を記録して保存
     */
    @PostMapping("/clock-in")
    // TODO: トランザクションが必要になる可能性がある（複数操作に備える）
    public ResponseEntity<String> clockIn(Authentication authentication) {
        // TODO: 認証オブジェクトがnullの可能性があるため、防御的なnullチェックを追加する
        // 認証情報から従業員番号を取得
        String employeeNumber = authentication.getName();

        // TODO: RuntimeExceptionではなく、業務用のUserNotFoundExceptionを使う
        // 従業員番号に対応するユーザーを検索
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        // TODO: 非アクティブユーザーや退職者のチェックを追加する必要がある
        // if (!user.getIsActive() || user.getRetiredAt() != null) { ... }

        // TODO: ZoneId定数を共通ユーティリティに抽出する（重複排除）
        // 日本時間での現在日時を取得
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));

        // 当日の勤怠記録を検索、存在しない場合は新規作成
        AttendanceRecord record = attendanceRecordRepository.findByUserAndDate(user, today)
                .orElse(AttendanceRecord.builder()
                        .user(user)
                        .date(today)
                        // TODO: createdAtの自動設定をエンティティまたはリスナー側に移す
                        .createdAt(now)
                        .isCorrected(false)
                        .build());

        // 既に出勤打刻がある場合はエラーを返す
        if (record.getClockIn() != null) {
            return ResponseEntity.badRequest().body("すでに出勤打刻ずみです。");
        }

        // 出勤時刻を記録して保存
        record.setClockIn(now);
        attendanceRecordRepository.save(record);

        return ResponseEntity.ok("出勤打刻しました：" + now);
    }

    /**
     * 退勤打刻を処理するエンドポイント
     * - POST リクエストで "/clock-out" に対して処理を行う
     * - 認証されたユーザーの退勤時刻を記録する
     *
     * @param authentication Spring Securityの認証オブジェクト
     * @return 打刻処理の結果メッセージを含むResponseEntity
     */
    @PostMapping("/clock-out")
    public ResponseEntity<String> clockOut(Authentication authentication) {
        // 認証情報から従業員番号を取得
        String employeeNumber = authentication.getName();

        // 従業員番号からユーザー情報を取得（存在しない場合は例外をスロー）
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        // 日本のタイムゾーンで現在の日付と時刻を取得
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tokyo"));
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Tokyo"));

        // 該当ユーザーの本日の勤怠記録を検索
        AttendanceRecord record = attendanceRecordRepository.findByUserAndDate(user, today)
                .orElse(null);

        // 勤怠記録が存在しないか、出勤打刻がない場合はエラー
        if (record == null || record.getClockIn() == null) {
            return ResponseEntity.badRequest().body("出勤打刻が先に必要です");
        }

        // すでに退勤打刻が存在する場合はエラー
        if (record.getClockOut() != null) {
            return ResponseEntity.badRequest().body("すでに退勤打刻済みです");
        }

        // 退勤時刻を記録
        record.setClockOut(now);
        attendanceRecordRepository.save(record);

        // 正常終了時のレスポンスを返す
        return ResponseEntity.ok("退勤打刻しました：" + now);
    }

    /**
     * 当日の勤怠情報を取得するエンドポイント。
     * 認証されたユーザーの当日の出退勤記録を返します。
     *
     * @param authentication 認証情報（Spring Securityから提供）
     * @return DailyAttendanceResponse 当日の出退勤情報を含むレスポンス
     */
    @GetMapping("/daily")
    public ResponseEntity<DailyAttendanceResponse> getTodayAttendance(Authentication authentication) {
        // 認証情報から従業員番号を取得
        String employeeNumber = authentication.getName();

        // 従業員番号を使用してユーザー情報を取得（存在しない場合は例外をスロー）
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        // 日本のタイムゾーンで現在の日付を取得
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tokyo"));

        // ユーザーと日付で勤怠記録を検索し、結果を返す
        // 記録が存在する場合は出退勤時刻を含むレスポンスを返す
        // 記録が存在しない場合は出退勤時刻がnullのレスポンスを返す
        return attendanceRecordRepository.findByUserAndDate(user, today)
                .map(record -> ResponseEntity.ok(
                        new DailyAttendanceResponse(record.getClockIn(), record.getClockOut())
                ))
                .orElse(ResponseEntity.ok(new DailyAttendanceResponse(null, null)));
    }
}