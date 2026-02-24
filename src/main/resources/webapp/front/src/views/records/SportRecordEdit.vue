<template>
  <el-card shadow="never">

    <el-form :model="form" :label-width="isMobile ? '90px' : '110px'">
      <el-row :gutter="12">

        <el-col :xs="24" :sm="12">
          <el-form-item label="运动类型">
            <el-input
                v-model="form.sportType"
                placeholder="如：RUN / WALK / BIKE"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="开始时间">
            <el-date-picker
                v-model="form.startTime"
                type="datetime"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="时长(分钟)">
            <el-input-number
                v-model="form.durationMin"
                :min="1"
                :controls="false"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="卡路里(kcal)">
            <el-input-number
                v-model="form.calorieKcal"
                :min="0"
                :step="10"
                :controls="false"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="距离(km)">
            <el-input-number
                v-model="form.distanceKm"
                :min="0"
                :step="0.1"
                :precision="2"
                :controls="false"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="平均心率">
            <el-input-number
                v-model="form.avgHr"
                :min="0"
                :max="250"
                :controls="false"
                style="width: 100%"
            />
          </el-form-item>
        </el-col>

        <el-col :span="24">
          <el-form-item label="备注">
            <el-input
                v-model="form.remark"
                type="textarea"
                :rows="3"
                placeholder="填写补充说明"
            />
          </el-form-item>
        </el-col>

      </el-row>

      <div class="footer">
        <el-button @click="back">返回</el-button>
        <el-button type="primary" @click="submit">
          {{ isEdit ? '保存修改' : '立即创建' }}
        </el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useResponsive } from '@/composables/useResponsive'

const route = useRoute()
const router = useRouter()
const isMobile = useResponsive()

const id = route.params.id
const isEdit = ref(!!id)

// 固定来源 MANUAL
const form = reactive({
  source: 'MANUAL',
  sportType: '',
  startTime: '',
  durationMin: 30,
  calorieKcal: 0,
  distanceKm: 0,
  avgHr: 0,
  remark: ''
})


const loadDetail = async () => {
  const res = await request.get(`/api/records/${id}`)
  Object.assign(form, res.data)
}

const submit = async () => {
  try {
    // 强制确保来源为 MANUAL
    form.source = 'MANUAL'

    if (isEdit.value) {
      form.id = id
      await request.put(`/api/records/update`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/api/records/save', form)
      ElMessage.success('创建成功')
    }
    back()
  } catch (e) {
    console.log(e)
  }
}

const back = () => {
  router.push('/records/list')
}

onMounted(() => {
  if (isEdit.value) {
    loadDetail()
  }
})
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.title {
  font-weight: bold;
  font-size: 16px;
}
.footer {
  margin-top: 20px;
  text-align: right;
}
</style>