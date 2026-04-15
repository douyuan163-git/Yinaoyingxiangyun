<template>
  <div class="page-container">
    <div class="page-header">
      <h2>检查互认管理</h2>
      <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增互认记录</el-button>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索检查项目..." clearable style="width: 220px" @keyup.enter="loadData" />
      <el-select v-model="filterType" placeholder="检查类型" clearable style="width: 140px">
        <el-option label="CT检查" value="CT" />
        <el-option label="MRI检查" value="MRI" />
        <el-option label="DR检查" value="DR" />
        <el-option label="超声检查" value="超声" />
        <el-option label="核医学" value="核医学" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>
    <div class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="检查项目" prop="examName" min-width="200" show-overflow-tooltip />
        <el-table-column label="检查类型" prop="examType" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.examType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发起机构" prop="fromInstitution" width="180" show-overflow-tooltip />
        <el-table-column label="接受机构" prop="toInstitution" width="180" show-overflow-tooltip />
        <el-table-column label="检查日期" prop="examDate" width="120" />
        <el-table-column label="互认状态" prop="status" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button text type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button text type="danger" @click="handleDelete(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="editForm.id ? '编辑互认记录' : '新增互认记录'" width="560px">
      <el-form :model="editForm" label-width="110px">
        <el-form-item label="检查项目" required>
          <el-input v-model="editForm.examName" />
        </el-form-item>
        <el-form-item label="检查类型" required>
          <el-select v-model="editForm.examType" style="width: 100%">
            <el-option label="CT检查" value="CT" />
            <el-option label="MRI检查" value="MRI" />
            <el-option label="DR检查" value="DR" />
            <el-option label="超声检查" value="超声" />
            <el-option label="核医学" value="核医学" />
          </el-select>
        </el-form-item>
        <el-form-item label="发起机构">
          <el-input v-model="editForm.fromInstitution" />
        </el-form-item>
        <el-form-item label="接受机构">
          <el-input v-model="editForm.toInstitution" />
        </el-form-item>
        <el-form-item label="患者姓名">
          <el-input v-model="editForm.patientName" />
        </el-form-item>
        <el-form-item label="检查日期">
          <el-date-picker v-model="editForm.examDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="互认状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="已互认" value="已互认" />
            <el-option label="待审核" value="待审核" />
            <el-option label="已拒绝" value="已拒绝" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="editForm.remark" type="textarea" :rows="2" />
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
import { listRecognitions, saveRecognition, deleteRecognition } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'RecognitionListView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const dialogVisible = ref(false)
    const editForm = ref({})
    const keyword = ref('')
    const filterType = ref('')
    const page = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listRecognitions({ keyword: keyword.value, examType: filterType.value, page: page.value, size: pageSize.value })
        list.value = res.data?.content || res.data || []
        total.value = res.data?.totalElements || list.value.length
      } finally {
        loading.value = false
      }
    }

    const resetFilter = () => {
      keyword.value = ''
      filterType.value = ''
      page.value = 1
      loadData()
    }

    const openDialog = (row = null) => {
      editForm.value = row ? { ...row } : { status: '待审核' }
      dialogVisible.value = true
    }

    const handleSave = async () => {
      saving.value = true
      try {
        await saveRecognition(editForm.value)
        ElMessage.success('保存成功')
        dialogVisible.value = false
        loadData()
      } finally {
        saving.value = false
      }
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该互认记录吗？', '提示', { type: 'warning' })
      await deleteRecognition(id)
      ElMessage.success('删除成功')
      loadData()
    }

    const statusType = (s) => ({ '已互认': 'success', '待审核': 'warning', '已拒绝': 'danger' }[s] || 'info')

    onMounted(loadData)
    return { list, loading, saving, dialogVisible, editForm, keyword, filterType, page, pageSize, total, loadData, resetFilter, openDialog, handleSave, handleDelete, statusType }
  }
}
</script>
