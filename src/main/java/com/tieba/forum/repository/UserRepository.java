package com.tieba.forum.repository;

import com.tieba.forum.entity.User;
import com.tieba.forum.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    // 查询用户 DTO（不包含密码）
    @Query("SELECT new com.tieba.forum.dto.UserDto(" +
           "u.id, u.username, u.avatar, u.level, u.exp, u.createdAt) " +
           "FROM User u WHERE u.id = :id")
    UserDto findUserDtoById(@Param("id") Long id);
    
    // 搜索用户（按用户名模糊查询）
    @Query("SELECT new com.tieba.forum.dto.UserDto(" +
           "u.id, u.username, u.avatar, u.level, u.exp, u.createdAt) " +
           "FROM User u WHERE u.username LIKE %:keyword% ORDER BY u.exp DESC")
    List<UserDto> searchUserDtos(@Param("keyword") String keyword);
}
