package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentFileProcessingErrorException;
import com.sprint.mission.discodeit.exception.user.DuplicateEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicateUsernameException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicUserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserStatusRepository userStatusRepository;
    @Mock private BinaryContentRepository binaryContentRepository;
    @Mock private BinaryContentStorage binaryContentStorage;

    @Mock private UserMapper userMapper;
    @Mock private BinaryContentMapper binaryContentMapper;

    @InjectMocks private BasicUserService basicUserService;

    /*
        사용자 등록 테스트
     */

    // [성공]
    @Test
    @DisplayName("회원가입 성공")
    public void create_user_success() throws IOException {
        // given | 테스트 준비
        UserCreateRequest request = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        MockMultipartFile profile = new MockMultipartFile(
                "profile",
                "test.png",
                "image/png",
                "test".getBytes()
        );

        // 가짜 객체
        UserEntity user = new UserEntity(
                request.username(),
                request.email(),
                request.password()
        );
        UserStatusEntity userStatusEntity = new UserStatusEntity(user);
        BinaryContentEntity profileImage = new BinaryContentEntity(
                profile.getOriginalFilename(),
                profile.getSize(),
                profile.getContentType()
        );

        // 중복 검사 통과
        given(userRepository.existsByUsername(user.getUsername())).willReturn(false);
        given(userRepository.existsByEmail(user.getEmail())).willReturn(false);

        // UserRepository에 User Entity 저장
        // userMapper 및 userRepository가 해당 함수를 호출하면, 미리 만든 객체 user를 반환
        given(userMapper.toEntity(any(UserCreateRequest.class))).willReturn(user);
        given(userRepository.save(any(UserEntity.class))).willReturn(user);

        given(binaryContentRepository.save(any(BinaryContentEntity.class))).willReturn(profileImage);

        // 가짜 응답 DTO
        BinaryContentDto expectedBinaryContentDto = BinaryContentDto.builder()
                .id(UUID.randomUUID())
                .fileName(profileImage.getFileName())
                .contentType(profileImage.getContentType())
                .size(profileImage.getSize())
                .bytes(profile.getBytes())
                .build();
        UserDto expectedDto = UserDto.builder()
                .id(UUID.randomUUID())
                .username(user.getUsername())
                .email(user.getEmail())
                .profile(expectedBinaryContentDto)
                .online(true)
                .build();
        // User 엔티티 -> 응답 DTO 변환
        given(userMapper.toDto(any(UserEntity.class))).willReturn(expectedDto);

        // when | 테스트 실행
        UserDto result = basicUserService.create(request, profile);

        // then | 테스트 검증
        assertEquals(user.getUsername(), result.username());
        assertEquals(user.getEmail(), result.email());
        assertEquals(profile.getOriginalFilename(), user.getProfile().getFileName());
        assertNotNull(user.getUserStatus());
        // 저장 함수가 한 번씩 호출됐는지 확인
        verify(userStatusRepository, times(1)).save(any(UserStatusEntity.class));
        verify(binaryContentRepository, times(1)).save(any(BinaryContentEntity.class));
        verify(binaryContentStorage, times(1)).put(any(), any());
    }

    // [실패] 이메일 중복
    @Test
    @DisplayName("회원가입 실패 : 이미 존재하는 이메일일 경우, DuplicateEmailException 발생")
    public void create_user_failure_duplicate_email() {
        // given
        UserCreateRequest request = new UserCreateRequest("yushi", "yushi@wish.com", "yushi1234");

        // 이메일 중복
        given(userRepository.existsByEmail(request.email())).willReturn(true);

        // when
        assertThrows(DuplicateEmailException.class, () -> {
            basicUserService.create(request, null);
        });

        // then
        // User 미등록 동작 확인
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // [실패] 사용자 이름 중복
    @Test
    @DisplayName("회원가입 실패 : 이미 존재하는 사용자 이름일 경우, DuplicateUsernameException 예외 발생")
    public void create_user_failure_duplicate_username() {
        // given
        UserCreateRequest request = new UserCreateRequest("yushi", "yushi@wish.com", "yushi1234");
        given(userRepository.existsByUsername(request.username())).willReturn(true);

        // when
        assertThrows(DuplicateUsernameException.class, () -> {
            basicUserService.create(request, null);
        });

        // then
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    // [실패] 프로필 이미지 생성 실패
    @Test
    @DisplayName("회원가입 실패: 프로필 파일 생성에 실패할 경우, BinaryContentFileProcessingErrorException 발생")
    public void create_user_fail_profile_io_exception() throws IOException {

    }

    /*
        사용자 수정 테스트
     */

    // [성공]

    // [실패] 기존 이메일과 동일

    // [실패] 다른 사용자의 이메일과 중복

    // [실패] 기존 사용자 이름과 동일

    // [실패] 다른 사용자의 이름과 중복

    // [실패] 프로필 이미지 생성 실패

    /*
        사용자 삭제
     */
    // [성공]

    // [삭제0]
}
