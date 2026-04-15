<template>
  <div>
    <div class="page-header">
      <div class="page-container">
        <h1>新闻动态</h1>
        <p>铁岭市医保影像云平台最新公告、政策动态与行业资讯</p>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <div class="filter-bar">
        <div class="filter-tabs">
          <button
            v-for="tab in categoryTabs"
            :key="tab.value"
            class="filter-tab"
            :class="{ active: activeCategory === tab.value }"
            @click="switchCategory(tab.value)"
          >{{ tab.label }}</button>
        </div>
        <el-input
          v-model="keyword"
          placeholder="搜索新闻标题..."
          :prefix-icon="Search"
          clearable
          style="width: 240px;"
          @input="handleSearch"
        />
      </div>

      <div v-if="loading" class="loading-wrapper">
        <el-icon class="is-loading" :size="32"><loading /></el-icon>
      </div>
      <div v-else>
        <div class="news-grid">
          <div
            v-for="item in newsList"
            :key="item.id"
            class="news-card card"
            @click="$router.push('/news/' + item.id)"
          >
            <div class="news-card-header">
              <span class="news-tag" :class="'tag-' + item.category">{{ categoryLabel(item.category) }}</span>
              <span v-if="item.isTop" class="top-badge">置顶</span>
            </div>
            <h3 class="news-card-title">{{ item.title }}</h3>
            <p class="news-card-excerpt">{{ item.excerpt }}</p>
            <div class="news-card-footer">
              <span class="news-author">{{ item.author || '铁岭市医保局' }}</span>
              <span class="news-date">{{ item.publishDate }}</span>
            </div>
          </div>
        </div>

        <div v-if="newsList.length === 0" class="empty-wrapper">
          <p>暂无相关新闻</p>
        </div>

        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            :total="total"
            :page-size="pageSize"
            layout="prev, pager, next, total"
            @current-change="loadNews"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { newsApi } from '@/api'
import { Search, Loading } from '@element-plus/icons-vue'

export default {
  name: 'NewsView',
  components: { Loading },
  data() {
    return {
      Search,
      loading: false,
      newsList: [],
      total: 0,
      currentPage: 1,
      pageSize: 9,
      activeCategory: '',
      keyword: '',
      categoryTabs: [
        { value: '', label: '全部' },
        { value: 'notice', label: '平台公告' },
        { value: 'dynamic', label: '工作动态' },
        { value: 'policy', label: '政策解读' },
        { value: 'industry', label: '行业资讯' }
      ]
    }
  },
  created() { this.loadNews() },
  methods: {
    async loadNews(page) {
      if (page) this.currentPage = page
      this.loading = true
      try {
        const res = await newsApi.list({ page: this.currentPage - 1, size: this.pageSize, category: this.activeCategory || undefined, keyword: this.keyword || undefined })
        this.newsList = res.data?.content || []
        this.total = res.data?.totalElements || 0
      } catch (e) {
        this.newsList = [
          { id: 1, title: '铁岭市医保影像云平台正式上线运行', category: 'notice', excerpt: '铁岭市医保影像云统一服务门户平台已正式上线，全市7家医疗机构同步接入。', author: '铁岭市医保局', publishDate: '2025-10-01', isTop: 1 },
          { id: 2, title: '关于云胶片服务开通使用的公告', category: 'notice', excerpt: '自2025年10月1日起，铁岭市接入医疗机构将全面推行云胶片服务。', author: '铁岭市医保局', publishDate: '2025-09-20', isTop: 0 },
          { id: 3, title: '辽宁省医保影像云试点工作现场会在铁岭召开', category: 'dynamic', excerpt: '辽宁省医疗保障局在铁岭市召开医保影像云试点工作现场会。', author: '铁岭市医保局', publishDate: '2025-09-10', isTop: 1 },
          { id: 4, title: '铁岭市医保局召开影像云推进工作会议', category: 'dynamic', excerpt: '铁岭市医疗保障局组织召开全市医保影像云推进工作会议。', author: '铁岭市医保局', publishDate: '2025-08-28', isTop: 0 },
          { id: 5, title: '解读：国家医保影像云建设标准规范要点', category: 'policy', excerpt: '本文对国家医保影像云建设相关标准规范进行解读。', author: '铁岭市医保局', publishDate: '2025-08-15', isTop: 0 },
          { id: 6, title: '2025年全国医保影像云建设进展综述', category: 'industry', excerpt: '截至2025年底，全国已有超过200个地市启动医保影像云建设。', author: '编辑部', publishDate: '2025-08-01', isTop: 0 }
        ]
        this.total = this.newsList.length
      } finally {
        this.loading = false
      }
    },
    switchCategory(cat) {
      this.activeCategory = cat
      this.currentPage = 1
      this.loadNews()
    },
    handleSearch() {
      this.currentPage = 1
      clearTimeout(this._t)
      this._t = setTimeout(() => this.loadNews(), 400)
    },
    categoryLabel(cat) {
      const map = { notice: '公告', dynamic: '动态', policy: '政策', industry: '行业' }
      return map[cat] || cat
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
    padding: 8px 16px;
    border: 1.5px solid $border-color;
    border-radius: $radius-md;
    background: white;
    cursor: pointer;
    font-size: 14px;
    color: $text-secondary;
    transition: all 0.2s;
    &:hover { border-color: $primary; color: $primary; }
    &.active { background: $primary; border-color: $primary; color: white; font-weight: 600; }
  }
}
.news-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}
.news-card {
  padding: 20px;
  cursor: pointer;
  .news-card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    .news-tag {
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 500;
      &.tag-notice { background: #EBF3FF; color: $primary; }
      &.tag-dynamic { background: #ECFDF5; color: $success; }
      &.tag-policy { background: #FFF7ED; color: $warning; }
      &.tag-industry { background: #F5F3FF; color: $developer-color; }
    }
    .top-badge {
      background: #FEF2F2;
      color: $error;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 500;
    }
  }
  .news-card-title {
    font-size: 15px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 10px;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .news-card-excerpt {
    font-size: 13px;
    color: $text-secondary;
    line-height: 1.6;
    margin-bottom: 16px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
  .news-card-footer {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: $text-muted;
  }
}
@media (max-width: $breakpoint-md) {
  .news-grid { grid-template-columns: 1fr; }
}
</style>
