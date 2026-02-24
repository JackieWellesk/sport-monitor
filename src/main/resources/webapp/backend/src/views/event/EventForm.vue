<template>
  <el-card shadow="never">
    <template #header>
      <div class="header">
        <div class="title">{{ isEdit ? '修改赛事' : '新增赛事' }}</div>
        <el-button @click="back">返回</el-button>
      </div>
    </template>

    <el-form :model="form" :label-width="isMobile ? '90px' : '110px'">
      <el-row :gutter="12">
        <el-col :xs="24" :sm="12">
          <el-form-item label="标题">
            <el-input v-model="form.title"/>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="类型">
            <el-input v-model="form.eventType" placeholder="如：RUN / JUMP"/>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="报名开始">
            <el-date-picker v-model="form.signupStart" type="datetime" style="width:100%"/>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="报名结束">
            <el-date-picker v-model="form.signupEnd" type="datetime" style="width:100%"/>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="比赛开始">
            <el-date-picker v-model="form.startTime" type="datetime" style="width:100%"/>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="比赛结束">
            <el-date-picker v-model="form.endTime" type="datetime" style="width:100%"/>
          </el-form-item>
        </el-col>

        <el-col :xs="24" :sm="12">
          <el-form-item label="人数上限">
            <el-input-number v-model="form.maxParticipants" :min="1" style="width:100%" :controls="false"/>
          </el-form-item>
        </el-col>

        <el-col :xs="24">
          <el-form-item label="内容(富文本)">
            <div class="editor-wrap">
              <!-- 工具栏 -->
              <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :defaultConfig="toolbarConfig"
                  :mode="mode"
              />
              <!-- 编辑器 -->
              <Editor
                  style="height: 500px; overflow-y: hidden;"
                  v-model="form.content"
                  :defaultConfig="editorConfig"
                  :mode="mode"
                  @onCreated="handleCreated"
              />
            </div>
          </el-form-item>
        </el-col>
      </el-row>

      <div class="actions">
        <el-button @click="back">返回</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </div>
    </el-form>
  </el-card>
</template>

<script setup>
import {ref, computed, onBeforeUnmount, shallowRef} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import {ElMessage} from 'element-plus'
import request from '@/utils/request'
import {useResponsive} from '@/composables/useResponsive'

import '@wangeditor/editor/dist/css/style.css'
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'

// 1. 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

const toolbarConfig = computed(() => {
  return isMobile.value
      ? toolbarConfigMobile
      : toolbarConfigDesktop
})

const toolbarConfigDesktop = {
  // 只排除视频相关菜单（上传视频/插入视频）
  excludeKeys: ['group-video', 'uploadVideo', 'insertVideo']
}

const toolbarConfigMobile = {
  toolbarKeys: [
    'headerSelect',  // 正文(标题下拉里包含“正文”)
    'bold',          // 粗体
    'underline',     // 下划线
    'italic',        // 斜体
    'uploadImage',    // 上传图片,
    'fullScreen'     // 最大化
  ]
}
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      // 关键：自定义上传
      customUpload: async (file, insertFn) => {
        try {
          const form = new FormData()
          form.append('file', file)

          const res = await request.post('/api/upload/image', form)

          // 按你的后端返回取 url
          // 如果你返回 { errno:0, data:[{url:'...'}] }
          const url = res.data

          // 插入图片到编辑器
          insertFn(url)
        } catch (e) {
          ElMessage.error('图片上传失败')
        }
      }
    }
  }
}
// ⭐ 根据设备切换模式
const mode = computed(() => {
  return isMobile.value ? 'simple' : 'default'
})

const {isMobile} = useResponsive()
const route = useRoute()
const router = useRouter()

const id = computed(() => route.query.id)
const isEdit = computed(() => !!id.value)

const form = ref({
  id: null,
  title: '',
  content: '',
  ruleText: '',
  eventType: '',
  signupStart: '',
  signupEnd: '',
  startTime: '',
  endTime: '',
  maxParticipants: 50,
  status: 'DRAFT'
})

const back = () => router.push('/events/list')

onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})
const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例
}

const loadDetail = async () => {
  if (!isEdit.value) return
  const res = await request.get(`/api/events/${id.value}`)
  for (const key in form.value) {
    if (key in res.data) {
      form.value[key] = res.data[key]
    }
  }
}

const save = async () => {
  if (!form.value.title) return ElMessage.warning('标题不能为空')

  if (isEdit.value) {
    await request.put(`/api/events/${id.value}`, form.value)
    ElMessage.success('修改成功')
  } else {
    const res = await request.post('/api/events', form.value)
    ElMessage.success('新增成功')
    // 后端返回 id 的话可以跳转到编辑态
    if (res.data) router.replace({path: '/events/edit', query: {id: res.data}})
  }
}

loadDetail()
</script>

<style scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.editor-wrap {
  width: 100%;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  overflow: hidden;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 10px;
}


</style>