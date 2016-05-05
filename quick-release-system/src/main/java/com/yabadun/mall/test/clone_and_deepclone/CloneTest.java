package com.yabadun.mall.test.clone_and_deepclone;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * CloneTest
 *
 * @author panjn
 * @date 2016/4/12
 */
public class CloneTest {
    public static void main(String[] args) throws CloneNotSupportedException, IOException, ClassNotFoundException {
        Person pzb = new Person("pzb", 48, null, null);
        Person zmc = new Person("zmc", 49, null, null);
        Person pjn = new Person("pjn", 25, pzb, zmc);
        Person cloner = (Person) pjn.clone();
        Person deepCloner = (Person) pjn.deepClone();
        pjn.setName("pjn_new");
        pjn.setAge(26);
        pzb.setAge(50);
        System.out.println(cloner);

        DecimalFormat df = new DecimalFormat("##.000");
        System.out.println(df.format(0.07));
        String shopUrl = "http://www.mPai.jia.cn/haha";
        if (!shopUrl.startsWith("http")) {
            shopUrl = "http://" + shopUrl;
        }
        String urlRegex = "^(https?)://[a-zA-Z0-9.]*\\.com[-a-zA-Z0-9+&@#/%=~_|]*";
        if (shopUrl.matches(urlRegex)) {
            System.out.println("right");
        } else {
            System.out.println("false");
        }

        System.out.println("<abc>def".replaceAll("[\\t\\n\\r<>;'\"]", " "));
    }
}
