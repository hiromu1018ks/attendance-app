package com.example.attendanceapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 従業員の役職を表すエンティティクラス。
 * システム内での権限管理や組織階層の管理に使用される。
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    /**
     * 役職の一意識別子
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自動採番
    private Long id;

    /**
     * 役職名
     * 一意制約があり、必須項目
     * 例: 一般職員, 課長, 人事課
     */
    @Column(nullable = false, unique = true)
    private String name;
}