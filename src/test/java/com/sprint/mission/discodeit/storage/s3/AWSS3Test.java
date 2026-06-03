package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSS3Test {

    private S3Client s3Client;                      // S3 요청 객체
    private S3Presigner s3Presigner;                // AWS URL 발급 객체
    private String bucketName;

    // S3 저장 경로 (폴더 + 파일)
    private final String TEST_FILE_KEY = "test/discodeit.txt";

    /*
        테스트 준비
     */
    @BeforeAll
    void setUp() {
        // 1. .env 파일을 읽기 위한 Properties 객체 생성
        Properties props = new Properties();

        // 2. Properties에 .env 파일 내용 업로드
        try (FileInputStream fis = new FileInputStream(".env")) {
            props.load(fis);
            System.out.println("[SUCCESS] Successfully loaded .env file");
        } catch (Exception e) {
            fail("[FAIL] Not able to load .env file");
        }

        String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        String regionStr = props.getProperty("AWS_S3_REGION");
        bucketName = props.getProperty("AWS_S3_BUCKET");

        // 3. AWS 출입증 생성 (AccessKey + SecretKey)
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // 4. S3 요청 객체 생성
        s3Client = S3Client.builder()
                .region(Region.of(regionStr))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        // 5. URL 발급 객체 생성
        s3Presigner = S3Presigner.builder()
                .region(Region.of(regionStr))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    /*
        S3 파일 업로드 테스트
     */
    @Test
    @Order(1)
    @DisplayName("S3 파일 업로드 테스트: 성공 시, 성공 출력문 출력")
    void testUpload() {
        // given

        // 업로드 요청 객체
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(TEST_FILE_KEY)
                .build();

        // 저장할 텍스트 파일
        String content = "하늘이 날 반기고 세상은 아름다워";

        // when & then
        assertDoesNotThrow(() -> {
            s3Client.putObject(putObjectRequest, RequestBody.fromString(content));
        });
        System.out.println("Successfully uploaded Test");
    }

    /*
        S3 파일 다운로드 테스트
     */
    @Test
    @Order(2)
    @DisplayName("S3 파일 다운로드 테스트: 성공 시, 다운로드 한 텍스트 파일 내용 출력")
    void testDownload() {
        // given

        // 다운로드 요청 객체
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(TEST_FILE_KEY)
                .build();

        // when
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);

        // Byte -> 한글 변환
        String data = new String(objectBytes.asByteArray(), StandardCharsets.UTF_8);

        // then
        System.out.println("Downloaded Data: " + data);
        assertTrue(data.contains("하늘이 날 반기고 세상은 아름다워"));
    }

    /*
        S3 Presigned URL 생성 테스트
     */
    @Test
    @Order(3)
    @DisplayName("S3 Presigned URL 생성 테스트: 성공 시, 생성한 URL 주소 출력")
    void testGeneratePresignedUrl() {
        // given

        // 다운로드 요청 객체
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(TEST_FILE_KEY)
                .build();

        // 다운로드 요청 객체 -> URL 변환 요청 객체 변환
        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))                  // 유효시간 10분
                .getObjectRequest(getObjectRequest)
                .build();

        // when

        // URL 객체
        PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
        String presignedUrl = presignedGetObjectRequest.url().toString();

        // then
        System.out.println("Created Presigned URL: ");
        System.out.println(presignedUrl);

        assertNotNull(presignedUrl);
        assertTrue(presignedUrl.contains("X-Amz-Algorithm"));
    }
}
