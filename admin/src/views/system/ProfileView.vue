<template>
  <div class="page-container">
    <div class="page-header">
      <h2>个人信息</h2>
    </div>
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="card" style="text-align: center; padding: 40px 20px;">
          <el-avatar :size="80" style="background: #1B6FD8; font-size: 32px; margin-bottom: 16px;">
            {{ (currentUser?.realName || currentUser?.username || 'A').charAt(0) }}
          </el-avatar>
          <div style="font-size: 18px; font-weight: 600; color: #1F2937; margin-bottom: 8px;">
            {{ currentUser?.realName || currentUser?.username }}
          </div>
          <el-tag :type="roleTagType(currentUser?.role)">{{ roleLabel(currentUser?.role) }}</el-tag>
        </div>
      </el-col>
      <el-col :span="16">
        <div class="card">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本信息" name="info">
              <el-form :model="infoForm" label-width="100px" style="margin-top: 16px;">
                <el-form-item label="用户名">
                  <el-input v-model="infoForm.username" disabled />
                </el-form-item>
                <el-form-item label="真实姓名">
                  <el-input v-model="infoForm.realName" />
                </el-form-item>
                <el-form-item label="手机号">
                  <el-input v-model="infoForm.phone" />
                </el-form-item>
                <el-form-item label="邮箱">
                  <el-input v-model="infoForm.email" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary">保存修改</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form :model="pwdForm" label-width="120px" style="margin-top: 16px;">
                <el-form-item label="当前密码">
                  <el-input v-model="pwdForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="新密码">
                  <el-input v-model="pwdForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item label="确认新密码">
                  <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePwd">修改密码</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, computed, reactive } from 'vue'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'

export default {
  name: 'ProfileView',
  setup() {
    const store = useStore()
    const currentUser = computed(() => store.getters.currentUser)
    const activeTab = ref('info')

    const infoForm = reactive({
      username: currentUser.value?.username || '',
      realName: currentUser.value?.realName || '',
      phone: '',
      email: ''
    })

    const pwdForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    const handleChangePwd = () => {
      if (!pwdForm.oldPassword || !pwdForm.newPassword) {
        ElMessage.warning('请填写完整密码信息')
        return
      }
      if (pwdForm.newPassword !== pwdForm.confirmPassword) {
        ElMessage.error('两次输入的新密码不一致')
        return
      }
      if (pwdForm.newPassword.length < 8) {
        ElMessage.warning('新密码长度不能少于8位')
        return
      }
      ElMessage.success('密码修改成功（演示模式）')
    }

    const roleLabel = (r) => ({ admin: '系统管理员', supervisor: '监管部门', hospital: '医疗机构', patient: '参保人员', developer: '开发者' }[r] || r)
    const roleTagType = (r) => ({ admin: 'danger', supervisor: 'primary', hospital: 'success', patient: '', developer: 'warning' }[r] || 'info')

    return { currentUser, activeTab, infoForm, pwdForm, handleChangePwd, roleLabel, roleTagType }
  }
}
</script>
