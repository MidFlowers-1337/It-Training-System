# 03. Auth 模块设计

## 1. 页面设计 (Page Design)

### 1.1 登录页 (Login)

**Pattern**: `CenteredLayout` (极简居中)

-   **Header**: Logo + "Sign in to IT Training" (H1)
-   **Form**:
    -   `Input` (Email)
    -   `Input` (Password)
    -   `Button` (Primary, "Sign In")
-   **Footer**: "Don't have an account? Sign up" (Link)

**禁止**: 背景图片、复杂的左右分栏、插画。
**强制**: 纯白背景，仅保留核心表单。

### 1.2 注册页 (Register)

**Pattern**: `CenteredLayout`

-   **Form**:
    -   `Input` (Username)
    -   `Input` (Email)
    -   `Input` (Password)
    -   `Input` (Confirm Password)
    -   `Button` (Primary, "Create Account")

## 2. 组件拆分 (Component Split)

```
src/features/auth/
├── components/
│   └── AuthLayout.vue  # 封装 CenteredLayout，处理 Logo 和 Footer
├── views/
│   ├── LoginView.vue   # 使用 AuthLayout + Form Patterns
│   └── RegisterView.vue
```

## 3. 状态管理 (State)

-   `useAuthStore`: 处理 Token 存储、用户信息获取。
-   **Error Handling**: 错误信息显示在 Input 下方 (红色小字)，不使用弹窗。

---

**下一步**: 更新 `04-user模块.md`。