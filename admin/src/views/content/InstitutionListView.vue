<template>
  <div class="page-container">
    <div class="page-header">
      <h2>机构接入管理</h2>
      <el-button type="primary" @click="$router.push('/content/institution/edit/')">
        <el-icon><Plus /></el-icon> 新增机构
      </el-button>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索机构名称..." clearable style="width: 220px" @keyup.enter="loadData" />
      <el-select v-model="filterLevel" placeholder="机构级别" clearable style="width: 140px">
        <el-option label="三级医院" value="三级" />
        <el-option label="二级医院" value="二级" />
        <el-option label="基层医疗" value="基层" />
      </el-select>
      <el-select v-model="filterStatus" placeholder="接入状态" clearable style="width: 140px">
        <el-option label="已接入" value="已接入" />
        <el-option label="接入中" value="接入中" />
        <el-option label="待接入" value="待接入" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>
    <div class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="机构名称" prop="name" min-width="220" show-overflow-tooltip />
        <el-table-column label="级别" prop="level" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="levelType(row.level)" size="small">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="区县" prop="district" width="100" />
        <el-table-column label="联系人" prop="contactPerson" width="100" />
        <el-table-column label="联系电话" prop="contactPhone" width="130" />
        <el-table-column label="接入状态" prop="connectionStatus" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.connectionStatus)" size="small">{{ row.connectionStatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button text type="primary" @click="$router.push(`/content/institution/edit/${row.id}`)">编辑</el-button>
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
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { listInstitutions, deleteInstitution } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'InstitutionListView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const keyword = ref('')
    const filterLevel = ref('')
    const filterStatus = ref('')
    const page = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listInstitutions({ keyword: keyword.value, level: filterLevel.value, connectionStatus: filterStatus.value, page: page.value, size: pageSize.value })
        list.value = res.data?.content || res.data || []
        total.value = res.data?.totalElements || list.value.length
      } finally {
        loading.value = false
      }
    }

    const resetFilter = () => {
      keyword.value = ''
      filterLevel.value = ''
      filterStatus.value = ''
      page.value = 1
      loadData()
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该机构吗？', '提示', { type: 'warning' })
      await deleteInstitution(id)
      ElMessage.success('删除成功')
      loadData()
    }

    const levelType = (l) => ({ '三级': 'danger', '二级': 'warning', '基层': 'success' }[l] || 'info')
    const statusType = (s) => ({ '已接入': 'success', '接入中': 'warning', '待接入': 'info' }[s] || 'info')

    onMounted(loadData)
    return { list, loading, keyword, filterLevel, filterStatus, page, pageSize, total, loadData, resetFilter, handleDelete, levelType, statusType }
  }
}
</script>
