import { PrismaClient } from "@prisma/client";

// Prisma Clientのインスタンスを作成
export const prisma = new PrismaClient({
  // 開発環境ではクエリログとエラーを出力し、それ以外ではエラーのみ
  log: process.env.NODE_ENV == "development" ? ["query", "error"] : ["error"],
});

// データベース接続の確認
export async function connectDatabase(): Promise<void> {
  try {
    await prisma.$connect();
    console.log("✅ データベースに接続しました");
  } catch (error) {
    console.error("❌ データベース接続に失敗しました", error);
    process.exit(1);
  }
}

// データベース接続の切断
export const disconnectDatabase = async (): Promise<void> => {
  await prisma.$disconnect();
  console.log("📴 データベース接続を切断しました");
};
