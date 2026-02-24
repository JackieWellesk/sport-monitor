import axios from 'axios'
import router from '@/router'
import { ElMessage } from 'element-plus'
import {useAuthStore} from "@/stores/auth";

const request = axios.create({
    baseURL: '',
    withCredentials: true,
    timeout: 10000
})

request.interceptors.response.use(
    (res) => {
        const body = res.data

        // 只处理你们的 R 结构
        if (body && typeof body.code !== 'undefined') {
            if (body.code === 0) {
                return body // ✅ 直接返回 body，后续拿到的是 {code,msg,data}
            }

            // 业务失败：统一弹 msg + 抛错，让业务 catch 到
            ElMessage.error(body.msg || '请求失败')
            return Promise.reject(new Error(body.msg || 'BUSINESS_ERROR'))
        }

        // 不是 R 结构就原样返回
        return res
    },
    (err) => {
        const status = err?.response?.status
        if (status === 401) {
            const auth = useAuthStore()
            auth.clear()   // ✅ 清空 user 缓存
            router.replace('/login')
        }
        else if (status === 403) router.replace({ name: 'Forbidden' })
        return Promise.reject(err)
    }
)

export default request
