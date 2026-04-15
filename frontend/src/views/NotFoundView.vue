<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h2>页面不存在</h2>
      <p>您访问的页面不存在或已被移除，请检查网址是否正确。</p>
      <div class="actions">
        <el-button type="primary" size="large" @click="$router.push('/')">返回首页</el-button>
        <el-button size="large" @click="$router.back()">返回上页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default { name: 'NotFoundView' }
</script>

<style lang="scss" scoped>
.not-found {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;

  .error-code {
    font-size: 96px;
    font-weight: 800;
    color: $primary;
    opacity: 0.15;
    line-height: 1;
    margin-bottom: 16px;
  }

  h2 {
    font-size: 24px;
    font-weight: 700;
    color: $text-primary;
    margin-bottom: 12px;
  }

  p {
    font-size: 15px;
    color: $text-secondary;
    margin-bottom: 32px;
  }

  .actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }
}
</style>
