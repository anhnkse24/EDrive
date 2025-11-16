package com.swp391.edrive.mapper.contract;

import com.swp391.edrive.dto.response.ContractFileResponse;
import com.swp391.edrive.dto.response.ContractResponse;
import com.swp391.edrive.dto.response.CustomerContractResponse;
import com.swp391.edrive.dto.response.ManufacturerContractResponse;
import com.swp391.edrive.entity.Contract;

public interface IContractMapper {
    @Deprecated
    ContractResponse toResponse(Contract entity);

    ManufacturerContractResponse toManufacturerContractResponse(Contract entity);

    CustomerContractResponse toCustomerContractResponse(Contract entity);

    ContractFileResponse toResponseFile(Contract entity);
}
