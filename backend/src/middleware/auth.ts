// ===================== 認証ミドルウェア =====================
// このファイルは、APIリクエストの認証を処理するミドルウェアを提供します。
// JWTトークンの検証とユーザー情報の取得を行い、認証されたリクエストのみを通過させます。
// ==========================================================

import { prisma } from "@/config/database";
import { UserProfile } from "@/types";
import { verifyToken } from "@/utils/auth";
import { logger } from "@/utils/logger";
import { Request, Response, NextFunction } from "express";

/**
 * JWT 認証ミドルウェア
 * リクエストヘッダーからJWTトークンを取得し、検証します。
 * トークンが有効な場合、ユーザー情報を取得してリクエストオブジェクトに付与します。
 * 認証に失敗した場合は401エラーを返します。
 */
export const authenticateToken = async (
  req: Request,
  res: Response,
  next: NextFunction
): Promise<void> => {
  try {
    // Authorizationヘッダーからトークンを取得
    // Bearer認証スキームを使用（"Bearer <token>"形式）
    const authHeader = req.headers.authorization;
    const token = authHeader && authHeader.split(" ")[1];

    if (!token) {
      res.status(401).json({
        success: false,
        error: "認証トークンが必要です",
      });
      return;
    }

    // JWTトークンを検証し、ペイロードを取得
    const payload = verifyToken(token);

    // データベースからユーザー情報を取得
    // アクティブなユーザーのみを対象とし、関連する部署・役職・ロール情報も取得
    const user = await prisma.user.findUnique({
      where: {
        id: payload.userId,
        isActive: true,
      },
      include: {
        department: {
          select: {
            id: true,
            name: true,
            code: true,
          },
        },
        position: {
          select: {
            id: true,
            name: true,
            level: true,
          },
        },
        userRoles: {
          include: {
            role: {
              select: {
                name: true,
              },
            },
          },
        },
      },
    });

    if (!user) {
      res.status(401).json({
        success: false,
        error: "ユーザーが見つかりません",
      });
      return;
    }

    // ユーザープロファイル情報を構築
    const userProfile: UserProfile = {
      id: user.id,
      employeeNumber: user.employeeNumber,
      email: user.email,
      name: user.name,
      nameKana: user.nameKana,
      department: user.department,
      position: user.position || undefined,
      roles: user.userRoles.map((ur) => ur.role.name),
      isPasswordTemporary: user.isPasswordTemporary,
      lastLoginAt: user.lastLoginAt || undefined,
    };

    // ユーザー情報をリクエストオブジェクトに付与し、次のミドルウェアに処理を委譲
    req.user = userProfile;
    next();
  } catch (error) {
    // 認証処理中にエラーが発生した場合のエラーハンドリング
    logger.error("Authentication middleware error", {
      error: error instanceof Error ? error.message : "Unknown error",
      path: req.path,
      method: req.method,
    });

    res.status(401).json({
      success: false,
      error: "認証に失敗しました",
    });
  }
};

/**
 * ロールベース認可ミドルウェア
 * 指定されたロールを持つユーザーのみがアクセスできるように制限します。
 *
 * @param requireRoles - アクセスに必要なロールの配列
 * @returns 認可チェックを行うミドルウェア関数
 *
 * このミドルウェアは以下の処理を行います：
 * 1. ユーザーが認証されているかチェック
 * 2. ユーザーが指定されたロールのいずれかを持っているかチェック
 * 3. 認可に失敗した場合は403エラーを返す
 */
export const requireRoles = (requireRoles: string[]) => {
  return (req: Request, res: Response, next: NextFunction): void => {
    // ユーザーが認証されているかチェック
    if (!req.user) {
      res.status(401).json({
        success: false,
        error: "認証が必要です",
      });
      return;
    }

    // ユーザーが指定されたロールのいずれかを持っているかチェック
    const hasRequiredRole = requireRoles.some((role) =>
      req.user!.roles.includes(role)
    );

    // 認可に失敗した場合は403エラーを返す
    if (!hasRequiredRole) {
      res.status(403).json({
        success: false,
        error: "このリソースへのアクセス権限がありません",
      });
      return;
    }

    // 認可成功時は次のミドルウェアに処理を委譲
    next();
  };
};
