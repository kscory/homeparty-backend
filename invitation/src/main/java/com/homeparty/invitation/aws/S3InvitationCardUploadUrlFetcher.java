package com.homeparty.invitation.aws;

import com.homeparty.invitation.domain.models.InvitationCardUploadUrlFetcher;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;


// https://github.com/aws/aws-sdk-java-v2/issues/1493
// https://github.com/aws/aws-sdk-java-v2/blob/master/core/auth/src/main/java/software/amazon/awssdk/auth/signer/internal/BaseAws4Signer.java
// 위를 보면 아직 presignedPost 구현이 되어 있지 않음
@RequiredArgsConstructor
public class S3InvitationCardUploadUrlFetcher implements InvitationCardUploadUrlFetcher {

    private final S3Presigner presigner;
    private final AwsConfig awsConfig;

    @Override
    public InvitationCardUploadUrlResult fetch(InvitationCardUploadUrlParams params) {

        String key = params.filePath().startsWith("/")
                ? params.filePath().substring(1)
                : params.filePath();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(awsConfig.invitationCardBucket())
                .key(key)
                .contentType(params.contentType().getContentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(awsConfig.invitationCardUploadDuration()))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

        return new InvitationCardUploadUrlResult(
                presignedRequest.url(),
                presignedRequest.httpRequest().method().name(),
                new HashMap<>()
        );
    }
}
