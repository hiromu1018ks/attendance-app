import { LoginForm } from "@/components/forms/LoginForm.tsx";

export default function LoginPage() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <div className="w-full max-w-md bg-white shadow-md p-6 rounded-lg space-y-4">
        <h1 className="text-2xl font-bold text-center">勤怠管理システム</h1>
        <LoginForm/>
      </div>
    </div>
  )
}