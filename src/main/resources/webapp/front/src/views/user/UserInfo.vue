<template>
  <el-container>
    <el-main style="padding: 10px 10px 10px 0;">
      <el-card shadow="never" body-style="padding: 0 10px 10px 10px;" style="margin-top: 10px">
        <el-row :gutter="20">
          <el-col :sm="8">
            <el-form :model="form" label-width="100px">
              <el-form-item label="头像" label-position="left" label-width="" style="margin-top: 10px">
                <el-upload
                    class="avatar-uploader"
                    :show-file-list="false"
                    accept="image/*"
                    :auto-upload='false'
                    :on-change="handleFileChange">
                  <img v-if="imageUrl" :src="imageUrl" class="avatar" alt="pic">
                  <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item label="用户名" label-position="left" label-width="">
                <el-input v-model="form.username" disabled></el-input>
              </el-form-item>
              <el-form-item label="密码" label-position="left" label-width="">
                <el-input
                    v-model="form.password"
                    type="password"
                    placeholder="密码为空，则不更改"
                    :show-password="true"
                ></el-input>
              </el-form-item>
              <el-form-item label-position="left" label-width="" >
                <div style="display: flex; justify-content: flex-end; width: 100%;">
                  <el-button @click="removeImage">清除图片</el-button>
                  <el-button type="primary" @click="saveInfo">保存信息</el-button>
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
import {onMounted, ref} from 'vue';
import {ElUpload, ElButton, ElRow, ElCol, ElInput, ElForm, ElFormItem, ElMessage} from 'element-plus';
import {Plus} from "@element-plus/icons-vue"
import request from '@/utils/request'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
// import { useResponsive } from '@/composables/useResponsive'
//
// const { isMobile } = useResponsive()
// Store the image file before uploading it
const imageUrl = ref('');
const removeImage = () => {
  imageUrl.value = '';
}
// Form data for password change
const form = ref({
  username: '',
  password: '',
  avatarUrl: '',
  avatarFile: null
});

// API call to fetch user data (replace with real API call)
const fetchUserData = async () => {
  const res = await request.get('/api/user/info')
  form.value.username = res.data.username;
  form.value.password = ''; // Initialize password as empty
  if (res.data.avatarFile !== undefined && res.data.avatarUrl !== null) {
    imageUrl.value = '/upload/' + res.data.avatarUrl;
  }
};


function handleFileChange(file) {
  // file.raw 是原始文件对象
  imageUrl.value = URL.createObjectURL(file.raw)
  form.value.avatarFile = file.raw
}

// Password change handler
const saveInfo = async () => {
  const formData = new FormData();
  formData.append('id', auth.user.id);
  formData.append('username', form.value.username);
  formData.append('password', form.value.password);

  if (imageUrl.value && form.value.avatarFile && form.value.avatarFile instanceof File) {
    formData.append('avatarFile', form.value.avatarFile); // Add the file to the form data
  }

  try {
    await request.put('/api/user/update', formData);
    ElMessage.success('信息已经更新');
    location.reload();
  } catch (error) {
    ElMessage.error('发生了错误');
  }
}


// Call fetchUserData when the component is mounted
onMounted(() => {
  fetchUserData(); // Replace with real API call
});
</script>

<style scoped>
.avatar-uploader {
  display: inline-block;
  text-align: center;
}
.avatar-uploader img {
  width: 50px;
  height: 50px;
}
</style>