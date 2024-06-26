package com.homeparty.identity.kakao;

import autoparams.AutoSource;
import com.homeparty.identity.domain.aggregates.identity.SocialProvider;
import com.homeparty.identity.domain.aggregates.identity.SocialProviderType;
import com.homeparty.identity.kakao.KakaoClient;
import com.homeparty.identity.kakao.KakaoSocialProviderFetcher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class KakaoSocialProviderFetcherTest {

    @DisplayName("카카오 유저 관련 정보를 가져온다.")
    @ParameterizedTest
    @AutoSource()
    @Disabled
    public void sut_return_kakaoSocialProvider(KakaoClient kakaoClient) {

        // given
        String accessToken = "16e3VE0j9OWjD-SCYTuLBLFpDFluFKIkzg4y18bRCisNHgAAAYT6pKXq";
        KakaoSocialProviderFetcher sut = new KakaoSocialProviderFetcher(kakaoClient);

        // when
        SocialProvider actual = sut.fetch(SocialProviderType.KAKAO, accessToken);

        // then
        log.info("check actual: {}", actual);
        assertThat(actual.getSocialId()).isNotNull();
        assertThat(actual.getSocialNickname()).isNotNull();
    }
}
