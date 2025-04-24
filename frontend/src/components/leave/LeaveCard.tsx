// 必要なUIコンポーネント(CardおよびCardContent)をimportします。
// これらはカード形式の表示を実現するためのコンポーネントです。
import { Card, CardContent } from "@/components/ui/card.tsx";

// コンポーネントのプロパティの型定義。
// Props型は、休暇申請の情報を格納したapplicationというオブジェクトを持ちます。
// applicationオブジェクトの各フィールドの説明:
// - id          : 申請の一意の識別子。
// - startDate   : 休暇開始日。
// - endDate     : 休暇終了日。
// - partDayType : 半日などの区分を示す文字列。
// - type        : 休暇のタイプ（例: 年次、有給など）。
// - reason      : 休暇申請の理由の説明。
// - status      : 申請の現在の状態（例: 承認待ち、承認済み、却下など）。
// - comment     : （オプション）承認者からのコメント。
// - submittedAt : （オプション）申請日時。
type Props = {
  application : {
    id : number;
    startDate : string;
    endDate : string;
    partDayType : string;
    type : string;
    reason : string;
    status : string;
    comment? : string;
    submittedAt? : string;
  }
}

// LeaveCardコンポーネントの定義。
// このコンポーネントは1件の休暇申請情報(application)を受け取り、カード形式で情報を表示します。
export const LeaveCard = ({ application } : Props) => {
  return (
    // カードデザインのUIコンポーネントであるCardで全体を囲みます。
    <Card>
      {/* カードの内容を表示するためのコンポーネントCardContent */ }
      <CardContent>
        {/* 休暇期間の開始日と終了日を表示 */ }
        <p className="text-sm font-medium">
          期間:{ application.startDate }~{ application.endDate }
        </p>
        {/* 休暇の区分情報を表示。半日区分と休暇タイプを含む */ }
        <p className="text-sm text-muted-foreground">
          区分:{ application.partDayType } / { application.type }
        </p>
        {/* 休暇申請の理由を表示 */ }
        <p className="text-sm">
          理由:{ application.reason }
        </p>
        {/* 現在の申請ステータスを表示。ステータスは強調して表示 */ }
        <p className="text-sm">
          ステータス:<span className="font-semibold">{ application.status }</span>
        </p>
        {/* オプションのフィールドであるコメントが存在する場合のみ、承認コメントを表示 */ }
        { application.comment && (
          <p className="text-xs text-muted-foreground">
            承認コメント:{ application.comment }
          </p>
        ) }
      </CardContent>
    </Card>
  );
};