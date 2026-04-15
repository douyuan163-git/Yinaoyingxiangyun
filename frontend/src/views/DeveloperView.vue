<template>
  <div>
    <div class="page-header" style="background: linear-gradient(135deg, #4C1D95 0%, #7C3AED 50%, #6D28D9 100%);">
      <div class="page-container">
        <h1>开发者中心</h1>
        <p>铁岭市医保影像云 API 文档、技术规范与开发资源</p>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <!-- 快速开始 -->
      <div class="quick-start card">
        <div class="qs-content">
          <h3>快速开始接入</h3>
          <p>完成以下步骤即可将您的系统接入铁岭市医保影像云平台</p>
          <div class="qs-steps">
            <div v-for="(s, i) in quickSteps" :key="i" class="qs-step">
              <div class="qs-num">{{ i + 1 }}</div>
              <span>{{ s }}</span>
            </div>
          </div>
        </div>
        <div class="qs-actions">
          <el-button type="primary" size="large" @click="$message.info('开发者注册功能即将开放')">
            申请开发者账号
          </el-button>
          <el-button size="large" @click="$message.info('测试环境申请功能即将开放')">
            申请测试环境
          </el-button>
        </div>
      </div>

      <!-- API 接口列表 -->
      <div class="api-section">
        <h3 class="section-title">API 接口文档</h3>
        <div class="api-tabs">
          <button
            v-for="tab in apiTabs"
            :key="tab.value"
            class="api-tab"
            :class="{ active: activeApiTab === tab.value }"
            @click="activeApiTab = tab.value"
          >{{ tab.label }}</button>
        </div>
        <div class="api-list">
          <div v-for="api in filteredApis" :key="api.path" class="api-item card">
            <div class="api-method" :class="api.method.toLowerCase()">{{ api.method }}</div>
            <div class="api-info">
              <code class="api-path">{{ api.path }}</code>
              <p class="api-desc">{{ api.desc }}</p>
            </div>
            <div class="api-auth">
              <span v-if="api.auth" class="auth-required">需要认证</span>
              <span v-else class="auth-open">公开接口</span>
            </div>
            <el-button type="primary" plain size="small" @click="$message.info('API详情文档即将开放')">
              查看文档
            </el-button>
          </div>
        </div>
      </div>

      <!-- SDK 下载 -->
      <div class="sdk-section">
        <h3 class="section-title">SDK 与工具下载</h3>
        <div class="sdk-grid">
          <div v-for="sdk in sdkList" :key="sdk.name" class="sdk-card card">
            <div class="sdk-lang">{{ sdk.lang }}</div>
            <h4>{{ sdk.name }}</h4>
            <p>{{ sdk.desc }}</p>
            <div class="sdk-footer">
              <span class="sdk-version">{{ sdk.version }}</span>
              <el-button type="primary" plain size="small" @click="$message.info('SDK下载功能即将开放')">
                下载
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 技术规范 -->
      <div class="spec-section">
        <h3 class="section-title">技术规范文档</h3>
        <div class="spec-list">
          <div v-for="spec in specDocs" :key="spec.name" class="spec-item card">
            <el-icon :size="20" color="#7C3AED"><document /></el-icon>
            <div class="spec-info">
              <h4>{{ spec.name }}</h4>
              <p>{{ spec.desc }}</p>
            </div>
            <span class="spec-version">{{ spec.version }}</span>
            <el-button size="small" @click="$message.info('文档下载功能即将开放')">下载 PDF</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Document } from '@element-plus/icons-vue'

export default {
  name: 'DeveloperView',
  components: { Document },
  data() {
    return {
      activeApiTab: 'all',
      quickSteps: [
        '申请开发者账号并完成实名认证',
        '获取 AppKey 和 AppSecret',
        '下载 SDK 或参考 API 文档',
        '在测试环境完成接口联调',
        '提交生产环境上线申请'
      ],
      apiTabs: [
        { value: 'all', label: '全部接口' },
        { value: 'auth', label: '认证接口' },
        { value: 'image', label: '影像接口' },
        { value: 'recognition', label: '互认接口' },
        { value: 'institution', label: '机构接口' }
      ],
      apis: [
        { method: 'POST', path: '/api/auth/login', desc: '统一身份认证登录，获取 JWT Token', auth: false, category: 'auth' },
        { method: 'POST', path: '/api/auth/logout', desc: '登出，注销当前 Token', auth: true, category: 'auth' },
        { method: 'POST', path: '/api/auth/refresh', desc: '刷新 AccessToken', auth: false, category: 'auth' },
        { method: 'GET', path: '/api/auth/sso-token', desc: '获取子系统 SSO 跳转 Token', auth: true, category: 'auth' },
        { method: 'POST', path: '/api/image/upload', desc: '上传 DICOM 影像文件至影像云', auth: true, category: 'image' },
        { method: 'GET', path: '/api/image/list', desc: '查询患者影像记录列表', auth: true, category: 'image' },
        { method: 'GET', path: '/api/image/{id}', desc: '获取影像详情及查看链接', auth: true, category: 'image' },
        { method: 'POST', path: '/api/image/film', desc: '生成云胶片并获取分享链接', auth: true, category: 'image' },
        { method: 'GET', path: '/api/recognition/query', desc: '根据身份证号查询互认记录', auth: true, category: 'recognition' },
        { method: 'POST', path: '/api/recognition/apply', desc: '发起检查互认申请', auth: true, category: 'recognition' },
        { method: 'GET', path: '/api/institution/all', desc: '获取所有已接入机构列表', auth: false, category: 'institution' },
        { method: 'GET', path: '/api/institution/stats', desc: '获取平台统计数据', auth: false, category: 'institution' }
      ],
      sdkList: [
        { lang: 'Java', name: 'Java SDK', desc: '适用于 Spring Boot / Spring Cloud 项目的官方 SDK', version: 'v1.2.0' },
        { lang: 'Python', name: 'Python SDK', desc: '适用于 Python 3.8+ 项目的官方 SDK', version: 'v1.1.0' },
        { lang: 'JavaScript', name: 'JavaScript SDK', desc: '适用于 Vue/React 前端项目的官方 SDK', version: 'v1.0.5' },
        { lang: 'C#', name: '.NET SDK', desc: '适用于 .NET 6+ 项目的官方 SDK', version: 'v1.0.2' }
      ],
      specDocs: [
        { name: '医保影像云基础设施技术规范', desc: '网络、服务器、存储配置要求', version: 'v1.0' },
        { name: '医保影像云软件应用规范', desc: 'HIS/PACS 系统接入技术要求', version: 'v1.0' },
        { name: '医保影像云影像人员及设备规范', desc: '影像技师、设备配置标准', version: 'v1.0' },
        { name: '医保影像云图像质控规范', desc: 'DICOM 影像质量控制标准', version: 'v1.0' }
      ]
    }
  },
  computed: {
    filteredApis() {
      if (this.activeApiTab === 'all') return this.apis
      return this.apis.filter(a => a.category === this.activeApiTab)
    }
  }
}
</script>

<style lang="scss" scoped>
.quick-start {
  padding: 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  margin-bottom: 40px;
  border-left: 4px solid $developer-color;

  .qs-content {
    flex: 1;
    h3 { font-size: 18px; font-weight: 700; color: $text-primary; margin-bottom: 6px; }
    p { font-size: 14px; color: $text-secondary; margin-bottom: 16px; }
    .qs-steps {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      .qs-step {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 13px;
        color: $text-secondary;
        .qs-num {
          width: 20px;
          height: 20px;
          background: $developer-color;
          color: white;
          border-radius: 50%;
          font-size: 11px;
          font-weight: 700;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }
      }
    }
  }

  .qs-actions {
    display: flex;
    flex-direction: column;
    gap: 12px;
    flex-shrink: 0;
  }
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 20px;
}

.api-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  .api-tab {
    padding: 6px 16px;
    border: 1.5px solid $border-color;
    border-radius: $radius-md;
    background: white;
    cursor: pointer;
    font-size: 13px;
    color: $text-secondary;
    transition: all 0.2s;
    &:hover { border-color: $developer-color; color: $developer-color; }
    &.active { background: $developer-color; border-color: $developer-color; color: white; font-weight: 600; }
  }
}

.api-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 40px;
}

.api-item {
  display: flex;
  align-items: center;
  padding: 14px 20px;
  gap: 16px;

  .api-method {
    width: 60px;
    text-align: center;
    padding: 3px 8px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 700;
    flex-shrink: 0;
    &.get { background: #ECFDF5; color: $success; }
    &.post { background: #EBF3FF; color: $primary; }
    &.put { background: #FFF7ED; color: $warning; }
    &.delete { background: #FEF2F2; color: $error; }
  }

  .api-info {
    flex: 1;
    .api-path {
      font-family: 'Consolas', monospace;
      font-size: 14px;
      color: $text-primary;
      display: block;
      margin-bottom: 4px;
    }
    .api-desc {
      font-size: 13px;
      color: $text-secondary;
    }
  }

  .auth-required { font-size: 12px; color: $warning; background: #FFF7ED; padding: 2px 8px; border-radius: 4px; }
  .auth-open { font-size: 12px; color: $success; background: #ECFDF5; padding: 2px 8px; border-radius: 4px; }
}

.sdk-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 40px;

  .sdk-card {
    padding: 20px;
    .sdk-lang {
      display: inline-block;
      background: #F5F3FF;
      color: $developer-color;
      padding: 2px 10px;
      border-radius: 4px;
      font-size: 12px;
      font-weight: 600;
      margin-bottom: 10px;
    }
    h4 { font-size: 15px; font-weight: 700; color: $text-primary; margin-bottom: 6px; }
    p { font-size: 13px; color: $text-secondary; line-height: 1.5; margin-bottom: 16px; }
    .sdk-footer {
      display: flex;
      align-items: center;
      justify-content: space-between;
      .sdk-version { font-size: 12px; color: $text-muted; }
    }
  }
}

.spec-list {
  display: flex;
  flex-direction: column;
  gap: 10px;

  .spec-item {
    display: flex;
    align-items: center;
    padding: 14px 20px;
    gap: 16px;

    .spec-info {
      flex: 1;
      h4 { font-size: 14px; font-weight: 600; color: $text-primary; margin-bottom: 4px; }
      p { font-size: 13px; color: $text-secondary; }
    }

    .spec-version { font-size: 12px; color: $text-muted; }
  }
}

@media (max-width: $breakpoint-md) {
  .quick-start { flex-direction: column; }
  .sdk-grid { grid-template-columns: repeat(2, 1fr); }
}
</style>
