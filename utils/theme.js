import { ref, computed } from 'vue'
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

    // 返回当前主题的副本，避免响应式 bug
    const getThemeConfig = () => {
        const theme = themes[currentTheme.value] || themes.default
        return { ...theme }
    }

    return {
        currentTheme,
        setTheme,
        getThemeConfig
    }
}
