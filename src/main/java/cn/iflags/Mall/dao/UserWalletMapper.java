package cn.iflags.Mall.dao;

import cn.iflags.Mall.pojo.UserWallet;
import org.apache.ibatis.annotations.Param;

public interface UserWalletMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserWallet record);

    int insertSelective(UserWallet record);

    UserWallet selectByPrimaryKey(Integer id);

    UserWallet selectByUserId(Integer userId);

    UserWallet selectByUserIdPassword(@Param("userId") Integer userId,@Param("password") String password);

    int updateByPrimaryKeySelective(UserWallet record);

    int updateByPrimaryKey(UserWallet record);

    int updataPasswordByUserId(@Param("userId") Integer userId,@Param("password") String password);

    int cheackPassword(@Param("userId") Integer userId,@Param("password") String password);

}