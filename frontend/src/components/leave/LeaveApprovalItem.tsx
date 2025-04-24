import { LeaveApplicationResponse } from "@/types/leave";
import { Button } from "@/components/ui/button";
import { useState } from "react";
import api from "@/lib/api";

type Props = {
  leave : LeaveApplicationResponse;
  onAction? : () => void; // 承認・却下後にリスト更新用
};

export const LeaveApprovalItem = ({ leave, onAction } : Props) => {
  const [ processing, setProcessing ] = useState(false);
  const [ message, setMessage ] = useState("");

  const handleApprove = async () => {
    try {
      setProcessing(true);
      await api.post(`/api/leaves/${ leave.id }/approve`, { comment : "承認します" });
      setMessage("承認しました");
      onAction?.();
    } catch {
      setMessage("承認に失敗しました");
    } finally {
      setProcessing(false);
    }
  };

  const handleReject = async () => {
    try {
      setProcessing(true);
      await api.post(`/api/leaves/${ leave.id }/reject`, { comment : "却下します" });
      setMessage("却下しました");
      onAction?.();
    } catch {
      setMessage("却下に失敗しました");
    } finally {
      setProcessing(false);
    }
  };

  return (
    <div className="border p-4 rounded-md space-y-2">
      <p><strong>氏名：</strong>{ leave.employeeName ?? "（非表示）" }</p>
      <p><strong>日付：</strong>{ leave.startDate } ～ { leave.endDate }</p>
      <p><strong>時間帯：</strong>{ leave.partDayType }</p>
      <p><strong>種類：</strong>{ leave.type }</p>
      <p><strong>理由：</strong>{ leave.reason }</p>
      <div className="flex gap-2">
        <Button variant="default" onClick={ handleApprove } disabled={ processing }>承認</Button>
        <Button variant="destructive" onClick={ handleReject } disabled={ processing }>却下</Button>
      </div>
      { message && <p className="text-sm text-muted-foreground">{ message }</p> }
    </div>
  );
};
