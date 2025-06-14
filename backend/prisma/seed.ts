// 必要なパッケージをインポート
// PrismaClient: データベース操作のためのクライアント
// bcrypt: パスワードのハッシュ化を行うためのライブラリ
import { PrismaClient } from "@prisma/client";
import * as bcrypt from "bcryptjs";

// Prismaクライアントのインスタンスを作成
// このインスタンスを使ってデータベースの操作を行います
const prisma = new PrismaClient();

// メインのシード処理を行う関数
// この関数内で初期データを作成します
async function main() {
  console.log("Start seeding..."); // シード処理の開始をログ出力

  // == 部署データの作成 ==
  // 総務課のデータを作成
  const generalAffairs = await prisma.department.create({
    data: {
      code: "GA", // 部署コード
      name: "総務課", // 部署名
      isActive: true, // 有効フラグ
    },
  });

  // 企画課のデータを作成
  const planning = await prisma.department.create({
    data: {
      code: "PL", // 部署コード
      name: "企画課", // 部署名
      isActive: true, // 有効フラグ
    },
  });

  console.log("department created!"); // 部署データ作成完了のログ出力

  // == 役職データの作成 ==
  // 課長のデータを作成
  const maneger = await prisma.position.create({
    data: {
      code: "MGR", // 役職コード
      name: "課長", // 役職名
      level: 3, // 役職レベル（高いほど上位）
      isActive: true, // 有効フラグ
    },
  });

  // 職員のデータを作成
  const staff = await prisma.position.create({
    data: {
      code: "STAFF", // 役職コード
      name: "職員", // 役職名
      level: 1, // 役職レベル
      isActive: true, // 有効フラグ
    },
  });

  console.log("position created!"); // 役職データ作成完了のログ出力

  // == ロール（権限）データの作成 ==
  // システム管理者ロールの作成
  const adminRole = await prisma.role.create({
    data: {
      name: "admin", // ロール名
      description: "システム管理者", // ロールの説明
      permissions: {
        // 権限の設定（JSON形式）
        canManageUsers: true, // ユーザー管理権限
        canManageSystem: true, // システム管理権限
        canViewAllAttendance: true, // 全員の勤怠閲覧権限
        canApproveAll: true, // 全員の勤怠承認権限
      },
    },
  });

  // 管理者ロールの作成
  const managerRole = await prisma.role.create({
    data: {
      name: "manager", // ロール名
      description: "管理者", // ロールの説明
      permissions: {
        // 権限の設定
        canManageUsers: false, // ユーザー管理権限なし
        canManageSystem: false, // システム管理権限なし
        canViewDepartmentAttendance: true, // 部署内の勤怠閲覧権限
        canApproveAttendance: true, // 勤怠承認権限
      },
    },
  });

  // 一般職員ロールの作成
  const employeeRole = await prisma.role.create({
    data: {
      name: "employee", // ロール名
      description: "一般職員", // ロールの説明
      permissions: {
        // 権限の設定
        canManageUsers: false, // ユーザー管理権限なし
        canManageSystem: false, // システム管理権限なし
        canViewOwnAttendance: true, // 自分の勤怠閲覧権限
        canRequestCorrection: true, // 勤怠修正申請権限
      },
    },
  });

  console.log("role created!"); // ロールデータ作成完了のログ出力

  // パスワードのハッシュ化
  // bcryptを使用して、平文のパスワードを安全なハッシュ値に変換
  const hashedPassword = await bcrypt.hash("password123", 12);

  // == ユーザーデータの作成 ==
  // 管理者ユーザーの作成
  const adminUser = await prisma.user.create({
    data: {
      employeeNumber: "0001", // 社員番号
      email: "admin@example.com", // メールアドレス
      hashedPassword, // ハッシュ化されたパスワード
      name: "管理者", // 氏名
      nameKana: "カンリシャ", // 氏名（カナ）
      departmentId: generalAffairs.id, // 所属部署ID
      positionId: maneger.id, // 役職ID
      hireDate: new Date("2020-04-01"), // 入社日
      isPasswordTemporary: false, // 仮パスワードフラグ
    },
  });

  // 一般ユーザーの作成
  const generalUser = await prisma.user.create({
    data: {
      employeeNumber: "1001", // 社員番号
      email: "tanaka@yakushima.lg.jp", // メールアドレス
      hashedPassword, // ハッシュ化されたパスワード
      name: "田中太郎", // 氏名
      nameKana: "タナカタロウ", // 氏名（カナ）
      departmentId: generalAffairs.id, // 所属部署ID
      positionId: staff.id, // 役職ID
      hireDate: new Date("2021-04-01"), // 入社日
      isPasswordTemporary: false, // 仮パスワードフラグ
    },
  });

  console.log("user created!"); // ユーザーデータ作成完了のログ出力

  // == ユーザーロールの関連付け ==
  // 管理者ユーザーに管理者ロールを割り当て
  await prisma.userRole.create({
    data: {
      userId: adminUser.id, // ユーザーID
      roleId: adminRole.id, // ロールID
    },
  });

  // 一般ユーザーに一般職員ロールを割り当て
  await prisma.userRole.create({
    data: {
      userId: generalUser.id, // ユーザーID
      roleId: employeeRole.id, // ロールID
    },
  });

  console.log("ユーザーロールを設定しました"); // ユーザーロール設定完了のログ出力
  console.log("シードデータの作成が完了しました"); // 全処理完了のログ出力

  // テスト用のユーザー情報を表示
  console.log("\nテストユーザー情報:");
  console.log("管理者: 職員番号=0001, パスワード=password123");
  console.log("一般職員: 職員番号=1001, パスワード=password123");
}

// メイン処理の実行
main()
  .catch((e) => {
    // エラーが発生した場合の処理
    console.error(e); // エラー内容をログ出力
    process.exit(1); // プロセスを終了（エラーコード1）
  })
  .finally(async () => {
    // 処理完了後の後処理
    await prisma.$disconnect(); // データベース接続を切断
  });
