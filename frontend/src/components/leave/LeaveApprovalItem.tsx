import { LeaveApplicationResponse } from "@/types/leave";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import api from "@/lib/api";

type Props = {
  leave : LeaveApplicationResponse;
  onAction? : () => void; // 承認・却下後にリスト更新用
};


/**
 * 休暇申請の承認/却下コンポーネント
 * 管理者が休暇申請を確認し、承認または却下するためのUI
 * @param {Props} props - コンポーネントのプロパティ
 * @param {LeaveApplicationResponse} props.leave - 休暇申請データ
 * @param {Function} props.onAction - 承認/却下後に実行されるコールバック関数
 */
export const LeaveApprovalItem = ({ leave, onAction } : Props) => {
  // 処理中の状態を管理（ボタン無効化などに使用）
  const [ processing, setProcessing ] = useState(false);
  // ユーザーへのフィードバックメッセージを管理
  const [ message, setMessage ] = useState("");

  /**
   * 休暇申請を承認する処理
   * APIを呼び出して申請を承認し、結果に応じてメッセージを表示
   */
  const handleApprove = async () => {
    try {
      // 処理中フラグをオンにしてボタンを無効化
      setProcessing(true);
      // 承認APIを呼び出し
      await api.post(`/api/manager/leaves/${ leave.id }/approve`, { comment : "承認します" });
      // 成功メッセージを設定
      setMessage("承認しました");
      // 親コンポーネントに処理完了を通知（リスト更新のため）
      onAction?.();
    } catch {
      // エラーが発生した場合のメッセージを設定
      setMessage("承認に失敗しました");
    } finally {
      // 処理完了後、処理中フラグをオフに戻す
      setProcessing(false);
    }
  };

  /**
   * 休暇申請を却下する処理
   * APIを呼び出して申請を却下し、結果に応じてメッセージを表示
   */
  const handleReject = async () => {
    try {
      // 処理中フラグをオンにしてボタンを無効化
      setProcessing(true);
      // 却下APIを呼び出し
      await api.post(`/api/manager/leaves/${ leave.id }/reject`, { comment : "却下します" });
      // 成功メッセージを設定
      setMessage("却下しました");
      // 親コンポーネントに処理完了を通知（リスト更新のため）
      onAction?.();
    } catch {
      // エラーが発生した場合のメッセージを設定
      setMessage("却下に失敗しました");
    } finally {
      // 処理完了後、処理中フラグをオフに戻す
      setProcessing(false);
    }
  };

  return (
    <div className="border p-4 rounded-md space-y-2">
      {/* 申請者の情報表示 */ }
      <p><strong>氏名：</strong>{ leave.employeeName ?? "（非表示）" }</p>
      {/* 休暇の期間表示 */ }
      <p><strong>日付：</strong>{ leave.startDate } ～ { leave.endDate }</p>
      {/* 時間帯（終日・午前・午後など）の表示 */ }
      <p><strong>時間帯：</strong>{ leave.partDayType }</p>
      {/* 休暇の種類（有給・特別休暇など）の表示 */ }
      <p><strong>種類：</strong>{ leave.type }</p>
      {/* 休暇の理由表示 */ }
      <p><strong>理由：</strong>{ leave.reason }</p>
      {/* 操作ボタンエリア */ }
      <div className="flex gap-2">
        {/* 承認ボタン - 処理中は無効化 */ }
        <Button variant="default" onClick={ handleApprove } disabled={ processing }>承認</Button>
        {/* 却下ボタン - 処理中は無効化 */ }
        <Button variant="destructive" onClick={ handleReject } disabled={ processing }>却下</Button>
      </div>
      {/* 処理結果のフィードバックメッセージ（存在する場合のみ表示） */ }
      { message && <p className="text-sm text-muted-foreground">{ message }</p> }
    </div>
  );
};
