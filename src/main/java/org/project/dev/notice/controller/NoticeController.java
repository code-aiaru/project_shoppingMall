package org.project.dev.notice.controller;


import lombok.RequiredArgsConstructor;
import org.project.dev.notice.dto.NoticeDto;
import org.project.dev.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    @Autowired
    private final NoticeService noticeService;

    /*
    Todo
     1. rladpwls1843@gamil.com
     2. 공지사항 쓰기페이지로 이동
     3.
     4.
     */
    @GetMapping("/write")
    public String getNoticeWrite(NoticeDto noticeDto){
        return "notice/write";
    }
    @PostMapping("/write")
    public String postNoticeWrite(@Validated NoticeDto noticeDto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "notice/write";
        }
        int rs = noticeService.NoticeInsert(noticeDto);
        if(rs==1){
            return "redirect:/notice/list";
        }
        return "index";
    }

    /*noticeList*/
//    @GetMapping("/List")
//    public String getNoticeList(Model model){
//        List<NoticeDto>  noticeDtoList = noticeService.findAllNoticeList();
//
//        if(!noticeDtoList.isEmpty()){
//            model.addAttribute("noticeDtoList", noticeDtoList);
//            return "notice/List";
//        }
//        System.out.println("조회할 공지사항이 없습니다.");
//        return "redirect:/index";
//    }
 /*
    Todo
     1. rladpwls1843@gamil.com
     2. 공지사항 목록 페이지로 이동
     3.
     4.
     */
    @GetMapping("/list")
    public String getNoticeList(
            @PageableDefault(page = 0, size = 10, sort = "notId", direction = Sort.Direction.DESC) Pageable pageable,
            Model model){

        /*pagingNoticeList noticeList*/
        Page<NoticeDto> noticeList = noticeService.NoticeList(pageable);

        if(noticeList == null){
            throw new IllegalArgumentException("없어");
        }

        Long totalCount = noticeList.getTotalElements();
        int totalPage = noticeList.getTotalPages();
        int pageSize = noticeList.getSize();
        int nowPage = noticeList.getNumber();
        int blockNum = 10;

        int startPage = (int)((Math.floor(nowPage/blockNum)*blockNum) + 1 <= totalPage ?
                (Math.floor(nowPage/blockNum)*blockNum) + 1 : totalPage);
        int endPage = (startPage + blockNum - 1 < totalPage ? startPage + blockNum - 1 : totalPage);

        if(!noticeList.isEmpty()){
            model.addAttribute("noticeList", noticeList);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "notice/list";
        }
        System.out.println("조회할 공지사항이 없다.");

        return "notice/list";
    }

//    @ResponseBody // ajax사용 시 사용해야 하는 어노테이션
    @GetMapping("/list/{type}")
    public String getNoticeList(
            @PageableDefault(page = 0, size = 10, sort = "notId",direction = Sort.Direction.DESC)Pageable pageable,
            Model model,
            @PathVariable("type") String type){

        // type을 가져오고 페이징
        Page<NoticeDto> noticeList = noticeService.noticeList(type,pageable);

        if (noticeList==null) {
         throw new RuntimeException("list none");
        }
        Long totalCount = noticeList.getTotalElements();
        int totalPage = noticeList.getTotalPages();
        int pageSize = noticeList.getSize();
        int nowPage = noticeList.getNumber();
        int blockNum = 10;

        int startPage = (int)((Math.floor(nowPage/blockNum)*blockNum) + 1 <= totalPage ?
                (Math.floor(nowPage/blockNum)*blockNum) + 1 : totalPage);
        int endPage = (startPage + blockNum - 1 < totalPage ? startPage + blockNum - 1 : totalPage);

        if(!noticeList.isEmpty()){
            model.addAttribute("noticeList", noticeList);
            model.addAttribute("startPage", startPage);
            model.addAttribute("endPage", endPage);
            return "notice/list";
        }
        System.out.println("조회할 공지사항이 없다.");

        return "notice/list";
    }

    /*
   Todo
    1. rladpwls1843@gamil.com
    2. 공지사항 검색 페이지로 이동
    3.
    4.
    */
    @GetMapping("/search")
    public String getNoticeSearch(
//            @PageableDefault(page = 0, size = 10, sort = "notId", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(value = "select", required = false) String noticeSelect,
            @RequestParam(value = "search", required = false) String noticeSearch,
            Model model){

        List<NoticeDto> noticeDtoList = noticeService.NoticeListSearch(noticeSelect,noticeSearch);

        if(!noticeDtoList.isEmpty()){
            model.addAttribute("noticeList", noticeDtoList);
            return "notice/searchlist";
        }
//        List<NoticeDto> noticeList = noticeService.NoticeListSearch(pageable);
//
//        if(noticeList != null){
//            model.addAttribute("noticeList", noticeList);
//            return "notice/list";
//        }
        return "redirect:notice/list";
    }

    @GetMapping("/detail/{id}")
    public String getNoticeDetail(@PathVariable("id") Long id, Model model){
        NoticeDto noticeDto = noticeService.NoticeDetail(id);

        if(noticeDto != null){
            model.addAttribute("noticeDto", noticeDto);
            return "notice/detail";
        }
//        model.addAttribute("myUserDetails", myUserDetails);
        return "redirect:/notice/list";
    }

    /*
    TODO
    공지사항 글 수정 페이지로 이동
    공지사항은 수정되면 안되도록 하기로 했는데 할지 말지
    */
    @GetMapping("/update/{id}")
    public String getNoticeUpdate(@PathVariable("id") Long id, Model model){

        NoticeDto noticeDto = noticeService.NoticeUpdate(id);

        if(noticeDto != null){
            model.addAttribute("noticeDto", noticeDto);
            return "notice/update";
        }
        return "redirect:/notice/list";
    }
    @PostMapping("/update/{id}")
    public String postNoticeUpdate(@PathVariable("id") Long id, NoticeDto noticeDto){

//        NoticeDto noticeDto1 = noticeService.NoticeDetail(id);

        int rs = noticeService.NoticeUpdateOk(noticeDto, id);

        if(rs == 1){
            System.out.println("수정 성공");
        }else{
            System.out.println("수정 실패");
        }
        return "redirect:/notice/detail/"+id;
    }

    /*
    TODO
    공지사항 글 삭제 페이지로 이동
    공지사항이 삭제되도 괜찮은지
    */
    @GetMapping("/delete/{id}")
    public String getNoticeDelete(@PathVariable("id") Long id){
        int rs = noticeService.NoticeDelete(id);
        if(rs == 1){
            System.out.println("공지사항 삭제");
        }else{
            System.out.println("공지사항 삭제 실패");
        }
        return "redirect:/notice/list";
    }

}