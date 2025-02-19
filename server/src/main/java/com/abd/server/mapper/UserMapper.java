package com.abd.server.mapper;

import com.abd.server.pojo.Authority;
import com.abd.server.pojo.User;
import com.abd.server.pojo.vo.UserVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT id, username, password, tel, email, cd_time FROM dub_user WHERE username = #{username}")
    @Results({
            @Result(property = "authorities", column = "id", many = @Many(select = "selectAuthoritiesByUserId"))
    })
    User selectUserByUsername(@Param("username") String username);

    @Select("<script>" +
            "SELECT * FROM dub_user\n" +
            "        <where>\n" +
            "            <if test=\"vo.username != null and vo.username != ''\">\n" +
            "                AND username LIKE CONCAT('%', #{vo.username}, '%')\n" +
            "            </if>\n" +
            "        </where>" +
            "</script>")
    @Results({
            @Result(property = "authorities", column = "id", many = @Many(select = "selectAuthoritiesByUserId"))
    })
    Page<User> selectUsersByPage(Page page, @Param("vo") UserVo vo);

    @Select("SELECT a.id, a.authority_name,a.label FROM dub_authority a JOIN dub_user_authority ua ON a.id = ua.authority_id WHERE ua.user_id = #{userId}")
    List<Authority> selectAuthoritiesByUserId(@Param("userId") Long userId);


}
