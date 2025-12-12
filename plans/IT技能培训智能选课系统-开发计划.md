# IT技能培训智能选课系统 - 开发计划

## 一、前端项目结构（续）

```
it-training-frontend/
├── src/
│   ├── views/                                              # 页面视图
│   │   ├── layout/                                         # 布局
│   │   │   ├── AdminLayout.vue                            # 管理端布局
│   │   │   ├── TeacherLayout.vue                          # 教师端布局
│   │   │   └── StudentLayout.vue                          # 学生端布局
│   │   ├── auth/                                          # 认证相关
│   │   │   ├── Login.vue
│   │   │   ├── Register.vue
│   │   │   └── ForgetPassword.vue
│   │   ├── student/                                       # 学生端页面
│   │   │   ├── Home.vue                                   # 首页
│   │   │   ├── CourseList.vue                             # 课程列表
│   │   │   ├── CourseDetail.vue                           # 课程详情
│   │   │   ├── MyCourses.vue                              # 我的课程
│   │   │   ├── Study.vue                                  # 学习页面
│   │   │   ├── Exam.vue                                   # 考试页面
│   │   │   ├── ExamResult.vue                             # 考试结果
│   │   │   ├── LearningPath.vue                           # 学习路径
│   │   │   ├── SkillTree.vue                              # 技能树
│   │   │   ├── Forum.vue                                  # 论坛
│   │   │   ├── PostDetail.vue                             # 帖子详情
│   │   │   ├── AiRecommend.vue                            # AI推荐
│   │   │   ├── AiChat.vue                                 # AI对话
│   │   │   ├── Profile.vue                                # 个人中心
│   │   │   └── Certificate.vue                            # 证书管理
│   │   ├── teacher/                                       # 教师端页面
│   │   │   ├── Dashboard.vue                              # 工作台
│   │   │   ├── CourseManage.vue                           # 课程管理
│   │   │   ├── CourseEdit.vue                             # 课程编辑
│   │   │   ├── StudentManage.vue                          # 学员管理
│   │   │   ├── ExamManage.vue                             # 考试管理
│   │   │   ├── QuestionBank.vue                           # 题库管理
│   │   │   └── Statistics.vue                             # 数据统计
│   │   └── admin/                                         # 管理端页面
│   │       ├── Dashboard.vue                              # 控制台
│   │       ├── UserManage.vue                             # 用户管理
│   │       ├── CourseAudit.vue                            # 课程审核
│   │       ├── CategoryManage.vue                         # 分类管理
│   │       ├── SystemConfig.vue                           # 系统配置
│   │       ├── NoticeManage.vue                           # 公告管理
│   │       └── LogManage.vue                              # 日志管理
│   ├── router/                                            # 路由配置
│   │   ├── index.js                                       # 主路由
│   │   ├── student.js                                     # 学生路由
│   │   ├── teacher.js                                     # 教师路由
│   │   └── admin.js                                       # 管理员路由
│   ├── store/                                             # 状态管理
│   │   ├── index.js
│   │   ├── modules/
│   │   │   ├── user.js                                    # 用户状态
│   │   │   ├── course.js                                  # 课程状态
│   │   │   ├── enrollment.js                              # 选课状态
│   │   │   └── ai.js                                      # AI状态
│   │   └── getters.js
│   ├── api/                                               # API接口
│   │   ├── auth.js                                        # 认证接口
│   │   ├── user.js                                        # 用户接口
│   │   ├── course.js                                      # 课程接口
│   │   ├── enrollment.js                                  # 选课接口
│   │   ├── study.js                                       # 学习接口
│   │   ├── exam.js                                        # 考试接口
│   │   ├── forum.js                                       # 论坛接口
│   │   ├── ai.js                                          # AI接口
│   │   └── payment.js                                     # 支付接口
│   ├── utils/                                             # 工具函数
│   │   ├── request.js                                     # axios封装
│   │   ├── auth.js                                        # 认证工具
│   │   ├── validate.js                                    # 表单验证
│   │   ├── format.js                                      # 格式化工具
│   │   └── storage.js                                     # 本地存储
│   ├── directives/                                        # 自定义指令
│   │   ├── permission.js                                  # 权限指令
│   │   └── loading.js                                     # 加载指令
│   ├── filters/                                           # 过滤器
│   │   ├── date.js
│   │   └── number.js
│   └── plugins/                                           # 插件
│       ├── element-plus.js
│       └── echarts.js
├── .env.development                                       # 开发环境变量
├── .env.production                                        # 生产环境变量
├── vite.config.js                                         # Vite配置
├── package.json                                           # 依赖配置
└── README.md                                              # 项目说明
```

---

## 二、开发步骤规划

### 阶段一：项目初始化（第1-2周）

#### 1. 环境搭建
- [ ] 安装开发工具（JDK 17、Node.js、MySQL、Redis、RocketMQ）
- [ ] 配置开发环境（IDEA、VS Code）
- [ ] 安装Ollama并下载Qwen3模型
- [ ] 配置Git仓库

#### 2. 项目创建
- [ ] 使用Spring Initializr创建后端项目
- [ ] 使用Vite创建前端项目
- [ ] 配置Maven依赖
- [ ] 配置前端依赖

#### 3. 基础配置
- [ ] 配置数据库连接
- [ ] 配置Redis连接
- [ ] 配置RocketMQ
- [ ] 配置跨域
- [ ] 配置日志
- [ ] 配置Swagger

### 阶段二：核心功能开发（第3-8周）

#### Week 3-4: 用户认证与权限管理
**后端任务：**
- [ ] 实现用户注册/登录功能
- [ ] 集成JWT认证
- [ ] 实现角色权限控制
- [ ] 实现密码加密
- [ ] 实现验证码功能

**前端任务：**
- [ ] 开发登录页面
- [ ] 开发注册页面
- [ ] 实现路由守卫
- [ ] 实现权限指令
- [ ] 开发个人中心页面

**测试：**
- [ ] 单元测试
- [ ] 接口测试
- [ ] 权限测试

#### Week 5-6: 课程管理模块
**后端任务：**
- [ ] 实现课程CRUD接口
- [ ] 实现课程分类管理
- [ ] 实现课程章节管理
- [ ] 实现课程资源上传
- [ ] 实现课程搜索和筛选
- [ ] 实现课程评价功能

**前端任务：**
- [ ] 开发课程列表页面
- [ ] 开发课程详情页面
- [ ] 开发课程编辑页面（教师端）
- [ ] 开发课程审核页面（管理端）
- [ ] 实现视频播放器
- [ ] 实现课程评价组件

**测试：**
- [ ] 功能测试
- [ ] 文件上传测试
- [ ] 性能测试

#### Week 7-8: 选课管理模块（核心）
**后端任务：**
- [ ] 实现选课接口
- [ ] 实现Redis预缓存
- [ ] 实现令牌桶限流
- [ ] 集成RocketMQ异步处理
- [ ] 实现Redisson分布式锁
- [ ] 实现三级防重校验
- [ ] 实现选课状态管理

**前端任务：**
- [ ] 开发选课页面
- [ ] 实现选课倒计时
- [ ] 实现选课状态轮询
- [ ] 开发我的课程页面
- [ ] 实现课程进度展示

**测试：**
- [ ] 高并发压力测试
- [ ] 防重测试
- [ ] 库存一致性测试

### 阶段三：AI智能推荐（第9-10周）

#### Week 9: AI基础集成
**后端任务：**
- [ ] 集成Spring AI
- [ ] 配置Ollama连接
- [ ] 实现AI对话服务
- [ ] 实现Prompt模板管理
- [ ] 实现对话上下文管理

**前端任务：**
- [ ] 开发AI对话界面
- [ ] 实现流式响应展示
- [ ] 实现对话历史记录

#### Week 10: 智能推荐算法
**后端任务：**
- [ ] 实现基于内容的推荐
- [ ] 实现协同过滤推荐
- [ ] 实现学习路径推荐
- [ ] 实现AI智能推荐
- [ ] 实现推荐结果缓存

**前端任务：**
- [ ] 开发推荐页面
- [ ] 开发学习路径页面
- [ ] 开发技能树页面
- [ ] 实现推荐卡片组件

**测试：**
- [ ] 推荐准确性测试
- [ ] AI响应速度测试

### 阶段四：学习与评估（第11-12周）

#### Week 11: 学习管理
**后端任务：**
- [ ] 实现学习进度记录
- [ ] 实现视频播放记录
- [ ] 实现学习笔记功能
- [ ] 实现学习统计

**前端任务：**
- [ ] 开发学习页面
- [ ] 实现进度条组件
- [ ] 开发笔记编辑器
- [ ] 实现学习日历

#### Week 12: 考试评估
**后端任务：**
- [ ] 实现题库管理
- [ ] 实现试卷生成
- [ ] 实现在线考试
- [ ] 实现自动评分
- [ ] 实现证书生成

**前端任务：**
- [ ] 开发考试页面
- [ ] 实现答题组件
- [ ] 实现考试计时器
- [ ] 开发成绩查询页面
- [ ] 开发证书展示页面

### 阶段五：社区与支付（第13-14周）

#### Week 13: 论坛社区
**后端任务：**
- [ ] 实现帖子发布
- [ ] 实现帖子回复
- [ ] 实现点赞功能
- [ ] 实现帖子搜索

**前端任务：**
- [ ] 开发论坛首页
- [ ] 开发帖子详情页
- [ ] 开发发帖编辑器
- [ ] 实现Markdown渲染

#### Week 14: 支付功能（可选）
**后端任务：**
- [ ] 集成支付宝SDK
- [ ] 实现订单创建
- [ ] 实现支付回调
- [ ] 实现退款功能

**前端任务：**
- [ ] 开发支付页面
- [ ] 开发订单管理页面
- [ ] 实现支付状态查询

### 阶段六：系统完善（第15-16周）

#### Week 15: 管理后台
**后端任务：**
- [ ] 实现数据统计接口
- [ ] 实现系统配置管理
- [ ] 实现公告管理
- [ ] 实现日志管理

**前端任务：**
- [ ] 开发管理员控制台
- [ ] 开发数据统计页面
- [ ] 开发系统配置页面
- [ ] 实现ECharts图表

#### Week 16: 优化与测试
- [ ] 代码优化
- [ ] SQL优化
- [ ] 缓存优化
- [ ] 前端性能优化
- [ ] 全面测试
- [ ] Bug修复

### 阶段七：部署上线（第17-18周）

#### Week 17: 部署准备
- [ ] 编写Docker配置
- [ ] 配置Nginx
- [ ] 准备服务器环境
- [ ] 配置域名和SSL证书
- [ ] 数据库迁移

#### Week 18: 上线与文档
- [ ] 部署到服务器
- [ ] 性能监控配置
- [ ] 编写用户手册
- [ ] 编写部署文档
- [ ] 编写API文档
- [ ] 项目总结

---

## 三、关键技术实现方案

### 3.1 高并发选课实现

#### 3.1.1 架构设计
```java
// 选课服务核心流程
@Service
public class EnrollmentService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    @Autowired
    private RedissonClient redissonClient;
    
    // 令牌桶限流
    private final RateLimiter rateLimiter = RateLimiter.create(100.0);
    
    // 本地缓存（Caffeine）
    private final Cache<String, Boolean> localCache = Caffeine.newBuilder()
        .maximumSize(10000)
        .expireAfterWrite(10, TimeUnit.MINUTES)
        .build();
    
    /**
     * 选课接口
     */
    public Result enroll(Long studentId, Long courseId) {
        // 1. 令牌桶限流
        if (!rateLimiter.tryAcquire()) {
            return Result.error("系统繁忙，请稍后再试");
        }
        
        // 2. 本地缓存防重（第一级）
        String localKey = studentId + ":" + courseId;
        if (localCache.getIfPresent(localKey) != null) {
            return Result.error("请勿重复选课");
        }
        
        // 3. Redis防重（第二级）
        String redisKey = "enroll:check:" + studentId + ":" + courseId;
        Boolean exists = redisTemplate.opsForValue()
            .setIfAbsent(redisKey, "1", 5, TimeUnit.MINUTES);
        if (Boolean.FALSE.equals(exists)) {
            return Result.error("请勿重复选课");
        }
        
        // 4. Redis预减库存
        String stockKey = "course:stock:" + courseId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock < 0) {
            // 库存不足，回滚
            redisTemplate.opsForValue().increment(stockKey);
            return Result.error("课程已满");
        }
        
        // 5. 发送MQ消息异步处理
        EnrollmentMessage message = new EnrollmentMessage();
        message.setStudentId(studentId);
        message.setCourseId(courseId);
        message.setTimestamp(System.currentTimeMillis());
        
        rocketMQTemplate.asyncSend("enrollment-topic", message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                // 消息发送成功
                localCache.put(localKey, true);
            }
            
            @Override
            public void onException(Throwable e) {
                // 消息发送失败，回滚库存
                redisTemplate.opsForValue().increment(stockKey);
                redisTemplate.delete(redisKey);
            }
        });
        
        return Result.success("选课请求已提交，请稍候查看结果");
    }
    
    /**
     * MQ消费者 - 异步处理选课
     */
    @RocketMQMessageListener(
        topic = "enrollment-topic",
        consumerGroup = "enrollment-consumer-group"
    )
    public class EnrollmentConsumer implements RocketMQListener<EnrollmentMessage> {
        
        @Override
        public void onMessage(EnrollmentMessage message) {
            Long studentId = message.getStudentId();
            Long courseId = message.getCourseId();
            
            // 获取分布式锁
            String lockKey = "enroll:lock:" + courseId;
            RLock lock = redissonClient.getLock(lockKey);
            
            try {
                // 尝试获取锁，最多等待5秒，锁定10秒后自动释放
                if (lock.tryLock(5, 10, TimeUnit.SECONDS)) {
                    // 数据库防重（第三级）
                    Enrollment existing = enrollmentMapper.selectByStudentAndCourse(
                        studentId, courseId);
                    if (existing != null) {
                        return; // 已选课，直接返回
                    }
                    
                    // 扣减数据库库存
                    int updated = courseMapper.decrementStock(courseId);
                    if (updated == 0) {
                        // 库存不足
                        return;
                    }
                    
                    // 创建选课记录
                    Enrollment enrollment = new Enrollment();
                    enrollment.setStudentId(studentId);
                    enrollment.setCourseId(courseId);
                    enrollment.setEnrollmentTime(new Date());
                    enrollment.setStatus(1);
                    enrollmentMapper.insert(enrollment);
                    
                    // 发送选课成功通知
                    notifyEnrollmentSuccess(studentId, courseId);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }
}
```

#### 3.1.2 数据库优化
```sql
-- 课程表添加库存字段和乐观锁
ALTER TABLE course ADD COLUMN stock INT DEFAULT 0 COMMENT '库存';
ALTER TABLE course ADD COLUMN version INT DEFAULT 0 COMMENT '版本号（乐观锁）';

-- 创建索引
CREATE INDEX idx_course_stock ON course(stock);
CREATE INDEX idx_enrollment_student_course ON enrollment(student_id, course_id);

-- 扣减库存SQL（乐观锁）
UPDATE course 
SET stock = stock - 1, version = version + 1 
WHERE id = #{courseId} AND stock > 0 AND version = #{version};
```

### 3.2 AI智能推荐实现

#### 3.2.1 Spring AI集成
```java
@Service
public class AiRecommendService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @Autowired
    private StudentSkillMapper studentSkillMapper;
    
    /**
     * 基于AI的课程推荐
     */
    public List<CourseRecommendation> recommendCourses(Long studentId) {
        // 1. 获取学员信息
        Student student = studentMapper.selectById(studentId);
        List<StudentSkill> skills = studentSkillMapper.selectByStudentId(studentId);
        List<Enrollment> history = enrollmentMapper.selectByStudentId(studentId);
        
        // 2. 构建Prompt
        String prompt = buildRecommendPrompt(student, skills, history);
        
        // 3. 调用AI模型
        ChatResponse response = chatClient.call(
            new Prompt(prompt,
                OllamaOptions.create()
                    .withModel("qwen3")
                    .withTemperature(0.7)
            )
        );
        
        // 4. 解析AI响应
        String aiResponse = response.getResult().getOutput().getContent();
        List<Long> recommendedCourseIds = parseAiResponse(aiResponse);
        
        // 5. 查询课程详情
        List<Course> courses = courseMapper.selectByIds(recommendedCourseIds);
        
        // 6. 计算推荐分数
        return calculateRecommendationScores(courses, student, skills);
    }
    
    /**
     * 构建推荐Prompt
     */
    private String buildRecommendPrompt(Student student, 
                                       List<StudentSkill> skills,
                                       List<Enrollment> history) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个IT技能培训课程推荐专家。\n\n");
        prompt.append("学员信息：\n");
        prompt.append("- 姓名：").append(student.getRealName()).append("\n");
        prompt.append("- 当前技能：");
        
        for (StudentSkill skill : skills) {
            prompt.append(skill.getSkillName())
                  .append("(")
                  .append(getLevelName(skill.getSkillLevel()))
                  .append("), ");
        }
        
        prompt.append("\n- 已学课程：");
        for (Enrollment e : history) {
            prompt.append(e.getCourseName()).append(", ");
        }
        
        prompt.append("\n\n请根据学员的技能水平和学习历史，");
        prompt.append("从以下课程中推荐5门最适合的课程：\n\n");
        
        // 添加候选课程列表
        List<Course> allCourses = courseMapper.selectAll();
        for (Course course : allCourses) {
            prompt.append("- ").append(course.getCourseName())
                  .append("（难度：").append(course.getDifficultyLevel())
                  .append("，分类：").append(course.getCategoryName())
                  .append("）\n");
        }
        
        prompt.append("\n请以JSON格式返回推荐结果，");
        prompt.append("包含课程ID和推荐理由。");
        
        return prompt.toString();
    }
    
    /**
     * AI对话助手
     */
    public String chat(Long userId, String message, String sessionId) {
        // 1. 获取对话历史
        List<AiConversation> history = conversationMapper
            .selectBySessionId(sessionId);
        
        // 2. 构建上下文
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(
            "你是一个IT技能培训助手，帮助学员解答课程相关问题。"
        ));
        
        for (AiConversation conv : history) {
            messages.add(new UserMessage(conv.getUserMessage()));
            messages.add(new AssistantMessage(conv.getAiResponse()));
        }
        messages.add(new UserMessage(message));
        
        // 3. 调用AI
        ChatResponse response = chatClient.call(new Prompt(messages));
        String aiResponse = response.getResult().getOutput().getContent();
        
        // 4. 保存对话记录
        AiConversation conversation = new AiConversation();
        conversation.setUserId(userId);
        conversation.setSessionId(sessionId);
        conversation.setUserMessage(message);
        conversation.setAiResponse(aiResponse);
        conversationMapper.insert(conversation);
        
        return aiResponse;
    }
}
```

#### 3.2.2 前端AI对话组件
```vue
<template>
  <div class="ai-chat-container">
    <div class="chat-messages" ref="messagesContainer">
      <div 
        v-for="(msg, index) in messages" 
        :key="index"
        :class="['message', msg.role]"
      >
        <div class="message-avatar">
          <img :src="msg.avatar" />
        </div>
        <div class="message-content">
          <div class="message-text" v-html="renderMarkdown(msg.content)"></div>
          <div class="message-time">{{ formatTime(msg.timestamp) }}</div>
        </div>
      </div>
      <div v-if="loading" class="message ai">
        <div class="message-avatar">
          <img src="@/assets/ai-avatar.png" />
        </div>
        <div class="message-content">
          <div class="typing-indicator">
            <span></span><span></span><span></span>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input">
      <el-input
        v-model="inputMessage"
        type="textarea"
        :rows="3"
        placeholder="输入您的问题..."
        @keydown.enter.ctrl="sendMessage"
      />
      <el-button 
        type="primary" 
        @click="sendMessage"
        :loading="loading"
      >
        发送 (Ctrl+Enter)
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { chatWithAi } from '@/api/ai'
import MarkdownIt from 'markdown-it'

const md = new MarkdownIt()
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesContainer = ref(null)
const sessionId = ref(generateSessionId())

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return
  
  // 添加用户消息
  messages.value.push({
    role: 'user',
    content: inputMessage.value,
    avatar: '/user-avatar.png',
    timestamp: Date.now()
  })
  
  const userMessage = inputMessage.value
  inputMessage.value = ''
  loading.value = true
  
  try {
    // 调用AI接口
    const response = await chatWithAi({
      message: userMessage,
      sessionId: sessionId.value
    })
    
    // 添加AI回复
    messages.value.push({
      role: 'ai',
      content: response.data,
      avatar: '/ai-avatar.png',
      timestamp: Date.now()
    })
    
    // 滚动到底部
    await nextTick()
    scrollToBottom()
  } catch (error) {
    console.error('AI对话失败:', error)
  } finally {
    loading.value = false
  }
}

const renderMarkdown = (content) => {
  return md.render(content)
}

const scrollToBottom = () => {
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const generateSessionId = () => {
  return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}
</script>
```

---

## 四、部署方案

### 4.1 Docker部署

#### 4.1.1 Dockerfile（后端）
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/it-training-backend.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]
```

#### 4.1.2 docker-compose.yml
```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: it-training-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: it_training
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql
    networks:
      - it-training-network

  redis:
    image: redis:7-alpine
    container_name: it-training-redis
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - it-training-network

  rocketmq-namesrv:
    image: apache/rocketmq:5.1.0
    container_name: rocketmq-namesrv
    ports:
      - "9876:9876"
    environment:
      JAVA_OPT: "-server -Xms512m -Xmx512m"
    command: sh mqnamesrv
    networks:
      - it-training-network

  rocketmq-broker:
    image: apache/rocketmq:5.1.0
    container_name: rocketmq-broker
    ports:
      - "10909:10909"
      - "10911:10911"
    environment:
      NAMESRV_ADDR: "rocketmq-namesrv:9876"
      JAVA_OPT: "-server -Xms512m -Xm