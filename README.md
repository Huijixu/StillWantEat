## 项目基本信息
- 项目名称：StillWantEat
- 业务描述： 外卖订餐后端管理与前端用户订餐 （前后端未分离版）
- 主要技术选型 
    - 后端： springboot + mybatis-plus 
    - 前端： vue
    - 持久化： mysql + redis
- 本地后端登录测试地址： http://localhost:8080/backend/page/login/login.html
- 本地前端登录测试地址： http://localhost:8080/front/page/login.html
- 项目管理员登录：
  - account： admin
  - password： 123456
- 员工新增初始密码为123456

## 项目亮点
- 登录验证
  - 移动端短信验证码登录
  - C端： 小程序
- 关联关系：套餐，菜品等的多表事务
- 性能优化 ：缓存redis
- 项目迭代开发
- 部分代码注释较为详细，易于阅读

## version-2.0功能简介
- 登录登出验证
- 员工管理
- 食物分类管理  
   - 公共字段自动填充
   - CURD
- 菜品管理
   - 文件上传、下载
   - 向两张表中新增，修改 数据
- 套餐管理
  - CURD
  - 套餐起售停售（批量未写）

- 用户端
   - 手机验证码登录
   - 菜品展示
   - 购物车
   - 用户下单
     限于个人资质原因，并没有实际发开支付功能 ，只是把三张表数据提交到数据库里

## 项目运行
- 如要使用短信验证码功能，请到阿里云申请该服务，并将数字签名验证码等参数填入java/com.huijixu/controller/UserController的SMSUtils.sendMessage中)
- 数据库密码/数据库名/时区等自行修改
- 图片资源保存目录请在application.yml中修改
