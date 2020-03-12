<%@ page language="java"  contentType="text/html; charset=UTF-8" %>

<html>
<body>
<H1 > 码农flag</H1>
<hr/>
<a href="index.html" title="Gitee" target="_blank"> 接口文档 </a>

<br/>
<hr/>
<H1> 测试组件</H1>
<hr/>
<br/>
<form name="input" action="https://mapi.iflags.cn/user/login.do" method="post" enctype="multipart/form-data" accept-charset="utf-8" target="_blank">
    Username: <input type="text" name="username"  value="admin" placeholder="First name">
    password: <input type="text" name="password" value="admin" placeholder="First password">

    <input type="file" name="fileUpload" οnchange="validate(this)"/>
    <input type="submit" value="Submit">
</form>
<br/>
上传文件
<form name="form1" action="https://mapi.iflags.cn/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="mvc">
</form>
<br>
<br>
富文本上传文件
<form name="form2" action="https://mapi.iflags.cn/iflags_war/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="mvc">
</form>
<script>
    //选择附件的时候直接验证附件的类型
    function validate( para) {
        var fileType = para.value.substring(para.value.lastIndexOf(".") + 1).toLowerCase();
        //如果数据库中设置的文件的后缀为*或者为空，则附件类型不受限制
        if ("*" == affixLimit || "" == affixLimit) {
            return true;
        } else {
            if (affixLimit.toLowerCase().indexOf(fileType) > -1) {
                return true;
            } else {
                $.messager.alert('<s:text name="message.warning"/>', '<s:text name="onlySupport"/>【' + affixLimit + '】<s:text name="affixType"/>');
                //清空文件选择标签
                para.select();
                //重绘标签
                para.outerHTML = para.outerHTML;
                return false;
            }
        }
    }
</script>
</body>
</html>
