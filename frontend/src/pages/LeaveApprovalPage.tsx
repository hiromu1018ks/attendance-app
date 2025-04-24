import { LeaveApprovalList } from "@/components/leave/LeaveApprovalList";

const LeaveApprovalPage = () => {
  return (
    <div className="max-w-2xl mx-auto mt-8 space-y-6">
      <h1 className="text-2xl font-bold">休暇申請の承認画面</h1>
      <LeaveApprovalList/>
    </div>
  );
};

export default LeaveApprovalPage;
