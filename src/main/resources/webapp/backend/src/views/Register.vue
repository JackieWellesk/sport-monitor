<template>
  <el-container class="auth-page">
    <el-main>
      <el-row justify="center" align="middle" style="min-height: 100vh">
        <el-col :xs="22" :sm="16" :md="10" :lg="8" :xl="6">
          <el-card shadow="always">
            <template #header>
              <el-space alignment="center">
                <el-avatar :icon="EditPen" />
                <el-text size="large" tag="b">用户注册</el-text>
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
                />
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input
                  v-model="form.confirmPassword"
                  placeholder="请再次输入密码"
                  show-password
                  type="password"
                  clearable
                  @keyup.enter="onSubmit"
                />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" :loading="loading" style="width: 100%" @click="onSubmit">
                  注册
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
              <el-text type="info" size="small">已有账号？</el-text>
              <el-link type="primary" :underline="false" @click="goLogin">去登录</el-link>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>

<script setup>
defineOptions({ name: 'RegisterView' })
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { EditPen } from '@element-plus/icons-vue'
import request from '@/utils/request'
import {useAuthStore} from "@/stores/auth";

const router = useRouter()
const route = useRoute()

const formRef = ref()
const loading = ref(false)
const errorMsg = ref('')

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (!value) return callback(new Error('请再次输入密码'))
  if (value !== form.password) return callback(new Error('两次输入的密码不一致'))
  return callback()
}

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [{ validator: validateConfirm, trigger: 'blur' }]
}
const auth = useAuthStore()

// 示例：请替换成你的真实后端接口
async function registerApi(data) {
  // 这里演示：非空且两次密码一致即成功
  return request.post('/api/auth/register', data)
}

function goLogin() {
  router.push({
    path: '/login',
    query: { username: form.username || '' }
  })
}

onMounted(async() => {
  // 从登录页过来可带 username 预填
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
      await registerApi({
        username: form.username,
        password: form.password,
        roleCode: 'TEACHER',
      })

      ElMessage.success('注册成功，请登录')

      // 注册成功 → 回登录页，并把 username 带回去
      router.replace({
        path: '/login',
        query: { username: form.username }
      })
    } catch (e) {
      errorMsg.value = '注册失败，请稍后重试'
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
