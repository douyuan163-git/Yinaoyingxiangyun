import { createStore } from 'vuex'
import { login, logout } from '@/api'

export default createStore({
  state: {
    token: localStorage.getItem('admin_token') || '',
    user: JSON.parse(localStorage.getItem('admin_user') || 'null'),
    sidebarCollapsed: false
  },
  getters: {
    isLoggedIn: state => !!state.token,
    currentUser: state => state.user,
    sidebarCollapsed: state => state.sidebarCollapsed
  },
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('admin_token', token)
    },
    SET_USER(state, user) {
      state.user = user
      localStorage.setItem('admin_user', JSON.stringify(user))
    },
    CLEAR_AUTH(state) {
      state.token = ''
      state.user = null
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_user')
    },
    TOGGLE_SIDEBAR(state) {
      state.sidebarCollapsed = !state.sidebarCollapsed
    }
  },
  actions: {
    async login({ commit }, credentials) {
      const res = await login(credentials)
      commit('SET_TOKEN', res.data.accessToken)
      commit('SET_USER', {
        username: res.data.username,
        realName: res.data.realName,
        role: res.data.role
      })
      return res
    },
    async logout({ commit }) {
      try {
        await logout()
      } catch (e) {
        // 忽略登出接口错误
      }
      commit('CLEAR_AUTH')
    }
  }
})
