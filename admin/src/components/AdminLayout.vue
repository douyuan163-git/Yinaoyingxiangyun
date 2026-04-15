<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside :class="['sidebar', { collapsed: sidebarCollapsed }]">
      <div class="logo-area">
        <div class="logo-icon">医</div>
        <span v-if="!sidebarCollapsed" class="logo-text">影像云管理</span>
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="sidebarCollapsed"
        router
        background-color="#001529"
        text-color="#B0C4DE"
        active-text-color="#1B6FD8"
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>

        <el-sub-menu index="config">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>网站配置</span>
          </template>
          <el-menu-item index="/config/basic">基本信息</el-menu-item>
          <el-menu-item index="/config/home">首页配置</el-menu-item>
          <el-menu-item index="/config/banner">Banner管理</el-menu-item>
          <el-menu-item index="/config/stats">统计数字</el-menu-item>
          <el-menu-item index="/config/portal">服务门户入口</el-menu-item>
          <el-menu-item index="/config/sso">SSO配置</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="content">
          <template #title>
            <el-icon><Files /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/content/policy">政策法规</el-menu-item>
          <el-menu-item index="/content/news">新闻动态</el-menu-item>
          <el-menu-item index="/content/institution">机构接入</el-menu-item>
          <el-menu-item index="/content/recognition">检查互认</el-menu-item>
          <el-menu-item index="/content/developer">开发者API</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>

        <el-menu-item index="/system/profile">
          <el-icon><UserFilled /></el-icon>
          <template #title>个人信息</template>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-area">
      <!-- 顶部导航 -->
      <header class="header">
        <div class="header-left">
          <el-button
            text
            @click="toggleSidebar"
            class="collapse-btn"
          >
            <el-icon size="18"><Fold v-if="!sidebarCollapsed" /><Expand v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <span class="welcome-text">欢迎，{{ currentUser?.realName || currentUser?.username }}</span>
          <el-dropdown @command="handleCommand">
            <el-avatar :size="32" style="background: #1B6FD8; cursor: pointer;">
              {{ (currentUser?.realName || currentUser?.username || 'A').charAt(0) }}
            </el-avatar>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 页面内容 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'

export default {
  name: 'AdminLayout',
  setup() {
    const store = useStore()
    const router = useRouter()

    const sidebarCollapsed = computed(() => store.getters.sidebarCollapsed)
    const currentUser = computed(() => store.getters.currentUser)

    const toggleSidebar = () => store.commit('TOGGLE_SIDEBAR')

    const handleCommand = async (command) => {
      if (command === 'logout') {
        await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await store.dispatch('logout')
        router.push('/login')
      } else if (command === 'profile') {
        router.push('/system/profile')
      }
    }

    return { sidebarCollapsed, currentUser, toggleSidebar, handleCommand }
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 220px;
  background: #001529;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  flex-shrink: 0;

  &.collapsed {
    width: 64px;
  }
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  gap: 10px;
  border-bottom: 1px solid rgba(255,255,255,0.08);
  overflow: hidden;
}

.logo-icon {
  width: 32px;
  height: 32px;
  background: #1B6FD8;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.logo-text {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
  overflow-x: hidden;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: rgba(255,255,255,0.1);
    border-radius: 2px;
  }
}

.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #F0F2F5;
}

.header {
  height: 60px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.08);
  flex-shrink: 0;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.collapse-btn {
  padding: 6px;
  color: #6B7280;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.welcome-text {
  font-size: 13px;
  color: #6B7280;
}

.content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}
</style>
