<template>
  <div class="page-container">
    <div class="page-header">
      <h2>开发者API管理</h2>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增API</el-button>
    </div>
    <div class="filter-bar">
      <el-select v-model="filterCategory" placeholder="按分类筛选" clearable style="width: 160px">
        <el-option label="认证授权" value="auth" />
        <el-option label="影像服务" value="imaging" />
        <el-option label="机构管理" value="institution" />
        <el-option label="互认查询" value="recognition" />
        <el-option label="数据统计" value="statistics" />
      </el-select>
    </div>
    <div class="table-card">
      <el-table :data="filteredList" v-loading="loading" stripe>
        <el-table-column label="分类" prop="category" width="110">
          <template #default="{ row }">
            <el-tag size="small">{{ categoryLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="API名称" prop="name" width="200" />
        <el-table-column label="方法" prop="method" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="methodType(row.method)" size="small">{{ row.method }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="路径" prop="path" min-width="200" show-overflow-tooltip />
        <el-table-column label="描述" prop="description" min-width="160" show-overflow-tooltip />
        <el-table-column label="需要认证" width="90" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.requiresAuth" color="#10B981"><CircleCheck /></el-icon>
            <el-icon v-else color="#9CA3AF"><CircleClose /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑API' : '新增API'" width="600px">
      <el-form :model="editForm" label-width="110px">
        <el-form-item label="分类" required>
          <el-select v-model="editForm.category" style="width: 100%">
            <el-option label="认证授权" value="auth" />
            <el-option label="影像服务" value="imaging" />
            <el-option label="机构管理" value="institution" />
            <el-option label="互认查询" value="recognition" />
            <el-option label="数据统计" value="statistics" />
          </el-select>
        </el-form-item>
        <el-form-item label="API名称" required>
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="请求方法" required>
          <el-select v-model="editForm.method" style="width: 100%">
            <el-option label="GET" value="GET" />
            <el-option label="POST" value="POST" />
            <el-option label="PUT" value="PUT" />
            <el-option label="DELETE" value="DELETE" />
          </el-select>
        </el-form-item>
        <el-form-item label="API路径" required>
          <el-input v-model="editForm.path" placeholder="/api/v1/..." />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="请求参数">
          <el-input v-model="editForm.requestParams" type="textarea" :rows="3" placeholder="JSON格式参数说明" />
        </el-form-item>
        <el-form-item label="响应示例">
          <el-input v-model="editForm.responseExample" type="textarea" :rows="4" placeholder="JSON格式响应示例" />
        </el-form-item>
        <el-form-item label="需要认证">
          <el-switch v-model="editForm.requiresAuth" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editForm.sortOrder" :min="1" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { listDevApis, saveDevApi, deleteDevApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'DevApiConfigView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editForm = ref({})
    const filterCategory = ref('')

    const filteredList = computed(() => {
      if (!filterCategory.value) return list.value
      return list.value.filter(i => i.category === filterCategory.value)
    })

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listDevApis(false)
        list.value = res.data || []
      } finally {
        loading.value = false
      }
    }

    const openDialog = (row = null) => {
      editForm.value = row ? { ...row } : { sortOrder: 1, requiresAuth: true, method: 'GET' }
      dialogVisible.value = true
    }

    const handleSave = async () => {
      saving.value = true
      try {
        await saveDevApi(editForm.value)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } finally {
        saving.value = false
      }
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该API吗？', '提示', { type: 'warning' })
      await deleteDevApi(id)
      ElMessage.success('删除成功')
      loadData()
    }

    const categoryLabel = (c) => {
      const map = { auth: '认证授权', imaging: '影像服务', institution: '机构管理', recognition: '互认查询', statistics: '数据统计' }
      return map[c] || c
    }

    const methodType = (m) => {
      const map = { GET: 'success', POST: 'primary', PUT: 'warning', DELETE: 'danger' }
      return map[m] || 'info'
    }

    onMounted(loadData)
    return { list, filteredList, loading, saving, dialogVisible, editForm, filterCategory, openDialog, handleSave, handleDelete, categoryLabel, methodType }
  }
}
</script>
