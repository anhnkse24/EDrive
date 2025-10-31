package com.swp391.edrive.service.serviceimpl;

import com.swp391.edrive.dto.response.FeedbackResponse;
import com.swp391.edrive.entity.Feedback;
import com.swp391.edrive.repository.FeedbackRepository;
import com.swp391.edrive.service.FeedbackService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepo;

    private FeedbackResponse toDto(Feedback f) {
        FeedbackResponse dto = new FeedbackResponse();
        dto.setFeedbackId(f.getFeedbackId());
        dto.setCustomerId(f.getCustomer() != null ? f.getCustomer().getCustomerId() : null);
        dto.setDealerId(f.getDealer() != null ? f.getDealer().getDealerId() : null);
        dto.setRating(f.getRating());
        dto.setContent(f.getContent());
        dto.setCreatedAt(f.getCreatedAt());
        return dto;
    }

    @Override
    public Page<FeedbackResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "feedbackId"));
        Page<Feedback> p = feedbackRepo.findAll(pageable);
        return p.map(this::toDto);
    }

    @Override
    public FeedbackResponse getById(Long id) {
        Feedback f = feedbackRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found: " + id));
        return toDto(f);
    }

    @Override
    public Page<FeedbackResponse> getByCustomerId(Long customerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "feedbackId"));
        Page<Feedback> p = feedbackRepo.findByCustomer_CustomerId(customerId, pageable);
        return p.map(this::toDto);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!feedbackRepo.existsById(id)) {
            throw new EntityNotFoundException("Feedback not found: " + id);
        }
        feedbackRepo.deleteById(id);
    }
}
