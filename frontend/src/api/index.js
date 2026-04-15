/**
 * API 接口统一管理
 * 所有请求 Content-Type 均为 application/json; charset=utf-8
 */
import request from '@/utils/request'

// ==================== 认证接口 ====================
export const authApi = {
  /** 登录 */
  login: (data) => request.post('/auth/login', data),

  /** 登出 */
  logout: () => request.post('/auth/logout'),

  /** 刷新 Token */
  refresh: (refreshToken) => request.post('/auth/refresh', { refreshToken }),

  /** 获取 SSO 跳转 Token */
  getSsoToken: (system) => request.get('/auth/sso-token', { params: { system } }),

  /** 获取当前用户信息 */
  getMe: () => request.get('/auth/me'),

  /** 验证 Token */
  validate: () => request.get('/auth/validate')
}

// ==================== 政策法规接口 ====================
export const policyApi = {
  /** 获取政策列表 */
  list: (params) => request.get('/policy/list', { params }),

  /** 获取政策详情 */
  detail: (id) => request.get(`/policy/${id}`)
}

// ==================== 新闻动态接口 ====================
export const newsApi = {
  /** 获取新闻列表 */
  list: (params) => request.get('/news/list', { params }),

  /** 获取最新5条新闻 */
  latest: () => request.get('/news/latest'),

  /** 获取新闻详情 */
  detail: (id) => request.get(`/news/${id}`)
}

// ==================== 机构接入接口 ====================
export const institutionApi = {
  /** 获取机构列表（分页） */
  list: (params) => request.get('/institution/list', { params }),

  /** 获取所有运行中机构 */
  all: () => request.get('/institution/all'),

  /** 获取支持互认的机构 */
  recognitionList: () => request.get('/institution/recognition'),

  /** 获取机构详情 */
  detail: (id) => request.get(`/institution/${id}`),

  /** 获取统计数据 */
  stats: () => request.get('/institution/stats')
}

// ==================== 检查互认接口 ====================
export const recognitionApi = {
  /** 根据身份证号查询互认记录 */
  query: (params) => request.get('/recognition/query', { params }),

  /** 获取互认统计 */
  stats: () => request.get('/recognition/stats')
}
