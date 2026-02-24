<template>
  <el-sub-menu v-if="hasChildren" :index="item.path">
    <template #title>
      <el-icon v-if="IconComp">
        <component :is="IconComp" />
      </el-icon>
      <span>{{ title }}</span>
    </template>

    <SidebarItem
        v-for="child in (item.children || [])"
        :key="child.key"
        :item="child"
    />
  </el-sub-menu>

  <el-menu-item v-else :index="item.path">
    <el-icon v-if="IconComp">
      <component :is="IconComp" />
    </el-icon>
    <span>{{ title }}</span>
  </el-menu-item>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import * as Icons from '@element-plus/icons-vue'

defineOptions({ name: 'SidebarItem' })

const props = defineProps({
  item: { type: Object, required: true }
})

const { t } = useI18n()

const hasChildren = computed(() =>
    Array.isArray(props.item?.children) && props.item.children.length > 0
)

const IconComp = computed(() => {
  const name = props.item?.icon
  const comp = name ? Icons[name] : null
  if (name && !comp) {
    console.warn('[Menu icon not found]', name, 'at path:', props.item?.path)
  }
  return comp || null
})

const title = computed(() => t(props.item?.titleKey) || props.item?.key || props.item?.path || '')
</script>