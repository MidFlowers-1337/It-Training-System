# IT 技能培训智能选课系统 - 安全代码审查报告

> 审查日期：2025-12-14
> 审查人：Security Reviewer
> 审查范围：后端安全配置、认证授权、数据保护、前端安全

---

## 📊 安全审查概要

| 安全领域 | 评分 | 风险等级 |
|----------|------|----------|
| **认证与授权** | ⭐⭐⭐⭐ (4/5) | 🟢 低风险 |
| **数据保护** | ⭐⭐⭐⭐ (4/5) | 🟢 低风险 |
| **配置安全** | ⭐⭐⭐ (3/5) | 🟡 中风险 |
| **输入验证** | ⭐⭐⭐⭐⭐ (5/5) | 🟢 低风险 |
| **日志与监控** | ⭐⭐⭐⭐ (4/5) | 🟢 低风险 |
| **前端安全** | ⭐⭐⭐ (3/5) | 🟡 中风险 |

**整体安全评分：⭐⭐⭐⭐ (4/5) - 良好**

---

## ✅ 安全优点

### 1. JWT 认证实现良好

**文件**: [`JwtTokenProvider.java`](../../it-training-backend/src/main/java/com/itts/common/security/JwtTokenProvider.java)

```java
// ✅ 使用 HMAC-SHA256 签名算法
private SecretKey getSigningKey() {
    byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
}

// ✅ 完善的 Token 验证和异常处理
public boolean validateToken(String token) {
    try {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
        return true;
    } catch (SignatureException e) {
        log.error("无效的JWT签名: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
        log.error("JWT已过期: {}", e.getMessage());
    }
    // ...
    return false;
}
```

**优点分析：**
- ✅ 使用安全的 HMAC-SHA256 签名算法
- ✅ 密钥通过环境变量配置，支持生产环境覆盖
- ✅ 完善的异常处理和日志记录
- ✅ Token 过期时间可配置

### 2. 密码安全处理

**文件**: [`SecurityConfig.java`](../../it-training-backend/src/main/java/com/itts/common/config/SecurityConfig.java)

```java
// ✅ 使用 BCrypt 密码编码器
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**优点分析：**
- ✅ 使用 BCrypt 算法（自动加盐）
- ✅ 密码从不以明文存储
- ✅ 密码验证使用 `passwordEncoder.matches()`

### 3. 完善的输入验证

**文件**: [`RegisterRequest.java`](../../it-training-backend/src/main/java/com/itts/modules/auth/dto/RegisterRequest.java)

```java
// ✅ 用户名验证
@NotBlank(message = "用户名不能为空")
@Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
@Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
private String username;

// ✅ 密码验证
@NotBlank(message = "密码不能为空")
@Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
private String password;

// ✅ 手机号验证
@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
private String phone;

// ✅ 邮箱验证
@Email(message = "邮箱格式不正确")
private String email;
```

**优点分析：**
- ✅ 使用 Jakarta Validation 进行输入验证
- ✅ 用户名限制特殊字符，防止注入攻击
- ✅ 密码长度要求合理
- ✅ 手机号和邮箱格式验证

### 4. 角色权限控制

**文件**: [`SecurityConfig.java`](../../it-training-backend/src/main/java/com/itts/common/config/SecurityConfig.java)

```java
// ✅ 基于角色的访问控制
.authorizeHttpRequests(auth -> auth
    // 公开接口
    .requestMatchers("/api/auth/**").permitAll()
    
    // 管理员专属接口
    .requestMatchers("/api/users/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/api/courses/**").hasRole("ADMIN")
    
    // 学员专属接口
    .requestMatchers("/api/ai/**").hasRole("STUDENT")
    .requestMatchers(HttpMethod.POST, "/api/enrollments").hasRole("STUDENT")
    
    // 其他需要认证
    .anyRequest().authenticated()
)
```

**优点分析：**
- ✅ 明确的角色权限划分
- ✅ 使用 Spring Security 的声明式权限控制
- ✅ 默认拒绝策略（anyRequest().authenticated()）

### 5. 敏感数据脱敏

**文件**: [`ProfileServiceImpl.java`](../../it-training-backend/src/main/java/com/itts/modules/user/service/impl/ProfileServiceImpl.java)

```java
// ✅ 邮箱脱敏
private String maskEmail(String email) {
    if (email == null || email.isEmpty()) {
        return null;
    }
    int atIndex = email.indexOf('@');
    if (atIndex <= 1) {
        return email;
    }
    return email.substring(0, 1) + "***" + email.substring(atIndex);
}

// ✅ 手机号脱敏
private String maskPhone(String phone) {
    if (phone == null || phone.length() < 7) {
        return phone;
    }
    return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
}
```

**优点分析：**
- ✅ 敏感信息在返回前进行脱敏处理
- ✅ 邮箱和手机号都有脱敏逻辑

### 6. 全局异常处理

**文件**: [`GlobalExceptionHandler.java`](../../it-training-backend/src/main/java/com/itts/common/exception/GlobalExceptionHandler.java)

```java
// ✅ 不暴露内部错误详情
@ExceptionHandler(Exception.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public R<Void> handleException(Exception e) {
    log.error("系统异常: ", e);
    return R.fail(ErrorCode.INTERNAL_ERROR.getCode(), "服务器内部错误，请稍后重试");
}
```

**优点分析：**
- ✅ 统一的异常处理
- ✅ 不向客户端暴露内部错误堆栈
- ✅ 错误信息记录到日志

---

## ⚠️ 安全问题与建议

### 问题 1：数据库凭据硬编码 (高优先级) 🔴

**文件**: [`application.yaml`](../../it-training-backend/src/main/resources/application.yaml:7-9)

```yaml
# ❌ 问题：数据库凭据硬编码
datasource:
  url: jdbc:mysql://localhost:3306/it_training?...
  username: root
  password: root
```

**风险等级**: 🔴 高
**影响**: 敏感凭据可能泄露到版本控制系统

**建议修复**:
```yaml
datasource:
  url: ${DB_URL:jdbc:mysql://localhost:3306/it_training?...}
  username: ${DB_USERNAME:root}
  password: ${DB_PASSWORD:}
```

---

### 问题 2：CORS 配置过于宽松 (高优先级) 🔴

**文件**: [`CorsConfig.java`](../../it-training-backend/src/main/java/com/itts/common/config/CorsConfig.java:17-25)

```java
// ❌ 问题：允许所有来源
config.addAllowedOriginPattern("*");
// ❌ 问题：允许携带凭据
config.setAllowCredentials(true);
// ❌ 问题：允许所有请求头
config.addAllowedHeader("*");
// ❌ 问题：允许所有方法
config.addAllowedMethod("*");
```

**风险等级**: 🔴 高
**影响**: 可能导致 CSRF 攻击和数据泄露

**建议修复**:
```java
@Bean
public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    
    // 生产环境：只允许特定来源
    if (isProduction()) {
        config.addAllowedOrigin("https://your-domain.com");
    } else {
        // 开发环境
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:3000");
    }
    
    config.setAllowCredentials(true);
    config.addAllowedHeader("Authorization");
    config.addAllowedHeader("Content-Type");
    config.addAllowedMethod("GET");
    config.addAllowedMethod("POST");
    config.addAllowedMethod("PUT");
    config.addAllowedMethod("DELETE");
    
    // ...
}
```

---

### 问题 3：JWT Secret 默认值不安全 (中优先级) 🟡

**文件**: [`application.yaml`](../../it-training-backend/src/main/resources/application.yaml:53)

```yaml
jwt:
  # ⚠️ 默认值过于简单，虽然支持环境变量覆盖
  secret: ${JWT_SECRET:YourSuperSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLong123456789}
```

**风险等级**: 🟡 中
**影响**: 如果忘记设置环境变量，可能使用不安全的默认密钥

**建议修复**:
```yaml
jwt:
  # 不提供默认值，强制要求配置
  secret: ${JWT_SECRET}
  # 或使用更复杂的默认值（仅开发环境）
  # secret: ${JWT_SECRET:#{T(java.util.UUID).randomUUID().toString()}}
```

---

### 问题 4：验证码功能未实现 (中优先级) 🟡

**文件**: [`ProfileServiceImpl.java`](../../it-training-backend/src/main/java/com/itts/modules/user/service/impl/ProfileServiceImpl.java:134-155)

```java
// ⚠️ 问题：验证码验证未实现
@Override
@Transactional
public void bindEmail(Long userId, String email, String code) {
    // 简化实现：实际应该验证验证码
    // TODO: 实现验证码验证逻辑
    
    // 直接绑定，没有验证
    user.setEmail(email);
    // ...
}
```

**风险等级**: 🟡 中
**影响**: 可能导致账号被恶意绑定

**建议修复**:
```java
@Override
@Transactional
public void bindEmail(Long userId, String email, String code) {
    // 验证验证码
    if (!verificationCodeService.verify(email, code, VerificationType.EMAIL_BIND)) {
        throw new BusinessException(ErrorCode.VERIFICATION_CODE_ERROR);
    }
    
    // 检查邮箱是否已被其他用户绑定
    if (userMapper.existsByEmail(email)) {
        throw new BusinessException(ErrorCode.EMAIL_ALREADY_BOUND);
    }
    
    // 绑定邮箱
    // ...
}
```

---

### 问题 5：前端 Token 存储在 localStorage (中优先级) 🟡

**文件**: [`user.js`](../../it-training-frontend/src/store/user.js:59-62)

```javascript
// ⚠️ 问题：Token 存储在 localStorage
setToken(token) {
  this.token = token
  localStorage.setItem('token', token)
}
```

**风险等级**: 🟡 中
**影响**: XSS 攻击可能窃取 Token

**建议改进**:
```javascript
// 方案1：使用 httpOnly Cookie（需要后端配合）
// 后端设置 Cookie
// response.addCookie(new Cookie("token", jwtToken) {{
//     setHttpOnly(true);
//     setSecure(true);
//     setPath("/");
//     setMaxAge(86400);
// }});

// 方案2：使用 sessionStorage（关闭浏览器后自动清除）
setToken(token) {
  this.token = token
  sessionStorage.setItem('token', token)  // 替代 localStorage
}

// 方案3：内存存储 + 短期 Token + Refresh Token
// 最安全但实现复杂
```

---

### 问题 6：缺少请求频率限制 (中优先级) 🟡

**影响范围**: 全局 API

**风险等级**: 🟡 中
**影响**: 可能遭受暴力破解、DDoS 攻击

**建议修复**:
```java
// 添加 Rate Limiting 依赖
// pom.xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.1.1</version>
</dependency>

// RateLimitFilter.java
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String clientIp = getClientIP(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, this::createBucket);
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("请求过于频繁，请稍后重试");
        }
    }
    
    private Bucket createBucket(String key) {
        // 每分钟最多 60 次请求
        return Bucket.builder()
            .addLimit(Bandwidth.classic(60, Refill.intervally(60, Duration.ofMinutes(1))))
            .build();
    }
}
```

---

### 问题 7：Swagger UI 生产环境暴露 (低优先级) 🟢

**文件**: [`SwaggerConfig.java`](../../it-training-backend/src/main/java/com/itts/common/config/SwaggerConfig.java)

**风险等级**: 🟢 低
**影响**: API 文档可能泄露接口信息

**建议修复**:
```java
@Configuration
@Profile("!prod")  // 仅在非生产环境启用
public class SwaggerConfig {
    // ...
}

// 或在 SecurityConfig 中限制访问
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
    .hasRole("ADMIN")  // 仅管理员可访问
```

---

### 问题 8：日志可能包含敏感信息 (低优先级) 🟢

**当前状态**: ✅ 良好

**文件**: [`JwtTokenProvider.java`](../../it-training-backend/src/main/java/com/itts/common/security/JwtTokenProvider.java)

```java
// ✅ 当前实现：只记录用户名，不记录 Token
log.debug("生成JWT Token, 用户: {}", username);
```

**建议增强**:
```java
// 确保不记录敏感信息
log.debug("生成JWT Token, 用户: {}", maskUsername(username));

// 添加审计日志
@Slf4j
public class SecurityAuditLogger {
    public void logLoginAttempt(String username, boolean success, String ip) {
        log.info("LOGIN_ATTEMPT | user={} | success={} | ip={}",
                 maskUsername(username), success, ip);
    }
}
```

---

## 📋 安全检查清单

### 认证与授权
| 检查项 | 状态 | 备注 |
|--------|------|------|
| 密码使用 BCrypt 加密 | ✅ | BCryptPasswordEncoder |
| JWT Token 签名验证 | ✅ | HMAC-SHA256 |
| Token 过期机制 | ✅ | 可配置过期时间 |
| 角色权限控制 | ✅ | RBAC 实现 |
| 登录失败锁定 | ❌ | 未实现 |
| 多因素认证 | ❌ | 未实现 |

### 数据保护
| 检查项 | 状态 | 备注 |
|--------|------|------|
| 敏感数据脱敏 | ✅ | 邮箱、手机号脱敏 |
| SQL 注入防护 | ✅ | MyBatis-Plus 参数化查询 |
| XSS 防护 | ⚠️ | 需要前端配合 |
| CSRF 防护 | ⚠️ | 依赖 JWT，但 CORS 过宽 |

### 配置安全
| 检查项 | 状态 | 备注 |
|--------|------|------|
| 数据库凭据外部化 | ❌ | 硬编码在配置文件 |
| JWT Secret 外部化 | ⚠️ | 有默认值 |
| CORS 配置 | ❌ | 过于宽松 |
| HTTPS 强制 | ❌ | 未配置 |

### 日志与监控
| 检查项 | 状态 | 备注 |
|--------|------|------|
| 安全事件日志 | ✅ | 有专门的安全日志文件 |
| 敏感信息不记录 | ✅ | 未记录密码和 Token |
| 审计日志 | ⚠️ | 基础实现 |

---

## 🎯 风险矩阵

```
影响程度
    高 │  [CORS配置]     [数据库凭据]
       │      🔴              🔴
    中 │  [JWT默认值]   [验证码未实现]  [Token存储]
       │      🟡              🟡            🟡
    低 │  [Swagger暴露]  [日志敏感信息]
       │      🟢              🟢
       └──────────────────────────────────────
              低              中              高
                        发生概率
```

---

## 🔧 修复优先级建议

### 立即修复 (P0)
1. **数据库凭据外部化** - 使用环境变量
2. **CORS 配置收紧** - 限制允许的来源

### 短期修复 (P1)
3. **JWT Secret 强制配置** - 移除默认值
4. **实现验证码功能** - 邮箱/手机绑定验证
5. **添加请求频率限制** - 防止暴力破解

### 中期改进 (P2)
6. **Token 存储优化** - 考虑 httpOnly Cookie
7. **Swagger 访问控制** - 生产环境禁用或限制
8. **登录失败锁定** - 防止暴力破解

---

## 📝 安全配置模板

### 生产环境 application-prod.yaml

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000  # 1小时

# 禁用 Swagger
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    enabled: false
```

### 环境变量清单 (.env.example)

```bash
# 数据库配置
DB_URL=jdbc:mysql://localhost:3306/it_training
DB_USERNAME=app_user
DB_PASSWORD=<strong-password>

# JWT 配置
JWT_SECRET=<random-256-bit-key>

# 允许的 CORS 来源
CORS_ALLOWED_ORIGINS=https://your-domain.com
```

---

## 📊 审查结论

本系统在安全方面整体表现**良好**，主要安全机制已经到位：

**优势：**
- ✅ 完善的 JWT 认证机制
- ✅ BCrypt 密码加密
- ✅ 完整的输入验证
- ✅ 角色权限控制
- ✅ 敏感数据脱敏

**需要改进：**
- ❌ 数据库凭据硬编码
- ❌ CORS 配置过于宽松
- ⚠️ 验证码功能未实现
- ⚠️ 缺少请求频率限制

**建议：** 在部署到生产环境前，务必完成 P0 和 P1 级别的修复项。

---

> 报告生成时间：2025-12-14
> 下次审查建议：功能迭代后或每季度一次