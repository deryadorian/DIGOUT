package com.digout.support.mail;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.digout.event.source.ApprovalEmailEventSource;
import com.digout.event.source.IssueEmailSource;
import com.digout.event.source.OrderShippingInfoEmailSource;
import com.digout.event.source.OrderSoldEmailSource;
import com.digout.support.i18n.I18nMessageSource;
import com.digout.utils.SecureUtils;
import com.google.common.base.Joiner;

@Slf4j
public final class BuySellProcessMailer extends Mailer {

    private static final String TEMPLATE_PACKAGE = PasswordRecoveryMailer.class.getPackage().getName()
            .replace(".", "/");
    private static final String TEMPLATE_APPROVAL = TEMPLATE_PACKAGE + "/approval-email-%s.vm";
    private static final String TEMPLATE_APPROVAL_SYSTEM = TEMPLATE_PACKAGE + "/approval-email-system-%s.vm";
    private static final String TEMPLATE_ITEM_SOLD = TEMPLATE_PACKAGE + "/item-sold-email-%s.vm";
    private static final String TEMPLATE_ITEM_SOLD_SYSTEM = TEMPLATE_PACKAGE + "/item-sold-email-system-%s.vm";
    private static final String TEMPLATE_REPORT_ISSUE = TEMPLATE_PACKAGE + "/report-issue-email-%s.vm";
    private static final String TEMPLATE_WELCOME = TEMPLATE_PACKAGE + "/welcome-email-%s.vm";
    private static final String TEMPLATE_ORDER_INFO = TEMPLATE_PACKAGE + "/order-info-email-%s.vm";
    private static final String TEMPLATE_REPORT_INAPPROP_PRODUCT = TEMPLATE_PACKAGE
            + "/report-inappropriate-product-email-%s.vm";
    private static final String TEMPLATE_REPORT_ORDER_SHIPPED = TEMPLATE_PACKAGE + "/order-shipped-emal-%s.vm";

    private String from;
    private String goToDigoutUrl;
    private String goToDigoutApproveUrl;
    private String goToDigoutItemSoldUrl;
    private String toSystemMailGroup;
    private String toSystemMailGroup2;

    @Autowired
    private I18nMessageSource i18n;

    public BuySellProcessMailer() {
    }

    public void sendApprovalEmailToSeller(final ApprovalEmailEventSource approvalEmailData) {
        final String subj = this.i18n.getMessage("approval.email.subject");
        final String template = String.format(TEMPLATE_APPROVAL, this.i18n.getLocale().getLanguage());
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", approvalEmailData.getBuyerName());
        model.put("productName", approvalEmailData.getProductName());
        model.put("approvalTime",
                approvalEmailData.getApprovalTime().toString(DateTimeFormat.forPattern("HH:mm dd-MM-yyyy")));
        model.put("goToDigoutApprovalUrl", this.goToDigoutApproveUrl);

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, new String[] { approvalEmailData.getSellerEmail() }, subj, template, model);
        log.debug("Sent email with content'{}'", approvalEmailData);
    }

    public void sendApprovalEmailToSystem(final ApprovalEmailEventSource approvalEmailData) {
        final String subj = "Product Approved, money transfer should be done";
        final String template = String.format(TEMPLATE_APPROVAL_SYSTEM, Locale.ENGLISH);

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("id", SecureUtils.generateSecureId());
        model.put("buyerUsername", approvalEmailData.getBuyerName());
        model.put("buyerEmail", approvalEmailData.getBuyerEmail());
        model.put("buyerMobile", approvalEmailData.getBuyerMobile());
        model.put("sellerUsername", approvalEmailData.getSellerName());
        model.put("sellerEmail", approvalEmailData.getSellerEmail());
        model.put("sellerMobile", approvalEmailData.getSellerMobile());
        model.put("productName", approvalEmailData.getProductName());
        model.put("price", approvalEmailData.getPrice());
        model.put("iban", approvalEmailData.getSellerIban());

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, new String[] { this.toSystemMailGroup }, subj, template, model);
        log.debug("Sent email with content'{}'", approvalEmailData);
    }

    public void sendItemSoldEmail(final OrderSoldEmailSource data) {
        // prepare email template for seller
        final String subjForSeller = this.i18n.getMessage("item.sold.email.subject");
        final String templateForSeller = String.format(TEMPLATE_ITEM_SOLD, this.i18n.getLocale().getLanguage());
        // prepare email template for buyer
        final String subjForBuyer = this.i18n.getMessage("order.info.email.subject") + "" + data.getUniqueOrderId();
        final String templateForBuyer = String.format(TEMPLATE_ORDER_INFO, this.i18n.getLocale().getLanguage());

        // prepare common model for both sides
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("uniqueOrderId", data.getUniqueOrderId());
        model.put("username", data.getBuyerName());
        model.put("productName", data.getProductName());
        model.put("orderTime", data.getOrderTime().toString(DateTimeFormat.forPattern("HH:mm dd MM YYYY")));
        model.put("price", data.getPrice());
        model.put("currency", data.getCurrency());
        model.put("goToDigoutItemSoldUrl", this.goToDigoutItemSoldUrl);
        model.put("goToDigoutUrl", this.goToDigoutUrl);

        // send email to seller
        log.debug("Preparing to send '{}' email", subjForSeller);
        sendEmail(this.from, new String[] { data.getSellerEmail() }, subjForSeller, templateForSeller, model);
        log.debug("Sent email with content'{}' to seller", data);
        // send email to buyer

        log.debug("Preparing to send '{}' email", subjForBuyer);
        sendEmail(this.from, new String[] { data.getBuyerEmail() }, subjForBuyer, templateForBuyer, model);
        log.debug("Sent email with content'{}' to buyer", data);

    }

    public void sendItemSoldEmailSystem(final OrderSoldEmailSource data) {
        final String subj = "Product sold!";
        final String template = String.format(TEMPLATE_ITEM_SOLD_SYSTEM, Locale.ENGLISH);

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("buyerName", data.getBuyerName());
        model.put("buyerEmail", data.getBuyerEmail());
        model.put("buyerMobile", data.getBuyerMobile());
        model.put("sellerName", data.getSellerName());
        model.put("sellerEmail", data.getSellerEmail());
        model.put("sellerMobile", data.getSellerMobile());
        model.put("productName", data.getProductName());
        model.put("price", data.getPrice());

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, new String[] { toSystemMailGroup2 }, subj, template, model);
        log.debug("Sent email with content'{}'", data);
    }

    public void sendReportIssueEmail(final IssueEmailSource source) {
        final String subj = this.i18n.getMessage("report.issue.email.subject");
        final String template = String.format(TEMPLATE_REPORT_ISSUE, this.i18n.getLocale().getLanguage());

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", source.getEmail());
        model.put("productName", source.getProductName());
        model.put("issueType", source.getIssueType());
        model.put("description", source.getDescription());
        model.put("goToDigoutUrl", this.goToDigoutUrl);

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, new String[] { source.getEmail() }, subj, template, model);
        log.debug("Sent email with content'{}'", source);
    }

    public void sendReportIssueEmailSystem(final IssueEmailSource source) {
        final String subj = this.i18n.getMessage("report.issue.email.subject");
        final String template = String.format(TEMPLATE_REPORT_ISSUE, Locale.ENGLISH);

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", source.getEmail());
        model.put("productName", source.getProductName());
        model.put("issueType", source.getIssueType());
        model.put("description", source.getDescription());
        model.put("goToDigoutUrl", this.goToDigoutUrl);

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, new String[] { toSystemMailGroup }, subj, template, model);
        log.debug("Sent email with content '{}'", source);
    }

    public void sendWelcomeEmail(final String[] emailTo, final String username) {
        final String subj = this.i18n.getMessage("welcome.email.subject");
        final String template = String.format(TEMPLATE_WELCOME, this.i18n.getLocale().getLanguage());
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("username", username);
        model.put("goToDigoutUrl", this.goToDigoutUrl);

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, emailTo, subj, template, model);
        log.debug("Sent email with content '{}'", username);
    }

    public void sendInappropriateProductEmail(final String[] emailTo, final long productId, final long reporterId,
            final long productOwnerId) {
        final String subj = String.format("PRODUCT '%s' reported as inappropriate", productId);
        final String template = String.format(TEMPLATE_REPORT_INAPPROP_PRODUCT, Locale.ENGLISH.getLanguage());
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("productId", productId);
        model.put("reporterId", reporterId);
        model.put("productOwnerId", productOwnerId);

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, emailTo, subj, template, model);
        log.debug("Sent email with content productId = '{}', reporterId = '{}', productOwnerId='{}'", new Object[] {
                productId, reporterId, productOwnerId });
    }

    public void sendOrderShippedEmail(final OrderShippingInfoEmailSource data) {
        final String subj = "Ürününüz kargoya verildi";
        final String template = String.format(TEMPLATE_REPORT_ORDER_SHIPPED, this.i18n.getLocale().getLanguage());

        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("productId", data.getProductId());
        model.put("productName", data.getProductName());
        model.put("price", data.getPrice());
        model.put("shippedUsername", data.getShippedUserName());
        model.put("shippedDate", data.getShipped().toString(DateTimeFormat.forPattern("HH:mm dd MM YYYY")));
        model.put("carrierName", data.getCarrierName());

        model.put("trackingCode", data.getTrackingCode());
        model.put("carrierWebsite", data.getCarrierWebsite());

        log.debug("Preparing to send '{}' email", subj);
        sendEmail(this.from, new String[] { data.getBuyerEmail() }, subj, template, model);
        log.debug("Sent email with content '{}'", data);
    }

    @Required
    public void setFrom(final String from) {
        this.from = from;
    }

    @Required
    public void setGoToDigoutApproveUrl(final String goToDigoutApproveUrl) {
        this.goToDigoutApproveUrl = goToDigoutApproveUrl;
    }

    @Required
    public void setGoToDigoutItemSoldUrl(final String goToDigoutItemSoldUrl) {
        this.goToDigoutItemSoldUrl = goToDigoutItemSoldUrl;
    }

    @Required
    public void setGoToDigoutUrl(final String goToDigoutUrl) {
        this.goToDigoutUrl = goToDigoutUrl;
    }

    @Required
    public void setToSystemMailGroup(final String toSystemMailGroup) {
        this.toSystemMailGroup = toSystemMailGroup;
    }

    public void setToSystemMailGroup2(final String toSystemMailGroup2) {
        this.toSystemMailGroup2 = toSystemMailGroup2;
    }
}
