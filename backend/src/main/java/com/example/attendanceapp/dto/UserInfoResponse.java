package com.example.attendanceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ユーザー情報のレスポンスDTOクラス
 * システムのユーザー情報を保持し、クライアントに返すためのデータ構造を定義します
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドを自動生成
@AllArgsConstructor  // 全フィールドを引数に持つコンストラクタを自動生成
public class UserInfoResponse {
    /**
     * ユーザーの一意識別子
     */
    private Long id;

    /**
     * 従業員番号 - ユーザーの社内での識別に使用
     */
    private String employeeNumber;

    /**
     * ユーザーの氏名
     */
    private String name;
   
    /**
     * ユーザーの役割（権限レベル）
     */
    private String role;

    /**
     * ユーザーの所属部門
     */
    private String department;
}