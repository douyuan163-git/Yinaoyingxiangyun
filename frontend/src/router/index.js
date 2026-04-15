import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { title: '首页 - 铁岭市医保影像云统一服务门户' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { title: '统一登录 - 铁岭市医保影像云', requiresGuest: true }
  },
  {
    path: '/portal',
    name: 'Portal',
    component: () => import('@/views/PortalView.vue'),
    meta: { title: '统一服务门户 - 铁岭市医保影像云', requiresAuth: true }
  },
  {
    path: '/policy',
    name: 'Policy',
    component: () => import('@/views/PolicyView.vue'),
    meta: { title: '政策法规 - 铁岭市医保影像云' }
  },
  {
    path: '/policy/:id',
    name: 'PolicyDetail',
    component: () => import('@/views/PolicyDetailView.vue'),
    meta: { title: '政策详情 - 铁岭市医保影像云' }
  },
  {
    path: '/news',
    name: 'News',
    component: () => import('@/views/NewsView.vue'),
    meta: { title: '新闻动态 - 铁岭市医保影像云' }
  },
  {
    path: '/news/:id',
    name: 'NewsDetail',
    component: () => import('@/views/NewsDetailView.vue'),
    meta: { title: '新闻详情 - 铁岭市医保影像云' }
  },
  {
    path: '/mutual-recognition',
    name: 'MutualRecognition',
    component: () => import('@/views/MutualRecognitionView.vue'),
    meta: { title: '检查互认 - 铁岭市医保影像云' }
  },
  {
    path: '/institutions',
    name: 'Institutions',
    component: () => import('@/views/InstitutionsView.vue'),
    meta: { title: '机构接入 - 铁岭市医保影像云' }
  },
  {
    path: '/developer',
    name: 'Developer',
    component: () => import('@/views/DeveloperView.vue'),
    meta: { title: '开发者中心 - 铁岭市医保影像云' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFoundView.vue'),
    meta: { title: '页面不存在 - 铁岭市医保影像云' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) return savedPosition
    return { top: 0 }
  }
})

// 路由守卫：需要登录的页面
router.beforeEach((to, from, next) => {
  // 更新页面标题
  document.title = to.meta.title || '铁岭市医保影像云统一服务门户'

  const isLoggedIn = store.getters.isLoggedIn

  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.meta.requiresGuest && isLoggedIn) {
    next({ path: '/portal' })
  } else {
    next()
  }
})

export default router
