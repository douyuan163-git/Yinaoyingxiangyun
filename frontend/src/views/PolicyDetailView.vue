<template>
  <div>
    <div class="page-header">
      <div class="page-container">
        <div class="breadcrumb">
          <router-link to="/">首页</router-link>
          <span>/</span>
          <router-link to="/policy">政策法规</router-link>
          <span>/</span>
          <span>政策详情</span>
        </div>
        <h1 v-if="policy">{{ policy.title }}</h1>
      </div>
    </div>

    <div class="page-container" style="padding-top: 32px; padding-bottom: 48px;">
      <div v-if="loading" class="loading-wrapper">
        <el-icon class="is-loading" :size="32"><loading /></el-icon>
      </div>
      <div v-else-if="policy" class="detail-layout">
        <div class="detail-main card" style="padding: 32px;">
          <div class="policy-meta-bar">
            <span class="level-tag" :class="'tag-' + policy.level">{{ levelLabel(policy.level) }}</span>
            <span v-if="policy.issuer" class="meta-item">
              <el-icon><office-building /></el-icon>{{ policy.issuer }}
            </span>
            <span v-if="policy.docNo" class="meta-item">
              <el-icon><document /></el-icon>{{ policy.docNo }}
            </span>
            <span class="meta-item">
              <el-icon><calendar /></el-icon>{{ policy.publishDate }}
            </span>
          </div>
          <div class="policy-content" v-html="policy.content || policy.summary"></div>
        </div>
        <div class="detail-sidebar">
          <div class="card" style="padding: 20px;">
            <h4 style="margin-bottom: 12px;">相关政策</h4>
            <div v-for="r in relatedPolicies" :key="r.id" class="related-item">
              <router-link :to="'/policy/' + r.id">{{ r.title }}</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { policyApi } from '@/api'
import { Loading, OfficeBuilding, Document, Calendar } from '@element-plus/icons-vue'

export default {
  name: 'PolicyDetailView',
  components: { Loading, OfficeBuilding, Document, Calendar },
  data() {
    return {
      loading: false,
      policy: null,
      relatedPolicies: []
    }
  },
  created() { this.loadPolicy() },
  watch: { '$route.params.id': 'loadPolicy' },
  methods: {
    async loadPolicy() {
      this.loading = true
      try {
        const res = await policyApi.detail(this.$route.params.id)
        this.policy = res.data
      } catch (e) {
        this.policy = {
          id: this.$route.params.id,
          title: '铁岭市医疗保障局关于铁岭市医保影像云平台建设实施方案',
          issuer: '铁岭市医疗保障局',
          docNo: '铁医保〔2025〕5号',
          level: 'municipal',
          publishDate: '2025-05-01',
          content: '<p>各县（市）区医疗保障局，市直各医疗机构：</p><p>为贯彻落实国家医疗保障局关于推进医保影像云建设的指导意见，加快推进铁岭市医保影像云平台建设，现将有关事项通知如下：</p><h3>一、总体目标</h3><p>2025年底前，完成全市二级及以上医疗机构医保影像云接入工作，实现影像数据的统一存储、调阅和互认，为参保人员提供便捷的云胶片服务。</p><h3>二、主要任务</h3><p>（一）建设统一服务门户。建设铁岭市医保影像云统一服务门户，为医保监管部门、医疗机构、参保人员和系统开发者提供一站式服务入口。</p><p>（二）推进机构接入。按照国家技术规范，组织全市医疗机构完成系统改造和接口对接工作。</p><p>（三）开展云胶片服务。全面推行云胶片替代传统胶片，降低参保人员就医成本。</p><p>（四）推进检查互认。依托影像云平台，推进全市医疗机构检查结果互认工作。</p>'
        }
        this.relatedPolicies = [
          { id: 1, title: '国家医疗保障局关于推进医保影像云建设的指导意见' },
          { id: 3, title: '辽宁省医疗保障局关于开展医保影像云试点工作的通知' }
        ]
      } finally {
        this.loading = false
      }
    },
    levelLabel(level) {
      const map = { national: '国家', provincial: '省级', municipal: '市级' }
      return map[level] || level
    }
  }
}
</script>

<style lang="scss" scoped>
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  opacity: 0.8;
  margin-bottom: 12px;
  a { color: rgba(255,255,255,0.8); &:hover { color: white; } }
}
.detail-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 24px;
}
.policy-meta-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid $border-color;

  .level-tag {
    padding: 3px 12px;
    border-radius: 4px;
    font-size: 12px;
    font-weight: 600;
    &.tag-national { background: #EBF3FF; color: $primary; }
    &.tag-provincial { background: #ECFDF5; color: $success; }
    &.tag-municipal { background: #FFF7ED; color: $warning; }
  }

  .meta-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 13px;
    color: $text-secondary;
  }
}
.policy-content {
  font-size: 15px;
  line-height: 1.9;
  color: $text-primary;
  :deep(h3) { font-size: 16px; font-weight: 700; margin: 20px 0 10px; }
  :deep(p) { margin-bottom: 12px; }
}
.related-item {
  padding: 10px 0;
  border-bottom: 1px solid $border-color;
  &:last-child { border-bottom: none; }
  a { font-size: 13px; color: $text-primary; line-height: 1.5; display: block; &:hover { color: $primary; } }
}
@media (max-width: $breakpoint-md) {
  .detail-layout { grid-template-columns: 1fr; }
}
</style>
