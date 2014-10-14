package com.digout.event.source;

public class WelcomeEmailEventSource {

    public static final class Builder {

        private String username;
        private String email;

        public Builder addEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder addUsername(final String username) {
            this.username = username;
            return this;
        }

        public WelcomeEmailEventSource build() {
            return new WelcomeEmailEventSource(this.email, this.username);
        }
    }
    private final String email;

    private final String username;

    private WelcomeEmailEventSource(final String email, final String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUsername() {
        return this.username;
    }
}
