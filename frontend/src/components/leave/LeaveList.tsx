import { useEffect, useState } from "react";
import { fetchLeaveApplications } from "@/lib/api";
import { LeaveApplicationResponse } from "@/types/leave";
import { LeaveItem } from "./LeaveItem";

export const LeaveList = () => {
  const [ leaves, setLeaves ] = useState<LeaveApplicationResponse[]>([]);
  const [ error, setError ] = useState("");

  useEffect(() => {
    fetchLeaveApplications()
      .then(res => setLeaves(res.data))
      .catch(() => setError("申請一覧の取得に失敗しました"));
  }, []);

  if ( error ) return <p className="text-red-500">{ error }</p>;
  if ( leaves.length === 0 ) return <p>申請履歴はありません。</p>;

  return (
    <div className="space-y-2">
      { leaves.map(leave => (
        <LeaveItem key={ leave.id } leave={ leave }/>
      )) }
    </div>
  );
};
