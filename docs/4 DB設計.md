# 勤怠管理アプリ データベース設計書

## テーブル一覧

| テーブル名               | 説明                             |
|--------------------------|----------------------------------|
| users                   | 職員アカウント情報               |
| departments             | 所属部署情報                     |
| roles                   | 権限・役職情報                   |
| attendance_records      | 出退勤の打刻記録                 |
| leave_applications      | 休暇申請の情報                   |
| correction_requests     | 打刻修正申請の履歴               |

---

## users テーブル

| カラム名           | 型         | 制約             | 説明                 |
|--------------------|------------|------------------|----------------------|
| id                 | BIGINT     | PK, AUTO         | ユーザーID           |
| employee_number    | VARCHAR    | UNIQUE, NOT NULL | 職員番号             |
| name               | VARCHAR    | NOT NULL         | 氏名                 |
| password_hash      | VARCHAR    | NOT NULL         | パスワード（ハッシュ）|
| role_id            | BIGINT     | FK → roles       | 権限ID               |
| department_id      | BIGINT     | FK → departments | 所属部署ID           |
| is_active          | BOOLEAN    | DEFAULT TRUE     | 在籍状態             |
| retired_at         | TIMESTAMP  | NULLABLE         | 退職日               |
| created_at         | TIMESTAMP  | DEFAULT now()    | 登録日時             |
| updated_at         | TIMESTAMP  | DEFAULT now()    | 更新日時             |

---

## departments テーブル

| カラム名     | 型       | 制約             | 説明         |
|--------------|----------|------------------|--------------|
| id           | BIGINT   | PK, AUTO         | 部署ID       |
| name         | VARCHAR  | UNIQUE, NOT NULL | 部署名       |
| created_at   | TIMESTAMP| DEFAULT now()    | 登録日時     |

---

## roles テーブル

| カラム名     | 型       | 制約             | 説明               |
|--------------|----------|------------------|--------------------|
| id           | BIGINT   | PK, AUTO         | 権限ID             |
| name         | VARCHAR  | UNIQUE, NOT NULL | 権限名（例：一般職員、課長、人事課） |

---

## attendance_records テーブル

| カラム名       | 型        | 制約               | 説明               |
|----------------|-----------|--------------------|--------------------|
| id             | BIGINT    | PK, AUTO           | 勤怠記録ID         |
| user_id        | BIGINT    | FK → users         | 対象職員           |
| date           | DATE      | NOT NULL           | 勤務日             |
| clock_in       | TIMESTAMP | NULLABLE           | 出勤時刻           |
| clock_out      | TIMESTAMP | NULLABLE           | 退勤時刻           |
| is_corrected   | BOOLEAN   | DEFAULT FALSE      | 修正済みフラグ     |
| correction_rate| DECIMAL   | NULLABLE           | 補正率（例：1.25） |
| created_at     | TIMESTAMP | DEFAULT now()      | 登録日時           |

---

## leave_applications テーブル

| カラム名       | 型        | 制約               | 説明               |
|----------------|-----------|--------------------|--------------------|
| id             | BIGINT    | PK, AUTO           | 申請ID             |
| user_id        | BIGINT    | FK → users         | 申請者             |
| type           | VARCHAR   | NOT NULL           | 種別（年休、代休など）|
| start_date     | DATE      | NOT NULL           | 開始日             |
| end_date       | DATE      | NOT NULL           | 終了日             |
| part_day_type  | VARCHAR   | NULLABLE           | 午前／午後／終日     |
| reason         | TEXT      | NULLABLE           | 申請理由           |
| status         | VARCHAR   | DEFAULT '申請中'   | ステータス         |
| approver_id    | BIGINT    | FK → users         | 承認者（課長）      |
| approved_at    | TIMESTAMP | NULLABLE           | 承認日時           |
| approver_comment | TEXT    | NULLABLE           | 承認者コメント（任意）|
| created_at     | TIMESTAMP | DEFAULT now()      | 申請日時           |

---

## correction_requests テーブル

| カラム名        | 型        | 制約               | 説明                   |
|-----------------|-----------|--------------------|------------------------|
| id              | BIGINT    | PK, AUTO           | 修正申請ID             |
| attendance_id   | BIGINT    | FK → attendance_records | 対象勤怠記録       |
| user_id         | BIGINT    | FK → users         | 申請者                 |
| original_time   | TIMESTAMP | NOT NULL           | 元の打刻時刻           |
| corrected_time  | TIMESTAMP | NOT NULL           | 修正後の打刻時刻       |
| type            | VARCHAR   | NOT NULL           | 修正区分（出勤／退勤） |
| reason          | TEXT      | NOT NULL           | 修正理由               |
| status          | VARCHAR   | DEFAULT '申請中'   | 承認ステータス         |
| approver_id     | BIGINT    | FK → users         | 承認者（課長）          |
| approved_at     | TIMESTAMP | NULLABLE           | 承認日時               |
| approver_comment| TEXT      | NULLABLE           | 承認者コメント（任意） |
| created_at      | TIMESTAMP | DEFAULT now()      | 申請日時               |