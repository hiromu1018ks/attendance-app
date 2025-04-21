import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Input } from "@/components/ui/input.tsx";
import { Button } from "@/components/ui/button.tsx";
import api from "@/lib/api.ts";

// LoginFormコンポーネントは、ユーザーに職員番号とパスワードを入力させ、ログイン処理を行うためのUIコンポーネントです。
export const LoginForm = () => {
  // useStateフックで、職員番号の入力値（文字列）を管理します。
  // 初期値は空文字列("")です。
  const [ employeeNumber, setEmployeeNumber ] = useState("");

  // useStateフックで、パスワードの入力値（文字列）を管理します。
  // 初期値は空文字列("")となっています。
  const [ password, setPassword ] = useState("");

  // useStateフックで、ログイン失敗時などのエラー情報を保持します。
  // エラーが発生した際に、エラーメッセージ（文字列）を格納します。
  const [ error, setError ] = useState("");

  // React RouterのuseNavigateフックを使用して、画面間の遷移操作を簡単に行えるようにします。
  const navigate = useNavigate();

  // handleLogin関数は、ユーザーがログインボタンをクリックした際に呼び出される非同期関数です。
  // この関数は、入力された職員番号とパスワードを利用して、ログインAPIにリクエストを送信し、
  // レスポンスに基づいてトークンの保存やページ遷移を実施します。
  const handleLogin = async () => {
    try {
      // api.postを用いて、ログインAPIエンドポイント("/api/auth/login")にPOSTリクエストを送信します。
      // リクエストボディには現在のemployeeNumberとpasswordの値を設定し、サーバ側で認証処理を行います。
      const response = await api.post("/api/auth/login", {
        employeeNumber,
        password,
      });

      // APIのレスポンスから認証に成功した場合のトークンを取得します。
      const token = response.data.token;

      // ローカルストレージにトークンを保存します。これにより、他のAPI呼び出しで認証情報として利用できるようになります。
      localStorage.setItem("token", token);

      // 認証成功後、ユーザーをダッシュボードページ("/dashboard")へ遷移させます。
      navigate("/dashboard");
    } catch {
      // もしログインAPIからの応答でエラーが発生した場合（例：認証失敗など）は、
      // エラーメッセージを設定して、ユーザーにログイン失敗の旨を知らせます。
      setError("ログインに失敗しました。職員番号またはパスワードを確認してください。")
    }
  };

  // コンポーネントのレンダリング部です。ここで、入力フィールドやボタンを配置し、ユーザーが操作できるようにします。
  return (
    <div className="space-y-4">
      {/* 職員番号入力フィールド:
          - プレースホルダーとして「職員番号」を表示します。
          - 入力値はemployeeNumber状態に紐づけられ、
          - 入力値の変更によってsetEmployeeNumber関数が呼ばれ、状態が更新されます。 */ }
      <Input
        placeholder="職員番号"
        value={ employeeNumber }
        onChange={ (e) => setEmployeeNumber(e.target.value) }
      />

      {/* パスワード入力フィールド:
          - 入力型を"password"に設定することで、入力内容が隠されます。
          - プレースホルダーとして「パスワード」を表示します。
          - 入力値はpassword状態に紐づけられ、入力変更ごとにsetPasswordで状態を更新します。 */ }
      <Input
        type="password"
        placeholder="パスワード"
        value={ password }
        onChange={ (e) => setPassword(e.target.value) }
      />

      {/* エラーメッセージの表示部分:
          - error状態に文字列が設定されていれば、そのメッセージを赤色で表示します。
          - エラーメッセージが無い場合は表示されません。 */ }
      { error && <p className="text-red-500 text-sm">{ error }</p> }

      {/* ログインボタン:
          - ボタンがクリックされるとhandleLogin関数が実行され、ログイン処理が開始されます。
          - ボタンは幅を全体に広げるクラスが適用されています。 */ }
      <Button className="w-full" onClick={ handleLogin }>
        ログイン
      </Button>
    </div>
  );
};