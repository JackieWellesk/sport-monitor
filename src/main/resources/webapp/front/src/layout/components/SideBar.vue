<template>
  <!-- 桌面端：正常侧边栏 -->
  <el-menu
      v-if="!isMobile"
      class="sidebar-menu"
      :default-active="route.path"
      router
      unique-opened
  >
    <SidebarItem
        v-for="item in menus"
        :key="item.key"
        :item="item"
    />
  </el-menu>

  <!-- 移动端：入口按钮 + Drawer 菜单 -->
  <el-card v-else shadow="never" class="mobile-menu-bar">
    <el-button
        text
        class="mobile-menu-btn"
        :icon="Menu"
        @click="drawerOpen = true"
    >
      {{ $t('message.menu') }}
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
            :default-active="route.path"
            router
            unique-opened
            @select="drawerOpen = false"
            :default-openeds="openMenus"
        >
          <SidebarItem
              v-for="item in menus"
              :key="item.key"
              :item="item"
          />
        </el-menu>
      </el-scrollbar>
    </el-drawer>
  </el-card>
</template>

<script setup>
import {computed, ref, watch} from 'vue'
import {Menu} from '@element-plus/icons-vue'
import {useMediaQuery} from '@vueuse/core'
import {useRoute} from 'vue-router'

import {useAuthStore} from '@/stores/auth'
import {filterMenuByRole, menuTree} from '@/config/menu'
import SidebarItem from '@/layout/components/SidebarItem.vue'

defineOptions({ name: 'SideBar' })

const isMobile = useMediaQuery('(max-width: 767px)')
const drawerOpen = ref(false)

const route = useRoute()
const authStore = useAuthStore()
const menus = computed(() => {
  if (!authStore.inited) return []
  if (!authStore.user) return []
  return filterMenuByRole(menuTree, authStore.user.roleCode)
})

// 默认展开：展开“当前路径所在的父节点”
const openMenus = computed(() => {
  const opens = []
  const cur = route.path

  function walk(list, parents = []) {
    for (const node of list || []) {
      if (node.path === cur) {
        for (const p of parents) {
          if (p.children && p.children.length) opens.push(p.path)
        }
      }
      if (node.children && node.children.length) {
        walk(node.children, [...parents, node])
      }
    }
  }

  walk(menus.value, [])
  return Array.from(new Set(opens))
})

watch(isMobile, (m) => {
  if (!m) drawerOpen.value = false
})
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
  justify-content: flex-start;
  padding: 0;
}
</style>