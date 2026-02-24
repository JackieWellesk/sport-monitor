import { useMediaQuery } from '@vueuse/core'

export function useResponsive() {
    // 只做手机/桌面：< 768px 视为手机
    const isMobile = useMediaQuery('(max-width: 767px)')
    return { isMobile }
}
