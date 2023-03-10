package com.softamo.bots.basecamp.l337;

import io.micronaut.chatbots.basecamp.api.Query;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Singleton;
import io.micronaut.chatbots.basecamp.core.BasecampHandler;
import io.micronaut.chatbots.basecamp.core.BasecampBotConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class HelloWorldBasecampHandler implements BasecampHandler {
    @Override
    public boolean canHandle(@Nullable BasecampBotConfiguration bot,
                      @NonNull @NotNull @Valid Query query) {
        return true;
    }

    @Override
    @NonNull
    public Optional<String> handle(@Nullable BasecampBotConfiguration bot, @NonNull @NotNull @Valid Query query) {
        return Optional.of("Hello World");
    }
}
