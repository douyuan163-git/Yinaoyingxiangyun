const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: 'dist',
  publicPath: '/',
  devServer: {
    port: 3002,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true
      }
    }
  },
  css: {
    loaderOptions: {
      sass: {
        additionalData: `@use "@/assets/styles/variables.scss" as *;`
      }
    }
  }
})
