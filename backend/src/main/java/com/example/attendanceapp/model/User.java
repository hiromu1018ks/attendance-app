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
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * ユーザーの一意識別子
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 従業員番号（一意の識別子として使用）
     */
    @Column(name = "employee_number", nullable = false, unique = true)
    private String employeeNumber;

    /**
     * ユーザーの氏名
     */
    @Column(nullable = false)
    private String name;

    /**
     * パスワードのハッシュ値
     */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * ユーザーの役割（権限）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * ユーザーの所属部署
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * アカウントの有効状態
     * true: 有効, false: 無効
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * ユーザーの退職日時
     */
    @Column(name = "retired_at")
    private LocalDateTime retiredAt;

    /**
     * レコードの作成日時
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * レコードの最終更新日時
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}