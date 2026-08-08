package com.my_portfolio_v1.backend_java.services;

import com.my_portfolio_v1.backend_java.dtos.ContactMessageAdminDTO;
import com.my_portfolio_v1.backend_java.dtos.ContactMessageDTO;
import com.my_portfolio_v1.backend_java.models.ContactMessage;
import com.my_portfolio_v1.backend_java.repositories.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactMessageService {

    private final ContactMessageRepository repository;
    private final ContactEmailService contactEmailService;

    @Transactional
    public void submitMessage(ContactMessageDTO dto) {
        ContactMessage savedMessage = repository.save(buildMessage(dto));
        contactEmailService.sendNotificationEmails(savedMessage);
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

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }
}
