<template>
  <el-result
      icon="warning"
      title="403"
      sub-title="您没有权限访问该页面"
  >
    <template #extra>
      <el-button type="primary" @click="goLogin">
        重新登录
      </el-button>
    </template>
  </el-result>
</template>
<script setup>
defineOptions({ name: 'NotAuthorized' })
import { useRouter } from 'vue-router'
import request from '@/utils/request' // ✅ 可选：如果你有后端 logout 接口
import { useAuthStore } from '@/stores/auth'
const  authStore = useAuthStore()

const router = useRouter()
// 跳首页
async function goLogin() {
  // 1️⃣ 调用后端注销接口（销毁 Session）
  try {
    await request.post('/api/auth/logout')
  } catch (e) {
    // 即便后端报错，也继续清理前端
    console.warn('logout api error:', e)
  }

  // 2️⃣ 清空 Pinia 状态
  authStore.clear()
  await router.replace('/login')
}

</script>