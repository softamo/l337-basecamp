package com.softamo.bots.basecamp.l337;

import io.micronaut.aws.cdk.function.MicronautFunction;
import io.micronaut.aws.cdk.function.MicronautFunctionFile;
import io.micronaut.starter.application.ApplicationType;
import io.micronaut.starter.options.BuildTool;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.CfnFunction;
import software.amazon.awscdk.services.lambda.FunctionUrlCorsOptions;
import software.constructs.IConstruct;
import software.amazon.awscdk.services.lambda.Alias;
import software.amazon.awscdk.services.lambda.Version;
import software.amazon.awscdk.services.lambda.FunctionUrl;
import software.amazon.awscdk.services.lambda.FunctionUrlAuthType;
import software.amazon.awscdk.services.lambda.FunctionUrlOptions;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Tracing;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.constructs.Construct;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AppStack extends Stack {

    public AppStack(final Construct parent, final String id) {
        this(parent, id, null);
    }

    public AppStack(final Construct parent, final String id, final StackProps props) {
        super(parent, id, props);

        Map<String, String> environmentVariables = new HashMap<>();
        // https://aws.amazon.com/blogs/compute/optimizing-aws-lambda-function-performance-for-java/
        environmentVariables.put("JAVA_TOOL_OPTIONS", "-XX:+TieredCompilation -XX:TieredStopAtLevel=1");
        Function function = MicronautFunction.create(ApplicationType.FUNCTION,
                false,
                this,
                "basecamp-l337-function")
                .handler("io.micronaut.chatbots.basecamp.lambda.Handler")
                .environment(environmentVariables)
                .code(Code.fromAsset(functionPath()))
                .timeout(Duration.seconds(10))
                .memorySize(2048)
                .logRetention(RetentionDays.ONE_WEEK)
                .tracing(Tracing.DISABLED)
                .architecture(Architecture.X86_64)
                .build();
        IConstruct defaultChild = function.getNode().getDefaultChild();
        if (defaultChild instanceof CfnFunction) {
            CfnFunction cfnFunction = (CfnFunction) defaultChild;
            cfnFunction.setSnapStart(CfnFunction.SnapStartProperty.builder()
                .applyOn("PublishedVersions")
                .build());
        }
        Version currentVersion = function.getCurrentVersion();
        Alias prodAlias = Alias.Builder.create(this, "ProdAlias")
                .aliasName("Prod")
                .version(currentVersion)
                .build();
        FunctionUrl functionUrl = prodAlias.addFunctionUrl(FunctionUrlOptions.builder()
                .authType(FunctionUrlAuthType.NONE)
                        .cors(FunctionUrlCorsOptions.builder().allowedOrigins(Collections.singletonList("*")).build())
                .build());
        CfnOutput.Builder.create(this, "BasecampL337ApiUrl")
                .exportName("BasecampL337ApiUrl")
                .value(functionUrl.getUrl())
                .build();
    }

    public static String functionPath() {
        return "../app/build/libs/" + functionFilename();
    }

    public static String functionFilename() {
        return MicronautFunctionFile.builder()
            .graalVMNative(false)
            .version("0.1")
            .archiveBaseName("app")
            .buildTool(BuildTool.GRADLE)
            .build();
    }
}