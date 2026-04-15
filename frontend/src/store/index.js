import { createStore } from 'vuex'
import { authApi } from '@/api'

const TOKEN_KEY = 'yibao_access_token'
const REFRESH_TOKEN_KEY = 'yibao_refresh_token'
const USER_KEY = 'yibao_user_info'

export default createStore({
  state: {
    accessToken: localStorage.getItem(TOKEN_KEY) || '',
    refreshToken: localStorage.getItem(REFRESH_TOKEN_KEY) || '',
    userInfo: JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  },

  getters: {
    isLoggedIn: state => !!state.accessToken,
    accessToken: state => state.accessToken,
    userInfo: state => state.userInfo,
    userRole: state => state.userInfo?.role || '',
    userName: state => state.userInfo?.realName || state.userInfo?.username || ''
  },

  mutations: {
    SET_TOKEN(state, { accessToken, refreshToken }) {
      state.accessToken = accessToken
      state.refreshToken = refreshToken
      localStorage.setItem(TOKEN_KEY, accessToken)
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken)
    },
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
    },
    CLEAR_AUTH(state) {
      state.accessToken = ''
      state.refreshToken = ''
      state.userInfo = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(REFRESH_TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    }
  },

  actions: {
    async login({ commit }, loginData) {
      const res = await authApi.login(loginData)
      const { accessToken, refreshToken, userId, username, realName, role, roleName } = res.data
      commit('SET_TOKEN', { accessToken, refreshToken })
      commit('SET_USER_INFO', { userId, username, realName, role, roleName })
      return res
    },

    async logout({ commit }) {
      try {
        await authApi.logout()
      } catch (e) {
        // 忽略登出接口错误
      } finally {
        commit('CLEAR_AUTH')
      }
    },

    async refreshToken({ state, commit }) {
      const res = await authApi.refresh(state.refreshToken)
      const { accessToken, refreshToken } = res.data
      commit('SET_TOKEN', { accessToken, refreshToken })
      return res
    }
  }
})
