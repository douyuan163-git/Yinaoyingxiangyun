import request from '@/utils/request'

// ===== 认证 =====
export const login = (data) => request.post('/auth/login', data)
export const logout = () => request.post('/auth/logout')

// ===== 仪表盘 =====
export const getDashboard = () => request.get('/admin/dashboard')

// ===== 网站配置 =====
export const getSiteConfig = (groupKey) => request.get('/admin/config', { params: { groupKey } })
export const saveSiteConfig = (groupKey, data) => request.post(`/admin/config/${groupKey}`, data)

// ===== Banner管理 =====
export const listBanners = (onlyEnabled = false) => request.get('/admin/banners', { params: { onlyEnabled } })
export const saveBanner = (data) => request.post('/admin/banners', data)
export const deleteBanner = (id) => request.delete(`/admin/banners/${id}`)
export const updateBannerStatus = (id, status) => request.put(`/admin/banners/${id}/status`, { status })

// ===== 首页统计 =====
export const listHomeStats = (onlyEnabled = false) => request.get('/admin/home-stats', { params: { onlyEnabled } })
export const saveHomeStat = (data) => request.post('/admin/home-stats', data)
export const deleteHomeStat = (id) => request.delete(`/admin/home-stats/${id}`)

// ===== 服务门户入口 =====
export const listPortalEntries = (onlyEnabled = false) => request.get('/admin/portal-entries', { params: { onlyEnabled } })
export const savePortalEntry = (data) => request.post('/admin/portal-entries', data)
export const deletePortalEntry = (id) => request.delete(`/admin/portal-entries/${id}`)
export const updatePortalEntryStatus = (id, status) => request.put(`/admin/portal-entries/${id}/status`, { status })

// ===== 开发者API =====
export const listDevApis = (onlyEnabled = false) => request.get('/admin/dev-apis', { params: { onlyEnabled } })
export const saveDevApi = (data) => request.post('/admin/dev-apis', data)
export const deleteDevApi = (id) => request.delete(`/admin/dev-apis/${id}`)

// ===== 政策法规 =====
export const listPolicies = (params) => request.get('/policies', { params })
export const getPolicy = (id) => request.get(`/policies/${id}`)
export const savePolicy = (data) => request.post('/policies', data)
export const deletePolicy = (id) => request.delete(`/policies/${id}`)

// ===== 新闻动态 =====
export const listNews = (params) => request.get('/news', { params })
export const getNewsDetail = (id) => request.get(`/news/${id}`)
export const saveNews = (data) => request.post('/news', data)
export const deleteNews = (id) => request.delete(`/news/${id}`)

// ===== 机构管理 =====
export const listInstitutions = (params) => request.get('/institutions', { params })
export const getInstitution = (id) => request.get(`/institutions/${id}`)
export const saveInstitution = (data) => request.post('/institutions', data)
export const deleteInstitution = (id) => request.delete(`/institutions/${id}`)

// ===== 检查互认 =====
export const listRecognitions = (params) => request.get('/recognition', { params })
export const saveRecognition = (data) => request.post('/recognition', data)
export const deleteRecognition = (id) => request.delete(`/recognition/${id}`)

// ===== 用户管理 =====
export const listUsers = (params) => request.get('/admin/users', { params })
export const saveUser = (data) => request.post('/admin/users', data)
export const deleteUser = (id) => request.delete(`/admin/users/${id}`)
export const updateUserStatus = (id, status) => request.put(`/admin/users/${id}/status`, { status })
export const resetPassword = (id, password) => request.put(`/admin/users/${id}/reset-password`, { password })
