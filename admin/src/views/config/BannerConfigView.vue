<template>
  <div class="page-container">
    <div class="page-header">
      <h2>Banner管理</h2>
      <el-button type="primary" @click="openDialog()">
        <el-icon><Plus /></el-icon> 新增Banner
      </el-button>
    </div>
    <div class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="排序" prop="sortOrder" width="70" align="center" />
        <el-table-column label="标题" prop="title" min-width="180" />
        <el-table-column label="副标题" prop="subtitle" min-width="200" show-overflow-tooltip />
        <el-table-column label="链接" prop="linkUrl" width="160" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              @change="toggleStatus(row)"
            />
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

    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑Banner' : '新增Banner'" width="560px">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="标题" required>
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="editForm.subtitle" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="editForm.imageUrl" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="editForm.linkUrl" placeholder="/news" />
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
import { listBanners, saveBanner, deleteBanner, updateBannerStatus } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'BannerConfigView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editForm = ref({})

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listBanners(false)
        list.value = res.data || []
      } finally {
        loading.value = false
      }
    }

    const openDialog = (row = null) => {
      if (row) {
        editForm.value = { ...row, statusBool: row.status === 1 }
      } else {
        editForm.value = { sortOrder: 1, statusBool: true }
      }
      dialogVisible.value = true
    }

    const handleSave = async () => {
      saving.value = true
      try {
        const data = { ...editForm.value, status: editForm.value.statusBool ? 1 : 0 }
        await saveBanner(data)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } finally {
        saving.value = false
      }
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该Banner吗？', '提示', { type: 'warning' })
      await deleteBanner(id)
      ElMessage.success('删除成功')
      loadData()
    }

    const toggleStatus = async (row) => {
      const newStatus = row.status === 1 ? 0 : 1
      await updateBannerStatus(row.id, newStatus)
      row.status = newStatus
    }

    onMounted(loadData)
    return { list, loading, saving, dialogVisible, editForm, openDialog, handleSave, handleDelete, toggleStatus }
  }
}
</script>
