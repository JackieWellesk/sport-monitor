<template>
  <el-menu
      mode="horizontal"
      :ellipsis="false"
      class="header-menu"
  >
    <el-menu-item class="logo" index="__logo__">
      {{ $t('message.sportMonitor') }}
    </el-menu-item>

    <!-- 桌面端 -->
    <template v-if="!isMobile">
      <el-menu-item index="__theme__">
        <el-switch
            :model-value="isDark"
            @change="onChange"
            @click.capture="onClick"
            :active-action-icon="Moon"
            :inactive-action-icon="Sunny"
        />
      </el-menu-item>

      <el-sub-menu index="__lang__">
        <template #title>{{ langTitle }}</template>
        <el-menu-item index="__lang_zh__" @click="setLang('zh')">中文</el-menu-item>
        <el-menu-item index="__lang_en__" @click="setLang('en')">en</el-menu-item>
      </el-sub-menu>

      <el-menu-item v-if="isLoggedIn" index="__user__" class="user-menu-item">
        <el-dropdown trigger="click" @command="handleUserCommand">
          <span class="user-trigger">
            <el-avatar :src="userAvatar" :icon="UserFilled" />
            <el-text class="user-name" truncated>{{ userName }}</el-text>
            <el-icon class="user-caret"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-menu-item>
    </template>

    <!-- 手机端 -->
    <template v-else>
      <el-menu-item index="__mobile_panel__" class="mobile-more" @click="togglePanel">
        <el-icon class="more-icon">
          <Close v-if="panelOpen" />
          <Menu v-else />
        </el-icon>
      </el-menu-item>
    </template>
  </el-menu>

  <!-- 手机端面板 -->
  <el-drawer
      v-model="panelOpen"
      :with-header="false"
      direction="rtl"
      size="100%"
      :append-to-body="true"
      :modal="true"
      custom-class="mobile-panel-below-header"
      modal-class="mobile-overlay-below-header"
  >
    <!-- 顶部用户区（登录后） -->
    <el-card v-if="isLoggedIn" shadow="never" class="mobile-user-card">
      <el-row align="middle" :gutter="12">
        <el-col :span="6" style="text-align:center;">
          <el-avatar  :src="userAvatar" :icon="UserFilled" />
        </el-col>
        <el-col :span="18">
          <el-space direction="vertical" alignment="start"  style="width:100%;">
            <el-text size="large" style="font-weight:700;">{{ userName }}</el-text>
            <el-tag type="info" effect="plain" size="small">
              {{ authStore.user?.roleCode || 'USER' }}
            </el-tag>
          </el-space>
        </el-col>
      </el-row>

      <el-divider />

      <el-space wrap>
        <!-- 预留：你有 /users/profile 就打开这个 -->
        <el-button size="small" @click="goProfile">个人信息</el-button>
        <el-button size="small" type="danger" @click="logout">退出登录</el-button>
      </el-space>
    </el-card>

    <!-- 顶部用户区（未登录） -->
    <el-card v-else shadow="never" class="mobile-user-card">
      <el-space direction="vertical" fill style="width:100%;">
        <el-text size="large" style="font-weight:700;">未登录</el-text>
        <el-text type="info" size="small">登录后可查看个人信息与操作入口</el-text>
        <el-space wrap>
          <el-button type="primary" @click="goLogin">去登录</el-button>
          <el-button @click="goRegister">去注册</el-button>
        </el-space>
      </el-space>
    </el-card>

    <!-- 设置区 -->
    <el-card shadow="never" class="mobile-setting-card">
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item :label="$t('message.lang')">
          <el-select v-model="currentLang" @change="setLang" class="full-width">
            <el-option label="中文" value="zh" />
            <el-option label="en" value="en" />
          </el-select>
        </el-descriptions-item>

        <el-descriptions-item :label="$t('message.theme')">
          <el-row align="middle">
            <el-col :span="24" style="text-align:right;">
              <el-switch
                  :model-value="isDark"
                  @change="onChange"
                  @click.capture="onClick"
                  :active-action-icon="Moon"
                  :inactive-action-icon="Sunny"
              />
            </el-col>
          </el-row>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </el-drawer>
</template>

<script setup>
defineOptions({ name: 'HeaderBar' })

import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { Menu, Close, Sunny, Moon, ArrowDown, UserFilled } from '@element-plus/icons-vue'

import { useDark } from '@/hooks/useDark'
import { useResponsive } from '@/composables/useResponsive'
import { useAuthStore } from '@/stores/auth'
import request from '@/utils/request'

const { isMobile } = useResponsive()
const router = useRouter()
const authStore = useAuthStore()

const isLoggedIn = computed(() => !!authStore.user)

const userName = computed(() => authStore.user?.username || authStore.user?.name || '')
const userAvatar = computed(() => (authStore.user ? ('/upload/' + (authStore.user.avatarUrl || '')) : ''))

const panelOpen = ref(false)

// i18n
const { locale } = useI18n()

// 当前选中语言（从 localStorage 读）
const currentLang = ref(localStorage.getItem('locale') || locale.value)

const langTitle = computed(() => (currentLang.value === 'zh' ? '中文' : currentLang.value))

function togglePanel() {
  panelOpen.value = !panelOpen.value
}

function closePanel() {
  panelOpen.value = false
}

function goLogin() {
  closePanel()
  router.push('/login')
}

function goRegister() {
  closePanel()
  router.push('/register')
}

function goProfile() {
  closePanel()
  router.push('/users/profile')
}
// 退出登录
async function logout() {
  try {
    await ElMessageBox.confirm('确认退出登录吗？', '提示', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    })

    try {
      await request.post('/api/auth/logout')
    } catch (e) {
      console.warn('logout api error:', e)
    }

    authStore.clear()
    closePanel()
    ElMessage.success('已退出登录')
    router.replace('/login')
  } catch (e) {
    // cancel
  }
}

function handleUserCommand(cmd) {
  if (cmd === 'logout') logout()
}


// HeaderBar.vue（或你放切换语言的地方）
function setLang(lang) {
  localStorage.setItem('locale', lang)
  location.reload()
}

// 暗黑模式
const { isDark, mode } = useDark()

const pos = ref({ x: 0, y: 0 })
function onClick(e) {
  pos.value = { x: e.clientX, y: e.clientY }
}

function onChange(val) {
  const clickX = pos.value.x ?? window.innerWidth / 2
  const clickY = pos.value.y ?? window.innerHeight / 2
  const maxRadius = Math.hypot(
      Math.max(clickX, window.innerWidth - clickX),
      Math.max(clickY, window.innerHeight - clickY)
  )

  if (!document.startViewTransition || window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
    mode.value = val ? 'dark' : 'light'
    return
  }

  const f = (100 * clickX) / window.innerWidth
  const v = (100 * clickY) / window.innerHeight
  const g = (100 * maxRadius) / (Math.hypot(window.innerWidth, window.innerHeight) / Math.SQRT2)

  document.startViewTransition(() => {
    document.documentElement.classList.toggle('dark', val)
    mode.value = val ? 'dark' : 'light'
  }).ready.then(() => {
    const clip = [`circle(0% at ${f}% ${v}%)`, `circle(${g}% at ${f}% ${v}%)`]
    document.documentElement.animate(
        { clipPath: val ? clip.reverse() : clip },
        {
          duration: 400,
          easing: 'ease-in-out',
          fill: 'both',
          pseudoElement: val ? '::view-transition-old(root)' : '::view-transition-new(root)'
        }
    )
  })
}

watch(isMobile, (m) => {
  if (!m) closePanel()
})
</script>

<style scoped>
.el-menu--horizontal > .el-menu-item.logo {
  margin-right: auto;
  font-size: 20px;
  font-weight: 700;
}

.header-menu {
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.mobile-more {
  padding: 0 10px;
}

.more-icon {
  font-size: 18px;
}

.user-menu-item {
  padding-right: 8px;
}

.user-trigger {
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.user-name {
  max-width: 120px;
}

.user-caret {
  font-size: 12px;
}

/* ===== 手机端：更像“用户面板” ===== */
.full-width {
  width: 100%;
}

.mobile-user-card,
.mobile-setting-card {
  border-radius: 12px;
  margin-bottom: 12px;
}

.mobile-user-card :deep(.el-card__body),
.mobile-setting-card :deep(.el-card__body) {
  padding: 12px;
}

/* Drawer 内部留白 */
:deep(.el-drawer__body) {
  padding: 12px;
}
</style>