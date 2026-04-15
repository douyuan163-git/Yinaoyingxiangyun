<template>
  <div class="page-container">
    <div class="page-header">
      <h2>首页配置</h2>
    </div>
    <div class="card">
      <el-form :model="form" label-width="160px" v-loading="loading">
        <el-divider content-position="left">Hero区域</el-divider>
        <el-form-item label="顶部标识徽章">
          <el-input v-model="form['home.hero.badge']" placeholder="辽宁省医保影像云试点示范单位" />
        </el-form-item>
        <el-form-item label="主标题">
          <el-input
            v-model="form['home.hero.title']"
            type="textarea"
            :rows="3"
            placeholder="铁岭市&#10;医保影像云&#10;统一服务门户"
          />
          <div class="form-tip">换行用 \n 分隔，将显示为多行大标题</div>
        </el-form-item>
        <el-form-item label="描述文字">
          <el-input
            v-model="form['home.hero.description']"
            type="textarea"
            :rows="3"
            placeholder="平台简介描述..."
          />
        </el-form-item>
        <el-form-item label="主按钮文字">
          <el-input v-model="form['home.hero.btn_primary']" placeholder="进入服务门户" />
        </el-form-item>
        <el-form-item label="次按钮文字">
          <el-input v-model="form['home.hero.btn_secondary']" placeholder="机构接入申请" />
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
  name: 'HomeConfigView',
  setup() {
    const form = ref({})
    const loading = ref(false)
    const saving = ref(false)

    onMounted(async () => {
      loading.value = true
      try {
        const res = await getSiteConfig('home')
        form.value = res.data || {}
      } finally {
        loading.value = false
      }
    })

    const handleSave = async () => {
      saving.value = true
      try {
        await saveSiteConfig('home', form.value)
        ElMessage.success('配置保存成功')
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
  color: #9CA3AF;
  margin-top: 4px;
}
</style>
