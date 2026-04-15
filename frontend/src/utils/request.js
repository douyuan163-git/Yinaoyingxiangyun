/**
 * Axios 请求封装
 * - 统一设置 Content-Type: application/json; charset=utf-8
 * - 自动携带 JWT Token
 * - 统一响应拦截和错误处理
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import store from '@/store'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json; charset=utf-8'
  }
})

// 请求拦截器：自动附加 JWT Token
request.interceptors.request.use(
  config => {
    const token = store.getters.accessToken
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器：统一处理响应和错误
request.interceptors.response.use(
  response => {
    const res = response.data
    // 业务成功
    if (res.code === 200) {
      return res
    }
    // 未授权：跳转登录
    if (res.code === 401) {
      store.dispatch('logout')
      router.push('/login')
      ElMessage.error(res.message || '登录已过期，请重新登录')
      return Promise.reject(new Error(res.message))
    }
    // 其他业务错误
    ElMessage.error(res.message || '操作失败')
    return Promise.reject(new Error(res.message))
  },
  error => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        store.dispatch('logout')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
      } else if (status === 403) {
        ElMessage.error('权限不足，拒绝访问')
      } else if (status === 500) {
        ElMessage.error(data?.message || '服务器内部错误')
      } else {
        ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请检查网络连接')
    } else {
      ElMessage.error('网络连接失败，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
