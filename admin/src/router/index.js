import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false, title: '管理员登录' }
  },
  {
    path: '/',
    component: () => import('@/components/AdminLayout.vue'),
    meta: { requiresAuth: true },
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      // ===== 网站配置 =====
      {
        path: 'config/basic',
        name: 'ConfigBasic',
        component: () => import('@/views/config/BasicConfigView.vue'),
        meta: { title: '基本信息配置', icon: 'Setting' }
      },
      {
        path: 'config/home',
        name: 'ConfigHome',
        component: () => import('@/views/config/HomeConfigView.vue'),
        meta: { title: '首页配置', icon: 'HomeFilled' }
      },
      {
        path: 'config/sso',
        name: 'ConfigSSO',
        component: () => import('@/views/config/SsoConfigView.vue'),
        meta: { title: 'SSO单点登录配置', icon: 'Key' }
      },
      {
        path: 'config/banner',
        name: 'ConfigBanner',
        component: () => import('@/views/config/BannerConfigView.vue'),
        meta: { title: 'Banner管理', icon: 'Picture' }
      },
      {
        path: 'config/stats',
        name: 'ConfigStats',
        component: () => import('@/views/config/StatsConfigView.vue'),
        meta: { title: '统计数字配置', icon: 'DataLine' }
      },
      {
        path: 'config/portal',
        name: 'ConfigPortal',
        component: () => import('@/views/config/PortalConfigView.vue'),
        meta: { title: '服务门户入口配置', icon: 'Grid' }
      },
      // ===== 内容管理 =====
      {
        path: 'content/policy',
        name: 'ContentPolicy',
        component: () => import('@/views/content/PolicyListView.vue'),
        meta: { title: '政策法规管理', icon: 'Document' }
      },
      {
        path: 'content/policy/edit/:id?',
        name: 'ContentPolicyEdit',
        component: () => import('@/views/content/PolicyEditView.vue'),
        meta: { title: '编辑政策', icon: 'Edit' }
      },
      {
        path: 'content/news',
        name: 'ContentNews',
        component: () => import('@/views/content/NewsListView.vue'),
        meta: { title: '新闻动态管理', icon: 'Notification' }
      },
      {
        path: 'content/news/edit/:id?',
        name: 'ContentNewsEdit',
        component: () => import('@/views/content/NewsEditView.vue'),
        meta: { title: '编辑新闻', icon: 'Edit' }
      },
      {
        path: 'content/institution',
        name: 'ContentInstitution',
        component: () => import('@/views/content/InstitutionListView.vue'),
        meta: { title: '机构接入管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'content/institution/edit/:id?',
        name: 'ContentInstitutionEdit',
        component: () => import('@/views/content/InstitutionEditView.vue'),
        meta: { title: '编辑机构', icon: 'Edit' }
      },
      {
        path: 'content/recognition',
        name: 'ContentRecognition',
        component: () => import('@/views/content/RecognitionListView.vue'),
        meta: { title: '检查互认管理', icon: 'Connection' }
      },
      {
        path: 'content/developer',
        name: 'ContentDeveloper',
        component: () => import('@/views/config/DevApiConfigView.vue'),
        meta: { title: '开发者API管理', icon: 'Monitor' }
      },
      // ===== 用户管理 =====
      {
        path: 'users',
        name: 'UserList',
        component: () => import('@/views/users/UserListView.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      // ===== 系统 =====
      {
        path: 'system/profile',
        name: 'SystemProfile',
        component: () => import('@/views/system/ProfileView.vue'),
        meta: { title: '个人信息', icon: 'UserFilled' }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '后台管理'} - 铁岭医保影像云`
  if (to.meta.requiresAuth !== false && !store.getters.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && store.getters.isLoggedIn) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
