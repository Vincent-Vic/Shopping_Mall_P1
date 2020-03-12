package cn.iflags.Mall.vo;

import cn.iflags.Mall.common.Const;
import cn.iflags.Mall.pojo.User;
import cn.iflags.Mall.pojo.UserInfo;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述:用户所有信息（不包含密码，问题，答案）
 * @author Vincent Vic
 * create 2020-02-27 15:40
 */


public class UserVo {

    private String username;

    private String email;

    private String phone;

    private Integer role;

    private String roledesc;

    private String introduce;

    private String tag;

    private Map<String,String> address;

    private String education;

    private String sex;

    private String industry;

    public UserVo() {
    }

    public UserVo(User user, UserInfo userInfo) {
        address = Maps.newHashMap();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.role = user.getRole();
        this.roledesc = this.role == Const.Role.ROLE_ADMIN ? "管理员":"会员";
        if (userInfo != null) {
            this.introduce = userInfo.getIntroduce();
            this.tag = userInfo.getTag();
            //设置城市信息，方便前台使用
            String [] addressKey ={"province","city","district","detailed"};
            List<String> productIdList = Splitter.on(",").splitToList(userInfo.getAddress());
            for (int i = 0; i < productIdList.size(); i++) {
                this.address.put(addressKey[i],productIdList.get(i));
            }
            this.education = userInfo.getEducation();
            this.sex = userInfo.getSex();
            this.industry = userInfo.getIndustry();
        }
    }

    public UserVo(String username, String email, String phone, Integer role, String roledesc, String introduce, String tag, Map<String, String> address, String education, String sex, String industry) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.roledesc = roledesc;
        this.introduce = introduce;
        this.tag = tag;
        this.address = address;
        this.education = education;
        this.sex = sex;
        this.industry = industry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getRoledesc() {
        return roledesc;
    }

    public void setRoledesc(String roledesc) {
        this.roledesc = roledesc;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<String, String> getAddress() {
        return address;
    }

    public void setAddress(Map<String, String> address) {
        this.address = address;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}