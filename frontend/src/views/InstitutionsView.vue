<template>
  <div>
    <div class="page-header">
      <div class="page-container">
        <h1>机构接入</h1>
        <p>铁岭市医保影像云平台接入机构展示与接入申请指南</p>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <!-- 接入统计 -->
      <div class="stats-row">
        <div class="stat-item" v-for="s in stats" :key="s.label">
          <span class="stat-num">{{ s.value }}</span>
          <span class="stat-label">{{ s.label }}</span>
        </div>
      </div>

      <!-- 接入机构列表 -->
      <div class="section-block">
        <div class="section-block-header">
          <h3>已接入医疗机构</h3>
          <el-input
            v-model="keyword"
            placeholder="搜索机构名称..."
            :prefix-icon="Search"
            clearable
            style="width: 240px;"
            @input="handleSearch"
          />
        </div>
        <div class="institution-grid">
          <div v-for="inst in institutions" :key="inst.id" class="institution-card card">
            <div class="inst-top">
              <div class="inst-avatar">
                <el-icon :size="24"><office-building /></el-icon>
              </div>
              <div class="inst-badge running">已接入</div>
            </div>
            <h4>{{ inst.name }}</h4>
            <div class="inst-meta">
              <span>{{ inst.level }}</span>
              <span class="dot">·</span>
              <span>{{ inst.type }}</span>
              <span class="dot">·</span>
              <span>{{ inst.district }}</span>
            </div>
            <div class="inst-address">
              <el-icon><location /></el-icon>{{ inst.address }}
            </div>
            <div class="inst-phone">
              <el-icon><phone /></el-icon>{{ inst.phone }}
            </div>
            <div class="inst-items">
              <span v-for="item in inst.recognitionItems?.split(',')" :key="item" class="item-tag">{{ item }}</span>
            </div>
            <div class="inst-join-date">接入时间：{{ inst.joinDate }}</div>
          </div>
        </div>
        <div class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            :total="total"
            :page-size="pageSize"
            layout="prev, pager, next, total"
            @current-change="loadInstitutions"
          />
        </div>
      </div>

      <!-- 接入流程 -->
      <div class="section-block">
        <h3 class="block-title">机构接入流程</h3>
        <div class="process-flow">
          <div v-for="(step, idx) in joinSteps" :key="idx" class="flow-step">
            <div class="flow-step-num">{{ idx + 1 }}</div>
            <h4>{{ step.title }}</h4>
            <p>{{ step.desc }}</p>
          </div>
        </div>
      </div>

      <!-- 技术文档下载 -->
      <div class="section-block">
        <h3 class="block-title">接入技术文档</h3>
        <div class="doc-grid">
          <div v-for="doc in techDocs" :key="doc.name" class="doc-item card">
            <div class="doc-icon">
              <el-icon :size="28"><document /></el-icon>
            </div>
            <div class="doc-info">
              <h4>{{ doc.name }}</h4>
              <p>{{ doc.desc }}</p>
              <span class="doc-version">{{ doc.version }}</span>
            </div>
            <el-button type="primary" plain size="small" @click="$message.info('文档下载功能即将开放')">
              下载
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { institutionApi, publicApi } from '@/api'
import { Search, OfficeBuilding, Location, Phone, Document } from '@element-plus/icons-vue'

export default {
  name: 'InstitutionsView',
  components: { OfficeBuilding, Location, Phone, Document },
  data() {
    return {
      Search,
      loading: false,
      institutions: [],
      total: 0,
      currentPage: 1,
      pageSize: 9,
      keyword: '',
      stats: [
        { value: '7家', label: '已接入机构' },
        { value: '3家', label: '三级医院' },
        { value: '4家', label: '二级医院' },
        { value: '100%', label: '市辖区覆盖率' }
      ],
      joinSteps: [
        { title: '提交接入申请', desc: '填写机构信息和接入需求，向铁岭市医保局提交书面申请' },
        { title: '资质审核', desc: '医保局对申请机构的资质、设备和网络条件进行审核' },
        { title: '技术对接', desc: '按照技术规范完成HIS/PACS系统改造和接口开发' },
        { title: '联调测试', desc: '在测试环境完成数据上传、影像调阅等功能的联调测试' },
        { title: '正式上线', desc: '测试通过后，完成生产环境部署，正式接入医保影像云' }
      ],
      techDocs: [
        { name: '医保影像云接入技术规范', desc: '详细描述接口规范、数据格式和安全要求', version: 'v2.1.0' },
        { name: '影像数据上传接口文档', desc: 'DICOM影像数据上传和存储接口说明', version: 'v1.5.0' },
        { name: '云胶片服务集成指南', desc: '云胶片生成、分发和查阅功能集成说明', version: 'v1.3.0' },
        { name: '医保影像云基础设施规范', desc: '网络、服务器和存储基础设施配置要求', version: 'v1.0.0' }
      ]
    }
  },
  async created() { await Promise.all([this.loadInstitutions(), this.loadStats()]) },

  methods: {
    async loadStats() {
      try {
        const res = await publicApi.getHomeStats()
        const data = res.data || []
        if (data.length > 0) {
          this.stats = data.map(s => ({ value: s.statValue + (s.statUnit || ''), label: s.statLabel }))
        }
      } catch (e) { /* 使用默认数据 */ }
    },
    async loadInstitutions(page) {
      if (page) this.currentPage = page
      this.loading = true
      try {
        const res = await institutionApi.list({ page: this.currentPage - 1, size: this.pageSize, keyword: this.keyword || undefined })
        this.institutions = res.data?.content || []
        this.total = res.data?.totalElements || 0
      } catch (e) {
        this.institutions = [
          { id: 1, name: '铁岭市中心医院', level: '三级甲等', type: '综合医院', district: '银州区', address: '铁岭市银州区广裕街1号', phone: '024-72222222', joinDate: '2025-09-01', recognitionItems: 'CT,MRI,DR,US' },
          { id: 2, name: '铁岭市第二人民医院', level: '三级乙等', type: '综合医院', district: '银州区', address: '铁岭市银州区凡河新区', phone: '024-72333333', joinDate: '2025-09-15', recognitionItems: 'CT,DR,US' },
          { id: 3, name: '铁岭县中心医院', level: '二级甲等', type: '综合医院', district: '铁岭县', address: '铁岭县银州镇', phone: '024-73111111', joinDate: '2025-10-01', recognitionItems: 'CT,DR' },
          { id: 4, name: '开原市中心医院', level: '二级甲等', type: '综合医院', district: '开原市', address: '开原市中央路1号', phone: '024-76111111', joinDate: '2025-10-01', recognitionItems: 'CT,MRI,DR' },
          { id: 5, name: '昌图县中心医院', level: '二级甲等', type: '综合医院', district: '昌图县', address: '昌图县昌图镇', phone: '024-77111111', joinDate: '2025-10-15', recognitionItems: 'CT,DR' },
          { id: 6, name: '西丰县人民医院', level: '二级乙等', type: '综合医院', district: '西丰县', address: '西丰县西丰镇', phone: '024-78111111', joinDate: '2025-11-01', recognitionItems: 'DR,US' },
          { id: 7, name: '调兵山市人民医院', level: '二级甲等', type: '综合医院', district: '调兵山市', address: '调兵山市铁法路', phone: '024-74111111', joinDate: '2025-11-01', recognitionItems: 'CT,DR,US' }
        ]
        this.total = this.institutions.length
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.currentPage = 1
      clearTimeout(this._t)
      this._t = setTimeout(() => this.loadInstitutions(), 400)
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-row {
  display: flex;
  gap: 0;
  background: white;
  border: 1px solid $border-color;
  border-radius: $radius-lg;
  overflow: hidden;
  margin-bottom: 32px;

  .stat-item {
    flex: 1;
    padding: 20px;
    text-align: center;
    border-right: 1px solid $border-color;
    &:last-child { border-right: none; }

    .stat-num {
      display: block;
      font-size: 28px;
      font-weight: 700;
      color: $primary;
      margin-bottom: 4px;
    }
    .stat-label {
      font-size: 13px;
      color: $text-secondary;
    }
  }
}

.section-block {
  margin-bottom: 40px;

  .section-block-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 20px;
    h3 { font-size: 20px; font-weight: 700; color: $text-primary; }
  }

  .block-title {
    font-size: 20px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 20px;
  }
}

.institution-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 16px;
}

.institution-card {
  padding: 20px;

  .inst-top {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .inst-avatar {
      width: 44px;
      height: 44px;
      background: $primary-bg;
      border-radius: $radius-md;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $primary;
    }

    .inst-badge {
      font-size: 12px;
      padding: 2px 10px;
      border-radius: 20px;
      &.running { background: #ECFDF5; color: $success; }
    }
  }

  h4 {
    font-size: 15px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 6px;
  }

  .inst-meta {
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 8px;
    .dot { margin: 0 4px; opacity: 0.4; }
  }

  .inst-address, .inst-phone {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    color: $text-secondary;
    margin-bottom: 6px;
  }

  .inst-items {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    margin: 10px 0;
    .item-tag {
      background: $primary-bg;
      color: $primary;
      padding: 1px 8px;
      border-radius: 4px;
      font-size: 12px;
    }
  }

  .inst-join-date {
    font-size: 12px;
    color: $text-muted;
    margin-top: 8px;
  }
}

.process-flow {
  display: flex;
  gap: 16px;
  counter-reset: step;

  .flow-step {
    flex: 1;
    background: white;
    border: 1px solid $border-color;
    border-radius: $radius-lg;
    padding: 20px;
    position: relative;

    .flow-step-num {
      width: 28px;
      height: 28px;
      background: $primary;
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 13px;
      font-weight: 700;
      margin-bottom: 12px;
    }

    h4 {
      font-size: 14px;
      font-weight: 700;
      color: $text-primary;
      margin-bottom: 8px;
    }

    p {
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.6;
    }
  }
}

.doc-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;

  .doc-item {
    display: flex;
    align-items: center;
    padding: 16px 20px;
    gap: 16px;

    .doc-icon {
      width: 48px;
      height: 48px;
      background: $primary-bg;
      border-radius: $radius-md;
      display: flex;
      align-items: center;
      justify-content: center;
      color: $primary;
      flex-shrink: 0;
    }

    .doc-info {
      flex: 1;
      h4 { font-size: 14px; font-weight: 600; color: $text-primary; margin-bottom: 4px; }
      p { font-size: 13px; color: $text-secondary; }
      .doc-version { font-size: 12px; color: $text-muted; }
    }
  }
}

@media (max-width: $breakpoint-md) {
  .institution-grid { grid-template-columns: 1fr; }
  .process-flow { flex-direction: column; }
}
</style>
