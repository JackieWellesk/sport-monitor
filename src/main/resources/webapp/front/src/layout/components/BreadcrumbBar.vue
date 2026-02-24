<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item v-for="(c, idx) in crumbs" :key="c.key || c.path || idx">
      <!-- 父级一般不可点（因为你父 path 可能不是路由），只有“可解析到路由”的才可点 -->
      <span v-if="idx === crumbs.length - 1 || !c.clickable">{{ c.label }}</span>
      <a v-else @click.prevent="go(c.path)">{{ c.label }}</a>
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup>
defineOptions({ name: 'BreadcrumbBar' })

import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'
import { menuTree, filterMenuByRole } from '@/config/menu'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()

function findChainByPath(tree, targetPath, parents = []) {
  for (const node of tree || []) {
    if (!node) continue
    const chain = [...parents, node]
    if (node.path === targetPath) return chain
    if (Array.isArray(node.children) && node.children.length) {
      const res = findChainByPath(node.children, targetPath, chain)
      if (res) return res
    }
  }
  return null
}

// 判断某个 path 是否能被路由解析（能解析才允许点击）
function isRoutable(path) {
  if (!path) return false
  const r = router.resolve(path)
  // matched 至少包含一个有组件的记录才算可导航（避免父path不存在导致 404）
  return Array.isArray(r.matched) && r.matched.length > 0
}

const crumbs = computed(() => {
  const role = authStore.user?.roleCode
  const tree = role ? filterMenuByRole(menuTree, role) : menuTree

  // 1) 先用菜单树反查链路（能拿到“父/子”）
  const chain = findChainByPath(tree, route.path) || []

  // 2) 转成可渲染结构
  let list = chain.map(n => ({
    key: n.key,
    path: n.path,
    label: t(n.titleKey) || n.titleKey || n.key || n.path,
    clickable: isRoutable(n.path)
  }))

  // 3) 如果菜单里没找到（例如你有些页面不在菜单里），兜底用路由 title
  if (list.length === 0 && route.meta?.title) {
    list = [{
      key: route.name,
      path: route.path,
      label: String(route.meta.title),
      clickable: false
    }]
  }

  // 父级通常不可点（防止你父 path 不是路由时误跳）
  if (list.length >= 2) {
    list[0].clickable = false
  }

  return list
})

function go(path) {
  router.push(path)
}
</script>