<template>
  <nav class="navbar">
    <div class="page-container navbar-inner">
      <!-- Logo -->
      <router-link to="/" class="navbar-logo">
        <img src="@/assets/images/logo.png" alt="铁岭医保影像云" class="logo-img" />
      </router-link>

      <!-- 主导航菜单 -->
      <div class="navbar-menu" :class="{ 'is-open': menuOpen }">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
          @click="menuOpen = false"
        >
          {{ item.label }}
        </router-link>
      </div>

      <!-- 右侧操作区 -->
      <div class="navbar-actions">
        <template v-if="isLoggedIn">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :style="{ background: roleColor }">
                {{ userName.charAt(0) }}
              </el-avatar>
              <span class="user-name">{{ userName }}</span>
              <el-icon><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="portal">
                  <el-icon><grid /></el-icon> 进入服务门户
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><switch-button /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <router-link to="/login" class="login-btn">
            <el-icon><user /></el-icon>
            统一登录
          </router-link>
        </template>

        <!-- 移动端菜单按钮 -->
        <button class="menu-toggle" @click="menuOpen = !menuOpen">
          <span></span><span></span><span></span>
        </button>
      </div>
    </div>
  </nav>
</template>

<script>
import { mapGetters } from 'vuex'
import { ArrowDown, Grid, SwitchButton, User } from '@element-plus/icons-vue'

export default {
  name: 'AppNavbar',
  components: { ArrowDown, Grid, SwitchButton, User },
  data() {
    return {
      menuOpen: false,
      navItems: [
        { path: '/', label: '首页' },
        { path: '/portal', label: '统一服务门户' },
        { path: '/policy', label: '政策法规' },
        { path: '/news', label: '新闻动态' },
        { path: '/mutual-recognition', label: '检查互认' },
        { path: '/institutions', label: '机构接入' },
        { path: '/developer', label: '开发者中心' }
      ]
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userName', 'userRole']),
    roleColor() {
      const colors = {
        supervisor: '#1B6FD8',
        hospital: '#0891B2',
        patient: '#EA580C',
        developer: '#7C3AED'
      }
      return colors[this.userRole] || '#1B6FD8'
    }
  },
  methods: {
    isActive(path) {
      if (path === '/') return this.$route.path === '/'
      return this.$route.path.startsWith(path)
    },
    async handleCommand(command) {
      if (command === 'logout') {
        await this.$store.dispatch('logout')
        this.$router.push('/')
        this.$message.success('已安全退出')
      } else if (command === 'portal') {
        this.$router.push('/portal')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: white;
  border-bottom: 1px solid $border-color;
  box-shadow: $shadow-sm;

  .navbar-inner {
    display: flex;
    align-items: center;
    height: 64px;
    gap: 0;
  }

  .navbar-logo {
    flex-shrink: 0;
    margin-right: 32px;
    .logo-img {
      height: 40px;
      width: auto;
    }
  }

  .navbar-menu {
    display: flex;
    align-items: center;
    gap: 4px;
    flex: 1;

    .nav-item {
      padding: 8px 14px;
      border-radius: $radius-md;
      font-size: 14px;
      font-weight: 500;
      color: $text-secondary;
      transition: all 0.2s;
      white-space: nowrap;

      &:hover {
        color: $primary;
        background: $primary-bg;
      }

      &.active {
        color: $primary;
        background: $primary-bg;
        font-weight: 600;
      }
    }
  }

  .navbar-actions {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-left: auto;

    .login-btn {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 20px;
      background: $primary;
      color: white;
      border-radius: $radius-md;
      font-size: 14px;
      font-weight: 500;
      transition: background 0.2s;

      &:hover {
        background: $primary-dark;
        color: white;
      }
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 6px 12px;
      border-radius: $radius-md;
      transition: background 0.2s;

      &:hover {
        background: $bg-light;
      }

      .user-name {
        font-size: 14px;
        font-weight: 500;
        color: $text-primary;
      }
    }
  }

  .menu-toggle {
    display: none;
    flex-direction: column;
    gap: 4px;
    background: none;
    border: none;
    cursor: pointer;
    padding: 4px;

    span {
      display: block;
      width: 22px;
      height: 2px;
      background: $text-primary;
      border-radius: 2px;
      transition: all 0.2s;
    }
  }
}

@media (max-width: $breakpoint-lg) {
  .navbar {
    .navbar-menu {
      display: none;
      position: fixed;
      top: 64px;
      left: 0;
      right: 0;
      background: white;
      border-bottom: 1px solid $border-color;
      flex-direction: column;
      padding: 16px;
      gap: 4px;
      box-shadow: $shadow-md;

      &.is-open {
        display: flex;
      }

      .nav-item {
        width: 100%;
        padding: 12px 16px;
      }
    }

    .menu-toggle {
      display: flex;
    }
  }
}
</style>
