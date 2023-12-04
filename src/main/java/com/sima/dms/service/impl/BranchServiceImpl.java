package com.sima.dms.service.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.sima.dms.domain.dto.BranchDto;
import com.sima.dms.domain.dto.response.AssignedBranchDto;
import com.sima.dms.domain.dto.response.BranchListDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.QBranch;
import com.sima.dms.domain.entity.QProfile;
import com.sima.dms.domain.enums.BranchTypeEnum;
import com.sima.dms.domain.enums.RoleEnum;
import com.sima.dms.repository.BranchRepository;
import com.sima.dms.repository.NodeBaseRepository;
import com.sima.dms.repository.ProfileRepository;
import com.sima.dms.service.BranchService;
import com.sima.dms.service.FolderService;
import com.sima.dms.service.GenericCacheHandler;
import com.sima.dms.service.mapper.BranchMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.utils.Responses.notFound;
import static com.sima.dms.utils.Responses.unauthorized;


@Service
//@Transactional
@AllArgsConstructor
public class BranchServiceImpl implements BranchService {

    @PersistenceContext
    private EntityManager entityManager;
    private final BranchMapper branchMapper;
    private final BranchRepository branchRepository;
    private final ProfileRepository profileRepository;
    private final NodeBaseRepository nodeBaseRepository;
    private final FolderService folderService;
    private final GenericCacheHandler genericCacheHandler;

    private final Logger log = LoggerFactory.getLogger(BranchServiceImpl.class);

    @Override
    public BranchDto save(BranchDto branchDto) {
        log.debug("Request to save branch : {}", branchDto);
        Branch branch = branchRepository.save(branchMapper.toEntity(branchDto));
        return branchMapper.toDto(branch);
    }

    @Override
    public BranchDto update(BranchDto branchDto) {
        log.debug("Request to update branch : {}", branchDto);
        return branchMapper.toDto(branchRepository.save(branchMapper.toEntity(branchDto)));
    }

    @Override
    public BranchDto findOne(Long id) {
        log.debug("Request to find one branch by id : {}", id);
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> notFound("branch not found"));
        return branchMapper.toDto(branch);
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<BranchDto> getAll(Pageable pageable) {
        log.debug("Request to get all branches");
        return branchRepository.findAllByActiveIsTrue(pageable)
                .map(branchMapper::toDto);
    }


    @Override
//    @Transactional(readOnly = true)
    public List<BranchListDto> getBranchList() {
        log.debug("Request to get all branches");
        return genericCacheHandler.getBranchListDto();
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete branch : {}", id);
        branchRepository.deleteById(id);
    }

    @Override
    public List<BranchDto> getProfileBranches(Long profileId) {

        log.debug("Request to get user base in branch  id : {}" , profileId);

        JPAQuery<?> query = new JPAQuery<Void>(entityManager);

        QProfile profile = QProfile.profile;
        QBranch branch = QBranch.branch;

        List<Branch> branches = query
                .select(branch)
                .from(branch)
                .join(branch.assignedProfiles, profile)
                .where(profile.id.eq(profileId))
                .fetch();

        return branchMapper.toDto(branches);
    }

    @Override
    public void deleteUsers(Long branchId, List<Long> userIds) {
        log.debug("Request to delete users from branch : {}" , branchId , userIds);
        branchRepository.deleteUsers(branchId, userIds);
    }

    @Override
    public Long getSequence() {
        log.debug("Request to get sequence from branch");
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        Long branchId = profile.getBranch().getId();
        return branchRepository.getSequence(branchId);
    }

    @Override
    public List<AssignedBranchDto> getAssignedBranch() {
        log.debug("Request to get assigned branch");
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        if (profile.getRole().equals(RoleEnum.DOPU) || profile.getRole().equals(RoleEnum.DOEU)) {
            return profileRepository.getAssignedBranches(profile.getId());
        } else throw unauthorized("you have not permission to this operation.");
    }

    @Override
    public List<BranchListDto> getAllByType(BranchTypeEnum type) {
        log.debug("Request to get all branch by type : {}" , type);
        return branchRepository.getAllByType(type);
    }

    public void setPathInBranch() {
        List<String> uuids = nodeBaseRepository.getChildrenUuid();
        List<String> folderPaths = folderService.getFolderPath(uuids);
        List<Long> branchCodes = branchRepository.getBranchCodes();
        branchCodes.forEach(code -> {
            folderPaths.forEach(path -> {
                if (path.substring(path.lastIndexOf("-")).contains(code.toString())) {
                    branchRepository.setPath(path, code);
                }
            });
        });
    }

    @Override
    public Page<BranchDto> getBranchWithProfile(Long branchId, Long profileId, Pageable pageable) {
        log.debug("Request to get Branch With Profile :  {}" , branchId , pageable);
        return branchRepository.getBranchWithProfile(branchId, profileId, pageable).map(branchMapper::toDto);
    }
}
