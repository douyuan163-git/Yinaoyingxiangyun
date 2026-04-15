const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,

  // 开发服务器配置
  devServer: {
    port: 3001,
    open: false,
    // API 代理配置：将 /api 请求转发到 Spring Boot 后端
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  },

  // 生产环境打包配置
  outputDir: 'dist',
  assetsDir: 'static',
  publicPath: '/',

  // 关闭 SourceMap（生产环境）
  productionSourceMap: false,

  // CSS 配置
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@import "@/assets/styles/variables.scss";`
      }
    }
  }
})
