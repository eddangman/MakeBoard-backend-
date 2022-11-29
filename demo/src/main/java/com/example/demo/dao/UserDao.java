package com.example.demo.dao;

import com.example.demo.dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

//스프링이 관리하는 bean
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsertOperations insertUser;

    public UserDao(DataSource dataSource){
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertUser= new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id");
        //자동으로 증가되는 id를 설정

    }
    //스프링 jdbc에 관한 코드를 작성


    @Transactional
    public User addUser(String email, String name, String password){
        //서비스에서 이미 트랜잭션이 시작햇기때문에 그트랜잭션이 포함된다.
        //insert into board(email,name,password,regdate)values(:email,:name,:password,regdate,now());#user_id auto gen

        //select last_insert_id();
        User user =new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRegdate(LocalDateTime.now());


        SqlParameterSource params= new BeanPropertySqlParameterSource(user);
        Number number= insertUser.executeAndReturnKey(params);
        //insert를 실행 하고 자동으로 생성된 id를 가져온다.
        int userId= number.intValue();
        user.setUserId(userId);
        return user;
    }

    @Transactional
public void  mappingUserRole(int userId){
        //서비스에서 이미 트랜잭션이 시작햇기때문에 그트랜색션이 포함된다.
    //      insert into user_role(user_id,role_id) values(?,1);
        String sql="insert into user_role(user_id, role_id) values(:userId,1)";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);
        jdbcTemplate.update(sql,params);
}

@Transactional
    public User getUser(String email) {
       try {
           String sql ="select user_id, email, name, password, regdate from user where email = :email ";
           SqlParameterSource params = new MapSqlParameterSource("email", email);
           RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
           User user =jdbcTemplate.queryForObject(sql,params, rowMapper );
           return user;

       }catch (Exception ex){
           return null;
       }


    }
@Transactional(readOnly = true)
    public List<String> getRoles(int userId) {
        String sql="select r.name from user_role ur, role r where ur.role_id=r.role_id and ur.user_id= :userId";
    List<String> roles = jdbcTemplate.query(sql, Map.of("userId", userId), (rs, rowNum) -> {
        return rs.getString(1);
    });
    return roles;
    }
}
//    insert into board(email,name,password,regdate)values(?,?,?,now());#user_id auto gen
//      select last_insert_id();
//      insert into user_role(user_id,role_id) values(?,1);