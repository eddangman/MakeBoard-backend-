package com.example.demo.controller;


import com.example.demo.dto.LoginInfo;
import com.example.demo.dto.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


    @GetMapping("/userRegForm")
    public String userRegForm(){
        return "userRegForm";
    }


@PostMapping("/userReg")
    public String userReg(@RequestParam("name") String name,
                          @RequestParam("email") String email,
                          @RequestParam("password") String password

){
        //email에 해당되는 회원정보를 읽어온후
        //아이디 암호가 맞다면 세션에서 회원정보를 저장한다.
    System.out.println("email: "+ email);
    System.out.println("password: "+ password);

    userService.addUser(name, email, password);

    //어떤기능이 필요한지 미리 알수있다.
    //회원정보를 저장한다.

        return "redirect:/welcome";//브라우저에게 자동으로 localhost 8080/welcome 으로이동
        }

        @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
        }
    @GetMapping("/loginform")
    public String loginform(){
        return "loginform";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession httpSession //스프링이 자동으로 session을 처리하는 http세션 객체를 넣어준다.
    ){// email에 해당되는 회원정보를 읽어온후
        //아이디 암호가 맞다면 세션에 회원정보를 저장한다.
        System.out.println("email: "+ email);
        System.out.println("password: "+ password);
       try{
            User user = userService.getUser(email); //f8키가 스택오버
            if(user.getPassword().equals(password)){
                System.out.println("암호가 같습니다.");
                LoginInfo logininfo =new LoginInfo(user.getUserId(), user.getEmail(),user.getName());

                //권한정보를 일겅와서 loginInfo에 추가한다.

               List<String> roles= userService.getRoles(user.getUserId());
               logininfo.setRoles(roles);

                httpSession.setAttribute("loginInfo",logininfo);//첫번째 파라미터가key,두번째 파라미터가 값.
                System.out.println("세션에 로그인정보가 저장된다.");


            }else {
                throw new RuntimeException("암호가 일치하지 않음.");
            }
        }catch (Exception ex){
           return "redirect:/loginform?error=true";
       }


         return "redirect:/";
    }
   @GetMapping("/logout")

    public String logout(HttpSession httpSession){
        //세션에서 회원정보를 삭제한다.
       //302 신호가 리다이렉트이다.
        httpSession.removeAttribute("loginInfo");
        return "redirect:/";
    }



}
