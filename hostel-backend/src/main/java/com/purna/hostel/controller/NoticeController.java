package com.purna.hostel.controller;

import com.purna.hostel.entity.Notice;
import com.purna.hostel.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {
    private final NoticeService noticeService;
    public NoticeController(NoticeService noticeService) { this.noticeService = noticeService; }

    @GetMapping
    public ResponseEntity<List<Notice>> getAll(){ return ResponseEntity.ok(noticeService.findAll()); }

    @PostMapping
    public ResponseEntity<Notice> create(@RequestBody Notice notice){
        return ResponseEntity.ok(noticeService.createNotice(notice));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        noticeService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
