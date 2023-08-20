package org.project.dev.notice.entity;

import lombok.*;
import org.project.dev.notice.dto.NoticeDto;
import org.project.dev.utils.BaseEntity;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "notice")
public class NoticeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "not_id")
    private Long notId; // 공지사항 ID
    // notID
    @Column(name = "not_title", nullable = false)
    private String noticeTitle; // 공지사항 글 제목
    @Column(name = "notContent", nullable = false)
    private String notContent; // 공지사항 글 내용
    @Column(name = "notWriter", nullable = false)
    private String notWriter; // 공지사항 글 작성자
    @Column(name = "notHit", nullable = false)
    private int notHit; // 공지사항 글 조회수


    public static NoticeEntity toNoticeEntityInsert(NoticeDto noticeDto) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setNoticeTitle(noticeDto.getNotTitle());
        noticeEntity.setNotContent(noticeDto.getNotContent());
        noticeEntity.setNotWriter(noticeDto.getNotWriter());
        noticeEntity.setNotHit(0);
        return noticeEntity;
    }

    public static NoticeEntity toNoticeEntityUpdate(NoticeDto noticeDto) {
        NoticeEntity noticeEntity = new NoticeEntity();
        noticeEntity.setNoticeTitle(noticeDto.getNotTitle());
        noticeEntity.setNotContent(noticeDto.getNotContent());
        noticeEntity.setNotWriter(noticeDto.getNotWriter());
        noticeEntity.setNotHit(noticeDto.getNotHit());
        return noticeEntity;
    }

    // 연관관계
    // 1:N 관리자
}
