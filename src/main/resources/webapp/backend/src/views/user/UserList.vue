<template>
  <el-container direction="vertical">
    <el-main style="padding: 0;">
      <el-card shadow="never" body-style="padding: 0 10px 10px 10px;" style="margin-top: 10px">
        <!-- 查询区 -->
        <el-form :inline="!isMobile" label-width="90px" @submit.prevent style="margin-top: 10px">
          <el-row :gutter="12">
            <el-col :xs="24" :sm="4">
              <el-form-item label="用户名" label-position="left" label-width="">
                <el-input
                    v-model="query.username"
                    clearable
                    placeholder="请输入用户名"
                    @keyup.enter="onSearch"
                    style="min-width: 200px;"
                />
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="4">
              <el-form-item label="角色" label-position="left" label-width="">
                <el-select v-model="query.roleCode" clearable placeholder="全部" style="min-width: 200px" >
                  <el-option label="ADMIN" value="ADMIN" />
                  <el-option label="TEACHER" value="TEACHER" />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="4">
              <el-form-item label="状态" label-position="left" label-width="">
                <el-select v-model="query.enabled" clearable placeholder="全部" style="min-width: 200px">
                  <el-option label="启用" :value="true" />
                  <el-option label="禁用" :value="false" />
                </el-select>
              </el-form-item>
            </el-col>

            <el-col :xs="24" :sm="6">
              <el-form-item label-width="">
                <el-row
                    :justify="isMobile ? 'end' : 'start'"
                    style="width: 100%;"
                >
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
            <el-table-column label="头像" width="90" align="center">
              <template #default="{ row }">
                <el-avatar :src="row.avatarUrl ? '/upload/' + row.avatarUrl : ''">
                  {{ (row.username || '').slice(0, 1).toUpperCase() }}
                </el-avatar>
              </template>
            </el-table-column>

            <el-table-column prop="username" label="用户名" min-width="160" />
            <el-table-column prop="roleCode" label="角色" width="120" />
            <el-table-column label="状态" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'info'">
                  {{ row.enabled ? '启用' : '禁用' }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <el-space>
                  <el-button
                      size="small"
                      type="primary"
                      link
                      :loading="actionLoadingId === row.id"
                      @click="onToggleEnabled(row)"
                  >
                    {{ row.enabled ? '禁用' : '启用' }}
                  </el-button>

                  <el-button
                      size="small"
                      type="warning"
                      link
                      :loading="actionLoadingId === row.id"
                      @click="deleteUser(row)"
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
                v-for="u in rows"
                :key="u.id"
                shadow="never"
                body-style="padding: 10px"
            >
              <el-row :gutter="12" align="middle">
                <el-col :span="6" style="text-align:center;">
                  <el-avatar :src="u.avatarUrl ? '/upload/' + u.avatarUrl : ''">
                    {{ (u.username || '').slice(0, 1).toUpperCase() }}
                  </el-avatar>
                </el-col>

                <el-col :span="18">
                  <el-descriptions :column="1" size="small" border>
                    <el-descriptions-item label="ID">{{ u.id }}</el-descriptions-item>
                    <el-descriptions-item label="用户名">
                      {{ u.username }}
                    </el-descriptions-item>
                    <el-descriptions-item label="角色">
                      {{ u.roleCode }}
                    </el-descriptions-item>
                    <el-descriptions-item label="状态">
                      <el-tag :type="u.enabled ? 'success' : 'info'">
                        {{ u.enabled ? '启用' : '禁用' }}
                      </el-tag>
                    </el-descriptions-item>
                  </el-descriptions>
                </el-col>

                <el-col :span="24" align="right" style="padding-top: 10px">
                  <el-button
                      size="small"
                      type="primary"
                      :loading="actionLoadingId === u.id"
                      @click="onToggleEnabled(u)"
                  >
                    {{ u.enabled ? '禁用' : '启用' }}
                  </el-button>

                  <el-button
                      size="small"
                      type="warning"
                      :loading="actionLoadingId === u.id"
                      @click="onResetPwd(u)"
                  >
                    删除
                  </el-button>
                </el-col>


              </el-row>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { useResponsive } from '@/composables/useResponsive'
import request from '@/utils/request'

// 如有 axios 实例可引入
// import request from '@/utils/request'

const { isMobile } = useResponsive()

const loading = ref(false)
const actionLoadingId = ref(null)

const query = reactive({
  username: '',
  roleCode: '',
  enabled: undefined,
})

const page = reactive({
  current: 1,
  size: 10,
  total: 0,
})

const rows = ref([])

// =======================
// 预留接口调用（替换为真实接口）
// =======================
async function fetchUsersApi(params) {
  // 示例：
  return request.get('/api/user/list', { params })
}

async function toggleEnabledApi(id, enabled) {
  return request.put(`/api/user/${id}/${enabled}`)
}

async function deleteUserById(id) {
  return request.delete(`/api/user/${id}`)
}
// =======================
// 页面逻辑
// =======================
function buildParams() {
  const params = {
    current: page.current,
    size: page.size,
  }
  if (query.keyword) params.keyword = query.keyword
  if (query.roleCode) params.roleCode = query.roleCode
  if (query.enabled !== undefined) params.queryEnabled = query.enabled
  return params
}

async function load() {
  loading.value = true
  try {
    const res = await fetchUsersApi(buildParams())
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
  query.keyword = ''
  query.roleCode = ''
  query.enabled = undefined
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

async function onToggleEnabled(row) {
  const nextEnabled = !row.enabled
  const text = nextEnabled ? '确认启用该用户？' : '确认禁用该用户？'

  try {
    await ElMessageBox.confirm(text, '提示', { type: 'warning' })
  } catch {
    return
  }

  actionLoadingId.value = row.id
  try {
    await toggleEnabledApi(row.id, nextEnabled)
    ElMessage.success('操作成功')
    await load()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    actionLoadingId.value = null
  }
}

async function deleteUser(row) {
  try {
    await ElMessageBox.confirm('确认删除该用户吗？', '提示', { type: 'warning' })
  } catch {
    return
  }

  actionLoadingId.value = row.id
  try {
    await deleteUserById(row.id)
    ElMessage.success('用户已删除')
    await load()
  } catch (e) {
    ElMessage.error(e?.message || '操作失败')
  } finally {
    actionLoadingId.value = null
  }
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