import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: null,
        inited: false, // 是否已做过 me 校验
    }),
    actions: {
        async fetchMe() {
            const body = await  request.get("/api/auth/me")// 如果你 request.js 做了 code 统一处理，这里拿到的是 {code,msg,data}
            this.user = body.data
            this.inited = true
            return this.user
        },
        clear() {
            this.user = null
            this.inited = false
        }
    }
})
