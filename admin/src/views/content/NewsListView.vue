<template>
  <div class="page-container">
    <div class="page-header">
      <h2>新闻动态管理</h2>
      <el-button type="primary" @click="$router.push('/content/news/edit/')">
        <el-icon><Plus /></el-icon> 发布新闻
      </el-button>
    </div>
    <div class="filter-bar">
      <el-input v-model="keyword" placeholder="搜索标题..." clearable style="width: 220px" @keyup.enter="loadData" />
      <el-select v-model="filterCategory" placeholder="新闻分类" clearable style="width: 140px">
        <el-option label="平台公告" value="announcement" />
        <el-option label="医保动态" value="yibao" />
        <el-option label="行业资讯" value="industry" />
      </el-select>
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>
    <div class="table-card">
      <el-table :data="list" v-loading="loading" stripe>
        <el-table-column label="标题" prop="title" min-width="280" show-overflow-tooltip />
        <el-table-column label="分类" prop="category" width="100">
          <template #default="{ row }">
            <el-tag :type="categoryType(row.category)" size="small">{{ categoryLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="来源" prop="source" width="140" show-overflow-tooltip />
        <el-table-column label="发布时间" prop="publishTime" width="160" />
        <el-table-column label="操作" width="160" align="center">
          <template #default="{ row }">
            <el-button text type="primary" @click="$router.push(`/content/news/edit/${row.id}`)">编辑</el-button>
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
import { listNews, deleteNews } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'NewsListView',
  setup() {
    const list = ref([])
    const loading = ref(false)
    const keyword = ref('')
    const filterCategory = ref('')
    const page = ref(1)
    const pageSize = ref(10)
    const total = ref(0)

    const loadData = async () => {
      loading.value = true
      try {
        const res = await listNews({ keyword: keyword.value, category: filterCategory.value, page: page.value, size: pageSize.value })
        list.value = res.data?.content || res.data || []
        total.value = res.data?.totalElements || list.value.length
      } finally {
        loading.value = false
      }
    }

    const resetFilter = () => {
      keyword.value = ''
      filterCategory.value = ''
      page.value = 1
      loadData()
    }

    const handleDelete = async (id) => {
      await ElMessageBox.confirm('确定删除该新闻吗？', '提示', { type: 'warning' })
      await deleteNews(id)
      ElMessage.success('删除成功')
      loadData()
    }

    const categoryLabel = (c) => ({ announcement: '平台公告', yibao: '医保动态', industry: '行业资讯' }[c] || c)
    const categoryType = (c) => ({ announcement: 'danger', yibao: 'primary', industry: 'success' }[c] || 'info')

    onMounted(loadData)
    return { list, loading, keyword, filterCategory, page, pageSize, total, loadData, resetFilter, handleDelete, categoryLabel, categoryType }
  }
}
</script>
