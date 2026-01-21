<template>
  <el-container style="height: 100vh;">
    <!-- 顶部导航 -->
    <el-header class="header">
      <el-menu mode="horizontal" :ellipsis="false">
        <el-menu-item class="logo" index="0">{{ $t('message.sportMonitor') }}</el-menu-item>
        <!-- 添加切换黑暗模式按钮 -->
        <el-menu-item index="1">
          <el-switch :model-value="isDark"  @change="onChange" @click.capture="onClick"
                     :active-action-icon="Moon"
                     :inactive-action-icon="Sunny"
          />
        </el-menu-item>
        <el-sub-menu index="2">
          <template #title>{{ menuTitle }}</template>
          <el-menu-item index="2-1" @click="changeLanguage('zh')">中文</el-menu-item>
          <el-menu-item index="2-2" @click="changeLanguage('en')">en</el-menu-item>
        </el-sub-menu>

      </el-menu>
    </el-header>

    <el-container>
      <!-- 左侧菜单 -->
      <el-aside width="220px" class="side">
        <el-menu default-active="button" router :collapse="isMobile">
          <el-sub-menu index="components">
            <template #title>
              <i class="el-icon-menu"></i><span>组件</span>
            </template>
            <el-menu-item index="button">Button 按钮</el-menu-item>
            <el-menu-item index="input">Input 输入框</el-menu-item>
            <el-menu-item index="form">Form 表单</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <!-- 主体内容 -->
      <el-main class="main-content">
        <router-view />
        <!--        <h1 style="text-align: center;font-size: 400px;margin-top: 0">please. </h1>-->
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDark } from '@/hooks/useDark';
import {Sunny, Moon} from '@element-plus/icons-vue'



export default {
  name: 'App',
  setup() {
    const { locale } = useI18n()  // 引入 i18n
    const isMobile = ref(false)
    const isDarkMode = ref(false)  // 控制是否启用黑暗模式
    const menuTitle = ref('中文')  // 初始菜单标题为中文
    const { isDark, mode } = useDark();
    const pos = ref({ x: 0, y: 0 });

    // 切换菜单折叠状态
    const toggleMenu = () => {
      isMobile.value = !isMobile.value
    }

    const updateMenuTitle = (lang) => {
      if (lang === 'zh') {
        menuTitle.value = '中文'  // 中文
      } else {
        menuTitle.value = 'en' // 英文
      }
    }

    // 切换语言函数
    const changeLanguage = (lang) => {
      locale.value = lang  // 切换语言
      updateMenuTitle(lang)
    }

    // 设置默认选中的值
    const selectedValue = ref('zh') // 默认选中 "中文"

    function onClick(e) {
      pos.value = { x: e.clientX, y: e.clientY };
    }

    function onChange(val) {
      const setTheme = () => {
        mode.value = val ? 'dark' : 'light';
      };

      const doAnimate = () => {
        const radius = Math.hypot(
            Math.max(pos.value.x, window.innerWidth - pos.value.x),
            Math.max(pos.value.y, window.innerHeight - pos.value.y)
        );

        const clipPath = [
          `circle(0px at ${pos.value.x}px ${pos.value.y}px)`,
          `circle(${radius}px at ${pos.value.x}px ${pos.value.y}px)`
        ];

        document.documentElement.animate(
            // 通过 val 值判断展示方向
            { clipPath: val ? clipPath.reverse() : clipPath },
            {
              duration: 600,
              pseudoElement: val
                  // 这里一样，通过 val 值判断方向
                  ? '::view-transition-old(root)'
                  : '::view-transition-new(root)'
            }
        );
      };
      document.startViewTransition
          ? document.startViewTransition(setTheme).ready.then(doAnimate)
          : setTheme();
    }


    return {
      isMobile,
      toggleMenu,
      selectedValue,
      changeLanguage,
      isDarkMode,
      menuTitle,
      updateMenuTitle,
      isDark,
      onClick,
      onChange,
      Moon,
      Sunny
    }
  }

}
</script>
<style scoped>

.el-menu--horizontal > .el-menu-item:nth-child(1) {
  margin-right: auto;
}

.header {
  padding: 0;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.side {
  height: 100%;
}

.side .el-menu {
  height: 100%;
}



</style>