package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@SpringBootTest(properties = "discodeit.storage.type=s3")
class S3BinaryContentStorageTest {
    // .env 파일 주입
    static {
        try (FileInputStream fis = new FileInputStream(".env")) {
            // .env 파일 내용을 저장할 설정 객체 생성
            Properties props = new Properties();

            // 설정 객체 내 .env 파일 짝지어 저장
            props.load(fis);
            props.forEach(
                    (key, value) -> System.setProperty(key.toString(), value.toString())
            );
            System.out.println("[SUCCESS] Successfully loaded .env file");
        } catch (Exception e) {
            fail("[FAIL] Not able to load .env file");
        }
    }

    @Autowired
    private BinaryContentStorage s3BinaryContentStorage;

    /*
        통합 테스트
     */
    @Test
    @DisplayName("S3 업로드, 다운로드, Presigned URL 리다이렉트 통합 테스트")
    void testS3FullCycle() throws Exception {
        // given
        UUID testId = UUID.randomUUID();
        String testContent = "하늘이 날 반기고 세상은 아름다워";
        byte[] testData = testContent.getBytes(StandardCharsets.UTF_8);

        // 가짜 객체 |
        BinaryContentDto testDto = BinaryContentDto.builder()
                .id(testId)
                .fileName("S3Test.txt")
                .size((long) testData.length)
                .contentType("text/plain")
                .bytes(testData)
                .build();

        // when | 업로드
        UUID uploadedId = s3BinaryContentStorage.put(testId, testData);

        // then | 업로드
        assertEquals(testId, uploadedId, "Different uploaded ID");
        System.out.println("Successfully uploaded file (Key: " + uploadedId + ")");

        // when | InputStream 변환
        try (InputStream inputStream = s3BinaryContentStorage.get(testId)) {
            // InputStream -> 한글 변환
            String downloadedContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // then | InputStream 변환
            assertEquals(testContent, downloadedContent, "Different downloaded content");
            System.out.println("Successfully downloaded file (내용: " + downloadedContent + ")");
        }

        // when | 다운로드
        ResponseEntity<?> response = s3BinaryContentStorage.download(testDto);

        // then | 다운로드
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        String locationUrl = Objects.requireNonNull(response.getHeaders().getLocation()).toString();
        assertTrue(locationUrl.contains("X-Amz-Algorithm"));
        System.out.println("Successfully created redirect URL (URL: " + locationUrl + ")");
    }
}