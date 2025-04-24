// axiosモジュールをインポート。axiosはHTTPリクエストを簡単に送信できるライブラリです。
import axios from 'axios';
import { LeaveApplicationResponse } from "@/types/leave.ts";

// axiosのインスタンスを作成します。
// このインスタンスは、HTTPリクエストの送信先のベースURLを指定しています。
// baseURLフィールドは、すべてのリクエストのURLの先頭部分として利用されるため、ルートパスを統一できます。
const api = axios.create({
  baseURL : "http://localhost:8080", // サーバーのアドレスとポート番号を指定しています。
});

// リクエストインターセプターを設定します。
// インターセプターはリクエストがサーバーに送信される前に処理を追加するための仕組みです。
api.interceptors.request.use((config) => {
  // localStorageから"token"を取得します。
  // このトークンは、認証が必要なリクエストに付加されるアクセストークンです。
  const token = localStorage.getItem("token");

  // トークンが存在する場合、リクエストヘッダーにAuthorizationフィールドを追加します。
  // Bearerスキームを使用して、サーバー側でアクセストークンとして認識されるようにしています。
  if ( token ) {
    config.headers.Authorization = `Bearer ${ token }`;
  }
  // 処理が完了した設定情報を返して、リクエストが実際に送信されるようにします。
  return config;
});

// 作成したaxiosインスタンスを他のモジュールから利用できるようにエクスポートします。
export default api;

// 休暇申請のデータをサーバーから取得するための関数です。
// この関数は api インスタンスの get メソッドを呼び出しており、指定されたエンドポイント "api/leaves" へ HTTP GET リクエストを送信します。
// レスポンスの型は LeaveApplicationResponse として型定義されており、
// 休暇申請に関する各フィールド (ID、開始日、終了日、部分休暇の種類、開始時刻、終了時刻、申請種類、理由、状況、及び承認者からのコメント) の情報が含まれています。
export const fetchLeaveApplications = () : Promise<{ data : LeaveApplicationResponse[] }> => {
  return api.get("/api/leaves");
};
