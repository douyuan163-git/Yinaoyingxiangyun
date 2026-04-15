<template>
  <div>
    <div class="page-header">
      <div class="page-container">
        <h1>检查互认</h1>
        <p>铁岭市医疗机构影像检查结果互认查询与管理</p>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card" v-for="s in statCards" :key="s.label" :style="{ '--card-color': s.color }">
          <div class="stat-icon">
            <el-icon :size="28"><component :is="s.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-num">{{ s.value }}</div>
            <div class="stat-label">{{ s.label }}</div>
          </div>
        </div>
      </div>

      <!-- 查询区域 -->
      <div class="query-section card">
        <h3>个人互认记录查询</h3>
        <p>请输入您的身份证号查询本人的检查互认记录</p>
        <div class="query-form">
          <el-input
            v-model="queryIdCard"
            placeholder="请输入18位身份证号"
            size="large"
            clearable
            maxlength="18"
            style="flex: 1;"
          />
          <el-button type="primary" size="large" :loading="queryLoading" @click="handleQuery">
            <el-icon><search /></el-icon>
            查询记录
          </el-button>
        </div>
        <div v-if="queryResult !== null">
          <div v-if="queryResult.length === 0" class="empty-wrapper">
            <p>未查询到相关互认记录</p>
          </div>
          <div v-else class="recognition-list">
            <div v-for="r in queryResult" :key="r.id" class="recognition-item">
              <div class="rec-header">
                <span class="rec-type">{{ r.examType }}</span>
                <span class="rec-status" :class="r.status">
                  {{ r.status === 'recognized' ? '已互认' : '待互认' }}
                </span>
              </div>
              <div class="rec-body">
                <div class="rec-row">
                  <span class="rec-label">检查机构：</span>
                  <span>{{ r.sourceInstitutionName }}</span>
                </div>
                <div class="rec-row">
                  <span class="rec-label">互认机构：</span>
                  <span>{{ r.targetInstitutionName }}</span>
                </div>
                <div class="rec-row">
                  <span class="rec-label">检查日期：</span>
                  <span>{{ r.examDate }}</span>
                </div>
                <div class="rec-row">
                  <span class="rec-label">互认日期：</span>
                  <span>{{ r.recognitionDate }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 互认机构列表 -->
      <div class="institutions-section">
        <h3 class="section-title">支持互认的医疗机构</h3>
        <div class="institution-table-wrapper card">
          <table class="institution-table">
            <thead>
              <tr>
                <th>机构名称</th>
                <th>等级</th>
                <th>区域</th>
                <th>互认项目</th>
                <th>接入状态</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="inst in recognitionInstitutions" :key="inst.id">
                <td class="inst-name">{{ inst.name }}</td>
                <td>{{ inst.level }}</td>
                <td>{{ inst.district }}</td>
                <td>
                  <span v-for="item in inst.recognitionItems?.split(',')" :key="item" class="item-tag">
                    {{ item }}
                  </span>
                </td>
                <td>
                  <span class="status-badge running">已接入</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 互认流程说明 -->
      <div class="process-section">
        <h3 class="section-title">检查互认流程</h3>
        <div class="process-steps">
          <div v-for="(step, idx) in processSteps" :key="idx" class="process-step">
            <div class="step-num">{{ idx + 1 }}</div>
            <div class="step-icon">
              <el-icon :size="24"><component :is="step.icon" /></el-icon>
            </div>
            <h4>{{ step.title }}</h4>
            <p>{{ step.desc }}</p>
            <div v-if="idx < processSteps.length - 1" class="step-arrow">→</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { recognitionApi, institutionApi } from '@/api'
import { Search, Share, Document, Check, User, OfficeBuilding } from '@element-plus/icons-vue'

export default {
  name: 'MutualRecognitionView',
  components: { Search, Share, Document, Check, User, OfficeBuilding },
  data() {
    return {
      queryIdCard: '',
      queryLoading: false,
      queryResult: null,
      recognitionInstitutions: [],
      statCards: [
        { label: '支持互认机构', value: '7家', icon: 'OfficeBuilding', color: '#1B6FD8' },
        { label: '累计互认次数', value: '3,240次', icon: 'Share', color: '#0891B2' },
        { label: '互认项目类型', value: '4类', icon: 'Document', color: '#EA580C' },
        { label: '节约检查费用', value: '约68万元', icon: 'Check', color: '#7C3AED' }
      ],
      processSteps: [
        { title: '医生开具申请', icon: 'Document', desc: '接诊医生在HIS系统中查询患者既往影像记录' },
        { title: '平台影像调阅', icon: 'Search', desc: '通过医保影像云平台调阅原始影像数据' },
        { title: '医生审核确认', icon: 'User', desc: '接诊医生确认影像质量符合诊断要求' },
        { title: '互认结果记录', icon: 'Check', desc: '系统自动记录互认操作，生成互认凭证' }
      ]
    }
  },
  created() {
    this.loadRecognitionInstitutions()
  },
  methods: {
    async loadRecognitionInstitutions() {
      try {
        const res = await institutionApi.recognitionList()
        this.recognitionInstitutions = res.data || []
      } catch (e) {
        this.recognitionInstitutions = [
          { id: 1, name: '铁岭市中心医院', level: '三级甲等', district: '银州区', recognitionItems: 'CT,MRI,DR,US' },
          { id: 2, name: '铁岭市第二人民医院', level: '三级乙等', district: '银州区', recognitionItems: 'CT,DR,US' },
          { id: 3, name: '铁岭县中心医院', level: '二级甲等', district: '铁岭县', recognitionItems: 'CT,DR' },
          { id: 4, name: '开原市中心医院', level: '二级甲等', district: '开原市', recognitionItems: 'CT,MRI,DR' },
          { id: 5, name: '昌图县中心医院', level: '二级甲等', district: '昌图县', recognitionItems: 'CT,DR' },
          { id: 6, name: '西丰县人民医院', level: '二级乙等', district: '西丰县', recognitionItems: 'DR,US' },
          { id: 7, name: '调兵山市人民医院', level: '二级甲等', district: '调兵山市', recognitionItems: 'CT,DR,US' }
        ]
      }
    },
    async handleQuery() {
      if (!this.queryIdCard || this.queryIdCard.length < 15) {
        this.$message.warning('请输入有效的身份证号')
        return
      }
      this.queryLoading = true
      try {
        const res = await recognitionApi.query({ idCard: this.queryIdCard, page: 0, size: 20 })
        this.queryResult = res.data?.content || []
      } catch (e) {
        // 演示数据
        if (this.queryIdCard === '110211198001010001') {
          this.queryResult = [{
            id: 1,
            examType: 'CT',
            sourceInstitutionName: '铁岭市中心医院',
            targetInstitutionName: '铁岭市第二人民医院',
            examDate: '2025-09-01',
            recognitionDate: '2025-09-06',
            status: 'recognized'
          }]
        } else {
          this.queryResult = []
        }
      } finally {
        this.queryLoading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 32px;

  .stat-card {
    background: white;
    border-radius: $radius-lg;
    border: 1px solid $border-color;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: $radius-md;
      background: color-mix(in srgb, var(--card-color) 10%, white);
      color: var(--card-color);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .stat-num {
      font-size: 22px;
      font-weight: 700;
      color: $text-primary;
    }

    .stat-label {
      font-size: 13px;
      color: $text-secondary;
    }
  }
}

.query-section {
  padding: 28px;
  margin-bottom: 32px;

  h3 {
    font-size: 18px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 6px;
  }

  p {
    font-size: 14px;
    color: $text-secondary;
    margin-bottom: 20px;
  }

  .query-form {
    display: flex;
    gap: 12px;
    max-width: 600px;
  }
}

.recognition-list {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;

  .recognition-item {
    border: 1px solid $border-color;
    border-radius: $radius-md;
    overflow: hidden;

    .rec-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 12px 16px;
      background: $bg-light;

      .rec-type {
        font-weight: 600;
        color: $primary;
        background: $primary-bg;
        padding: 2px 10px;
        border-radius: 4px;
        font-size: 13px;
      }

      .rec-status {
        font-size: 12px;
        padding: 2px 10px;
        border-radius: 20px;
        &.recognized { background: #ECFDF5; color: $success; }
        &.pending { background: #FFF7ED; color: $warning; }
      }
    }

    .rec-body {
      padding: 16px;
      .rec-row {
        display: flex;
        gap: 8px;
        font-size: 13px;
        margin-bottom: 8px;
        &:last-child { margin-bottom: 0; }
        .rec-label { color: $text-muted; white-space: nowrap; }
      }
    }
  }
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 20px;
}

.institution-table-wrapper {
  overflow-x: auto;
  padding: 0;
  margin-bottom: 32px;

  .institution-table {
    width: 100%;
    border-collapse: collapse;

    th, td {
      padding: 12px 16px;
      text-align: left;
      border-bottom: 1px solid $border-color;
      font-size: 14px;
    }

    th {
      background: $bg-light;
      font-weight: 600;
      color: $text-secondary;
      font-size: 13px;
    }

    td.inst-name {
      font-weight: 600;
      color: $text-primary;
    }

    .item-tag {
      display: inline-block;
      background: $primary-bg;
      color: $primary;
      padding: 1px 8px;
      border-radius: 4px;
      font-size: 12px;
      margin-right: 4px;
    }

    .status-badge {
      font-size: 12px;
      padding: 2px 10px;
      border-radius: 20px;
      &.running { background: #ECFDF5; color: $success; }
    }
  }
}

.process-steps {
  display: flex;
  align-items: flex-start;
  gap: 0;
  position: relative;

  .process-step {
    flex: 1;
    text-align: center;
    padding: 24px 16px;
    background: white;
    border: 1px solid $border-color;
    border-radius: $radius-lg;
    position: relative;

    .step-num {
      position: absolute;
      top: -12px;
      left: 50%;
      transform: translateX(-50%);
      width: 24px;
      height: 24px;
      background: $primary;
      color: white;
      border-radius: 50%;
      font-size: 12px;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .step-icon {
      color: $primary;
      margin-bottom: 12px;
    }

    h4 {
      font-size: 15px;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: 8px;
    }

    p {
      font-size: 13px;
      color: $text-secondary;
      line-height: 1.6;
    }

    .step-arrow {
      position: absolute;
      right: -20px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 20px;
      color: $text-muted;
      z-index: 1;
    }
  }
}

@media (max-width: $breakpoint-md) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
  .process-steps { flex-direction: column; gap: 16px; }
}
</style>
