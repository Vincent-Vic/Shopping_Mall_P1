package cn.iflags.Mall.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class UserWallet {
    private Integer id;

    private Integer userId;

    private BigDecimal balance;

    private BigDecimal coin;

    private String payPassword;

    private Date lastLogin;

    private Date createTime;

    private Date updateTime;

    public UserWallet(Integer id, Integer userId, BigDecimal balance, BigDecimal coin, String payPassword, Date lastLogin, Date createTime, Date updateTime) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.coin = coin;
        this.payPassword = payPassword;
        this.lastLogin = lastLogin;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public UserWallet() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getCoin() {
        return coin;
    }

    public void setCoin(BigDecimal coin) {
        this.coin = coin;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword == null ? null : payPassword.trim();
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}