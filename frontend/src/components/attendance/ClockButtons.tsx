import { useState } from "react";
import { Button } from "@/components/ui/button.tsx";
import api from "@/lib/api.ts";

export const ClockButtons = () => {
  // React の useState フックを使って、打刻結果のメッセージを管理します。
  // message は表示するメッセージ、setMessage はその値を更新する関数です。
  const [ message, setMessage ] = useState("");

  // clock 関数は、打刻（出勤または退勤）の処理を行う非同期関数です。
  // 引数 type には "in" または "out" を渡し、それぞれ出勤と退勤を表します。
  const clock = async (type : "in" | "out") => {
    try {
      // 入力された type に基づいて、対応する API エンドポイントへ POST リクエストを送信します。
      // 例: type が "in"の場合は "/api/attendance/clock-in" にリクエストを送信します。
      const res = await api.post(`/api/attendance/clock-${ type }`);

      // API のレスポンスから受け取ったメッセージを状態変数に保存し、画面に表示します。
      setMessage(res.data);
    } catch {
      // API 呼び出しに失敗した場合、エラーメッセージを状態変数にセットし、画面に通知します。
      setMessage("打刻に失敗しました");
    }
  };

  // コンポーネントのレンダリング内容です。
  // 2 つのボタンがあり、それぞれ出勤と退勤の打刻処理を実行します。
  // さらに、message に値がある場合はその内容を表示します。
  return (
    <div className="space-x-4">
      {/* 出勤ボタン: クリックすると clock 関数が "in" を引数として呼び出されます */ }
      <Button onClick={ () => clock("in") }>出勤</Button>

      {/* 退勤ボタン: クリックすると clock 関数が "out" を引数として呼び出されます */ }
      <Button onClick={ () => clock("out") }>退勤</Button>

      {/* message が空でなければ、そのメッセージを表示します */ }
      { message && <p className="mt-2 text-sm text-green-600">{ message }</p> }
    </div>
  );
};