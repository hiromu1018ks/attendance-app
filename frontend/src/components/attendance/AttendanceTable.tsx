import { useEffect, useState } from "react";
import api from "@/lib/api.ts";
import dayjs from "dayjs";

type AttendanceItem = {
  date : string;
  clockIn : string | null;
  clockOut : string | null;
  workingHours : string;
  isCorrected : boolean;
};

// AttendanceTable コンポーネントは出席情報をテーブル形式で表示するためのコンポーネントです。
export const AttendanceTable = () => {
  // useState を使って、AttendanceItem 型の配列を管理します。
  // この配列は API から取得した各日の出席情報を保持します。
  const [ items, setItems ] = useState<AttendanceItem[]>([]);

  // useEffect フックを使ってコンポーネントの初回レンダリング時に処理を実行します。
  useEffect(() => {
    // 現在の日付情報を取得
    const now = new Date();
    // 現在の年を取得
    const year = now.getFullYear();
    // 現在の月を取得（JavaScript の getMonth() は 0 から始まるため +1 しています）
    const month = now.getMonth() + 1;

    // 指定された年と月に基づいて API リクエストを実行し、月単位の出席情報を取得
    api.get(`/api/attendance/monthly?year=${ year }&month=${ month }`).then(res => {
      // API のレスポンスデータを items ステートにセット
      setItems(res.data);
    })
  }, [])

  // コンポーネントのレンダリング部分
  return (
    // 横にスクロール可能な div コンテナ
    <div className="overflow-x-auto mt-4">
      {/* テーブル全体のスタイルを設定 */ }
      <table className="min-w-full border border-gray-300 text-sm">
        {/* テーブルのヘッダー部分 */ }
        <thead className="bg-gray-100">
        <tr>
          {/* 各列の見出し: 日付、出勤、退勤、勤務時間、修正済み */ }
          <th className="border p-2">日付</th>
          <th className="border p-2">出勤</th>
          <th className="border p-2">退勤</th>
          <th className="border p-2">勤務時間</th>
          <th className="border p-2">修正済み</th>
        </tr>
        </thead>
        {/* テーブルのボディ部分。各行は items 配列の各 AttendanceItem を表します。 */ }
        <tbody>
        { items.map((item, idx) => (
          // 各行に一意のキーとしてインデックスを設定
          <tr key={ idx } className="text-center">
            {/* 日付は dayjs を利用して "M/D" 形式に変換 */ }
            <td className="border p-2">{ dayjs(item.date).format("M/D") }</td>
            {/* 出勤時間が存在する場合には "HH:mm" 形式に変換、それ以外は "-" を表示 */ }
            <td className="border p-2">{ item.clockIn ? dayjs(item.clockIn).format("HH:mm") : "-" }</td>
            {/* 退勤時間に関しても同様に処理 */ }
            <td className="border p-2">{ item.clockOut ? dayjs(item.clockOut).format("HH:mm") : "-" }</td>
            {/* 勤務時間の文字列から "PT" プレフィックスを除去して表示 */ }
            <td className="border p-2">{ item.workingHours.replace("PT", "") }</td>
            {/* isCorrected が true の場合はチェックマーク、false の場合は "-" を表示 */ }
            <td className="border p-2">{ item.isCorrected ? "✔️" : "-" }</td>
          </tr>
        )) }
        </tbody>
      </table>
    </div>
  );
};