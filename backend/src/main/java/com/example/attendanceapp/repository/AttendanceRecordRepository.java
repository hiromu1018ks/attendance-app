package com.example.attendanceapp.repository;

import com.example.attendanceapp.model.AttendanceRecord;
import com.example.attendanceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
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
}