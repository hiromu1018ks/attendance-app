// react-router-domからNavigateコンポーネントをインポートしています。
// Navigateは、リダイレクト処理に利用されます。
import { Navigate } from "react-router-dom";

// reactパッケージからJSX型をインポートしています。
// JSX.Elementは、Reactコンポーネントの戻り値の型です。
import { JSX } from "react";

// ProtectedRouteは、認証されたユーザーのみがアクセスできるルートを作成するためのコンポーネントです。
// 以下のコードは、ProtectedRouteコンポーネントの定義です。
// 子コンポーネントとしてJSX要素を受け取り、認証状態に応じてレンダリングします。
export const ProtectedRoute = ({ children } :
                               {
                                 // children: このコンポーネントに渡される子コンポーネントを表します。
                                 children : JSX.Element
                               }
) => {
  // ローカルストレージから"token"の値を取得します。
  // この"token"は、ユーザーが認証済みかどうかの判断に利用されます。
  const token = localStorage.getItem("token");

  // tokenが存在しない場合は、ユーザーが認証されていないと判断し、
  // loginページにリダイレクトします。
  if ( !token ) return <Navigate to="/login" replace/>

  // tokenが存在する場合は、認証されたユーザーとみなし、
  // 渡された子コンポーネントをそのままレンダリングします。
  return children;
}