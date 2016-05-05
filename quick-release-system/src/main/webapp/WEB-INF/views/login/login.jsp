<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<html>
<body>
    <h3>Summner系统登录</h3>
    <form action="/index/login" method="post">
        <table>
            <tr>
                <td>账号：</td>
                <td><input type="text" name="account"></td>
            </tr>
            <tr>
                <td>密码：</td>
                <td><input type="text" name="password"></td>
            </tr>
            <tr>
                <td><input type="submit" value="登录"></td>
                <td><input type="reset" value="重置"></td>
            </tr>
        </table>
        <div style="color: red">${message}</div>
    </form>
</body>
</html>