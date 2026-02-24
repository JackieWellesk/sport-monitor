<template>
  <!-- 这个区域会被截图导出PDF -->
  <div class="page" ref="pdfAreaRef">
    <!-- 顶部操作栏 -->
    <el-card shadow="never" class="filter-card">
      <el-row :gutter="12" align="middle">

        <el-col :xs="24" :sm="6" class="mt-m">
          <div class="months-box">
            <span class="months-label">统计月数</span>
            <el-input-number v-model="query.months" :min="1" :max="12" />
          </div>
        </el-col>

        <el-col :xs="24" :sm="12" class="mt-m actions">
          <el-button type="primary" @click="loadReport" :loading="loading">
            生成报告
          </el-button>
          <el-button @click="exportPdf" :disabled="!hasReport">
            PDF导出
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 概览（评分 + 等级 + 简评） -->
    <el-card shadow="never" class="mt20">
      <div class="score-section">
        <div class="score-box">
          <div class="score">{{ report.evaluation?.totalScore ?? 0 }}</div>
          <div class="label">综合评分</div>
          <div class="level">等级：{{ levelText }}</div>
        </div>

        <div class="score-detail">
          <div class="item">有氧能力：{{ report.evaluation?.aerobicScore ?? '-' }} / 40</div>
          <div class="item">运动规律性：{{ report.evaluation?.consistencyScore ?? '-' }} / 30</div>
          <div class="item">运动负荷：{{ report.evaluation?.loadScore ?? '-' }} / 30</div>
          <div class="comment">{{ report.evaluation?.comment || '点击“生成报告”获取评估结论。' }}</div>
        </div>
      </div>
    </el-card>

    <!-- 趋势分析：时长/次数 -->
    <el-card shadow="never" class="mt20">
      <div class="card-title">运动趋势分析</div>
      <div ref="trendChartRef" class="chart"></div>
      <div class="hint" v-if="report.trend?.summary?.text">
        趋势摘要：{{ report.trend.summary.text }}
      </div>
    </el-card>

    <!-- 趋势分析：跑步（距离/心率） -->
    <el-card shadow="never" class="mt20">
      <div class="card-title">跑步能力趋势（距离 / 平均心率）</div>
      <div ref="runChartRef" class="chart"></div>
      <div class="hint" v-if="!runMonths.length">
        没有跑步数据时，这张图会为空（正常）。
      </div>
    </el-card>

    <!-- 改进建议 -->
    <el-card shadow="never" class="mt20">
      <div class="card-title">改进建议</div>
      <el-empty v-if="!report.suggestions?.length" description="暂无建议" />
      <el-timeline v-else>
        <el-timeline-item v-for="(item, idx) in report.suggestions" :key="idx">
          <h4 class="sug-title">{{ item.title }}</h4>
          <p class="sug-detail">{{ item.detail }}</p>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import {ref, computed, nextTick, onBeforeUnmount, onMounted} from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'
import request from '@/utils/request'

/** 查询条件 */
const query = ref({
  userId: 6,
  months: 10
})

/** 数据 */
const report = ref({})
const loading = ref(false)

const hasReport = computed(() => !!report.value?.trend?.monthly?.length)

/** 中文等级展示 */
const levelText = computed(() => {
  const level = report.value?.evaluation?.level
  if (level === 'EXCELLENT') return '优秀'
  if (level === 'GOOD') return '良好'
  if (level === 'NORMAL') return '一般'
  if (level === 'NEED_IMPROVE') return '需改进'
  return '-'
})

/** 图表 refs */
const trendChartRef = ref(null)
const runChartRef = ref(null)
const pdfAreaRef = ref(null)

let trendChart = null
let runChart = null

/** 派生数据 */
const months = computed(() => report.value?.trend?.monthly?.map(x => x.month) || [])
const minutes = computed(() => report.value?.trend?.monthly?.map(x => x.totalMinutes) || [])
const sessions = computed(() => report.value?.trend?.monthly?.map(x => x.totalSessions) || [])

const runMonths = computed(() => report.value?.trend?.running?.map(x => x.month) || [])
const runAvgDist = computed(() => report.value?.trend?.running?.map(x => x.avgDistance ?? 0) || [])
const runAvgHr = computed(() => report.value?.trend?.running?.map(x => x.avgHeartRate ?? 0) || [])

/** 拉取报告 */
const loadReport = async () => {
  try {
    loading.value = true
    const res = await request.get('/api/health-report', { params: query.value })

    // 兼容：有些 request 封装返回 {data: ...}，有些直接返回 data
    report.value = res?.data ?? res ?? {}

    await nextTick()
    renderCharts()
  } catch (e) {
    ElMessage.error('获取报告失败，请检查接口或网络')
  } finally {
    loading.value = false
  }
}

/** 渲染趋势图 */
const renderCharts = () => {
  renderTrendChart()
  renderRunChart()
}

const renderTrendChart = () => {
  if (!trendChartRef.value) return

  if (!trendChart) trendChart = echarts.init(trendChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['运动时长(分钟)', '运动次数'] },
    xAxis: { type: 'category', data: months.value },
    yAxis: [
      { type: 'value', name: '分钟' },
      { type: 'value', name: '次数' }
    ],
    series: [
      { name: '运动时长(分钟)', type: 'line', smooth: true, data: minutes.value },
      { name: '运动次数', type: 'bar', yAxisIndex: 1, data: sessions.value }
    ]
  }

  trendChart.setOption(option)
  trendChart.resize()
}

const renderRunChart = () => {
  if (!runChartRef.value) return

  if (!runChart) runChart = echarts.init(runChartRef.value)

  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['平均距离(km)', '平均心率'] },
    xAxis: { type: 'category', data: runMonths.value },
    yAxis: [
      { type: 'value', name: 'km' },
      { type: 'value', name: 'bpm' }
    ],
    series: [
      { name: '平均距离(km)', type: 'line', smooth: true, data: runAvgDist.value },
      { name: '平均心率', type: 'line', smooth: true, yAxisIndex: 1, data: runAvgHr.value }
    ]
  }

  runChart.setOption(option)
  runChart.resize()
}

/** PDF导出（截图分页） */
const exportPdf = async () => {
  if (!pdfAreaRef.value) return
  if (!hasReport.value) {
    ElMessage.warning('请先生成报告再导出')
    return
  }

  await nextTick()
  // 防止图表导出时尺寸异常
  if (trendChart) trendChart.resize()
  if (runChart) runChart.resize()

  const canvas = await html2canvas(pdfAreaRef.value, {
    scale: 2,
    useCORS: true,
    backgroundColor: '#ffffff'
  })

  const imgData = canvas.toDataURL('image/png')

  const pdf = new jsPDF('p', 'mm', 'a4')
  const pdfW = pdf.internal.pageSize.getWidth()
  const pdfH = pdf.internal.pageSize.getHeight()

  const imgW = pdfW
  const imgH = (canvas.height * imgW) / canvas.width

  let position = 0
  let leftHeight = imgH

  pdf.addImage(imgData, 'PNG', 0, position, imgW, imgH)
  leftHeight -= pdfH

  while (leftHeight > 0) {
    position -= pdfH
    pdf.addPage()
    pdf.addImage(imgData, 'PNG', 0, position, imgW, imgH)
    leftHeight -= pdfH
  }

  const uid = query.value.userId || 'user'
  const monthsVal = query.value.months || ''
  pdf.save(`健康报告_user${uid}_${monthsVal}months.pdf`)
}

/** 清理 */
onBeforeUnmount(() => {
  if (trendChart) {
    trendChart.dispose()
    trendChart = null
  }
  if (runChart) {
    runChart.dispose()
    runChart = null
  }
})

onMounted(() => {
  loadReport()
})
</script>

<style scoped>
.page {
  padding: 20px;
}

.mt20 {
  margin-top: 20px;
}

.filter-card .actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.months-box {
  display: flex;
  align-items: center;
  gap: 10px;
}
.months-label {
  font-size: 13px;
  color: var(--el-text-color-regular);
}

.score-section {
  display: flex;
  gap: 20px;
  align-items: center;
  justify-content: space-between;
}

.score-box {
  min-width: 180px;
  text-align: center;
  padding: 10px 0;
}

.score {
  font-size: 52px;
  font-weight: 800;
  color: var(--el-color-primary);
  line-height: 1;
}

.label {
  margin-top: 6px;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.level {
  margin-top: 6px;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.score-detail {
  flex: 1;
  font-size: 14px;
  line-height: 28px;
}

.score-detail .comment {
  margin-top: 6px;
  color: var(--el-text-color-primary);
}

.card-title {
  font-weight: 700;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.chart {
  height: 360px;
}

.hint {
  margin-top: 10px;
  color: var(--el-text-color-regular);
  font-size: 13px;
}

.sug-title {
  margin: 0 0 6px;
  color: var(--el-color-primary);
}

.sug-detail {
  margin: 0;
  color: var(--el-text-color-primary);
}

@media (max-width: 768px) {
  .filter-card .actions {
    justify-content: flex-start;
  }
  .mt-m {
    margin-top: 10px;
  }
  .score-section {
    flex-direction: column;
    align-items: flex-start;
  }
  .score-box {
    text-align: left;
  }
}
</style>