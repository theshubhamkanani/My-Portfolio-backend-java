package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.ContactMessageAdminDTO;
import com.my_portfolio_v1.backend_java.dtos.ContactMessageDTO;
import com.my_portfolio_v1.backend_java.models.ContactMessage;
import com.my_portfolio_v1.backend_java.repositories.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository repository;
    private final JavaMailSender mailSender;

    @Value("${admin.personal.email}")
    private String adminEmail;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${portfolio.domain.link:http://localhost:3000}")
    private String domainLink;

    @Value("classpath:templates/email-template.txt")
    private Resource userEmailTemplate;

    @Value("classpath:templates/admin-email-template.txt")
    private Resource adminEmailTemplate;

    @Transactional
    public void submitMessage(ContactMessageDTO dto) {
        ContactMessage savedMessage = repository.save(buildMessage(dto));
        sendNotificationEmails(savedMessage);
    }

    @Transactional(readOnly = true)
    public List<ContactMessageAdminDTO> getAllMessages(String search) {
        String normalizedSearch = normalize(search);

        List<ContactMessage> messages = normalizedSearch.isBlank()
                ? repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                : repository.findByEmailContainingIgnoreCaseOrReasonContainingIgnoreCaseOrderByCreatedAtDesc(
                normalizedSearch,
                normalizedSearch
        );

        return messages.stream()
                .map(this::mapToAdminDTO)
                .toList();
    }

    @Async
    public void sendNotificationEmails(ContactMessage message) {
        sendEmailToAdmin(message);
        sendEmailToUser(message);
    }

    private ContactMessage buildMessage(ContactMessageDTO dto) {
        ContactMessage message = new ContactMessage();
        message.setName(normalize(dto.getName()));
        message.setEmail(normalize(dto.getEmail()));
        message.setReason(normalize(dto.getReason()));
        message.setDescription(normalize(dto.getDescription()));
        return message;
    }

    private ContactMessageAdminDTO mapToAdminDTO(ContactMessage message) {
        return ContactMessageAdminDTO.builder()
                .id(message.getId())
                .name(message.getName())
                .email(message.getEmail())
                .reason(message.getReason())
                .description(message.getDescription())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private void sendEmailToAdmin(ContactMessage message) {
        try {
            String finalBody = readTemplate(adminEmailTemplate)
                    .replace("{{name}}", message.getName())
                    .replace("{{reason}}", message.getReason())
                    .replace("{{description}}", message.getDescription())
                    .replace("{{email}}", message.getEmail())
                    .replace("{{domain_link}}", domainLink);

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(senderEmail);
            mail.setTo(adminEmail);
            mail.setSubject("New Portfolio Contact: " + message.getReason());
            mail.setText(finalBody);
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to send admin contact email: " + e.getMessage());
        }
    }

    private void sendEmailToUser(ContactMessage message) {
        try {
            String finalBody = readTemplate(userEmailTemplate)
                    .replace("{{name}}", message.getName())
                    .replace("{{reason}}", message.getReason());

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(senderEmail);
            mail.setTo(message.getEmail());
            mail.setSubject("Thank you for reaching out!");
            mail.setText(finalBody);
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to send user confirmation email: " + e.getMessage());
        }
    }

    private String readTemplate(Resource resource) throws Exception {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
