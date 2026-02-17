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
        <template #title>{{ menuTitle }}</template>
        <el-menu-item index="__lang_zh__" @click="setLang('zh')">中文</el-menu-item>
        <el-menu-item index="__lang_en__" @click="setLang('en')">en</el-menu-item>
      </el-sub-menu>

      <el-menu-item v-if="isLoggedIn" index="__user__" class="user-menu-item">
        <el-dropdown trigger="click" @command="handleUserCommand">
          <span class="user-trigger">
            <el-avatar :size="28" :src="userAvatar" :icon="UserFilled" />
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

  <!-- 手机端面板：覆盖 header 以下区域 -->
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
    <el-menu class="mobile-menu">
      <el-menu-item index="0" class="mobile-menu-item">
        <el-select v-model="langValue"  @change="setLang">
          <el-option label="中文" value="zh" />
          <el-option label="en" value="en" />
        </el-select>
      </el-menu-item>
      <el-menu-item index="1" class="mobile-menu-item">
        <el-text>{{ $t('message.theme') }}</el-text>
        <el-switch
            :model-value="isDark"
            @change="onChange"
            @click.capture="onClick"
            :active-action-icon="Moon"
            :inactive-action-icon="Sunny"
            class="mobile-el-switch"
        />
      </el-menu-item>
    </el-menu>




  </el-drawer>
</template>

<script setup>
// 组件名（可选，但建议加，避免一些规则/调试更清楚）
defineOptions({ name: 'HeaderBar' })
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useI18n } from 'vue-i18n'
import { Menu, Close, Sunny, Moon, ArrowDown, UserFilled } from '@element-plus/icons-vue'
import { useDark } from '@/hooks/useDark'
import { useResponsive } from '@/composables/useResponsive'

const { isMobile } = useResponsive()

const router = useRouter()

/** 轻量读取登录信息：兼容你可能使用的 localStorage 字段 */
const userName = ref('')
const userAvatar = ref('')
const isLoggedIn = computed(() => Boolean(userName.value))

function readAuthFromStorage() {
  // 1) user（JSON）
  try {
    const rawUser = localStorage.getItem('user')
    if (rawUser) {
      const u = JSON.parse(rawUser)
      userName.value = u?.username || u?.name || u?.nickName || u?.nickname || ''
      userAvatar.value = u?.avatar || u?.avatarUrl || u?.headImg || ''
      if (userName.value) return
    }
  } catch (exception) {
    console.log(exception)
  }

  // 2) 单独字段
  userName.value =
      localStorage.getItem('username') ||
      localStorage.getItem('userName') ||
      localStorage.getItem('name') ||
      ''

  userAvatar.value =
      localStorage.getItem('avatar') ||
      localStorage.getItem('avatarUrl') ||
      ''

  // 3) 仅 token：但没有用户名就不展示（避免显示空白）
}

function clearAuth() {
  ['token', 'access_token', 'Authorization', 'user', 'username', 'userName', 'name', 'avatar', 'avatarUrl'].forEach((k) =>
      localStorage.removeItem(k)
  )
}

function logout() {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning'
  })
      .then(() => {
        clearAuth()
        readAuthFromStorage()
        // 手机端也顺便收起面板
        panelOpen.value = false
        ElMessage.success('已退出')
        router.push('/login')
      })
      .catch(() => {})
}

function handleUserCommand(cmd) {
  if (cmd === 'logout') logout()
}

function onStorageChange(e) {
  if (!e?.key) return
  // 登录信息变化时刷新 header 显示
  if (['token', 'access_token', 'Authorization', 'user', 'username', 'userName', 'name', 'avatar', 'avatarUrl'].includes(e.key)) {
    readAuthFromStorage()
  }
}

onMounted(() => {
  readAuthFromStorage()
  window.addEventListener('storage', onStorageChange)
})

onBeforeUnmount(() => {
  window.removeEventListener('storage', onStorageChange)
})

// 手机端面板开关（控制 el-drawer）
const panelOpen = ref(false)
function togglePanel() {
  panelOpen.value = !panelOpen.value
}

// i18n
const { locale } = useI18n()
const menuTitle = computed(() => (locale.value === 'zh' ? '中文' : 'en'))
const langValue = computed({
  get: () => locale.value,
  set: (v) => (locale.value = v)
})
function setLang(lang) {
  locale.value = lang
  // 手机端选完语言就收起
  if (isMobile.value) panelOpen.value = false
}

// 暗黑模式（沿用你现有 useDark）
const { isDark, mode } = useDark()

// 点击坐标（用于你那套 ViewTransition 动画）
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

// 切回桌面端时自动关闭手机面板
watch(isMobile, (m) => {
  if (!m) panelOpen.value = false
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
.mobile-menu {
  border-right: 0;
}
.mobile-el-switch {
  margin-left: auto;
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
.mobile-user {
  gap: 10px;
}
.mobile-user-name {
  margin-left: 6px;
  flex: 1;
}
.mobile-logout-btn {
  margin-left: auto;
}

.mobile-menu-item {
  background-color: var(--el-fill-color-light);
  margin-bottom: 5px;
}
</style>

