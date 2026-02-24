<template>
  <el-container class="auth-page">
    <el-main>
      <el-row justify="center" align="middle" style="min-height: 100vh">
        <el-col :xs="22" :sm="16" :md="10" :lg="8" :xl="6">
          <el-card shadow="always">
            <template #header>
              <el-space alignment="center">
                <el-avatar :icon="UserFilled" />
                <el-text size="large" tag="b">校园运动学生端</el-text>
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
              <el-link type="primary" :underline='"never"' @click="goRegister">去注册</el-link>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
defineOptions({ name: 'LoginView' })
import {useAuthStore} from "@/stores/auth";
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled } from '@element-plus/icons-vue'
import request from '@/utils/request'

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
async function loginApi(data) {
  return request.post('/api/auth/login', data)
}


function goRegister() {
  router.push({
    path: '/register',
    query: { username: form.username || '' }
  })
}
const auth = useAuthStore()

onMounted(async() => {
  // 注册页跳回来可带 username 预填
  const u = route.query.username
  if (typeof u === 'string' && u) form.username = u

  try {
    if (!auth.user) {
      await auth.fetchMe()
    }
    if (auth.user.roleCode !== 'anonymousUser') {
      router.replace('/home')
    }
  } catch {
    // 未登录不处理
  }
})

async function onSubmit() {
  errorMsg.value = ''

  await formRef.value?.validate(async (valid) => {
    if (!valid) return

    try {
      loading.value = true
      await loginApi({ ...form })
      // ✅ 登录成功后马上把 user 拉下来，保证 auth.user 有值
      await auth.fetchMe()

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
