# 微信小程序配置说明

## 1. 获取微信小程序AppID和AppSecret

1. 登录 [微信公众平台](https://mp.weixin.qq.com/)
2. 进入小程序管理后台
3. 在"开发" -> "开发管理" -> "开发设置"中找到：
   - **AppID (小程序ID)**
   - **AppSecret (小程序密钥)**

## 2. 配置后端

在 `src/main/resources/application.yml` 中配置：

```yaml
# 微信小程序配置
weixin:
  app-id: your_actual_app_id_here
  app-secret: your_actual_app_secret_here
```

**重要**：请将 `your_actual_app_id_here` 和 `your_actual_app_secret_here` 替换为实际的AppID和AppSecret。

## 3. 微信小程序前端配置

在小程序项目中，确保 `app.json` 中配置了正确的AppID：

```json
{
  "pages": [...],
  "window": {...},
  "tabBar": {...},
  "sitemapLocation": "sitemap.json"
}
```

## 4. 测试登录流程

1. 启动后端服务
2. 在小程序中点击"微信授权登录"
3. 后端会调用微信API获取真实的openid和session_key
4. 创建或更新用户信息
5. 返回JWT token

## 5. 注意事项

- AppSecret 是敏感信息，不要提交到版本控制系统
- 生产环境建议使用环境变量或配置中心管理敏感配置
- 确保小程序的AppID与后端配置的AppID一致

## 6. 错误排查

如果登录失败，检查：

1. AppID和AppSecret是否正确
2. 网络是否能访问微信API
3. 后端日志中的错误信息
4. 小程序的code是否有效（code只能使用一次，且有时效性）

