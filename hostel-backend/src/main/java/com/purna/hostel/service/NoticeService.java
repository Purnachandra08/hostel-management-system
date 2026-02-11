package com.purna.hostel.service;

import com.purna.hostel.entity.Notice;
import com.purna.hostel.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoticeService {
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public Notice createNotice(Notice notice) {
        return noticeRepository.save(notice);
    }

    public Optional<Notice> findById(Long id) { return noticeRepository.findById(id); }
    public List<Notice> findAll() { return noticeRepository.findAll(); }
    public void deleteById(Long id) { noticeRepository.deleteById(id); }
}
