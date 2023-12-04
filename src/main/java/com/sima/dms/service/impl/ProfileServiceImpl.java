package com.sima.dms.service.impl;

import com.sima.dms.domain.dto.ProfileDto;
import com.sima.dms.domain.dto.response.BadgeResponseDto;
import com.sima.dms.domain.dto.response.ProfileSearchResponseDto;
import com.sima.dms.domain.dto.request.AdvanceProfileSearchDto;
import com.sima.dms.domain.dto.response.PersonnelResponseDto;
import com.sima.dms.domain.entity.Branch;
import com.sima.dms.domain.entity.Profile;
import com.sima.dms.domain.entity.User;
import com.sima.dms.domain.enums.*;
import com.sima.dms.repository.*;
import com.sima.dms.service.ProfileService;
import com.sima.dms.service.mapper.ProfileMapper;
import com.sima.dms.service.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.sima.dms.domain.entity.session.Authorized.currentUser;
import static com.sima.dms.domain.entity.session.Authorized.currentUserId;
import static com.sima.dms.domain.enums.DocumentRequestStateEnum.*;
import static com.sima.dms.domain.enums.DocumentSetStateEnum.*;
import static com.sima.dms.domain.enums.DocumentSetStateEnum.REGISTERED;
import static com.sima.dms.domain.enums.RoleEnum.BA;
import static com.sima.dms.domain.enums.RoleEnum.BU;
import static com.sima.dms.utils.Responses.*;

@Service
//@Transactional
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserMapper userMapper;
    private final ProfileMapper profileMapper;

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final ProfileRepository profileRepository;

    private final DocumentSetRepository documentSetRepository;
    private final DocumentRepository documentRepository;
    private final DocumentRequestRepository documentRequestRepository;
    private final OtherDocumentRepository otherDocumentRepository;

    private final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    @Override
    public ProfileDto create(PersonnelResponseDto personnelResponseDto) {
        if (personnelResponseDto != null) {
            boolean existUser = userRepository.existsByPersonelUserName(personnelResponseDto.getPersonelUserName());
            ProfileDto profileDto = new ProfileDto(personnelResponseDto);
            Profile profile;
            if (existUser) {
                profile = profileRepository.findByUser_PersonelUserNameIgnoreCaseAndActiveIsTrue(personnelResponseDto.getPersonelUserName());
                if (profile == null) {
                    profileDto.setUser(null);
                    profileDto.setRole(personnelResponseDto.getRole());
                    profileDto.setBranchId(personnelResponseDto.getBranchId());
                    profile = profileMapper.toEntity(profileDto);
                    profile.setUser(userMapper.fromId(userRepository.getUserId(personnelResponseDto.getPersonelUserName())));
                    profile = profileRepository.save(profile);
                }
            } else {
                profileDto.setRole(personnelResponseDto.getRole());
                profileDto.setBranchId(personnelResponseDto.getBranchId());
                profile = profileRepository.save(profileMapper.toEntity(profileDto));
            }
            return profileMapper.toDto(profile);
        } else
            throw forbidden("personnel username can not be null");
    }

    @Transactional
    public ProfileDto update(PersonnelResponseDto updateProfileDto) {
        log.debug("Request to update profile : {}", updateProfileDto);
        Profile profile = profileRepository.findById(updateProfileDto.getProfileId()).orElseThrow(() -> notFound("profile not found"));
        User user = profile.getUser();
        if (updateProfileDto.getPersonelUserName() != null && !updateProfileDto.getPersonelUserName().isEmpty())
            user.setPersonelUserName(updateProfileDto.getPersonelUserName());
        if (updateProfileDto.getFirstName() != null && !updateProfileDto.getFirstName().isEmpty())
            user.setFirstName(updateProfileDto.getFirstName());
        if (updateProfileDto.getLastName() != null && !updateProfileDto.getLastName().isEmpty())
            user.setLastName(updateProfileDto.getLastName());
        if (updateProfileDto.getFullName() != null && !updateProfileDto.getFullName().isEmpty())
            user.setFullName(updateProfileDto.getFullName());
        if (updateProfileDto.getNationalKey() != null && !updateProfileDto.getNationalKey().isEmpty())
            user.setNationalKey(updateProfileDto.getNationalKey());
        if (updateProfileDto.getPrsnCode() != null)
            user.setPersonCode(updateProfileDto.getPrsnCode());
        if (!profile.getRole().equals(updateProfileDto.getRole()) || !profile.getBranch().getId().equals(updateProfileDto.getBranchId())) {
            profile.setActive(false);
            profileRepository.save(profile);

            Profile newProfile = profile.clone();
            newProfile.setActive(true);
            if (updateProfileDto.getRole() != null) {
                newProfile.setRole(updateProfileDto.getRole());
            }
            if (updateProfileDto.getBranchId() != null) {
                Branch branch = branchRepository.findById(updateProfileDto.getBranchId()).orElseThrow(() -> notFound("branch not found"));
                newProfile.setBranch(branch);
                newProfile.setBranchName(branch.getBranchName());
                newProfile.setBranchCode(branch.getBranchCode());
            }
            profileRepository.save(newProfile);
            return profileMapper.toDto(newProfile);
        } else {
            profileRepository.save(profile);
            return profileMapper.toDto(profile);
        }
    }

    @Override
    public ProfileDto findOne(String personelUsername) {
        return profileMapper.toDto(profileRepository.findByUser_PersonelUserNameIgnoreCaseAndActiveIsTrue(personelUsername));
    }

    @Override
    public ProfileDto findById(Long profileId) {
        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> notFound("profile not found"));
        if (!profileRepository.getRole(currentUserId()).equals(RoleEnum.ADMIN) && !profile.getId().equals(currentUserId()))
            throw unauthorized("you have not permission to this operation.");
        return profileMapper.toDto(profile);
    }

    @Override
    public Profile findByCurrentId(Long id) {
        return profileRepository.findById(id).orElseThrow(() -> notFound("profile not found"));
    }

    @Override
//    @Transactional(readOnly = true)
    public Page<ProfileDto> findAll(Pageable pageable) {
        return profileRepository.findAll(pageable)
                .map(profileMapper::toDto);
    }

    @Override
    public List<ProfileDto> findAllByRole(List<RoleEnum> roles) {
        return profileMapper.toDto(profileRepository.findAllByRoleInAndActiveIsTrue(roles));
    }

    @Override
    public String getBranchCode(String personelUserName) {
        return profileRepository.getBranchCode(personelUserName).orElseThrow(() -> notFound("Branch not found"));
    }

    @Override
    public List<ProfileDto> assignBranchToUsers(Long branchId, List<String> usernames) {

        List<Profile> profiles = profileRepository.findAllByUser_PersonelUserNameIn(usernames);
        if (profiles != null && !profiles.isEmpty()) {
            branchRepository.findById(branchId).ifPresent(branch -> {
                profiles.forEach(profile -> profile.setBranch(branch));
            });
            return profileMapper.toDto(profileRepository.saveAll(profiles));
        } else throw notFound("user is not document office member");
    }

    @Override
    public Profile checkProfile(PersonnelResponseDto personnelResponseDto) {

        if (personnelResponseDto != null) {

            boolean existUser = userRepository.existsByPersonelUserName(personnelResponseDto.getPersonelUserName());
            ProfileDto profileDto = new ProfileDto(personnelResponseDto);
            Profile profile = new Profile();
            if (existUser) {

                boolean existProfile = profileRepository.existsByUser_PersonelUserNameIgnoreCaseAndActiveIsTrue(personnelResponseDto.getPersonelUserName());

                if (!existProfile) {

                    profileDto.setUser(null);
                    profileDto.setRole(RoleEnum.BU);
                    profileDto.setBranchId(branchRepository.findByBranchCode(personnelResponseDto.getAbrnchcod()));
                    profile = profileMapper.toEntity(profileDto);
                    profile.setUser(userMapper.fromId(userRepository.getUserId(personnelResponseDto.getPersonelUserName())));
                    profile = profileRepository.save(profile);
                }
//                else if (existProfile) {
//                    Profile profile = profileRepository.findByUser_PersonelUserNameAndActiveIsTrue(requestDto.getPersonelUserName());
//                    if (profile != null) {
//                        if (!profile.getBranchCode().equals(requestDto.getBranchCode()) || !profile.getJob().equals(requestDto.getJob())) {
//                            profile.setActive(false);
//                            profileRepository.save(profile);
//                            profileDto.setRole(roleService.getRole(requestDto.getJob()));
//                            profileDto.setBranchId(branchRepository.findByBranchCode(requestDto.getBranchCode()));
//                            Profile newProfile = profileMapper.toEntity(profileDto);
//                            newProfile.setUser(profile.getUser());
//                            profileRepository.save(newProfile);
//                        }
//                    }
//                }
            } else {
                profileDto.setRole(RoleEnum.BU);
                profileDto.setBranchId(branchRepository.findByBranchCode(personnelResponseDto.getAbrnchcod()));
                profile = profileRepository.save(profileMapper.toEntity(profileDto));
            }
            return profile;
        } else
            throw forbidden("personnel username can not be null");
    }

    @Override
    public Page<ProfileSearchResponseDto> advanceSearch(AdvanceProfileSearchDto searchDto, Pageable pageable) {
        log.debug("Request to search profile : {}");
        return profileRepository.advanceSearch(
                searchDto.getFullName(),
                searchDto.getNationalKey(),
                searchDto.getPersonCode(),
                searchDto.getPersonelUserName(),
                searchDto.getJob(),
                searchDto.getBranchId(),
                searchDto.getRole(),
                pageable
        );
    }

    @Override
    public BadgeResponseDto getBadge() {
        Profile profile = profileRepository.findById(currentUser().getId()).orElseThrow(() -> notFound("profile not found"));
        Long documentSetByBranchCount = 0L;
        Long acceptWaitingCount = 0L;
        Long fixedConflictedDocumentsCount = 0L;
        Long conflictingCount = 0L;
        Long sentConflictedDocumentCount = 0L;
        Long documentRequestCount = 0L;
        Long otherDocumentCount = 0L ;
        if (profile.getRole().equals(BA)) {
            List<Long> branchIds = branchRepository.getAllByParentId(profile.getBranch().getId());
            documentSetByBranchCount = documentSetRepository.countByState_NameInAndBranch_idIn(Collections.singletonList(DocumentSetStateEnum.REJECTED), branchIds);
            acceptWaitingCount = documentSetRepository.countByState_NameInAndBranch_idIn(Arrays.asList(REGISTERED, FIX_CONFLICT), branchIds);
            fixedConflictedDocumentsCount = documentRepository.countByState_NameAndDocumentSet_Branch_IdIn(DocumentStateEnum.FIX_CONFLICT, branchIds);
            conflictingCount = documentSetRepository.countByState_NameInAndBranch_idIn(Collections.singletonList(CONFLICTING),branchIds);
            sentConflictedDocumentCount = documentRepository.countByState_NameAndDocumentSet_Branch_IdIn(DocumentStateEnum.SENT_CONFLICT, branchIds);
            documentRequestCount = documentRequestRepository.countByLastState_StateInAndBranch_idIn(Arrays.asList(DocumentRequestStateEnum.REGISTERED,SENT_DOCUMENT_REQUESTED,DOCUMENT_OFFICE_REJECTED),branchIds);
            otherDocumentCount = otherDocumentRepository.countByLastState_StateInAndBranch_IdIn(Collections.singletonList(OtherDocumentStateEnum.SENT),branchIds);
        } else if (profile.getRole().equals(BU)) {
            documentSetByBranchCount = documentSetRepository.countByState_NameInAndBranch_idIn(Collections.singletonList(DocumentSetStateEnum.REJECTED), Collections.singletonList(profile.getBranch().getId()));
            conflictingCount = documentSetRepository.countByState_NameInAndBranch_idIn(Collections.singletonList(CONFLICTING), Collections.singletonList(profile.getBranch().getId()));
            sentConflictedDocumentCount = documentRepository.countByState_NameAndDocumentSet_Branch_IdIn(DocumentStateEnum.SENT_CONFLICT, Collections.singletonList(profile.getBranch().getId()));
            documentRequestCount = documentRequestRepository.countByLastState_StateInAndBranch_idIn(Arrays.asList(SENT_DOCUMENT_REQUESTED,BRANCH_REJECTED,DOCUMENT_OFFICE_REJECTED),Collections.singletonList(profile.getBranch().getId()));
            otherDocumentCount = otherDocumentRepository.countByLastState_StateInAndBranch_IdIn(Arrays.asList(OtherDocumentStateEnum.BRANCH_REJECTED,OtherDocumentStateEnum.BRANCH_CONFIRMED),Collections.singletonList(profile.getBranch().getId()));

        }

        return new BadgeResponseDto(documentSetByBranchCount, acceptWaitingCount, fixedConflictedDocumentsCount, conflictingCount, sentConflictedDocumentCount, documentRequestCount,otherDocumentCount);

    }
}
