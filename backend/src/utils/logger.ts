// ==================ログ設定==================
// このファイルは、アプリケーション全体で使用するログの設定を管理します。
// ログは、アプリケーションの動作状況や問題が発生した際のデバッグに重要な役割を果たします。
// ===========================================

// 必要なパッケージの読み込み
// path: ファイルパスを操作するためのNode.jsの標準モジュール
// winston: Node.jsで最も人気のあるログ管理ライブラリ
import path from "path";
import winston from "winston";

// ログレベルの設定
// ログレベルは、どの程度の詳細さでログを記録するかを決定します。
const logLevel =
  // 環境変数LOG_LEVELが設定されている場合はその値を使用
  process.env.LOG_LEVEL ||
  // 環境変数が設定されていない場合：
  // - 本番環境（production）では "info" レベル
  // - 開発環境では "debug" レベルを使用
  (process.env.NODE_ENV === "production" ? "info" : "debug");

// Winston ロガーの設定
// createLogger関数で新しいロガーインスタンスを作成します
export const logger = winston.createLogger({
  // ログレベルを設定
  level: logLevel,
  // ログのフォーマット設定
  format: winston.format.combine(
    // タイムスタンプを追加（YYYY-MM-DD HH:mm:ss形式）
    winston.format.timestamp({ format: "YYYY-MM-DD HH:mm:ss" }),
    // エラーが発生した場合、スタックトレースも記録
    winston.format.errors({ stack: true }),
    // ログをJSON形式で出力
    winston.format.json()
  ),
  // ログの出力先（トランスポート）を設定
  transports: [
    // 1. コンソールへの出力設定
    new winston.transports.Console({
      format: winston.format.combine(
        // ログレベルに応じて色付け
        winston.format.colorize(),
        // ログの表示形式をカスタマイズ
        winston.format.printf(({ timestamp, level, message, ...meta }) => {
          return `${timestamp} [${level}]: ${message} ${
            Object.keys(meta).length ? JSON.stringify(meta, null, 2) : ""
          }`;
        })
      ),
    }),
    // 2. エラーログファイルの設定
    new winston.transports.File({
      // エラーログの保存先ファイルパス
      filename: path.join(process.env.LOG_FILE_PATH || "./logs", "error.log"),
      // エラーレベルのログのみを記録
      level: "error",
      // ファイルサイズの上限（5MB）
      maxsize: 5242880,
      // 保持するファイル数（5個）
      maxFiles: 5,
    }),
    // 3. 全ログファイルの設定
    new winston.transports.File({
      // 全ログの保存先ファイルパス
      filename: path.join(
        process.env.LOG_FILE_PATH || "./logs",
        "combined.log"
      ),
      // ファイルサイズの上限（5MB）
      maxsize: 5242880,
      // 保持するファイル数（5個）
      maxFiles: 5,
    }),
  ],
});

// 監査ログ関数
// 重要な操作（例：ユーザーのログイン、データの変更など）を記録するための関数
export const logAudit = (action: string, details: any) => {
  // 監査ログをinfoレベルで記録
  logger.info("AUDIT", {
    // 実行されたアクション
    action,
    // アクションの詳細情報
    details,
    // 実行時刻
    timestamp: new Date().toISOString(),
  });
};
