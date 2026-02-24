<template>
  <el-config-provider :locale="locale">
    <router-view />
  </el-config-provider>
</template>

<script setup>
import { computed, ref } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import en from 'element-plus/es/locale/lang/en'
const language = ref(localStorage.getItem('locale') || 'zh') // 从 localStorage 获取当前语言
const locale = computed(() => (language.value === 'zh' ? zhCn : en))

</script>