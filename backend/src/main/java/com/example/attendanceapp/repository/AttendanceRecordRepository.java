package com.example.attendanceapp.repository;

import com.example.attendanceapp.model.AttendanceRecord;
import com.example.attendanceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 勤怠記録のデータアクセスを管理するリポジトリインターフェース。
 * Spring Data JPAを使用してデータベース操作を簡素化します。
 * <p>
 * JpaRepository<AttendanceRecord, Long>を継承することで：
 * - 基本的なCRUD操作（作成、読み取り、更新、削除）
 * - ページネーション
 * - ソート機能
 * が自動的に提供されます。
 */
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    /**
     * 特定のユーザーと日付に対応する勤怠記録を検索します。
     *
     * @param user 検索対象の職員（必須）
     * @param date 検索対象の日付（必須）
     * @return 該当する勤怠記録をOptionalでラップして返します。
     * - 記録が存在する場合：Optional内に該当のAttendanceRecordが格納されます
     * - 記録が存在しない場合：空のOptionalが返されます
     * <p>
     * このメソッドは命名規則に基づいて自動的にクエリが生成されます：
     * - findBy: 検索操作を示す接頭辞
     * - UserAndDate: 検索条件となるエンティティのフィールド名
     */
    Optional<AttendanceRecord> findByUserAndDate(User user, LocalDate date);

    /**
     * 指定された期間内の特定ユーザーの全ての勤怠記録を検索します。
     *
     * @param user 検索対象の職員（必須）- 勤怠記録の所有者
     * @param from 検索期間の開始日（必須）- この日付を含む
     * @param to   検索期間の終了日（必須）- この日付を含む
     * @return 条件に一致する勤怠記録のリスト
     * - 該当する記録が存在しない場合は空のリストを返します
     * - 記録は日付順で返されます
     * <p>
     * このメソッドは命名規則に基づいて自動的にクエリが生成されます：
     * - findBy: 検索操作を示す接頭辞
     * - User: 検索条件の職員
     * - And: 条件の結合
     * - DateBetween: 日付の範囲検索を示す（開始日と終了日を含む）
     * <p>
     * 使用例：
     * - 月次の勤怠記録の取得
     * - 期間指定での勤務実績の集計
     * - 特定期間の勤怠状況の分析
     */
    List<AttendanceRecord> findByUserAndDateBetween(User user, LocalDate from, LocalDate to);
}