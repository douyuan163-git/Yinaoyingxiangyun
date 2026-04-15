<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑机构' : '新增机构' }}</h2>
      <el-button @click="$router.back()">返回列表</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="140px">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="机构名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="机构级别" prop="level">
          <el-select v-model="form.level" style="width: 200px">
            <el-option label="三级医院" value="三级" />
            <el-option label="二级医院" value="二级" />
            <el-option label="基层医疗机构" value="基层" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属区县">
          <el-select v-model="form.district" style="width: 200px">
            <el-option label="银州区" value="银州区" />
            <el-option label="清河区" value="清河区" />
            <el-option label="铁岭县" value="铁岭县" />
            <el-option label="西丰县" value="西丰县" />
            <el-option label="昌图县" value="昌图县" />
            <el-option label="开原市" value="开原市" />
            <el-option label="调兵山市" value="调兵山市" />
          </el-select>
        </el-form-item>
        <el-form-item label="机构地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-divider content-position="left">联系信息</el-divider>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" />
        </el-form-item>
        <el-divider content-position="left">接入信息</el-divider>
        <el-form-item label="接入状态">
          <el-select v-model="form.connectionStatus" style="width: 200px">
            <el-option label="已接入" value="已接入" />
            <el-option label="接入中" value="接入中" />
            <el-option label="待接入" value="待接入" />
          </el-select>
        </el-form-item>
        <el-form-item label="接入日期">
          <el-date-picker v-model="form.connectionDate" type="date" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="设备数量">
          <el-input-number v-model="form.deviceCount" :min="0" />
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
import { getInstitution, saveInstitution } from '@/api'
import { ElMessage } from 'element-plus'

export default {
  name: 'InstitutionEditView',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const formRef = ref(null)
    const form = ref({ connectionStatus: '待接入', deviceCount: 0 })
    const loading = ref(false)
    const saving = ref(false)
    const isEdit = computed(() => !!route.params.id)

    const rules = {
      name: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
      level: [{ required: true, message: '请选择机构级别', trigger: 'change' }]
    }

    onMounted(async () => {
      if (isEdit.value) {
        loading.value = true
        try {
          const res = await getInstitution(route.params.id)
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
          await saveInstitution(form.value)
          ElMessage.success('保存成功')
          router.push('/content/institution')
        } finally {
          saving.value = false
        }
      })
    }

    return { form, formRef, loading, saving, isEdit, rules, handleSave }
  }
}
</script>
