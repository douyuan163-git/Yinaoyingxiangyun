<template>
  <div class="page-container">
    <div class="page-header">
      <h2>用户管理</h2>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增用户</el-button>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索用户名/姓名..." clearable style="width: 220px" @keyup.enter="loadData" />
      <el-select v-model="filterRole" placeholder="用户角色" clearable style="width: 140px">
        <el-option label="系统管理员" value="admin" />
        <el-option label="监管部门" value="supervisor" />
        <el-option label="医疗机构" value="hospital" />
        <el-option label="参保人员" value="patient" />
        <el-option label="系统开发者" value="developer" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="账号状态" clearable style="width: 120px">
        <el-option label="正常" :value="1" />
        <el-option label="禁用" :value="0" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>
    <div class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="用户名" prop="username" width="140" />
        <el-table-column label="姓名" prop="realName" width="100" />
        <el-table-column label="角色" prop="role" width="120">
          <template #default="{ row }">
            <el-tag :type="roleTagType(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="手机号" prop="phone" width="130" />
        <el-table-column label="所属机构" prop="institution" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              @change="toggleStatus(row)"
              :disabled="row.role === 'admin'"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createdAt" width="160" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="warning" @click="openResetPwd(row)">重置密码</el-button>
            <el-button text type="danger" @click="handleDelete(row.id)" :disabled="row.role === 'admin'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadData"
        />
      </div>
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑用户' : '新增用户'" width="520px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="用户名" required>
          <el-input v-model="editForm.username" :disabled="!!editForm.id" />
        </el-form-item>
        <el-form-item v-if="!editForm.id" label="初始密码" required>
          <el-input v-model="editForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="用户角色" required>
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option label="系统管理员" value="admin" />
            <el-option label="监管部门" value="supervisor" />
            <el-option label="医疗机构" value="hospital" />
            <el-option label="参保人员" value="patient" />
            <el-option label="系统开发者" value="developer" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="所属机构">
          <el-input v-model="editForm.institution" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
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

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="resetPwdVisible" title="重置密码" width="400px">
      <el-form label-width="100px">
        <el-form-item label="用户">
          <span>{{ resetTarget?.realName || resetTarget?.username }}</span>
        </el-form-item>
        <el-form-item label="新密码" required>
          <el-input v-model="newPassword" type="password" show-password placeholder="请输入新密码（8位以上）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetPwdVisible = false">取消</el-button>
        <el-button type="warning" :loading="saving" @click="handleResetPwd">确认重置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { listUsers, saveUser, deleteUser, updateUserStatus, resetPassword } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'UserListView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const resetPwdVisible = ref(false)
    const editForm = ref({})
    const resetTarget = ref(null)
    const newPassword = ref('')
    const keyword = ref('')
    const filterRole = ref('')
    const filterStatus = ref(null)
    const page = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listUsers({ keyword: keyword.value, role: filterRole.value, status: filterStatus.value, page: page.value, size: pageSize.value })
        list.value = res.data?.content || res.data || []
        total.value = res.data?.totalElements || list.value.length
      } finally {
        loading.value = false
      }
    }

    const resetFilter = () => {
      keyword.value = ''
      filterRole.value = ''
      filterStatus.value = null
      page.value = 1
      loadData()
    }

    const openDialog = (row = null) => {
      editForm.value = row ? { ...row, statusBool: row.status === 1 } : { role: 'hospital', statusBool: true }
      dialogVisible.value = true
    }

    const handleSave = async () => {
      saving.value = true
      try {
        await saveUser({ ...editForm.value, status: editForm.value.statusBool ? 1 : 0 })
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } finally {
        saving.value = false
      }
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该用户吗？此操作不可恢复。', '警告', { type: 'warning' })
      await deleteUser(id)
      ElMessage.success('删除成功')
      loadData()
    }

    const toggleStatus = async (row) => {
      const newStatus = row.status === 1 ? 0 : 1
      await updateUserStatus(row.id, newStatus)
      row.status = newStatus
    }

    const openResetPwd = (row) => {
      resetTarget.value = row
      newPassword.value = ''
      resetPwdVisible.value = true
    }

    const handleResetPwd = async () => {
      if (!newPassword.value || newPassword.value.length < 8) {
        ElMessage.warning('密码长度不能少于8位')
        return
      }
      saving.value = true
      try {
        await resetPassword(resetTarget.value.id, newPassword.value)
        ElMessage.success('密码重置成功')
        resetPwdVisible.value = false
      } finally {
        saving.value = false
      }
    }

    const roleLabel = (r) => ({ admin: '系统管理员', supervisor: '监管部门', hospital: '医疗机构', patient: '参保人员', developer: '开发者' }[r] || r)
    const roleTagType = (r) => ({ admin: 'danger', supervisor: 'primary', hospital: 'success', patient: '', developer: 'warning' }[r] || 'info')

    onMounted(loadData)
    return { list, loading, saving, dialogVisible, resetPwdVisible, editForm, resetTarget, newPassword, keyword, filterRole, filterStatus, page, pageSize, total, loadData, resetFilter, openDialog, handleSave, handleDelete, toggleStatus, openResetPwd, handleResetPwd, roleLabel, roleTagType }
  }
}
</script>
