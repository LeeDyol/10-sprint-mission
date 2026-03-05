//package com.sprint.mission.discodeit.repository.file;
//
//import com.sprint.mission.discodeit.entity.UserEntity;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.util.FileUtil;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Repository;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Repository
//@ConditionalOnProperty(
//        name = "discodeit.repository.type" ,
//        havingValue = "file"
//)
//public class FileUserRepository implements UserRepository {
//    private final Path directory;      // 경로 설정
//
//    public FileUserRepository(@Value("${discodeit.repository.file-directory}") String baseDirectory) {
//        this.directory = Paths.get(System.getProperty("user.dir"), baseDirectory, "user");
//        FileUtil.init(directory);
//    }
//
//    // 사용자 저장
//    @Override
//    public void save(UserEntity user) {
//        Path filePath = Paths.get(directory.toString(), user.getId() + ".ser");
//        FileUtil.save(filePath, user);
//    }
//
//    // 사용자 단건 조회 (사용자 id)
//    @Override
//    public Optional<UserEntity> findById(UUID userId) {
//        UserEntity targetUser = FileUtil.loadSingle(directory.resolve(userId + ".ser"));
//
//        return Optional.ofNullable(targetUser);
//    }
//
//    // 사용자 단건 조회 (사용자 이름)
//    @Override
//    public Optional<UserEntity> findByUsername(String username) {
//        List<UserEntity> users = findAll();
//
//        return users.stream()
//                .filter(user -> user.getUsername().equals(username))
//                .findFirst();
//    }
//
//    // 사용자 전체 조회
//    @Override
//    public List<UserEntity> findAll() {
//        return FileUtil.load(directory);
//    }
//
//    // 사용자 삭제
//    @Override
//    public void delete(UserEntity user) {
//        try {
//            Files.deleteIfExists(directory.resolve(user.getId() + ".ser"));
//        } catch (IOException e) {
//            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
//        }
//    }
//
//    // 유효성 검증 (사용자 존재 여부)
//    @Override
//    public boolean existsById(UUID userId) {
//        return findAll().stream()
//                .anyMatch(user -> user.getId().equals(userId));
//    }
//
//    // 유효성 검증 (이메일 중복)
//    @Override
//    public boolean existsByEmail(String email) {
//        return findAll().stream()
//                .anyMatch(user -> user.getEmail().equals(email));
//    }
//
//    // 유효성 검증 (이름 중복)
//    @Override
//    public boolean existsByUsername(String username) {
//        return findAll().stream()
//                .anyMatch(user -> user.getUsername().equals(username));
//    }
//}
