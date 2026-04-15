<template>
  <div class="login-page">
    <div class="login-left">
      <div class="login-left-content">
        <img src="@/assets/images/logo.png" alt="铁岭医保影像云" class="login-logo" />
        <h1>铁岭市医保影像云<br/>统一服务门户</h1>
        <p>整合全市医疗机构影像数据，实现云胶片存储、<br/>检查结果互认、跨机构影像共享</p>
        <div class="feature-list">
          <div class="feature-item" v-for="f in features" :key="f.text">
            <div class="feature-icon" :style="{ background: f.color + '20', color: f.color }">
              <el-icon><component :is="f.icon" /></el-icon>
            </div>
            <span>{{ f.text }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="login-right">
      <div class="login-box">
        <div class="login-header">
          <h2>统一身份认证登录</h2>
          <p>请选择您的身份类型并输入账号信息</p>
        </div>

        <!-- 角色选择 -->
        <div class="role-tabs">
          <button
            v-for="role in roles"
            :key="role.value"
            class="role-tab"
            :class="{ active: loginForm.role === role.value }"
            :style="loginForm.role === role.value ? { '--active-color': role.color } : {}"
            @click="selectRole(role)"
          >
            <el-icon><component :is="role.icon" /></el-icon>
            {{ role.label }}
          </button>
        </div>

        <!-- 登录方式切换 -->
        <div class="login-method-tabs">
          <button
            v-for="method in loginMethods"
            :key="method.value"
            class="method-tab"
            :class="{ active: loginMethod === method.value }"
            @click="loginMethod = method.value"
          >
            {{ method.label }}
          </button>
        </div>

        <!-- 账号密码登录 -->
        <el-form
          v-if="loginMethod === 'password'"
          ref="loginFormRef"
          :model="loginForm"
          :rules="rules"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              :placeholder="usernamePlaceholder"
              size="large"
              :prefix-icon="User"
              clearable
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>
          <div class="form-options">
            <el-checkbox v-model="rememberMe">记住账号</el-checkbox>
            <a href="#" class="forgot-link">忘记密码？</a>
          </div>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
            native-type="submit"
          >
            {{ loading ? '登录中...' : '安全登录' }}
          </el-button>
        </el-form>

        <!-- 短信验证码登录（演示） -->
        <div v-if="loginMethod === 'sms'" class="sms-login">
          <el-input placeholder="请输入手机号" size="large" :prefix-icon="Cellphone" />
          <div class="sms-code-row">
            <el-input placeholder="请输入验证码" size="large" />
            <el-button size="large" :disabled="smsCounting" @click="sendSms">
              {{ smsCounting ? `${smsCountdown}s后重发` : '获取验证码' }}
            </el-button>
          </div>
          <el-button type="primary" size="large" class="login-btn">
            登录
          </el-button>
        </div>

        <!-- 扫码登录（演示） -->
        <div v-if="loginMethod === 'qrcode'" class="qrcode-login">
          <div class="qrcode-placeholder">
            <el-icon :size="64" color="#94A3B8"><qrcode /></el-icon>
            <p>请使用医保电子凭证APP扫码</p>
          </div>
        </div>

        <!-- 演示账号提示 -->
        <div class="demo-accounts">
          <div class="demo-title">演示账号（测试环境）</div>
          <div class="demo-list">
            <div
              v-for="acc in demoAccounts"
              :key="acc.username"
              class="demo-item"
              @click="fillDemo(acc)"
            >
              <span class="demo-role" :style="{ color: acc.color }">{{ acc.roleLabel }}</span>
              <span class="demo-user">{{ acc.username }} / {{ acc.password }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { User, Lock, Cellphone, Monitor, OfficeBuilding, Setting } from '@element-plus/icons-vue'

export default {
  name: 'LoginView',
  components: { User, Lock, Cellphone, Monitor, OfficeBuilding, Setting },
  data() {
    return {
      User, Lock, Cellphone,
      loginMethod: 'password',
      loading: false,
      rememberMe: false,
      smsCounting: false,
      smsCountdown: 60,
      loginForm: {
        username: '',
        password: '',
        role: 'supervisor'
      },
      rules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      roles: [
        { value: 'supervisor', label: '监管部门', icon: 'Monitor', color: '#1B6FD8' },
        { value: 'hospital', label: '医疗机构', icon: 'OfficeBuilding', color: '#0891B2' },
        { value: 'patient', label: '参保人员', icon: 'Cellphone', color: '#EA580C' },
        { value: 'developer', label: '开发者', icon: 'Setting', color: '#7C3AED' }
      ],
      loginMethods: [
        { value: 'password', label: '账号密码' },
        { value: 'sms', label: '短信验证码' },
        { value: 'qrcode', label: '扫码登录' }
      ],
      features: [
        { text: '云胶片存储与查阅', icon: 'Document', color: '#1B6FD8' },
        { text: '检查结果跨机构互认', icon: 'Share', color: '#0891B2' },
        { text: '参保人员自助查询', icon: 'Search', color: '#EA580C' },
        { text: '医保数据合规监管', icon: 'Shield', color: '#7C3AED' }
      ],
      demoAccounts: [
        { username: 'admin', password: 'admin123', role: 'supervisor', roleLabel: '监管部门', color: '#1B6FD8' },
        { username: 'doctor001', password: 'doctor123', role: 'hospital', roleLabel: '医疗机构', color: '#0891B2' },
        { username: '110211198001010001', password: 'patient123', role: 'patient', roleLabel: '参保人员', color: '#EA580C' },
        { username: 'dev001', password: 'dev123', role: 'developer', roleLabel: '开发者', color: '#7C3AED' }
      ]
    }
  },
  computed: {
    usernamePlaceholder() {
      const map = {
        supervisor: '请输入工号或用户名',
        hospital: '请输入工号或用户名',
        patient: '请输入身份证号或医保卡号',
        developer: '请输入开发者账号'
      }
      return map[this.loginForm.role] || '请输入账号'
    }
  },
  created() {
    // 从路由参数预选角色
    const role = this.$route.query.role
    if (role && this.roles.find(r => r.value === role)) {
      this.loginForm.role = role
    }
  },
  methods: {
    selectRole(role) {
      this.loginForm.role = role.value
      this.loginForm.username = ''
      this.loginForm.password = ''
    },
    fillDemo(acc) {
      this.loginForm.role = acc.role
      this.loginForm.username = acc.username
      this.loginForm.password = acc.password
    },
    async handleLogin() {
      this.$refs.loginFormRef?.validate(async (valid) => {
        if (!valid) return
        this.loading = true
        try {
          await this.$store.dispatch('login', this.loginForm)
          this.$message.success('登录成功，欢迎使用铁岭市医保影像云')
          const redirect = this.$route.query.redirect || '/portal'
          this.$router.push(redirect)
        } catch (e) {
          // 错误已在request.js中处理
        } finally {
          this.loading = false
        }
      })
    },
    sendSms() {
      this.smsCounting = true
      this.smsCountdown = 60
      const timer = setInterval(() => {
        this.smsCountdown--
        if (this.smsCountdown <= 0) {
          clearInterval(timer)
          this.smsCounting = false
        }
      }, 1000)
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #0D4FA0 0%, #1B6FD8 50%, #0891B2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;

  .login-left-content {
    max-width: 480px;
    color: white;

    .login-logo {
      height: 48px;
      filter: brightness(0) invert(1);
      margin-bottom: 32px;
    }

    h1 {
      font-size: 36px;
      font-weight: 700;
      line-height: 1.3;
      margin-bottom: 16px;
    }

    p {
      font-size: 15px;
      opacity: 0.8;
      line-height: 1.8;
      margin-bottom: 40px;
    }

    .feature-list {
      display: flex;
      flex-direction: column;
      gap: 16px;

      .feature-item {
        display: flex;
        align-items: center;
        gap: 16px;
        font-size: 15px;

        .feature-icon {
          width: 40px;
          height: 40px;
          border-radius: $radius-md;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }
      }
    }
  }
}

.login-right {
  width: 480px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-light;
  padding: 48px 40px;

  .login-box {
    width: 100%;

    .login-header {
      margin-bottom: 28px;
      h2 {
        font-size: 24px;
        font-weight: 700;
        color: $text-primary;
        margin-bottom: 6px;
      }
      p {
        font-size: 14px;
        color: $text-secondary;
      }
    }
  }
}

.role-tabs {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 20px;

  .role-tab {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    padding: 10px 8px;
    border: 1.5px solid $border-color;
    border-radius: $radius-md;
    background: white;
    cursor: pointer;
    font-size: 12px;
    color: $text-secondary;
    transition: all 0.2s;

    &:hover {
      border-color: $primary;
      color: $primary;
    }

    &.active {
      border-color: var(--active-color, #{$primary});
      color: var(--active-color, #{$primary});
      background: color-mix(in srgb, var(--active-color, #{$primary}) 8%, white);
      font-weight: 600;
    }
  }
}

.login-method-tabs {
  display: flex;
  border-bottom: 1px solid $border-color;
  margin-bottom: 24px;

  .method-tab {
    padding: 8px 16px;
    background: none;
    border: none;
    cursor: pointer;
    font-size: 14px;
    color: $text-secondary;
    border-bottom: 2px solid transparent;
    margin-bottom: -1px;
    transition: all 0.2s;

    &.active {
      color: $primary;
      border-bottom-color: $primary;
      font-weight: 600;
    }
  }
}

.el-form {
  .el-form-item {
    margin-bottom: 16px;
  }
}

.form-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  font-size: 13px;

  .forgot-link {
    color: $primary;
    &:hover { color: $primary-dark; }
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 15px;
  font-weight: 600;
}

.sms-login {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .sms-code-row {
    display: flex;
    gap: 12px;
    .el-input { flex: 1; }
    .el-button { white-space: nowrap; }
  }
}

.qrcode-login {
  .qrcode-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 40px;
    background: white;
    border-radius: $radius-lg;
    border: 1px dashed $border-color;
    gap: 16px;
    p { font-size: 14px; color: $text-secondary; }
  }
}

.demo-accounts {
  margin-top: 24px;
  padding: 16px;
  background: white;
  border-radius: $radius-md;
  border: 1px solid $border-color;

  .demo-title {
    font-size: 12px;
    color: $text-muted;
    margin-bottom: 10px;
    font-weight: 500;
  }

  .demo-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
  }

  .demo-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 6px 10px;
    border-radius: $radius-sm;
    cursor: pointer;
    transition: background 0.2s;
    &:hover { background: $bg-light; }

    .demo-role {
      font-size: 12px;
      font-weight: 600;
      width: 60px;
      flex-shrink: 0;
    }

    .demo-user {
      font-size: 12px;
      color: $text-secondary;
      font-family: monospace;
    }
  }
}

@media (max-width: $breakpoint-md) {
  .login-page {
    flex-direction: column;
  }
  .login-left {
    padding: 32px 24px;
    h1 { font-size: 24px; }
    .feature-list { display: none; }
  }
  .login-right {
    width: 100%;
    padding: 32px 24px;
  }
}
</style>
