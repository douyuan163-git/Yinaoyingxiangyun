<template>
  <div id="app">
    <!-- 政务通知栏 -->
    <div class="gov-notice-bar" v-if="showNoticeBar">
      <div class="page-container">
        <span>铁岭市医疗保障局 主办</span>
        <span class="divider">|</span>
        <span>辽宁省医保影像云试点示范单位</span>
        <button class="close-btn" @click="showNoticeBar = false">×</button>
      </div>
    </div>

    <!-- 主导航 -->
    <AppNavbar />

    <!-- 页面内容 -->
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>

    <!-- 页脚 -->
    <AppFooter />
  </div>
</template>

<script>
import AppNavbar from '@/components/AppNavbar.vue'
import AppFooter from '@/components/AppFooter.vue'

export default {
  name: 'App',
  components: { AppNavbar, AppFooter },
  data() {
    return {
      showNoticeBar: true
    }
  }
}
</script>

<style lang="scss">
.gov-notice-bar {
  position: relative;
  .divider {
    margin: 0 12px;
    opacity: 0.5;
  }
  .close-btn {
    position: absolute;
    right: 24px;
    top: 50%;
    transform: translateY(-50%);
    background: none;
    border: none;
    color: white;
    font-size: 16px;
    cursor: pointer;
    opacity: 0.7;
    &:hover { opacity: 1; }
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
