package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ユーザー情報を表すエンティティクラス。
 * システムにおけるユーザーの基本情報、認証情報、所属情報などを管理します。
 *
 * @Entity - JPAエンティティとしてこのクラスを指定します
 * @Table - データベースのテーブル名を'users'と指定します
 * @Data - Lombokによりゲッター、セッター、equals、hashCode、toStringを自動生成します
 * @Builder - ビルダーパターンを使用したオブジェクト生成を可能にします
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * ユーザーの一意識別子。
     * 自動採番される主キーとして使用されます。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 従業員番号。
     * システム内で一意に識別するために使用される業務上のID。
     * 例: "EMP001"
     */
    @Column(name = "employee_number", nullable = false, unique = true)
    private String employeeNumber;

    /**
     * ユーザーの氏名。
     * 従業員のフルネームを保持します。
     */
    @Column(nullable = false)
    private String name;

    /**
     * パスワードのハッシュ値。
     * セキュリティのため、平文ではなくハッシュ化されたパスワードを保存します。
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * ユーザーの役割（権限）。
     * Roleエンティティへの参照を保持します。
     * FetchType.LAZYにより、必要時のみデータを取得します。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * ユーザーの所属部署。
     * Departmentエンティティへの参照を保持します。
     * FetchType.LAZYにより、必要時のみデータを取得します。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * アカウントの有効状態。
     * true: アカウントが有効で利用可能
     * false: アカウントが無効化されている
     * デフォルトはtrue（有効）です。
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * ユーザーの退職日時。
     * 退職していない場合はnullです。
     */
    @Column(name = "retired_at")
    private LocalDateTime retiredAt;

    /**
     * レコードの作成日時。
     * エンティティが作成された時点で自動的に現在時刻が設定されます。
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * レコードの最終更新日時。
     * エンティティが更新されるたびに現在時刻に更新する必要があります。
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}