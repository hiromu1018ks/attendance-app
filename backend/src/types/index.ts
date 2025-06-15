import { User, Department, Position, Role } from "@prisma/client";

// Prismaクライアントから必要な型をインポートします
// User: ユーザー情報の型
// Department: 部署情報の型
// Position: 役職情報の型
// Role: 権限情報の型

// 認証関連の型定義
// ログインリクエストの型定義
// employeeNumber: 社員番号（文字列型）
// password: パスワード（文字列型）
export interface LoginRequest {
  employeeNumber: string;
  password: string;
}

// ログインレスポンスの型定義
// token: JWTトークン（文字列型）
// user: ユーザープロフィール情報（UserProfile型）
// message: レスポンスメッセージ（文字列型）
export interface LoginResponse {
  token: string;
  user: UserProfile;
  message: string;
}

// パスワード変更リクエストの型定義
// currentPassword: 現在のパスワード（文字列型）
// newPassword: 新しいパスワード（文字列型）
export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

// ユーザープロフィールの型定義
// id: ユーザーID（文字列型）
// employeeNumber: 社員番号（文字列型）
// email: メールアドレス（文字列型）
// name: 氏名（文字列型）
// nameKana: 氏名カナ（文字列型）
// department: 部署情報（オブジェクト型）
//   - id: 部署ID（文字列型）
//   - name: 部署名（文字列型）
//   - level: 部署レベル（数値型）
// position: 役職情報（オブジェクト型、オプショナル）
//   - id: 役職ID（文字列型）
//   - name: 役職名（文字列型）
//   - level: 役職レベル（数値型）
// roles: 権限リスト（文字列配列型）
// isPasswordTemporary: 一時パスワードかどうか（真偽値型）
// lastLoginAt: 最終ログイン日時（日付型、オプショナル）
export interface UserProfile {
  id: string;
  employeeNumber: string;
  email: string;
  name: string;
  nameKana: string;
  department: {
    id: string;
    name: string;
    level: number;
  };
  position?: {
    id: string;
    name: string;
    level: number;
  };
  roles: string[];
  isPasswordTemporary: boolean;
  lastLoginAt?: Date;
}

// JWTペイロードの型定義
// userId: ユーザーID（文字列型）
// employeeNumber: 社員番号（文字列型）
// email: メールアドレス（文字列型）
// roles: 権限リスト（配列型）
// iat: トークン発行時刻（数値型、オプショナル）
// exp: トークン有効期限（数値型、オプショナル）
export interface JwtPayload {
  userId: string;
  employeeNumber: string;
  email: string;
  roles: string[];
  iat?: number;
  exp?: number;
}

// Expressのリクエスト型の拡張
// グローバル名前空間のExpressに型定義を追加
// Requestインターフェースにuserプロパティを追加（UserProfile型、オプショナル）
declare global {
  namespace Express {
    interface Request {
      user?: UserProfile;
    }
  }
}

// APIレスポンスの共通型定義
// success: 処理成功フラグ（真偽値型）
// data: レスポンスデータ（ジェネリック型T、オプショナル）
// error: エラーメッセージ（文字列型、オプショナル）
// message: 成功メッセージ（文字列型、オプショナル）
export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}
