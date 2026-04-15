<template>
  <div>
    <div class="page-header">
      <div class="page-container">
        <h1>政策法规</h1>
        <p>国家、省、市三级医保影像云相关政策文件汇编</p>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <!-- 筛选区 -->
      <div class="filter-bar">
        <div class="filter-tabs">
          <button
            v-for="tab in levelTabs"
            :key="tab.value"
            class="filter-tab"
            :class="{ active: activeLevel === tab.value }"
            @click="switchLevel(tab.value)"
          >
            {{ tab.label }}
          </button>
        </div>
        <el-input
          v-model="keyword"
          placeholder="搜索政策标题、文号..."
          :prefix-icon="Search"
          clearable
          style="width: 280px;"
          @input="handleSearch"
        />
      </div>

      <!-- 政策列表 -->
      <div v-if="loading" class="loading-wrapper">
        <el-icon class="is-loading" :size="32"><loading /></el-icon>
        <span style="margin-left: 8px;">加载中...</span>
      </div>

      <div v-else>
        <div class="policy-list">
          <div
            v-for="item in policyList"
            :key="item.id"
            class="policy-item card"
            @click="$router.push('/policy/' + item.id)"
          >
            <div class="policy-item-left">
              <span class="level-tag" :class="'tag-' + item.level">
                {{ levelLabel(item.level) }}
              </span>
              <div class="policy-info">
                <h3 class="policy-title">{{ item.title }}</h3>
                <div class="policy-meta">
                  <span v-if="item.issuer">
                    <el-icon><office-building /></el-icon>{{ item.issuer }}
                  </span>
                  <span v-if="item.docNo">
                    <el-icon><document /></el-icon>{{ item.docNo }}
                  </span>
                  <span>
                    <el-icon><calendar /></el-icon>{{ item.publishDate }}
                  </span>
                </div>
                <p v-if="item.summary" class="policy-summary">{{ item.summary }}</p>
              </div>
            </div>
            <div class="policy-item-right">
              <el-button type="primary" plain size="small">
                查看详情
              </el-button>
            </div>
          </div>
        </div>

        <div v-if="policyList.length === 0" class="empty-wrapper">
          <el-icon :size="48" color="#CBD5E1"><document /></el-icon>
          <p style="margin-top: 12px;">暂无相关政策文件</p>
        </div>

        <!-- 分页 -->
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="prev, pager, next, total"
            @current-change="loadPolicies"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { policyApi } from '@/api'
import { Search, Loading, OfficeBuilding, Document, Calendar } from '@element-plus/icons-vue'

export default {
  name: 'PolicyView',
  components: { Loading, OfficeBuilding, Document, Calendar },
  data() {
    return {
      Search,
      loading: false,
      policyList: [],
      total: 0,
      currentPage: 1,
      pageSize: 10,
      activeLevel: '',
      keyword: '',
      levelTabs: [
        { value: '', label: '全部' },
        { value: 'national', label: '国家级' },
        { value: 'provincial', label: '省级' },
        { value: 'municipal', label: '市级' }
      ]
    }
  },
  created() {
    this.loadPolicies()
  },
  methods: {
    async loadPolicies(page) {
      if (page) this.currentPage = page
      this.loading = true
      try {
        const params = {
          page: this.currentPage - 1,
          size: this.pageSize,
          level: this.activeLevel || undefined,
          keyword: this.keyword || undefined
        }
        const res = await policyApi.list(params)
        this.policyList = res.data?.content || []
        this.total = res.data?.totalElements || 0
      } catch (e) {
        // 演示数据
        this.policyList = [
          { id: 1, title: '国家医疗保障局关于推进医保影像云建设的指导意见', issuer: '国家医疗保障局', docNo: '医保发〔2024〕18号', level: 'national', publishDate: '2024-06-01', summary: '推进全国医保影像云统一建设，实现影像数据互联互通，降低参保人员重复检查负担。' },
          { id: 2, title: '国家卫生健康委关于推进医疗机构检查检验结果互认工作的通知', issuer: '国家卫生健康委员会', docNo: '国卫医函〔2022〕53号', level: 'national', publishDate: '2022-03-15', summary: '要求各级医疗机构积极推进检查检验结果互认，减少重复检查，节约医疗资源。' },
          { id: 3, title: '辽宁省医疗保障局关于开展医保影像云试点工作的通知', issuer: '辽宁省医疗保障局', docNo: '辽医保发〔2025〕12号', level: 'provincial', publishDate: '2025-03-01', summary: '在铁岭等地市开展医保影像云试点，探索云胶片替代传统胶片的实施路径。' },
          { id: 4, title: '辽宁省卫生健康委员会关于医学影像检查结果互认工作实施方案', issuer: '辽宁省卫生健康委员会', docNo: '辽卫发〔2024〕88号', level: 'provincial', publishDate: '2024-08-01', summary: '明确辽宁省医学影像检查结果互认的范围、标准和实施步骤。' },
          { id: 5, title: '铁岭市医疗保障局关于铁岭市医保影像云平台建设实施方案', issuer: '铁岭市医疗保障局', docNo: '铁医保〔2025〕5号', level: 'municipal', publishDate: '2025-05-01', summary: '铁岭市医保影像云平台建设总体方案，明确建设目标、任务分工和时间节点。' },
          { id: 6, title: '铁岭市卫生健康委员会关于推进医保影像云接入工作的通知', issuer: '铁岭市卫生健康委员会', docNo: '铁卫健发〔2025〕8号', level: 'municipal', publishDate: '2025-06-01', summary: '要求全市二级及以上医疗机构于2025年底前完成医保影像云接入工作。' }
        ]
        this.total = this.policyList.length
      } finally {
        this.loading = false
      }
    },
    switchLevel(level) {
      this.activeLevel = level
      this.currentPage = 1
      this.loadPolicies()
    },
    handleSearch() {
      this.currentPage = 1
      clearTimeout(this._searchTimer)
      this._searchTimer = setTimeout(() => this.loadPolicies(), 400)
    },
    levelLabel(level) {
      const map = { national: '国家', provincial: '省级', municipal: '市级' }
      return map[level] || level
    }
  }
}
</script>

<style lang="scss" scoped>
.filter-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  gap: 16px;
  flex-wrap: wrap;
}

.filter-tabs {
  display: flex;
  gap: 8px;

  .filter-tab {
    padding: 8px 20px;
    border: 1.5px solid $border-color;
    border-radius: $radius-md;
    background: white;
    cursor: pointer;
    font-size: 14px;
    color: $text-secondary;
    transition: all 0.2s;

    &:hover {
      border-color: $primary;
      color: $primary;
    }

    &.active {
      background: $primary;
      border-color: $primary;
      color: white;
      font-weight: 600;
    }
  }
}

.policy-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.policy-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  cursor: pointer;
  gap: 16px;

  .policy-item-left {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    flex: 1;
  }

  .level-tag {
    flex-shrink: 0;
    display: inline-block;
    padding: 3px 12px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
    margin-top: 2px;
    &.tag-national { background: #EBF3FF; color: $primary; }
    &.tag-provincial { background: #ECFDF5; color: $success; }
    &.tag-municipal { background: #FFF7ED; color: $warning; }
  }

  .policy-info {
    flex: 1;

    .policy-title {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 8px;
      line-height: 1.5;
    }

    .policy-meta {
      display: flex;
      align-items: center;
      gap: 16px;
      flex-wrap: wrap;
      margin-bottom: 8px;
      span {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 13px;
        color: $text-secondary;
      }
    }

    .policy-summary {
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.6;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}
</style>
