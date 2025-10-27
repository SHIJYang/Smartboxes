import { ref } from 'vue'
import { themes } from '@/theme/config'

// 主题状态管理
export const useTheme = () => {
    const currentTheme = ref(uni.getStorageSync('theme') || 'default')

    const setTheme = (themeName) => {
        if (themes[themeName]) {
            currentTheme.value = themeName
            uni.setStorageSync('theme', themeName)
        }
    }

    const getThemeConfig = () => themes[currentTheme.value]

    return {
        currentTheme,
        setTheme,
        getThemeConfig
    }
}
