<template>
  <el-container style="height: 100vh;">
    <el-header class="header">
      <HeaderBar/>
    </el-header>

    <el-container :class="{ 'layout-body-mobile': isMobile }">
      <el-aside :class="{ 'aside-mobile': isMobile }">
        <SideBar/>
      </el-aside>

      <el-main class="main-content">
        <BreadcrumbBar />
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import BreadcrumbBar from "@/layout/components/BreadcrumbBar.vue";

defineOptions({ name: 'LayoutView', components: { HeaderBar, SideBar } })

import HeaderBar from './components/HeaderBar.vue'
import SideBar from './components/SideBar.vue'
import { useResponsive } from '@/composables/useResponsive'
const { isMobile } = useResponsive()


</script>

<style scoped>
.main-content { height: 100%; }
.header {padding: 0}
/* 移动端：让 aside 和 main 上下排列 */
.layout-body-mobile {
  flex-direction: column;
}
.aside-mobile {
  width: 100% !important;
  flex: 0 0 auto !important;  /* 高度跟内容走 */
}
</style>
