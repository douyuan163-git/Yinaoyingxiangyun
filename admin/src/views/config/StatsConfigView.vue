<template>
  <div class="page-container">
    <div class="page-header">
      <h2>首页统计数字配置</h2>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增</el-button>
    </div>
    <div class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
        <el-table-column label="标签" prop="label" width="160" />
        <el-table-column label="数值" prop="value" width="100" align="right" />
        <el-table-column label="单位" prop="suffix" width="80" />
        <el-table-column label="颜色类" prop="color" width="160" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
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

    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑统计项' : '新增统计项'" width="480px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="标签名称" required>
          <el-input v-model="editForm.label" placeholder="已接入医疗机构" />
        </el-form-item>
        <el-form-item label="数值" required>
          <el-input-number v-model="editForm.value" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="editForm.suffix" placeholder="家 / 万张 / 次" />
        </el-form-item>
        <el-form-item label="颜色类">
          <el-select v-model="editForm.color" style="width: 100%">
            <el-option label="蓝色 (text-blue-600)" value="text-blue-600" />
            <el-option label="青色 (text-cyan-600)" value="text-cyan-600" />
            <el-option label="绿色 (text-emerald-600)" value="text-emerald-600" />
            <el-option label="靛蓝 (text-indigo-600)" value="text-indigo-600" />
            <el-option label="紫色 (text-purple-600)" value="text-purple-600" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editForm.sortOrder" :min="1" :max="99" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editForm.statusBool" active-text="启用" inactive-text="禁用" />
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
import { ref, onMounted } from 'vue'
import { listHomeStats, saveHomeStat, deleteHomeStat } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'StatsConfigView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editForm = ref({})

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listHomeStats(false)
        list.value = res.data || []
      } finally {
        loading.value = false
      }
    }

    const openDialog = (row = null) => {
      editForm.value = row ? { ...row, statusBool: row.status === 1 } : { sortOrder: 1, statusBool: true, value: 0 }
      dialogVisible.value = true
    }

    const handleSave = async () => {
      saving.value = true
      try {
        await saveHomeStat({ ...editForm.value, status: editForm.value.statusBool ? 1 : 0 })
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } finally {
        saving.value = false
      }
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该统计项吗？', '提示', { type: 'warning' })
      await deleteHomeStat(id)
      ElMessage.success('删除成功')
      loadData()
    }

    onMounted(loadData)
    return { list, loading, saving, dialogVisible, editForm, openDialog, handleSave, handleDelete }
  }
}
</script>
