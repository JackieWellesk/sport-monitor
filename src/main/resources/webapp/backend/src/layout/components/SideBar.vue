<template>
  <!-- 桌面端：正常侧边栏 -->
  <el-menu
      v-if="!isMobile"
      class="sidebar-menu"
      :default-active="$route.path"
      router
  >
    <el-sub-menu index="home">
      <template #title>
        <span>首页</span>
      </template>

      <el-menu-item index="/button">Button 按钮</el-menu-item>
      <el-menu-item index="/input">Input 输入框</el-menu-item>
      <el-menu-item index="/form">Form 表单</el-menu-item>
    </el-sub-menu>
  </el-menu>

  <!-- 移动端：只显示入口按钮，菜单放到 Drawer 里 -->
  <el-card v-else shadow="never" class="mobile-menu-bar">
    <el-button
        text
        class="mobile-menu-btn"
        :icon="Menu"
        @click="drawerOpen = true"
    >
      Menu
    </el-button>

    <el-drawer
        v-model="drawerOpen"
        direction="ltr"
        size="80%"
        :with-header="false"
        :append-to-body="true"
        body-class="mobile-drawer-body"
    >
      <el-scrollbar height="100%">
        <el-menu
            class="sidebar-menu"
            :default-active="$route.path"
            router
            @select="drawerOpen = false"
            :default-openeds="openMenus"
        >
          <el-sub-menu index="home">
            <template #title>
              <span>首页</span>
            </template>

            <el-menu-item index="/button">Button 按钮</el-menu-item>
            <el-menu-item index="/input">Input 输入框</el-menu-item>
            <el-menu-item index="/form">Form 表单</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-scrollbar>
    </el-drawer>
  </el-card>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Menu } from '@element-plus/icons-vue'
import { useMediaQuery } from '@vueuse/core'

defineOptions({ name: 'SideBar' })

// 手机端判断
const isMobile = useMediaQuery('(max-width: 767px)')

const drawerOpen = ref(false)

const openMenus = ['home']
// 从移动端切回桌面端时，自动关掉抽屉，避免状态残留
watch(
    () => isMobile,
    (m) => {
      if (!m) drawerOpen.value = false
    }
)
</script>

<style scoped>
.sidebar-menu {
  height: 100%;
}


.mobile-menu-bar {
  width: 100%;
  border-radius: 0;
  border-left: 0;
  border-right: 0;
  border-top: 0;
  padding-right: 0 !important;
}

.mobile-menu-btn {
  width: 100%;
  justify-content: flex-start; /* 图标文字靠左 */
  padding: 0;
}

</style>
