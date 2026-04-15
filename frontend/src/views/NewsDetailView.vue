<template>
  <div>
    <div class="page-header">
      <div class="page-container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span>/</span>
          <router-link to="/news">新闻动态</router-link>
          <span>/</span>
          <span>新闻详情</span>
        </div>
        <h1 v-if="news">{{ news.title }}</h1>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <div v-if="loading" class="loading-wrapper">
        <el-icon class="is-loading" :size="32"><loading /></el-icon>
      </div>
      <div v-else-if="news" class="detail-layout">
        <div class="detail-main card" style="padding: 32px;">
          <div class="news-meta-bar">
            <span class="news-tag" :class="'tag-' + news.category">{{ categoryLabel(news.category) }}</span>
            <span class="meta-item"><el-icon><user /></el-icon>{{ news.author || '铁岭市医保局' }}</span>
            <span class="meta-item"><el-icon><calendar /></el-icon>{{ news.publishDate }}</span>
          </div>
          <div class="news-content" v-html="news.content || ('<p>' + news.excerpt + '</p>')"></div>
        </div>
        <div class="detail-sidebar">
          <div class="card" style="padding: 20px;">
            <h4 style="margin-bottom: 12px;">相关新闻</h4>
            <div v-for="r in relatedNews" :key="r.id" class="related-item">
              <router-link :to="'/news/' + r.id">{{ r.title }}</router-link>
              <span class="related-date">{{ r.publishDate }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { newsApi } from '@/api'
import { Loading, User, Calendar } from '@element-plus/icons-vue'

export default {
  name: 'NewsDetailView',
  components: { Loading, User, Calendar },
  data() {
    return {
      loading: false,
      news: null,
      relatedNews: []
    }
  },
  created() { this.loadNews() },
  watch: { '$route.params.id': 'loadNews' },
  methods: {
    async loadNews() {
      this.loading = true
      try {
        const res = await newsApi.detail(this.$route.params.id)
        this.news = res.data
      } catch (e) {
        this.news = {
          id: this.$route.params.id,
          title: '铁岭市医保影像云平台正式上线运行',
          category: 'notice',
          author: '铁岭市医保局',
          publishDate: '2025-10-01',
          content: '<p>2025年10月1日，铁岭市医保影像云统一服务门户平台正式上线运行。</p><p>铁岭市医保影像云平台由铁岭市医疗保障局主导建设，融御科技提供技术支撑。平台整合全市7家医疗机构的影像数据，实现云胶片存储、检查结果互认、跨机构影像共享等核心功能，为参保人员提供便捷、安全的数字化影像服务。</p><p>平台上线后，参保人员可通过统一服务门户查询个人历次影像检查记录，在线查看和下载云胶片，无需再携带传统胶片就医。医疗机构可通过平台调阅患者在其他机构的影像资料，减少重复检查，节约医疗资源。</p><p>铁岭市医疗保障局表示，将持续完善平台功能，扩大接入机构范围，为铁岭市参保人员提供更加优质、便捷的医疗保障服务。</p>'
        }
        this.relatedNews = [
          { id: 2, title: '关于云胶片服务开通使用的公告', publishDate: '2025-09-20' },
          { id: 3, title: '辽宁省医保影像云试点工作现场会在铁岭召开', publishDate: '2025-09-10' }
        ]
      } finally {
        this.loading = false
      }
    },
    categoryLabel(cat) {
      const map = { notice: '公告', dynamic: '动态', policy: '政策', industry: '行业' }
      return map[cat] || cat
    }
  }
}
</script>

<style lang="scss" scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  opacity: 0.8;
  margin-bottom: 12px;
  a { color: rgba(255,255,255,0.8); &:hover { color: white; } }
}
.detail-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
}
.news-meta-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid $border-color;

  .news-tag {
    padding: 3px 12px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
    &.tag-notice { background: #EBF3FF; color: $primary; }
    &.tag-dynamic { background: #ECFDF5; color: $success; }
    &.tag-policy { background: #FFF7ED; color: $warning; }
    &.tag-industry { background: #F5F3FF; color: $developer-color; }
  }

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: $text-secondary;
  }
}
.news-content {
  font-size: 15px;
  line-height: 1.9;
  color: $text-primary;
  :deep(p) { margin-bottom: 14px; }
}
.related-item {
  padding: 10px 0;
  border-bottom: 1px solid $border-color;
  &:last-child { border-bottom: none; }
  a { font-size: 13px; color: $text-primary; display: block; margin-bottom: 4px; &:hover { color: $primary; } }
  .related-date { font-size: 12px; color: $text-muted; }
}
@media (max-width: $breakpoint-md) {
  .detail-layout { grid-template-columns: 1fr; }
}
</style>
