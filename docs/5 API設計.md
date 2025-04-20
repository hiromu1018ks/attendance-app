# 勤怠管理アプリ API設計書

## 認証系

### POST /api/auth/login
- **概要**：ログイン認証を行う
- **リクエスト**：
  ```json
  {
    "employeeNumber": "12345",
    "password": "string"
  }
  ```
- **レスポンス**：
  ```json
  {
    "token": "JWT token"
  }
  ```

---

## ユーザー情報

### GET /api/users/me
- **概要**：ログイン中の職員情報を取得
- **認証**：必須（JWT）
- **レスポンス**：
  ```json
  {
    "id": 1,
    "employeeNumber": "12345",
    "name": "山田 太郎",
    "role": "課長",
    "department": "総務課"
  }
  ```

### GET /api/users
- **概要**：ユーザー一覧を取得（管理者用）
- **認証**：管理者権限

### POST /api/users
- **概要**：新規ユーザーを登録（管理者用）
- **ボディ**：氏名、職員番号、部署ID、役職ID など

---

## 出退勤打刻

### POST /api/attendance/clock-in
- **概要**：出勤打刻
- **レスポンス**：打刻時刻

### POST /api/attendance/clock-out
- **概要**：退勤打刻
- **レスポンス**：打刻時刻

### GET /api/attendance/daily
- **概要**：本日の出退勤情報を取得

### GET /api/attendance/monthly?year=2025&month=4
- **概要**：月次勤怠一覧を取得
- **レスポンス**：出勤日、出退勤時刻、補正率などの一覧

---

## 修正申請

### POST /api/corrections
- **概要**：打刻修正申請を送信
- **ボディ**：
  ```json
  {
    "attendanceId": 100,
    "type": "clockOut",
    "correctedTime": "2025-04-19T18:30:00",
    "reason": "早く押してしまったため"
  }
  ```

### GET /api/corrections
- **概要**：自分の修正申請履歴を取得

### PUT /api/corrections/{id}/approve
- **概要**：承認者による承認処理

### PUT /api/corrections/{id}/reject
- **概要**：承認者による却下処理（理由付き）

---

## 休暇申請

### POST /api/leaves
- **概要**：休暇申請を送信
- **ボディ**：
  ```json
  {
    "type": "年休",
    "startDate": "2025-04-25",
    "endDate": "2025-04-25",
    "partDayType": "終日",
    "reason": "私用のため"
  }
  ```

### GET /api/leaves
- **概要**：自分の申請一覧を取得

### PUT /api/leaves/{id}/approve
- **概要**：承認者による承認処理

### PUT /api/leaves/{id}/reject
- **概要**：承認者による却下処理（理由付き）

---

## 集計・管理

### GET /api/admin/attendance-summary?year=2025&month=4&department=総務課
- **概要**：部署・月単位での勤怠集計一覧
- **認証**：人事課権限

### GET /api/departments
- **概要**：部署一覧取得

### GET /api/roles
- **概要**：権限一覧取得

---

## 備考
- 各エンドポイントはJWTベースで認証保護されている
- 承認処理系（PUT）は承認者の権限チェックが必要
- 時刻データはすべてISO 8601形式（例：2025-04-19T08:30:00）

