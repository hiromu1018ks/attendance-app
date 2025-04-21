import { AttendanceTable } from "@/components/attendance/AttendanceTable";
import { ClockButtons } from "@/components/attendance/ClockButtons.tsx";

export default function DashboardPage() {
  return (
    <div className="max-w-3xl mx-auto mt-10 p-4 space-y-6">
      <h1 className="text-2xl font-bold">ダッシュボード</h1>
      <ClockButtons/>
      <AttendanceTable/>
    </div>
  );
}