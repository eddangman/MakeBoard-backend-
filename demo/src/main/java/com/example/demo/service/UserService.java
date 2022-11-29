package com.example.demo.service;


import com.example.demo.dao.UserDao;
import com.example.demo.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//트랜잭션 단위로 실행될 메소드를 선언하고 있는 인터페이스
// 스프링이 관리하는 bean
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserDao userDao;



    //spring이 userservice를 bean으로 생성할때 생성자를 이용해 생성을 하는데 이떄  userdao bean이 있는지 보고
    // 그빈을 주입한다. 생성자 주입
  //보통서비스에서는 @Transactional 을 붙여서 하나의 트랜잭션으로 처리하게 한다.
    //스프링부트는 트랜잭션을 처리해주는 트랜잭션 관리자를 가지고 있다.
    @Transactional
    public User addUser(String name, String email, String password){

        //트랜잭션이 시작된다.
        User user1 =userDao.getUser(email); //이메일 중복검사
        if(user1 != null){
            throw new RuntimeException("이미 가입된 이메일입니다.");
        }
        //트랜잭션이 첫번째로 시작한다.
        User user= userDao.addUser(email, name, password);
        userDao.mappingUserRole(user.getUserId()); //권한을 부여한다.
        return user;
        //트랜잭션이 끝난다.
    }


    @Transactional
    public User getUser(String email){
        return userDao.getUser(email);
    }

    @Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        return userDao.getRoles(userId);
    }
}
