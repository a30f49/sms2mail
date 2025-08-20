# SMS2Mail - 短信转邮件应用

一个Android应用，可以自动将接收到的短信转发到指定的邮箱地址。

## 功能特性

- 🔔 **实时短信监控** - 后台监控所有接收到的短信
- 📧 **自动邮件转发** - 将短信内容自动发送到指定邮箱
- ⚙️ **灵活配置** - 支持多种邮件服务商（Gmail、Outlook、QQ邮箱等）
- 🔒 **安全可靠** - 本地存储配置，不上传任何个人信息
- 🎯 **前台服务** - 确保应用在后台稳定运行
- 📱 **简洁界面** - 直观的用户界面，易于配置和使用

## 系统要求

- Android 8.1 (API 27) 或更高版本
- 短信和网络权限
- 邮箱账户（支持SMTP）

## 安装使用

### 1. 下载安装
- 下载最新的APK文件
- 在Android设备上安装应用
- 授予必要的权限（短信、网络等）

### 2. 配置邮箱
1. 打开应用
2. 输入发送邮箱账户信息
3. 输入接收邮箱地址
4. 点击"保存设置"

### 3. 启动服务
1. 点击"启动监控服务"
2. 应用将在后台运行
3. 接收到短信时会自动转发到邮箱

## 支持的邮件服务商

| 服务商 | SMTP服务器 | 端口 | 加密 |
|--------|------------|------|------|
| Gmail | smtp.gmail.com | 587 | TLS |
| Outlook/Hotmail | smtp-mail.outlook.com | 587 | TLS |
| Yahoo | smtp.mail.yahoo.com | 587 | TLS |
| QQ邮箱 | smtp.qq.com | 587 | TLS |
| 163邮箱 | smtp.163.com | 25 | 无 |
| 126邮箱 | smtp.126.com | 25 | 无 |

## 权限说明

应用需要以下权限：

- **接收短信** (`RECEIVE_SMS`) - 监听新短信
- **读取短信** (`READ_SMS`) - 读取短信内容
- **网络访问** (`INTERNET`) - 发送邮件
- **前台服务** (`FOREGROUND_SERVICE`) - 后台运行
- **唤醒锁定** (`WAKE_LOCK`) - 保持服务活跃

## 技术架构

### 核心组件
- **MainActivity** - 主界面，配置管理
- **SmsReceiver** - 短信广播接收器
- **SmsMonitorService** - 前台监控服务
- **EmailService** - 邮件发送服务
- **EmailConfigManager** - 配置管理器

### 技术栈
- **开发语言**: Java
- **最低SDK**: API 27 (Android 8.1)
- **目标SDK**: API 34 (Android 14)
- **邮件库**: JavaMail API for Android
- **UI框架**: Material Design Components

## 构建项目

### 环境要求
- Android Studio Arctic Fox 或更高版本
- JDK 8 或更高版本
- Android SDK 34

### 构建步骤
```bash
# 克隆项目
git clone https://github.com/your-username/sms2mail.git
cd sms2mail

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease
```

## 安全说明

- 所有邮箱配置信息仅存储在本地设备
- 不会上传任何短信内容或个人信息到服务器
- 建议使用应用专用密码而非主密码
- 定期更新应用以获得安全修复

## 故障排除

### 常见问题

**Q: 短信没有转发到邮箱？**
A: 检查以下项目：
- 确认已授予短信权限
- 检查邮箱配置是否正确
- 确认网络连接正常
- 查看应用日志获取详细错误信息

**Q: Gmail发送失败？**
A: Gmail需要使用应用专用密码：
1. 开启两步验证
2. 生成应用专用密码
3. 使用应用专用密码而非账户密码

**Q: 服务经常停止？**
A: 在系统设置中：
- 关闭电池优化
- 允许后台运行
- 设置为自启动应用

## 更新日志

### v1.0.0 (2025-08-19)
- ✨ 初始版本发布
- 🔔 支持短信实时监控
- 📧 支持多种邮件服务商
- ⚙️ 完整的配置管理
- 🔒 安全的本地存储
- 📱 Material Design界面

## 贡献指南

欢迎提交Issue和Pull Request！

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交Issue: [GitHub Issues](https://github.com/your-username/sms2mail/issues)
- 邮箱: your-email@example.com

---

**免责声明**: 本应用仅用于个人学习和研究目的。使用时请遵守当地法律法规，尊重他人隐私。开发者不承担因使用本应用而产生的任何责任。