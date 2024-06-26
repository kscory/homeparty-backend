package com.homeparty.identity.domain.commands;

import abstraction.command.CommandHandler;
import com.homeparty.identity.domain.aggregates.authtoken.AuthToken;
import com.homeparty.identity.domain.aggregates.identity.Identity;
import com.homeparty.identity.domain.aggregates.identity.IdentityRepository;
import com.homeparty.identity.domain.exception.IdentityException;
import com.homeparty.identity.domain.exception.IdentityExceptionCode;
import com.homeparty.identity.domain.models.AuthTokenGenerator;
import com.homeparty.identity.domain.models.SocialProviderFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignInSocialCommandHandler implements CommandHandler<SignInSocialCommand, AuthToken> {

    private final SocialProviderFetcher socialProviderFetcher;
    private final AuthTokenGenerator authTokenGenerator;
    private final IdentityRepository identityRepository;

    public AuthToken handle(SignInSocialCommand command) {
        var socialProvider = socialProviderFetcher.fetch(command.providerType, command.providerToken);
        Optional<Identity> identity = identityRepository.findByProviderSocialId(socialProvider.getSocialId());
        if (identity.isEmpty()) {
            log.info("this user need signup, socialProvider: {}", socialProvider);
            throw new IdentityException(IdentityExceptionCode.NEED_SIGN_UP);
        }

        return authTokenGenerator.generate(identity.get().getId().toString());
    }
}
