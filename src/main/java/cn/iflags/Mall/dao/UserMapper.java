package cn.iflags.Mall.dao;

import cn.iflags.Mall.common.ServerResponse;
import cn.iflags.Mall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    //检查用户是否存在 返回满足条件用户数量
    int checkUserName(String username);

    //检测邮箱是否存在 返回满足条件用户数量
    int checkEmail(String email);

    //统计用户数
    int userCount();

    //多个参数需要用@Param(参数一致名称)
    User selectLogin(@Param("username") String username,@Param("password")String password);

    //通过名字获取问题
    String selectQuestionByUsername(String question);

    //通过名字获取ID
    Integer selectIDByUsername(String username);

    //校验答案 返回满足条件用户数量
    int checkAnswer(@Param("username")String username, @Param("question")String question, @Param("answer")String answer);

    //修改密码
    int updataPasswordByUsername(@Param("username") String username,@Param("password")String password);

    //校验密码是否是当前用户
    int cheackPassword(@Param("password")String password, @Param("userId")Integer userId);

    //校验不是当前邮箱是否存在
    int checkEmailByUserId(@Param("email")String email, @Param("userId")Integer userId);

}