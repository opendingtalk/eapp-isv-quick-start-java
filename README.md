
## 运行环境开发工具
java8
intellij idea

## 项目结构
```
.
├── README.md
├── eapp-isv-quick-start.iml
├── lib
│   ├── taobao-sdk-java-auto_1479188381469-20180525-source.jar
│   └── taobao-sdk-java-auto_1479188381469-20180525.jar
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       ├── Application.java
    │   │       ├── config
    │   │       │   ├── Constant.java
    │   │       │   └── URLConstant.java
    │   │       ├── controller
    │   │       │   └── IndexController.java
    │   │       └── util
    │   │           └── ServiceResult.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── ApplicationTests.java
```
                    
                
## 项目配置
1.更新Constant.java文件的SUITE_KEY，SUITE_SECRET三个属性。  
具体数值值请登录[开发者后台套件列表](https://open-dev.dingtalk.com/#/isveapp)，查看套件详情中获取

2.更新application.properties文件的服务器启动端口。

## 打包命令
mvn clean package  -Dmaven.test.skip=true  
打成的包在工程文件的target目录下。文件为  "工程名"-"版本号".jar。()

## 服务部署    
java -jar  target/"工程名"-"版本号".jar
