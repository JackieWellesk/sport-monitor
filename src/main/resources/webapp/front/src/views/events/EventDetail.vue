<template>
  <el-container direction="vertical">
    <el-main style="padding: 0;">
      <el-card
          shadow="never"
          body-style="padding: 0 16px 16px 16px;"
          style="margin-top: 10px"
          v-loading="loading"
      >

        <div class="article">

          <!-- 标题 -->
          <div class="article-title">
            {{ detail.title }}
          </div>

          <!-- 基本信息 -->
          <el-row
              class="article-meta"
              :gutter="12"
              align="middle"
              style="margin-top: 12px"
          >
            <el-col :xs="24" :sm="16">
              <el-space wrap>
                <span class="meta-text">
                  创建时间：{{ detail.publishTime }}
                </span>
                <span class="meta-text">
                  报名时间：{{ detail.signupStartDateYMD }} ~ {{ detail.signupEndYMD }}
                </span>
                <span class="meta-text">
                  比赛时间：{{ detail.startTimeYMD }}
                </span>
              </el-space>
            </el-col>

            <el-col :xs="24" :sm="8" style="margin-top: 8px;">
              <el-row justify="end">
                <el-space wrap>
                  <el-tag :type="statusType(detail.signupStatus)">
                    {{ statusText(detail.signupStatus) }}
                  </el-tag>
                  <el-tag type="primary" effect="plain">
                    {{ detail.eventType }}
                  </el-tag>
                </el-space>
              </el-row>
            </el-col>
          </el-row>

          <!-- 报名按钮 -->
          <el-row justify="end" style="margin-top: 16px">
            <el-button @click="goBack">返回</el-button>
            <el-button
                v-if="detail.signupStatus === 'SIGNUP'"
                type="primary"
                @click="onSignup"
            >
              立即报名
            </el-button>

            <el-button
                v-else
                disabled
            >
              {{ detail.signupStatus === 'NOT_START'
                ? '未到报名时间'
                : '报名已结束'
              }}
            </el-button>
          </el-row>

          <el-divider style="margin: 20px 0;" />

          <!-- 正文内容 -->
          <div
              class="article-body"
              v-html="detail.content"
          />

          <!-- 底部 -->
          <el-divider style="margin-top: 24px;" />
          <el-row justify="space-between" align="middle">
            <span class="meta-text">
              更新时间：{{ detail.updatedAt }}
            </span>
            <el-button size="small" @click="scrollTop">
              回到顶部
            </el-button>
          </el-row>

        </div>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import {useRoute, useRouter} from 'vue-router'
// import { useResponsive } from '@/composables/useResponsive'
import { ElMessage } from 'element-plus'
import request from "@/utils/request";

// const { isMobile } = useResponsive()
const route = useRoute()
const router = useRouter()

const loading = ref(false)

const detail = reactive({})



async function load() {
  loading.value = true
  try {
    const id = route.params.id || route.query.id

    const res = await request.get(`/api/events/${id}`)

    // 防止 data 为 null
    const data = res.data ?? {}

    Object.assign(detail, data)

  } catch (e) {
    ElMessage.error('加载赛事详情失败')
  } finally {
    loading.value = false
  }
}

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

async function onSignup() {
  loading.value = true
  try {
    const id = route.params.id || route.query.id
    await request.put(`/api/events/signup/me/${id}`)
    ElMessage.success('报名成功')
  } catch (e) {
    console.log(e)
  } finally {
    loading.value = false
  }

}

function goBack() {
  router.back()
}

function scrollTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.article {
  margin-top: 20px;
}
.page-title {
  font-size: var(--el-font-size-large);
  font-weight: var(--el-font-weight-primary);
}

.article-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.meta-text {
  font-size: var(--el-font-size-small);
  color: var(--el-text-color-secondary);
}

.article-body {
  margin-top: 10px;
  line-height: 1.8;
  color: var(--el-text-color-regular);
}

.article-body :deep(p) {
  margin-bottom: 12px;
}

.article-body :deep(img) {
  max-width: 100%;
  height: auto;
  display: block;
  margin: 12px auto;
  border-radius: var(--el-border-radius-base);
}
</style>