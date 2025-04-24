package com.example.attendanceapp.repository;

import com.example.attendanceapp.model.LeaveApplication;
import com.example.attendanceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * LeaveApplicationRepositoryインターフェースは、休暇申請エンティティ（LeaveApplication）のデータベース操作を定義します。
 * このインターフェースはSpring Data JPAが提供するJpaRepositoryを継承しており、基本的なCRUD処理を利用可能です。
 */
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    /**
     * 指定されたユーザーと対象の年に対して、承認済みの休暇申請の合計休暇時間（分単位）を計算するメソッドです。
     * <p>
     * クエリの説明:
     * - SELECT SUM(l.durationMinutes): 各休暇申請の持続時間（分）の合計値を計算します。
     * - FROM LeaveApplication l: LeaveApplicationエンティティからデータを取得します。
     * - WHERE l.user = :user: 指定されたユーザーの申請に絞り込みます。
     * - AND l.status = 'APPROVED': 申請状態が「APPROVED」であるレコードに限定します。
     * - AND YEAR(l.startDate) = :year: 休暇の開始日が指定された年に該当する申請に限定します。
     * <p>
     * パラメータ:
     * - @Param("user") User user: 休暇申請を検索する対象のユーザー。
     * - @Param("year") int year: 検索対象となる年（startDateの年）。
     * <p>
     * 戻り値:
     * - Integer: 条件に一致する申請の合計休暇時間（分単位）。条件に合致する申請が存在しない場合はnullとなる可能性があります。
     */
    @Query("""
            SELECT SUM(l.durationMinutes)
            FROM LeaveApplication l
            WHERE l.user = :user
            AND l.status = 'APPROVED'
            AND YEAR(l.startDate) = :year
            """)
    Integer sumUsedMinutesByUserAndYear(@Param("user") User user, @Param("year") int year);

    /**
     * 指定されたユーザーが提出した休暇申請を作成日時の降順で取得します。
     * このメソッドは、パラメータとして渡されたユーザーに紐づく休暇申請のすべてのレコードを、
     * 新しい順（最新の申請が最初）に並び替えて返します。
     *
     * @param user 休暇申請情報を取得したい対象ユーザー。
     *             このパラメータにより、該当ユーザーの申請記録がフィルタリングされます。
     * @return List&lt;LeaveApplication&gt; ユーザーが作成した休暇申請のリスト。
     * このリストは、作成日時(createdAt)に基づいて降順（最新順）に整列されています。
     */
    List<LeaveApplication> findByUserOrderByCreatedAtDesc(User user);

    @Query("""
                SELECT l
                FROM LeaveApplication l
                WHERE l.status = '申請中'
            """)
    List<LeaveApplication> findPendingByManager(@Param("managerNumber") String managerNumber);
}
