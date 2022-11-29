package com.example.demo.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor  // 기본생성자가 자동을 만들어준다.
@ToString   //tostring ()메소드를 자동으로 만들어준다.
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private LocalDateTime regdate;


}





//'user_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
//        'email', 'varchar(255)', 'NO', '', NULL, ''
//        'name', 'varchar(50)', 'NO', '', NULL, ''
//        'password', 'varchar(500)', 'NO', '', NULL, ''
//        'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
