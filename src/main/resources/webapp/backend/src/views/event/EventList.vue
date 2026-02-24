<template>
  <el-container direction="vertical">
    <el-main style="padding: 0;">
      <el-card shadow="never" body-style="padding: 0 10px 10px 10px;" style="margin-top: 10px">

        <!-- 顶部：标题 + 新增按钮 -->
        <el-row justify="space-between" align="middle" style="margin-top: 10px;">
          <div style="font-weight: 600;">赛事列表</div>
          <el-button type="primary" @click="onCreate">新增赛事</el-button>
        </el-row>

        <!-- 查询区（模仿用户列表页） -->
        <el-form :inline="!isMobile" label-width="90px" @submit.prevent style="margin-top: 10px">
          <el-row :gutter="12">

            <el-col :xs="24" :sm="4">
              <el-form-item label="状态" label-position="left" label-width="">
                <el-select v-model="query.status" clearable placeholder="全部" style="min-width: 200px">
                  <el-option label="草稿" value="DRAFT" />
                  <el-option label="已发布" value="PUBLISHED" />
                  <el-option label="已结束" value="CLOSED" />
                  <el-option label="已完成" value="FINISHED" />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="4">
              <el-form-item label="发布人ID" label-position="left" label-width="">
                <el-input
                    v-model="query.createdBy"
                    clearable
                    placeholder="请输入发布人ID"
                    style="min-width: 180px;"
                />
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="8">
              <el-form-item label-width="">
                <el-row :justify="isMobile ? 'end' : 'start'" style="width: 100%;">
                  <el-space>
                    <el-button type="primary" :loading="loading" @click="onSearch">查询</el-button>
                    <el-button :loading="loading" @click="onReset">重置</el-button>
                  </el-space>
                </el-row>
              </el-form-item>
            </el-col>

          </el-row>
        </el-form>

        <!-- 桌面：表格 -->
        <template v-if="!isMobile">
          <el-table :data="rows" v-loading="loading" border style="width: 100%;">
            <el-table-column prop="id" label="ID" width="90" />

            <el-table-column label="赛事名称" min-width="220">
              <template #default="{ row }">
                <!-- 可点击性按状态控制 -->
                <el-link
                    v-if="canEdit(row)"
                    type="primary"
                    @click="onEdit(row)"
                >
                  {{ row.title }}
                </el-link>
                <span v-else>{{ row.title }}</span>
              </template>
            </el-table-column>

            <el-table-column label="状态" width="140" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'DRAFT'">草稿</el-tag>
                <el-tag v-else-if="row.status === 'PUBLISHED'" type="success">已发布</el-tag>
                <el-tag v-else-if="row.status === 'CLOSED'" type="warning">已结束</el-tag>
                <el-tag v-else type="info">已完成</el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="createdBy" label="发布人ID" width="120" />

            <el-table-column prop="signupStart" label="报名开始" min-width="160" />
            <el-table-column prop="signupEnd" label="报名结束" min-width="160" />
            <el-table-column prop="startTime" label="比赛开始" min-width="160" />
            <el-table-column prop="endTime" label="比赛结束" min-width="160" />

            <el-table-column label="操作" width="260" fixed="right">
              <template #default="{ row }">
                <el-space>
                  <el-button
                      size="small"
                      type="primary"
                      link
                      :disabled="!canEdit(row)"
                      @click="onEdit(row)"
                  >
                    编辑
                  </el-button>

                  <el-button
                      v-if="row.status === 'DRAFT'"
                      size="small"
                      type="success"
                      link
                      :loading="actionLoadingId === row.id"
                      @click="onPublish(row)"
                  >
                    发布
                  </el-button>

                  <el-button
                      v-if="row.status === 'PUBLISHED'"
                      size="small"
                      type="warning"
                      link
                      :loading="actionLoadingId === row.id"
                      @click="onClose(row)"
                  >
                    关闭
                  </el-button>

                  <el-button
                      v-if="row.status === 'CLOSED'"
                      size="small"
                      type="warning"
                      link
                      :loading="actionLoadingId === row.id"
                      @click="onFinished(row)"
                  >
                    标记完成
                  </el-button>

                </el-space>
              </template>
            </el-table-column>
          </el-table>
        </template>

        <!-- 手机：卡片列表（模仿你的用户列表页手机卡片区） -->
        <!-- 手机端 -->
        <template v-else>
          <el-space direction="vertical" fill style="width: 100%;">
            <el-card
                v-for="e in rows"
                :key="e.id"
                shadow="never"
                body-style="padding: 12px"
                style="border: 1px solid #ebeef5; border-radius: 8px;"
            >
              <!-- 信息区 -->
              <el-row>
                <el-col :span="24">
                  <el-descriptions :column="1" size="small" border>

                    <el-descriptions-item label="ID">
                      {{ e.id }}
                    </el-descriptions-item>

                    <el-descriptions-item label="赛事名称">
                      <el-link
                          v-if="canEdit(e)"
                          type="primary"
                          @click="onEdit(e)"
                      >
                        {{ e.title }}
                      </el-link>
                      <span v-else>{{ e.title }}</span>
                    </el-descriptions-item>

                    <el-descriptions-item label="状态">
                      <el-tag v-if="e.status === 'DRAFT'">草稿</el-tag>
                      <el-tag v-else-if="e.status === 'PUBLISHED'" type="success">已发布</el-tag>
                      <el-tag v-else-if="e.status === 'CLOSED'" type="warning">已结束</el-tag>
                      <el-tag v-else type="info">已完成</el-tag>
                    </el-descriptions-item>

                    <el-descriptions-item label="发布人ID">
                      {{ e.createdBy }}
                    </el-descriptions-item>

                    <el-descriptions-item label="报名时间">
                      {{ e.signupStart }} ~ {{ e.signupEnd }}
                    </el-descriptions-item>

                    <el-descriptions-item label="比赛时间">
                      {{ e.startTime }} ~ {{ e.endTime }}
                    </el-descriptions-item>

                    <el-descriptions-item label="人数上限">
                      {{ e.maxParticipants }}
                    </el-descriptions-item>

                  </el-descriptions>
                </el-col>
              </el-row>

              <!-- 操作按钮区 -->
              <el-row justify="end" style="margin-top: 12px;">
                <el-space>
                  <el-button
                      size="small"
                      type="primary"
                      :disabled="!canEdit(e)"
                      @click="onEdit(e)"
                  >
                    编辑
                  </el-button>

                  <el-button
                      v-if="e.status === 'DRAFT'"
                      size="small"
                      type="success"
                      @click="onPublish(e)"
                  >
                    发布
                  </el-button>

                  <el-button
                      v-if="e.status === 'PUBLISHED'"
                      size="small"
                      type="warning"
                      @click="onClose(e)"
                  >
                    关闭
                  </el-button>

                  <el-button
                      v-if="e.status === 'CLOSED'"
                      size="small"
                      type="warning"
                      @click="onFinished(e)"
                  >
                    标记完成
                  </el-button>

                </el-space>
              </el-row>
            </el-card>
          </el-space>
        </template>

        <!-- 分页（模仿你的 layout 切换） -->
        <el-row justify="end" style="margin-top: 20px">
          <el-pagination
              v-model:current-page="page.current"
              v-model:page-size="page.size"
              :total="page.total"
              :layout="isMobile ? 'prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
              :page-sizes="[10, 20, 50]"
              background
              @size-change="onSizeChange"
              @current-change="onCurrentChange"
          />
        </el-row>

      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useResponsive } from '@/composables/useResponsive'
import request from '@/utils/request'
import { useRouter } from 'vue-router'

const router = useRouter()
const { isMobile } = useResponsive()

const loading = ref(false)
const actionLoadingId = ref(null)

const query = reactive({
  status: '',
  createdBy: '',
})

const page = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const rows = ref([])

/** 可点击/可编辑规则：按状态决定（你可以改这里） */
function canEdit(row) {
  // 参考你的需求：根据状态控制可点击性
  // 常见做法：只有草稿可编辑
  return row.status === 'DRAFT'
}

// ============== API（按你后端实际地址调整）==============
async function fetchEventsApi(params) {
  // 推荐：后端返回 Page：{ records, total }
  return request.get('/api/events', { params })
}

async function publishApi(id) {
  return request.put(`/api/events/${id}/publish`)
}

async function closeApi(id) {
  return request.put(`/api/events/${id}/close`)
}

async function finishApi(id) {
  return request.put(`/api/events/${id}/finish`)
}
// ======================================================

function buildParams() {
  const params = {
    current: page.current,
    size: page.size,
  }
  if (query.status) params.status = query.status
  if (query.createdBy) params.createdBy = query.createdBy
  return params
}

async function load() {
  loading.value = true
  try {
    const res = await fetchEventsApi(buildParams())
    rows.value = res.data.records || []
    page.total = Number(res.data.total || 0)
  } catch (e) {
    ElMessage.error(e?.message || '加载失败')
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.current = 1
  load()
}

function onReset() {
  query.status = ''
  query.createdBy = ''
  page.current = 1
  load()
}

function onSizeChange() {
  page.current = 1
  load()
}

function onCurrentChange() {
  load()
}

function onCreate() {
  router.push('/events/edit')
}

function onEdit(row) {
  if (!canEdit(row)) return
  router.push({ path: '/events/edit', query: { id: row.id } })
}

async function onPublish(row) {
  try {
    await ElMessageBox.confirm('确认发布该赛事吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  actionLoadingId.value = row.id
  try {
    await publishApi(row.id)
    ElMessage.success('已发布')
    await load()
  } catch (e) {
    ElMessage.error(e?.message || '发布失败')
  } finally {
    actionLoadingId.value = null
  }
}

async function onClose(row) {
  try {
    await ElMessageBox.confirm('确认关闭该赛事吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  actionLoadingId.value = row.id
  try {
    await closeApi(row.id)
    ElMessage.success('已关闭')
    await load()
  } catch (e) {
    ElMessage.error(e?.message || '关闭失败')
  } finally {
    actionLoadingId.value = null
  }
}

async function onFinished(row) {
  try {
    await ElMessageBox.confirm('确认关闭该赛事吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  actionLoadingId.value = row.id
  try {
    await finishApi(row.id)
    ElMessage.success('已关闭')
    await load()
  } catch (e) {
    ElMessage.error(e?.message || '关闭失败')
  } finally {
    actionLoadingId.value = null
  }
}

onMounted(() => load())
</script>

<style scoped>
.el-card__body {
  padding: 0 !important;
}
</style>