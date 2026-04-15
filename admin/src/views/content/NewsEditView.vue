<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑新闻' : '发布新闻' }}</h2>
      <el-button @click="$router.back()">返回列表</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="新闻标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入新闻标题" />
        </el-form-item>
        <el-form-item label="新闻分类" prop="category">
          <el-select v-model="form.category" style="width: 200px">
            <el-option label="平台公告" value="announcement" />
            <el-option label="医保动态" value="yibao" />
            <el-option label="行业资讯" value="industry" />
          </el-select>
        </el-form-item>
        <el-form-item label="新闻来源">
          <el-input v-model="form.source" placeholder="如：铁岭市医疗保障局" />
        </el-form-item>
        <el-form-item label="封面图URL">
          <el-input v-model="form.coverImage" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="新闻摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="新闻摘要（150字以内）" />
        </el-form-item>
        <el-form-item label="新闻正文">
          <el-input v-model="form.content" type="textarea" :rows="16" placeholder="新闻正文内容（支持Markdown格式）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">发布</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getNewsDetail, saveNews } from '@/api'
import { ElMessage } from 'element-plus'

export default {
  name: 'NewsEditView',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const formRef = ref(null)
    const form = ref({ category: 'announcement' })
    const loading = ref(false)
    const saving = ref(false)
    const isEdit = computed(() => !!route.params.id)

    const rules = {
      title: [{ required: true, message: '请输入新闻标题', trigger: 'blur' }],
      category: [{ required: true, message: '请选择分类', trigger: 'change' }]
    }

    onMounted(async () => {
      if (isEdit.value) {
        loading.value = true
        try {
          const res = await getNewsDetail(route.params.id)
          form.value = res.data
        } finally {
          loading.value = false
        }
      }
    })

    const handleSave = async () => {
      if (!formRef.value) return
      await formRef.value.validate(async (valid) => {
        if (!valid) return
        saving.value = true
        try {
          await saveNews(form.value)
          ElMessage.success('发布成功')
          router.push('/content/news')
        } finally {
          saving.value = false
        }
      })
    }

    return { form, formRef, loading, saving, isEdit, rules, handleSave }
  }
}
</script>
