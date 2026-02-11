package com.purna.hostel.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notice")
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition="TEXT")
    private String content;

    @Column(name = "posted_by")
    private Long postedBy; // you could link to User if required

    @Column(name = "posted_at", nullable=false)
    private Timestamp postedAt;

    public Notice() {}

    @PrePersist
    protected void onCreate() {
        this.postedAt = new Timestamp(System.currentTimeMillis());
    }

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getPostedBy() { return postedBy; }
    public void setPostedBy(Long postedBy) { this.postedBy = postedBy; }

    public Timestamp getPostedAt() { return postedAt; }
    public void setPostedAt(Timestamp postedAt) { this.postedAt = postedAt; }
}
