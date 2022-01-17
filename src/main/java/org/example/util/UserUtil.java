package org.example.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.entity.SecKillUser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserUtil {
    private static void createUser(int count) throws Exception {
        List<SecKillUser> users = new ArrayList<SecKillUser>(count);

        //生成用户
        for (int i = 0; i < count; i++) {
            SecKillUser user = new SecKillUser();
            user.setId(13000000000L + i);
            user.setLoginCount(1);
            user.setNickname("user" + i);
            user.setRegisterDate(new Date());
            user.setSalt("1a2b3c");
            user.setPassword(MD5Util.inputPassToDBPass("123456", user.getSalt()));
            users.add(user);
        }
        System.out.println("create user");

        //插入数据库
        Connection conn = DBUtil.getConn();
        String sql = "insert into USERS(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        for (SecKillUser user : users) {
            preparedStatement.setInt(1, user.getLoginCount());
            preparedStatement.setString(2, user.getNickname());
            preparedStatement.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            preparedStatement.setString(4, user.getSalt());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setLong(6, user.getId());
            preparedStatement.addBatch();
        }
        preparedStatement.executeBatch();
        preparedStatement.close();
        conn.close();
        System.out.println("insert to db");
        //登录，生成token
        String urlString = "http://localhost:8080/login/do_login";
        File file = new File("D:/tokens.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        file.createNewFile();
        randomAccessFile.seek(0);
        for (SecKillUser user : users) {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFormPass("123456");
            outputStream.write(params.getBytes());
            outputStream.flush();
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                byteArrayOutputStream.write(buff, 0, len);
            }
            inputStream.close();
            byteArrayOutputStream.close();
            String response = new String(byteArrayOutputStream.toByteArray());
            JSONObject jsonObject = JSON.parseObject(response);
            String token = jsonObject.getString("data");
            System.out.println("create token : " + user.getId());

            String row = user.getId() + "," + token;
            randomAccessFile.seek(randomAccessFile.length());
            randomAccessFile.write(row.getBytes());
            randomAccessFile.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        randomAccessFile.close();
        System.out.println("over");
    }

    public static void main(String[] args) throws Exception {
        createUser(5000);
    }
}
