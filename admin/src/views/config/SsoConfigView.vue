<template>
  <div class="page-container">
    <div class="page-header">
      <h2>SSO单点登录配置</h2>
    </div>
    <div class="card">
      <el-form :model="form" label-width="180px" v-loading="loading">
        <el-divider content-position="left">SSO服务端配置</el-divider>
        <el-form-item label="SSO服务地址">
          <el-input v-model="form['sso.server.url']" placeholder="https://sso.tieling.gov.cn" />
        </el-form-item>
        <el-form-item label="客户端ID">
          <el-input v-model="form['sso.client.id']" placeholder="tieling-imaging-portal" />
        </el-form-item>
        <el-form-item label="客户端密钥">
          <el-input v-model="form['sso.client.secret']" type="password" show-password placeholder="client_secret" />
        </el-form-item>
        <el-form-item label="回调地址">
          <el-input v-model="form['sso.redirect.uri']" placeholder="https://imaging.tieling.gov.cn/api/auth/callback" />
        </el-form-item>
        <el-divider content-position="left">Token配置</el-divider>
        <el-form-item label="JWT密钥">
          <el-input v-model="form['sso.jwt.secret']" type="password" show-password placeholder="建议32位以上随机字符串" />
          <div class="form-tip">生产环境必须替换为强随机密钥，不得使用默认值</div>
        </el-form-item>
        <el-form-item label="AccessToken有效期(分钟)">
          <el-input-number v-model.number="form['sso.token.access.expire']" :min="5" :max="1440" />
        </el-form-item>
        <el-form-item label="RefreshToken有效期(天)">
          <el-input-number v-model.number="form['sso.token.refresh.expire']" :min="1" :max="30" />
        </el-form-item>
        <el-divider content-position="left">子系统接入</el-divider>
        <el-form-item label="监管部门系统URL">
          <el-input v-model="form['sso.subsystem.supervisor']" placeholder="https://supervisor.imaging.tieling.gov.cn" />
        </el-form-item>
        <el-form-item label="医疗机构系统URL">
          <el-input v-model="form['sso.subsystem.hospital']" placeholder="https://hospital.imaging.tieling.gov.cn" />
        </el-form-item>
        <el-form-item label="参保人员系统URL">
          <el-input v-model="form['sso.subsystem.patient']" placeholder="https://patient.imaging.tieling.gov.cn" />
        </el-form-item>
        <el-form-item label="开发者系统URL">
          <el-input v-model="form['sso.subsystem.developer']" placeholder="https://dev.imaging.tieling.gov.cn" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存配置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getSiteConfig, saveSiteConfig } from '@/api'
import { ElMessage } from 'element-plus'

export default {
  name: 'SsoConfigView',
  setup() {
    const form = ref({})
    const loading = ref(false)
    const saving = ref(false)

    onMounted(async () => {
      loading.value = true
      try {
        const res = await getSiteConfig('sso')
        form.value = res.data || {}
      } finally {
        loading.value = false
      }
    })

    const handleSave = async () => {
      saving.value = true
      try {
        await saveSiteConfig('sso', form.value)
        ElMessage.success('SSO配置保存成功')
      } finally {
        saving.value = false
      }
    }

    return { form, loading, saving, handleSave }
  }
}
</script>

<style lang="scss" scoped>
.form-tip {
  font-size: 12px;
  color: #EF4444;
  margin-top: 4px;
}
</style>
