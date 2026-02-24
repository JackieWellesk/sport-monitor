<template>
  <el-card
      shadow="never"
      :body-style="isMobile? 'padding:5px': 'padding: 16px'"
      class="news-card"
      @click="$emit('click', event.id)"
  >
    <!-- 桌面布局 -->
    <el-row v-if="!isMobile" :gutter="16">
      <el-col v-if="event.firstImg" :span="6">
        <el-image
            :src="event.firstImg"
            fit="cover"
            style="width: 100%; height: 120px; border-radius: 8px"
        />
      </el-col>

      <el-col :span="event.firstImg ? 18 : 24">
        <el-space direction="vertical" fill style="width: 100%;">
          <el-row justify="space-between" align="middle">
            <span class="news-title">{{ event.title }}</span>
            <el-tag :type="statusType(event.signupStatus)">
              {{ statusText(event.signupStatus) }}
            </el-tag>
          </el-row>

          <span class="news-summary">{{ event.simpleContent }}</span>

          <el-row justify="space-between" align="middle">
            <el-space>
              <el-tag type="primary" effect="plain">{{ event.eventType }}</el-tag>
              <el-tag type="info" effect="plain">
                报名 {{ event.signupStartDate }} ~ {{ event.signupEndDate }}
              </el-tag>
            </el-space>
            <span class="news-time">{{ event.signupYear }}</span>
          </el-row>
        </el-space>
      </el-col>
    </el-row>

    <!-- 手机布局 -->
    <el-space v-else direction="vertical" fill style="width: 100%;">
      <el-image
          v-if="event.firstImg"
          :src="event.firstImg"
          fit="cover"
          style="width: 100%; height: 160px; border-radius: 8px"
      />
      <el-space direction="vertical" fill style="width: 100%;">
        <el-row justify="space-between" align="middle">
          <span class="news-title">{{ event.title }}</span>
          <el-tag :type="statusType(event.signupStatus)">
            {{ statusText(event.signupStatus) }}
          </el-tag>
        </el-row>

        <span class="news-summary">{{ event.simpleContent }}</span>

        <el-row justify="space-between" align="middle">
          <el-space>
            <el-tag type="primary" effect="plain">{{ event.eventType }}</el-tag>
            <el-tag type="info" effect="plain">
              报名 {{ event.signupStartDate }} ~ {{ event.signupEndDate }}
            </el-tag>
          </el-space>
          <span class="news-time">{{ event.signupYear }}</span>
        </el-row>
      </el-space>
    </el-space>
  </el-card>
</template>

<script setup>
import { useResponsive } from '@/composables/useResponsive'

defineEmits(['click'])

defineProps({
  event: { type: Object, required: true }
})

const { isMobile } = useResponsive()

function statusText(status) {
  if (status === 'SIGNUP') return '报名中'
  if (status === 'NOT_START') return '未开始'
  if (status === 'CLOSED') return '已结束'
  if (status === 'FINISHED') return '已完成'
  return '报名已结束'
}

function statusType(status) {
  if (status === 'SIGNUP') return 'success'
  if (status === 'NOT_START') return 'warning'
  if (status === 'CLOSED') return 'info'
  if (status === 'FINISHED') return 'danger'
  return 'info'
}
</script>

<style scoped>
.news-card {
  cursor: pointer;
  transition: var(--el-transition-duration-fast);
}

.news-card:hover {
  background-color: var(--el-fill-color-light);
}

.news-title {
  font-size: var(--el-font-size-base);
  font-weight: var(--el-font-weight-primary);
  color: var(--el-text-color-primary);
}

.news-summary {
  color: var(--el-text-color-regular);
  line-height: var(--el-font-line-height-primary);
}

.news-time {
  font-size: var(--el-font-size-small);
  color: var(--el-text-color-secondary);
}
</style>