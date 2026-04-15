<template>
  <div class="home">
    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="hero-bg"></div>
      <div class="page-container hero-content">
        <div class="hero-text">
          <div class="hero-badge">{{ siteConfig['home.hero.badge'] || '辽宁省医保影像云试点示范单位' }}</div>
          <h1 class="hero-title" v-html="(siteConfig['home.hero.title'] || '铁岭市医保影像云<br/>统一服务门户').replace(/\\n/g, '<br/>')"></h1>
          <p class="hero-subtitle">{{ siteConfig['home.hero.description'] || '整合全市医疗机构影像数据，实现云胶片存储、检查结果互认、跨机构影像共享，为参保人员提供便捷、安全的数字化影像服务。' }}</p>
          <div class="hero-actions">
            <router-link to="/login" class="btn-primary">
              <el-icon><user /></el-icon>
              {{ siteConfig['home.hero.btn_primary'] || '立即登录使用' }}
            </router-link>
            <router-link to="/mutual-recognition" class="btn-outline">
              {{ siteConfig['home.hero.btn_secondary'] || '查询检查互认记录' }}
            </router-link>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-item" v-for="stat in homeStats" :key="stat.id">
            <div class="stat-value">
              <span class="stat-num">{{ stat.statValue }}</span>
              <span class="stat-unit">{{ stat.statUnit }}</span>
            </div>
            <div class="stat-label">{{ stat.statLabel }}</div>
          </div>
        </div>
      </div>
    </section>

    <!-- 四大角色入口 -->
    <section class="portal-section">
      <div class="page-container">
        <div class="section-header">
          <h2>统一服务门户入口</h2>
          <p>根据您的身份选择对应入口，通过统一认证安全访问专属服务</p>
        </div>
        <div class="portal-grid">
          <div
            v-for="portal in portalEntries"
            :key="portal.id"
            class="portal-card"
            :style="{ '--role-color': roleColor(portal.roleGroup) }"
            @click="handlePortalClick(portal)"
          >
            <div class="portal-icon">
              <el-icon :size="36"><component :is="portal.icon || roleIcon(portal.roleGroup)" /></el-icon>
            </div>
            <h3>{{ portal.name }}</h3>
            <p>{{ portal.description }}</p>
            <div class="portal-action">
              进入系统
              <el-icon><arrow-right /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 新闻动态 + 政策法规 -->
    <section class="news-policy-section">
      <div class="page-container">
        <div class="news-policy-grid">
          <!-- 新闻动态 -->
          <div class="news-block">
            <div class="block-header">
              <h3>新闻动态</h3>
              <router-link to="/news" class="more-link">更多 <el-icon><arrow-right /></el-icon></router-link>
            </div>
            <div v-if="newsLoading" class="loading-wrapper">
              <el-icon class="is-loading"><loading /></el-icon>
            </div>
            <ul v-else class="news-list">
              <li v-for="item in newsList" :key="item.id" class="news-item">
                <span class="news-tag" :class="'tag-' + item.category">
                  {{ categoryLabel(item.category) }}
                </span>
                <router-link :to="'/news/' + item.id" class="news-title">
                  {{ item.title }}
                </router-link>
                <span class="news-date">{{ formatDate(item.publishTime || item.publishDate) }}</span>
              </li>
            </ul>
          </div>

          <!-- 政策法规 -->
          <div class="policy-block">
            <div class="block-header">
              <h3>政策法规</h3>
              <router-link to="/policy" class="more-link">更多 <el-icon><arrow-right /></el-icon></router-link>
            </div>
            <div v-if="policyLoading" class="loading-wrapper">
              <el-icon class="is-loading"><loading /></el-icon>
            </div>
            <ul v-else class="policy-list">
              <li v-for="item in policyList" :key="item.id" class="policy-item">
                <span class="policy-level" :class="'tag-' + item.category">
                  {{ levelLabel(item.category) }}
                </span>
                <router-link :to="'/policy/' + item.id" class="policy-title">
                  {{ item.title }}
                </router-link>
                <span class="policy-date">{{ formatDate(item.publishDate) }}</span>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </section>

    <!-- 接入机构展示 -->
    <section class="institution-section">
      <div class="page-container">
        <div class="section-header">
          <h2>接入医疗机构</h2>
          <p>铁岭市已接入医保影像云平台的医疗机构</p>
        </div>
        <div class="institution-grid">
          <div
            v-for="inst in institutions"
            :key="inst.id"
            class="institution-card"
          >
            <div class="inst-header">
              <div class="inst-icon">
                <el-icon><office-building /></el-icon>
              </div>
              <div class="inst-status running">已接入</div>
            </div>
            <h4>{{ inst.name }}</h4>
            <div class="inst-info">
              <span>{{ inst.level }}</span>
              <span class="divider">·</span>
              <span>{{ inst.district }}</span>
            </div>
            <div class="inst-items">
              <span v-for="item in (inst.recognitionItems || '').split(',')" :key="item" class="item-tag">
                {{ item.trim() }}
              </span>
            </div>
          </div>
        </div>
        <div class="section-footer">
          <router-link to="/institutions" class="btn-outline-primary">
            查看全部接入机构
          </router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { newsApi, policyApi, institutionApi, publicApi } from '@/api'
import {
  User, ArrowRight, Check, Loading, OfficeBuilding,
  Monitor, Document, Cellphone, Setting
} from '@element-plus/icons-vue'

const FALLBACK_STATS = [
  { id: 1, statValue: '7', statUnit: '家', statLabel: '接入医疗机构' },
  { id: 2, statValue: '12680', statUnit: '例', statLabel: '云胶片存储' },
  { id: 3, statValue: '3240', statUnit: '次', statLabel: '检查互认次数' },
  { id: 4, statValue: '99.9', statUnit: '%', statLabel: '系统可用率' }
]

const FALLBACK_PORTALS = [
  { id: 1, roleGroup: 'supervisor', name: '医保监管部门', description: '面向医保局工作人员，提供影像数据监管、统计分析与合规审核功能', linkUrl: '/portal' },
  { id: 2, roleGroup: 'hospital', name: '医疗机构', description: '面向医院管理者和临床医生，提供影像上传、调阅和互认操作功能', linkUrl: '/portal' },
  { id: 3, roleGroup: 'patient', name: '参保人员', description: '面向参保患者，提供个人影像查询、云胶片下载和授权管理功能', linkUrl: '/portal' },
  { id: 4, roleGroup: 'developer', name: '系统开发者', description: '面向技术开发人员，提供API文档、接入规范和技术支持资源', linkUrl: '/developer' }
]

const FALLBACK_NEWS = [
  { id: 1, title: '铁岭市医保影像云平台正式上线运行', category: 'announcement', publishTime: '2025-10-01' },
  { id: 2, title: '关于云胶片服务开通使用的公告', category: 'announcement', publishTime: '2025-09-20' },
  { id: 3, title: '辽宁省医保影像云试点工作现场会在铁岭召开', category: 'yibao', publishTime: '2025-09-10' },
  { id: 4, title: '铁岭市医保局召开影像云推进工作会议', category: 'yibao', publishTime: '2025-08-28' },
  { id: 5, title: '解读：国家医保影像云建设标准规范要点', category: 'industry', publishTime: '2025-08-15' }
]

const FALLBACK_POLICIES = [
  { id: 1, title: '国家医疗保障局关于推进医保影像云建设的指导意见', category: 'national', publishDate: '2024-06-01' },
  { id: 2, title: '国家卫生健康委关于推进医疗机构检查检验结果互认工作的通知', category: 'national', publishDate: '2024-03-15' },
  { id: 3, title: '辽宁省医疗保障局关于开展医保影像云试点工作的通知', category: 'provincial', publishDate: '2025-03-01' },
  { id: 4, title: '铁岭市医疗保障局关于铁岭市医保影像云平台建设实施方案', category: 'municipal', publishDate: '2025-05-01' },
  { id: 5, title: '铁岭市卫生健康委员会关于推进医保影像云接入工作的通知', category: 'municipal', publishDate: '2025-06-01' }
]

export default {
  name: 'HomeView',
  components: {
    User, ArrowRight, Check, Loading, OfficeBuilding,
    Monitor, Document, Cellphone, Setting
  },
  data() {
    return {
      newsLoading: false,
      policyLoading: false,
      siteConfig: {},
      homeStats: FALLBACK_STATS,
      portalEntries: FALLBACK_PORTALS,
      newsList: [],
      policyList: [],
      institutions: []
    }
  },
  async created() {
    await Promise.all([
      this.loadSiteConfig(),
      this.loadHomeStats(),
      this.loadPortalEntries(),
      this.loadNews(),
      this.loadPolicies(),
      this.loadInstitutions()
    ])
  },
  methods: {
    async loadSiteConfig() {
      try {
        const res = await publicApi.getSiteConfig('home')
        this.siteConfig = res.data || {}
      } catch (e) {
        this.siteConfig = {}
      }
    },
    async loadHomeStats() {
      try {
        const res = await publicApi.getHomeStats()
        const data = res.data || []
        if (data.length > 0) this.homeStats = data
      } catch (e) {
        this.homeStats = FALLBACK_STATS
      }
    },
    async loadPortalEntries() {
      try {
        const res = await publicApi.getPortalEntries()
        const data = res.data || []
        if (data.length > 0) this.portalEntries = data
      } catch (e) {
        this.portalEntries = FALLBACK_PORTALS
      }
    },
    async loadNews() {
      this.newsLoading = true
      try {
        const res = await newsApi.latest()
        this.newsList = res.data || []
        if (!this.newsList.length) this.newsList = FALLBACK_NEWS
      } catch (e) {
        this.newsList = FALLBACK_NEWS
      } finally {
        this.newsLoading = false
      }
    },
    async loadPolicies() {
      this.policyLoading = true
      try {
        const res = await policyApi.list({ page: 0, size: 5 })
        this.policyList = res.data?.content || res.data || []
        if (!this.policyList.length) this.policyList = FALLBACK_POLICIES
      } catch (e) {
        this.policyList = FALLBACK_POLICIES
      } finally {
        this.policyLoading = false
      }
    },
    async loadInstitutions() {
      try {
        const res = await institutionApi.all()
        this.institutions = (res.data || []).slice(0, 6)
        if (!this.institutions.length) this.institutions = this.fallbackInstitutions()
      } catch (e) {
        this.institutions = this.fallbackInstitutions()
      }
    },
    fallbackInstitutions() {
      return [
        { id: 1, name: '铁岭市中心医院', level: '三级甲等', district: '银州区', recognitionItems: 'CT,MRI,DR,US' },
        { id: 2, name: '铁岭市第二人民医院', level: '三级乙等', district: '银州区', recognitionItems: 'CT,DR,US' },
        { id: 3, name: '铁岭县中心医院', level: '二级甲等', district: '铁岭县', recognitionItems: 'CT,DR' },
        { id: 4, name: '开原市中心医院', level: '二级甲等', district: '开原市', recognitionItems: 'CT,MRI,DR' },
        { id: 5, name: '昌图县中心医院', level: '二级甲等', district: '昌图县', recognitionItems: 'CT,DR' },
        { id: 6, name: '调兵山市人民医院', level: '二级甲等', district: '调兵山市', recognitionItems: 'CT,DR,US' }
      ]
    },
    handlePortalClick(portal) {
      if (this.$store.getters.isLoggedIn) {
        const role = this.$store.getters.userRole
        if (role === portal.roleGroup) {
          this.$router.push(portal.linkUrl || '/portal')
        } else {
          this.$router.push('/portal')
        }
      } else {
        this.$router.push('/login')
      }
    },
    roleColor(roleGroup) {
      const map = { supervisor: '#1B6FD8', hospital: '#0891B2', patient: '#EA580C', developer: '#7C3AED' }
      return map[roleGroup] || '#1B6FD8'
    },
    roleIcon(roleGroup) {
      const map = { supervisor: 'Monitor', hospital: 'OfficeBuilding', patient: 'Cellphone', developer: 'Setting' }
      return map[roleGroup] || 'Document'
    },
    categoryLabel(cat) {
      const map = { announcement: '公告', yibao: '医保', industry: '资讯', notice: '公告', dynamic: '动态', policy: '政策' }
      return map[cat] || cat
    },
    levelLabel(level) {
      const map = { national: '国家', provincial: '省级', municipal: '市级' }
      return map[level] || level
    },
    formatDate(d) {
      if (!d) return ''
      return String(d).substring(0, 10)
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.home {
  .hero-section {
    position: relative;
    min-height: 480px;
    background: linear-gradient(135deg, #EFF6FF 0%, #DBEAFE 50%, #E0F2FE 100%);
    overflow: hidden;
    .hero-bg {
      position: absolute;
      inset: 0;
      background: radial-gradient(ellipse at 70% 50%, rgba(27,111,216,0.08) 0%, transparent 60%);
    }
    .hero-content {
      position: relative;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 60px 0;
      gap: 40px;
    }
    .hero-text {
      flex: 1;
      max-width: 560px;
    }
    .hero-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      background: rgba(27,111,216,0.1);
      color: #1B6FD8;
      border: 1px solid rgba(27,111,216,0.2);
      border-radius: 20px;
      padding: 4px 14px;
      font-size: 13px;
      margin-bottom: 20px;
      &::before {
        content: '';
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: #1B6FD8;
        animation: pulse 2s infinite;
      }
    }
    .hero-title {
      font-size: 40px;
      font-weight: 700;
      color: #1F2937;
      line-height: 1.3;
      margin-bottom: 16px;
    }
    .hero-subtitle {
      font-size: 15px;
      color: #6B7280;
      line-height: 1.8;
      margin-bottom: 32px;
    }
    .hero-actions {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
      .btn-primary {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: #1B6FD8;
        color: white;
        padding: 12px 28px;
        border-radius: 8px;
        font-size: 15px;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s;
        &:hover { background: #1558b0; transform: translateY(-1px); }
      }
      .btn-outline {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: white;
        color: #1B6FD8;
        border: 1.5px solid #1B6FD8;
        padding: 12px 28px;
        border-radius: 8px;
        font-size: 15px;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s;
        &:hover { background: #EFF6FF; }
      }
    }
    .hero-stats {
      display: flex;
      gap: 32px;
      flex-wrap: wrap;
      .stat-item {
        text-align: center;
        .stat-value {
          display: flex;
          align-items: baseline;
          gap: 2px;
          justify-content: center;
          .stat-num { font-size: 32px; font-weight: 700; color: #1B6FD8; }
          .stat-unit { font-size: 14px; color: #6B7280; }
        }
        .stat-label { font-size: 13px; color: #9CA3AF; margin-top: 4px; }
      }
    }
  }

  .portal-section {
    padding: 64px 0;
    background: white;
    .section-header {
      text-align: center;
      margin-bottom: 40px;
      h2 { font-size: 28px; font-weight: 700; color: #1F2937; margin-bottom: 8px; }
      p { font-size: 15px; color: #6B7280; }
    }
    .portal-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 20px;
      @media (max-width: 1024px) { grid-template-columns: repeat(2, 1fr); }
      @media (max-width: 640px) { grid-template-columns: 1fr; }
    }
    .portal-card {
      border: 2px solid #E5E7EB;
      border-radius: 12px;
      padding: 28px 24px;
      cursor: pointer;
      transition: all 0.25s;
      &:hover {
        border-color: var(--role-color);
        box-shadow: 0 8px 24px rgba(0,0,0,0.1);
        transform: translateY(-4px);
      }
      .portal-icon {
        width: 64px;
        height: 64px;
        border-radius: 12px;
        background: color-mix(in srgb, var(--role-color) 10%, white);
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--role-color);
        margin-bottom: 16px;
      }
      h3 { font-size: 18px; font-weight: 600; color: #1F2937; margin-bottom: 8px; }
      p { font-size: 13px; color: #6B7280; line-height: 1.6; margin-bottom: 16px; }
      .portal-action {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 14px;
        font-weight: 500;
        color: var(--role-color);
      }
    }
  }

  .news-policy-section {
    padding: 64px 0;
    background: #F9FAFB;
    .news-policy-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 32px;
      @media (max-width: 768px) { grid-template-columns: 1fr; }
    }
    .news-block, .policy-block {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 1px 4px rgba(0,0,0,0.06);
    }
    .block-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      border-bottom: 2px solid #1B6FD8;
      padding-bottom: 12px;
      h3 { font-size: 17px; font-weight: 600; color: #1F2937; }
      .more-link { font-size: 13px; color: #1B6FD8; text-decoration: none; display: flex; align-items: center; gap: 2px; }
    }
    .news-list, .policy-list { list-style: none; padding: 0; margin: 0; }
    .news-item, .policy-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 0;
      border-bottom: 1px solid #F3F4F6;
      &:last-child { border-bottom: none; }
    }
    .news-tag, .policy-level {
      flex-shrink: 0;
      font-size: 11px;
      padding: 2px 8px;
      border-radius: 4px;
      font-weight: 500;
      &.tag-announcement, &.tag-notice { background: #FEF2F2; color: #DC2626; }
      &.tag-yibao, &.tag-dynamic { background: #EFF6FF; color: #1B6FD8; }
      &.tag-industry, &.tag-policy { background: #F0FDF4; color: #16A34A; }
      &.tag-national { background: #FEF2F2; color: #DC2626; }
      &.tag-provincial { background: #FFFBEB; color: #D97706; }
      &.tag-municipal { background: #EFF6FF; color: #1B6FD8; }
    }
    .news-title, .policy-title {
      flex: 1;
      font-size: 14px;
      color: #374151;
      text-decoration: none;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      &:hover { color: #1B6FD8; }
    }
    .news-date, .policy-date { flex-shrink: 0; font-size: 12px; color: #9CA3AF; }
    .loading-wrapper { display: flex; justify-content: center; padding: 40px; }
  }

  .institution-section {
    padding: 64px 0;
    background: white;
    .section-header {
      text-align: center;
      margin-bottom: 40px;
      h2 { font-size: 28px; font-weight: 700; color: #1F2937; margin-bottom: 8px; }
      p { font-size: 15px; color: #6B7280; }
    }
    .institution-grid {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 20px;
      @media (max-width: 1024px) { grid-template-columns: repeat(2, 1fr); }
      @media (max-width: 640px) { grid-template-columns: 1fr; }
    }
    .institution-card {
      border: 1px solid #E5E7EB;
      border-radius: 10px;
      padding: 20px;
      transition: all 0.2s;
      &:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.08); border-color: #BFDBFE; }
      .inst-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;
        .inst-icon {
          width: 40px;
          height: 40px;
          background: #EFF6FF;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: #1B6FD8;
          font-size: 20px;
        }
        .inst-status {
          font-size: 12px;
          padding: 2px 10px;
          border-radius: 20px;
          &.running { background: #F0FDF4; color: #16A34A; }
        }
      }
      h4 { font-size: 15px; font-weight: 600; color: #1F2937; margin-bottom: 6px; }
      .inst-info { font-size: 13px; color: #6B7280; margin-bottom: 10px; .divider { margin: 0 6px; } }
      .inst-items {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
        .item-tag {
          font-size: 11px;
          background: #EFF6FF;
          color: #1B6FD8;
          padding: 2px 8px;
          border-radius: 4px;
        }
      }
    }
    .section-footer {
      text-align: center;
      margin-top: 32px;
      .btn-outline-primary {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        background: white;
        color: #1B6FD8;
        border: 1.5px solid #1B6FD8;
        padding: 10px 28px;
        border-radius: 8px;
        font-size: 14px;
        font-weight: 500;
        text-decoration: none;
        transition: all 0.2s;
        &:hover { background: #EFF6FF; }
      }
    }
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
</style>
