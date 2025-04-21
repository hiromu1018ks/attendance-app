package com.example.attendanceapp.controller;

import com.example.attendanceapp.dto.DailyAttendanceResponse;
import com.example.attendanceapp.dto.MonthlyAttendanceResponse;
import com.example.attendanceapp.model.AttendanceRecord;
import com.example.attendanceapp.model.User;
import com.example.attendanceapp.repository.AttendanceRecordRepository;
import com.example.attendanceapp.repository.UserRepository;
import com.example.attendanceapp.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * 勤怠管理に関するAPIエンドポイントを提供するコントローラです。
 * このクラスはユーザーの出勤・退勤打刻、日次・月次の勤怠情報取得の処理を行います。
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    // 日本のタイムゾーン（Asia/Tokyo）を定義しています。
    private static final ZoneId JAPAN_ZONE = ZoneId.of("Asia/Tokyo");

    // ユーザー情報を管理するリポジトリ
    private final UserRepository userRepository;

    // 勤怠記録の情報を管理するリポジトリ
    private final AttendanceRecordRepository attendanceRecordRepository;

    // 勤怠の様々な処理ロジックを提供するサービス
    private final AttendanceService attendanceService;

    /**
     * 出勤打刻を処理するエンドポイントです。
     * <p>
     * 処理内容：
     * 1. 認証情報からログイン中のユーザーを取得します。
     * 2. 現在の日付と時刻（日本時間）を取得します。
     * 3. ユーザーの今日の勤怠記録が存在するか確認し、存在しなければ新規に作成します。
     * 4. 既に出勤打刻がある場合はエラーを返します。
     * 5. 出勤打刻時刻を記録し、勤怠記録を保存します。
     * 6. 成功したら打刻時刻を含むメッセージを返します。
     *
     * @param authentication 認証情報
     * @return 出勤打刻の結果を示すResponseEntity
     */
    @PostMapping("/clock-in")
    public ResponseEntity<String> clockIn(Authentication authentication) {
        // ログイン中のユーザー情報を取得
        User user = getAuthenticatedUser(authentication);
        // 現在の日付（日本時間）を取得
        LocalDate today = getCurrentDate();
        // 現在の日時（日本時間）を取得
        LocalDateTime now = getCurrentDateTime();

        // ユーザーと当日で既存の勤怠記録をリポジトリから取得
        AttendanceRecord record = attendanceRecordRepository.findByUserAndDate(user, today)
                .orElse(AttendanceRecord.builder()
                        .user(user)
                        .date(today)
                        .createdAt(now) // TODO: createdAtの自動設定をエンティティまたはリスナー側に移す
                        .isCorrected(false)
                        .build());

        // もしすでに出勤打刻が記録されている場合、400エラーで応答する
        if (record.getClockIn() != null) {
            return ResponseEntity.badRequest().body("すでに出勤打刻済みです。");
        }
        // 出勤打刻の日時を設定し、レコードを保存する
        record.setClockIn(now);
        attendanceRecordRepository.save(record);
        // 正常終了のレスポンスを返す
        return ResponseEntity.ok("出勤打刻しました：" + now);
    }

    /**
     * 退勤打刻を処理するエンドポイントです。
     * <p>
     * 処理内容：
     * 1. 認証情報からログイン中のユーザーを取得します。
     * 2. 現在の日付と時刻（日本時間）を取得します。
     * 3. ユーザーの今日の勤怠記録を取得し、存在しない場合はエラーを返します。
     * 4. まだ出勤打刻が行われていない場合や、既に退勤打刻がある場合はエラーを返します。
     * 5. 退勤打刻時刻を記録し、勤怠記録を保存します。
     * 6. 成功したら打刻時刻を含むメッセージを返します。
     *
     * @param authentication 認証情報
     * @return 退勤打刻の結果を示すResponseEntity
     */
    @PostMapping("/clock-out")
    public ResponseEntity<String> clockOut(Authentication authentication) {
        // ログイン中のユーザー情報を取得
        User user = getAuthenticatedUser(authentication);
        // 現在の日付（日本時間）を取得
        LocalDate today = getCurrentDate();
        // 現在の日時（日本時間）を取得
        LocalDateTime now = getCurrentDateTime();

        // ユーザーと当日で既存の勤怠記録をリポジトリから取得、なければnullになる
        AttendanceRecord record = attendanceRecordRepository.findByUserAndDate(user, today)
                .orElse(null);

        // 出勤打刻がされていなければエラーメッセージを返す
        if (record == null || record.getClockIn() == null) {
            return ResponseEntity.badRequest().body("出勤打刻が先に必要です");
        }
        // 既に退勤打刻がある場合もエラーメッセージを返す
        if (record.getClockOut() != null) {
            return ResponseEntity.badRequest().body("すでに退勤打刻済みです");
        }
        // 退勤打刻の日時を設定し、レコードを保存する
        record.setClockOut(now);
        attendanceRecordRepository.save(record);
        // 正常終了のレスポンスを返す
        return ResponseEntity.ok("退勤打刻しました：" + now);
    }

    /**
     * 今日の勤怠情報を取得するエンドポイントです。
     * <p>
     * 処理内容：
     * 1. 認証情報からログイン中のユーザーを取得します。
     * 2. 現在の日付（日本時間）を取得します。
     * 3. 本日分の勤怠記録が存在する場合は出勤・退勤時刻を含むレスポンスを返し、
     * 存在しない場合は null としてレスポンスを返します。
     *
     * @param authentication 認証情報
     * @return 今日の出勤・退勤時刻情報を含むDailyAttendanceResponse
     */
    @GetMapping("/daily")
    public ResponseEntity<DailyAttendanceResponse> getTodayAttendance(Authentication authentication) {
        // ログイン中のユーザー情報を取得
        User user = getAuthenticatedUser(authentication);
        // 現在の日付（日本時間）を取得
        LocalDate today = getCurrentDate();
        // ユーザーと当日で既存の勤怠記録をリポジトリから取得し、存在する場合は出勤・退勤時刻をレスポンスに設定
        return attendanceRecordRepository.findByUserAndDate(user, today)
                .map(record -> ResponseEntity.ok(
                        new DailyAttendanceResponse(record.getClockIn(), record.getClockOut())))
                .orElse(ResponseEntity.ok(new DailyAttendanceResponse(null, null)));
    }

    /**
     * 月次の勤怠記録を取得するエンドポイントです。
     * <p>
     * 処理内容：
     * 1. 認証情報からログイン中のユーザーを取得します。
     * 2. リクエストパラメータで指定された年と月に基づいて、勤怠サービスから月次の勤怠記録を取得します。
     *
     * @param authentication 認証情報
     * @param year           取得対象の年（例：2023）
     * @param month          取得対象の月（例：4）
     * @return 月次の勤怠記録情報のリスト
     */
    @GetMapping("/monthly")
    public List<MonthlyAttendanceResponse> getMonthlyAttendance(
            Authentication authentication,
            @RequestParam int year,
            @RequestParam int month) {
        // ログイン中のユーザー情報を取得
        User user = getAuthenticatedUser(authentication);
        // 勤怠サービスを利用して指定された年月の勤怠記録を取得して返す
        return attendanceService.getMonthlyRecords(user, year, month);
    }

    /**
     * 認証情報からユーザーを取得する共通処理です。
     * <p>
     * 処理内容：
     * 1. 認証情報から従業員番号を取得します。
     * 2. 従業員番号に基づいてユーザーレコードをリポジトリから検索します。
     * 3. 存在しなければ例外をスローします。
     *
     * @param authentication 認証情報
     * @return 認証されたユーザー情報
     * @throws UsernameNotFoundException ユーザーが見つからない場合に発生
     */
    private User getAuthenticatedUser(Authentication authentication) {
        // 認証情報から従業員番号を取得
        String employeeNumber = authentication.getName();
        // 従業員番号に一致するユーザーをリポジトリから検索し、存在しない場合は例外をスローする
        return userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません"));
    }

    /**
     * 日本時間での現在の日付を取得するメソッドです。
     * <p>
     * 処理内容：
     * 1. 定義されたタイムゾーン（Asia/Tokyo）を使用して、現在の日付を取得します。
     *
     * @return 現在の日付
     */
    private LocalDate getCurrentDate() {
        return LocalDate.now(JAPAN_ZONE);
    }

    /**
     * 日本時間での現在の日時を取得するメソッドです。
     * <p>
     * 処理内容：
     * 1. 定義されたタイムゾーン（Asia/Tokyo）を使用して、現在の日時を取得します。
     *
     * @return 現在の日時
     */
    private LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(JAPAN_ZONE);
    }
}