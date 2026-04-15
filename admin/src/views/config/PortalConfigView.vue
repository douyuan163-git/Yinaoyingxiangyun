<template>
  <div class="page-container">
    <div class="page-header">
      <h2>服务门户入口配置</h2>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增入口</el-button>
    </div>
    <div class="filter-bar">
      <el-select v-model="filterRole" placeholder="按角色筛选" clearable style="width: 160px" @change="loadData">
        <el-option label="医保监管部门" value="supervisor" />
        <el-option label="医疗机构" value="hospital" />
        <el-option label="参保人员" value="patient" />
        <el-option label="系统开发者" value="developer" />
      </el-select>
    </div>
    <div class="table-card">
      <el-table :data="filteredList" v-loading="loading" stripe>
        <el-table-column label="角色" prop="roleGroup" width="120">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.roleGroup)" size="small">{{ roleLabel(row.roleGroup) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="入口名称" prop="name" width="160" />
        <el-table-column label="描述" prop="description" min-width="200" show-overflow-tooltip />
        <el-table-column label="图标" prop="icon" width="100" />
        <el-table-column label="链接" prop="linkUrl" width="160" show-overflow-tooltip />
        <el-table-column label="主入口" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.isPrimary" color="#10B981"><CircleCheck /></el-icon>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @change="toggleStatus(row)" />
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

    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑入口' : '新增入口'" width="520px">
      <el-form :model="editForm" label-width="110px">
        <el-form-item label="所属角色" required>
          <el-select v-model="editForm.roleGroup" style="width: 100%">
            <el-option label="医保监管部门" value="supervisor" />
            <el-option label="医疗机构" value="hospital" />
            <el-option label="参保人员" value="patient" />
            <el-option label="系统开发者" value="developer" />
          </el-select>
        </el-form-item>
        <el-form-item label="入口名称" required>
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" />
        </el-form-item>
        <el-form-item label="图标名称">
          <el-input v-model="editForm.icon" placeholder="如: Shield, Hospital, Search" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="editForm.linkUrl" placeholder="/portal/supervisor" />
        </el-form-item>
        <el-form-item label="是否主入口">
          <el-switch v-model="editForm.isPrimary" />
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
import { ref, computed, onMounted } from 'vue'
import { listPortalEntries, savePortalEntry, deletePortalEntry, updatePortalEntryStatus } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'PortalConfigView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editForm = ref({})
    const filterRole = ref('')

    const filteredList = computed(() => {
      if (!filterRole.value) return list.value
      return list.value.filter(i => i.roleGroup === filterRole.value)
    })

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listPortalEntries(false)
        list.value = res.data || []
      } finally {
        loading.value = false
      }
    }

    const openDialog = (row = null) => {
      editForm.value = row
        ? { ...row, statusBool: row.status === 1 }
        : { sortOrder: 1, statusBool: true, isPrimary: false }
      dialogVisible.value = true
    }

    const handleSave = async () => {
      saving.value = true
      try {
        await savePortalEntry({ ...editForm.value, status: editForm.value.statusBool ? 1 : 0 })
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } finally {
        saving.value = false
      }
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该入口吗？', '提示', { type: 'warning' })
      await deletePortalEntry(id)
      ElMessage.success('删除成功')
      loadData()
    }

    const toggleStatus = async (row) => {
      const newStatus = row.status === 1 ? 0 : 1
      await updatePortalEntryStatus(row.id, newStatus)
      row.status = newStatus
    }

    const roleLabel = (role) => {
      const map = { supervisor: '监管部门', hospital: '医疗机构', patient: '参保人员', developer: '开发者' }
      return map[role] || role
    }

    const roleTagType = (role) => {
      const map = { supervisor: 'primary', hospital: 'success', patient: '', developer: 'warning' }
      return map[role] || 'info'
    }

    onMounted(loadData)
    return { list, filteredList, loading, saving, dialogVisible, editForm, filterRole, openDialog, handleSave, handleDelete, toggleStatus, roleLabel, roleTagType }
  }
}
</script>
