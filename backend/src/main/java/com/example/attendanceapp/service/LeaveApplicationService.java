package com.example.attendanceapp.service;

import com.example.attendanceapp.dto.LeaveApplicationRequest;
import com.example.attendanceapp.dto.LeaveApplicationResponse;
import com.example.attendanceapp.dto.LeaveBalanceResponse;
import com.example.attendanceapp.mapper.LeaveApplicationMapper;
import com.example.attendanceapp.model.LeaveApplication;
import com.example.attendanceapp.model.User;
import com.example.attendanceapp.repository.LeaveApplicationRepository;
import com.example.attendanceapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * このサービスクラスは、休暇申請に関するビジネスロジックを実装しています。
 * ユーザーの休暇申請処理を行い、休暇の上限チェックや時間計算などのロジックを含みます。
 */
@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

    // 休暇申請に関するデータの永続化操作を行うリポジトリ
    private final LeaveApplicationRepository leaveApplicationRepository;

    // ユーザー情報の取得を行うリポジトリ
    private final UserRepository userRepository;

    private final LeaveApplicationMapper mapper;

    /**
     * ユーザーが休暇を申請する際の処理を実装するメソッドです。
     *
     * @param request        画面などから送信される休暇申請データを保持するDTO
     * @param employeeNumber ユーザー固有の社員番号。これによりユーザー情報を特定します。
     *                       <p>
     *                       このメソッドでは、以下の処理を行います:
     *                       1. ユーザーリポジトリから社員番号に基づいてユーザー情報を取得。ユーザーが存在しない場合は例外を投げます。
     *                       2. 休暇の種類(partDayType)に応じた休暇申請の所要時間（分単位）を計算します。
     *                       - "FULL": フルデイ休暇の場合、465分を設定
     *                       - "AM"または"PM": 午前または午後の半休の場合、465分の半分となる232分(小数点以下切り捨て)を設定
     *                       - "TIME": 時間指定の場合、開始時刻と終了時刻の差分から分を計算
     *                       - 上記以外の場合は不正な区分として例外を投げます。
     *                       3. 当該ユーザーの指定された年度の既に使用した休暇時間の合計を取得し、今回の申請時間と合算。
     *                       合計が年間上限（ここでは1860分）を超えている場合は例外を投げ、申請をブロックします。
     *                       4. 新しい休暇申請エンティティを作成し、各フィールドにデータを設定します。
     *                       - ステータスは初期状態として"PENDING"に設定し、申請状態を管理
     *                       - 申請日時として現在の時刻を設定
     *                       5. 最終的に、休暇申請リポジトリを利用してデータベースに保存します。
     */
    public void applyLeave(LeaveApplicationRequest request, String employeeNumber) {
        // 社員番号に基づきユーザーを取得。ユーザーが存在しない場合は例外を発生させて処理を中断
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        // 部分休暇の種類に応じた休暇申請の時間（分単位）を決定するスイッチ式
        int durationMinutes = switch (request.getPartDayType()) {
            case "FULL" -> {
                // フルデイ休暇の場合、所定の休暇時間を465分とする
                yield 465;
            }
            case "AM", "PM" -> {
                // 午前または午後の半休の場合、465分の半分（切り捨て処理は不要、整数計算）を設定
                yield 465 / 2;
            }
            case "TIME" -> {
                // 時間指定の場合、開始時刻と終了時刻が必須なので、NULLの場合は例外を発生させる
                if (request.getStartTime() == null || request.getEndTime() == null) {
                    throw new IllegalArgumentException("時間指定には開始・終了時刻が必要です");
                }
                // 開始・終了時刻の差分を分単位で計算し、その値を用いる
                yield (int) Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
            }
            default -> throw new IllegalArgumentException("不正な区分です");
        };

        // 指定年度におけるユーザーの既に使用した休暇時間（分単位）の合計を取得
        // もしデータが存在しない場合はNULLとなるため0で初期化する
        Integer usedMinutes = leaveApplicationRepository
                .sumUsedMinutesByUserAndYear(user, request.getStartDate().getYear());
        if (usedMinutes == null) usedMinutes = 0;

        // 合計休暇時間が年間の上限（ここでは1860分）を超えるかチェックし、超える場合は例外を発生させる
        if (usedMinutes + durationMinutes > 1860) {
            throw new IllegalArgumentException("年間の休暇取得上限を超えています");
        }

        // 新しい休暇申請エンティティを生成し、各フィールドにデータを設定
        LeaveApplication entity = new LeaveApplication();
        // 申請者のユーザー情報
        entity.setUser(user);
        // 休暇の開始・終了日
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        // 時間指定の休暇の場合の開始・終了時刻（存在する場合）
        entity.setStartTime(request.getStartTime());
        entity.setEndTime(request.getEndTime());
        // 部分休暇の種類（例："FULL", "AM", "PM", "TIME"）
        entity.setPartDayType(request.getPartDayType());
        // 休暇の種類（例：有給休暇、病気休暇など）
        entity.setType(request.getType());
        // 休暇申請の理由
        entity.setReason(request.getReason());
        // 初期状態として、申請ステータスを"申請中"に設定
        entity.setStatus("申請中");
        // 計算された休暇の期間（分単位）
        entity.setDurationMinutes(durationMinutes);
        // 申請日時として、現在時刻を設定
        entity.setCreatedAt(LocalDateTime.now());

        // 最後にエンティティを永続化し、データベースに保存する
        leaveApplicationRepository.save(entity);
    }

    // ユーザー自身の休暇申請を取得するためのメソッド
    // 各ユーザーに紐づいた申請データを新しい順に取得し、レスポンスDTOに変換して返します
    public List<LeaveApplicationResponse> getOwnApplications(String employeeNumber) {
        // 社員番号に基づいてユーザー情報を取得します
        // ユーザーが見つからない場合は RuntimeException をスローします
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        // ユーザーに関連する休暇申請データを新しい順に取得します
        // 取得した各エンティティを toResponse メソッドで DTO に変換し、結果のリストを返します
        return leaveApplicationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 休暇申請エンティティをレスポンス用のDTOに変換するためのヘルパーメソッド
// エンティティからDTOへのプロパティコピーを行い、DTOを返します
    private LeaveApplicationResponse toResponse(LeaveApplication entity) {
        // 新しいレスポンスDTOのインスタンスを作成します
        LeaveApplicationResponse dto = new LeaveApplicationResponse();
        // BeanUtils を用いて、エンティティのプロパティを DTO にコピーします
        BeanUtils.copyProperties(entity, dto);
        // コピー後のDTOを返却します
        return dto;
    }

    public LeaveBalanceResponse getLeaveBalance(String employeeNumber) {
        // 指定された従業員番号に基づいてユーザー情報を検索する。
        // ユーザーが存在しない場合は例外をスローする。
        User user = userRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません"));

        // 現在の西暦年において、ユーザーが既に使用した休暇の合計時間（分単位）を取得する。
        // 結果がnullの場合は0とする。
        int used = Optional.ofNullable(
                leaveApplicationRepository.sumUsedMinutesByUserAndYear(user, LocalDate.now().getYear())
        ).orElse(0);

        // TODO: 将来的にはアプリ側で休暇上限の設定を取得できるようにする。
        // 現在はハードコーディングされた休暇上限（分単位）を設定している。
        int limit = 1860;

        // LeaveBalanceResponseオブジェクトを作成し、使用済み分、残り分、全上限を設定して返す。
        return new LeaveBalanceResponse(used, limit - used, limit);
    }

    // 従業員の休暇申請を承認するメソッド
    public void approveLeave(Long id, String approverEmployeeNumber, String comment) {
        // 休暇申請IDから休暇申請オブジェクトを取得する。存在しない場合は例外をスローする。
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("申請が見つかりません"));

        // 承認者の社員番号からユーザーオブジェクトを取得する。存在しない場合は例外をスローする。
        User approver = userRepository.findByEmployeeNumber(approverEmployeeNumber)
                .orElseThrow(() -> new RuntimeException("承認者が見つかりません"));

        // 休暇申請の状態を"承認済"（承認済み）に更新する
        application.setStatus("承認済");

        // 承認者からのコメントを休暇申請に設定する
        application.setApproverComment(comment);

        // 承認日時を現在の日時に設定する
        application.setApprovedAt(LocalDateTime.now());

        // 承認者のユーザー情報を休暇申請に設定する
        application.setApprover(approver);

        // 更新された休暇申請情報をデータベースに保存する
        leaveApplicationRepository.save(application);
    }

    // 従業員の休暇申請を拒否するメソッド
    public void rejectLeave(Long id, String approverEmployeeNumber, String comment) {
        // 休暇申請IDから休暇申請オブジェクトを取得する。存在しない場合は例外をスローする。
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("申請が見つかりません"));

        // 承認者の社員番号からユーザーオブジェクトを取得する。存在しない場合は例外をスローする。
        User approver = userRepository.findByEmployeeNumber(approverEmployeeNumber)
                .orElseThrow(() -> new RuntimeException("承認者が見つかりません"));

        // 休暇申請の状態を"却下"（拒否）に更新する
        application.setStatus("却下");

        // 承認者からのコメントを休暇申請に設定する
        application.setApproverComment(comment);

        // 拒否日時または決定日時として現在の日時を設定する
        application.setApprovedAt(LocalDateTime.now());

        // 承認者のユーザー情報を休暇申請に設定する
        application.setApprover(approver);

        // 更新された休暇申請情報をデータベースに保存する
        leaveApplicationRepository.save(application);
    }

    public List<LeaveApplicationResponse> getPendingApprovals(String managerNumber) {
        return leaveApplicationRepository
                .findPendingByManager(managerNumber)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
