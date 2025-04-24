// LeaveApplicationResponse型のフィールドの意味
// id: 申請の一意な識別子
// startDate: 休暇の開始日
// endDate: 休暇の終了日
// partDayType: 休暇が半日か時間単位かを示す種類 ("TIME"の場合、時間指定)
// startTime: 時間指定の場合の開始時刻（任意）
// endTime: 時間指定の場合の終了時刻（任意）
// type: 休暇の種類（例：有給、欠勤など）
// reason: 休暇申請の理由
// status: 現在の申請状況（例：承認待ち、承認済みなど）
// approverComment: 承認者からのコメント（任意）

// コンポーネントのPropsの型定義
// leave: LeaveApplicationResponse型のデータを受け取る
import { LeaveApplicationResponse } from "@/types/leave.ts";

type Props = {
  leave : LeaveApplicationResponse
};

// LeaveItemコンポーネントは個々の休暇申請の情報を表示するためのコンポーネントです。
// 各フィールドの情報を見やすく表示するために、スタイル付きのdivタグでラップしています。
export const LeaveItem = ({ leave } : Props) => {

  // コンポーネントのレンダリング部分
  // 申請情報を表示するために、各フィールドの値を適切にレンダリングしています
  return (
    <div className="border p-4 rounded shadow-sm mb-2">
      {/* 休暇期間を表示：開始日と終了日 */ }
      <p className="text-sm text-muted-foreground">
        取得日: { leave.startDate } ~ { leave.endDate }
      </p>
      {/* 休暇の取得区分を表示：全日、半日、または時間単位かを区別 */ }
      <p>
        取得区分: { leave.partDayType }
      </p>
      {/* 休暇の取得区分が"TIME"の場合、時間単位の申請として開始時刻と終了時刻を表示 */ }
      { leave.partDayType === "TIME" && (
        <p>
          時間: { leave.startTime } ~ { leave.endTime }
        </p>
      ) }
      {/* 休暇の種類を表示 */ }
      <p>
        種類: { leave.type }
      </p>
      {/* 休暇申請の理由を表示 */ }
      <p>
        理由: { leave.reason }
      </p>
      {/* 現在の申請ステータスを表示 */ }
      <p>
        ステータス: { leave.status }
      </p>
      {/* 承認者からのコメントがある場合のみ、そのコメントを表示 */ }
      { leave.approverComment && (
        <p className="text-sm text-muted-foreground">
          承認者コメント: { leave.approverComment }
        </p>
      ) }
    </div>
  );
};