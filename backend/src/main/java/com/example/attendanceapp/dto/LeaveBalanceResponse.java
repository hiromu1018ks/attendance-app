package com.example.attendanceapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 休暇残高の情報を保持するDTOクラスです。
 * このクラスは、従業員の休暇の利用状況を示すためのデータ構造を定義しています。
 * 利用済みの休暇時間、残りの休暇時間、および休暇使用可能な上限時間を管理します。
 * Lombokの@Dataアノテーションによりgetterやsetterなどのメソッドが自動生成され、
 * &#064;AllArgsConstructorアノテーションにより全フィールドを引数に持つコンストラクタが自動生成されます。
 */
@Data
@AllArgsConstructor
public class LeaveBalanceResponse {

    /**
     * 利用済みの休暇時間（分単位）
     * 従業員がすでに休暇として使用した分数を示します。
     */
    private int usedMinutes;

    /**
     * 残りの休暇時間（分単位）
     * 現在利用可能な休暇の分数を示します。残りの分数は上限から利用済みの分数を差し引いた値です。
     */
    private int remainingMinutes;

    /**
     * 休暇使用可能な上限時間（分単位）
     * 従業員が使用できる休暇時間の最大値を示しています。システムによって定められた上限です。
     */
    private int limitMinutes;
}