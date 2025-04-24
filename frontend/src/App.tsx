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

// App関数コンポーネントは、アプリケーション全体のルーティング設定を管理しています。
function App() {
  return (
    // BrowserRouterコンポーネントでラップすることで、ルーティングできる環境を提供
    <BrowserRouter>
      {/* Routesコンポーネントは、個々のRoute定義を内包します。 */ }
      <Routes>
        {/* "/login"パスにアクセスした場合、LoginPageコンポーネントがレンダリングされます。 */ }
        <Route path="/login" element={ <LoginPage/> }/>

        {/* "/dashboard"パスは、ProtectedRouteコンポーネントでラップされています。
            ProtectedRouteは、ユーザーが認証されているかを確認し、認証済みならDashboardPageを表示、
            未認証の場合はログインページ等にリダイレクトする処理を行います。 */ }
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <DashboardPage/>
            </ProtectedRoute>
          }
        />
        <Route path="/leave" element={
          <ProtectedRoute>
            <LeavePage/>
          </ProtectedRoute>
        }/>

        {/* 定義されていないその他のパスにアクセスされた場合、"/login"にリダイレクトします。 */ }
        <Route path="*" element={ <Navigate to="/login"/> }/>

        <Route path="/admin/leaves" element={
          <ProtectedRoute>
            <LeaveApprovalPage/>
          </ProtectedRoute>
        }/>
        <Route path="/approvals" element={ <ProtectedRoute><LeaveApprovalPage/></ProtectedRoute> }/>

      </Routes>
    </BrowserRouter>
  );
}

// Appコンポーネントをエクスポートすることで、他のモジュールからこのルーティング設定にアクセスできるよう
export default App;