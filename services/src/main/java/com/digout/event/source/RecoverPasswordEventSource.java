package com.digout.event.source;

public class RecoverPasswordEventSource {

    public static final class Builder {
        private String email;
        private String fullname;
        private String generatedPassword;

        public Builder addEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder addFullname(final String fullname) {
            this.fullname = fullname;
            return this;
        }

        public Builder addGenPassword(final String generatedPassword) {
            this.generatedPassword = generatedPassword;
            return this;
        }

        public RecoverPasswordEventSource build() {
            return new RecoverPasswordEventSource(this.email, this.fullname, this.generatedPassword);
        }
    }

    private final String email;
    private final String fullname;

    private final String generatedPassword;

    private RecoverPasswordEventSource(final String email, final String fullname, final String generatedPassword) {
        this.email = email;
        this.fullname = fullname;
        this.generatedPassword = generatedPassword;
    }

    public String getEmail() {
        return this.email;
    }

    public String getFullname() {
        return this.fullname;
    }

    public String getGeneratedPassword() {
        return this.generatedPassword;
    }
}
