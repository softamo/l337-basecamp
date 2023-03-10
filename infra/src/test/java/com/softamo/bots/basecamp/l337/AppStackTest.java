package com.softamo.bots.basecamp.l337;

import com.softamo.bots.basecamp.l337.AppStack;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.assertions.Template;
import java.io.File;
import java.util.Collections;

class AppStackTest {

    @Test
    void testAppStack() {
        if (new File(AppStack.functionPath()).exists()) {
            AppStack stack = new AppStack(new App(), "TestMicronautAppStack");
            Template template = Template.fromStack(stack);
            template.hasResourceProperties("AWS::Lambda::Function", Collections.singletonMap("Handler", "io.micronaut.chatbots.basecamp.lambda.Handler"));
        }
    }
}
