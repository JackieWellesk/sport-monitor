<template>
  <el-container direction="vertical">
    <el-main style="padding: 0;">
      <el-card
          shadow="never"
          :body-style='isMobile? "padding: 0 5px 5px 5px;" : "padding: 0 16px 16px 16px;"'
          style="margin-top: 10px"
      >

        <!-- 列表 -->
        <el-space direction="vertical" fill style="width: 100%; margin-top: 20px">
          <EventListItem
              v-for="e in rows"
              :key="e.id"
              :event="e"
              @click="openDetail"
          />
        </el-space>

        <!-- 分页 -->
        <el-row justify="end" style="margin-top: 20px">
          <el-pagination
              v-model:current-page="page.current"
              v-model:page-size="page.size"
              :total="page.total"
              :layout="isMobile ? 'prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
              :page-sizes="[5, 10, 20]"
              :pager-count="isMobile ? 3 : 7"
              background
              @current-change="onCurrentChange"
              @size-change="onSizeChange"
          />
        </el-row>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { useResponsive } from '@/composables/useResponsive'
import request from '@/utils/request'
import EventListItem from "@/components/EventListItem.vue";
import { useRouter } from 'vue-router'

const { isMobile } = useResponsive()

const router = useRouter()
const loading = ref(false)

const page = reactive({
  current: 1,
  size: 5,
  total: 0
})

const rows = ref([])


/* ========= 加载数据 ========= */
async function load() {
  loading.value = true
  try {
    const res = await request.get('/api/events', {
      params: {
        current: page.current,
        size: page.size
      }
    })

    const data = res.data ?? {}

    rows.value = data.records ?? []
    page.total = data.total ?? 0

  } catch (e) {
    rows.value = []
    page.total = 0
  } finally {
    loading.value = false
  }
}

/* ========= 翻页自动加载 ========= */
function onCurrentChange() {
  load()
}

function onSizeChange() {
  page.current = 1
  load()
}

function openDetail(id) {
  router.push({
    path: '/events/detail',
    query: { id }
  })
}

/* ========= 页面初始化 ========= */
onMounted(() => {
  load()
})
</script>

<style scoped>
.page-title {
  font-size: 18px;
  font-weight: 600;
}
</style>