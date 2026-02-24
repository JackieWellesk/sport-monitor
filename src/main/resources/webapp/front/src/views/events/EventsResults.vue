<template>
  <el-container direction="vertical">
    <el-main style="padding: 0;">
      <el-card
          shadow="never"
          body-style="padding: 0 10px 10px 10px;"
          style="margin-top: 10px"
      >
        <!-- 查询区 -->
        <el-form :inline="!isMobile" @submit.prevent style="margin-top: 10px">
          <el-row :gutter="12">
            <el-col :xs="24" :sm="5">
              <el-form-item label="赛事ID" label-position="left" label-width="">
                <el-input
                    v-model="query.eventId"
                    clearable
                    placeholder="请输入赛事ID"
                    @keyup.enter="onSearch"
                    style="min-width: 200px;"
                />
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="8">
              <el-form-item label-width="">
                <el-row :justify="isMobile ? 'end' : 'start'" style="width: 100%;">
                  <el-space>
                    <el-button type="primary" :loading="loading" @click="onSearch">
                      查询
                    </el-button>
                    <el-button :loading="loading" @click="onReset">
                      重置
                    </el-button>
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
            <el-table-column prop="eventTitle" label="赛事名称" min-width="120" />
            <el-table-column prop="userName" label="用户名" min-width="120" />
            <el-table-column label="成绩" min-width="120">
              <template #default="{ row }">
                {{ row.scoreValue }} {{ row.scoreUnit }}
              </template>
            </el-table-column>
            <el-table-column prop="rankNo" label="排名" width="90" />

            <el-table-column label="是否发布" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.published ? 'success' : 'info'">
                  {{ row.published ? '已发布' : '未发布' }}
                </el-tag>
              </template>
            </el-table-column>

          </el-table>
        </template>

        <!-- 手机：卡片列表 -->
        <template v-else>
          <el-space direction="vertical" fill style="width: 100%;">
            <el-card
                v-for="r in rows"
                :key="r.id"
                shadow="never"
                body-style="padding: 10px"
            >
              <el-descriptions :column="1" size="small" border>
                <el-descriptions-item label="ID">{{ r.id }}</el-descriptions-item>
                <el-descriptions-item label="赛事名称">{{ r.eventTitle }}</el-descriptions-item>
                <el-descriptions-item label="用户名">{{ r.userName }}</el-descriptions-item>
                <el-descriptions-item label="成绩">
                  {{ r.scoreValue }} {{ r.scoreUnit }}
                </el-descriptions-item>
                <el-descriptions-item label="排名">{{ r.rankNo }}</el-descriptions-item>
                <el-descriptions-item label="发布状态">
                  <el-tag :type="r.published ? 'success' : 'info'">
                    {{ r.published ? '已发布' : '未发布' }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>

            </el-card>
          </el-space>
        </template>

        <!-- 分页 -->
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
import { ElMessage } from 'element-plus'
import { useResponsive } from '@/composables/useResponsive'
import request from '@/utils/request'

const { isMobile } = useResponsive()

const loading = ref(false)

const query = reactive({
  eventId: '',
  userId: '',
})

const page = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const rows = ref([])

function buildParams() {
  const params = {
    current: page.current,
    size: page.size,
  }
  if (query.eventId) params.eventId = query.eventId
  return params
}

async function load() {
  loading.value = true
  try {
    // 你按自己后端实际接口改这里
    const res = await request.get('/api/results', { params: buildParams() })
    rows.value = res.data?.records || []
    page.total = Number(res.data?.total || 0)
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

onMounted(() => {
  load()
})
</script>

<style scoped>
.el-card__body {
  padding: 0 !important;
}
</style>