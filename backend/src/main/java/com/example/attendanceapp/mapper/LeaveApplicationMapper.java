package com.example.attendanceapp.mapper;

import com.example.attendanceapp.dto.LeaveApplicationResponse;
import com.example.attendanceapp.model.LeaveApplication;
import org.mapstruct.Mapper;

/**
 * 休暇申請のエンティティとDTOの間のマッピングを行うインターフェース
 * MapStructフレームワークを使用して、エンティティとDTOの変換処理を自動生成します。
 */
@Mapper(componentModel = "spring")
public interface LeaveApplicationMapper {

    LeaveApplicationResponse toDto(LeaveApplication entity);

}