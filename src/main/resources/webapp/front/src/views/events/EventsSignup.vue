<template>
  <el-container direction="vertical">
    <el-main style="padding: 0;">
      <el-card shadow="never" body-style="padding: 0 10px 10px 10px;" style="margin-top: 10px">

        <!-- 顶部：标题 -->
        <el-row justify="space-between" align="middle" style="margin-top: 10px;">
          <div style="font-weight: 600;">报名列表</div>
          <el-space>
            <el-button :loading="loading" @click="load">刷新</el-button>
          </el-space>
        </el-row>

        <!-- 查询区（仿 event_list） -->
        <el-form :inline="!isMobile" label-width="90px" @submit.prevent style="margin-top: 10px">
          <el-row :gutter="12">

            <el-col :xs="24" :sm="4">
              <el-form-item label="赛事ID" label-position="left" label-width="">
                <el-input
                    v-model="query.eventId"
                    clearable
                    placeholder="请输入赛事ID"
                    style="min-width: 180px;"
                />
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="4">
              <el-form-item label="用户ID" label-position="left" label-width="">
                <el-input
                    v-model="query.userId"
                    clearable
                    placeholder="请输入用户ID"
                    style="min-width: 180px;"
                />
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="4">
              <el-form-item label="状态" label-position="left" label-width="">
                <el-select v-model="query.status" clearable placeholder="全部" style="min-width: 200px">
                  <el-option label="已报名" value="SIGNED" />
                  <el-option label="已取消" value="CANCELED" />
                </el-select>
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
            <el-table-column prop="eventId" label="赛事ID" width="110" />
            <el-table-column prop="userId" label="用户ID" width="110" />

            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag v-if="row.status === 'SIGNED'" type="success">已报名</el-tag>
                <el-tag v-else type="info">已取消</el-tag>
              </template>
            </el-table-column>

            <el-table-column prop="signupTime" label="报名时间" min-width="170" />

            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-space>
                  <el-button
                      size="small"
                      type="danger"
                      link
                      :disabled="row.status !== 'SIGNED'"
                      :loading="actionLoadingId === row.id"
                      @click="onDelete(row)"
                  >
                    删除
                  </el-button>
                </el-space>
              </template>
            </el-table-column>
          </el-table>
        </template>

        <!-- 手机：卡片列表 -->
        <template v-else>
          <el-space direction="vertical" fill style="width: 100%;">
            <el-card
                v-for="s in rows"
                :key="s.id"
                shadow="never"
                body-style="padding: 12px"
                style="border: 1px solid #ebeef5; border-radius: 8px;"
            >
              <el-row>
                <el-col :span="24">
                  <el-descriptions :column="1" size="small" border>
                    <el-descriptions-item label="ID">
                      {{ s.id }}
                    </el-descriptions-item>

                    <el-descriptions-item label="赛事ID">
                      {{ s.eventId }}
                    </el-descriptions-item>

                    <el-descriptions-item label="用户ID">
                      {{ s.userId }}
                    </el-descriptions-item>

                    <el-descriptions-item label="状态">
                      <el-tag v-if="s.status === 'SIGNED'" type="success">已报名</el-tag>
                      <el-tag v-else type="info">已取消</el-tag>
                    </el-descriptions-item>

                    <el-descriptions-item label="报名时间">
                      {{ s.signupTime }}
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>
              </el-row>

              <el-row justify="end" style="margin-top: 12px;">
                <el-space>
                  <el-button
                      size="small"
                      type="danger"
                      :disabled="s.status !== 'SIGNED'"
                      :loading="actionLoadingId === s.id"
                      @click="onDelete(s)"
                  >
                    删除
                  </el-button>
                </el-space>
              </el-row>
            </el-card>
          </el-space>
        </template>

        <!-- 分页（仿 event_list） -->
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

const { isMobile } = useResponsive()

const loading = ref(false)
const actionLoadingId = ref(null)

const query = reactive({
  eventId: '',
  userId: '',
  status: '', // SIGNED / CANCELED
})

const page = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const rows = ref([])

// ============== API（按你后端实际地址调整）==============
// 推荐后端返回 Page：{ records, total }
async function fetchSignupsApi(params) {
  return request.get('/api/events/signups', { params })
}

/**
 * 删除按钮：你可以二选一
 * 1) 真删除：DELETE /api/events/signups/{id}
 * 2) 业务取消：PUT /api/events/signups/{id}/cancel
 *
 * 默认这里用 DELETE；如果你后端是 cancel，改成 put 即可。
 */
async function deleteSignupApi(id) {
  return request.delete(`/api/events/signups/${id}`)
  // return request.put(`/api/events/signups/${id}/cancel`)
}
// ======================================================

function buildParams() {
  const params = {
    current: page.current,
    size: page.size,
  }
  if (query.eventId) params.eventId = query.eventId
  if (query.userId) params.userId = query.userId
  if (query.status) params.status = query.status
  return params
}

async function load() {
  loading.value = true
  try {
    const res = await fetchSignupsApi(buildParams())
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
  query.eventId = ''
  query.userId = ''
  query.status = ''
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

async function onDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该报名记录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  actionLoadingId.value = row.id
  try {
    await deleteSignupApi(row.id)
    ElMessage.success('已删除')
    await load()
  } catch (e) {
    console.log(e)
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