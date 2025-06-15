// ================= 認証関連のユーティリティ =================
// このファイルは、アプリケーションの認証（ログインやパスワード管理など）に関する機能を提供します。
// ========================================================

// 必要なライブラリをインポートします
// JwtPayload: JWTトークンに含まれる情報の型定義
// bcrypt: パスワードのハッシュ化に使用するライブラリ
// jwt: JSON Web Tokenの生成と検証に使用するライブラリ
// logger: ログ出力用のユーティリティ
import { JwtPayload } from "@/types";
import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import { logger } from "./logger";

// 環境変数から設定値を取得します。値が設定されていない場合はデフォルト値を使用します
// JWT_SECRET: JWTトークンの暗号化に使用する秘密鍵
// JWT_EXPIRES_IN: JWTトークンの有効期限（デフォルトは24時間）
// SALT_ROUNDS: パスワードハッシュ化時のソルトの強度（デフォルトは12回）
const JWT_SECRET = process.env.JWT_SECRET || "fallback-secret-key";
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || "24h";
const SALT_ROUNDS = parseInt(process.env.SALT_ROUNDS || "12");

/**
 * パスワードをハッシュ化する関数
 * @param password - ハッシュ化するパスワード
 * @returns ハッシュ化されたパスワード
 *
 * この関数は以下の処理を行います：
 * 1. bcryptライブラリを使用してパスワードをハッシュ化
 * 2. 設定されたSALT_ROUNDSの回数だけハッシュ化を繰り返し、セキュリティを強化
 * 3. ハッシュ化されたパスワードを返却
 */
export const hashPassword = async (password: string): Promise<string> => {
  return bcrypt.hash(password, SALT_ROUNDS);
};

/**
 * パスワードが正しいかどうかを検証する関数
 * @param password - 検証するパスワード
 * @param hashedPassword - ハッシュ化された正しいパスワード
 * @returns パスワードが正しい場合はtrue、そうでない場合はfalse
 *
 * この関数は以下の処理を行います：
 * 1. bcrypt.compareを使用して、入力されたパスワードとハッシュ化されたパスワードを比較
 * 2. 比較結果を返却
 */
export const verifyPassword = async (
  password: string,
  hashedPassword: string
): Promise<boolean> => {
  return bcrypt.compare(password, hashedPassword);
};

/**
 * JWTトークンを生成する関数
 * @param payload - トークンに含める情報（ユーザーIDなど）
 * @returns 生成されたJWTトークン
 *
 * この関数は以下の処理を行います：
 * 1. jwt.signを使用して、指定された情報を含むトークンを生成
 * 2. トークンに有効期限を設定
 * 3. 生成されたトークンを返却
 */
export const generateToken = (
  payload: Omit<JwtPayload, "iat" | "exp">
): string => {
  return jwt.sign(payload, JWT_SECRET, {
    expiresIn: JWT_EXPIRES_IN,
  } as jwt.SignOptions);
};

/**
 * JWTトークンを検証する関数
 * @param token - 検証するJWTトークン
 * @returns トークンに含まれる情報
 * @throws トークンが無効な場合はエラーをスロー
 *
 * この関数は以下の処理を行います：
 * 1. jwt.verifyを使用してトークンの有効性を検証
 * 2. トークンが有効な場合は、含まれる情報を返却
 * 3. トークンが無効な場合は、エラーをログに記録して例外をスロー
 */
export const verifyToken = (token: string): JwtPayload => {
  try {
    return jwt.verify(token, JWT_SECRET) as JwtPayload;
  } catch (error) {
    logger.warn("Token verification failed", {
      error: error instanceof Error ? error.message : "Unknow error",
    });
    throw new Error("Invalid token");
  }
};

/**
 * パスワードポリシーを検証する関数
 * @param password - 検証するパスワード
 * @returns 検証結果（有効かどうかとエラーメッセージの配列）
 *
 * この関数は以下の処理を行います：
 * 1. パスワードの長さが8文字以上かチェック
 * 2. 大文字が含まれているかチェック
 * 3. 小文字が含まれているかチェック
 * 4. 数字が含まれているかチェック
 * 5. すべての条件を満たしているかどうかと、エラーメッセージを返却
 */
export const validatePassword = (
  password: string
): { isValid: boolean; errors: string[] } => {
  const errors: string[] = [];

  if (password.length < 8) {
    errors.push("パスワードは8文字以上である必要があります。");
  }

  if (!/[A-Z]/.test(password)) {
    errors.push("パスワードは大文字を含む必要があります。");
  }

  if (!/[a-z]/.test(password)) {
    errors.push("パスワードは小文字を含む必要があります。");
  }

  if (!/[0-9]/.test(password)) {
    errors.push("パスワードは数字を含む必要があります。");
  }

  return {
    isValid: errors.length === 0,
    errors,
  };
};

/**
 * セキュアなランダム文字列を生成する関数
 * @param length - 生成する文字列の長さ
 * @returns 生成されたランダム文字列
 *
 * この関数は以下の処理を行います：
 * 1. 使用可能な文字（英数字）を定義
 * 2. 指定された長さのランダムな文字列を生成
 * 3. 生成された文字列を返却
 */
export const generateSecureRandomString = (length: number): string => {
  const chars =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
  let result = "";

  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
};

/**
 * ログイン成功を記録する関数
 * @param userId - ユーザーID
 * @param employeeNumber - 社員番号
 * @param ipAddress - ログイン元のIPアドレス
 * @param userAgent - ブラウザ情報（オプション）
 *
 * この関数は以下の処理を行います：
 * 1. ログイン成功時の情報（ユーザーID、社員番号、IPアドレス、ブラウザ情報、タイムスタンプ）をログに記録
 */
export const recordLoginSuccess = async (
  userId: string,
  employeeNumber: string,
  ipAddress: string,
  userAgent?: string
) => {
  logger.info("LOGIN_SUCCESS", {
    userId,
    employeeNumber,
    ipAddress,
    userAgent,
    timeStamp: new Date().toISOString(),
  });
};

/**
 * ログイン失敗を記録する関数
 * セキュリティ監査のために、ログイン失敗の情報をログに記録します。
 *
 * @param employeeNumber - ログインを試みた社員番号
 * @param ipAddress - ログイン試行元のIPアドレス
 * @param userAgent - ブラウザ情報（オプション）
 *
 * この関数は以下の処理を行います：
 * 1. ログイン失敗時の情報（社員番号、IPアドレス、ブラウザ情報、タイムスタンプ）をログに記録
 * 2. セキュリティ監査や不正アクセス検知のために使用
 */
export const recordLoginFailure = async (
  employeeNumber: string,
  ipAddress: string,
  userAgent?: string
) => {
  logger.warn("LOGIN_FAILURE", {
    employeeNumber,
    ipAddress,
    userAgent,
    timestamp: new Date().toISOString(),
  });
};

/**
 * ログイン試行回数をチェックする関数
 * @param employeeNumber - 社員番号
 * @returns アカウントがロックされているかどうかと、ロック解除時刻
 *
 * この関数は以下の処理を行います：
 * 1. 現在は簡易実装で、常にロックされていない状態を返却
 * 2. 将来的にはRedisやデータベースを使用して、実際のログイン試行回数を管理する予定
 */
export const checkLoginAttempts = async (employeeNumber: string) => {
  // 簡易実装：実際にはRedisやデータベースで管理
  return {
    isLocked: false,
    lockExpiresAt: new Date(Date.now() + 1000 * 60 * 5),
  };
};
