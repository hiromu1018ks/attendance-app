// ================= 認証関連のサービス =================
// このファイルは、ユーザー認証に関するビジネスロジックを提供します。
// ログイン処理、ユーザープロファイル取得などの機能を実装しています。
// ====================================================

import { prisma } from "@/config/database";
import { JwtPayload, LoginRequest, LoginResponse, UserProfile } from "@/types";
import {
  checkLoginAttempts,
  generateToken,
  recordLoginFailure,
  recordLoginSuccess,
  verifyPassword,
} from "@/utils/auth";
import { logger } from "@/utils/logger";

/**
 * ユーザーログイン処理
 * 社員番号とパスワードを使用してユーザーを認証し、JWTトークンを生成します。
 *
 * @param loginData - ログイン情報（社員番号とパスワード）
 * @param ipAddress - ログイン元のIPアドレス
 * @param userAgent - ブラウザ情報（オプション）
 * @returns ログイン成功時のレスポンス（トークン、ユーザー情報、メッセージ）
 * @throws 認証失敗時はエラーをスロー
 *
 * この関数は以下の処理を行います：
 * 1. ログイン試行回数のチェック
 * 2. ユーザー情報の取得と検証
 * 3. パスワードの検証
 * 4. JWTトークンの生成
 * 5. 最終ログイン日時の更新
 * 6. ログイン成功の記録
 */
export async function loginUser(
  loginData: LoginRequest,
  ipAddress: string,
  userAgent?: string
): Promise<LoginResponse> {
  const { employeeNumber, password } = loginData;

  logger.info("Login attempt started!", { employeeNumber, password });

  try {
    // ログイン試行回数をチェック
    const loginAttempts = await checkLoginAttempts(employeeNumber);
    if (loginAttempts.isLocked) {
      logger.warn("Login attempt blocked: Account locked", {
        employeeNumber,
        ipAddress,
        lockExpiresAt: loginAttempts.lockExpiresAt,
      });

      throw new Error(
        `アカウントがロックされています。${loginAttempts.lockExpiresAt?.toLocaleString(
          "ja-Jp"
        )}以降に再試行してください`
      );
    }

    // ユーザー情報をデータベースから取得
    const user = await prisma.user.findUnique({
      where: {
        employeeNumber,
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
      await recordLoginFailure(employeeNumber, ipAddress, userAgent);

      logger.warn("Login failed: User not found", {
        employeeNumber,
        ipAddress,
      });

      throw new Error("職員番号またはパスワードが正しくありません");
    }

    // パスワードを検証
    const isPasswordValid = await verifyPassword(password, user.hashedPassword);

    if (!isPasswordValid) {
      await recordLoginFailure(employeeNumber, ipAddress, userAgent);

      logger.warn("Login failed: Invalid password", {
        userId: user.id,
        employeeNumber,
        ipAddress,
      });

      throw new Error("職員番号またはパスワードが正しくありません");
    }

    // ロール情報を取得
    const roles = user.userRoles.map((ur) => ur.role.name);

    // JWTペイロード作成
    const jwtPayload: Omit<JwtPayload, "iat" | "exp"> = {
      userId: user.id,
      employeeNumber: user.employeeNumber,
      email: user.email,
      roles,
    };

    // トークンを生成
    const token = generateToken(jwtPayload);

    // 最終ログイン日時を更新
    await prisma.user.update({
      where: {
        id: user.id,
      },
      data: {
        lastLoginAt: new Date(),
      },
    });

    // ログイン成功を記録
    await recordLoginSuccess(user.id, employeeNumber, ipAddress, userAgent);

    // ユーザープロファイルを作成
    const userProfile: UserProfile = {
      id: user.id,
      employeeNumber: user.employeeNumber,
      email: user.email,
      name: user.name,
      nameKana: user.nameKana,
      department: user.department,
      position: user.position || undefined,
      roles,
      isPasswordTemporary: user.isPasswordTemporary,
      lastLoginAt: user.lastLoginAt || undefined,
    };

    logger.info("User login successful", {
      userId: user.id,
      employeeNumber,
      ipAddress,
      isPasswordTemporary: user.isPasswordTemporary,
    });

    // 仮パスワードの場合は警告メッセージ
    const message = user.isPasswordTemporary
      ? "ログインに成功しました。セキュリティのためパスワードを変更してください。"
      : "ログインに成功しました。";

    return {
      token,
      user: userProfile,
      message,
    };
  } catch (error) {
    const errorMessage =
      error instanceof Error ? error.message : "ログインに失敗しました。";

    logger.error("Login service error", {
      employeeNumber,
      ipAddress,
      error: errorMessage,
    });

    throw new Error(errorMessage);
  }
}

/**
 * ユーザープロファイル取得
 * 指定されたユーザーIDのプロファイル情報を取得します。
 *
 * @param userId - 取得対象のユーザーID
 * @returns ユーザープロファイル情報
 * @throws ユーザーが見つからない場合はエラーをスロー
 *
 * この関数は以下の処理を行います：
 * 1. ユーザー情報の取得（部署、役職、ロール情報を含む）
 * 2. ユーザープロファイルの構築
 * 3. エラーハンドリングとログ記録
 */
export async function getUserProfile(userId: string): Promise<UserProfile> {
  try {
    const user = await prisma.user.findUnique({
      where: {
        id: userId,
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
      throw new Error("ユーザーが見つかりません");
    }

    const roles = user.userRoles.map((ur) => ur.role.name);

    const userProfile: UserProfile = {
      id: user.id,
      employeeNumber: user.employeeNumber,
      email: user.email,
      name: user.name,
      nameKana: user.nameKana,
      department: user.department,
      position: user.position || undefined,
      roles,
      isPasswordTemporary: user.isPasswordTemporary,
      lastLoginAt: user.lastLoginAt || undefined,
    };
    return userProfile;
  } catch (error) {
    const errorMessage =
      error instanceof Error
        ? error.message
        : "ユーザープロファイル取得に失敗しました";

    logger.error("Get user profile service error", {
      userId,
      error: errorMessage,
    });

    throw new Error(errorMessage);
  }
}
