package com.sima.dms.repository;

import com.sima.dms.domain.entity.baseinformation.RequestReasonValidation;
import com.sima.dms.domain.enums.FieldNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestReasonValidationRepository extends JpaRepository<RequestReasonValidation,Long> {

    @Query(value = " select rrv.fieldName from RequestReasonValidation rrv where rrv.requestReason.id = :requestReasonId and rrv.required = true")
    List<FieldNameEnum> getFieldNameByRequestReasonId(@Param("requestReasonId") Long requestReasonId);

}
