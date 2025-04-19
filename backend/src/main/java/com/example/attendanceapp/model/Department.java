package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 部署情報を表すエンティティクラス。
 * データベースの departments テーブルにマッピングされます。
 */
@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    /**
     * 部署のID。
     * 主キーとして自動生成される。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部署名。
     * null不可、一意制約あり。
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * レコードの作成日時。
     * データベースでは created_at カラムとしてマッピング。
     * null不可、デフォルト値として現在時刻が設定される。
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}