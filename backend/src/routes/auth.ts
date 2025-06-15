// ================= 認証関連のルート =================
// このファイルは、ユーザー認証に関するルートを定義します。
// ログイン、ログアウト、ユーザープロファイル取得などのAPIを提供します。
// ====================================================

import express from "express";
import Joi from "joi";
import { ApiResponse } from "@/types";
import { getUserProfile, loginUser } from "@/services/authService";
import { logger } from "@/utils/logger";
import { authenticateToken } from "@/middleware/auth";

const router = express.Router();

// ログイン時の入力値検証スキーマ
// 社員番号とパスワードの必須チェックとバリデーション
const loginSchema = Joi.object({
  employeeNumber: Joi.string().required().messages({
    "string.empty": "職員番号は必須です",
    "any.required": "職員番号は必須です",
  }),
  password: Joi.string().required().messages({
    "string.empty": "パスワードは必須です",
    "any.required": "パスワードは必須です",
  }),
});

/**
 * POST /api/auth/login
 * ユーザーログイン
 *
 * リクエストボディ:
 * - employeeNumber: 社員番号
 * - password: パスワード
 *
 * レスポンス:
 * - 成功時: 200 OK
 *   - token: JWTトークン
 *   - user: ユーザー情報
 *   - message: 成功メッセージ
 * - 失敗時: 400 Bad Request（バリデーションエラー）または401 Unauthorized（認証エラー）
 *   - error: エラーメッセージ
 *   - message: 詳細なエラー内容
 */
router.post("/login", async (req, res: express.Response<ApiResponse>) => {
  try {
    // 入力値の検証
    const { error, value } = loginSchema.validate(req.body);

    if (error) {
      return res.status(400).json({
        success: false,
        error: "入力値が正しくありません",
        message: error.details.map((detail) => detail.message).join(","),
      });
    }

    // クライアント情報を取得
    // IPアドレスとUser-Agentを取得してセキュリティ監査に使用
    const ipAddress = req.ip || req.socket.remoteAddress || "unknown";
    const userAgent = req.get("User-Agent");

    // ログイン処理を実行
    const result = await loginUser(value, ipAddress, userAgent);

    // 成功レスポンスを返却
    res.json({
      success: true,
      data: result,
      message: result.message,
    });
  } catch (error) {
    // エラーログを記録
    logger.error("Login endpoint error", {
      error: error instanceof Error ? error.message : "Unknown error",
      ip: req.ip,
    });

    // エラーレスポンスを返却
    res.status(401).json({
      success: false,
      error: error instanceof Error ? error.message : "ログインに失敗しました",
    });
  }
});

/**
 * POST /api/auth/logout
 * ユーザーログアウト
 *
 * 認証済みユーザーのログアウト処理を行います。
 * クライアント側でJWTトークンを破棄する必要があります。
 *
 * 認証:
 * - JWTトークンが必要（authenticateTokenミドルウェアで検証）
 *
 * レスポンス:
 * - 成功時: 200 OK
 *   - message: ログアウト成功メッセージ
 * - 失敗時: 500 Internal Server Error
 *   - error: エラーメッセージ
 */
router.post(
  "/logout",
  authenticateToken,
  (req: any, res: express.Response<ApiResponse>) => {
    try {
      // ログアウト情報をログに記録
      logger.info("User logout", {
        userId: req.user?.id,
        employeeNumber: req.user?.employeeNumber,
      });

      // 成功レスポンスを返却
      res.json({
        success: true,
        message: "ログアウトしました",
      });
    } catch (error) {
      // エラーログを記録
      logger.error("Logout endpoint error", {
        error: error instanceof Error ? error.message : "Unknown error",
      });

      // エラーレスポンスを返却
      res.status(500).json({
        success: false,
        error: "ログアウト処理に失敗しました",
      });
    }
  }
);

/**
 * GET /api/auth/me
 * 現在のユーザー情報取得
 *
 * 認証済みユーザーのプロファイル情報を取得します。
 *
 * 認証:
 * - JWTトークンが必要（authenticateTokenミドルウェアで検証）
 *
 * レスポンス:
 * - 成功時: 200 OK
 *   - data: ユーザープロファイル情報
 *   - message: 成功メッセージ
 * - 失敗時: 401 Unauthorized（未認証）または500 Internal Server Error
 *   - error: エラーメッセージ
 */
router.get(
  "/me",
  authenticateToken,
  async (req: any, res: express.Response<ApiResponse>) => {
    try {
      // ユーザー情報の存在確認
      if (!req.user) {
        return res.status(401).json({
          success: false,
          error: "ユーザー情報が見つかりません",
        });
      }
      // 最新のユーザー情報を取得
      const userProfile = await getUserProfile(req.user.id);

      // 成功レスポンスを返却
      res.json({
        success: true,
        data: userProfile,
        message: "ユーザー情報を取得しました",
      });
    } catch (error) {
      // エラーログを記録
      logger.error("Get user profile endpoint error", {
        error: error instanceof Error ? error.message : "Unknown error",
        userId: req.user?.id,
      });

      // エラーレスポンスを返却
      res.status(500).json({
        success: false,
        error: "ユーザー情報の取得に失敗しました",
      });
    }
  }
);

export default router;
