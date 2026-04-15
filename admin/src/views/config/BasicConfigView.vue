<template>
  <div class="page-container">
    <div class="page-header">
      <h2>网站基本信息配置</h2>
    </div>
    <div class="card">
      <el-form :model="form" label-width="140px" v-loading="loading">
        <el-divider content-position="left">网站标识</el-divider>
        <el-form-item label="网站标题">
          <el-input v-model="form['site.title']" placeholder="铁岭市医保影像云统一服务门户" />
        </el-form-item>
        <el-form-item label="网站副标题">
          <el-input v-model="form['site.subtitle']" placeholder="英文副标题" />
        </el-form-item>
        <el-form-item label="ICP备案号">
          <el-input v-model="form['site.icp']" placeholder="辽ICP备XXXXXXXX号" />
        </el-form-item>
        <el-form-item label="版权信息">
          <el-input v-model="form['site.copyright']" placeholder="© 2025 铁岭市医疗保障局 版权所有" />
        </el-form-item>
        <el-divider content-position="left">主办单位</el-divider>
        <el-form-item label="主办单位">
          <el-input v-model="form['site.host']" placeholder="铁岭市医疗保障局" />
        </el-form-item>
        <el-form-item label="技术支持单位">
          <el-input v-model="form['site.tech_support']" placeholder="融御科技（辽宁）有限公司" />
        </el-form-item>
        <el-divider content-position="left">联系方式</el-divider>
        <el-form-item label="联系电话">
          <el-input v-model="form['site.contact.phone']" placeholder="024-72000000" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="form['site.contact.email']" placeholder="yibao@tieling.gov.cn" />
        </el-form-item>
        <el-form-item label="联系地址">
          <el-input v-model="form['site.contact.address']" placeholder="辽宁省铁岭市银州区广裕街1号" />
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
  name: 'BasicConfigView',
  setup() {
    const form = ref({})
    const loading = ref(false)
    const saving = ref(false)

    onMounted(async () => {
      loading.value = true
      try {
        const res = await getSiteConfig('basic')
        form.value = res.data || {}
      } finally {
        loading.value = false
      }
    })

    const handleSave = async () => {
      saving.value = true
      try {
        await saveSiteConfig('basic', form.value)
        ElMessage.success('配置保存成功')
      } finally {
        saving.value = false
      }
    }

    return { form, loading, saving, handleSave }
  }
}
</script>
