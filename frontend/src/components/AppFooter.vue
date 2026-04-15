<template>
  <footer class="app-footer">
    <div class="footer-main">
      <div class="page-container footer-grid">
        <!-- 平台信息 -->
        <div class="footer-col footer-brand">
          <img src="@/assets/images/logo.png" alt="铁岭医保影像云" class="footer-logo" />
          <p class="footer-desc">
            {{ siteConfig['site.subtitle'] || '铁岭市医保影像云统一服务门户，为医保监管部门、医疗机构、参保人员及系统开发者提供一站式数字化服务。' }}
          </p>
        </div>

        <!-- 快速链接 -->
        <div class="footer-col">
          <h4>快速链接</h4>
          <ul>
            <li><router-link to="/portal">统一服务门户</router-link></li>
            <li><router-link to="/policy">政策法规</router-link></li>
            <li><router-link to="/news">新闻动态</router-link></li>
            <li><router-link to="/mutual-recognition">检查互认</router-link></li>
          </ul>
        </div>

        <!-- 服务支持 -->
        <div class="footer-col">
          <h4>服务支持</h4>
          <ul>
            <li><router-link to="/institutions">机构接入</router-link></li>
            <li><router-link to="/developer">开发者中心</router-link></li>
            <li><a href="#">用户手册</a></li>
            <li><a href="#">常见问题</a></li>
          </ul>
        </div>

        <!-- 联系方式 -->
        <div class="footer-col">
          <h4>联系我们</h4>
          <ul class="contact-list">
            <li>
              <span class="label">主办单位：</span>
              <span>{{ siteConfig['site.host'] || '铁岭市医疗保障局' }}</span>
            </li>
            <li>
              <span class="label">技术支持：</span>
              <span>{{ siteConfig['site.tech_support'] || '融御科技' }}</span>
            </li>
            <li>
              <span class="label">联系电话：</span>
              <span>{{ siteConfig['site.phone'] || '024-XXXX-XXXX' }}</span>
            </li>
            <li>
              <span class="label">服务时间：</span>
              <span>工作日 8:00-17:00</span>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <div class="footer-bottom">
      <div class="page-container footer-bottom-inner">
        <span>{{ siteConfig['site.copyright'] || '© 2025 铁岭市医疗保障局 版权所有' }}</span>
        <span class="divider">|</span>
        <a href="#">隐私政策</a>
        <span class="divider">|</span>
        <a href="#">使用条款</a>
        <span class="divider">|</span>
        <span>{{ siteConfig['site.icp'] || '辽ICP备XXXXXXXX号' }}</span>
      </div>
    </div>
  </footer>
</template>

<script>
import { publicApi } from '@/api'

export default {
  name: 'AppFooter',
  data() {
    return {
      siteConfig: {}
    }
  },
  async created() {
    try {
      const res = await publicApi.getSiteConfig('basic')
      this.siteConfig = res.data || {}
    } catch (e) {
      this.siteConfig = {}
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/variables.scss';

.app-footer {
  background: #1E293B;
  color: rgba(255, 255, 255, 0.75);
  margin-top: auto;

  .footer-main {
    padding: 48px 0 32px;

    .footer-grid {
      display: grid;
      grid-template-columns: 2fr 1fr 1fr 1.5fr;
      gap: 40px;
      @media (max-width: 1024px) { grid-template-columns: 1fr 1fr; }
      @media (max-width: 640px) { grid-template-columns: 1fr; }
    }

    .footer-brand {
      .footer-logo {
        height: 36px;
        filter: brightness(0) invert(1);
        margin-bottom: 16px;
      }
      .footer-desc {
        font-size: 13px;
        line-height: 1.7;
        color: rgba(255, 255, 255, 0.55);
      }
    }

    .footer-col {
      h4 {
        font-size: 14px;
        font-weight: 600;
        color: white;
        margin-bottom: 16px;
        padding-bottom: 8px;
        border-bottom: 1px solid rgba(255,255,255,0.1);
      }
      ul {
        list-style: none;
        padding: 0;
        margin: 0;
        li {
          margin-bottom: 10px;
          a, .router-link-active {
            font-size: 13px;
            color: rgba(255,255,255,0.6);
            text-decoration: none;
            transition: color 0.2s;
            &:hover { color: white; }
          }
        }
      }
      .contact-list li {
        display: flex;
        gap: 6px;
        font-size: 13px;
        color: rgba(255,255,255,0.6);
        .label { color: rgba(255,255,255,0.4); flex-shrink: 0; }
      }
    }
  }

  .footer-bottom {
    border-top: 1px solid rgba(255,255,255,0.08);
    padding: 16px 0;
    .footer-bottom-inner {
      display: flex;
      align-items: center;
      gap: 12px;
      font-size: 12px;
      color: rgba(255,255,255,0.4);
      flex-wrap: wrap;
      a { color: rgba(255,255,255,0.4); text-decoration: none; &:hover { color: rgba(255,255,255,0.7); } }
      .divider { color: rgba(255,255,255,0.2); }
    }
  }
}
</style>
