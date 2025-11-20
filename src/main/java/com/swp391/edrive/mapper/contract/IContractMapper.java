package com.swp391.edrive.mapper.contract;

import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.entity.Contract;

public interface IContractMapper {
    ContractResponse toResponse(Contract entity);
    ContractFileResponse toResponseFile(Contract entity);
}
