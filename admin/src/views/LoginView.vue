<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-icon">医</div>
        <h1>铁岭医保影像云</h1>
        <p>后台管理系统</p>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="管理员账号"
            size="large"
            :prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="登录密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            style="width: 100%"
            @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-hint">
        <p>默认管理员账号：<strong>sysadmin</strong></p>
        <p>默认密码：<strong>Admin@2026</strong></p>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

export default {
  name: 'LoginView',
  setup() {
    const store = useStore()
    const router = useRouter()
    const formRef = ref(null)
    const loading = ref(false)

    const form = reactive({
      username: '',
      password: '',
      loginType: 'account'
    })

    const rules = {
      username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
      password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }

    const handleLogin = async () => {
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (!valid) return
        loading.value = true
        try {
          await store.dispatch('login', form)
          ElMessage.success('登录成功')
          router.push('/dashboard')
        } catch (e) {
          // 错误已在request拦截器处理
        } finally {
          loading.value = false
        }
      })
    }

    return { formRef, form, rules, loading, handleLogin, User, Lock }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #001529 0%, #0A2540 50%, #1B3A6B 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 20px 60px rgba(0,0,0,0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  .logo-icon {
    width: 56px;
    height: 56px;
    background: #1B6FD8;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 22px;
    font-weight: 700;
    margin: 0 auto 16px;
  }

  h1 {
    font-size: 20px;
    font-weight: 700;
    color: #1F2937;
    margin-bottom: 6px;
  }

  p {
    font-size: 13px;
    color: #6B7280;
  }
}

.login-hint {
  margin-top: 20px;
  padding: 12px;
  background: #F0F9FF;
  border-radius: 6px;
  border: 1px solid #BAE6FD;
  font-size: 12px;
  color: #0369A1;
  line-height: 1.8;

  strong {
    font-family: monospace;
  }
}
</style>
