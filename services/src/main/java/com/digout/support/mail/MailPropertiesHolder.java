package com.digout.support.mail;

public final class MailPropertiesHolder {

    private String username;
    private String password;
    private String host;
    private Integer port;
    private Boolean mailSmtpAuth;
    private Boolean mailSmtpStarttlsEnable;

    public MailPropertiesHolder() {
    }

    public String getHost() {
        return this.host;
    }

    public Boolean getMailSmtpAuth() {
        return this.mailSmtpAuth;
    }

    public Boolean getMailSmtpStarttlsEnable() {
        return this.mailSmtpStarttlsEnable;
    }

    public String getPassword() {
        return this.password;
    }

    public Integer getPort() {
        return this.port;
    }

    public String getUsername() {
        return this.username;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public void setMailSmtpAuth(final Boolean mailSmtpAuth) {
        this.mailSmtpAuth = mailSmtpAuth;
    }

    public void setMailSmtpStarttlsEnable(final Boolean mailSmtpStarttlsEnable) {
        this.mailSmtpStarttlsEnable = mailSmtpStarttlsEnable;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    public void setUsername(final String username) {
        this.username = username;
    }
}
