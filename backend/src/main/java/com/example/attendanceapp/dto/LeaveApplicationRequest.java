package com.example.attendanceapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 休暇申請のリクエスト情報を保持するDTOクラス
 * クライアントから送信される休暇申請の各項目をカプセル化しています。
 */
@Data
public class LeaveApplicationRequest {

    /**
     * 休暇の開始日を保持するフィールド
     * アノテーション @NotNull により、開始日は必須であることを示しています。
     */
    @NotNull
    private LocalDate startDate;

    /**
     * 休暇の終了日を保持するフィールド
     * アノテーション @NotNull により、終了日は必須であることを示しています。
     */
    @NotNull
    private LocalDate endDate;

    /**
     * 休暇の開始時刻を保持するフィールド
     * 分単位や時間単位の休暇申請などで、詳細な時間設定が必要な場合に使用されます。
     */
    private LocalTime startTime;

    /**
     * 休暇の終了時刻を保持するフィールド
     * 分単位や時間単位の休暇申請などで、詳細な時間設定が必要な場合に使用されます。
     */
    private LocalTime endTime;

    /**
     * 部分休暇の種類を示すフィールド
     * 午前半休、午後半休など、1日の全休ではなく一部のみの休暇指定に用います。
     */
    private String partDayType;

    /**
     * 休暇の種類を示すフィールド
     * 例：年次有給休暇、病気休暇など。アノテーション @NotBlank により、空文字ではない必要があることを示しています。
     */
    @NotBlank
    private String type;

    /**
     * 休暇申請の理由を保持するフィールド
     * 休暇申請の正当性を示すための説明を記述します。アノテーション @NotBlank により、必ず値が入力される必要があります。
     */
    @NotBlank
    private String reason;
}