package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.ContactMessageDTO;
import com.my_portfolio_v1.backend_java.models.ContactMessage;
import com.my_portfolio_v1.backend_java.repositories.ContactMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Service
public class ContactMessageService {

    private final ContactMessageRepository repository;
    private final JavaMailSender mailSender;

    @Value("${admin.personal.email}")
    private String adminEmail;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // Pulls from .env, but defaults to localhost if the variable is missing
    @Value("${portfolio.domain.link:http://localhost:3000}")
    private String domainLink;

    @Value("classpath:email-template.txt")
    private Resource userEmailTemplate;

    @Value("classpath:admin-email-template.txt")
    private Resource adminEmailTemplate;

    @Autowired
    public ContactMessageService(ContactMessageRepository repository, JavaMailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    public void processAndSaveMessage(ContactMessageDTO dto) {
        ContactMessage message = new ContactMessage();
        message.setName(dto.getName());
        message.setEmail(dto.getEmail());
        message.setReason(dto.getReason());
        message.setDescription(dto.getDescription());

        repository.save(message);
    }

    @Async
    public void sendNotificationEmails(ContactMessageDTO dto) {
        sendEmailToAdmin(dto);
        sendEmailToUser(dto);
    }

    private void sendEmailToAdmin(ContactMessageDTO dto) {
        try (Reader reader = new InputStreamReader(adminEmailTemplate.getInputStream(), StandardCharsets.UTF_8)) {
            String templateContent = FileCopyUtils.copyToString(reader);

            // Chain the replace methods to swap all placeholders
            String finalBody = templateContent
                    .replace("{{name}}", dto.getName())
                    .replace("{{reason}}", dto.getReason())
                    .replace("{{description}}", dto.getDescription())
                    .replace("{{email}}", dto.getEmail())
                    .replace("{{domain_link}}", domainLink);

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(senderEmail);
            mail.setTo(adminEmail);
            mail.setSubject("New Portfolio Contact: " + dto.getReason());
            mail.setText(finalBody);
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to read admin template or send email: " + e.getMessage());
        }
    }

    private void sendEmailToUser(ContactMessageDTO dto) {
        try (Reader reader = new InputStreamReader(userEmailTemplate.getInputStream(), StandardCharsets.UTF_8)) {
            String templateContent = FileCopyUtils.copyToString(reader);

            String finalBody = templateContent
                    .replace("{{name}}", dto.getName())
                    .replace("{{reason}}", dto.getReason());

            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(senderEmail);
            mail.setTo(dto.getEmail());
            mail.setSubject("Thank you for reaching out!");
            mail.setText(finalBody);
            mailSender.send(mail);
        } catch (Exception e) {
            System.err.println("Failed to read user template or send email: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<ContactMessage> getAllMessages() {
        return repository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

}