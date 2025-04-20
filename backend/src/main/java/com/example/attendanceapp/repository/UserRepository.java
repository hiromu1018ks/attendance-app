package com.example.attendanceapp.repository;

import com.example.attendanceapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * ユーザー情報のデータベースアクセスを担当するリポジトリインターフェース。
 * Spring Data JPAを使用して、基本的なCRUD操作と独自のクエリメソッドを提供します。
 * <p>
 * JpaRepositoryを継承することで、以下の標準的なデータベース操作が自動的に利用可能になります：
 * - findAll(): 全てのユーザーを取得
 * - findById(): IDによるユーザーの取得
 * - save(): ユーザーの保存/更新
 * - delete(): ユーザーの削除
 * など
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 従業員番号を使用してユーザーを検索するメソッド。
     *
     * @param employeeNumber 検索対象の従業員番号
     * @return 該当するユーザーを含むOptional。ユーザーが見つからない場合は空のOptionalを返します。
     * <p>
     * このメソッドは以下のような場合に使用されます：
     * - ログイン時のユーザー認証
     * - 従業員番号による検索
     * - ユーザー情報の重複チェック
     */
    Optional<User> findByEmployeeNumber(String employeeNumber);
}