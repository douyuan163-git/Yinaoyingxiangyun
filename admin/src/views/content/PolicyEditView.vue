<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑政策' : '新增政策' }}</h2>
      <el-button @click="$router.back()">返回列表</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="政策标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入政策标题" />
        </el-form-item>
        <el-form-item label="政策分类" prop="category">
          <el-select v-model="form.category" style="width: 200px">
            <el-option label="国家政策" value="national" />
            <el-option label="省级政策" value="provincial" />
            <el-option label="市级政策" value="municipal" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布机构">
          <el-input v-model="form.issuer" placeholder="如：国家医疗保障局" />
        </el-form-item>
        <el-form-item label="文号">
          <el-input v-model="form.documentNo" placeholder="如：医保发〔2024〕XX号" />
        </el-form-item>
        <el-form-item label="发布日期">
          <el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="附件链接">
          <el-input v-model="form.attachmentUrl" placeholder="PDF文件URL" />
        </el-form-item>
        <el-form-item label="政策摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" placeholder="政策核心内容摘要（100字以内）" />
        </el-form-item>
        <el-form-item label="政策全文">
          <el-input v-model="form.content" type="textarea" :rows="12" placeholder="政策全文内容（支持Markdown格式）" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPolicy, savePolicy } from '@/api'
import { ElMessage } from 'element-plus'

export default {
  name: 'PolicyEditView',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const formRef = ref(null)
    const form = ref({ category: 'municipal' })
    const loading = ref(false)
    const saving = ref(false)
    const isEdit = computed(() => !!route.params.id)

    const rules = {
      title: [{ required: true, message: '请输入政策标题', trigger: 'blur' }],
      category: [{ required: true, message: '请选择分类', trigger: 'change' }]
    }

    onMounted(async () => {
      if (isEdit.value) {
        loading.value = true
        try {
          const res = await getPolicy(route.params.id)
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
          await savePolicy(form.value)
          ElMessage.success('保存成功')
          router.push('/content/policy')
        } finally {
          saving.value = false
        }
      })
    }

    return { form, formRef, loading, saving, isEdit, rules, handleSave }
  }
}
</script>
