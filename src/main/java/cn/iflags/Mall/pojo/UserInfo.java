package cn.iflags.Mall.pojo;

import java.util.Date;

public class UserInfo {

    private Integer id;

    private Integer userId;

    private String introduce;

    private String tag;

    private String address;

    private String education;

    private String sex;

    private String industry;

    private Date createTime;

    private Date updateTime;

    public UserInfo(Integer id, Integer userId, String introduce, String tag, String address, String education, String sex, String industry, Date createTime, Date updateTime) {
        this.id = id;
        this.userId = userId;
        this.introduce = introduce;
        this.tag = tag;
        this.address = address;
        this.education = education;
        this.sex = sex;
        this.industry = industry;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public UserInfo() {
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

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce == null ? null : introduce.trim();
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag == null ? null : tag.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education == null ? null : education.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry == null ? null : industry.trim();
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