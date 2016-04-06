# SpringLearn
learning spring and making my spring


环境：
1、jdk 1.7
2、apache-tomcat-7.0.39


发展历程》》》：
1、完成Summer MVC框架基本搭建，实现IOC
    不足： 1）目前仅支持单例Bean
           2）MVC关系不够紧密，缺少视图解析
           3）IOC效率低下
           4）XML和注释混合使用，不够清晰
           5) bean初始化顺序并非递归式，而是按层次

2、实现视图解析
    不足   1）请求参数封装，调用方法参数个数不定等未解决