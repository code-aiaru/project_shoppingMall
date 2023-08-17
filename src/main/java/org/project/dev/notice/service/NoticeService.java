
package org.project.dev.notice.service;

import lombok.RequiredArgsConstructor;
import org.project.dev.notice.dto.NoticeDto;
import org.project.dev.notice.entity.NoticeEntity;
import org.project.dev.notice.repository.NoticeReopsitory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.IllformedLocaleException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeReopsitory noticeReopsitory;

    /*
   Todo
    1. rladpwls1843@gamil.com
    2. 공지사항 쓰기 서비스
    3.
    4.
    */
    @Transactional
    public int NoticeInsert(NoticeDto noticeDto) {
        NoticeEntity noticeEntity = NoticeEntity.toNoticeEntityInsert(noticeDto);
        Long noticeId = noticeReopsitory.save(noticeEntity).getNotId();

        Optional<NoticeEntity> optionalNoticeEntity
                = Optional.ofNullable(noticeReopsitory.findById(noticeId).orElseThrow(() -> {
            return new IllegalArgumentException("공지사항을 찾을 수 없습니다.");
        }));
        if(!optionalNoticeEntity.isPresent()){
            return 0;
        }
        return 1;
    }

    /*
  Todo
   1. rladpwls1843@gamil.com
   2. 공지사항 목록 서비스
   3.
   4.
   */
//    public List<NoticeDto> findAllNoticeList() {
//        List<NoticeDto> noticeDtos = new ArrayList<>();
//        List<NoticeEntity> noticeEntities = noticeReopsitory.findAll();
//        /**/
//        if (!noticeEntities.isEmpty()){
//            for (NoticeEntity noticeEntity : noticeEntities) {
//                NoticeDto noticeDto = NoticeDto.builder()
//                        .notTitle(noticeEntity.getNotTitle())
//                        .notContent(noticeEntity.getNotContent())
//                        .notWriter(noticeEntity.getNotWriter())
//                        .notHit(noticeEntity.getNotHit())
//                        .build();
//                noticeDtos.add(noticeDto);
//            }
//        }
//        return noticeDtos;
//    }
    /*
       Todo
        1. rladpwls1843@gamil.com
        2. 공지사항 목록로 이동 -> paging
        3.
        4.
        */
    public Page<NoticeDto> NoticeList(Pageable pageable) {
        Page<NoticeEntity> noticeEntities = noticeReopsitory.findAll(pageable);

        noticeEntities.getNumber();
        noticeEntities.getTotalElements();
        noticeEntities.getTotalPages();
        noticeEntities.getSize();

        Page<NoticeDto> noticeDtoPage = noticeEntities.map(NoticeDto::tonoticeDto);

        return noticeDtoPage;
    }

    /*
       Todo
        1. rladpwls1843@gamil.com
        2. 공지사항 검색페이지로 이동
        3.
        4.
        */
    public List<NoticeDto> NoticeListSearch(String noticeId, String noticeSearch) {
        List<NoticeDto> noticeDtoList = new ArrayList<>();
        List<NoticeEntity> noticeEntities = new ArrayList<>();

        if(noticeId.equals("title")){
            noticeEntities = noticeReopsitory.findByTitleContaining(noticeSearch);
        }else if(noticeId.equals("content")){
            noticeEntities = noticeReopsitory.findByContentContaining(noticeSearch);
        }else{
            noticeEntities = noticeReopsitory.findAll();
        }

        if(!noticeEntities.isEmpty()){
            for(NoticeEntity noticeEntity : noticeEntities) {
                NoticeDto noticeDto = NoticeDto.builder()
                        .notTitle(noticeEntity.getNotTitle())
                        .notContent(noticeEntity.getNotContent())
                        .notWriter(noticeEntity.getNotWriter())
                        .notHit(noticeEntity.getNotHit())
                        .build();
                noticeDtoList.add(noticeDto);
            }
        }
        return noticeDtoList;
    }
    /*
           Todo
            1. rladpwls1843@gamil.com
            2. 공지사항 상세페이지로 이동
            3.
            4.
            */
    @Transactional
    public NoticeDto NoticeDetail(Long id) {
        NoticeHit(id);

        NoticeEntity noticeEntity = noticeReopsitory.findById(id).orElseThrow(IllformedLocaleException::new);

        return NoticeDto.builder()
                .notId(noticeEntity.getNotId())
                .notTitle(noticeEntity.getNotTitle())
                .notContent(noticeEntity.getNotContent())
                .notWriter(noticeEntity.getNotWriter())
                .notHit(noticeEntity.getNotHit())
                .build();
    }
    /*
       Todo
        1. rladpwls1843@gamil.com
        2. 상세페이지 선택할 떄마다 조회수 올라감
        3.
        4.
        */
    @Transactional
    public void NoticeHit(Long id){
        noticeReopsitory.NoticeHit(id);
    }
}
