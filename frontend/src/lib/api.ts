// axiosモジュールをインポート。axiosはHTTPリクエストを簡単に送信できるライブラリです。
import axios from 'axios';

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