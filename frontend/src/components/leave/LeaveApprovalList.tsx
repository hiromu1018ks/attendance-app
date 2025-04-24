import { useEffect, useState } from "react";
import api from "@/lib/api";
import { LeaveApplicationResponse } from "@/types/leave";
import { LeaveApprovalItem } from "./LeaveApprovalItem";

export const LeaveApprovalList = () => {
  const [ leaves, setLeaves ] = useState<LeaveApplicationResponse[]>([]);
  const [ error, setError ] = useState("");

  const fetchLeaves = async () => {
    try {
      const res = await api.get("/api/manager/leaves");
      setLeaves(res.data);
    } catch {
      setError("申請一覧の取得に失敗しました");
    }
  };

  useEffect(() => {
    fetchLeaves();
  }, []);

  if ( error ) return <p className="text-red-500">{ error }</p>;
  if ( leaves.length === 0 ) return <p>申請はありません。</p>;

  return (
    <div className="space-y-4">
      { leaves.map(leave => (
        <LeaveApprovalItem
          key={ leave.id }
          leave={ leave }
          onAction={ fetchLeaves }
        />
      )) }
    </div>
  );
};
