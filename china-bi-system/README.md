# 中国BI系统 (China BI System)

## 项目概述

中国BI系统是一个用于展示全球酒店运营数据的BI报表系统，它负责读取 `china_bi_system` 数据库的数据，进行展示，并且可以生成Mock中国地区数据，从而展示在中国总部查看全球区域运营数据的能力。

## 技术架构

- **框架**: Spring Boot 3.3.1
- **Java版本**: 17
- **模板引擎**: Thymeleaf
- **数据库**: MySQL
- **ORM框架**: MyBatis Plus
- **端口**: 8081

## 数据库字段更新

### 添加region字段

由于系统升级需要支持多地区数据展示，需要为现有数据库表添加`region`字段：

1. **执行DDL语句**：
   ```bash
   # 连接到MySQL数据库
   mysql -u root -p
   
   # 执行添加region字段的DDL
   source /path/to/china-bi-system/src/main/resources/sql/add_region_field.sql
   ```

2. **插入虚拟中国地区数据**：
   ```bash
   # 执行插入中国地区Mock数据的SQL
   source /path/to/china-bi-system/src/main/resources/sql/mock_china_data.sql
   ```

### 字段说明

- **region字段**: 用于区分不同地区的数据（EU、CHINA、US等）
- **默认值**: 现有EU数据自动标记为'EU'
- **索引**: 为region字段创建索引以提高查询性能

## 系统访问地址

### 🏨 EU酒店预订系统 (EU Hotel Reservation System)
- **端口**: 8080
- **访问地址**: http://localhost:8080/
- **数据同步监控**: http://localhost:8080/data-sync-monitor
- **API接口**: http://localhost:8080/api/data-sync/*

### 🌍 中国BI系统 (China BI System)
- **端口**: 8081
- **访问地址**: http://localhost:8081/

#### 主要页面
- **首页 - 全球业务数据概览**: http://localhost:8081/
- **收入统计报表**: http://localhost:8081/income
- **房型分析报表**: http://localhost:8081/room-types
- **数据管理**: http://localhost:8081/data-management

#### 功能特性
- **实时数据展示**: 显示EU地区同步的运营数据
- **Mock数据生成**: 生成中国地区模拟数据
- **全球业务监控**: 在中国总部查看全球运营状况

## 主要功能

### 1. 全球业务数据概览
- 显示EU地区的实时运营数据
- 展示今日收入、预订数等关键指标
- 显示最近7天的累计数据

### 2. 热门房型分析
- 展示EU地区热门房型Top5
- 分析房型预订趋势和收入贡献

### 3. 收入统计报表
- 详细的收入分析报表
- 支持7天、30天等不同时间维度

### 4. Mock数据生成
- 生成模拟的中国地区酒店运营数据
- 用于演示全球数据展示功能

## 数据来源

### EU地区数据
- 来源：EU酒店预订系统
- 同步频率：每15秒
- 数据特点：完全匿名化，无PII信息

### 中国地区数据
- 来源：Mock数据生成
- 特点：模拟真实业务场景
- 用途：展示全球业务监控能力

## 快速开始

### 1. 环境要求
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
确保 `china_bi_system` 数据库已创建并运行，配置信息在 `application.yml` 中。

### 3. 启动EU系统
```bash
cd hotel-reservation-eu
mvn spring-boot:run
```
EU系统将在 http://localhost:8080/ 启动

### 4. 启动中国BI系统
```bash
cd china-bi-system
mvn spring-boot:run
```
中国BI系统将在 http://localhost:8081/ 启动

### 5. 访问系统
#### EU系统访问
- **主页**: http://localhost:8080/
- **数据同步监控**: http://localhost:8080/data-sync-monitor
- **同步状态API**: http://localhost:8080/api/data-sync/status
- **手动同步API**: http://localhost:8080/api/data-sync/manual-sync

#### 中国BI系统访问
- **首页**: http://localhost:8081/
- **收入报表**: http://localhost:8081/income
- **房型分析**: http://localhost:8081/room-types
- **数据管理**: http://localhost:8081/data-management

## 系统集成流程

```
1. EU系统启动 (8080端口)
   ↓
2. 定时任务每15秒同步数据到中国BI数据库
   ↓
3. 中国BI系统启动 (8081端口)
   ↓
4. 读取数据库数据并展示
   ↓
5. 在中国总部查看全球业务数据
```

## 项目结构

```
china-bi-system/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hotelbooking/china_bi_system/
│   │   │       ├── controller/          # 控制器层
│   │   │       ├── service/             # 服务层
│   │   │       ├── mapper/              # 数据访问层
│   │   │       ├── model/               # 数据模型
│   │   │       └── ChinaBiSystemApplication.java
│   │   └── resources/
│   │       ├── templates/               # Thymeleaf模板
│   │       ├── static/                  # 静态资源
│   │       └── application.yml          # 配置文件
├── pom.xml                              # Maven配置
└── README.md                            # 项目说明
```

## 核心特性

### 1. 实时数据展示
- 从EU系统同步的实时运营数据
- 自动更新的统计指标

### 2. 响应式设计
- 支持桌面和移动设备
- 现代化的UI设计

### 3. 数据安全
- 所有敏感数据已脱敏
- 符合GDPR等数据保护法规

### 4. 可扩展性
- 模块化设计
- 易于添加新的报表类型

## 部署说明

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
mvn clean package
java -jar target/china-bi-system-0.0.1-SNAPSHOT.jar
```

## 使用说明

### 1. 首次启动
1. 确保MySQL数据库运行正常
2. 启动EU系统 (8080端口)
3. 启动中国BI系统 (8081端口)
4. 访问 http://localhost:8081/ 查看首页

### 2. 生成Mock数据
1. 访问中国BI系统首页
2. 点击"生成Mock数据"按钮
3. 系统将自动生成最近7天的模拟数据

### 3. 查看实时数据
1. EU系统会自动同步数据到中国BI数据库
2. 中国BI系统实时读取并展示这些数据
3. 每15秒数据自动更新

## 注意事项

1. 确保MySQL数据库服务正在运行
2. 检查数据库连接配置是否正确
3. 首次启动时可能需要生成Mock数据
4. 系统依赖EU系统的数据同步服务
5. 两个系统需要同时运行才能实现完整功能

## 故障排除

### 常见问题
1. **数据库连接失败**: 检查MySQL服务状态和连接配置
2. **页面显示异常**: 检查浏览器控制台错误信息
3. **数据不更新**: 确认EU系统正在运行并同步数据

### 日志查看
- EU系统日志: `hotel-reservation-eu/logs/hotel-reservation-eu.log`
- 中国BI系统日志: `china-bi-system/logs/china-bi-system.log`

## 联系方式

如有问题或建议，请联系开发团队。
