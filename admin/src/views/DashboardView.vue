<template>
  <div class="page-container">
    <div class="page-header">
      <h2>仪表盘</h2>
      <span style="font-size: 13px; color: #6B7280;">{{ currentDate }}</span>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stats-row">
      <el-col :span="6" v-for="item in statCards" :key="item.key">
        <div class="stat-card" :style="{ borderTopColor: item.color }">
          <div class="stat-icon" :style="{ background: item.bgColor }">
            <el-icon :size="22" :style="{ color: item.color }">
              <component :is="item.icon" />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats[item.key] ?? '-' }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 用户角色分布 -->
    <el-row :gutter="16" style="margin-top: 16px;">
      <el-col :span="12">
        <div class="card">
          <h3 class="card-title">用户角色分布</h3>
          <div v-if="stats.userByRole" class="role-list">
            <div
              v-for="(count, role) in stats.userByRole"
              :key="role"
              class="role-item"
            >
              <div class="role-info">
                <el-tag :type="roleTagType(role)" size="small">{{ roleLabel(role) }}</el-tag>
                <span class="role-count">{{ count }} 人</span>
              </div>
              <el-progress
                :percentage="Math.round(count / totalUsers * 100)"
                :color="roleColor(role)"
                :show-text="false"
                style="flex: 1; margin-left: 12px;"
              />
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="card">
          <h3 class="card-title">快捷操作</h3>
          <div class="quick-actions">
            <el-button
              v-for="action in quickActions"
              :key="action.path"
              :type="action.type"
              plain
              @click="$router.push(action.path)"
            >
              <el-icon><component :is="action.icon" /></el-icon>
              {{ action.label }}
            </el-button>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '@/api'

export default {
  name: 'DashboardView',
  setup() {
    const stats = ref({})

    const currentDate = new Date().toLocaleDateString('zh-CN', {
      year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
    })

    const statCards = [
      { key: 'totalUsers', label: '注册用户', icon: 'User', color: '#1B6FD8', bgColor: '#EFF6FF' },
      { key: 'totalInstitutions', label: '接入机构', icon: 'OfficeBuilding', color: '#10B981', bgColor: '#ECFDF5' },
      { key: 'totalPolicies', label: '政策文件', icon: 'Document', color: '#F59E0B', bgColor: '#FFFBEB' },
      { key: 'totalRecognitions', label: '互认记录', icon: 'Connection', color: '#8B5CF6', bgColor: '#F5F3FF' }
    ]

    const quickActions = [
      { label: '发布新闻', path: '/content/news/edit/', type: 'primary', icon: 'Plus' },
      { label: '添加政策', path: '/content/policy/edit/', type: 'success', icon: 'Plus' },
      { label: '配置首页', path: '/config/home', type: 'warning', icon: 'Setting' },
      { label: '用户管理', path: '/users', type: 'info', icon: 'User' }
    ]

    const totalUsers = computed(() => {
      if (!stats.value.userByRole) return 1
      return Object.values(stats.value.userByRole).reduce((a, b) => a + b, 0) || 1
    })

    const roleLabel = (role) => {
      const map = { admin: '系统管理员', supervisor: '监管部门', hospital: '医疗机构', patient: '参保人员', developer: '开发者' }
      return map[role] || role
    }

    const roleTagType = (role) => {
      const map = { admin: 'danger', supervisor: 'primary', hospital: 'success', patient: '', developer: 'warning' }
      return map[role] || 'info'
    }

    const roleColor = (role) => {
      const map = { admin: '#EF4444', supervisor: '#1B6FD8', hospital: '#10B981', patient: '#6B7280', developer: '#F59E0B' }
      return map[role] || '#6B7280'
    }

    onMounted(async () => {
      try {
        const res = await getDashboard()
        stats.value = res.data
      } catch (e) {
        // 忽略
      }
    })

    return { stats, currentDate, statCards, quickActions, totalUsers, roleLabel, roleTagType, roleColor }
  }
}
</script>

<style lang="scss" scoped>
.stats-row {
  margin-bottom: 0;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  border-top: 3px solid transparent;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1F2937;
  line-height: 1;
}

.stat-label {
  font-size: 13px;
  color: #6B7280;
  margin-top: 4px;
}

.card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #1F2937;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #F3F4F6;
}

.role-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.role-info {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 140px;
  flex-shrink: 0;
}

.role-count {
  font-size: 13px;
  color: #374151;
  font-weight: 500;
}

.quick-actions {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
</style>
