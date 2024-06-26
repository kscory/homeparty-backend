package com.homeparty.identity.domain.commands;

import autoparams.AutoSource;
import autoparams.customization.Customization;
import com.homeparty.identity.domain.aggregates.identity.Identity;
import com.homeparty.identity.domain.aggregates.identity.IdentityRepository;
import com.homeparty.identity.domain.aggregates.identity.SocialProvider;
import com.homeparty.identity.domain.aggregates.identity.SocialProviderType;
import com.homeparty.identity.domain.exception.IdentityException;
import com.homeparty.identity.domain.exception.IdentityExceptionCode;
import com.homeparty.identity.domain.models.AuthTokenGenerator;
import com.homeparty.identity.domain.models.SocialProviderFetcher;
import com.homeparty.identity.testing.CommandsCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class SignUpSocialCommandHandlerTest {

    @Autowired
    private IdentityRepository identityRepository;

    @DisplayName("이미 가입되어 있다면 에러를 반환한다.")
    @ParameterizedTest
    @AutoSource()
    @Customization(CommandsCustomizer.class)
    public void sut_fails_if_already_sign_up(
            Identity identity,
            String socialId,
            String socialNickname,
            String socialEmail,
            String socialImageUrl,
            AuthTokenGenerator authTokenGenerator,
            SignUpSocialCommand command
    ) {
        // given
        setSocialIdAndType(identity, socialId, command.providerType);
        SocialProviderFetcher fetcher = socialProviderFetcherTestDouble(socialId, socialNickname, socialEmail, socialImageUrl);
        identityRepository.save(identity);

        var sut = new SignUpSocialCommandHandler(fetcher, authTokenGenerator, identityRepository);

        // when
        IdentityException actual = assertThrows(IdentityException.class, () -> sut.handle(command));

        // then
        assertThat(actual).isEqualTo(new IdentityException(IdentityExceptionCode.ALREADY_SIGN_UP));
    }

    @DisplayName("새로운 identity 를 저장한다.")
    @ParameterizedTest
    @AutoSource()
    @Customization(CommandsCustomizer.class)
    public void sut_save_identity(
            String socialId,
            String socialNickname,
            String socialEmail,
            String socialImageUrl,
            AuthTokenGenerator authTokenGenerator,
            SignUpSocialCommand command
    ) {
        // given
        SocialProviderFetcher fetcher = socialProviderFetcherTestDouble(socialId, socialNickname, socialEmail, socialImageUrl);

        var sut = new SignUpSocialCommandHandler(fetcher, authTokenGenerator, identityRepository);

        // when
        sut.handle(command);

        // then
        var actual =  identityRepository.findById(command.identityId);
        assertThat(actual.get().getNickname()).isEqualTo(socialNickname);
        assertThat(actual.get().getEmail()).isEqualTo(socialEmail);
        assertThat(actual.get().getProvider())
                .usingRecursiveComparison()
                .isEqualTo(new SocialProvider(
                        command.providerType,
                        socialId,
                        socialNickname,
                        socialEmail,
                        socialImageUrl
                ));
    }

    @DisplayName("AuthToken 을 반환한다.")
    @ParameterizedTest
    @AutoSource()
    @Customization(CommandsCustomizer.class)
    public void sut_return_authToken(
            String socialId,
            String socialNickname,
            String socialEmail,
            String socialImageUrl,
            AuthTokenGenerator authTokenGenerator,
            SignUpSocialCommand command
    ) {
        // given
        SocialProviderFetcher fetcher = socialProviderFetcherTestDouble(socialId, socialNickname, socialEmail, socialImageUrl);
        var sut = new SignUpSocialCommandHandler(fetcher, authTokenGenerator, identityRepository);

        // when
        var actual = sut.handle(command);

        // then
        assertThat(actual.getAccessToken()).isNotNull();
        assertThat(actual.getAccessTokenExpiredAt()).isNotNull();
        assertThat(actual.getRefreshToken()).isNotNull();
        assertThat(actual.getRefreshTokenExpiredAt()).isNotNull();
    }

    private static SocialProviderFetcher socialProviderFetcherTestDouble(
            String socialId,
            String socialNickname,
            String socialEmail,
            String socialImageUrl
    ) {
        return (type, token) -> new SocialProvider(
                type,
                socialId,
                socialNickname,
                socialEmail,
                socialImageUrl
        );
    }

    private static void setSocialIdAndType(Identity identity, String socialId, SocialProviderType providerType) {
        try {
            Field provider = Identity.class.getDeclaredField("provider");
            provider.setAccessible(true);

            Field id = provider.getType().getDeclaredField("socialId");
            id.setAccessible(true);
            id.set(identity.getProvider(), socialId);

            Field type = provider.getType().getDeclaredField("providerType");
            type.setAccessible(true);
            type.set(identity.getProvider(), providerType);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
