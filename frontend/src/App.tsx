// react-router-domライブラリから必要なコンポーネントをインポートしています。
// BrowserRouter: ブラウザのアドレスバーを利用したルーティング機能を提供
// Routes: 一連のRoute定義をまとめるコンテナ
// Route: URLパスに対応するコンポーネントのレンダリングを定義
// Navigate: 条件によりリダイレクトを行うコンポーネント
import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

// ログインページのコンポーネントをインポート
import LoginPage from "@/pages/LoginPage.tsx";

// 認証が必要なルートを保護するためのコンポーネントをインポート
import { ProtectedRoute } from "@/components/auth/ProtectedRoute.tsx";

// ダッシュボードページのコンポーネントをインポート
import DashboardPage from "@/pages/DashboardPage.tsx";
import LeavePage from "@/pages/LeavePage.tsx";
import LeaveApprovalPage from "@/pages/LeaveApprovalPage.tsx";

// Appコンポーネントは、アプリケーションのエントリーポイントとなる関数コンポーネントです。
// このコンポーネントは、全体のルーティング構成および各ページのレンダリング処理を担当します。
function App() {
  return (
    // <BrowserRouter>は、HTML5のhistory APIを利用してクライアントサイドのルーティングを可能にするコンポーネントです。
    // これにより、アプリケーション内で異なるパスに対して異なるコンポーネントを表示できます。
    <BrowserRouter>
      {/* <Routes>コンポーネントは、複数の<Route>コンポーネントを内包し、各パスに対応するレンダリング処理を管理します。 */ }
      <Routes>
        {/* "/login"パスにアクセスした場合、LoginPageコンポーネントがレンダリングされます。 */ }
        <Route path="/login" element={ <LoginPage/> }/>

        {/* "/dashboard"パスは、ProtectedRouteコンポーネントでラップされています。
            ProtectedRouteは、ユーザーの認証状態を確認し、認証されている場合はDashboardPageコンポーネントを表示し、
            認証されていない場合はログインページやエラーページにリダイレクトするロジックを内包しています。 */ }
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage/>
            </ProtectedRoute>
          }
        />

        {/* "/leave"パスもProtectedRouteでラップされ、ユーザーの認証状態に基づいてLeavePageコンポーネントをレンダリングします。 */ }
        <Route path="/leave"
               element={
                 <ProtectedRoute>
                   <LeavePage/>
                 </ProtectedRoute>
               }
        />

        {/* "/admin/leaves"パスは、ProtectedRouteコンポーネントでラップされており、
            認証が確認された場合にLeaveApprovalPageコンポーネントをレンダリングします。 */ }
        <Route path="/manager/leaves"
               element={
                 <ProtectedRoute>
                   <LeaveApprovalPage/>
                 </ProtectedRoute>
               }
        />

        {/* "/approvals"パスもProtectedRouteでラップされ、ユーザーが認証されている場合にLeaveApprovalPageコンポーネントを表示します。 */ }
        <Route path="/approvals" element={
          <ProtectedRoute>
            <LeaveApprovalPage/>
          </ProtectedRoute>
        }/>

        {/* 定義されていない任意のパスにアクセスされた場合、 Navigateコンポーネントにより"/login"パスにリダイレクトされます。
            これにより、存在しないページへのアクセス時にユーザーがログインページに誘導されるようになっています。 */ }
        <Route path="*" element={ <Navigate to="/login"/> }/>
      </Routes>
    </BrowserRouter>
  );
}

// Appコンポーネントをエクスポートすることで、他のモジュールからこのルーティング設定にアクセスできるよう
export default App;
