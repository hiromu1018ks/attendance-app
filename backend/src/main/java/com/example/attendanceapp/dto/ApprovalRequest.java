package com.example.attendanceapp.dto;

import lombok.Data;

/**
 * このクラスは承認申請に関するリクエストデータを保持するためのDTO（Data Transfer Object）です。
 * 承認申請時に必要な情報をクライアントから受け取り、サーバ側へ転送するために使用されます。
 */
@Data  // Lombokアノテーション：getter、setter、equals、hashCode、toStringメソッドが自動生成されます
public class ApprovalRequest {

    /**
     * コメントフィールド
     * <p>
     * 承認申請に対して、ユーザーが追加するコメントを保持します。
     * このフィールドは、申請の理由や補足情報を説明するために利用されます。
     */
    private String comment;
}