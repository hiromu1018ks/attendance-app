import { useEffect, useState } from "react";
import api from "@/lib/api.ts";
import { Input } from "@/components/ui/input.tsx";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group.tsx";
import { Textarea } from "@/components/ui/textarea.tsx";
import { Button } from "@/components/ui/button.tsx";

export const LeaveForm = () => {
  const [ startDate, setStartDate ] = useState("");
  const [ endDate, setEndDate ] = useState("");
  const [ startTime, setStartTime ] = useState("");
  const [ endTime, setEndTime ] = useState("");
  const [ partDayType, setPartDayType ] = useState("FULL");
  const [ type, setType ] = useState("年次休暇");
  const [ reason, setReason ] = useState("");
  const [ message, setMessage ] = useState("");
  const [ balance, setBalance ] = useState<null | {
    usedMinutes : number,
    remainingMinutes : number,
    limitMinutes : number,
  }>(null);

  useEffect(() => {
    const fetchBalance = async () => {
      try {
        const res = await api.get("/api/leaves/balance");
        setBalance(res.data);
      } catch {
        console.error("休暇残数の取得に失敗しました")
      }
    }
    fetchBalance();
  }, [])

  const handleSubmit = async () => {
    try {
      const payload : any = {
        startDate,
        endDate,
        partDayType,
        type,
        reason,
      };

      if ( partDayType === "TIME" ) {
        payload.startTime = startTime;
        payload.endTime = endTime;
      }

      await api.post("/api/leaves", payload);
      setMessage("申請を送信しました")
    } catch {
      setMessage("申請に失敗しました")
    }
  };

  return (
    <div className="space-y-4">
      { balance && (
        <div className="text-sm text-muted-foreground border p-3 rounded-md mb-4">
          年間休暇上限：{ balance.limitMinutes } 分 /
          使用済み：{ balance.usedMinutes } 分 /
          残り：{ balance.remainingMinutes } 分
        </div>
      ) }
      <div>
        <Label>開始日</Label>
        <Input type="date" value={ startDate } onChange={ e => setStartDate(e.target.value) }/>
      </div>

      <div>
        <Label>終了日</Label>
        <Input type="date" value={ endDate } onChange={ e => setEndDate(e.target.value) }/>
      </div>

      <div>
        <Label>取得区分</Label>
        <RadioGroup defaultValue="FULL" onValueChange={ setPartDayType } className="flex gap-4">
          <div>
            <RadioGroupItem value="FULL" id="full"/>
            <Label htmlFor="full">全日</Label>
          </div>
          <div>
            <RadioGroupItem value="AM" id="am"/>
            <Label htmlFor="am">午前</Label>
          </div>
          <div>
            <RadioGroupItem value="PM" id="pm"/>
            <Label htmlFor="pm">午後</Label>
          </div>
          <div>
            <RadioGroupItem value="TIME" id="time"/>
            <Label htmlFor="time">時間指定</Label>
          </div>
        </RadioGroup>
      </div>

      { partDayType === "TIME" && (
        <div className="flex gap-4">
          <div className="flex-1">
            <Label>開始時刻</Label>
            <Input type="time" value={ startTime } onChange={ e => setStartTime(e.target.value) }/>
          </div>
          <div className="flex-1">
            <Label>終了時刻</Label>
            <Input type="time" value={ endTime } onChange={ e => setEndTime(e.target.value) }/>
          </div>
        </div>
      ) }

      <div>
        <Label>休暇の種類</Label>
        <Input value={ type } onChange={ e => setType(e.target.value) }/>
      </div>

      <div>
        <Label>理由</Label>
        <Textarea value={ reason } onChange={ e => setReason(e.target.value) }/>
      </div>

      { message && <p className="text-sm text-muted-foreground">{ message }</p> }

      <Button className="w-full" onClick={ handleSubmit }>
        申請する
      </Button>
    </div>
  );
};