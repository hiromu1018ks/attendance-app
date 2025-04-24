import { useEffect, useState } from "react";
import api from "@/lib/api";
import { LeaveApplicationResponse } from "@/types/leave";
import { LeaveApprovalItem } from "./LeaveApprovalItem";

/**
 * 休暇申請の承認待ちリストを表示するコンポーネント
 * マネージャー向けの画面で、承認待ちの休暇申請一覧を取得して表示する
 */
export const LeaveApprovalList = () => {
  // 休暇申請データを保持するstate
  const [ leaves, setLeaves ] = useState<LeaveApplicationResponse[]>([]);
  // エラーメッセージを保持するstate
  const [ error, setError ] = useState("");

  /**
   * APIから休暇申請データを取得する関数
   * 成功時はleavesステートに申請データを設定
   * 失敗時はエラーメッセージを設定
   */
  const fetchLeaves = async () => {
    console.log("★fetchLeaves 実行");
    try {
      // マネージャー向けAPIエンドポイントから休暇申請一覧を取得
      const res = await api.get("/api/manager/leaves");
      // 取得したデータをステートに設定
      setLeaves(res.data);
    } catch {
      // エラー発生時はエラーメッセージをステートに設定
      setError("申請一覧の取得に失敗しました");
    }
  };

  /**
   * コンポーネントのマウント時に一度だけ休暇申請データを取得
   * 空の依存配列により、コンポーネントが初めてレンダリングされた時のみ実行される
   */
  useEffect(() => {
    fetchLeaves();
  }, []);

  // エラーが発生した場合はエラーメッセージを表示
  if ( error ) return <p className="text-red-500">{ error }</p>;
  // 申請データが空の場合はメッセージを表示
  if ( leaves.length === 0 ) return <p>申請はありません。</p>;

  // 申請データがある場合は一覧を表示
  return (
    <div className="space-y-4">
      {/* 各休暇申請に対してLeaveApprovalItemコンポーネントを表示 */ }
      { leaves.map(leave => (
        <LeaveApprovalItem
          key={ leave.id } // リストの各要素に一意のキーを指定
          leave={ leave } // 休暇申請データをプロップスとして渡す
          onAction={ fetchLeaves } // アクション（承認・却下）後に一覧を再取得するコールバック
        />
      )) }
    </div>
  );
};