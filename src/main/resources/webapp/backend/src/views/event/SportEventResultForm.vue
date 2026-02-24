<template>
  <el-container>
    <el-main style="padding: 10px 10px 10px 0;">
      <el-card
          shadow="never"
          body-style="padding: 10px 10px 10px 10px;"
          style="margin-top: 10px"
      >

        <el-row :gutter="20">
          <el-col :sm="10" :xs="24">
            <el-form :model="form" label-width="">

              <el-form-item label="ID" v-if="id" label-width="">
                <el-input v-model="form.id" disabled />
              </el-form-item>

              <el-form-item label="赛事ID" style="margin-top: 10px" label-width="">
                <el-input v-model="form.eventId" />
              </el-form-item>

              <el-form-item label="用户ID" label-width="">
                <el-input v-model="form.userId" />
              </el-form-item>

              <el-form-item label="成绩" label-width="">
                <el-input v-model="form.scoreValue" />
              </el-form-item>

              <el-form-item label="单位" label-width="">
                <el-input v-model="form.scoreUnit" />
              </el-form-item>

              <el-form-item label="排名" label-width="">
                <el-input v-model="form.rankNo" />
              </el-form-item>

              <el-form-item label="是否发布" label-width="">
                <el-switch v-model="form.published" />
              </el-form-item>

              <el-form-item label="备注" label-width="">
                <el-input type="textarea" v-model="form.remark" />
              </el-form-item>

              <el-form-item>

                <div style="display:flex; justify-content:flex-end; width:100%;">
                  <el-button @click="goBack">
                    返回
                  </el-button>
                  <el-button type="primary" :loading="saving" @click="save">
                    {{ id ? '保存修改' : '登记成绩' }}
                  </el-button>
                </div>
              </el-form-item>

            </el-form>
          </el-col>
        </el-row>

      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

// ✅ 列表页带过来的 id：有 id => 修改；无 id => 新增
const id = ref(route.query.id ? Number(route.query.id) : null)

const saving = ref(false)

const form = reactive({
  id: null,
  eventId: '',
  userId: '',
  scoreValue: '',
  scoreUnit: '',
  rankNo: '',
  remark: '',
  published: false,
})

async function loadDetail() {
  if (!id.value) return

  // 你按自己后端实际接口改这里（用于回显）
  const res = await request.get(`/api/results/${id.value}`)

  // 直接覆盖表单
  const data = res.data || {}
  form.id = data.id ?? id.value
  form.eventId = data.eventId ?? ''
  form.userId = data.userId ?? ''
  form.scoreValue = data.scoreValue ?? ''
  form.scoreUnit = data.scoreUnit ?? ''
  form.rankNo = data.rankNo ?? ''
  form.remark = data.remark ?? ''
  form.published = !!data.published
}

async function save() {
  saving.value = true
  try {
    if (id.value) {
      // ✅ 修改：一定要带 id（你要求的）
      const payload = { ...form, id: id.value }
      // 你按自己后端实际接口改这里
      await request.put(`/api/results`, payload)
      ElMessage.success('修改成功')
    } else {
      // ✅ 新增
      // 你按自己后端实际接口改这里
      await request.post('/api/results', { ...form, id: null })
      ElMessage.success('登记成功')
    }
    router.back()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

function goBack() {
  router.back()
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.el-card__body {
  padding: 0 !important;
}
</style>