<template>
  <div class="home">
    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="hero-bg"></div>
      <div class="page-container hero-content">
        <div class="hero-text">
          <div class="hero-badge">辽宁省医保影像云试点示范单位</div>
          <h1 class="hero-title">铁岭市医保影像云<br/>统一服务门户</h1>
          <p class="hero-subtitle">
            整合全市医疗机构影像数据，实现云胶片存储、检查结果互认、<br/>
            跨机构影像共享，为参保人员提供便捷、安全的数字化影像服务。
          </p>
          <div class="hero-actions">
            <router-link to="/login" class="btn-primary">
              <el-icon><user /></el-icon>
              立即登录使用
            </router-link>
            <router-link to="/mutual-recognition" class="btn-outline">
              查询检查互认记录
            </router-link>
          </div>
        </div>
        <div class="hero-stats">
          <div class="stat-item" v-for="stat in stats" :key="stat.label">
            <div class="stat-value">
              <count-up :end="stat.value" :duration="2" />
              <span class="stat-unit">{{ stat.unit }}</span>
            </div>
            <div class="stat-label">{{ stat.label }}</div>
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
            v-for="portal in portals"
            :key="portal.role"
            class="portal-card"
            :style="{ '--role-color': portal.color }"
            @click="handlePortalClick(portal)"
          >
            <div class="portal-icon">
              <el-icon :size="36"><component :is="portal.icon" /></el-icon>
            </div>
            <h3>{{ portal.title }}</h3>
            <p>{{ portal.desc }}</p>
            <ul class="portal-features">
              <li v-for="f in portal.features" :key="f">
                <el-icon><check /></el-icon>{{ f }}
              </li>
            </ul>
            <div class="portal-action">
              {{ portal.action }}
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
                <span class="news-date">{{ formatDate(item.publishDate) }}</span>
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
                <span class="policy-level" :class="'tag-' + item.level">
                  {{ levelLabel(item.level) }}
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
              <span v-for="item in inst.recognitionItems?.split(',')" :key="item" class="item-tag">
                {{ item }}
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
import { newsApi, policyApi, institutionApi } from '@/api'
import {
  User, ArrowRight, Check, Loading, OfficeBuilding,
  Monitor, Document, Cellphone, Setting
} from '@element-plus/icons-vue'

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
      newsList: [],
      policyList: [],
      institutions: [],
      stats: [
        { value: 7, unit: '家', label: '接入医疗机构' },
        { value: 12680, unit: '例', label: '云胶片存储' },
        { value: 3240, unit: '次', label: '检查互认次数' },
        { value: 99.9, unit: '%', label: '系统可用率' }
      ],
      portals: [
        {
          role: 'supervisor',
          title: '医保监管部门',
          desc: '面向医保局工作人员，提供影像数据监管、统计分析与合规审核功能',
          color: '#1B6FD8',
          icon: 'Monitor',
          features: ['影像数据统计监控', '机构合规审核', '费用核查分析', '政策执行追踪'],
          action: '进入监管系统'
        },
        {
          role: 'hospital',
          title: '医疗机构',
          desc: '面向医院管理者和临床医生，提供影像上传、调阅和互认操作功能',
          color: '#0891B2',
          icon: 'OfficeBuilding',
          features: ['影像上传与管理', '跨机构影像调阅', '检查结果互认', '云胶片开具'],
          action: '进入机构系统'
        },
        {
          role: 'patient',
          title: '参保人员',
          desc: '面向参保患者，提供个人影像查询、云胶片下载和授权管理功能',
          color: '#EA580C',
          icon: 'Cellphone',
          features: ['个人影像记录查询', '云胶片在线查看', '影像授权管理', '互认记录查询'],
          action: '查询我的影像'
        },
        {
          role: 'developer',
          title: '系统开发者',
          desc: '面向技术开发人员，提供API文档、接入规范和技术支持资源',
          color: '#7C3AED',
          icon: 'Setting',
          features: ['API接口文档', '技术规范下载', '接入测试环境', '开发者社区'],
          action: '进入开发者中心'
        }
      ]
    }
  },
  async created() {
    await Promise.all([
      this.loadNews(),
      this.loadPolicies(),
      this.loadInstitutions()
    ])
  },
  methods: {
    async loadNews() {
      this.newsLoading = true
      try {
        const res = await newsApi.latest()
        this.newsList = res.data || []
      } catch (e) {
        // 使用演示数据
        this.newsList = [
          { id: 1, title: '铁岭市医保影像云平台正式上线运行', category: 'notice', publishDate: '2025-10-01' },
          { id: 2, title: '关于云胶片服务开通使用的公告', category: 'notice', publishDate: '2025-09-20' },
          { id: 3, title: '辽宁省医保影像云试点工作现场会在铁岭召开', category: 'dynamic', publishDate: '2025-09-10' },
          { id: 4, title: '铁岭市医保局召开影像云推进工作会议', category: 'dynamic', publishDate: '2025-08-28' },
          { id: 5, title: '解读：国家医保影像云建设标准规范要点', category: 'policy', publishDate: '2025-08-15' }
        ]
      } finally {
        this.newsLoading = false
      }
    },
    async loadPolicies() {
      this.policyLoading = true
      try {
        const res = await policyApi.list({ page: 0, size: 5 })
        this.policyList = res.data?.content || []
      } catch (e) {
        this.policyList = [
          { id: 1, title: '国家医疗保障局关于推进医保影像云建设的指导意见', level: 'national', publishDate: '2024-06-01' },
          { id: 2, title: '国家卫生健康委关于推进医疗机构检查检验结果互认工作的通知', level: 'national', publishDate: '2024-03-15' },
          { id: 3, title: '辽宁省医疗保障局关于开展医保影像云试点工作的通知', level: 'provincial', publishDate: '2025-03-01' },
          { id: 4, title: '铁岭市医疗保障局关于铁岭市医保影像云平台建设实施方案', level: 'municipal', publishDate: '2025-05-01' },
          { id: 5, title: '铁岭市卫生健康委员会关于推进医保影像云接入工作的通知', level: 'municipal', publishDate: '2025-06-01' }
        ]
      } finally {
        this.policyLoading = false
      }
    },
    async loadInstitutions() {
      try {
        const res = await institutionApi.all()
        this.institutions = (res.data || []).slice(0, 6)
      } catch (e) {
        this.institutions = [
          { id: 1, name: '铁岭市中心医院', level: '三级甲等', district: '银州区', recognitionItems: 'CT,MRI,DR,US' },
          { id: 2, name: '铁岭市第二人民医院', level: '三级乙等', district: '银州区', recognitionItems: 'CT,DR,US' },
          { id: 3, name: '铁岭县中心医院', level: '二级甲等', district: '铁岭县', recognitionItems: 'CT,DR' },
          { id: 4, name: '开原市中心医院', level: '二级甲等', district: '开原市', recognitionItems: 'CT,MRI,DR' },
          { id: 5, name: '昌图县中心医院', level: '二级甲等', district: '昌图县', recognitionItems: 'CT,DR' },
          { id: 6, name: '调兵山市人民医院', level: '二级甲等', district: '调兵山市', recognitionItems: 'CT,DR,US' }
        ]
      }
    },
    handlePortalClick(portal) {
      if (this.$store.getters.isLoggedIn) {
        const role = this.$store.getters.userRole
        if (role === portal.role) {
          this.$router.push('/portal')
        } else {
          this.$message.warning(`您当前登录身份为 ${this.$store.getters.userInfo?.roleName}，无法访问该入口`)
        }
      } else {
        this.$router.push({ path: '/login', query: { role: portal.role } })
      }
    },
    formatDate(date) {
      if (!date) return ''
      return date.substring(0, 10)
    },
    categoryLabel(cat) {
      const map = { notice: '公告', dynamic: '动态', policy: '政策', industry: '行业' }
      return map[cat] || cat
    },
    levelLabel(level) {
      const map = { national: '国家', provincial: '省级', municipal: '市级' }
      return map[level] || level
    }
  }
}
</script>

<style lang="scss" scoped>
// Hero 区域
.hero-section {
  position: relative;
  background: linear-gradient(135deg, #0D4FA0 0%, #1B6FD8 40%, #0891B2 100%);
  color: white;
  overflow: hidden;
  padding: 64px 0 48px;

  .hero-bg {
    position: absolute;
    inset: 0;
    background-image:
      radial-gradient(circle at 20% 50%, rgba(255,255,255,0.05) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255,255,255,0.08) 0%, transparent 40%);
  }

  .hero-content {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 48px;
  }

  .hero-text {
    flex: 1;
    max-width: 600px;

    .hero-badge {
      display: inline-block;
      background: rgba(255,255,255,0.15);
      border: 1px solid rgba(255,255,255,0.3);
      border-radius: 20px;
      padding: 4px 16px;
      font-size: 13px;
      margin-bottom: 20px;
    }

    .hero-title {
      font-size: 40px;
      font-weight: 700;
      line-height: 1.3;
      margin-bottom: 16px;
      letter-spacing: -0.5px;
    }

    .hero-subtitle {
      font-size: 15px;
      line-height: 1.8;
      opacity: 0.85;
      margin-bottom: 32px;
    }

    .hero-actions {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;
    }
  }

  .hero-stats {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
    flex-shrink: 0;

    .stat-item {
      background: rgba(255,255,255,0.1);
      border: 1px solid rgba(255,255,255,0.2);
      border-radius: $radius-lg;
      padding: 20px 24px;
      text-align: center;
      backdrop-filter: blur(8px);

      .stat-value {
        font-size: 32px;
        font-weight: 700;
        line-height: 1;
        margin-bottom: 6px;
        .stat-unit { font-size: 16px; font-weight: 400; }
      }

      .stat-label {
        font-size: 13px;
        opacity: 0.8;
      }
    }
  }
}

// 按钮
.btn-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: white;
  color: $primary;
  border-radius: $radius-md;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.2s;
  &:hover {
    background: rgba(255,255,255,0.9);
    transform: translateY(-1px);
    box-shadow: 0 4px 16px rgba(0,0,0,0.2);
  }
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  background: transparent;
  color: white;
  border: 1.5px solid rgba(255,255,255,0.6);
  border-radius: $radius-md;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s;
  &:hover {
    background: rgba(255,255,255,0.1);
    border-color: white;
  }
}

.btn-outline-primary {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 24px;
  background: transparent;
  color: $primary;
  border: 1.5px solid $primary;
  border-radius: $radius-md;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
  &:hover {
    background: $primary-bg;
  }
}

// 版块通用
.section-header {
  text-align: center;
  margin-bottom: 40px;
  h2 {
    font-size: 28px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 8px;
  }
  p {
    font-size: 15px;
    color: $text-secondary;
  }
}

.section-footer {
  text-align: center;
  margin-top: 32px;
}

// 四大角色入口
.portal-section {
  padding: 64px 0;
  background: $bg-light;

  .portal-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;
  }

  .portal-card {
    background: white;
    border-radius: $radius-xl;
    border: 2px solid transparent;
    padding: 28px 24px;
    cursor: pointer;
    transition: all 0.25s;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: var(--role-color);
    }

    &:hover {
      border-color: var(--role-color);
      transform: translateY(-4px);
      box-shadow: 0 12px 32px rgba(0,0,0,0.12);
    }

    .portal-icon {
      width: 60px;
      height: 60px;
      border-radius: $radius-lg;
      background: color-mix(in srgb, var(--role-color) 10%, white);
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--role-color);
      margin-bottom: 16px;
    }

    h3 {
      font-size: 18px;
      font-weight: 700;
      color: $text-primary;
      margin-bottom: 8px;
    }

    p {
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.6;
      margin-bottom: 16px;
    }

    .portal-features {
      list-style: none;
      margin-bottom: 20px;
      li {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: $text-secondary;
        margin-bottom: 6px;
        .el-icon { color: var(--role-color); font-size: 12px; }
      }
    }

    .portal-action {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 14px;
      font-weight: 600;
      color: var(--role-color);
    }
  }
}

// 新闻 + 政策
.news-policy-section {
  padding: 64px 0;
  background: white;

  .news-policy-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 48px;
  }

  .block-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid $primary;

    h3 {
      font-size: 18px;
      font-weight: 700;
      color: $text-primary;
    }

    .more-link {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: $primary;
    }
  }

  .news-list, .policy-list {
    list-style: none;

    li {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 12px 0;
      border-bottom: 1px solid $border-color;
      &:last-child { border-bottom: none; }
    }

    .news-title, .policy-title {
      flex: 1;
      font-size: 14px;
      color: $text-primary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      &:hover { color: $primary; }
    }

    .news-date, .policy-date {
      font-size: 12px;
      color: $text-muted;
      white-space: nowrap;
    }

    .news-tag {
      flex-shrink: 0;
      display: inline-block;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 500;
      &.tag-notice { background: #EBF3FF; color: $primary; }
      &.tag-dynamic { background: #ECFDF5; color: $success; }
      &.tag-policy { background: #FFF7ED; color: $warning; }
      &.tag-industry { background: #F5F3FF; color: $developer-color; }
    }

    .policy-level {
      flex-shrink: 0;
      display: inline-block;
      padding: 2px 8px;
      border-radius: 4px;
      font-size: 11px;
      font-weight: 500;
      &.tag-national { background: #EBF3FF; color: $primary; }
      &.tag-provincial { background: #ECFDF5; color: $success; }
      &.tag-municipal { background: #FFF7ED; color: $warning; }
    }
  }
}

// 接入机构
.institution-section {
  padding: 64px 0;
  background: $bg-light;

  .institution-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }

  .institution-card {
    background: white;
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    padding: 20px;
    transition: box-shadow 0.2s;
    &:hover { box-shadow: $shadow-md; }

    .inst-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 12px;

      .inst-icon {
        width: 40px;
        height: 40px;
        background: $primary-bg;
        border-radius: $radius-md;
        display: flex;
        align-items: center;
        justify-content: center;
        color: $primary;
      }

      .inst-status {
        font-size: 12px;
        padding: 2px 10px;
        border-radius: 20px;
        &.running { background: #ECFDF5; color: $success; }
      }
    }

    h4 {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 6px;
    }

    .inst-info {
      font-size: 13px;
      color: $text-secondary;
      margin-bottom: 12px;
      .divider { margin: 0 6px; opacity: 0.4; }
    }

    .inst-items {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
      .item-tag {
        background: $primary-bg;
        color: $primary;
        padding: 2px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 500;
      }
    }
  }
}

@media (max-width: $breakpoint-lg) {
  .hero-section {
    .hero-content { flex-direction: column; }
    .hero-stats { grid-template-columns: repeat(4, 1fr); width: 100%; }
    .hero-title { font-size: 30px; }
  }
  .portal-section .portal-grid { grid-template-columns: repeat(2, 1fr); }
  .institution-section .institution-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: $breakpoint-md) {
  .hero-section {
    padding: 40px 0 32px;
    .hero-stats { grid-template-columns: repeat(2, 1fr); }
    .hero-title { font-size: 24px; }
  }
  .portal-section .portal-grid { grid-template-columns: 1fr; }
  .news-policy-section .news-policy-grid { grid-template-columns: 1fr; }
  .institution-section .institution-grid { grid-template-columns: 1fr; }
}
</style>
