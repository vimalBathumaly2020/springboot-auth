package com.venueelite.app.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetLink = "https://binery-venue.web.app/reset-password?token=" + resetToken;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("vbathumaly@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request – VenueElite");
            helper.setText(buildPlainText(resetLink), buildHtmlEmail(resetLink));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPlainText(String resetLink) {
        return "VenueElite – Password Reset Request\n\n"
                + "We received a request to reset your VenueElite password.\n\n"
                + "Click the link below to reset it:\n"
                + resetLink + "\n\n"
                + "This link expires in 30 minutes.\n\n"
                + "If you didn't request a password reset, you can safely ignore this email.\n\n"
                + "© 2025 VenueElite. All rights reserved.";
    }

    private String buildHtmlEmail(String resetLink) {
        return "<!DOCTYPE html>"
                + "<html lang='en'><head><meta charset='UTF-8'>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "<title>Password Reset</title></head>"
                + "<body style='margin:0;padding:0;background:#f3f4f6;font-family:Arial,sans-serif;'>"
                + "<table width='100%' cellpadding='0' cellspacing='0' style='background:#f3f4f6;padding:2rem 1rem;'>"
                + "<tr><td align='center'>"
                + "<table width='520' cellpadding='0' cellspacing='0' style='background:#ffffff;border-radius:12px;border:1px solid #e5e7eb;overflow:hidden;'>"

                + "<tr><td style='background:#185FA5;padding:28px 32px;text-align:center;'>"
                + "<span style='font-size:20px;font-weight:600;color:#ffffff;letter-spacing:0.3px;'>&#127970; VenueElite</span>"
                + "</td></tr>"

                + "<tr><td style='padding:32px 32px 24px;'>"

                + "<div style='width:48px;height:48px;border-radius:50%;background:#E6F1FB;display:flex;align-items:center;justify-content:center;margin:0 auto 20px;text-align:center;line-height:48px;'>"
                + "<span style='font-size:22px;'>&#128274;</span>"
                + "</div>"

                + "<h2 style='text-align:center;font-size:18px;font-weight:600;margin:0 0 8px;color:#111827;'>Password reset request</h2>"
                + "<p style='text-align:center;font-size:14px;color:#6b7280;margin:0 0 28px;line-height:1.6;'>"
                + "We received a request to reset your VenueElite password.<br>Click the button below to choose a new one."
                + "</p>"

                + "<div style='text-align:center;margin-bottom:28px;'>"
                + "<a href='" + resetLink + "' style='display:inline-block;background:#185FA5;color:#ffffff;text-decoration:none;padding:12px 32px;border-radius:8px;font-size:15px;font-weight:600;'>Reset my password</a>"
                + "</div>"

                + "<div style='background:#f9fafb;border-radius:8px;padding:12px 16px;margin-bottom:24px;'>"
                + "<p style='font-size:12px;color:#6b7280;margin:0;line-height:1.6;text-align:center;'>"
                + "&#9201; This link expires in <strong style='color:#111827;'>30 minutes</strong>. "
                + "If you didn't request a password reset, you can safely ignore this email."
                + "</p>"
                + "</div>"

                + "<div style='border-top:1px solid #e5e7eb;padding-top:20px;'>"
                + "<p style='font-size:11px;color:#9ca3af;text-align:center;margin:0 0 6px;'>If the button doesn't work, copy and paste this link into your browser:</p>"
                + "<p style='font-size:11px;color:#185FA5;text-align:center;word-break:break-all;margin:0;'>" + resetLink + "</p>"
                + "</div>"

                + "</td></tr>"

                + "<tr><td style='border-top:1px solid #e5e7eb;padding:16px 32px;text-align:center;'>"
                + "<p style='font-size:11px;color:#9ca3af;margin:0;'>© 2025 VenueElite &middot; All rights reserved</p>"
                + "</td></tr>"

                + "</table>"
                + "</td></tr></table>"
                + "</body></html>";
    }
}