<template>
  <el-container direction="vertical">
    <el-main style="padding:0;">
      <el-card shadow="never" body-style="padding: 0 10px 10px 10px;" style="margin-top:10px">

        <!-- ================= 查询区 ================= -->
        <el-form :inline="!isMobile" label-width="90px" @submit.prevent style="margin-top:10px">
          <el-row :gutter="12">

            <el-col :xs="24" :sm="4">
              <el-form-item label="运动类型">
                <el-input
                    v-model="query.sportType"
                    clearable
                    placeholder="请输入运动类型"
                    @keyup.enter="onSearch"
                />
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="6">
              <el-form-item>
                <el-space>
                  <el-button type="primary" :loading="loading" @click="onSearch">
                    查询
                  </el-button>
                  <el-button @click="onReset">
                    重置
                  </el-button>
                  <el-button type="success" @click="goToEditPage()">
                    新增记录
                  </el-button>
                </el-space>
              </el-form-item>
            </el-col>

          </el-row>
        </el-form>

        <!-- ================= 桌面表格 ================= -->
        <template v-if="!isMobile">
          <el-table :data="rows" v-loading="loading" border>

            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="sportType" label="运动类型" />
            <el-table-column prop="durationMin" label="时长(分钟)" width="120" />
            <el-table-column prop="calorieKcal" label="卡路里" width="120" />
            <el-table-column prop="distanceKm" label="距离(km)" width="120" />
            <el-table-column prop="avgHr" label="平均心率" width="120" />

            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-space>
                  <el-button type="primary" link size="small" @click="goToEditPage(row)">
                    编辑
                  </el-button>
                  <el-button type="danger" link size="small" @click="onDelete(row)">
                    删除
                  </el-button>
                </el-space>
              </template>
            </el-table-column>

          </el-table>
        </template>

        <!-- ================= 手机卡片 ================= -->
        <template v-else>
          <el-space direction="vertical" fill style="width:100%;">
            <el-card
                v-for="r in rows"
                :key="r.id"
                shadow="never"
                body-style="padding:10px"
            >
              <el-descriptions :column="1" size="small" border>
                <el-descriptions-item label="运动类型">{{ r.sportType }}</el-descriptions-item>
                <el-descriptions-item label="时长">{{ r.durationMin }} 分钟</el-descriptions-item>
                <el-descriptions-item label="卡路里">{{ r.calorieKcal }}</el-descriptions-item>
                <el-descriptions-item label="距离">{{ r.distanceKm }} km</el-descriptions-item>
                <el-descriptions-item label="心率">{{ r.avgHr }}</el-descriptions-item>
              </el-descriptions>

              <el-row justify="end" style="margin-top:10px">
                <el-space>
                  <el-button size="small" type="primary" @click="goToEditPage(r)">
                    编辑
                  </el-button>
                  <el-button size="small" type="danger" @click="onDelete(r)">
                    删除
                  </el-button>
                </el-space>
              </el-row>
            </el-card>
          </el-space>
        </template>

        <!-- ================= 分页 ================= -->
        <el-row justify="end" style="margin-top:20px">
          <el-pagination
              v-model:current-page="page.current"
              v-model:page-size="page.size"
              :total="page.total"
              :layout="isMobile ? 'prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
              :page-sizes="[10,20,50]"
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
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const { isMobile } = useResponsive()

const loading = ref(false)

const query = reactive({
  sportType: ''
})

const page = reactive({
  current: 1,
  size: 10,
  total: 0
})

const rows = ref([])

// const form = reactive({
//   id: null,
//   sportType: '',
//   durationMin: 0,
//   calorieKcal: 0,
//   distanceKm: 0,
//   avgHr: 0,
//   remark: ''
// })

/* ================= API ================= */

const fetchApi = (params) =>
    request.get('/api/records/list', { params })

const deleteApi = (id) =>
    request.delete(`/api/records/${id}`)

/* ================= 逻辑 ================= */

function buildParams() {
  return {
    current: page.current,
    size: page.size,
    sportType: query.sportType
  }
}

async function load() {
  loading.value = true
  try {
    const res = await fetchApi(buildParams())
    rows.value = res.data.records || []
    page.total = Number(res.data.total || 0)
  } finally {
    loading.value = false
  }
}

function onSearch() {
  page.current = 1
  load()
}

function onReset() {
  query.sportType = ''
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

// 跳转到编辑页面
function goToEditPage(row) {
  if (row) {
    router.push({ name: 'SportRecordEdit', params: { id: row.id } })
  } else {
    router.push({ name: 'SportRecordEdit', params: { id: 'new' } })
  }
}

async function onDelete(row) {
  try {
    await ElMessageBox.confirm('确认删除该记录吗？', '提示', { type: 'warning' })
  } catch {
    return
  }

  await deleteApi(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.el-card__body {
  padding: 0 !important;
}
</style>