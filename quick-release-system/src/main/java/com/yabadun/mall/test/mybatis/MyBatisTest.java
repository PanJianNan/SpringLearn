package com.yabadun.mall.test.mybatis;

import com.alibaba.fastjson.JSONArray;
import com.yabadun.mall.mapper.TUserMapper;
import com.yabadun.mall.domain.TUser;
import com.yabadun.mall.domain.TUserExample;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;

/**
 * Created by panjiannan on 2018/8/1.
 */
public class MyBatisTest {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisTest.class);

    public static void main(String[] args) {
        logger.info("log test hhhhhhhhhhhh!");
        //1.获取SqlSessionFactory
        SqlSessionFactory sqlSessionFactory = SessionFactory.getSqlSessionFactory("mybatis/mybatis-config.xml");
        //2.获取SqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession();//通过SessionFactory工具类（此工具类为自己构造即util包中的SessionFactory）构造SqlSession
        //3.获取mapper接口实现
        TUserMapper tUserMapper = sqlSession.getMapper(TUserMapper.class);
        //4.操作数据库
        TUser user = new TUser();
        user.setAccount("pp3@qq.com");
        user.setName("Pange3");
        user.setPassward("123456");
        System.out.println(tUserMapper.insertSelective(user));
//        sqlSession.commit();//insert后需要手动提交事务才能生效

        TUserExample example = new TUserExample();
        example.createCriteria().andNameLike("%Pange%");
        List<TUser> list = tUserMapper.selectByExample(example);
        TUser userRs = tUserMapper.selectByPrimaryKey(4L);


        TUser userRs2 = tUserMapper.selectByPrimaryKey(4L);
        System.out.println(JSONArray.toJSONString(list));

    }
}

class SessionFactory {

    public static SqlSessionFactory sqlSessionFactory;

    public static SqlSessionFactory getSqlSessionFactory(String resource) {
        if (sqlSessionFactory == null) {
            synchronized (SessionFactory.class) {
                if (sqlSessionFactory == null) {
                    InputStream in = SessionFactory.class.getClassLoader().getResourceAsStream(resource);//Resources.getResourceAsStream();
                    sqlSessionFactory = new SqlSessionFactoryBuilder().build(in);
                }
            }
        }

        return sqlSessionFactory;
    }

}
