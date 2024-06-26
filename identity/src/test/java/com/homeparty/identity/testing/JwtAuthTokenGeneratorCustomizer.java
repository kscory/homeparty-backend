package com.homeparty.identity.testing;

import autoparams.customization.Customizer;
import autoparams.generator.ObjectContainer;
import autoparams.generator.ObjectGenerationContext;
import autoparams.generator.ObjectGenerator;
import com.homeparty.identity.domain.models.AuthTokenGenerator;
import com.homeparty.identity.jwt.JwtAuthTokenConfig;
import com.homeparty.identity.jwt.JwtAuthTokenGenerator;

public class JwtAuthTokenGeneratorCustomizer implements Customizer {

    @Override
    public ObjectGenerator customize(ObjectGenerator generator) {
        return (query, context) -> query.getType().equals(AuthTokenGenerator.class)
                ? new ObjectContainer(factory(context))
                : generator.generate(query, context);
    }

    private AuthTokenGenerator factory(ObjectGenerationContext context) {
        JwtAuthTokenConfig config = (JwtAuthTokenConfig) context.generate(() -> JwtAuthTokenConfig.class);
        return new JwtAuthTokenGenerator(config);
    }

}
