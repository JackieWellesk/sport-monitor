<template>
  <el-container class="auth-page">
    <el-main>
      <el-row justify="center" align="middle" style="min-height: 100vh">
        <el-col :xs="22" :sm="16" :md="10" :lg="8" :xl="6">
          <el-card shadow="always">
            <template #header>
              <el-space alignment="center" :size="12">
                <el-avatar :size="36" :icon="UserFilled" />
                <el-text size="large" tag="b">校园运动监测系统</el-text>
              </el-space>
            </template>

            <el-form
              ref="formRef"
              :model="form"
              :rules="rules"
              label-position="top"
              status-icon
            >
              <el-form-item label="账号" prop="username">
                <el-input v-model="form.username" placeholder="请输入账号" clearable />
              </el-form-item>

              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="form.password"
                  placeholder="请输入密码"
                  show-password
                  type="password"
                  clearable
                  @keyup.enter="onSubmit"
                />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" :loading="loading" style="width: 100%" @click="onSubmit">
                  登录
                </el-button>
              </el-form-item>

              <el-alert
                v-if="errorMsg"
                :title="errorMsg"
                type="error"
                show-icon
                :closable="false"
              />
            </el-form>

            <el-divider />

            <el-row justify="space-between" align="middle">
              <el-text type="info" size="small">没有账号？</el-text>
              <el-link type="primary" :underline="false" @click="goRegister">去注册</el-link>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
defineOptions({ name: 'LoginView' })
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const formRef = ref()
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 示例：请替换成你的真实后端接口
async function loginApi(payload) {
  // 这里演示：非空即成功
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        token: 'mock-token',
        user: {
          username: payload.username,
          avatarUrl: ''
        }
      })
    }, 350)
  })
}

function safeSetAuth(res) {
  localStorage.setItem('token', res.token)
  localStorage.setItem('user', JSON.stringify(res.user || {}))
}

function goRegister() {
  router.push({
    path: '/register',
    query: { username: form.username || '' }
  })
}

onMounted(() => {
  // 注册页跳回来可带 username 预填
  const u = route.query.username
  if (typeof u === 'string' && u) form.username = u

  // 已登录就不该停在登录页
  const token = localStorage.getItem('token') || localStorage.getItem('access_token')
  if (token) router.replace('/home')
})

async function onSubmit() {
  errorMsg.value = ''

  await formRef.value?.validate(async (valid) => {
    if (!valid) return

    try {
      loading.value = true
      const res = await loginApi({ ...form })
      safeSetAuth(res)

      ElMessage.success('登录成功')

      const redirect = route.query.redirect
      const target = typeof redirect === 'string' && redirect ? redirect : '/home'
      router.replace(target)
    } catch (e) {
      errorMsg.value = '登录失败，请检查账号密码'
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.auth-page {
  background: var(--el-bg-color-page);
}
</style>
