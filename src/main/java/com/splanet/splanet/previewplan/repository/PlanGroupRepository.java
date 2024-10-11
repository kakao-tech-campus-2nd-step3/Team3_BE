package com.splanet.splanet.previewplan.repository;

import com.splanet.splanet.previewplan.entity.PlanGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PlanGroupRepository extends CrudRepository<PlanGroup, String> {
    void addGroupToDeviceIndex(String indexKey, String groupKey);
    List<PlanGroup> findAllByDeviceId(String deviceId);

}
