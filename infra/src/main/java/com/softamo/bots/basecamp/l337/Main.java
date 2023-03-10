package com.softamo.bots.basecamp.l337;

import software.amazon.awscdk.App;

public class Main {
    public static void main(final String[] args) {
        App app = new App();
        new AppStack(app, "BasecampL337");
        app.synth();
    }
}