import { LeaveForm } from "@/components/leave/LeaveForm.tsx";
import { LeaveList } from "@/components/leave/LeaveList.tsx";

const LeavePage = () => {
  return (

    <div className="max-w-xl mx-auto p-4">
      <h1 className="text-xl font-bold mb-4">休暇申請フォーム</h1>
      <LeaveForm/>
      <hr/>
      <h2 className="text-lg font-semibold">申請履歴</h2>
      <LeaveList/>
    </div>
  )
}

export default LeavePage;