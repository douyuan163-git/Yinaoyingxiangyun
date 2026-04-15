<template>
  <div class="portal-page">
    <div class="portal-header">
      <div class="page-container">
        <div class="portal-welcome">
          <div class="welcome-avatar" :style="{ background: roleColor }">
            {{ userName.charAt(0) }}
          </div>
          <div>
            <h2>欢迎回来，{{ userName }}</h2>
            <p>{{ roleName }} · 铁岭市医保影像云统一服务门户</p>
          </div>
        </div>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <!-- 子系统入口卡片 -->
      <h3 class="portal-section-title">我的服务</h3>
      <div class="subsystem-grid">
        <div
          v-for="sys in currentSystems"
          :key="sys.name"
          class="subsystem-card"
          :style="{ '--sys-color': sys.color }"
          @click="handleSsoJump(sys)"
        >
          <div class="sys-icon">
            <el-icon :size="32"><component :is="sys.icon" /></el-icon>
          </div>
          <div class="sys-info">
            <h4>{{ sys.name }}</h4>
            <p>{{ sys.desc }}</p>
          </div>
          <div class="sys-arrow">
            <el-icon><arrow-right /></el-icon>
          </div>
        </div>
      </div>

      <!-- 快速操作 -->
      <h3 class="portal-section-title">快速操作</h3>
      <div class="quick-actions">
        <router-link
          v-for="action in quickActions"
          :key="action.label"
          :to="action.path"
          class="quick-action-item"
        >
          <el-icon :size="20" :color="action.color"><component :is="action.icon" /></el-icon>
          <span>{{ action.label }}</span>
        </router-link>
      </div>

      <!-- 最新公告 -->
      <h3 class="portal-section-title">最新公告</h3>
      <div class="notice-list">
        <div v-for="n in notices" :key="n.id" class="notice-item">
          <span class="notice-dot"></span>
          <router-link :to="'/news/' + n.id" class="notice-title">{{ n.title }}</router-link>
          <span class="notice-date">{{ n.publishDate }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { authApi, newsApi } from '@/api'
import { ArrowRight, Monitor, OfficeBuilding, Cellphone, Setting, Document, Share, Search, Bell } from '@element-plus/icons-vue'

const ROLE_SYSTEMS = {
  supervisor: [
    { name: '影像监管系统', desc: '影像数据统计、机构合规审核、费用核查', icon: 'Monitor', color: '#1B6FD8', system: 'supervisor-main' },
    { name: '数据分析平台', desc: '影像使用数据统计与分析报表', icon: 'Document', color: '#0891B2', system: 'supervisor-analytics' },
    { name: '政策管理后台', desc: '政策法规文件发布与管理', icon: 'Setting', color: '#7C3AED', system: 'supervisor-policy' }
  ],
  hospital: [
    { name: '影像工作站', desc: '影像上传、调阅与质控管理', icon: 'Monitor', color: '#0891B2', system: 'hospital-workstation' },
    { name: '云胶片管理', desc: '云胶片生成、开具与分发', icon: 'Document', color: '#1B6FD8', system: 'hospital-film' },
    { name: '互认操作台', desc: '检查结果互认申请与确认', icon: 'Share', color: '#EA580C', system: 'hospital-recognition' }
  ],
  patient: [
    { name: '我的影像', desc: '查询个人历次影像检查记录', icon: 'Search', color: '#EA580C', system: 'patient-images' },
    { name: '云胶片查看', desc: '在线查看和下载云胶片', icon: 'Document', color: '#1B6FD8', system: 'patient-film' },
    { name: '互认记录', desc: '查询个人检查互认历史', icon: 'Share', color: '#0891B2', system: 'patient-recognition' }
  ],
  developer: [
    { name: 'API 控制台', desc: '接口调用统计与密钥管理', icon: 'Setting', color: '#7C3AED', system: 'dev-console' },
    { name: '测试沙箱', desc: '接口联调测试环境', icon: 'Monitor', color: '#1B6FD8', system: 'dev-sandbox' },
    { name: '文档中心', desc: 'API 文档与技术规范', icon: 'Document', color: '#0891B2', system: 'dev-docs' }
  ]
}

export default {
  name: 'PortalView',
  components: { ArrowRight, Monitor, OfficeBuilding, Cellphone, Setting, Document, Share, Search, Bell },
  data() {
    return {
      notices: [],
      quickActions: [
        { label: '政策法规', path: '/policy', icon: 'Document', color: '#1B6FD8' },
        { label: '新闻动态', path: '/news', icon: 'Bell', color: '#0891B2' },
        { label: '检查互认', path: '/mutual-recognition', icon: 'Share', color: '#EA580C' },
        { label: '机构接入', path: '/institutions', icon: 'OfficeBuilding', color: '#7C3AED' },
        { label: '开发者中心', path: '/developer', icon: 'Setting', color: '#16A34A' }
      ]
    }
  },
  computed: {
    ...mapGetters(['userInfo', 'userName', 'userRole']),
    roleName() {
      const map = { supervisor: '医保监管部门', hospital: '医疗机构', patient: '参保人员', developer: '系统开发者' }
      return map[this.userRole] || '用户'
    },
    roleColor() {
      const colors = { supervisor: '#1B6FD8', hospital: '#0891B2', patient: '#EA580C', developer: '#7C3AED' }
      return colors[this.userRole] || '#1B6FD8'
    },
    currentSystems() {
      return ROLE_SYSTEMS[this.userRole] || []
    }
  },
  created() {
    this.loadNotices()
  },
  methods: {
    async loadNotices() {
      try {
        const res = await newsApi.latest()
        this.notices = (res.data || []).slice(0, 5)
      } catch (e) {
        this.notices = [
          { id: 1, title: '铁岭市医保影像云平台正式上线运行', publishDate: '2025-10-01' },
          { id: 2, title: '关于云胶片服务开通使用的公告', publishDate: '2025-09-20' },
          { id: 3, title: '辽宁省医保影像云试点工作现场会在铁岭召开', publishDate: '2025-09-10' }
        ]
      }
    },
    async handleSsoJump(sys) {
      try {
        await authApi.getSsoToken(sys.system)
        this.$message.success(`正在跳转至 ${sys.name}...`)
        // 实际项目中跳转到子系统URL并携带SSO Token
        // window.open(`${sys.url}?ssoToken=${token}`, '_blank')
        this.$message.info(`${sys.name} 子系统即将上线，敬请期待`)
      } catch (e) {
        this.$message.info(`${sys.name} 子系统即将上线，敬请期待`)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.portal-header {
  background: linear-gradient(135deg, #0D4FA0 0%, #1B6FD8 100%);
  padding: 32px 0;

  .portal-welcome {
    display: flex;
    align-items: center;
    gap: 20px;
    color: white;

    .welcome-avatar {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
      font-weight: 700;
      color: white;
      flex-shrink: 0;
      border: 2px solid rgba(255,255,255,0.4);
    }

    h2 { font-size: 22px; font-weight: 700; margin-bottom: 4px; }
    p { font-size: 14px; opacity: 0.8; }
  }
}

.portal-section-title {
  font-size: 18px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 16px;
  margin-top: 32px;
  &:first-child { margin-top: 0; }
}

.subsystem-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 8px;

  .subsystem-card {
    display: flex;
    align-items: center;
    gap: 16px;
    background: white;
    border: 1px solid $border-color;
    border-radius: $radius-lg;
    padding: 16px 20px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: var(--sys-color);
      box-shadow: $shadow-md;
    }

    .sys-icon {
      width: 52px;
      height: 52px;
      border-radius: $radius-md;
      background: color-mix(in srgb, var(--sys-color) 10%, white);
      color: var(--sys-color);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .sys-info {
      flex: 1;
      h4 { font-size: 15px; font-weight: 700; color: $text-primary; margin-bottom: 4px; }
      p { font-size: 13px; color: $text-secondary; }
    }

    .sys-arrow {
      color: $text-muted;
      transition: color 0.2s;
    }

    &:hover .sys-arrow { color: var(--sys-color); }
  }
}

.quick-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 8px;

  .quick-action-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 20px;
    background: white;
    border: 1px solid $border-color;
    border-radius: $radius-md;
    font-size: 14px;
    color: $text-secondary;
    transition: all 0.2s;

    &:hover {
      border-color: $primary;
      color: $primary;
      background: $primary-bg;
    }
  }
}

.notice-list {
  background: white;
  border: 1px solid $border-color;
  border-radius: $radius-lg;
  padding: 8px 0;

  .notice-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 20px;
    border-bottom: 1px solid $border-color;
    &:last-child { border-bottom: none; }

    .notice-dot {
      width: 6px;
      height: 6px;
      background: $primary;
      border-radius: 50%;
      flex-shrink: 0;
    }

    .notice-title {
      flex: 1;
      font-size: 14px;
      color: $text-primary;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      &:hover { color: $primary; }
    }

    .notice-date {
      font-size: 12px;
      color: $text-muted;
      white-space: nowrap;
    }
  }
}
</style>
