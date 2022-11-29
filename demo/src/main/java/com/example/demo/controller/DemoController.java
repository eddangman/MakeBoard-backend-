package com.example.demo.controller;

import com.example.demo.dto.Board;
import com.example.demo.dto.LoginInfo;
import com.example.demo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

//http요청을 받아서 응답을 하는 컴포넌트 스프링부트가 자동으로 bean 으로생성한다.
@Controller
@RequiredArgsConstructor
public class DemoController {


    private final BoardService boardService;
  //게시물목록을 보여준다.
    //컨트롤러의 메소드가 리턴하는문자열은 템플릿이름이다.
    // list를 리턴한다는것은
    //classpath:/templates/list.html
    @GetMapping("/")
    public String list(@RequestParam(name = "page",defaultValue = "1")int page, HttpSession session, Model model){
        //HttpSession Model 은
        // 스프링이자동으로 넣어준다.
         //게시물 목록을 읽어온다. 페이징처리한다.
        LoginInfo logininfo = (LoginInfo) session.getAttribute("loginInfo");
        model.addAttribute("loginInfo",logininfo);//템플릿에게

       int totalCount = boardService.getTotalCount(); //12
       List<Board>  list =boardService.getBoards(page); //page가 1,2,3,4...
        int pageCount =totalCount /10;
        if(totalCount % 10 > 0){ // 나머지가 있을 경우 1페이지를 추가
            pageCount++;
        }
        int currentPage=page;
//        System.out.println("totalCount : " +totalCount);
//        for(Board board : list){
//            System.out.println(board);
//        }
        model.addAttribute("list",list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);

        return "list";


    }
    @GetMapping("/board")
    public String board(
            @RequestParam("boardId") int boardId, Model model){
        System.out.println("id: " +boardId);

        Board board=boardService.getBoard(boardId);
        model.addAttribute("board",board);

        return "board";

        // id에 해당하는 게시물을 읽어온다.
        //id에 해당하는 게시물의 조회수도 1증가해야한다.
    }


    //관리자는 모든글을 삭제할수있다.
    //수정기능


    @GetMapping("/writeForm")
    public String writeForm(HttpSession session, Model model){
        LoginInfo logininfo = (LoginInfo) session.getAttribute("loginInfo");
        if(logininfo == null){ //세션에 로그인정보가없으면 /loginform으로 리다이렉트
            return "redirect:/loginform";
        }
        model.addAttribute("loginInfo",logininfo);

        return "writeForm";
    }//로그인한 사용자만 글쓴다
    //세션에서 로그인한정보를 일겅들인다. 로그인을 하지않았ㅎ다면 리스트보기로 자동이동시킨다.

    @PostMapping("/write")
    public String write(
            @RequestParam("title")String title,
            @RequestParam("content")String content,
            HttpSession session
    ){
        LoginInfo logininfo = (LoginInfo) session.getAttribute("loginInfo");
        if(logininfo == null){ //세션에 로그인정보가없으면 /loginform으로 리다이렉트
            return "redirect:/loginform";
        }

        System.out.println("title: "+title);
        boardService.addBoard(logininfo.getUserId(),title,content);

         //어떤 기능이 필요한지 미리 알수있다.
        //로그인 한 회원정보 제목 내용을 저장한다.
        return "redirect:/";

    }
    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session
    ){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null){
            return "redirect:/loginform";
        }

        //loginInfo.getUserId() 사용자가 쓴글일경우에만 삭제한다.
        List<String> roles= loginInfo.getRoles();
        if(roles.contains("ROLE_ADMIN")){
            boardService.deleteBoard(boardId);
        }else {
            boardService.deleteBoard(loginInfo.getUserId() ,boardId);
        }


        return "redirect:/";

    }
    @GetMapping("/updateform")
    public String updateform( @RequestParam("boardId") int boardId, Model model,
                              HttpSession session){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }


        // boardId 에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달한다.
       Board board= boardService.getBoard(boardId,false);
       model.addAttribute("board",board);
       model.addAttribute("loginInfo",loginInfo);
        return "updateform";
    }
    @PostMapping("/update")
    public String update( @RequestParam("boardId") int boardId,@RequestParam("title")String title,
                          @RequestParam("content")String content, HttpSession session){
        LoginInfo loginInfo = (LoginInfo)session.getAttribute("loginInfo");
        if(loginInfo == null){ // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }
        Board board= boardService.getBoard(boardId,false);
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId="+boardId; //글보기로 이동한다.
        }

        // boardId 에 해당하는 글의 제목과 내용을 수정한다.
                            //글쓴이만 수정가능.
        boardService.updateBoard(boardId,title,content);
        return "redirect:/board?boardId="+boardId; //수정된글보기로 리다이렉트한다.

    }
}
