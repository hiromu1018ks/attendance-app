export type LeaveApplicationResponse = {
  id : number;
  startDate : string;
  endDate : string;
  partDayType : string;
  startTime? : string;
  endTime? : string;
  type : string;
  reason : string;
  status : string;
  approverComment? : string;
  employeeName? : string;
}